package com.kagaries.fabric;

import com.kagaries.fabric.world.block.ModBlocks;
import com.kagaries.fabric.world.entity.ModEntities;
import com.kagaries.fabric.world.item.ModItems;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.logging.Level;

public class Magia implements ModInitializer {

    public static final String MOD_ID = "me-fabric";

    private static final StackWalker STACK_WALKER;

    public static final ModContainer CONTAINER = FabricLoader.getInstance().getModContainer(MOD_ID).get();
    public static final String VERSION = CONTAINER.getMetadata().getVersion().getFriendlyString();


    @Override
    public void onInitialize() {
        getLogger().info("Initializing Magia Edition: {}", VERSION);
        ModEntities.createDefaultAttributes();
        ModBlocks.initialize();
        ModItems.initialize();
    }

    public static Logger getLogger() {
        return LoggerFactory.getLogger(STACK_WALKER.getCallerClass());
    }

    static {
        STACK_WALKER = StackWalker.getInstance(StackWalker.Option.RETAIN_CLASS_REFERENCE);
    }
}
