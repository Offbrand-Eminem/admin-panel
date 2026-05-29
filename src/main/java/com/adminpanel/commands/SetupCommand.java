package com.adminpanel.commands;

import com.adminpanel.AdminPanelMod;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

import static net.minecraft.server.command.CommandManager.literal;

public class SetupCommand {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(literal("setupadmin")
            .executes(SetupCommand::setupFirstOwner)
        );
    }

    private static int setupFirstOwner(CommandContext<ServerCommandSource> context) {
        ServerCommandSource source = context.getSource();
        
        if (!(source.getEntity() instanceof ServerPlayerEntity)) {
            source.sendError(Text.literal("§cThis command can only be used by a player"));
            return 0;
        }

        ServerPlayerEntity player = (ServerPlayerEntity) source.getEntity();
        
        // Check if setup already completed
        if (AdminPanelMod.configManager.isSetupCompleted()) {
            source.sendError(Text.literal("§cSetup has already been completed"));
            return 0;
        }
        
        // Add as owner
        AdminPanelMod.configManager.addOwner(player.getName().getString());
        AdminPanelMod.configManager.setSetupCompleted(true);
        
        source.sendFeedback(() -> Text.literal("§a" + player.getName().getString() + " has been set as the first owner!"), true);
        source.sendFeedback(() -> Text.literal("§eThe setup command has been disabled."), true);
        
        return 1;
    }
}
