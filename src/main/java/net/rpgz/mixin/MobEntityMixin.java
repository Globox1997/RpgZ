package net.rpgz.mixin;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.ai.controller.BodyController;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.rpgz.access.IInventoryAccess;

@Mixin(MobEntity.class)
public abstract class MobEntityMixin extends LivingEntity implements IInventoryAccess {

  @Shadow
  @Final
  @Mutable
  private final BodyController bodyController;

  public MobEntityMixin(EntityType<? extends LivingEntity> entityType, World world) {
    super(entityType, world);
    this.bodyController = this.createBodyController();
  }

  // Stop turning after death
  @Inject(method = "updateDistance", at = @At("HEAD"), cancellable = true)
  public void updateDistance(float bodyRotation, float headRotation, CallbackInfoReturnable<Float> info) {
    if (this.deathTime > 0) {
    	info.setReturnValue(0.0F);
    }
  }

  @Inject(method = "Lnet/minecraft/entity/MobEntity;isInDaylight()Z", at = @At("HEAD"), cancellable = true)
  private void isInDaylightMixin(CallbackInfoReturnable<Boolean> info) {
    if (this.getShouldBeDead()) {
      info.setReturnValue(false);
    }
  }

  @Redirect(method = "dropSpecialItems", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/MobEntity;entityDropItem(Lnet/minecraft/item/ItemStack;)Lnet/minecraft/entity/item/ItemEntity;"))
  private ItemEntity dropSpecialItemsMixin(MobEntity mobEntity, ItemStack itemStack) {
    this.addingInventoryItems(itemStack);
    return null;
  }

  @Shadow
  protected float getDropChance(EquipmentSlotType slot) {
    return 1F;
  }

  @Shadow
  protected BodyController createBodyController() {
    return new BodyController(null);
  }

}