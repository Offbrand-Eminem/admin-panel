package com.adminpanel.commands;

import com.adminpanel.utils.CommandUtils;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

import static net.minecraft.server.command.CommandManager.literal;

public class SetSpawnCommand {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(literal("setspawn")
            .requires(source -> CommandUtils.hasOwnerPermission(source))
            .executes(SetSpawnCommand::setSpawn)
        );
    }

    private static int setSpawn(CommandContext<ServerCommandSource> context) {
        ServerCommandSource source = context.getSource();
        
        if (!(source.getEntity() instanceof ServerPlayerEntity)) {
            source.sendError(Text.literal("§cThis command can only be used by a player"));
            return 0;
        }

        ServerPlayerEntity player = (ServerPlayerEntity) source.getEntity();
        
        // Use the vanilla setworldspawn command
        try {
            String pos = player.getBlockPos().getX() + " " + player.getBlockPos().getY() + " " + player.getBlockPos().getZ();
            source.getServer().getCommandManager().getDispatcher().execute("setworldspawn " + pos, source);
            CommandUtils.sendSuccess(source, "Spawn point set to your current location");
        } catch (Exception e) {
            source.sendError(Text.literal("§cFailed to set spawn point"));
            return 0;
        }
        
        return 1;
    }
}
