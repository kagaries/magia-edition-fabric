package com.kagaries.fabric.world.item.material;

import net.minecraft.item.Items;
import net.minecraft.item.ToolMaterial;
import net.minecraft.recipe.Ingredient;

public class EmeraldMaterial implements ToolMaterial {

    public static final EmeraldMaterial INSTANCE = new EmeraldMaterial();

    @Override
    public int getDurability() {
        return 15052;
    }

    @Override
    public float getMiningSpeedMultiplier() {
        return 3.5F;
    }

    @Override
    public float getAttackDamage() {
        return 4;
    }

    @Override
    public int getMiningLevel() {
        return 2;
    }

    @Override
    public int getEnchantability() {
        return 16;
    }

    @Override
    public Ingredient getRepairIngredient() {
        return Ingredient.ofItems(Items.EMERALD);
    }
}
