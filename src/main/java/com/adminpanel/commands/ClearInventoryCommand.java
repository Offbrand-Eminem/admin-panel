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

public class ClearInventoryCommand {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
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
    
    private static int clearInventorySelf(CommandContext<ServerCommandSource> context) {
        ServerCommandSource source = context.getSource();
        
        if (source.getPlayer() == null) {
            CommandUtils.sendError(source, "This command can only be used by players");
            return 0;
        }
        
        return clearInventory(context, source.getPlayer());
    }
    
    private static int clearInventoryOther(CommandContext<ServerCommandSource> context) {
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
        
        return clearInventory(context, target);
    }
    
    private static int clearInventory(CommandContext<ServerCommandSource> context, ServerPlayerEntity player) {
        int itemCount = player.getInventory().size();
        player.getInventory().clear();
        
        CommandUtils.sendSuccess(context.getSource(), "Cleared " + player.getName().getString() + "'s inventory (" + itemCount + " items)");
        
        if (context.getSource().getPlayer() != null && !context.getSource().getPlayer().equals(player)) {
            player.sendMessage(Text.literal("§cYour inventory was cleared by " + context.getSource().getPlayer().getName().getString()));
        }
        
        return 1;
    }
}
