package ru.turtlebot.mixin;

import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.packet.s2c.play.PlayerPositionLookS2CPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import ru.turtlebot.goldbot.modules.ChorusControlModule;

@Mixin(ClientPlayNetworkHandler.class)
public abstract class ChorusControlMixin {
    @Inject(method = "onPlayerPositionLook", at = @At("HEAD"), cancellable = true)
    private void turtlebot$holdChorusTeleport(PlayerPositionLookS2CPacket packet, CallbackInfo ci) {
        if (ChorusControlModule.hold(packet)) ci.cancel();
    }
}