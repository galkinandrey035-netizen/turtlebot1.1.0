package ru.turtlebot.goldbot.modules;
import net.minecraft.client.MinecraftClient;
import ru.turtlebot.goldbot.GoldModule;
import ru.turtlebot.goldbot.FriendRegistry;
public final class NameProtectModule extends GoldModule {
 private static NameProtectModule instance; private String selfMask="Player",friendMask="Friend"; private boolean hideFriends;
 public NameProtectModule(){super("NameProtect","Misc");instance=this;}
 public static String mask(String input){
  if(input==null||instance==null||!instance.enabled()||MinecraftClient.getInstance().player==null)return input;
  String r=input.replace(MinecraftClient.getInstance().player.getGameProfile().getName(),instance.selfMask);
  if(instance.hideFriends)for(String f:FriendRegistry.all())r=r.replaceAll("(?i)"+java.util.regex.Pattern.quote(f),java.util.regex.Matcher.quoteReplacement(instance.friendMask));
  return r;
 }
 public void setSelfMask(String v){selfMask=v==null||v.isBlank()?"Player":v;}
 public void setFriendMask(String v){friendMask=v==null||v.isBlank()?"Friend":v;}
 public void setHideFriends(boolean v){hideFriends=v;}
 @Override protected void onTick(MinecraftClient client){}
}