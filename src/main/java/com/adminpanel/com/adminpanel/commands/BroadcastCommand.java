package com.adminpanel.commands;

import com.adminpanel.AdminPanelMod;
import com.adminpanel.permission.PermissionManager;
import com.adminpanel.utils.CommandUtils;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;

import static net.minecraft.commands.Commands.argument;
import static net.minecraft.commands.Commands.literal;

public class BroadcastCommand {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
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
    
    private static int broadcastMessage(CommandContext<CommandSourceStack> context) {
        String message = StringArgumentType.getString(context, "message");
        CommandSourceStack source = context.getSource();
        
        if (message == null || message.trim().isEmpty()) {
            CommandUtils.sendError(source, "Message cannot be empty");
            return 0;
        }
        
        Component formattedMessage = Component.literal("§6[§cAlert§6] §f" + message);
        source.getServer().getPlayerList().broadcastSystemMessage(formattedMessage, false);
        
        CommandUtils.sendSuccess(source, "Broadcast sent: " + message);
        return 1;
    }
    
    private static int warnMessage(CommandContext<CommandSourceStack> context) {
        String message = StringArgumentType.getString(context, "message");
        CommandSourceStack source = context.getSource();
        
        if (message == null || message.trim().isEmpty()) {
            CommandUtils.sendError(source, "Message cannot be empty");
            return 0;
        }
        
        Component formattedMessage = Component.literal("§c[§4WARNING§c] §f" + message);
        source.getServer().getPlayerList().broadcastSystemMessage(formattedMessage, false);
        
        CommandUtils.sendSuccess(source, "Warning sent: " + message);
        return 1;
    }
}
