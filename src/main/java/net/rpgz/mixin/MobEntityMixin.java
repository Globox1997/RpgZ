package net.rpgz.mixin;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.control.BodyRotationControl;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.rpgz.access.IInventoryAccess;

@Mixin(Mob.class)
public abstract class MobEntityMixin extends LivingEntity implements IInventoryAccess {

  @Shadow
  @Final
  @Mutable
  private final BodyRotationControl bodyRotationControl;

  public MobEntityMixin(EntityType<? extends LivingEntity> entityType, Level world) {
    super(entityType, world);
    this.bodyRotationControl = this.createBodyControl();
  }

  // Stop turning after death
  @Inject(method = "tickHeadTurn", at = @At("HEAD"), cancellable = true)
  public void updateDistance(float bodyRotation, float headRotation, CallbackInfoReturnable<Float> info) {
    if (this.deathTime > 0) {
    	info.setReturnValue(0.0F);
    }
  }

  @Inject(method = "Lnet/minecraft/world/entity/Mob;isSunBurnTick()Z", at = @At("HEAD"), cancellable = true)
  private void isInDaylightMixin(CallbackInfoReturnable<Boolean> info) {
    if (this.isDeadOrDying()) {
      info.setReturnValue(false);
    }
  }

  @Redirect(method = "dropCustomDeathLoot", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Mob;spawnAtLocation(Lnet/minecraft/world/item/ItemStack;)Lnet/minecraft/world/entity/item/ItemEntity;"))
  private ItemEntity dropSpecialItemsMixin(Mob mobEntity, ItemStack itemStack) {
    this.addingInventoryItems(itemStack);
    return null;
  }

  @Shadow
  protected float getEquipmentDropChance(EquipmentSlot slot) {
    return 1F;
  }

  @Shadow
  protected BodyRotationControl createBodyControl() {
    return new BodyRotationControl(null);
  }

}