package net.rpgz;

import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;

public class RpgzClient {

    public static final Identifier LOOT_BAG_HUD_ID = Identifier.fromNamespaceAndPath("rpgz", "loot_bag");

    private static final Identifier LOOT_BAG_TEXTURE = Identifier.fromNamespaceAndPath("rpgz", "textures/sprite/loot_bag.png");

    public static void renderLootBag(GuiGraphicsExtractor drawContext, DeltaTracker tickCounter) {
        Minecraft client = Minecraft.getInstance();
        if (client.hitResult != null && client.hitResult.getType() == HitResult.Type.ENTITY) {
            Entity entity = ((EntityHitResult) client.hitResult).getEntity();
            if (entity instanceof Mob) {
                Mob deadBody = (Mob) entity;
                if (deadBody != null && deadBody.deathTime > 20) {
                    int scaledWidth = client.getWindow().getGuiScaledWidth();
                    int scaledHeight = client.getWindow().getGuiScaledHeight();
                    drawContext.blit(RenderPipelines.GUI_TEXTURED, LOOT_BAG_TEXTURE, (scaledWidth / 2), (scaledHeight / 2) - 16, 0.0F, 0.0F, 16, 16, 16, 16);
                }
            }
        }
    }

}
