package ru.turtlebot.goldbot.modules;

import net.minecraft.client.MinecraftClient;
import ru.turtlebot.goldbot.GoldModule;

public final class SprintModule extends GoldModule {
    public SprintModule() { super("Sprint", "Movement"); }

    @Override
    protected void onTick(MinecraftClient client) {
        if (client.player == null || client.currentScreen != null) return;
        if (client.options.forwardKey.isPressed()
                && !client.options.sneakKey.isPressed()
                && client.player.getHungerManager().getFoodLevel() > 6) {
            client.player.setSprinting(true);
        }
    }
}
