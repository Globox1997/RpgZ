package net.rpgz.mixin;

import java.util.stream.StreamSupport;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.FlyingMob;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.npc.InventoryCarrier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ShovelItem;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;
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
import net.rpgz.init.ConfigInit;
import net.rpgz.screen.MobEntityScreenHandler;

@Mixin(Mob.class)
public abstract class MobEntityMixin extends LivingEntity implements DeadMobInventory, MobCorpse {

    @Unique
    private static final int DESPAWN_WARNING_TICKS = 200;

    @Unique
    private final SimpleContainer deadMobInventory = new SimpleContainer(9);

    public MobEntityMixin(EntityType<? extends LivingEntity> entityType, Level world) {
        super(entityType, world);
    }

    @Inject(method = "addAdditionalSaveData", at = @At("TAIL"))
    private void writeCustomDataToNbtMixin(CompoundTag nbt, CallbackInfo info) {
        if (this.isDeadOrDying()) {
            this.writeDeadMobInventory(nbt, this.registryAccess());
        }
    }

    @Inject(method = "readAdditionalSaveData", at = @At("TAIL"))
    private void readCustomDataFromNbtMixin(CompoundTag nbt, CallbackInfo info) {
        if (this.isDeadOrDying()) {
            this.readDeadMobInventory(nbt, this.registryAccess());
        }
    }

    @Inject(method = "aiStep", at = @At("HEAD"), cancellable = true)
    private void tickMovementMixin(CallbackInfo info) {
        if (this.deathTime > 19) {
            AABB box = this.getBoundingBox();
            BlockPos blockPos = BlockPos.containing(box.getCenter().x(), box.minY, box.getCenter().z());
            if (this.level().getBlockState(blockPos).isAir()) {
                if ((Object) this instanceof FlyingMob) {
                    this.setPosRaw(this.getX(), this.getY() - 0.25D, this.getZ());
                } else if (this.getDeltaMovement().y > 0) {
                    this.setPosRaw(this.getX(), this.getY() - (Math.min(this.getDeltaMovement().y, 0.8D)), this.getZ());
                } else if (this.getDeltaMovement().y < 0) {
                    this.setPosRaw(this.getX(), this.getY() + (Math.max(this.getDeltaMovement().y, -0.8D)) + (this.getDeltaMovement().y > -0.2D ? -0.4D : 0.0D), this.getZ());
                } else {
                    this.setPosRaw(this.getX(), this.getY() - 0.1D, this.getZ());
                }
            } else
                // Water floating
                if (this.level().containsAnyLiquid(box.move(0.0D, box.getYsize(), 0.0D))) {
                    if (ConfigInit.CONFIG.surfacing_in_water)
                        this.setPosRaw(this.getX(), this.getY() + 0.03D, this.getZ());
                    if (this.canStandOnFluid(this.level().getFluidState(this.blockPosition())))
                        this.setPosRaw(this.getX(), this.getY() + 0.03D, this.getZ());
                    else if (this.level().containsAnyLiquid(box.move(0.0D, -box.getYsize() + (box.getYsize() / 5), 0.0D)) && !ConfigInit.CONFIG.surfacing_in_water)
                        this.setPosRaw(this.getX(), this.getY() - 0.05D, this.getZ());
                }
            info.cancel();
        }
    }

