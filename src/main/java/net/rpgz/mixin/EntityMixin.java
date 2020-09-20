package net.rpgz.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.At;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.server.command.CommandOutput;
import net.minecraft.util.Nameable;
import net.minecraft.world.World;
import net.minecraft.scoreboard.*;
import net.minecraft.block.RedstoneOreBlock;

@Mixin(Entity.class) // Useless right now
public abstract class EntityMixin implements Nameable, CommandOutput {

  @Shadow
  public int age;

  // public EntityMixin(EntityType<?> type, World world) {
  // }

  public EntityMixin(EntityType<? extends Entity> entityType, World world) {
    // super(entityType, world);
  }

  // @Inject(method = "tick", at = @At("HEAD"), cancellable = true)
  // public void moveMixin(CallbackInfo info) {
  //   Object object = this;
  //   if (object instanceof LivingEntity) {
  //     LivingEntity livingEntity = (LivingEntity) object;
  //     if (livingEntity.deathTime > 0) {
  //       // if (!livingEntity.getEntityWorld().isClient) {
  //       //   this.age = 0;
  //       //   System.out.print(this.age);
  //       // }
  //       // this.age = 0.0F;

  //     }

  //   }
  //   // info.cancel();
  // }

  // @Inject(method = "move", at = @At("HEAD"), cancellable = true)
  // public void moveMixin(MovementType type, Vec3d movement, CallbackInfo info) {
  // info.cancel();
  // }

  // @Inject(method = "moveToBoundingBoxCenter", at = @At("HEAD"), cancellable =
  // true)
  // public void moveToBoundingBoxCenterMixin(CallbackInfo info) {
  // info.cancel();
  // // Box box = this.getBoundingBox();
  // /// >/ this.setPos((box.minX + box.maxX) / 2.0D, box.minY, (box.minZ +
  // box.maxZ)
  // // / 2.0D);
  // }

}