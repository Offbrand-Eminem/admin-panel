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
import net.minecraft.world.level.GameType;

import static net.minecraft.commands.Commands.argument;
import static net.minecraft.commands.Commands.literal;

public class GamemodeCommand {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
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
    
    private static int setGamemodeSelf(CommandContext<CommandSourceStack> context) {
        String gamemodeStr = StringArgumentType.getString(context, "gamemode");
        CommandSourceStack source = context.getSource();
        
        if (source.getPlayer() == null) {
            CommandUtils.sendError(source, "This command can only be used by players");
            return 0;
        }
        
        return setGamemode(context, source.getPlayer(), gamemodeStr);
    }
    
    private static int setGamemodeOther(CommandContext<CommandSourceStack> context) {
        String gamemodeStr = StringArgumentType.getString(context, "gamemode");
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
        
        return setGamemode(context, target, gamemodeStr);
    }
    
    private static int setGamemode(CommandContext<CommandSourceStack> context, ServerPlayer player, String gamemodeStr) {
        GameType gameMode = parseGameMode(gamemodeStr);
        
        if (gameMode == null) {
            CommandUtils.sendError(context.getSource(), "Invalid gamemode. Use: survival(s/0), creative(c/1), adventure(a/2), spectator(sp/3)");
            return 0;
        }
        
        GameType previousMode = player.gameMode.getGameModeForPlayer();
        player.setGameMode(gameMode);
        
        String gameModeName = getGameModeName(gameMode);
        String previousModeName = getGameModeName(previousMode);
        CommandUtils.sendSuccess(context.getSource(), "Set " + player.getName().getString() + "'s gamemode to " + gameModeName + " (was " + previousModeName + ")");
        
        if (context.getSource().getPlayer() != null && !context.getSource().getPlayer().equals(player)) {
            player.sendSystemMessage(Component.literal("§aYour gamemode was set to " + gameModeName + " by " + context.getSource().getPlayer().getName().getString()));
        }
        
        return 1;
    }
    
    private static String getGameModeName(GameType gameMode) {
        if (gameMode == GameType.SURVIVAL) return "survival";
        if (gameMode == GameType.CREATIVE) return "creative";
        if (gameMode == GameType.ADVENTURE) return "adventure";
        if (gameMode == GameType.SPECTATOR) return "spectator";
        return "unknown";
    }
    
    private static int setSurvivalSelf(CommandContext<CommandSourceStack> context) {
        CommandSourceStack source = context.getSource();
        if (source.getPlayer() == null) {
            CommandUtils.sendError(source, "This command can only be used by players");
            return 0;
        }
        return setGamemode(context, source.getPlayer(), "survival");
    }
    
    private static int setSurvivalOther(CommandContext<CommandSourceStack> context) {
        String playerName = StringArgumentType.getString(context, "player");
        CommandSourceStack source = context.getSource();
        ServerPlayer target = CommandUtils.getPlayer(source, playerName);
        if (target == null) {
            CommandUtils.sendError(source, "Player not found: " + playerName);
            return 0;
        }
        return setGamemode(context, target, "survival");
    }
    
    private static int setCreativeSelf(CommandContext<CommandSourceStack> context) {
        CommandSourceStack source = context.getSource();
        if (source.getPlayer() == null) {
            CommandUtils.sendError(source, "This command can only be used by players");
            return 0;
        }
        return setGamemode(context, source.getPlayer(), "creative");
    }
    
    private static int setCreativeOther(CommandContext<CommandSourceStack> context) {
        String playerName = StringArgumentType.getString(context, "player");
        CommandSourceStack source = context.getSource();
        ServerPlayer target = CommandUtils.getPlayer(source, playerName);
        if (target == null) {
            CommandUtils.sendError(source, "Player not found: " + playerName);
            return 0;
        }
        return setGamemode(context, target, "creative");
    }
    
    private static int setAdventureSelf(CommandContext<CommandSourceStack> context) {
        CommandSourceStack source = context.getSource();
        if (source.getPlayer() == null) {
            CommandUtils.sendError(source, "This command can only be used by players");
            return 0;
        }
        return setGamemode(context, source.getPlayer(), "adventure");
    }
    
    private static int setAdventureOther(CommandContext<CommandSourceStack> context) {
        String playerName = StringArgumentType.getString(context, "player");
        CommandSourceStack source = context.getSource();
        ServerPlayer target = CommandUtils.getPlayer(source, playerName);
        if (target == null) {
            CommandUtils.sendError(source, "Player not found: " + playerName);
            return 0;
        }
        return setGamemode(context, target, "adventure");
    }
    
    private static int setSpectatorSelf(CommandContext<CommandSourceStack> context) {
        CommandSourceStack source = context.getSource();
        if (source.getPlayer() == null) {
            CommandUtils.sendError(source, "This command can only be used by players");
            return 0;
        }
        return setGamemode(context, source.getPlayer(), "spectator");
    }
    
    private static int setSpectatorOther(CommandContext<CommandSourceStack> context) {
        String playerName = StringArgumentType.getString(context, "player");
        CommandSourceStack source = context.getSource();
        ServerPlayer target = CommandUtils.getPlayer(source, playerName);
        if (target == null) {
            CommandUtils.sendError(source, "Player not found: " + playerName);
            return 0;
        }
        return setGamemode(context, target, "spectator");
    }
    
    private static GameType parseGameMode(String str) {
        switch (str.toLowerCase()) {
            case "survival":
            case "0":
            case "s":
                return GameType.SURVIVAL;
            case "creative":
            case "1":
            case "c":
                return GameType.CREATIVE;
            case "adventure":
            case "2":
            case "a":
                return GameType.ADVENTURE;
            case "spectator":
            case "3":
            case "sp":
                return GameType.SPECTATOR;
            default:
                return null;
        }
    }
}
