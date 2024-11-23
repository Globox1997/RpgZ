package net.rpgz.config;

import java.util.ArrayList;

import com.stereowalker.unionlib.config.ConfigObject;
import com.stereowalker.unionlib.config.UnionConfig;
import com.stereowalker.unionlib.config.UnionConfig.Comment;
import com.stereowalker.unionlib.config.UnionConfig.Entry;

//@Config.Gui.Background("minecraft:textures/block/stone.png")
@UnionConfig(name = "rpgz")
public class RpgzConfig implements ConfigObject {

	@Entry(name = "Drop loot after time", translatable = "text.rpgz.option.drop_unlooted")
	public boolean drop_unlooted = false;
	@Entry(name = "Duration in ticks (1s = 20ticks)", translatable = "text.rpgz.option.hopper_extracting")
	public boolean hopper_extracting = false;
	//  @ConfigEntry.Gui.PrefixText
	@Entry(name = "Ticks before automatic loot drop", translatable = "text.rpgz.option.drop_after_ticks")
	@Comment(comment = "Default: 2400ticks = 2min")
	public int drop_after_ticks = 2400;
	@Entry(name = "Ticks before corps despawn", translatable = "text.rpgz.option.despawn_corps_after_ticks")
	public int despawn_corps_after_ticks = 4800;
	@Entry(name = "Despawn corps immediately when empty", translatable = "text.rpgz.option.despawn_immediately_when_empty")
	public boolean despawn_immediately_when_empty = true;
	@Entry(name = "Surfacing in water", translatable = "text.rpgz.option.surfacing_in_water")
	public boolean surfacing_in_water = true;
	@Comment(comment = "Example: minecraft:zombie or adventurez:brown_fungus")
	@Entry(name = "Exclude entities by name modid:mobname", translatable = "text.rpgz.option.excluded_entities")
	public ArrayList<String> excluded_entities = new ArrayList<>() {
		{
			add("minecraft:shulker");
		}
		//This prevents duplicate entries from polluting the list. 
		//TODO: Find a way to allow anyone modifying the config to remove whatever the defaults are on this list, 
		//remember that you have tried all sorts of things to make this possible. I hope you, future me, can solve this conundrum 
		public boolean add(String e) {
			for (String s : this) if (s.equals(e)) return false;
			return super.add(e);
		};
	};

}