package com.adminpanel.commands;

import com.adminpanel.AdminPanelMod;
import com.adminpanel.network.OpenGuiPayload;
import com.adminpanel.utils.CommandUtils;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.level.ServerPlayer;

import static net.minecraft.commands.Commands.literal;

public class AdminUICommand {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(literal("adminui")
            .requires(source -> CommandUtils.hasAdminPermission(source))
            .executes(AdminUICommand::openAdminUI)
        );

        dispatcher.register(literal("gui")
            .requires(source -> CommandUtils.hasAdminPermission(source))
            .executes(AdminUICommand::openAdminUI)
        );
    }

    private static int openAdminUI(CommandContext<CommandSourceStack> context) {
        CommandSourceStack source = context.getSource();

        if (source.getPlayer() == null) {
            CommandUtils.sendError(source, "This command can only be used by players");
            return 0;
        }

        ServerPlayer player = source.getPlayer();

        // Send packet to client to open GUI
        ServerPlayNetworking.send(player, new OpenGuiPayload());
        
        CommandUtils.sendSuccess(source, "Opening Admin Panel GUI");
        return 1;
    }
}
