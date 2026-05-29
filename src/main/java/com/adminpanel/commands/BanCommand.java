package com.adminpanel.commands;

import com.adminpanel.AdminPanelMod;
import com.adminpanel.utils.CommandUtils;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.server.BannedPlayerList;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class BanCommand {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(literal("banish")
            .requires(source -> CommandUtils.hasAdminPermission(source))
            .then(argument("player", StringArgumentType.word())
                .executes(BanCommand::banPlayer)
                .then(argument("reason", StringArgumentType.greedyString())
                    .executes(BanCommand::banPlayerWithReason)
                )
            )
        );

        dispatcher.register(literal("pardon")
            .requires(source -> CommandUtils.hasAdminPermission(source))
            .then(argument("player", StringArgumentType.word())
                .executes(BanCommand::unbanPlayer)
            )
        );

        dispatcher.register(literal("banip")
            .requires(source -> CommandUtils.hasAdminPermission(source))
            .then(argument("player", StringArgumentType.word())
                .executes(BanCommand::banIP)
                .then(argument("reason", StringArgumentType.greedyString())
                    .executes(BanCommand::banIPWithReason)
                )
            )
        );
    }

    private static int banPlayer(CommandContext<ServerCommandSource> context) {
        return banPlayerWithReason(context, "Banned by admin");
    }

    private static int banPlayerWithReason(CommandContext<ServerCommandSource> context) {
        String reason = StringArgumentType.getString(context, "reason");
        return banPlayerWithReason(context, reason);
    }

    private static int banPlayerWithReason(CommandContext<ServerCommandSource> context, String reason) {
        String playerName = StringArgumentType.getString(context, "player");
        ServerCommandSource source = context.getSource();

        if (!CommandUtils.isValidPlayerName(playerName)) {
            CommandUtils.sendError(source, "Invalid player name: " + playerName);
            return 0;
        }

        ServerPlayerEntity target = source.getServer().getPlayerManager().getPlayer(playerName);

        BannedPlayerList banList = source.getServer().getPlayerManager().getUserBanList();

        if (target != null) {
            // Use the vanilla ban command through the command dispatcher
            try {
                source.getServer().getCommandManager().getDispatcher().execute("ban " + playerName + " " + reason, source);
            } catch (Exception e) {
                // Ignore command execution errors
            }
            AdminPanelMod.configManager.addBannedPlayer(target.getUuid());
        } else {
            CommandUtils.sendError(source, "Player not found online. Use the vanilla /ban command to ban offline players.");
            return 0;
        }

        CommandUtils.sendSuccess(source, "Banned player: " + playerName + " - Reason: " + reason);
        return 1;
    }

    private static int banIP(CommandContext<ServerCommandSource> context) {
        return banIPWithReason(context, "IP banned by admin");
    }

    private static int banIPWithReason(CommandContext<ServerCommandSource> context) {
        String reason = StringArgumentType.getString(context, "reason");
        return banIPWithReason(context, reason);
    }

    private static int banIPWithReason(CommandContext<ServerCommandSource> context, String reason) {
        String playerName = StringArgumentType.getString(context, "player");
        ServerCommandSource source = context.getSource();

        ServerPlayerEntity target = CommandUtils.getPlayer(source, playerName);

        if (target == null) {
            CommandUtils.sendError(source, "Player not found: " + playerName);
            return 0;
        }

        String ip = target.getIp();
        // Use the vanilla ban-ip command through the command dispatcher
        try {
            source.getServer().getCommandManager().getDispatcher().execute("ban-ip " + ip + " " + reason, source);
        } catch (Exception e) {
            // Ignore command execution errors
        }
        target.networkHandler.disconnect(Text.literal("§cYou have been IP banned: " + reason));

        CommandUtils.sendSuccess(source, "Banned IP: " + ip + " (Player: " + playerName + ") - Reason: " + reason);
        return 1;
    }

    private static int unbanPlayer(CommandContext<ServerCommandSource> context) {
        String playerName = StringArgumentType.getString(context, "player");
        ServerCommandSource source = context.getSource();

        // Use the vanilla pardon command through the command dispatcher
        try {
            source.getServer().getCommandManager().getDispatcher().execute("pardon " + playerName, source);
        } catch (Exception e) {
            // Ignore command execution errors
        }
        CommandUtils.sendSuccess(source, "Unbanned player: " + playerName);
        return 1;
    }
}
