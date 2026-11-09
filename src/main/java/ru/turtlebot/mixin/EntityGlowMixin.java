package ru.turtlebot.mixin;

import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import ru.turtlebot.util.FedTurtleHighlighter;

@Mixin(Entity.class)
public abstract class EntityGlowMixin {
    @Inject(method = "isGlowing", at = @At("HEAD"), cancellable = true)
    private void turtlebot$glow(CallbackInfoReturnable<Boolean> cir) {
        if (FedTurtleHighlighter.shouldGlow((Entity) (Object) this)) cir.setReturnValue(true);
    }
}
