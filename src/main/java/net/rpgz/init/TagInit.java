package net.rpgz.init;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;

public class TagInit {

    public static final TagKey<Item> RARE_ITEMS = TagKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath("rpgz", "rare_items"));
    public static final TagKey<EntityType<?>> EXCLUDED_ENTITIES = TagKey.create(Registries.ENTITY_TYPE, Identifier.fromNamespaceAndPath("rpgz", "excluded_entities"));

    public static void init() {
    }

}
