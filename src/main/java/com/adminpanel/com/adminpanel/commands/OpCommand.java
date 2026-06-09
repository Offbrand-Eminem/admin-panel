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

public class OpCommand {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
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
    
    private static int addAdmin(CommandContext<CommandSourceStack> context) {
        String playerName = StringArgumentType.getString(context, "player");
        CommandSourceStack source = context.getSource();
        
        if (!CommandUtils.isValidPlayerName(playerName)) {
            CommandUtils.sendError(source, "Invalid player name: " + playerName);
            return 0;
        }
        
        AdminPanelMod.configManager.addAdmin(playerName);
        CommandUtils.sendSuccess(source, "Added " + playerName + " to admin list");
        
        ServerPlayer target = source.getServer().getPlayerList().getPlayerByName(playerName);
        if (target != null) {
            target.sendSystemMessage(Component.literal("§aYou have been promoted to admin!"));
        }
        
        return 1;
    }
    
    private static int addOwner(CommandContext<CommandSourceStack> context) {
        String playerName = StringArgumentType.getString(context, "player");
        CommandSourceStack source = context.getSource();
        
        if (!CommandUtils.isValidPlayerName(playerName)) {
            CommandUtils.sendError(source, "Invalid player name: " + playerName);
            return 0;
        }
        
        AdminPanelMod.configManager.addOwner(playerName);
        CommandUtils.sendSuccess(source, "Added " + playerName + " to owner list");
        
        ServerPlayer target = source.getServer().getPlayerList().getPlayerByName(playerName);
        if (target != null) {
            target.sendSystemMessage(Component.literal("§aYou have been promoted to owner!"));
        }
        
        return 1;
    }
}
