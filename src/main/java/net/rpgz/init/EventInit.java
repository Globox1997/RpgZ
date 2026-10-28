package net.rpgz.init;

import net.fabricmc.fabric.api.networking.v1.EntityTrackingEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.world.entity.Mob;
import net.rpgz.network.packet.DeathTimePacket;

public class EventInit {

    public static void init() {

        // ClientEntityEvents.ENTITY_LOAD has desynced health at joining the world
        EntityTrackingEvents.START_TRACKING.register((entity, player) -> {
            if (entity instanceof Mob mobEntity && mobEntity.isDeadOrDying()) {
                ServerPlayNetworking.send(player, new DeathTimePacket(mobEntity.getId(), mobEntity.deathTime));
            }
        });
    }
}
