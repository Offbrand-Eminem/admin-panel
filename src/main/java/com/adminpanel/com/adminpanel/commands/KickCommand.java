package com.adminpanel.commands;

import com.adminpanel.AdminPanelMod;
import com.adminpanel.permission.PermissionManager;
import com.adminpanel.utils.CommandUtils;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

import static net.minecraft.commands.Commands.argument;
import static net.minecraft.commands.Commands.literal;

public class KickCommand {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(literal("remove")
            .requires(source -> CommandUtils.hasAdminPermission(source))
            .then(argument("player", StringArgumentType.word())
                .executes(KickCommand::kickPlayer)
                .then(argument("reason", StringArgumentType.greedyString())
                    .executes(KickCommand::kickPlayerWithReason)
                )
            )
        );
        
        dispatcher.register(literal("removeall")
            .requires(source -> CommandUtils.hasAdminPermission(source))
            .then(argument("reason", StringArgumentType.greedyString())
                .executes(KickCommand::kickAll)
            )
        );
    }
    
    private static int kickPlayer(CommandContext<CommandSourceStack> context) {
        return kickPlayerWithReason(context, "Kicked by admin");
    }
    
    private static int kickPlayerWithReason(CommandContext<CommandSourceStack> context) {
        String reason = StringArgumentType.getString(context, "reason");
        return kickPlayerWithReason(context, reason);
    }
    
    private static int kickPlayerWithReason(CommandContext<CommandSourceStack> context, String reason) {
        String playerName = StringArgumentType.getString(context, "player");
        CommandSourceStack source = context.getSource();
        
        if (!CommandUtils.isValidPlayerName(playerName)) {
            CommandUtils.sendError(source, "Invalid player name: " + playerName);
            return 0;
        }
        
        ServerPlayer target = CommandUtils.getPlayer(source, playerName);
        
        if (target == null) {
            CommandUtils.sendError(source, "Player not found: " + playerName);
            return 0;
        }
        
        // Don't kick admins/owners unless the kicker is an owner
        if (AdminPanelMod.permissionManager.isAdmin(target)) {
            if (!AdminPanelMod.permissionManager.isOwner(source.getPlayer())) {
                CommandUtils.sendError(source, "Cannot kick other admins/owners");
                return 0;
            }
        }
        
        target.connection.disconnect(Component.nullToEmpty("§cYou have been kicked: " + reason));
        CommandUtils.sendSuccess(source, "Kicked player: " + playerName + " - Reason: " + reason);
        return 1;
    }
    
    private static int kickAll(CommandContext<CommandSourceStack> context) {
        String reason = StringArgumentType.getString(context, "reason");
        CommandSourceStack source = context.getSource();
        
        int kickedCount = 0;
        ServerPlayer kicker = source.getPlayer();
        
        for (ServerPlayer player : source.getServer().getPlayerList().getPlayers()) {
            // Skip admins/owners and the kicker
            if (AdminPanelMod.permissionManager.isAdmin(player)) continue;
            if (kicker != null && player.equals(kicker)) continue;
            
            player.connection.disconnect(Component.nullToEmpty("§cYou have been kicked: " + reason));
            kickedCount++;
        }
        
        CommandUtils.sendSuccess(source, "Kicked " + kickedCount + " players - Reason: " + reason);
        return 1;
    }
}
