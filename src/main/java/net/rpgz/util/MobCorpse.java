package net.rpgz.util;

import org.jetbrains.annotations.Nullable;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;

public interface MobCorpse {

    public void onCorpseCreated();

    public void tickCorpse();

    @Nullable
    public ActionResult interactWithCorpse(PlayerEntity player, Hand hand);
}
