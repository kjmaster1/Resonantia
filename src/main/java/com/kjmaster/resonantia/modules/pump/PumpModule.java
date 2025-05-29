package com.kjmaster.resonantia.modules.pump;

import com.kjmaster.resonantia.client.renderer.blockentity.ResonantEnergyMachineRenderer;
import com.kjmaster.resonantia.datagen.DataGenHelper;
import com.kjmaster.resonantia.modules.pump.blocks.SimpleResonantPumpTE;
import com.kjmaster.resonantia.modules.pump.client.GuiResonantPump;
import mcjty.lib.blocks.BaseBlock;
import mcjty.lib.blocks.RBlock;
import mcjty.lib.container.GenericContainer;
import mcjty.lib.datagen.DataGen;
import mcjty.lib.datagen.Dob;
import mcjty.lib.modules.IModule;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraft.core.HolderLookup;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.BlockItem;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;

import java.util.function.Supplier;

import static com.kjmaster.resonantia.setup.Registration.*;
import static mcjty.lib.setup.Registration.ITEM_BASE_BE_DATA;
import static mcjty.lib.setup.Registration.ITEM_INFUSABLE;

public class PumpModule implements IModule {

    public static final RBlock<BaseBlock, BlockItem, SimpleResonantPumpTE> SIMPLE_RESONANT_PUMP = RBLOCKS.registerBlock("simple_resonant_pump",
            SimpleResonantPumpTE.class,
            SimpleResonantPumpTE::createBlock,
            block -> new BlockItem(block.get(), createStandardProperties()),
            SimpleResonantPumpTE::new
    );

    public static final Supplier<MenuType<GenericContainer>> CONTAINER_RESONANT_PUMP = CONTAINERS.register("resonant_pump", GenericContainer::createContainerType);

    public PumpModule(IEventBus bus) {
        bus.addListener(this::registerMenuScreens);
    }

    public void registerMenuScreens(RegisterMenuScreensEvent event) {
        GuiResonantPump.register(event);
    }

    @Override
    public void initDatagen(DataGen dataGen, HolderLookup.Provider provider) {
        dataGen.add(
                Dob.blockBuilder(SIMPLE_RESONANT_PUMP)
                        .name("Simple Resonant Pump")
                        .ironPickaxeTags()
                        .standardLoot(ITEM_INFUSABLE.get(), ITEM_BASE_BE_DATA.get(), ITEM_FREQUENCY.get(), ITEM_SIDED_ENERGY_MODE_DATA.get(), ITEM_SIDED_ITEM_MODE_DATA.get())
                        .blockState(p -> DataGenHelper.createProcessingMachineModel(p, SIMPLE_RESONANT_PUMP.block().get(), "resonant_pump", "simple"))
                        .parentedItem()
        );
    }

    @Override
    public void init(FMLCommonSetupEvent fmlCommonSetupEvent) {
    }

    @Override
    public void initConfig(IEventBus iEventBus) {

    }

    @Override
    public void initClient(FMLClientSetupEvent fmlClientSetupEvent) {
        BlockEntityRenderers.register(SIMPLE_RESONANT_PUMP.be().get(), ResonantEnergyMachineRenderer::new);
    }
}
