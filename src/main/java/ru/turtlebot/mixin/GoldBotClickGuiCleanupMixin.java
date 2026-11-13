package ru.turtlebot.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import java.util.Arrays;

/**
 * Hides GoldBot cards explicitly requested for removal without rewriting its GUI.
 */
@Pseudo
@Mixin(targets = "dev.goldbot.ClickGuiScreen")
public abstract class GoldBotClickGuiCleanupMixin {
    @Shadow(remap = false)
    @Final
    @Mutable
    private static String[] CATS;

    @Inject(method = "<clinit>", at = @At("TAIL"), remap = false)
    private static void turtlebot$removeCosmeticsCategory(CallbackInfo ci) {
        if (CATS != null && CATS.length > 4) {
            CATS = Arrays.copyOf(CATS, 4);
        }
    }

    @Overwrite(remap = false)
    private void chinaHatCard(int index) { }

    @Overwrite(remap = false)
    private void customModelsCard(int index) { }

    @Overwrite(remap = false)
    private void hitBubblesCard(int index) { }

    @Overwrite(remap = false)
    private void wingsCard(int index) { }

    @Overwrite(remap = false)
    private void trailLineCard(int index) { }

    @Overwrite(remap = false)
    private void cosmeticsGeneralTab() { }

    @Overwrite(remap = false)
    private void cosmeticsListTab(String category, String title) { }
}
