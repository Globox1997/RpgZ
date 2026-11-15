package net.rpgz.util;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.Nullable;

public interface MobCorpse {

    public void onCorpseCreated();

    public void tickCorpse();

    @Nullable
    public InteractionResult interactWithCorpse(Player player, InteractionHand hand);
}
