package com.kjmaster.resonantia.modules.smelter.crafting;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.common.crafting.SizedIngredient;

import java.util.List;

import static com.kjmaster.resonantia.Resonantia.MODID;

public class AlloyRecipeBuilder {

    public static void build(ItemStack output, List<SizedIngredient> inputs, int energy, float experience,
                         RecipeOutput recipeOutput) {
        build(ResourceLocation.fromNamespaceAndPath(MODID,"alloy_smelting/" + BuiltInRegistries.ITEM.getKey(output.getItem()).getPath()), inputs,
                output, energy, experience, recipeOutput);
    }

    public static void build(ItemStack output, String suffix, List<SizedIngredient> inputs, int energy, float experience,
                         RecipeOutput recipeOutput) {
        build(ResourceLocation.fromNamespaceAndPath(MODID, "alloy_smelting/" + BuiltInRegistries.ITEM.getKey(output.getItem()).getPath() + "_" + suffix),
                inputs, output, energy, experience, recipeOutput);
    }

    private static void build(ResourceLocation id, List<SizedIngredient> inputs, ItemStack output, int energy,
                         float experience, RecipeOutput recipeOutput) {
        recipeOutput.accept(id, new AlloySmeltingRecipe(inputs, output, energy, experience), null);
    }
}
