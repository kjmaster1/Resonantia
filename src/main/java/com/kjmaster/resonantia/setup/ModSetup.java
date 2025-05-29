package com.kjmaster.resonantia.setup;

import com.kjmaster.resonantia.blocks.ResonantiaBlockRegistry;
import com.kjmaster.resonantia.tileentity.ResonantiaAnnotationHolder;
import com.kjmaster.resonantia.tileentity.ResonantiaTileEntity;
import mcjty.lib.setup.DefaultModSetup;
import mcjty.lib.tileentity.AnnotationHolder;
import mcjty.lib.tileentity.GenericTileEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.capabilities.BlockCapability;
import net.neoforged.neoforge.capabilities.IBlockCapabilityProvider;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;

public class ModSetup extends DefaultModSetup {

    @Override
    public void init(FMLCommonSetupEvent e) {
        super.init(e);
    }

    @Override
    protected void setupModCompat() {

    }

    public Consumer<RegisterCapabilitiesEvent> getBlockCapabilityRegistrar(ResonantiaBlockRegistry registry) {
        return (event) -> {

            for (AnnotationHolder holder : registry.getHolders()) {
                for (int i = 0; i < holder.getCapSize(); ++i) {
                    AnnotationHolder.CapHolder<Object, Object> hd = holder.getCapHolder(i);
                    BlockCapability<Object, Object> bc = hd.capability();
                    final Function<? super GenericTileEntity, Object> function = hd.function();
                    event.registerBlock(bc, new IBlockCapabilityProvider<Object, Object>() {
                        public @Nullable Object getCapability(Level level, BlockPos blockPos, BlockState blockState, @Nullable BlockEntity blockEntity, Object o) {
                            if (blockEntity instanceof GenericTileEntity be) {
                                return function.apply(be);
                            } else {
                                return null;
                            }
                        }
                    }, new Block[]{(Block) hd.block().get()});
                }
            }

            for (ResonantiaAnnotationHolder holder : registry.getResonantiaAnnotationHolders()) {
                for (int i = 0; i < holder.getCapSize(); ++i) {
                    ResonantiaAnnotationHolder.CapHolder<Object, Object> hd = holder.getCapHolder(i);
                    BlockCapability<Object, Object> bc = hd.capability();
                    final BiFunction<? super ResonantiaTileEntity, Object, Object> function = hd.function();
                    event.registerBlock(bc, new IBlockCapabilityProvider<>() {
                        public @Nullable Object getCapability(@NotNull Level level, @NotNull BlockPos blockPos, @NotNull BlockState blockState, @Nullable BlockEntity blockEntity, Object o) {
                            if (blockEntity instanceof ResonantiaTileEntity be) {
                                return function.apply(be, o);
                            } else {
                                return null;
                            }
                        }
                    }, new Block[]{(Block) hd.block().get()});
                }
            }
        };
    }
}