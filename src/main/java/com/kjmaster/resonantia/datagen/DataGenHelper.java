package com.kjmaster.resonantia.datagen;

import mcjty.lib.datagen.BaseBlockStateProvider;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.neoforged.neoforge.client.model.generators.BlockModelBuilder;
import net.neoforged.neoforge.client.model.generators.MultiPartBlockStateBuilder;

public class DataGenHelper {



    public static void createProcessingMachineModel(BaseBlockStateProvider provider, Block block, String name, String suffix) {

        BlockModelBuilder face = provider.models().getBuilder("block/" + name + "/face_" + suffix);
        face.parent(provider.models().getExistingFile(provider.modLoc("block/machine/processing_machine_face")));
        face.texture("front", provider.modLoc("block/" + name + "/" + name + "_" + suffix + "_front"));
        face.texture("back", provider.modLoc("block/" + name + "/" + name + "_" + suffix + "_back"));

        BlockModelBuilder face_glow = provider.models().getBuilder("block/" + name + "/face_glow_" + suffix);
        face_glow.parent(provider.models().getExistingFile(provider.modLoc("block/machine/processing_machine_face_glow")));
        face_glow.texture("front", provider.modLoc("block/" + name + "/" + name + "_" + suffix + "_front"));
        face_glow.texture("front_glow", provider.modLoc("block/" + name + "/" + name + "_front_glow"));
        face_glow.texture("back", provider.modLoc("block/" + name + "/" + name + "_" + suffix + "_back"));

        BlockModelBuilder frame = provider.models().getBuilder("block/" + name + "/frame");
        frame.parent(provider.models().getExistingFile(provider.modLoc("block/machine/processing_machine_frame")));
        frame.texture("frame", provider.modLoc("block/machine/windows"));
        frame.texture("particle", provider.modLoc("block/" + name + "/" + name + "_" + suffix + "_front"));

        BlockModelBuilder windows = provider.models().getBuilder("block/" + name + "/windows");
        windows.parent(provider.models().getExistingFile(provider.modLoc("block/machine/processing_machine_windows")));
        windows.texture("window", provider.modLoc("block/machine/windows"));

        MultiPartBlockStateBuilder bld = provider.getMultipartBuilder(block);

        bld.part().modelFile(frame).addModel();

        bld.part().modelFile(windows).addModel().condition(BlockStateProperties.FACING, Direction.NORTH);
        bld.part().modelFile(face).addModel().condition(BlockStateProperties.FACING, Direction.NORTH).condition(BlockStateProperties.LIT, Boolean.FALSE);
        bld.part().modelFile(face_glow).addModel().condition(BlockStateProperties.FACING, Direction.NORTH).condition(BlockStateProperties.LIT, Boolean.TRUE);

        bld.part().modelFile(windows).rotationY(180).addModel().condition(BlockStateProperties.FACING, Direction.SOUTH);
        bld.part().modelFile(face).rotationY(180).addModel().condition(BlockStateProperties.FACING, Direction.SOUTH).condition(BlockStateProperties.LIT, Boolean.FALSE);
        bld.part().modelFile(face_glow).rotationY(180).addModel().condition(BlockStateProperties.FACING, Direction.SOUTH).condition(BlockStateProperties.LIT, Boolean.TRUE);

        bld.part().modelFile(windows).rotationY(270).addModel().condition(BlockStateProperties.FACING, Direction.WEST);
        bld.part().modelFile(face).rotationY(270).addModel().condition(BlockStateProperties.FACING, Direction.WEST).condition(BlockStateProperties.LIT, Boolean.FALSE);
        bld.part().modelFile(face_glow).rotationY(270).addModel().condition(BlockStateProperties.FACING, Direction.WEST).condition(BlockStateProperties.LIT, Boolean.TRUE);

        bld.part().modelFile(windows).rotationY(90).addModel().condition(BlockStateProperties.FACING, Direction.EAST);
        bld.part().modelFile(face).rotationY(90).addModel().condition(BlockStateProperties.FACING, Direction.EAST).condition(BlockStateProperties.LIT, Boolean.FALSE);
        bld.part().modelFile(face_glow).rotationY(90).addModel().condition(BlockStateProperties.FACING, Direction.EAST).condition(BlockStateProperties.LIT, Boolean.TRUE);

        bld.part().modelFile(windows).rotationX(-90).addModel().condition(BlockStateProperties.FACING, Direction.UP);
        bld.part().modelFile(face).rotationX(-90).addModel().condition(BlockStateProperties.FACING, Direction.UP).condition(BlockStateProperties.LIT, Boolean.FALSE);
        bld.part().modelFile(face_glow).rotationX(-90).addModel().condition(BlockStateProperties.FACING, Direction.UP).condition(BlockStateProperties.LIT, Boolean.TRUE);

        bld.part().modelFile(windows).rotationX(90).addModel().condition(BlockStateProperties.FACING, Direction.DOWN);
        bld.part().modelFile(face).rotationX(90).addModel().condition(BlockStateProperties.FACING, Direction.DOWN).condition(BlockStateProperties.LIT, Boolean.FALSE);
        bld.part().modelFile(face_glow).rotationX(90).addModel().condition(BlockStateProperties.FACING, Direction.DOWN).condition(BlockStateProperties.LIT, Boolean.TRUE);
    }
}
