package ru.turtlebot.goldbot.modules;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import ru.turtlebot.goldbot.GoldModule;

/** Automatic sprint with the Normal and Legit activation modes from GoldBot. */
public final class SprintModule extends GoldModule {
    public enum Mode { NORMAL, LEGIT }

    private Mode mode = Mode.NORMAL;

    public SprintModule() {
        super("Sprint", "Movement");
    }

    public void setMode(Mode mode) {
        if (mode != null) this.mode = mode;
    }

    public Mode getMode() {
        return mode;
    }

    @Override
    protected void onTick(MinecraftClient client) {
        ClientPlayerEntity player = client.player;
        if (player == null || client.currentScreen != null || client.world == null) return;

        boolean movingForward = player.input.movementForward > 0.0F;
        boolean blockedBySneak = client.options.sneakKey.isPressed();
        boolean blockedByFood = player.getHungerManager().getFoodLevel() <= 6;
        boolean blockedByUse = player.isUsingItem();

        if (!movingForward || blockedBySneak || blockedByFood || blockedByUse
                || player.isTouchingWater() || player.isGliding()) {
            player.setSprinting(false);
            return;
        }

        if (mode == Mode.LEGIT && client.options.sprintKey.isPressed()) {
            return;
        }

        player.setSprinting(true);
    }
}
