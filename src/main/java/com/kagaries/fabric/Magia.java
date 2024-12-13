package com.kagaries.fabric;

import com.kagaries.fabric.world.block.ModBlocks;
import com.kagaries.fabric.world.entity.ModEntities;
import com.kagaries.fabric.world.item.ModItems;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;

import java.util.logging.Level;
import java.util.logging.Logger;

public class Magia implements ModInitializer {

    public static final String MOD_ID = "me-fabric";

    public static final Logger LOGGER = Logger.getLogger(MOD_ID);

    public static final ModContainer CONTAINER = FabricLoader.getInstance().getModContainer(MOD_ID).get();
    public static final String VERSION = CONTAINER.getMetadata().getVersion().getFriendlyString();


    @Override
    public void onInitialize() {
        LOGGER.info("Initializing Magia Edition:" + VERSION);
        ModEntities.createDefaultAttributes();
        ModBlocks.initialize();
        ModItems.initialize();
    }
}
