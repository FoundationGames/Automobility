package io.github.foundationgames.automobility.entity;

import io.github.foundationgames.automobility.automobile.AutomobileFrame;
import io.github.foundationgames.automobility.automobile.AutomobileWheel;
import io.github.foundationgames.automobility.util.AUtils;
import io.github.foundationgames.automobility.util.network.PayloadPackets;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.block.SideShapeType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.MovementType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.vehicle.BoatEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.Packet;
import net.minecraft.network.packet.s2c.play.EntitySpawnS2CPacket;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.*;
import net.minecraft.world.World;

public class AutomobileEntity extends Entity {
    private AutomobileFrame frame = AutomobileFrame.REGISTRY.getOrDefault(null);
    private AutomobileWheel wheels = AutomobileWheel.REGISTRY.getOrDefault(null);

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

    // Duplicating these fields and tracking them manually makes the automobile not have a seizure every few seconds
    private float yaw = super.getYaw();
    private float lastYaw = yaw;

    private boolean drifting = false;
    private int driftDir = 0;
    private int driftTimer = 0;
    private int lastDriftTimer = driftTimer;

    @Override
    protected void readCustomDataFromNbt(NbtCompound nbt) {
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

        yaw = nbt.getFloat("yaw");
    }

    @Override
    protected void writeCustomDataToNbt(NbtCompound nbt) {
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

        nbt.putFloat("yaw", yaw);
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

    public float getWeight() {
        return this.frame.weight();
    }

    public float getHandling() {
        return 0.42f;
    }

    public float getHSpeed() {
        return hSpeed;
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
        processSteerInputs();
        processDriftInputs();
        movementTick();
    }

    // feast your eyes
    public void movementTick() {
        if (boostTimer > 0) {
            boostTimer--;
            boostSpeed = Math.min(boostPower, boostSpeed + 0.09f);
        } else {
            boostSpeed = AUtils.zero(boostSpeed, 0.1f);
        }
        if (drifting) {
            driftTimer++;
        }

        var lastPos = getPos();

        var cumulative = addedVelocity;
        lastWheelAngle = wheelAngle;

        if (this.isOnGround()) {
            verticalSpeed = 0;
        } else {
            verticalSpeed = Math.max(verticalSpeed - 0.15f, -0.7f);
        }

        cumulative = cumulative.add(0, verticalSpeed * (isSubmergedInWater() ? 0.15f : 1), 0);

        this.speedDirection = getYaw() - (drifting ? Math.min(driftTimer * 6, 43 + (steering * 12)) * driftDir : 0);

        if (inFwd && this.engineSpeed >= 0) {
            // Torque stat will be added when engine types are added
            this.engineSpeed += this.drifting ? 0 : calculateAcceleration(this.engineSpeed, getWeight(), this.wheels.size(), 0.5f);
        }
        if (inBack) {
            this.engineSpeed = Math.max(this.engineSpeed - 0.15f, -0.25f);
        }
        if (!inFwd && !inBack) {
            this.engineSpeed = AUtils.zero(this.engineSpeed, (Math.max(0, 1 - getWeight()) * 0.08f) + 0.03f);
        }

        hSpeed = engineSpeed + (boostTimer > 0 ? boostSpeed : 0);
        float angle = (float) Math.toRadians(-speedDirection);
        cumulative = cumulative.add(Math.sin(angle) * hSpeed, 0, Math.cos(angle) * hSpeed);

        wheelAngle += hSpeed * 100f;

        this.move(MovementType.SELF, cumulative);

        addedVelocity = new Vec3d(
                AUtils.zero((float)addedVelocity.x, 0.1f),
                AUtils.zero((float)addedVelocity.y, 0.1f),
                AUtils.zero((float)addedVelocity.z, 0.1f)
        );

        var displacement = getPos().subtract(lastPos);
        if (hSpeed > 0.1 && displacement.length() < hSpeed - 0.17 && verticalSpeed < 0.2 && verticalSpeed > -0.2 && addedVelocity.length() < 0.05) {
            engineSpeed /= 3.6;
            float knockSpeed = ((-0.2f * hSpeed) - 0.5f);
            addedVelocity = addedVelocity.add(Math.sin(angle) * knockSpeed, 0, Math.cos(angle) * knockSpeed);
        }

        lastYaw = yaw;
        if (hSpeed != 0) {
            float yawInc = drifting ? ((this.steering + (driftDir)) * driftDir * 2.5f + 1.5f) * driftDir : this.steering * ((4 * hSpeed) + (hSpeed > 0 ? 2 : -3.5f));
            this.setYaw(getYaw() + yawInc);
            for (Entity e : getPassengerList()) {
                e.setYaw(e.getYaw() + yawInc);
                e.setBodyYaw(e.getYaw() + yawInc);
            }
        }

        lastVTravelPitch = verticalTravelPitch;
        BlockPos below = new BlockPos(Math.floor(getX()), Math.floor(getY() - 0.05), Math.floor(getZ()));
        if ((!(verticalSpeed < 0.2 && verticalSpeed > -0.2)) && !world.getBlockState(below).isSideSolid(world, below, Direction.UP, SideShapeType.RIGID)) {
            if (verticalSpeed > 0) {
                verticalTravelPitch = Math.min(verticalTravelPitch + 13, 45) * (hSpeed > 0 ? 1 : -1);
            } else {
                verticalTravelPitch = Math.max(verticalTravelPitch - 13, -45) * (hSpeed > 0 ? 1 : -1);
            }
        } else {
            verticalTravelPitch = AUtils.zero(verticalTravelPitch, 22);
        }
    }

    @Override
    protected void onBlockCollision(BlockState state) {
        super.onBlockCollision(state);
    }

    private float calculateAcceleration(float speed, float weight, float wheelSize, float torque) {
        return (1f / ((200 * speed) + 9f)) * 0.7f;
    }

    @Environment(EnvType.CLIENT)
    public void provideClientInput(boolean fwd, boolean back, boolean left, boolean right, boolean space) {
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

    private void processSteerInputs() {
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

    private void processDriftInputs() {
        if (steering != 0) {
            if (!prevSpace && inSpace && hSpeed > 0.5f) {
                drifting = true;
                driftDir = steering > 0 ? 1 : -1;
                driftTimer = 0;
            }
        }
        if (drifting) {
            if (prevSpace && !inSpace) {
                drifting = false;
                if (driftTimer > 50) {
                    boostTimer = 45;
                    boostPower = 0.3f;
                }
            } else if (!inFwd || inBack || hSpeed < 0.5f) {
                drifting = false;
            }
        }
    }

    @Override
    public ActionResult interact(PlayerEntity player, Hand hand) {
        return ActionResult.success(player.startRiding(this));
    }

    @Override
    public double getMountedHeightOffset() {
        return (wheels.model().radiusPx() + frame.model().seatHeight() - 4) / 16;
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
    public float getYaw() {
        return yaw;
    }

    @Override
    public float getYaw(float tickDelta) {
        return MathHelper.lerp(tickDelta, lastYaw, yaw);
    }

    @Override
    public void setYaw(float yaw) {
        this.yaw = yaw;
    }

    @Override
    protected void initDataTracker() {

    }

    @Override
    public Packet<?> createSpawnPacket() {
        return new EntitySpawnS2CPacket(this);
    }
}
