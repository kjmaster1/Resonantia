package com.kjmaster.resonantia.modules.resonantenergy;

import com.kjmaster.resonantia.modules.resonantenergy.blocks.receiver.SimpleResonantEnergyReceiverTE;
import com.kjmaster.resonantia.modules.resonantenergy.blocks.transmitter.SimpleResonantEnergyTransmitterTE;
import com.kjmaster.resonantia.modules.resonantenergy.client.gui.GuiResonantEnergyReceiver;
import com.kjmaster.resonantia.modules.resonantenergy.client.gui.GuiResonantEnergyTransmitter;
import com.kjmaster.resonantia.modules.resonantenergy.client.renderer.ResonantEnergyReceiverRenderer;
import com.kjmaster.resonantia.modules.resonantenergy.client.renderer.ResonantEnergyTransmitterRenderer;
import mcjty.lib.blocks.BaseBlock;
import mcjty.lib.blocks.RBlock;
import mcjty.lib.container.GenericContainer;
import mcjty.lib.datagen.DataGen;
import mcjty.lib.datagen.Dob;
import mcjty.lib.modules.IModule;
import net.minecraft.core.HolderLookup;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.BlockItem;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;

import java.util.function.Supplier;

import static com.kjmaster.resonantia.setup.Registration.*;
import static mcjty.lib.datagen.BaseBlockStateProvider.*;
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
                        .blockState(p -> {
                            p.simpleBlock(SIMPLE_RESONANT_ENERGY_TRANSMITTER.block().get(), p.models().slab("simple_resonant_energy_transmitter",
                                    RFTOOLSBASE_SIDE,
                                    RFTOOLSBASE_BOTTOM,
                                    RFTOOLSBASE_TOP
                            ));
                        }),
                Dob.blockBuilder(SIMPLE_RESONANT_ENERGY_RECEIVER)
                        .stonePickaxeTags()
                        .standardLoot(ITEM_INFUSABLE.get(), ITEM_BASE_BE_DATA.get(), ITEM_FREQUENCY.get())
                        .parentedItem()
                        .blockState(p -> {
                            p.simpleBlock(SIMPLE_RESONANT_ENERGY_RECEIVER.block().get(), p.models().slab("simple_resonant_energy_receiver",
                                    RFTOOLSBASE_SIDE,
                                    RFTOOLSBASE_BOTTOM,
                                    RFTOOLSBASE_TOP
                            ));
                        })
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
