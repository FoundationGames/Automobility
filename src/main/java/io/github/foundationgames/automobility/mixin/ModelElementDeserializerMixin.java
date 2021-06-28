package io.github.foundationgames.automobility.mixin;

import com.google.gson.JsonObject;
import net.minecraft.util.JsonHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(targets = "net/minecraft/client/render/model/json/ModelElement$Deserializer")
public class ModelElementDeserializerMixin {
    /*
             i am inevitable
     */
    @Inject(method = "deserializeRotationAngle(Lcom/google/gson/JsonObject;)F", at = @At("HEAD"), cancellable = true)
    private void automobility$letAtanOfZeroPointFiveBeAValidBlockModelCuboidRotationAngle(JsonObject object, CallbackInfoReturnable<Float> cir) {
        float f = JsonHelper.getFloat(object, "angle");
        if (f >= 26.5 && f <= 26.6) cir.setReturnValue(f);
    }
}
