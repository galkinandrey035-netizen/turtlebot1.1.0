package ru.turtlebot.goldbot.modules;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.GenericContainerScreen;
import net.minecraft.item.ItemStack;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.screen.slot.Slot;
import net.minecraft.text.Text;
import ru.turtlebot.goldbot.GoldModule;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class AuctionHelperModule extends GoldModule {
    private static AuctionHelperModule instance;
    private static final Pattern NUMBER = Pattern.compile("(?<!\\d)(\\d[\\d ,._]{2,})");
    private Slot cheapest;
    private long bestPrice = Long.MAX_VALUE;
    private int ticks;
    public AuctionHelperModule() { super("AuctionHelper", "Render"); instance = this; }
    public static boolean active() { return instance != null && instance.enabled(); }
    public static boolean isCheapest(Slot slot) { return active() && instance.cheapest == slot; }
    @Override protected void onTick(MinecraftClient client) {
        if (!(client.currentScreen instanceof GenericContainerScreen) || client.player == null ||
                ++ticks % 5 != 0) { if (!(client.currentScreen instanceof GenericContainerScreen)) cheapest = null; return; }
        cheapest = null;
        bestPrice = Long.MAX_VALUE;
        for (Slot slot : client.player.currentScreenHandler.slots) {
            if (!slot.hasStack() || slot.inventory == client.player.getInventory()) continue;
            long price = extractPrice(slot.getStack());
            if (price > 0 && price < bestPrice) { bestPrice = price; cheapest = slot; }
        }
    }
    private static long extractPrice(ItemStack stack) {
        StringBuilder text = new StringBuilder(stack.getName().getString());
        var lore = stack.get(DataComponentTypes.LORE);
        if (lore != null) for (Text line : lore.lines()) text.append(' ').append(line.getString());
        Matcher matcher = NUMBER.matcher(text);
        long best = Long.MAX_VALUE;
        while (matcher.find()) {
            String digits = matcher.group(1).replaceAll("[^0-9]", "");
            try {
                long value = Long.parseLong(digits);
                if (value > 0) best = Math.min(best, value);
            } catch (NumberFormatException ignored) {}
        }
        return best == Long.MAX_VALUE ? -1 : best;
    }
}