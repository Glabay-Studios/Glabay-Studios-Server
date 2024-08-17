package io.xeros.content.combat;

import java.util.Arrays;
import java.util.Optional;
import java.util.Set;

import io.xeros.content.SkillcapePerks;
import io.xeros.content.combat.melee.MeleeExtras;
import io.xeros.content.combat.weapon.CombatStyle;
import io.xeros.content.items.OrnamentedItem;
import io.xeros.model.CombatType;
import io.xeros.model.Graphic;
import io.xeros.model.Items;
import io.xeros.model.definitions.ItemDef;
import io.xeros.model.entity.Entity;
import io.xeros.model.entity.HealthStatus;
import io.xeros.model.entity.npc.NPC;
import io.xeros.model.entity.npc.NPCHandler;
import io.xeros.model.entity.player.Player;
import io.xeros.model.entity.player.PlayerHandler;
import io.xeros.model.items.ItemAssistant;
import io.xeros.util.Misc;
import org.apache.commons.lang3.RandomUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CombatItems {

	private static final Logger logger = LoggerFactory.getLogger(CombatItems.class);

	public static final Set<Integer> SLAYER_HELMETS_REGULAR = Set.of(
			Items.BLACK_MASK,
			Items.SLAYER_HELMET,
			OrnamentedItem.TWISTED_SLAYER_HELMET.getOrnamentedItem(),
			OrnamentedItem.HYDRA_SLAYER_HELMET.getOrnamentedItem(),
			OrnamentedItem.KBD_SLAYER_HELMET.getOrnamentedItem(),
			OrnamentedItem.KQ_SLAYER_HELMET.getOrnamentedItem(),
			OrnamentedItem.ABYSSAL_SLAYER_HELMET.getOrnamentedItem(),
			OrnamentedItem.DARK_CLAW_SLAYER_HELMET.getOrnamentedItem(),
			OrnamentedItem.VORKATH_SLAYER_HELMET.getOrnamentedItem()
	);

	public static final Set<Integer> SLAYER_HELMETS_IMBUED = Set.of(
			Items.BLACK_MASK_I,
			Items.SLAYER_HELMET_I,
			OrnamentedItem.TWISTED_SLAYER_HELMET_I.getOrnamentedItem(),
			OrnamentedItem.HYDRA_SLAYER_HELMET_I.getOrnamentedItem(),
			OrnamentedItem.KBD_SLAYER_HELMET_I.getOrnamentedItem(),
			OrnamentedItem.KQ_SLAYER_HELMET_I.getOrnamentedItem(),
			OrnamentedItem.ABYSSAL_SLAYER_HELMET_I.getOrnamentedItem(),
			OrnamentedItem.DARK_CLAW_SLAYER_HELMET_I.getOrnamentedItem(),
			OrnamentedItem.VORKATH_SLAYER_HELMET_I.getOrnamentedItem()
	);

	/**
	 * Charged is [x][0], uncharged is [x][1].
	 */
	public static final int[][] MUTAGEN_HELMETS = { { 12931, 12929 }, { 13199, 13198 }, { 13197, 13196 } };

	private final Player c;

	public CombatItems(Player Client) {
		this.c = Client;
	}

	public boolean hasArcLight() {
		return this.c.getArcLightCharge() > 0 && c.getItems().isWearingItem(Items.ARCLIGHT, Player.playerWeapon);
	}

	public boolean hasTomeOfFire() {
		return c.getItems().isWearingItem(Items.TOME_OF_FIRE, Player.playerShield)
				&& c.getTomeOfFire().hasCharges();
	}

	public static int getUnchargedSerpentineHelmet(int chargedHelmetId) {
		for (int i = 0; i < MUTAGEN_HELMETS.length; i++) {
			if (MUTAGEN_HELMETS[i][0] == chargedHelmetId) {
				return MUTAGEN_HELMETS[i][1];
			}
		}

		logger.error("No uncharged serpentine helmet for id {}", chargedHelmetId);
		return Items.SERPENTINE_HELM_UNCHARGED;
	}

	/**
	 * The armour has a set effect in which each piece of the set boosts damage and accuracy by 0.5% when using the crush attack style.
	 * If all three pieces are worn, an additional 1.0% bonus is added for a total of 2.5% accuracy and damage bonus.
	 */
	public double getInquisitorBonus() {
		if (c.attacking.getCombatType() == CombatType.MELEE && c.getCombatConfigs().getWeaponMode().getCombatStyle() == CombatStyle.CRUSH) {
			double bonus = 100;
			int[] items = {Items.INQUISITORS_GREAT_HELM, Items.INQUISITORS_HAUBERK, Items.INQUISITORS_PLATESKIRT};
			long wearingCount = Arrays.stream(items).filter(item -> c.getItems().isWearingItem(item)).count();
			bonus += wearingCount * 0.5;
			if (wearingCount >= 3) {
				bonus += 1;
			}
			return bonus / 100d;
		} else {
			return 1.0;
		}
	}

	public boolean elyProc() {
		if (c.playerEquipment[Player.playerShield] == 12817 && Misc.trueRand(10) <= 6) {
			c.startGraphic(new Graphic(321));
			return true;
		}
		return false;
	}

	public boolean doQueuedGraniteMaulSpecials() {
		if (c.graniteMaulSpecialCharges > 0) {
			if (c.npcAttackingIndex != 0) {
				NPC o = NPCHandler.npcs[c.npcAttackingIndex];
				if (!c.goodDistance(c.getX(), c.getY(), o.getX(), o.getY(), c.attacking.getRequiredDistance())) {
					return false;
				}
			} else if (c.playerAttackingIndex != 0) {
				Player o = PlayerHandler.players[c.playerAttackingIndex];
				if (o != null && !c.goodDistance(c.getX(), c.getY(), o.getX(), o.getY(), c.attacking.getRequiredDistance())) {
					return false;
				}
			}

			boolean spec = c.graniteMaulSpecialCharges > 0;
			while (c.graniteMaulSpecialCharges != 0) {
				MeleeExtras.graniteMaulSpecial(c, false);
				c.graniteMaulSpecialCharges--;
			}

			return spec;
		}

		return false;
	}

	public void absorbDragonfireDamage() {
		int shieldId = c.playerEquipment[Player.playerShield];
		String shieldName = ItemAssistant.getItemName(shieldId).toLowerCase();
		if (shieldName.contains("dragonfire")) {
			int charges = c.getDragonfireShieldCharge();
			if (charges < 50) {
				c.setDragonfireShieldCharge(charges++);
				if (charges == 50) {
					c.sendMessage("<col=255>Your dragonfire shield has completely finished charging.");
				}
				c.startAnimation(6695);
				c.gfx0(1164);
				c.setDragonfireShieldCharge(charges);
				return;
			}
		}
	}

	/**
	 * Determines if the player is wearing a crawsbow
	 * @return True if the player is wearing a crawsbow
	 */
	public boolean wearingCrawsBow() {
		return c.getItems().getWeapon() == Items.CRAWS_BOW;
	}

	/**
	 * Determines if the player is wearing a chainmace
	 * @return True if the player is wearing a chainmace
	 */
	public boolean wearingViggorasChainmace() {
		return c.getItems().getWeapon() == Items.VIGGORAS_CHAINMACE;
	}

	/**
	 * Determines if the player is wearing a sceptre
	 * @return True if the player is wearing a sceptre
	 */
	public boolean wearingThammaronsSceptre() {
		return c.getItems().getWeapon() == Items.THAMMARONS_SCEPTRE;
	}

	public boolean usingNightmareStaffSpecial() {
		int weapon = c.playerEquipment[3];
		return weapon == 24424 && c.usingSpecial && !c.usingClickCast;
	}

	public boolean usingEldritchStaffSpecial() {
		int weapon = c.playerEquipment[3];
		return weapon == 24425 && c.usingSpecial && !c.usingClickCast;
	}

	public boolean usingCrystalBow() {
		return c.playerEquipment[Player.playerWeapon] >= 4212 && c.playerEquipment[Player.playerWeapon] <= 4223;
	}

	public boolean usingCrawsBow() {
		return c.playerEquipment[Player.playerWeapon] == Items.CRAWS_BOW;
	}

	public boolean usingBlowPipe() {
		return c.playerEquipment[Player.playerWeapon] == Items.TOXIC_BLOWPIPE;
	}

	public boolean usingTwistedBow() {
		return c.playerEquipment[Player.playerWeapon] == Items.TWISTED_BOW;
	}

	public boolean usingDbow() {
		return c.playerEquipment[Player.playerWeapon] == 11235 || c.playerEquipment[Player.playerWeapon] == 12765 || c.playerEquipment[Player.playerWeapon] == 12766
				|| c.playerEquipment[Player.playerWeapon] == 12767 || c.playerEquipment[Player.playerWeapon] == 12768;
	}

	public boolean properBolts() {
		int i = c.playerEquipment[Player.playerArrows];
		return (i >= 9139 && i <= 9145) || (i >= 9236 && i <= 9245) || (i >= 9236 && i <= 9245) || (i >= 21924 && i <= 21974)
				|| (i >= 9335 && i <= 9341) || i == 11875 || i == 9340 || i == 21905 || i == 21906 ||i == 21316;
	}

	public boolean usingJavelins(int i) {
		return (i >= 825 && i <= 830) || i == 19484 || i == 21318;
	}
	
	public void checkDemonItems() {
		if (c.getItems().isWearingItem(19675, Player.playerWeapon)) {
			c.setArcLightCharge(c.getArcLightCharge() - 1);
			if (c.getArcLightCharge() <= 0) {
				c.setArcLightCharge(0);
				c.sendMessage("Your arclight has lost all charge.");
				c.getItems().equipItem(-1, 0, Player.playerWeapon);
				c.getItems().addItemUnderAnyCircumstance(19675, 1);
			}
		}
	}

	public boolean usingAssembler() {
		return c.getItems().isWearingItem(Items.ASSEMBLER_MAX_CAPE)
				|| c.getItems().isWearingItem(Items.AVAS_ASSEMBLER);
	}

	public boolean usingAccumulator() {
		return c.getItems().isWearingItem(Items.AVAS_ACCUMULATOR)
				|| c.getItems().isWearingItem(Items.ACCUMULATOR_MAX_CAPE)
				|| c.getItems().isWearingItem(Items.RANGING_CAPE)
				|| c.getItems().isWearingItem(Items.RANGE_MASTER_CAPE);
	}

	public boolean consumeDart() {
		int chance = usingAccumulator() ? 72 : usingAssembler() ? 80 : 0;
		if (chance == 0)
			return true;
		return Misc.isLucky(100 - chance);
	}

	public boolean consumeScale() {
		return Misc.random(3) == 1;
	}

	public void checkBlowpipeShotsRemaining() {
		if (c.getToxicBlowpipeAmmo() != 0) {
			c.sendMessage("The blowpipe has " + c.getToxicBlowpipeAmmoAmount() + " " + ItemDef.forId(c.getToxicBlowpipeAmmo()).getName()
					+ " and " + c.getToxicBlowpipeCharge() + " scales remaining.");
		} else {
			c.sendMessage("The blowpipe has no ammo and " + c.getToxicBlowpipeCharge() + " scales remaining.");
		}
	}

	public void checkBlowpipe() {
		if (c.getItems().isWearingItem(Items.TOXIC_BLOWPIPE, Player.playerWeapon)) {

			if (consumeDart())
				c.setToxicBlowpipeAmmoAmount(c.getToxicBlowpipeAmmoAmount() - 1);

			if (consumeScale())
				c.setToxicBlowpipeCharge(c.getToxicBlowpipeCharge() - 1);


			if (c.getToxicBlowpipeAmmoAmount() % 500 == 0 && c.getToxicBlowpipeAmmoAmount() > 0) {
				c.sendMessage("<col=255>You have " + c.getToxicBlowpipeAmmoAmount() + " ammo in your blow pipe remaining.</col>");
			}
			if (c.getToxicBlowpipeAmmoAmount() <= 0 && c.getToxicBlowpipeCharge() <= 0) {
				c.sendMessage("Your toxic blowpipe has lost all charge.");
				c.getItems().equipItem(-1, 0, 3);
				c.getItems().addItemUnderAnyCircumstance(12924, 1);
				c.setToxicBlowpipeAmmo(0);
				c.setToxicBlowpipeAmmoAmount(0);
				c.setToxicBlowpipeCharge(0);
			}
		}
	}

	public void checkCombatTickBasedItems() {
		if (c.serpHelmCombatTicks > 0 && (c.serpHelmCombatTicks % 8 == 0)) {
			c.serpHelmCombatTicks = 0;
			for (int[] helmets : MUTAGEN_HELMETS) {
				int charged = helmets[0];
				int uncharged = helmets[1];
				if (c.getItems().isWearingItem(charged) && c.getItems().getWornItemSlot(charged) == Player.playerHat) {
					c.setSerpentineHelmCharge(c.getSerpentineHelmCharge() - 1);
					if (c.getSerpentineHelmCharge() % 500 == 0 && c.getSerpentineHelmCharge() != 0) {
						c.sendMessage("<col=255>You have " + c.getSerpentineHelmCharge() + " charges remaining in your serpentine helm.</col>");
					}
					if (c.getSerpentineHelmCharge() <= 0) {
						c.sendMessage("Your serpentine helm has lost all of it's charge.");
						c.getItems().equipItem(-1, 0, Player.playerHat);
						c.getItems().addItemUnderAnyCircumstance(uncharged, 1);
						c.setSerpentineHelmCharge(0);
					}
				}
			}
		}
	}

	public void checkVenomousItems(Entity target) {
		if (c.getItems().isWearingItem(12904, Player.playerWeapon)) {
			c.setToxicStaffOfTheDeadCharge(c.getToxicStaffOfTheDeadCharge() - 1);
			if (c.getToxicStaffOfTheDeadCharge() <= 0) {
				c.setToxicStaffOfTheDeadCharge(0);
				c.sendMessage("Your toxic staff of the dead has lost all charge.");
				c.getItems().equipItem(-1, 0, Player.playerWeapon);
				c.getItems().addItemUnderAnyCircumstance(12902, 1);
			}
		}
		if (target.isNPC() && Misc.random(6) == 1) {
			for (int[] helmets : MUTAGEN_HELMETS) {
				int charged = helmets[0];
				int uncharged = helmets[1];
				if (c.getItems().isWearingItem(charged) && c.getItems().getWornItemSlot(charged) == Player.playerHat) {
					if (c.getSerpentineHelmCharge() > 0) {
						target.getHealth().proposeStatus(HealthStatus.VENOM, 6, Optional.of(c));
					}
				}
			}
		}
	}
}
