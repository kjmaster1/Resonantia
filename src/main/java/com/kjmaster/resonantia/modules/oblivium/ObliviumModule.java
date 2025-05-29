package com.kjmaster.resonantia.modules.oblivium;

import com.kjmaster.resonantia.Resonantia;
import com.kjmaster.resonantia.modules.crusher.crafting.CrushingRecipeBuilder;
import com.kjmaster.resonantia.modules.smelter.crafting.AlloyRecipeBuilder;
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
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DropExperienceBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.material.MapColor;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.common.crafting.SizedIngredient;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredItem;

import java.util.List;

import static com.kjmaster.resonantia.Resonantia.tab;
import static com.kjmaster.resonantia.setup.Registration.*;
import static mcjty.lib.datagen.Dob.has;
import static net.minecraft.world.level.block.Blocks.DIAMOND_BLOCK;

public class ObliviumModule implements IModule {

    public static final DeferredBlock<Block> OBLIVIUM_ORE = BLOCKS.register("oblivium_ore", () -> new DropExperienceBlock(UniformInt.of(4, 10), BlockBehaviour.Properties.of().mapColor(MapColor.SAND).instrument(NoteBlockInstrument.BASEDRUM).requiresCorrectToolForDrops().strength(3.0F, 9.0F)));
    public static final DeferredItem<Item> OBLIVIUM_ORE_ITEM = ITEMS.register("oblivium_ore", tab(() -> new BlockItem(OBLIVIUM_ORE.get(), Registration.createStandardProperties())));

    public static final DeferredItem<Item> OBLIVIUM_FRAGMENT = ITEMS.register("oblivium_fragment", tab(ObliviumModule::createItem64));

    public static final DeferredItem<Item> OBLIVIUM_GEM = ITEMS.register("oblivium_gem", tab(ObliviumModule::createItem64));
    public static final DeferredBlock<Block> OBLIVIUM_GEM_BLOCK = BLOCKS.register("oblivium_gem_block", () -> new Block(BlockBehaviour.Properties.ofFullCopy(DIAMOND_BLOCK)));
    public static final DeferredItem<Item> OBLIVIUM_GEM_BLOCK_ITEM = ITEMS.register("oblivium_gem_block", tab(() -> new BlockItem(OBLIVIUM_GEM_BLOCK.get(), Registration.createStandardProperties())));

    public static final DeferredItem<Item> OBLIVIUM_DUST = ITEMS.register("oblivium_dust", tab(ObliviumModule::createItem64));

    public static final TagKey<Block> OBLIVIUM_ORE_BLOCK_TAG = TagTools.createBlockTagKey(ResourceLocation.fromNamespaceAndPath("c", "ores/oblivium"));
    public static final TagKey<Item> OBLIVIUM_ORE_ITEM_TAG = TagTools.createItemTagKey(ResourceLocation.fromNamespaceAndPath("c", "ores/oblivium"));
    public static final TagKey<Item> OBLIVIUM_FRAGMENT_ITEM_TAG = TagTools.createItemTagKey(ResourceLocation.fromNamespaceAndPath("resonantia", "fragments/oblivium"));

    public static final TagKey<Item> OBLIVIUM_GEM_ITEM_TAG = TagTools.createItemTagKey(ResourceLocation.fromNamespaceAndPath("c", "gems/oblivium"));
    public static final TagKey<Block> OBLIVIUM_GEM_BLOCK_BLOCK_TAG = TagTools.createBlockTagKey(ResourceLocation.fromNamespaceAndPath("c", "storage_blocks/oblivium"));
    public static final TagKey<Item> OBLIVIUM_GEM_BLOCK_ITEM_TAG = TagTools.createItemTagKey(ResourceLocation.fromNamespaceAndPath("c", "storage_blocks/oblivium"));

    public static final TagKey<Item> OBLIVIUM_DUST_ITEM_TAG = TagTools.createItemTagKey(ResourceLocation.fromNamespaceAndPath("c", "dusts/oblivium"));

    @Override
    public void initDatagen(DataGen dataGen, HolderLookup.Provider provider) {
        dataGen.add(
                Dob.builder(OBLIVIUM_ORE, OBLIVIUM_ORE_ITEM)
                        .diamondPickaxeTags()
                        .silkTouchLoot(provider, OBLIVIUM_FRAGMENT, 1f, 1f)
                        .blockTags(List.of(Tags.Blocks.ORES, OBLIVIUM_ORE_BLOCK_TAG))
                        .itemTags(List.of(Tags.Items.ORES, OBLIVIUM_ORE_ITEM_TAG))
                        .simpleBlockState()
                        .name("Oblivium Ore")
                        .parentedItem(),
                Dob.itemBuilder(OBLIVIUM_FRAGMENT)
                        .name("Oblivium Fragment")
                        .itemTags(List.of(FRAGMENTS_ITEM_TAG, OBLIVIUM_FRAGMENT_ITEM_TAG))
                        .generatedItem("item/oblivium_fragment"),
                Dob.builder(OBLIVIUM_GEM_BLOCK, OBLIVIUM_GEM_BLOCK_ITEM)
                        .ironPickaxeTags()
                        .simpleLoot()
                        .blockTags(List.of(Tags.Blocks.STORAGE_BLOCKS, OBLIVIUM_GEM_BLOCK_BLOCK_TAG))
                        .itemTags(List.of(Tags.Items.STORAGE_BLOCKS, OBLIVIUM_GEM_BLOCK_ITEM_TAG))
                        .simpleBlockState()
                        .parentedItem()
                        .name("Oblivium Gem Block")
                        .shaped(builder -> builder
                                        .define('v', OBLIVIUM_GEM_ITEM_TAG)
                                        .unlockedBy("has_oblivium_gem", has(OBLIVIUM_GEM_ITEM_TAG)),
                                "vvv", "vvv", "vvv"
                        ),
                Dob.itemBuilder(OBLIVIUM_GEM)
                        .itemTags(List.of(Tags.Items.GEMS, OBLIVIUM_GEM_ITEM_TAG))
                        .generatedItem("item/oblivium_gem")
                        .name("Oblivium Gem")
                        .recipeConsumer(() -> (recipeOutput) -> {
                            AlloyRecipeBuilder.build(new ItemStack(OBLIVIUM_GEM.get()),
                                    List.of(SizedIngredient.of(OBLIVIUM_FRAGMENT_ITEM_TAG, 4), SizedIngredient.of(Tags.Items.GEMS_DIAMOND, 1)),
                                    12000, 1.8f, recipeOutput);
                        }),
                Dob.itemBuilder(OBLIVIUM_DUST)
                        .itemTags(List.of(Tags.Items.DUSTS, OBLIVIUM_DUST_ITEM_TAG))
                        .generatedItem("item/oblivium_dust")
                        .name("Oblivium Dust")
                        .recipeConsumer(() -> (recipeOutput) -> {
                            CrushingRecipeBuilder.build(new ItemStack(OBLIVIUM_DUST.get(), 1),
                                    SizedIngredient.of(OBLIVIUM_GEM_ITEM_TAG, 1),
                                    12000, 1.8f, recipeOutput);
                        })
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
