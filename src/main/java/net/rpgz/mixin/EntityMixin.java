package net.rpgz.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import net.rpgz.util.MobCorpse;
import net.rpgz.util.RpgHelper;

@Mixin(Entity.class)
public abstract class EntityMixin {

    @Inject(method = "spawnAtLocation(Lnet/minecraft/world/item/ItemStack;)Lnet/minecraft/world/entity/item/ItemEntity;", at = @At("HEAD"), cancellable = true)
    private void dropStackMixin(ItemStack stack, CallbackInfoReturnable<ItemEntity> info) {
        if ((Object) this instanceof Mob mobEntity && !mobEntity.level().isClientSide() && mobEntity.isDeadOrDying() && !RpgHelper.shouldDropLootDirectly(mobEntity)) {
            ItemStack remainder = RpgHelper.getDeadMobInventory(mobEntity).addItem(stack);
            info.setReturnValue(remainder.isEmpty() ? null : this.spawnAtLocation(remainder, 0.0F));
        }
    }

    @Inject(method = "interactAt", at = @At("HEAD"), cancellable = true)
    private void interactAtMixin(Player player, Vec3 hitPos, InteractionHand hand, CallbackInfoReturnable<InteractionResult> info) {
        if ((Object) this instanceof MobCorpse mobCorpse) {
            InteractionResult result = mobCorpse.interactWithCorpse(player, hand);
            if (result != null) {
                info.setReturnValue(result);
            }
        }
    }

    @Shadow
    public abstract ItemEntity spawnAtLocation(ItemStack stack, float yOffset);
}
