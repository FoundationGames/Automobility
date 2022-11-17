package io.github.foundationgames.automobility.mixin;

import com.google.gson.JsonObject;
import net.minecraft.util.GsonHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(targets = "net/minecraft/client/renderer/block/model/BlockElement$Deserializer")
public class BlockElementDeserializerMixin {
    @Inject(method = "getAngle(Lcom/google/gson/JsonObject;)F", at = @At("HEAD"), cancellable = true)
    private void automobility$letAtanOfZeroPointFiveBeAValidBlockModelCuboidRotationAngle(JsonObject object, CallbackInfoReturnable<Float> cir) {
        float f = GsonHelper.getAsFloat(object, "angle");
        if (f >= 26.5 && f <= 26.6) cir.setReturnValue(f);
    }
}
