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

public class HealCommand {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(literal("heal")
            .requires(source -> CommandUtils.hasAdminPermission(source))
            .executes(HealCommand::healSelf)
            .then(argument("player", StringArgumentType.word())
                .executes(HealCommand::healOther)
            )
        );
    }
    
    private static int healSelf(CommandContext<ServerCommandSource> context) {
        ServerCommandSource source = context.getSource();
        
        if (source.getPlayer() == null) {
            CommandUtils.sendError(source, "This command can only be used by players");
            return 0;
        }
        
        return heal(context, source.getPlayer());
    }
    
    private static int healOther(CommandContext<ServerCommandSource> context) {
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
        
        return heal(context, target);
    }
    
    private static int heal(CommandContext<ServerCommandSource> context, ServerPlayerEntity player) {
        // Heal health
        player.setHealth(player.getMaxHealth());
        
        // Heal hunger
        player.getHungerManager().setFoodLevel(20);
        player.getHungerManager().setSaturationLevel(20.0f);
        
        // Clear all status effects
        player.clearStatusEffects();
        
        // Extinguish if on fire
        player.setOnFire(false);
        
        // Reset air if underwater
        player.setAir(player.getMaxAir());
        
        String targetName = player.getName().getString();
        CommandUtils.sendSuccess(context.getSource(), "Healed " + targetName);
        
        if (context.getSource().getPlayer() != null && !context.getSource().getPlayer().equals(player)) {
            player.sendMessage(Text.literal("§aYou have been healed by " + context.getSource().getPlayer().getName().getString()));
        }
        
        return 1;
    }
}
