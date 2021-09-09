package net.rpgz.config;

import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.JanksonConfigSerializer;

public class Config {
    public static RpgzConfig CONFIG = new RpgzConfig();

    public static void init() {
        AutoConfig.register(RpgzConfig.class, JanksonConfigSerializer::new);
        CONFIG = AutoConfig.getConfigHolder(RpgzConfig.class).getConfig();
    }

}
