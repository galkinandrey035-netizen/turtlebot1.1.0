package ru.turtlebot.goldbot.modules;

import com.mojang.authlib.GameProfile;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.OtherClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import ru.turtlebot.goldbot.GoldModule;

import java.util.UUID;

/** Spawns a local-only player clone for visual/testing purposes. */
public final class FakePlayerModule extends GoldModule {
    private String fakeName = "GoldBot";
    private float health = 20.0F;
    private OtherClientPlayerEntity fakePlayer;

    public FakePlayerModule() {
        super("FakePlayer", "Player");
    }

    public void setFakeName(String value) {
        if (value != null && !value.isBlank()) fakeName = value.trim();
    }

    public void setHealth(float value) {
        health = Math.max(1.0F, Math.min(100.0F, value));
        if (fakePlayer != null) fakePlayer.setHealth(health);
    }

    @Override
    protected void onEnable(MinecraftClient client) {
        PlayerEntity player = client.player;
        if (player == null || client.world == null) return;
        fakePlayer = new OtherClientPlayerEntity(client.world,
                new GameProfile(UUID.randomUUID(), fakeName));
        fakePlayer.setId(-1337);
        fakePlayer.copyPositionAndRotation(player);
        fakePlayer.setHealth(health);
        client.world.addEntity(fakePlayer);
    }

    @Override
    protected void onTick(MinecraftClient client) {
        if (fakePlayer != null && client.world != null && !fakePlayer.isRemoved()) return;
        if (client.player != null && client.world != null) onEnable(client);
    }

    @Override
    protected void onDisable(MinecraftClient client) {
        if (fakePlayer != null) {
            fakePlayer.remove(Entity.RemovalReason.DISCARDED);
            fakePlayer = null;
        }
    }
}
