package ru.turtlebot.mixin;

import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.Window;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import ru.turtlebot.goldbot.modules.AspectRatioModule;

@Mixin(GameRenderer.class)
public abstract class AspectRatioMixin {
    @Redirect(method = "getBasicProjectionMatrix", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/client/util/Window;getFramebufferWidth()I"))
    private int turtlebot$adjustProjectionWidth(Window window) {
        return Math.max(1, (int) Math.round(window.getFramebufferWidth() * AspectRatioModule.ratio()));
    }
}