package net.rpgz.config;

import java.util.function.Supplier;

import me.shedaniel.autoconfig.AutoConfig;
import net.minecraftforge.client.ConfigScreenHandler.ConfigScreenFactory;

public class ModMenuIntegration {

	public static Supplier<ConfigScreenFactory> getModConfigScreenFactory() {
		return () -> new ConfigScreenFactory((minecraft, parent) -> {
			return AutoConfig.getConfigScreen(RpgzConfig.class, parent).get();
		});
	}
}