package ru.turtlebot.state;

import net.minecraft.client.MinecraftClient;
import org.lwjgl.glfw.GLFW;
import ru.turtlebot.config.ConfigManager;

import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

public final class MacroController {
    private final Set<String> down = new HashSet<>();

    public void tick(MinecraftClient mc) {
        if (mc.player == null || mc.getNetworkHandler() == null || mc.currentScreen != null) {
            down.clear();
            return;
        }
        long window = mc.getWindow().getHandle();
        for (var entry : ConfigManager.get().macros.entrySet()) {
            String key = entry.getKey().toUpperCase(Locale.ROOT);
            int code = parseKey(key);
            if (code < 0) continue;
            boolean pressed = code <= -1
                    ? GLFW.glfwGetMouseButton(window, -code - 1) == GLFW.GLFW_PRESS
                    : GLFW.glfwGetKey(window, code) == GLFW.GLFW_PRESS;
            if (pressed && down.add(key)) {
                String value = entry.getValue();
                if (value == null || value.isBlank()) continue;
                if (value.startsWith("/")) mc.getNetworkHandler().sendChatCommand(value.substring(1));
                else mc.getNetworkHandler().sendChatMessage(value);
            } else if (!pressed) {
                down.remove(key);
            }
        }
    }

    public static int parseKey(String name) {
        if (name == null) return -1;
        String key = name.trim().toUpperCase(Locale.ROOT);
        if (key.length() == 1 && key.charAt(0) >= 'A' && key.charAt(0) <= 'Z') return GLFW.GLFW_KEY_A + key.charAt(0) - 'A';
        if (key.length() == 1 && key.charAt(0) >= '0' && key.charAt(0) <= '9') return GLFW.GLFW_KEY_0 + key.charAt(0) - '0';
        if (key.matches("F([1-9]|1[0-9]|2[0-5])")) return GLFW.GLFW_KEY_F1 + Integer.parseInt(key.substring(1)) - 1;
        if (key.matches("NUMPAD_[0-9]")) return GLFW.GLFW_KEY_KP_0 + Integer.parseInt(key.substring(7));
        return switch (key) {
            case "SPACE" -> GLFW.GLFW_KEY_SPACE;
            case "TAB" -> GLFW.GLFW_KEY_TAB;
            case "ENTER" -> GLFW.GLFW_KEY_ENTER;
            case "INSERT" -> GLFW.GLFW_KEY_INSERT;
            case "DELETE" -> GLFW.GLFW_KEY_DELETE;
            case "HOME" -> GLFW.GLFW_KEY_HOME;
            case "END" -> GLFW.GLFW_KEY_END;
            case "PAGEUP" -> GLFW.GLFW_KEY_PAGE_UP;
            case "PAGEDOWN" -> GLFW.GLFW_KEY_PAGE_DOWN;
            case "UP" -> GLFW.GLFW_KEY_UP;
            case "DOWN" -> GLFW.GLFW_KEY_DOWN;
            case "LEFT" -> GLFW.GLFW_KEY_LEFT;
            case "RIGHT" -> GLFW.GLFW_KEY_RIGHT;
            case "M4" -> -GLFW.GLFW_MOUSE_BUTTON_4 - 1;
            case "M5" -> -GLFW.GLFW_MOUSE_BUTTON_5 - 1;
            default -> -1;
        };
    }
}
