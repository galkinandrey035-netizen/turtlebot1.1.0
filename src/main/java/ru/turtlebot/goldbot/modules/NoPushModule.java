package ru.turtlebot.goldbot.modules;

import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import ru.turtlebot.goldbot.GoldModule;

/** Selectively blocks player, block and fluid push sources on the local player. */
public final class NoPushModule extends GoldModule {
    private static NoPushModule instance;
    private boolean players = true;
    private boolean blocks = true;
    private boolean water = true;

    public NoPushModule() { super("NoPush", "Player"); instance = this; }
    public static boolean blocksPlayerPush(Entity self, Entity other) {
        return instance != null && instance.enabled() && instance.players &&
                self == MinecraftClient.getInstance().player && other instanceof PlayerEntity;
    }
    public static boolean blocksFluidPush(Entity self) {
        return instance != null && instance.enabled() && instance.water &&
                self == MinecraftClient.getInstance().player;
    }
    public static boolean blocksOutOfBlockPush() {
        return instance != null && instance.enabled() && instance.blocks;
    }
    public void setPlayers(boolean value) { players = value; }
    public void setBlocks(boolean value) { blocks = value; }
    public void setWater(boolean value) { water = value; }
    @Override protected void onTick(MinecraftClient client) {}
}