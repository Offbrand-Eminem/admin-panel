package me.drex.vanish.api;

import com.adminpanel.commands.VanishCommand;
import com.adminpanel.utils.CommandUtils;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.UUID;

public interface VanishAPI {
    static boolean isVanished(@NotNull Entity entity) {
        return entity instanceof ServerPlayer player && isVanished(player);
    }

    static boolean isVanished(@NotNull ServerPlayer player) {
        return VanishCommand.isVanished(player.getUUID());
    }

    static boolean setVanish(@NotNull ServerPlayer player, boolean status) {
        boolean current = isVanished(player);
        if (current == status) {
            return false;
        }
        player.displayClientMessage(Component.literal("Use /vanish to toggle vanish state."), false);
        return false;
    }

    static boolean canSeePlayer(@NotNull ServerPlayer actor, @NotNull ServerPlayer observer) {
        return !isVanished(actor) || canViewVanished(observer);
    }

    static boolean canSeePlayer(@NotNull MinecraftServer server, @NotNull UUID uuid, @NotNull ServerPlayer observer) {
        ServerPlayer actor = server.getPlayerList().getPlayer(uuid);
        return actor == null || canSeePlayer(actor, observer);
    }

    static boolean canSeePlayer(@NotNull MinecraftServer server, @NotNull UUID uuid, @NotNull CommandSourceStack observer) {
        ServerPlayer player = observer.getPlayer();
        return player == null || canSeePlayer(server, uuid, player);
    }

    static boolean canViewVanished(ServerPlayer observer) {
        return observer != null && CommandUtils.hasAdminPermission(observer.createCommandSourceStack());
    }

    static boolean canViewVanished(CommandSourceStack observer) {
        return CommandUtils.hasAdminPermission(observer);
    }

    static @NotNull List<ServerPlayer> getVisiblePlayers(@NotNull ServerPlayer observer) {
        return observer.createCommandSourceStack().getServer().getPlayerList().getPlayers().stream()
            .filter(player -> canSeePlayer(player, observer))
            .toList();
    }

    static @NotNull List<ServerPlayer> getVisiblePlayers(@NotNull CommandSourceStack observer) {
        ServerPlayer player = observer.getPlayer();
        if (player == null) {
            return observer.getServer().getPlayerList().getPlayers();
        }
        return getVisiblePlayers(player);
    }

    static @NotNull List<ServerPlayer> getViewingPlayers(@NotNull ServerPlayer actor) {
        return actor.createCommandSourceStack().getServer().getPlayerList().getPlayers().stream()
            .filter(observer -> canSeePlayer(actor, observer))
            .toList();
    }

    static void broadcastHiddenMessage(@NotNull ServerPlayer actor, @NotNull Component message) {
        MutableComponent component = message.copy().append(Component.literal(" (hidden)"));
        for (ServerPlayer observer : getViewingPlayers(actor)) {
            observer.displayClientMessage(component, false);
        }
    }

    static void sendHiddenMessage(@NotNull ServerPlayer actor, @NotNull ServerPlayer observer, @NotNull Component message) {
        if (canSeePlayer(actor, observer)) {
            observer.displayClientMessage(message.copy().append(Component.literal(" (hidden)")), false);
        }
    }
}
