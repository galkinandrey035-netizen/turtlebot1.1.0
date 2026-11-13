package ru.turtlebot.state;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;
import ru.turtlebot.config.BotConfig;
import ru.turtlebot.config.ConfigManager;
import ru.turtlebot.util.BotLog;

import java.util.Locale;

/**
 * Independent AutoLeave/AutoJoin loop. It is intentionally separate from BotController,
 * so a disconnect or a completed turtle-feeding run does not disable these features.
 */
public final class AutoSafetyController {
    private long nextLeaveAllowedAt;
    private long joinAt;
    private boolean joinPending;

    public void tick(MinecraftClient mc) {
        if (mc.player == null || mc.world == null || mc.getNetworkHandler() == null) {
            // Keep a pending join while the client is loading the hub, but do not send
            // commands until the player has a world and a network handler again.
            return;
        }

        long now = System.currentTimeMillis();
        BotConfig cfg = ConfigManager.get();

        if (joinPending && now >= joinAt) {
            String command = normalizeCommand(cfg.autoJoinCommand);
            if (!command.isEmpty()) {
                mc.getNetworkHandler().sendChatCommand(command);
                BotLog.info("AutoJoin: отправлена команда /" + command);
            }
            joinPending = false;
            // Prevent immediate re-triggering on players visible in the hub.
            nextLeaveAllowedAt = now + 30_000L;
        }

        if (!cfg.autoLeaveEnabled || now < nextLeaveAllowedAt) return;

        String trigger = findTrigger(mc, cfg);
        if (trigger == null) return;

        BotLog.warn("AutoLeave: " + trigger);
        nextLeaveAllowedAt = now + Math.max(30_000L, (cfg.autoJoinDelaySeconds + 30L) * 1000L);

        if (cfg.autoLeaveToHub) {
            mc.getNetworkHandler().sendChatCommand("hub");
            BotLog.info("AutoLeave: отправлена команда /hub");
            if (!normalizeCommand(cfg.autoJoinCommand).isEmpty()) {
                joinPending = true;
                joinAt = now + cfg.autoJoinDelaySeconds * 1000L;
            }
        } else {
            mc.getNetworkHandler().getConnection().disconnect(Text.literal("AutoLeave"));
            joinPending = false;
        }
    }

    private String findTrigger(MinecraftClient mc, BotConfig cfg) {
        ClientPlayerEntity self = mc.player;
        double maxDistance = cfg.autoLeaveMaxDistance;
        for (PlayerEntity other : mc.world.getPlayers()) {
            if (other == self || !other.isAlive()) continue;
            double distance = self.distanceTo(other);
            if (distance > maxDistance) continue;

            String display = other.getDisplayName().getString();
            String playerName = other.getName().getString();
            if (containsName(cfg.friendNames, playerName)) continue;
            if (cfg.autoLeaveStaff && (containsName(cfg.staffNames, playerName) || looksLikeStaff(display))) {
                return "обнаружен возможный сотрудник " + display
                        + " на расстоянии " + String.format(Locale.ROOT, "%.1f", distance) + " м";
            }
            if (cfg.autoLeavePlayers) {
                return "обнаружен игрок " + other.getName().getString()
                        + " на расстоянии " + String.format(Locale.ROOT, "%.1f", distance) + " м";
            }
        }
        return null;
    }

    private boolean containsName(java.util.List<String> names, String playerName) {
        if (names == null || playerName == null) return false;
        for (String name : names) {
            if (name != null && !name.isBlank() && name.equalsIgnoreCase(playerName)) return true;
        }
        return false;
    }

    private boolean looksLikeStaff(String displayName) {
        String value = displayName.toLowerCase(Locale.ROOT);
        for (String prefix : ConfigManager.get().staffPrefixes) {
            if (prefix != null && !prefix.isBlank() && value.contains(prefix.toLowerCase(Locale.ROOT))) return true;
        }
        String[] markers = {
                "админ", "модер", "хелпер", "куратор", "владелец", "сотрудник",
                "admin", "moder", "helper", "staff", "owner", "curator"
        };
        for (String marker : markers) {
            if (value.contains(marker)) return true;
        }
        return false;
    }

    private String normalizeCommand(String value) {
        if (value == null) return "";
        String command = value.trim();
        while (command.startsWith("/")) command = command.substring(1);
        return command.replace("\\r", "").replace("\\n", "");
    }
}
