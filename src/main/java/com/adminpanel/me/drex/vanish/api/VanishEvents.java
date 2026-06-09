package me.drex.vanish.api;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.fabricmc.fabric.api.util.TriState;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

public final class VanishEvents {
    public static final Event<VanishEvent> VANISH_EVENT = EventFactory.createArrayBacked(
        VanishEvent.class,
        callbacks -> (player, vanish) -> {
            for (VanishEvent callback : callbacks) {
                callback.onVanish(player, vanish);
            }
        }
    );

    public static final Event<JoinEvent> JOIN_EVENT = EventFactory.createArrayBacked(
        JoinEvent.class,
        callbacks -> player -> {
            for (JoinEvent callback : callbacks) {
                TriState result = callback.onJoin(player);
                if (result != TriState.DEFAULT) {
                    return result;
                }
            }
            return TriState.DEFAULT;
        }
    );

    public static final Event<VanishMessageEvent> VANISH_MESSAGE_EVENT = EventFactory.createArrayBacked(
        VanishMessageEvent.class,
        callbacks -> player -> Component.empty()
    );

    public static final Event<UnVanishMessageEvent> UN_VANISH_MESSAGE_EVENT = EventFactory.createArrayBacked(
        UnVanishMessageEvent.class,
        callbacks -> player -> Component.empty()
    );

    private VanishEvents() {
    }

    @FunctionalInterface
    public interface VanishEvent {
        void onVanish(ServerPlayer player, boolean vanish);
    }

    @FunctionalInterface
    public interface JoinEvent {
        TriState onJoin(ServerPlayer player);
    }

    @FunctionalInterface
    public interface VanishMessageEvent {
        Component getVanishMessage(ServerPlayer player);
    }

    @FunctionalInterface
    public interface UnVanishMessageEvent {
        Component getUnVanishMessage(ServerPlayer player);
    }
}
