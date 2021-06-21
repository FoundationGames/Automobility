package io.github.foundationgames.automobility.entity;

import io.github.foundationgames.automobility.automobile.AutomobileFrame;
import io.github.foundationgames.automobility.automobile.AutomobileWheel;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.vehicle.BoatEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.Packet;
import net.minecraft.network.packet.s2c.play.EntitySpawnS2CPacket;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

public class AutomobileEntity extends Entity {
    private AutomobileFrame frame = AutomobileFrame.REGISTRY.getOrDefault(null);
    private AutomobileWheel wheels = AutomobileWheel.REGISTRY.getOrDefault(null);

    private float engineSpeed;
    private float speedV;

    @Environment(EnvType.CLIENT)
    public boolean updateModels = true;

    public AutomobileEntity(EntityType<?> type, World world) {
        super(type, world);
    }

    public AutomobileFrame getFrame() {
        return frame;
    }

    public AutomobileWheel getWheels() {
        return wheels;
    }

    @Override
    public ActionResult interact(PlayerEntity player, Hand hand) {
        return ActionResult.success(player.startRiding(this));
    }

    @Override
    public boolean collidesWith(Entity other) {
        return BoatEntity.canCollide(this, other);
    }

    @Override
    public boolean isCollidable() {
        return true;
    }



    @Override
    public boolean isPushable() {
        return true;
    }

    @Override
    protected void readCustomDataFromNbt(NbtCompound nbt) {
        frame = AutomobileFrame.REGISTRY.getOrDefault(Identifier.tryParse(nbt.getString("frame")));
        wheels = AutomobileWheel.REGISTRY.getOrDefault(Identifier.tryParse(nbt.getString("wheels")));
    }

    @Override
    protected void writeCustomDataToNbt(NbtCompound nbt) {
        nbt.putString("frame", frame.getId().toString());
        nbt.putString("wheels", wheels.getId().toString());
    }

    @Override
    protected void initDataTracker() {

    }

    @Override
    public Packet<?> createSpawnPacket() {
        return new EntitySpawnS2CPacket(this);
    }
}
