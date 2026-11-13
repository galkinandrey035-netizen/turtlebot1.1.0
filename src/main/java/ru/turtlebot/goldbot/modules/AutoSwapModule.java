package ru.turtlebot.goldbot.modules;

import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.screen.slot.SlotActionType;
import org.lwjgl.glfw.GLFW;
import ru.turtlebot.goldbot.GoldModule;

public final class AutoSwapModule extends GoldModule {
    private static final KeyBinding SWAP_KEY = KeyBindingHelper.registerKeyBinding(
            new KeyBinding("key.turtlebot.autoswap", InputUtil.Type.KEYSYM,
                    GLFW.GLFW_KEY_H, "category.turtlebot"));

    public AutoSwapModule() { super("AutoSwap", "Combat"); }

    @Override
    protected void onTick(MinecraftClient client) {
        if (client.player == null || client.interactionManager == null ||
                client.currentScreen != null || !SWAP_KEY.wasPressed()) return;

        Item target = client.player.getOffHandStack().isOf(Items.TOTEM_OF_UNDYING)
                ? Items.GOLDEN_APPLE : Items.TOTEM_OF_UNDYING;
        int inventoryIndex = -1;
        for (int i = 0; i < 36; i++) {
            if (client.player.getInventory().getStack(i).isOf(target)) {
                inventoryIndex = i;
                break;
            }
        }
        if (inventoryIndex < 0) return;

        int handlerSlot = inventoryIndex < 9 ? inventoryIndex + 36 : inventoryIndex;
        client.interactionManager.clickSlot(client.player.playerScreenHandler.syncId,
                handlerSlot, 40, SlotActionType.SWAP, client.player);
    }
}
