package com.kjmaster.resonantia.capabilities.item;

import com.kjmaster.resonantia.data.Mode;
import mcjty.lib.container.GenericItemHandler;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.common.util.INBTSerializable;
import net.neoforged.neoforge.items.IItemHandlerModifiable;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;

public class SidedItemHandler implements ISidedItemHandler, IItemHandlerModifiable, INBTSerializable<CompoundTag> {

    private final ISidedItemTE te;
    private final Direction facing;
    private final GenericItemHandler wrapped;

    public SidedItemHandler(ISidedItemTE te, GenericItemHandler wrapped, Direction facing) {
        this.te = te;
        this.wrapped = wrapped;
        this.facing = facing;
    }

    public void setStackInSlot(int slot, @Nonnull ItemStack stack) {
        this.wrapped.setStackInSlot(slot, stack);
    }

    @Override
    public int getSlots() {
        return this.wrapped.getSlots();
    }

    @Override
    public @NotNull ItemStack getStackInSlot(int slot) {
        return this.wrapped.getStackInSlot(slot);
    }

    @Override
    public @NotNull ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
        return this.insertItem(slot, stack, simulate, facing);
    }

    @Override
    public @NotNull ItemStack extractItem(int slot, int amount, boolean simulate) {
        return this.extractItem(slot, amount, simulate, facing);
    }

    @Override
    public int getSlotLimit(int slot) {
        return this.wrapped.getSlotLimit(slot);
    }

    @Override
    public boolean isItemValid(int slot, ItemStack stack) {
        return this.wrapped.isItemValid(slot, stack);
    }

    @Override
    public ItemStack insertItem(int slot, ItemStack stack, boolean simulate, Direction facing) {
        if (!this.canAutomationInsert(slot)) return  stack;
        Mode mode = te.getItemMode(facing);
        return mode == Mode.MODE_INPUT || mode == Mode.MODE_BOTH ? this.wrapped.insertItem(slot, stack, simulate) : stack;
    }

    @Override
    public ItemStack extractItem(int slot, int amount, boolean simulate, Direction facing) {
        if (!this.canAutomationExtract(slot)) return ItemStack.EMPTY;
        Mode mode = te.getItemMode(facing);
        return mode == Mode.MODE_OUTPUT || mode == Mode.MODE_BOTH ? this.wrapped.extractItem(slot, amount, simulate) : ItemStack.EMPTY;
    }

    public boolean canAutomationInsert(int slot) {
        return this.wrapped.getContainerFactory().isInputSlot(slot);
    }

    public boolean canAutomationExtract(int slot) {
        return this.wrapped.getContainerFactory().isOutputSlot(slot);
    }

    public CompoundTag serializeNBT(HolderLookup.Provider provider) {
        return this.wrapped.serializeNBT(provider);
    }

    public void deserializeNBT(HolderLookup.Provider provider, CompoundTag nbt) {
        this.wrapped.deserializeNBT(provider, nbt);
    }
}
