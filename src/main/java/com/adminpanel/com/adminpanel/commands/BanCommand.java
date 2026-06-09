package com.adminpanel.commands;

import com.adminpanel.AdminPanelMod;
import com.adminpanel.utils.CommandUtils;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.UserBanList;

import static net.minecraft.commands.Commands.argument;
import static net.minecraft.commands.Commands.literal;

public class BanCommand {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
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

    private static int banPlayer(CommandContext<CommandSourceStack> context) {
        return banPlayerWithReason(context, "Banned by admin");
    }

    private static int banPlayerWithReason(CommandContext<CommandSourceStack> context) {
        String reason = StringArgumentType.getString(context, "reason");
        return banPlayerWithReason(context, reason);
    }

    private static int banPlayerWithReason(CommandContext<CommandSourceStack> context, String reason) {
        String playerName = StringArgumentType.getString(context, "player");
        CommandSourceStack source = context.getSource();

        if (!CommandUtils.isValidPlayerName(playerName)) {
            CommandUtils.sendError(source, "Invalid player name: " + playerName);
            return 0;
        }

        ServerPlayer target = source.getServer().getPlayerList().getPlayerByName(playerName);

        UserBanList banList = source.getServer().getPlayerList().getBans();

        if (target != null) {
            // Use the vanilla ban command through the command dispatcher
            try {
                source.getServer().getCommands().getDispatcher().execute("ban " + playerName + " " + reason, source);
            } catch (Exception e) {
                // Ignore command execution errors
            }
            AdminPanelMod.configManager.addBannedPlayer(target.getUUID());
        } else {
            CommandUtils.sendError(source, "Player not found online. Use the vanilla /ban command to ban offline players.");
            return 0;
        }

        CommandUtils.sendSuccess(source, "Banned player: " + playerName + " - Reason: " + reason);
        return 1;
    }

    private static int banIP(CommandContext<CommandSourceStack> context) {
        return banIPWithReason(context, "IP banned by admin");
    }

    private static int banIPWithReason(CommandContext<CommandSourceStack> context) {
        String reason = StringArgumentType.getString(context, "reason");
        return banIPWithReason(context, reason);
    }

    private static int banIPWithReason(CommandContext<CommandSourceStack> context, String reason) {
        String playerName = StringArgumentType.getString(context, "player");
        CommandSourceStack source = context.getSource();

        ServerPlayer target = CommandUtils.getPlayer(source, playerName);

        if (target == null) {
            CommandUtils.sendError(source, "Player not found: " + playerName);
            return 0;
        }

        String ip = target.getIpAddress();
        // Use the vanilla ban-ip command through the command dispatcher
        try {
            source.getServer().getCommands().getDispatcher().execute("ban-ip " + ip + " " + reason, source);
        } catch (Exception e) {
            // Ignore command execution errors
        }
        target.connection.disconnect(Component.literal("§cYou have been IP banned: " + reason));

        CommandUtils.sendSuccess(source, "Banned IP: " + ip + " (Player: " + playerName + ") - Reason: " + reason);
        return 1;
    }

    private static int unbanPlayer(CommandContext<CommandSourceStack> context) {
        String playerName = StringArgumentType.getString(context, "player");
        CommandSourceStack source = context.getSource();

        // Use the vanilla pardon command through the command dispatcher
        try {
            source.getServer().getCommands().getDispatcher().execute("pardon " + playerName, source);
        } catch (Exception e) {
            // Ignore command execution errors
        }
        CommandUtils.sendSuccess(source, "Unbanned player: " + playerName);
        return 1;
    }
}
