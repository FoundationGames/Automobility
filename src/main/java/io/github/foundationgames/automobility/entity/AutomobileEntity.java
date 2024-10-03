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
import io.github.foundationgames.automobility.screen.AutomobileScreenHandlerContext;
import io.github.foundationgames.automobility.block.AutomobileAssemblerBlock;
import io.github.foundationgames.automobility.block.LaunchGelBlock;
import io.github.foundationgames.automobility.block.OffRoadBlock;
import io.github.foundationgames.automobility.item.AutomobileInteractable;
import io.github.foundationgames.automobility.item.AutomobilityItems;
import io.github.foundationgames.automobility.particle.AutomobilityParticles;
import io.github.foundationgames.automobility.sound.AutomobileSoundInstance;
import io.github.foundationgames.automobility.sound.AutomobilitySounds;
import io.github.foundationgames.automobility.util.AUtils;
import io.github.foundationgames.automobility.util.duck.CollisionArea;
import io.github.foundationgames.automobility.util.midnightcontrols.ControllerUtils;
import io.github.foundationgames.automobility.util.network.PayloadPackets;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.ShapeContext;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.model.Model;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MovementType;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.WaterCreatureEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.vehicle.BoatEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.s2c.play.EntitySpawnS2CPacket;
import net.minecraft.predicate.entity.EntityPredicates;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.CuboidBlockIterator;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.TypeFilter;
import net.minecraft.util.function.BooleanBiFunction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3f;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Deque;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

public class AutomobileEntity extends Entity implements RenderableAutomobile, EntityWithInventory {
    private static final TrackedData<Float> REAR_ATTACHMENT_YAW = DataTracker.registerData(AutomobileEntity.class, TrackedDataHandlerRegistry.FLOAT);
    private static final TrackedData<Float> REAR_ATTACHMENT_ANIMATION = DataTracker.registerData(AutomobileEntity.class, TrackedDataHandlerRegistry.FLOAT);
    private static final TrackedData<Float> FRONT_ATTACHMENT_ANIMATION = DataTracker.registerData(AutomobileEntity.class, TrackedDataHandlerRegistry.FLOAT);

    private AutomobileFrame frame = AutomobileFrame.REGISTRY.getOrDefault(null);
    private AutomobileWheel wheels = AutomobileWheel.REGISTRY.getOrDefault(null);
    private AutomobileEngine engine = AutomobileEngine.REGISTRY.getOrDefault(null);
    private RearAttachment rearAttachment;
    private FrontAttachment frontAttachment;

    private final AutomobileStats stats = new AutomobileStats();


    private Model frameModel = null;
    
    private Model wheelModel = null;
    
    private Model engineModel = null;
    
    private @Nullable Model rearAttachmentModel = null;
    
    private @Nullable Model frontAttachmentModel = null;

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

    private Vec3d addedVelocity = getVelocity();

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

    private Vec3d lastVelocity = Vec3d.ZERO;
    private Vec3d lastPosForDisplacement = Vec3d.ZERO;

    private Vec3d prevTailPos = null;

    private int slopeStickingTimer = 0;
    private float grip = 1;

    private int suspensionBounceTimer = 0;
    private int lastSusBounceTimer = suspensionBounceTimer;

    private final Deque<Double> prevYDisplacements = new ArrayDeque<>();

    private boolean offRoad = false;
    private Vec3f debrisColor = new Vec3f();

    private int fallTicks = 0;

    private int despawnTime = -1;
    private int despawnCountdown = 0;
    private boolean decorative = false;

    private boolean wasEngineRunning = false;

    private float standStillTime = -1.3f;

