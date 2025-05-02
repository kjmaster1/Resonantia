package com.kjmaster.resonantia.api.frequency;

import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.capabilities.BlockCapability;
import org.jetbrains.annotations.Nullable;

import static com.kjmaster.resonantia.Resonantia.MODID;

public class CapabilityFrequency {
    public static final BlockCapability<IFrequency, @Nullable Direction> FREQUENCY_CAPABILITY = BlockCapability.createSided(ResourceLocation.fromNamespaceAndPath(MODID, "frequency"), IFrequency.class);

    public CapabilityFrequency() {
    }
}
