package net.rpgz.tag;

import net.fabricmc.fabric.api.tag.TagRegistry;
import net.minecraft.entity.EntityType;
import net.minecraft.item.Item;
import net.minecraft.tag.Tag;
import net.minecraft.util.Identifier;

public class Tags {

  public static final Tag<Item> RARE_ITEMS = TagRegistry.item(new Identifier("rpgz", "rare_items"));
  public static final Tag<EntityType<?>> EXCLUDED_ENTITIES = TagRegistry
      .entityType(new Identifier("rpgz", "excluded_entities"));

  public static void init() {
  }

}