package ru.turtlebot.goldbot.modules;

import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.math.BlockPos;
import ru.turtlebot.goldbot.GoldModule;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/** Finds configured valuable blocks in a small client-side scan radius. */
public final class BlockESPModule extends GoldModule {
    private static BlockESPModule instance;
    private static final Set<Block> TARGETS = Set.of(Blocks.DIAMOND_ORE, Blocks.DEEPSLATE_DIAMOND_ORE,
            Blocks.ANCIENT_DEBRIS, Blocks.GOLD_ORE, Blocks.DEEPSLATE_GOLD_ORE);
    private final List<BlockPos> found = new ArrayList<>();
    private int ticks;

    public BlockESPModule() { super("BlockESP", "Render"); instance = this; }
    public static boolean active() { return instance != null && instance.enabled(); }
    public static List<BlockPos> positions() { return instance == null ? List.of() : List.copyOf(instance.found); }
    @Override protected void onTick(MinecraftClient client) {
        if (client.player == null || client.world == null || ++ticks % 10 != 0) return;
        found.clear();
        BlockPos center = client.player.getBlockPos();
        BlockPos.Mutable pos = new BlockPos.Mutable();
        for (int x = -12; x <= 12; x++) for (int y = -8; y <= 8; y++) for (int z = -12; z <= 12; z++) {
            pos.set(center.getX() + x, center.getY() + y, center.getZ() + z);
            if (TARGETS.contains(client.world.getBlockState(pos).getBlock())) found.add(pos.toImmutable());
        }
    }
}