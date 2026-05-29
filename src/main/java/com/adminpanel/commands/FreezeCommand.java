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

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class FreezeCommand {
    private static final Map<UUID, Boolean> frozenPlayers = new HashMap<>();
    
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
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
    
    private static int freezePlayer(CommandContext<ServerCommandSource> context) {
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
        
        if (isFrozen(target.getUuid())) {
            CommandUtils.sendError(source, "Player " + playerName + " is already frozen");
            return 0;
        }
        
        frozenPlayers.put(target.getUuid(), true);
        target.sendAbilitiesUpdate();
        
        CommandUtils.sendSuccess(source, "Froze player: " + playerName);
        target.sendMessage(Text.literal("§cYou have been frozen by an admin"));
        
        return 1;
    }
    
    private static int unfreezePlayer(CommandContext<ServerCommandSource> context) {
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
        
        if (!isFrozen(target.getUuid())) {
            CommandUtils.sendError(source, "Player " + playerName + " is not frozen");
            return 0;
        }
        
        frozenPlayers.remove(target.getUuid());
        target.sendAbilitiesUpdate();
        
        CommandUtils.sendSuccess(source, "Unfroze player: " + playerName);
        target.sendMessage(Text.literal("§aYou have been unfrozen"));
        
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
