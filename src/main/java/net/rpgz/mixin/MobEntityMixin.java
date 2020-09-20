package net.rpgz.mixin;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;

import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.control.BodyControl;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
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

  // // // Stop moving after death
  // @Inject(method = "tickMovement", at = @At("HEAD"), cancellable = true)
  // private void tickMovementMixin(CallbackInfo info) {
  // LivingEntity jo = this;
  // if (jo instanceof PlayerEntity) {
  // // System.out.print("yaw:" + this.yaw + "bodyyaw:" + this.bodyYaw);
  // // System.out.println(this.getOppositeRotationVector(1F));
  // System.out.println(this.bodyYaw);

  // }
  // // if (this.deathTime > 19 && lol instanceof FlyingEntity) {
  // // // lol.setVelocity(0D, -1D, 0D);
  // // this.setNoGravity(false);
  // // info.cancel();
  // // }
  // if (this.deathTime > 0) {// && this.onGround) { // && !world.isClient
  // if (this.onGround) {
  // // this.setAiDisabled(true);
  // // info.cancel();
  // }
  // // this.setNoGravity(false);
  // // this.setAiDisabled(true);
  // // info.cancel();
  // }
  // }

  // @Redirect(method = "dropEquipment", at = @At(value = "INVOKE", target =
  // "Lnet/minecraft/entity/Entity;dropStack(Lnet/minecraft/item/ItemStack;)Lnet/minecraft/entity/ItemEntity;"))
  // private ItemStack renderMixin(ItemStack itemStack) {
  // this.addingInventoryItems(itemStack);
  // return itemStack.EMPTY;
  // }

  // @ModifyArg(at=@At(value="INVOKE",target="signature of
  // dropstack"),method="whatever method")
  // private ItemStack addtoinv(ItemStack originalStack) {
  // this.addingInventoryItems(originalStack)
  // return ItemStack.EMPTY;
  // }

  // @ModifyArg(method = "dropEquipment", at = @At(value = "INVOKE", target =
  // "Lnet/minecraft/entity/Entity;dropStack(Lnet/minecraft/item/ItemStack;)Lnet/minecraft/entity/ItemEntity;"))
  // // (Lnet/minecraft/item/ItemStack;)Lnet/minecraft/entity/ItemEntity;
  // private ItemEntity dropEquipmentMixin(ItemStack itemStack) {

  // this.addingInventoryItems(itemStack);
  // return new ItemEntity(world, serverHeadYaw, serverHeadYaw, serverHeadYaw);
  // }

  // @Redirect(method = "dropEquipment", at = @At(value = "INVOKE", target =
  // "Lnet/minecraft/entity/Entity;dropStack(Lnet/minecraft/item/ItemStack;)Lnet/minecraft/entity/ItemEntity;"))
  // public ItemEntity dropEquipmentMixin(MobEntity lol,Entity entity, ItemStack
  // itemStack3) {
  // return null;
  // //return null;
  // // this.addingInventoryItems(itemStack);
  // // return null;

  // }

  // @Redirect(method =
  // "Lnet/minecraft/client/render/entity/LivingEntityRenderer;setupTransforms(Lnet/minecraft/entity/LivingEntity;Lnet/minecraft/client/util/math/MatrixStack;FFF)V",
  // at = @At(value = "INVOKE", target =
  // "Lnet/minecraft/client/render/entity/LivingEntityRenderer;isShaking(Lnet/minecraft/entity/LivingEntity;)Z"))
  // public boolean isShakingMixin(LivingEntityRenderer<T, M> renderer, T entity,
  // T secondentity, MatrixStack matrix,
  // float o, float k, float m) {
  // if (entity.isDead() || !this.isShaking(entity)) {
  // return false;
  // } else
  // return true;
  // }

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