    public void writeSyncToClientData(PacketByteBuf buf) {
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

    public void readSyncToClientData(PacketByteBuf buf) {
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
    public void readCustomDataFromNbt(NbtCompound nbt) {
        setComponents(
                AutomobileFrame.REGISTRY.getOrDefault(Identifier.tryParse(nbt.getString("frame"))),
                AutomobileWheel.REGISTRY.getOrDefault(Identifier.tryParse(nbt.getString("wheels"))),
                AutomobileEngine.REGISTRY.getOrDefault(Identifier.tryParse(nbt.getString("engine")))
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

        updateModels = true;
    }

    @Override
    public void writeCustomDataToNbt(NbtCompound nbt) {
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

    
    public boolean updateModels = true;

    public AutomobileEntity(EntityType<?> type, World world) {
        super(type, world);

        this.setRearAttachment(RearAttachmentType.REGISTRY.getOrDefault(null));
        this.setFrontAttachment(FrontAttachmentType.REGISTRY.getOrDefault(null));
    }

    public AutomobileEntity(World world) {
        this(AutomobilityEntities.AUTOMOBILE, world);
    }

    @Override
    public void onSpawnPacket(EntitySpawnS2CPacket packet) {
        super.onSpawnPacket(packet);
        if (world.isClient()) {
            PayloadPackets.requestSyncAutomobileComponentsPacket(this);
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
        return MathHelper.lerp(tickDelta, lastSteering, steering);
    }

    @Override
    public float getWheelAngle(float tickDelta) {
        return MathHelper.lerp(tickDelta, lastWheelAngle, wheelAngle);
    }

    public float getBoostSpeed(float tickDelta) {
        return MathHelper.lerp(tickDelta, lastBoostSpeed, boostSpeed);
    }

    @Override
    public float getSuspensionBounce(float tickDelta) {
        return MathHelper.lerp(tickDelta, lastSusBounceTimer, suspensionBounceTimer);
    }

    @Override
    public boolean engineRunning() {
        return hasPassengers();
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
        if (this.getPrimaryPassenger() instanceof PlayerEntity player && player.isMainPlayer()) {
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
    public Vec3f debrisColor() {
        return debrisColor;
    }

    public boolean burningOut() {
        return burningOut;
    }

    private void setDrifting(boolean drifting) {
        if (this.world.isClient() && !this.drifting && drifting) {
            playSkiddingSound();
        }

        this.drifting = drifting;
    }

    private void setBurningOut(boolean burningOut) {
        if (this.world.isClient() && !this.drifting && !this.burningOut && burningOut) {
            playSkiddingSound();
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
            this.rearAttachment.setYaw(this.getYaw());

            if (!world.isClient() && !this.rearAttachment.isRideable() && this.getPassengerList().size() > 1) {
                this.getPassengerList().get(1).stopRiding();
            }

            this.updateModels = true;
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

            this.updateModels = true;
            syncAttachments();
        }
    }

    public void setComponents(AutomobileFrame frame, AutomobileWheel wheel, AutomobileEngine engine) {
        this.frame = frame;
        this.wheels = wheel;
        this.engine = engine;
        this.updateModels = true;
        this.stepHeight = wheels.size();
        this.stats.from(frame, wheel, engine);
        this.displacement.applyWheelbase(frame.model().wheelBase());
        if (!world.isClient()) syncComponents();
    }

    public void forNearbyPlayers(int radius, boolean ignoreDriver, Consumer<ServerPlayerEntity> action) {
        for (PlayerEntity p : world.getPlayers()) {
            if (ignoreDriver && p == getFirstPassenger()) {
                continue;
            }
            if (p.getPos().distanceTo(getPos()) < radius && p instanceof ServerPlayerEntity player) {
                action.accept(player);
            }
        }
    }

    public Vec3d getTailPos() {
        return this.getPos()
                .add(new Vec3d(0, 0, this.getFrame().model().rearAttachmentPos() * 0.0625)
                        .rotateY((float) Math.toRadians(180 - this.getYaw()))
                );
    }

    public Vec3d getHeadPos() {
        return this.getPos()
                .add(new Vec3d(0, 0, this.getFrame().model().frontAttachmentPos() * 0.0625)
                        .rotateY((float) Math.toRadians(-this.getYaw()))
                );
    }

    public boolean hasSpaceForPassengers() {
        return (this.rearAttachment.isRideable()) ? (this.getPassengerList().size() < 2) : (!this.hasPassengers());
    }

    public void setSpeed(float horizontal, float vertical) {
        this.hSpeed = horizontal;
        this.vSpeed = vertical;
    }

    @Environment(EnvType.CLIENT)
    private void playEngineSound() {
        if (this.getEngine().isEmpty()) {
            return;
        }

        var client = MinecraftClient.getInstance();
        client.getSoundManager().play(new AutomobileSoundInstance.EngineSound(client, this));
    }

    @Environment(EnvType.CLIENT)
    private void playSkiddingSound() {
        var client = MinecraftClient.getInstance();
        client.getSoundManager().play(new AutomobileSoundInstance.SkiddingSound(client, this));
    }

    @Override
    public void tick() {
        boolean first = this.firstUpdate;

        if (lastWheelAngle != wheelAngle) markDirty();
        lastWheelAngle = wheelAngle;

        if (!this.wasEngineRunning && this.engineRunning() && this.world.isClient()) {
            playEngineSound();
        }
        this.wasEngineRunning = this.engineRunning();

        if (!this.hasPassengers() || !this.getFrontAttachment().canDrive(this.getFirstPassenger())) {
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

        var prevPos = this.getPos();

        positionTrackingTick();
        collisionStateTick();
        steeringTick();
        driftingTick();
        burnoutTick();

        movementTick();
        if (this.isLogicalSideForUpdatingMovement()) {
            this.move(MovementType.SELF, this.getVelocity());
        }
        postMovementTick();

        if (!world.isClient()) {
            var prevTailPos = this.prevTailPos != null ? this.prevTailPos : this.getTailPos();
            var tailPos = this.getTailPos();

            this.rearAttachment.pull(prevTailPos.subtract(tailPos));
            this.prevTailPos = tailPos;

            if (dirty) {
                syncData();
                dirty = false;
            }
            if (this.hasSpaceForPassengers() && !decorative) {
                var touchingEntities = this.world.getOtherEntities(this, this.getBoundingBox().expand(0.2, 0, 0.2), EntityPredicates.canBePushedBy(this));
                for (Entity entity : touchingEntities) {
                    if (!entity.hasPassenger(this)) {
                        if (!entity.hasVehicle() && entity.getWidth() <= this.getWidth() && entity instanceof MobEntity && !(entity instanceof WaterCreatureEntity)) {
                            entity.startRiding(this);
                        }
                    }
                }
            }
            if (this.hasPassengers()) {
                if (this.getFrontAttachment().canDrive(this.getFirstPassenger()) && this.getFirstPassenger() instanceof MobEntity mob) {
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

            if (Math.abs(this.hSpeed) < 0.05 && !this.burningOut && this.getPrimaryPassenger() instanceof PlayerEntity) {
                this.standStillTime = AUtils.shift(this.standStillTime, 0.05f, 1f);
            } else {
                this.standStillTime = AUtils.shift(this.standStillTime, 0.15f, -1.3f);
            }
        }

        displacementTick(first || (this.getPos().subtract(prevPos).length() > 0 || this.getYaw() != this.prevYaw));
    }

    public void positionTrackingTick() {
        if (this.isLogicalSideForUpdatingMovement()) {
            this.lerpTicks = 0;
            updateTrackedPosition(getX(), getY(), getZ());
        } else if (lerpTicks > 0) {
            this.setPosition(
                    this.getX() + ((this.trackedX - this.getX()) / (double)this.lerpTicks),
                    this.getY() + ((this.trackedY - this.getY()) / (double)this.lerpTicks),
                    this.getZ() + ((this.trackedZ - this.getZ()) / (double)this.lerpTicks)
            );
            this.setYaw(this.getYaw() + (MathHelper.wrapDegrees(this.trackedYaw - this.getYaw()) / (float)this.lerpTicks));

            this.lerpTicks--;
        }
    }

    public void markDirty() {
        dirty = true;
    }

    private void syncData() {
        forNearbyPlayers(200, true, player -> PayloadPackets.sendSyncAutomobileDataPacket(this, player));
    }

    private void syncComponents() {
        forNearbyPlayers(200, false, player -> PayloadPackets.sendSyncAutomobileComponentsPacket(this, player));
    }

    private void syncAttachments() {
        forNearbyPlayers(200, false, player -> PayloadPackets.sendSyncAutomobileAttachmentsPacket(this, player));
    }

    public ItemStack asPrefabItem() {
        var stack = new ItemStack(AutomobilityItems.AUTOMOBILE);
        var automobile = stack.getOrCreateSubNbt("Automobile");
        automobile.putString("frame", frame.getId().toString());
        automobile.putString("wheels", wheels.getId().toString());
        automobile.putString("engine", engine.getId().toString());
        return stack;
    }

    @Nullable
    @Override
    public ItemStack getPickBlockStack() {
        return asPrefabItem();
    }

    // making mobs drive automobiles
    // technically the mobs don't drive, instead the automobile
    // self-drives to the mob's destination...
    public void provideMobDriverInputs(MobEntity driver) {
        // Don't move if the driver doesn't exist or can't drive
        if (driver == null || driver.isDead() || driver.isRemoved()) {
            if (accelerating || steeringLeft || steeringRight) markDirty();
            accelerating = false;
            steeringLeft = false;
            steeringRight = false;
            return;
        }

        var path = driver.getNavigation().getCurrentPath();
        // checks if there is a current, incomplete path that the entity has targeted
        if (path != null && !path.isFinished() && path.getEnd() != null) {
            // determines the relative position to drive to, based on the end of the path
            var pos = path.getEnd().getPos().subtract(getPos());
            // determines the angle to that position
            double target = MathHelper.wrapDegrees(Math.toDegrees(Math.atan2(pos.getX(), pos.getZ())));
            // determines another relative position, this time to the path's current node (in the case of the path directly to the end being obstructed)
            var fnPos = path.getCurrentNode().getPos().subtract(getPos());
            // determines the angle to that current node's position
            double fnTarget = MathHelper.wrapDegrees(Math.toDegrees(Math.atan2(fnPos.getX(), fnPos.getZ())));
            // if the difference in angle between the end position and the current node's position is too great,
            // the automobile will drive to that current node under the assumption that the path directly to the
            // end is obstructed
            if (Math.abs(target - fnTarget) > 69) {
                pos = fnPos;
                target = fnTarget;
            }
            // fixes up the automobile's own yaw value
            float yaw = MathHelper.wrapDegrees(-getYaw());
            // finds the difference between the target angle and the yaw
            double offset = MathHelper.wrapDegrees(yaw - target);
            // whether the automobile should go in reverse
            boolean reverse = false;
            // a value to determine the threshold used to determine whether the automobile is moving
            // both slow enough and is at an extreme enough offset angle to incrementally move in reverse
            float mul = 0.5f + (MathHelper.clamp(hSpeed, 0, 1) * 0.5f);
            if (pos.length() < 20 * mul && Math.abs(offset) > 180 - (170 * mul)) {
                long time = world.getTime();
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
        var blockBelow = new BlockPos(getX(), getY() - 0.05, getZ());
        this.grip = 1 - ((MathHelper.clamp((world.getBlockState(blockBelow).getBlock().getSlipperiness() - 0.6f) / 0.4f, 0, 1) * (1 - stats.getGrip() * 0.8f)));
        this.grip *= this.grip;

        // Bounce on gel
        if (this.automobileOnGround && this.jumpCooldown <= 0 && world.getBlockState(this.getBlockPos()).getBlock() instanceof LaunchGelBlock) {
            this.setSpeed(Math.max(this.getHSpeed(), 0.1f), Math.max(this.getVSpeed(), 0.9f));
            this.jumpCooldown = 5;
            this.automobileOnGround = false;
        }

        // Track the last position of the automobile
        this.lastPosForDisplacement = getPos();

        // cumulative will be modified by the following code and then the automobile will be moved by it
        // Currently initialized with the value of addedVelocity (which is a general velocity vector applied to the automobile, i.e. for when it bumps into a wall and is pushed back)
        var cumulative = addedVelocity;

        // Reduce gravity underwater
        cumulative = cumulative.add(0, (vSpeed * (isSubmergedInWater() ? 0.15f : 1)), 0);

        // This is the general direction the automobile will move, which is slightly offset to the side when drifting
        this.speedDirection = getYaw() - (drifting ? Math.min(turboCharge * 6, 43 + (-steering * 12)) * driftDir : -steering * 12); //MathHelper.lerp(grip, getYaw(), getYaw() - (drifting ? Math.min(turboCharge * 6, 43 + (-steering * 12)) * driftDir : -steering * 12));

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
        var below = new BlockPos(Math.floor(getX()), Math.floor(getY() - 0.51), Math.floor(getZ()));
        var state = world.getBlockState(below);
        if (state.isIn(Automobility.STICKY_SLOPES)) {
            slopeStickingTimer = 1;
        } else {
            slopeStickingTimer = Math.max(0, slopeStickingTimer--);
        }

        // Handle being in off-road
        if (boostSpeed < 0.4f && world.getBlockState(getBlockPos()).getBlock() instanceof OffRoadBlock offRoad) {
            int layers = world.getBlockState(getBlockPos()).get(OffRoadBlock.LAYERS);
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
                this.addedVelocity = new Vec3d(Math.sin(angle) * hSpeed, 0, Math.cos(angle) * hSpeed);
                this.hSpeed = 0;
                cumulative = cumulative.add(addedVelocity);
            }
        } else {
            // Apply the horizontal speed to the cumulative movement
            cumulative = cumulative.add(Math.sin(angle) * hSpeed, 0, Math.cos(angle) * hSpeed);
        }

        cumulative = cumulative.multiply(this.grip).add(this.lastVelocity.multiply(1 - this.grip));
        if (cumulative.length() < 0.001) {
            cumulative = Vec3d.ZERO;
        }

        // Turn the wheels
        float wheelCircumference = (float)(2 * (wheels.model().radius() / 16) * Math.PI);
        if (hSpeed > 0) markDirty();
        wheelAngle += 300 * (hSpeed / wheelCircumference) + (hSpeed > 0 ? ((1 - grip) * 15) : 0); // made it a bit slower intentionally, also make it spin more when on slippery surface

        // Set the automobile's velocity
        if (this.isLogicalSideForUpdatingMovement()) {
            this.setVelocity(cumulative);
        }
        this.scheduleVelocityUpdate();
        this.velocityDirty = true;

        lastVelocity = cumulative;

        // Damage and launch entities that are hit by a moving automobile
        if (Math.abs(hSpeed) > 0.2) {
            runOverEntities(cumulative);
        }
    }

    public void runOverEntities(Vec3d velocity) {
        var frontBox = getBoundingBox().offset(velocity.multiply(0.5));
        var velAdd = velocity.add(0, 0.1, 0).multiply(3);
        for (var entity : world.getEntitiesByType(TypeFilter.instanceOf(Entity.class), frontBox, entity -> entity != this && entity != getFirstPassenger())) {
            if (!entity.isInvulnerable()) {
                if (entity instanceof LivingEntity living && entity.getVehicle() != this) {
                    living.damage(AutomobilityEntities.AUTOMOBILE_DAMAGE_SOURCE, hSpeed * 10);

                    entity.addVelocity(velAdd.x, velAdd.y, velAdd.z);
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
        if (addVelLen > 0) addedVelocity = addedVelocity.multiply(Math.max(0, addVelLen - addedVelReduction) / addVelLen);

        float angle = (float) Math.toRadians(-speedDirection);
        if (touchingWall && hSpeed > 0.1 && addedVelocity.length() <= 0) {
            engineSpeed /= 3.6;
            double knockSpeed = ((-0.2 * hSpeed) - 0.5);
            addedVelocity = addedVelocity.add(Math.sin(angle) * knockSpeed, 0, Math.cos(angle) * knockSpeed);

            world.playSound(this.getX(), this.getY(), this.getZ(), AutomobilitySounds.COLLISION, SoundCategory.AMBIENT, 0.76f, 0.65f + (0.06f * (this.world.random.nextFloat() - 0.5f)), true);
        }

        double yDisp = getPos().subtract(this.lastPosForDisplacement).getY();

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
            vSpeed = (float)MathHelper.clamp(highestPrevYDisp, 0, hSpeed * 0.6f);
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
                newAngularSpeed = MathHelper.clamp(newAngularSpeed + (acc * this.steering), -lim, lim);
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
            float prevYaw = getYaw();
            this.setYaw(getYaw() + yawInc);
            if (world.isClient) {
                var passenger = getFirstPassenger();
                if (passenger instanceof PlayerEntity player) {
                    if (inLockedViewMode()) {
                        player.setYaw(MathHelper.wrapDegrees(getYaw() + lockedViewOffset));
                        player.setBodyYaw(MathHelper.wrapDegrees(getYaw() + lockedViewOffset));
                    } else {
                        player.setYaw(MathHelper.wrapDegrees(player.getYaw() + yawInc));
                        player.setBodyYaw(MathHelper.wrapDegrees(player.getYaw() + yawInc));
                    }
                }
            } else {
                for (Entity e : getPassengerList()) {
                    if (e == getFirstPassenger()) {
                        e.setYaw(MathHelper.wrapDegrees(e.getYaw() + yawInc));
                        e.setBodyYaw(MathHelper.wrapDegrees(e.getYaw() + yawInc));
                    }
                }
            }
            if (world.isClient()) {
                this.prevYaw = prevYaw;
            }
        }
    }

    @Override
    public void move(MovementType movementType, Vec3d movement) {
        if (!this.world.isClient() && movementType == MovementType.PLAYER) {
            AUtils.IGNORE_ENTITY_GROUND_CHECK_STEPPING = true;
        }
        super.move(movementType, movement);
    }

    @Override
    public boolean handleFallDamage(float fallDistance, float damageMultiplier, DamageSource damageSource) {
        return false; // Riders shouldn't take fall damage
    }

    public void accumulateCollisionAreas(Collection<CollisionArea> areas) {
        this.world.getEntitiesByClass(Entity.class, this.getBoundingBox().expand(3, 3, 3), e -> e != this && e.getVehicle() != this)
                .forEach(e -> areas.add(CollisionArea.entity(e)));
    }

    public void displacementTick(boolean tick) {
        if (this.world.isClient()) {
            this.displacement.preTick();

            if (tick) {
                this.displacement.otherColliders.clear();
                this.accumulateCollisionAreas(this.displacement.otherColliders);

                this.displacement.tick(this.world, this, this.getPos(), this.getYaw(), this.stepHeight);
            }

            if (world.getBlockState(this.getBlockPos()).getBlock() instanceof AutomobileAssemblerBlock) {
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
        var groundBox = new Box(b.minX, b.minY - 0.04, b.minZ, b.maxX, b.minY, b.maxZ);
        var wid = (b.getXLength() + b.getZLength()) * 0.5f;
        var floorBox = new Box(b.minX + (wid * 0.94), b.minY - 0.05, b.minZ + (wid * 0.94), b.maxX - (wid * 0.94), b.minY, b.maxZ - (wid * 0.94));
        var wallBox = b.contract(0.05).offset(this.lastVelocity.normalize().multiply(0.12));
        var start = new BlockPos(b.minX - 0.1, b.minY - 0.2, b.minZ - 0.1);
        var end = new BlockPos(b.maxX + 0.1, b.maxY + 0.2 + this.stepHeight, b.maxZ + 0.1);
        var groundCuboid = VoxelShapes.cuboid(groundBox);
        var floorCuboid = VoxelShapes.cuboid(floorBox);
        var wallCuboid = VoxelShapes.cuboid(wallBox);
        var stepWallCuboid = wallCuboid.offset(0, this.stepHeight - 0.05, 0);
        boolean wallHit = false;
        boolean stepWallHit = false;
        var shapeCtx = ShapeContext.of(this);
        if (this.world.isRegionLoaded(start, end)) {
            var pos = new BlockPos.Mutable();
            for(int x = start.getX(); x <= end.getX(); ++x) {
                for(int y = start.getY(); y <= end.getY(); ++y) {
                    for(int z = start.getZ(); z <= end.getZ(); ++z) {
                        pos.set(x, y, z);
                        var state = this.world.getBlockState(pos);
                        var blockShape = state.getCollisionShape(this.world, pos, shapeCtx).offset(pos.getX(), pos.getY(), pos.getZ());
                        this.automobileOnGround |= VoxelShapes.matchesAnywhere(blockShape, groundCuboid, BooleanBiFunction.AND);
                        this.isFloorDirectlyBelow |= VoxelShapes.matchesAnywhere(blockShape, floorCuboid, BooleanBiFunction.AND);
                        wallHit |= VoxelShapes.matchesAnywhere(blockShape, wallCuboid, BooleanBiFunction.AND);
                        stepWallHit |= VoxelShapes.matchesAnywhere(blockShape, stepWallCuboid, BooleanBiFunction.AND);
                    }
                }
            }
        }
        this.touchingWall = (wallHit && stepWallHit);

        var otherColliders = new HashSet<CollisionArea>();
        this.accumulateCollisionAreas(otherColliders);
        this.automobileOnGround |= otherColliders.stream().anyMatch(col -> col.boxIntersects(groundBox));
    }

    public void updateTrackedPositionAndAngles(double x, double y, double z, float yaw, float pitch, int interpolationSteps, boolean interpolate) {
        this.trackedX = x;
        this.trackedY = y;
        this.trackedZ = z;
        this.trackedYaw = yaw;
        this.lerpTicks = this.getType().getTrackTickInterval() + 1;
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
            PayloadPackets.sendSyncAutomobileInputPacket(this, accelerating, braking, steeringLeft, steeringRight, holdingDrift);
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
        if (this.isLogicalSideForUpdatingMovement()) {
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
            } else if (steering == 0 && !this.getWorld().isClient() && this.getRearAttachment() instanceof DeployableRearAttachment att) {
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
        var origin = this.getPos().add(0, this.displacement.verticalTarget, 0);
        for (var wheel : this.getFrame().model().wheelBase().wheels) {
            if (wheel.end() == WheelBase.WheelEnd.BACK) {
                var pos = new Vec3d(wheel.right() + ((wheel.right() > 0 ? 1 : -1) * this.getWheels().model().width() * wheel.scale()), 0, wheel.forward())
                        .rotateX((float) Math.toRadians(this.displacement.currAngularX))
                        .rotateZ((float) Math.toRadians(this.displacement.currAngularZ))
                        .rotateY((float) Math.toRadians(-this.getYaw())).multiply(0.0625).add(0, 0.4, 0);
                world.addParticle(AutomobilityParticles.DRIFT_SMOKE, origin.x + pos.x, origin.y + pos.y, origin.z + pos.z, 0, 0, 0);
            }
        }
    }

    private static boolean inLockedViewMode() {
        return ControllerUtils.inControllerMode();
    }

    
    private void updateModels(EntityRendererFactory.Context ctx) {
        if (updateModels) {
            this.frameModel = frame.model().model().apply(ctx);
            this.wheelModel = wheels.model().model().apply(ctx);
            this.engineModel = engine.model().model().apply(ctx);
            this.rearAttachmentModel = this.rearAttachment.type.model().model().apply(ctx);
            this.frontAttachmentModel = this.frontAttachment.type.model().model().apply(ctx);

            updateModels = false;
        }
    }

    
    public Model getWheelModel(EntityRendererFactory.Context ctx) {
        updateModels(ctx);
        return wheelModel;
    }

    
    public Model getFrameModel(EntityRendererFactory.Context ctx) {
        updateModels(ctx);
        return frameModel;
    }

    
    public Model getEngineModel(EntityRendererFactory.Context ctx) {
        updateModels(ctx);
        return engineModel;
    }

    @Override
    public @Nullable Model getRearAttachmentModel(EntityRendererFactory.Context ctx) {
        updateModels(ctx);
        return rearAttachmentModel;
    }

    @Override
    public @Nullable Model getFrontAttachmentModel(EntityRendererFactory.Context ctx) {
        updateModels(ctx);
        return frontAttachmentModel;
    }

    @Override
    public float getAutomobileYaw(float tickDelta) {
        return getYaw(tickDelta);
    }

    @Override
    public float getRearAttachmentYaw(float tickDelta) {
        return this.rearAttachment.yaw(tickDelta);
    }

    @Nullable
    @Override
    public Entity getPrimaryPassenger() {
        return getFirstPassenger();
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
    public void openInventory(PlayerEntity player) {
        var factory = this.getRearAttachment().createMenu(new AutomobileScreenHandlerContext(this));
        if (factory != null) {
            player.openHandledScreen(factory);
        }
    }

    public float getStandStillTime() {
        return this.standStillTime;
    }

    public void playHitSound() {
        world.emitGameEvent(GameEvent.ENTITY_DAMAGED, this);
        world.playSound(null, this.getX(), this.getY(), this.getZ(), SoundEvents.BLOCK_COPPER_BREAK, SoundCategory.AMBIENT, 1, 0.9f + (this.world.random.nextFloat() * 0.2f));
    }

    private void dropParts(Vec3d pos) {
        world.spawnEntity(new ItemEntity(world, pos.x, pos.y, pos.z, AutomobilityItems.AUTOMOBILE_FRAME.createStack(this.getFrame())));
        world.spawnEntity(new ItemEntity(world, pos.x, pos.y, pos.z, AutomobilityItems.AUTOMOBILE_ENGINE.createStack(this.getEngine())));

        var wheelStack = AutomobilityItems.AUTOMOBILE_WHEEL.createStack(this.getWheels());
        wheelStack.setCount(this.getFrame().model().wheelBase().wheelCount);
        world.spawnEntity(new ItemEntity(world, pos.x, pos.y, pos.z, wheelStack));
    }

    public void destroyRearAttachment(boolean drop) {
        if (drop) {
            var dropPos = this.rearAttachment.pos();
            world.spawnEntity(new ItemEntity(world, dropPos.x, dropPos.y, dropPos.z,
                    AutomobilityItems.REAR_ATTACHMENT.createStack(this.getRearAttachmentType())));
        }
        this.setRearAttachment(RearAttachmentType.EMPTY);
    }

    public void destroyFrontAttachment(boolean drop) {
        if (drop) {
            var dropPos = this.frontAttachment.pos();
            world.spawnEntity(new ItemEntity(world, dropPos.x, dropPos.y, dropPos.z,
                    AutomobilityItems.FRONT_ATTACHMENT.createStack(this.getFrontAttachmentType())));
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
            this.dropParts(this.getPos().add(0, 0.3, 0));
        }
        this.remove(reason);
    }

    @Override
    public ActionResult interact(PlayerEntity player, Hand hand) {
        if (player.isSneaking()) {
            if (this.hasInventory()) {
                if (!world.isClient()) {
                    openInventory(player);
                    return ActionResult.PASS;
                } else {
                    return ActionResult.SUCCESS;
                }
            }
        }

        var stack = player.getStackInHand(hand);
        if ((!this.decorative || player.isCreative()) && stack.isOf(AutomobilityItems.CROWBAR)) {
            double playerAngle = Math.toDegrees(Math.atan2(player.getZ() - this.getZ(), player.getX() - this.getX()));
            double angleDiff = MathHelper.wrapDegrees(this.getYaw() - playerAngle);

            if (angleDiff < 0 && !this.frontAttachment.type.isEmpty()) {
                this.destroyFrontAttachment(!player.isCreative());
                this.playHitSound();

                return ActionResult.success(world.isClient);
            } else if (!this.rearAttachment.type.isEmpty()) {
                this.destroyRearAttachment(!player.isCreative());
                this.playHitSound();

                return ActionResult.success(world.isClient);
            } else {
                this.destroyAutomobile(!player.isCreative(), RemovalReason.KILLED);
                this.playHitSound();

                return ActionResult.success(world.isClient);
            }
        }

        if (!decorative) {
            if (stack.getItem() instanceof AutomobileInteractable interactable) {
                return interactable.interactAutomobile(stack, player, hand, this);
            }

            if (!this.hasSpaceForPassengers()) {
                if (!(this.getFirstPassenger() instanceof PlayerEntity)) {
                    if (!world.isClient()) {
                        this.getFirstPassenger().stopRiding();
                    }
                    return ActionResult.success(world.isClient);
                }
                return ActionResult.PASS;
            }
            if (!world.isClient()) {
                player.startRiding(this);
            }
            return ActionResult.success(world.isClient());
        }

        return ActionResult.PASS;
    }

    @Override
    public double getMountedHeightOffset() {
        return ((wheels.model().radius() + frame.model().seatHeight() - 4) / 16);
    }

    @Override
    public void updatePassengerPosition(Entity passenger) {
        if (passenger == this.getFirstPassenger()) {
            var pos = this.getPos().add(0, this.displacement.verticalTarget + passenger.getHeightOffset(), 0)
                    .add(new Vec3d(0, this.getMountedHeightOffset(), 0)
                        .rotateX((float) Math.toRadians(-this.displacement.currAngularX))
                        .rotateZ((float) Math.toRadians(-this.displacement.currAngularZ)));

            passenger.setPosition(pos.x, pos.y, pos.z);
        } else if (this.hasPassenger(passenger)) {
            var pos = this.getPos().add(
                    new Vec3d(0, this.displacement.verticalTarget, this.getFrame().model().rearAttachmentPos() * 0.0625)
                        .rotateY((float) Math.toRadians(180 - this.getYaw())).add(0, this.rearAttachment.getPassengerHeightOffset() + passenger.getHeightOffset() - 0.14, 0)
                        .add(this.rearAttachment.scaledYawVec())
                        .rotateX((float) Math.toRadians(-this.displacement.currAngularX))
                        .rotateZ((float) Math.toRadians(-this.displacement.currAngularZ)));

            passenger.setPosition(pos.x, pos.y, pos.z);
        }
    }

    @Override
    public boolean collidesWith(Entity other) {
        return BoatEntity.canCollide(this, other);
    }

    @Override
    public boolean isCollidable() {
        return true;
    }

    @Override
    public boolean collides() {
        return !this.isRemoved();
    }

    @Override
    public boolean isPushable() {
        return true;
    }

    @Override
    protected void initDataTracker() {
        this.dataTracker.startTracking(REAR_ATTACHMENT_YAW, 0f);
        this.dataTracker.startTracking(REAR_ATTACHMENT_ANIMATION, 0f);
    }

    @Override
    public void onTrackedDataSet(TrackedData<?> data) {
        super.onTrackedDataSet(data);

        if (REAR_ATTACHMENT_YAW.equals(data)) {
            this.rearAttachment.onTrackedYawUpdated(getTrackedRearAttachmentYaw());
        } else if (REAR_ATTACHMENT_ANIMATION.equals(data)) {
            this.rearAttachment.onTrackedAnimationUpdated(getTrackedRearAttachmentAnimation());
        } else if (FRONT_ATTACHMENT_ANIMATION.equals(data)) {
            this.frontAttachment.onTrackedAnimationUpdated(getTrackedFrontAttachmentAnimation());
        }
    }

    @Override
    public Packet<?> createSpawnPacket() {
        return new EntitySpawnS2CPacket(this);
    }

    public void setTrackedRearAttachmentYaw(float value) {
        this.dataTracker.set(REAR_ATTACHMENT_YAW, value);
    }

    public float getTrackedRearAttachmentYaw() {
        return this.dataTracker.get(REAR_ATTACHMENT_YAW);
    }

    public void setTrackedRearAttachmentAnimation(float animation) {
        this.dataTracker.set(REAR_ATTACHMENT_ANIMATION, animation);
    }

    public float getTrackedRearAttachmentAnimation() {
        return this.dataTracker.get(REAR_ATTACHMENT_ANIMATION);
    }

    public void setTrackedFrontAttachmentAnimation(float animation) {
        this.dataTracker.set(FRONT_ATTACHMENT_ANIMATION, animation);
    }

    public float getTrackedFrontAttachmentAnimation() {
        return this.dataTracker.get(FRONT_ATTACHMENT_ANIMATION);
    }

    public void bounce() {
        suspensionBounceTimer = 3;
        world.playSound(this.getX(), this.getY(), this.getZ(), AutomobilitySounds.LANDING, SoundCategory.AMBIENT, 1, 1.5f + (0.15f * (this.world.random.nextFloat() - 0.5f)), true);
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
        private final List<Vec3d> scanPoints = new ArrayList<>();
        public final Set<CollisionArea> otherColliders = new HashSet<>();

        public void preTick() {
            this.lastAngularX = currAngularX;
            this.lastAngularZ = currAngularZ;
            this.lastVertical = verticalTarget;

            this.currAngularX = AUtils.shift(this.currAngularX, 9, this.angularXTarget);
            this.currAngularZ = AUtils.shift(this.currAngularZ, 9, this.angularZTarget);
        }

        public void tick(World world, AutomobileEntity entity, Vec3d centerPos, double yaw, double stepHeight) {
            yaw = 360 - yaw;
            Vec3d lowestDisplacementPos = null;
            Vec3d highestDisplacementPos = null;
            var scannedPoints = new ArrayList<Vec3d>();
            var colliders = new HashSet<CollisionArea>();
            boolean anyOnGround = false;
            boolean allOnGround = true;
            for (var scanPoint : scanPoints) {
                scanPoint = scanPoint
                        .rotateY((float) Math.toRadians(yaw));
                var pointPos = scanPoint.add(centerPos);
                colliders.clear();
                colliders.addAll(this.otherColliders);

                double scanDist = scanPoint.length();

                int heightOffset = (int) Math.ceil(scanDist);
                var iter = new CuboidBlockIterator(
                        (int) Math.min(Math.floor(centerPos.x), Math.floor(pointPos.x)),
                        (int) Math.floor(centerPos.y) - heightOffset,
                        (int) Math.min(Math.floor(centerPos.z), Math.floor(pointPos.z)),
                        (int) Math.max(Math.floor(centerPos.x), Math.floor(pointPos.x)),
                        (int) Math.floor(centerPos.y) + heightOffset,
                        (int) Math.max(Math.floor(centerPos.z), Math.floor(pointPos.z))
                );

                var mpos = new BlockPos.Mutable();
                while (iter.step()) {
                    mpos.set(iter.getX(), iter.getY(), iter.getZ());
                    var shape = world.getBlockState(mpos).getCollisionShape(world, mpos);
                    if (!shape.isEmpty()) {
                        if (shape == VoxelShapes.fullCube()) {
                            colliders.add(CollisionArea.box(mpos.getX(), mpos.getY() - (INV_SCAN_STEPS * 2), mpos.getZ(), mpos.getX() + 1, mpos.getY() + 1, mpos.getZ() + 1));
                        } else {
                            shape.offset(mpos.getX(), mpos.getY(), mpos.getZ()).forEachBox(((minX, minY, minZ, maxX, maxY, maxZ) ->
                                    colliders.add(CollisionArea.box(minX, minY - (INV_SCAN_STEPS * 2), minZ, maxX, maxY, maxZ))));
                        }
                    }
                }

                var pointDir = new Vec3d(scanPoint.x, 0, scanPoint.z).normalize().multiply(INV_SCAN_STEPS);

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

                pointPos = new Vec3d(pointPos.x, pointY, pointPos.z);

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
                var displacementCenterPos = new Vec3d(centerPos.x, (lowestDisplacementPos.y + highestDisplacementPos.y) * 0.5, centerPos.z);

                var combinedNormals = Vec3d.ZERO;
                int normalCount = 0;
                Vec3d positiveXOffset = null;
                Vec3d negativeXOffset = null;
                Vec3d positiveZOffset = null;
                Vec3d negativeZOffset = null;

                for (var pointPos : scannedPoints) {
                    var pointOffset = pointPos.subtract(displacementCenterPos);
                    if (pointOffset.x > 0) {
                        if (positiveXOffset != null) {
                            var normal = positiveXOffset.crossProduct(pointOffset).normalize();
                            if (normal.y < 0) normal = normal.negate();
                            combinedNormals = combinedNormals.add(normal);
                            normalCount++;
                            positiveXOffset = null;
                        } else positiveXOffset = pointOffset;
                    } else if (pointOffset.x < 0) {
                        if (negativeXOffset != null) {
                            var normal = negativeXOffset.crossProduct(pointOffset).normalize();
                            if (normal.y < 0) normal = normal.negate();
                            combinedNormals = combinedNormals.add(normal);
                            normalCount++;
                            negativeXOffset = null;
                        } else negativeXOffset = pointOffset;
                    } else if (pointOffset.z > 0) {
                        if (positiveZOffset != null) {
                            var normal = positiveZOffset.crossProduct(pointOffset).normalize();
                            if (normal.y < 0) normal = normal.negate();
                            combinedNormals = combinedNormals.add(normal);
                            normalCount++;
                            positiveZOffset = null;
                        } else positiveZOffset = pointOffset;
                    } else if (pointOffset.z < 0) {
                        if (negativeZOffset != null) {
                            var normal = negativeZOffset.crossProduct(pointOffset).normalize();
                            if (normal.y < 0) normal = normal.negate();
                            combinedNormals = combinedNormals.add(normal);
                            normalCount++;
                            negativeZOffset = null;
                        } else negativeZOffset = pointOffset;
                    }
                }

                combinedNormals = normalCount > 0 ? combinedNormals.multiply(1f / normalCount) : new Vec3d(0, 1, 0);

                angularXTarget = MathHelper.wrapDegrees(90f - (float) Math.toDegrees(Math.atan2(combinedNormals.y, combinedNormals.z)));
                angularZTarget = MathHelper.wrapDegrees(270f + (float) Math.toDegrees(Math.atan2(combinedNormals.y, combinedNormals.x)));

                verticalTarget = (float) displacementCenterPos.subtract(centerPos).y;
            }
        }

        public void applyWheelbase(WheelBase wheelBase) {
            this.scanPoints.clear();
            for (WheelBase.WheelPos pos : wheelBase.wheels) {
                this.scanPoints.add(new Vec3d(pos.right() / 16, 0, pos.forward() / 16));
            }
        }

        public float getVertical(float tickDelta) {
            return MathHelper.lerp(tickDelta, lastVertical, verticalTarget);
        }

        public float getAngularX(float tickDelta) {
            return MathHelper.lerpAngleDegrees(tickDelta, lastAngularX, currAngularX);
        }

        public float getAngularZ(float tickDelta) {
            return MathHelper.lerpAngleDegrees(tickDelta, lastAngularZ, currAngularZ);
        }
    }
}
