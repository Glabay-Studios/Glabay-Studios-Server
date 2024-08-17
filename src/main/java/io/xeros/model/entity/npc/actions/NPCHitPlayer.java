package io.xeros.model.entity.npc.actions;

import java.rmi.UnexpectedException;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import com.fasterxml.jackson.databind.introspect.POJOPropertiesCollector;
import com.google.common.base.Preconditions;
import io.xeros.Server;
import io.xeros.content.bosses.Scorpia;
import io.xeros.content.bosses.Vorkath;
import io.xeros.content.bosses.wildypursuit.TheUnbearable;
import io.xeros.content.combat.CombatHit;
import io.xeros.content.combat.Damage;
import io.xeros.content.combat.Hitmark;
import io.xeros.content.combat.core.HitDispatcher;
import io.xeros.content.combat.effects.damageeffect.DamageEffect;
import io.xeros.content.combat.effects.damageeffect.impl.SerpentineHelmEffect;
import io.xeros.content.combat.formula.CombatFormula;
import io.xeros.content.combat.formula.MagicMaxHit;
import io.xeros.content.combat.formula.MeleeMaxHit;
import io.xeros.content.combat.formula.RangeMaxHit;
import io.xeros.content.combat.formula.rework.MagicCombatFormula;
import io.xeros.content.combat.formula.rework.MeleeCombatFormula;
import io.xeros.content.combat.formula.rework.RangeCombatFormula;
import io.xeros.content.combat.melee.MeleeData;
import io.xeros.content.combat.melee.MeleeExtras;
import io.xeros.content.combat.npc.NPCAutoAttack;
import io.xeros.content.combat.npc.NPCAutoAttackDamage;
import io.xeros.content.combat.npc.NPCCombatAttack;
import io.xeros.content.combat.specials.Special;
import io.xeros.content.combat.specials.Specials;
import io.xeros.model.CombatType;
import io.xeros.model.Items;
import io.xeros.model.entity.HealthStatus;
import io.xeros.model.entity.npc.NPC;
import io.xeros.model.entity.npc.NPCHandler;
import io.xeros.model.entity.npc.stats.NpcBonus;
import io.xeros.model.entity.npc.stats.NpcCombatDefinition;
import io.xeros.model.entity.npc.stats.NpcCombatSkill;
import io.xeros.model.entity.player.Boundary;
import io.xeros.model.entity.player.Player;
import io.xeros.model.entity.player.PlayerHandler;
import io.xeros.util.Misc;
import org.apache.commons.net.io.SocketOutputStream;

public class NPCHitPlayer {

    public static boolean rollAccuracy(NPC npc, Player c, CombatType combatType) {
        int attackLevel = attackLevel(npc, combatType);
        int attackBonus = attackBonus(npc, combatType);
        int effectiveAttack = getEffectLevel(attackLevel + 150, attackBonus);
        int effectiveDefence = combatType == CombatType.RANGE ? RangeMaxHit.calculateRangeDefence(c)
                : combatType == CombatType.MAGE ? MagicMaxHit.mageDefence(c) : MeleeMaxHit.calculateMeleeDefence(c, npc);
        if (c.isPrintDefenceStats()) {

            c.sendMessage("n->p Hit%: " + CombatFormula.getHitChance(effectiveAttack, effectiveDefence) + "%, " +
                    "Att level: " + attackLevel + ", " +
                    "Att bonus: " + attackBonus + ", " +
                    "Att roll: " + effectiveAttack + ", " +
                    "Def: " + effectiveDefence + ", " +
                    combatType);
        }
        return CombatFormula.rollAccuracy(effectiveAttack, effectiveDefence);
    }

    public static int attackBonus(NPC npc, CombatType combatType) {
        NpcCombatDefinition definition = npc.getCombatDefinition();

        if (definition != null) {
            switch (combatType) {
                case SPECIAL:
                case MELEE:
                    return definition.getAttackBonus(NpcBonus.ATTACK_BONUS);
                case RANGE:
                    return definition.getAttackBonus(NpcBonus.RANGE_BONUS);
                case MAGE:
                case DRAGON_FIRE:
                    return definition.getAttackBonus(NpcBonus.MAGIC_BONUS);
            }
        }
        return 0;
    }

    public static int attackLevel(NPC npc, CombatType combatType) {
        NpcCombatDefinition definition = npc.getCombatDefinition();

        if (definition != null) {
            switch (combatType) {
                case MELEE:
                case SPECIAL:
                    return definition.getLevel(NpcCombatSkill.ATTACK);
                case RANGE:
                    return definition.getLevel(NpcCombatSkill.RANGE);
                case DRAGON_FIRE:
                case MAGE:
                    return definition.getLevel(NpcCombatSkill.MAGIC);
            }
        }
        return 0;
    }

    public static int getEffectLevel(int attackLevel, int attackBonus) {
        return attackLevel + attackBonus;
    }

