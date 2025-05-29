package com.kjmaster.resonantia.capabilities.energy;

import com.kjmaster.resonantia.capabilities.ICapabilityModule;
import com.kjmaster.resonantia.data.Mode;
import com.kjmaster.resonantia.data.SidedEnergyModeData;
import com.kjmaster.resonantia.tileentity.ModularResonatingMachineTE;
import mcjty.lib.api.container.DefaultContainerProvider;
import mcjty.lib.api.container.IGenericContainer;
import mcjty.lib.setup.Registration;
import mcjty.lib.tileentity.GenericEnergyStorage;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.client.model.data.ModelData;
import net.neoforged.neoforge.common.util.Lazy;
import net.neoforged.neoforge.energy.IEnergyStorage;

import java.util.function.BiFunction;

import static com.kjmaster.resonantia.setup.Registration.*;

public class EnergyModule implements ICapabilityModule<GenericEnergyStorage, IEnergyStorage, SidedEnergyModeData> {

    private final GenericEnergyStorage masterEnergyStorage;
    private Lazy<SidedEnergyHandler>[] sidedEnergyHandlers;
    private final AttachmentType<SidedEnergyModeData> sidedModeDataAttachment;

    public EnergyModule(GenericEnergyStorage energyStorage, AttachmentType<SidedEnergyModeData> sidedModeDataAttachment) {
        this.masterEnergyStorage = energyStorage;
        this.sidedModeDataAttachment = sidedModeDataAttachment;
        if (this.masterEnergyStorage != null) {
            // Initialize array, but defer Lazy creation to initializeSidedHandlers
            this.sidedEnergyHandlers = (Lazy<SidedEnergyHandler>[]) new Lazy[Direction.values().length];
        }
    }

    @Override
    public void initializeSidedHandlers(ModularResonatingMachineTE tileEntity, GenericEnergyStorage masterHandler) {
        if (!isActive() || masterHandler == null) return;
        for (Direction dir : Direction.values()) {
            this.sidedEnergyHandlers[dir.ordinal()] = Lazy.of(() -> new SidedEnergyHandler(tileEntity, masterHandler, dir));
        }
    }

    @Override
    public boolean isActive() {
        return this.masterEnergyStorage != null;
    }

    @Override
    public IEnergyStorage getSidedHandler(ModularResonatingMachineTE tileEntity, Direction facing) {
        if (!isActive()) return null;
        if (facing == null) {
            return masterEnergyStorage;
        }
        Mode mode = getMode(tileEntity, facing);
        if (mode != Mode.MODE_NONE && sidedEnergyHandlers != null && sidedEnergyHandlers[facing.ordinal()] != null) {
            return sidedEnergyHandlers[facing.ordinal()].get();
        }
        return null;
    }

    @Override
    public GenericEnergyStorage getMasterHandler() {
        return masterEnergyStorage;
    }

    @Override
    public Mode getMode(ModularResonatingMachineTE tileEntity, Direction side) {
        if (!isActive()) return Mode.MODE_NONE;
        SidedEnergyModeData data = tileEntity.getData(this.sidedModeDataAttachment);
        return data.getEnergyModeFromDirection(side); // Assuming SidedEnergyModeData has this
    }

    @Override
    public void setMode(ModularResonatingMachineTE tileEntity, Mode mode, Direction side) {
        if (!isActive()) return;
        SidedEnergyModeData oldData = tileEntity.getData(this.sidedModeDataAttachment);
        SidedEnergyModeData newData;
        switch (side) {
            case UP -> newData = oldData.withEnergyModeUp(mode);
            case DOWN -> newData = oldData.withEnergyModeDown(mode);
            case NORTH -> newData = oldData.withEnergyModeNorth(mode);
            case SOUTH -> newData = oldData.withEnergyModeSouth(mode);
            case WEST -> newData = oldData.withEnergyModeWest(mode);
            case EAST -> newData = oldData.withEnergyModeEast(mode);
            default -> {
                return;
            }
        }

        if (oldData != newData) {
            tileEntity.setData(this.sidedModeDataAttachment, newData);
            tileEntity.onDataChanged(this.sidedModeDataAttachment, oldData, newData);
        }
    }

    @Override
    public AttachmentType<SidedEnergyModeData> getSidedModeDataAttachmentType() {
        return this.sidedModeDataAttachment;
    }

    @Override
    public void onSidedModeDataChanged(Object oldData, Object newData, ModularResonatingMachineTE tileEntity) {
        if (tileEntity.getLevel() == null || tileEntity.getLevel().isClientSide()) return;
        for (Direction dir : Direction.values()) {
            if (((SidedEnergyModeData) newData).getEnergyModeFromDirection(dir) != ((SidedEnergyModeData) oldData).getEnergyModeFromDirection(dir)) {
                tileEntity.invalidateCapabilities();
                tileEntity.markDirtyClient();
                tileEntity.getLevel().updateNeighborsAt(tileEntity.getBlockPos(), tileEntity.getBlockState().getBlock());
                break;
            }
        }
    }

