package com.kjmaster.resonantia.capabilities.item;

import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;

public interface ISidedItemHandler {

    ItemStack insertItem(int slot, ItemStack stack, boolean simulate, Direction facing);

    ItemStack extractItem(int slot, int amount, boolean simulate, Direction facing);

}
