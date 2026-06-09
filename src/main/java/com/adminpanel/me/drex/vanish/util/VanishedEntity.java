package me.drex.vanish.util;

public interface VanishedEntity {
    default boolean vanish$isVanished() {
        return false;
    }

    default void vanish$setDirty() {
    }
}