    @Override
    public void configureContainerProvider(DefaultContainerProvider<IGenericContainer> provider, ModularResonatingMachineTE tileEntity) {
        if (!isActive()) return;
        provider.energyHandler(() -> this.masterEnergyStorage)
                .data(() -> this.sidedModeDataAttachment, SidedEnergyModeData.STREAM_CODEC, SidedEnergyModeData.CODEC);
    }

    @Override
    public void loadClientData(CompoundTag tag, HolderLookup.Provider registries, ModularResonatingMachineTE tileEntity) {
        if (!isActive()) return;
        SidedEnergyModeData data = SidedEnergyModeData.CODEC.parse(NbtOps.INSTANCE, tag.getCompound("sided_energy_mode_data")).getOrThrow();
        tileEntity.setData(SIDED_ENERGY_MODE_DATA, data);
        tileEntity.requestModelDataUpdate();
    }

    @Override
    public void saveClientData(CompoundTag tag, HolderLookup.Provider registries, ModularResonatingMachineTE tileEntity) {
        if (!isActive()) return;
        tag.put("sided_energy_mode_data", SidedEnergyModeData.CODEC.encodeStart(NbtOps.INSTANCE, tileEntity.getData(SIDED_ENERGY_MODE_DATA)).getOrThrow());
    }

    @Override
    public void loadAdditional(CompoundTag tag, HolderLookup.Provider registries, ModularResonatingMachineTE tileEntity) {
        if (!isActive()) return;
        this.masterEnergyStorage.load(tag, "energy", registries);
    }

    @Override
    public void saveAdditional(CompoundTag tag, HolderLookup.Provider registries, ModularResonatingMachineTE tileEntity) {
        if (!isActive()) return;
        this.masterEnergyStorage.save(tag, "energy", registries);
    }

    @Override
    public void applyImplicitComponents(BlockEntity.DataComponentInput input, ModularResonatingMachineTE tileEntity) {
        if (!isActive()) return;
        this.masterEnergyStorage.applyImplicitComponents(input.get(Registration.ITEM_ENERGY));
        SidedEnergyModeData energyModeData = input.get(ITEM_SIDED_ENERGY_MODE_DATA);
        if (energyModeData != null) {
            tileEntity.setData(SIDED_ENERGY_MODE_DATA, energyModeData);
        }
    }

    @Override
    public void collectImplicitComponents(DataComponentMap.Builder components, ModularResonatingMachineTE tileEntity) {
        if (!isActive()) return;
        this.masterEnergyStorage.collectImplicitComponents(components);
        components.set(ITEM_SIDED_ENERGY_MODE_DATA, tileEntity.getData(SIDED_ENERGY_MODE_DATA));
    }

    @Override
    public void onDataPacket(CompoundTag tag, ModularResonatingMachineTE tileEntity, Object oldData) {
        if (!tag.isEmpty() && tag.contains("sided_energy_mode_data")) {
            SidedEnergyModeData newData = SidedEnergyModeData.CODEC.parse(NbtOps.INSTANCE, tag.getCompound("sided_energy_mode_data")).getOrThrow();
            for (Direction dir : Direction.values()) {
                if (newData.getEnergyModeFromDirection(dir) != ((SidedEnergyModeData) oldData).getEnergyModeFromDirection(dir)) {
                    tileEntity.requestModelDataUpdate();
                    if (tileEntity.getLevel() == null) return;
                    tileEntity.invalidateCapabilities();
                    tileEntity.getLevel().sendBlockUpdated(tileEntity.getBlockPos(), tileEntity.getBlockState(), tileEntity.getBlockState(), Block.UPDATE_ALL);
                    break;
                }
            }
        }
    }

    @Override
    public void populateModelData(ModelData.Builder builder, ModularResonatingMachineTE tileEntity) {
        if (!isActive()) return;
        builder.with(ENERGY_NORTH, this.getMode(tileEntity, Direction.NORTH));
        builder.with(ENERGY_SOUTH, this.getMode(tileEntity, Direction.SOUTH));
        builder.with(ENERGY_WEST, this.getMode(tileEntity, Direction.WEST));
        builder.with(ENERGY_EAST, this.getMode(tileEntity, Direction.EAST));
        builder.with(ENERGY_UP, this.getMode(tileEntity, Direction.UP));
        builder.with(ENERGY_DOWN, this.getMode(tileEntity, Direction.DOWN));
    }

    @Override
    public BiFunction<ModularResonatingMachineTE, Object, IEnergyStorage> getCapabilityProviderFunction() {
        return (tile, context) -> getSidedHandler(tile, (Direction) context);
    }
}
