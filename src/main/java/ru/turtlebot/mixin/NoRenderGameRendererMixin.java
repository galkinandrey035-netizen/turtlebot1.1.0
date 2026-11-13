package ru.turtlebot.mixin;

import net.minecraft.client.render.GameRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import ru.turtlebot.goldbot.modules.NoRenderModule;

@Mixin(GameRenderer.class)
public abstract class NoRenderGameRendererMixin {
    @Inject(method = "tiltViewWhenHurt", at = @At("HEAD"), cancellable = true)
    private void turtlebot$disableHurtCamera(CallbackInfo ci) {
        if (NoRenderModule.hurtCam()) ci.cancel();
    }
}