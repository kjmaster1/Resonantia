package com.kjmaster.resonantia.capabilities.energy;

import net.neoforged.neoforge.energy.IEnergyStorage;

public class NullEnergyHandler implements IEnergyStorage {

    private final INullEnergy nullEnergy;

    public NullEnergyHandler(INullEnergy nullEnergy) {
        this.nullEnergy = nullEnergy;
    }

    @Override
    public int receiveEnergy(int toReceive, boolean simulate) {
        return 0;
    }

    @Override
    public int extractEnergy(int toExtract, boolean simulate) {
        return 0;
    }

    @Override
    public int getEnergyStored() {
        return this.nullEnergy.getEnergyStored();
    }

    @Override
    public int getMaxEnergyStored() {
        return this.nullEnergy.getMaxEnergyStored();
    }

    @Override
    public boolean canExtract() {
        return false;
    }

    @Override
    public boolean canReceive() {
        return false;
    }
}
