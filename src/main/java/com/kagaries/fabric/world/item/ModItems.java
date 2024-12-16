package com.kagaries.fabric.world.item;

import com.kagaries.fabric.Magia;
import com.kagaries.fabric.world.block.ModBlocks;
import com.kagaries.fabric.world.item.material.EmeraldMaterial;
import io.wispforest.owo.itemgroup.Icon;
import io.wispforest.owo.itemgroup.OwoItemGroup;
import io.wispforest.owo.registration.reflect.ItemRegistryContainer;
import net.fabricmc.fabric.mixin.content.registry.AxeItemAccessor;
import net.minecraft.block.Block;
import net.minecraft.item.AxeItem;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.util.Identifier;

import java.util.logging.Level;

public class ModItems implements ItemRegistryContainer {
    public static final AxeItem EMERALD_AXE = new AxeItem(EmeraldMaterial.INSTANCE, 6, -3.35F, new Item.Settings());

    public static final OwoItemGroup TEST_GROUP = OwoItemGroup.builder(new Identifier(Magia.MOD_ID, "test"), () -> Icon.of(EMERALD_AXE))
            .build();
}
