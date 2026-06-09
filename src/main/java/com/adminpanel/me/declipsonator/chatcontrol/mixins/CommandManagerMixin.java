package me.declipsonator.chatcontrol.mixins;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.ParseResults;
import me.declipsonator.chatcontrol.ChatControl;
import me.declipsonator.chatcontrol.util.Config;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Objects;
import java.util.regex.Pattern;

@Mixin(value = Commands.class, priority = 500)
public class CommandManagerMixin {
    @Shadow
    @Final
    private CommandDispatcher<CommandSourceStack> dispatcher;

    @Inject(method="performCommand", at = @At(value = "HEAD"), cancellable = true)
    private void onExecute(ParseResults<CommandSourceStack> parseResults, String command, CallbackInfo cir) {
        CommandSourceStack source = parseResults.getContext().getSource();
        command = command.replaceFirst(Pattern.quote("/"), "");

        if(command.startsWith("say") || command.startsWith("me") || (!Config.ignorePrivateMessages && (command.startsWith("whisper") || command.startsWith("tell") || command.startsWith("msg") || command.startsWith("w")))) {
            String string = command.replaceFirst("say ", "");
            string = string.replaceFirst("me ", "");
            string = string.replaceFirst("w ", "");
            string = string.replaceFirst("tell ", "");
            string = string.replaceFirst("msg ", "");
            string = string.replaceFirst("w ", "");

            ServerPlayer sender = source.getPlayer();
            if(sender == null || Config.isIgnored(sender.getUUID())) return;
            if(Config.isMuted(sender.getUUID())
                    || (!Config.censorAndSend && (Config.checkWords(string)
                    || Config.checkPhrases(string)
                    || Config.checkRegexes(string)
                    || Config.checkStandAloneWords(string)))) {
                cir.cancel();
                if(Config.logFiltered) {
                    ChatControl.LOG.info(Component.translatable("text.control.feedback.filteredMessageFrom").getString() + Objects.requireNonNull(sender.getDisplayName()).getString() + " (" + sender.getUUID().toString() + ")" + ": " + string);
                }
                if(Config.tellPlayer && Config.isTempMuted(sender.getUUID())) {
                    sender.sendSystemMessage(Component.nullToEmpty(String.format(Component.translatable("text.control.feedback.moreMinutes").getString(), (Config.timeLeftTempMuted(sender.getUUID()) / 60000)) + Component.translatable("text.control.feedback.reason") + Config.getMuteReason(sender.getUUID())));
                } else if(Config.tellPlayer && Config.isMuted(sender.getUUID())) {
                    sender.sendSystemMessage(Component.translatable("text.control.feedback.muted").append(Component.translatable("text.control.feedback.reason").getString() +  Config.getMuteReason(sender.getUUID())));
                } else if(Config.tellPlayer) {
                    sender.sendSystemMessage(Component.translatable("text.control.feedback.filteredMessage"));

                    if(Config.muteAfterOffense) {
                        Config.addOffense(sender.getUUID());
                        if (Config.offenseCount(sender.getUUID()) >= Config.muteAfterOffenseNumber) {
                            if (Config.muteAfterOffenseType == Config.MuteType.PERMANENT) {
                                Config.addMutedPlayer(sender.getUUID(), Component.translatable("text.control.feedback.repeatedOffenses").getString());
                                sender.sendSystemMessage(Component.translatable("text.control.feedback.permMuted"));
                            } else {
                                Config.addTempMutedPlayer(sender.getUUID(), System.currentTimeMillis() + (Config.muteAfterOffenseMinutes * 60000L), Component.translatable("text.control.feedback.repeatedOffenses").getString());
                                sender.sendSystemMessage(Component.nullToEmpty(String.format(Component.translatable("text.control.feedback.tempMuted").getString(),  Config.muteAfterOffenseMinutes)));
                            }
                            Config.removeOffenses(sender.getUUID());
                        }
                    }
                }
            }

        }

    }

