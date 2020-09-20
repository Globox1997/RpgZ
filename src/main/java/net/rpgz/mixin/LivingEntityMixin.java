package net.rpgz.mixin;

import java.util.Iterator;
import java.util.List;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.At;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
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
import net.minecraft.world.World;
import net.rpgz.access.AddingInventoryItems;
import net.rpgz.ui.LivingEntityScreenHandler;
import net.minecraft.screen.SimpleNamedScreenHandlerFactory;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity implements AddingInventoryItems {
  @Shadow
  public int deathTime;
  @Shadow
  public float bodyYaw;
  @Shadow
  protected int playerHitTimer;

  SimpleInventory inventory = new SimpleInventory(9);

  public LivingEntityMixin(EntityType<? extends LivingEntity> entityType, World world) {
    super(entityType, world);
  }

  @Inject(method = "tickMovement", at = @At("HEAD"), cancellable = true)
  private void tickMovementMixin(CallbackInfo info) {

    // Entity jo = this;
    // if (jo instanceof PlayerEntity) {
    // // System.out.print("yaw:" + this.yaw + "bodyyaw:" + this.bodyYaw);
    // // System.out.println(this.getOppositeRotationVector(1F));
    // if (this.world.isClient) {
    // System.out.println(this.bodyYaw);
    // }

    // }
    Entity entity = this;
    LivingEntity livingEntity = (LivingEntity) entity;
    if (this.deathTime > 19 && livingEntity instanceof MobEntity) {

      if (this.world.getBlockState(this.getBlockPos()).isAir()) {
        if (livingEntity instanceof FlyingEntity) {
          this.setPos(this.getX(), this.getY() - 0.25D, this.getZ());
        } else if (!this.onGround) {
          if (this.getVelocity().y > 0) {
            this.setPos(this.getX(), this.getY() - this.getVelocity().y, this.getZ());
          } else if (this.getVelocity().y < 0) {
            this.setPos(this.getX(), this.getY() + this.getVelocity().y, this.getZ());
          } else {
            this.setPos(this.getX(), this.getY() - 0.1D, this.getZ());
          }

        }
      } else if (this.world.containsFluid(this.getBoundingBox().offset(0.0D,
          -this.getBoundingBox().getYLength() + (this.getBoundingBox().getYLength() / 5), 0.0D))) {
        // if (livingEntity instanceof WaterCreatureEntity) {
        // double fluidHeight = this.getFluidHeight(FluidTags.WATER);
        // if (livingEntity.isTouchingWater() && fluidHeight > 0.0D) {
        // this.setPos(this.getX(), this.getY() + 0.1D, this.getZ());
        // }
        // } else
        if (this.world.getBlockState(this.getBlockPos()).getFluidState().isIn(FluidTags.LAVA)
            && this.canWalkOnFluid(Fluids.LAVA)) {
          this.setPos(this.getX(), this.getY() + 0.1D, this.getZ());
        } else if (!this.onGround) {
          this.setPos(this.getX(), this.getY() - 0.06D, this.getZ());
        }
      }
      info.cancel();
    }

  }

  @Overwrite
  public void updatePostDeath() {
    ++this.deathTime;
    // if (this.world.isClient) {
    // this.age = 0;
    // }

    if (this.isOnFire() && this.deathTime == 1) {
      this.setFireTicks(0);
    }
    if (this.getVehicle() != null) {
      this.stopRiding();
    }
    // Entity entity = this;
    // if (entity instanceof ZombieEntity) {
    // System.out.println("X:" +
    // Vector3f.POSITIVE_X.getDegreesQuaternion(90).getX());
    // System.out.println("Z:" +
    // Vector3f.POSITIVE_Y.getDegreesQuaternion(90).getZ());
    // System.out.println("W:" +
    // Vector3f.POSITIVE_Y.getDegreesQuaternion(90).getW());
    // }

    if (this.deathTime >= 20) {
      Box newBoundingBox = new Box(this.getX() - (this.getWidth() / 3.0F), this.getY() - (this.getWidth() / 3.0F),
          this.getZ() - (this.getWidth() / 3.0F), this.getX() + (this.getWidth() / 1.5F),
          this.getY() + (this.getWidth() / 1.5F), this.getZ() + (this.getWidth() / 1.5F));
      if ((this.getDimensions(EntityPose.STANDING).height < 1.0F
          && this.getDimensions(EntityPose.STANDING).width < 1.0F)
          || (this.getDimensions(EntityPose.STANDING).width
              / this.getDimensions(EntityPose.STANDING).height) > 1.395F) {
        this.setBoundingBox(newBoundingBox);
      } else {
        this.setBoundingBox(newBoundingBox.offset(this.getRotationVector(0F, this.yaw).rotateY(-30.0F))); // this.bodyYaw
      } // entity.getCameraPosVec(f)
      
      //this.checkBlockCollision(); //solution to fix in wall bug

      if (this.isInsideWall()) { //Doenst work
     }
     
        Box box = this.getBoundingBox();
        BlockPos blockPos = new BlockPos(box.minX + 0.001D, box.minY + 0.001D, box.minZ + 0.001D).up();
        BlockPos blockPos2 = new BlockPos(box.maxX - 0.001D, box.maxY - 0.001D, box.maxZ - 0.001D);
       // BlockPos.Mutable mutable = new BlockPos.Mutable();
        if (this.world.isRegionLoaded(blockPos, blockPos2)) {
          if(!world.isClient){
          //  world.setBlockState(blockPos, Blocks.GOLD_BLOCK.getDefaultState(), 3);
           // world.setBlockState(blockPos2, Blocks.GOLD_BLOCK.getDefaultState(), 3);
          //  world.setBlockState(mutable, Blocks.DIAMOND_BLOCK.getDefaultState(), 3);
          if(world.getBlockState(blockPos).isFullCube(world, blockPos)||world.getBlockState(blockPos2).isFullCube(world, blockPos2)){
            if (this.inventory != null) {
              for(int i = 0; i < this.inventory.size(); ++i) {
                 ItemStack itemStack = this.inventory.getStack(i);
                 if (!itemStack.isEmpty() ) {//&& !EnchantmentHelper.hasVanishingCurse(itemStack)
                    this.dropStack(itemStack);
                 }
              }
     
           }

      //  this.dropInventory();
      //  System.out.println("drop::");
      //  notInsideBlock = false;
      //  boolean bl = this.playerHitTimer > 0;
      //  this.dropLoot(DamageSource.IN_WALL,bl);
          }
                                  }
          //  for(int i = blockPos.getX(); i <= blockPos2.getX(); ++i) {
          //     for(int j = blockPos.getY(); j <= blockPos2.getY(); ++j) {
          //        for(int k = blockPos.getZ(); k <= blockPos2.getZ(); ++k) {
          //           mutable.set(i, j, k);
          //           BlockState blockState = this.world.getBlockState(mutable);
                    
          //           if(blockState.isSolidBlock(world, mutable)){
          //             System.out.println("allesklarlauftnicht");

          //           }
          //           // try {
          //           //    blockState.onEntityCollision(this.world, mutable, this);
          //           //    this.onBlockCollision(blockState);
          //           // } catch(Throwable var12){
                 
          //           // }
          //         }
          //       }
          //     }
            }

    }
    if ((this.deathTime >= 20 && !this.world.isClient && this.inventory.isEmpty())
        || (this.deathTime == 4800)) { //world.getClosestPlayer(this, 1.0D) != null || 
      if (!this.world.isClient) { // Make sure only on server particle
        this.despawnParticlesServer();
      }
      this.remove();
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
    Entity entity = this;
    if (entity instanceof MobEntity) {
      LootTable lootTable = this.world.getServer().getLootManager().getTable(this.getType().getLootTableId());
      LootContext.Builder builder = this.getLootContextBuilder(causedByPlayer, source);
      lootTable.generateLoot(builder.build(LootContextTypes.ENTITY), this::addingInventoryItems);
      info.cancel();
    }

  }

  @Override
  public void addingInventoryItems(ItemStack stack) {
    if (stack.isEmpty()) {
      return;
    } else if (this.world.isClient) {
      return;
    } else {
      this.inventory.addStack(stack);
    }
  }

  @Override
  public ActionResult interactAt(PlayerEntity player, Vec3d hitPos, Hand hand) {
    if (world.isClient && this.deathTime > 20) {
      return ActionResult.SUCCESS;
    } else if (!world.isClient && this.deathTime > 20 && !this.inventory.isEmpty()) {
      player.openHandledScreen(new SimpleNamedScreenHandlerFactory(
          (syncId, inv, p) -> new LivingEntityScreenHandler(syncId, p.inventory, this.inventory), new LiteralText("")));
      return ActionResult.SUCCESS;
    } else
      return ActionResult.PASS;
  }

  @Shadow
  protected LootContext.Builder getLootContextBuilder(boolean causedByPlayer, DamageSource source) {
    return (new LootContext.Builder((ServerWorld) this.world));
  }

  @Shadow
  public boolean canWalkOnFluid(Fluid fluid) {
    return false;
  }

  //Test
  @Shadow
  protected void dropInventory() {
  }

  @Shadow
  public void dropLoot(DamageSource source, boolean causedByPlayer) {
  }

//   public void dropAll() {
//     Iterator<ItemStack> var1 = this.inventory.clearToList().iterator();

//     while(var1.hasNext()) {
//        List<ItemStack> list = (List)var1.next();

//        for(int i = 0; i < list.size(); ++i) {
//           ItemStack itemStack = (ItemStack)list.get(i);
//           if (!itemStack.isEmpty()) {
//              this.dropItem(itemStack.getItem(), 1);
//              list.set(i, ItemStack.EMPTY);
//           }
//        }
//     }

//  }

}