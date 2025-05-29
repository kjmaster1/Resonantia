package com.kjmaster.resonantia.recipes;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import java.util.function.Function;

/**
 * <a href="https://github.com/Team-EnderIO/EnderIO/blob/dev/1.21.1/endercore/src/main/java/com/enderio/core/common/recipes/WrappedShapedRecipe.java">From EnderIO</a>
 */
public abstract class WrappedShapedRecipe extends ShapedRecipe implements CraftingRecipe {
    private final ShapedRecipe wrapped;

    protected WrappedShapedRecipe(ShapedRecipe wrapped) {
        super(wrapped.getGroup(), wrapped.category(), wrapped.pattern, ItemStack.EMPTY, wrapped.showNotification());
        this.wrapped = wrapped;
    }

    public ShapedRecipe getWrapped() {
        return wrapped;
    }

    @Override
    public @NotNull CraftingBookCategory category() {
        return wrapped.category();
    }

    @Override
    public abstract @NotNull ItemStack assemble(@NotNull CraftingInput inv, HolderLookup.@NotNull Provider lookupProvider);

    @Override
    public boolean matches(@NotNull CraftingInput inv, @NotNull Level world) {
        // Note: We do not override the matches method if it matches ignoring NBT,
        // to ensure that we return the proper value for if there is a match that gives
        // a proper output
        return wrapped.matches(inv, world) && !assemble(inv, world.registryAccess()).isEmpty();
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return wrapped.canCraftInDimensions(width, height);
    }

    @Override
    public @NotNull ItemStack getResultItem(HolderLookup.@NotNull Provider lookupProvider) {
        return wrapped.getResultItem(lookupProvider);
    }

    @Override
    public @NotNull NonNullList<ItemStack> getRemainingItems(@NotNull CraftingInput inv) {
        return wrapped.getRemainingItems(inv);
    }

    @Override
    public @NotNull NonNullList<Ingredient> getIngredients() {
        return wrapped.getIngredients();
    }

    @Override
    public boolean isSpecial() {
        return wrapped.isSpecial();
    }

    @Override
    public @NotNull String getGroup() {
        return wrapped.getGroup();
    }

    @Override
    public @NotNull ItemStack getToastSymbol() {
        return wrapped.getToastSymbol();
    }

    @Override
    public int getWidth() {
        return wrapped.getWidth();
    }

    @Override
    public int getHeight() {
        return wrapped.getHeight();
    }

    @Override
    public boolean isIncomplete() {
        return wrapped.isIncomplete();
    }

    public static class Serializer<T extends WrappedShapedRecipe> implements RecipeSerializer<T> {
        private final Function<ShapedRecipe, T> wrapper;
        private MapCodec<T> codec;
        private StreamCodec<RegistryFriendlyByteBuf, T> streamCodec;

        public Serializer(Function<ShapedRecipe, T> wrapper) {
            this.wrapper = wrapper;
        }

        @NotNull
        @Override
        public MapCodec<T> codec() {
            if (codec == null) {
                codec = RecipeSerializer.SHAPED_RECIPE.codec().xmap(wrapper, WrappedShapedRecipe::getWrapped);
            }

            return codec;
        }

        @Override
        public @NotNull StreamCodec<RegistryFriendlyByteBuf, T> streamCodec() {
            if (streamCodec == null) {
                streamCodec = RecipeSerializer.SHAPED_RECIPE.streamCodec()
                        .map(wrapper, WrappedShapedRecipe::getWrapped);
            }

            return streamCodec;
        }
    }
}
