package io.github.foundationgames.automobility.automobile.attachment.front;

import io.github.foundationgames.automobility.automobile.attachment.FrontAttachmentType;
import io.github.foundationgames.automobility.entity.AutomobileEntity;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;

public class MobControllerFrontAttachment extends FrontAttachment {
    public MobControllerFrontAttachment(FrontAttachmentType<?> type, AutomobileEntity automobile) {
        super(type, automobile);
    }

    @Override
    public boolean canDrive(Entity entity) {
        return super.canDrive(entity) || (entity instanceof Mob);
    }
}
