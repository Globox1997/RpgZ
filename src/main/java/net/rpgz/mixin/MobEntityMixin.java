package net.rpgz.mixin;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.ai.controller.BodyController;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;
import net.rpgz.access.AddingInventoryItems;

@Mixin(MobEntity.class)
public abstract class MobEntityMixin extends LivingEntity implements AddingInventoryItems {

  @Shadow
  @Final
  @Mutable
  private final BodyController bodyController;

  public MobEntityMixin(EntityType<? extends LivingEntity> entityType, World world) {
    super(entityType, world);
    this.bodyController = this.createBodyController();
  }

  // Stop turning after death
  @Overwrite
  public float updateDistance(float bodyRotation, float headRotation) {
    if (this.deathTime > 0) {
      return 0.0F;
    } else
      this.bodyController.updateRenderAngles();
    return headRotation;
  }

  // @Inject(method = "dropSpecialItems", at = @At(value = "INVOKE", target =
  // "Lnet/minecraft/entity/Entity;dropStack(Lnet/minecraft/item/ItemStack;)Lnet/minecraft/entity/ItemEntity;"))
  // public void dropEquipmentMixin(ItemStack itemStack,DamageSource source, int
  // lootingMultiplier, boolean allowDrops,CallbackInfo info) {
  // this.addingInventoryItems(itemStack);
  // info.cancel();
  // }
  // Lnet/minecraft/entity/mob/MobEntity;isInDaylight()Z

  @Inject(method = "Lnet/minecraft/entity/MobEntity;isInDaylight()Z", at = @At("HEAD"), cancellable = true)
  private void isInDaylightMixin(CallbackInfoReturnable<Boolean> info) {
    if (this.getShouldBeDead()) {
      info.setReturnValue(false);
    }
  }

  @Overwrite
  public void dropSpecialItems(DamageSource source, int lootingMultiplier, boolean allowDrops) {
    super.dropSpecialItems(source, lootingMultiplier, allowDrops);
    EquipmentSlotType[] var4 = EquipmentSlotType.values();
    int var5 = var4.length;

    for (int var6 = 0; var6 < var5; ++var6) {
      EquipmentSlotType equipmentSlot = var4[var6];
      ItemStack itemStack = this.getItemStackFromSlot(equipmentSlot);
      float f = this.getDropChance(equipmentSlot);
      boolean bl = f > 1.0F;
      if (!itemStack.isEmpty() && !EnchantmentHelper.hasVanishingCurse(itemStack) && (allowDrops || bl)
          && Math.max(this.rand.nextFloat() - (float) lootingMultiplier * 0.01F, 0.0F) < f) {
        if (!bl && itemStack.isDamageable()) {
          itemStack.setDamage(itemStack.getMaxDamage()
              - this.rand.nextInt(1 + this.rand.nextInt(Math.max(itemStack.getMaxDamage() - 3, 1))));
        }
        this.addingInventoryItems(itemStack);
        this.setItemStackToSlot(equipmentSlot, ItemStack.EMPTY);
      }
    }

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