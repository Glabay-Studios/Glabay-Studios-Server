package io.xeros.model.definitions;

import java.io.FileReader;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import io.xeros.Server;
import io.xeros.model.entity.npc.combat.CombatMethod;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import lombok.Getter;

public class NpcStats {
    private static final java.util.logging.Logger log = java.util.logging.Logger.getLogger(NpcStats.class.getName());
    @Getter
    public static Int2ObjectOpenHashMap<NpcStats> npcStatsMap;

    public static void load() {
        try (FileReader fr = new FileReader(Server.getDataDirectory() + "/cfg/npc/npc_stats.json")) {
            npcStatsMap = new Gson().fromJson(fr, new TypeToken<Int2ObjectOpenHashMap<NpcStats>>() {}.getType());
            log.info("Loaded " + npcStatsMap.size() + " npc stats.");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static NpcStats forId(int npcId) {
        if (!npcStatsMap.containsKey(npcId)) {
            npcStatsMap.put(npcId, DEFAULT);
        }
        return npcStatsMap.get(npcId);
    }

    static final NpcStats DEFAULT = builder().createNpcStats();

    public static NpcStatsBuilder builder() {
        return new NpcStatsBuilder();
    }

    public NpcStatsBuilder from(NpcStats npcStats) {
        NpcStatsBuilder builder = new NpcStatsBuilder();
        builder.from(npcStats);
        return builder;
    }

    private String name;
    private final int hitpoints;
    private final int combatLevel;
    private final int slayerLevel;
    private final int attackSpeed;
    private final int attackLevel;
    private final int strengthLevel;
    private final int defenceLevel;
    private final int rangeLevel;
    private final int magicLevel;
    private final int stab;
    private final int slash;
    private final int crush;
    private final int range;
    private final int magic;
    private final int stabDef;
    private final int slashDef;
    private final int crushDef;
    private final int rangeDef;
    private final int magicDef;
    private final int bonusAttack;
    private final int bonusStrength;
    private final int bonusRangeStrength;
    private final int bonusMagicDamage;
    private final boolean poisonImmune;
    private final boolean venomImmune;
    private final boolean dragon;
    private final boolean demon;
    private final boolean undead;
    public Scripts scripts;

    public NpcStats(String name, int hitpoints, int combatLevel, int slayerLevel, int attackSpeed, int attackLevel, int strengthLevel, int defenceLevel, int rangeLevel, int magicLevel, int stab, int slash, int crush, int range, int magic, int stabDef, int slashDef, int crushDef, int rangeDef, int magicDef, int bonusAttack, int bonusStrength, int bonusRangeStrength, int bonusMagicDamage, boolean poisonImmune, boolean venomImmune, boolean dragon, boolean demon, boolean undead) {
        this.name = name;
        this.hitpoints = hitpoints;
        this.combatLevel = combatLevel;
        this.slayerLevel = slayerLevel;
        this.attackSpeed = attackSpeed;
        this.attackLevel = attackLevel;
        this.strengthLevel = strengthLevel;
        this.defenceLevel = defenceLevel;
        this.rangeLevel = rangeLevel;
        this.magicLevel = magicLevel;
        this.stab = stab;
        this.slash = slash;
        this.crush = crush;
        this.range = range;
        this.magic = magic;
        this.stabDef = stabDef;
        this.slashDef = slashDef;
        this.crushDef = crushDef;
        this.rangeDef = rangeDef;
        this.magicDef = magicDef;
        this.bonusAttack = bonusAttack;
        this.bonusStrength = bonusStrength;
        this.bonusRangeStrength = bonusRangeStrength;
        this.bonusMagicDamage = bonusMagicDamage;
        this.poisonImmune = poisonImmune;
        this.venomImmune = venomImmune;
        this.dragon = dragon;
        this.demon = demon;
        this.undead = undead;
    }

    @Override
    public String toString() {
        return "NpcCombatDefinition{" + "name=\'" + name + '\'' + ", attackLevel=" + attackLevel + ", strengthLevel=" + strengthLevel + ", defenceLevel=" + defenceLevel + ", rangeLevel=" + rangeLevel + ", magicLevel=" + magicLevel + ", stab=" + stab + ", slash=" + slash + ", crush=" + crush + ", range=" + range + ", magic=" + magic + ", stabDef=" + stabDef + ", slashDef=" + slashDef + ", crushDef=" + crushDef + ", rangeDef=" + rangeDef + ", magicDef=" + magicDef + ", bonusAttack=" + bonusAttack + ", bonusStrength=" + bonusStrength + ", bonusRangeStrength=" + bonusRangeStrength + ", bonusMagicDamage=" + bonusMagicDamage + ", poisonImmune=" + poisonImmune + ", venomImmune=" + venomImmune + ", dragon=" + dragon + ", demon=" + demon + ", undead=" + undead + '}';
    }

    public String getName() {
        return this.name;
    }

    public int getHitpoints() {
        return this.hitpoints;
    }

    public int getCombatLevel() {
        return this.combatLevel;
    }

    public int getSlayerLevel() {
        return this.slayerLevel;
    }

    public int getAttackSpeed() {
        return this.attackSpeed;
    }

    public int getAttackLevel() {
        return this.attackLevel;
    }

    public int getStrengthLevel() {
        return this.strengthLevel;
    }

    public int getDefenceLevel() {
        return this.defenceLevel;
    }

    public int getRangeLevel() {
        return this.rangeLevel;
    }

    public int getMagicLevel() {
        return this.magicLevel;
    }

    public int getStab() {
        return this.stab;
    }

    public int getSlash() {
        return this.slash;
    }

    public int getCrush() {
        return this.crush;
    }

    public int getRange() {
        return this.range;
    }

    public int getMagic() {
        return this.magic;
    }

    public int getStabDef() {
        return this.stabDef;
    }

    public int getSlashDef() {
        return this.slashDef;
    }

    public int getCrushDef() {
        return this.crushDef;
    }

    public int getRangeDef() {
        return this.rangeDef;
    }

    public int getMagicDef() {
        return this.magicDef;
    }

    public int getBonusAttack() {
        return this.bonusAttack;
    }

    public int getBonusStrength() {
        return this.bonusStrength;
    }

    public int getBonusRangeStrength() {
        return this.bonusRangeStrength;
    }

    public int getBonusMagicDamage() {
        return this.bonusMagicDamage;
    }

    public boolean isPoisonImmune() {
        return this.poisonImmune;
    }

    public boolean isVenomImmune() {
        return this.venomImmune;
    }

    public boolean isDragon() {
        return this.dragon;
    }

    public boolean isDemon() {
        return this.demon;
    }

    public boolean isUndead() {
        return this.undead;
    }

    public void setName(final String name) {
        this.name = name;
    }

    @Override
    public boolean equals(final Object o) {
        if (o == this) return true;
        if (!(o instanceof NpcStats)) return false;
        final NpcStats other = (NpcStats) o;
        if (!other.canEqual((Object) this)) return false;
        if (this.getHitpoints() != other.getHitpoints()) return false;
        if (this.getCombatLevel() != other.getCombatLevel()) return false;
        if (this.getSlayerLevel() != other.getSlayerLevel()) return false;
        if (this.getAttackSpeed() != other.getAttackSpeed()) return false;
        if (this.getAttackLevel() != other.getAttackLevel()) return false;
        if (this.getStrengthLevel() != other.getStrengthLevel()) return false;
        if (this.getDefenceLevel() != other.getDefenceLevel()) return false;
        if (this.getRangeLevel() != other.getRangeLevel()) return false;
        if (this.getMagicLevel() != other.getMagicLevel()) return false;
        if (this.getStab() != other.getStab()) return false;
        if (this.getSlash() != other.getSlash()) return false;
        if (this.getCrush() != other.getCrush()) return false;
        if (this.getRange() != other.getRange()) return false;
        if (this.getMagic() != other.getMagic()) return false;
        if (this.getStabDef() != other.getStabDef()) return false;
        if (this.getSlashDef() != other.getSlashDef()) return false;
        if (this.getCrushDef() != other.getCrushDef()) return false;
        if (this.getRangeDef() != other.getRangeDef()) return false;
        if (this.getMagicDef() != other.getMagicDef()) return false;
        if (this.getBonusAttack() != other.getBonusAttack()) return false;
        if (this.getBonusStrength() != other.getBonusStrength()) return false;
        if (this.getBonusRangeStrength() != other.getBonusRangeStrength()) return false;
        if (this.getBonusMagicDamage() != other.getBonusMagicDamage()) return false;
        if (this.isPoisonImmune() != other.isPoisonImmune()) return false;
        if (this.isVenomImmune() != other.isVenomImmune()) return false;
        if (this.isDragon() != other.isDragon()) return false;
        if (this.isDemon() != other.isDemon()) return false;
        if (this.isUndead() != other.isUndead()) return false;
        final Object this$name = this.getName();
        final Object other$name = other.getName();
        if (this$name == null ? other$name != null : !this$name.equals(other$name)) return false;
        return true;
    }

    protected boolean canEqual(final Object other) {
        return other instanceof NpcStats;
    }

    @Override
    public int hashCode() {
        final int PRIME = 59;
        int result = 1;
        result = result * PRIME + this.getHitpoints();
        result = result * PRIME + this.getCombatLevel();
        result = result * PRIME + this.getSlayerLevel();
        result = result * PRIME + this.getAttackSpeed();
        result = result * PRIME + this.getAttackLevel();
        result = result * PRIME + this.getStrengthLevel();
        result = result * PRIME + this.getDefenceLevel();
        result = result * PRIME + this.getRangeLevel();
        result = result * PRIME + this.getMagicLevel();
        result = result * PRIME + this.getStab();
        result = result * PRIME + this.getSlash();
        result = result * PRIME + this.getCrush();
        result = result * PRIME + this.getRange();
        result = result * PRIME + this.getMagic();
        result = result * PRIME + this.getStabDef();
        result = result * PRIME + this.getSlashDef();
        result = result * PRIME + this.getCrushDef();
        result = result * PRIME + this.getRangeDef();
        result = result * PRIME + this.getMagicDef();
        result = result * PRIME + this.getBonusAttack();
        result = result * PRIME + this.getBonusStrength();
        result = result * PRIME + this.getBonusRangeStrength();
        result = result * PRIME + this.getBonusMagicDamage();
        result = result * PRIME + (this.isPoisonImmune() ? 79 : 97);
        result = result * PRIME + (this.isVenomImmune() ? 79 : 97);
        result = result * PRIME + (this.isDragon() ? 79 : 97);
        result = result * PRIME + (this.isDemon() ? 79 : 97);
        result = result * PRIME + (this.isUndead() ? 79 : 97);
        final Object $name = this.getName();
        result = result * PRIME + ($name == null ? 43 : $name.hashCode());
        return result;
    }

    public static class Scripts {
        public String combat;
        public CombatMethod combat_;
        public Class<CombatMethod> combatMethodClass;

        public void resolve() {
            try {
                combat_ = resolveCombat(combat);
                if (combat != null) combatMethodClass = (Class<CombatMethod>) resolveCCM(combat);
            } catch (ClassNotFoundException e) {
                log.info("Missing script, no such class: " + e);
            } catch (Exception e) {
                e.printStackTrace(System.err);
            }
        }

        public CombatMethod newCombatInstance() {
            if (combatMethodClass != null) {
                try {
                    return combatMethodClass.getDeclaredConstructor().newInstance();
                } catch (InstantiationException | IllegalAccessException | InvocationTargetException |
                         NoSuchMethodException e) {
                   log.info("issue initializing new combat instance!");
                }
            }
            return null;
        }

        public Class<? extends CombatMethod> resolveCCM(String className) throws ClassNotFoundException {
            Class<? extends CombatMethod> clazz = null;
            for (var v : DynamicClassLoader.scriptmap.values()) {
                if (v.getSimpleName().equalsIgnoreCase(className)) {
                    clazz = v;
                    break;
                }
            }
            return clazz;
        }

        public static CombatMethod resolveCombat(String className) throws InstantiationException, IllegalAccessException, ClassNotFoundException, NoSuchMethodException, InvocationTargetException {
            CombatMethod result = null;
            for (var c : DynamicClassLoader.scriptmap.keySet()) {
                if (c == null) continue;
                if (c.getSimpleName().equalsIgnoreCase(className)) {
                    result = c.getDeclaredConstructor().newInstance();
                    break;
                }
            }
            return result;
        }
    }
}
