package com.kjmaster.resonantia.modules.smelter;

import com.kjmaster.resonantia.client.renderer.blockentity.ResonantEnergyMachineRenderer;
import com.kjmaster.resonantia.datagen.DataGenHelper;
import com.kjmaster.resonantia.modules.resonantenergy.ResonantEnergyModule;
import com.kjmaster.resonantia.modules.smelter.blocks.SimpleResonantAlloySmelterTE;
import com.kjmaster.resonantia.modules.smelter.blocks.SimpleResonantSmelterTE;
import com.kjmaster.resonantia.modules.smelter.client.GuiResonantAlloySmelter;
import com.kjmaster.resonantia.modules.smelter.client.GuiResonantSmelter;
import com.kjmaster.resonantia.modules.smelter.crafting.AlloySmeltingRecipe;
import com.kjmaster.resonantia.recipes.RecipeTypeSerializerPair;
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
import net.minecraft.world.level.block.Blocks;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;

import java.util.function.Supplier;

import static com.kjmaster.resonantia.modules.lumen.LumenModule.LUMEN_CAPACITOR_ITEM_TAG;
import static com.kjmaster.resonantia.setup.Registration.*;
import static mcjty.lib.datagen.Dob.has;
import static mcjty.lib.setup.Registration.ITEM_BASE_BE_DATA;
import static mcjty.lib.setup.Registration.ITEM_INFUSABLE;

public class SmelterModule implements IModule {

    public static final RBlock<BaseBlock, BlockItem, SimpleResonantSmelterTE> SIMPLE_RESONANT_SMELTER = RBLOCKS.registerBlock("simple_resonant_smelter",
            SimpleResonantSmelterTE.class,
            SimpleResonantSmelterTE::createBlock,
            block -> new BlockItem(block.get(), createStandardProperties()),
            SimpleResonantSmelterTE::new
    );

    public static final RBlock<BaseBlock, BlockItem, SimpleResonantAlloySmelterTE> SIMPLE_RESONANT_ALLOY_SMELTER = RBLOCKS.registerBlock("simple_resonant_alloy_smelter",
            SimpleResonantAlloySmelterTE.class,
            SimpleResonantAlloySmelterTE::createBlock,
            block -> new BlockItem(block.get(), createStandardProperties()),
            SimpleResonantAlloySmelterTE::new
    );

    public static final Supplier<MenuType<GenericContainer>> CONTAINER_RESONANT_SMELTER = CONTAINERS.register("resonant_smelter", GenericContainer::createContainerType);

    public static final Supplier<MenuType<GenericContainer>> CONTAINER_RESONANT_ALLOY_SMELTER = CONTAINERS.register("resonant_alloy_smelter", GenericContainer::createContainerType);

    public static final RecipeTypeSerializerPair<AlloySmeltingRecipe, AlloySmeltingRecipe.Serializer> ALLOY_SMELTING = registerRecipePair(
            "alloy_smelting", AlloySmeltingRecipe.Serializer::new);

    public SmelterModule(IEventBus bus) {
        bus.addListener(this::registerMenuScreens);
    }

    public void registerMenuScreens(RegisterMenuScreensEvent event) {
        GuiResonantSmelter.register(event);
        GuiResonantAlloySmelter.register(event);
    }

    @Override
    public void initDatagen(DataGen dataGen, HolderLookup.Provider provider) {
        dataGen.add(
                Dob.blockBuilder(SIMPLE_RESONANT_SMELTER)
                        .name("Simple Resonant Smelter")
                        .ironPickaxeTags()
                        .standardLoot(ITEM_INFUSABLE.get(), ITEM_BASE_BE_DATA.get(), ITEM_FREQUENCY.get(), ITEM_PROCESSING_MACHINE_DATA.get(), ITEM_SIDED_ENERGY_MODE_DATA.get(), ITEM_SIDED_ITEM_MODE_DATA.get())
                        .blockState(p -> DataGenHelper.createProcessingMachineModel(p, SIMPLE_RESONANT_SMELTER.block().get(), "resonant_smelter", "simple"))
                        .parentedItem()
                        .shaped(builder -> builder
                                        .define('m', ResonantEnergyModule.SIMPLE_RESONANT_ENERGY_RECEIVER.item())
                                        .define('l', LUMEN_CAPACITOR_ITEM_TAG)
                                        .define('f', Blocks.FURNACE)
                                        .unlockedBy("simple_energy_receiver", has(LUMEN_CAPACITOR_ITEM_TAG)),
                                "   ", " m ", "lfl"
                        ),
                Dob.blockBuilder(SIMPLE_RESONANT_ALLOY_SMELTER)
                        .name("Simple Resonant Alloy Smelter")
                        .ironPickaxeTags()
                        .standardLoot(ITEM_INFUSABLE.get(), ITEM_BASE_BE_DATA.get(), ITEM_FREQUENCY.get(), ITEM_PROCESSING_MACHINE_DATA.get(), ITEM_SIDED_ENERGY_MODE_DATA.get(), ITEM_SIDED_ITEM_MODE_DATA.get())
                        .blockState(p -> DataGenHelper.createProcessingMachineModel(p, SIMPLE_RESONANT_ALLOY_SMELTER.block().get(), "resonant_alloy_smelter", "simple"))
                        .parentedItem()
        );
    }

    @Override
    public void init(FMLCommonSetupEvent fmlCommonSetupEvent) {

    }

    @Override
    public void initClient(FMLClientSetupEvent fmlClientSetupEvent) {
        BlockEntityRenderers.register(SIMPLE_RESONANT_SMELTER.be().get(), ResonantEnergyMachineRenderer::new);
        BlockEntityRenderers.register(SIMPLE_RESONANT_ALLOY_SMELTER.be().get(), ResonantEnergyMachineRenderer::new);
    }

    @Override
    public void initConfig(IEventBus iEventBus) {

    }
}
