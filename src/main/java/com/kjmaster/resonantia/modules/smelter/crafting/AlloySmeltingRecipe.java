package com.kjmaster.resonantia.modules.smelter.crafting;

import com.kjmaster.resonantia.crafting.MachineRecipe;
import com.kjmaster.resonantia.modules.smelter.SmelterModule;
import com.kjmaster.resonantia.recipes.OutputStack;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.common.crafting.SizedIngredient;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public record AlloySmeltingRecipe(List<SizedIngredient> inputs, ItemStack output, int energy,
                                  float experience) implements MachineRecipe<AlloySmeltingRecipe.Input> {

    @Override
    public int getBaseEnergyCost() {
        return energy;
    }

    @Override
    public @NotNull NonNullList<Ingredient> getIngredients() {
        return NonNullList.of(Ingredient.EMPTY,
                inputs.stream().map(SizedIngredient::ingredient).toArray(Ingredient[]::new));
    }

    @Override
    public boolean matches(@NotNull Input recipeInput, @NotNull Level level) {
        if (inputs.isEmpty()) {
            return false;
        }

        boolean[] matchedInputs = new boolean[2];

        // Iterate over the slots
        for (int i = 0; i < 2; i++) {
            // Iterate over the inputs
            for (int j = 0; j < 2; j++) {
                // If this ingredient has been matched already, continue
                if (matchedInputs[j]) {
                    continue;
                }

                var slotItem = recipeInput.getItem(i);

                if (j < inputs.size()) {
                    // If we expect an input, test we have a match for it.
                    if (inputs.get(j).test(slotItem)) {
                        matchedInputs[j] = true;
                        break;
                    }
                } else if (slotItem.isEmpty()) {
                    // If we don't expect an input, make sure we have a blank for it.
                    matchedInputs[j] = true;
                    break;
                }
            }
        }

        // If we matched all our ingredients, we win!
        for (int i = 0; i < 2; i++) {
            if (!matchedInputs[i]) {
                return false;
            }
        }

        return true;
    }

    @Override
    public List<OutputStack> craft(Input container, RegistryAccess registryAccess) {
        ItemStack outputStack = output.copy();
        return List.of(OutputStack.of(outputStack));
    }

    @Override
    public List<OutputStack> getResultStacks(RegistryAccess registryAccess) {
        return List.of(OutputStack.of(output.copy()));
    }

    @Override
    public @NotNull RecipeSerializer<?> getSerializer() {
        return SmelterModule.ALLOY_SMELTING.serializer().get();
    }

    @Override
    public @NotNull RecipeType<?> getType() {
        return SmelterModule.ALLOY_SMELTING.type().get();
    }

    public record Input(List<ItemStack> inputs) implements RecipeInput {

        @Override
        public @NotNull ItemStack getItem(int slotIndex) {
            if (slotIndex >= inputs.size()) {
                throw new IllegalArgumentException("No item for index " + slotIndex);
            }

            return inputs.get(slotIndex);
        }

        public ItemStack getFirstPopulated() {
            for (ItemStack stack : inputs) {
                if (!stack.isEmpty()) {
                    return stack;
                }
            }
            return ItemStack.EMPTY;
        }

        @Override
        public int size() {
            return inputs.size();
        }
    }

    public static class Serializer implements RecipeSerializer<AlloySmeltingRecipe> {
        public static final MapCodec<AlloySmeltingRecipe> CODEC = RecordCodecBuilder.mapCodec(inst -> inst
                .group(SizedIngredient.FLAT_CODEC.listOf().fieldOf("inputs").forGetter(AlloySmeltingRecipe::inputs),
                        ItemStack.CODEC.fieldOf("output").forGetter(AlloySmeltingRecipe::output),
                        Codec.INT.fieldOf("energy").forGetter(AlloySmeltingRecipe::energy),
                        Codec.FLOAT.fieldOf("experience").forGetter(AlloySmeltingRecipe::experience))
                .apply(inst, AlloySmeltingRecipe::new));

        public static final StreamCodec<RegistryFriendlyByteBuf, AlloySmeltingRecipe> STREAM_CODEC = StreamCodec
                .composite(SizedIngredient.STREAM_CODEC.apply(ByteBufCodecs.list()), AlloySmeltingRecipe::inputs,
                        ItemStack.STREAM_CODEC, AlloySmeltingRecipe::output, ByteBufCodecs.INT,
                        AlloySmeltingRecipe::energy, ByteBufCodecs.FLOAT, AlloySmeltingRecipe::experience,
                        AlloySmeltingRecipe::new);

        @Override
        public @NotNull MapCodec<AlloySmeltingRecipe> codec() {
            return CODEC;
        }

        @Override
        public @NotNull StreamCodec<RegistryFriendlyByteBuf, AlloySmeltingRecipe> streamCodec() {
            return STREAM_CODEC;
        }
    }
}