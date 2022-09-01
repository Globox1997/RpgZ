package net.rpgz;

import net.fabricmc.api.ModInitializer;
import net.rpgz.init.ConfigInit;
import net.rpgz.init.SoundInit;
import net.rpgz.init.TagInit;

public class RpgzMain implements ModInitializer {

    @Override
    public void onInitialize() {
        ConfigInit.init();
        SoundInit.init();
        TagInit.init();
    }
}
