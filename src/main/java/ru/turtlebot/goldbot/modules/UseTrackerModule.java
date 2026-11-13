package ru.turtlebot.goldbot.modules;
import net.minecraft.client.MinecraftClient;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.PotionItem;
import net.minecraft.text.Text;
import ru.turtlebot.goldbot.GoldModule;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
public final class UseTrackerModule extends GoldModule {
 private final Map<UUID,Session> sessions=new HashMap<>();
 public UseTrackerModule(){super("UseTracker","Misc");}
 @Override protected void onTick(MinecraftClient c){
  if(c.world==null||c.player==null){sessions.clear();return;}
  for(PlayerEntity p:c.world.getPlayers()){
   if(p==c.player)continue; UUID id=p.getUuid(); Session s=sessions.get(id);
   if(p.isUsingItem()){if(s==null)sessions.put(id,new Session(p.getActiveItem().copy(),0));else s.stoppedTicks=0;}
   else if(s!=null&&++s.stoppedTicks>=2){
    if(s.stack.getItem() instanceof PotionItem||s.stack.contains(DataComponentTypes.FOOD))
     c.inGameHud.getChatHud().addMessage(Text.literal("[UseTracker] "+p.getGameProfile().getName()+" used "+s.stack.getName().getString()));
    sessions.remove(id);
   }
  }
  sessions.keySet().removeIf(id->c.world.getPlayerByUuid(id)==null);
 }
 private static final class Session{final ItemStack stack;int stoppedTicks;Session(ItemStack s,int t){stack=s;stoppedTicks=t;}}
}
