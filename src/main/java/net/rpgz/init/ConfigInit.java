package net.rpgz.init;

import java.util.HashSet;
import java.util.Set;

import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.ConfigHolder;
import me.shedaniel.autoconfig.serializer.GsonConfigSerializer;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Identifier;
import net.rpgz.config.RpgzConfig;

public class ConfigInit {

    public static RpgzConfig CONFIG = new RpgzConfig();
    public static Set<Identifier> EXCLUDED_ENTITIES = Set.of();

    public static void init() {
        ConfigHolder<RpgzConfig> holder = AutoConfig.register(RpgzConfig.class, GsonConfigSerializer::new);
        holder.registerSaveListener((configHolder, config) -> {
            config.validatePostLoad();
            updateExcludedEntities(config);
            return ActionResult.PASS;
        });
        holder.registerLoadListener((configHolder, config) -> {
            CONFIG = config;
            updateExcludedEntities(config);
            return ActionResult.PASS;
        });
        CONFIG = holder.getConfig();
        updateExcludedEntities(CONFIG);
    }

    private static void updateExcludedEntities(RpgzConfig config) {
        Set<Identifier> excludedEntities = new HashSet<>();
        if (config.excluded_entities == null) {
            config.validatePostLoad();
        }
        for (String id : config.excluded_entities) {
            Identifier identifier = Identifier.tryParse(id.trim());
            if (identifier != null) {
                excludedEntities.add(identifier);
            }
        }
        EXCLUDED_ENTITIES = Set.copyOf(excludedEntities);
    }

}
