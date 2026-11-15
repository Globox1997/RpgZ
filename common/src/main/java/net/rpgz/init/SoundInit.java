package net.rpgz.init;

import java.util.function.BiConsumer;

import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundEvent;

public class SoundInit {

    public static final Identifier LOOT_SOUND = Identifier.parse("rpgz:loot");
    public static final Identifier COIN_LOOT_SOUND = Identifier.parse("rpgz:coin_loot");

    public static SoundEvent LOOT_SOUND_EVENT;
    public static SoundEvent COIN_LOOT_SOUND_EVENT;

    public static void register(BiConsumer<Identifier, SoundEvent> registrar) {
        LOOT_SOUND_EVENT = SoundEvent.createVariableRangeEvent(LOOT_SOUND);
        COIN_LOOT_SOUND_EVENT = SoundEvent.createVariableRangeEvent(COIN_LOOT_SOUND);
        registrar.accept(LOOT_SOUND, LOOT_SOUND_EVENT);
        registrar.accept(COIN_LOOT_SOUND, COIN_LOOT_SOUND_EVENT);
    }

}
