package com.kagaries.fabric.entity;

import com.kagaries.fabric.KeFabric;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.projectile.FishingHook;

public class ModEntities {

    public static final EntityType<ZombieClone> ZOMBIE_CLONE = register("zombie_clone", EntityType.Builder.<ZombieClone>of(ZombieClone::new, MobCategory.CREATURE).sized(0.75f, 0.75f).clientTrackingRange(8));

    private static <T extends Entity> EntityType<T> register(String p_20635_, EntityType.Builder<T> p_20636_) {
        return Registry.register(BuiltInRegistries.ENTITY_TYPE, p_20635_, p_20636_.build(p_20635_));
    }

}
