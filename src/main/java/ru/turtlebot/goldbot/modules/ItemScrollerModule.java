package ru.turtlebot.goldbot.modules;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import org.lwjgl.glfw.GLFW;
import ru.turtlebot.goldbot.GoldModule;
import ru.turtlebot.mixin.accessor.HandledScreenAccessor;

/** Shift + left mouse repeatedly quick-moves the slot under the cursor. */
public final class ItemScrollerModule extends GoldModule {
    private long delayMillis = 100L;
    private long nextMoveAt;

    public ItemScrollerModule() { super("ItemScroller", "Player"); }
    public void setDelayMillis(long value) { delayMillis = Math.max(1L, Math.min(1000L, value)); }

    @Override protected void onTick(MinecraftClient client) {
        if (!(client.currentScreen instanceof HandledScreen<?> screen) ||
                client.player == null || client.interactionManager == null) return;
        long window = client.getWindow().getHandle();
        if (GLFW.glfwGetMouseButton(window, GLFW.GLFW_MOUSE_BUTTON_LEFT) != GLFW.GLFW_PRESS ||
                !(GLFW.glfwGetKey(window, GLFW.GLFW_KEY_LEFT_SHIFT) == GLFW.GLFW_PRESS ||
                  GLFW.glfwGetKey(window, GLFW.GLFW_KEY_RIGHT_SHIFT) == GLFW.GLFW_PRESS)) return;
        long now = System.currentTimeMillis();
        if (now < nextMoveAt) return;
        Slot slot = ((HandledScreenAccessor) screen).turtlebot$getFocusedSlot();
        if (slot == null || !slot.hasStack()) return;
        client.interactionManager.clickSlot(client.player.currentScreenHandler.syncId,
                slot.id, 0, SlotActionType.QUICK_MOVE, client.player);
        nextMoveAt = now + delayMillis;
    }
}