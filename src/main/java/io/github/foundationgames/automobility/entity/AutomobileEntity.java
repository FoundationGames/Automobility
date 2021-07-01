package io.github.foundationgames.automobility.entity;

import io.github.foundationgames.automobility.automobile.AutomobileEngine;
import io.github.foundationgames.automobility.automobile.AutomobileFrame;
import io.github.foundationgames.automobility.automobile.AutomobileStats;
import io.github.foundationgames.automobility.automobile.AutomobileWheel;
import io.github.foundationgames.automobility.automobile.render.RenderableAutomobile;
import io.github.foundationgames.automobility.block.Sloped;
import io.github.foundationgames.automobility.item.AutomobilityItems;
import io.github.foundationgames.automobility.util.AUtils;
import io.github.foundationgames.automobility.util.lambdacontrols.ControllerUtils;
import io.github.foundationgames.automobility.util.network.PayloadPackets;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.SideShapeType;
import net.minecraft.client.model.Model;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MovementType;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.vehicle.BoatEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.Packet;
import net.minecraft.network.packet.s2c.play.EntitySpawnS2CPacket;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.TypeFilter;
import net.minecraft.util.function.BooleanBiFunction;
import net.minecraft.util.math.*;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

public class AutomobileEntity extends Entity implements RenderableAutomobile {
    private AutomobileFrame frame = AutomobileFrame.REGISTRY.getOrDefault(null);
    private AutomobileWheel wheels = AutomobileWheel.REGISTRY.getOrDefault(null);
    private AutomobileEngine engine = AutomobileEngine.REGISTRY.getOrDefault(null);

    private final AutomobileStats stats = new AutomobileStats();

    @Environment(EnvType.CLIENT)
    private Model frameModel = null;
    @Environment(EnvType.CLIENT)
    private Model wheelModel = null;
    @Environment(EnvType.CLIENT)
    private Model engineModel = null;

    public static final int DRIFT_TURBO_TIME = 50;
    public static final float TERMINAL_VELOCITY = -1.2f;

    private float engineSpeed = 0;
    private float boostSpeed = 0;
    private float speedDirection = 0;
    private float lastBoostSpeed = boostSpeed;

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

    private float lockedViewOffset = 0;

    private float groundSlopeX = 0;
    private float groundSlopeZ = 0;
    private float lastGroundSlopeX = groundSlopeX;
    private float lastGroundSlopeZ = groundSlopeZ;
    private float targetSlopeX = 0;
    private float targetSlopeZ = 0;

    private boolean automobileOnGround = true;
    private boolean wasOnGround = automobileOnGround;
    private boolean isFloorDirectlyBelow = true;

    // Prevents jittering when going down slopes
    private int slopeStickingTimer = 0;

    private int suspensionBounceTimer = 0;
    private int lastSusBounceTimer = suspensionBounceTimer;

    private final Deque<Double> prevYDisplacements = new ArrayDeque<>();

    @Override
    public void readCustomDataFromNbt(NbtCompound nbt) {
        setComponents(
                AutomobileFrame.REGISTRY.getOrDefault(Identifier.tryParse(nbt.getString("frame"))),
                AutomobileWheel.REGISTRY.getOrDefault(Identifier.tryParse(nbt.getString("wheels"))),
                AutomobileEngine.REGISTRY.getOrDefault(Identifier.tryParse(nbt.getString("engine")))
        );
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

        updateModels = true;
    }

