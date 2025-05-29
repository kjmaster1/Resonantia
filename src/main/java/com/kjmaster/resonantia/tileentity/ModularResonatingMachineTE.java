package com.kjmaster.resonantia.tileentity;

import com.kjmaster.resonantia.api.frequency.DefaultFrequency;
import com.kjmaster.resonantia.api.frequency.DriftContext;
import com.kjmaster.resonantia.api.frequency.IFrequency;
import com.kjmaster.resonantia.api.machine.IResonantMachine;
import com.kjmaster.resonantia.api.machine.ResonantMachineRole;
import com.kjmaster.resonantia.capabilities.ICapabilityModule;
import com.kjmaster.resonantia.capabilities.ResonantMachineCapabilityType;
import com.kjmaster.resonantia.capabilities.energy.EnergyModule;
import com.kjmaster.resonantia.capabilities.energy.ISidedEnergyTE;
import com.kjmaster.resonantia.capabilities.fluids.FluidModule;
import com.kjmaster.resonantia.capabilities.fluids.ISidedFluidTE;
import com.kjmaster.resonantia.capabilities.item.ISidedItemTE;
import com.kjmaster.resonantia.capabilities.item.ItemModule;
import com.kjmaster.resonantia.data.Mode;
import com.kjmaster.resonantia.modules.resonantenergy.blocks.transmitter.ResonantEnergyTransmitterTileEntity;
import com.kjmaster.resonantia.modules.resonantenergy.data.ResonantMachineIndex;
import com.kjmaster.resonantia.resonance.PacketLinkVisualization;
import com.kjmaster.resonantia.resonance.PacketSyncEnabled;
import com.kjmaster.resonantia.resonance.ResonanceNetworkSavedData;
import com.kjmaster.resonantia.setup.ResonantiaMessages;
import mcjty.lib.api.container.DefaultContainerProvider;
import mcjty.lib.api.container.IGenericContainer;
import mcjty.lib.api.infusable.DefaultInfusable;
import mcjty.lib.api.infusable.IInfusable;
import mcjty.lib.blockcommands.Command;
import mcjty.lib.blockcommands.ServerCommand;
import mcjty.lib.container.ContainerFactory;
import mcjty.lib.container.GenericContainer;
import mcjty.lib.container.GenericItemHandler;
import mcjty.lib.setup.Registration;
import mcjty.lib.tileentity.BaseBEData;
import mcjty.lib.tileentity.Cap;
import mcjty.lib.tileentity.CapType;
import mcjty.lib.tileentity.GenericEnergyStorage;
import mcjty.lib.typed.Key;
import mcjty.lib.typed.Type;
import mcjty.lib.varia.CustomTank;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.client.model.data.ModelData;
import net.neoforged.neoforge.common.util.Lazy;
import net.neoforged.neoforge.energy.IEnergyStorage;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.items.IItemHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

import static com.kjmaster.resonantia.setup.Registration.*;
import static mcjty.lib.setup.Registration.BASE_BE_DATA;

public abstract class ModularResonatingMachineTE extends ResonantiaTileEntity implements ISidedEnergyTE, ISidedItemTE, ISidedFluidTE, IResonantMachine {

    public final IFrequency frequencyHandler = getFrequencyHandler();
    @ResonantiaCap(type = ResonantiaCapTypes.FREQUENCY)
    private static final BiFunction<ModularResonatingMachineTE, Object, IFrequency> FREQUENCY_CAP = (tile, context) -> tile.frequencyHandler;

    public final DefaultInfusable infusableHandler = getInfusableHandler();
    @Cap(type = CapType.INFUSABLE)
    private static final Function<ModularResonatingMachineTE, IInfusable> INFUSABLE_CAP = (tile) -> tile.infusableHandler;

    public final GenericEnergyStorage energyStorage = getEnergyStorage();
    @ResonantiaCap(type = ResonantiaCapTypes.ENERGY)
    private static final BiFunction<ModularResonatingMachineTE, Object, IEnergyStorage> ENERGY_CAP =
            (tile, context) -> tile.getCapabilityHandler(ResonantMachineCapabilityType.ENERGY, (Direction) context);

    public final GenericItemHandler items = getItemHandler();
    @ResonantiaCap(type = ResonantiaCapTypes.ITEMS_AUTOMATION)
    private static final BiFunction<ModularResonatingMachineTE, Object, IItemHandler> ITEM_CAP =
            (tile, context) -> tile.getCapabilityHandler(ResonantMachineCapabilityType.ITEMS, (Direction) context);

