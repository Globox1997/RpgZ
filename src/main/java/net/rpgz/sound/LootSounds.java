package net.rpgz.sound;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.registries.IForgeRegistry;

public class LootSounds {

  public static final ResourceLocation LOOT_SOUND = new ResourceLocation("rpgz:loot");
  public static final ResourceLocation COIN_LOOT_SOUND = new ResourceLocation("rpgz:coin_loot");

  public static SoundEvent LOOT_SOUND_EVENT = new SoundEvent(LOOT_SOUND);
  public static SoundEvent COIN_LOOT_SOUND_EVENT = new SoundEvent(COIN_LOOT_SOUND);

  public static void registerAll(IForgeRegistry<SoundEvent> registry) {
		registry.register(LOOT_SOUND_EVENT.setRegistryName(LOOT_SOUND));
		registry.register(COIN_LOOT_SOUND_EVENT.setRegistryName(COIN_LOOT_SOUND));
	}

}