    @Override
    public void writeCustomDataToNbt(NbtCompound nbt) {
        nbt.putString("frame", frame.getId().toString());
        nbt.putString("wheels", wheels.getId().toString());
        nbt.putString("engine", engine.getId().toString());
        nbt.putFloat("engineSpeed", engineSpeed);
        nbt.putFloat("boostSpeed", boostSpeed);
        nbt.putInt("boostTimer", boostTimer);
        nbt.putFloat("boostPower", boostPower);
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

    public AutomobileEngine getEngine() {
        return engine;
    }

    @Override
    public float getSteering(float tickDelta) {
        return MathHelper.lerp(tickDelta, lastSteering, steering);
    }

    @Override
    public float getWheelAngle(float tickDelta) {
        return MathHelper.lerp(tickDelta, lastWheelAngle, wheelAngle);
    }

    @Override
    public float getVerticalTravelPitch(float tickDelta) {
        return MathHelper.lerp(tickDelta, lastVTravelPitch, verticalTravelPitch);
    }

    public float getBoostSpeed(float tickDelta) {
        return MathHelper.lerp(tickDelta, lastBoostSpeed, boostSpeed);
    }

    public float getGroundSlopeX(float tickDelta) {
        return MathHelper.lerp(tickDelta, lastGroundSlopeX, groundSlopeX);
    }

    public float getGroundSlopeZ(float tickDelta) {
        return MathHelper.lerp(tickDelta, lastGroundSlopeZ, groundSlopeZ);
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
    public int getDriftTimer() {
        return drifting ? driftTimer : 0;
    }

    @Override
    public long getWorldTime() {
        return world.getTime();
    }

    public float getHSpeed() {
        return hSpeed;
    }

    @Override
    public int getBoostTimer() {
        return boostTimer;
    }

    @Override
    public boolean automobileOnGround() {
        return automobileOnGround;
    }

    public void setComponents(AutomobileFrame frame, AutomobileWheel wheel, AutomobileEngine engine) {
        this.frame = frame;
        this.wheels = wheel;
        this.engine = engine;
        this.updateModels = true;
        this.stepHeight = wheels.size();
        this.stats.from(frame, wheel, engine);
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
        collisionStateTick();
        steeringTick();
        driftingTick();
        movementTick();
        updateTrackedPosition(getX(), getY(), getZ());

        if (!world.isClient()) {
            for (PlayerEntity p : world.getPlayers()) {
                if (p != getFirstPassenger() && p.getPos().distanceTo(getPos()) < 100 && p instanceof ServerPlayerEntity player) {
                    sync(player);
                }
            }
        } else {
            clientTick();
        }
    }

    private void sync(ServerPlayerEntity player) {
        PayloadPackets.sendSyncAutomobileDataPacket(this, player);
    }

    // witness me fighting against minecraft's collision/physics
    public void movementTick() {
        //this.wasOnGround = automobileOnGround;
        //this.automobileOnGround = (verticalSpeed < 0.2 && verticalSpeed > -0.2) || isOnGround();

        lastSusBounceTimer = suspensionBounceTimer;
        if (suspensionBounceTimer > 0) suspensionBounceTimer--;
        if (!wasOnGround && automobileOnGround) {
            suspensionBounceTimer = 3;
        }

        // Handles boosting
        lastBoostSpeed = boostSpeed;
        if (boostTimer > 0) {
            boostTimer--;
            boostSpeed = Math.min(boostPower, boostSpeed + 0.09f);
            if (engineSpeed < stats.getComfortableSpeed()) {
                engineSpeed += 0.012f;
            }
        } else {
            boostSpeed = AUtils.zero(boostSpeed, 0.09f);
        }

        // Track the last position of the automobile
        var lastPos = getPos();

        // cumulative will be modified by the following code and then the automobile will be moved by it
        // Currently initialized with the value of addedVelocity (which is a general velocity vector applied to the automobile, i.e. for when it bumps into a wall and is pushed back)
        var cumulative = addedVelocity;
        lastWheelAngle = wheelAngle;

        // Reduce gravity underwater
        cumulative = cumulative.add(0, (verticalSpeed * (isSubmergedInWater() ? 0.15f : 1)), 0);

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
                    ) ? (this.hSpeed < stats.getComfortableSpeed() ? 0.001 : 0) // This will supply a small amount of acceleration if the automobile is moving slowly only

                    // Otherwise, it will receive acceleration as normal
                    // It will receive this acceleration if the automobile is moving straight or wide-drifting (the latter slightly reduces acceleration)
                    : calculateAcceleration(speed, stats) * (drifting ? 0.86 : 1) * (engineSpeed > stats.getComfortableSpeed() ? 0.25f : 1);
        }
        // Handle braking/reverse
        if (inBack) {
            this.engineSpeed = Math.max(this.engineSpeed - 0.15f, -0.25f);
        }
        // Handle when the automobile is rolling to a stop
        if (!inFwd && !inBack) {
            this.engineSpeed = AUtils.zero(this.engineSpeed, 0.025f);
        }

        // Slow the automobile a bit while steering and moving fast
        if (!drifting && steering != 0 && hSpeed > 0.8) {
            engineSpeed -= engineSpeed * 0.00042f;
        }

        // Allows for the sticky slope effect to continue for a tick after not being on a slope
        // This prevents the automobile from randomly jumping if it's moving down a slope quickly
        var below = new BlockPos(Math.floor(getX()), Math.floor(getY() - 0.51), Math.floor(getZ()));
        var state = world.getBlockState(below);
        if (state.getBlock() instanceof Sloped slope && slope.isSticky()) {
            slopeStickingTimer = 1;
        } else {
            slopeStickingTimer = Math.max(0, slopeStickingTimer--);
        }

        // Set the horizontal speed
        hSpeed = engineSpeed + boostSpeed;

        // Sticking to sticky slopes
        double lowestPrevYDisp = 0;
        for (double d : prevYDisplacements) {
            lowestPrevYDisp = Math.min(d, lowestPrevYDisp);
        }
        if (slopeStickingTimer > 0 && automobileOnGround && lowestPrevYDisp <= 0) {
            cumulative = cumulative.add(0, -0.5, 0);
        }

        // Apply the horizontal speed to the cumulative movement
        float angle = (float) Math.toRadians(-speedDirection);
        cumulative = cumulative.add(Math.sin(angle) * hSpeed, 0, Math.cos(angle) * hSpeed);

        // Turn the wheels
        float wheelCircumference = (float)(2 * (wheels.model().radius() / 16) * Math.PI);
        wheelAngle += 300 * (hSpeed / wheelCircumference); // made it a bit slower intentionally

        // Move the automobile by the cumulative vector
        this.move(MovementType.SELF, cumulative);

        // Damage and launch entities that are hit by a moving automobile
        if (hSpeed > 0.2) {
            var frontBox = getBoundingBox().offset(cumulative.multiply(0.3));
            var velAdd = cumulative.add(0, 0.1, 0).multiply(3);
            for (var entity : world.getEntitiesByType(TypeFilter.instanceOf(Entity.class), frontBox, entity -> entity != this)) {
                if (entity instanceof LivingEntity living) {
                    living.damage(AutomobilityEntities.AUTOMOBILE_DAMAGE_SOURCE, hSpeed * 10);
                }
                entity.addVelocity(velAdd.x, velAdd.y, velAdd.z);
            }
        }

        // ############################################################################################################
        // ############################################################################################################
        // ###########################################POST#MOVE#CODE###################################################
        // ############################################################################################################
        // ############################################################################################################

        // Reduce the values of addedVelocity incrementally
        addedVelocity = new Vec3d(
                AUtils.zero((float)addedVelocity.x, 0.1f),
                AUtils.zero((float)addedVelocity.y, 0.1f),
                AUtils.zero((float)addedVelocity.z, 0.1f)
        );

        // This code handles bumping into a wall, yes it is utterly horrendous
        var displacement = new Vec3d(getX(), 0, getZ()).subtract(lastPos.x, 0, lastPos.z);
        if (hSpeed > 0.1 && displacement.length() < hSpeed * 0.5 && addedVelocity.length() <= 0) {
            engineSpeed /= 3.6;
            float knockSpeed = ((-0.2f * hSpeed) - 0.5f);
            addedVelocity = addedVelocity.add(Math.sin(angle) * knockSpeed, 0, Math.cos(angle) * knockSpeed);
        }

        double yDisp = getPos().subtract(lastPos).getY();

        // Handle launching off slopes
        double highestPrevYDisp = 0;
        for (double d : prevYDisplacements) {
            highestPrevYDisp = Math.max(d, highestPrevYDisp);
        }
        if (wasOnGround && !automobileOnGround && !isFloorDirectlyBelow) {
            verticalSpeed = (float)MathHelper.clamp(highestPrevYDisp, 0, hSpeed * 0.6f);
        }

        // Handles gravity
        verticalSpeed = Math.max(verticalSpeed - 0.08f, !automobileOnGround ? TERMINAL_VELOCITY : -0.01f);

        //if (verticalSpeed == 0) System.out.println("ZERO V SPEED");

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

        // Turns the automobile based on steering/drifting
        if (hSpeed != 0) {
            float yawInc = (drifting ? (((this.steering + (driftDir)) * driftDir * 2.5f + 1.5f) * driftDir) * (((1 - stats.getGrip()) + 2) / 2.5f) : this.steering * ((4f * Math.min(hSpeed, 1)) + (hSpeed > 0 ? 2 : -3.5f))) * ((stats.getHandling() + 1) / 2);
            this.setYaw(getYaw() + yawInc);
            if (world.isClient) {
                var passenger = getFirstPassenger();
                if (passenger instanceof PlayerEntity player) {
                    if (inLockedViewMode()) {
                        player.setYaw(getYaw() + lockedViewOffset);
                        player.setBodyYaw(getYaw() + lockedViewOffset);
                    } else {
                        player.setYaw(player.getYaw() + yawInc);
                        player.setBodyYaw(player.getYaw() + yawInc);
                    }
                }
            } else {
                for (Entity e : getPassengerList()) {
                    if (e == getFirstPassenger()) continue;
                    e.setYaw(e.getYaw() + yawInc);
                    e.setBodyYaw(e.getYaw() + yawInc);
                }
            }
        }

        // Adjusts the pitch of the automobile when falling onto a block/climbing up a block
        lastVTravelPitch = verticalTravelPitch;
        below = new BlockPos(Math.floor(getX()), Math.floor(getY() - 0.01), Math.floor(getZ()));
        var moreBelow = new BlockPos(Math.floor(getX()), Math.floor(getY() - 1.01), Math.floor(getZ()));
        if (
                hSpeed != 0 &&
                !automobileOnGround &&
                !world.getBlockState(below).isSideSolid(world, below, Direction.UP, SideShapeType.RIGID) &&
                world.getBlockState(moreBelow).isSideSolid(world, moreBelow, Direction.UP, SideShapeType.RIGID)
        ) {
            if (yDisp > 0 && (wasOnGround || automobileOnGround)) {
                verticalTravelPitch = Math.min(verticalTravelPitch + 13, 45) * (hSpeed > 0 ? 1 : -1);
            }
        } else {
            verticalTravelPitch = AUtils.zero(verticalTravelPitch, 22);
        }
    }

