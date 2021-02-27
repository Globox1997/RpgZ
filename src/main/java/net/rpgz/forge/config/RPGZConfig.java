package net.rpgz.forge.config;

import net.minecraftforge.common.ForgeConfigSpec;

public class RPGZConfig {
	public static ForgeConfigSpec.BooleanValue drop_unlooted;
	public static ForgeConfigSpec.ConfigValue<Integer> drop_after_ticks;
	public static ForgeConfigSpec.ConfigValue<Integer> despawn_corps_after_ticks;
	public static ForgeConfigSpec.BooleanValue despawn_immediately_when_empty; 
	public static ForgeConfigSpec.BooleanValue hopper_extracting; 
	
	public static int MAX = 2147483647;
	public static void init(ForgeConfigSpec.Builder common) {
		String category = "";
		
		drop_after_ticks = common
				.define(category+"drop_after_ticks", 2400);
		
		despawn_corps_after_ticks = common
				.define(category+"despawn_corps_after_ticks", 4800);
		
		despawn_immediately_when_empty = common
				.define(category+"despawn_immediately_when_empty", true);
		
		drop_unlooted = common
				.define(category+"drop_unlooted", false);
		
		hopper_extracting = common
				.define(category+"hopper_extracting", false);
		
	}
}
