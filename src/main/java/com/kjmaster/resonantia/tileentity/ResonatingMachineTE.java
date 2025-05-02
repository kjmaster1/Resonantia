package com.kjmaster.resonantia.tileentity;

import com.kjmaster.resonantia.api.frequency.DriftContext;
import com.kjmaster.resonantia.api.frequency.IFrequency;
import com.kjmaster.resonantia.api.machine.IResonantMachine;
import com.kjmaster.resonantia.api.machine.ResonantMachineRole;
import com.kjmaster.resonantia.modules.resonantenergy.blocks.transmitter.ResonantEnergyTransmitterTileEntity;
import com.kjmaster.resonantia.modules.resonantenergy.data.ResonantMachineIndex;
import com.kjmaster.resonantia.resonance.PacketLinkVisualization;
import com.kjmaster.resonantia.resonance.PacketSyncEnabled;
import com.kjmaster.resonantia.resonance.ResonanceNetworkSavedData;
import com.kjmaster.resonantia.setup.ResonantiaMessages;
import mcjty.lib.api.infusable.DefaultInfusable;
import mcjty.lib.api.infusable.IInfusable;
import mcjty.lib.setup.Registration;
import mcjty.lib.tileentity.Cap;
import mcjty.lib.tileentity.CapType;
import mcjty.lib.tileentity.GenericEnergyStorage;
import mcjty.lib.varia.RedstoneMode;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.neoforge.attachment.AttachmentType;

import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;

import static com.kjmaster.resonantia.setup.Registration.*;

public class ResonatingMachineTE extends ResonantiaTileEntity implements IResonantMachine {

    public IFrequency frequency;
    @ResonantiaCap(type = ResonantiaCapTypes.FREQUENCY)
    private static final Function<ResonatingMachineTE, IFrequency> FREQUENCY_CAP = tile -> tile.frequency;

    public final DefaultInfusable infusableHandler = new DefaultInfusable(ResonatingMachineTE.this);
    @Cap(type = CapType.INFUSABLE)
    private static final Function<ResonatingMachineTE, IInfusable> INFUSABLE_CAP = tile -> tile.infusableHandler;

    public GenericEnergyStorage energyStorage;
    @Cap(type = CapType.ENERGY)
    private static final Function<ResonatingMachineTE, GenericEnergyStorage> ENERGY_CAP = tile -> tile.energyStorage;

    public static final VoxelShape SLAB = Shapes.box(0f, 0f, 0f, 1f, 0.5f, 1f);

    public transient boolean wasEnabled = false;

    public ResonatingMachineTE(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
        setRSMode(RedstoneMode.REDSTONE_ONREQUIRED);
    }

    @Override
    public ResonantMachineRole getMachineRole() {
        return ResonantMachineRole.PASSIVE;
    }

    @Override
    public void onDataChanged(AttachmentType<?> type, Object oldData, Object newData) {
        super.onDataChanged(type, oldData, newData);
        if (type == RESONANTIA_BE_DATA.get()) {
            onDataChanged((ResonantiaBEData) oldData, (ResonantiaBEData) newData);
        }
    }

    private void onDataChanged(ResonantiaBEData oldData, ResonantiaBEData newData) {
        if (level.isClientSide()) return;

        if (oldData.unstable() != newData.unstable()) {
            ResonantiaMessages.sendToPlayersTrackingChunk(PacketUpdateResonantiaBEData.create(getBlockPos(), newData), (ServerLevel) level, new ChunkPos(getBlockPos()));
        }
    }

    @Override
    protected boolean needsRedstoneMode() {
        return true;
    }

    @Override
    protected void tickServer() {
        markDirtyQuick();

        if (level == null) return;

        boolean isEnabled = isMachineEnabled();

        List<BlockPos> linkedMachines = List.of();

        if (isEnabled != wasEnabled) {
            if (isEnabled) {
                if (this instanceof ResonantEnergyTransmitterTileEntity) {
                    linkedMachines = findLinkedMachines(getBlockPos(), this.frequency.getFrequency(), this.frequency.getRadius(), this.frequency.getLinkTolerance());
                    ResonantiaMessages.sendToPlayersTrackingChunk(PacketLinkVisualization.create(getBlockPos(), linkedMachines), (ServerLevel) level, new ChunkPos(getBlockPos()));
                }
                ResonanceNetworkSavedData.get((ServerLevel) level).registerMachine(this.frequency);
            } else {
                if (this instanceof ResonantEnergyTransmitterTileEntity) {
                    ResonantiaMessages.sendToPlayersTrackingChunk(PacketLinkVisualization.create(getBlockPos(), linkedMachines), (ServerLevel) level, new ChunkPos(getBlockPos()));
                }
                ResonanceNetworkSavedData.get((ServerLevel) level).unregisterMachine(this.frequency);
                setUnstable(false);
            }
            ResonantiaMessages.sendToPlayersTrackingChunk(PacketSyncEnabled.create(getBlockPos(), isEnabled), (ServerLevel) level, new ChunkPos(getBlockPos()));
            wasEnabled = isEnabled; // Update tracker
        }

        if (!isEnabled) return;

        ServerLevel serverLevel = (ServerLevel) level;

        if (serverLevel.getGameTime() % this.frequency.getDriftInterval() == 0) {
            performDrift(serverLevel, this.frequency, getBlockPos());
        }

        if (linkedMachines.isEmpty()) {
            linkedMachines = findLinkedMachines(getBlockPos(), this.frequency.getFrequency(), this.frequency.getRadius(), this.frequency.getLinkTolerance());
        }

        boolean unstable = setUnstable(linkedMachines);

        if (unstable) {
            serverLevel.sendParticles(ParticleTypes.SMOKE, getBlockPos().getX() + 0.5, getBlockPos().getY() + 1.0, getBlockPos().getZ() + 0.5, 2, 0.2, 0.2, 0.2, 0.01);
        }

        performMachineOperations(linkedMachines, unstable);
    }

