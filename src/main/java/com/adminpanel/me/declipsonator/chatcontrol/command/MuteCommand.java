package me.declipsonator.chatcontrol.command;


import com.adminpanel.utils.CommandUtils;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import me.declipsonator.chatcontrol.util.Config;
import me.declipsonator.chatcontrol.util.MutedPlayer;
import me.declipsonator.chatcontrol.util.PlayerUtils;
import me.declipsonator.chatcontrol.util.TempMutedPlayer;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.arguments.GameProfileArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.players.NameAndId;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static com.mojang.brigadier.Command.SINGLE_SUCCESS;
import static net.minecraft.commands.Commands.argument;
import static net.minecraft.commands.Commands.literal;

public class MuteCommand {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        if (!Config.muteCommand) return;
        dispatcher.register(literal("mute").requires(CommandUtils::hasAdminPermission)
                .then(literal("add")
                        .then(literal("permanent")
                                .then(argument("target", GameProfileArgument.gameProfile()).executes(context -> {
                                            int players = GameProfileArgument.getGameProfiles(context, "target").size();
                                            for (NameAndId profile : GameProfileArgument.getGameProfiles(context, "target")) {
                                                if (Config.isMuted(profile.id())) {
                                                    context.getSource().sendFailure(Component.nullToEmpty(profile.name()).copy().append(Component.translatable("text.control.mute.alreadyMuted")));
                                                    if (players == 1) return 0;
                                                    else continue;
                                                }

                                                Config.addMutedPlayer(profile.id(), "No reason provided");
                                                context.getSource().sendSuccess(() -> Component.nullToEmpty(profile.name()).copy().append(Component.translatable("text.control.mute.permanentlyMuted")), true);
                                            }
                                            Config.saveConfig();
                                            return SINGLE_SUCCESS;
                                        })
                                        .then(argument("reason", StringArgumentType.greedyString()).executes(context -> {
                                            int players = GameProfileArgument.getGameProfiles(context, "target").size();
                                            for (NameAndId profile : GameProfileArgument.getGameProfiles(context, "target")) {
                                                if (Config.isMuted(profile.id())) {
                                                    context.getSource().sendFailure(Component.nullToEmpty(profile.name()).copy().append(Component.translatable("text.control.mute.alreadyMuted")));
                                                    if (players == 1) return 0;
                                                    else continue;
                                                }

                                                Config.addMutedPlayer(profile.id(), context.getArgument("reason", String.class));
                                                context.getSource().sendSuccess(() -> Component.nullToEmpty(profile.name()).copy().append(Component.translatable("text.control.mute.permanentlyMuted")), true);
                                            }
                                            Config.saveConfig();
                                            return SINGLE_SUCCESS;
                                        }))))
                        .then(literal("temporary")
                                .then(argument("target", GameProfileArgument.gameProfile())
                                        .then(argument("minutes", IntegerArgumentType.integer(0, 525960)).executes(context -> {
                                                    int players = GameProfileArgument.getGameProfiles(context, "target").size();
                                                    for (NameAndId profile : GameProfileArgument.getGameProfiles(context, "target")) {
                                                        if (Config.isMuted(profile.id())) {
                                                            context.getSource().sendFailure(Component.nullToEmpty(profile.name()).copy().append(Component.translatable("text.control.mute.alreadyMuted")));
                                                            if (players == 1) return 0;
                                                            else continue;
                                                        }
                                                        long until = System.currentTimeMillis() + (IntegerArgumentType.getInteger(context, "minutes") * 60000L);
                                                        Config.addTempMutedPlayer(profile.id(), until, "No reason provided");
                                                        context.getSource().sendSuccess(() -> Component.nullToEmpty(profile.name()).copy().append(Component.translatable("text.control.mute.temporarilyMuted")), true);
                                                    }
                                                    Config.saveConfig();
                                                    return SINGLE_SUCCESS;
                                                })
                                                .then(argument("reason", StringArgumentType.greedyString()).executes(context -> {
                                                    int players = GameProfileArgument.getGameProfiles(context, "target").size();
                                                    for (NameAndId profile : GameProfileArgument.getGameProfiles(context, "target")) {
                                                        if (Config.isMuted(profile.id())) {
                                                            context.getSource().sendFailure(Component.nullToEmpty(profile.name()).copy().append(Component.translatable("text.control.mute.alreadyMuted")));
                                                            if (players == 1) return 0;
                                                            else continue;
                                                        }
                                                        long until = System.currentTimeMillis() + (IntegerArgumentType.getInteger(context, "minutes") * 60000L);
                                                        Config.addTempMutedPlayer(profile.id(), until, context.getArgument("reason", String.class));
                                                        context.getSource().sendSuccess(() -> Component.nullToEmpty(profile.name()).copy().append(Component.translatable("text.control.mute.temporarilyMuted")), true);
                                                    }
                                                    Config.saveConfig();
                                                    return SINGLE_SUCCESS;
                                                })))
                                )
                        )

                )
                .then(literal("remove").then(argument("player", GameProfileArgument.gameProfile()).suggests((context, builder) -> {
                    List<TempMutedPlayer> tempMutedPlayers = Config.getTempMutedPlayers();
                    List<MutedPlayer> mutedPlayers = Config.getMutedPlayers();
                    List<UUID> playersOnMuteList = new ArrayList<>();
                    tempMutedPlayers.forEach(player -> playersOnMuteList.add(player.uuid()));
                    mutedPlayers.forEach(player -> playersOnMuteList.add(player.uuid()));
                    return SharedSuggestionProvider.suggest(PlayerUtils.getPlayerNames(playersOnMuteList), builder);
                }).executes(context -> {
                    int players = GameProfileArgument.getGameProfiles(context, "player").size();
                    for (NameAndId profile : GameProfileArgument.getGameProfiles(context, "player")) {
                        if (!Config.isMuted(profile.id())) {
                            context.getSource().sendFailure(Component.nullToEmpty(profile.name()).copy().append(Component.translatable("text.control.mute.notMuted")));
                            if (players == 1) return 0;
                            else continue;

                        }

                        Config.removeMutedPlayer(profile.id());
                        context.getSource().sendSuccess(() -> Component.nullToEmpty(profile.name()).copy().append(Component.translatable("text.control.mute.unmuted")), true);

                    }
                    Config.saveConfig();
                    return SINGLE_SUCCESS;
                })))
                .then(literal("list").executes(context -> {
                    List<MutedPlayer> mutedPlayers = Config.getMutedPlayers();
                    List<TempMutedPlayer> tempMutedPlayers = Config.getTempMutedPlayers();
                    context.getSource().sendSuccess(() -> Component.translatable("text.control.mute.mutedPlayers"), false);
                    mutedPlayers.forEach(player -> context.getSource().sendSuccess(() -> Component.nullToEmpty(PlayerUtils.getPlayerName(player.uuid().toString()) + " - " + player.reason()), false));
                    context.getSource().sendSuccess(() -> Component.translatable("text.control.mute.tempMutedPlayers"), false);
                    tempMutedPlayers.forEach(player -> context.getSource().sendSuccess(() -> Component.nullToEmpty(PlayerUtils.getPlayerName(player.uuid().toString()) + " - " + player.reason()), false));
                    return SINGLE_SUCCESS;
                }))
        );


    }
}
