package com.kjmaster.resonantia.capabilities.fluids;

import net.minecraft.core.Direction;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;

public interface ISidedFluidHandler extends IFluidHandler {
    public int fill(FluidStack resource, FluidAction action, Direction facing);

    public FluidStack drain(FluidStack resource, FluidAction action, Direction facing);

    public FluidStack drain(int maxDrain, FluidAction action, Direction facing);
}
