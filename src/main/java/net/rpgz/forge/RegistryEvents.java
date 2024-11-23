package net.rpgz.forge;

import net.minecraft.core.registries.Registries;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.EventBusSubscriber.Bus;
import net.neoforged.neoforge.registries.RegisterEvent;
import net.rpgz.init.SoundInit;

@EventBusSubscriber(bus=Bus.MOD, modid = Rpgz.MOD_ID)
public class RegistryEvents
{
	@SubscribeEvent
	public static void registerSoundEvents(final RegisterEvent event) {
		event.register(Registries.SOUND_EVENT,
				helper -> {
					SoundInit.registerAll(helper);
				});
	}
}
