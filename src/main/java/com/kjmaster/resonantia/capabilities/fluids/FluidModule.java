package com.kjmaster.resonantia.capabilities.fluids;

import com.kjmaster.resonantia.capabilities.ICapabilityModule;
import com.kjmaster.resonantia.data.Mode;
import com.kjmaster.resonantia.data.SidedFluidModeData;
import com.kjmaster.resonantia.data.SidedItemModeData;
import com.kjmaster.resonantia.tileentity.ModularResonatingMachineTE;
import mcjty.lib.api.container.DefaultContainerProvider;
import mcjty.lib.api.container.IGenericContainer;
import mcjty.lib.setup.Registration;
import mcjty.lib.varia.CustomTank;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.world.inventory.DataSlot;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.client.model.data.ModelData;
import net.neoforged.neoforge.common.util.Lazy;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;

import java.util.function.BiFunction;

import static com.kjmaster.resonantia.setup.Registration.*;

public class FluidModule implements ICapabilityModule<CustomTank, IFluidHandler, SidedFluidModeData> {

    private final CustomTank masterFluidHandler;
    private Lazy<SidedFluidHandler>[] sidedFluidHandlers;
    private final AttachmentType<SidedFluidModeData> sidedModeDataAttachment;

    public FluidModule(CustomTank fluidHandler, AttachmentType<SidedFluidModeData> sidedModeDataAttachment) {
        this.masterFluidHandler = fluidHandler;
        this.sidedModeDataAttachment = sidedModeDataAttachment;
        if (this.masterFluidHandler != null) {
            this.sidedFluidHandlers = (Lazy<SidedFluidHandler>[]) new Lazy[Direction.values().length];
        }
    }

    @Override
    public void initializeSidedHandlers(ModularResonatingMachineTE tileEntity, CustomTank masterHandler) {
        if (!isActive() || masterHandler == null) return;
        for (Direction dir : Direction.values()) {
            this.sidedFluidHandlers[dir.ordinal()] = Lazy.of(() -> new SidedFluidHandler(tileEntity, masterHandler, dir));
        }
    }

    @Override
    public boolean isActive() {
        return this.masterFluidHandler != null;
    }

    @Override
    public IFluidHandler getSidedHandler(ModularResonatingMachineTE tileEntity, Direction facing) {
        if (!isActive()) return null;
        if (facing == null) {
            return masterFluidHandler;
        }
        Mode mode = getMode(tileEntity, facing);
        if (mode != Mode.MODE_NONE && sidedFluidHandlers != null && sidedFluidHandlers[facing.ordinal()] != null) {
            return sidedFluidHandlers[facing.ordinal()].get();
        }
        return null;
    }

    @Override
    public CustomTank getMasterHandler() {
        return masterFluidHandler;
    }

    @Override
    public Mode getMode(ModularResonatingMachineTE tileEntity, Direction side) {
        if (!isActive()) return Mode.MODE_NONE;
        SidedFluidModeData data = tileEntity.getData(this.sidedModeDataAttachment);
        return data.getFluidModeFromDirection(side);
    }

