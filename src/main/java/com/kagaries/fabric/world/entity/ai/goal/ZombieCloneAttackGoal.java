package com.kagaries.fabric.world.entity.ai.goal;

import com.kagaries.fabric.world.entity.ZombieClone;
import net.minecraft.entity.ai.goal.MeleeAttackGoal;

public class ZombieCloneAttackGoal extends MeleeAttackGoal {
    private final ZombieClone zombie;
    private int ticks;

    public ZombieCloneAttackGoal(ZombieClone zombie, double speed, boolean pauseWhenMobIdle) {
        super(zombie, speed, pauseWhenMobIdle);
        this.zombie = zombie;
    }

    public void start() {
        super.start();
        this.ticks = 0;
    }

    public void stop() {
        super.stop();
        this.zombie.setAttacking(false);
    }

    public void tick() {
        super.tick();
        ++this.ticks;
        this.zombie.setAttacking(this.ticks >= 5 && this.getCooldown() < this.getMaxCooldown() / 2);
    }
}
