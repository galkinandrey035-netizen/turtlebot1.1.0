package ru.turtlebot.mixin;

import net.minecraft.client.gui.hud.ChatHud;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import ru.turtlebot.goldbot.modules.BetterChatModule;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

@Mixin(ChatHud.class)
public abstract class BetterChatMixin {
    @ModifyVariable(method = "addMessage(Lnet/minecraft/text/Text;)V", at = @At("HEAD"),
            argsOnly = true, ordinal = 0)
    private Text turtlebot$addTimestamp(Text message) {
        if (!BetterChatModule.timestamps()) return message;
        DateTimeFormatter format = DateTimeFormatter.ofPattern(
                BetterChatModule.seconds() ? "HH:mm:ss" : "HH:mm");
        return Text.literal("[" + LocalTime.now().format(format) + "] ").append(message);
    }
}