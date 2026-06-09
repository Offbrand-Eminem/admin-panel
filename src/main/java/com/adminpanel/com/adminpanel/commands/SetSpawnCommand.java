package com.adminpanel.commands;

import com.adminpanel.utils.CommandUtils;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

import static net.minecraft.commands.Commands.literal;

public class SetSpawnCommand {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(literal("setspawn")
            .requires(source -> CommandUtils.hasOwnerPermission(source))
            .executes(SetSpawnCommand::setSpawn)
        );
    }

    private static int setSpawn(CommandContext<CommandSourceStack> context) {
        CommandSourceStack source = context.getSource();
        
        if (!(source.getEntity() instanceof ServerPlayer)) {
            source.sendFailure(Component.literal("§cThis command can only be used by a player"));
            return 0;
        }

        ServerPlayer player = (ServerPlayer) source.getEntity();
        
        // Use the vanilla setworldspawn command
        try {
            String pos = player.blockPosition().getX() + " " + player.blockPosition().getY() + " " + player.blockPosition().getZ();
            source.getServer().getCommands().getDispatcher().execute("setworldspawn " + pos, source);
            CommandUtils.sendSuccess(source, "Spawn point set to your current location");
        } catch (Exception e) {
            source.sendFailure(Component.literal("§cFailed to set spawn point"));
            return 0;
        }
        
        return 1;
    }
}
