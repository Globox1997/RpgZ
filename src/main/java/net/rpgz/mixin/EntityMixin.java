package net.rpgz.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.rpgz.util.RpgHelper;

@Mixin(Entity.class)
public abstract class EntityMixin {

    @Inject(method = "spawnAtLocation(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/item/ItemStack;)Lnet/minecraft/world/entity/item/ItemEntity;", at = @At("HEAD"), cancellable = true)
    private void dropStackMixin(ServerLevel level, ItemStack stack, CallbackInfoReturnable<ItemEntity> info) {
        if ((Object) this instanceof Mob mobEntity && mobEntity.isDeadOrDying() && !RpgHelper.shouldDropLootDirectly(mobEntity)) {
            ItemStack remainder = RpgHelper.getDeadMobInventory(mobEntity).addItem(stack);
            info.setReturnValue(remainder.isEmpty() ? null : this.spawnAtLocation(level, remainder, 0.0F));
        }
    }

    @Shadow
    public abstract ItemEntity spawnAtLocation(ServerLevel level, ItemStack stack, float yOffset);
}
