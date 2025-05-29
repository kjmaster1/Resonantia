package com.kjmaster.resonantia.capabilities;

import com.kjmaster.resonantia.data.Mode;
import com.kjmaster.resonantia.tileentity.ModularResonatingMachineTE;
import mcjty.lib.api.container.DefaultContainerProvider;
import mcjty.lib.api.container.IGenericContainer;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.client.model.data.ModelData;

import java.util.function.BiFunction;

public interface ICapabilityModule<MASTER_HANDLER, SIDED_HANDLER, SIDED_DATA> {

    // Initialization
    void initializeSidedHandlers(ModularResonatingMachineTE tileEntity, MASTER_HANDLER masterHandler);

    // Capability Access
    SIDED_HANDLER getSidedHandler(ModularResonatingMachineTE tileEntity, Direction facing);
    MASTER_HANDLER getMasterHandler();

    // Mode Management
    Mode getMode(ModularResonatingMachineTE tileEntity, Direction side);
    void setMode(ModularResonatingMachineTE tileEntity, Mode mode, Direction side);

    // Container Provider Integration
    void configureContainerProvider(DefaultContainerProvider<IGenericContainer> provider, ModularResonatingMachineTE tileEntity);

    // NBT & Data Handling
    void loadClientData(CompoundTag tag, HolderLookup.Provider registries, ModularResonatingMachineTE tileEntity);
    void saveClientData(CompoundTag tag, HolderLookup.Provider registries, ModularResonatingMachineTE tileEntity);
    void loadAdditional(CompoundTag tag, HolderLookup.Provider registries, ModularResonatingMachineTE tileEntity);
    void saveAdditional(CompoundTag tag, HolderLookup.Provider registries, ModularResonatingMachineTE tileEntity);

    // Implicit Components
    void applyImplicitComponents(BlockEntity.DataComponentInput input, ModularResonatingMachineTE tileEntity);
    void collectImplicitComponents(DataComponentMap.Builder components, ModularResonatingMachineTE tileEntity);

    // Model Data
    void populateModelData(ModelData.Builder builder, ModularResonatingMachineTE tileEntity);

    // Data Changed Notification
    AttachmentType<SIDED_DATA> getSidedModeDataAttachmentType();
    void onSidedModeDataChanged(Object oldData, Object newData, ModularResonatingMachineTE tileEntity);
    void onDataPacket(CompoundTag tag, ModularResonatingMachineTE tileEntity, Object oldData);

    // Helper to know if this module is active (master handler is not null)
    boolean isActive();

    // Optional: Provide the ResonantiaCap BiFunction for this capability
    BiFunction<ModularResonatingMachineTE, Object, SIDED_HANDLER> getCapabilityProviderFunction();
}
