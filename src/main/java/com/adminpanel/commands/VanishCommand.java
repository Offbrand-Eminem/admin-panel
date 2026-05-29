package com.adminpanel.commands;

import com.adminpanel.AdminPanelMod;
import com.adminpanel.permission.PermissionManager;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.network.packet.s2c.play.PlayerRemoveS2CPacket;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class VanishCommand {
    private static final Map<UUID, Boolean> vanishedPlayers = new HashMap<>();

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(literal("vanish")
            .requires(source -> hasAdminPermission(source))
            .executes(VanishCommand::toggleVanishSelf)
            .then(argument("player", StringArgumentType.word())
                .executes(VanishCommand::toggleVanishOther)
            )
        );
    }

    private static boolean hasAdminPermission(ServerCommandSource source) {
        // if (source.hasPermissionLevel(4)) return true;
        if (source.getPlayer() == null) return false;
        return AdminPanelMod.permissionManager.hasPermission(
            source.getPlayer(),
            PermissionManager.PermissionLevel.ADMIN
        );
    }

    private static int toggleVanishSelf(CommandContext<ServerCommandSource> context) {
        ServerCommandSource source = context.getSource();
        if (source.getPlayer() == null) {
            source.sendError(Text.literal("§cThis command can only be used by players"));
            return 0;
        }
        return toggleVanish(context, source.getPlayer());
    }

    private static int toggleVanishOther(CommandContext<ServerCommandSource> context) {
        String playerName = StringArgumentType.getString(context, "player");
        ServerCommandSource source = context.getSource();
        ServerPlayerEntity target = source.getServer().getPlayerManager().getPlayer(playerName);
        if (target == null) {
            source.sendError(Text.literal("§cPlayer not found: " + playerName));
            return 0;
        }
        return toggleVanish(context, target);
    }

    private static int toggleVanish(CommandContext<ServerCommandSource> context, ServerPlayerEntity player) {
        UUID uuid = player.getUuid();
        boolean isVanished = vanishedPlayers.getOrDefault(uuid, false);
        boolean newVanishState = !isVanished;
        vanishedPlayers.put(uuid, newVanishState);

        if (newVanishState) {
            // Remove from tab list for all other players (1.21.11 API: takes a List of UUIDs)
            context.getSource().getServer().getPlayerManager().sendToAll(
                new PlayerRemoveS2CPacket(List.of(player.getUuid()))
            );
            player.setInvisible(true);
            player.setNoGravity(true);
            player.getAbilities().invulnerable = true;
            player.sendAbilitiesUpdate();
            context.getSource().sendFeedback(() -> Text.literal("§aYou are now vanished"), true);
            if (context.getSource().getPlayer() != null && context.getSource().getPlayer().equals(player)) {
                context.getSource().getServer().getPlayerManager().broadcast(
                    Text.literal("§e" + player.getName().getString() + " left the game"), false);
            }
        } else {
            // Re-add to tab list by sending player list update
            context.getSource().getServer().getPlayerManager().sendToAll(
                net.minecraft.network.packet.s2c.play.PlayerListS2CPacket.entryFromPlayer(
                    List.of(player)
                )
            );
            player.setInvisible(false);
            player.setNoGravity(false);
            player.getAbilities().invulnerable = false;
            player.sendAbilitiesUpdate();
            context.getSource().sendFeedback(() -> Text.literal("§aYou are no longer vanished"), true);
            if (context.getSource().getPlayer() != null && context.getSource().getPlayer().equals(player)) {
                context.getSource().getServer().getPlayerManager().broadcast(
                    Text.literal("§e" + player.getName().getString() + " joined the game"), false);
            }
        }
        return 1;
    }

    public static boolean isVanished(UUID playerUuid) {
        return vanishedPlayers.getOrDefault(playerUuid, false);
    }

    public static void updateVanishState(ServerPlayerEntity player) {
        if (isVanished(player.getUuid())) {
            player.setInvisible(true);
            player.setNoGravity(true);
            player.getAbilities().invulnerable = true;
            player.sendAbilitiesUpdate();
        }
    }
}
