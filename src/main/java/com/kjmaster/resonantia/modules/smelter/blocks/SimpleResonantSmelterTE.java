package com.kjmaster.resonantia.modules.smelter.blocks;

import com.kjmaster.resonantia.blocks.ProcessingMachineBlock;
import com.kjmaster.resonantia.modules.smelter.SmelterModule;
import mcjty.lib.blocks.BaseBlock;
import mcjty.lib.builder.BlockBuilder;
import mcjty.lib.gui.ManualEntry;
import mcjty.lib.tileentity.GenericEnergyStorage;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

import static mcjty.lib.builder.TooltipBuilder.*;

public class SimpleResonantSmelterTE extends ResonantSmelterTE {

    public SimpleResonantSmelterTE(BlockPos pos, BlockState state) {
        super(SmelterModule.SIMPLE_RESONANT_SMELTER.be().get(), pos, state, 20, 200);
    }

    @Override
    protected GenericEnergyStorage getEnergyStorage() {
        return new GenericEnergyStorage(this, true, 8000, 500);
    }

    public static BaseBlock createBlock() {
        return new ProcessingMachineBlock(new BlockBuilder()
                .tileEntitySupplier(SimpleResonantSmelterTE::new)
                // .topDriver(RFToolsPowerTOPDriver.DRIVER)
                .infusable()
                .manualEntry(ManualEntry.EMPTY)
                .info(key("message.rftoolspower.shiftmessage"))
                .infoShift(header(), gold(),
                        parameter("info", stack -> "Uses " + 20 + " RF/FE per tick"))
        );
    }
}