    public static CombatHit applyAutoAttackDamage(NPC npc, Player c, NPCAutoAttack npcAutoAttack) {
        NPCCombatAttack npcCombatAttack = new NPCCombatAttack(npc, c);

        if (npc.isDead() || c.respawnTimer > 0 || !npcAutoAttack.isAttackDamagesPlayer()) {
            return CombatHit.miss();
        }

        if (c.playerAttackingIndex <= 0 && c.npcAttackingIndex <= 0) {
            if (c.autoRet == 1) {
                c.npcAttackingIndex = npc.getIndex();
                c.combatFollowing = true;
            }
        }

        if (c.getItems().isWearingItem(12931) || c.getItems().isWearingItem(13197)
                || c.getItems().isWearingItem(13199) && !(npc.getNpcId() == 319)) {
            DamageEffect venom = new SerpentineHelmEffect();
            if (venom.isExecutable(c)) {
                venom.execute(c, npc, new Damage(6));
            }
        }

        // Auto attack modifiers
        double autoAttackAccuracyBonus = npcAutoAttack.getAccuracyBonus() == null ? 1.0D : npcAutoAttack.getAccuracyBonus().apply(npcCombatAttack);
        double autoAttackPrayerPercentage = npcAutoAttack.getPrayerProtectionPercentage() == null ? 0 : npcAutoAttack.getPrayerProtectionPercentage().apply(npcCombatAttack);
        double autoAttackMaxHitBonus = npcAutoAttack.getMaxHitBonus() == null ? 0d : npcAutoAttack.getMaxHitBonus().apply(npcCombatAttack);

        double accuracyRoll = 0;
        try {
            switch (npcAutoAttack.getCombatType()) {
                case MELEE:
                    accuracyRoll = MeleeCombatFormula.get().getAccuracy(npc, c, autoAttackAccuracyBonus, 1.0);
                    break;
                case RANGE:
                    accuracyRoll = RangeCombatFormula.STANDARD.getAccuracy(npc, c, autoAttackAccuracyBonus, 1.0);
                    break;
                case SPECIAL:
                case MAGE:
                    accuracyRoll = MagicCombatFormula.STANDARD.getAccuracy(npc, c, autoAttackAccuracyBonus, 1.0);
                    break;
                default:
                    throw new UnexpectedException("NPC Auto attack accuracy roll not specified for " + npcAutoAttack.getCombatType());
            }
        } catch (UnexpectedException e) {
            e.printStackTrace(System.err);
        }

        double damageModifier = 1.0 + autoAttackMaxHitBonus;
        int secondDamage = -1;

        if (npcAutoAttack.getCombatType() == CombatType.MELEE && c.protectingMelee()
                || npcAutoAttack.getCombatType() == CombatType.MAGE && c.protectingMagic()
                || npcAutoAttack.getCombatType() == CombatType.RANGE && c.protectingRange() ) {
            Preconditions.checkState(autoAttackPrayerPercentage <= 1, "autoAttackPrayercentage out of bounds!");
            damageModifier = autoAttackPrayerPercentage;
        }

        if (npcAutoAttack.getEndGraphic() != null) {
            c.startGraphic(npcAutoAttack.getEndGraphic());
        }

        int maxHit = (int) (npcAutoAttack.getMaxHit() * damageModifier);
        int damage = maxHit > 0 ? Misc.random(maxHit) : 0;

        if (c.isPrintDefenceStats()) {
            String hitPercentage = String.format("%.2f", accuracyRoll * 100);

            int npcAttackLevel = 0;
            int npcAttackBonus = 0;
            int npcDefBonus = 0;

            NpcCombatDefinition definition = npc.getCombatDefinition();
            if (definition != null) {
                CombatType combatType = npcAutoAttack.getCombatType();
                NpcCombatSkill combatSkill = combatType.equals(CombatType.MELEE) ? NpcCombatSkill.ATTACK
                        : combatType.equals(CombatType.RANGE) ? NpcCombatSkill.RANGE : NpcCombatSkill.MAGIC;
                NpcBonus bonus = combatType.equals(CombatType.MELEE) ? NpcBonus.ATTACK_BONUS
                        : combatType.equals(CombatType.RANGE) ? NpcBonus.ATTACK_RANGE_BONUS : NpcBonus.ATTACK_MAGIC_BONUS;

                npcAttackLevel = definition.getLevel(combatSkill);
                npcAttackBonus = definition.getAttackBonus(bonus);
                npcDefBonus = definition.getDefenceBonus(bonus);
            }

            c.sendMessage("n->p Hit%: " + hitPercentage + "%, " +
                    "Skill Level: " + npcAttackLevel + ", " +
                    "Attack Bonus: " + npcAttackBonus + ", " +
                    npcAutoAttack.getCombatType());
        }

        boolean successfulHit = accuracyRoll >= HitDispatcher.rand.nextDouble();
        if (!successfulHit) {
            damage = 0;
        }

        if (c.getHealth().getCurrentHealth() - damage < 0) {
            damage = c.getHealth().getCurrentHealth();
        }

        if (c.playerEquipment[Player.playerHat] == 22326 && c.playerEquipment[Player.playerChest] == 22327 && c.playerEquipment[Player.playerLegs] == 22328) {
            damage *= .75;
        }

        if (damage > 0 && c.getCombatItems().elyProc()) {
            damage *= .75;
        }

        if (npcAutoAttack.getCombatType() == CombatType.MELEE) {
            if (Server.getEventHandler().isRunning(c, "staff_of_the_dead")) {
                Special special = Specials.STAFF_OF_THE_DEAD.getSpecial();
                Damage d = new Damage(damage);
                special.hit(c, npc, d);
                damage = d.getAmount();
            }
        }

        if (npcAutoAttack.getPoisonDamage() > 0 && Misc.random(10) == 1) {
            c.getHealth().proposeStatus(HealthStatus.POISON, npcAutoAttack.getPoisonDamage(), Optional.of(npc));
        }

        c.logoutDelay = System.currentTimeMillis();

        if (npcAutoAttack.getModifyDamage() != null) {
            damage = npcAutoAttack.getModifyDamage().apply(new NPCAutoAttackDamage(npc, c, damage));
        }

        if (damage > -1) {
            c.appendDamage(npc, damage, damage > 0 ? Hitmark.HIT : Hitmark.MISS);
            c.addDamageTaken(npc, damage);
        }

        if (damage > 0) {
            MeleeExtras.appendVengeanceNPC(c, damage + (Math.max(secondDamage, 0)), npc);
            MeleeExtras.applyRecoilNPC(c, damage + (Math.max(secondDamage, 0)), npc);
        }

        int rol = c.getHealth().getCurrentHealth() - damage;

        if (rol > 0 && rol < c.getHealth().getMaximumHealth() / 10) {
            Server.npcHandler.ringOfLife(c);
        }

        return new CombatHit(successfulHit, damage);
    }

