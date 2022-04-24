package io.github.foundationgames.automobility.resource;

import io.github.foundationgames.automobility.Automobility;
import net.devtech.arrp.api.RRPCallback;
import net.devtech.arrp.api.RuntimeResourcePack;
import net.devtech.arrp.json.tags.JTag;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

public enum AutomobilityData {;
    public static final RuntimeResourcePack PACK = RuntimeResourcePack.create("automobility:data");
    private static final Set<Consumer<RuntimeResourcePack>> PROCESSORS = new HashSet<>();

    public static final List<Identifier> STEEP_SLOPE_TAG_CANDIDATES = new ArrayList<>();
    public static final List<Identifier> NON_STEEP_SLOPE_TAG_CANDIDATES = new ArrayList<>();
    public static final List<Identifier> STICKY_SLOPE_TAG_CANDIDATES = new ArrayList<>();

    public static void setup() {
        var steepSlopesTag = new JTag();
        for (var id : STEEP_SLOPE_TAG_CANDIDATES) steepSlopesTag.add(id);
        PACK.addTag(Automobility.id("blocks/steep_slopes"), steepSlopesTag);

        var nonSteepSlopesTag = new JTag();
        for (var id : NON_STEEP_SLOPE_TAG_CANDIDATES) nonSteepSlopesTag.add(id);
        PACK.addTag(Automobility.id("blocks/non_steep_slopes"), nonSteepSlopesTag);

        var slopesTag = new JTag();
        slopesTag.tag(Automobility.id("blocks/steep_slopes"));
        slopesTag.tag(Automobility.id("blocks/non_steep_slopes"));
        PACK.addTag(Automobility.id("blocks/slopes"), slopesTag);

        var stickySlopesTag = new JTag();
        stickySlopesTag.tag(Automobility.id("blocks/slopes"));
        for (var id : STICKY_SLOPE_TAG_CANDIDATES) stickySlopesTag.add(id);
        PACK.addTag(Automobility.id("blocks/sticky_slopes"), stickySlopesTag);

        for (var p : PROCESSORS) {
            p.accept(PACK);
        }

        RRPCallback.AFTER_VANILLA.register(a -> a.add(PACK));
    }

    public static void addProcessor(Consumer<RuntimeResourcePack> processor) {
        PROCESSORS.add(processor);
    }
}
