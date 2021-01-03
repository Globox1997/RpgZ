package net.rpgz.mixin;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.At;

import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.control.BodyControl;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.rpgz.access.AddingInventoryItems;

@Mixin(MobEntity.class)
public abstract class MobEntityMixin extends LivingEntity implements AddingInventoryItems {

  @Shadow
  @Final
  @Mutable
  private final BodyControl bodyControl;

  public MobEntityMixin(EntityType<? extends LivingEntity> entityType, World world) {
    super(entityType, world);
    this.bodyControl = this.createBodyControl();
  }

  // Stop turning after death
  @Overwrite
  public float turnHead(float bodyRotation, float headRotation) {
    if (this.deathTime > 0) {
      return 0.0F;
    } else
      this.bodyControl.tick();
    return headRotation;
  }

  // @Inject(method = "dropEquipment", at = @At(value = "INVOKE", target =
  // "Lnet/minecraft/entity/Entity;dropStack(Lnet/minecraft/item/ItemStack;)Lnet/minecraft/entity/ItemEntity;"))
  // public void dropEquipmentMixin(ItemStack itemStack,DamageSource source, int
  // lootingMultiplier, boolean allowDrops,CallbackInfo info) {
  // this.addingInventoryItems(itemStack);
  // info.cancel();
  // }
  // Lnet/minecraft/entity/mob/MobEntity;isAffectedByDaylight()Z

  @Inject(method = "Lnet/minecraft/entity/mob/MobEntity;isAffectedByDaylight()Z", at = @At("HEAD"), cancellable = true)
  private void isAffectedByDaylightMixin(CallbackInfoReturnable<Boolean> info) {
    if (this.isDead()) {
      info.setReturnValue(false);
    }
  }

  @Overwrite
  public void dropEquipment(DamageSource source, int lootingMultiplier, boolean allowDrops) {
    super.dropEquipment(source, lootingMultiplier, allowDrops);
    EquipmentSlot[] var4 = EquipmentSlot.values();
    int var5 = var4.length;

    for (int var6 = 0; var6 < var5; ++var6) {
      EquipmentSlot equipmentSlot = var4[var6];
      ItemStack itemStack = this.getEquippedStack(equipmentSlot);
      float f = this.getDropChance(equipmentSlot);
      boolean bl = f > 1.0F;
      if (!itemStack.isEmpty() && !EnchantmentHelper.hasVanishingCurse(itemStack) && (allowDrops || bl)
          && Math.max(this.random.nextFloat() - (float) lootingMultiplier * 0.01F, 0.0F) < f) {
        if (!bl && itemStack.isDamageable()) {
          itemStack.setDamage(itemStack.getMaxDamage()
              - this.random.nextInt(1 + this.random.nextInt(Math.max(itemStack.getMaxDamage() - 3, 1))));
        }
        this.addingInventoryItems(itemStack);
        this.equipStack(equipmentSlot, ItemStack.EMPTY);
      }
    }

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