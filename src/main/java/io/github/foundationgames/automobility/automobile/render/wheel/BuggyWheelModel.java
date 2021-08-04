package io.github.foundationgames.automobility.automobile.render.wheel;

import dev.monarkhes.myron.api.Myron;
import io.github.foundationgames.automobility.Automobility;
import io.github.foundationgames.automobility.automobile.WheelBase;
import io.github.foundationgames.automobility.util.AUtils;
import net.minecraft.client.model.Model;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Vec3f;

public class BuggyWheelModel extends Model implements WheelContextReceiver {
    private final BakedModel front = Myron.getModel(Automobility.id("models/misc/automobile/wheel/buggywheel_frontsteer"));
    private final BakedModel rear = Myron.getModel(Automobility.id("models/misc/automobile/wheel/buggywheel_rear"));

    private WheelBase.WheelPos wheelPos;

    public BuggyWheelModel(EntityRendererFactory.Context ctx) {
        super(id -> RenderLayer.getSolid());
    }

    @Override
    public void render(MatrixStack matrices, VertexConsumer vertices, int light, int overlay, float red, float green, float blue, float alpha) {
        if (wheelPos == null) return;
        var model = wheelPos.end() == WheelBase.WheelEnd.BACK ? rear : front;
        if (model == null) return;
        matrices.push();
        matrices.multiply(Vec3f.POSITIVE_X.getDegreesQuaternion(180));
        matrices.translate(0, -1.5, 0);
        AUtils.renderMyronObj(model, vertices, matrices, light, overlay);
        matrices.pop();
    }

    @Override
    public void provideContext(WheelBase.WheelPos pos) {
        this.wheelPos = pos;
    }
}
