package net.rpgz.init;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.registries.RegisterEvent.RegisterHelper;

public class SoundInit {

  public static final ResourceLocation LOOT_SOUND = new ResourceLocation("rpgz:loot");
  public static final ResourceLocation COIN_LOOT_SOUND = new ResourceLocation("rpgz:coin_loot");

  public static SoundEvent LOOT_SOUND_EVENT = new SoundEvent(LOOT_SOUND);
  public static SoundEvent COIN_LOOT_SOUND_EVENT = new SoundEvent(COIN_LOOT_SOUND);

  public static void registerAll(RegisterHelper<SoundEvent> helper) {
		helper.register(LOOT_SOUND, LOOT_SOUND_EVENT);
		helper.register(COIN_LOOT_SOUND, COIN_LOOT_SOUND_EVENT);
	}

}