package io.github.foundationgames.automobility.automobile.render;

import io.github.foundationgames.automobility.automobile.AutomobileEngine;
import io.github.foundationgames.automobility.automobile.AutomobileFrame;
import io.github.foundationgames.automobility.automobile.AutomobileWheel;
import io.github.foundationgames.automobility.automobile.attachment.RearAttachmentType;
import io.github.foundationgames.automobility.automobile.attachment.rear.RearAttachment;
import net.minecraft.client.model.Model;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.util.math.Vec3f;
import org.jetbrains.annotations.Nullable;

public interface RenderableAutomobile {
    AutomobileFrame getFrame();

    AutomobileEngine getEngine();

    AutomobileWheel getWheels();

    RearAttachmentType<?> getRearAttachmentType();

    Model getFrameModel(EntityRendererFactory.Context ctx);

    Model getWheelModel(EntityRendererFactory.Context ctx);

    Model getEngineModel(EntityRendererFactory.Context ctx);

    Model getRearAttachmentModel(EntityRendererFactory.Context ctx);

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

    int getDriftTimer();

    long getWorldTime();

    boolean automobileOnGround();

    boolean debris();

    Vec3f debrisColor();
}