    public final CustomTank fluidHandler = getFluidHandler();
    @ResonantiaCap(type = ResonantiaCapTypes.FLUIDS)
    private final static BiFunction<ModularResonatingMachineTE, Object, IFluidHandler> FLUID_CAP =
            (tile, context) -> tile.getCapabilityHandler(ResonantMachineCapabilityType.FLUIDS, (Direction) context);

    @Cap(type = CapType.CONTAINER)
    private static final Function<? extends ModularResonatingMachineTE, MenuProvider> SCREEN_CAP = ModularResonatingMachineTE::getContainerProvider;

    private final Map<ResonantMachineCapabilityType, ICapabilityModule<?, ?, ?>> capabilityModules = new EnumMap<>(ResonantMachineCapabilityType.class);

    public transient boolean wasEnabled = false;

    public ModularResonatingMachineTE(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);

        if (this.energyStorage != null) {
            EnergyModule energyModule = new EnergyModule(energyStorage, SIDED_ENERGY_MODE_DATA.get());
            energyModule.initializeSidedHandlers(this, this.energyStorage);
            this.capabilityModules.put(ResonantMachineCapabilityType.ENERGY, energyModule);
        }

        if (this.items != null) {
            ItemModule itemModule = new ItemModule(this.items, SIDED_ITEM_MODE_DATA.get());
            itemModule.initializeSidedHandlers(this, this.items);
            this.capabilityModules.put(ResonantMachineCapabilityType.ITEMS, itemModule);
        }

