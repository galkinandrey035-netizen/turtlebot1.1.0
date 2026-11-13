package ru.turtlebot.goldbot.modules;

import net.minecraft.client.MinecraftClient;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;
import ru.turtlebot.goldbot.GoldModule;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class WaypointModule extends GoldModule {
    private static WaypointModule instance;
    public record Waypoint(String name, Vec3d position, Identifier dimension) {}
    private final Map<String, Waypoint> waypoints = new ConcurrentHashMap<>();
    public WaypointModule() { super("Waypoint", "Render"); instance = this; }
    public static boolean active() { return instance != null && instance.enabled(); }
    public static Map<String, Waypoint> all() { return instance == null ? Map.of() : Map.copyOf(instance.waypoints); }
    public void add(String name, Vec3d position, Identifier dimension) {
        waypoints.put(name.toLowerCase(java.util.Locale.ROOT), new Waypoint(name, position, dimension));
    }
    public void remove(String name) { waypoints.remove(name.toLowerCase(java.util.Locale.ROOT)); }
    @Override protected void onTick(MinecraftClient client) {}
}