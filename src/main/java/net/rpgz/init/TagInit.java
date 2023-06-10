package net.rpgz.init;

import net.minecraft.entity.EntityType;
import net.minecraft.item.Item;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;

public class TagInit {

    public static final TagKey<Item> RARE_ITEMS = TagKey.of(RegistryKeys.ITEM, new Identifier("rpgz", "rare_items"));
    public static final TagKey<EntityType<?>> EXCLUDED_ENTITIES = TagKey.of(RegistryKeys.ENTITY_TYPE, new Identifier("rpgz", "excluded_entities"));

    public static void init() {
    }

}
