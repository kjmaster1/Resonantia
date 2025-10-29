package com.kjmaster.resonantia.modules.stabilizer.crafting;

import com.kjmaster.resonantia.crafting.MachineRecipe;
import com.kjmaster.resonantia.modules.stabilizer.StabilizerModule;
import com.kjmaster.resonantia.recipes.OutputStack;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.common.crafting.SizedIngredient;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public record StabilizingRecipe(SizedIngredient input, int energy) implements MachineRecipe<SingleRecipeInput> {


    @Override
    public int getBaseEnergyCost() {
        return energy;
    }

    @Override
    public NonNullList<Ingredient> getIngredients() {
        return NonNullList.of(Ingredient.EMPTY, input.ingredient());
    }

    @Override
    public List<OutputStack> craft(SingleRecipeInput container, RegistryAccess registryAccess) {
        return List.of();
    }

    @Override
    public List<OutputStack> getResultStacks(RegistryAccess registryAccess) {
        return List.of();
    }

    @Override
    public boolean matches(SingleRecipeInput input, Level level) {
        return this.input.test(input.item());
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return StabilizerModule.STABILIZING.serializer().get();
    }

    @Override
    public RecipeType<?> getType() {
        return StabilizerModule.STABILIZING.type().get();
    }

    public static class Serializer implements RecipeSerializer<StabilizingRecipe> {
        public static final MapCodec<StabilizingRecipe> CODEC = RecordCodecBuilder.mapCodec(inst -> inst
                .group(SizedIngredient.FLAT_CODEC.fieldOf("input").forGetter(StabilizingRecipe::input),
                        Codec.INT.fieldOf("energy").forGetter(StabilizingRecipe::energy))
                .apply(inst, StabilizingRecipe::new));

        public static final StreamCodec<RegistryFriendlyByteBuf, StabilizingRecipe> STREAM_CODEC = StreamCodec
                .composite(SizedIngredient.STREAM_CODEC, StabilizingRecipe::input,
                        ByteBufCodecs.INT, StabilizingRecipe::energy,
                        StabilizingRecipe::new);

        @Override
        public @NotNull MapCodec<StabilizingRecipe> codec() {
            return CODEC;
        }

        @Override
        public @NotNull StreamCodec<RegistryFriendlyByteBuf, StabilizingRecipe> streamCodec() {
            return STREAM_CODEC;
        }
    }
}
