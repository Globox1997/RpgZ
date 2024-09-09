package net.rpgz.network;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.entity.mob.MobEntity;
import net.rpgz.network.packet.DeathTimePacket;

@Environment(EnvType.CLIENT)
public class RpgClientPacket {

    public static void init() {
        ClientPlayNetworking.registerGlobalReceiver(DeathTimePacket.PACKET_ID, (payload, context) -> {
            int entityId = payload.entityId();
            int deathTime = payload.deathTime();
            context.client().execute(() -> {
                if (context.client().world != null && context.client().world.getEntityById(entityId) instanceof MobEntity mobEntity) {
                    mobEntity.deathTime = deathTime;
                }
            });
        });
    }

}
