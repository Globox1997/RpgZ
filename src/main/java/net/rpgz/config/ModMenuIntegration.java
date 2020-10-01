package net.rpgz.config;

import io.github.prospector.modmenu.api.ModMenuApi;
import me.sargunvohra.mcmods.autoconfig1u.AutoConfig;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.Screen;

import java.util.function.Function;

@Environment(EnvType.CLIENT)
public class ModMenuIntegration implements ModMenuApi {

  @Override
  public String getModId() {
    return "rpgz";
  }

  @Override
  public Function<Screen, ? extends Screen> getConfigScreenFactory() {
    return screen -> AutoConfig.getConfigScreen(RpgzConfig.class, screen).get();
  }
}
