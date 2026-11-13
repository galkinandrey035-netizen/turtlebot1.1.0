package ru.turtlebot.mixin;

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
import ru.turtlebot.goldbot.modules.ViewModelModule;

@Mixin(HeldItemRenderer.class)
public abstract class ViewModelMixin {
    @Inject(method = "renderFirstPersonItem", at = @At("HEAD"))
    private void turtlebot$pushViewModel(AbstractClientPlayerEntity player, float tickDelta, float pitch,
            Hand hand, float swingProgress, ItemStack item, float equipProgress, MatrixStack matrices,
            VertexConsumerProvider consumers, int light, CallbackInfo ci) {
        float[] offset = ViewModelModule.offset(hand);
        matrices.push();
        matrices.translate(offset[0], offset[1], offset[2]);
    }
    @Inject(method = "renderFirstPersonItem", at = @At("TAIL"))
    private void turtlebot$popViewModel(AbstractClientPlayerEntity player, float tickDelta, float pitch,
            Hand hand, float swingProgress, ItemStack item, float equipProgress, MatrixStack matrices,
            VertexConsumerProvider consumers, int light, CallbackInfo ci) {
        matrices.pop();
    }
}