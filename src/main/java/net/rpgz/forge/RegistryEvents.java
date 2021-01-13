package net.rpgz.forge;

import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.common.ForgeTagHandler;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.rpgz.sound.LootSounds;
import net.rpgz.tag.Tags;

@Mod.EventBusSubscriber(bus=Mod.EventBusSubscriber.Bus.MOD, modid = Rpgz.MOD_ID)
public class RegistryEvents
{
	@SubscribeEvent
	public static void registerSoundEvents(final RegistryEvent.Register<SoundEvent> event) {
		LootSounds.registerAll(event.getRegistry());
	}
	
	@SubscribeEvent
	public static void registerItems(final RegistryEvent.Register<Item> event) {
		Tags.RARE_ITEMS = ForgeTagHandler.makeWrapperTag(event.getRegistry(), new ResourceLocation("rpgz", "rare_items"));
	}
}