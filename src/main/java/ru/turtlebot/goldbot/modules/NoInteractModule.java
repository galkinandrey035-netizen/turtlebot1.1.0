package ru.turtlebot.goldbot.modules;

import net.minecraft.block.BlockState;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.client.MinecraftClient;
import ru.turtlebot.goldbot.GoldModule;

public final class NoInteractModule extends GoldModule {
    public NoInteractModule() { super("NoInteract", "Combat"); }

    public static boolean shouldBlock(BlockState state) {
        return state.isIn(BlockTags.DOORS) || state.isIn(BlockTags.TRAPDOORS) ||
                state.isIn(BlockTags.BUTTONS) || state.isIn(BlockTags.FENCE_GATES) ||
                state.isIn(BlockTags.PRESSURE_PLATES) || state.isIn(BlockTags.SHULKER_BOXES);
    }

    @Override
    protected void onTick(MinecraftClient client) {}
}
