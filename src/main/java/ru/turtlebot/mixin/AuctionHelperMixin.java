package ru.turtlebot.mixin;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.screen.slot.Slot;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import ru.turtlebot.goldbot.modules.AuctionHelperModule;

@Mixin(HandledScreen.class)
public abstract class AuctionHelperMixin {
    @Inject(method = "drawSlot", at = @At("HEAD"))
    private void turtlebot$highlightCheapest(DrawContext context, Slot slot, CallbackInfo ci) {
        if (AuctionHelperModule.isCheapest(slot)) {
            context.fill(slot.x, slot.y, slot.x + 16, slot.y + 16, 0x6633FF33);
        }
    }
}