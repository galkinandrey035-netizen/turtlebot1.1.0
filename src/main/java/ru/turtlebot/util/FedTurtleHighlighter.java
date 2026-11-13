package ru.turtlebot.util;

import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.TurtleEntity;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.Team;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import ru.turtlebot.data.TurtleRegistry;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

/** Клиентская подсветка всех черепах и таймеры над ними. */
public final class FedTurtleHighlighter {
    public static boolean enabled = true;
    public static boolean labels = true;

    private static final Set<UUID> glowing = new HashSet<>();
    private static final Set<UUID> named = new HashSet<>();
    private static final Map<UUID, String> lastText = new HashMap<>();

    private FedTurtleHighlighter() {}

    public static void apply(TurtleEntity turtle) { }

    public static boolean shouldGlow(Entity e) {
        return e.getWorld().isClient && enabled && e instanceof TurtleEntity && glowing.contains(e.getUuid());
    }

    private static Team team(Scoreboard sb, String name, Formatting color) {
        Team t = sb.getTeam(name);
        if (t == null) {
            t = sb.addTeam(name);
            t.setColor(color);
        }
        return t;
    }

    public static void tick(MinecraftClient mc, TurtleRegistry reg) {
        if (mc.world == null || mc.player == null) {
            glowing.clear(); named.clear(); lastText.clear();
            return;
        }
        Scoreboard sb = mc.world.getScoreboard();
        long now = System.currentTimeMillis();
        Set<UUID> present = new HashSet<>();

        for (Entity e : mc.world.getEntities()) {
            if (!(e instanceof TurtleEntity t) || !t.isAlive()) continue;
            UUID id = t.getUuid();
            present.add(id);

            String teamName;
            Formatting color;
            Text label;
            if (t.isBaby()) {
                teamName = "tb_baby"; color = Formatting.AQUA;
                label = Text.literal("Детёныш").formatted(color);
            } else if (t.hasEgg()) {
                teamName = "tb_egg"; color = Formatting.RED;
                label = Text.literal("С яйцом").formatted(color);
            } else {
                long left = reg.eligibleAt(id) - now;
                if (left <= 0) {
                    teamName = "tb_ready"; color = Formatting.GREEN;
                    label = Text.literal("Готова").formatted(color);
                } else {
                    long s = (left + 999) / 1000;
                    teamName = "tb_cool"; color = Formatting.YELLOW;
                    label = Text.literal(String.format("%d:%02d", s / 60, s % 60)).formatted(color);
                }
            }

            if (enabled) {
                Team want = team(sb, teamName, color);
                String key = t.getUuidAsString();
                Team cur = sb.getScoreHolderTeam(key);
                if (cur == null || !cur.getName().equals(teamName)) sb.addScoreHolderToTeam(key, want);
                glowing.add(id);
            } else if (glowing.remove(id)) {
                sb.clearTeam(t.getUuidAsString());
            }

            if (labels) {
                boolean foreignName = t.hasCustomName() && !named.contains(id);
                if (!foreignName) {
                    String text = label.getString();
                    if (!text.equals(lastText.get(id)) || !named.contains(id)) {
                        t.setCustomName(label);
                        t.setCustomNameVisible(true);
                        named.add(id);
                        lastText.put(id, text);
                    }
                }
            } else if (named.remove(id)) {
                t.setCustomName(null);
                t.setCustomNameVisible(false);
                lastText.remove(id);
            }
        }

        glowing.retainAll(present);
        named.retainAll(present);
        lastText.keySet().retainAll(present);
    }
}
