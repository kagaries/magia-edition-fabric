package com.kagaries.fabric.world.entity;

import com.kagaries.fabric.Magia;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.DataFixerBuilder;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

import java.util.logging.Level;

public class ModEntities {

    public static final EntityType<ZombieClone> ZOMBIE_CLONE = register(Identifier.of(Magia.MOD_ID, "zombie_clone"), EntityType.Builder.create(ZombieClone::new, SpawnGroup.CREATURE).setDimensions(0.75f, 0.75f).maxTrackingRange(8));

    private static <T extends Entity> EntityType<T> register(Identifier p_20635_, EntityType.Builder<T> p_20636_) {
        Magia.getLogger().info("Registering Entity: {}:{}", p_20635_.getNamespace(), p_20635_.getPath());

        return Registry.register(Registries.ENTITY_TYPE, p_20635_, p_20636_.build(p_20635_.getPath()));
    }

    public static void createDefaultAttributes() {
        FabricDefaultAttributeRegistry.register(ZOMBIE_CLONE, ZombieClone.createZombieAttributes());
    }
}
