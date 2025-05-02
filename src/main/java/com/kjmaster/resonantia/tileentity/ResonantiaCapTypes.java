package com.kjmaster.resonantia.tileentity;

import com.kjmaster.resonantia.api.frequency.CapabilityFrequency;
import net.neoforged.neoforge.capabilities.BlockCapability;

public enum ResonantiaCapTypes {

    FREQUENCY(CapabilityFrequency.FREQUENCY_CAPABILITY);

    private final BlockCapability capability;

    private ResonantiaCapTypes(BlockCapability capability) {
        this.capability = capability;
    }

    public BlockCapability getCapability() {
        return this.capability;
    }
}