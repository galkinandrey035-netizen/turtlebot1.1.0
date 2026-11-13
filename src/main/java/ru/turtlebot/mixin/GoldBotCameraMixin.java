package ru.turtlebot.mixin;

import dev.goldbot.Config;
import dev.goldbot.GoldBotMod;
import net.minecraft.client.render.Camera;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.world.BlockView;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Camera.class)
public abstract class GoldBotCameraMixin {
    @Inject(method = "update", at = @At("HEAD"))
    private void turtlebot$updateFreeCamBeforeCamera(BlockView area, Entity focusedEntity,
                                                      boolean thirdPerson, boolean inverseView,
                                                      float tickDelta, CallbackInfo ci) {
        if (GoldBotMod.FREECAM != null) {
            GoldBotRuntime.freeCamFrame(GoldBotMod.FREECAM, MinecraftClient.getInstance());
        }
    }

    @Inject(method = "clipToSpace", at = @At("HEAD"), cancellable = true)
    private void turtlebot$cameraNoClip(float desiredDistance, CallbackInfoReturnable<Float> cir) {
        GoldBotMod.cameraMixinHit = true;
        Config config = GoldBotMod.cfg;
        if (config != null && config.cameraNoClip) {
            cir.setReturnValue(Math.max(0.0F, config.cameraDistance / 10.0F));
        }
    }
}
