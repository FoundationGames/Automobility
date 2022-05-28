package io.github.foundationgames.automobility.automobile.render.item;

import io.github.foundationgames.automobility.automobile.AutomobileData;
import io.github.foundationgames.automobility.automobile.AutomobileEngine;
import io.github.foundationgames.automobility.automobile.AutomobileFrame;
import io.github.foundationgames.automobility.automobile.AutomobileWheel;
import io.github.foundationgames.automobility.automobile.attachment.RearAttachmentType;
import io.github.foundationgames.automobility.automobile.attachment.rear.RearAttachment;
import io.github.foundationgames.automobility.automobile.render.RenderableAutomobile;
import io.github.foundationgames.automobility.util.EntityRenderHelper;
import net.minecraft.client.model.Model;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3f;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public class ItemRenderableAutomobile implements RenderableAutomobile {
    private final AutomobileData reader;
    private final Map<Identifier, Model> frameModelCache = new HashMap<>();
    private final Map<Identifier, Model> wheelModelCache = new HashMap<>();
    private final Map<Identifier, Model> engineModelCache = new HashMap<>();
    private Model emptyRearAttModel;

    public ItemRenderableAutomobile(AutomobileData reader) {
        this.reader = reader;
        EntityRenderHelper.registerContextListener(ctx -> {
            frameModelCache.clear();
            wheelModelCache.clear();
            engineModelCache.clear();
            emptyRearAttModel = null;
        });
    }

    @Override
    public AutomobileFrame getFrame() {
        return reader.getFrame();
    }

    @Override
    public AutomobileEngine getEngine() {
        return reader.getEngine();
    }

    @Override
    public AutomobileWheel getWheels() {
        return reader.getWheel();
    }

    @Override
    public @Nullable RearAttachment getRearAttachment() {
        return null;
    }

    @Override
    public Model getFrameModel(EntityRendererFactory.Context ctx) {
        if (!frameModelCache.containsKey(reader.getFrame().getId())) frameModelCache.put(reader.getFrame().getId(), reader.getFrame().model().model().apply(ctx));
        return frameModelCache.get(reader.getFrame().getId());
    }

    @Override
    public Model getWheelModel(EntityRendererFactory.Context ctx) {
        if (!wheelModelCache.containsKey(reader.getWheel().getId())) wheelModelCache.put(reader.getWheel().getId(), reader.getWheel().model().model().apply(ctx));
        return wheelModelCache.get(reader.getWheel().getId());
    }

    @Override
    public Model getEngineModel(EntityRendererFactory.Context ctx) {
        if (!engineModelCache.containsKey(reader.getEngine().getId())) engineModelCache.put(reader.getEngine().getId(), reader.getEngine().model().model().apply(ctx));
        return engineModelCache.get(reader.getEngine().getId());
    }

    @Override
    public @Nullable Model getRearAttachmentModel(EntityRendererFactory.Context ctx) {
        if (emptyRearAttModel == null) emptyRearAttModel = RearAttachmentType.EMPTY.model().model().apply(ctx);
        return emptyRearAttModel;
    }

    @Override
    public float getAutomobileYaw(float tickDelta) {
        return 0;
    }

    @Override
    public float getRearAttachmentYaw(float tickDelta) {
        return 0;
    }

    @Override
    public float getWheelAngle(float tickDelta) {
        return 0;
    }

    @Override
    public float getSteering(float tickDelta) {
        return 0;
    }

    @Override
    public float getSuspensionBounce(float tickDelta) {
        return 0;
    }

    @Override
    public boolean engineRunning() {
        return false;
    }

    @Override
    public int getBoostTimer() {
        return 0;
    }

    @Override
    public int getDriftTimer() {
        return 0;
    }

    @Override
    public long getTime() {
        return 0;
    }

    @Override
    public boolean automobileOnGround() {
        return true;
    }

    @Override
    public boolean debris() {
        return false;
    }

    @Override
    public Vec3f debrisColor() {
        return new Vec3f();
    }
}
