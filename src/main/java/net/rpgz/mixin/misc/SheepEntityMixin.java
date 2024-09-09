package net.rpgz.mixin.misc;

import net.minecraft.item.ItemConvertible;
import net.minecraft.util.DyeColor;
import net.rpgz.util.RpgHelper;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.passive.SheepEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;

import java.util.Map;

@Mixin(SheepEntity.class)
public abstract class SheepEntityMixin extends AnimalEntity {

    @Shadow
    @Mutable
    @Final
    private static Map<DyeColor, ItemConvertible> DROPS;

    public SheepEntityMixin(EntityType<? extends AnimalEntity> entityType, World world) {
        super(entityType, world);
    }

    @Override
    public void dropLoot(DamageSource source, boolean causedByPlayer) {
        super.dropLoot(source, causedByPlayer);
        if ((Object) this instanceof SheepEntity sheepEntity) {
            RpgHelper.getDeadMobInventory(sheepEntity).addStack(new ItemStack(DROPS.get(sheepEntity.getColor())));
        }

    }
}
