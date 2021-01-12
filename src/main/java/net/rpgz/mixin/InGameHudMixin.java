package net.rpgz.mixin;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.IngameGui;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
@Mixin(IngameGui.class)
public abstract class InGameHudMixin extends AbstractGui {
  @Shadow
  @Final
  @Mutable
  private final Minecraft mc;

  public InGameHudMixin(Minecraft mc) {
    this.mc = mc;
  }

  @Inject(method = "renderIngameGui", at = @At(value = "TAIL"))
  private void renderIngameGuiMixin(MatrixStack matrixStack, float f, CallbackInfo info) {
    this.renderLootBag(matrixStack);
  }

  private void renderLootBag(MatrixStack matrixStack) {
    if (this.mc.objectMouseOver != null && this.mc.objectMouseOver.getType() == RayTraceResult.Type.ENTITY) {
      Entity entity = ((EntityRayTraceResult) this.mc.objectMouseOver).getEntity();
      if (entity instanceof LivingEntity) {
        LivingEntity deadBody = (LivingEntity) entity;
        if (deadBody != null && deadBody.deathTime > 20) {
          int scaledWidth = this.mc.getMainWindow().getScaledWidth();
          int scaledHeight = this.mc.getMainWindow().getScaledHeight();
          RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
          this.mc.getTextureManager().bindTexture(new ResourceLocation("rpgz:textures/sprite/loot_bag.png"));
          AbstractGui.blit(matrixStack, (scaledWidth / 2), (scaledHeight / 2) - 16, 0.0F, 0.0F, 16, 16, 16,
              16);
        }
      }
    }

  }

}