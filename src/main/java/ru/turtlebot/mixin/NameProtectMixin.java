package ru.turtlebot.mixin;
import net.minecraft.client.gui.hud.ChatHud;
import net.minecraft.client.gui.hud.PlayerListHud;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import ru.turtlebot.goldbot.modules.NameProtectModule;
import ru.turtlebot.goldbot.modules.TabExpertModule;
@Mixin(ChatHud.class) abstract class NameProtectChatMixin {
 @ModifyVariable(method="addMessage(Lnet/minecraft/text/Text;)V",at=@At("HEAD"),argsOnly=true)
 private Text turtlebot$maskChat(Text m){return Text.literal(NameProtectModule.mask(m.getString()));}
}
@Mixin(PlayerListHud.class) abstract class NameProtectTabMixin {
 @Inject(method="getPlayerName",at=@At("RETURN"),cancellable=true)
 private void turtlebot$maskTabName(PlayerListEntry e,CallbackInfoReturnable<Text> cir){
  Text original=cir.getReturnValue(); if(original==null)return;
  String name=e.getProfile().getName(); Text result=Text.literal(NameProtectModule.mask(original.getString()));
  if(TabExpertModule.highlightFriend(name))result=result.copy().styled(s->s.withColor(0x55FF55));
  cir.setReturnValue(result);
 }
}