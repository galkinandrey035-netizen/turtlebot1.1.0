package ru.turtlebot.goldbot.modules;
import net.minecraft.client.MinecraftClient;
import ru.turtlebot.goldbot.GoldModule;
import ru.turtlebot.goldbot.FriendRegistry;
public final class TabExpertModule extends GoldModule {
 private static TabExpertModule instance; private int limit=200; private boolean highlightFriends=true;
 public TabExpertModule(){super("TabExpert","Misc");instance=this;}
 public static boolean highlightFriend(String n){return instance!=null&&instance.enabled()&&instance.highlightFriends&&FriendRegistry.isFriend(n);}
 public void setLimit(int v){limit=Math.max(1,Math.min(1000,v));}
 public int limit(){return limit;}
 public void setHighlightFriends(boolean v){highlightFriends=v;}
 @Override protected void onTick(MinecraftClient client){}
}