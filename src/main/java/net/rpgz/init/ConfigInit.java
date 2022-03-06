package net.rpgz.init;

import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.JanksonConfigSerializer;
import net.rpgz.config.RpgzConfig;

public class ConfigInit {

    public static RpgzConfig CONFIG = new RpgzConfig();

    public static void init() {
        AutoConfig.register(RpgzConfig.class, JanksonConfigSerializer::new);
        CONFIG = AutoConfig.getConfigHolder(RpgzConfig.class).getConfig();
    }

}
