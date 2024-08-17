package io.xeros.model.entity.npc.combat.scripts;

import io.xeros.Server;
import io.xeros.model.CombatType;
import io.xeros.model.entity.Entity;
import io.xeros.model.entity.npc.NPC;
import io.xeros.model.entity.npc.combat.CommonCombatMethod;
import io.xeros.model.entity.npc.combat.HitBuilder;
import io.xeros.model.entity.player.Player;
import io.xeros.model.projectile.ProjectileEntity;

public class RevenantCombat extends CommonCombatMethod {
    @Override
    public boolean prepareAttack(Entity entity, Player target) {
        if (isReachable()) {
            meleeAttack();
        } else {
            if (Server.random.rollDie(3, 1)) {
                magicAttack();
            } else rangedAttack();
        }
        return false;
    }

    private void meleeAttack() {
        int animation = getAnimation((NPC) entity);
        this.build(Server.random.inclusive(0, 35), animation, CombatType.MELEE).execute();
    }

    private void rangedAttack() {
        this.build(Server.random.inclusive(0, 35), getAnimation((NPC) entity), CombatType.RANGE).consume(builder -> {
            var tileDist = entity.getCenterPosition().getManhattanDistance(target.getCenterPosition());
            int duration = (41 + 11 + (5 * tileDist));
            ProjectileEntity projectile = new ProjectileEntity(entity, target, 206, 41, duration, 43, 31, entity.getEntitySize(), 5, true);
            projectile.sendProjectile();
            builder.setDelay((int) (projectile.getSpeed() / 30D));
        }).execute();
    }

    private void magicAttack() {
        this.build(Server.random.inclusive(0, 35), getAnimation((NPC) entity), CombatType.MAGE).consume(builder -> {
            var tileDist = entity.getCenterPosition().getManhattanDistance(target.getCenterPosition());
            int duration = (51 + -5 + (10 * tileDist));
            ProjectileEntity projectile = new ProjectileEntity(entity, target, 1415, 51, duration, 43, 31, entity.getEntitySize(), 5, true);
            projectile.sendProjectile();
            builder.setDelay((int) (projectile.getSpeed() / 30D));
            builder.setEndGraphic(1454, projectile.getSpeed(), 50);
        }).execute();
    }

    private static int getAnimation(NPC entity) {
        int animation = 0;
        NPC npc = entity;
        switch (npc.getNpcId()) {
            case 7881 -> animation = 169;
            case 7932 -> animation = 1582;
            case 7940, 16003 -> animation = 80;
            case 7936 -> animation = 64;
            case 7935 -> animation = 6581;
            case 7939, 16004 -> animation = 6600;
            case 7937, 16001 -> animation = 4320;
            case 7938, 16000 -> animation = 2731;
            case 7934, 16002 -> animation = 4652;
            case 7931 -> animation = 6184;
            case 7933 -> animation = 164;
        }
        return animation;
    }

    @Override
    public int getAttackSpeed(Entity entity) {
        return 0;
    }

    @Override
    public int moveCloseToTargetTileRange(Entity entity) {
        return 0;
    }
}
