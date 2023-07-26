package io.github.foundationgames.automobility.entity;

import io.github.foundationgames.automobility.Automobility;
import io.github.foundationgames.automobility.automobile.AutomobileEngine;
import io.github.foundationgames.automobility.automobile.AutomobileFrame;
import io.github.foundationgames.automobility.automobile.AutomobileStats;
import io.github.foundationgames.automobility.automobile.AutomobileWheel;
import io.github.foundationgames.automobility.automobile.WheelBase;
import io.github.foundationgames.automobility.automobile.attachment.FrontAttachmentType;
import io.github.foundationgames.automobility.automobile.attachment.RearAttachmentType;
import io.github.foundationgames.automobility.automobile.attachment.front.FrontAttachment;
import io.github.foundationgames.automobility.automobile.attachment.rear.DeployableRearAttachment;
import io.github.foundationgames.automobility.automobile.attachment.rear.RearAttachment;
import io.github.foundationgames.automobility.automobile.render.RenderableAutomobile;
import io.github.foundationgames.automobility.block.AutomobileAssemblerBlock;
import io.github.foundationgames.automobility.block.LaunchGelBlock;
import io.github.foundationgames.automobility.block.OffRoadBlock;
import io.github.foundationgames.automobility.item.AutomobileInteractable;
import io.github.foundationgames.automobility.item.AutomobilityItems;
import io.github.foundationgames.automobility.particle.AutomobilityParticles;
import io.github.foundationgames.automobility.platform.Platform;
import io.github.foundationgames.automobility.screen.AutomobileContainerLevelAccess;
import io.github.foundationgames.automobility.sound.AutomobilitySounds;
import io.github.foundationgames.automobility.util.AUtils;
import io.github.foundationgames.automobility.util.duck.CollisionArea;
import io.github.foundationgames.automobility.util.network.ClientPackets;
import io.github.foundationgames.automobility.util.network.CommonPackets;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Cursor3D;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.animal.WaterAnimal;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.entity.EntityTypeTest;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Deque;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

public class AutomobileEntity extends Entity implements RenderableAutomobile, EntityWithInventory {
    public static Consumer<AutomobileEntity> engineSound = e -> {};
    public static Consumer<AutomobileEntity> skidSound = e -> {};

