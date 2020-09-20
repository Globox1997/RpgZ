package net.rpgz;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.screenhandler.v1.ScreenHandlerRegistry;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.util.Identifier;
import net.rpgz.sound.LootSounds;
import net.rpgz.tag.Tags;
import net.rpgz.ui.LivingEntityScreenHandler;

public class RpgzMain implements ModInitializer {

  public static final ScreenHandlerType<LivingEntityScreenHandler> LIVING_ENTITY_CONTAINER = ScreenHandlerRegistry
      .registerSimple(new Identifier("rpgz", "entity_screen_handler"), LivingEntityScreenHandler::new);

  @Override
  public void onInitialize() {
    LootSounds.init();
    Tags.init();

  }
}