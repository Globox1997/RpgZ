package net.rpgz;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.rpgz.network.RpgClientPacket;

@Environment(EnvType.CLIENT)
public class RpgzClient implements ClientModInitializer {

    private static final Identifier LOOT_BAG_TEXTURE = Identifier.of("rpgz", "textures/sprite/loot_bag.png");

    @Override
    public void onInitializeClient() {
        HudRenderCallback.EVENT.register((drawContext, tickCounter) -> {
            MinecraftClient client = MinecraftClient.getInstance();
            if (client.crosshairTarget != null && client.crosshairTarget.getType() == HitResult.Type.ENTITY) {
                Entity entity = ((EntityHitResult) client.crosshairTarget).getEntity();
                if (entity instanceof MobEntity) {
                    MobEntity deadBody = (MobEntity) entity;
                    if (deadBody != null && deadBody.deathTime > 20) {
                        int scaledWidth = client.getWindow().getScaledWidth();
                        int scaledHeight = client.getWindow().getScaledHeight();
                        drawContext.drawTexture(LOOT_BAG_TEXTURE, (scaledWidth / 2), (scaledHeight / 2) - 16, 0.0F, 0.0F, 16, 16, 16, 16);
                    }
                }
            }
        });
        RpgClientPacket.init();
    }

}