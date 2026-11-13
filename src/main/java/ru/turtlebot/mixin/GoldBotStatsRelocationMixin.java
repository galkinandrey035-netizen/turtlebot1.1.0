package ru.turtlebot.mixin;

import net.minecraft.client.MinecraftClient;
import ru.turtlebot.config.ConfigManager;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.lang.reflect.Method;
import java.util.Locale;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;

/**
 * Moves the always-visible inventory/status/health strip into GoldBot's GoldHelper page.
 */
@Pseudo
@Mixin(targets = "dev.goldbot.ClickGuiScreen")
public abstract class GoldBotStatsRelocationMixin {
    @Shadow(remap = false)
    private void note(String text) { }

    @Shadow(remap = false)
    private void beginCard(int column, Item icon, String title,
                           BooleanSupplier enabled, Consumer<Boolean> toggle) { }

    @Shadow(remap = false)
    private void endCard() { }

    /**
     * Prevents the stats strip from being drawn at the bottom of every ClickGUI page.
     */
    @Overwrite(remap = false)
    private void drawStats(DrawContext context) { }

    @Inject(method = "visualPage", at = @At("TAIL"), remap = false)
    private void turtlebot$addTargetHudCard(CallbackInfo ci) {
        beginCard(0, Items.PLAYER_HEAD, "TargetHUD",
                () -> ConfigManager.get().targetHudEnabled,
                enabled -> {
                    ConfigManager.get().targetHudEnabled = enabled;
                    ConfigManager.save();
                });
        note("Показывает имя и здоровье последней атакованной цели.");
        endCard();
    }

    @Inject(method = "helperTab", at = @At("TAIL"), remap = false)
    private void turtlebot$appendGoldBotStats(CallbackInfo ci) {
        beginCard(0, Items.GOLD_INGOT, "GoldBot status", null, null);
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player == null) {
            note("Золото в инвентаре: —");
            note("Статус: недоступен");
            note("Здоровье: —");
            endCard();
            return;
        }

        int gold = 0;
        try {
            Class<?> dropResources = Class.forName("dev.goldbot.DropResources");
            Method isGold = dropResources.getDeclaredMethod("isGold", ItemStack.class);
            isGold.setAccessible(true);
            for (int slot = 0; slot < 36; slot++) {
                ItemStack stack = client.player.getInventory().getStack(slot);
                if ((boolean) isGold.invoke(null, stack)) gold += stack.getCount();
            }
        } catch (ReflectiveOperationException | RuntimeException ignored) {
            // Keep the ClickGUI usable even if an optional GoldBot helper changes.
        }

        String status = "неизвестен";
        try {
            Class<?> botLink = Class.forName("dev.goldbot.BotLink");
            status = String.valueOf(botLink.getMethod("status").invoke(null));
        } catch (ReflectiveOperationException | RuntimeException ignored) {
            // Status is optional; the other two values are still useful.
        }

        String health = String.format(Locale.ROOT, "%.1f / %.1f HP",
                client.player.getHealth(), client.player.getMaxHealth());
        note("Золото в инвентаре: " + gold);
        note("Статус: " + status);
        note("Здоровье: " + health);
        endCard();
    }
}
