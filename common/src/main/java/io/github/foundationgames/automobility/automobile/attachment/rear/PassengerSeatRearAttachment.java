package io.github.foundationgames.automobility.automobile.attachment.rear;

import io.github.foundationgames.automobility.automobile.attachment.RearAttachmentType;
import io.github.foundationgames.automobility.entity.AutomobileEntity;

public class PassengerSeatRearAttachment extends RearAttachment {
    public PassengerSeatRearAttachment(RearAttachmentType<?> type, AutomobileEntity entity) {
        super(type, entity);
    }

    @Override
    public boolean isRideable() {
        return true;
    }

    @Override
    public double getPassengerHeightOffset() {
        return 0.69;
    }
}
