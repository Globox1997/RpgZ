package net.rpgz.mixin;

import java.util.stream.StreamSupport;

import net.minecraft.entity.*;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.*;
import net.rpgz.util.CorpseBox;
import net.rpgz.util.DeadMobInventory;
import net.rpgz.util.MobCorpse;
import net.rpgz.util.RpgHelper;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.At;

import net.minecraft.entity.mob.FlyingEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ShovelItem;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.screen.SimpleNamedScreenHandlerFactory;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.World;
import net.rpgz.init.ConfigInit;
import net.rpgz.screen.MobEntityScreenHandler;

@Mixin(MobEntity.class)
public abstract class MobEntityMixin extends LivingEntity implements DeadMobInventory, MobCorpse {

    @Unique
    private static final int DESPAWN_WARNING_TICKS = 200;

    @Unique
    private final SimpleInventory deadMobInventory = new SimpleInventory(9);

    public MobEntityMixin(EntityType<? extends LivingEntity> entityType, World world) {
        super(entityType, world);
    }

    @Inject(method = "writeCustomDataToNbt", at = @At("TAIL"))
    private void writeCustomDataToNbtMixin(NbtCompound nbt, CallbackInfo info) {
        if (this.isDead()) {
            this.writeDeadMobInventory(nbt, this.getRegistryManager());
        }
    }

    @Inject(method = "readCustomDataFromNbt", at = @At("TAIL"))
    private void readCustomDataFromNbtMixin(NbtCompound nbt, CallbackInfo info) {
        if (this.isDead()) {
            this.readDeadMobInventory(nbt, this.getRegistryManager());
        }
    }

    @Inject(method = "tickMovement", at = @At("HEAD"), cancellable = true)
    private void tickMovementMixin(CallbackInfo info) {
        if (this.deathTime > 19) {
            Box box = this.getBoundingBox();
            BlockPos blockPos = BlockPos.ofFloored(box.getCenter().getX(), box.minY, box.getCenter().getZ());
            if (this.getWorld().getBlockState(blockPos).isAir()) {
                if ((Object) this instanceof FlyingEntity) {
                    this.setPos(this.getX(), this.getY() - 0.25D, this.getZ());
                } else if (this.getVelocity().y > 0) {
                    this.setPos(this.getX(), this.getY() - (Math.min(this.getVelocity().y, 0.8D)), this.getZ());
                } else if (this.getVelocity().y < 0) {
                    this.setPos(this.getX(), this.getY() + (Math.max(this.getVelocity().y, -0.8D)) + (this.getVelocity().y > -0.2D ? -0.4D : 0.0D), this.getZ());
                } else {
                    this.setPos(this.getX(), this.getY() - 0.1D, this.getZ());
                }
            } else
                // Water floating
                if (this.getWorld().containsFluid(box.offset(0.0D, box.getLengthY(), 0.0D))) {
                    if (ConfigInit.CONFIG.surfacing_in_water)
                        this.setPos(this.getX(), this.getY() + 0.03D, this.getZ());
                    if (this.canWalkOnFluid(this.getWorld().getFluidState(this.getBlockPos())))
                        this.setPos(this.getX(), this.getY() + 0.03D, this.getZ());
                    else if (this.getWorld().containsFluid(box.offset(0.0D, -box.getLengthY() + (box.getLengthY() / 5), 0.0D)) && !ConfigInit.CONFIG.surfacing_in_water)
                        this.setPos(this.getX(), this.getY() - 0.05D, this.getZ());
                }
            info.cancel();
        }
    }

    @Override
    public void onCorpseCreated() {
        if (!this.getWorld().isClient()) {
            this.removeAllPassengers();
            // Clear the inventory, some mobs (piglin, allay) drop it again via dropStack in onDeath
            if (this instanceof InventoryOwner inventoryOwner && !inventoryOwner.getInventory().isEmpty()) {
                for (ItemStack stack : inventoryOwner.getInventory().clearToList()) {
                    this.dropStack(stack);
                }
            }
        }
    }

    @SuppressWarnings("deprecation")
    @Override
    public void tickCorpse() {
        ++this.deathTime;
        if (this.deathTime == 1) {
            if (this.isOnFire()) {
                this.extinguish();
            }
            if (this.getVehicle() != null) {
                this.stopRiding();
            }
        }

        if (this.deathTime >= 20) {
            this.setBoundingBox(CorpseBox.getBoundingBox((MobEntity) (Object) this));

            if (!this.getWorld().isClient()) {
                Box box = this.getBoundingBox();
                BlockPos blockPos = BlockPos.ofFloored(box.minX + 0.001D, box.minY + 0.001D, box.minZ + 0.001D).up();
                BlockPos blockPos2 = BlockPos.ofFloored(box.maxX - 0.001D, box.maxY - 0.001D, box.maxZ - 0.001D);

                if (this.getWorld().isRegionLoaded(blockPos, blockPos2) && !this.getDeadMobInventory().isEmpty()
                        && (RpgHelper.shouldDropLootDirectly((MobEntity) (Object) this) || this.isCorpseInsideBlock(box))) {
                    for (ItemStack stack : this.getDeadMobInventory().clearToList()) {
                        this.dropStack(stack, 0.0F);
                    }
                }

                int ticksUntilDespawn = ConfigInit.CONFIG.despawn_corps_after_ticks - this.deathTime;
                if (ConfigInit.CONFIG.despawn_warning && ticksUntilDespawn > 0 && ticksUntilDespawn <= DESPAWN_WARNING_TICKS && ticksUntilDespawn % 10 == 0
                        && !this.getDeadMobInventory().isEmpty()) {
                    this.despawnWarningParticlesServer();
                }
            }
        }

        if (!this.getWorld().isClient() && this.deathTime >= 20
                && ((this.getDeadMobInventory().isEmpty() && ConfigInit.CONFIG.despawn_immediately_when_empty) || this.deathTime >= ConfigInit.CONFIG.despawn_corps_after_ticks)) {
            this.despawnParticlesServer();
            this.remove(RemovalReason.KILLED);
        }
    }

