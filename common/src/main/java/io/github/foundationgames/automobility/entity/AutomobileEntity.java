package io.github.foundationgames.automobility.entity;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import io.github.foundationgames.automobility.Automobility;
import io.github.foundationgames.automobility.automobile.AutomobileData;
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
import io.github.foundationgames.automobility.block.SpecialAutomobileColliderBlock;
import io.github.foundationgames.automobility.controller.AutomobileController;
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
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Cursor3D;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerEntity;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.Container;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.animal.WaterAnimal;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.DismountHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.entity.EntityTypeTest;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;
import org.joml.AxisAngle4f;
import org.joml.Quaterniond;
import org.joml.Quaternionf;
import org.joml.Vector3d;
import org.joml.Vector3f;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;

public class AutomobileEntity extends Entity implements RenderableAutomobile, EntityWithInventory, EntityWithContainer {
    public static Consumer<AutomobileEntity> engineSound = e -> {};
    public static Consumer<AutomobileEntity> skidSound = e -> {};
    public static Consumer<AutomobileEntity> hornSound = e -> {};

    private static final EntityDataAccessor<Float> REAR_ATTACHMENT_YAW = SynchedEntityData.defineId(AutomobileEntity.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Float> REAR_ATTACHMENT_ANIMATION = SynchedEntityData.defineId(AutomobileEntity.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Float> FRONT_ATTACHMENT_ANIMATION = SynchedEntityData.defineId(AutomobileEntity.class, EntityDataSerializers.FLOAT);

    private static final EntityDataAccessor<Holder<AutomobileFrame>> FRAME_TYPE = SynchedEntityData.defineId(AutomobileEntity.class, AutomobileFrame.SERIALIZER);
    private static final EntityDataAccessor<Holder<AutomobileWheel>> WHEEL_TYPE = SynchedEntityData.defineId(AutomobileEntity.class, AutomobileWheel.SERIALIZER);
    private static final EntityDataAccessor<Holder<AutomobileEngine>> ENGINE_TYPE = SynchedEntityData.defineId(AutomobileEntity.class, AutomobileEngine.SERIALIZER);

    private RearAttachment rearAttachment;
    private FrontAttachment frontAttachment;

    private final AutomobileStats stats = new AutomobileStats();

    public static final int SMALL_TURBO_TIME = 35;
    public static final int MEDIUM_TURBO_TIME = 70;
    public static final int LARGE_TURBO_TIME = 125;
    public static final float TERMINAL_VELOCITY = -1.2f;
    public static final float TRICK_MIN_VELOCITY = 0.4f;

    public final Input input = new Input();
    private boolean prevHoldDrift = input.holdingDrift;
    private boolean prevPrevHoldDrift = input.holdingDrift;
    private boolean prevPrevPrevHoldDrift = input.holdingDrift;

    private long clientTime;

    private double trackedX;
    private double trackedY;
    private double trackedZ;
    private float trackedYaw;
    private int lerpTicks;

    private static final int CLIENT_SYNC_INTERVAL = 4;
    private int clientSyncTicks = CLIENT_SYNC_INTERVAL;

    public final List<HitboxEntity> hitboxes = new ArrayList<>();
    private EntityDimensions size;
    private AABB cullingBox = new AABB(0, 0, 0, 0, 0, 0);

    private boolean dirty = false;

    private float engineSpeed = 0;
    private float boostSpeed = 0;
    private float speedDirection = 0;
    private float lastBoostSpeed = boostSpeed;

    private float lossySyncedEffectiveSpeed = 0;
    private float lastSyncedBoostSpeed = 0;
    private float lossySyncedBoostSpeed = 0;
    private float lastSyncedWheelAngle = 0;
    private float lossySyncedWheelAngle = 0;
    private int dataLerpTicks;

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

    private boolean honking = false;

    private boolean automobileOnGround = true;
    private boolean wasOnGround = automobileOnGround;
    private boolean isFloorDirectlyBelow = true;
    private boolean isFloorWithinOneBlockBelow = true;
    private boolean touchingWall = false;
    private int hadVehicleCollision = 0;

    private Vec3 lastVelocity = Vec3.ZERO;
    private Vec3 lastMeasuredPos = Vec3.ZERO;

    private float measuredAngVel = 0;
    private float smoothAngVel = 0;

    private Vec3 prevTailPos = null;
    private float prevYawForRotate = 0;

    private int slopeStickingTimer = 0;
    private float grip = 1;

    private int suspensionBounceTimer = 0;
    private int lastSusBounceTimer = suspensionBounceTimer;

    private final Deque<Double> prevYDisplacements = new ArrayDeque<>();

    private boolean offRoad = false;
    private Vector3f debrisColor = new Vector3f();

    private int fallTicks = 0;
    private int airTime = 0;

    private int despawnTime = -1;
    private int despawnCountdown = 0;
    private boolean decorative = false;
    private boolean trickBuffered = false;

    private boolean wasEngineRunning = false;

    private float standStillTime = -1.3f;

    public void writeSyncStateData(FriendlyByteBuf buf) {
        buf.writeInt(boostTimer);
        buf.writeInt(airTime);
        buf.writeFloat(steering);
        buf.writeFloat(wheelAngle);
        buf.writeInt(turboCharge);
        buf.writeFloat(engineSpeed);
        buf.writeFloat(boostSpeed);
        input.writePacket(buf);

        buf.writeBoolean(drifting);
        buf.writeBoolean(trickBuffered);
        buf.writeBoolean(burningOut);
    }

    public void readSyncStateData(FriendlyByteBuf buf) {
        boostTimer = buf.readInt();
        airTime = buf.readInt();
        steering = buf.readFloat();
        wheelAngle = buf.readFloat();
        turboCharge = buf.readInt();
        engineSpeed = buf.readFloat();
        boostSpeed = buf.readFloat();
        input.readPacket(buf);

        setDrifting(buf.readBoolean());
        setTrickBuffered(buf.readBoolean());
        setBurningOut(buf.readBoolean());

        this.dataLerpTicks = CLIENT_SYNC_INTERVAL;
    }

    @Override
    public void readAdditionalSaveData(CompoundTag nbt) {
        var reg = this.registryAccess();
        setComponents(
                reg.registryOrThrow(AutomobileFrame.REGISTRY).getHolder(ResourceLocation.tryParse(nbt.getString("frame")))
                        .map(r -> (Holder<AutomobileFrame>)r).orElseGet(() -> Holder.direct(AutomobileFrame.EMPTY)),
                reg.registryOrThrow(AutomobileWheel.REGISTRY).getHolder(ResourceLocation.tryParse(nbt.getString("wheels")))
                        .map(r -> (Holder<AutomobileWheel>)r).orElseGet(() -> Holder.direct(AutomobileWheel.EMPTY)),
                reg.registryOrThrow(AutomobileEngine.REGISTRY).getHolder(ResourceLocation.tryParse(nbt.getString("engine")))
                        .map(r -> (Holder<AutomobileEngine>)r).orElseGet(() -> Holder.direct(AutomobileEngine.EMPTY))
        );

        var rAtt = nbt.getCompound("rearAttachment");
        setRearAttachment(RearAttachment.fromNbt(rAtt));
        rearAttachment.readNbt(rAtt, this.level().registryAccess());

        var fAtt = nbt.getCompound("frontAttachment");
        setFrontAttachment(FrontAttachment.fromNbt(fAtt));
        frontAttachment.readNbt(fAtt, this.level().registryAccess());

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
        honking = nbt.getBoolean("honking");
        turboCharge = nbt.getInt("turboCharge");
        input.accelerating = nbt.getBoolean("accelerating");
        input.braking = nbt.getBoolean("braking");
        input.steering = nbt.getFloat("steeringInput");
        input.holdingDrift = nbt.getBoolean("holdingDrift");
        input.holdingHorn = nbt.getBoolean("holdingHorn");
        fallTicks = nbt.getInt("fallTicks");
        airTime = nbt.getInt("airTime");
        despawnTime = nbt.getInt("despawnTime");
        despawnCountdown = nbt.getInt("despawnCountdown");
        decorative = nbt.getBoolean("decorative");
        trickBuffered = nbt.getBoolean("trickBuffered");
    }

    @Override
    public void addAdditionalSaveData(CompoundTag nbt) {
        this.entityData.get(FRAME_TYPE).unwrapKey().ifPresent(k -> nbt.putString("frame", k.location().toString()));
        this.entityData.get(WHEEL_TYPE).unwrapKey().ifPresent(k -> nbt.putString("wheels", k.location().toString()));
        this.entityData.get(ENGINE_TYPE).unwrapKey().ifPresent(k -> nbt.putString("engine", k.location().toString()));
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
        nbt.putBoolean("honking", honking);
        nbt.putInt("turboCharge", turboCharge);
        nbt.putBoolean("accelerating", input.accelerating);
        nbt.putBoolean("braking", input.braking);
        nbt.putFloat("steeringInput", input.steering);
        nbt.putBoolean("holdingDrift", input.holdingDrift);
        nbt.putBoolean("holdingHorn", input.holdingHorn);
        nbt.putInt("fallTicks", fallTicks);
        nbt.putInt("airTime", airTime);
        nbt.putInt("despawnTime", despawnTime);
        nbt.putInt("despawnCountdown", despawnCountdown);
        nbt.putBoolean("decorative", decorative);
        nbt.putBoolean("trickBuffered", trickBuffered);
    }

    public AutomobileEntity(EntityType<?> type, Level world) {
        super(type, world);

        this.setRearAttachment(RearAttachmentType.REGISTRY.getOrDefault(null));
        this.setFrontAttachment(FrontAttachmentType.REGISTRY.getOrDefault(null));

        this.size = type.getDimensions();
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

    private void controllerAction(Consumer<AutomobileController> action) {
        if (this.level().isClientSide()) {
            if (this.getControllingPassenger() == Minecraft.getInstance().player) {
                action.accept(Platform.get().controller());
            }
        }
    }

    @Override
    public AutomobileFrame getFrame() {
        return this.entityData.get(FRAME_TYPE).value();
    }

    @Override
    public AutomobileWheel getWheels() {
        return this.entityData.get(WHEEL_TYPE).value();
    }

    @Override
    public AutomobileEngine getEngine() {
        return this.entityData.get(ENGINE_TYPE).value();
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
        if (this.isControlledByLocalInstance()) {
            return Mth.lerp(tickDelta, lastWheelAngle, wheelAngle);
        }

        return Mth.lerp(tickDelta, lastSyncedWheelAngle, lossySyncedWheelAngle);
    }

    public float getBoostSpeed(float tickDelta) {
        if (this.isControlledByLocalInstance()) {
            return Mth.lerp(tickDelta, lastBoostSpeed, boostSpeed);
        }

        return Mth.lerp(tickDelta, lastSyncedBoostSpeed, lossySyncedBoostSpeed);
    }

    @Override
    public float getSuspensionBounce(float tickDelta) {
        return Mth.lerp(tickDelta, lastSusBounceTimer, suspensionBounceTimer);
    }

    @Override
    public boolean engineRunning() {
        boolean running = this.boostTimer > 0 || isVehicle();

        if (!running) {
            var fAtt = getFrontAttachment();
            if (fAtt != null) {
                running = fAtt.isProvidingAlternativeInputs(this, getFirstPassenger());
            }
        }

        return running;
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

    public float calculateEffectiveSpeed() {
        if (this.getControllingPassenger() instanceof Player player && player.isLocalPlayer()) {
            return (float) Math.max(this.addedVelocity.length(), Math.abs(this.hSpeed));
        }

        return (float) Math.max(this.addedVelocity.length(), Math.abs(this.engineSpeed + this.boostSpeed));
    }

    public float getEffectiveSpeed() {
        if (this.isControlledByLocalInstance()) {
            return calculateEffectiveSpeed();
        }

        return this.lossySyncedEffectiveSpeed;
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

    public boolean honking() {
        return honking;
    }

    private void setDrifting(boolean drifting) {
        if (this.level().isClientSide()) {
            if (!this.drifting && drifting) {
                skidSound.accept(this);
            }
        }

        this.drifting = drifting;
    }

    private void setTrickBuffered(boolean trickBuffered) {
        this.trickBuffered = trickBuffered;
    }

    private void setBurningOut(boolean burningOut) {
        if (this.level().isClientSide() && !this.drifting && !this.burningOut && burningOut) {
            skidSound.accept(this);
        }

        if (this.burningOut != burningOut || (this.turboCharge >= LARGE_TURBO_TIME) != burningOut) {
            controllerAction(c -> c.updateMaxChargeRumbleState(burningOut));
        }

        this.burningOut = burningOut;
    }

    private void setHonking(boolean honking) {
        if (this.level().isClientSide() && !this.honking && honking) {
            hornSound.accept(this);
        }

        this.honking = honking;
    }

    public boolean isDrifting() {
        return this.drifting;
    }

    public boolean isTrickBuffered() {
        return this.trickBuffered;
    }

    public boolean isBike() {
        return this.getFrame().model().wheelBase().wheelCount() == 2;
    }

    public boolean isDecorative() {
        return this.decorative;
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

    public void setComponents(Holder<AutomobileFrame> frame, Holder<AutomobileWheel> wheel, Holder<AutomobileEngine> engine) {
        this.entityData.set(FRAME_TYPE, frame);
        this.entityData.set(WHEEL_TYPE, wheel);
        this.entityData.set(ENGINE_TYPE, engine);
        this.stats.from(getFrame(), getWheels(), getEngine());

        this.size = getFrame().makeBounds();
        this.displacement.applyWheelbase(getFrame().model().wheelBase());
        this.refreshDimensions();
    }

    public void verifyHitboxesFor(AutomobileFrame frame) {
        this.hitboxes.removeIf(Entity::isRemoved);

        if (this.level().isClientSide()) {
            return;
        }

        var boxes = frame.hitboxes();
        if (boxes.isEmpty()) boxes = List.of(AutomobileFrame.Hitbox.DEFAULT);

        if (this.hitboxes.size() != boxes.size()) {
            this.hitboxes.forEach(e -> e.remove(RemovalReason.DISCARDED));
            this.hitboxes.clear();

            for (var box : boxes) {
                var boxEntity = new HitboxEntity(this.level(), this, box);
                boxEntity.setPos(this.position());
                this.hitboxes.add(boxEntity);

                this.level().addFreshEntity(boxEntity);
            }
        }
    }

    @Override
    public float maxUpStep() {
        return this.getWheels().size();
    }

    public void forPlayersTrackingMe(boolean ignoreDriver, Consumer<ServerPlayer> action) {
        if (level() instanceof ServerLevel sl) {
            var cPos = new ChunkPos(blockPosition());
            for (var p : sl.getPlayers(s -> s.getChunkTrackingView().contains(cPos))) {
                if (ignoreDriver && isDriving(p)) {
                    continue;
                }
                action.accept(p);
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

    public void updateCullingBox() {
        this.cullingBox = super.getBoundingBoxForCulling();
        for (var hitbox : this.hitboxes) {
            this.cullingBox = this.cullingBox.minmax(hitbox.getBoundingBox());
        }
    }

    @Override
    public AABB getBoundingBoxForCulling() {
        return this.cullingBox;
    }

    public boolean hasSpaceForPassengers() {
        var frame = this.getFrame();
        int maxPassengers = this.rearAttachment.isRideable() ? 2 : 1;
        if (frame != null) {
            maxPassengers += frame.model().passengerSeats().size();
        }
        return this.getPassengers().size() < maxPassengers;
    }

    public boolean isDriving(@Nullable Entity entity) {
        if (entity != null && entity == getFirstPassenger()) {
            var fAtt = getFrontAttachment();
            if (fAtt != null) {
                return fAtt.canDrive(entity);
            }

            return true;
        }

        return false;
    }

    public void setSpeed(float horizontal, float vertical) {
        this.hSpeed = horizontal;
        this.vSpeed = vertical;
    }

    public void clientOnAboutToDismount() {
        ClientPackets.sendServerboundAutomobileSyncPacket(this);
    }

    private void lerpAutomobileData() {
        this.lastSyncedWheelAngle = this.lossySyncedWheelAngle;
        this.lastSyncedBoostSpeed = this.lossySyncedBoostSpeed;
        if (this.dataLerpTicks <= 0) return;

        this.lossySyncedEffectiveSpeed = this.lossySyncedEffectiveSpeed + (this.calculateEffectiveSpeed() - this.lossySyncedEffectiveSpeed) / (float)this.dataLerpTicks;
        this.lossySyncedBoostSpeed = this.lossySyncedBoostSpeed + (this.boostSpeed - this.lossySyncedBoostSpeed) / (float)this.dataLerpTicks;
        this.lossySyncedWheelAngle = this.lossySyncedWheelAngle + (this.wheelAngle - this.lossySyncedWheelAngle) / (float)this.dataLerpTicks;

        this.dataLerpTicks--;
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

        var fAtt = this.getFrontAttachment();
        boolean fAttDriving = fAtt != null && fAtt.isProvidingAlternativeInputs(this, getFirstPassenger());
        if (!(this.isVehicle() || this.isDriving(this.getFirstPassenger()) || fAttDriving)) {
            input.clearInputs();
        }

        if (this.jumpCooldown > 0) {
            this.jumpCooldown--;
        }

        super.tick();
        if (!this.rearAttachment.type.isEmpty()) this.rearAttachment.tick();
        if (!this.frontAttachment.type.isEmpty()) this.frontAttachment.tick();

        var prevPos = this.position();
        this.prevYawForRotate = getYRot();

        this.setHonking(this.input.holdingHorn);

        lerpAutomobileData();
        positionTrackingTick();
        collisionStateTick();
        steeringTick();
        rampTrickTick();
        driftingTick();
        burnoutTick();

        receiveVehicleCollisions();
        movementTick();
        if (this.isControlledByLocalInstance()) {
            this.move(MoverType.SELF, this.getDeltaMovement());
        }
        postMovementTick();

        this.verifyHitboxesFor(getFrame());
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
            if (fAttDriving) {
                boolean wasHoldingHorn = this.input.holdingHorn;
                fAtt.provideAlternativeInputs(this, this.input, this.getFirstPassenger());
                this.despawnCountdown = 0;

                if (wasHoldingHorn != this.input.holdingHorn) {
                    this.syncData();
                }
            } else if (this.isVehicle()) {
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

            this.clientSyncTicks--;
            if (this.clientSyncTicks <= 0) {
                this.clientSyncTicks = CLIENT_SYNC_INTERVAL;

                ClientPackets.sendServerboundAutomobileSyncPacket(this);
            }

            updateCullingBox();
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
        forPlayersTrackingMe(true, player -> CommonPackets.sendClientboundAutomobileSyncPacket(this, player));
    }

    private void syncAttachments() {
        forPlayersTrackingMe(false, player -> CommonPackets.sendSyncAutomobileAttachmentsPacket(this, player));
    }

    public ItemStack asPrefabItem() {
        return new AutomobileData(Optional.empty(),
                this.entityData.get(FRAME_TYPE).unwrapKey().orElse(AutomobileFrame.EMPTY_KEY),
                this.entityData.get(WHEEL_TYPE).unwrapKey().orElse(AutomobileWheel.EMPTY_KEY),
                this.entityData.get(ENGINE_TYPE).unwrapKey().orElse(AutomobileEngine.EMPTY_KEY)
        ).asStack();
    }

    public void updateEngineSpeed(float speed) {
         if (!this.isControlledByLocalInstance()) {
             return;
         }

         if(engineSpeed > stats.getComfortableSpeed() * 1.25f) {
             speed = stats.getComfortableSpeed() * 1.25f;
         }

         this.engineSpeed = speed;
    }

    public void updateBoostSpeed(float speed) {
        if (!this.isControlledByLocalInstance()) {
            return;
        }

        this.boostSpeed = speed;
    }

    public void movementTick() {
        // Handles boosting
        lastBoostSpeed = boostSpeed;
        if (boostTimer > 0) {
            boostTimer--;
            this.updateBoostSpeed(Math.min(boostPower, boostSpeed + 0.09f));
            if (engineSpeed < stats.getComfortableSpeed()) {
                engineSpeed += 0.012f;
            }
            markDirty();

            if (boostTimer == 0) {
                controllerAction(c -> c.updateBoostingRumbleState(false, 0));
            }
        } else {
            this.updateBoostSpeed(AUtils.zero(boostSpeed, 0.09f));
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
        this.lastMeasuredPos = position();

        // cumulative will be modified by the following code and then the automobile will be moved by it
        // Currently initialized with the value of addedVelocity (which is a general velocity vector applied to the automobile, i.e. for when it bumps into a wall and is pushed back)
        var cumulative = addedVelocity;

        // Reduce gravity underwater
        cumulative = cumulative.add(0, (vSpeed * (isUnderWater() ? 0.15f : 1)), 0);

        // This is the general direction the automobile will move, which is slightly offset to the side when drifting
        this.speedDirection = getYRot() - (drifting ? Math.min(turboCharge * 6, 43 + (-steering * 12)) * driftDir : -steering * 12); //MathHelper.lerp(grip, getYaw(), getYaw() - (drifting ? Math.min(turboCharge * 6, 43 + (-steering * 12)) * driftDir : -steering * 12));

        // Handle acceleration
        if (input.accelerating) {
            float speed = Math.max(this.engineSpeed, 0);
            // yeah ...
            double acc =
                    // The following conditions check whether the automobile should NOT receive normal acceleration
                    // It will not receive this acceleration if the automobile is steering or tight-drifting
                    (
                            (this.drifting && AUtils.haveSameSign(this.steering, this.driftDir)) ||
                            (!this.drifting && this.steering != 0 && hSpeed > 0.5)
                    ) ? (this.hSpeed < stats.getComfortableSpeed() ? 0.001 : 0) // This will supply a small amount of acceleration if the automobile is moving slowly only

                    // Otherwise, it will receive acceleration as normal
                    // It will receive this acceleration if the automobile is moving straight or wide-drifting (the latter slightly reduces acceleration)
                    : calculateAcceleration(speed, stats) * (drifting ? 0.86 : 1) * (engineSpeed > stats.getComfortableSpeed() ? 0.25f : 1) * grip;
            this.updateEngineSpeed(this.engineSpeed + (float) acc);
        }

        // Handle braking/reverse
        if (input.braking) {
            this.updateEngineSpeed(Math.max(this.engineSpeed - 0.15f, -0.25f));
        }
        // Handle when the automobile is rolling to a stop
        if (!input.accelerating && !input.braking) {
            this.updateEngineSpeed(AUtils.zero(this.engineSpeed, 0.025f));
        }

        // Slow the automobile a bit while steering and moving fast
        if (!drifting && steering != 0 && hSpeed > 0.8) {
            this.updateEngineSpeed(engineSpeed - (engineSpeed * 0.00042f));
        }

        if (this.burningOut()) {
            this.updateEngineSpeed(engineSpeed - (engineSpeed * 0.5f));
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

        boolean wasOffRoad = this.offRoad && this.hSpeed > 0.01;

        // Handle being in off-road
        if (boostSpeed < 0.4f && level().getBlockState(blockPosition()).getBlock() instanceof OffRoadBlock block) {
            int layers = level().getBlockState(blockPosition()).getValue(OffRoadBlock.LAYERS);
            float cap = stats.getComfortableSpeed() * (1 - ((float)layers / 3.5f));
            this.updateEngineSpeed(Math.min(cap, engineSpeed));
            this.debrisColor = block.color;
            this.offRoad = true;
        } else this.offRoad = false;

        if ((this.offRoad && this.hSpeed > 0.01) != wasOffRoad) {
            controllerAction(c -> c.updateOffRoadRumbleState(this.offRoad));
        }

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
        float wheelCircumference = (float)(2 * (getWheels().model().radius() / 16) * Math.PI);
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
        var entitiesToHit = new HashSet<LivingEntity>();
        var velAdd = velocity.add(0, 0.1, 0).scale(3);

        for (var box : this.hitboxes) {
            var bbox = box.getBoundingBox().move(velocity.scale(0.5));
            for (var entity : level().getEntities(EntityTypeTest.forClass(Entity.class), bbox, entity -> entity != this && entity.getVehicle() != this)) {
                if (!entity.isInvulnerable() && !entity.isSpectator()) {
                    if (entity instanceof LivingEntity living && entity.getVehicle() != this) {
                        entitiesToHit.add(living);
                    }
                }
            }
        }

        entitiesToHit.forEach(e -> {
            AutomobilityEntities.automobileDamageSource(level()).ifPresent(dmg -> e.hurt(dmg, hSpeed * 10));
            e.push(velAdd.x, velAdd.y, velAdd.z);
        });
    }

    public void receiveVehicleCollisions() {
        if (this.decorative) {
            return;
        }

        var collisions = new HashMap<AutomobileEntity, IncomingCollision>();

        for (var box : this.hitboxes) {
            var bbox = box.getBoundingBox().inflate(0.15);
            for (var hitbox : level().getEntities(EntityTypeTest.forClass(HitboxEntity.class), bbox, h -> h.automobile() != this)) {
                var auto = hitbox.automobile();
                var intersect = hitbox.getBoundingBox().inflate(0.15).intersect(bbox);

                var collDepth = new Vec3(intersect.getXsize(), 0, intersect.getZsize());
                if (auto == null || collisions.containsKey(auto) && collisions.get(auto).depth().lengthSqr() > collDepth.lengthSqr()) {
                    continue;
                }

                var momentum = auto.getMeasuredMovement();
                var origin = intersect.getCenter();

                collisions.put(auto, new IncomingCollision(collDepth, momentum, origin, auto.getFrame().weight()));
            }
        }

        hadVehicleCollision = Math.max(0, hadVehicleCollision - 1);
        for (var col : collisions.values()) {
            var meToCollision = col.origin().subtract(this.position()).multiply(1, 0, 1);
            double hitScale = hadVehicleCollision <= 0 ? 0.15 : 0.07;
            hitScale *= (1 + col.inertia() / this.getFrame().weight()) * 0.5;
            this.addedVelocity = this.addedVelocity.add(
                    meToCollision.reverse().normalize().scale(hitScale * (1 + 0.1 * Math.sqrt(col.velocity().length())) * col.depth().lengthSqr())
                            .multiply(1, 0, 1));

            if (hadVehicleCollision <= 0) {
                level().playLocalSound(this.getX(), this.getY(), this.getZ(), AutomobilitySounds.COLLISION.require(), SoundSource.AMBIENT, 0.22f, 0.7f + (0.06f * (this.level().random.nextFloat() - 0.5f)), false);
                this.engineSpeed *= 0.6f;
                hadVehicleCollision = 12;
            }
        }
    }

    public Vec3 getMeasuredMovement() {
        return position().subtract(lastMeasuredPos);
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
            this.updateEngineSpeed(engineSpeed / 3.6f);
            double knockSpeed = ((-0.2 * hSpeed) - 0.5);
            addedVelocity = addedVelocity.add(Math.sin(angle) * knockSpeed, 0, Math.cos(angle) * knockSpeed);

            level().playLocalSound(this.getX(), this.getY(), this.getZ(), AutomobilitySounds.COLLISION.require(), SoundSource.AMBIENT, 0.76f, 0.65f + (0.06f * (this.level().random.nextFloat() - 0.5f)), true);

            if (isVehicle() && level().isClientSide()) {
                if (getPassengers().stream().anyMatch(p -> p instanceof LocalPlayer)) {
                    controllerAction(c -> c.crashRumble());
                }
            }

        }

        this.touchingWall = false;
        double yDisp = getMeasuredMovement().y();

        // Increment the falling timer
        if (!automobileOnGround && yDisp < 0) {
            fallTicks += 1;
        } else {
            fallTicks = 0;
        }

        // Increment air time timer
        if (!automobileOnGround) {
            airTime += 1;
        } else {
            airTime = 0;
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

        float newAngularSpeed = this.angularSpeed;
        if (this.burningOut()) {
            float speed = (float) this.addedVelocity.length();
            float acc = (1.7f / (1 + this.getFrame().weight())) + (4 * speed);
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

        float yawInc = angularSpeed;
        if (hSpeed == 0 && !this.burningOut()) {
            yawInc = 0;
        }

        // Turns the automobile
        if (this.isControlledByLocalInstance()) {
            this.setYRot(getYRot() + yawInc);

            var first = this.getFirstPassenger();
            if (first != null && this.isDriving(first)) {
                if (inLockedViewMode()) {
                    whenRotatedSmooth(first);
                } else {
                    whenRotated(yawInc, first);
                }
            }
        }

        this.measuredAngVel = yawInc;
        if (this.automobileOnGround()) {
            this.smoothAngVel = Mth.lerp(0.34f, this.smoothAngVel, this.measuredAngVel);
        }
    }

    public void whenRotated(float dYaw, Entity e) {
        e.setYRot(Mth.wrapDegrees(e.getYRot() + dYaw));
        e.setYBodyRot(Mth.wrapDegrees(e.getYRot() + dYaw));
    }

    private void whenRotatedSmooth(Entity passenger) {
        double lockStrength = 0.36;

        if (this.drifting) {
            lockStrength = 0.16;
        } else if (this.burningOut()) {
            lockStrength = 0.43;
        }

        var selfDir = new Vec3(0, 0, 1).yRot((float) Math.toRadians(180 - this.getYRot()));
        var playerDir = new Vec3(0, 0, 1).yRot((float) Math.toRadians(180 - passenger.getYRot()));
        var newDir = playerDir.add(selfDir.scale(lockStrength));

        float rot = 180 - (float) Math.toDegrees(Math.atan2(newDir.x, newDir.z));
        passenger.setYRot(rot);
        passenger.setYBodyRot(rot);
    }

    @Override
    public void move(MoverType movementType, Vec3 movement) {
        if (movementType == MoverType.PLAYER) {
            this.setPos(this.position().add(movement));
            return;
        }

        super.move(movementType, movement);
    }

    @Override
    public boolean causeFallDamage(float fallDistance, float damageMultiplier, DamageSource damageSource) {
        return false; // Riders shouldn't take fall damage
    }

    public boolean isOneOfMyHitboxes(Entity e) {
        return e instanceof HitboxEntity hitbox && hitbox.automobile() == this;
    }

    public void accumulateCollisionAreas(Collection<CollisionArea> areas) {
        this.level().getEntitiesOfClass(Entity.class, this.getBoundingBox().inflate(3, 3, 3),
                        e -> e != this &&
                                e.getVehicle() != this &&
                                !(e instanceof AutomobileEntity) &&
                                !this.isOneOfMyHitboxes(e))
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
                this.displacement.lastVertical = this.displacement.verticalTarget = (this.getY() - this.getWheels().model().radius() / 16);
            }

            this.displacement.postTick();
        } else {
            this.displacement.lastVertical = this.displacement.currVertical = this.displacement.verticalTarget = this.getY();
        }
    }

    public Displacement getDisplacement() {
        return this.displacement;
    }

    public void collisionStateTick() {
        wasOnGround = automobileOnGround;
        automobileOnGround = false;
        isFloorDirectlyBelow = false;
        isFloorWithinOneBlockBelow = false;

        var b = getBoundingBox();
        var groundBox = new AABB(b.minX, b.minY - 0.04, b.minZ, b.maxX, b.minY, b.maxZ);
        var wid = (b.getXsize() + b.getZsize()) * 0.5f;
        var floorBox = new AABB(b.minX + (wid * 0.94), b.minY - 0.05, b.minZ + (wid * 0.94), b.maxX - (wid * 0.94), b.minY, b.maxZ - (wid * 0.94));
        var floorOneBlockBelowBox = new AABB(b.minX, b.minY - 1, b.minZ, b.maxX, b.minY, b.maxZ);
        var wallBox = b.deflate(0.05).move(this.lastVelocity.normalize().scale(0.12));
        var start = new BlockPos((int) Math.floor(b.minX - 0.1), (int) Math.floor(b.minY - 1), (int) Math.floor(b.minZ - 0.1));
        var end = new BlockPos((int) Math.floor(b.maxX + 0.1), (int) Math.floor(b.maxY + 0.2 + this.maxUpStep()), (int) Math.floor(b.maxZ + 0.1));
        var groundCuboid = Shapes.create(groundBox);
        var floorCuboid = Shapes.create(floorBox);
        var floorOneBlockBelowCuboid = Shapes.create(floorOneBlockBelowBox);
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
                        this.isFloorWithinOneBlockBelow |= Shapes.joinIsNotEmpty(blockShape, floorOneBlockBelowCuboid, BooleanOp.AND);
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

    @Override
    public void lerpTo(double x, double y, double z, float yaw, float pitch, int interpolationSteps) {
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

    public float getHandling() {
        return stats.getHandling();
    }

    public void provideClientInput(boolean fwd, boolean back, boolean left, boolean right, boolean space, boolean ctrl) {
        // Receives inputs client-side and sends them to the server
        if (this.input.setDigitalInputs(fwd, back, left, right, space, ctrl)) {
            ClientPackets.sendServerboundAutomobileSyncPacket(this);
        }
    }

    public void boost(float power, int time) {
        if (power > boostPower || time > boostTimer) {
            boostTimer = time;
            boostPower = power;
        }
        if (this.isControlledByLocalInstance()) {
            this.updateEngineSpeed(Math.max(this.engineSpeed, this.stats.getComfortableSpeed() * 0.5f));
        }

        controllerAction(c -> c.updateBoostingRumbleState(true, power));

        float boostSoundPitch = 1.6f - (Math.clamp(power - 0.15f, 0f, 0.25f) * 3);
        float boostSoundVolume = 0.5f;
        level().playLocalSound(getX(), getY(), getZ(), SoundEvents.BLAZE_SHOOT, SoundSource.AMBIENT, boostSoundVolume, boostSoundPitch, true);
    }

    private void steeringTick() {
        // Adjust the steering based on the left/right inputs
        this.lastSteering = steering;
        this.steering = AUtils.shift(this.steering, 0.42f, this.input.steering);
    }

    private void consumeTurboCharge() {
        if (turboCharge > LARGE_TURBO_TIME) {
            boost(0.25f, 38);
        } else if (turboCharge > MEDIUM_TURBO_TIME) {
            boost(0.25f, 21);
        } else if (turboCharge > SMALL_TURBO_TIME) {
            boost(0.25f, 9);
        }
        turboCharge = 0;
    }

    private void driftingTick() {
        int prevTurboCharge = turboCharge;

        // Handles starting a drift
        if ((!prevHoldDrift && input.holdingDrift) || (!prevPrevHoldDrift && prevHoldDrift) || (!prevPrevPrevHoldDrift && prevPrevHoldDrift)) {
            if (steering != 0 && !drifting && hSpeed > 0.4f && automobileOnGround) {
                setDrifting(true);
                controllerAction(AutomobileController::driftChargeRumble);
                driftDir = steering > 0 ? 1 : -1;
                // Reduce speed when a drift starts, based on how long the last drift was for
                // This allows you to do a series of short drifts without tanking all your speed, while still reducing your speed when you begin the drift(s)
                this.updateEngineSpeed(engineSpeed - (0.028f * engineSpeed));
            } else if (steering == 0 && !this.level().isClientSide() && this.getRearAttachment() instanceof DeployableRearAttachment att) {
                att.deploy();
            }
        }

        // Handles drifting effects, ending a drift, and the drift timer (for drift turbos)
        if (drifting) {
            if (this.automobileOnGround()) createDriftParticles();
            // Ending a drift successfully, giving you a turbo boost
            if (prevHoldDrift && !input.holdingDrift) {
                setDrifting(false);
                controllerAction(c -> c.updateMaxChargeRumbleState(false));
                consumeTurboCharge();
            // Ending a drift unsuccessfully, not giving you a boost
            } else if (hSpeed < 0.33f) {
                setDrifting(false);
                controllerAction(c -> c.updateMaxChargeRumbleState(false));
                turboCharge = 0;
            }
            if (automobileOnGround) turboCharge += (input.steering * driftDir > 0) ? 2 : 1;
        }

        if (turboCharge == SMALL_TURBO_TIME || turboCharge == MEDIUM_TURBO_TIME || turboCharge == LARGE_TURBO_TIME) {
            controllerAction(AutomobileController::driftChargeRumble);
        }

        if (turboCharge >= LARGE_TURBO_TIME && prevTurboCharge < LARGE_TURBO_TIME) {
            controllerAction(c -> c.updateMaxChargeRumbleState(true));
        } else if (prevTurboCharge >= LARGE_TURBO_TIME && turboCharge < LARGE_TURBO_TIME) {
            controllerAction(c -> c.updateMaxChargeRumbleState(false));
        }

        if (turboCharge >= SMALL_TURBO_TIME && prevTurboCharge < SMALL_TURBO_TIME) {
            level().playLocalSound(getX(), getY(), getZ(), SoundEvents.WITHER_SHOOT, SoundSource.AMBIENT, 0.09f, 1.5f, true);
        } else if (turboCharge >= MEDIUM_TURBO_TIME && prevTurboCharge < MEDIUM_TURBO_TIME) {
            level().playLocalSound(getX(), getY(), getZ(), SoundEvents.WITHER_SHOOT, SoundSource.AMBIENT, 0.10f, 1.75f, true);
        } else if (turboCharge >= LARGE_TURBO_TIME && prevTurboCharge < LARGE_TURBO_TIME) {
            level().playLocalSound(getX(), getY(), getZ(), SoundEvents.WITHER_SHOOT, SoundSource.AMBIENT, 0.11f, 2.0f, true);
        }

        this.prevPrevPrevHoldDrift = this.prevPrevHoldDrift;
        this.prevPrevHoldDrift = this.prevHoldDrift;
        this.prevHoldDrift = input.holdingDrift;
    }

    private void rampTrickTick() {
        boolean trickCommandEarly = wasOnGround &&
                !automobileOnGround &&
                !isFloorDirectlyBelow &&
                hSpeed > TRICK_MIN_VELOCITY &&
                ((!prevHoldDrift && input.holdingDrift) || (!prevPrevHoldDrift && prevHoldDrift) || (!prevPrevPrevHoldDrift && prevPrevHoldDrift));

        boolean trickCommandLate = !wasOnGround &&
                !automobileOnGround &&
                !isFloorDirectlyBelow &&
                hSpeed > TRICK_MIN_VELOCITY &&
                airTime < 2 &&
                (!prevHoldDrift && input.holdingDrift);

        if (trickCommandEarly || trickCommandLate) {
            setDrifting(false);
            spawnTrickEffect();
            level().playLocalSound(getX(), getY(), getZ(), SoundEvents.EXPERIENCE_ORB_PICKUP, SoundSource.AMBIENT, 0.25f, 1.5f, true);
            trickBuffered = true;
        }

        if ((isFloorWithinOneBlockBelow || isFloorDirectlyBelow || automobileOnGround) && trickBuffered) {
            boost(0.25f, 9);
            trickBuffered = false;
        }
    }

    private void spawnTrickEffect() {

        for (int i = 0; i < 6; i++) {
            for (int j = 0; j < 6; j++) {
                double xVel = Math.sin(i) * Math.cos(j) * 2;
                double yVel = Math.sin(i) * Math.sin(j) * 2;
                double zVel = Math.cos(i) * 2;

                level().addParticle(
                        ParticleTypes.CRIT,
                        getX(),
                        getY() + 0.5,
                        getZ(),
                        xVel,
                        yVel,
                        zVel);
            }
        }
    }

    private void endBurnout() {
        setBurningOut(false);
        this.updateEngineSpeed(0);
    }

    private void burnoutTick() {
        if (this.burningOut()) {
            if (this.automobileOnGround()) {
                if (this.addedVelocity.length() > 0.05 || Math.abs(this.angularSpeed) > 0.05) {
                    createDriftParticles();
                }
                if (hSpeed < 0.08 && turboCharge <= SMALL_TURBO_TIME) turboCharge += 1;
            }
            if (!input.braking) {
                endBurnout();
                consumeTurboCharge();
            } else if (!input.accelerating) {
                endBurnout();
                this.turboCharge = 0;
            }
            this.wheelAngle += 20;
        } else if ((input.accelerating || hSpeed > 0.05) && input.braking) {
            setBurningOut(true);
            this.turboCharge = 0;
        }
    }

    public void createDriftParticles() {
        for (var wheel : this.getFrame().model().wheelBase().wheels()) {
            if (wheel.end() == WheelBase.WheelEnd.BACK) {
                var pos = new Vector3d(wheel.right() + ((wheel.right() > 0 ? 1 : -1) * this.getWheels().model().width() * wheel.scale()), 0, wheel.forward())
                        .mul(0.0625);

                this.localPosToWorldSpace(pos);
                pos.add(0, 0.4, 0);

                level().addParticle(AutomobilityParticles.DRIFT_SMOKE.require(), pos.x(), pos.y(), pos.z(), 0, 0, 0);
            }
        }
    }

    private static boolean inLockedViewMode() {
        return Platform.get().controller().inControllerMode();
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

        boolean canDrive = true;
        var fAtt = getFrontAttachment();
        if (fAtt != null) {
            canDrive = fAtt.canDrive(firstPassenger);
        }

        if (firstPassenger instanceof LivingEntity living && canDrive) {
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
    protected void addPassenger(Entity passenger) {
        if (passenger.getVehicle() != this) {
            super.addPassenger(passenger);
        } else {
            if (this.passengers.isEmpty()) {
                this.passengers = ImmutableList.of(passenger);
            } else {
                var newPassengerList = Lists.newArrayList(this.passengers);
                if (!this.level().isClientSide() && passenger instanceof Player player &&
                        this.canKickPassengerOut(getFirstPassenger(), player)) {
                    newPassengerList.addFirst(passenger);
                } else newPassengerList.add(passenger);

                this.passengers = ImmutableList.copyOf(newPassengerList);
            }

            this.gameEvent(GameEvent.ENTITY_MOUNT, passenger);
        }
    }

    @Override
    public boolean hasInventory(@Nullable Player player) {
        return this.getRearAttachment().hasMenu(player);
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
        this.entityData.get(FRAME_TYPE).unwrapKey().ifPresent(key ->
                level().addFreshEntity(new ItemEntity(level(), pos.x, pos.y, pos.z, AutomobilityItems.AUTOMOBILE_FRAME.require().createStack(key))));
        this.entityData.get(ENGINE_TYPE).unwrapKey().ifPresent(key ->
                level().addFreshEntity(new ItemEntity(level(), pos.x, pos.y, pos.z, AutomobilityItems.AUTOMOBILE_ENGINE.require().createStack(key))));

        this.entityData.get(WHEEL_TYPE).unwrapKey().ifPresent(key -> {
            var wheelStack = AutomobilityItems.AUTOMOBILE_WHEEL.require().createStack(key);
            wheelStack.setCount(this.getFrame().model().wheelBase().wheelCount());
            level().addFreshEntity(new ItemEntity(level(), pos.x, pos.y, pos.z, wheelStack));
        });
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

    public boolean canKickPassengerOut(@Nullable Entity passenger, Player kickerOuter) {
        if (passenger != null) {
            if (passenger instanceof Player) {
                return false;
            }

            var fAtt = getFrontAttachment();
            if (fAtt != null && fAtt.canDrive(passenger)) {
                return kickerOuter.isCreative();
            }

            if (passenger.isInvulnerable()) {
                return !this.canAddPassenger(kickerOuter) && kickerOuter.isCreative();
            }
        }

        return true;
    }

    public InteractionResult handleInteraction(Player player, InteractionHand hand) {
        if (player.isShiftKeyDown()) {
            if (this.hasInventory(player)) {
                if (!level().isClientSide()) {
                    openInventory(player);
                    return InteractionResult.PASS;
                } else {
                    return InteractionResult.SUCCESS;
                }
            }
        }

        var stack = player.getItemInHand(hand);
        boolean vulnerable = !this.isInvulnerable() || player.isCreative();

        if (vulnerable && stack.is(AutomobilityItems.CROWBAR.require())) {
            double playerAngle = Math.toDegrees(Math.atan2(player.getZ() - this.getZ(), player.getX() - this.getX()));
            double angleDiff = Mth.wrapDegrees(this.getYRot() - playerAngle);

            if (angleDiff < 0 && !this.frontAttachment.type.isEmpty()) {
                if (level().isClientSide()) {
                    return InteractionResult.SUCCESS;
                }

                this.destroyFrontAttachment(!player.isCreative());
                this.playHitSound(this.getHeadPos());

                return InteractionResult.PASS;
            } else if (!this.rearAttachment.type.isEmpty()) {
                if (level().isClientSide()) {
                    return InteractionResult.SUCCESS;
                }

                this.destroyRearAttachment(!player.isCreative());
                this.playHitSound(this.rearAttachment.pos());

                return InteractionResult.PASS;
            } else {
                if (level().isClientSide()) {
                    return InteractionResult.SUCCESS;
                }

                this.destroyAutomobile(!player.isCreative(), RemovalReason.KILLED);
                this.playHitSound(this.position());

                return InteractionResult.PASS;
            }
        }

        if (vulnerable && stack.getItem() instanceof AutomobileInteractable interactable) {
            return interactable.interactAutomobile(stack, player, hand, this);
        }

        if (!this.decorative) {
            if (!this.hasSpaceForPassengers()) {
                if (canKickPassengerOut(this.getFirstPassenger(), player)) {
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

    public Vec3 fixPassengerRidingOffset(Vec3 offset) {
        return offset.add(0, getWheels().model().radius(), 0).scale(1.0 / 16);
    }

    @Override
    public void positionRider(Entity passenger, Entity.MoveFunction moveFunc) {
        if (getFrame() == null) {
            super.positionRider(passenger, moveFunc);
        }

        var attPoint = passenger.getVehicleAttachmentPoint(this);
        double attYOffset = passenger.getEyeHeight() - attPoint.y();
        var frameModel = getFrame().model();

        if (this.hasPassenger(passenger)) {
            int idx = this.getPassengers().indexOf(passenger);
            var offset = Vec3.ZERO;

            if (idx == 0) {
                offset = this.fixPassengerRidingOffset(frameModel.driverSeatPos());
            } else if (idx <= frameModel.passengerSeats().size()) {
                offset = this.fixPassengerRidingOffset(frameModel.passengerSeats().get(idx - 1));
            } else {
                offset = new Vec3(0, 0, -this.getFrame().model().rearAttachmentPos() / 16)
                        .add(this.rearAttachment.scaledYawVec().yRot((float) Math.toRadians(this.getYRot())))
                        .add(0, this.rearAttachment.getPassengerHeightOffset(), 0);
            }

            var pos = new Vector3d(offset.x(), offset.y() + attYOffset, offset.z());
            this.localPosToWorldSpace(pos);
            pos.sub(attPoint.x(), attPoint.y() + attYOffset, attPoint.z());

            moveFunc.accept(passenger, pos.x(), pos.y(), pos.z());

            if (getControllingPassenger() != passenger) whenRotated(this.getYRot() - this.prevYawForRotate, passenger);
        }
    }

    @Override
    public Vec3 getDismountLocationForPassenger(LivingEntity passenger) {
        float maxWidth = this.getBbWidth();
        float maxHeight = this.getBbHeight();

        for (var box : this.getFrame().hitboxes()) {
            var origin = box.origin();
            if (Math.abs(origin.z()) > 0.5f * box.width()) {
                continue;
            }

            float width = ((float) Math.abs(origin.x()) + 0.5f * box.width()) * 2;
            if (width > maxWidth) maxWidth = width;

            float height = (float) (origin.y() + box.height() * 0.5);
            if (height > maxHeight) maxHeight = height;
        }

        float yaw = this.getYRot() + (passenger.getMainArm() == HumanoidArm.RIGHT ? 90 : -90);
        var dir = Entity.getCollisionHorizontalEscapeVector(maxWidth + 0.25, passenger.getBbWidth(), yaw);

        var scanPos = new Vector3d(
                dir.x() + getX(),
                dir.y() + this.displacement.getVertical(1),
                dir.z() + getZ());

        var pos = new BlockPos.MutableBlockPos();
        double maxDismountHeight = this.getBoundingBox().maxY + 0.75;

        for (var pose : passenger.getDismountPoses()) {
            pos.set(scanPos.x(), scanPos.y(), scanPos.z());

            while (true) {
                double height = this.level().getBlockFloorHeight(pos);
                if (pos.getY() + height > maxDismountHeight) break;

                if (DismountHelper.isBlockFloorValid(height)) {
                    var bounds = passenger.getLocalBoundsForPose(pose);
                    var dismountPos = new Vec3(scanPos.x(), (double)pos.getY() + height, scanPos.z());
                    if (DismountHelper.canDismountTo(this.level(), passenger, bounds.move(dismountPos))) {
                        passenger.setPose(pose);
                        return dismountPos;
                    }
                }

                pos.move(Direction.UP);
                if (pos.getY() > maxDismountHeight) break;
            }
        }

        return new Vec3(this.getX(), this.getY() + maxHeight, this.getZ());
    }

    @Override
    public boolean canCollideWith(Entity entity) {
        return entity instanceof HitboxEntity hitbox && hitbox.automobile() != this;
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        builder.define(REAR_ATTACHMENT_YAW, 0f);
        builder.define(REAR_ATTACHMENT_ANIMATION, 0f);
        builder.define(FRONT_ATTACHMENT_ANIMATION, 0f);

        builder.define(FRAME_TYPE, Holder.direct(AutomobileFrame.EMPTY));
        builder.define(WHEEL_TYPE, Holder.direct(AutomobileWheel.EMPTY));
        builder.define(ENGINE_TYPE, Holder.direct(AutomobileEngine.EMPTY));
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
        } else if (FRAME_TYPE.equals(data)) {
            this.displacement.applyWheelbase(getFrame().model().wheelBase());

            this.size = getFrame().makeBounds();
            this.refreshDimensions();
        }

        if (FRAME_TYPE.equals(data) || WHEEL_TYPE.equals(data) || ENGINE_TYPE.equals(data)) {
            this.stats.from(getFrame(), getWheels(), getEngine());
        }
    }

    @Override
    public Packet<ClientGamePacketListener> getAddEntityPacket(ServerEntity entity) {
        return new ClientboundAddEntityPacket(this, entity);
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
        controllerAction(AutomobileController::groundThudRumble);
    }

    @Override
    public Vec3 collide(Vec3 mvmt) {
        var myCollider = this.getBoundingBox();

        var entityColliders = this.level().getEntityCollisions(this, myCollider.expandTowards(mvmt));
        var mvmtHit = mvmt.lengthSqr() == 0 ? mvmt : collideBoundingBox(this, mvmt, myCollider, this.level(), entityColliders);

        boolean movedIntoGround = mvmt.y != mvmtHit.y && mvmt.y < 0;

        if (this.maxUpStep() > 0 &&
                (movedIntoGround || this.onGround()) &&
                (mvmt.x != mvmtHit.x || mvmt.z != mvmtHit.z)) {
            double upStep = this.maxUpStep();
            var mvmtUpHit = moveAndCollide(this, new Vec3(mvmt.x, upStep, mvmt.z), myCollider, this.level(), entityColliders);
            var mvmtHorizHit = moveAndCollide(this, new Vec3(0, upStep, 0), myCollider.expandTowards(mvmt.x, 0, mvmt.z), this.level(), entityColliders);
            if (mvmtHorizHit.y < upStep) {
                var mvmtHorizThenUp = moveAndCollide(this, new Vec3(mvmt.x, 0, mvmt.z), myCollider.move(mvmtHorizHit), this.level(), entityColliders).add(mvmtHorizHit);
                if (mvmtHorizThenUp.horizontalDistanceSqr() > mvmtUpHit.horizontalDistanceSqr()) {
                    mvmtUpHit = mvmtHorizThenUp;
                }
            }

            if (mvmtUpHit.horizontalDistanceSqr() > mvmtHit.horizontalDistanceSqr()) {
                return mvmtUpHit.add(moveAndCollide(this, new Vec3(0, mvmt.y - mvmtUpHit.y, 0), myCollider.move(mvmtUpHit), this.level(), entityColliders));
            }
        }

        return mvmtHit;
    }

    public static Vec3 moveAndCollide(Entity entity, Vec3 mvmt, AABB myCollider, Level level, List<VoxelShape> nearbyEntities) {
        var colliders = collectColliders(entity, level, nearbyEntities, myCollider.expandTowards(mvmt));
        return collideWithShapes(mvmt, myCollider, colliders);
    }

    /**
     * Transform a local (model space) position into world space
     */
    public void localPosToWorldSpace(Vector3d position) {
        position.rotateY(Math.toRadians(-this.getYRot()));
        var disp = this.getDisplacement();

        var rot = new Quaternionf();
        disp.getAngular(1, rot);

        position.rotate(new Quaterniond(rot.x(), rot.y(), rot.z(), rot.w()));

        var pos = this.position();
        position.add(pos.x(), pos.y() + disp.getVerticalOffset(1, this), pos.z());
    }

    @Override
    public EntityDimensions getDimensions(Pose pose) {
        return size;
    }

    @Override
    public Container underlyingContainer() {
        if (getRearAttachment() instanceof Container container) {
            return container;
        }

        return null;
    }

    public static final class Displacement {
        private static final int SCAN_STEPS_PER_BLOCK = 20;
        private static final double INV_SCAN_STEPS = 1d / SCAN_STEPS_PER_BLOCK;

        private boolean wereAllOnGround = true;
        private double lastVertical = 0;
        private double currVertical = 0;
        private double verticalTarget = 0;
        private final Quaternionf lastAngular = new Quaternionf();
        private final Quaternionf currAngular = new Quaternionf();
        private final Quaternionf angularTarget = new Quaternionf();
        private final List<Vec3> scanPoints = new ArrayList<>();
        public final Set<CollisionArea> otherColliders = new HashSet<>();

        public void preTick() {
            this.lastVertical = currVertical;
            this.lastAngular.set(this.currAngular);
        }

        public void postTick() {
            float lerpAmount = 1;

            var rotation = angularTarget.mul(currAngular.invert(new Quaternionf()), new Quaternionf());
            var rotationInfo = new AxisAngle4f(rotation);
            if (rotationInfo.angle > 0) {
                lerpAmount = (float) Math.min(rotationInfo.angle, Math.toRadians(7)) / rotationInfo.angle;
                lerpAmount = Mth.clamp(lerpAmount, 0, 1);
                this.currAngular.slerp(this.angularTarget, lerpAmount);
            }

            this.currVertical = Mth.lerp(Mth.sqrt(lerpAmount), this.currVertical, this.verticalTarget);
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
                    var state = world.getBlockState(mpos);
                    if (state.getBlock() instanceof SpecialAutomobileColliderBlock special) {
                        colliders.add(special.getCollisionArea(state, world, mpos, INV_SCAN_STEPS * 2));
                    } else {
                        var shape = state.getCollisionShape(world, mpos);
                        if (!shape.isEmpty()) {
                            if (shape == Shapes.block()) {
                                colliders.add(CollisionArea.box(mpos.getX(), mpos.getY() - (INV_SCAN_STEPS * 2), mpos.getZ(), mpos.getX() + 1, mpos.getY() + 1, mpos.getZ() + 1));
                            } else {
                                shape.move(mpos.getX(), mpos.getY(), mpos.getZ()).forAllBoxes(((minX, minY, minZ, maxX, maxY, maxZ) ->
                                        colliders.add(CollisionArea.box(minX, minY - (INV_SCAN_STEPS * 2), minZ, maxX, maxY, maxZ))));
                            }
                        }
                    }
                }

                var pointDir = new Vec3(scanPoint.x, 0, scanPoint.z).normalize().scale(INV_SCAN_STEPS);

                double pointY = centerPos.y;
                for (int i = 0; i < Math.ceil(scanDist * SCAN_STEPS_PER_BLOCK); i++) {
                    double pointX = centerPos.x + (i * pointDir.x);
                    double pointZ = centerPos.z + (i * pointDir.z);
                    double originalPointY = pointY;
                    pointY -= INV_SCAN_STEPS * 1.5;

                    boolean ground = false;
                    double nextPointY = pointY;

                    for (int j = 0; j < 2; j++) for (var col : colliders) {
                        if (col.isPointInside(pointX, nextPointY, pointZ)) {
                            nextPointY = Math.max(nextPointY, col.highestY(pointX, nextPointY, pointZ));
                            pointY = originalPointY;
                        }
                    }

                    if (nextPointY - pointY < (stepHeight + (INV_SCAN_STEPS * 1.5))) {
                        pointY = nextPointY;
                        ground = true;
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

            verticalTarget = centerPos.y;
            if (!anyOnGround) {
                return;
            }

            angularTarget.identity();

            if (lowestDisplacementPos != null) {
                var displacementCenterPos = new Vec3(centerPos.x, (lowestDisplacementPos.y + highestDisplacementPos.y) * 0.5, centerPos.z);
                var combinedNormals = Vec3.ZERO;
                int normalCount = 0;

                if (scannedPoints.size() == 2) {
                    var forward = highestDisplacementPos.subtract(lowestDisplacementPos);
                    var alongGround = forward.multiply(1, 0, 1);
                    var side = forward.cross(alongGround).normalize();

                    if (side.lengthSqr() > 0.0001) {
                        combinedNormals = forward.cross(side).normalize();
                        normalCount = 1;
                    }
                } else {
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
                }

                combinedNormals = normalCount > 0 ? combinedNormals.scale(1f / normalCount) : new Vec3(0, 1, 0);

                var up = new Vector3f(0, 1, 0);
                var rotatedUp = new Vector3f((float) combinedNormals.x(), (float) combinedNormals.y(), (float) combinedNormals.z());

                if (entity.isBike()) {
                    double sign = entity.input.braking && !entity.input.accelerating ? 1 : -1;
                    var side = new Vec3(entity.smoothAngVel * sign * (entity.isDrifting() ? 0.16 : 0.08), 0, 0).yRot((float) -Math.toRadians(entity.getYRot()));
                    rotatedUp.add((float) side.x(), (float) side.y(), (float) side.z());

                    double overSpeed = entity.getMeasuredMovement().lengthSqr() - Math.pow(entity.stats.getComfortableSpeed() * 0.95, 2);
                    double boost = entity.getBoostTimer() - 5;
                    if (!entity.isDrifting() && boost > 0 && overSpeed > 0) {
                        double wheelie = -overSpeed * 0.65 * Math.clamp(Math.sqrt(boost * 0.2), 0, 1);
                        double base = entity.getFrame().model().wheelBase().longLength();
                        double sine = -wheelie / Math.sqrt(rotatedUp.lengthSquared() + wheelie * wheelie);

                        var back = new Vec3(0, 0, (float) wheelie).yRot((float) -Math.toRadians(entity.getYRot()));
                        rotatedUp.add((float) back.x(), (float) back.y(), (float) back.z());
                        displacementCenterPos = displacementCenterPos.add(0, base * sine * 0.5, 0);
                    }
                }

                rotatedUp.normalize();
                up.rotationTo(rotatedUp, this.angularTarget);

                verticalTarget = (float) displacementCenterPos.y;
            }
        }

        public void applyWheelbase(WheelBase wheelBase) {
            this.scanPoints.clear();
            for (WheelBase.WheelPos pos : wheelBase.wheels()) if (pos.end() != WheelBase.WheelEnd.NONE) {
                this.scanPoints.add(new Vec3(pos.right() / 16, 0, pos.forward() / 16));
            }
        }

        public double getVertical(float tickDelta) {
            return Mth.lerp(tickDelta, lastVertical, currVertical);
        }

        public float getVerticalOffset(float tickDelta, AutomobileEntity entity) {
            return (float) (getVertical(tickDelta) - entity.getPosition(tickDelta).y());
        }

        public void getAngular(float tickDelta, Quaternionf rot) {
            this.lastAngular.slerp(this.currAngular, tickDelta, rot);
        }
    }

    public static class Input {
        public boolean accelerating, braking, holdingDrift, holdingHorn;
        public float steering;

        public boolean setDigitalInputs(boolean fwd, boolean back, boolean left, boolean right, boolean space, boolean ctrl) {
            boolean isLeft = steering < 0;
            boolean isRight = steering > 0;

            boolean changed = fwd != this.accelerating || back != this.braking || left != isLeft || right != isRight ||
                    space != this.holdingDrift || ctrl != this.holdingHorn;

            this.accelerating = fwd;
            this.braking = back;
            this.steering = (left ? -1 : 0) + (right ? 1 : 0);
            this.holdingDrift = space;
            this.holdingHorn = ctrl;

            return changed;
        }

        public boolean setInputs(boolean fwd, boolean back, float side, boolean space, boolean ctrl) {
            boolean changed = fwd != this.accelerating || back != this.braking || this.steering != side ||
                    space != this.holdingDrift || ctrl != this.holdingHorn;

            this.accelerating = fwd;
            this.braking = back;
            this.steering = side;
            this.holdingDrift = space;
            this.holdingHorn = ctrl;

            return changed;
        }

        public void clearInputs() {
            this.accelerating = false;
            this.braking = false;
            this.steering = 0;
            this.holdingDrift = false;
            this.holdingHorn = false;
        }

        public void writePacket(FriendlyByteBuf buf) {
            buf.writeBoolean(this.accelerating);
            buf.writeBoolean(this.braking);
            buf.writeFloat(this.steering);
            buf.writeBoolean(this.holdingDrift);
            buf.writeBoolean(this.holdingHorn);
        }

        public void readPacket(FriendlyByteBuf buf) {
            this.accelerating = buf.readBoolean();
            this.braking = buf.readBoolean();
            this.steering = buf.readFloat();
            this.holdingDrift = buf.readBoolean();
            this.holdingHorn = buf.readBoolean();
        }
    }

    public record IncomingCollision(Vec3 depth, Vec3 velocity, Vec3 origin, float inertia) {
    }
}