    public static void applyDamage(NPC npc, NPCHandler context) {
        if (npc != null) {
            if (PlayerHandler.players[npc.oldIndex] == null) {
                return;
            }
            if (npc.isDead()) {
                return;
            }
            if (npc.getNpcId() >= 1739 && npc.getNpcId() <= 1742 || npc.getNpcId() == 7413) {
                return;
            }
            Player c = PlayerHandler.players[npc.oldIndex];

            if (context.multiAttacks(npc)) {
                context.multiAttackDamage(npc);
                return;
            }

            if (c.getZulrahEvent().isTransforming()) {
                return;
            }
            if (c.isAutoRetaliate()) {
                c.attackEntity(npc);
            }

            if (c.attackTimer <= 3) {
                if (!NPCHandler.isFightCaveNpc(npc) || npc.getNpcId() != 319) {
                    c.startAnimation(MeleeData.getBlockEmote(c));
                }
            }
            if (c.getItems().isWearingItem(12931) || c.getItems().isWearingItem(13197)
                    || c.getItems().isWearingItem(13199) && !(npc.getNpcId() == 319)) {
                DamageEffect venom = new SerpentineHelmEffect();
                if (venom.isExecutable(c)) {
                    venom.execute(c, npc, new Damage(6));
                }
            }
            npc.totalAttacks++;
            boolean protectionIgnored = context.prayerProtectionIgnored(npc);
            if (c.respawnTimer <= 0) {
                int damage = 0;
                int secondDamage = -1;

                /**
                 * Handles all the different damage approaches npcs are dealing
                 */
                if (npc.getAttackType() != null) {
                    if (npc.getNpcId() >= 7931 && npc.getNpcId() <= 7940 && c.getItems().isWearingItem(21816)) {
                        c.braceletEtherCount -= 1;
                        damage = 0;
                        return;
                    }
                    switch (npc.getAttackType()) {

                        /**
                         * Handles npcs who are dealing melee based attacks
                         */
                        case MELEE:
                            damage = Misc.random(context.getMaxHit(c, npc));
                            switch (npc.getNpcId()) {
                                case 6374:
                                    secondDamage = Misc.random(context.getMaxHit(c, npc));
                                    break;
                                case 423:
                                    if (!c.getItems().isWearingItem(Items.FACEMASK) && !c.getItems().isWearingAnyItem(c.SLAYER_HELMETS) && !c.getItems().isWearingAnyItem(c.IMBUED_SLAYER_HELMETS)
                                            && !c.getItems().isWearingAnyItem(c.BLACK_MASKS)) {
                                        c.sendMessage("@red@Your stats are reduced since you do not have a face mask or slayer helm.");
                                        int[] toDecrease = { 0, 1, 2, 4, 6 };
                                        for (int tD : toDecrease) {
                                            c.playerLevel[tD] -= c.playerLevel[tD];
                                            if (c.playerLevel[tD] < 0) {
                                                c.playerLevel[tD] = 1;
                                            }
                                            c.getPA().refreshSkill(tD);
                                            c.getPA().setSkillLevel(tD, c.playerLevel[tD], c.playerXP[tD]);
                                        }
                                    }
                                    break;
                                case 7930:
                                case 7931:
                                case 7932:
                                case 7933:
                                case 7934:
                                case 7935:
                                case 7936:
                                case 3937:
                                case 7938:
                                case 7939:
                                case 7940:
                                    if (damage > 0 && c.getCombatItems().elyProc()) {
                                        damage *= .75;
                                    }

                                    if (c.playerEquipment[Player.playerHat] == 22326 && c.playerEquipment[Player.playerChest] == 22327 && c.playerEquipment[Player.playerLegs] == 22328) {
                                        damage *= .75;
                                    }

                                    break;
                                case 3116:
                                    c.playerLevel[5] -= 1;
                                    if (c.playerLevel[5] < 0) {
                                        c.playerLevel[5] = 0;
                                    }
                                    c.getPA().refreshSkill(Player.playerPrayer);
                                    break;
                            }

                            double accuracyRoll = MeleeCombatFormula.get().getAccuracy(npc, c);
                            boolean isAccurate = accuracyRoll >= HitDispatcher.rand.nextDouble();
                            /**
                             * Calculate defence
                             */
                            if (!isAccurate) {
                                damage = 0;
                            }

                            if (npc.getNpcId() == 5869) {
                                damage = !c.protectingMelee() ? 30 : 0;
                                c.playerLevel[5] -= c.protectingMelee() && !c.getItems().isWearingItem(12821) ? 30
                                        : c.protectingMelee() && c.getItems().isWearingItem(12821) ? 15 : 0;
                                if (c.playerLevel[5] < 0) {
                                    c.playerLevel[5] = 0;
                                }
                            }


                            /**
                             * Zulrah
                             */
                            if (npc.getNpcId() == 2043 && c.getZulrahEvent().getNpc() != null
                                    && c.getZulrahEvent().getNpc().equals(npc)) {
                                Boundary boundary = new Boundary(npc.targetedLocation.getX(),
                                        npc.targetedLocation.getY(), npc.targetedLocation.getX(),
                                        npc.targetedLocation.getY());
                                if (!Boundary.isIn(c, boundary)) {
                                    return;
                                }
                                damage = 20 + Misc.random(25);
                            }

                            /**
                             * Protection prayer
                             */
                            if (c.protectingMelee() && !protectionIgnored) {
                                if (npc.getNpcId() == 5890)
                                    damage /= 3;
                                else if (npc.getNpcId() == 963 || npc.getNpcId() == 965 || npc.getNpcId() == 8349
                                        || npc.getNpcId() == 8133 || npc.getNpcId() == 6342 || npc.getNpcId() == 2054
                                        || npc.getNpcId() == 239 || npc.getNpcId() == 998 || npc.getNpcId() == 999 ||
                                        npc.getNpcId() == 1000 || npc.getNpcId() == 7554 || npc.getNpcId() == 319
                                        || npc.getNpcId() == 320 || npc.getNpcId() == 6615 || npc.getNpcId() == 5916 || npc.getNpcId() == 8781
                                        || npc.getNpcId() == 7544 || npc.getNpcId() == 5129 || npc.getNpcId() == 8918 || npc.getNpcId() == 8030 || npc.getNpcId() == 8031)
                                    damage /= 2;

                                else if (npc.getNpcId() == TheUnbearable.NPC_ID) {//Here is melee 80%
                                    damage *= .8;
                                } else {
                                    damage = 0;
                                }
                            } else if (c.protectingMelee() && protectionIgnored) {
                                damage /= 2;
                            }
                            if (c.playerEquipment[Player.playerCape] == 10556 ) { //attacker icon
                                damage /= 0.5;
                            }
                            if (c.protectingRange() && !protectionIgnored) {
                                if (npc.getNpcId() == 7554)
                                    damage /= 2;
                            }
                            /**
                             * Specials and defenders
                             */
                            if (Server.getEventHandler().isRunning(c, "staff_of_the_dead")) {
                                Special special = Specials.STAFF_OF_THE_DEAD.getSpecial();
                                Damage d = new Damage(damage);
                                special.hit(c, npc, d);
                                damage = d.getAmount();
                            }

                            if (damage > 0 && c.getCombatItems().elyProc()) {
                                damage *= .75;
                            }

                            if (c.playerEquipment[Player.playerChest] == 22327 && c.playerEquipment[Player.playerHat] == 22326 && c.playerEquipment[Player.playerLegs] == 22328) {
                                damage *= .75;
                            }
                            if (c.getHealth().getCurrentHealth() - damage < 0) {
                                damage = c.getHealth().getCurrentHealth();
                            }
                            break;

                        /**
                         * Handles npcs who are dealing range based attacks
                         */
                        case RANGE:
                            damage = Misc.random(context.getMaxHit(c, npc));

                            if (damage > 0 && c.getCombatItems().elyProc()) {
                                damage *= .75;
                            }
                            if (c.playerEquipment[Player.playerHat] == 22326 && c.playerEquipment[Player.playerChest] == 22327 && c.playerEquipment[Player.playerLegs] == 22328) {
                                damage *= .75;
                            }


                            switch (npc.getNpcId()) {
                                case 6377:
                                    secondDamage = Misc.random(context.getMaxHit(c, npc));
                                    break;
                            }


                            accuracyRoll = RangeCombatFormula.STANDARD.getAccuracy(npc, c);
                            isAccurate = accuracyRoll >= HitDispatcher.rand.nextDouble();

                            /**
                             * Range defence
                             */
                            if (!isAccurate) {
                                damage = 0;
                            }

                            if (npc.getNpcId() == 5867) {
                                damage = !c.protectingRange() ? 30 : 0;
                                c.playerLevel[5] -= c.protectingMelee() && !c.getItems().isWearingItem(12821) ? 30
                                        : c.protectingMelee() && c.getItems().isWearingItem(12821) ? 15 : 0;
                                if (c.playerLevel[5] < 0) {
                                    c.playerLevel[5] = 0;
                                }
                            }
                            /**
                             * Protection prayer
                             */

                            if (c.protectingRange() && !protectionIgnored) {
                                if (npc.getNpcId() == 963 || npc.getNpcId() == 965 || npc.getNpcId() == 8349
                                        || npc.getNpcId() == 8133 || npc.getNpcId() == 6342 || npc.getNpcId() == 2054
                                        || npc.getNpcId() == 7554 || npc.getNpcId() == 239 || npc.getNpcId() == 319
                                        || npc.getNpcId() == 499) {
                                    damage /= 2;
                                }

                                else if (npc.getNpcId() == 7931 || npc.getNpcId() == 7932 || npc.getNpcId() == 7933 ||
                                        npc.getNpcId() == 7934 || npc.getNpcId() == 7935 || npc.getNpcId() == 7936 ||
                                        npc.getNpcId() == 7937 || npc.getNpcId() == 7938 || npc.getNpcId() == 7939 ||
                                        npc.getNpcId() == 7940)
                                    damage /=3;

                                else {
                                    damage = 0;
                                }
                                if (c.getHealth().getCurrentHealth() - damage < 0) {
                                    damage = c.getHealth().getCurrentHealth();
                                }
                            } else if (c.protectingRange() && protectionIgnored) {
                                damage /= 2;
                            }
                            if (npc.getNpcId() == 2042 || npc.getNpcId() == 2044) {
                                c.getHealth().proposeStatus(HealthStatus.VENOM, 6, Optional.of(npc));
                            }
                            if (npc.endGfx > 0 || npc.getNpcId() == 3127) {
                                c.gfx100(npc.endGfx);
                            }
                            if (npc.endGfx > 0 || npc.getNpcId() == 7700) {
                                c.gfx100(npc.endGfx);
                            }
                            break;

                        /**
                         * Handles npcs who are dealing mage based attacks
                         */
                        case MAGE:
                            damage = Misc.random(context.getMaxHit(c, npc));
                            boolean magicFailed = false;

                            if (damage > 0 && c.getCombatItems().elyProc()) {
                                damage *= .75;
                            }
                            if (c.playerEquipment[Player.playerHat] == 22326 && c.playerEquipment[Player.playerChest] == 22327 && c.playerEquipment[Player.playerLegs] == 22328) {
                                damage *= .75;
                            }

                            /**
                             * Attacks
                             */
                            switch (npc.getNpcId()) {
                                case 6373:
                                case 6375:
                                case 6376:
                                case 6378:
                                    secondDamage = Misc.random(context.getMaxHit(c, npc));
                                    break;

                                case 6371: // Karamel
                                    c.freezeTimer = 4;
                                    break;

                                case 2205:
                                    secondDamage = Misc.random(27);
                                    break;

                                case 6609:
                                    c.sendMessage("Callisto's fury sends an almighty shockwave through you.");
                                    break;

                                case 8781:
                                    int r11 = Misc.random(10);
                                    if (r11 >= 7) {
                                        secondDamage = Misc.random(5);}
                                    break;
                            }

                            accuracyRoll = MagicCombatFormula.STANDARD.getAccuracy(npc, c);
                            isAccurate = accuracyRoll >= HitDispatcher.rand.nextDouble();
                            /**
                             * Magic defence
                             */
                            if (!isAccurate) {
                                damage = 0;
                                if (secondDamage > -1) {
                                    secondDamage = 0;
                                }
                                magicFailed = true;
                            }

                            if (npc.getNpcId() == 5868) {
                                damage = !c.protectingMagic() ? 30 : 0;
                                c.playerLevel[5] -= c.protectingMagic() && !c.getItems().isWearingItem(12821) ? 30
                                        : c.protectingMagic() && c.getItems().isWearingItem(12821) ? 15 : 0;
                                if (c.playerLevel[5] < 0) {
                                    c.playerLevel[5] = 0;
                                }
                            }

                            /**
                             * Protection prayer
                             */
                            if (c.protectingMagic() && !protectionIgnored) {

                                switch (npc.getNpcId()) {
                                    case 494:
                                    case 492:
                                    case 5535:
                                        int max = npc.getNpcId() == 494 ? 2 : 0;
                                        if (Misc.random(2) == 0) {
                                            damage = 1 + Misc.random(max);
                                        } else {
                                            damage = 0;
                                            if (secondDamage > -1) {
                                                secondDamage = 0;
                                            }
                                        }
                                        break;

                                    case 1677:
                                    case 963:
                                    case 965:
                                    case 8349:
                                    case 8133:
                                    case 6342:
                                    case 2054:
                                    case 239:
                                    case 1046:
                                    case 8028:
                                    case 319:
                                    case 7554:
                                    case 7604: // Skeletal mystic
                                    case 7605: // Skeletal mystic
                                    case 7606: // Skeletal mystic
                                    case 7617:
                                    case 8923:
                                        damage /= 2;
                                        break;

                                    default:
                                        damage = 0;
                                        if (secondDamage > -1) {
                                            secondDamage = 0;
                                        }
                                        magicFailed = true;
                                        break;

                                }

                            } else if (c.protectingMagic() && protectionIgnored) {
                                damage /= 2;
                            }
                            if (c.getHealth().getCurrentHealth() - damage < 0) {
                                damage = c.getHealth().getCurrentHealth();
                            }
                            if (npc.endGfx > 0 && (!magicFailed || NPCHandler.isFightCaveNpc(npc))) {
                                c.gfx100(npc.endGfx);
                            } else {
                                c.gfx100(85);
                            }
                            MeleeExtras.appendVengeanceNPC(c, damage + (secondDamage > 0 ? secondDamage : 0), npc);
                            break;

                        /**
                         * Handles npcs who are dealing dragon fire based attacks
                         */
                        case DRAGON_FIRE:

                            boolean isProtected =

                                    /**
                                     * Anti-dragon shield
                                     */
                                    c.getItems().isWearingItem(1540) ||
                                    /**
                                     * Ancient wyvern shields
                                     */
                                    c.getItems().isWearingItem(Items.ANCIENT_WYVERN_SHIELD) ||
                                    c.getItems().isWearingItem(22002) ||
                                    c.getItems().isWearingItem(22003) ||
                                    /**
                                     * Dragonfire shields
                                     */
                                    c.getItems().isWearingItem(11283) ||
                                    c.getItems().isWearingItem(11284) ||
                                    /**
                                     * Magic prayer
                                     */
                                    c.protectingMagic() ||
                                    /**
                                     * Anti-fire potion
                                     */
                                    (System.currentTimeMillis() - c.lastAntifirePotion) < c.antifireDelay ||
                                            /**
                                             * Elemental Shield
                                             */
                                    (npc.getNpcId() == 465 && c.getItems().isWearingItem(2890));


                                if (!isProtected) {
                                    damage = Misc.random(50);
                                    c.sendMessage(npc.getNpcId() == 465 ? "You are badly burnt by the cold breeze!" : "You are badly burnt by the dragon fire!");
                                }

                            /**
                             * Attacks
                             */
                            switch (npc.endGfx) {
                                case 429:
                                    c.getHealth().proposeStatus(HealthStatus.POISON, 6, Optional.of(npc));
                                    break;

                                case 163:
                                    c.freezeTimer = 15;
                                    c.sendMessage("You have been frozen to the ground.");
                                    break;

                                case 428:
                                    c.freezeTimer = 10;
                                    break;

                                case 431:
                                    c.lastSpear = System.currentTimeMillis();
                                    break;
                            }
                            if (c.getHealth().getCurrentHealth() - damage < 0)
                                damage = c.getHealth().getCurrentHealth();
                            c.gfx100(npc.endGfx);

                            MeleeExtras.appendVengeanceNPC(c, damage + 0, npc);
                            break;

                        /**
                         * Handles npcs who are dealing special attacks
                         */
                        case SPECIAL:
                            damage = Misc.random(context.getMaxHit(c, npc));

                            /**
                             * Attacks
                             */
                            switch (npc.getNpcId()) {
                                case 3129:
                                    int prayerReduction = c.playerLevel[5] / 2;
                                    if (prayerReduction < 1) {
                                        break;
                                    }
                                    c.playerLevel[5] -= prayerReduction;
                                    if (c.playerLevel[5] < 0) {
                                        c.playerLevel[5] = 0;
                                    }
                                    c.getPA().refreshSkill(5);
                                    c.sendMessage("K'ril Tsutsaroth slams through your protection prayer, leaving you feeling drained.");
                                    break;
                                case 1046:
                                case 1049:
                                    prayerReduction = c.playerLevel[5] / 10;
                                    if (prayerReduction < 1) {
                                        break;
                                    }
                                    c.playerLevel[5] -= prayerReduction;
                                    if (c.playerLevel[5] < 0) {
                                        c.playerLevel[5] = 0;
                                    }
                                    c.getPA().refreshSkill(5);
                                    c.sendMessage("Your prayer has been drained drastically.");
                                    break;
                                case 6609:
                                    damage = 3;
                                    c.gfx0(80);
                                    c.lastSpear = System.currentTimeMillis();
                                    c.getPA().getSpeared(npc.absX, npc.absY, 3);
                                    c.sendMessage("Callisto's roar sends your backwards.");
                                    break;
                                case 6610:
                                    if (c.protectingMagic()) {
                                        damage *= .7;
                                    }
                                    secondDamage = Misc.random(context.getMaxHit(c, npc));
                                    if (secondDamage > 0) {
                                        c.gfx0(80);
                                    }
                                    break;

                                case 465:
                                    c.freezeTimer = 15;
                                    c.sendMessage("You have been frozen.");
                                    break;

                                case 8781:
                                    break;
                                case 7144:
                                case 7145:
                                case 7146:
                                    if (context.gorillaBoulder.stream().noneMatch(p -> p[0] == c.absX && p[1] == c.absY)) {
                                        return;
                                    }
                                    break;

                                case 5890:
                                    if (damage > 0 && Misc.random(2) == 0) {
                                        if (npc.getHealth().getStatus() == HealthStatus.POISON) {
                                            c.getHealth().proposeStatus(HealthStatus.POISON, 15, Optional.of(npc));
                                        }
                                    }
                                    if (context.gorillaBoulder.stream().noneMatch(p -> p[0] == c.absX && p[1] == c.absY)) {
                                        return;
                                    }
                                    break;
                            }
                            break;
                    }

                    if (npc.getNpcId() == 320) {
                        int distanceFromTarget = c.distanceToPoint(npc.getX(), npc.getY());

                        if (distanceFromTarget <= 1) {
                            NPC corp = NPCHandler.getNpc(319);
                            Damage heal = new Damage(
                                    damage + Misc.random(15 + 5) + (secondDamage > 0 ? secondDamage : 0));
                            if (corp != null && corp.getHealth().getCurrentHealth() < 2000) {
                                corp.getHealth().increase(heal.getAmount());
                            }
                        }
                    }
                    if (npc.getNpcId() == 6617 || npc.getNpcId() == 6616 || npc.getNpcId() == 6615) {
                        int distanceFromTarget = c.distanceToPoint(npc.getX(), npc.getY());

                        List<NPC> healer = Arrays.asList(NPCHandler.npcs);

                        if (distanceFromTarget <= 1 && Scorpia.stage > 0 && healer.stream().filter(Objects::nonNull)
                                .anyMatch(n -> n.getNpcId() == 6617 && !n.isDead() && n.getHealth().getCurrentHealth() > 0)) {
                            NPC scorpia = NPCHandler.getNpc(6615);
                            Damage heal = new Damage(
                                    damage + Misc.random(45 + 5) + (secondDamage > 0 ? secondDamage : 0));
                            if (scorpia != null && scorpia.getHealth().getCurrentHealth() < 150) {
                                scorpia.getHealth().increase(heal.getAmount());
                            }
                        }
                    }
                    if (npc.endGfx > 0) {
                        c.gfx100(npc.endGfx);
                    }
                    int poisonDamage = context.getPoisonDamage(npc);
                    if (poisonDamage > 0 && Misc.random(10) == 1) {
                        c.getHealth().proposeStatus(HealthStatus.POISON, poisonDamage, Optional.of(npc));
                    }
                    if (c.getHealth().getCurrentHealth() - damage < 0
                            || secondDamage > -1 && c.getHealth().getCurrentHealth() - secondDamage < 0) {
                        damage = c.getHealth().getCurrentHealth();
                        if (secondDamage > -1) {
                            secondDamage = 0;
                        }
                    }
                    context.handleSpecialEffects(c, npc, damage);
                    c.logoutDelay = System.currentTimeMillis();
                    if (damage > -1) {
                        c.appendDamage(npc, damage, damage > 0 ? Hitmark.HIT : Hitmark.MISS);
                        c.addDamageTaken(npc, damage);
                    }
                    if (secondDamage > -1) {
                        c.appendDamage(npc, secondDamage, secondDamage > 0 ? Hitmark.HIT : Hitmark.MISS);
                        c.addDamageTaken(npc, secondDamage);
                    }
                    if (damage > 0 || secondDamage > 0) {
                        MeleeExtras.appendVengeanceNPC(c, damage + (Math.max(secondDamage, 0)), npc);
                        MeleeExtras.applyRecoilNPC(c, damage + (Math.max(secondDamage, 0)), npc);
                    }
                    int rol = c.getHealth().getCurrentHealth();
                    if (rol > 0 && rol < c.getHealth().getMaximumHealth() / 10) {
                        context.ringOfLife(c);
                    }
                    switch (npc.getNpcId()) {
                        // Abyssal sire
                        case 5890:
                            int health = npc.getHealth().getCurrentHealth();
                            c.sireHits++;
                            int randomAmount = Misc.random(5);
                            switch (c.sireHits) {
                                case 10:
                                case 20:
                                case 30:
                                case 40:
                                    for (int id = 0; id < randomAmount; id++) {
                                        int x = npc.absX + Misc.random(2);
                                        int y = npc.absY - Misc.random(2);
                                        NPCHandler.newNPC(5916, x, y, 0, 0, 15);
                                    }
                                    break;

                                case 45:
                                    c.sireHits = 0;
                                    break;

                            }
                            if (health < 400 && health > 329 || health < 100) {
                                npc.setAttackType(CombatType.MELEE);
                            }
                            if (health < 330 && health > 229) {
                                npc.setAttackType(CombatType.MAGE);
                            }
                            if (health < 230 && health > 99) {
                                npc.setAttackType(CombatType.SPECIAL);
                                npc.getHealth().increase(6);
                            }
                            break;

                        case 8028:
                            if (Vorkath.attackStyle == 0) {
                                npc.setAttackType(CombatType.MAGE);
                            }
                            break;
                        case 6607:
                            if (Misc.random(12) == 4) {
                                c.setTeleportToX(3236);
                                c.setTeleportToY(3620);
                            }
                            npc.setAttackType(CombatType.MAGE);

                            break;
                        /**
                         * Hunllef
                         */
                        case 9021: //melee
                        case 9022: //range
                        case 9023: //mage
                            if (damage >= 100) {
                            }
                            if (damage == 0) {
                                if (c.totalMissedHunllefHits >= 6) {
                                    c.totalMissedHunllefHits = 0;
                                }
                                c.totalMissedHunllefHits++;
                            }
                            if (c.totalMissedHunllefHits == 6) {
                                c.totalMissedHunllefHits = 0;

                                switch (npc.getAttackType()) {
                                    case MELEE:
                                        switch (Misc.random(1)) {
                                            case 0:
                                                npc.setAttackType(CombatType.MAGE);
                                                break;
                                            case 1:
                                                npc.setAttackType(CombatType.MELEE);
                                                break;
                                        }
                                        break;
                                    case MAGE:
                                        switch (Misc.random(1)) {
                                            case 0:
                                                npc.setAttackType(CombatType.MELEE);
                                                break;
                                            case 1:
                                                npc.setAttackType(CombatType.MAGE);
                                                break;
                                        }
                                        break;
                                    case RANGE:
                                        switch (Misc.random(1)) {
                                            case 0:
                                                npc.setAttackType(CombatType.MAGE);
                                                break;
                                            case 1:
                                                npc.setAttackType(CombatType.MELEE);
                                                break;
                                        }
                                        break;

                                    default:
                                        break;
                                }
                                break;
                            }
                        case 7144:
                        case 7145:
                        case 7146:
                            if (damage >= 50) {
                            }
                            if (damage == 0) {
                                if (c.totalMissedGorillaHits >= 6) {
                                    c.totalMissedGorillaHits = 0;
                                }
                                c.totalMissedGorillaHits++;
                            }
                            if (c.totalMissedGorillaHits == 6) {
                                c.totalMissedGorillaHits = 0;

                                switch (npc.getAttackType()) {
                                    case MELEE:
                                        switch (Misc.random(2)) {
                                            case 0:
                                                npc.setAttackType(CombatType.MAGE);
                                                break;
                                            case 1:
                                                npc.setAttackType(CombatType.SPECIAL);
                                                break;
                                            case 2:
                                                npc.setAttackType(CombatType.RANGE);
                                                break;
                                        }
                                        break;
                                    case MAGE:
                                        switch (Misc.random(2)) {
                                            case 0:
                                                npc.setAttackType(CombatType.MELEE);
                                                break;
                                            case 1:
                                                npc.setAttackType(CombatType.SPECIAL);
                                                break;
                                            case 2:
                                                npc.setAttackType(CombatType.RANGE);
                                                break;
                                        }
                                        break;
                                    case RANGE:
                                        switch (Misc.random(2)) {
                                            case 0:
                                                npc.setAttackType(CombatType.MAGE);
                                                break;
                                            case 1:
                                                npc.setAttackType(CombatType.SPECIAL);
                                                break;
                                            case 2:
                                                npc.setAttackType(CombatType.MELEE);
                                                break;
                                        }
                                        break;
                                    case SPECIAL:
                                        switch (Misc.random(2)) {
                                            case 0:
                                                npc.setAttackType(CombatType.MAGE);
                                                break;
                                            case 1:
                                                npc.setAttackType(CombatType.MELEE);
                                                break;
                                            case 2:
                                                npc.setAttackType(CombatType.RANGE);
                                                break;
                                        }
                                        break;

                                    default:
                                        break;
                                }
                                break;
                            }
                            c.setUpdateRequired(true);
                    }
                }
            }
        }
    }

}