    @Override
    public void setMode(ModularResonatingMachineTE tileEntity, Mode mode, Direction side) {
        if (!isActive()) return;
        SidedFluidModeData oldData = tileEntity.getData(this.sidedModeDataAttachment);
        SidedFluidModeData newData;

        switch (side) {
            case UP -> newData = oldData.withFluidModeUp(mode);
            case DOWN -> newData = oldData.withFluidModeDown(mode);
            case NORTH -> newData = oldData.withFluidModeNorth(mode);
            case SOUTH -> newData = oldData.withFluidModeSouth(mode);
            case WEST -> newData = oldData.withFluidModeWest(mode);
            case EAST -> newData = oldData.withFluidModeEast(mode);
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
    public AttachmentType<SidedFluidModeData> getSidedModeDataAttachmentType() {
        return this.sidedModeDataAttachment;
    }

    @Override
    public void onSidedModeDataChanged(Object oldData, Object newData, ModularResonatingMachineTE tileEntity) {
        if (tileEntity.getLevel() == null || tileEntity.getLevel().isClientSide()) return;
        for (Direction dir : Direction.values()) {
            if (((SidedFluidModeData)newData).getFluidModeFromDirection(dir) != ((SidedFluidModeData)oldData).getFluidModeFromDirection(dir)) {
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
        provider.integerListener(new DataSlot() {
                    @Override
                    public int get() {
                        return masterFluidHandler.getFluidAmount();
                    }

                    @Override
                    public void set(int amount) {
                        masterFluidHandler.getFluid().setAmount(amount);
                    }
                })
                .data(() -> this.sidedModeDataAttachment, SidedFluidModeData.STREAM_CODEC, SidedFluidModeData.CODEC);
    }

    @Override
    public void loadClientData(CompoundTag tag, HolderLookup.Provider registries, ModularResonatingMachineTE tileEntity) {
        if (!isActive()) return;
        // Assuming "sided_fluid_mode_data" is the key
        if (tag.contains("sided_fluid_mode_data")) {
            SidedFluidModeData data = SidedFluidModeData.CODEC.parse(NbtOps.INSTANCE, tag.getCompound("sided_fluid_mode_data")).getOrThrow();
            tileEntity.setData(this.sidedModeDataAttachment, data);
            tileEntity.requestModelDataUpdate();
        }
    }

    @Override
    public void saveClientData(CompoundTag tag, HolderLookup.Provider registries, ModularResonatingMachineTE tileEntity) {
        if (!isActive()) return;
        tag.put("sided_fluid_mode_data", SidedFluidModeData.CODEC.encodeStart(NbtOps.INSTANCE, tileEntity.getData(this.sidedModeDataAttachment)).getOrThrow());
    }

    @Override
    public void loadAdditional(CompoundTag tag, HolderLookup.Provider registries, ModularResonatingMachineTE tileEntity) {
        if (!isActive()) return;
        this.masterFluidHandler.load(tag, "fluids", registries);
    }

    @Override
    public void saveAdditional(CompoundTag tag, HolderLookup.Provider registries, ModularResonatingMachineTE tileEntity) {
        if (!isActive()) return;
        this.masterFluidHandler.save(tag, "fluids", registries);
    }

    @Override
    public void applyImplicitComponents(BlockEntity.DataComponentInput input, ModularResonatingMachineTE tileEntity) {
        if (!isActive()) return;
        this.masterFluidHandler.applyImplicitComponents(input.get(Registration.ITEM_FLUIDS));
        SidedFluidModeData fluidModeData = input.get(ITEM_SIDED_FLUID_MODE_DATA);
        if (fluidModeData != null) {
            tileEntity.setData(this.sidedModeDataAttachment, fluidModeData);
        }
    }

    @Override
    public void collectImplicitComponents(DataComponentMap.Builder components, ModularResonatingMachineTE tileEntity) {
        if (!isActive()) return;
        this.masterFluidHandler.collectImplicitComponents(components);
        components.set(ITEM_SIDED_FLUID_MODE_DATA, tileEntity.getData(this.sidedModeDataAttachment));
    }

    @Override
    public void onDataPacket(CompoundTag tag, ModularResonatingMachineTE tileEntity, Object oldData) {
        if (!tag.isEmpty() && tag.contains("sided_fluid_mode_data")) {
            SidedFluidModeData newData = SidedFluidModeData.CODEC.parse(NbtOps.INSTANCE, tag.getCompound("sided_fluid_mode_data")).getOrThrow();
            for (Direction dir : Direction.values()) {
                if (newData.getFluidModeFromDirection(dir) != ((SidedFluidModeData) oldData).getFluidModeFromDirection(dir)) {
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
        builder.with(FLUID_NORTH, this.getMode(tileEntity, Direction.NORTH));
        builder.with(FLUID_SOUTH, this.getMode(tileEntity, Direction.SOUTH));
        builder.with(FLUID_WEST, this.getMode(tileEntity, Direction.WEST));
        builder.with(FLUID_EAST, this.getMode(tileEntity, Direction.EAST));
        builder.with(FLUID_UP, this.getMode(tileEntity, Direction.UP));
        builder.with(FLUID_DOWN, this.getMode(tileEntity, Direction.DOWN));
    }

    @Override
    public BiFunction<ModularResonatingMachineTE, Object, IFluidHandler> getCapabilityProviderFunction() {
        return (tile, context) -> getSidedHandler(tile, (Direction) context);
    }
}
