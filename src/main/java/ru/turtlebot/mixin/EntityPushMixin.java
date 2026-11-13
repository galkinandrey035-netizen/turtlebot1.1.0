package ru.turtlebot.mixin;

import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import ru.turtlebot.goldbot.modules.NoPushModule;

@Mixin(Entity.class)
public abstract class EntityPushMixin {
    @Inject(method = "pushAwayFrom", at = @At("HEAD"), cancellable = true)
    private void turtlebot$cancelPlayerPush(Entity other, CallbackInfo ci) {
        if (NoPushModule.blocksPlayerPush((Entity) (Object) this, other)) ci.cancel();
    }
}