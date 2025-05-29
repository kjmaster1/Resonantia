package com.kjmaster.resonantia.capabilities.energy;

import com.kjmaster.resonantia.data.Mode;
import mcjty.lib.tileentity.GenericEnergyStorage;
import net.minecraft.core.Direction;

public class SidedEnergyHandler implements ISidedEnergy {

    private final ISidedEnergyTE te;
    private final GenericEnergyStorage wrapped;
    private final Direction facing;

    public SidedEnergyHandler(ISidedEnergyTE te, GenericEnergyStorage energyStorage, Direction facing) {
        this.te = te;
        this.wrapped = energyStorage;
        this.facing = facing;
    }

    @Override
    public int receiveEnergyFacing(Direction facing, int maxReceive, boolean simulate) {
        Mode mode = te.getEnergyMode(facing);
        return mode == Mode.MODE_INPUT || mode == Mode.MODE_BOTH ? this.wrapped.receiveEnergy(maxReceive, simulate) : 0;
    }

    @Override
    public int extractEnergyFacing(Direction facing, int maxExtract, boolean simulate) {
        Mode mode = te.getEnergyMode(facing);
        return mode == Mode.MODE_OUTPUT || mode == Mode.MODE_BOTH ? this.wrapped.extractEnergy(maxExtract, simulate) : 0;
    }

    @Override
    public boolean canExtractFacing(Direction facing) {
        Mode mode = te.getEnergyMode(facing);
        return (mode == Mode.MODE_OUTPUT || mode == Mode.MODE_BOTH) && this.wrapped.canExtract();
    }

    @Override
    public boolean canReceiveFacing(Direction facing) {
        Mode mode = te.getEnergyMode(facing);
        return (mode == Mode.MODE_INPUT || mode == Mode.MODE_BOTH) && this.wrapped.canReceive();
    }

    @Override
    public int receiveEnergy(int toReceive, boolean simulate) {
        return this.receiveEnergyFacing(facing, toReceive, simulate);
    }

    @Override
    public int extractEnergy(int toExtract, boolean simulate) {
        return this.extractEnergyFacing(facing, toExtract, simulate);
    }

    @Override
    public int getEnergyStored() {
        return this.wrapped.getEnergyStored();
    }

    @Override
    public int getMaxEnergyStored() {
        return this.wrapped.getMaxEnergyStored();
    }

    @Override
    public boolean canExtract() {
        return this.canExtractFacing(facing);
    }

    @Override
    public boolean canReceive() {
        return this.canReceiveFacing(facing);
    }
}
