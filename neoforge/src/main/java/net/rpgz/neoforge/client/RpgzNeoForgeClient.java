package net.rpgz.neoforge.client;

import me.shedaniel.autoconfig.AutoConfigClient;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.RegisterGuiLayersEvent;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import net.neoforged.neoforge.client.network.event.RegisterClientPayloadHandlersEvent;
import net.rpgz.RpgzClient;
import net.rpgz.RpgzMain;
import net.rpgz.config.RpgzConfig;
import net.rpgz.network.RpgClientPacket;
import net.rpgz.network.packet.DeathTimePacket;

@Mod(value = RpgzMain.MOD_ID, dist = Dist.CLIENT)
public class RpgzNeoForgeClient {

    public RpgzNeoForgeClient(IEventBus modBus, ModContainer container) {
        modBus.addListener(RegisterGuiLayersEvent.class, event -> event.registerAboveAll(RpgzClient.LOOT_BAG_HUD_ID, RpgzClient::renderLootBag));
        modBus.addListener(RegisterClientPayloadHandlersEvent.class,
                event -> event.register(DeathTimePacket.PACKET_ID, (payload, context) -> RpgClientPacket.handleDeathTime(payload)));

        container.registerExtensionPoint(IConfigScreenFactory.class,
                (modContainer, parent) -> AutoConfigClient.getConfigScreen(RpgzConfig.class, parent).get());
    }

}
