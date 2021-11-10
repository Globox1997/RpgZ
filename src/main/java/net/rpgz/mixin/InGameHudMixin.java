package net.rpgz.mixin;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.world.phys.HitResult;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
@Mixin(Gui.class)
public abstract class InGameHudMixin extends GuiComponent {
  @Shadow
  @Final
  @Mutable
  private final Minecraft minecraft;

  public InGameHudMixin(Minecraft mc) {
    this.minecraft = mc;
  }

  @Inject(method = "render", at = @At(value = "TAIL"))
  private void renderIngameGuiMixin(PoseStack matrixStack, float f, CallbackInfo info) {
    this.renderLootBag(matrixStack);
  }

  private void renderLootBag(PoseStack matrixStack) {
    if (this.minecraft.hitResult != null && this.minecraft.hitResult.getType() == HitResult.Type.ENTITY) {
      Entity entity = ((EntityHitResult) this.minecraft.hitResult).getEntity();
      if (entity instanceof LivingEntity) {
        LivingEntity deadBody = (LivingEntity) entity;
        if (deadBody != null && deadBody.deathTime > 20) {
          int scaledWidth = this.minecraft.getWindow().getGuiScaledWidth();
          int scaledHeight = this.minecraft.getWindow().getGuiScaledHeight();
          RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
          RenderSystem.setShader(GameRenderer::getPositionTexShader);
          RenderSystem.setShaderTexture(0, new ResourceLocation("rpgz:textures/sprite/loot_bag.png"));
          GuiComponent.blit(matrixStack, (scaledWidth / 2), (scaledHeight / 2) - 16, 0.0F, 0.0F, 16, 16, 16,
              16);
        }
      }
    }

  }

}