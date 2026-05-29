package com.adminpanel.commands;

import com.adminpanel.AdminPanelMod;
import com.adminpanel.utils.CommandUtils;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.registry.entry.RegistryEntry;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class KillCommand {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(literal("kill")
            .requires(source -> CommandUtils.hasAdminPermission(source))
            .executes(KillCommand::killSelf)
            .then(argument("player", StringArgumentType.word())
                .executes(KillCommand::killOther)
            )
        );

        dispatcher.register(literal("slay")
            .requires(source -> CommandUtils.hasAdminPermission(source))
            .executes(KillCommand::killSelf)
            .then(argument("player", StringArgumentType.word())
                .executes(KillCommand::killOther)
            )
        );
    }

    private static int killSelf(CommandContext<ServerCommandSource> context) {
        ServerCommandSource source = context.getSource();
        if (source.getPlayer() == null) {
            CommandUtils.sendError(source, "This command can only be used by players");
            return 0;
        }
        return kill(context, source.getPlayer());
    }

    private static int killOther(CommandContext<ServerCommandSource> context) {
        String playerName = StringArgumentType.getString(context, "player");
        ServerCommandSource source = context.getSource();

        if (!CommandUtils.isValidPlayerName(playerName)) {
            CommandUtils.sendError(source, "Invalid player name: " + playerName);
            return 0;
        }

        ServerPlayerEntity target = CommandUtils.getPlayer(source, playerName);
        if (target == null) {
            CommandUtils.sendError(source, "Player not found: " + playerName);
            return 0;
        }

        if (AdminPanelMod.permissionManager.isAdmin(target)) {
            if (!AdminPanelMod.permissionManager.isOwner(source.getPlayer())) {
                CommandUtils.sendError(source, "Cannot kill other admins/owners");
                return 0;
            }
        }

        return kill(context, target);
    }

    private static int kill(CommandContext<ServerCommandSource> context, ServerPlayerEntity player) {
        // Use out-of-world damage which instantly kills - compatible with 1.21.11
        ServerWorld world = context.getSource().getWorld();
        player.damage(world, world.getDamageSources().outOfWorld(), Float.MAX_VALUE);
        CommandUtils.sendSuccess(context.getSource(), "Killed " + player.getName().getString());

        if (context.getSource().getPlayer() != null && !context.getSource().getPlayer().equals(player)) {
            player.sendMessage(Text.literal("§cYou were killed by " + context.getSource().getPlayer().getName().getString()));
        }
        return 1;
    }
}
