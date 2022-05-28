package io.github.foundationgames.automobility.automobile.attachment.rear;

import io.github.foundationgames.automobility.automobile.attachment.RearAttachmentType;
import io.github.foundationgames.automobility.entity.AutomobileEntity;
import io.github.foundationgames.automobility.util.duck.EnderChestInventoryDuck;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.ChestLidAnimator;
import net.minecraft.block.entity.ViewerCountManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.GenericContainerScreenHandler;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.ScreenHandlerContext;
import net.minecraft.screen.SimpleNamedScreenHandlerFactory;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.function.BiFunction;

public class BaseChestRearAttachment extends BlockRearAttachment {
    public static final Text TITLE_CHEST = new TranslatableText("container.chest");
    public static final Text TITLE_ENDER_CHEST = new TranslatableText("container.enderchest");

    private final ViewerCountManager viewerManager;
    public final ChestLidAnimator lidAnimator;

    public BaseChestRearAttachment(RearAttachmentType<?> type, AutomobileEntity entity, BlockState block, @Nullable BiFunction<ScreenHandlerContext, BlockRearAttachment, NamedScreenHandlerFactory> screenProvider) {
        super(type, entity, block, screenProvider);
        this.viewerManager = new ViewerCountManager() {
            protected void onContainerOpen(World world, BlockPos pos, BlockState state) {
                sound(world, pos, BaseChestRearAttachment.this.getOpenSound());
            }

            protected void onContainerClose(World world, BlockPos pos, BlockState state) {
                sound(world, pos, BaseChestRearAttachment.this.getCloseSound());
            }

            protected void onViewerCountUpdate(World world, BlockPos pos, BlockState state, int oldViewerCount, int newViewerCount) {
                if (!world.isClient()) {
                    BaseChestRearAttachment.this.updateTrackedAnimation(newViewerCount);
                }
            }

            protected boolean isPlayerViewing(PlayerEntity player) {
                if (!(player.currentScreenHandler instanceof GenericContainerScreenHandler)) {
                    return false;
                } else {
                    var inventory = ((GenericContainerScreenHandler)player.currentScreenHandler).getInventory();
                    return inventory == BaseChestRearAttachment.this;
                }
            }
        };
        this.lidAnimator = new ChestLidAnimator();
    }

    public void open(PlayerEntity player) {
        if (!player.isSpectator()) {
            this.viewerManager.openContainer(player, this.world(), this.automobile.getBlockPos(), Blocks.AIR.getDefaultState());
        }
    }

    public void close(PlayerEntity player) {
        if (!player.isSpectator()) {
            this.viewerManager.closeContainer(player, this.world(), this.automobile.getBlockPos(), Blocks.AIR.getDefaultState());
        }
    }

    @Override
    public void onTrackedAnimationUpdated(float animation) {
        super.onTrackedAnimationUpdated(animation);

        this.lidAnimator.setOpen(animation > 0);
    }

    @Override
    public void tick() {
        super.tick();

        if (world().isClient()) {
            this.lidAnimator.step();
        }
    }

    protected SoundEvent getOpenSound() {
        return SoundEvents.BLOCK_ENDER_CHEST_OPEN;
    }

    protected SoundEvent getCloseSound() {
        return SoundEvents.BLOCK_ENDER_CHEST_CLOSE;
    }

    private static void sound(World world, BlockPos pos, SoundEvent soundEvent) {
        world.playSound(null, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, soundEvent, SoundCategory.BLOCKS, 0.5F, world.random.nextFloat() * 0.1F + 0.9F);
    }

    public static BaseChestRearAttachment chest(RearAttachmentType<?> type, AutomobileEntity entity) {
        return new ChestRearAttachment(type, entity,
                Blocks.ENDER_CHEST.getDefaultState(),
                (ctx, att) -> att instanceof ChestRearAttachment chest ? chest : null);
    }

    public static BaseChestRearAttachment enderChest(RearAttachmentType<?> type, AutomobileEntity entity) {
        return new BaseChestRearAttachment(type, entity,
                Blocks.ENDER_CHEST.getDefaultState(),
                (ctx, att) -> new SimpleNamedScreenHandlerFactory((syncId, inventory, player) -> {
                    var enderChest = player.getEnderChestInventory();
                    if (att instanceof BaseChestRearAttachment chest) {
                        EnderChestInventoryDuck.of(enderChest).automobility$setActiveAttachment(chest);
                    }
                    return GenericContainerScreenHandler.createGeneric9x3(syncId, inventory, enderChest);
                }, TITLE_ENDER_CHEST)
        );
    }
}
