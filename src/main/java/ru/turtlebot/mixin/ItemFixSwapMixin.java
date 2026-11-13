package ru.turtlebot.mixin;

import net.minecraft.network.ClientConnection;
import net.minecraft.network.packet.Packet;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import ru.turtlebot.goldbot.modules.ItemFixSwapModule;

@Mixin(ClientConnection.class)
public abstract class ItemFixSwapMixin {
    @Inject(method = "send(Lnet/minecraft/network/packet/Packet;)V", at = @At("HEAD"), cancellable = true)
    private void turtlebot$fixSelectedSlot(Packet<?> packet, CallbackInfo ci) {
        ClientConnection connection = (ClientConnection) (Object) this;
        ItemFixSwapModule.beforeAction(connection, packet);
        if (ItemFixSwapModule.cancelSlotPacket(packet)) ci.cancel();
    }
}