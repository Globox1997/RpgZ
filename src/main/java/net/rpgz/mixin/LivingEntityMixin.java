package net.rpgz.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;
import org.spongepowered.asm.mixin.injection.At;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.context.LootContextParameterSet;
import net.minecraft.util.Identifier;
import net.rpgz.access.InventoryAccess;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin implements InventoryAccess {

    private final boolean isEntityInstanceOfMobEnity = (Object) this instanceof MobEntity;

    // generateLoot
    @Inject(method = "dropLoot", at = @At(value = "INVOKE", target = "Lnet/minecraft/loot/LootTable;generateLoot(Lnet/minecraft/loot/context/LootContextParameterSet;JLjava/util/function/Consumer;)V"), cancellable = true, locals = LocalCapture.CAPTURE_FAILSOFT)
    private void dropLootMixin(DamageSource source, boolean causedByPlayer, CallbackInfo info, Identifier identifier, LootTable lootTable, LootContextParameterSet.Builder builder,
            LootContextParameterSet lootContextParameterSet) {
        if (isEntityInstanceOfMobEnity) {
            lootTable.generateLoot(lootContextParameterSet, this.getLootTableSeed(), this::addInventoryItem);
            info.cancel();
        }
    }

    @Shadow
    public long getLootTableSeed() {
        return 0L;
    }

}