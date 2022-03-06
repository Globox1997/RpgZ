package net.rpgz.init;

import net.minecraft.entity.EntityType;
import net.minecraft.item.Item;
import net.minecraft.tag.TagKey;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class TagInit {

    public static final TagKey<Item> RARE_ITEMS = TagKey.of(Registry.ITEM_KEY, new Identifier("rpgz", "rare_items"));
    public static final TagKey<EntityType<?>> EXCLUDED_ENTITIES = TagKey.of(Registry.ENTITY_TYPE_KEY, new Identifier("rpgz", "excluded_entities"));

    public static void init() {
    }

}
