package com.kjmaster.resonantia.tileentity;

import mcjty.lib.tileentity.GenericTileEntity;
import mcjty.lib.tileentity.TickingTileEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class ResonantiaTileEntity extends TickingTileEntity {

    public ResonantiaTileEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
        ResonantiaAnnotationHolder holder = this.getResonantiaAnnotationHolder();
        if (holder == null) {
            ResonantiaAnnotationTools.createAnnotationHolder(this.getClass(), null);
        }
    }

    private ResonantiaAnnotationHolder getResonantiaAnnotationHolder() {
        return ResonantiaAnnotationHolder.annotations.get(this.getClass());
    }
}
