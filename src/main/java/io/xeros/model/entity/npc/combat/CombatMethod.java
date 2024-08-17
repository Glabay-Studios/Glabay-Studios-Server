package io.xeros.model.entity.npc.combat;

import io.xeros.content.combat.core.HitExecutor;
import io.xeros.model.entity.Entity;
import io.xeros.model.entity.player.Player;

public interface CombatMethod {
    boolean prepareAttack(Entity entity, Player target);

    int getAttackSpeed(Entity entity);

    int moveCloseToTargetTileRange(Entity entity);

    default boolean customOnDeath(HitExecutor hit) {
        return false;
    }

    default boolean canMultiAttackInSingleZones() {
        return false;
    }

    default boolean ignoreEntityInteraction() {
        return false;
    }
}
