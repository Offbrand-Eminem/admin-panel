package com.adminpanel.network;

import com.adminpanel.AdminPanelMod;
import com.adminpanel.client.ForgeAdminPanelClient;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.ChannelBuilder;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.SimpleChannel;

public final class ForgeOpenGuiPacket {
    private static final SimpleChannel CHANNEL = ChannelBuilder
        .named(AdminPanelMod.id("main"))
        .networkProtocolVersion(1)
        .optional()
        .simpleChannel();

    private static boolean registered;

    private ForgeOpenGuiPacket() {
    }

    public static synchronized void register() {
        if (registered) {
            return;
        }

        CHANNEL.messageBuilder(Open.class, 0, NetworkDirection.PLAY_TO_CLIENT)
            .encoder(Open::encode)
            .decoder(Open::decode)
            .consumerMainThread((message, context) -> ForgeAdminPanelClient.openAdminPanel())
            .add();
        CHANNEL.build();
        registered = true;
    }

    public static void sendTo(ServerPlayer player) {
        CHANNEL.send(new Open(), PacketDistributor.PLAYER.with(player));
    }

    public record Open() {
        private static void encode(Open message, FriendlyByteBuf buffer) {
        }

        private static Open decode(FriendlyByteBuf buffer) {
            return new Open();
        }
    }
}
