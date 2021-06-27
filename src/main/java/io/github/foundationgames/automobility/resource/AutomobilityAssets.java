package io.github.foundationgames.automobility.resource;

import io.github.foundationgames.automobility.Automobility;
import io.github.foundationgames.automobility.util.AUtils;
import net.devtech.arrp.api.RRPCallback;
import net.devtech.arrp.api.RuntimeResourcePack;
import net.devtech.arrp.json.blockstate.JState;
import net.devtech.arrp.json.models.JModel;
import net.minecraft.util.math.Direction;

public enum AutomobilityAssets {;
    public static final RuntimeResourcePack PACK = RuntimeResourcePack.create("automobility_assets");

    public static void setup() {
        var dashPanel = JState.variant();
        for (Direction dir : AUtils.HORIZONTAL_DIRS) {
            dashPanel.put("left=false,right=false,facing="+ dir, JState.model(Automobility.id("block/dash_panel_single")).y((int)dir.asRotation() + 180));
            dashPanel.put("left=true,right=false,facing="+ dir, JState.model(Automobility.id("block/dash_panel_left")).y((int)dir.asRotation() + 180));
            dashPanel.put("left=false,right=true,facing="+ dir, JState.model(Automobility.id("block/dash_panel_right")).y((int)dir.asRotation() + 180));
            dashPanel.put("left=true,right=true,facing="+ dir, JState.model(Automobility.id("block/dash_panel_center")).y((int)dir.asRotation() + 180));
        }
        PACK.addBlockState(new JState().add(dashPanel), Automobility.id("dash_panel"));

        addSlope("stone_slope", "minecraft:block/stone");
        addSlope("cobblestone_slope", "minecraft:block/cobblestone");

        RRPCallback.AFTER_VANILLA.register(a -> a.add(PACK));
    }

    public static void addSlope(String name, String texture) {
        {
            name = "steep_"+name;
            var path = "block/"+name;
            PACK.addModel(new JModel().parent("automobility:block/template_steep_slope").textures(JModel.textures().var("slope", texture)), Automobility.id(path));
            var variants = JState.variant();
            for (Direction dir : AUtils.HORIZONTAL_DIRS) {
                variants.put("facing="+ dir, JState.model(Automobility.id(path)).y((int)dir.asRotation()));
            }
            PACK.addBlockState(new JState().add(variants), Automobility.id(name));
            PACK.addModel(new JModel().parent("automobility:"+path), Automobility.id("item/"+name));
        }
    }
}
