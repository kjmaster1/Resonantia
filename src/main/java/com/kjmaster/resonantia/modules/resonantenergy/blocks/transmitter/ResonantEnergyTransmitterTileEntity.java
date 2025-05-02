package com.kjmaster.resonantia.modules.resonantenergy.blocks.transmitter;

import com.kjmaster.resonantia.api.machine.IResonantMachine;
import com.kjmaster.resonantia.api.machine.ResonantMachineRole;
import com.kjmaster.resonantia.modules.resonantenergy.blocks.receiver.ResonantEnergyReceiverTileEntity;
import com.kjmaster.resonantia.modules.resonantenergy.data.TransmitterIndex;
import com.kjmaster.resonantia.resonance.PacketLinkVisualization;
import com.kjmaster.resonantia.setup.ResonantiaMessages;
import com.kjmaster.resonantia.tileentity.ResonatingMachineTE;
import mcjty.lib.api.container.DefaultContainerProvider;
import mcjty.lib.container.ContainerFactory;
import mcjty.lib.container.GenericContainer;
import mcjty.lib.container.GenericItemHandler;
import mcjty.lib.container.SlotDefinition;
import mcjty.lib.setup.Registration;
import mcjty.lib.tileentity.BaseBEData;
import mcjty.lib.tileentity.Cap;
import mcjty.lib.tileentity.CapType;
import mcjty.lib.varia.EnergyTools;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.common.util.Lazy;
import net.neoforged.neoforge.energy.IEnergyStorage;

import java.util.List;
import java.util.function.Function;

import static com.kjmaster.resonantia.modules.resonantenergy.ResonantEnergyModule.CONTAINER_RESONANT_ENERGY_TRANSMITTER;
import static mcjty.lib.api.container.DefaultContainerProvider.container;
import static mcjty.lib.setup.Registration.BASE_BE_DATA;

public class ResonantEnergyTransmitterTileEntity extends ResonatingMachineTE {

    public static final int SLOT_CHARGEITEM = 0;

    public static final Lazy<ContainerFactory> CONTAINER_FACTORY = Lazy.of(() -> new ContainerFactory(1)
            .slot(SlotDefinition.specific(EnergyTools::isEnergyItem), SLOT_CHARGEITEM, 82, 24)
            .playerSlots(10, 70));

    private final GenericItemHandler items = GenericItemHandler.create(this, CONTAINER_FACTORY).itemValid(
                    (slot, stack) -> EnergyTools.isEnergyItem(stack))
            .build();

    @Cap(type = CapType.ITEMS_AUTOMATION)
    private static final Function<ResonantEnergyTransmitterTileEntity, GenericItemHandler> ITEM_CAP = tile -> tile.items;

    @Cap(type = CapType.CONTAINER)
    private static final Function<ResonantEnergyTransmitterTileEntity, MenuProvider> SCREEN_CAP = be -> new DefaultContainerProvider<GenericContainer>("Resonant Energy Transmitter")
            .containerSupplier(container(CONTAINER_RESONANT_ENERGY_TRANSMITTER, CONTAINER_FACTORY, be))
            .itemHandler(() -> be.items)
            .energyHandler(() -> be.energyStorage)
            .data(BASE_BE_DATA, BaseBEData.STREAM_CODEC, BaseBEData.CODEC)
            .setupSync(be);

    private List<BlockPos> lastSentLinks = List.of();

    public ResonantEnergyTransmitterTileEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Override
    public ResonantMachineRole getMachineRole() {
        return ResonantMachineRole.TRANSMITTER;
    }

    @Override
    public void performMachineOperations(List<BlockPos> linkedMachines, boolean unstable) {
        if (level == null || level.isClientSide) return;

        if (!linkedMachines.equals(lastSentLinks)) {
            ResonantiaMessages.sendToPlayersTrackingChunk(PacketLinkVisualization.create(getBlockPos(), linkedMachines), (ServerLevel) level, new ChunkPos(getBlockPos()));
            lastSentLinks = List.copyOf(linkedMachines);
        }

        if (energyStorage.getEnergyStored() == 0) return;

        int availableEnergy = energyStorage.getEnergyStored();

        List<IEnergyStorage> validTargets = linkedMachines.stream()
                .map(level::getBlockEntity)
                .filter((blockEntity) -> {
                    if (blockEntity instanceof IResonantMachine resonantMachine) {
                        return resonantMachine.getMachineRole() != ResonantMachineRole.TRANSMITTER;
                    }
                    return false;
                })
                .map(be -> {
                    if (be instanceof ResonantEnergyReceiverTileEntity receiver) {
                        return receiver.internalEnergyStorage;
                    }
                    return level.getCapability(Capabilities.EnergyStorage.BLOCK, be.getBlockPos(), null);
                })
                .filter(target -> target != null && target.canReceive())
                .toList();

        if (validTargets.isEmpty()) return;

        int perTarget = Math.min(500, availableEnergy / validTargets.size());

        for (IEnergyStorage target : validTargets) {
            int accepted = target.receiveEnergy(perTarget, false);
            energyStorage.consumeEnergy(accepted);
            availableEnergy -= accepted;
            if (availableEnergy <= 0) break;
        }
    }

    @Override
    public void onLoad() {
        super.onLoad();
        if (level == null) return;
        if (!level.isClientSide) {
            TransmitterIndex.add((ServerLevel) level, worldPosition);
        }
    }

    @Override
    public void setRemoved() {
        super.setRemoved();
        if (level == null) return;
        if (!level.isClientSide) {
            TransmitterIndex.remove((ServerLevel) level, worldPosition);
        }
    }

    @Override
    protected void applyImplicitComponents(DataComponentInput input) {
        super.applyImplicitComponents(input);
        items.applyImplicitComponents(input.get(Registration.ITEM_INVENTORY));
    }

    @Override
    protected void collectImplicitComponents(DataComponentMap.Builder builder) {
        super.collectImplicitComponents(builder);
        items.collectImplicitComponents(builder);
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider provider) {
        super.saveAdditional(tag, provider);
        items.save(tag, "items", provider);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider provider) {
        super.loadAdditional(tag, provider);
        items.load(tag, "items", provider);
    }
}
