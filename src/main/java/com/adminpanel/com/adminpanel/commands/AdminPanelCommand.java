package com.adminpanel.commands;

import com.adminpanel.AdminPanelMod;
import com.adminpanel.permission.PermissionManager;
import com.adminpanel.utils.CommandUtils;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;

import static net.minecraft.commands.Commands.literal;

public class AdminPanelCommand {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(literal("adminpanel")
            .requires(source -> CommandUtils.hasAdminPermission(source))
            .executes(AdminPanelCommand::showInfo)
            .then(literal("list")
                .executes(AdminPanelCommand::listMembers)
            )
        );
        
        dispatcher.register(literal("ap")
            .requires(source -> CommandUtils.hasAdminPermission(source))
            .executes(AdminPanelCommand::showInfo)
            .then(literal("list")
                .executes(AdminPanelCommand::listMembers)
            )
        );
    }
    
    private static int showInfo(CommandContext<CommandSourceStack> context) {
        CommandSourceStack source = context.getSource();
        
        source.sendSuccess(() -> Component.literal("§6=== Admin Panel v" + AdminPanelMod.VERSION + " ==="), false);
        source.sendSuccess(() -> Component.literal("§eCommands: ban, kick, mute, unmute, gm, tp, tphere, give, clearinv, heal, fly, speed, time, weather"), false);
        source.sendSuccess(() -> Component.literal("§eAdmin Commands: setadmin, removeadmin"), false);
        source.sendSuccess(() -> Component.literal("§eOwner Commands: setowner, removeowner"), false);
        source.sendSuccess(() -> Component.literal("§eUse /adminpanel list to see all admins and owners"), false);
        
        return 1;
    }
    
    private static int listMembers(CommandContext<CommandSourceStack> context) {
        CommandSourceStack source = context.getSource();
        
        source.sendSuccess(() -> Component.literal("§6=== Admin Panel Members ==="), false);
        source.sendSuccess(() -> Component.literal("§cOwners: " + CommandUtils.formatPlayerList(AdminPanelMod.configManager.getOwners())), false);
        source.sendSuccess(() -> Component.literal("§aAdmins: " + CommandUtils.formatPlayerList(AdminPanelMod.configManager.getAdmins())), false);
        
        if (AdminPanelMod.configManager.isMaintenanceMode()) {
            source.sendSuccess(() -> Component.literal("§cMaintenance Mode: §aENABLED"), false);
        }
        
        return 1;
    }
}
