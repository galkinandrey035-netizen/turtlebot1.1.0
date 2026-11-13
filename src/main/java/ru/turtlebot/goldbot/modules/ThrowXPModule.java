package ru.turtlebot.goldbot.modules;
import net.minecraft.client.MinecraftClient;
import net.minecraft.item.Items;
import net.minecraft.util.Hand;
import ru.turtlebot.goldbot.GoldModule;
public final class ThrowXPModule extends GoldModule {
 private int delay; public ThrowXPModule(){super("ThrowXP","Misc");}
 @Override protected void onTick(MinecraftClient c){
  if(c.player==null||c.interactionManager==null)return;if(--delay>0)return;
  if(c.player.getMainHandStack().isOf(Items.EXPERIENCE_BOTTLE)){c.interactionManager.interactItem(c.player,Hand.MAIN_HAND);delay=6;}
 }
}