package ru.turtlebot.mixin;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import ru.turtlebot.goldbot.modules.AntiInvisibleModule;

@Mixin(Entity.class)
public abstract class AntiInvisibleMixin {
    @Inject(method = "isInvisibleTo", at = @At("HEAD"), cancellable = true)
    private void turtlebot$revealInvisible(PlayerEntity viewer, CallbackInfoReturnable<Boolean> cir) {
        if (AntiInvisibleModule.shouldReveal((Entity) (Object) this)) cir.setReturnValue(false);
    }
}