package net.rpgz;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.rpgz.network.RpgClientPacket;

@Environment(EnvType.CLIENT)
public class RpgzClient implements ClientModInitializer {

    private static final ResourceLocation LOOT_BAG_TEXTURE = ResourceLocation.fromNamespaceAndPath("rpgz", "textures/sprite/loot_bag.png");

    @Override
    public void onInitializeClient() {
        HudRenderCallback.EVENT.register((drawContext, tickCounter) -> {
            Minecraft client = Minecraft.getInstance();
            if (client.hitResult != null && client.hitResult.getType() == HitResult.Type.ENTITY) {
                Entity entity = ((EntityHitResult) client.hitResult).getEntity();
                if (entity instanceof Mob) {
                    Mob deadBody = (Mob) entity;
                    if (deadBody != null && deadBody.deathTime > 20) {
                        int scaledWidth = client.getWindow().getGuiScaledWidth();
                        int scaledHeight = client.getWindow().getGuiScaledHeight();
                        drawContext.blit(LOOT_BAG_TEXTURE, (scaledWidth / 2), (scaledHeight / 2) - 16, 0.0F, 0.0F, 16, 16, 16, 16);
                    }
                }
            }
        });
        RpgClientPacket.init();
    }

}