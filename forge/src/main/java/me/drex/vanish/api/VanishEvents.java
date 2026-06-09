package me.drex.vanish.api;

import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

public final class VanishEvents {
    private VanishEvents() {
    }

    public interface VanishEvent {
        void onVanish(ServerPlayer player, boolean vanish);
    }

    public interface JoinEvent {
        boolean onJoin(ServerPlayer player);
    }

    public interface VanishMessageEvent {
        Component getVanishMessage(ServerPlayer player);
    }

    public interface UnVanishMessageEvent {
        Component getUnVanishMessage(ServerPlayer player);
    }
}
