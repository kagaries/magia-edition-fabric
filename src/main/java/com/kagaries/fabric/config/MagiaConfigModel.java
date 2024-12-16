package com.kagaries.fabric.config;

import io.wispforest.owo.config.annotation.Config;
import io.wispforest.owo.config.annotation.Modmenu;
import io.wispforest.owo.config.annotation.RangeConstraint;

@Modmenu(modId = "me-fabric")
@Config(name = "magia-config", wrapperName = "MagiaConfig")
public class MagiaConfigModel {
    @RangeConstraint(min = 0.0f, max = 5.0f)
    public float damageMulti = 1.0f;

    public boolean UnfairMode = false;
    public boolean doQuickExplosionDamage = true;
    public boolean PvPRangedDamage = true;
    public boolean PvPExplosionDamage = true;

    //public Choices PvPMode = Choices.NORMAL;
    /*
    public enum Choices {
        NORMAL, WEAK_QUICK_EXPLOSION, NO_QUICK_EXPLOSION, ONLY_MELEE, ONLY_RANGED, ONLY_EXPLOSION;
    }

     */
}
