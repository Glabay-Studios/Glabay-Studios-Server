package io.xeros.content.combat;

import io.xeros.content.combat.weapon.CombatStyle;
import io.xeros.model.CombatType;
import io.xeros.model.entity.Entity;
import io.xeros.model.entity.npc.NPC;
import io.xeros.model.entity.npc.stats.NpcCombatDefinition;
import io.xeros.model.entity.player.Player;

/**
 * @author Arthur Behesnilian 1:56 PM
 */
public class CombatConfigs {

    /**
     * Retrieves the combat style for the Npc
     * @param definition The npc definition
     * @return The combat style for the Npc
     */
    private static CombatStyle getCombatStyleForNpc(NpcCombatDefinition definition) {
        switch(definition.getAttackStyle()) {
            case "Stab":
                return CombatStyle.STAB;
            case "Slash":
                return CombatStyle.SLASH;
            case "Crush":
                return CombatStyle.CRUSH;
            case "Ranged":
                return CombatStyle.RANGE;
            case "Magic":
                return CombatStyle.MAGIC;
            default:
                return CombatStyle.SPECIAL;
        }
    }

    /**
     * Retrieves the Combat style for the different entity types
     * @param entity The entity whose Combat style is being checked
     * @return The combat style for the entity type
     */
    public static CombatStyle getCombatStyle(Entity entity) {
        if (entity.isNPC()) {
            NPC n = entity.asNPC();
            NpcCombatDefinition definition = NpcCombatDefinition.definitions.get(n.getNpcId());
            if (definition != null) {
                return getCombatStyleForNpc(definition);
            }
            return CombatStyle.SPECIAL;
        } else if (entity.isPlayer()) {
            Player player = entity.asPlayer();

            switch (getCombatType(player)) {
                case MAGE:
                    return CombatStyle.MAGIC;
                case RANGE:
                    return CombatStyle.RANGE;
                default:
                    return player.getCombatConfigs().getWeaponMode().getCombatStyle();
            }

        } else {
            throw new IllegalArgumentException("You cannot use that entity type here. ");
        }
    }

    /**
     * Determines the combat type for the player
     * @param player The player
     * @return The combat type of the player
     */
    public static CombatType getCombatType(Player player) {
        if (player.usingMagic) {
            return CombatType.MAGE;
        } else if (player.usingBow || player.usingOtherRangeWeapons || player.usingCross || player.usingBallista) {
            return CombatType.RANGE;
        } else {
            return CombatType.MELEE;
        }
    }

}
