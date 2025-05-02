package com.kjmaster.resonantia.api.frequency;

import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.nbt.CompoundTag;

public interface IFrequency {
    int getFrequency();

    void setFrequency(int var1);

    void onFrequencyChanged();

    BlockPos getBlockPos();

    int getDriftInterval();
    int getLinkTolerance();
    int getDestabilizationTolerance();
    int getRadius();

    int calculateDrift(DriftContext driftContext);

    void save(CompoundTag tag, String tagName);
    void load(CompoundTag tag, String tagName);

    void applyImplicitComponents(ItemFrequency frequency);
    void collectImplicitComponents(DataComponentMap.Builder builder);
}
