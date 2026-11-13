package ru.turtlebot.goldbot.modules;

import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import ru.turtlebot.goldbot.GoldModule;

/** Prevents entity-to-entity player push; block and fluid push remain vanilla. */
public final class NoPushModule extends GoldModule {
    private static NoPushModule instance;
    private boolean players = true;

    public NoPushModule() { super("NoPush", "Player"); instance = this; }
    public static boolean blocksPlayerPush(Entity self, Entity other) {
        return instance != null && instance.enabled() && instance.players &&
                self instanceof PlayerEntity && other instanceof PlayerEntity;
    }
    public void setPlayers(boolean value) { players = value; }
    @Override protected void onTick(MinecraftClient client) {}
}