package net.rpgz.sound;

import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class LootSounds {

    public static final Identifier LOOT_SOUND = new Identifier("rpgz:loot");
    public static final Identifier COIN_LOOT_SOUND = new Identifier("rpgz:coin_loot");

    public static SoundEvent LOOT_SOUND_EVENT = new SoundEvent(LOOT_SOUND);
    public static SoundEvent COIN_LOOT_SOUND_EVENT = new SoundEvent(COIN_LOOT_SOUND);

    public static void init() {
        Registry.register(Registry.SOUND_EVENT, LOOT_SOUND, LOOT_SOUND_EVENT);
        Registry.register(Registry.SOUND_EVENT, COIN_LOOT_SOUND, COIN_LOOT_SOUND_EVENT);
    }

}