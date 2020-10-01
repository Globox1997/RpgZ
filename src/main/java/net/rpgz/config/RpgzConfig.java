package net.rpgz.config;

import me.sargunvohra.mcmods.autoconfig1u.ConfigData;
import me.sargunvohra.mcmods.autoconfig1u.annotation.ConfigEntry;
import me.sargunvohra.mcmods.autoconfig1u.shadowed.blue.endless.jankson.Comment;
import me.sargunvohra.mcmods.autoconfig1u.annotation.Config;

@Config(name = "rpgz")
@Config.Gui.Background("minecraft:textures/block/stone.png")
public class RpgzConfig implements ConfigData {

  public boolean drop_unlooted = false;
  @ConfigEntry.Gui.PrefixText
  @Comment("Default: 2400ticks = 2min")
  public int drop_after_ticks = 2400;

}
