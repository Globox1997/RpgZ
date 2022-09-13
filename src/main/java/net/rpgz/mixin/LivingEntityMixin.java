package net.rpgz.mixin;

import java.util.stream.StreamSupport;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.FlyingMob;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ShovelItem;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.rpgz.access.IInventoryAccess;
import net.rpgz.init.ConfigInit;
import net.rpgz.init.TagInit;
import net.rpgz.ui.LivingEntityScreenHandler;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity implements IInventoryAccess {
	@Shadow
	public int deathTime;
	@Shadow
	public float yBodyRot;
	@Shadow
	protected int lastHurtByPlayerTime;

	private final boolean isEntityInstanceOfMobEnity = (Object) this instanceof Mob;

	SimpleContainer dropInventory = new SimpleContainer(9);

	public LivingEntityMixin(EntityType<? extends LivingEntity> entityType, Level world) {
		super(entityType, world);
	}

	@Inject(method = "aiStep", at = @At("HEAD"), cancellable = true)
	private void livingTickMixin(CallbackInfo info) {
		LivingEntity livingEntity = (LivingEntity) (Object) this;
		if (this.deathTime > 19 && isEntityInstanceOfMobEnity) {
			AABB box = this.getBoundingBox();
			BlockPos blockPos = new BlockPos(box.getCenter().x(), box.minY, box.getCenter().z());
			if (this.level.getBlockState(blockPos).isAir()) {
				if (livingEntity instanceof FlyingMob) {
					this.setPosRaw(this.getX(), this.getY() - 0.25D, this.getZ());
				} else if (this.getDeltaMovement().y > 0) {
					this.setPosRaw(this.getX(), this.getY() - (this.getDeltaMovement().y > 0.8D ? 0.8D : this.getDeltaMovement().y),
							this.getZ());
				} else if (this.getDeltaMovement().y < 0) {
					this.setPosRaw(this.getX(), this.getY() + (this.getDeltaMovement().y < -0.8D ? -0.8D : this.getDeltaMovement().y)
							+ (this.getDeltaMovement().y > -0.2D ? -0.4D : 0.0D), this.getZ());
				} else {
					this.setPosRaw(this.getX(), this.getY() - 0.1D, this.getZ());
				}
			} else
				// Water floating
				if (this.level.containsAnyLiquid(box.move(0.0D, box.getYsize(), 0.0D))) {
					if (ConfigInit.CONFIG.surfacing_in_water)
						this.setPosRaw(this.getX(), this.getY() + 0.03D, this.getZ());
					if (this.canStandOnFluid(this.level.getFluidState(this.blockPosition())))
						this.setPosRaw(this.getX(), this.getY() + 0.03D, this.getZ());
					else if (this.level.containsAnyLiquid(box.move(0.0D, -box.getYsize() + (box.getYsize() / 5), 0.0D))
							&& !ConfigInit.CONFIG.surfacing_in_water)
						this.setPosRaw(this.getX(), this.getY() - 0.05D, this.getZ());
				}
			info.cancel();
		}

	}

	@Inject(method = "tickDeath", at = @At("HEAD"), cancellable = true)
	protected void tickDeathMixin(CallbackInfo info) {
		if (isEntityInstanceOfMobEnity) {
			++this.deathTime;
			if (this.deathTime == 1) {
				if (this.isOnFire())
					this.clearFire();
				if (this.getVehicle() != null)
					this.stopRiding();
			}

			if (this.deathTime >= 20) {
				// Has to get set on server and client
				AABB newBoundingBox = new AABB(this.getX() - (this.getBbWidth() / 3.0F), this.getY() - (this.getBbWidth() / 3.0F), this.getZ() - (this.getBbWidth() / 3.0F),
						this.getX() + (this.getBbWidth() / 1.5F), this.getY() + (this.getBbWidth() / 1.5F), this.getZ() + (this.getBbWidth() / 1.5F));
				if ((this.getDimensions(Pose.STANDING).height < 1.0F && this.getDimensions(Pose.STANDING).width < 1.0F)
						|| (this.getDimensions(Pose.STANDING).width / this.getDimensions(Pose.STANDING).height) > 1.395F) {
					this.setBoundingBox(newBoundingBox);
				} else {
					this.setBoundingBox(newBoundingBox.move(this.calculateViewVector(0F, this.yBodyRot).yRot(-30.0F)));
					// this.setBoundingBox(newBoundingBox.offset(this.getRotationVecClient().yRot(-30.0F)));
					// acceptable solution
				}
				// Chicken always has trouble - not fixable
				// Shulker has trouble
				// this.checkBlockCollision(); //Doesnt solve problem
				// if (this.isInsideWall()) {} // Doenst work
				if (!level.isClientSide) {
					AABB box = this.getBoundingBox();
					BlockPos blockPos = new BlockPos(box.minX + 0.001D, box.minY + 0.001D, box.minZ + 0.001D).above();
					BlockPos blockPos2 = new BlockPos(box.maxX - 0.001D, box.maxY - 0.001D, box.maxZ - 0.001D);

					// Older method, might be better?
					// if (this.level.isRegionLoaded(blockPos, blockPos2)) {
					// if (!level.isClientSide && !this.inventory.isEmpty()
					// && (level.getBlockState(blockPos).isFullCube(level, blockPos)
					// || level.getBlockState(blockPos2).isFullCube(level, blockPos2) ||
					// this.isBaby()
					// || (Config.CONFIG.drop_unlooted && this.deathTime >
					// Config.CONFIG.drop_after_ticks))
					// || this.getType().isIn(Tags.EXCLUDED_ENTITIES)
					// ||
					// Config.CONFIG.excluded_entities.contains(this.getType().toString().replace("entity.",
					// ""))) {
					// this.inventory.removeAllItems().forEach(this::spawnAtLocation);
					// }
					// }

					// New method to check if inside block
					AABB checkBox = new AABB(box.maxX, box.maxY, box.maxZ, box.maxX + 0.001D, box.maxY + 0.001D, box.maxZ + 0.001D);
					AABB checkBoxTwo = new AABB(box.minX, box.maxY, box.minZ, box.minX + 0.001D, box.maxY + 0.001D, box.minZ + 0.001D);
					AABB checkBoxThree = new AABB(box.maxX - (box.getXsize() / 3D), box.maxY, box.maxZ - (box.getZsize() / 3D), box.maxX + 0.001D - (box.getXsize() / 3D), box.maxY + 0.001D,
							box.maxZ + 0.001D - (box.getZsize() / 3D));
					if (this.level.hasChunksAt(blockPos, blockPos2))
						if (!this.dropInventory.isEmpty()
								&& (((!StreamSupport.stream(this.level.getBlockCollisions(this, checkBox).spliterator(), false).allMatch(VoxelShape::isEmpty)
										|| !StreamSupport.stream(this.level.getBlockCollisions(this, checkBoxThree).spliterator(), false).allMatch(VoxelShape::isEmpty))
										&& (!StreamSupport.stream(this.level.getBlockCollisions(this, checkBoxTwo).spliterator(), false).allMatch(VoxelShape::isEmpty)
												|| !StreamSupport.stream(this.level.getBlockCollisions(this, checkBoxThree).spliterator(), false).allMatch(VoxelShape::isEmpty)))
										|| this.isBaby() || (ConfigInit.CONFIG.drop_unlooted && this.deathTime > ConfigInit.CONFIG.drop_after_ticks))
								|| this.getType().is(TagInit.EXCLUDED_ENTITIES) || ConfigInit.CONFIG.excluded_entities.contains(this.getType().toString().replace("entity.", "").replace(".", ":")))

							this.dropInventory.removeAllItems().forEach(this::spawnAtLocation);
				}
				// level.getClosestPlayer(this,// 1.0D)// !=// null// || Testing purpose
			}

			if ((!this.level.isClientSide && this.deathTime >= 20 && this.dropInventory.isEmpty() && ConfigInit.CONFIG.despawn_immediately_when_empty)
					|| (this.deathTime >= ConfigInit.CONFIG.despawn_corps_after_ticks)) {
				if (!this.level.isClientSide) // Make sure only on server particle
					this.despawnParticlesServer();

				this.remove(RemovalReason.KILLED);
			}
			info.cancel();
		}
	}

	private void despawnParticlesServer() {
		for (int i = 0; i < 20; ++i) {
			double d = this.random.nextGaussian() * 0.025D;
			double e = this.random.nextGaussian() * 0.025D;
			double f = this.random.nextGaussian() * 0.025D;
			double x = Mth.nextDouble(random, this.getBoundingBox().minX - 0.5D, this.getBoundingBox().maxX) + 0.5D;
			double y = Mth.nextDouble(random, this.getBoundingBox().minY, this.getBoundingBox().maxY) + 0.5D;
			double z = Mth.nextDouble(random, this.getBoundingBox().minZ - 0.5D, this.getBoundingBox().maxZ) + 0.5D;
			((ServerLevel) this.level).sendParticles(ParticleTypes.POOF, x, y, z, 0, d, e, f, 0.01D);
		}
	}

	@Inject(method = "dropFromLootTable", at = @At("HEAD"), cancellable = true)
	private void dropLootMixin(DamageSource source, boolean causedByPlayer, CallbackInfo info) {
		if (isEntityInstanceOfMobEnity) {
			LootTable lootTable = this.level.getServer().getLootTables().get(this.getType().getDefaultLootTable());
			LootContext.Builder builder = this.createLootContext(causedByPlayer, source);
			lootTable.getRandomItems(builder.create(LootContextParamSets.ENTITY), this::addingInventoryItems);
			info.cancel();
		}

	}

	@Override
	public void addingInventoryItems(ItemStack stack) {
		if (!stack.isEmpty() && !this.level.isClientSide)
			this.dropInventory.addItem(stack);
	}

	@Override
	public InteractionResult interactAt(Player player, Vec3 hitPos, InteractionHand hand) {
		if (this.deathTime > 20) {
			if (!this.level.isClientSide) {
				if (player.getItemInHand(hand).getItem() instanceof ShovelItem) {
					if (!this.dropInventory.isEmpty())
						for (int i = 0; i < this.dropInventory.getContainerSize(); i++)
							player.getInventory().placeItemBackInInventory(this.dropInventory.getItem(i));
					this.dropInventory.clearContent();
					if (!ConfigInit.CONFIG.despawn_immediately_when_empty) {
						this.despawnParticlesServer();
						this.remove(RemovalReason.KILLED);
					}
					return InteractionResult.SUCCESS;
				}
				if (!this.dropInventory.isEmpty()) {
					if (player.isShiftKeyDown()) {
						for (int i = 0; i < this.dropInventory.getContainerSize(); i++)
							player.getInventory().placeItemBackInInventory(this.dropInventory.getItem(i));
						this.dropInventory.clearContent();
					}
					else
						player.openMenu(new SimpleMenuProvider(
								(syncId, inv, p) -> new LivingEntityScreenHandler(syncId, p.getInventory(), this.dropInventory), Component.literal("")));
					return InteractionResult.SUCCESS;
				}
			} else if ((Object) this instanceof Player) {
				return super.interactAt(player, hitPos, hand);
			}
			return InteractionResult.SUCCESS;
		}
		return super.interactAt(player, hitPos, hand);
	}

	@Shadow
	protected LootContext.Builder createLootContext(boolean causedByPlayer, DamageSource source) {
		return (new LootContext.Builder((ServerLevel) this.level));
	}

	@Shadow
	public boolean canStandOnFluid(FluidState fluidState) {
		return false;
	}

	@Shadow
	public boolean isBaby() {
		return false;
	}

	@Override
	public SimpleContainer getDropsInventory() {
		return dropInventory;
	}

	@Override
	public void setDropsInventory(SimpleContainer inventory) {
		this.dropInventory = inventory;

	}

}