package com.kjmaster.resonantia.modules.crusher.crafting;

import com.kjmaster.resonantia.crafting.MachineRecipe;
import com.kjmaster.resonantia.modules.crusher.CrusherModule;
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
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.common.crafting.SizedIngredient;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public record CrushingRecipe(SizedIngredient input, ItemStack output, int energy,
                             float experience) implements MachineRecipe<SingleRecipeInput> {

    @Override
    public int getBaseEnergyCost() {
        return energy;
    }

    @Override
    public @NotNull NonNullList<Ingredient> getIngredients() {
        return NonNullList.of(Ingredient.EMPTY, input.ingredient());
    }

    @Override
    public boolean matches(@NotNull SingleRecipeInput input, @NotNull Level level) {
        return this.input.test(input.item());
    }

    @Override
    public List<OutputStack> craft(SingleRecipeInput container, RegistryAccess registryAccess) {
        ItemStack outputStack = output.copy();
        return List.of(OutputStack.of(outputStack));
    }

    @Override
    public List<OutputStack> getResultStacks(RegistryAccess registryAccess) {
        return List.of(OutputStack.of(output.copy()));
    }

    @Override
    public @NotNull RecipeType<?> getType() {
        return CrusherModule.CRUSHING.type().get();
    }

    @Override
    public @NotNull RecipeSerializer<?> getSerializer() {
        return CrusherModule.CRUSHING.serializer().get();
    }

    public static class Serializer implements RecipeSerializer<CrushingRecipe> {
        public static final MapCodec<CrushingRecipe> CODEC = RecordCodecBuilder.mapCodec(inst -> inst
                .group(SizedIngredient.FLAT_CODEC.fieldOf("input").forGetter(CrushingRecipe::input),
                        ItemStack.CODEC.fieldOf("output").forGetter(CrushingRecipe::output),
                        Codec.INT.fieldOf("energy").forGetter(CrushingRecipe::energy),
                        Codec.FLOAT.fieldOf("experience").forGetter(CrushingRecipe::experience))
                .apply(inst, CrushingRecipe::new));

        public static final StreamCodec<RegistryFriendlyByteBuf, CrushingRecipe> STREAM_CODEC = StreamCodec
                .composite(SizedIngredient.STREAM_CODEC, CrushingRecipe::input,
                        ItemStack.STREAM_CODEC, CrushingRecipe::output, ByteBufCodecs.INT,
                        CrushingRecipe::energy, ByteBufCodecs.FLOAT, CrushingRecipe::experience,
                        CrushingRecipe::new);

        @Override
        public @NotNull MapCodec<CrushingRecipe> codec() {
            return CODEC;
        }

        @Override
        public @NotNull StreamCodec<RegistryFriendlyByteBuf, CrushingRecipe> streamCodec() {
            return STREAM_CODEC;
        }
    }
}
