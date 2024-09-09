package net.rpgz;

import net.fabricmc.api.ModInitializer;
import net.rpgz.init.*;
import net.rpgz.network.RpgServerPacket;

public class RpgzMain implements ModInitializer {

    @Override
    public void onInitialize() {
        ConfigInit.init();
        SoundInit.init();
        TagInit.init();
        RpgServerPacket.init();
        EventInit.init();
    }
}
