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
import net.minecraft.world.GameMode;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class GamemodeCommand {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(literal("gm")
            .requires(source -> CommandUtils.hasAdminPermission(source))
            .then(argument("gamemode", StringArgumentType.word())
                .executes(GamemodeCommand::setGamemodeSelf)
                .then(argument("player", StringArgumentType.word())
                    .executes(GamemodeCommand::setGamemodeOther)
                )
            )
        );
        
        dispatcher.register(literal("gms")
            .requires(source -> CommandUtils.hasAdminPermission(source))
            .executes(GamemodeCommand::setSurvivalSelf)
            .then(argument("player", StringArgumentType.word())
                .executes(GamemodeCommand::setSurvivalOther)
            )
        );
        
        dispatcher.register(literal("gmc")
            .requires(source -> CommandUtils.hasAdminPermission(source))
            .executes(GamemodeCommand::setCreativeSelf)
            .then(argument("player", StringArgumentType.word())
                .executes(GamemodeCommand::setCreativeOther)
            )
        );
        
        dispatcher.register(literal("gma")
            .requires(source -> CommandUtils.hasAdminPermission(source))
            .executes(GamemodeCommand::setAdventureSelf)
            .then(argument("player", StringArgumentType.word())
                .executes(GamemodeCommand::setAdventureOther)
            )
        );
        
        dispatcher.register(literal("gmsp")
            .requires(source -> CommandUtils.hasAdminPermission(source))
            .executes(GamemodeCommand::setSpectatorSelf)
            .then(argument("player", StringArgumentType.word())
                .executes(GamemodeCommand::setSpectatorOther)
            )
        );
    }
    
    private static int setGamemodeSelf(CommandContext<ServerCommandSource> context) {
        String gamemodeStr = StringArgumentType.getString(context, "gamemode");
        ServerCommandSource source = context.getSource();
        
        if (source.getPlayer() == null) {
            CommandUtils.sendError(source, "This command can only be used by players");
            return 0;
        }
        
        return setGamemode(context, source.getPlayer(), gamemodeStr);
    }
    
    private static int setGamemodeOther(CommandContext<ServerCommandSource> context) {
        String gamemodeStr = StringArgumentType.getString(context, "gamemode");
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
        
        return setGamemode(context, target, gamemodeStr);
    }
    
    private static int setGamemode(CommandContext<ServerCommandSource> context, ServerPlayerEntity player, String gamemodeStr) {
        GameMode gameMode = parseGameMode(gamemodeStr);
        
        if (gameMode == null) {
            CommandUtils.sendError(context.getSource(), "Invalid gamemode. Use: survival(s/0), creative(c/1), adventure(a/2), spectator(sp/3)");
            return 0;
        }
        
        GameMode previousMode = player.interactionManager.getGameMode();
        player.changeGameMode(gameMode);
        
        String gameModeName = getGameModeName(gameMode);
        String previousModeName = getGameModeName(previousMode);
        CommandUtils.sendSuccess(context.getSource(), "Set " + player.getName().getString() + "'s gamemode to " + gameModeName + " (was " + previousModeName + ")");
        
        if (context.getSource().getPlayer() != null && !context.getSource().getPlayer().equals(player)) {
            player.sendMessage(Text.literal("§aYour gamemode was set to " + gameModeName + " by " + context.getSource().getPlayer().getName().getString()));
        }
        
        return 1;
    }
    
    private static String getGameModeName(GameMode gameMode) {
        if (gameMode == GameMode.SURVIVAL) return "survival";
        if (gameMode == GameMode.CREATIVE) return "creative";
        if (gameMode == GameMode.ADVENTURE) return "adventure";
        if (gameMode == GameMode.SPECTATOR) return "spectator";
        return "unknown";
    }
    
    private static int setSurvivalSelf(CommandContext<ServerCommandSource> context) {
        ServerCommandSource source = context.getSource();
        if (source.getPlayer() == null) {
            CommandUtils.sendError(source, "This command can only be used by players");
            return 0;
        }
        return setGamemode(context, source.getPlayer(), "survival");
    }
    
    private static int setSurvivalOther(CommandContext<ServerCommandSource> context) {
        String playerName = StringArgumentType.getString(context, "player");
        ServerCommandSource source = context.getSource();
        ServerPlayerEntity target = CommandUtils.getPlayer(source, playerName);
        if (target == null) {
            CommandUtils.sendError(source, "Player not found: " + playerName);
            return 0;
        }
        return setGamemode(context, target, "survival");
    }
    
    private static int setCreativeSelf(CommandContext<ServerCommandSource> context) {
        ServerCommandSource source = context.getSource();
        if (source.getPlayer() == null) {
            CommandUtils.sendError(source, "This command can only be used by players");
            return 0;
        }
        return setGamemode(context, source.getPlayer(), "creative");
    }
    
    private static int setCreativeOther(CommandContext<ServerCommandSource> context) {
        String playerName = StringArgumentType.getString(context, "player");
        ServerCommandSource source = context.getSource();
        ServerPlayerEntity target = CommandUtils.getPlayer(source, playerName);
        if (target == null) {
            CommandUtils.sendError(source, "Player not found: " + playerName);
            return 0;
        }
        return setGamemode(context, target, "creative");
    }
    
    private static int setAdventureSelf(CommandContext<ServerCommandSource> context) {
        ServerCommandSource source = context.getSource();
        if (source.getPlayer() == null) {
            CommandUtils.sendError(source, "This command can only be used by players");
            return 0;
        }
        return setGamemode(context, source.getPlayer(), "adventure");
    }
    
    private static int setAdventureOther(CommandContext<ServerCommandSource> context) {
        String playerName = StringArgumentType.getString(context, "player");
        ServerCommandSource source = context.getSource();
        ServerPlayerEntity target = CommandUtils.getPlayer(source, playerName);
        if (target == null) {
            CommandUtils.sendError(source, "Player not found: " + playerName);
            return 0;
        }
        return setGamemode(context, target, "adventure");
    }
    
    private static int setSpectatorSelf(CommandContext<ServerCommandSource> context) {
        ServerCommandSource source = context.getSource();
        if (source.getPlayer() == null) {
            CommandUtils.sendError(source, "This command can only be used by players");
            return 0;
        }
        return setGamemode(context, source.getPlayer(), "spectator");
    }
    
    private static int setSpectatorOther(CommandContext<ServerCommandSource> context) {
        String playerName = StringArgumentType.getString(context, "player");
        ServerCommandSource source = context.getSource();
        ServerPlayerEntity target = CommandUtils.getPlayer(source, playerName);
        if (target == null) {
            CommandUtils.sendError(source, "Player not found: " + playerName);
            return 0;
        }
        return setGamemode(context, target, "spectator");
    }
    
    private static GameMode parseGameMode(String str) {
        switch (str.toLowerCase()) {
            case "survival":
            case "0":
            case "s":
                return GameMode.SURVIVAL;
            case "creative":
            case "1":
            case "c":
                return GameMode.CREATIVE;
            case "adventure":
            case "2":
            case "a":
                return GameMode.ADVENTURE;
            case "spectator":
            case "3":
            case "sp":
                return GameMode.SPECTATOR;
            default:
                return null;
        }
    }
}
