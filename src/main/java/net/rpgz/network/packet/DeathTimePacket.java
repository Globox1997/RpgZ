package net.rpgz.network.packet;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public record DeathTimePacket(int entityId, int deathTime) implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<DeathTimePacket> PACKET_ID = new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath("rpgz", "death_time_packet"));

    public static final StreamCodec<RegistryFriendlyByteBuf, DeathTimePacket> PACKET_CODEC = StreamCodec.ofMember((value, buf) -> {
        buf.writeInt(value.entityId);
        buf.writeInt(value.deathTime);
    }, buf -> new DeathTimePacket(buf.readInt(), buf.readInt()));

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return PACKET_ID;
    }

}

