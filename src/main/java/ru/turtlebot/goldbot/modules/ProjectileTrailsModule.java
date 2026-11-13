package ru.turtlebot.goldbot.modules;

import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.util.math.Vec3d;
import ru.turtlebot.goldbot.GoldModule;

import java.util.ArrayDeque;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class ProjectileTrailsModule extends GoldModule {
    private static ProjectileTrailsModule instance;
    private final Map<Integer, ArrayDeque<Vec3d>> trails = new ConcurrentHashMap<>();
    public ProjectileTrailsModule() { super("ProjectileTrails", "Render"); instance = this; }
    public static boolean active() { return instance != null && instance.enabled(); }
    public static Map<Integer, ArrayDeque<Vec3d>> trails() { return instance == null ? Map.of() : instance.trails; }
    @Override protected void onTick(MinecraftClient client) {
        if (client.world == null) { trails.clear(); return; }
        if (!enabled()) { trails.clear(); return; }
        for (var entity : client.world.getEntities()) {
            if (!(entity instanceof ProjectileEntity projectile)) continue;
            ArrayDeque<Vec3d> points = trails.computeIfAbsent(projectile.getId(), id -> new ArrayDeque<>());
            points.addLast(projectile.getPos());
            while (points.size() > 24) points.removeFirst();
        }
        trails.keySet().removeIf(id -> client.world.getEntityById(id) == null);
    }
}