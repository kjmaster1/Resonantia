package com.kjmaster.resonantia.recipes;

import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.neoforged.neoforge.registries.DeferredHolder;

/**
 * <a href="https://github.com/Team-EnderIO/EnderIO/blob/dev/1.21.1/endercore/src/main/java/com/enderio/core/common/recipes/RecipeTypeSerializerPair.java">From EnderIO</a>
 */
public record RecipeTypeSerializerPair<R extends Recipe<?>, S extends RecipeSerializer<? extends R>>(
        DeferredHolder<RecipeType<?>, RecipeType<R>> type, DeferredHolder<RecipeSerializer<?>, S> serializer) {
}
