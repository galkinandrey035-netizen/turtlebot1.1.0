package ru.turtlebot.mixin;

import net.minecraft.client.network.ClientPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import ru.turtlebot.goldbot.modules.NoPushModule;

@Mixin(ClientPlayerEntity.class)
public abstract class ClientPlayerPushMixin {
    @Inject(method = "pushOutOfBlocks", at = @At("HEAD"), cancellable = true)
    private void turtlebot$cancelBlockPush(double x, double z, CallbackInfo ci) {
        if (NoPushModule.blocksOutOfBlockPush()) ci.cancel();
    }
}