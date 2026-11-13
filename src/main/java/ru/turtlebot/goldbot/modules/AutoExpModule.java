package ru.turtlebot.goldbot.modules;

import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.item.Items;
import net.minecraft.util.Hand;
import org.lwjgl.glfw.GLFW;
import ru.turtlebot.goldbot.GoldModule;

public final class AutoExpModule extends GoldModule {
    private static final KeyBinding USE_KEY = KeyBindingHelper.registerKeyBinding(
            new KeyBinding("key.turtlebot.autoexp", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_X, "category.turtlebot"));
    private boolean lookDown = true;

    public AutoExpModule() { super("AutoExp", "Player"); }
    public void setLookDown(boolean value) { lookDown = value; }

    @Override protected void onTick(MinecraftClient client) {
        if (client.player == null || client.interactionManager == null || client.currentScreen != null || !USE_KEY.wasPressed()) return;
        int slot = -1;
        for (int i = 0; i < 9; i++) if (client.player.getInventory().getStack(i).isOf(Items.EXPERIENCE_BOTTLE)) { slot = i; break; }
        if (slot < 0) return;
        int previous = client.player.getInventory().selectedSlot;
        float pitch = client.player.getPitch();
        client.player.getInventory().selectedSlot = slot;
        if (lookDown) client.player.setPitch(90.0F);
        client.interactionManager.interactItem(client.player, Hand.MAIN_HAND);
        if (lookDown) client.player.setPitch(pitch);
        client.player.getInventory().selectedSlot = previous;
    }
}