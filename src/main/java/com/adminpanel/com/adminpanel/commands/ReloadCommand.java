package com.adminpanel.commands;

import com.adminpanel.AdminPanelMod;
import com.adminpanel.permission.PermissionManager;
import com.adminpanel.utils.CommandUtils;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;

import static net.minecraft.commands.Commands.literal;

public class ReloadCommand {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(literal("adminreload")
            .requires(source -> CommandUtils.hasAdminPermission(source))
            .executes(ReloadCommand::reloadConfig)
        );
        
        dispatcher.register(literal("reload")
            .requires(source -> CommandUtils.hasAdminPermission(source))
            .executes(ReloadCommand::reloadConfig)
        );
    }
    
    private static int reloadConfig(CommandContext<CommandSourceStack> context) {
        CommandSourceStack source = context.getSource();
        
        if (AdminPanelMod.configManager == null) {
            CommandUtils.sendError(source, "Config manager is not initialized");
            return 0;
        }
        
        AdminPanelMod.configManager.reloadConfig();
        CommandUtils.sendSuccess(source, "Admin panel configuration reloaded");
        
        return 1;
    }
}
