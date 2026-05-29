package com.adminpanel.commands;

import com.adminpanel.AdminPanelMod;
import com.adminpanel.permission.PermissionManager;
import com.adminpanel.utils.CommandUtils;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;

import static net.minecraft.server.command.CommandManager.literal;

public class AdminPanelCommand {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
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
    
    private static int showInfo(CommandContext<ServerCommandSource> context) {
        ServerCommandSource source = context.getSource();
        
        source.sendFeedback(() -> Text.literal("§6=== Admin Panel v" + AdminPanelMod.VERSION + " ==="), false);
        source.sendFeedback(() -> Text.literal("§eCommands: ban, kick, mute, unmute, gm, tp, tphere, give, clearinv, heal, fly, speed, time, weather"), false);
        source.sendFeedback(() -> Text.literal("§eAdmin Commands: setadmin, removeadmin"), false);
        source.sendFeedback(() -> Text.literal("§eOwner Commands: setowner, removeowner"), false);
        source.sendFeedback(() -> Text.literal("§eUse /adminpanel list to see all admins and owners"), false);
        
        return 1;
    }
    
    private static int listMembers(CommandContext<ServerCommandSource> context) {
        ServerCommandSource source = context.getSource();
        
        source.sendFeedback(() -> Text.literal("§6=== Admin Panel Members ==="), false);
        source.sendFeedback(() -> Text.literal("§cOwners: " + CommandUtils.formatPlayerList(AdminPanelMod.configManager.getOwners())), false);
        source.sendFeedback(() -> Text.literal("§aAdmins: " + CommandUtils.formatPlayerList(AdminPanelMod.configManager.getAdmins())), false);
        
        if (AdminPanelMod.configManager.isMaintenanceMode()) {
            source.sendFeedback(() -> Text.literal("§cMaintenance Mode: §aENABLED"), false);
        }
        
        return 1;
    }
}