    private static final EntityDataAccessor<Float> REAR_ATTACHMENT_YAW = SynchedEntityData.defineId(AutomobileEntity.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Float> REAR_ATTACHMENT_ANIMATION = SynchedEntityData.defineId(AutomobileEntity.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Float> FRONT_ATTACHMENT_ANIMATION = SynchedEntityData.defineId(AutomobileEntity.class, EntityDataSerializers.FLOAT);

    private AutomobileFrame frame = AutomobileFrame.REGISTRY.getOrDefault(null);
    private AutomobileWheel wheels = AutomobileWheel.REGISTRY.getOrDefault(null);
    private AutomobileEngine engine = AutomobileEngine.REGISTRY.getOrDefault(null);
    private RearAttachment rearAttachment;
    private FrontAttachment frontAttachment;

    private final AutomobileStats stats = new AutomobileStats();

    public static final int SMALL_TURBO_TIME = 35;
    public static final int MEDIUM_TURBO_TIME = 70;
    public static final int LARGE_TURBO_TIME = 115;
    public static final float TERMINAL_VELOCITY = -1.2f;

    private long clientTime;

    private double trackedX;
    private double trackedY;
    private double trackedZ;
    private float trackedYaw;
    private int lerpTicks;

    private boolean dirty = false;

    private float engineSpeed = 0;
    private float boostSpeed = 0;
    private float speedDirection = 0;
    private float lastBoostSpeed = boostSpeed;

    private int boostTimer = 0;
    private float boostPower = 0;
    private int jumpCooldown = 0;

    private float hSpeed = 0;
    private float vSpeed = 0;

    private Vec3 addedVelocity = getDeltaMovement();

    private float steering = 0;
    private float lastSteering = steering;

    private float angularSpeed = 0;

    private float wheelAngle = 0;
    private float lastWheelAngle = 0;

    private final Displacement displacement = new AutomobileEntity.Displacement();

    private boolean drifting = false;
    private boolean burningOut = false;
    private int driftDir = 0;
    private int turboCharge = 0;

    private float lockedViewOffset = 0;

    private boolean automobileOnGround = true;
    private boolean wasOnGround = automobileOnGround;
    private boolean isFloorDirectlyBelow = true;
    private boolean touchingWall = false;

    private Vec3 lastVelocity = Vec3.ZERO;
    private Vec3 lastPosForDisplacement = Vec3.ZERO;

    private Vec3 prevTailPos = null;

    private int slopeStickingTimer = 0;
    private float grip = 1;

    private int suspensionBounceTimer = 0;
    private int lastSusBounceTimer = suspensionBounceTimer;

    private final Deque<Double> prevYDisplacements = new ArrayDeque<>();

    private boolean offRoad = false;
    private Vector3f debrisColor = new Vector3f();

    private int fallTicks = 0;

    private int despawnTime = -1;
    private int despawnCountdown = 0;
    private boolean decorative = false;

    private boolean wasEngineRunning = false;

    private float standStillTime = -1.3f;

    public void writeSyncToClientData(FriendlyByteBuf buf) {
        buf.writeInt(boostTimer);
        buf.writeFloat(steering);
        buf.writeFloat(wheelAngle);
        buf.writeInt(turboCharge);
        buf.writeFloat(engineSpeed);
        buf.writeFloat(boostSpeed);
        buf.writeByte(compactInputData());

        buf.writeBoolean(drifting);
        buf.writeBoolean(burningOut);
    }

    public void readSyncToClientData(FriendlyByteBuf buf) {
        boostTimer = buf.readInt();
        steering = buf.readFloat();
        wheelAngle = buf.readFloat();
        turboCharge = buf.readInt();
        engineSpeed = buf.readFloat();
        boostSpeed = buf.readFloat();
        readCompactedInputData(buf.readByte());

        setDrifting(buf.readBoolean());
        setBurningOut(buf.readBoolean());
    }

    @Override
    public void readAdditionalSaveData(CompoundTag nbt) {
        setComponents(
                AutomobileFrame.REGISTRY.getOrDefault(ResourceLocation.tryParse(nbt.getString("frame"))),
                AutomobileWheel.REGISTRY.getOrDefault(ResourceLocation.tryParse(nbt.getString("wheels"))),
                AutomobileEngine.REGISTRY.getOrDefault(ResourceLocation.tryParse(nbt.getString("engine")))
        );

        var rAtt = nbt.getCompound("rearAttachment");
        setRearAttachment(RearAttachment.fromNbt(rAtt));
        rearAttachment.readNbt(rAtt);

        var fAtt = nbt.getCompound("frontAttachment");
        setFrontAttachment(FrontAttachment.fromNbt(fAtt));
        frontAttachment.readNbt(fAtt);

        engineSpeed = nbt.getFloat("engineSpeed");
        boostSpeed = nbt.getFloat("boostSpeed");
        boostTimer = nbt.getInt("boostTimer");
        boostPower = nbt.getFloat("boostPower");
        speedDirection = nbt.getFloat("speedDirection");
        vSpeed = nbt.getFloat("verticalSpeed");
        hSpeed = nbt.getFloat("horizontalSpeed");
        addedVelocity = AUtils.v3dFromNbt(nbt.getCompound("addedVelocity"));
        lastVelocity = AUtils.v3dFromNbt(nbt.getCompound("lastVelocity"));
        angularSpeed = nbt.getFloat("angularSpeed");
        steering = nbt.getFloat("steering");
        wheelAngle = nbt.getFloat("wheelAngle");
        drifting = nbt.getBoolean("drifting");
        driftDir = nbt.getInt("driftDir");
        burningOut = nbt.getBoolean("burningOut");
        turboCharge = nbt.getInt("turboCharge");
        accelerating = nbt.getBoolean("accelerating");
        braking = nbt.getBoolean("braking");
        steeringLeft = nbt.getBoolean("steeringLeft");
        steeringRight = nbt.getBoolean("steeringRight");
        holdingDrift = nbt.getBoolean("holdingDrift");
        fallTicks = nbt.getInt("fallTicks");
        despawnTime = nbt.getInt("despawnTime");
        despawnCountdown = nbt.getInt("despawnCountdown");
        decorative = nbt.getBoolean("decorative");
    }

    @Override
    public void addAdditionalSaveData(CompoundTag nbt) {
        nbt.putString("frame", frame.getId().toString());
        nbt.putString("wheels", wheels.getId().toString());
        nbt.putString("engine", engine.getId().toString());
        nbt.put("rearAttachment", rearAttachment.toNbt());
        nbt.put("frontAttachment", frontAttachment.toNbt());
        nbt.putFloat("engineSpeed", engineSpeed);
        nbt.putFloat("boostSpeed", boostSpeed);
        nbt.putInt("boostTimer", boostTimer);
        nbt.putFloat("boostPower", boostPower);
        nbt.putFloat("speedDirection", speedDirection);
        nbt.putFloat("verticalSpeed", vSpeed);
        nbt.putFloat("horizontalSpeed", hSpeed);
        nbt.put("addedVelocity", AUtils.v3dToNbt(addedVelocity));
        nbt.put("lastVelocity", AUtils.v3dToNbt(lastVelocity));
        nbt.putFloat("angularSpeed", angularSpeed);
        nbt.putFloat("steering", steering);
        nbt.putFloat("wheelAngle", wheelAngle);
        nbt.putBoolean("drifting", drifting);
        nbt.putInt("driftDir", driftDir);
        nbt.putBoolean("burningOut", burningOut);
        nbt.putInt("turboCharge", turboCharge);
        nbt.putBoolean("accelerating", accelerating);
        nbt.putBoolean("braking", braking);
        nbt.putBoolean("steeringLeft", steeringLeft);
        nbt.putBoolean("steeringRight", steeringRight);
        nbt.putBoolean("holdingDrift", holdingDrift);
        nbt.putInt("fallTicks", fallTicks);
        nbt.putInt("despawnTime", despawnTime);
        nbt.putInt("despawnCountdown", despawnCountdown);
        nbt.putBoolean("decorative", decorative);
    }

    private boolean accelerating = false;
    private boolean braking = false;
    private boolean steeringLeft = false;
    private boolean steeringRight = false;
    private boolean holdingDrift = false;

    private boolean prevHoldDrift = holdingDrift;

    public byte compactInputData() {
        int r = ((((((((accelerating ? 1 : 0) << 1) | (braking ? 1 : 0)) << 1) | (steeringLeft ? 1 : 0)) << 1) | (steeringRight ? 1 : 0)) << 1) | (holdingDrift ? 1 : 0);
        return (byte) r;
    }

    public void readCompactedInputData(byte data) {
        int d = data;
        holdingDrift = (1 & d) > 0;
        d = d >> 0b1;
        steeringRight = (1 & d) > 0;
        d = d >> 0b1;
        steeringLeft = (1 & d) > 0;
        d = d >> 0b1;
        braking = (1 & d) > 0;
        d = d >> 0b1;
        accelerating = (1 & d) > 0;
    }

    public AutomobileEntity(EntityType<?> type, Level world) {
        super(type, world);

        this.setRearAttachment(RearAttachmentType.REGISTRY.getOrDefault(null));
        this.setFrontAttachment(FrontAttachmentType.REGISTRY.getOrDefault(null));
    }

    public AutomobileEntity(Level world) {
        this(AutomobilityEntities.AUTOMOBILE.require(), world);
    }

    @Override
    public void recreateFromPacket(ClientboundAddEntityPacket packet) {
        super.recreateFromPacket(packet);
        if (level().isClientSide()) {
            ClientPackets.requestSyncAutomobileComponentsPacket(this);
        }
    }

    @Override
    public AutomobileFrame getFrame() {
        return frame;
    }

    @Override
    public AutomobileWheel getWheels() {
        return wheels;
    }

    @Override
    public AutomobileEngine getEngine() {
        return engine;
    }

    @Override
    public @Nullable RearAttachment getRearAttachment() {
        return rearAttachment;
    }

    @Override
    public @Nullable FrontAttachment getFrontAttachment() {
        return frontAttachment;
    }

    @Override
    public float getSteering(float tickDelta) {
        return Mth.lerp(tickDelta, lastSteering, steering);
    }

    @Override
    public float getWheelAngle(float tickDelta) {
        return Mth.lerp(tickDelta, lastWheelAngle, wheelAngle);
    }

    public float getBoostSpeed(float tickDelta) {
        return Mth.lerp(tickDelta, lastBoostSpeed, boostSpeed);
    }

    @Override
    public float getSuspensionBounce(float tickDelta) {
        return Mth.lerp(tickDelta, lastSusBounceTimer, suspensionBounceTimer);
    }

    @Override
    public boolean engineRunning() {
        return this.boostTimer > 0 || isVehicle();
    }

    @Override
    public int getTurboCharge() {
        return turboCharge;
    }

    @Override
    public long getTime() {
        return this.clientTime;
    }

    public float getHSpeed() {
        return hSpeed;
    }

    public float getVSpeed() {
        return vSpeed;
    }

    @Override
    public int getBoostTimer() {
        return boostTimer;
    }

    public double getEffectiveSpeed() {
        if (this.getControllingPassenger() instanceof Player player && player.isLocalPlayer()) {
            return Math.max(this.addedVelocity.length(), Math.abs(this.hSpeed));
        }

        return Math.max(this.addedVelocity.length(), Math.abs(this.engineSpeed + this.boostSpeed));
    }

    @Override
    public boolean automobileOnGround() {
        return automobileOnGround;
    }

    @Override
    public boolean debris() {
        return offRoad && hSpeed != 0;
    }

    @Override
    public Vector3f debrisColor() {
        return debrisColor;
    }

    public boolean burningOut() {
        return burningOut;
    }

    private void setDrifting(boolean drifting) {
        if (this.level().isClientSide()) {
            if (!this.drifting && drifting) {
                skidSound.accept(this);
            }

            if (this.drifting != drifting) {
                Platform.get().controllerCompat().updateDriftRumbleState(drifting);
            }
        }

        this.drifting = drifting;
    }

    private void setBurningOut(boolean burningOut) {
        if (this.level().isClientSide() && !this.drifting && !this.burningOut && burningOut) {
            skidSound.accept(this);
        }

        this.burningOut = burningOut;
    }

    public boolean isDrifting() {
        return this.drifting;
    }

    public <T extends RearAttachment> void setRearAttachment(RearAttachmentType<T> rearAttachment) {
        if (rearAttachment == null) {
            return;
        }
        if (this.rearAttachment == null || this.rearAttachment.type != rearAttachment) {
            if (this.rearAttachment != null) {
                this.rearAttachment.onRemoved();
            }

            this.rearAttachment = rearAttachment.constructor().apply(rearAttachment, this);
            this.rearAttachment.setYaw(this.getYRot());

            if (!level().isClientSide() && !this.rearAttachment.isRideable() && this.getPassengers().size() > 1) {
                this.getPassengers().get(1).stopRiding();
            }

            syncAttachments();
        }
    }

    public <T extends FrontAttachment> void setFrontAttachment(FrontAttachmentType<T> frontAttachment) {
        if (frontAttachment == null) {
            return;
        }
        if (this.frontAttachment == null || this.frontAttachment.type != frontAttachment) {
            if (this.frontAttachment != null) {
                this.frontAttachment.onRemoved();
            }
            this.frontAttachment = frontAttachment.constructor().apply(frontAttachment, this);

            syncAttachments();
        }
    }

    public void setComponents(AutomobileFrame frame, AutomobileWheel wheel, AutomobileEngine engine) {
        this.frame = frame;
        this.wheels = wheel;
        this.engine = engine;
        this.setMaxUpStep(wheels.size());
        this.stats.from(frame, wheel, engine);
        this.displacement.applyWheelbase(frame.model().wheelBase());
        if (!level().isClientSide()) syncComponents();
    }

    public void forNearbyPlayers(int radius, boolean ignoreDriver, Consumer<ServerPlayer> action) {
        for (Player p : level().players()) {
            if (ignoreDriver && p == getFirstPassenger()) {
                continue;
            }
            if (p.position().distanceTo(position()) < radius && p instanceof ServerPlayer player) {
                action.accept(player);
            }
        }
    }

    public Vec3 getTailPos() {
        return this.position()
                .add(new Vec3(0, 0, this.getFrame().model().rearAttachmentPos() * 0.0625)
                        .yRot((float) Math.toRadians(180 - this.getYRot()))
                );
    }

    public Vec3 getHeadPos() {
        return this.position()
                .add(new Vec3(0, 0, this.getFrame().model().frontAttachmentPos() * 0.0625)
                        .yRot((float) Math.toRadians(-this.getYRot()))
                );
    }

    public boolean hasSpaceForPassengers() {
        return (this.rearAttachment.isRideable()) ? (this.getPassengers().size() < 2) : (!this.isVehicle());
    }

    public void setSpeed(float horizontal, float vertical) {
        this.hSpeed = horizontal;
        this.vSpeed = vertical;
    }

    @Override
    public void tick() {
        boolean first = this.firstTick;

        if (lastWheelAngle != wheelAngle) markDirty();
        lastWheelAngle = wheelAngle;

        if (!this.wasEngineRunning && this.engineRunning() && this.level().isClientSide()) {
            engineSound.accept(this);
        }
        this.wasEngineRunning = this.engineRunning();

        if (!this.isVehicle() || !this.getFrontAttachment().canDrive(this.getFirstPassenger())) {
            accelerating = false;
            braking = false;
            steeringLeft = false;
            steeringRight = false;
            holdingDrift = false;
        }

        if (this.jumpCooldown > 0) {
            this.jumpCooldown--;
        }

        super.tick();
        if (!this.rearAttachment.type.isEmpty()) this.rearAttachment.tick();
        if (!this.frontAttachment.type.isEmpty()) this.frontAttachment.tick();

        var prevPos = this.position();

        positionTrackingTick();
        collisionStateTick();
        steeringTick();
        driftingTick();
        burnoutTick();

        movementTick();
        if (this.isControlledByLocalInstance()) {
            this.move(MoverType.SELF, this.getDeltaMovement());
        }
        postMovementTick();

        if (!level().isClientSide()) {
            var prevTailPos = this.prevTailPos != null ? this.prevTailPos : this.getTailPos();
            var tailPos = this.getTailPos();

            this.rearAttachment.pull(prevTailPos.subtract(tailPos));
            this.prevTailPos = tailPos;

            if (dirty) {
                syncData();
                dirty = false;
            }
            if (this.hasSpaceForPassengers() && !decorative) {
                var touchingEntities = this.level().getEntities(this, this.getBoundingBox().inflate(0.2, 0, 0.2), EntitySelector.pushableBy(this));
                for (Entity entity : touchingEntities) {
                    if (!entity.hasPassenger(this)) {
                        if (!entity.isPassenger() && entity.getBbWidth() <= this.getBbWidth() && entity instanceof Mob && !(entity instanceof WaterAnimal)) {
                            entity.startRiding(this);
                        }
                    }
                }
            }
            if (this.isVehicle()) {
                if (this.getFrontAttachment().canDrive(this.getFirstPassenger()) && this.getFirstPassenger() instanceof Mob mob) {
                    provideMobDriverInputs(mob);
                }

                this.despawnCountdown = 0;
            } else if (this.despawnTime > 0) {
                this.despawnCountdown++;

                if (this.despawnCountdown >= this.despawnTime) {
                    this.destroyAutomobile(false, RemovalReason.DISCARDED);
                }
            }
        } else {
            clientTime++;

            lastSusBounceTimer = suspensionBounceTimer;
            if (suspensionBounceTimer > 0) {
                suspensionBounceTimer--;
            }

            if (Math.abs(this.hSpeed) < 0.05 && !this.burningOut && this.getControllingPassenger() instanceof Player) {
                this.standStillTime = AUtils.shift(this.standStillTime, 0.05f, 1f);
            } else {
                this.standStillTime = AUtils.shift(this.standStillTime, 0.15f, -1.3f);
            }
        }

        displacementTick(first || (this.position().subtract(prevPos).length() > 0 || this.getYRot() != this.yRotO));
    }

    public void positionTrackingTick() {
        if (this.isControlledByLocalInstance()) {
            this.lerpTicks = 0;
            syncPacketPositionCodec(getX(), getY(), getZ());
        } else if (lerpTicks > 0) {
            this.setPos(
                    this.getX() + ((this.trackedX - this.getX()) / (double)this.lerpTicks),
                    this.getY() + ((this.trackedY - this.getY()) / (double)this.lerpTicks),
                    this.getZ() + ((this.trackedZ - this.getZ()) / (double)this.lerpTicks)
            );
            this.setYRot(this.getYRot() + (Mth.wrapDegrees(this.trackedYaw - this.getYRot()) / (float)this.lerpTicks));

            this.lerpTicks--;
        }
    }

    public void markDirty() {
        dirty = true;
    }

    private void syncData() {
        forNearbyPlayers(200, true, player -> CommonPackets.sendSyncAutomobileDataPacket(this, player));
    }

    private void syncComponents() {
        forNearbyPlayers(200, false, player -> CommonPackets.sendSyncAutomobileComponentsPacket(this, player));
    }

    private void syncAttachments() {
        forNearbyPlayers(200, false, player -> CommonPackets.sendSyncAutomobileAttachmentsPacket(this, player));
    }

    public ItemStack asPrefabItem() {
        var stack = new ItemStack(AutomobilityItems.AUTOMOBILE.require());
        var automobile = stack.getOrCreateTagElement("Automobile");
        automobile.putString("frame", frame.getId().toString());
        automobile.putString("wheels", wheels.getId().toString());
        automobile.putString("engine", engine.getId().toString());
        return stack;
    }

    @Nullable
    @Override
    public ItemStack getPickResult() {
        return asPrefabItem();
    }

    // making mobs drive automobiles
    // technically the mobs don't drive, instead the automobile
    // self-drives to the mob's destination...
    public void provideMobDriverInputs(Mob driver) {
        // Don't move if the driver doesn't exist or can't drive
        if (driver == null || driver.isDeadOrDying() || driver.isRemoved()) {
            if (accelerating || steeringLeft || steeringRight) markDirty();
            accelerating = false;
            steeringLeft = false;
            steeringRight = false;
            return;
        }

        var path = driver.getNavigation().getPath();
        // checks if there is a current, incomplete path that the entity has targeted
        if (path != null && !path.isDone() && path.getEndNode() != null) {
            // determines the relative position to drive to, based on the end of the path
            var pos = path.getEndNode().asVec3().subtract(position());
            // determines the angle to that position
            double target = Mth.wrapDegrees(Math.toDegrees(Math.atan2(pos.x(), pos.z())));
            // determines another relative position, this time to the path's current node (in the case of the path directly to the end being obstructed)
            var fnPos = path.getNextNode().asVec3().subtract(position());
            // determines the angle to that current node's position
            double fnTarget = Mth.wrapDegrees(Math.toDegrees(Math.atan2(fnPos.x(), fnPos.z())));
            // if the difference in angle between the end position and the current node's position is too great,
            // the automobile will drive to that current node under the assumption that the path directly to the
            // end is obstructed
            if (Math.abs(target - fnTarget) > 69) {
                pos = fnPos;
                target = fnTarget;
            }
            // fixes up the automobile's own yaw value
            float yaw = Mth.wrapDegrees(-getYRot());
            // finds the difference between the target angle and the yaw
            double offset = Mth.wrapDegrees(yaw - target);
            // whether the automobile should go in reverse
            boolean reverse = false;
            // a value to determine the threshold used to determine whether the automobile is moving
            // both slow enough and is at an extreme enough offset angle to incrementally move in reverse
            float mul = 0.5f + (Mth.clamp(hSpeed, 0, 1) * 0.5f);
            if (pos.length() < 20 * mul && Math.abs(offset) > 180 - (170 * mul)) {
                long time = level().getGameTime();
                // this is so that the automobile alternates between reverse and forward,
                // like a driver would do in order to angle their vehicle toward a target location
                reverse = (time % 80 <= 30);
            }
            // set the accel/brake inputs
            accelerating = !reverse;
            braking = reverse;
            // set the steering inputs, with a bit of a dead zone to prevent jittering
            if (offset < -7) {
                steeringRight = reverse;
                steeringLeft = !reverse;
            } else if (offset > 7) {
                steeringRight = !reverse;
                steeringLeft = reverse;
            }
            markDirty();
        } else {
            if (accelerating || steeringLeft || steeringRight) markDirty();
            accelerating = false;
            steeringLeft = false;
            steeringRight = false;
        }
    }

    public void movementTick() {
        // Handles boosting
        lastBoostSpeed = boostSpeed;
        if (boostTimer > 0) {
            boostTimer--;
            boostSpeed = Math.min(boostPower, boostSpeed + 0.09f);
            if (engineSpeed < stats.getComfortableSpeed()) {
                engineSpeed += 0.012f;
            }
            markDirty();
        } else {
            boostSpeed = AUtils.zero(boostSpeed, 0.09f);
        }

        // Get block below's friction
        var blockBelow = new BlockPos((int) getX(), (int) (getY() - 0.05), (int) getZ());
        this.grip = 1 - ((Mth.clamp((level().getBlockState(blockBelow).getBlock().getFriction() - 0.6f) / 0.4f, 0, 1) * (1 - stats.getGrip() * 0.8f)));
        this.grip *= this.grip;

        // Bounce on gel
        if (this.automobileOnGround && this.jumpCooldown <= 0 && level().getBlockState(this.blockPosition()).getBlock() instanceof LaunchGelBlock) {
            this.setSpeed(Math.max(this.getHSpeed(), 0.1f), Math.max(this.getVSpeed(), 0.9f));
            this.jumpCooldown = 5;
            this.automobileOnGround = false;
        }

        // Track the last position of the automobile
        this.lastPosForDisplacement = position();

        // cumulative will be modified by the following code and then the automobile will be moved by it
        // Currently initialized with the value of addedVelocity (which is a general velocity vector applied to the automobile, i.e. for when it bumps into a wall and is pushed back)
        var cumulative = addedVelocity;

        // Reduce gravity underwater
        cumulative = cumulative.add(0, (vSpeed * (isUnderWater() ? 0.15f : 1)), 0);

        // This is the general direction the automobile will move, which is slightly offset to the side when drifting
        this.speedDirection = getYRot() - (drifting ? Math.min(turboCharge * 6, 43 + (-steering * 12)) * driftDir : -steering * 12); //MathHelper.lerp(grip, getYaw(), getYaw() - (drifting ? Math.min(turboCharge * 6, 43 + (-steering * 12)) * driftDir : -steering * 12));

        // Handle acceleration
        if (accelerating) {
            float speed = Math.max(this.engineSpeed, 0);
            // yeah ...
            this.engineSpeed +=
                    // The following conditions check whether the automobile should NOT receive normal acceleration
                    // It will not receive this acceleration if the automobile is steering or tight-drifting
                    (
                            (this.drifting && AUtils.haveSameSign(this.steering, this.driftDir)) ||
                            (!this.drifting && this.steering != 0 && hSpeed > 0.5)
                    ) ? (this.hSpeed < stats.getComfortableSpeed() ? 0.001 : 0) // This will supply a small amount of acceleration if the automobile is moving slowly only

                    // Otherwise, it will receive acceleration as normal
                    // It will receive this acceleration if the automobile is moving straight or wide-drifting (the latter slightly reduces acceleration)
                    : calculateAcceleration(speed, stats) * (drifting ? 0.86 : 1) * (engineSpeed > stats.getComfortableSpeed() ? 0.25f : 1) * grip;
        }

        // Handle braking/reverse
        if (braking) {
            this.engineSpeed = Math.max(this.engineSpeed - 0.15f, -0.25f);
        }
        // Handle when the automobile is rolling to a stop
        if (!accelerating && !braking) {
            this.engineSpeed = AUtils.zero(this.engineSpeed, 0.025f);
        }

        // Slow the automobile a bit while steering and moving fast
        if (!drifting && steering != 0 && hSpeed > 0.8) {
            engineSpeed -= engineSpeed * 0.00042f;
        }

        if (this.burningOut()) {
            engineSpeed -= (engineSpeed) * 0.5;
        }

        // Allows for the sticky slope effect to continue for a tick after not being on a slope
        // This prevents the automobile from randomly jumping if it's moving down a slope quickly
        var below = new BlockPos((int) getX(), (int) (getY() - 0.51), (int) getZ());
        var state = level().getBlockState(below);
        if (state.is(Automobility.STICKY_SLOPES)) {
            slopeStickingTimer = 1;
        } else {
            slopeStickingTimer = Math.max(0, slopeStickingTimer--);
        }

        // Handle being in off-road
        if (boostSpeed < 0.4f && level().getBlockState(blockPosition()).getBlock() instanceof OffRoadBlock offRoad) {
            int layers = level().getBlockState(blockPosition()).getValue(OffRoadBlock.LAYERS);
            float cap = stats.getComfortableSpeed() * (1 - ((float)layers / 3.5f));
            engineSpeed = Math.min(cap, engineSpeed);
            this.debrisColor = offRoad.color;
            this.offRoad = true;
        } else this.offRoad = false;

        // Set the horizontal speed
        if (!burningOut()) hSpeed = engineSpeed + boostSpeed;

        // Sticking to sticky slopes
        double lowestPrevYDisp = 0;
        for (double d : prevYDisplacements) {
            lowestPrevYDisp = Math.min(d, lowestPrevYDisp);
        }
        if (slopeStickingTimer > 0 && automobileOnGround && lowestPrevYDisp <= 0) {
            double cumulHSpeed = Math.sqrt((cumulative.x * cumulative.x) + (cumulative.z * cumulative.z));
            cumulative = cumulative.add(0, -(0.25 + cumulHSpeed), 0);
        }


        float angle = (float) Math.toRadians(-speedDirection);
        if (this.burningOut()) {
            if (Math.abs(hSpeed) > 0.02) {
                this.addedVelocity = new Vec3(Math.sin(angle) * hSpeed, 0, Math.cos(angle) * hSpeed);
                this.hSpeed = 0;
                cumulative = cumulative.add(addedVelocity);
            }
        } else {
            // Apply the horizontal speed to the cumulative movement
            cumulative = cumulative.add(Math.sin(angle) * hSpeed, 0, Math.cos(angle) * hSpeed);
        }

        cumulative = cumulative.scale(this.grip).add(this.lastVelocity.scale(1 - this.grip));
        if (cumulative.length() < 0.001) {
            cumulative = Vec3.ZERO;
        }

        // Turn the wheels
        float wheelCircumference = (float)(2 * (wheels.model().radius() / 16) * Math.PI);
        if (hSpeed > 0) markDirty();
        wheelAngle += 300 * (hSpeed / wheelCircumference) + (hSpeed > 0 ? ((1 - grip) * 15) : 0); // made it a bit slower intentionally, also make it spin more when on slippery surface

        // Set the automobile's velocity
        if (this.isControlledByLocalInstance()) {
            this.setDeltaMovement(cumulative);
        }
        this.markHurt();
        this.hasImpulse = true;

        lastVelocity = cumulative;

        // Damage and launch entities that are hit by a moving automobile
        if (Math.abs(hSpeed) > 0.2) {
            runOverEntities(cumulative);
        }
    }

    public void runOverEntities(Vec3 velocity) {
        var frontBox = getBoundingBox().move(velocity.scale(0.5));
        var velAdd = velocity.add(0, 0.1, 0).scale(3);
        for (var entity : level().getEntities(EntityTypeTest.forClass(Entity.class), frontBox, entity -> entity != this && entity != getFirstPassenger())) {
            if (!entity.isInvulnerable()) {
                if (entity instanceof LivingEntity living && entity.getVehicle() != this) {
                    AutomobilityEntities.automobileDamageSource(level()).ifPresent(dmg -> living.hurt(dmg, hSpeed * 10));

                    entity.push(velAdd.x, velAdd.y, velAdd.z);
                }
            }
        }
    }

    public void postMovementTick() {
        float addedVelReduction = 0.1f;
        if (this.burningOut()) {
            addedVelReduction = 0.05f;
        }

        // Reduce the values of addedVelocity incrementally
        double addVelLen = addedVelocity.length();
        if (addVelLen > 0) addedVelocity = addedVelocity.scale(Math.max(0, addVelLen - addedVelReduction) / addVelLen);

        float angle = (float) Math.toRadians(-speedDirection);
        if (touchingWall && hSpeed > 0.1 && addedVelocity.length() <= 0) {
            engineSpeed /= 3.6;
            double knockSpeed = ((-0.2 * hSpeed) - 0.5);
            addedVelocity = addedVelocity.add(Math.sin(angle) * knockSpeed, 0, Math.cos(angle) * knockSpeed);

            level().playLocalSound(this.getX(), this.getY(), this.getZ(), AutomobilitySounds.COLLISION.require(), SoundSource.AMBIENT, 0.76f, 0.65f + (0.06f * (this.level().random.nextFloat() - 0.5f)), true);

            if (isVehicle() && level().isClientSide()) {
                if (getPassengers().stream().anyMatch(p -> p instanceof LocalPlayer)) {
                    Platform.get().controllerCompat().crashRumble();
                }
            }

        }

        double yDisp = position().subtract(this.lastPosForDisplacement).y();

        // Increment the falling timer
        if (!automobileOnGround && yDisp < 0) {
            fallTicks += 1;
        } else {
            fallTicks = 0;
        }

        // Handle launching off slopes
        double highestPrevYDisp = 0;
        for (double d : prevYDisplacements) {
            highestPrevYDisp = Math.max(d, highestPrevYDisp);
        }
        if (wasOnGround && !automobileOnGround && !isFloorDirectlyBelow) {
            vSpeed = (float)Mth.clamp(highestPrevYDisp, 0, hSpeed * 0.6f);
        }

        // Handles gravity
        vSpeed = Math.max(vSpeed - 0.08f, !automobileOnGround ? TERMINAL_VELOCITY : -0.01f);

        // Store previous y displacement to use when launching off slopes
        prevYDisplacements.push(yDisp);
        if (prevYDisplacements.size() > 2) {
            prevYDisplacements.removeLast();
        }

        // Handle setting the locked view offset
        if (hSpeed != 0) {
            float vOTarget = (drifting ? driftDir * -23 : steering * -5.6f);
            if (vOTarget == 0) lockedViewOffset = AUtils.zero(lockedViewOffset, 2.5f);
            else {
                if (lockedViewOffset < vOTarget) lockedViewOffset = Math.min(lockedViewOffset + 3.7f, vOTarget);
                else lockedViewOffset = Math.max(lockedViewOffset - 3.7f, vOTarget);
            }
        }

        float newAngularSpeed = this.angularSpeed;
        if (this.burningOut()) {
            float speed = (float) this.addedVelocity.length();
            float acc = (1.7f / (1 + this.frame.weight())) + (4 * speed);
            float lim = 9 + (4 * speed);
            if (this.steering != 0) {
                newAngularSpeed = Mth.clamp(newAngularSpeed + (acc * this.steering), -lim, lim);
            } else {
                newAngularSpeed = AUtils.shift(newAngularSpeed, acc * 0.5f, 0);
            }
        } else if (hSpeed != 0) {
            float traction = (1 / (1 + (4 * this.hSpeed))) + (0.3f * this.stats.getGrip());
            newAngularSpeed = AUtils.shift(newAngularSpeed, 6 * traction,
                    (drifting ? (((this.steering + (driftDir)) * driftDir * 2.5f + 1.5f) * driftDir) * (((1 - stats.getGrip()) + 2) / 2.5f) : this.steering * ((4f * Math.min(hSpeed, 1)) + (hSpeed > 0 ? 2 : -3.5f))) * ((stats.getHandling() + 1) / 2));
        } else {
            newAngularSpeed = AUtils.shift(newAngularSpeed, 3, 0);
        }

        this.angularSpeed = (newAngularSpeed * this.grip) + (this.angularSpeed * (1 - this.grip));
        if (Math.abs(this.angularSpeed) < 0.00003) {
            this.angularSpeed = 0;
        }

        // Turns the automobile based on steering/drifting
        if (hSpeed != 0 || this.burningOut()) {
            float yawInc = angularSpeed;// + (drifting ? (((this.steering + (driftDir)) * driftDir * 2.5f + 1.5f) * driftDir) * (((1 - stats.getGrip()) + 2) / 2.5f) : this.steering * ((4f * Math.min(hSpeed, 1)) + (hSpeed > 0 ? 2 : -3.5f))) * ((stats.getHandling() + 1) / 2);
            float prevYaw = getYRot();
            this.setYRot(getYRot() + yawInc);
            if (level().isClientSide) {
                var passenger = getFirstPassenger();
                if (passenger instanceof Player player) {
                    if (inLockedViewMode()) {
                        player.setYRot(Mth.wrapDegrees(getYRot() + lockedViewOffset));
                        player.setYBodyRot(Mth.wrapDegrees(getYRot() + lockedViewOffset));
                    } else {
                        player.setYRot(Mth.wrapDegrees(player.getYRot() + yawInc));
                        player.setYBodyRot(Mth.wrapDegrees(player.getYRot() + yawInc));
                    }
                }
            } else {
                for (Entity e : getPassengers()) {
                    if (e == getFirstPassenger()) {
                        e.setYRot(Mth.wrapDegrees(e.getYRot() + yawInc));
                        e.setYBodyRot(Mth.wrapDegrees(e.getYRot() + yawInc));
                    }
                }
            }
            if (level().isClientSide()) {
                this.yRotO = prevYaw;
            }
        }
    }

    @Override
    public void move(MoverType movementType, Vec3 movement) {
        if (!this.level().isClientSide() && movementType == MoverType.PLAYER) {
            AUtils.IGNORE_ENTITY_GROUND_CHECK_STEPPING = true;
        }
        super.move(movementType, movement);
    }

    @Override
    public boolean causeFallDamage(float fallDistance, float damageMultiplier, DamageSource damageSource) {
        return false; // Riders shouldn't take fall damage
    }

    public void accumulateCollisionAreas(Collection<CollisionArea> areas) {
        this.level().getEntitiesOfClass(Entity.class, this.getBoundingBox().inflate(3, 3, 3), e -> e != this && e.getVehicle() != this)
                .forEach(e -> areas.add(CollisionArea.entity(e)));
    }

    public void displacementTick(boolean tick) {
        if (this.level().isClientSide()) {
            this.displacement.preTick();

            if (tick) {
                this.displacement.otherColliders.clear();
                this.accumulateCollisionAreas(this.displacement.otherColliders);

                this.displacement.tick(this.level(), this, this.position(), this.getYRot(), this.maxUpStep());
            }

            if (level().getBlockState(this.blockPosition()).getBlock() instanceof AutomobileAssemblerBlock) {
                this.displacement.lastVertical = this.displacement.verticalTarget = (-this.wheels.model().radius() / 16);
            }
        }
    }

    public Displacement getDisplacement() {
        return this.displacement;
    }

    public void collisionStateTick() {
        wasOnGround = automobileOnGround;
        automobileOnGround = false;
        isFloorDirectlyBelow = false;
        touchingWall = false;
        var b = getBoundingBox();
        var groundBox = new AABB(b.minX, b.minY - 0.04, b.minZ, b.maxX, b.minY, b.maxZ);
        var wid = (b.getXsize() + b.getZsize()) * 0.5f;
        var floorBox = new AABB(b.minX + (wid * 0.94), b.minY - 0.05, b.minZ + (wid * 0.94), b.maxX - (wid * 0.94), b.minY, b.maxZ - (wid * 0.94));
        var wallBox = b.deflate(0.05).move(this.lastVelocity.normalize().scale(0.12));
        var start = new BlockPos((int) (b.minX - 0.1), (int) (b.minY - 0.2), (int) (b.minZ - 0.1));
        var end = new BlockPos((int) (b.maxX + 0.1), (int) (b.maxY + 0.2 + this.maxUpStep()), (int) (b.maxZ + 0.1));
        var groundCuboid = Shapes.create(groundBox);
        var floorCuboid = Shapes.create(floorBox);
        var wallCuboid = Shapes.create(wallBox);
        var stepWallCuboid = wallCuboid.move(0, this.maxUpStep() - 0.05, 0);
        boolean wallHit = false;
        boolean stepWallHit = false;
        var shapeCtx = CollisionContext.of(this);
        if (this.level().hasChunksAt(start, end)) {
            var pos = new BlockPos.MutableBlockPos();
            for(int x = start.getX(); x <= end.getX(); ++x) {
                for(int y = start.getY(); y <= end.getY(); ++y) {
                    for(int z = start.getZ(); z <= end.getZ(); ++z) {
                        pos.set(x, y, z);
                        var state = this.level().getBlockState(pos);
                        var blockShape = state.getCollisionShape(this.level(), pos, shapeCtx).move(pos.getX(), pos.getY(), pos.getZ());
                        this.automobileOnGround |= Shapes.joinIsNotEmpty(blockShape, groundCuboid, BooleanOp.AND);
                        this.isFloorDirectlyBelow |= Shapes.joinIsNotEmpty(blockShape, floorCuboid, BooleanOp.AND);
                        wallHit |= Shapes.joinIsNotEmpty(blockShape, wallCuboid, BooleanOp.AND);
                        stepWallHit |= Shapes.joinIsNotEmpty(blockShape, stepWallCuboid, BooleanOp.AND);
                    }
                }
            }
        }
        this.touchingWall = (wallHit && stepWallHit);

        var otherColliders = new HashSet<CollisionArea>();
        this.accumulateCollisionAreas(otherColliders);
        this.automobileOnGround |= otherColliders.stream().anyMatch(col -> col.boxIntersects(groundBox));
    }

    public void lerpTo(double x, double y, double z, float yaw, float pitch, int interpolationSteps, boolean interpolate) {
        this.trackedX = x;
        this.trackedY = y;
        this.trackedZ = z;
        this.trackedYaw = yaw;
        this.lerpTicks = this.getType().updateInterval() + 1;
    }

    private float calculateAcceleration(float speed, AutomobileStats stats) {
        // A somewhat over-engineered function to accelerate the automobile, since I didn't want to add a hard speed cap
        return (1 / ((300 * speed) + (18.5f - (stats.getAcceleration() * 5.3f)))) * (0.9f * ((stats.getAcceleration() + 1) / 2));
    }

    public void provideClientInput(boolean fwd, boolean back, boolean left, boolean right, boolean space) {
        // Receives inputs client-side and sends them to the server
        if (!(
                fwd == accelerating &&
                back == braking &&
                left == steeringLeft &&
                right == steeringRight &&
                space == holdingDrift
        )) {
            setInputs(fwd, back, left, right, space);
            ClientPackets.sendSyncAutomobileInputPacket(this, accelerating, braking, steeringLeft, steeringRight, holdingDrift);
        }
    }

    public void setInputs(boolean fwd, boolean back, boolean left, boolean right, boolean space) {
        this.accelerating = fwd;
        this.braking = back;
        this.steeringLeft = left;
        this.steeringRight = right;
        this.holdingDrift = space;
    }

    public void boost(float power, int time) {
        if (power > boostPower || time > boostTimer) {
            boostTimer = time;
            boostPower = power;
        }
        if (this.isControlledByLocalInstance()) {
            this.engineSpeed = Math.max(this.engineSpeed, this.stats.getComfortableSpeed() * 0.5f);
        }
    }

    private void steeringTick() {
        // Adjust the steering based on the left/right inputs
        this.lastSteering = steering;
        if (steeringLeft == steeringRight) {
            this.steering = AUtils.zero(this.steering, 0.42f);
        } else if (steeringLeft) {
            this.steering -= 0.42f;
            this.steering = Math.max(-1, this.steering);
        } else {
            this.steering += 0.42f;
            this.steering = Math.min(1, this.steering);
        }
    }

    private void consumeTurboCharge() {
        if (turboCharge > LARGE_TURBO_TIME) {
            boost(0.38f, 38);
        } else if (turboCharge > MEDIUM_TURBO_TIME) {
            boost(0.3f, 21);
        } else if (turboCharge > SMALL_TURBO_TIME) {
            boost(0.23f, 9);
        }
        turboCharge = 0;
    }

    private void driftingTick() {
        // Handles starting a drift
        if (!prevHoldDrift && holdingDrift) {
            if (steering != 0 && !drifting && hSpeed > 0.4f && automobileOnGround) {
                setDrifting(true);
                driftDir = steering > 0 ? 1 : -1;
                // Reduce speed when a drift starts, based on how long the last drift was for
                // This allows you to do a series of short drifts without tanking all your speed, while still reducing your speed when you begin the drift(s)
                engineSpeed -= 0.028 * engineSpeed;
            } else if (steering == 0 && !this.level().isClientSide() && this.getRearAttachment() instanceof DeployableRearAttachment att) {
                att.deploy();
            }
        }

        // Handles drifting effects, ending a drift, and the drift timer (for drift turbos)
        if (drifting) {
            if (this.automobileOnGround()) createDriftParticles();
            // Ending a drift successfully, giving you a turbo boost
            if (prevHoldDrift && !holdingDrift) {
                setDrifting(false);
                consumeTurboCharge();
            // Ending a drift unsuccessfully, not giving you a boost
            } else if (hSpeed < 0.33f) {
                setDrifting(false);
                turboCharge = 0;
            }
            if (automobileOnGround) turboCharge += ((steeringLeft && driftDir < 0) || (steeringRight && driftDir > 0)) ? 2 : 1;
        }

        this.prevHoldDrift = this.holdingDrift;
    }

    private void endBurnout() {
        setBurningOut(false);
        this.engineSpeed = 0;
    }

    private void burnoutTick() {
        if (this.burningOut()) {
            if (this.automobileOnGround()) {
                if (this.addedVelocity.length() > 0.05 || Math.abs(this.angularSpeed) > 0.05) {
                    createDriftParticles();
                }
                if (hSpeed < 0.08 && turboCharge <= SMALL_TURBO_TIME) turboCharge += 1;
            }
            if (!this.braking) {
                endBurnout();
                consumeTurboCharge();
            } else if (!this.accelerating) {
                endBurnout();
                this.turboCharge = 0;
            }
            this.wheelAngle += 20;
        } else if ((this.accelerating || hSpeed > 0.05) && this.braking) {
            setBurningOut(true);
            this.turboCharge = 0;
        }
    }

    public void createDriftParticles() {
        var origin = this.position().add(0, this.displacement.verticalTarget, 0);
        for (var wheel : this.getFrame().model().wheelBase().wheels) {
            if (wheel.end() == WheelBase.WheelEnd.BACK) {
                var pos = new Vec3(wheel.right() + ((wheel.right() > 0 ? 1 : -1) * this.getWheels().model().width() * wheel.scale()), 0, wheel.forward())
                        .xRot((float) Math.toRadians(this.displacement.currAngularX))
                        .zRot((float) Math.toRadians(this.displacement.currAngularZ))
                        .yRot((float) Math.toRadians(-this.getYRot())).scale(0.0625).add(0, 0.4, 0);
                level().addParticle(AutomobilityParticles.DRIFT_SMOKE.require(), origin.x + pos.x, origin.y + pos.y, origin.z + pos.z, 0, 0, 0);
            }
        }
    }

    private static boolean inLockedViewMode() {
        return Platform.get().controllerCompat().inControllerMode();
    }

    @Override
    public float getAutomobileYaw(float tickDelta) {
        return getViewYRot(tickDelta);
    }

    @Override
    public float getRearAttachmentYaw(float tickDelta) {
        return this.rearAttachment.yaw(tickDelta);
    }

    @Nullable
    @Override
    public LivingEntity getControllingPassenger() {
        final var firstPassenger = getFirstPassenger();

        if (firstPassenger instanceof LivingEntity living) {
            return living;
        } else {
            return null;
        }
    }

    @Override
    protected boolean canAddPassenger(Entity passenger) {
        return this.hasSpaceForPassengers();
    }

    @Override
    public boolean hasInventory() {
        return this.getRearAttachment().hasMenu();
    }

    @Override
    public void openInventory(Player player) {
        var factory = this.getRearAttachment().createMenu(new AutomobileContainerLevelAccess(this));
        if (factory != null) {
            player.openMenu(factory);
        }
    }

    public float getStandStillTime() {
        return this.standStillTime;
    }

    public void playHitSound(Vec3 pos) {
        level().gameEvent(this, GameEvent.ENTITY_DAMAGE, pos);
        level().playSound(null, this.getX(), this.getY(), this.getZ(), SoundEvents.COPPER_BREAK, SoundSource.AMBIENT, 1, 0.9f + (this.level().random.nextFloat() * 0.2f));
    }

    private void dropParts(Vec3 pos) {
        level().addFreshEntity(new ItemEntity(level(), pos.x, pos.y, pos.z, AutomobilityItems.AUTOMOBILE_FRAME.require().createStack(this.getFrame())));
        level().addFreshEntity(new ItemEntity(level(), pos.x, pos.y, pos.z, AutomobilityItems.AUTOMOBILE_ENGINE.require().createStack(this.getEngine())));

        var wheelStack = AutomobilityItems.AUTOMOBILE_WHEEL.require().createStack(this.getWheels());
        wheelStack.setCount(this.getFrame().model().wheelBase().wheelCount);
        level().addFreshEntity(new ItemEntity(level(), pos.x, pos.y, pos.z, wheelStack));
    }

    public void destroyRearAttachment(boolean drop) {
        if (drop) {
            var dropPos = this.rearAttachment.pos();
            level().addFreshEntity(new ItemEntity(level(), dropPos.x, dropPos.y, dropPos.z,
                    AutomobilityItems.REAR_ATTACHMENT.require().createStack(this.getRearAttachmentType())));
        }
        this.setRearAttachment(RearAttachmentType.EMPTY);
    }

    public void destroyFrontAttachment(boolean drop) {
        if (drop) {
            var dropPos = this.frontAttachment.pos();
            level().addFreshEntity(new ItemEntity(level(), dropPos.x, dropPos.y, dropPos.z,
                    AutomobilityItems.FRONT_ATTACHMENT.require().createStack(this.getFrontAttachmentType())));
        }
        this.setFrontAttachment(FrontAttachmentType.EMPTY);
    }

    public void destroyAutomobile(boolean drop, RemovalReason reason) {
        if (!this.rearAttachment.type.isEmpty()) {
            this.destroyRearAttachment(drop);
        }
        if (!this.frontAttachment.type.isEmpty()) {
            this.destroyFrontAttachment(drop);
        }
        if (drop) {
            this.dropParts(this.position().add(0, 0.3, 0));
        }
        this.remove(reason);
    }

    @Override
    public InteractionResult interact(Player player, InteractionHand hand) {
        if (player.isShiftKeyDown()) {
            if (this.hasInventory()) {
                if (!level().isClientSide()) {
                    openInventory(player);
                    return InteractionResult.PASS;
                } else {
                    return InteractionResult.SUCCESS;
                }
            }
        }

        var stack = player.getItemInHand(hand);
        if ((!this.decorative || player.isCreative()) && stack.is(AutomobilityItems.CROWBAR.require())) {
            double playerAngle = Math.toDegrees(Math.atan2(player.getZ() - this.getZ(), player.getX() - this.getX()));
            double angleDiff = Mth.wrapDegrees(this.getYRot() - playerAngle);

            if (angleDiff < 0 && !this.frontAttachment.type.isEmpty()) {
                this.destroyFrontAttachment(!player.isCreative());
                this.playHitSound(this.getHeadPos());

                return InteractionResult.sidedSuccess(level().isClientSide);
            } else if (!this.rearAttachment.type.isEmpty()) {
                this.destroyRearAttachment(!player.isCreative());
                this.playHitSound(this.rearAttachment.pos());

                return InteractionResult.sidedSuccess(level().isClientSide);
            } else {
                this.destroyAutomobile(!player.isCreative(), RemovalReason.KILLED);
                this.playHitSound(this.position());

                return InteractionResult.sidedSuccess(level().isClientSide);
            }
        }

        if (!decorative) {
            if (stack.getItem() instanceof AutomobileInteractable interactable) {
                return interactable.interactAutomobile(stack, player, hand, this);
            }

            if (!this.hasSpaceForPassengers()) {
                if (!(this.getFirstPassenger() instanceof Player)) {
                    if (!level().isClientSide()) {
                        this.getFirstPassenger().stopRiding();
                    }
                    return InteractionResult.sidedSuccess(level().isClientSide);
                }
                return InteractionResult.PASS;
            }
            if (!level().isClientSide()) {
                player.startRiding(this);
            }
            return InteractionResult.sidedSuccess(level().isClientSide());
        }

        return InteractionResult.PASS;
    }

    @Override
    public double getPassengersRidingOffset() {
        return ((wheels.model().radius() + frame.model().seatHeight() - 4) / 16);
    }

    @Override
    public void positionRider(Entity passenger, Entity.MoveFunction moveFunc) {
        if (passenger == this.getFirstPassenger()) {
            var pos = this.position().add(0, this.displacement.verticalTarget + passenger.getMyRidingOffset(), 0)
                    .add(new Vec3(0, this.getPassengersRidingOffset(), 0)
                        .xRot((float) Math.toRadians(-this.displacement.currAngularX))
                        .zRot((float) Math.toRadians(-this.displacement.currAngularZ)));

            moveFunc.accept(passenger, pos.x, pos.y, pos.z);
        } else if (this.hasPassenger(passenger)) {
            var pos = this.position().add(
                    new Vec3(0, this.displacement.verticalTarget, this.getFrame().model().rearAttachmentPos() * 0.0625)
                        .yRot((float) Math.toRadians(180 - this.getYRot())).add(0, this.rearAttachment.getPassengerHeightOffset() + passenger.getMyRidingOffset() - 0.14, 0)
                        .add(this.rearAttachment.scaledYawVec())
                        .xRot((float) Math.toRadians(-this.displacement.currAngularX))
                        .zRot((float) Math.toRadians(-this.displacement.currAngularZ)));

            moveFunc.accept(passenger, pos.x, pos.y, pos.z);
        }
    }

    @Override
    public boolean canCollideWith(Entity other) {
        return Boat.canVehicleCollide(this, other);
    }

    @Override
    public boolean canBeCollidedWith() {
        return true;
    }

    @Override
    public boolean isPickable() {
        return !this.isRemoved();
    }

    @Override
    public boolean isPushable() {
        return true;
    }

    @Override
    protected void defineSynchedData() {
        this.entityData.define(REAR_ATTACHMENT_YAW, 0f);
        this.entityData.define(REAR_ATTACHMENT_ANIMATION, 0f);
    }

    @Override
    public void onSyncedDataUpdated(EntityDataAccessor<?> data) {
        super.onSyncedDataUpdated(data);

        if (REAR_ATTACHMENT_YAW.equals(data)) {
            this.rearAttachment.onTrackedYawUpdated(getTrackedRearAttachmentYaw());
        } else if (REAR_ATTACHMENT_ANIMATION.equals(data)) {
            this.rearAttachment.onTrackedAnimationUpdated(getTrackedRearAttachmentAnimation());
        } else if (FRONT_ATTACHMENT_ANIMATION.equals(data)) {
            this.frontAttachment.onTrackedAnimationUpdated(getTrackedFrontAttachmentAnimation());
        }
    }

    @Override
    public Packet<ClientGamePacketListener> getAddEntityPacket() {
        return new ClientboundAddEntityPacket(this);
    }

    public void setTrackedRearAttachmentYaw(float value) {
        this.entityData.set(REAR_ATTACHMENT_YAW, value);
    }

    public float getTrackedRearAttachmentYaw() {
        return this.entityData.get(REAR_ATTACHMENT_YAW);
    }

    public void setTrackedRearAttachmentAnimation(float animation) {
        this.entityData.set(REAR_ATTACHMENT_ANIMATION, animation);
    }

    public float getTrackedRearAttachmentAnimation() {
        return this.entityData.get(REAR_ATTACHMENT_ANIMATION);
    }

    public void setTrackedFrontAttachmentAnimation(float animation) {
        this.entityData.set(FRONT_ATTACHMENT_ANIMATION, animation);
    }

    public float getTrackedFrontAttachmentAnimation() {
        return this.entityData.get(FRONT_ATTACHMENT_ANIMATION);
    }

    public void bounce() {
        suspensionBounceTimer = 3;
        level().playLocalSound(this.getX(), this.getY(), this.getZ(), AutomobilitySounds.LANDING.require(), SoundSource.AMBIENT, 1, 1.5f + (0.15f * (this.level().random.nextFloat() - 0.5f)), true);
    }

    public static final class Displacement {
        private static final int SCAN_STEPS_PER_BLOCK = 20;
        private static final double INV_SCAN_STEPS = 1d / SCAN_STEPS_PER_BLOCK;

        private boolean wereAllOnGround = true;
        private float lastVertical = 0;
        private float lastAngularX = 0;
        private float lastAngularZ = 0;
        private float currAngularX = 0;
        private float currAngularZ = 0;
        private float verticalTarget = 0;
        private float angularXTarget = 0;
        private float angularZTarget = 0;
        private final List<Vec3> scanPoints = new ArrayList<>();
        public final Set<CollisionArea> otherColliders = new HashSet<>();

        public void preTick() {
            this.lastAngularX = currAngularX;
            this.lastAngularZ = currAngularZ;
            this.lastVertical = verticalTarget;

            this.currAngularX = AUtils.shift(this.currAngularX, 9, this.angularXTarget);
            this.currAngularZ = AUtils.shift(this.currAngularZ, 9, this.angularZTarget);
        }

        public void tick(Level world, AutomobileEntity entity, Vec3 centerPos, double yaw, double stepHeight) {
            yaw = 360 - yaw;
            Vec3 lowestDisplacementPos = null;
            Vec3 highestDisplacementPos = null;
            var scannedPoints = new ArrayList<Vec3>();
            var colliders = new HashSet<CollisionArea>();
            boolean anyOnGround = false;
            boolean allOnGround = true;
            for (var scanPoint : scanPoints) {
                scanPoint = scanPoint
                        .yRot((float) Math.toRadians(yaw));
                var pointPos = scanPoint.add(centerPos);
                colliders.clear();
                colliders.addAll(this.otherColliders);

                double scanDist = scanPoint.length();

                int heightOffset = (int) Math.ceil(scanDist);
                var iter = new Cursor3D(
                        (int) Math.min(Math.floor(centerPos.x), Math.floor(pointPos.x)),
                        (int) Math.floor(centerPos.y) - heightOffset,
                        (int) Math.min(Math.floor(centerPos.z), Math.floor(pointPos.z)),
                        (int) Math.max(Math.floor(centerPos.x), Math.floor(pointPos.x)),
                        (int) Math.floor(centerPos.y) + heightOffset,
                        (int) Math.max(Math.floor(centerPos.z), Math.floor(pointPos.z))
                );

                var mpos = new BlockPos.MutableBlockPos();
                while (iter.advance()) {
                    mpos.set(iter.nextX(), iter.nextY(), iter.nextZ());
                    var shape = world.getBlockState(mpos).getCollisionShape(world, mpos);
                    if (!shape.isEmpty()) {
                        if (shape == Shapes.block()) {
                            colliders.add(CollisionArea.box(mpos.getX(), mpos.getY() - (INV_SCAN_STEPS * 2), mpos.getZ(), mpos.getX() + 1, mpos.getY() + 1, mpos.getZ() + 1));
                        } else {
                            shape.move(mpos.getX(), mpos.getY(), mpos.getZ()).forAllBoxes(((minX, minY, minZ, maxX, maxY, maxZ) ->
                                    colliders.add(CollisionArea.box(minX, minY - (INV_SCAN_STEPS * 2), minZ, maxX, maxY, maxZ))));
                        }
                    }
                }

                var pointDir = new Vec3(scanPoint.x, 0, scanPoint.z).normalize().scale(INV_SCAN_STEPS);

                double pointY = centerPos.y;
                for (int i = 0; i < Math.ceil(scanDist * SCAN_STEPS_PER_BLOCK); i++) {
                    double pointX = centerPos.x + (i * pointDir.x);
                    double pointZ = centerPos.z + (i * pointDir.z);
                    pointY -= INV_SCAN_STEPS * 1.5;

                    boolean ground = false;
                    for (var col : colliders) {
                        if (col.isPointInside(pointX, pointY, pointZ)) {
                            double hY = col.highestY(pointX, pointY, pointZ);
                            double diff = hY - pointY;
                            if (diff < (stepHeight + (INV_SCAN_STEPS * 1.5))) {
                                pointY = hY;
                                ground = true;
                            }
                        }
                    }
                    if (ground) {
                        anyOnGround = true;
                    } else {
                        allOnGround = false;
                    }
                }

                pointPos = new Vec3(pointPos.x, pointY, pointPos.z);

                if (lowestDisplacementPos == null || pointPos.y < lowestDisplacementPos.y) {
                    lowestDisplacementPos = pointPos;
                }
                if (highestDisplacementPos == null || pointPos.y > highestDisplacementPos.y) {
                    highestDisplacementPos = pointPos;
                }

                scannedPoints.add(pointPos);
            }

            if (allOnGround && !wereAllOnGround) {
                entity.bounce();
            }
            wereAllOnGround = allOnGround;

            if (!anyOnGround) {
                return;
            }

            angularXTarget = 0;
            angularZTarget = 0;
            verticalTarget = 0;

            if (lowestDisplacementPos != null) {
                var displacementCenterPos = new Vec3(centerPos.x, (lowestDisplacementPos.y + highestDisplacementPos.y) * 0.5, centerPos.z);

                var combinedNormals = Vec3.ZERO;
                int normalCount = 0;
                Vec3 positiveXOffset = null;
                Vec3 negativeXOffset = null;
                Vec3 positiveZOffset = null;
                Vec3 negativeZOffset = null;

                for (var pointPos : scannedPoints) {
                    var pointOffset = pointPos.subtract(displacementCenterPos);
                    if (pointOffset.x > 0) {
                        if (positiveXOffset != null) {
                            var normal = positiveXOffset.cross(pointOffset).normalize();
                            if (normal.y < 0) normal = normal.reverse();
                            combinedNormals = combinedNormals.add(normal);
                            normalCount++;
                            positiveXOffset = null;
                        } else positiveXOffset = pointOffset;
                    } else if (pointOffset.x < 0) {
                        if (negativeXOffset != null) {
                            var normal = negativeXOffset.cross(pointOffset).normalize();
                            if (normal.y < 0) normal = normal.reverse();
                            combinedNormals = combinedNormals.add(normal);
                            normalCount++;
                            negativeXOffset = null;
                        } else negativeXOffset = pointOffset;
                    } else if (pointOffset.z > 0) {
                        if (positiveZOffset != null) {
                            var normal = positiveZOffset.cross(pointOffset).normalize();
                            if (normal.y < 0) normal = normal.reverse();
                            combinedNormals = combinedNormals.add(normal);
                            normalCount++;
                            positiveZOffset = null;
                        } else positiveZOffset = pointOffset;
                    } else if (pointOffset.z < 0) {
                        if (negativeZOffset != null) {
                            var normal = negativeZOffset.cross(pointOffset).normalize();
                            if (normal.y < 0) normal = normal.reverse();
                            combinedNormals = combinedNormals.add(normal);
                            normalCount++;
                            negativeZOffset = null;
                        } else negativeZOffset = pointOffset;
                    }
                }

                combinedNormals = normalCount > 0 ? combinedNormals.scale(1f / normalCount) : new Vec3(0, 1, 0);

                angularXTarget = Mth.wrapDegrees(90f - (float) Math.toDegrees(Math.atan2(combinedNormals.y, combinedNormals.z)));
                angularZTarget = Mth.wrapDegrees(270f + (float) Math.toDegrees(Math.atan2(combinedNormals.y, combinedNormals.x)));

                verticalTarget = (float) displacementCenterPos.subtract(centerPos).y;
            }
        }

        public void applyWheelbase(WheelBase wheelBase) {
            this.scanPoints.clear();
            for (WheelBase.WheelPos pos : wheelBase.wheels) {
                this.scanPoints.add(new Vec3(pos.right() / 16, 0, pos.forward() / 16));
            }
        }

        public float getVertical(float tickDelta) {
            return Mth.lerp(tickDelta, lastVertical, verticalTarget);
        }

        public float getAngularX(float tickDelta) {
            return Mth.rotLerp(tickDelta, lastAngularX, currAngularX);
        }

        public float getAngularZ(float tickDelta) {
            return Mth.rotLerp(tickDelta, lastAngularZ, currAngularZ);
        }
    }
}
