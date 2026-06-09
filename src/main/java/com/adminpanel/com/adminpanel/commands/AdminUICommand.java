package com.adminpanel.commands;

import com.adminpanel.gui.AdminPanelScreenHandler;
import com.adminpanel.utils.CommandUtils;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.SimpleMenuProvider;

import static net.minecraft.commands.Commands.argument;
import static net.minecraft.commands.Commands.literal;

public class AdminUICommand {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(literal("adminui")
            .requires(source -> CommandUtils.hasAdminPermission(source))
            .executes(AdminUICommand::openAdminUI)
            .then(argument("player", StringArgumentType.word())
                .executes(AdminUICommand::openAdminUIForTarget)
            )
        );

        dispatcher.register(literal("gui")
            .requires(source -> CommandUtils.hasAdminPermission(source))
            .executes(AdminUICommand::openAdminUI)
            .then(argument("player", StringArgumentType.word())
                .executes(AdminUICommand::openAdminUIForTarget)
            )
        );
    }

    private static int openAdminUI(CommandContext<CommandSourceStack> context) {
        return openAdminUI(context, null);
    }

    private static int openAdminUIForTarget(CommandContext<CommandSourceStack> context) {
        CommandSourceStack source = context.getSource();
        String targetName = StringArgumentType.getString(context, "player");

        if (!CommandUtils.isValidPlayerName(targetName)) {
            CommandUtils.sendError(source, "Invalid player name: " + targetName);
            return 0;
        }

        if (source.getServer().getPlayerList().getPlayerByName(targetName) == null) {
            CommandUtils.sendError(source, "Player not found: " + targetName);
            return 0;
        }

        return openAdminUI(context, targetName);
    }

    private static int openAdminUI(CommandContext<CommandSourceStack> context, String targetName) {
        CommandSourceStack source = context.getSource();

        if (source.getPlayer() == null) {
            CommandUtils.sendError(source, "This command can only be used by players");
            return 0;
        }

        ServerPlayer player = source.getPlayer();

        Component title = targetName == null
            ? Component.literal("Admin Panel")
            : Component.literal("Admin Panel: " + targetName);
        player.openMenu(new SimpleMenuProvider(
            (syncId, inventory, menuPlayer) -> new AdminPanelScreenHandler(syncId, inventory, targetName),
            title
        ));
        
        CommandUtils.sendSuccess(source, "Opening Admin Panel GUI");
        return 1;
    }
}
