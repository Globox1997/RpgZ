package net.rpgz.fabric;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.networking.v1.EntityTrackingEvents;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.rpgz.RpgzMain;
import net.rpgz.fabric.network.RpgServerPacket;
import net.rpgz.init.EventInit;
import net.rpgz.init.SoundInit;

public class RpgzFabric implements ModInitializer {

    @Override
    public void onInitialize() {
        RpgzMain.init();
        SoundInit.register((id, sound) -> Registry.register(BuiltInRegistries.SOUND_EVENT, id, sound));
        RpgServerPacket.init();
        EntityTrackingEvents.START_TRACKING.register(EventInit::onStartTracking);
    }
}
