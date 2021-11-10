package net.rpgz.forge;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLPaths;
import net.minecraftforge.fmllegacy.network.NetworkRegistry;
import net.minecraftforge.fmllegacy.network.simple.SimpleChannel;
import net.rpgz.forge.config.RPGZConfigBuilder;

@Mod(value = Rpgz.MOD_ID)
public class Rpgz {

	public static Rpgz instance;
	public static final String MOD_ID = "rpgz";
	public static final Logger LOGGER = LogManager.getLogger(MOD_ID);
	public static final String INVENTORY_KEY = "UnionInventory";
	private static final String NETWORK_PROTOCOL_VERSION = "1";
	public static boolean debugMode = false;
	public static final SimpleChannel CHANNEL = NetworkRegistry.newSimpleChannel(location("main"), () -> NETWORK_PROTOCOL_VERSION, NETWORK_PROTOCOL_VERSION::equals, NETWORK_PROTOCOL_VERSION::equals);
	
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
		instance = this;
		final IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
		modEventBus.addListener(this::setup);
		modEventBus.addListener(this::clientSetup);
		MinecraftForge.EVENT_BUS.register(this);
		ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, RPGZConfigBuilder.common_config, "rpgz.common.toml");
		RPGZConfigBuilder.loadConfig(RPGZConfigBuilder.common_config, FMLPaths.CONFIGDIR.get().resolve("rpgz.common.toml").toString());
		
//		NetRegistry.registerMessages();
	}

	private void setup(final FMLCommonSetupEvent event)
	{
//		Config.init();
//	    LootSounds.init();
//	    Tags.init();
	}

	private void clientSetup(final FMLClientSetupEvent event) {
	}

	public static ResourceLocation location(String name)
	{
		return new ResourceLocation(MOD_ID, name);
	}
}
