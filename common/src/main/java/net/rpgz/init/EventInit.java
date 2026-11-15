package net.rpgz.init;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.rpgz.network.packet.DeathTimePacket;
import net.rpgz.platform.Services;

public class EventInit {

    public static void onStartTracking(Entity entity, ServerPlayer player) {
        if (entity instanceof Mob mobEntity && mobEntity.isDeadOrDying() && Services.network().canSendToPlayer(player, DeathTimePacket.PACKET_ID)) {
            Services.network().sendToPlayer(player, new DeathTimePacket(mobEntity.getId(), mobEntity.deathTime));
        }
    }
}
