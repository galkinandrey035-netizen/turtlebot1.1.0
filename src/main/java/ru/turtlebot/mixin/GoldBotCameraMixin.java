package ru.turtlebot.mixin;

import dev.goldbot.Config;
import dev.goldbot.GoldBotMod;
import net.minecraft.client.render.Camera;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Camera.class)
public abstract class GoldBotCameraMixin {
    @Inject(method = "clipToSpace", at = @At("HEAD"), cancellable = true)
    private void turtlebot$cameraNoClip(float desiredDistance, CallbackInfoReturnable<Float> cir) {
        GoldBotMod.cameraMixinHit = true;
        Config config = GoldBotMod.cfg;
        if (config != null && config.cameraNoClip) {
            cir.setReturnValue(Math.max(0.0F, config.cameraDistance / 10.0F));
        }
    }
}
