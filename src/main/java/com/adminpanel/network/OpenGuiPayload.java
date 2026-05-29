package com.adminpanel.network;

import com.adminpanel.AdminPanelMod;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;

public record OpenGuiPayload() implements CustomPayload {
    public static final Id<OpenGuiPayload> ID = new Id<>(AdminPanelMod.id("opengui"));
    public static final PacketCodec<RegistryByteBuf, OpenGuiPayload> CODEC = PacketCodec.unit(new OpenGuiPayload());

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}