    public void clientTick() {
        lastGroundSlopeX = groundSlopeX;
        lastGroundSlopeZ = groundSlopeZ;
        var below = new BlockPos(Math.floor(getX()), Math.floor(getY() - 0.06), Math.floor(getZ()));
        var state = world.getBlockState(below);
        boolean onSlope = false;
        if (state.getBlock() instanceof Sloped slope) {
            targetSlopeX = slope.getGroundSlopeX(world, state, below);
            targetSlopeZ = slope.getGroundSlopeZ(world, state, below);
            onSlope = true;
        } else if (!state.isAir()) {
            targetSlopeX = 0;
            targetSlopeZ = 0;
        }
        if (automobileOnGround || onSlope) {
            groundSlopeX = AUtils.shift(groundSlopeX, 15, targetSlopeX);
            groundSlopeZ = AUtils.shift(groundSlopeZ, 15, targetSlopeZ);
        }
    }

    public void collisionStateTick() {
        // scuffed ground check
        wasOnGround = automobileOnGround;
        automobileOnGround = false;
        isFloorDirectlyBelow = false;
        var b = getBoundingBox();
        var groundBox = new Box(b.minX, b.minY - 0.04, b.minZ, b.maxX, b.minY, b.maxZ);
        var wid = (b.getXLength() + b.getZLength()) * 0.5f;
        var floorBox = new Box(b.minX + (wid * 0.94), b.minY - 0.05, b.minZ + (wid * 0.94), b.maxX - (wid * 0.94), b.minY, b.maxZ - (wid * 0.94));
        var start = new BlockPos(b.minX - 0.1, b.minY - 0.2, b.minZ - 0.1);
        var end = new BlockPos(b.maxX + 0.1, b.maxY + 0.2, b.maxZ + 0.1);
        var groundCuboid = VoxelShapes.cuboid(groundBox);
        var floorCuboid = VoxelShapes.cuboid(floorBox);
        if (this.world.isRegionLoaded(start, end)) {
            var pos = new BlockPos.Mutable();
            for(int x = start.getX(); x <= end.getX(); ++x) {
                for(int y = start.getY(); y <= end.getY(); ++y) {
                    for(int z = start.getZ(); z <= end.getZ(); ++z) {
                        pos.set(x, y, z);
                        var state = this.world.getBlockState(pos);
                        var blockShape = state.getCollisionShape(this.world, pos, ShapeContext.of(this)).offset(pos.getX(), pos.getY(), pos.getZ());
                        automobileOnGround = automobileOnGround || VoxelShapes.matchesAnywhere(blockShape, groundCuboid, BooleanBiFunction.AND);
                        isFloorDirectlyBelow = isFloorDirectlyBelow || VoxelShapes.matchesAnywhere(blockShape, floorCuboid, BooleanBiFunction.AND);
                    }
                }
            }
        }
    }

