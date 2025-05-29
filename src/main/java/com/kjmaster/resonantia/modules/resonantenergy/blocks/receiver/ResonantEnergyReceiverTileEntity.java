package com.kjmaster.resonantia.modules.resonantenergy.blocks.receiver;

import com.kjmaster.resonantia.api.machine.ResonantMachineRole;
import com.kjmaster.resonantia.data.Mode;
import com.kjmaster.resonantia.modules.resonantenergy.ResonantEnergyModule;
import com.kjmaster.resonantia.tileentity.ModularResonatingMachineTE;
import mcjty.lib.container.ContainerFactory;
import mcjty.lib.container.GenericContainer;
import mcjty.lib.container.GenericItemHandler;
import mcjty.lib.container.SlotDefinition;
import mcjty.lib.tileentity.GenericEnergyStorage;
import mcjty.lib.varia.EnergyTools;
import mcjty.lib.varia.OrientationTools;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.common.util.Lazy;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Supplier;

import static com.kjmaster.resonantia.Resonantia.MODID;
import static mcjty.lib.varia.EnergyTools.isEnergyTE;
import static mcjty.lib.varia.EnergyTools.receiveEnergy;

public abstract class ResonantEnergyReceiverTileEntity extends ModularResonatingMachineTE {

    public static final int SLOT_CHARGEITEM = 0;

    public static final Lazy<ContainerFactory> CONTAINER_FACTORY = Lazy.of(() -> new ContainerFactory(1)
            .slot(SlotDefinition.specific(EnergyTools::isEnergyItem), SLOT_CHARGEITEM, 82, 24)
            .playerSlots(10, 70));

    public final GenericEnergyStorage internalEnergyStorage;

    public ResonantEnergyReceiverTileEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
        this.internalEnergyStorage = getInternalEnergyStorage();
    }

    @Override
    protected @Nullable GenericItemHandler getItemHandler() {
        return GenericItemHandler.create(this, getContainerFactory()).build();
    }

    @Override
    protected Lazy<ContainerFactory> getContainerFactory() {
        return CONTAINER_FACTORY;
    }

    @Override
    protected Supplier<MenuType<GenericContainer>> getMenuType() {
        return ResonantEnergyModule.CONTAINER_RESONANT_ENERGY_RECEIVER;
    }

    @Override
    protected String getContainerTitle() {
        return "Resonant Energy Receiver";
    }

    protected abstract GenericEnergyStorage getInternalEnergyStorage();

    @Override
    public ResourceLocation getGui() {
        return ResourceLocation.fromNamespaceAndPath(MODID, "textures/gui/resonant_energy_gui.png");
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
        if (unstable) {
            applyInstabilityEffects();
        }
        handleSendingEnergy();
    }

    private void applyInstabilityEffects() {
        long stored = internalEnergyStorage.getEnergy();
        if (stored <= 0) return;

        int lost = (int) Math.min(stored, (stored * 0.25));
        internalEnergyStorage.consumeEnergy(lost);
    }

    private void handleSendingEnergy() {
        long storedPower = internalEnergyStorage.getEnergy();
        handleSendingEnergy(level, worldPosition, storedPower, 500, internalEnergyStorage);
    }

    public void handleSendingEnergy(Level world, BlockPos pos, long storedPower, long sendPerTick, GenericEnergyStorage storage) {
        for(Direction facing : OrientationTools.DIRECTION_VALUES) {
            Mode mode = getEnergyMode(facing);
            if (mode != Mode.MODE_BOTH && mode != Mode.MODE_OUTPUT) continue;
            BlockPos p = pos.relative(facing);
            BlockEntity te = world.getBlockEntity(p);
            Direction opposite = facing.getOpposite();
            if (isEnergyTE(te, opposite)) {
                long rfToGive = Math.min(sendPerTick, storedPower);
                long received = receiveEnergy(te, opposite, rfToGive);
                storage.consumeEnergy(received);
                storedPower -= received;
                if (storedPower <= 0L) {
                    break;
                }
            }
        }

    }
}
