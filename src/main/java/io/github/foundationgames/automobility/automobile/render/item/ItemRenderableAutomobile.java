package io.github.foundationgames.automobility.automobile.render.item;

import com.mojang.math.Vector3f;
import io.github.foundationgames.automobility.automobile.AutomobileData;
import io.github.foundationgames.automobility.automobile.AutomobileEngine;
import io.github.foundationgames.automobility.automobile.AutomobileFrame;
import io.github.foundationgames.automobility.automobile.AutomobileWheel;
import io.github.foundationgames.automobility.automobile.attachment.FrontAttachmentType;
import io.github.foundationgames.automobility.automobile.attachment.RearAttachmentType;
import io.github.foundationgames.automobility.automobile.attachment.front.FrontAttachment;
import io.github.foundationgames.automobility.automobile.attachment.rear.RearAttachment;
import io.github.foundationgames.automobility.automobile.render.RenderableAutomobile;
import io.github.foundationgames.automobility.util.EntityRenderHelper;
import net.minecraft.client.model.Model;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public class ItemRenderableAutomobile implements RenderableAutomobile {
    private final AutomobileData reader;
    private final Map<ResourceLocation, Model> frameModelCache = new HashMap<>();
    private final Map<ResourceLocation, Model> wheelModelCache = new HashMap<>();
    private final Map<ResourceLocation, Model> engineModelCache = new HashMap<>();
    private Model emptyRearAttModel;
    private Model emptyFrontAttModel;

    public ItemRenderableAutomobile(AutomobileData reader) {
        this.reader = reader;
        EntityRenderHelper.registerContextListener(ctx -> {
            frameModelCache.clear();
            wheelModelCache.clear();
            engineModelCache.clear();
            emptyRearAttModel = null;
            emptyFrontAttModel = null;
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
    public @Nullable FrontAttachment getFrontAttachment() {
        return null;
    }

    @Override
    public Model getFrameModel(EntityRendererProvider.Context ctx) {
        if (!frameModelCache.containsKey(reader.getFrame().getId())) frameModelCache.put(reader.getFrame().getId(), reader.getFrame().model().model().apply(ctx));
        return frameModelCache.get(reader.getFrame().getId());
    }

    @Override
    public Model getWheelModel(EntityRendererProvider.Context ctx) {
        if (!wheelModelCache.containsKey(reader.getWheel().getId())) wheelModelCache.put(reader.getWheel().getId(), reader.getWheel().model().model().apply(ctx));
        return wheelModelCache.get(reader.getWheel().getId());
    }

    @Override
    public Model getEngineModel(EntityRendererProvider.Context ctx) {
        if (!engineModelCache.containsKey(reader.getEngine().getId())) engineModelCache.put(reader.getEngine().getId(), reader.getEngine().model().model().apply(ctx));
        return engineModelCache.get(reader.getEngine().getId());
    }

    @Override
    public Model getRearAttachmentModel(EntityRendererProvider.Context ctx) {
        if (emptyRearAttModel == null) emptyRearAttModel = RearAttachmentType.EMPTY.model().model().apply(ctx);
        return emptyRearAttModel;
    }

    @Override
    public Model getFrontAttachmentModel(EntityRendererProvider.Context ctx) {
        if (emptyFrontAttModel == null) emptyFrontAttModel = FrontAttachmentType.EMPTY.model().model().apply(ctx);
        return emptyFrontAttModel;
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
    public int getTurboCharge() {
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
    public Vector3f debrisColor() {
        return new Vector3f();
    }
}