    private float calculateAcceleration(float speed, AutomobileStats stats) {
        // A somewhat over-engineered function to accelerate the automobile, since I didn't want to add a hard speed cap
        return (1 / ((300 * speed) + (18.5f - (stats.getAcceleration() * 5.3f)))) * (0.9f * ((stats.getAcceleration() + 1) / 2));
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

    public void boost(float power, int time) {
        if (power > boostPower || time > boostTimer) {
            boostTimer = time;
            boostPower = power;
        }
    }

    private void steeringTick() {
        // Adjust the steering based on the left/right inputs
        this.lastSteering = steering;
        if (inLeft == inRight) {
            this.steering = AUtils.zero(this.steering, 0.42f);
        } else if (inLeft) {
            this.steering -= 0.42f;
            this.steering = Math.max(-1, this.steering);
        } else {
            this.steering += 0.42f;
            this.steering = Math.min(1, this.steering);
        }
    }

    private void driftingTick() {
        // Handles starting a drift
        if (steering != 0) {
            if (!drifting && !prevSpace && inSpace && hSpeed > 0.4f && automobileOnGround) {
                drifting = true;
                driftDir = steering > 0 ? 1 : -1;
                // Reduce speed when a drift starts, based on how long the last drift was for
                // This allows you to do a series of short drifts without tanking all your speed, while still reducing your speed when you begin the drift(s)
                engineSpeed -= (0.028 * (Math.min(driftTimer, 20f) / 20)) * engineSpeed;
                driftTimer = 0;
            }
        }
        // Handles ending a drift and the drift timer (for drift turbos)
        if (drifting) {
            // Ending a drift successfully, giving you a turbo boost
            if (prevSpace && !inSpace) {
                drifting = false;
                steering = 0;
                if (driftTimer > DRIFT_TURBO_TIME) {
                    boost(0.3f, 32);
                }
            // Ending a drift unsuccessfully, not giving you a boost
            } else if (hSpeed < 0.33f) {
                drifting = false;
                steering = 0;
            }
            if (automobileOnGround) driftTimer++;
        }
    }

    private static boolean inLockedViewMode() {
        return ControllerUtils.inControllerMode();
    }

    @Environment(EnvType.CLIENT)
    private void updateModels(EntityRendererFactory.Context ctx) {
        if (updateModels) {
            frameModel = frame.model().model().apply(ctx);
            wheelModel = wheels.model().model().apply(ctx);
            engineModel = engine.model().model().apply(ctx);
            updateModels = false;
        }
    }

    @Environment(EnvType.CLIENT)
    public Model getWheelModel(EntityRendererFactory.Context ctx) {
        updateModels(ctx);
        return wheelModel;
    }

    @Environment(EnvType.CLIENT)
    public Model getFrameModel(EntityRendererFactory.Context ctx) {
        updateModels(ctx);
        return frameModel;
    }

    @Environment(EnvType.CLIENT)
    public Model getEngineModel(EntityRendererFactory.Context ctx) {
        updateModels(ctx);
        return engineModel;
    }

    @Override
    public float getAutomobileYaw(float tickDelta) {
        return getYaw(tickDelta);
    }

    @Nullable
    @Override
    public Entity getPrimaryPassenger() {
        return getFirstPassenger();
    }

    @Override
    public ActionResult interact(PlayerEntity player, Hand hand) {
        if (player.getStackInHand(hand).isOf(AutomobilityItems.CROWBAR)) {
            this.remove(RemovalReason.KILLED);
            return ActionResult.success(world.isClient);
        }
        return ActionResult.success(player.startRiding(this));
    }

    @Override
    public double getMountedHeightOffset() {
        return ((wheels.model().radius() + frame.model().seatHeight() - 4) / 16) - (suspensionBounceTimer * 0.048f);
    }

    @Override
    public void updatePassengerPosition(Entity passenger) {
        super.updatePassengerPosition(passenger);
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
