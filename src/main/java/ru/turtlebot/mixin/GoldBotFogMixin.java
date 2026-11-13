package ru.turtlebot.mixin;

import dev.goldbot.Config;
import dev.goldbot.GoldBotMod;
import dev.goldbot.SkyFx;
import net.minecraft.client.render.BackgroundRenderer;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.Fog;
import net.minecraft.client.render.FogShape;
import net.minecraft.client.world.ClientWorld;
import org.joml.Vector4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BackgroundRenderer.class)
public abstract class GoldBotFogMixin {
    @Inject(method = "getFogColor", at = @At("HEAD"), cancellable = true)
    private static void turtlebot$skyFogColor(Camera camera, float tickDelta, ClientWorld world,
                                               int viewDistance, float skyDarkness,
                                               CallbackInfoReturnable<Vector4f> cir) {
        int rgb = GoldBotRuntime.fogRgb(camera, world);
        if (rgb < 0) return;
        cir.setReturnValue(new Vector4f(
                ((rgb >> 16) & 255) / 255.0F,
                ((rgb >> 8) & 255) / 255.0F,
                (rgb & 255) / 255.0F,
                1.0F
        ));
    }

    @Inject(method = "applyFog", at = @At("HEAD"), cancellable = true)
    private static void turtlebot$customFog(Camera camera, BackgroundRenderer.FogType fogType,
                                             Vector4f fogColor, float viewDistance, boolean thickFog,
                                             float tickDelta, CallbackInfoReturnable<Fog> cir) {
        Config config = GoldBotMod.cfg;
        if (config == null || !config.fogEnabled
                || fogType != BackgroundRenderer.FogType.FOG_TERRAIN
                || GoldBotRuntime.fogBlocked(camera)) {
            return;
        }

        int rgb = config.fogC.at(System.currentTimeMillis(), 0.0D) & 0xFFFFFF;
        float start = Math.max(0, config.fogStart);
        float end = Math.max(start + 1.0F, config.fogEnd);
        cir.setReturnValue(new Fog(start, end, FogShape.SPHERE,
                ((rgb >> 16) & 255) / 255.0F,
                ((rgb >> 8) & 255) / 255.0F,
                (rgb & 255) / 255.0F,
                1.0F));
    }
}
