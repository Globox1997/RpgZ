package net.rpgz.mixin.client;

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
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.entity.Entity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.EntityHitResult;

@Environment(EnvType.CLIENT)
@Mixin(InGameHud.class)
public abstract class InGameHudMixin {

    @Shadow
    @Final
    @Mutable
    private MinecraftClient client;

    private static final Identifier LOOT_BAG_TEXTURE = new Identifier("rpgz", "textures/sprite/loot_bag.png");

    public InGameHudMixin(MinecraftClient client) {
    }

    @Inject(method = "render", at = @At(value = "TAIL"))
    private void renderMixin(DrawContext context, float f, CallbackInfo info) {
        this.renderLootBag(context);
    }

    private void renderLootBag(DrawContext context) {
        if (this.client.crosshairTarget != null && this.client.crosshairTarget.getType() == HitResult.Type.ENTITY) {
            Entity entity = ((EntityHitResult) this.client.crosshairTarget).getEntity();
            if (entity instanceof MobEntity) {
                MobEntity deadBody = (MobEntity) entity;
                if (deadBody != null && deadBody.deathTime > 20) {
                    int scaledWidth = this.client.getWindow().getScaledWidth();
                    int scaledHeight = this.client.getWindow().getScaledHeight();
                    context.drawTexture(LOOT_BAG_TEXTURE, (scaledWidth / 2), (scaledHeight / 2) - 16, 0.0F, 0.0F, 16, 16, 16, 16);
                }
            }
        }

    }

}