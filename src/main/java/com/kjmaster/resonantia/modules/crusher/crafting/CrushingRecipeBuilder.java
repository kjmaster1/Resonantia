package com.kjmaster.resonantia.modules.crusher.crafting;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.common.crafting.SizedIngredient;

import static com.kjmaster.resonantia.Resonantia.MODID;

public class CrushingRecipeBuilder {

    public static void build(ItemStack output, SizedIngredient input, int energy, float experience,
                             RecipeOutput recipeOutput) {
        build(ResourceLocation.fromNamespaceAndPath(MODID, "crushing/" + BuiltInRegistries.ITEM.getKey(output.getItem()).getPath()), input,
                output, energy, experience, recipeOutput);
    }

    public static void build(ItemStack output, String suffix, SizedIngredient input, int energy, float experience,
                             RecipeOutput recipeOutput) {
        build(ResourceLocation.fromNamespaceAndPath(MODID, "crushing/" + BuiltInRegistries.ITEM.getKey(output.getItem()).getPath() + "_" + suffix),
                input, output, energy, experience, recipeOutput);
    }

    private static void build(ResourceLocation id, SizedIngredient input, ItemStack output, int energy,
                              float experience, RecipeOutput recipeOutput) {
        recipeOutput.accept(id, new CrushingRecipe(input, output, energy, experience), null);
    }
}
