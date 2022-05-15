package io.github.foundationgames.automobility.automobile.attachment;

import io.github.foundationgames.automobility.Automobility;
import io.github.foundationgames.automobility.automobile.AutomobileFrame;
import io.github.foundationgames.automobility.automobile.attachment.rear.EmptyRearAttachment;
import io.github.foundationgames.automobility.automobile.attachment.rear.PassengerSeatRearAttachment;
import io.github.foundationgames.automobility.automobile.attachment.rear.RearAttachment;
import io.github.foundationgames.automobility.entity.AutomobileEntity;
import io.github.foundationgames.automobility.render.AutomobilityModels;
import io.github.foundationgames.automobility.util.SimpleMapContentRegistry;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.Model;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.util.Identifier;

import java.util.function.BiFunction;
import java.util.function.Function;

public record RearAttachmentType<T extends RearAttachment>(
        Identifier id, BiFunction<RearAttachmentType<T>, AutomobileEntity, T> constructor, RearAttachmentModel model
) implements SimpleMapContentRegistry.Identifiable {
    public static final SimpleMapContentRegistry<RearAttachmentType<?>> REGISTRY = new SimpleMapContentRegistry<>();

    public static final RearAttachmentType<EmptyRearAttachment> EMPTY = register(new RearAttachmentType<>(
            Automobility.id("empty"), EmptyRearAttachment::new, new RearAttachmentModel(new Identifier("empty"), Automobility.id("empty"), 0)
    ));

    public static final RearAttachmentType<PassengerSeatRearAttachment> PASSENGER_SEAT = register(new RearAttachmentType<>(
            Automobility.id("passenger_seat"), PassengerSeatRearAttachment::new,
            new RearAttachmentModel(Automobility.id("textures/entity/automobile/rear_attachment/passenger_seat.png"), Automobility.id("rearatt_passenger_seat"), 11)
    ));

    public boolean isEmpty() {
        return this == EMPTY;
    }

    @Override
    public Identifier getId() {
        return this.id;
    }

    private static <T extends RearAttachment> RearAttachmentType<T> register(RearAttachmentType<T> entry) {
        REGISTRY.register(entry);
        return entry;
    }

    public record RearAttachmentModel(Identifier texture, Identifier modelId, float pivotDistPx) {
        @Environment(EnvType.CLIENT)
        public Function<EntityRendererFactory.Context, Model> model() {
            return AutomobilityModels.MODELS.get(modelId);
        }
    }
}
