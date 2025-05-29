package com.kjmaster.resonantia.capabilities.fluids;

import com.kjmaster.resonantia.data.Mode;
import mcjty.lib.varia.CustomTank;
import net.minecraft.core.Direction;
import net.neoforged.neoforge.fluids.FluidStack;
import org.jetbrains.annotations.NotNull;

public class SidedFluidHandler implements ISidedFluidHandler {

    private final ISidedFluidTE te;
    private final Direction facing;
    private final CustomTank wrapped;

    public SidedFluidHandler(ISidedFluidTE te, CustomTank wrapped, Direction facing) {
        this.te = te;
        this.facing = facing;
        this.wrapped = wrapped;
    }

    @Override
    public int getTanks() {
        return this.wrapped.getTanks();
    }

    @Override
    public @NotNull FluidStack getFluidInTank(int tank) {
        return this.wrapped.getFluidInTank(tank);
    }

    @Override
    public int getTankCapacity(int tank) {
        return this.wrapped.getTankCapacity(tank);
    }

    @Override
    public boolean isFluidValid(int tank, @NotNull FluidStack stack) {
        return this.wrapped.isFluidValid(tank, stack);
    }

    @Override
    public int fill(@NotNull FluidStack resource, @NotNull FluidAction action) {
        return this.fill(resource, action, facing);
    }

    @Override
    public @NotNull FluidStack drain(@NotNull FluidStack resource, @NotNull FluidAction action) {
        return this.drain(resource, action, facing);
    }

    @Override
    public @NotNull FluidStack drain(int maxDrain, @NotNull FluidAction action) {
        return this.drain(maxDrain, action, facing);
    }

    @Override
    public int fill(FluidStack resource, FluidAction action, Direction facing) {
        Mode mode = te.getFluidMode(facing);
        return mode == Mode.MODE_INPUT || mode == Mode.MODE_BOTH ? this.wrapped.fill(resource, action) : 0;
    }

    @Override
    public FluidStack drain(FluidStack resource, FluidAction action, Direction facing) {
        Mode mode = te.getFluidMode(facing);
        return mode == Mode.MODE_OUTPUT || mode == Mode.MODE_BOTH ? this.wrapped.drain(resource, action) : FluidStack.EMPTY;
    }

    @Override
    public FluidStack drain(int maxDrain, FluidAction action, Direction facing) {
        Mode mode = te.getFluidMode(facing);
        return mode == Mode.MODE_OUTPUT || mode == Mode.MODE_BOTH ? this.wrapped.drain(maxDrain, action) : FluidStack.EMPTY;
    }
}
