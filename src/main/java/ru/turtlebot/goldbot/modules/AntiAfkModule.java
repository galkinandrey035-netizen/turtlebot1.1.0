package ru.turtlebot.goldbot.modules;

import net.minecraft.client.MinecraftClient;
import net.minecraft.util.Hand;
import ru.turtlebot.goldbot.GoldModule;

public final class AntiAfkModule extends GoldModule {
    private int ticks;

    public AntiAfkModule() { super("AntiAFK", "Player"); }

    @Override
    protected void onEnable(MinecraftClient client) { ticks = 0; }

    @Override
    protected void onTick(MinecraftClient client) {
        if (client.player == null || client.world == null || client.currentScreen != null) return;
        if (++ticks >= 100) {
            ticks = 0;
            client.player.swingHand(Hand.MAIN_HAND);
        }
    }
}
