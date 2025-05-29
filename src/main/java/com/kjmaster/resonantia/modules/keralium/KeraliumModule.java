package com.kjmaster.resonantia.modules.keralium;

import com.kjmaster.resonantia.Resonantia;
import com.kjmaster.resonantia.setup.Registration;
import mcjty.lib.datagen.DataGen;
import mcjty.lib.datagen.Dob;
import mcjty.lib.modules.IModule;
import mcjty.lib.varia.TagTools;
import net.minecraft.core.HolderLookup;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DropExperienceBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.material.MapColor;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredItem;

import java.util.List;

import static com.kjmaster.resonantia.Resonantia.tab;
import static com.kjmaster.resonantia.setup.Registration.BLOCKS;
import static com.kjmaster.resonantia.setup.Registration.ITEMS;

public class KeraliumModule implements IModule {

    public static final DeferredBlock<Block> KERALIUM_ORE = BLOCKS.register("keralium_ore", () -> new DropExperienceBlock(UniformInt.of(0, 2), BlockBehaviour.Properties.of().mapColor(MapColor.STONE).instrument(NoteBlockInstrument.BASEDRUM).requiresCorrectToolForDrops().strength(3.0F, 3.0F)));
    public static final DeferredItem<Item> KERALIUM_ORE_ITEM = ITEMS.register("keralium_ore", tab(() -> new BlockItem(KERALIUM_ORE.get(), Registration.createStandardProperties())));

    public static final DeferredItem<Item> KERALIUM_GLOBULE = ITEMS.register("keralium_globule", tab(KeraliumModule::createItem64));

    public static final TagKey<Block> KERALIUM_ORE_BLOCK_TAG = TagTools.createBlockTagKey(ResourceLocation.fromNamespaceAndPath("c", "ores/keralium"));
    public static final TagKey<Item> KERALIUM_ORE_ITEM_TAG = TagTools.createItemTagKey(ResourceLocation.fromNamespaceAndPath("c", "ores/keralium"));
    public static final TagKey<Item> KERALIUM_GLOBULE_ITEM_TAG = TagTools.createItemTagKey(ResourceLocation.fromNamespaceAndPath("resonantia", "globules/keralium"));

    @Override
    public void initDatagen(DataGen dataGen, HolderLookup.Provider provider) {
        dataGen.add(
                Dob.builder(KERALIUM_ORE, KERALIUM_ORE_ITEM)
                        .stonePickaxeTags()
                        .silkTouchLoot(provider, KERALIUM_GLOBULE, 1f, 3f)
                        .blockTags(List.of(Tags.Blocks.ORES, KERALIUM_ORE_BLOCK_TAG))
                        .itemTags(List.of(Tags.Items.ORES, KERALIUM_ORE_ITEM_TAG))
                        .simpleBlockState()
                        .name("Keralium Ore")
                        .parentedItem(),
                Dob.itemBuilder(KERALIUM_GLOBULE)
                        .name("Keralium Globule")
                        .itemTags(List.of(Registration.GLOBULES_ITEM_TAG, KERALIUM_GLOBULE_ITEM_TAG))
                        .generatedItem("item/keralium_globule")
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

    private static Item createItem64() {
        return new Item(Resonantia.setup.defaultProperties().stacksTo(64));
    }
}