    @Override
    public void onCorpseCreated() {
        if (!this.level().isClientSide()) {
            this.ejectPassengers();
            // Clear the inventory, some mobs (piglin, allay) drop it again via dropStack in onDeath
            if (this instanceof InventoryCarrier inventoryOwner && !inventoryOwner.getInventory().isEmpty()) {
                for (ItemStack stack : inventoryOwner.getInventory().removeAllItems()) {
                    this.spawnAtLocation(stack);
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
                this.clearFire();
            }
            if (this.getVehicle() != null) {
                this.stopRiding();
            }
        }

        if (this.deathTime >= 20) {
            this.setBoundingBox(CorpseBox.getBoundingBox((Mob) (Object) this));

            if (!this.level().isClientSide()) {
                AABB box = this.getBoundingBox();
                BlockPos blockPos = BlockPos.containing(box.minX + 0.001D, box.minY + 0.001D, box.minZ + 0.001D).above();
                BlockPos blockPos2 = BlockPos.containing(box.maxX - 0.001D, box.maxY - 0.001D, box.maxZ - 0.001D);

                if (this.level().hasChunksAt(blockPos, blockPos2) && !this.getDeadMobInventory().isEmpty()
                        && (RpgHelper.shouldDropLootDirectly((Mob) (Object) this) || this.isCorpseInsideBlock(box))) {
                    for (ItemStack stack : this.getDeadMobInventory().removeAllItems()) {
                        this.spawnAtLocation(stack, 0.0F);
                    }
                }

                int ticksUntilDespawn = ConfigInit.CONFIG.despawn_corps_after_ticks - this.deathTime;
                if (ConfigInit.CONFIG.despawn_warning && ticksUntilDespawn > 0 && ticksUntilDespawn <= DESPAWN_WARNING_TICKS && ticksUntilDespawn % 10 == 0
                        && !this.getDeadMobInventory().isEmpty()) {
                    this.despawnWarningParticlesServer();
                }
            }
        }

        if (!this.level().isClientSide() && this.deathTime >= 20
                && ((this.getDeadMobInventory().isEmpty() && ConfigInit.CONFIG.despawn_immediately_when_empty) || this.deathTime >= ConfigInit.CONFIG.despawn_corps_after_ticks)) {
            this.despawnParticlesServer();
            this.remove(RemovalReason.KILLED);
        }
    }

    @Unique
    private boolean isCorpseInsideBlock(AABB box) {
        AABB checkBox = new AABB(box.maxX, box.maxY, box.maxZ, box.maxX + 0.001D, box.maxY + 0.001D, box.maxZ + 0.001D);
        AABB checkBoxTwo = new AABB(box.minX, box.maxY, box.minZ, box.minX + 0.001D, box.maxY + 0.001D, box.minZ + 0.001D);
        AABB checkBoxThree = new AABB(box.maxX - (box.getXsize() / 3D), box.maxY, box.maxZ - (box.getZsize() / 3D), box.maxX + 0.001D - (box.getXsize() / 3D), box.maxY + 0.001D,
                box.maxZ + 0.001D - (box.getZsize() / 3D));
        boolean collidesThree = this.hasBlockCollision(checkBoxThree);
        return (collidesThree || this.hasBlockCollision(checkBox)) && (collidesThree || this.hasBlockCollision(checkBoxTwo));
    }

    @Unique
    private boolean hasBlockCollision(AABB box) {
        return !StreamSupport.stream(this.level().getBlockCollisions(this, box).spliterator(), false).allMatch(VoxelShape::isEmpty);
    }

    @Unique
    private void despawnParticlesServer() {
        for (int i = 0; i < 20; ++i) {
            double d = this.random.nextGaussian() * 0.025D;
            double e = this.random.nextGaussian() * 0.025D;
            double f = this.random.nextGaussian() * 0.025D;
            double x = Mth.nextDouble(random, this.getBoundingBox().minX - 0.5D, this.getBoundingBox().maxX) + 0.5D;
            double y = Mth.nextDouble(random, this.getBoundingBox().minY, this.getBoundingBox().maxY) + 0.5D;
            double z = Mth.nextDouble(random, this.getBoundingBox().minZ - 0.5D, this.getBoundingBox().maxZ) + 0.5D;
            ((ServerLevel) this.level()).sendParticles(ParticleTypes.POOF, x, y, z, 0, d, e, f, 0.01D);
        }
    }

    @Unique
    private void despawnWarningParticlesServer() {
        AABB box = this.getBoundingBox();
        Vec3 center = box.getCenter();
        ((ServerLevel) this.level()).sendParticles(ParticleTypes.SMOKE, center.x(), box.maxY, center.z(), 3, box.getXsize() / 4.0D, 0.05D, box.getZsize() / 4.0D, 0.01D);
    }

    // Stop turning after death
    @Inject(method = "tickHeadTurn", at = @At("HEAD"), cancellable = true)
    public void turnHead(float bodyRotation, float headRotation, CallbackInfoReturnable<Float> info) {
        if (this.deathTime > 0) {
            info.setReturnValue(0.0F);
        }
    }

    @Inject(method = "isSunBurnTick", at = @At("HEAD"), cancellable = true)
    private void isAffectedByDaylightMixin(CallbackInfoReturnable<Boolean> info) {
        if (this.isDeadOrDying()) {
            info.setReturnValue(false);
        }
    }

    @Override
    public SimpleContainer getDeadMobInventory() {
        return this.deadMobInventory;
    }

    @Override
    @Nullable
    public InteractionResult interactWithCorpse(Player player, InteractionHand hand) {
        if (this.deathTime <= 20) {
            return null;
        }
        if (!this.level().isClientSide()) {
            if (player.getItemInHand(hand).getItem() instanceof ShovelItem) {
                if (!this.getDeadMobInventory().isEmpty()) {
                    for (int i = 0; i < this.getDeadMobInventory().getContainerSize(); i++) {
                        player.getInventory().placeItemBackInInventory(this.getDeadMobInventory().getItem(i));
                    }
                    this.getDeadMobInventory().clearContent();
                }
                if (!ConfigInit.CONFIG.despawn_immediately_when_empty) {
                    this.despawnParticlesServer();
                    this.remove(RemovalReason.KILLED);
                }
                return InteractionResult.SUCCESS;
            }
            if (!this.getDeadMobInventory().isEmpty()) {
                if (player.isShiftKeyDown()) {
                    for (int i = 0; i < this.getDeadMobInventory().getContainerSize(); i++) {
                        player.getInventory().placeItemBackInInventory(this.getDeadMobInventory().getItem(i));
                    }
                    this.getDeadMobInventory().clearContent();
                } else {
                    player.openMenu(new SimpleMenuProvider((syncId, inv, p) -> new MobEntityScreenHandler(syncId, p.getInventory(), this.getDeadMobInventory(), (Mob) (Object) this), this.getName()));
                }
                return InteractionResult.SUCCESS;
            }
        }
        return InteractionResult.SUCCESS;
    }

}
