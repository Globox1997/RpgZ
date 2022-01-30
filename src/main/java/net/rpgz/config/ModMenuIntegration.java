package net.rpgz.config;

import java.util.function.Supplier;

import me.shedaniel.autoconfig.AutoConfig;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.ConfigGuiHandler;
import net.minecraftforge.client.ConfigGuiHandler.ConfigGuiFactory;

@OnlyIn(Dist.CLIENT)
public class ModMenuIntegration {

	public static Supplier<ConfigGuiFactory> getModConfigScreenFactory() {
		return () -> new ConfigGuiHandler.ConfigGuiFactory((minecraft, parent) -> {
			return AutoConfig.getConfigScreen(RpgzConfig.class, parent).get();
		});
	}
}