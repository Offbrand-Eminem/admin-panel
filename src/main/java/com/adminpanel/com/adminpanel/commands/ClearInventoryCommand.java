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

public class ClearInventoryCommand {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(literal("clearinv")
            .requires(source -> CommandUtils.hasAdminPermission(source))
            .executes(ClearInventoryCommand::clearInventorySelf)
            .then(argument("player", StringArgumentType.word())
                .executes(ClearInventoryCommand::clearInventoryOther)
            )
        );
        
        dispatcher.register(literal("ci")
            .requires(source -> CommandUtils.hasAdminPermission(source))
            .executes(ClearInventoryCommand::clearInventorySelf)
            .then(argument("player", StringArgumentType.word())
                .executes(ClearInventoryCommand::clearInventoryOther)
            )
        );
    }
    
    private static int clearInventorySelf(CommandContext<CommandSourceStack> context) {
        CommandSourceStack source = context.getSource();
        
        if (source.getPlayer() == null) {
            CommandUtils.sendError(source, "This command can only be used by players");
            return 0;
        }
        
        return clearInventory(context, source.getPlayer());
    }
    
    private static int clearInventoryOther(CommandContext<CommandSourceStack> context) {
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
        
        return clearInventory(context, target);
    }
    
    private static int clearInventory(CommandContext<CommandSourceStack> context, ServerPlayer player) {
        int itemCount = player.getInventory().getContainerSize();
        player.getInventory().clearContent();
        
        CommandUtils.sendSuccess(context.getSource(), "Cleared " + player.getName().getString() + "'s inventory (" + itemCount + " items)");
        
        if (context.getSource().getPlayer() != null && !context.getSource().getPlayer().equals(player)) {
            player.sendSystemMessage(Component.literal("§cYour inventory was cleared by " + context.getSource().getPlayer().getName().getString()));
        }
        
        return 1;
    }
}
