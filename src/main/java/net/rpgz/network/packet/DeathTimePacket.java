package net.rpgz.network.packet;

import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

public record DeathTimePacket(int entityId, int deathTime) implements CustomPayload {

    public static final CustomPayload.Id<DeathTimePacket> PACKET_ID = new CustomPayload.Id<>(Identifier.of("rpgz", "death_time_packet"));

    public static final PacketCodec<RegistryByteBuf, DeathTimePacket> PACKET_CODEC = PacketCodec.of((value, buf) -> {
        buf.writeInt(value.entityId);
        buf.writeInt(value.deathTime);
    }, buf -> new DeathTimePacket(buf.readInt(), buf.readInt()));

    @Override
    public Id<? extends CustomPayload> getId() {
        return PACKET_ID;
    }

}

