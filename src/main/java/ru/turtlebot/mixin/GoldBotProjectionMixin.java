package ru.turtlebot.mixin;

import dev.goldbot.Config;
import dev.goldbot.GoldBotMod;
import dev.goldbot.HandFxBridge;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.GameRenderer;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(GameRenderer.class)
public abstract class GoldBotProjectionMixin {
    @Inject(method = "getBasicProjectionMatrix", at = @At("TAIL"), cancellable = true)
    private void turtlebot$projection(float fov, CallbackInfoReturnable<Matrix4f> cir) {
        Config config = GoldBotMod.cfg;
        if (config == null) return;

        if (GoldBotMod.projPhase == 2) {
            GoldBotMod.projPhase = 3;
            if (HandFxBridge.wanted()) {
                cir.setReturnValue(HandFxBridge.apply(cir.getReturnValue()));
            }
            return;
        }

        if (!config.aspectEnabled) return;
        MinecraftClient client = MinecraftClient.getInstance();
        float width = client.getWindow().getWidth();
        float height = client.getWindow().getHeight();
        float targetAspect = config.aspectRatio / 100.0F;
        if (width <= 0.0F || height <= 0.0F || targetAspect < 0.1F) return;

        Matrix4f projection = cir.getReturnValue();
        projection.m00(projection.m00() * (height / width) / targetAspect);
        cir.setReturnValue(projection);
    }
}
