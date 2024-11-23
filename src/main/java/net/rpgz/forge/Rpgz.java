package net.rpgz.forge;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.stereowalker.unionlib.api.collectors.ConfigCollector;
import com.stereowalker.unionlib.mod.MinecraftMod;
import com.stereowalker.unionlib.mod.ServerSegment;
import com.stereowalker.unionlib.util.VersionHelper;

import net.minecraft.resources.ResourceLocation;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.rpgz.RpgzClientSegment;
import net.rpgz.init.ConfigInit;
import net.rpgz.init.TagInit;

@Mod(value = Rpgz.MOD_ID)
public class Rpgz extends MinecraftMod {

	public static Rpgz instance;
	public static final String MOD_ID = "rpgz";
	public static final Logger LOGGER = LogManager.getLogger(MOD_ID);
	public static final String INVENTORY_KEY = "UnionInventory";
	public static boolean debugMode = false;
	
	public static void debug(String message) {
		if (debugMode) {
			Rpgz.LOGGER.debug(message);
		}
	}
	
	public static void warn(String message) {
		if (debugMode) {
			Rpgz.LOGGER.warn(message);
		}
	}
	
	public static boolean disableConfig() {
		return false;
	}
	
	public static boolean drawMainMenuButton() {
		return true;
	}

	public Rpgz() 
	{
		super("rpgz", () -> new RpgzClientSegment(), () -> new ServerSegment());
		instance = this;
		TagInit.init();
		eventBus().addListener(this::setup);
		eventBus().addListener(this::clientSetup);
//		NeoForge.EVENT_BUS.register(this);
//		NetRegistry.registerMessages();
	}
	
	@Override
	public void setupConfigs(ConfigCollector collector) {
		collector.registerConfig(ConfigInit.CONFIG);
	}

	private void setup(final FMLCommonSetupEvent event)
	{
//	    LootSounds.init();
//	    Tags.init();
	}

	private void clientSetup(final FMLClientSetupEvent event) {
	}

	public static ResourceLocation locationz(String name)
	{
		return VersionHelper.toLoc(MOD_ID, name);
	}
}
