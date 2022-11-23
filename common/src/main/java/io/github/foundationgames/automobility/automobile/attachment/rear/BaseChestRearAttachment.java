package io.github.foundationgames.automobility.automobile.attachment.rear;

import io.github.foundationgames.automobility.automobile.attachment.RearAttachmentType;
import io.github.foundationgames.automobility.entity.AutomobileEntity;
import io.github.foundationgames.automobility.util.duck.EnderChestContainerDuck;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.ChestLidController;
import net.minecraft.world.level.block.entity.ContainerOpenersCounter;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.util.function.BiFunction;

public class BaseChestRearAttachment extends BlockRearAttachment {
    public static final Component TITLE_CHEST = Component.translatable("container.chest");
    public static final Component TITLE_ENDER_CHEST = Component.translatable("container.enderchest");
    public static final Component TITLE_BARREL = Component.translatable("container.barrel");

    private final ContainerOpenersCounter viewerManager;
    public final ChestLidController lidAnimator;

    public BaseChestRearAttachment(RearAttachmentType<?> type, AutomobileEntity entity, BlockState block, @Nullable BiFunction<ContainerLevelAccess, BlockRearAttachment, MenuProvider> screenProvider) {
        super(type, entity, block, screenProvider);
        this.viewerManager = new ContainerOpenersCounter() {
            protected void onOpen(Level world, BlockPos pos, BlockState state) {
                sound(world, pos, BaseChestRearAttachment.this.getOpenSound());
            }

            protected void onClose(Level world, BlockPos pos, BlockState state) {
                sound(world, pos, BaseChestRearAttachment.this.getCloseSound());
            }

            protected void openerCountChanged(Level world, BlockPos pos, BlockState state, int oldViewerCount, int newViewerCount) {
                if (!world.isClientSide()) {
                    BaseChestRearAttachment.this.updateTrackedAnimation(newViewerCount);
                }
            }

            protected boolean isOwnContainer(Player player) {
                if (!(player.containerMenu instanceof ChestMenu)) {
                    return false;
                } else {
                    var inventory = ((ChestMenu)player.containerMenu).getContainer();
                    return inventory == BaseChestRearAttachment.this;
                }
            }
        };
        this.lidAnimator = new ChestLidController();
    }

    public void open(Player player) {
        if (!player.isSpectator()) {
            this.viewerManager.incrementOpeners(player, this.world(), this.automobile.blockPosition(), Blocks.AIR.defaultBlockState());
        }
    }

    public void close(Player player) {
        if (!player.isSpectator()) {
            this.viewerManager.decrementOpeners(player, this.world(), this.automobile.blockPosition(), Blocks.AIR.defaultBlockState());
        }
    }

    @Override
    public void onTrackedAnimationUpdated(float animation) {
        super.onTrackedAnimationUpdated(animation);

        this.lidAnimator.shouldBeOpen(animation > 0);
    }

    @Override
    public void tick() {
        super.tick();

        if (world().isClientSide()) {
            this.lidAnimator.tickLid();
        }
    }

    protected SoundEvent getOpenSound() {
        return SoundEvents.ENDER_CHEST_OPEN;
    }

    protected SoundEvent getCloseSound() {
        return SoundEvents.ENDER_CHEST_CLOSE;
    }

    private static void sound(Level world, BlockPos pos, SoundEvent soundEvent) {
        world.playSound(null, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, soundEvent, SoundSource.BLOCKS, 0.5F, world.random.nextFloat() * 0.1F + 0.9F);
    }

    public static BaseChestRearAttachment chest(RearAttachmentType<?> type, AutomobileEntity entity) {
        return new ChestRearAttachment(type, entity,
                Blocks.ENDER_CHEST.defaultBlockState(),
                (ctx, att) -> att instanceof ChestRearAttachment chest ? chest : null);
    }

    public static BaseChestRearAttachment enderChest(RearAttachmentType<?> type, AutomobileEntity entity) {
        return new BaseChestRearAttachment(type, entity,
                Blocks.ENDER_CHEST.defaultBlockState(),
                (ctx, att) -> new SimpleMenuProvider((syncId, inventory, player) -> {
                    var enderChest = player.getEnderChestInventory();
                    if (att instanceof BaseChestRearAttachment chest) {
                        EnderChestContainerDuck.of(enderChest).automobility$setActiveAttachment(chest);
                    }
                    return ChestMenu.threeRows(syncId, inventory, enderChest);
                }, TITLE_ENDER_CHEST)
        );
    }

    public static BaseChestRearAttachment saddledBarrel(RearAttachmentType<?> type, AutomobileEntity entity) {
        return new SaddledBarrelRearAttachment(type, entity,
                Blocks.BARREL.defaultBlockState(),
                (ctx, att) -> att instanceof SaddledBarrelRearAttachment barrel ? barrel : null);
    }
}
