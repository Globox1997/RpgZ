package net.rpgz.forge.config;

import java.util.ArrayList;
import java.util.List;

import net.minecraftforge.common.ForgeConfigSpec;

public class RPGZConfig {
	public static ForgeConfigSpec.BooleanValue drop_unlooted;
	public static ForgeConfigSpec.ConfigValue<Integer> drop_after_ticks;
	public static ForgeConfigSpec.ConfigValue<Integer> despawn_corps_after_ticks;
	public static ForgeConfigSpec.BooleanValue despawn_immediately_when_empty; 
	public static ForgeConfigSpec.BooleanValue hopper_extracting; 
	public static ForgeConfigSpec.ConfigValue<List<String>> excluded_entities;
	
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
		
		excluded_entities = common
				.comment("Example: minecraft:zombie or adventurez.brown_fungus")
				.define(category+"excluded_entities", new ArrayList<>());
		
	}
}
