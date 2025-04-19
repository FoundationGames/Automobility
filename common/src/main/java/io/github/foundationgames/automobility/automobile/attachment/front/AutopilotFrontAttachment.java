package io.github.foundationgames.automobility.automobile.attachment.front;

import io.github.foundationgames.automobility.automobile.attachment.FrontAttachmentType;
import io.github.foundationgames.automobility.block.AutopilotSignBlock;
import io.github.foundationgames.automobility.entity.AutomobileEntity;
import io.github.foundationgames.automobility.entity.AutomobilityEntities;
import io.github.foundationgames.automobility.entity.HitboxEntity;
import io.github.foundationgames.automobility.item.AutopilotSignBlockItem;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

public class AutopilotFrontAttachment extends FrontAttachment {
    public static final int MAX_HEADING_COMMAND_TIME = 6000;

    private @Nullable AutopilotSignBlock.Heading currentHeading = null;
    private int headingTimeLimit = 0;
    private int animationTimer = 0;

    private int honkTimer = -6;
    private int impatience = 0;

    public AutopilotFrontAttachment(FrontAttachmentType<?> type, AutomobileEntity automobile) {
        super(type, automobile);
    }

    @Override
    public boolean canDrive(Entity entity) {
        return false;
    }

    @Override
    public boolean isProvidingAlternativeInputs(AutomobileEntity automobile, @Nullable Entity driver) {
        return true;
    }

    @Override
    public void provideAlternativeInputs(AutomobileEntity automobile, AutomobileEntity.Input input, @Nullable Entity driver) {
        honkTimer -= input.accelerating ? 6 : 1;
        if (honkTimer < -10) {
            honkTimer = -10;
        }

        boolean beingInterrupted = false;

        if (currentHeading == null) {
            input.clearInputs();
        } else {
            var dirHeading = automobile.getLookAngle();
            boolean somethingInTheWay = false;

            double width = 0.8 * automobile.getBbWidth();
            var box = new AABB(-width, -2, -width, width, 4, width);
            box = box.move(pos().add(dirHeading.scale(1 + 1.75 * width)));

            for (var e : world().getEntitiesOfClass(HitboxEntity.class, box)) {
                if (!automobile.isOneOfMyHitboxes(e) && (!automobile.isInvulnerable() || e.automobile().isInvulnerable())) {
                    somethingInTheWay = true;
                    break;
                }
            }
            beingInterrupted |= somethingInTheWay;

            if (somethingInTheWay) {
                if (input.accelerating && world().getRandom().nextFloat() < 0.06) {
                    this.honkTimer = 8 + world().getRandom().nextInt(5);
                }
            } else for (var e : world().getEntitiesOfClass(LivingEntity.class, box.inflate(1.25, -0.5, 1.25))) {
                if (e.getTags().contains("shouldHaltAutomobiles")) {
                    somethingInTheWay = true;
                    break;
                }

                if (e.isUsingItem() && e.getItemInHand(e.getUsedItemHand()).getItem() instanceof AutopilotSignBlockItem) {
                    var eLooking = e.getLookAngle();
                    var meToE = e.position().subtract(pos()).normalize();

                    if (eLooking.dot(meToE) < 0) {
                        somethingInTheWay = true;
                        break;
                    }
                } else if (!(e.getVehicle() instanceof AutomobileEntity) && this.honkTimer < -3) {
                    this.honkTimer = 15 + world().getRandom().nextInt(25);
                    beingInterrupted = true;
                }
            }

            var autoPos = autoPos();
            var autoMovement = automobile.getMeasuredMovement();

            var dirAlongPath = currentHeading.dir();
            double offCosine = dirHeading.dot(dirAlongPath);

            var pathToPath = currentHeading.pathToPath(autoPos);
            double distToPath = pathToPath.length();

            var dirToPath = pathToPath.normalize();
            double autoSpeedIntoPath = currentHeading.stop() || somethingInTheWay ? autoMovement.length() : Math.max(0, -autoMovement.dot(dirToPath));

            double favorDirToPath = Math.clamp(distToPath * 0.1, 0, 1);

            boolean burnout;
            if (somethingInTheWay || (
                    currentHeading.stop() && currentHeading.origin().distanceToSqr(autoPos) < 25 + 49 * autoMovement.lengthSqr())) {
                burnout = offCosine > 0 && automobile.getTurboCharge() < AutomobileEntity.SMALL_TURBO_TIME - 5;
                input.accelerating = burnout;
                input.braking = burnout || autoSpeedIntoPath > 0.2;
                favorDirToPath = Math.sqrt(favorDirToPath);
            } else {
                burnout = automobile.burningOut() ? offCosine > -0.8 : offCosine > 0.2;
                input.accelerating = burnout || autoSpeedIntoPath * autoSpeedIntoPath * distToPath * distToPath < 5 + (2 / automobile.getHandling());
                input.braking = burnout;
            }

            var dirHeadingF = dirHeading.toVector3f().mul(1, 0, 1);
            var dirDesiredHeadingF = dirAlongPath.lerp(dirToPath, favorDirToPath).normalize().toVector3f().mul(1, 0, 1);

            double headingToPathDir = dirHeadingF.angleSigned(dirDesiredHeadingF, new Vector3f(0, 1, 0)) / Mth.HALF_PI;
            input.steering = (float) Math.clamp(headingToPathDir, -1, 1);

            if (!burnout) {
                float offset = (float) dirAlongPath.cross(dirHeading).length();
                float damp = (float) Mth.clamp(Math.abs(distToPath * 0.2) + Math.sqrt(offset), 0, 1);
                input.steering *= damp * damp;
            }
        }

        if (beingInterrupted) {
            this.impatience++;
        } else if (this.impatience > 0) {
            if (input.accelerating) {
                this.impatience = Math.max(0, (int)(0.9 * this.impatience) - 4);
            } else this.impatience--;
        }

        float wantsToHonk = 1 + 1f / (-1 - 0.0003f * Math.max(0, this.impatience - 60));

        if (world().getRandom().nextFloat() < wantsToHonk * wantsToHonk) {
            int threshold = (int) (-10 + 8 * (world().getRandom().nextFloat() * wantsToHonk));

            if (this.honkTimer <= threshold) {
                int duration = (int) (5 + wantsToHonk * wantsToHonk * world().getRandom().nextInt(17));
                this.honkTimer = duration;
            }
        }

        input.holdingHorn = this.honkTimer > 0;
    }

