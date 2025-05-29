package com.kjmaster.resonantia.capabilities.energy;

import net.minecraft.core.Direction;
import net.neoforged.neoforge.energy.IEnergyStorage;

public interface ISidedEnergy extends IEnergyStorage {
    int receiveEnergyFacing(Direction facing, int maxReceive, boolean simulate);
    int extractEnergyFacing(Direction facing, int maxExtract, boolean simulate);
    boolean canExtractFacing(Direction facing);
    boolean canReceiveFacing(Direction facing);
}
