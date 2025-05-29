package com.kjmaster.resonantia.capabilities.fluids;

import com.kjmaster.resonantia.data.Mode;
import net.minecraft.core.Direction;

public interface ISidedFluidTE {
    Mode getFluidMode(Direction side);
}
