package com.kjmaster.resonantia.modules.resonantenergy;

import com.kjmaster.resonantia.modules.resonantenergy.blocks.receiver.SimpleResonantEnergyReceiverTE;
import com.kjmaster.resonantia.modules.resonantenergy.blocks.transmitter.SimpleResonantEnergyTransmitterTE;
import com.kjmaster.resonantia.modules.resonantenergy.client.gui.GuiResonantEnergyReceiver;
import com.kjmaster.resonantia.modules.resonantenergy.client.gui.GuiResonantEnergyTransmitter;
import com.kjmaster.resonantia.modules.resonantenergy.client.renderer.ResonantEnergyReceiverRenderer;
import com.kjmaster.resonantia.modules.resonantenergy.client.renderer.ResonantEnergyTransmitterRenderer;
import com.kjmaster.resonantia.modules.simple.SimpleModule;
import mcjty.lib.blocks.BaseBlock;
import mcjty.lib.blocks.RBlock;
import mcjty.lib.container.GenericContainer;
import mcjty.lib.datagen.DataGen;
import mcjty.lib.datagen.Dob;
import mcjty.lib.modules.IModule;
import mcjty.rftoolsbase.modules.various.VariousModule;
import net.minecraft.core.HolderLookup;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import net.neoforged.neoforge.common.Tags;

import java.util.function.Supplier;

import static com.kjmaster.resonantia.modules.lumen.LumenModule.LUMEN_CRYSTAL;
import static com.kjmaster.resonantia.modules.lumen.LumenModule.LUMEN_CRYSTAL_ITEM_TAG;
import static com.kjmaster.resonantia.setup.Registration.*;
import static mcjty.lib.datagen.BaseBlockStateProvider.*;
import static mcjty.lib.datagen.Dob.has;
import static mcjty.lib.setup.Registration.ITEM_BASE_BE_DATA;
import static mcjty.lib.setup.Registration.ITEM_INFUSABLE;

public class ResonantEnergyModule implements IModule {

    public static final RBlock<BaseBlock, BlockItem, SimpleResonantEnergyTransmitterTE> SIMPLE_RESONANT_ENERGY_TRANSMITTER = RBLOCKS.registerBlock("simple_resonant_energy_transmitter",
            SimpleResonantEnergyTransmitterTE.class,
            SimpleResonantEnergyTransmitterTE::createBlock,
            block -> new BlockItem(block.get(), createStandardProperties()),
            SimpleResonantEnergyTransmitterTE::new
    );

    public static final Supplier<MenuType<GenericContainer>> CONTAINER_RESONANT_ENERGY_TRANSMITTER = CONTAINERS.register("resonant_energy_transmitter", GenericContainer::createContainerType);

    public static final RBlock<BaseBlock, BlockItem, SimpleResonantEnergyReceiverTE> SIMPLE_RESONANT_ENERGY_RECEIVER = RBLOCKS.registerBlock("simple_resonant_energy_receiver",
            SimpleResonantEnergyReceiverTE.class,
            SimpleResonantEnergyReceiverTE::createBlock,
            block -> new BlockItem(block.get(), createStandardProperties()),
            SimpleResonantEnergyReceiverTE::new
    );

    public static final Supplier<MenuType<GenericContainer>> CONTAINER_RESONANT_ENERGY_RECEIVER = CONTAINERS.register("resonant_energy_receiver", GenericContainer::createContainerType);

    public static final VoxelShape SLAB = Shapes.box(0f, 0f, 0f, 1f, 0.5f, 1f);

    public ResonantEnergyModule(IEventBus bus) {
        bus.addListener(this::registerScreens);
    }

    public void registerScreens(RegisterMenuScreensEvent event) {
        GuiResonantEnergyTransmitter.register(event);
        GuiResonantEnergyReceiver.register(event);
    }

    @Override
    public void initDatagen(DataGen dataGen, HolderLookup.Provider provider) {
        dataGen.add(
                Dob.blockBuilder(SIMPLE_RESONANT_ENERGY_TRANSMITTER)
                        .stonePickaxeTags()
                        .standardLoot(ITEM_INFUSABLE.get(), ITEM_BASE_BE_DATA.get(), ITEM_FREQUENCY.get())
                        .parentedItem()
                        .name("Simple Resonant Energy Transmitter")
                        .shaped(builder -> builder
                                        .define('m', SimpleModule.SIMPLE_RESONANT_MACHINE_FRAME_ITEM)
                                        .define('l', LUMEN_CRYSTAL_ITEM_TAG)
                                        .unlockedBy("simple_resonant_machine_frame", has(SimpleModule.SIMPLE_RESONANT_MACHINE_FRAME_ITEM)),
                                " l ", " m ", "   "
                        )
                        .shapeless("simple_resonant_energy_transmitter_shapeless", builder -> builder
                                .requires(SIMPLE_RESONANT_ENERGY_RECEIVER.item())
                                .unlockedBy("simple_resonant_energy_receiver", has(SIMPLE_RESONANT_ENERGY_RECEIVER.item()))
                        ),
                Dob.blockBuilder(SIMPLE_RESONANT_ENERGY_RECEIVER)
                        .name("Simple Resonant Energy Receiver")
                        .stonePickaxeTags()
                        .standardLoot(ITEM_INFUSABLE.get(), ITEM_BASE_BE_DATA.get(), ITEM_FREQUENCY.get())
                        .parentedItem()
                        .shapeless(builder -> builder
                                .requires(SIMPLE_RESONANT_ENERGY_TRANSMITTER.item())
                                .unlockedBy("simple_resonant_energy_transmitter", has(SIMPLE_RESONANT_ENERGY_TRANSMITTER.item()))
                        )
        );
    }

    @Override
    public void init(FMLCommonSetupEvent fmlCommonSetupEvent) {

    }

    @Override
    public void initClient(FMLClientSetupEvent fmlClientSetupEvent) {
        ResonantEnergyReceiverRenderer.register();
        ResonantEnergyTransmitterRenderer.register();
    }

    @Override
    public void initConfig(IEventBus iEventBus) {

    }
}
