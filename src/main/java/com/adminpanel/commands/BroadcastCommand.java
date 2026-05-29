package com.adminpanel.commands;

import com.adminpanel.AdminPanelMod;
import com.adminpanel.permission.PermissionManager;
import com.adminpanel.utils.CommandUtils;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class BroadcastCommand {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(literal("broadcast")
            .requires(source -> CommandUtils.hasAdminPermission(source))
            .then(argument("message", StringArgumentType.greedyString())
                .executes(BroadcastCommand::broadcastMessage)
            )
        );
        
        dispatcher.register(literal("bc")
            .requires(source -> CommandUtils.hasAdminPermission(source))
            .then(argument("message", StringArgumentType.greedyString())
                .executes(BroadcastCommand::broadcastMessage)
            )
        );
        
        dispatcher.register(literal("alert")
            .requires(source -> CommandUtils.hasAdminPermission(source))
            .then(argument("message", StringArgumentType.greedyString())
                .executes(BroadcastCommand::broadcastMessage)
            )
        );
        
        dispatcher.register(literal("warn")
            .requires(source -> CommandUtils.hasAdminPermission(source))
            .then(argument("message", StringArgumentType.greedyString())
                .executes(BroadcastCommand::warnMessage)
            )
        );
    }
    
    private static int broadcastMessage(CommandContext<ServerCommandSource> context) {
        String message = StringArgumentType.getString(context, "message");
        ServerCommandSource source = context.getSource();
        
        if (message == null || message.trim().isEmpty()) {
            CommandUtils.sendError(source, "Message cannot be empty");
            return 0;
        }
        
        Text formattedMessage = Text.literal("§6[§cAlert§6] §f" + message);
        source.getServer().getPlayerManager().broadcast(formattedMessage, false);
        
        CommandUtils.sendSuccess(source, "Broadcast sent: " + message);
        return 1;
    }
    
    private static int warnMessage(CommandContext<ServerCommandSource> context) {
        String message = StringArgumentType.getString(context, "message");
        ServerCommandSource source = context.getSource();
        
        if (message == null || message.trim().isEmpty()) {
            CommandUtils.sendError(source, "Message cannot be empty");
            return 0;
        }
        
        Text formattedMessage = Text.literal("§c[§4WARNING§c] §f" + message);
        source.getServer().getPlayerManager().broadcast(formattedMessage, false);
        
        CommandUtils.sendSuccess(source, "Warning sent: " + message);
        return 1;
    }
}
