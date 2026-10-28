package net.rpgz.network;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.world.entity.Mob;
import net.rpgz.network.packet.DeathTimePacket;

@Environment(EnvType.CLIENT)
public class RpgClientPacket {

    public static void init() {
        ClientPlayNetworking.registerGlobalReceiver(DeathTimePacket.PACKET_ID, (payload, context) -> {
            int entityId = payload.entityId();
            int deathTime = payload.deathTime();
            context.client().execute(() -> {
                if (context.client().level != null && context.client().level.getEntity(entityId) instanceof Mob mobEntity) {
                    mobEntity.deathTime = deathTime;
                }
            });
        });
    }

}
