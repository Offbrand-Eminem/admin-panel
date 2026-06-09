package com.adminpanel.network;

import com.adminpanel.AdminPanelMod;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

public record OpenGuiPayload() implements CustomPacketPayload {
    public static final Type<OpenGuiPayload> ID = new Type<>(AdminPanelMod.id("opengui"));
    public static final StreamCodec<RegistryFriendlyByteBuf, OpenGuiPayload> CODEC = StreamCodec.unit(new OpenGuiPayload());
    private static boolean registered;

    public static synchronized void registerS2C() {
        if (!registered) {
            PayloadTypeRegistry.playS2C().register(ID, CODEC);
            registered = true;
        }
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return ID;
    }
}
