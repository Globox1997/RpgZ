package net.rpgz.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.At;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.util.ActionResult;
import net.minecraft.util.hit.HitResult;

@Mixin(MinecraftClient.class)
public class MinecraftClientMixin {
  @Shadow
  public ClientPlayerInteractionManager interactionManager;
  @Shadow
  public ClientPlayerEntity player;
  @Shadow
  public HitResult crosshairTarget;
  @Shadow
  protected int attackCooldown;

  // @Inject(method = "tick", at = @At("HEAD"), cancellable = true)
  // public void tick(CallbackInfo info) {
  //   System.out.print(crosshairTarget);
  // }

  // @Inject(method = "doAttack", at = @At("HEAD"), cancellable = true)
  // private void doAttackMixin(CallbackInfo info) {
  //   System.out.print("test");
  //   if (this.attackCooldown <= 0) {

  //   }
  // }

  // @Inject(method = "doItemUse", at = @At("HEAD"), cancellable = true)
  // private void doItemUseMixin(CallbackInfo info) {
  //   // ActionResult actionResult =
  //   // this.interactionManager.interactEntityAtLocation(this.player, entity,
  //   // entityHitResult, hand);
  // }

}
