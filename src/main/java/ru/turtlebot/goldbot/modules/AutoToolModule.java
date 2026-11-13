package ru.turtlebot.goldbot.modules;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import ru.turtlebot.goldbot.GoldModule;

public final class AutoToolModule extends GoldModule {
    private int originalSlot = -1;
    private BlockPos target;

    public AutoToolModule() { super("AutoTool", "Player"); }

    @Override
    protected void onTick(MinecraftClient client) {
        ClientPlayerEntity player = client.player;
        if (player == null || client.world == null || client.currentScreen != null
                || !client.options.attackKey.isPressed()
                || !(client.crosshairTarget instanceof BlockHitResult hit)
                || hit.getType() != HitResult.Type.BLOCK) {
            restore(player);
            return;
        }

        BlockPos pos = hit.getBlockPos();
        if (!pos.equals(target)) {
            if (originalSlot < 0) originalSlot = player.getInventory().getSelectedSlot();
            target = pos.toImmutable();
        }

        var state = client.world.getBlockState(pos);
        int bestSlot = player.getInventory().getSelectedSlot();
        float bestSpeed = player.getInventory().getStack(bestSlot).getMiningSpeedMultiplier(state);

        for (int slot = 0; slot < 9; slot++) {
            ItemStack stack = player.getInventory().getStack(slot);
            float speed = stack.getMiningSpeedMultiplier(state);
            if (speed > bestSpeed) {
                bestSpeed = speed;
                bestSlot = slot;
            }
        }
        player.getInventory().setSelectedSlot(bestSlot);
    }

    private void restore(ClientPlayerEntity player) {
        if (player != null && originalSlot >= 0) {
            player.getInventory().setSelectedSlot(originalSlot);
        }
        originalSlot = -1;
        target = null;
    }

    @Override
    protected void onDisable(MinecraftClient client) { restore(client.player); }
}