    public void performMachineOperations(List<BlockPos> linkedMachines, boolean unstable) {
    }

    public boolean setUnstable(List<BlockPos> testList) {
        boolean unstable = ResonanceNetworkSavedData.get((ServerLevel) level).isMachineUnstable(testList, this.frequency);
        setUnstable(unstable);
        return unstable;
    }

    public void setUnstable(boolean unstable) {
        ResonantiaBEData resonantiaBEData = this.getData(RESONANTIA_BE_DATA);
        ResonantiaBEData newResonantiaBEData = resonantiaBEData.withUnstable(unstable);
        this.setData(RESONANTIA_BE_DATA, newResonantiaBEData);
        this.onDataChanged(RESONANTIA_BE_DATA.get(), resonantiaBEData, newResonantiaBEData);
        this.markDirtyClient();
    }

    public boolean isUnstable() {
        ResonantiaBEData data = this.getData(RESONANTIA_BE_DATA);
        return data.unstable();
    }

    private void performDrift(ServerLevel level, IFrequency frequencyCap, BlockPos pos) {
        int drift = frequencyCap.calculateDrift(new DriftContext(level, pos));
        if (drift != 0) {
            int newFrequency = frequencyCap.getFrequency() + drift;
            frequencyCap.setFrequency(newFrequency);
        }
    }

    @Override
    public void onLoad() {
        super.onLoad();
        if (level == null) return;
        if (!level.isClientSide) {
            wasEnabled = isMachineEnabled();
            ResonanceNetworkSavedData.get((ServerLevel) level).registerMachine(this.frequency);
            ResonantMachineIndex.add((ServerLevel) level, getBlockPos());
        }
    }

    @Override
    public void setRemoved() {
        super.setRemoved();
        if (level == null) return;
        if (!level.isClientSide) {
            ResonanceNetworkSavedData.get((ServerLevel) level).unregisterMachine(this.frequency);
            ResonantiaMessages.sendToPlayersTrackingChunk(PacketSyncEnabled.create(getBlockPos(), false), (ServerLevel) level, new ChunkPos(getBlockPos()));
            ResonantMachineIndex.remove((ServerLevel) level, getBlockPos());
        }
    }

    public List<BlockPos> findLinkedMachines(BlockPos origin, int frequency, int radius, int tolerance) {
        if (level == null) return Collections.emptyList();
        if (!level.isClientSide) {
            Predicate<BlockPos> activeTest = p -> {
                if (!(level instanceof ServerLevel serverLevel)) return false;
                if (!level.hasChunkAt(p)) return false;
                BlockEntity be = serverLevel.getBlockEntity(p);
                return be instanceof ResonatingMachineTE te && te.isMachineEnabled() && getMachineRole().isCompatibleWith(te.getMachineRole());
            };
            return ResonanceNetworkSavedData.get((ServerLevel) level).findLinkedMachines((ServerLevel) level, origin, frequency, radius, tolerance, activeTest);
        }
        return Collections.emptyList();
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider provider) {
        CompoundTag tag = super.getUpdateTag(provider);
        tag.putBoolean("unstable", getData(RESONANTIA_BE_DATA.get()).unstable());
        return tag;
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider provider) {
        super.loadAdditional(tag, provider);
        energyStorage.load(tag, "energy", provider);
        frequency.load(tag, "frequency");
        infusableHandler.load(tag, "infusable");
        setData(RESONANTIA_BE_DATA, new ResonantiaBEData(tag.getBoolean("unstable")));
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider provider) {
        super.saveAdditional(tag, provider);
        energyStorage.save(tag, "energy", provider);
        frequency.save(tag, "frequency");
        infusableHandler.save(tag, "infusable");
    }

    @Override
    protected void applyImplicitComponents(DataComponentInput input) {
        super.applyImplicitComponents(input);
        ResonantiaBEData data = input.get(ITEM_RESONANTIA_BE_DATA);
        if (data != null) {
            this.setData(RESONANTIA_BE_DATA, data);
        }
        energyStorage.applyImplicitComponents(input.get(Registration.ITEM_ENERGY));
        infusableHandler.applyImplicitComponents(input.get(Registration.ITEM_INFUSABLE));
        frequency.applyImplicitComponents(input.get(ITEM_FREQUENCY));
    }

    @Override
    protected void collectImplicitComponents(DataComponentMap.Builder components) {
        super.collectImplicitComponents(components);
        components.set(ITEM_RESONANTIA_BE_DATA, this.getData(RESONANTIA_BE_DATA));
        energyStorage.collectImplicitComponents(components);
        infusableHandler.collectImplicitComponents(components);
        frequency.collectImplicitComponents(components);
    }
}
