package ru.turtlebot.mixin;

import net.minecraft.client.gui.hud.InGameOverlayRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import ru.turtlebot.goldbot.modules.NoRenderModule;

@Mixin(InGameOverlayRenderer.class)
public abstract class NoRenderOverlayMixin {
    @Inject(method = "renderFireOverlay", at = @At("HEAD"), cancellable = true)
    private static void turtlebot$hideFire(CallbackInfo ci) {
        if (NoRenderModule.fire()) ci.cancel();
    }
}