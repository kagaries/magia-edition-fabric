package com.kagaries.fabric.client;

import com.kagaries.fabric.client.renderer.ZombieCloneRenderer;
import com.kagaries.fabric.world.entity.ModEntities;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;

@Environment(EnvType.CLIENT)
public class KeFabricClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        EntityRendererRegistry.register(ModEntities.ZOMBIE_CLONE, ZombieCloneRenderer::new);
    }
}
