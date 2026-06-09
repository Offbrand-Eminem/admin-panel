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

public class HealCommand {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(literal("heal")
            .requires(source -> CommandUtils.hasAdminPermission(source))
            .executes(HealCommand::healSelf)
            .then(argument("player", StringArgumentType.word())
                .executes(HealCommand::healOther)
            )
        );
    }
    
    private static int healSelf(CommandContext<CommandSourceStack> context) {
        CommandSourceStack source = context.getSource();
        
        if (source.getPlayer() == null) {
            CommandUtils.sendError(source, "This command can only be used by players");
            return 0;
        }
        
        return heal(context, source.getPlayer());
    }
    
    private static int healOther(CommandContext<CommandSourceStack> context) {
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
        
        return heal(context, target);
    }
    
    private static int heal(CommandContext<CommandSourceStack> context, ServerPlayer player) {
        // Heal health
        player.setHealth(player.getMaxHealth());
        
        // Heal hunger
        player.getFoodData().setFoodLevel(20);
        player.getFoodData().setSaturation(20.0f);
        
        // Clear all status effects
        player.removeAllEffects();
        
        // Extinguish if on fire
        player.setSharedFlagOnFire(false);
        
        // Reset air if underwater
        player.setAirSupply(player.getMaxAirSupply());
        
        String targetName = player.getName().getString();
        CommandUtils.sendSuccess(context.getSource(), "Healed " + targetName);
        
        if (context.getSource().getPlayer() != null && !context.getSource().getPlayer().equals(player)) {
            player.sendSystemMessage(Component.literal("§aYou have been healed by " + context.getSource().getPlayer().getName().getString()));
        }
        
        return 1;
    }
}
