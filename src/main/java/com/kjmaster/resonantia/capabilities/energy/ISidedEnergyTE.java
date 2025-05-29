package com.kjmaster.resonantia.capabilities.energy;

import com.kjmaster.resonantia.data.Mode;
import net.minecraft.core.Direction;

public interface ISidedEnergyTE {
    Mode getEnergyMode(Direction side);
}
