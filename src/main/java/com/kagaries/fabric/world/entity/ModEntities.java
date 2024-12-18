package com.kagaries.fabric.world.entity;

import io.wispforest.owo.registration.reflect.EntityRegistryContainer;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;

public class ModEntities implements EntityRegistryContainer {

    // registered as 'magia-entities:<name>'
    public static final EntityType<ZombieClone> ZOMBIE_CLONE = EntityType.Builder.create(ZombieClone::new, SpawnGroup.CREATURE).setDimensions(0.75f, 0.75f).maxTrackingRange(8).build("zombie_clone");

    public static void createDefaultAttributes() {
        FabricDefaultAttributeRegistry.register(ZOMBIE_CLONE, ZombieClone.createZombieAttributes());
    }
}
