package com.kjmaster.resonantia.modules.resonantenergy.blocks.transmitter;

import com.kjmaster.resonantia.api.machine.IResonantMachine;
import com.kjmaster.resonantia.api.machine.ResonantMachineRole;
import com.kjmaster.resonantia.modules.resonantenergy.blocks.receiver.ResonantEnergyReceiverTileEntity;
import com.kjmaster.resonantia.resonance.PacketLinkVisualization;
import com.kjmaster.resonantia.setup.ResonantiaMessages;
import com.kjmaster.resonantia.tileentity.ILitOverride;
import com.kjmaster.resonantia.tileentity.ModularResonatingMachineTE;
import mcjty.lib.container.ContainerFactory;
import mcjty.lib.container.GenericContainer;
import mcjty.lib.container.GenericItemHandler;
import mcjty.lib.container.SlotDefinition;
import mcjty.lib.varia.EnergyTools;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.common.util.Lazy;
import net.neoforged.neoforge.energy.IEnergyStorage;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Supplier;

import static com.kjmaster.resonantia.Resonantia.MODID;
import static com.kjmaster.resonantia.modules.resonantenergy.ResonantEnergyModule.CONTAINER_RESONANT_ENERGY_TRANSMITTER;

public abstract class ResonantEnergyTransmitterTileEntity extends ModularResonatingMachineTE {

    public static final int SLOT_CHARGEITEM = 0;

    public static final Lazy<ContainerFactory> CONTAINER_FACTORY = Lazy.of(() -> new ContainerFactory(1)
            .slot(SlotDefinition.specific(EnergyTools::isEnergyItem), SLOT_CHARGEITEM, 82, 24)
            .playerSlots(10, 70));

    private List<BlockPos> lastSentLinks = List.of();

    public ResonantEnergyTransmitterTileEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
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
    protected String getContainerTitle() {
        return "Resonant Energy Transmitter";
    }

    @Override
    protected Supplier<MenuType<GenericContainer>> getMenuType() {
        return CONTAINER_RESONANT_ENERGY_TRANSMITTER;
    }

    @Override
    public ResourceLocation getGui() {
        return ResourceLocation.fromNamespaceAndPath(MODID, "textures/gui/resonant_energy_gui.png");
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

        if (unstable) {
            applyInstabilityEffects(linkedMachines);
        }

        transmitEnergy(linkedMachines);
    }

    private void transmitEnergy(List<BlockPos> linkedMachines) {
        int availableEnergy = energyStorage.getEnergyStored();

        List<IEnergyStorage> validTargets = linkedMachines.stream()
                .map(level::getBlockEntity)
                .filter((blockEntity) -> {
                    if (blockEntity instanceof IResonantMachine resonantMachine) {
                        return resonantMachine.getMachineRole().isCompatibleWith(getMachineRole());
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

    private void applyInstabilityEffects(List<BlockPos> linkedMachines) {
        int stored = energyStorage.getEnergyStored();
        if (stored <= 0) return;

        int lost = Math.min(stored, (int) (stored * 0.15));
        energyStorage.consumeEnergy(lost);

        if (level != null && level.random.nextFloat() < 0.1f) {
            BlockPos pos = linkedMachines.isEmpty() ? worldPosition : linkedMachines.get(level.random.nextInt(linkedMachines.size()));
            BlockEntity be = level.getBlockEntity(pos);
            if (be instanceof ILitOverride litOverride) {
                litOverride.applyLitOverride(10);
            }
            ((ServerLevel) level).sendParticles(ParticleTypes.CRIT, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, 6, 0.3, 0.3, 0.3, 0.01);
        }
    }
}
