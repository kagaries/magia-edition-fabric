package com.kagaries.fabric.world.block;

import com.kagaries.fabric.Magia;
import com.kagaries.fabric.world.item.ModItems;
import io.wispforest.owo.registration.annotations.RegistryNamespace;
import io.wispforest.owo.registration.reflect.BlockRegistryContainer;
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

public class ModBlocks implements BlockRegistryContainer {

    // registered as 'magia-blocks:<name>'
    public static final Block TEST = new Block(AbstractBlock.Settings.create());

    @Override
    public BlockItem createBlockItem(Block block, String identifier) {
        return new BlockItem(block, new Item.Settings());
    }
}
