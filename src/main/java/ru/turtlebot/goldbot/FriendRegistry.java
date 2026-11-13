package ru.turtlebot.goldbot;

import java.util.Locale;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public final class FriendRegistry {
    private static final Set<String> FRIENDS = ConcurrentHashMap.newKeySet();

    private FriendRegistry() {}

    public static boolean add(String name) {
        if (name == null || name.isBlank()) return false;
        return FRIENDS.add(name.trim().toLowerCase(Locale.ROOT));
    }

    public static boolean remove(String name) {
        return name != null && FRIENDS.remove(name.trim().toLowerCase(Locale.ROOT));
    }

    public static boolean isFriend(String name) {
        return name != null && FRIENDS.contains(name.trim().toLowerCase(Locale.ROOT));
    }

    public static Set<String> all() {
        return Set.copyOf(FRIENDS);
    }
}
