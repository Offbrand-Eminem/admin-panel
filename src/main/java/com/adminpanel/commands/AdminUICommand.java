package com.adminpanel.commands;

import com.adminpanel.AdminPanelMod;
import com.adminpanel.network.OpenGuiPayload;
import com.adminpanel.utils.CommandUtils;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

import static net.minecraft.server.command.CommandManager.literal;

public class AdminUICommand {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(literal("adminui")
            .requires(source -> CommandUtils.hasAdminPermission(source))
            .executes(AdminUICommand::openAdminUI)
        );

        dispatcher.register(literal("gui")
            .requires(source -> CommandUtils.hasAdminPermission(source))
            .executes(AdminUICommand::openAdminUI)
        );
    }

    private static int openAdminUI(CommandContext<ServerCommandSource> context) {
        ServerCommandSource source = context.getSource();

        if (source.getPlayer() == null) {
            CommandUtils.sendError(source, "This command can only be used by players");
            return 0;
        }

        ServerPlayerEntity player = source.getPlayer();

        // Send packet to client to open GUI
        ServerPlayNetworking.send(player, new OpenGuiPayload());
        
        CommandUtils.sendSuccess(source, "Opening Admin Panel GUI");
        return 1;
    }
}
