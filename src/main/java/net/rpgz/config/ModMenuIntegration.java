//package net.rpgz.config;
//
//import java.util.function.Function;
//
//import io.github.prospector.modmenu.api.ModMenuApi;
//import me.sargunvohra.mcmods.autoconfig1u.AutoConfig;
//import net.minecraft.client.gui.screen.Screen;
//import net.minecraftforge.api.distmarker.Dist;
//import net.minecraftforge.api.distmarker.OnlyIn;
//
//@OnlyIn(Dist.CLIENT)
//public class ModMenuIntegration implements ModMenuApi {
//
//  @Override
//  public String getModId() {
//    return "rpgz";
//  }
//
//  @Override
//  public Function<Screen, ? extends Screen> getConfigScreenFactory() {
//    return screen -> AutoConfig.getConfigScreen(RpgzConfig.class, screen).get();
//  }
//}
