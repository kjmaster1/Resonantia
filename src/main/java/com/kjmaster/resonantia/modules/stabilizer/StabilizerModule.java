package com.kjmaster.resonantia.modules.stabilizer;

import com.kjmaster.resonantia.client.renderer.blockentity.ResonantEnergyMachineRenderer;
import com.kjmaster.resonantia.datagen.DataGenHelper;
import com.kjmaster.resonantia.modules.stabilizer.blocks.SimpleResonantStabilizerTE;
import com.kjmaster.resonantia.modules.stabilizer.client.GuiResonantStabilizer;
import com.kjmaster.resonantia.modules.stabilizer.crafting.StabilizingRecipe;
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
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;

import java.util.function.Supplier;

import static com.kjmaster.resonantia.setup.Registration.*;
import static mcjty.lib.setup.Registration.ITEM_BASE_BE_DATA;
import static mcjty.lib.setup.Registration.ITEM_INFUSABLE;

public class StabilizerModule implements IModule {

    public static final RBlock<BaseBlock, BlockItem, SimpleResonantStabilizerTE> SIMPLE_RESONANT_STABILIZER = RBLOCKS.registerBlock("simple_resonant_stabilizer",
            SimpleResonantStabilizerTE.class,
            SimpleResonantStabilizerTE::createBlock,
            block -> new BlockItem(block.get(), createStandardProperties()),
            SimpleResonantStabilizerTE::new
    );

    public static final Supplier<MenuType<GenericContainer>> CONTAINER_RESONANT_STABILIZER = CONTAINERS.register("resonant_stabilizer", GenericContainer::createContainerType);

    public static final RecipeTypeSerializerPair<StabilizingRecipe, StabilizingRecipe.Serializer> STABILIZING = registerRecipePair(
            "stabilizing", StabilizingRecipe.Serializer::new);

    public StabilizerModule(IEventBus bus) {
        bus.addListener(this::registerScreens);
    }

    public void registerScreens(RegisterMenuScreensEvent event) {
        GuiResonantStabilizer.register(event);
    }

    @Override
    public void initDatagen(DataGen dataGen, HolderLookup.Provider provider) {
        dataGen.add(
                Dob.blockBuilder(SIMPLE_RESONANT_STABILIZER)
                        .name("Simple Resonant Stabilizer")
                        .ironPickaxeTags()
                        .standardLoot(ITEM_INFUSABLE.get(), ITEM_BASE_BE_DATA.get(), ITEM_FREQUENCY.get(), ITEM_PROCESSING_MACHINE_DATA.get(), ITEM_SIDED_ENERGY_MODE_DATA.get(), ITEM_SIDED_ITEM_MODE_DATA.get())
                        .blockState(p -> DataGenHelper.createProcessingMachineModel(p, SIMPLE_RESONANT_STABILIZER.block().get(), "resonant_stabilizer", "simple"))
                        .parentedItem()
        );
    }

    @Override
    public void init(FMLCommonSetupEvent fmlCommonSetupEvent) {

    }

    @Override
    public void initClient(FMLClientSetupEvent fmlClientSetupEvent) {
        BlockEntityRenderers.register(SIMPLE_RESONANT_STABILIZER.be().get(), ResonantEnergyMachineRenderer::new);
    }

    @Override
    public void initConfig(IEventBus iEventBus) {

    }
}
