package ru.turtlebot.goldbot.modules;

import net.minecraft.client.MinecraftClient;
import ru.turtlebot.goldbot.GoldModule;

public final class AutoRespawnModule extends GoldModule {
    private int cooldown;

    public AutoRespawnModule() { super("AutoRespawn", "Player"); }

    @Override
    protected void onTick(MinecraftClient client) {
        if (client.player == null) return;
        if (cooldown > 0) cooldown--;
        if (client.player.getHealth() <= 0.0F && cooldown == 0) {
            client.player.requestRespawn();
            cooldown = 20;
        }
    }
}
