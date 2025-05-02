package com.kjmaster.resonantia.modules.resonantenergy.blocks.receiver;

import com.kjmaster.resonantia.api.machine.ResonantMachineRole;
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
import mcjty.lib.tileentity.GenericEnergyStorage;
import mcjty.lib.varia.EnergyTools;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.common.util.Lazy;

import java.util.List;
import java.util.function.Function;

import static com.kjmaster.resonantia.modules.resonantenergy.ResonantEnergyModule.CONTAINER_RESONANT_ENERGY_TRANSMITTER;
import static mcjty.lib.api.container.DefaultContainerProvider.container;
import static mcjty.lib.setup.Registration.BASE_BE_DATA;

public class ResonantEnergyReceiverTileEntity extends ResonatingMachineTE {

    public static final int SLOT_CHARGEITEM = 0;

    public static final Lazy<ContainerFactory> CONTAINER_FACTORY = Lazy.of(() -> new ContainerFactory(1)
            .slot(SlotDefinition.specific(EnergyTools::isEnergyItem), SLOT_CHARGEITEM, 82, 24)
            .playerSlots(10, 70));

    private final GenericItemHandler items = GenericItemHandler.create(this, CONTAINER_FACTORY).itemValid(
                    (slot, stack) -> EnergyTools.isEnergyItem(stack))
            .build();

    @Cap(type = CapType.ITEMS_AUTOMATION)
    private static final Function<ResonantEnergyReceiverTileEntity, GenericItemHandler> ITEM_CAP = tile -> tile.items;

    @Cap(type = CapType.CONTAINER)
    private static final Function<ResonantEnergyReceiverTileEntity, MenuProvider> SCREEN_CAP = be -> new DefaultContainerProvider<GenericContainer>("Resonant Energy Receiver")
            .containerSupplier(container(CONTAINER_RESONANT_ENERGY_TRANSMITTER, CONTAINER_FACTORY, be))
            .itemHandler(() -> be.items)
            .energyHandler(() -> be.energyStorage)
            .data(BASE_BE_DATA, BaseBEData.STREAM_CODEC, BaseBEData.CODEC)
            .setupSync(be);

    public GenericEnergyStorage internalEnergyStorage;

    public ResonantEnergyReceiverTileEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Override
    public ResonantMachineRole getMachineRole() {
        return ResonantMachineRole.RECEIVER;
    }

    @Override
    public void performMachineOperations(List<BlockPos> linkedMachines, boolean unstable) {
        if (energyStorage.getEnergy() != internalEnergyStorage.getEnergy()) {
            energyStorage.setEnergy(internalEnergyStorage.getEnergy());
        }
        handleSendingEnergy();
    }

    private void handleSendingEnergy() {
        long storedPower = internalEnergyStorage.getEnergy();
        EnergyTools.handleSendingEnergy(level, worldPosition, storedPower, 500, internalEnergyStorage);
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
