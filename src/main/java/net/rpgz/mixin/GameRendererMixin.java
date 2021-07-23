package net.rpgz.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.projectile.ProjectileHelper;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@Mixin(GameRenderer.class)
@OnlyIn(Dist.CLIENT)
public class GameRendererMixin {
  @Shadow
  private final Minecraft mc;

  public GameRendererMixin(Minecraft mc) {
    this.mc = mc;
  }

  @Inject(method = "getMouseOver", at = @At(value = "INVOKE", target = "Lnet/minecraft/profiler/IProfiler;endSection()V"))
  public void getMouseOverMixin(float tickDelta, CallbackInfo info) {
    Entity entity = this.mc.getRenderViewEntity();
    if (this.mc.objectMouseOver.getType() == RayTraceResult.Type.BLOCK) {
      BlockPos pos = ((BlockRayTraceResult) this.mc.objectMouseOver).getPos();
      if (!this.mc.world.getBlockState(pos).hasOpaqueCollisionShape(this.mc.world, pos)) {
        double reachDinstance = (double) this.mc.playerController.getBlockReachDistance();
        Vector3d vec3d = this.mc.player.getEyePosition(tickDelta);
        Vector3d vec3d2 = this.mc.player.getLook(tickDelta);
        Vector3d vec3d3 = vec3d.add(vec3d2.x * reachDinstance, vec3d2.y * reachDinstance, vec3d2.z * reachDinstance);
        AxisAlignedBB box = entity.getBoundingBox().expand(vec3d2.scale(reachDinstance)).expand(1.0D, 1.0D, 1.0D);
        EntityRayTraceResult entityHitResult = ProjectileHelper.rayTraceEntities(entity, vec3d, vec3d3, box, (entityx) -> {
          return !entityx.isSpectator() && entityx.canBeCollidedWith();
        }, 5D);
        if (entityHitResult != null) {
          this.mc.objectMouseOver = entityHitResult;
        }

      }

    }
  }
}
// this.mc.player.getMainHandStack().getItem() instanceof SwordItem