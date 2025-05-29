package com.kjmaster.resonantia.modules.simple;

import com.kjmaster.resonantia.setup.Registration;
import mcjty.lib.blocks.BaseBlock;
import mcjty.lib.blocks.RotationType;
import mcjty.lib.builder.BlockBuilder;
import mcjty.lib.datagen.DataGen;
import mcjty.lib.datagen.Dob;
import mcjty.lib.modules.IModule;
import mcjty.rftoolsbase.modules.various.VariousModule;
import net.minecraft.core.HolderLookup;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.material.MapColor;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredItem;

import static com.kjmaster.resonantia.Resonantia.tab;
import static com.kjmaster.resonantia.modules.lumen.LumenModule.LUMEN_CRYSTAL;
import static com.kjmaster.resonantia.modules.lumen.LumenModule.LUMEN_CRYSTAL_ITEM_TAG;
import static com.kjmaster.resonantia.setup.Registration.BLOCKS;
import static com.kjmaster.resonantia.setup.Registration.ITEMS;
import static mcjty.lib.datagen.BaseBlockStateProvider.RFTOOLSBASE_SIDE;
import static mcjty.lib.datagen.BaseBlockStateProvider.RFTOOLSBASE_TOP;
import static mcjty.lib.datagen.Dob.has;

public class SimpleModule implements IModule {

    public static final DeferredBlock<BaseBlock> SIMPLE_RESONANT_MACHINE_FRAME = BLOCKS.register("simple_resonant_machine_frame", () -> new BaseBlock(
                    new BlockBuilder()
                            .properties(
                                    BlockBehaviour.Properties.of()
                                            .mapColor(MapColor.METAL)
                                            .instrument(NoteBlockInstrument.IRON_XYLOPHONE)
                                            .requiresCorrectToolForDrops()
                                            .strength(5.0F, 6.0F)
                                            .sound(SoundType.METAL))
            ) {
                @Override
                public RotationType getRotationType() {
                    return RotationType.HORIZROTATION;
                }
            }
    );

    public static final DeferredItem<Item> SIMPLE_RESONANT_MACHINE_FRAME_ITEM = ITEMS.register("simple_resonant_machine_frame", tab(() -> new BlockItem(SIMPLE_RESONANT_MACHINE_FRAME.get(), Registration.createStandardProperties())));


    @Override
    public void initDatagen(DataGen dataGen, HolderLookup.Provider provider) {
        dataGen.add(
                Dob.builder(SIMPLE_RESONANT_MACHINE_FRAME, SIMPLE_RESONANT_MACHINE_FRAME_ITEM)
                        .name("Simple Resonant Machine Frame")
                        .ironPickaxeTags()
                        .simpleLoot()
                        .blockState(baseBlockStateProvider -> baseBlockStateProvider.horizontalBlock(SIMPLE_RESONANT_MACHINE_FRAME.get(),
                                ResourceLocation.fromNamespaceAndPath("rftoolsbase", "block/base/machinesidec"),
                                RFTOOLSBASE_SIDE,
                                RFTOOLSBASE_TOP))
                        .parentedItem()
                        .shaped(builder -> builder
                                        .define('m', VariousModule.MACHINE_FRAME)
                                        .define('l', LUMEN_CRYSTAL_ITEM_TAG)
                                        .define('C', Tags.Items.STORAGE_BLOCKS_COPPER)
                                        .unlockedBy("lumen_crystal", has(LUMEN_CRYSTAL)),
                                "ioi", "lml", "RCR"
                        )
        );
    }

    @Override
    public void init(FMLCommonSetupEvent fmlCommonSetupEvent) {

    }

    @Override
    public void initClient(FMLClientSetupEvent fmlClientSetupEvent) {

    }

    @Override
    public void initConfig(IEventBus iEventBus) {

    }
}
