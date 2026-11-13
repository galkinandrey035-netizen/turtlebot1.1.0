package ru.turtlebot.goldbot.modules;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.util.InputUtil;
import net.minecraft.client.option.KeyBinding;
import ru.turtlebot.goldbot.GoldModule;

public final class GuiMoveModule extends GoldModule {
    private final KeyBinding[] movementKeys;

    public GuiMoveModule() {
        super("GuiMove", "Movement");
        movementKeys = new KeyBinding[5];
    }

    @Override protected void onTick(MinecraftClient client) {
        if (client.player == null || client.getWindow() == null) return;
        if (client.currentScreen == null || client.currentScreen instanceof ChatScreen) return;
        if (client.currentScreen instanceof HandledScreen<?>) return;
        KeyBinding[] keys = {client.options.forwardKey, client.options.backKey,
                client.options.leftKey, client.options.rightKey, client.options.jumpKey};
        long window = client.getWindow().getHandle();
        for (KeyBinding key : keys) {
            key.setPressed(InputUtil.isKeyPressed(window, key.getDefaultKey().getCode()));
        }
    }

    @Override protected void onDisable(MinecraftClient client) {
        if (client == null) return;
        client.options.forwardKey.setPressed(false);
        client.options.backKey.setPressed(false);
        client.options.leftKey.setPressed(false);
        client.options.rightKey.setPressed(false);
        client.options.jumpKey.setPressed(false);
    }
}
