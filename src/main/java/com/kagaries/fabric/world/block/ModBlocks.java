package com.kagaries.fabric.world.block;

import com.kagaries.fabric.Magia;
import com.kagaries.fabric.world.item.ModItems;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

import java.util.logging.Level;

public class ModBlocks {

    public static final Block TEST = register(new Block(AbstractBlock.Settings.create()), "test", true);

    public static Block register(Block block, String name, boolean shouldRegisterItem) {
        // Register the block and its item.
        Identifier id = Identifier.of(Magia.MOD_ID, name);

        // Sometimes, you may not want to register an item for the block.
        // Eg: if it's a technical block like `minecraft:air` or `minecraft:end_gateway`
        if (shouldRegisterItem) {
            BlockItem blockItem = new BlockItem(block, new Item.Settings());
            ModItems.register(blockItem, name);
        }

        Magia.getLogger().info("Registering Block: {}:{}", id.getNamespace(), id.getPath());

        return Registry.register(Registries.BLOCK, id, block);
    }

    public static void initialize() {}
}
