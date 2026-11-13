package ru.turtlebot.goldbot;

import net.minecraft.client.MinecraftClient;

public abstract class GoldModule {
    private final String name;
    private final String category;
    private boolean enabled;

    protected GoldModule(String name, String category) {
        this.name = name;
        this.category = category;
    }

    public final String name() { return name; }
    public final String category() { return category; }
    public final boolean enabled() { return enabled; }

    public final void setEnabled(boolean value, MinecraftClient client) {
        if (enabled == value) return;
        enabled = value;
        if (value) onEnable(client); else onDisable(client);
    }

    public final void toggle(MinecraftClient client) { setEnabled(!enabled, client); }
    public final void tick(MinecraftClient client) { if (enabled) onTick(client); }

    protected void onEnable(MinecraftClient client) {}
    protected void onDisable(MinecraftClient client) {}
    protected abstract void onTick(MinecraftClient client);
}
