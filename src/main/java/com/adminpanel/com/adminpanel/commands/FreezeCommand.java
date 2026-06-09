package com.adminpanel.commands;

import com.adminpanel.AdminPanelMod;
import com.adminpanel.permission.PermissionManager;
import com.adminpanel.utils.CommandUtils;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

import static net.minecraft.commands.Commands.argument;
import static net.minecraft.commands.Commands.literal;

public class FreezeCommand {
    private static final Map<UUID, Boolean> frozenPlayers = new HashMap<>();
    
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(literal("freeze")
            .requires(source -> CommandUtils.hasAdminPermission(source))
            .then(argument("player", StringArgumentType.word())
                .executes(FreezeCommand::freezePlayer)
            )
        );
        
        dispatcher.register(literal("unfreeze")
            .requires(source -> CommandUtils.hasAdminPermission(source))
            .then(argument("player", StringArgumentType.word())
                .executes(FreezeCommand::unfreezePlayer)
            )
        );
    }
    
    private static int freezePlayer(CommandContext<CommandSourceStack> context) {
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
        
        if (isFrozen(target.getUUID())) {
            CommandUtils.sendError(source, "Player " + playerName + " is already frozen");
            return 0;
        }
        
        frozenPlayers.put(target.getUUID(), true);
        target.onUpdateAbilities();
        
        CommandUtils.sendSuccess(source, "Froze player: " + playerName);
        target.sendSystemMessage(Component.literal("§cYou have been frozen by an admin"));
        
        return 1;
    }
    
    private static int unfreezePlayer(CommandContext<CommandSourceStack> context) {
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
        
        if (!isFrozen(target.getUUID())) {
            CommandUtils.sendError(source, "Player " + playerName + " is not frozen");
            return 0;
        }
        
        frozenPlayers.remove(target.getUUID());
        target.onUpdateAbilities();
        
        CommandUtils.sendSuccess(source, "Unfroze player: " + playerName);
        target.sendSystemMessage(Component.literal("§aYou have been unfrozen"));
        
        return 1;
    }
    
    public static boolean isFrozen(UUID playerUuid) {
        return frozenPlayers.getOrDefault(playerUuid, false);
    }
    
    public static void unfreezeAll() {
        frozenPlayers.clear();
    }
    
    public static Map<UUID, Boolean> getAllFrozenPlayers() {
        return new HashMap<>(frozenPlayers);
    }
}