    protected Vec3 autoPos() {
        return automobile().position().add(0, 0.5, 0);
    }

    public void notifyHeadingCommand(AutopilotSignBlock.Heading heading) {
        var autoPos = autoPos();
        if (heading.inFrontOfLimitPlane(autoPos) && heading.withinReasonableDistance(autoPos)) {
            if (currentHeading != null) {
                if (currentHeading.origin().distanceToSqr(autoPos) < heading.origin().distanceToSqr(autoPos)) {
                    return;
                }
            }

            currentHeading = heading;
            headingTimeLimit = MAX_HEADING_COMMAND_TIME;
        }
    }

    @Override
    public void tick() {
        super.tick();

        if (!world().isClientSide()) {
            if (currentHeading != null && !currentHeading.withinReasonableDistance(autoPos())) {
                currentHeading = null;
            }

            float anim = 0;
            if (this.currentHeading != null) {
                anim = this.currentHeading.stop() ? 2 : 1;
            }

            this.updateTrackedAnimation(anim);

            if (this.headingTimeLimit > 0) {
                this.headingTimeLimit--;

                if (this.headingTimeLimit <= 0) {
                    this.currentHeading = null;
                }
            }
        }

        this.animationTimer++;

        if (this.animationTimer >= 60) {
            this.animationTimer = 0;
        }
    }

    @Override
    public void writeNbt(CompoundTag nbt, HolderLookup.Provider registry) {
        super.writeNbt(nbt, registry);

        if (this.currentHeading != null) {
            nbt.put("Command", this.currentHeading.toNbt());
        }

        nbt.putInt("timeout", this.headingTimeLimit);
        nbt.putInt("honk_time", this.honkTimer);
        nbt.putInt("impatience", this.impatience);
    }

    @Override
    public void readNbt(CompoundTag nbt, HolderLookup.Provider reg) {
        super.readNbt(nbt, reg);

        if (nbt.contains("Command")) {
            this.currentHeading = AutopilotSignBlock.Heading.fromNbt(nbt.getCompound("Command"));
        } else {
            this.currentHeading = null;
        }

        this.headingTimeLimit = nbt.getInt("timeout");
        this.honkTimer = nbt.getInt("honk_time");
        this.impatience = nbt.getInt("impatience");
    }

    public int getAnimationTimer() {
        return animationTimer;
    }

    public State getState() {
        int anim = Math.clamp((int) animation(), 0, State.values().length);
        return State.values()[anim];
    }

    public enum State {
        IDLE(0xfff8e0, 0xffae00, 15, 0),
        GO(0xe8fffb, 0x00ffd5, 12, 4),
        HALT(0xffbdbd, 0xff0000, 0, 0);

        public final int lightColor;
        public final int glowColor;
        public final int flashPeriod;
        public final int flashSubPeriod;

        State(int lightColor, int glowColor, int flashPeriod, int flashSubPeriod) {
            this.lightColor = lightColor;
            this.glowColor = glowColor;
            this.flashPeriod = flashPeriod;
            this.flashSubPeriod = flashSubPeriod;
        }
    }
}
