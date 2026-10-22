package net.rpgz.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.Vec3d;
import net.rpgz.util.MobCorpse;
import net.rpgz.util.RpgHelper;

@Mixin(Entity.class)
public abstract class EntityMixin {

    @Inject(method = "dropStack(Lnet/minecraft/item/ItemStack;)Lnet/minecraft/entity/ItemEntity;", at = @At("HEAD"), cancellable = true)
    private void dropStackMixin(ItemStack stack, CallbackInfoReturnable<ItemEntity> info) {
        if ((Object) this instanceof MobEntity mobEntity && !mobEntity.getWorld().isClient() && mobEntity.isDead() && !RpgHelper.shouldDropLootDirectly(mobEntity)) {
            ItemStack remainder = RpgHelper.getDeadMobInventory(mobEntity).addStack(stack);
            info.setReturnValue(remainder.isEmpty() ? null : this.dropStack(remainder, 0.0F));
        }
    }

    @Inject(method = "interactAt", at = @At("HEAD"), cancellable = true)
    private void interactAtMixin(PlayerEntity player, Vec3d hitPos, Hand hand, CallbackInfoReturnable<ActionResult> info) {
        if ((Object) this instanceof MobCorpse mobCorpse) {
            ActionResult result = mobCorpse.interactWithCorpse(player, hand);
            if (result != null) {
                info.setReturnValue(result);
            }
        }
    }

    @Shadow
    public abstract ItemEntity dropStack(ItemStack stack, float yOffset);
}
