package com.kjmaster.resonantia.api.frequency;

import com.kjmaster.resonantia.resonance.ResonanceNetworkSavedData;
import com.kjmaster.resonantia.setup.Registration;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.entity.BlockEntity;

public class DefaultFrequency implements IFrequency {
    private final BlockEntity owner;
    private int frequency = 0;

    public DefaultFrequency(BlockEntity owner) {
        this.owner = owner;
    }

    public void applyImplicitComponents(ItemFrequency frequency) {
        if (frequency != null) {
            this.setFrequency(frequency.frequency());
        }
    }

    public void collectImplicitComponents(DataComponentMap.Builder builder) {
        builder.set(Registration.ITEM_FREQUENCY.get(), new ItemFrequency(this.getFrequency()));
    }

    @Override
    public int getFrequency() {
        return this.frequency;
    }

    @Override
    public void setFrequency(int i) {
        this.frequency = Math.clamp(i, 0, 10000);
        this.owner.setChanged();
        onFrequencyChanged();
    }

    @Override
    public void onFrequencyChanged() {
        if (owner.getLevel() == null) return;
        if (!owner.getLevel().isClientSide && owner.getLevel() instanceof ServerLevel serverLevel) {
            ResonanceNetworkSavedData resonanceNetworkSavedData = ResonanceNetworkSavedData.get(serverLevel);
            resonanceNetworkSavedData.updateMachine(this);
        }
    }

    @Override
    public BlockPos getBlockPos() {
        return this.owner.getBlockPos();
    }

    @Override
    public int getDriftInterval() {
        return 600;
    }

    @Override
    public int getLinkTolerance() {
        return 8;
    }

    @Override
    public int getDestabilizationTolerance() {
        return 4;
    }

    @Override
    public int getRadius() {
        return 8;
    }

    @Override
    public int calculateDrift(DriftContext driftContext) {

        ServerLevel level = driftContext.level();
        BlockPos pos = driftContext.pos();
        RandomSource rand = level.getRandom();

        int baseDrift = rand.nextInt(3) - 1; // -1, 0, +1 drift

        if (pos.getY() > 100) {
            baseDrift += rand.nextBoolean() ? 1 : 0;
        }

        return baseDrift;
    }

    public void save(CompoundTag tag, String tagName) {
        tag.putInt(tagName, this.frequency);
    }

    public void load(CompoundTag tag, String tagName) {
        this.frequency = tag.getInt(tagName);
    }
}

