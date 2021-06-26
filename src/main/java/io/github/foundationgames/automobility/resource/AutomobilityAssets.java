package io.github.foundationgames.automobility.resource;

import io.github.foundationgames.automobility.Automobility;
import net.devtech.arrp.api.RRPCallback;
import net.devtech.arrp.api.RuntimeResourcePack;
import net.devtech.arrp.json.blockstate.JState;
import net.minecraft.util.math.Direction;

public enum AutomobilityAssets {;
    public static final RuntimeResourcePack PACK = RuntimeResourcePack.create("automobility_assets");

    private static final Direction[] horizontal = {Direction.NORTH, Direction.SOUTH, Direction.EAST, Direction.WEST};

    public static void setup() {
        var dashPanel = JState.variant();
        for (Direction dir : horizontal) {
            dashPanel.put("left=false,right=false,facing="+ dir, JState.model(Automobility.id("block/dash_panel_single")).y((int)dir.asRotation() + 180));
            dashPanel.put("left=true,right=false,facing="+ dir, JState.model(Automobility.id("block/dash_panel_left")).y((int)dir.asRotation() + 180));
            dashPanel.put("left=false,right=true,facing="+ dir, JState.model(Automobility.id("block/dash_panel_right")).y((int)dir.asRotation() + 180));
            dashPanel.put("left=true,right=true,facing="+ dir, JState.model(Automobility.id("block/dash_panel_center")).y((int)dir.asRotation() + 180));
        }
        PACK.addBlockState(new JState().add(dashPanel), Automobility.id("dash_panel"));

        RRPCallback.AFTER_VANILLA.register(a -> a.add(PACK));
    }
}
