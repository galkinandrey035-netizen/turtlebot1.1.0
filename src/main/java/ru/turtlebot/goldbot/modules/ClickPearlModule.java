package ru.turtlebot.goldbot.modules;

import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.item.Items;
import net.minecraft.util.Hand;
import org.lwjgl.glfw.GLFW;
import ru.turtlebot.goldbot.GoldModule;

public final class ClickPearlModule extends GoldModule {
    private static final KeyBinding THROW_KEY = KeyBindingHelper.registerKeyBinding(
            new KeyBinding("key.turtlebot.clickpearl", InputUtil.Type.KEYSYM,
                    GLFW.GLFW_KEY_P, "category.turtlebot"));

    public ClickPearlModule() { super("ClickPearl", "Combat"); }

    @Override protected void onTick(MinecraftClient client) {
        if (client.player == null || client.interactionManager == null ||
                client.currentScreen != null || !THROW_KEY.wasPressed()) return;
        int pearlSlot = -1;
        for (int slot = 0; slot < 9; slot++) {
            if (client.player.getInventory().getStack(slot).isOf(Items.ENDER_PEARL)) {
                pearlSlot = slot;
                break;
            }
        }
        if (pearlSlot < 0) return;
        int previousSlot = client.player.getInventory().selectedSlot;
        client.player.getInventory().selectedSlot = pearlSlot;
        client.interactionManager.interactItem(client.player, Hand.MAIN_HAND);
        client.player.getInventory().selectedSlot = previousSlot;
    }
}
