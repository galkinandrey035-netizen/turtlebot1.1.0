package ru.turtlebot.mixin;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.item.HeldItemRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import ru.turtlebot.goldbot.modules.ItemChamsModule;

@Mixin(HeldItemRenderer.class)
public abstract class ItemChamsMixin {
    @Inject(method = "renderFirstPersonItem", at = @At("HEAD"))
    private void turtlebot$applyItemTint(AbstractClientPlayerEntity player, float tickDelta, float pitch,
            Hand hand, float swingProgress, ItemStack item, float equipProgress, MatrixStack matrices,
            VertexConsumerProvider consumers, int light, CallbackInfo ci) {
        if (ItemChamsModule.active()) {
            float[] c = ItemChamsModule.color();
            RenderSystem.setShaderColor(c[0], c[1], c[2], c[3]);
        }
    }
    @Inject(method = "renderFirstPersonItem", at = @At("TAIL"))
    private void turtlebot$resetItemTint(AbstractClientPlayerEntity player, float tickDelta, float pitch,
            Hand hand, float swingProgress, ItemStack item, float equipProgress, MatrixStack matrices,
            VertexConsumerProvider consumers, int light, CallbackInfo ci) {
        RenderSystem.setShaderColor(1, 1, 1, 1);
    }
}