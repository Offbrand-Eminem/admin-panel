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

public class UnmuteCommand {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(literal("unmute")
            .requires(source -> CommandUtils.hasAdminPermission(source))
            .then(argument("player", StringArgumentType.word())
                .executes(UnmuteCommand::unmutePlayer)
            )
        );
    }
    
    private static int unmutePlayer(CommandContext<CommandSourceStack> context) {
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
        
        if (!MuteCommand.isMuted(target.getUUID())) {
            CommandUtils.sendError(source, "Player " + playerName + " is not muted");
            return 0;
        }
        
        MuteCommand.unmutePlayer(target.getUUID());
        CommandUtils.sendSuccess(source, "Unmuted player: " + playerName);
        target.sendSystemMessage(Component.literal("§aYou have been unmuted"));
        
        return 1;
    }
}
