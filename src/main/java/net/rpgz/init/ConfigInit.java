package net.rpgz.init;

import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.ConfigHolder;
import net.minecraft.util.ActionResult;
import me.shedaniel.autoconfig.serializer.GsonConfigSerializer;
import net.rpgz.config.RpgzConfig;

public class ConfigInit {

    public static RpgzConfig CONFIG = new RpgzConfig();

    public static void init() {
        ConfigHolder<RpgzConfig> holder = AutoConfig.register(RpgzConfig.class, GsonConfigSerializer::new);
        holder.registerSaveListener((configHolder, config) -> {
            config.validatePostLoad();
            return ActionResult.PASS;
        });
        CONFIG = holder.getConfig();
    }

}
