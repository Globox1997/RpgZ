package net.rpgz.init;

import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;

public class SoundInit {

    public static final Identifier LOOT_SOUND = new Identifier("rpgz:loot");
    public static final Identifier COIN_LOOT_SOUND = new Identifier("rpgz:coin_loot");

    public static SoundEvent LOOT_SOUND_EVENT = SoundEvent.of(LOOT_SOUND);
    public static SoundEvent COIN_LOOT_SOUND_EVENT = SoundEvent.of(COIN_LOOT_SOUND);

    public static void init() {
        Registry.register(Registries.SOUND_EVENT, LOOT_SOUND, LOOT_SOUND_EVENT);
        Registry.register(Registries.SOUND_EVENT, COIN_LOOT_SOUND, COIN_LOOT_SOUND_EVENT);
    }

}