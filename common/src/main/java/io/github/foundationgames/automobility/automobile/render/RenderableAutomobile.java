package io.github.foundationgames.automobility.automobile.render;

import io.github.foundationgames.automobility.automobile.AutomobileEngine;
import io.github.foundationgames.automobility.automobile.AutomobileFrame;
import io.github.foundationgames.automobility.automobile.AutomobileWheel;
import io.github.foundationgames.automobility.automobile.attachment.FrontAttachmentType;
import io.github.foundationgames.automobility.automobile.attachment.RearAttachmentType;
import io.github.foundationgames.automobility.automobile.attachment.front.FrontAttachment;
import io.github.foundationgames.automobility.automobile.attachment.rear.RearAttachment;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

public interface RenderableAutomobile {
    AutomobileFrame getFrame();

    AutomobileEngine getEngine();

    AutomobileWheel getWheels();

    @Nullable RearAttachment getRearAttachment();

    @Nullable FrontAttachment getFrontAttachment();

    default RearAttachmentType<?> getRearAttachmentType() {
        if (this.getRearAttachment() == null) {
            return RearAttachmentType.EMPTY;
        }
        return this.getRearAttachment().type;
    }

    default FrontAttachmentType<?> getFrontAttachmentType() {
        if (this.getFrontAttachment() == null) {
            return FrontAttachmentType.EMPTY;
        }
        return this.getFrontAttachment().type;
    }

    float getAutomobileYaw(float tickDelta);

    float getRearAttachmentYaw(float tickDelta);

    float getWheelAngle(float tickDelta);

    float getSteering(float tickDelta);

    float getSuspensionBounce(float tickDelta);

    boolean engineRunning();

    default int getWheelCount() {
        return this.getFrame().model().wheelBase().wheelCount;
    }

    int getBoostTimer();

    int getTurboCharge();

    long getTime();

    boolean automobileOnGround();

    boolean debris();

    Vector3f debrisColor();
}
