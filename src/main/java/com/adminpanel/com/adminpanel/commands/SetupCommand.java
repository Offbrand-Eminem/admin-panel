package com.adminpanel.commands;

import com.adminpanel.AdminPanelMod;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

import static net.minecraft.commands.Commands.literal;

public class SetupCommand {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(literal("setupadmin")
            .executes(SetupCommand::setupFirstOwner)
        );
    }

    private static int setupFirstOwner(CommandContext<CommandSourceStack> context) {
        CommandSourceStack source = context.getSource();
        
        if (!(source.getEntity() instanceof ServerPlayer)) {
            source.sendFailure(Component.literal("§cThis command can only be used by a player"));
            return 0;
        }

        ServerPlayer player = (ServerPlayer) source.getEntity();
        
        // Check if setup already completed
        if (AdminPanelMod.configManager.isSetupCompleted()) {
            source.sendFailure(Component.literal("§cSetup has already been completed"));
            return 0;
        }
        
        // Add as owner
        AdminPanelMod.configManager.addOwner(player.getName().getString());
        AdminPanelMod.configManager.setSetupCompleted(true);
        
        source.sendSuccess(() -> Component.literal("§a" + player.getName().getString() + " has been set as the first owner!"), true);
        source.sendSuccess(() -> Component.literal("§eThe setup command has been disabled."), true);
        
        return 1;
    }
}
