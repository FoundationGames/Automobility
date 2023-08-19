package io.github.foundationgames.automobility.item;

import io.github.foundationgames.automobility.block.AutomobilityBlocks;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.Nullable;

public class DashPanelItem extends TooltipBlockItem {
    private static final Component TOOLTIP = Component.translatable("tooltip.block.automobility.dash_panel")
            .withStyle(ChatFormatting.BLUE);

    public DashPanelItem(Block block, Properties settings) {
        super(block, TOOLTIP, settings);
    }

    @Override
    public InteractionResult useOn(UseOnContext ctx) {
        var lvl = ctx.getLevel();
        var pos = ctx.getClickedPos();
        var state = lvl.getBlockState(pos);

        if (state.is(AutomobilityBlocks.SLOPE.require())) {
            if (!lvl.isClientSide()) {
                ctx.getLevel().setBlockAndUpdate(pos, AutomobilityBlocks.SLOPE_WITH_DASH_PANEL.require()
                        .withPropertiesOf(state));
            }
            afterPlace(ctx.getItemInHand(), lvl, ctx.getPlayer(), pos);

            return InteractionResult.SUCCESS;
        }
        if (state.is(AutomobilityBlocks.STEEP_SLOPE.require())) {
            if (!lvl.isClientSide()) {
                ctx.getLevel().setBlockAndUpdate(pos, AutomobilityBlocks.STEEP_SLOPE_WITH_DASH_PANEL.require()
                        .withPropertiesOf(state));
            }
            afterPlace(ctx.getItemInHand(), lvl, ctx.getPlayer(), pos);

            return InteractionResult.SUCCESS;
        }

        return super.useOn(ctx);
    }

    private void afterPlace(ItemStack stack, Level level, @Nullable Player player, BlockPos pos) {
        if (!level.isClientSide()) {
            level.playSound(null, pos, SoundEvents.METAL_PLACE, SoundSource.BLOCKS, 1, 1.25f);

            if (player == null || !player.isCreative()) {
                stack.shrink(1);
            }
        }
    }
}
