package net.rpgz.init;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;

public class SoundInit {

    public static final ResourceLocation LOOT_SOUND = ResourceLocation.parse("rpgz:loot");
    public static final ResourceLocation COIN_LOOT_SOUND = ResourceLocation.parse("rpgz:coin_loot");

    public static SoundEvent LOOT_SOUND_EVENT = SoundEvent.createVariableRangeEvent(LOOT_SOUND);
    public static SoundEvent COIN_LOOT_SOUND_EVENT = SoundEvent.createVariableRangeEvent(COIN_LOOT_SOUND);

    public static void init() {
        Registry.register(BuiltInRegistries.SOUND_EVENT, LOOT_SOUND, LOOT_SOUND_EVENT);
        Registry.register(BuiltInRegistries.SOUND_EVENT, COIN_LOOT_SOUND, COIN_LOOT_SOUND_EVENT);
    }

}