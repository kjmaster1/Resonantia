package com.kjmaster.resonantia.tileentity;

import com.kjmaster.resonantia.api.frequency.CapabilityFrequency;
import net.neoforged.neoforge.capabilities.BlockCapability;
import net.neoforged.neoforge.capabilities.Capabilities;

public enum ResonantiaCapTypes {

    FREQUENCY(CapabilityFrequency.FREQUENCY_CAPABILITY),
    ENERGY(Capabilities.EnergyStorage.BLOCK),
    ITEMS_AUTOMATION(Capabilities.ItemHandler.BLOCK),
    FLUIDS(Capabilities.FluidHandler.BLOCK);

    private final BlockCapability capability;

    private ResonantiaCapTypes(BlockCapability capability) {
        this.capability = capability;
    }

    public BlockCapability getCapability() {
        return this.capability;
    }
}