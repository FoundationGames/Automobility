package io.github.foundationgames.automobility.resource;

import io.github.foundationgames.automobility.Automobility;
import io.github.foundationgames.automobility.util.AUtils;
import net.devtech.arrp.api.RRPCallback;
import net.devtech.arrp.api.RuntimeResourcePack;
import net.devtech.arrp.json.blockstate.JState;
import net.devtech.arrp.json.models.JModel;
import net.minecraft.util.math.Direction;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;

public enum AutomobilityAssets {;
    public static final RuntimeResourcePack PACK = RuntimeResourcePack.create("automobility_assets");
    private static final Set<Consumer<RuntimeResourcePack>> PROCESSORS = new HashSet<>();

    public static void setup() {
        var dashPanel = JState.variant();
        for (Direction dir : AUtils.HORIZONTAL_DIRS) {
            dashPanel.put("left=false,right=false,facing="+ dir, JState.model(Automobility.id("block/dash_panel_single")).y((int)dir.asRotation() + 180));
            dashPanel.put("left=true,right=false,facing="+ dir, JState.model(Automobility.id("block/dash_panel_left")).y((int)dir.asRotation() + 180));
            dashPanel.put("left=false,right=true,facing="+ dir, JState.model(Automobility.id("block/dash_panel_right")).y((int)dir.asRotation() + 180));
            dashPanel.put("left=true,right=true,facing="+ dir, JState.model(Automobility.id("block/dash_panel_center")).y((int)dir.asRotation() + 180));
        }
        PACK.addBlockState(new JState().add(dashPanel), Automobility.id("dash_panel"));

        var slopedDashPanel = JState.variant();
        for (Direction dir : AUtils.HORIZONTAL_DIRS) {
            slopedDashPanel.put("half=bottom,left=false,right=false,facing="+ dir, JState.model(Automobility.id("block/sloped_dash_panel_single_bottom")).y((int)dir.asRotation()));
            slopedDashPanel.put("half=bottom,left=true,right=false,facing="+ dir, JState.model(Automobility.id("block/sloped_dash_panel_left_bottom")).y((int)dir.asRotation()));
            slopedDashPanel.put("half=bottom,left=false,right=true,facing="+ dir, JState.model(Automobility.id("block/sloped_dash_panel_right_bottom")).y((int)dir.asRotation()));
            slopedDashPanel.put("half=bottom,left=true,right=true,facing="+ dir, JState.model(Automobility.id("block/sloped_dash_panel_center_bottom")).y((int)dir.asRotation()));
            slopedDashPanel.put("half=top,left=false,right=false,facing="+ dir, JState.model(Automobility.id("block/sloped_dash_panel_single_top")).y((int)dir.asRotation()));
            slopedDashPanel.put("half=top,left=true,right=false,facing="+ dir, JState.model(Automobility.id("block/sloped_dash_panel_left_top")).y((int)dir.asRotation()));
            slopedDashPanel.put("half=top,left=false,right=true,facing="+ dir, JState.model(Automobility.id("block/sloped_dash_panel_right_top")).y((int)dir.asRotation()));
            slopedDashPanel.put("half=top,left=true,right=true,facing="+ dir, JState.model(Automobility.id("block/sloped_dash_panel_center_top")).y((int)dir.asRotation()));
        }
        PACK.addBlockState(new JState().add(slopedDashPanel), Automobility.id("sloped_dash_panel"));

        var steepDashPanel = JState.variant();
        for (Direction dir : AUtils.HORIZONTAL_DIRS) {
            steepDashPanel.put("left=false,right=false,facing="+ dir, JState.model(Automobility.id("block/steep_sloped_dash_panel_single")).y((int)dir.asRotation()));
            steepDashPanel.put("left=true,right=false,facing="+ dir, JState.model(Automobility.id("block/steep_sloped_dash_panel_left")).y((int)dir.asRotation()));
            steepDashPanel.put("left=false,right=true,facing="+ dir, JState.model(Automobility.id("block/steep_sloped_dash_panel_right")).y((int)dir.asRotation()));
            steepDashPanel.put("left=true,right=true,facing="+ dir, JState.model(Automobility.id("block/steep_sloped_dash_panel_center")).y((int)dir.asRotation()));
        }
        PACK.addBlockState(new JState().add(steepDashPanel), Automobility.id("steep_sloped_dash_panel"));

        for (var p : PROCESSORS) {
            p.accept(PACK);
        }

        RRPCallback.AFTER_VANILLA.register(a -> a.add(PACK));
    }

    public static void addSlope(String name, String texture) {
        {
            var path = "block/"+name;
            PACK.addModel(new JModel().parent("automobility:block/template_slope_bottom").textures(JModel.textures().var("slope", texture)), Automobility.id(path+"_bottom"));
            PACK.addModel(new JModel().parent("automobility:block/template_slope_top").textures(JModel.textures().var("slope", texture)), Automobility.id(path+"_top"));
            var variants = JState.variant();
            for (Direction dir : AUtils.HORIZONTAL_DIRS) {
                variants.put("half=bottom,facing="+ dir, JState.model(Automobility.id(path)+"_bottom").y((int)dir.asRotation()));
                variants.put("half=top,facing="+ dir, JState.model(Automobility.id(path)+"_top").y((int)dir.asRotation()));
            }
            PACK.addBlockState(new JState().add(variants), Automobility.id(name));
            PACK.addModel(new JModel().parent("automobility:"+path+"_bottom"), Automobility.id("item/"+name));
        }
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

    public static void addProcessor(Consumer<RuntimeResourcePack> processor) {
        PROCESSORS.add(processor);
    }
}
