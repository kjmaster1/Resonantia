package com.kjmaster.resonantia.modules.stabilizer.crafting;

import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.common.crafting.SizedIngredient;

import static com.kjmaster.resonantia.Resonantia.MODID;

public class StabilizingRecipeBuilder {

    public static void build(String path, SizedIngredient input, int energy, RecipeOutput recipeOutput) {
        build(ResourceLocation.fromNamespaceAndPath(MODID, "stabilizing/" + path), input, energy, recipeOutput);
    }

    public static void build(String path, String suffix, SizedIngredient input, int energy, RecipeOutput recipeOutput) {
        build(ResourceLocation.fromNamespaceAndPath(MODID, "stabilizing/" + path + "_" + suffix),
                input, energy, recipeOutput);
    }

    private static void build(ResourceLocation id, SizedIngredient input, int energy, RecipeOutput recipeOutput) {
        recipeOutput.accept(id, new StabilizingRecipe(input, energy), null);
    }
}
