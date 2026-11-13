package ru.turtlebot.mixin;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import ru.turtlebot.goldbot.modules.NoRenderModule;

@Mixin(InGameHud.class)
public abstract class NoRenderHudMixin {
    @Inject(method = "renderPortalOverlay", at = @At("HEAD"), cancellable = true)
    private void turtlebot$hidePortal(DrawContext context, float strength, CallbackInfo ci) {
        if (NoRenderModule.portal()) ci.cancel();
    }
    @Inject(method = "renderNauseaOverlay", at = @At("HEAD"), cancellable = true)
    private void turtlebot$hideNausea(DrawContext context, float strength, CallbackInfo ci) {
        if (NoRenderModule.nausea()) ci.cancel();
    }
    @Inject(method = "renderStatusEffectOverlay", at = @At("HEAD"), cancellable = true)
    private void turtlebot$hidePotionIcons(DrawContext context, RenderTickCounter counter, CallbackInfo ci) {
        if (NoRenderModule.potions()) ci.cancel();
    }
    @Inject(method = "renderVignetteOverlay", at = @At("HEAD"), cancellable = true)
    private void turtlebot$hideVignette(DrawContext context, Entity entity, CallbackInfo ci) {
        if (NoRenderModule.vignette()) ci.cancel();
    }
}