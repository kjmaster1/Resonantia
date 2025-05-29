package com.kjmaster.resonantia.modules.pump.blocks;

import com.kjmaster.resonantia.blocks.ProcessingMachineBlock;
import com.kjmaster.resonantia.modules.pump.PumpModule;
import mcjty.lib.blocks.BaseBlock;
import mcjty.lib.builder.BlockBuilder;
import mcjty.lib.gui.ManualEntry;
import mcjty.lib.tileentity.GenericEnergyStorage;
import mcjty.lib.varia.CustomTank;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.fluids.FluidType;
import org.jetbrains.annotations.Nullable;

import static mcjty.lib.builder.TooltipBuilder.*;

public class SimpleResonantPumpTE extends ResonantPumpTE {

    public SimpleResonantPumpTE(BlockPos pos, BlockState state) {
        super(PumpModule.SIMPLE_RESONANT_PUMP.be().get(), pos, state, 60, 19, 256);
    }

    @Override
    protected @Nullable CustomTank getFluidHandler() {
        return new CustomTank(FluidType.BUCKET_VOLUME * 10);
    }

    @Override
    protected GenericEnergyStorage getEnergyStorage() {
        return new GenericEnergyStorage(this, true, 8000, 500);
    }

    public static BaseBlock createBlock() {
        return new ProcessingMachineBlock(new BlockBuilder()
                .tileEntitySupplier(SimpleResonantPumpTE::new)
                // .topDriver(RFToolsPowerTOPDriver.DRIVER)
                .infusable()
                .manualEntry(ManualEntry.EMPTY)
                .info(key("message.rftoolspower.shiftmessage"))
                .infoShift(header(), gold(),
                        parameter("info", stack -> "Uses " + 60 + " RF/FE per tick"))
        );
    }
}
