package net.rpgz.mixin;

import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.client.gui.screen.ingame.HandledScreen;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.At;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.EntityHitResult;

@Environment(EnvType.CLIENT)
@Mixin(InGameHud.class)
public abstract class InGameHudMixin extends DrawableHelper {
    @Shadow
    @Final
    @Mutable
    private final MinecraftClient client;

    public InGameHudMixin(MinecraftClient client) {
        this.client = client;
    }

    @Inject(method = "render", at = @At(value = "TAIL"))
    private void renderMixin(MatrixStack matrixStack, float f, CallbackInfo info) {
        this.renderLootBag(matrixStack);
    }

    private void renderLootBag(MatrixStack matrixStack) {
        if (this.client.crosshairTarget != null && this.client.crosshairTarget.getType() == HitResult.Type.ENTITY) {
            Entity entity = ((EntityHitResult) this.client.crosshairTarget).getEntity();
            if (entity instanceof LivingEntity) {
                LivingEntity deadBody = (LivingEntity) entity;
                if (deadBody != null && deadBody.deathTime > 20) {
                    int scaledWidth = this.client.getWindow().getScaledWidth();
                    int scaledHeight = this.client.getWindow().getScaledHeight();
                    RenderSystem.setShaderTexture(0, new Identifier("rpgz:textures/sprite/loot_bag.png"));
                    RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
                    DrawableHelper.drawTexture(matrixStack, (scaledWidth / 2), (scaledHeight / 2) - 16, 0.0F, 0.0F, 16, 16, 16,
                            16);
                }
            }
        }

    }

}