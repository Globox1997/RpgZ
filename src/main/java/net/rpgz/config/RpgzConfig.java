package net.rpgz.config;

import java.util.ArrayList;
import java.util.List;

import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.ConfigEntry;
import me.shedaniel.autoconfig.annotation.Config;
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
  @Comment("Example: minecraft.zombie or adventurez.brown_fungus")
  public List<String> excluded_entities = new ArrayList<>();

}