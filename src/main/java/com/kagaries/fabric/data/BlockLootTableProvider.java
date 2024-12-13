package com.kagaries.fabric.data;

import com.kagaries.fabric.Magia;
import com.kagaries.fabric.world.block.ModBlocks;
import com.kagaries.fabric.world.item.ModItems;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricBlockLootTableProvider;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;

public class BlockLootTableProvider extends FabricBlockLootTableProvider {
    public BlockLootTableProvider(FabricDataOutput dataOutput) {
        super(dataOutput);
    }

    @Override
    public void generate() {
        addDrop(ModBlocks.TEST, drops(Registries.ITEM.get(Identifier.of(Magia.MOD_ID, "test"))));
    }
}
