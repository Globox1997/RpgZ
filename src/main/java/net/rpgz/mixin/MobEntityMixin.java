package net.rpgz.mixin;

import java.util.stream.StreamSupport;

import net.minecraft.entity.*;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.*;
import net.rpgz.util.DeadMobInventory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.At;

import net.minecraft.entity.damage.DamageSource;
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
import net.rpgz.init.TagInit;
import net.rpgz.screen.MobEntityScreenHandler;

@Mixin(MobEntity.class)
public abstract class MobEntityMixin extends LivingEntity implements DeadMobInventory {

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

    @SuppressWarnings("deprecation")
    @Override
    protected void updatePostDeath() {
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
            // Has to get set on server and client
            Box newBoundingBox = new Box(this.getX() - (this.getWidth() / 3.0F), this.getY() - (this.getWidth() / 3.0F), this.getZ() - (this.getWidth() / 3.0F),
                    this.getX() + (this.getWidth() / 1.5F), this.getY() + (this.getWidth() / 1.5F), this.getZ() + (this.getWidth() / 1.5F));
            if ((this.getDimensions(EntityPose.STANDING).height() < 1.0F && this.getDimensions(EntityPose.STANDING).width() < 1.0F)
                    || (this.getDimensions(EntityPose.STANDING).width() / this.getDimensions(EntityPose.STANDING).height()) > 1.395F) {
                this.setBoundingBox(newBoundingBox);
            } else {
                this.setBoundingBox(newBoundingBox.offset(this.getRotationVector(0F, this.bodyYaw).rotateY(-30.0F)));
                // this.setBoundingBox(newBoundingBox.offset(this.getRotationVecClient().rotateY(-30.0F)));
                // acceptable solution
            }
            // Chicken always has trouble - not fixable
            // Shulker has trouble
            // this.checkBlockCollision(); //Doesnt solve problem
            // if (this.isInsideWall()) {} // Doenst work
            if (!this.getWorld().isClient()) {
                Box box = this.getBoundingBox();
                BlockPos blockPos = BlockPos.ofFloored(box.minX + 0.001D, box.minY + 0.001D, box.minZ + 0.001D).up();
                BlockPos blockPos2 = BlockPos.ofFloored(box.maxX - 0.001D, box.maxY - 0.001D, box.maxZ - 0.001D);

                // Older method, might be better?
                // if (this.getWorld().isRegionLoaded(blockPos, blockPos2)) {
                // if (!world.isClient && !this.inventory.isEmpty()
                // && (world.getBlockState(blockPos).isFullCube(world, blockPos)
                // || world.getBlockState(blockPos2).isFullCube(world, blockPos2) ||
                // this.isBaby()
                // || (Config.CONFIG.drop_unlooted && this.deathTime >
                // Config.CONFIG.drop_after_ticks))
                // || this.getType().isIn(Tags.EXCLUDED_ENTITIES)
                // ||
                // Config.CONFIG.excluded_entities.contains(this.getType().toString().replace("entity.",
                // ""))) {
                // this.inventory.clearToList().forEach(this::dropStack);
                // }
                // }

                // New method to check if inside block
                Box checkBox = new Box(box.maxX, box.maxY, box.maxZ, box.maxX + 0.001D, box.maxY + 0.001D, box.maxZ + 0.001D);
                Box checkBoxTwo = new Box(box.minX, box.maxY, box.minZ, box.minX + 0.001D, box.maxY + 0.001D, box.minZ + 0.001D);
                Box checkBoxThree = new Box(box.maxX - (box.getLengthX() / 3D), box.maxY, box.maxZ - (box.getLengthZ() / 3D), box.maxX + 0.001D - (box.getLengthX() / 3D), box.maxY + 0.001D,
                        box.maxZ + 0.001D - (box.getLengthZ() / 3D));
                if (this.getWorld().isRegionLoaded(blockPos, blockPos2)) {
                    if (!this.getDeadMobInventory().isEmpty()
                            && (((!StreamSupport.stream(this.getWorld().getBlockCollisions(this, checkBox).spliterator(), false).allMatch(VoxelShape::isEmpty)
                            || !StreamSupport.stream(this.getWorld().getBlockCollisions(this, checkBoxThree).spliterator(), false).allMatch(VoxelShape::isEmpty))
                            && (!StreamSupport.stream(this.getWorld().getBlockCollisions(this, checkBoxTwo).spliterator(), false).allMatch(VoxelShape::isEmpty)
                            || !StreamSupport.stream(this.getWorld().getBlockCollisions(this, checkBoxThree).spliterator(), false).allMatch(VoxelShape::isEmpty)))
                            || this.isBaby() || (ConfigInit.CONFIG.drop_unlooted && this.deathTime > ConfigInit.CONFIG.drop_after_ticks))
                            || this.getType().isIn(TagInit.EXCLUDED_ENTITIES) || ConfigInit.CONFIG.excluded_entities.contains(this.getType().toString().replace("entity.", "").replace(".", ":"))) {
                        this.getDeadMobInventory().clearToList().forEach(this::dropStack);
                    }
                }
            }
            // world.getClosestPlayer(this,// 1.0D)// !=// null// || Testing purpose
        }

        if ((!this.getWorld().isClient() && this.deathTime >= 20 && (this.getDeadMobInventory() == null || this.getDeadMobInventory().isEmpty()) && ConfigInit.CONFIG.despawn_immediately_when_empty)
                || (this.deathTime >= ConfigInit.CONFIG.despawn_corps_after_ticks)) {
            if (!this.getWorld().isClient()) { // Make sure only on server particle
                this.despawnParticlesServer();
            }

            this.remove(RemovalReason.KILLED);
        }
    }

    @Override
    public void onDeath(DamageSource damageSource) {
        if (!this.getWorld().isClient()) {
            if (this.hasPassengers()) {
                for (int i = 0; i < this.getPassengerList().size(); i++) {
                    this.getPassengerList().get(i).dismountVehicle();
                }
            }
            if (this instanceof InventoryOwner inventoryOwner && !inventoryOwner.getInventory().isEmpty()) {
                for (ItemStack stack : inventoryOwner.getInventory().getHeldStacks()) {
                    this.getDeadMobInventory().addStack(stack);
                }

            }
        }
        super.onDeath(damageSource);
    }

    @Override
    public ItemEntity dropStack(ItemStack stack) {
        if (this.isBaby() || (ConfigInit.CONFIG.drop_unlooted && this.deathTime > ConfigInit.CONFIG.drop_after_ticks)
                || this.getType().isIn(TagInit.EXCLUDED_ENTITIES) || ConfigInit.CONFIG.excluded_entities.contains(this.getType().toString().replace("entity.", "").replace(".", ":"))) {
            return super.dropStack(stack);
        } else if (this.isDead()) {
            this.getDeadMobInventory().addStack(stack);
            return null;
        } else {
            return super.dropStack(stack);
        }
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
    public ActionResult interactAt(PlayerEntity player, Vec3d hitPos, Hand hand) {
        if (this.deathTime > 20) {
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
                        player.openHandledScreen(new SimpleNamedScreenHandlerFactory((syncId, inv, p) -> new MobEntityScreenHandler(syncId, p.getInventory(), this.getDeadMobInventory()), this.getName()));
                    }
                    return ActionResult.SUCCESS;
                }
            }
            return ActionResult.SUCCESS;
        }
        return super.interactAt(player, hitPos, hand);
    }

}