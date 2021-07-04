package io.github.foundationgames.automobility.automobile.render;

import net.minecraft.client.model.Model;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.util.math.Vec3f;

public interface RenderableAutomobile {
    Model getFrameModel(EntityRendererFactory.Context ctx);

    Model getWheelModel(EntityRendererFactory.Context ctx);

    Model getEngineModel(EntityRendererFactory.Context ctx);

    float getAutomobileYaw(float tickDelta);

    float getVerticalTravelPitch(float tickDelta);

    float getWheelAngle(float tickDelta);

    float getSteering(float tickDelta);

    float getSuspensionBounce(float tickDelta);

    boolean engineRunning();

    int getBoostTimer();

    int getDriftTimer();

    long getWorldTime();

    boolean automobileOnGround();

    boolean debris();

    Vec3f debrisColor();
}
