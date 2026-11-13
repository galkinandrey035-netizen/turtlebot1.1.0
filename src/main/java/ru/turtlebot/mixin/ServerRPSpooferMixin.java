package ru.turtlebot.mixin;
import net.minecraft.client.network.ClientCommonNetworkHandler;
import net.minecraft.network.packet.s2c.common.ResourcePackSendS2CPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import ru.turtlebot.goldbot.modules.ServerRPSpooferModule;
@Mixin(ClientCommonNetworkHandler.class) public abstract class ServerRPSpooferMixin {
 @Inject(method="onResourcePackSend",at=@At("HEAD"),cancellable=true)
 private void turtlebot$spoof(ResourcePackSendS2CPacket p,CallbackInfo ci){if(ServerRPSpooferModule.handle(p))ci.cancel();}
}