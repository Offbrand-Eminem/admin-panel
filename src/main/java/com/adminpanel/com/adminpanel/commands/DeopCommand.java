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

public class DeopCommand {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(literal("removeadmin")
            .requires(source -> CommandUtils.hasOwnerPermission(source))
            .then(argument("player", StringArgumentType.word())
                .executes(DeopCommand::removeAdmin)
            )
        );
        
        dispatcher.register(literal("removeowner")
            .requires(source -> CommandUtils.hasOwnerPermission(source))
            .then(argument("player", StringArgumentType.word())
                .executes(DeopCommand::removeOwner)
            )
        );
    }
    
    private static int removeAdmin(CommandContext<CommandSourceStack> context) {
        String playerName = StringArgumentType.getString(context, "player");
        CommandSourceStack source = context.getSource();
        
        if (!CommandUtils.isValidPlayerName(playerName)) {
            CommandUtils.sendError(source, "Invalid player name: " + playerName);
            return 0;
        }
        
        if (!AdminPanelMod.configManager.isAdmin(playerName) || AdminPanelMod.configManager.isOwner(playerName)) {
            CommandUtils.sendError(source, "Player " + playerName + " is not an admin");
            return 0;
        }
        
        AdminPanelMod.configManager.removeAdmin(playerName);
        CommandUtils.sendSuccess(source, "Removed " + playerName + " from admin list");
        
        ServerPlayer target = source.getServer().getPlayerList().getPlayerByName(playerName);
        if (target != null) {
            target.sendSystemMessage(Component.literal("§cYou have been demoted from admin"));
        }
        
        return 1;
    }
    
    private static int removeOwner(CommandContext<CommandSourceStack> context) {
        String playerName = StringArgumentType.getString(context, "player");
        CommandSourceStack source = context.getSource();
        
        if (!CommandUtils.isValidPlayerName(playerName)) {
            CommandUtils.sendError(source, "Invalid player name: " + playerName);
            return 0;
        }
        
        if (!AdminPanelMod.configManager.isOwner(playerName)) {
            CommandUtils.sendError(source, "Player " + playerName + " is not an owner");
            return 0;
        }
        
        AdminPanelMod.configManager.removeOwner(playerName);
        CommandUtils.sendSuccess(source, "Removed " + playerName + " from owner list");
        
        ServerPlayer target = source.getServer().getPlayerList().getPlayerByName(playerName);
        if (target != null) {
            target.sendSystemMessage(Component.literal("§cYou have been demoted from owner"));
        }
        
        return 1;
    }
}
