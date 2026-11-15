package net.rpgz.fabric.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.rpgz.RpgzClient;
import net.rpgz.network.RpgClientPacket;
import net.rpgz.network.packet.DeathTimePacket;

@Environment(EnvType.CLIENT)
public class RpgzFabricClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        HudElementRegistry.addLast(RpgzClient.LOOT_BAG_HUD_ID, RpgzClient::renderLootBag);
        ClientPlayNetworking.registerGlobalReceiver(DeathTimePacket.PACKET_ID,
                (payload, context) -> context.client().execute(() -> RpgClientPacket.handleDeathTime(payload)));
    }

}
