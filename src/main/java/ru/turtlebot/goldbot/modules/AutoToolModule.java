package ru.turtlebot.goldbot.modules;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import ru.turtlebot.goldbot.GoldModule;

/**
 * Selects the most effective hotbar tool while the player is mining.
 * Matches GoldBot's Swap Back and Swap Back Delay behavior.
 */
public final class AutoToolModule extends GoldModule {
    private boolean swapBack = true;
    private int swapBackDelay = 4;
    private int originalSlot = -1;
    private int releaseTicks;
    private BlockPos target;

    public AutoToolModule() { super("AutoTool", "Player"); }

    public boolean swapBack() { return swapBack; }

    public void setSwapBack(boolean value) {
        swapBack = value;
    }

    public int swapBackDelay() { return swapBackDelay; }

    public void setSwapBackDelay(int ticks) {
        swapBackDelay = Math.max(0, Math.min(40, ticks));
    }

    @Override
    protected void onTick(MinecraftClient client) {
        ClientPlayerEntity player = client.player;
        if (player == null || client.world == null || client.currentScreen != null) {
            restore(player);
            return;
        }

        if (!client.options.attackKey.isPressed()) {
            target = null;
            if (swapBack && originalSlot >= 0) {
                releaseTicks++;
                if (releaseTicks >= swapBackDelay) restore(player);
            } else if (!swapBack) {
                originalSlot = -1;
                releaseTicks = 0;
            }
            return;
        }

        if (!(client.crosshairTarget instanceof BlockHitResult hit)
                || hit.getType() != HitResult.Type.BLOCK) {
            return;
        }

        BlockPos pos = hit.getBlockPos();
        if (!pos.equals(target)) {
            target = pos.toImmutable();
            releaseTicks = 0;
            if (originalSlot < 0) originalSlot = player.getInventory().selectedSlot;
        }

        var state = client.world.getBlockState(pos);
        int bestSlot = -1;
        float bestSpeed = 1.0F;

        for (int slot = 0; slot < 9; slot++) {
            ItemStack stack = player.getInventory().getStack(slot);
            if (stack.isEmpty()) continue;

            float speed = stack.getMiningSpeedMultiplier(state);
            if (speed > bestSpeed) {
                bestSpeed = speed;
                bestSlot = slot;
            }
        }

        if (bestSlot >= 0) {
            player.getInventory().selectedSlot = bestSlot;
        }
    }

    private void restore(ClientPlayerEntity player) {
        if (player != null && originalSlot >= 0 && swapBack) {
            player.getInventory().selectedSlot = originalSlot;
        }
        originalSlot = -1;
        releaseTicks = 0;
        target = null;
    }

    @Override
    protected void onDisable(MinecraftClient client) {
        restore(client.player);
    }
}
