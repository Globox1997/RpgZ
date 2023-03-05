package net.rpgz.config;

import java.util.ArrayList;

import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry;
import me.shedaniel.cloth.clothconfig.shadowed.blue.endless.jankson.Comment;

@Config(name = "rpgz")
@Config.Gui.Background("minecraft:textures/block/stone.png")
public class RpgzConfig implements ConfigData {

  public boolean drop_unlooted = false;
  public boolean hopper_extracting = false;
  @ConfigEntry.Gui.PrefixText
  @Comment("Default: 2400ticks = 2min")
  public int drop_after_ticks = 2400;
  public int despawn_corps_after_ticks = 4800;
  public boolean despawn_immediately_when_empty = true;
  public boolean surfacing_in_water = true;
  @Comment("Example: minecraft:zombie or adventurez:brown_fungus")
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