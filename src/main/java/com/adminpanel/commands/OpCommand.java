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

public class OpCommand {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(literal("setadmin")
            .requires(source -> CommandUtils.hasOwnerPermission(source))
            .then(argument("player", StringArgumentType.word())
                .executes(OpCommand::addAdmin)
            )
        );
        
        dispatcher.register(literal("setowner")
            .requires(source -> CommandUtils.hasOwnerPermission(source))
            .then(argument("player", StringArgumentType.word())
                .executes(OpCommand::addOwner)
            )
        );
    }
    
    private static int addAdmin(CommandContext<ServerCommandSource> context) {
        String playerName = StringArgumentType.getString(context, "player");
        ServerCommandSource source = context.getSource();
        
        if (!CommandUtils.isValidPlayerName(playerName)) {
            CommandUtils.sendError(source, "Invalid player name: " + playerName);
            return 0;
        }
        
        AdminPanelMod.configManager.addAdmin(playerName);
        CommandUtils.sendSuccess(source, "Added " + playerName + " to admin list");
        
        ServerPlayerEntity target = source.getServer().getPlayerManager().getPlayer(playerName);
        if (target != null) {
            target.sendMessage(Text.literal("§aYou have been promoted to admin!"));
        }
        
        return 1;
    }
    
    private static int addOwner(CommandContext<ServerCommandSource> context) {
        String playerName = StringArgumentType.getString(context, "player");
        ServerCommandSource source = context.getSource();
        
        if (!CommandUtils.isValidPlayerName(playerName)) {
            CommandUtils.sendError(source, "Invalid player name: " + playerName);
            return 0;
        }
        
        AdminPanelMod.configManager.addOwner(playerName);
        CommandUtils.sendSuccess(source, "Added " + playerName + " to owner list");
        
        ServerPlayerEntity target = source.getServer().getPlayerManager().getPlayer(playerName);
        if (target != null) {
            target.sendMessage(Text.literal("§aYou have been promoted to owner!"));
        }
        
        return 1;
    }
}
