package net.rpgz.init;

import net.fabricmc.fabric.api.networking.v1.EntityTrackingEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.mob.MobEntity;
import net.rpgz.network.packet.DeathTimePacket;

public class EventInit {

    public static void init() {

        // ClientEntityEvents.ENTITY_LOAD has desynced health at joining the world
        EntityTrackingEvents.START_TRACKING.register((entity, player) -> {
            if (entity instanceof MobEntity mobEntity && mobEntity.isDead()) {
                ServerPlayNetworking.send(player, new DeathTimePacket(mobEntity.getId(), mobEntity.deathTime));
            }
        });
    }
}
