package com.adminpanel.commands;

import com.adminpanel.AdminPanelMod;
import com.adminpanel.permission.PermissionManager;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundPlayerInfoRemovePacket;
import net.minecraft.server.level.ServerPlayer;

import static net.minecraft.commands.Commands.argument;
import static net.minecraft.commands.Commands.literal;

public class VanishCommand {
    private static final Map<UUID, Boolean> vanishedPlayers = new HashMap<>();

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(literal("vanish")
            .requires(source -> hasAdminPermission(source))
            .executes(VanishCommand::toggleVanishSelf)
            .then(argument("player", StringArgumentType.word())
                .executes(VanishCommand::toggleVanishOther)
            )
        );
    }

    private static boolean hasAdminPermission(CommandSourceStack source) {
        // if (source.hasPermissionLevel(4)) return true;
        if (source.getPlayer() == null) return false;
        return AdminPanelMod.permissionManager.hasPermission(
            source.getPlayer(),
            PermissionManager.PermissionLevel.ADMIN
        );
    }

    private static int toggleVanishSelf(CommandContext<CommandSourceStack> context) {
        CommandSourceStack source = context.getSource();
        if (source.getPlayer() == null) {
            source.sendFailure(Component.literal("§cThis command can only be used by players"));
            return 0;
        }
        return toggleVanish(context, source.getPlayer());
    }

    private static int toggleVanishOther(CommandContext<CommandSourceStack> context) {
        String playerName = StringArgumentType.getString(context, "player");
        CommandSourceStack source = context.getSource();
        ServerPlayer target = source.getServer().getPlayerList().getPlayerByName(playerName);
        if (target == null) {
            source.sendFailure(Component.literal("§cPlayer not found: " + playerName));
            return 0;
        }
        return toggleVanish(context, target);
    }

    private static int toggleVanish(CommandContext<CommandSourceStack> context, ServerPlayer player) {
        UUID uuid = player.getUUID();
        boolean isVanished = vanishedPlayers.getOrDefault(uuid, false);
        boolean newVanishState = !isVanished;
        vanishedPlayers.put(uuid, newVanishState);

        if (newVanishState) {
            // Remove from tab list for all other players (1.21.11 API: takes a List of UUIDs)
            context.getSource().getServer().getPlayerList().broadcastAll(
                new ClientboundPlayerInfoRemovePacket(List.of(player.getUUID()))
            );
            player.setInvisible(true);
            player.setNoGravity(true);
            player.getAbilities().invulnerable = true;
            player.onUpdateAbilities();
            context.getSource().sendSuccess(() -> Component.literal("§aYou are now vanished"), true);
            if (context.getSource().getPlayer() != null && context.getSource().getPlayer().equals(player)) {
                context.getSource().getServer().getPlayerList().broadcastSystemMessage(
                    Component.literal("§e" + player.getName().getString() + " left the game"), false);
            }
        } else {
            // Re-add to tab list by sending player list update
            context.getSource().getServer().getPlayerList().broadcastAll(
                net.minecraft.network.protocol.game.ClientboundPlayerInfoUpdatePacket.createPlayerInitializing(
                    List.of(player)
                )
            );
            player.setInvisible(false);
            player.setNoGravity(false);
            player.getAbilities().invulnerable = false;
            player.onUpdateAbilities();
            context.getSource().sendSuccess(() -> Component.literal("§aYou are no longer vanished"), true);
            if (context.getSource().getPlayer() != null && context.getSource().getPlayer().equals(player)) {
                context.getSource().getServer().getPlayerList().broadcastSystemMessage(
                    Component.literal("§e" + player.getName().getString() + " joined the game"), false);
            }
        }
        return 1;
    }

    public static boolean isVanished(UUID playerUuid) {
        return vanishedPlayers.getOrDefault(playerUuid, false);
    }

    public static void updateVanishState(ServerPlayer player) {
        if (isVanished(player.getUUID())) {
            player.setInvisible(true);
            player.setNoGravity(true);
            player.getAbilities().invulnerable = true;
            player.onUpdateAbilities();
        }
    }
}