    @Unique
    private boolean isCorpseInsideBlock(Box box) {
        Box checkBox = new Box(box.maxX, box.maxY, box.maxZ, box.maxX + 0.001D, box.maxY + 0.001D, box.maxZ + 0.001D);
        Box checkBoxTwo = new Box(box.minX, box.maxY, box.minZ, box.minX + 0.001D, box.maxY + 0.001D, box.minZ + 0.001D);
        Box checkBoxThree = new Box(box.maxX - (box.getLengthX() / 3D), box.maxY, box.maxZ - (box.getLengthZ() / 3D), box.maxX + 0.001D - (box.getLengthX() / 3D), box.maxY + 0.001D,
                box.maxZ + 0.001D - (box.getLengthZ() / 3D));
        boolean collidesThree = this.hasBlockCollision(checkBoxThree);
        return (collidesThree || this.hasBlockCollision(checkBox)) && (collidesThree || this.hasBlockCollision(checkBoxTwo));
    }

    @Unique
    private boolean hasBlockCollision(Box box) {
        return !StreamSupport.stream(this.getWorld().getBlockCollisions(this, box).spliterator(), false).allMatch(VoxelShape::isEmpty);
    }

    @Unique
    private void despawnParticlesServer() {
        for (int i = 0; i < 20; ++i) {
            double d = this.random.nextGaussian() * 0.025D;
            double e = this.random.nextGaussian() * 0.025D;
            double f = this.random.nextGaussian() * 0.025D;
            double x = MathHelper.nextDouble(random, this.getBoundingBox().minX - 0.5D, this.getBoundingBox().maxX) + 0.5D;
            double y = MathHelper.nextDouble(random, this.getBoundingBox().minY, this.getBoundingBox().maxY) + 0.5D;
            double z = MathHelper.nextDouble(random, this.getBoundingBox().minZ - 0.5D, this.getBoundingBox().maxZ) + 0.5D;
            ((ServerWorld) this.getWorld()).spawnParticles(ParticleTypes.POOF, x, y, z, 0, d, e, f, 0.01D);
        }
    }

    @Unique
    private void despawnWarningParticlesServer() {
        Box box = this.getBoundingBox();
        Vec3d center = box.getCenter();
        ((ServerWorld) this.getWorld()).spawnParticles(ParticleTypes.SMOKE, center.getX(), box.maxY, center.getZ(), 3, box.getLengthX() / 4.0D, 0.05D, box.getLengthZ() / 4.0D, 0.01D);
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

    @Override
    public SimpleInventory getDeadMobInventory() {
        return this.deadMobInventory;
    }

    @Override
    @Nullable
    public ActionResult interactWithCorpse(PlayerEntity player, Hand hand) {
        if (this.deathTime <= 20) {
            return null;
        }
        if (!this.getWorld().isClient()) {
            if (player.getStackInHand(hand).getItem() instanceof ShovelItem) {
                if (!this.getDeadMobInventory().isEmpty()) {
                    for (int i = 0; i < this.getDeadMobInventory().size(); i++) {
                        player.getInventory().offerOrDrop(this.getDeadMobInventory().getStack(i));
                    }
                    this.getDeadMobInventory().clear();
                }
                if (!ConfigInit.CONFIG.despawn_immediately_when_empty) {
                    this.despawnParticlesServer();
                    this.remove(RemovalReason.KILLED);
                }
                return ActionResult.SUCCESS;
            }
            if (!this.getDeadMobInventory().isEmpty()) {
                if (player.isSneaking()) {
                    for (int i = 0; i < this.getDeadMobInventory().size(); i++) {
                        player.getInventory().offerOrDrop(this.getDeadMobInventory().getStack(i));
                    }
                    this.getDeadMobInventory().clear();
                } else {
                    player.openHandledScreen(new SimpleNamedScreenHandlerFactory((syncId, inv, p) -> new MobEntityScreenHandler(syncId, p.getInventory(), this.getDeadMobInventory(), (MobEntity) (Object) this), this.getName()));
                }
                return ActionResult.SUCCESS;
            }
        }
        return ActionResult.SUCCESS;
    }

}
