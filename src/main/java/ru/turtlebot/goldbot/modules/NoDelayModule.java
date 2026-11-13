package ru.turtlebot.goldbot.modules;

import net.minecraft.client.MinecraftClient;
import ru.turtlebot.goldbot.GoldModule;
import ru.turtlebot.mixin.accessor.ClientPlayerInteractionManagerAccessor;
import ru.turtlebot.mixin.accessor.LivingEntityAccessor;
import ru.turtlebot.mixin.accessor.MinecraftClientAccessor;

/** Removes selected vanilla client-side delays. */
public final class NoDelayModule extends GoldModule {
    private boolean jump = true;
    private boolean rightClick = true;
    private boolean breakCooldown = true;

    public NoDelayModule() { super("NoDelay", "Player"); }

    public void setJump(boolean value) { jump = value; }
    public void setRightClick(boolean value) { rightClick = value; }
    public void setBreakCooldown(boolean value) { breakCooldown = value; }

    @Override protected void onTick(MinecraftClient client) {
        if (client.player == null) return;
        if (jump) ((LivingEntityAccessor) client.player).turtlebot$setJumpingCooldown(0);
        if (rightClick) ((MinecraftClientAccessor) client).turtlebot$setItemUseCooldown(0);
        if (breakCooldown && client.interactionManager != null)
            ((ClientPlayerInteractionManagerAccessor) client.interactionManager).turtlebot$setBlockBreakingCooldown(0);
    }
}