        if (this.fluidHandler != null) {
            FluidModule fluidModule = new FluidModule(this.fluidHandler, SIDED_FLUID_MODE_DATA.get());
            fluidModule.initializeSidedHandlers(this, this.fluidHandler);
            this.capabilityModules.put(ResonantMachineCapabilityType.FLUIDS, fluidModule);
        }
    }

    @SuppressWarnings("unchecked")
    private <H> H getCapabilityHandler(ResonantMachineCapabilityType capType, Direction facing) {
        ICapabilityModule<?, ?, ?> module = this.capabilityModules.get(capType);
        if (module != null && module.isActive()) {
            return (H) module.getSidedHandler(this, facing);
        }
        return null;
    }

    // Container Provider

    protected DefaultContainerProvider<IGenericContainer> getContainerProvider() {

        DefaultContainerProvider<IGenericContainer> defaultContainerProvider = new DefaultContainerProvider<>(getContainerTitle())
                .containerSupplier((windowId, player) -> new GenericContainer(getMenuType(), windowId, getContainerFactory(), this, player))
                .data(BASE_BE_DATA, BaseBEData.STREAM_CODEC, BaseBEData.CODEC)
                .setupSync(this);

        for (ICapabilityModule<?, ?, ?> module : this.capabilityModules.values()) {
            if (module.isActive()) {
                module.configureContainerProvider(defaultContainerProvider, this);
            }
        }

        return defaultContainerProvider;
    }

    protected abstract Lazy<ContainerFactory> getContainerFactory();

    protected abstract String getContainerTitle();

    protected abstract Supplier<MenuType<GenericContainer>> getMenuType();

    // Machine

    protected abstract void performMachineOperations(List<BlockPos> linkedMachines, boolean unstable);

    @Override
    public ResonantMachineRole getMachineRole() {
        return ResonantMachineRole.PASSIVE;
    }

    @Override
    protected boolean needsRedstoneMode() {
        return true;
    }

    // Tick

    @Override
    protected void tickServer() {
        markDirtyQuick();

        if (level == null) return;

        boolean isEnabled = isMachineEnabled();

        List<BlockPos> linkedMachines = List.of();

        if (isEnabled != wasEnabled) {
            if (isEnabled) {
                if (this instanceof ResonantEnergyTransmitterTileEntity) {
                    linkedMachines = findLinkedMachines(getBlockPos(), this.frequencyHandler.getFrequency(), this.frequencyHandler.getRadius(), this.frequencyHandler.getLinkTolerance());
                    ResonantiaMessages.sendToPlayersTrackingChunk(PacketLinkVisualization.create(getBlockPos(), linkedMachines), (ServerLevel) level, new ChunkPos(getBlockPos()));
                }
                ResonanceNetworkSavedData.get((ServerLevel) level).registerMachine(this.frequencyHandler);
            } else {
                if (this instanceof ResonantEnergyTransmitterTileEntity) {
                    ResonantiaMessages.sendToPlayersTrackingChunk(PacketLinkVisualization.create(getBlockPos(), linkedMachines), (ServerLevel) level, new ChunkPos(getBlockPos()));
                }
                ResonanceNetworkSavedData.get((ServerLevel) level).unregisterMachine(this.frequencyHandler);
                setUnstable(false);
            }
            ResonantiaMessages.sendToPlayersTrackingChunk(PacketSyncEnabled.create(getBlockPos(), isEnabled), (ServerLevel) level, new ChunkPos(getBlockPos()));
            wasEnabled = isEnabled; // Update tracker
        }

        if (!isEnabled) return;

        ServerLevel serverLevel = (ServerLevel) level;

        if (serverLevel.getGameTime() % this.frequencyHandler.getDriftInterval() == 0) {
            performDrift(serverLevel, this.frequencyHandler, getBlockPos());
        }

        if (linkedMachines.isEmpty()) {
            linkedMachines = findLinkedMachines(getBlockPos(), this.frequencyHandler.getFrequency(), this.frequencyHandler.getRadius(), this.frequencyHandler.getLinkTolerance());
        }

        boolean unstable = setUnstable(linkedMachines);

        if (unstable) {
            serverLevel.sendParticles(ParticleTypes.SMOKE, getBlockPos().getX() + 0.5, getBlockPos().getY() + 1.0, getBlockPos().getZ() + 0.5, 2, 0.2, 0.2, 0.2, 0.01);
        }

        if (getMachineRole() != ResonantMachineRole.PASSIVE) {
            performMachineOperations(linkedMachines, unstable);
        }
    }

    // Instability

    public boolean setUnstable(List<BlockPos> testList) {
        boolean unstable = ResonanceNetworkSavedData.get((ServerLevel) level).isMachineUnstable(testList, this.frequencyHandler);
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

    @Override
    public boolean isUnstable() {
        ResonantiaBEData data = this.getData(RESONANTIA_BE_DATA);
        return data.unstable();
    }

    // Drift

    private void performDrift(ServerLevel level, IFrequency frequencyCap, BlockPos pos) {
        int drift = frequencyCap.calculateDrift(new DriftContext(level, pos));
        if (drift != 0) {
            int newFrequency = frequencyCap.getFrequency() + drift;
            frequencyCap.setFrequency(newFrequency);
        }
    }

    // Linked Machines

    public List<BlockPos> findLinkedMachines(BlockPos origin, int frequency, int radius, int tolerance) {
        if (level == null || level.isClientSide()) return Collections.emptyList();
        Predicate<BlockPos> activeTest = p -> {
            if (!(level instanceof ServerLevel serverLevel)) return false;
            if (!level.hasChunkAt(p)) return false;
            BlockEntity be = serverLevel.getBlockEntity(p);
            return be instanceof ModularResonatingMachineTE te && te.isMachineEnabled() && getMachineRole().isCompatibleWith(te.getMachineRole());
        };
        return ResonanceNetworkSavedData.get((ServerLevel) level).findLinkedMachines((ServerLevel) level, origin, frequency, radius, tolerance, activeTest);
    }

    // Load / Unload

    @Override
    public void onLoad() {
        super.onLoad();
        if (level == null) return;
        if (!level.isClientSide) {
            wasEnabled = isMachineEnabled();
            ResonanceNetworkSavedData.get((ServerLevel) level).registerMachine(this.frequencyHandler);
            ResonantMachineIndex.add((ServerLevel) level, getBlockPos());
        }
    }

    @Override
    public void setRemoved() {
        super.setRemoved();
        if (level == null) return;
        if (!level.isClientSide) {
            ResonanceNetworkSavedData.get((ServerLevel) level).unregisterMachine(this.frequencyHandler);
            ResonantiaMessages.sendToPlayersTrackingChunk(PacketSyncEnabled.create(getBlockPos(), false), (ServerLevel) level, new ChunkPos(getBlockPos()));
            ResonantMachineIndex.remove((ServerLevel) level, getBlockPos());
        }
    }

    // --- Capability Mode Management ---
    public void setCapabilityMode(ResonantMachineCapabilityType capType, Mode mode, Direction side) {
        ICapabilityModule<?, ?, ?> module = this.capabilityModules.get(capType);
        if (module != null && module.isActive()) {
            module.setMode(this, mode, side);
        }
    }

    public Mode getCapabilityMode(ResonantMachineCapabilityType capType, Direction side) {
        ICapabilityModule<?, ?, ?> module = this.capabilityModules.get(capType);
        if (module != null && module.isActive()) {
            return module.getMode(this, side);
        }
        // Sensible defaults
        return Mode.MODE_NONE;
    }

    public void setEnergyMode(Mode mode, Direction side) {
        setCapabilityMode(ResonantMachineCapabilityType.ENERGY, mode, side);
    }

    @Override
    public Mode getEnergyMode(Direction side) {
        return getCapabilityMode(ResonantMachineCapabilityType.ENERGY, side);
    }

    public void setItemMode(Mode mode, Direction side) {
        setCapabilityMode(ResonantMachineCapabilityType.ITEMS, mode, side);
    }

    @Override
    public Mode getItemMode(Direction side) {
        return getCapabilityMode(ResonantMachineCapabilityType.ITEMS, side);
    }

    public void setFluidMode(Mode mode, Direction side) {
        setCapabilityMode(ResonantMachineCapabilityType.FLUIDS, mode, side);
    }

    @Override
    public Mode getFluidMode(Direction side) {
        return getCapabilityMode(ResonantMachineCapabilityType.FLUIDS, side);
    }


    // Data

    @Override
    @SuppressWarnings("unchecked")
    public void onDataChanged(AttachmentType<?> type, Object oldData, Object newData) {
        super.onDataChanged(type, oldData, newData);

        if (type == RESONANTIA_BE_DATA.get()) {
            onDataChanged((ResonantiaBEData) oldData, (ResonantiaBEData) newData);
        } else {
            for (ICapabilityModule<?, ?, ?> module : this.capabilityModules.values()) {
                if (module.isActive() && module.getSidedModeDataAttachmentType().equals(type)) {
                    // We need to cast newData and oldData to the module's specific SIDED_DATA type.
                    // This might require a helper method or careful casting within the module's onSidedModeDataChanged.
                    // For simplicity, assuming the module handles the cast or the interface is adjusted.
                    ((ICapabilityModule<Object, Object, Object>) module).onSidedModeDataChanged(oldData, newData, this);
                    return; // Assuming one module per attachment type
                }
            }
        }
    }

    private void onDataChanged(ResonantiaBEData oldData, ResonantiaBEData newData) {
        if (level == null || level.isClientSide()) return;

        if (oldData.unstable() != newData.unstable()) {
            ResonantiaMessages.sendToPlayersTrackingChunk(PacketUpdateResonantiaBEData.create(getBlockPos(), newData), (ServerLevel) level, new ChunkPos(getBlockPos()));
        }
    }

    @Override
    public void loadClientDataFromNBT(CompoundTag tagCompound, HolderLookup.Provider provider) {
        super.loadClientDataFromNBT(tagCompound, provider);
        for (ICapabilityModule<?, ?, ?> module : this.capabilityModules.values()) {
            if (module.isActive()) {
                module.loadClientData(tagCompound, provider, this);
            }
        }
    }

    @Override
    public void saveClientDataToNBT(CompoundTag tagCompound, HolderLookup.Provider provider) {
        super.saveClientDataToNBT(tagCompound, provider);
        for (ICapabilityModule<?, ?, ?> module : this.capabilityModules.values()) {
            if (module.isActive()) {
                module.saveClientData(tagCompound, provider, this);
            }
        }
    }

    @Override
    public @NotNull CompoundTag getUpdateTag(HolderLookup.Provider provider) {
        CompoundTag tag = super.getUpdateTag(provider);
        tag.putBoolean("unstable", getData(RESONANTIA_BE_DATA.get()).unstable());
        return tag;
    }

    @Override
    public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket pkt, HolderLookup.Provider provider) {

        Map<ResonantMachineCapabilityType, Object> oldDataMap = new EnumMap<>(ResonantMachineCapabilityType.class);

        for (ResonantMachineCapabilityType type : ResonantMachineCapabilityType.values()) {
            ICapabilityModule<?, ?, ?> module = this.capabilityModules.get(type);
            if (module != null && module.isActive()) {
                oldDataMap.put(type, getData(module.getSidedModeDataAttachmentType()));
            }
        }

        super.onDataPacket(net, pkt, provider);
        CompoundTag tag = pkt.getTag();

        for (ResonantMachineCapabilityType type : ResonantMachineCapabilityType.values()) {
            ICapabilityModule<?, ?, ?> module = this.capabilityModules.get(type);
            if (module != null && module.isActive()) {
                Object oldData = oldDataMap.get(type);
                module.onDataPacket(tag, this, oldData);
            }
        }
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider provider) {
        super.loadAdditional(tag, provider);
        frequencyHandler.load(tag, "frequency");
        infusableHandler.load(tag, "infusable");
        setData(RESONANTIA_BE_DATA, new ResonantiaBEData(tag.getBoolean("unstable")));

        for (ICapabilityModule<?, ?, ?> module : this.capabilityModules.values()) {
            if (module.isActive()) {
                module.loadAdditional(tag, provider, this);
            }
        }
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider provider) {
        super.saveAdditional(tag, provider);
        frequencyHandler.save(tag, "frequency");
        infusableHandler.save(tag, "infusable");

        for (ICapabilityModule<?, ?, ?> module : this.capabilityModules.values()) {
            if (module.isActive()) {
                module.saveAdditional(tag, provider, this);
            }
        }
    }

    @Override
    protected void applyImplicitComponents(DataComponentInput input) {
        super.applyImplicitComponents(input);

        ResonantiaBEData data = input.get(ITEM_RESONANTIA_BE_DATA);
        if (data != null) {
            this.setData(RESONANTIA_BE_DATA, data);
        }
        infusableHandler.applyImplicitComponents(input.get(Registration.ITEM_INFUSABLE));
        frequencyHandler.applyImplicitComponents(input.get(ITEM_FREQUENCY));

        for (ICapabilityModule<?, ?, ?> module : this.capabilityModules.values()) {
            if (module.isActive()) {
                module.applyImplicitComponents(input, this);
            }
        }
    }

    @Override
    protected void collectImplicitComponents(DataComponentMap.Builder components) {
        super.collectImplicitComponents(components);

        components.set(ITEM_RESONANTIA_BE_DATA, this.getData(RESONANTIA_BE_DATA));
        infusableHandler.collectImplicitComponents(components);
        frequencyHandler.collectImplicitComponents(components);

        for (ICapabilityModule<?, ?, ?> module : this.capabilityModules.values()) {
            if (module.isActive()) {
                module.collectImplicitComponents(components, this);
            }
        }
    }

    // Server Commands

    public static final Key<Integer> PARAM_ITEM_MODE = new Key<>("item_mode", Type.INTEGER);
    public static final Key<Integer> PARAM_ENERGY_MODE = new Key<>("energy_mode", Type.INTEGER);
    public static final Key<Integer> PARAM_FLUID_MODE = new Key<>("fluid_mode", Type.INTEGER);
    public static final Key<Integer> PARAM_DIRECTION = new Key<>("direction", Type.INTEGER);

    @ServerCommand
    public static final Command<?> ACTION_SET_ENERGY_MODE = Command.<ModularResonatingMachineTE>create("setEnergyMode",
            (te, player, params) ->
                    te.setEnergyMode(Mode.values()[params.get(PARAM_ENERGY_MODE)], Direction.values()[params.get(PARAM_DIRECTION)]));
    @ServerCommand
    public static final Command<?> ACTION_SET_ITEM_MODE = Command.<ModularResonatingMachineTE>create("setItemMode",
            (te, player, params) ->
                    te.setItemMode(Mode.values()[params.get(PARAM_ITEM_MODE)], Direction.values()[params.get(PARAM_DIRECTION)]));
    @ServerCommand
    public static final Command<?> ACTION_SET_FLUID_MODE = Command.<ModularResonatingMachineTE>create("setFluidMode",
            (te, player, params) ->
                    te.setFluidMode(Mode.values()[params.get(PARAM_FLUID_MODE)], Direction.values()[params.get(PARAM_DIRECTION)]));
    // Model Data

    @Override
    public @NotNull ModelData getModelData() {
        ModelData.Builder builder = ModelData.builder();

        for (ICapabilityModule<?, ?, ?> module : this.capabilityModules.values()) {
            if (module.isActive()) {
                module.populateModelData(builder, this);
            }
        }

        return builder.build();
    }

    // Should Be Overridden Based On Caps
    @Nullable
    protected GenericEnergyStorage getEnergyStorage() {
        return null;
    }

    @Nullable
    protected GenericItemHandler getItemHandler() {
        return null;
    }

    @Nullable
    protected CustomTank getFluidHandler() {
        return null;
    }

    @NotNull
    protected IFrequency getFrequencyHandler() {
        return new DefaultFrequency(this);
    }

    @NotNull
    protected DefaultInfusable getInfusableHandler() {
        return new DefaultInfusable(this);
    }

    // Override if te has Gui
    @Nullable
    public ResourceLocation getGui() {
        return null;
    }
}