    @ModifyVariable(method = "performCommand", at = @At(value = "HEAD"), ordinal = 0, argsOnly = true)
    private ParseResults<CommandSourceStack> onExecution(ParseResults<CommandSourceStack> parseResults,  ParseResults<CommandSourceStack> p, String command) {
        CommandSourceStack source = parseResults.getContext().getSource();
        command = command.replaceFirst(Pattern.quote("/"), "");
        if(command.startsWith("say") || command.startsWith("me") || (!Config.ignorePrivateMessages && (command.startsWith("whisper") || command.startsWith("tell") || command.startsWith("msg") || command.startsWith("w")))) {
            String string = command.replaceFirst("say ", "");
            string = string.replaceFirst("me ", "");
            string = string.replaceFirst("w ", "");
            string = string.replaceFirst("tell ", "");
            string = string.replaceFirst("msg ", "");
            string = string.replaceFirst("w ", "");
            ServerPlayer sender = source.getPlayer();
            if (sender == null || Config.isIgnored(sender.getUUID())) return parseResults;
            if (!Config.isMuted(sender.getUUID()) && Config.censorAndSend) {
                String newMessage = Config.censorWords(string);
                newMessage = Config.censorPhrases(newMessage);
                newMessage = Config.censorRegexes(newMessage);
                newMessage = Config.censorStandAloneWords(newMessage);
                newMessage = Config.censorWords(newMessage);
                if (!newMessage.equals(string)) {
                    if(Config.muteAfterOffense) {
                        Config.addOffense(sender.getUUID());
                        if (Config.offenseCount(sender.getUUID()) >= Config.muteAfterOffenseNumber) {
                            if (Config.muteAfterOffenseType == Config.MuteType.PERMANENT) {
                                Config.addMutedPlayer(sender.getUUID(), Component.translatable("text.control.feedback.repeatedOffenses").getString());
                                sender.sendSystemMessage(Component.translatable("text.control.feedback.permMuted"));
                            } else {
                                Config.addTempMutedPlayer(sender.getUUID(), System.currentTimeMillis() + (Config.muteAfterOffenseMinutes * 60000L), Component.translatable("text.control.feedback.repeatedOffenses").getString());
                                sender.sendSystemMessage(Component.nullToEmpty(String.format(Component.translatable("text.control.feedback.tempMuted").getString(),  Config.muteAfterOffenseMinutes)));
                            }
                            Config.removeOffenses(sender.getUUID());
                        }
                    }
                    return dispatcher.parse(command.split(" ")[0] + " " +  newMessage, source);
                }
            }
        }
        return parseResults;
    }


    @ModifyVariable(method = "performCommand", at = @At(value = "HEAD"), ordinal = 0, argsOnly = true)
    private String onExecute(String command, ParseResults<CommandSourceStack> parseResults, String c) {
        CommandSourceStack source = parseResults.getContext().getSource();
        String newCommand = command.replaceFirst(Pattern.quote("/"), "");

        if(newCommand.startsWith("say") || newCommand.startsWith("me") || (!Config.ignorePrivateMessages && (newCommand.startsWith("whisper") || newCommand.startsWith("tell") || newCommand.startsWith("msg") || newCommand.startsWith("w")))) {

            String string = newCommand.replaceFirst("say ", "");
            string = string.replaceFirst("me ", "");
            string = string.replaceFirst("w ", "");
            string = string.replaceFirst("tell ", "");
            string = string.replaceFirst("msg ", "");
            string = string.replaceFirst("w ", "");

            ServerPlayer sender = source.getPlayer();
            if (sender == null || Config.isIgnored(sender.getUUID())) return command;
            if (!Config.isMuted(sender.getUUID()) && Config.censorAndSend) {
                String newMessage = Config.censorWords(string);
                newMessage = Config.censorPhrases(newMessage);
                newMessage = Config.censorRegexes(newMessage);
                newMessage = Config.censorStandAloneWords(newMessage);
                newMessage = Config.censorWords(newMessage);
                if (!newMessage.equals(string)) {

                    if(Config.tellPlayer) sender.sendSystemMessage(Component.translatable("text.control.feedback.censoredMessage"));
                    if (Config.logFiltered)
                        ChatControl.LOG.info(Component.translatable("text.control.feedback.censoredMessageFrom").getString() + Objects.requireNonNull(sender.getDisplayName()).getString() + " (" + sender.getUUID().toString() + ")" + ": " + string);
                    if(Config.muteAfterOffense) {
                        Config.addOffense(sender.getUUID());
                        if (Config.offenseCount(sender.getUUID()) >= Config.muteAfterOffenseNumber) {
                            if (Config.muteAfterOffenseType == Config.MuteType.PERMANENT) {
                                Config.addMutedPlayer(sender.getUUID(), Component.translatable("text.control.feedback.repeatedOffenses").getString());

                                sender.sendSystemMessage(Component.translatable("text.control.feedback.permMuted"));
                            } else {
                                Config.addTempMutedPlayer(sender.getUUID(), System.currentTimeMillis() + (Config.muteAfterOffenseMinutes * 60000L), Component.translatable("text.control.feedback.repeatedOffenses").getString());
                                sender.sendSystemMessage(Component.nullToEmpty(String.format(Component.translatable("text.control.feedback.tempMuted").getString(),  Config.muteAfterOffenseMinutes)));
                            }
                            Config.removeOffenses(sender.getUUID());
                        }
                    }
                    return command.split(" ")[0] + " " +  newMessage;
                }
            }
        }
        return command;
    }




}
