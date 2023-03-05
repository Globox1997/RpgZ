package net.rpgz.forge;

import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegisterEvent;
import net.rpgz.init.SoundInit;

@Mod.EventBusSubscriber(bus=Mod.EventBusSubscriber.Bus.MOD, modid = Rpgz.MOD_ID)
public class RegistryEvents
{
	@SubscribeEvent
	public static void registerSoundEvents(final RegisterEvent event) {
		event.register(ForgeRegistries.Keys.SOUND_EVENTS,
				helper -> {
					SoundInit.registerAll(helper);
				});
	}
}
