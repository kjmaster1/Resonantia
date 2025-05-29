package com.kjmaster.resonantia.modules.crusher.blocks;

import com.kjmaster.resonantia.blocks.ProcessingMachineBlock;
import com.kjmaster.resonantia.modules.crusher.CrusherModule;
import mcjty.lib.blocks.BaseBlock;
import mcjty.lib.builder.BlockBuilder;
import mcjty.lib.gui.ManualEntry;
import mcjty.lib.tileentity.GenericEnergyStorage;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;

import static mcjty.lib.builder.TooltipBuilder.*;

public class SimpleResonantCrusherTE extends ResonantCrusherTE {

    public SimpleResonantCrusherTE(BlockPos pos, BlockState state) {
        super(CrusherModule.SIMPLE_RESONANT_CRUSHER.be().get(), pos, state, 20);
    }

    @Override
    protected GenericEnergyStorage getEnergyStorage() {
        return new GenericEnergyStorage(this, true, 8000, 500);
    }

    public static BaseBlock createBlock() {
        return new ProcessingMachineBlock(new BlockBuilder()
                .properties(BlockBehaviour.Properties.of()
                        .noOcclusion()
                )
                .tileEntitySupplier(SimpleResonantCrusherTE::new)
                // .topDriver(RFToolsPowerTOPDriver.DRIVER)
                .infusable()
                .manualEntry(ManualEntry.EMPTY)
                .info(key("message.rftoolspower.shiftmessage"))
                .infoShift(header(), gold(),
                        parameter("info", stack -> "Uses " + 20 + " RF/FE per tick"))
        );
    }
}
