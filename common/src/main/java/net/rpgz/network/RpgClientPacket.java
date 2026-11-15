package net.rpgz.network;

import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.Mob;
import net.rpgz.network.packet.DeathTimePacket;

public class RpgClientPacket {

    public static void handleDeathTime(DeathTimePacket payload) {
        Minecraft client = Minecraft.getInstance();
        if (client.level != null && client.level.getEntity(payload.entityId()) instanceof Mob mobEntity) {
            mobEntity.deathTime = payload.deathTime();
        }
    }

}
