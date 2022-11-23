package io.github.foundationgames.automobility.fabric.resource;

import io.github.foundationgames.automobility.Automobility;
import io.github.foundationgames.automobility.util.AUtils;
import net.devtech.arrp.api.RRPCallback;
import net.devtech.arrp.api.RuntimeResourcePack;
import net.devtech.arrp.json.blockstate.JState;
import net.devtech.arrp.json.models.JModel;
import net.minecraft.core.Direction;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;

public enum AutomobilityAssets {;
    public static final RuntimeResourcePack PACK = RuntimeResourcePack.create("automobility:assets");
    private static final Set<Consumer<RuntimeResourcePack>> PROCESSORS = new HashSet<>();

    public static void setup() {
        for (var p : PROCESSORS) {
            p.accept(PACK);
        }

        RRPCallback.AFTER_VANILLA.register(a -> a.add(PACK));
    }

    public static void addSlope(String name, String texture) {
        {
            var path = "block/"+name;
            PACK.addModel(new JModel().parent("automobility:block/template_slope_bottom").textures(JModel.textures().var("slope", texture)), Automobility.rl(path+"_bottom"));
            PACK.addModel(new JModel().parent("automobility:block/template_slope_top").textures(JModel.textures().var("slope", texture)), Automobility.rl(path+"_top"));
            var variants = JState.variant();
            for (Direction dir : AUtils.HORIZONTAL_DIRS) {
                variants.put("half=bottom,facing="+ dir, JState.model(Automobility.rl(path)+"_bottom").y((int)dir.toYRot()));
                variants.put("half=top,facing="+ dir, JState.model(Automobility.rl(path)+"_top").y((int)dir.toYRot()));
            }
            PACK.addBlockState(new JState().add(variants), Automobility.rl(name));
        }
        {
            name = "steep_"+name;
            var path = "block/"+name;
            PACK.addModel(new JModel().parent("automobility:block/template_steep_slope").textures(JModel.textures().var("slope", texture)), Automobility.rl(path));
            var variants = JState.variant();
            for (Direction dir : AUtils.HORIZONTAL_DIRS) {
                variants.put("facing="+ dir, JState.model(Automobility.rl(path)).y((int)dir.toYRot()));
            }
            PACK.addBlockState(new JState().add(variants), Automobility.rl(name));
        }
    }

    public static void addMinecraftSlope(String name, String base) {
        base = switch (base) {
            case "snow_block" -> "snow";
            case "quartz_block" -> "quartz_block_side";
            case "smooth_sandstone" -> "sandstone_top";
            case "smooth_quartz" -> "quartz_block_top";
            case "smooth_red_sandstone" -> "red_sandstone_top";
            case "dried_kelp_block" -> "dried_kelp_side";
            case "ancient_debris" -> "ancient_debris_side";
            case "lodestone" -> "lodestone_top";
            default -> base;
        };
        if (base.startsWith("waxed_") && base.contains("copper")) {
            base = base.replaceFirst("waxed_", "");
        }
        var tex = "minecraft:block/"+base;
        addSlope(name, tex);
    }

    public static void addProcessor(Consumer<RuntimeResourcePack> processor) {
        PROCESSORS.add(processor);
    }
}
