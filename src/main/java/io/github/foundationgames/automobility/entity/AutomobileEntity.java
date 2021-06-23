package io.github.foundationgames.automobility.entity;

import io.github.foundationgames.automobility.automobile.AutomobileFrame;
import io.github.foundationgames.automobility.automobile.AutomobileWheel;
import io.github.foundationgames.automobility.util.AUtils;
import io.github.foundationgames.automobility.util.network.PayloadPackets;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.SideShapeType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.MovementType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.vehicle.BoatEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.Packet;
import net.minecraft.network.packet.s2c.play.EntitySpawnS2CPacket;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class AutomobileEntity extends Entity {
    private AutomobileFrame frame = AutomobileFrame.REGISTRY.getOrDefault(null);
    private AutomobileWheel wheels = AutomobileWheel.REGISTRY.getOrDefault(null);

    public static final int DRIFT_TURBO_TIME = 50;

    private float engineSpeed = 0;
    private float boostSpeed = 0;
    private float speedDirection = 0;

    private int boostTimer = 0;
    private float boostPower = 0;

    private float hSpeed = 0;

    private float verticalSpeed = 0;
    private Vec3d addedVelocity = getVelocity();

    private float steering = 0;
    private float lastSteering = steering;

    private float wheelAngle = 0;
    private float lastWheelAngle = 0;
    
    private float verticalTravelPitch = 0;
    private float lastVTravelPitch = verticalTravelPitch;

    private boolean drifting = false;
    private int driftDir = 0;
    private int driftTimer = 0;
    private int lastDriftTimer = driftTimer;

    @Override
    public void readCustomDataFromNbt(NbtCompound nbt) {
        frame = AutomobileFrame.REGISTRY.getOrDefault(Identifier.tryParse(nbt.getString("frame")));
        wheels = AutomobileWheel.REGISTRY.getOrDefault(Identifier.tryParse(nbt.getString("wheels")));
        engineSpeed = nbt.getFloat("engineSpeed");
        boostSpeed = nbt.getFloat("boostSpeed");
        boostTimer = nbt.getInt("boostTimer");
        boostPower = nbt.getFloat("boostPower");
        speedDirection = nbt.getFloat("speedDirection");
        verticalSpeed = nbt.getFloat("verticalSpeed");
        hSpeed = nbt.getFloat("horizontalSpeed");
        addedVelocity = AUtils.v3dFromNbt(nbt.getCompound("addedVelocity"));
        steering = nbt.getFloat("steering");
        wheelAngle = nbt.getFloat("wheelAngle");
        drifting = nbt.getBoolean("drifting");
        driftDir = nbt.getInt("driftDir");
        driftTimer = nbt.getInt("driftTimer");
        inFwd = nbt.getBoolean("accelerating");
        inBack = nbt.getBoolean("braking");
        inLeft = nbt.getBoolean("steeringLeft");
        inRight = nbt.getBoolean("steeringRight");
        inSpace = nbt.getBoolean("holdingDrift");
    }

    @Override
    public void writeCustomDataToNbt(NbtCompound nbt) {
        nbt.putString("frame", frame.getId().toString());
        nbt.putString("wheels", wheels.getId().toString());
        nbt.putFloat("engineSpeed", engineSpeed);
        nbt.putFloat("boostSpeed", boostSpeed);
        nbt.putFloat("speedDirection", speedDirection);
        nbt.putFloat("verticalSpeed", verticalSpeed);
        nbt.putFloat("horizontalSpeed", hSpeed);
        nbt.put("addedVelocity", AUtils.v3dToNbt(addedVelocity));
        nbt.putFloat("steering", steering);
        nbt.putFloat("wheelAngle", wheelAngle);
        nbt.putBoolean("drifting", drifting);
        nbt.putInt("driftDir", driftDir);
        nbt.putInt("driftTimer", driftTimer);
        nbt.putBoolean("accelerating", inFwd);
        nbt.putBoolean("braking", inBack);
        nbt.putBoolean("steeringLeft", inLeft);
        nbt.putBoolean("steeringRight", inRight);
        nbt.putBoolean("holdingDrift", inSpace);
    }

    private boolean inFwd = false;
    private boolean inBack = false;
    private boolean inLeft = false;
    private boolean inRight = false;
    private boolean inSpace = false;

    private boolean prevSpace = inSpace;

    @Environment(EnvType.CLIENT)
    public boolean updateModels = true;

    public AutomobileEntity(EntityType<?> type, World world) {
        super(type, world);
    }

    public AutomobileFrame getFrame() {
        return frame;
    }

    public AutomobileWheel getWheels() {
        return wheels;
    }

    public float getSteering(float tickDelta) {
        return MathHelper.lerp(tickDelta, lastSteering, steering);
    }

    public float getWheelAngle(float tickDelta) {
        return MathHelper.lerp(tickDelta, lastWheelAngle, wheelAngle);
    }

    public float getVerticalTravelPitch(float tickDelta) {
        return MathHelper.lerp(tickDelta, lastVTravelPitch, verticalTravelPitch);
    }

    public int getDriftTimer() {
        return drifting ? driftTimer : 0;
    }

    public float getWeight() {
        return this.frame.weight();
    }

    public float getHandling() {
        return 0.42f;
    }

    public float getHSpeed() {
        return hSpeed;
    }

    public int getBoostTimer() {
        return boostTimer;
    }

    @Override
    public void baseTick() {
        super.baseTick();
        if (!this.hasPassengers()) {
            inFwd = false;
            inBack = false;
            inLeft = false;
            inRight = false;
            inSpace = false;
        }
        steeringTick();
        driftingTick();
        movementTick();
        updateTrackedPosition(getX(), getY(), getZ());

        if (!world.isClient()) {
            for (PlayerEntity p : world.getPlayers()) {
                if (p != getFirstPassenger() && p.getPos().distanceTo(getPos()) < 100 && p instanceof ServerPlayerEntity player) {
                    PayloadPackets.sendSyncAutomobileDataPacket(this, player);
                }
            }
        }
    }

    // feast your eyes
    public void movementTick() {
        // Handles boosting
        if (boostTimer > 0) {
            boostTimer--;
            boostSpeed = Math.min(boostPower, boostSpeed + 0.05f);
        } else {
            boostSpeed = AUtils.zero(boostSpeed, 0.09f);
        }

        // Handles the drift timer (for drift turbos)
        if (drifting) {
            driftTimer++;
        }

        // Track the last position of the automobile
        var lastPos = getPos();

        // cumulative will be modified by the following code and then the automobile will be moved by it
        // Currently initialized with the value of addedVelocity (which is a general velocity vector applied to the automobile, i.e. for when it bumps into a wall and is pushed back)
        var cumulative = addedVelocity;
        lastWheelAngle = wheelAngle;

        // Handles gravity
        if (this.isOnGround()) {
            verticalSpeed = 0;
        } else {
            verticalSpeed = Math.max(verticalSpeed - 0.15f, -0.7f);
        }

        // Reduce gravity underwater
        cumulative = cumulative.add(0, verticalSpeed * (isSubmergedInWater() ? 0.15f : 1), 0);

        // This is the general direction the automobile will move, which is slightly offset to the side when drifting
        this.speedDirection = getYaw() - (drifting ? Math.min(driftTimer * 6, 43 + (-steering * 12)) * driftDir : 0);

        // Handle acceleration
        if (inFwd) {
            float speed = Math.max(this.engineSpeed, 0);
            // yeah ...
            this.engineSpeed +=
                    // The following conditions check whether the automobile should NOT receive normal acceleration
                    // It will not receive this acceleration if the automobile is steering or tight-drifting
                    (
                            (this.drifting && AUtils.haveSameSign(this.steering, this.driftDir)) ||
                            (!this.drifting && this.steering != 0 && hSpeed > 0.5)
                    ) ? (this.hSpeed < 0.8 ? 0.001 : 0) // This will supply a small amount of acceleration if the automobile is moving slowly only

                    // Otherwise, it will receive acceleration as normal
                    // It will receive this acceleration if the automobile is moving straight or wide-drifting
                    : calculateAcceleration(speed, getWeight(), this.wheels.size(), 0.5f) * (drifting ? 0.69 : 1);
        }
        // Handle braking/reverse
        if (inBack) {
            this.engineSpeed = Math.max(this.engineSpeed - 0.15f, -0.25f);
        }
        // Handle when the automobile is rolling to a stop
        if (!inFwd && !inBack) {
            this.engineSpeed = AUtils.zero(this.engineSpeed, (Math.max(0, 1 - getWeight()) * 0.08f) + 0.03f);
        }

        // Slow the automobile a bit while steering and moving fast
        if (!drifting && steering != 0 && hSpeed > 0.8) {
            engineSpeed -= engineSpeed * 0.00016f;
        }

        // Set the horizontal speed
        hSpeed = engineSpeed + boostSpeed;

        // Apply the horizontal speed to the cumulative movement
        float angle = (float) Math.toRadians(-speedDirection);
        cumulative = cumulative.add(Math.sin(angle) * hSpeed, 0, Math.cos(angle) * hSpeed);

        // Turn the wheels
        wheelAngle += hSpeed * 100f;

        // Move the automobile by the cumulative vector
        this.move(MovementType.SELF, cumulative);

        // Reduce the values of addedVelocity incrementally
        addedVelocity = new Vec3d(
                AUtils.zero((float)addedVelocity.x, 0.1f),
                AUtils.zero((float)addedVelocity.y, 0.1f),
                AUtils.zero((float)addedVelocity.z, 0.1f)
        );

        // This code handles bumping into a wall, yes it is utterly horrendous
        var displacement = getPos().subtract(lastPos);
        if (hSpeed > 0.1 && displacement.length() < hSpeed - 0.17 && verticalSpeed < 0.2 && verticalSpeed > -0.2 && addedVelocity.length() < 0.05) {
            engineSpeed /= 3.6;
            float knockSpeed = ((-0.2f * hSpeed) - 0.5f);
            addedVelocity = addedVelocity.add(Math.sin(angle) * knockSpeed, 0, Math.cos(angle) * knockSpeed);
        }

        // Turns the automobile based on steering/drifting
        if (hSpeed != 0) {
            float yawInc = drifting ? ((this.steering + (driftDir)) * driftDir * 2.5f + 1.5f) * driftDir : this.steering * ((4 * Math.min(hSpeed, 1)) + (hSpeed > 0 ? 2 : -3.5f));
            this.setYaw(getYaw() + yawInc);
            for (Entity e : getPassengerList()) {
                e.setYaw(e.getYaw() + yawInc);
                e.setBodyYaw(e.getYaw() + yawInc);
            }
        }

        // Adjusts the pitch of the automobile when falling onto a block/climbing up a block
        lastVTravelPitch = verticalTravelPitch;
        var below = new BlockPos(Math.floor(getX()), Math.floor(getY() - 0.01), Math.floor(getZ()));
        var moreBelow = new BlockPos(Math.floor(getX()), Math.floor(getY() - 1.01), Math.floor(getZ()));
        if (
                hSpeed != 0 &&
                !(verticalSpeed < 0.2 && verticalSpeed > -0.2) &&
                !world.getBlockState(below).isSideSolid(world, below, Direction.UP, SideShapeType.RIGID) &&
                world.getBlockState(moreBelow).isSideSolid(world, moreBelow, Direction.UP, SideShapeType.RIGID)
        ) {
            if (verticalSpeed > 0) {
                verticalTravelPitch = Math.min(verticalTravelPitch + 13, 45) * (hSpeed > 0 ? 1 : -1);
            } else {
                verticalTravelPitch = Math.max(verticalTravelPitch - 13, -45) * (hSpeed > 0 ? 1 : -1);
            }
        } else {
            verticalTravelPitch = AUtils.zero(verticalTravelPitch, 22);
        }
    }

    private float calculateAcceleration(float speed, float weight, float wheelSize, float torque) {
        // A somewhat over-engineered function to accelerate the automobile, since I didn't want to add a hard speed cap
        return (1f / ((300 * speed) + 17f)) * 0.85f;
    }

    @Environment(EnvType.CLIENT)
    public void provideClientInput(boolean fwd, boolean back, boolean left, boolean right, boolean space) {
        // Receives inputs client-side and sends them to the server
        if (!(
                fwd == inFwd &&
                back == inBack &&
                left == inLeft &&
                right == inRight &&
                space == inSpace
        )) {
            setInputs(fwd, back, left, right, space);
            PayloadPackets.sendSyncAutomobileInputPacket(this, inFwd, inBack, inLeft, inRight, inSpace);
        }
    }

    public void setInputs(boolean fwd, boolean back, boolean left, boolean right, boolean space) {
        this.prevSpace = this.inSpace;
        this.inFwd = fwd;
        this.inBack = back;
        this.inLeft = left;
        this.inRight = right;
        this.inSpace = space;
    }

    private void steeringTick() {
        // Adjust the steering based on the left/right inputs
        this.lastSteering = steering;
        if (inLeft == inRight) {
            this.steering = AUtils.zero(this.steering, getHandling());
        } else if (inLeft) {
            this.steering -= getHandling();
            this.steering = Math.max(-1, this.steering);
        } else {
            this.steering += getHandling();
            this.steering = Math.min(1, this.steering);
        }
    }

    private void driftingTick() {
        // Handles starting a drift
        if (steering != 0) {
            if (!drifting && !prevSpace && inSpace && hSpeed > 0.4f) {
                drifting = true;
                driftDir = steering > 0 ? 1 : -1;
                // Reduce speed when a drift starts, based on how long the last drift was for
                // This allows you to do a series of short drifts without tanking all your speed, while still reducing your speed when you begin the drift(s)
                engineSpeed -= (0.028 * (Math.min(driftTimer, 20f) / 20)) * engineSpeed;
                driftTimer = 0;
            }
        }
        // Handles ending a drift
        if (drifting) {
            // Ending a drift successfully, giving you a turbo boost
            if (prevSpace && !inSpace) {
                drifting = false;
                steering = 0;
                if (driftTimer > DRIFT_TURBO_TIME) {
                    boostTimer = 40;
                    boostPower = 0.38f;
                }
            // Ending a drift unsuccessfully, not giving you a boost
            } else if (hSpeed < 0.33f) {
                drifting = false;
                steering = 0;
            }
        }
    }

    @Nullable
    @Override
    public Entity getPrimaryPassenger() {
        return getFirstPassenger();
    }

    @Override
    public ActionResult interact(PlayerEntity player, Hand hand) {
        return ActionResult.success(player.startRiding(this));
    }

    @Override
    public double getMountedHeightOffset() {
        return (wheels.model().radius() + frame.model().seatHeight() - 4) / 16;
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

    }

    @Override
    public Packet<?> createSpawnPacket() {
        return new EntitySpawnS2CPacket(this);
    }
}
