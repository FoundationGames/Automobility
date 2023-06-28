package io.github.foundationgames.automobility.forge.vendored.jsonem.serialization;

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.foundationgames.automobility.forge.mixin.jsonem.CubeDefinitionAccess;
import io.github.foundationgames.automobility.forge.mixin.jsonem.CubeDeformationAccess;
import io.github.foundationgames.automobility.forge.mixin.jsonem.LayerDefinitionAccess;
import io.github.foundationgames.automobility.forge.mixin.jsonem.MaterialDefinitionAccess;
import io.github.foundationgames.automobility.forge.mixin.jsonem.PartDefinitionAccess;
import io.github.foundationgames.automobility.forge.vendored.jsonem.util.UVPairComparable;
import org.joml.Vector2f;
import org.joml.Vector3f;

import net.minecraft.Util;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDefinition;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MaterialDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.model.geom.builders.UVPair;
import net.minecraft.core.Direction;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Optional;
import java.util.Set;

public class JsonEMCodecs {
    private static final Set<Direction> ALL_DIRECTIONS = EnumSet.allOf(Direction.class);

    public static final Codec<Vector3f> VECTOR3F = Codec.FLOAT.listOf().comapFlatMap((vec) ->
                    Util.fixedSize(vec, 3).map(coords -> new Vector3f(coords.get(0), coords.get(1), coords.get(2))),
            (vec) -> ImmutableList.of(vec.x, vec.y, vec.z)
    );

    public static final Codec<MaterialDefinition> MATERIAL_DEFINITION = RecordCodecBuilder.create((instance) ->
        instance.group(
                Codec.INT.fieldOf("width").forGetter(obj -> ((MaterialDefinitionAccess) obj).automobility$width()),
                Codec.INT.fieldOf("height").forGetter(obj -> ((MaterialDefinitionAccess) obj).automobility$height())
        ).apply(instance, MaterialDefinition::new)
    );

    public static final Codec<PartPose> PART_POSE = RecordCodecBuilder.create((instance) ->
            instance.group(
                    VECTOR3F.optionalFieldOf("origin", new Vector3f()).forGetter(obj -> new Vector3f(obj.x, obj.y, obj.z)),
                    VECTOR3F.optionalFieldOf("rotation", new Vector3f()).forGetter(obj -> new Vector3f(obj.xRot, obj.yRot, obj.zRot))
            ).apply(instance, (origin, rot) -> PartPose.offsetAndRotation(origin.x(), origin.y(), origin.z(), rot.x(), rot.y(), rot.z()))
    );

    public static final Codec<CubeDeformation> CUBE_DEFORMATION = VECTOR3F.xmap(
            vec -> new CubeDeformation(vec.x(), vec.y(), vec.z()),
            dil -> new Vector3f(
                    ((CubeDeformationAccess) dil).automobility$radiusX(),
                    ((CubeDeformationAccess) dil).automobility$radiusY(),
                    ((CubeDeformationAccess) dil).automobility$radiusZ())
    );

    public static final Codec<UVPair> UV_PAIR = Codec.FLOAT.listOf().comapFlatMap((vec) ->
            Util.fixedSize(vec, 2).map((arr) -> new UVPairComparable(arr.get(0), arr.get(1))),
            (vec) -> ImmutableList.of(vec.u(), vec.v())
    );

    private static CubeDefinition createCuboidData(Optional<String> name, Vector3f offset, Vector3f dimensions, CubeDeformation dilation, boolean mirror, UVPair uv, UVPair uvSize) {
        return CubeDefinitionAccess.automobility$create(name.orElse(null), uv.u(), uv.v(), offset.x(), offset.y(), offset.z(), dimensions.x(), dimensions.y(), dimensions.z(), dilation, mirror, uvSize.u(), uvSize.v(), ALL_DIRECTIONS);
    }

    private static final UVPair DEFAULT_UV_SCALE = new UVPairComparable(1.0f, 1.0f);

    public static final Codec<CubeDefinition> CUBE_DEFINITION = RecordCodecBuilder.create((instance) ->
            instance.group(
                    Codec.STRING.optionalFieldOf("name").forGetter(obj -> Optional.ofNullable(((CubeDefinitionAccess)(Object)obj).automobility$name())),
                    VECTOR3F.fieldOf("offset").forGetter(obj -> ((CubeDefinitionAccess)(Object)obj).automobility$offset()),
                    VECTOR3F.fieldOf("dimensions").forGetter(obj -> ((CubeDefinitionAccess)(Object)obj).automobility$dimensions()),
                    CUBE_DEFORMATION.optionalFieldOf("dilation", CubeDeformation.NONE).forGetter(obj -> ((CubeDefinitionAccess)(Object)obj).automobility$dilation()),
                    Codec.BOOL.optionalFieldOf("mirror", false).forGetter(obj -> ((CubeDefinitionAccess)(Object)obj).automobility$mirror()),
                    UV_PAIR.fieldOf("uv").forGetter(obj -> ((CubeDefinitionAccess)(Object)obj).automobility$uv()),
                    UV_PAIR.optionalFieldOf("uv_scale", DEFAULT_UV_SCALE).forGetter(obj -> UVPairComparable.of(((CubeDefinitionAccess)(Object)obj).automobility$uvScale()))
            ).apply(instance, JsonEMCodecs::createCuboidData)
    );

    private static Codec<PartDefinition> createPartDefinitionCodec() {
        return RecordCodecBuilder.create((instance) ->
                instance.group(
                        PART_POSE.optionalFieldOf("transform", PartPose.ZERO).forGetter(obj -> ((PartDefinitionAccess)obj).automobility$transform()),
                        Codec.list(CUBE_DEFINITION).fieldOf("cuboids").forGetter(obj -> ((PartDefinitionAccess)obj).automobility$cuboids()),
                        LazyTypeUnboundedMapCodec.of(Codec.STRING, JsonEMCodecs::createPartDefinitionCodec).optionalFieldOf("children", new HashMap<>()).forGetter(obj -> ((PartDefinitionAccess)obj).automobility$children())
                ).apply(instance, (transform, cuboids, children) -> {
                    var data = PartDefinitionAccess.automobility$create(cuboids, transform);
                    ((PartDefinitionAccess) data).automobility$children().putAll(children);
                    return data;
                })
        );
    }

    public static final Codec<PartDefinition> PART_DEFINITION = createPartDefinitionCodec();

    public static final Codec<LayerDefinition> LAYER_DEFINITION = RecordCodecBuilder.create((instance) ->
            instance.group(
                    MATERIAL_DEFINITION.fieldOf("texture").forGetter(obj -> ((LayerDefinitionAccess) obj).automobility$texture()),
                    Codec.unboundedMap(Codec.STRING, PART_DEFINITION).fieldOf("bones").forGetter(obj -> ((PartDefinitionAccess) ((LayerDefinitionAccess) obj).automobility$root().getRoot()).automobility$children())
            ).apply(instance, (texture, bones) -> {
                var data = new MeshDefinition();
                ((PartDefinitionAccess) data.getRoot()).automobility$children().putAll(bones);
                return LayerDefinitionAccess.automobility$create(data, texture);
            })
    );
}
