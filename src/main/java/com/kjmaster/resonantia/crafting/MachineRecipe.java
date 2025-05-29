package com.kjmaster.resonantia.crafting;

import com.kjmaster.resonantia.recipes.OutputStack;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.RegistryAccess;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeInput;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public interface MachineRecipe<T extends RecipeInput> extends Recipe<T> {
    /**
     * Gets the basic energy cost, irrespective of machine configuration.
     */
    int getBaseEnergyCost();

    /**
     * Get the energy cost of a machine.
     * @param container Container/context. This is the state of the container *after* inputs are taken.
     */
    default int getEnergyCost(T container) {
        return getBaseEnergyCost();
    }

    /**
     * Craft outputs for this recipe.
     * @return An array of item and fluid outputs.
     */
    List<OutputStack> craft(T container, RegistryAccess registryAccess);

    /**
     * Get the results of this machine, for display or verification purposes only.
     */
    List<OutputStack> getResultStacks(RegistryAccess registryAccess);

    /**
     * @deprecated Replaced by {@link #craft(T, RegistryAccess)} to support multiple outputs and output types.
     */
    @Deprecated
    @Override
    default @NotNull ItemStack assemble(@NotNull T container, HolderLookup.@NotNull Provider lookupProvider) {
        return ItemStack.EMPTY;
    }

    @Override
    default boolean canCraftInDimensions(int pWidth, int pHeight) {
        return true;
    }

    /**
     * @deprecated Should use {@link #getResultStacks(RegistryAccess)} instead.
     */
    @Deprecated
    @Override
    default @NotNull ItemStack getResultItem(HolderLookup.@NotNull Provider lookupProvider) {
        return ItemStack.EMPTY;
    }

    @Override
    default boolean isSpecial() {
        return true;
    }
}
