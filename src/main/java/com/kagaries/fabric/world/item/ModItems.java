package com.kagaries.fabric.world.item;

import com.kagaries.fabric.Magia;
import com.kagaries.fabric.world.block.ModBlocks;
import com.kagaries.fabric.world.item.material.EmeraldMaterial;
import io.wispforest.owo.itemgroup.Icon;
import io.wispforest.owo.itemgroup.OwoItemGroup;
import io.wispforest.owo.itemgroup.OwoItemSettings;
import io.wispforest.owo.registration.annotations.RegistryNamespace;
import io.wispforest.owo.registration.reflect.ItemRegistryContainer;
import net.fabricmc.fabric.api.item.v1.CustomDamageHandler;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.AxeItem;
import net.minecraft.item.FoodComponent;
import net.minecraft.item.Item;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.util.Identifier;
import net.minecraft.util.Rarity;

public class ModItems implements ItemRegistryContainer {

    @RegistryNamespace("magia-mob-items")
    public static class ModMobItems implements ItemRegistryContainer {
        // registered as 'magia-mob-items:blue_slime'
        public static final Item BLUE_SLIME_BALL = new Item(new OwoItemSettings().food(new FoodComponent.Builder().hunger(2).saturationModifier(0.4F).statusEffect(new StatusEffectInstance(StatusEffects.SPEED, 35), 1f).alwaysEdible().build()));
    }

    @RegistryNamespace("magia-food-items")
    public static class ModFoodItems implements ItemRegistryContainer {
        // registered as 'magia-food-items:enchanted_golden_carrot'
        public static final Item ENCHANTED_GOLDEN_CARROT = new EnchantedGoldenCarrotItem(new OwoItemSettings().food(new FoodComponent.Builder().hunger(6).saturationModifier(1.2F).statusEffect(new StatusEffectInstance(StatusEffects.REGENERATION, 100, 0), 1.0F).statusEffect(new StatusEffectInstance(StatusEffects.ABSORPTION, 2400, 0), 1.0F).statusEffect(new StatusEffectInstance(StatusEffects.RESISTANCE, 6000, 0), 1.0F).build()).rarity(Rarity.EPIC));
    }

    public static final OwoItemGroup ITEM_GROUP = OwoItemGroup.builder(new Identifier(Magia.MOD_ID, "me-fabric"), () -> Icon.of(ModItems.EMERALD_AXE))
            .initializer(owoItemGroup -> {
                owoItemGroup.addCustomTab(Icon.of(ModMobItems.BLUE_SLIME_BALL), "magia-edition", ((displayContext, entries) -> {
                    entries.add(ModItems.EMERALD_AXE);
                    entries.add(ModBlocks.TEST);
                    entries.add(ModMobItems.BLUE_SLIME_BALL);
                }), true);
                owoItemGroup.addCustomTab(Icon.of(ModItems.EMERALD_AXE), "magia-weapons-and-tools", (displayContext, entries) -> {
                    entries.add(ModItems.EMERALD_AXE);
                }, false);
                owoItemGroup.addCustomTab(Icon.of(ModFoodItems.ENCHANTED_GOLDEN_CARROT), "magia-foods", ((displayContext, entries) -> {
                    entries.add(ModFoodItems.ENCHANTED_GOLDEN_CARROT);
                    entries.add(ModMobItems.BLUE_SLIME_BALL);
                }), false);
                owoItemGroup.addCustomTab(Icon.of(ModBlocks.TEST), "magia-blocks", (displayContext, entries) -> {
                    entries.add(ModBlocks.TEST);
                }, false);
                owoItemGroup.addCustomTab(Icon.of(ModMobItems.BLUE_SLIME_BALL), "magia-ingredients", (displayContext, entries) -> {
                    entries.add(ModMobItems.BLUE_SLIME_BALL);
                }, false);
            })
            .build();

    public static final AxeItem EMERALD_AXE = new AxeItem(EmeraldMaterial.INSTANCE, 6, -3.35F, new OwoItemSettings());

    public static void init() {
        ITEM_GROUP.initialize();
    }
}
