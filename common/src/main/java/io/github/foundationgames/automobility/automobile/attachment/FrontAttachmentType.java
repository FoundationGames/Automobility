package io.github.foundationgames.automobility.automobile.attachment;

import io.github.foundationgames.automobility.Automobility;
import io.github.foundationgames.automobility.automobile.AutomobileComponent;
import io.github.foundationgames.automobility.automobile.DisplayStat;
import io.github.foundationgames.automobility.automobile.attachment.front.CropHarvesterFrontAttachment;
import io.github.foundationgames.automobility.automobile.attachment.front.EmptyFrontAttachment;
import io.github.foundationgames.automobility.automobile.attachment.front.FrontAttachment;
import io.github.foundationgames.automobility.automobile.attachment.front.GrassCutterFrontAttachment;
import io.github.foundationgames.automobility.automobile.attachment.front.MobControllerFrontAttachment;
import io.github.foundationgames.automobility.entity.AutomobileEntity;
import io.github.foundationgames.automobility.util.SimpleMapContentRegistry;
import net.minecraft.resources.ResourceLocation;

import java.util.function.BiFunction;
import java.util.function.Consumer;

public record FrontAttachmentType<T extends FrontAttachment>(
        ResourceLocation id, BiFunction<FrontAttachmentType<T>, AutomobileEntity, T> constructor, FrontAttachmentModel model
) implements AutomobileComponent<FrontAttachmentType<?>> {
    public static final ResourceLocation ID = Automobility.rl("front_attachment");
    public static final SimpleMapContentRegistry<FrontAttachmentType<?>> REGISTRY = new SimpleMapContentRegistry<>();

    public static final FrontAttachmentType<EmptyFrontAttachment> EMPTY = register(new FrontAttachmentType<>(
            Automobility.rl("empty"), EmptyFrontAttachment::new, new FrontAttachmentModel(new ResourceLocation("empty"), Automobility.rl("empty"), 1)
    ));

    public static final FrontAttachmentType<MobControllerFrontAttachment> MOB_CONTROLLER = register(new FrontAttachmentType<>(
            Automobility.rl("mob_controller"), MobControllerFrontAttachment::new,
            new FrontAttachmentModel(Automobility.rl("textures/entity/automobile/front_attachment/mob_controller.png"), Automobility.rl("frontatt_mob_controller"), 1.7f)
    ));

    public static final FrontAttachmentType<CropHarvesterFrontAttachment> CROP_HARVESTER = register(new FrontAttachmentType<>(
            Automobility.rl("crop_harvester"), CropHarvesterFrontAttachment::new,
            new FrontAttachmentModel(Automobility.rl("textures/entity/automobile/front_attachment/crop_harvester.png"), Automobility.rl("frontatt_harvester"), 0.83f)
    ));

    public static final FrontAttachmentType<GrassCutterFrontAttachment> GRASS_CUTTER = register(new FrontAttachmentType<>(
            Automobility.rl("grass_cutter"), GrassCutterFrontAttachment::new,
            new FrontAttachmentModel(Automobility.rl("textures/entity/automobile/front_attachment/grass_cutter.png"), Automobility.rl("frontatt_harvester"), 0.83f)
    ));

    @Override
    public boolean isEmpty() {
        return this == EMPTY;
    }

    @Override
    public ResourceLocation containerId() {
        return ID;
    }

    @Override
    public void forEachStat(Consumer<DisplayStat<FrontAttachmentType<?>>> action) {
    }

    @Override
    public ResourceLocation getId() {
        return this.id();
    }

    private static <T extends FrontAttachment> FrontAttachmentType<T> register(FrontAttachmentType<T> entry) {
        REGISTRY.register(entry);
        return entry;
    }

    public record FrontAttachmentModel(ResourceLocation texture, ResourceLocation modelId, float scale) {}
}
