package net.rpgz.neoforge;

import net.minecraft.core.registries.Registries;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.registries.RegisterEvent;
import net.rpgz.RpgzMain;
import net.rpgz.init.EventInit;
import net.rpgz.init.SoundInit;
import net.rpgz.network.packet.DeathTimePacket;

@Mod(RpgzMain.MOD_ID)
public class RpgzNeoForge {

    public RpgzNeoForge(IEventBus modBus) {
        RpgzMain.init();
        modBus.addListener(RegisterEvent.class, event -> event.register(Registries.SOUND_EVENT, helper -> SoundInit.register(helper::register)));
        modBus.addListener(RegisterPayloadHandlersEvent.class, event -> event.registrar("1").optional().playToClient(DeathTimePacket.PACKET_ID, DeathTimePacket.PACKET_CODEC));
        NeoForge.EVENT_BUS.addListener(PlayerEvent.StartTracking.class, event -> {
            if (event.getEntity() instanceof ServerPlayer player) {
                EventInit.onStartTracking(event.getTarget(), player);
            }
        });
    }

}
