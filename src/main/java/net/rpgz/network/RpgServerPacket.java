package net.rpgz.network;

import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.rpgz.network.packet.DeathTimePacket;

public class RpgServerPacket {

    public static void init() {
        PayloadTypeRegistry.playS2C().register(DeathTimePacket.PACKET_ID, DeathTimePacket.PACKET_CODEC);
    }

}
