package io.xeros.model;

import io.xeros.content.combat.weapon.CombatStyle;

public enum Bonus {

	ATTACK_STAB, ATTACK_SLASH, ATTACK_CRUSH, ATTACK_MAGIC, ATTACK_RANGED,
	DEFENCE_STAB, DEFENCE_SLASH, DEFENCE_CRUSH, DEFENCE_MAGIC, DEFENCE_RANGED,
	STRENGTH, 
	RANGED_STRENGTH,
	MAGIC_DMG,
	PRAYER;

	public static Bonus attackBonusForCombatStyle(CombatStyle combatStyle) {
		switch (combatStyle) {
			case SLASH:
				return ATTACK_SLASH;
			case CRUSH:
				return ATTACK_CRUSH;
			case MAGIC:
			case SPECIAL:
				return ATTACK_MAGIC;
			case RANGE:
				return ATTACK_RANGED;
			default:
				return ATTACK_STAB;
		}
	}
	
}