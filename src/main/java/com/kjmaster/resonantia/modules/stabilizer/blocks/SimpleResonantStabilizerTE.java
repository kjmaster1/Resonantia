package com.kjmaster.resonantia.modules.stabilizer.blocks;

import com.kjmaster.resonantia.blocks.ProcessingMachineBlock;
import com.kjmaster.resonantia.modules.stabilizer.StabilizerModule;
import mcjty.lib.blocks.BaseBlock;
import mcjty.lib.builder.BlockBuilder;
import mcjty.lib.gui.ManualEntry;
import mcjty.lib.tileentity.GenericEnergyStorage;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import static mcjty.lib.builder.TooltipBuilder.*;
import static mcjty.lib.builder.TooltipBuilder.parameter;

public class SimpleResonantStabilizerTE extends ResonantStabilizerTE {

    public SimpleResonantStabilizerTE(BlockPos pos, BlockState state) {
        super(StabilizerModule.SIMPLE_RESONANT_STABILIZER.be().get(), pos, state, 100);
    }

    @Override
    public float getStabilizationFactor() {
        return 1f;
    }

    @Override
    protected @Nullable GenericEnergyStorage getEnergyStorage() {
        return new GenericEnergyStorage(this, true, 8000, 500);
    }

    public static BaseBlock createBlock() {
        return new ProcessingMachineBlock(new BlockBuilder()
                .properties(BlockBehaviour.Properties.of()
                        .noOcclusion()
                )
                .tileEntitySupplier(SimpleResonantStabilizerTE::new)
                // .topDriver(RFToolsPowerTOPDriver.DRIVER)
                .infusable()
                .manualEntry(ManualEntry.EMPTY)
                .info(key("message.rftoolspower.shiftmessage"))
                .infoShift(header(), gold(),
                        parameter("info", stack -> "Uses " + 100 + " RF/FE per tick"))
        );
    }
}
