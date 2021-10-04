package net.rpgz.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.At;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.mob.FlyingEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.Fluids;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.context.LootContextTypes;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.tag.FluidTags;
import net.minecraft.text.LiteralText;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.World;
import net.rpgz.access.InventoryAccess;
import net.rpgz.config.Config;
import net.rpgz.tag.Tags;
import net.rpgz.ui.LivingEntityScreenHandler;
import net.minecraft.screen.SimpleNamedScreenHandlerFactory;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity implements InventoryAccess {
    @Shadow
    public int deathTime;
    @Shadow
    public float bodyYaw;
    @Shadow
    protected int playerHitTimer;

    public SimpleInventory inventory = new SimpleInventory(9);

    public LivingEntityMixin(EntityType<? extends LivingEntity> entityType, World world) {
        super(entityType, world);
    }

    @Inject(method = "tickMovement", at = @At("HEAD"), cancellable = true)
    private void tickMovementMixin(CallbackInfo info) {
        LivingEntity livingEntity = (LivingEntity) (Object) this;
        if (this.deathTime > 19 && livingEntity instanceof MobEntity) {
            Box box = this.getBoundingBox();
            BlockPos blockPos = new BlockPos(box.getCenter().getX(), box.minY, box.getCenter().getZ());
            if (this.world.getBlockState(blockPos).isAir()) {
                if (livingEntity instanceof FlyingEntity) {
                    this.setPos(this.getX(), this.getY() - 0.25D, this.getZ());
                } else if (this.getVelocity().y > 0) {
                    this.setPos(this.getX(), this.getY() - (this.getVelocity().y > 0.8D ? 0.8D : this.getVelocity().y), this.getZ());
                } else if (this.getVelocity().y < 0) {
                    this.setPos(this.getX(), this.getY() + (this.getVelocity().y < -0.8D ? -0.8D : this.getVelocity().y) + (this.getVelocity().y > -0.2D ? -0.4D : 0.0D), this.getZ());
                } else {
                    this.setPos(this.getX(), this.getY() - 0.1D, this.getZ());
                }
            } else
            // Water floating
            if (this.world.containsFluid(box.offset(0.0D, box.getYLength(), 0.0D))) {
                if (Config.CONFIG.surfacing_in_water) {
                    this.setPos(this.getX(), this.getY() + 0.03D, this.getZ());
                }

                BlockPos newBlockPos = new BlockPos(box.getCenter().getX(), box.maxY, box.getCenter().getZ());
                if (this.world.getBlockState(newBlockPos).getFluidState().isIn(FluidTags.LAVA) && this.canWalkOnFluid(Fluids.LAVA)) {
                    this.setPos(this.getX(), this.getY() + 0.03D, this.getZ());
                } else if (this.world.containsFluid(box.offset(0.0D, -box.getYLength() + (box.getYLength() / 5), 0.0D)) && !Config.CONFIG.surfacing_in_water) {
                    this.setPos(this.getX(), this.getY() - 0.05D, this.getZ());
                }
            }
            info.cancel();
        }

    }

    /**
     * @author
     */
    @Overwrite
    public void updatePostDeath() {
        ++this.deathTime;

        if (this.isOnFire() && this.deathTime == 1) {
            this.extinguish();
        }
        if (this.getVehicle() != null) {
            this.stopRiding();
        }

        if (this.deathTime >= 20) {
            Box newBoundingBox = new Box(this.getX() - (this.getWidth() / 3.0F), this.getY() - (this.getWidth() / 3.0F), this.getZ() - (this.getWidth() / 3.0F),
                    this.getX() + (this.getWidth() / 1.5F), this.getY() + (this.getWidth() / 1.5F), this.getZ() + (this.getWidth() / 1.5F));
            if ((this.getDimensions(EntityPose.STANDING).height < 1.0F && this.getDimensions(EntityPose.STANDING).width < 1.0F)
                    || (this.getDimensions(EntityPose.STANDING).width / this.getDimensions(EntityPose.STANDING).height) > 1.395F) {
                this.setBoundingBox(newBoundingBox);
            } else {
                this.setBoundingBox(newBoundingBox.offset(this.getRotationVector(0F, this.bodyYaw).rotateY(-30.0F)));
                // this.setBoundingBox(newBoundingBox.offset(this.getRotationVecClient().rotateY(-30.0F)));
                // acceptable solution
            }
            // Chicken always has trouble - not fixable
            // this.checkBlockCollision(); //Doesnt solve problem
            // if (this.isInsideWall()) {} // Doenst work

            Box box = this.getBoundingBox();
            BlockPos blockPos = new BlockPos(box.minX + 0.001D, box.minY + 0.001D, box.minZ + 0.001D).up();
            BlockPos blockPos2 = new BlockPos(box.maxX - 0.001D, box.maxY - 0.001D, box.maxZ - 0.001D);

            // Older method, might be better?
            // if (this.world.isRegionLoaded(blockPos, blockPos2)) {
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
            Box checkBoxThree = new Box(box.maxX - (box.getXLength() / 3D), box.maxY, box.maxZ - (box.getZLength() / 3D), box.maxX + 0.001D - (box.getXLength() / 3D), box.maxY + 0.001D,
                    box.maxZ + 0.001D - (box.getZLength() / 3D));
            if (this.world.isRegionLoaded(blockPos, blockPos2)) {
                if (!world.isClient && !this.inventory.isEmpty()
                        && (((!this.world.getBlockCollisions(this, checkBox).allMatch(VoxelShape::isEmpty) || !this.world.getBlockCollisions(this, checkBoxThree).allMatch(VoxelShape::isEmpty))
                                && (!this.world.getBlockCollisions(this, checkBoxTwo).allMatch(VoxelShape::isEmpty)
                                        || !this.world.getBlockCollisions(this, checkBoxThree).allMatch(VoxelShape::isEmpty)))
                                || this.isBaby() || (Config.CONFIG.drop_unlooted && this.deathTime > Config.CONFIG.drop_after_ticks))
                        || this.getType().isIn(Tags.EXCLUDED_ENTITIES) || Config.CONFIG.excluded_entities.contains(this.getType().toString().replace("entity.", ""))) {

                    this.inventory.clearToList().forEach(this::dropStack);
                }

            }
            // world.getClosestPlayer(this,// 1.0D)// !=// null// || Testing purpose
        }

        if ((this.deathTime >= 20 && !this.world.isClient && this.inventory.isEmpty() && Config.CONFIG.despawn_immediately_when_empty)
                || (this.deathTime == Config.CONFIG.despawn_corps_after_ticks)) {
            if (!this.world.isClient) { // Make sure only on server particle
                this.despawnParticlesServer();
            }
            this.remove(RemovalReason.KILLED);
        }

    }

    private void despawnParticlesServer() {
        for (int i = 0; i < 20; ++i) {
            double d = this.random.nextGaussian() * 0.025D;
            double e = this.random.nextGaussian() * 0.025D;
            double f = this.random.nextGaussian() * 0.025D;
            double x = MathHelper.nextDouble(random, this.getBoundingBox().minX - 0.5D, this.getBoundingBox().maxX) + 0.5D;
            double y = MathHelper.nextDouble(random, this.getBoundingBox().minY, this.getBoundingBox().maxY) + 0.5D;
            double z = MathHelper.nextDouble(random, this.getBoundingBox().minZ - 0.5D, this.getBoundingBox().maxZ) + 0.5D;
            ((ServerWorld) this.world).spawnParticles(ParticleTypes.POOF, x, y, z, 0, d, e, f, 0.01D);
        }
    }

    @Inject(method = "dropLoot", at = @At("HEAD"), cancellable = true)
    private void dropLootMixin(DamageSource source, boolean causedByPlayer, CallbackInfo info) {
        if ((Entity) this instanceof MobEntity) {
            LootTable lootTable = this.world.getServer().getLootManager().getTable(this.getType().getLootTableId());
            LootContext.Builder builder = this.getLootContextBuilder(causedByPlayer, source);
            lootTable.generateLoot(builder.build(LootContextTypes.ENTITY), this::addingInventoryItems);
            info.cancel();
        }

    }

    @Override
    public void addingInventoryItems(ItemStack stack) {
        if (!this.world.isClient && !stack.isEmpty()) {
            this.inventory.addStack(stack);
        }
    }

    @Override
    public ActionResult interactAt(PlayerEntity player, Vec3d hitPos, Hand hand) {
        if (this.deathTime > 20) {
            if (!this.world.isClient)
                if (!this.inventory.isEmpty()) {
                    player.openHandledScreen(new SimpleNamedScreenHandlerFactory((syncId, inv, p) -> new LivingEntityScreenHandler(syncId, p.getInventory(), this.inventory), new LiteralText("")));
                    return ActionResult.SUCCESS;
                } else if ((Object) this instanceof PlayerEntity) {
                    return super.interactAt(player, hitPos, hand);
                }
            return ActionResult.SUCCESS;
        }
        return super.interactAt(player, hitPos, hand);
    }

    @Shadow
    protected LootContext.Builder getLootContextBuilder(boolean causedByPlayer, DamageSource source) {
        return (new LootContext.Builder((ServerWorld) this.world));
    }

    @Shadow
    public boolean canWalkOnFluid(Fluid fluid) {
        return false;
    }

    @Shadow
    public boolean isBaby() {
        return false;
    }

    @Override
    public SimpleInventory getInventory() {
        return this.inventory;
    }

}