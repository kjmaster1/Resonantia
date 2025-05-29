package com.kjmaster.resonantia.modules.resonantenergy.blocks.transmitter;

import com.kjmaster.resonantia.modules.resonantenergy.ResonantEnergyModule;
import mcjty.lib.blocks.BaseBlock;
import mcjty.lib.blocks.RotationType;
import mcjty.lib.builder.BlockBuilder;
import mcjty.lib.gui.ManualEntry;
import mcjty.lib.tileentity.GenericEnergyStorage;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

import static com.kjmaster.resonantia.modules.resonantenergy.ResonantEnergyModule.SLAB;
import static mcjty.lib.builder.TooltipBuilder.*;

public class SimpleResonantEnergyTransmitterTE extends ResonantEnergyTransmitterTileEntity {

    public SimpleResonantEnergyTransmitterTE(BlockPos pos, BlockState state) {
        super(ResonantEnergyModule.SIMPLE_RESONANT_ENERGY_TRANSMITTER.be().get(), pos, state);
    }

    @Override
    protected GenericEnergyStorage getEnergyStorage() {
        return new GenericEnergyStorage(this, true, 8000, 500);
    }

    public static BaseBlock createBlock() {
        return new BaseBlock(new BlockBuilder()
                .tileEntitySupplier(SimpleResonantEnergyTransmitterTE::new)
                .infusable()
                .manualEntry(ManualEntry.EMPTY)
                .info(key("message.simple_resonant_energy_transmitter.shiftmessage"))
                .infoShift(header(), gold())) {

            @Override
            public RotationType getRotationType() {
                return RotationType.NONE;
            }

            @Override
            protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
                return SLAB;
            }
        };
    }
}
