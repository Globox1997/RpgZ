package net.rpgz.mixin;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.At;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.control.BodyControl;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.rpgz.access.InventoryAccess;

@Mixin(MobEntity.class)
public abstract class MobEntityMixin extends LivingEntity implements InventoryAccess {

  @Shadow
  @Final
  @Mutable
  private final BodyControl bodyControl;

  public MobEntityMixin(EntityType<? extends LivingEntity> entityType, World world) {
    super(entityType, world);
    this.bodyControl = this.createBodyControl();
  }

  // Stop turning after death
  @Inject(method = "turnHead", at = @At("HEAD"), cancellable = true)
  public void turnHead(float bodyRotation, float headRotation, CallbackInfoReturnable<Float> info) {
    if (this.deathTime > 0) {
      info.setReturnValue(0.0F);
    }
  }

  @Inject(method = "Lnet/minecraft/entity/mob/MobEntity;isAffectedByDaylight()Z", at = @At("HEAD"), cancellable = true)
  private void isAffectedByDaylightMixin(CallbackInfoReturnable<Boolean> info) {
    if (this.isDead()) {
      info.setReturnValue(false);
    }
  }

  @Redirect(method = "dropEquipment", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/mob/MobEntity;dropStack(Lnet/minecraft/item/ItemStack;)Lnet/minecraft/entity/ItemEntity;"))
  private ItemEntity dropEquipmentMixin(MobEntity mobEntity, ItemStack itemStack) {
    this.addingInventoryItems(itemStack);
    return null;
  }

  @Shadow
  protected float getDropChance(EquipmentSlot slot) {
    return 1F;
  }

  @Shadow
  protected BodyControl createBodyControl() {
    return new BodyControl(null);
  }

}