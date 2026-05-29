package com.adminpanel.commands;

import com.adminpanel.AdminPanelMod;
import com.adminpanel.permission.PermissionManager;
import com.adminpanel.utils.CommandUtils;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class KickCommand {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
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
    
    private static int kickPlayer(CommandContext<ServerCommandSource> context) {
        return kickPlayerWithReason(context, "Kicked by admin");
    }
    
    private static int kickPlayerWithReason(CommandContext<ServerCommandSource> context) {
        String reason = StringArgumentType.getString(context, "reason");
        return kickPlayerWithReason(context, reason);
    }
    
    private static int kickPlayerWithReason(CommandContext<ServerCommandSource> context, String reason) {
        String playerName = StringArgumentType.getString(context, "player");
        ServerCommandSource source = context.getSource();
        
        if (!CommandUtils.isValidPlayerName(playerName)) {
            CommandUtils.sendError(source, "Invalid player name: " + playerName);
            return 0;
        }
        
        ServerPlayerEntity target = CommandUtils.getPlayer(source, playerName);
        
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
        
        target.networkHandler.disconnect(Text.of("§cYou have been kicked: " + reason));
        CommandUtils.sendSuccess(source, "Kicked player: " + playerName + " - Reason: " + reason);
        return 1;
    }
    
    private static int kickAll(CommandContext<ServerCommandSource> context) {
        String reason = StringArgumentType.getString(context, "reason");
        ServerCommandSource source = context.getSource();
        
        int kickedCount = 0;
        ServerPlayerEntity kicker = source.getPlayer();
        
        for (ServerPlayerEntity player : source.getServer().getPlayerManager().getPlayerList()) {
            // Skip admins/owners and the kicker
            if (AdminPanelMod.permissionManager.isAdmin(player)) continue;
            if (kicker != null && player.equals(kicker)) continue;
            
            player.networkHandler.disconnect(Text.of("§cYou have been kicked: " + reason));
            kickedCount++;
        }
        
        CommandUtils.sendSuccess(source, "Kicked " + kickedCount + " players - Reason: " + reason);
        return 1;
    }
}
