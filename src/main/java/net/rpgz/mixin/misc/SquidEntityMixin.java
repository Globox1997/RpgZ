package net.rpgz.mixin.misc;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.AgeableWaterCreature;
import net.minecraft.world.entity.animal.squid.Squid;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(Squid.class)
public abstract class SquidEntityMixin extends AgeableWaterCreature {

    public SquidEntityMixin(EntityType<? extends AgeableWaterCreature> entityType, Level world) {
        super(entityType, world);
    }

    @Inject(method = "aiStep", at = @At(value = "HEAD"), cancellable = true)
    public void tickMovementMixinSquid(CallbackInfo info) {
        if (this.isDeadOrDying()) {
            super.aiStep();
            info.cancel();
        }
    }

}