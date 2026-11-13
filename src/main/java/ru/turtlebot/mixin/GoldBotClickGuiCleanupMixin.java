package ru.turtlebot.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

/**
 * Hides GoldBot cards explicitly requested for removal without rewriting its GUI.
 */
@Mixin(targets = "dev.goldbot.ClickGuiScreen")
public abstract class GoldBotClickGuiCleanupMixin {
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
