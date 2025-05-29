package com.kjmaster.resonantia.blocks;

import mcjty.lib.blocks.BaseBlock;
import mcjty.lib.blocks.RotationType;
import mcjty.lib.builder.BlockBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;

public class ProcessingMachineBlock extends BaseBlock {

    public static final DirectionProperty FACING = BlockStateProperties.FACING;
    public static final BooleanProperty LIT = BlockStateProperties.LIT;

    public ProcessingMachineBlock(BlockBuilder builder) {
        super(builder);
        this.registerDefaultState(this.stateDefinition.any()
                .setValue(FACING, Direction.NORTH)
                .setValue(LIT, Boolean.valueOf(false))
        );
    }

    @Override
    protected @NotNull VoxelShape getOcclusionShape(BlockState state, BlockGetter level, BlockPos pos) {
        return Block.box(1, 1, 1, 15, 15, 2);
    }

    @Override
    protected void createBlockStateDefinition(@NotNull StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(BlockStateProperties.LIT);
    }

    @Override
    public RotationType getRotationType() {
        return RotationType.ROTATION;
    }
}
