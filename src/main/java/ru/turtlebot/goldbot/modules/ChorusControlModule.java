package ru.turtlebot.goldbot.modules;

import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.item.Items;
import net.minecraft.network.packet.s2c.play.PlayerPositionLookS2CPacket;
import org.lwjgl.glfw.GLFW;
import ru.turtlebot.goldbot.GoldModule;

/** Holds the chorus-fruit teleport correction until the user confirms it. */
public final class ChorusControlModule extends GoldModule {
    private static final KeyBinding CONFIRM_KEY = KeyBindingHelper.registerKeyBinding(
            new KeyBinding("key.turtlebot.chorus_confirm", InputUtil.Type.KEYSYM,
                    GLFW.GLFW_KEY_LEFT_SHIFT, "category.turtlebot"));
    private static ChorusControlModule instance;
    private PlayerPositionLookS2CPacket pending;
    private boolean armed;
    private boolean replaying;

    public ChorusControlModule() { super("ChorusControl", "Player"); instance = this; }
    public static boolean hold(PlayerPositionLookS2CPacket packet) {
        if (instance == null || !instance.enabled() || instance.replaying || !instance.armed) return false;
        instance.pending = packet;
        return true;
    }

    @Override protected void onTick(MinecraftClient client) {
        if (client.player == null) return;
        if (client.player.isUsingItem() && client.player.getActiveItem().isOf(Items.CHORUS_FRUIT)
                && client.player.getItemUseTimeLeft() <= 1) armed = true;
        if (pending != null && CONFIRM_KEY.wasPressed()) applyPending(client);
    }

    private void applyPending(MinecraftClient client) {
        if (pending == null || client.getNetworkHandler() == null) return;
        PlayerPositionLookS2CPacket packet = pending;
        pending = null;
        armed = false;
        replaying = true;
        try { packet.apply(client.getNetworkHandler()); }
        finally { replaying = false; }
    }

    @Override protected void onDisable(MinecraftClient client) {
        applyPending(client);
        pending = null;
        armed = false;
    }
}