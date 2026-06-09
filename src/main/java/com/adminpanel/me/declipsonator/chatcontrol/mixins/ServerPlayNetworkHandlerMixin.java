package me.declipsonator.chatcontrol.mixins;

import me.declipsonator.chatcontrol.ChatControl;
import me.declipsonator.chatcontrol.util.Config;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.PlayerChatMessage;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Objects;

@Mixin(value = ServerGamePacketListenerImpl.class, priority = 500)
public class ServerPlayNetworkHandlerMixin {
    @Inject(method="broadcastChatMessage", at = @At(value = "HEAD"), cancellable = true)
    private void onHandleDecoratedMessage(PlayerChatMessage message, CallbackInfo ci) {
        ServerPlayer sender = ((ServerGamePacketListenerImpl) (Object) this).player;

        if(sender == null || Config.isIgnored(sender.getUUID())) return;
        String string = message.decoratedContent().getString();
        if(Config.isMuted(sender.getUUID())
                || (!Config.censorAndSend && (Config.checkWords(string)
                || Config.checkPhrases(string)
                || Config.checkRegexes(string)
                || Config.checkStandAloneWords(string)))) {
            ci.cancel();
            if(Config.logFiltered) {
                ChatControl.LOG.info(Component.translatable("text.control.feedback.filteredMessageFrom").getString() +  Objects.requireNonNull(sender.getDisplayName()).getString() + " (" + sender.getUUID().toString() + ")" + ": " + message.decoratedContent().getString());
            }
            if(Config.tellPlayer && Config.isTempMuted(sender.getUUID())) {
                sender.sendSystemMessage(Component.nullToEmpty(String.format(Component.translatable("text.control.feedback.moreMinutes").getString(), (Config.timeLeftTempMuted(sender.getUUID()) / 60000)) + Component.translatable("text.control.feedback.reason").getString() +  Config.getMuteReason(sender.getUUID())));
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

    @ModifyVariable(method = "broadcastChatMessage", at = @At(value = "HEAD"), ordinal = 0, argsOnly = true)
    private PlayerChatMessage onHandleDecoratedMessage(PlayerChatMessage message, PlayerChatMessage m) {
        ServerPlayer sender = ((ServerGamePacketListenerImpl) (Object) this).player;
        if(Config.ignorePrivateMessages || Config.isIgnored(sender.getUUID())) return message;
        if(!Config.isMuted(sender.getUUID()) && Config.censorAndSend) {
            String newMessage = Config.censorWords(message.decoratedContent().getString());
            newMessage = Config.censorPhrases(newMessage);
            newMessage = Config.censorRegexes(newMessage);
            newMessage = Config.censorStandAloneWords(newMessage);
            newMessage = Config.censorWords(newMessage);
            if(!newMessage.equals(message.decoratedContent().getString())) {
                if(Config.tellPlayer) sender.sendSystemMessage(Component.translatable("text.control.feedback.censoredMessage"));
                if(Config.logFiltered) ChatControl.LOG.info(Component.translatable("text.control.feedback.censoredMessageFrom").getString() +  Objects.requireNonNull(sender.getDisplayName()).getString() + " (" + sender.getUUID().toString() + ")" + ": " + message.decoratedContent().getString());

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
                return PlayerChatMessage.system(newMessage);
            }
        }
        return message;
    }


}
