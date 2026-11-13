package ru.turtlebot.goldbot.modules;
import net.minecraft.client.MinecraftClient;
import net.minecraft.network.packet.s2c.common.ResourcePackSendS2CPacket;
import net.minecraft.network.packet.c2s.common.ResourcePackStatusC2SPacket;
import ru.turtlebot.goldbot.GoldModule;
import java.util.UUID;
public final class ServerRPSpooferModule extends GoldModule {
 private static ServerRPSpooferModule instance; private UUID pendingId; private int ticks;
 public ServerRPSpooferModule(){super("ServerRPSpoofer","Misc");instance=this;}
 public static boolean handle(ResourcePackSendS2CPacket p){
  if(instance==null||!instance.enabled())return false; MinecraftClient c=MinecraftClient.getInstance();
  if(c.getNetworkHandler()==null)return false;
  c.getNetworkHandler().getConnection().send(new ResourcePackStatusC2SPacket(p.id(),ResourcePackStatusC2SPacket.Status.ACCEPTED));
  instance.pendingId=p.id();instance.ticks=6;return true;
 }
 @Override protected void onTick(MinecraftClient c){
  if(pendingId==null||c.getNetworkHandler()==null)return;
  if(--ticks<=0){c.getNetworkHandler().getConnection().send(new ResourcePackStatusC2SPacket(pendingId,ResourcePackStatusC2SPacket.Status.SUCCESSFULLY_LOADED));pendingId=null;}
 }
 @Override protected void onDisable(MinecraftClient c){pendingId=null;ticks=0;}
}