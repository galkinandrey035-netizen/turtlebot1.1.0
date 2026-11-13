package ru.turtlebot.goldbot.modules;

import net.minecraft.client.MinecraftClient;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import ru.turtlebot.goldbot.GoldModule;

/**
 * AutoRespawn port with GoldBot's Default and FunTime Back modes.
 * FunTime Back sends the legacy fallback position before requesting respawn.
 */
public final class AutoRespawnModule extends GoldModule {
    public enum Mode {
        DEFAULT,
        FUNTIME_BACK
    }

    private Mode mode = Mode.DEFAULT;
    private int cooldown;

    public AutoRespawnModule() {
        super("AutoRespawn", "Player");
    }

    public Mode mode() {
        return mode;
    }

    public void setMode(Mode mode) {
        if (mode != null) this.mode = mode;
    }

    @Override
    protected void onEnable(MinecraftClient client) {
        cooldown = 0;
    }

    @Override
    protected void onTick(MinecraftClient client) {
        if (client.player == null) return;

        if (cooldown > 0) cooldown--;
        if (client.player.getHealth() > 0.0F || cooldown > 0) return;

        if (mode == Mode.FUNTIME_BACK && client.getNetworkHandler() != null) {
            client.getNetworkHandler().sendPacket(
                    new PlayerMoveC2SPacket.PositionAndOnGround(1448.0, 1337.0, 228.0, false, false));
        }

        client.player.requestRespawn();
        if (mode == Mode.DEFAULT) {
            client.setScreen(null);
        }
        cooldown = 20;
    }
}
