package ru.turtlebot.goldbot.modules;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.ItemEntity;
import net.minecraft.item.Items;
import net.minecraft.item.SpawnEggItem;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import ru.turtlebot.goldbot.GoldModule;
public final class AutoPilotModule extends GoldModule {
 public AutoPilotModule(){super("AutoPilot","Misc");}
 @Override protected void onTick(MinecraftClient client){
  if(client.player==null||client.world==null||!client.player.isGliding())return;
  ItemEntity target=client.world.getEntitiesByClass(ItemEntity.class,client.player.getBoundingBox().expand(50.0),
   item->item.isAlive()&&isFlightTarget(item)).stream().min(java.util.Comparator.comparingDouble(client.player::squaredDistanceTo)).orElse(null);
  if(target==null)return;
  Vec3d d=target.getPos().subtract(client.player.getEyePos()); double h=Math.sqrt(d.x*d.x+d.z*d.z);
  float yaw=(float)(Math.toDegrees(Math.atan2(d.z,d.x))-90.0), pitch=(float)-Math.toDegrees(Math.atan2(d.y,h));
  client.player.setYaw(client.player.getYaw()+MathHelper.clamp(MathHelper.wrapDegrees(yaw-client.player.getYaw()),-18,18));
  client.player.setPitch(client.player.getPitch()+MathHelper.clamp(MathHelper.wrapDegrees(pitch-client.player.getPitch()),-12,12));
 }
 private static boolean isFlightTarget(ItemEntity e){var i=e.getStack().getItem();return i==Items.ELYTRA||i==Items.FIREWORK_ROCKET||i instanceof SpawnEggItem;}
}