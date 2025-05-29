package com.kjmaster.resonantia.modules.thermon;

import com.kjmaster.resonantia.Resonantia;
import com.kjmaster.resonantia.modules.resonantquartz.ResonantQuartzModule;
import com.kjmaster.resonantia.modules.smelter.crafting.AlloyRecipeBuilder;
import com.kjmaster.resonantia.setup.Registration;
import mcjty.lib.datagen.DataGen;
import mcjty.lib.datagen.Dob;
import mcjty.lib.modules.IModule;
import mcjty.lib.varia.TagTools;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.ShapelessRecipeBuilder;
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
import static com.kjmaster.resonantia.setup.Registration.BLOCKS;
import static com.kjmaster.resonantia.setup.Registration.ITEMS;
import static mcjty.lib.datagen.Dob.has;
import static net.minecraft.world.level.block.Blocks.COPPER_BLOCK;

public class ThermonModule implements IModule {

    public static final DeferredBlock<Block> THERMONITE_ORE = BLOCKS.register("thermonite_ore", () -> new DropExperienceBlock(UniformInt.of(0, 2), BlockBehaviour.Properties.of().mapColor(MapColor.NETHER).instrument(NoteBlockInstrument.BASEDRUM).requiresCorrectToolForDrops().strength(3.0F, 3.0F)));
    public static final DeferredItem<Item> THERMONITE_ORE_ITEM = ITEMS.register("thermonite_ore", tab(() -> new BlockItem(THERMONITE_ORE.get(), Registration.createStandardProperties())));

    public static final DeferredItem<Item> THERMONITE_DUST = ITEMS.register("thermonite_dust", tab(ThermonModule::createItem64));
    public static final DeferredItem<Item> RESONANT_THERMONITE_QUARTZ = ITEMS.register("resonant_thermonite_quartz", tab(ThermonModule::createItem64));

    public static final DeferredItem<Item> THERMION_ALLOY_NUGGET = ITEMS.register("thermion_alloy_nugget", tab(ThermonModule::createItem64));
    public static final DeferredItem<Item> THERMION_ALLOY_INGOT = ITEMS.register("thermion_alloy_ingot", tab(ThermonModule::createItem64));
    public static final DeferredBlock<Block> THERMION_ALLOY_BLOCK = BLOCKS.register("thermion_alloy_block", () -> new Block(BlockBehaviour.Properties.ofFullCopy(COPPER_BLOCK)));
    public static final DeferredItem<Item> THERMION_ALLOY_BLOCK_ITEM = ITEMS.register("thermion_alloy_block", tab(() -> new BlockItem(THERMION_ALLOY_BLOCK.get(), Registration.createStandardProperties())));

    public static final TagKey<Block> THERMONITE_ORE_BLOCK_TAG = TagTools.createBlockTagKey(ResourceLocation.fromNamespaceAndPath("c", "ores/thermonite"));
    public static final TagKey<Item> THERMONITE_ORE_ITEM_TAG = TagTools.createItemTagKey(ResourceLocation.fromNamespaceAndPath("c", "ores/thermonite"));
    public static final TagKey<Item> THERMONITE_DUST_ITEM_TAG = TagTools.createItemTagKey(ResourceLocation.fromNamespaceAndPath("c", "dusts/thermonite"));
    public static final TagKey<Item> RESONANT_THERMONITE_QUARTZ_ITEM_TAG = TagTools.createItemTagKey(ResourceLocation.fromNamespaceAndPath("c", "gems/resonant_thermonite_quartz"));

    public static final TagKey<Item> THERMION_ALLOY_NUGGET_ITEM_TAG = TagTools.createItemTagKey(ResourceLocation.fromNamespaceAndPath("c", "nuggets/thermion_alloy"));
    public static final TagKey<Item> THERMION_ALLOY_INGOT_ITEM_TAG = TagTools.createItemTagKey(ResourceLocation.fromNamespaceAndPath("c", "ingots/thermion_alloy"));
    public static final TagKey<Block> THERMION_ALLOY_BLOCK_BLOCK_TAG = TagTools.createBlockTagKey(ResourceLocation.fromNamespaceAndPath("c", "storage_blocks/thermion_alloy"));
    public static final TagKey<Item> THERMION_ALLOY_BLOCK_ITEM_TAG = TagTools.createItemTagKey(ResourceLocation.fromNamespaceAndPath("c", "storage_blocks/thermion_alloy"));

    @Override
    public void initDatagen(DataGen dataGen, HolderLookup.Provider provider) {
        dataGen.add(
                Dob.builder(THERMONITE_ORE, THERMONITE_ORE_ITEM)
                        .name("Thermonite Ore")
                        .ironPickaxeTags()
                        .silkTouchLoot(provider, THERMONITE_DUST, 1f, 3f)
                        .blockTags(List.of(Tags.Blocks.ORES, THERMONITE_ORE_BLOCK_TAG))
                        .itemTags(List.of(Tags.Items.ORES, THERMONITE_ORE_ITEM_TAG))
                        .simpleBlockState()
                        .parentedItem(),
                Dob.itemBuilder(THERMONITE_DUST)
                        .name("Thermonite Dust")
                        .itemTags(List.of(Tags.Items.DUSTS, THERMONITE_DUST_ITEM_TAG))
                        .generatedItem("item/thermonite_dust"),
                Dob.itemBuilder(RESONANT_THERMONITE_QUARTZ)
                        .name("Resonant Thermonite Quartz")
                        .itemTags(List.of(Tags.Items.GEMS, RESONANT_THERMONITE_QUARTZ_ITEM_TAG))
                        .generatedItem("item/resonant_thermonite_quartz")
                        .recipeConsumer(() -> (output) -> {
                            AlloyRecipeBuilder.build(new ItemStack(ThermonModule.RESONANT_THERMONITE_QUARTZ.get()),
                                    List.of(SizedIngredient.of(ThermonModule.THERMONITE_DUST_ITEM_TAG, 4), SizedIngredient.of(ResonantQuartzModule.RESONANT_QUARTZ_ITEM_TAG, 1)),
                                    4800, 0.3f, output);
                        }),
                Dob.builder(THERMION_ALLOY_BLOCK, THERMION_ALLOY_BLOCK_ITEM)
                        .ironPickaxeTags()
                        .simpleLoot()
                        .blockTags(List.of(Tags.Blocks.STORAGE_BLOCKS, THERMION_ALLOY_BLOCK_BLOCK_TAG))
                        .itemTags(List.of(Tags.Items.STORAGE_BLOCKS, THERMION_ALLOY_BLOCK_ITEM_TAG))
                        .simpleBlockState()
                        .parentedItem()
                        .name("Thermion Alloy Block")
                        .shaped(builder -> builder
                                        .define('v', THERMION_ALLOY_INGOT_ITEM_TAG)
                                        .unlockedBy("has_thermion_alloy_ingot", has(THERMION_ALLOY_INGOT_ITEM_TAG)),
                                "vvv", "vvv", "vvv"
                        ),
                Dob.itemBuilder(THERMION_ALLOY_INGOT)
                        .itemTags(List.of(Tags.Items.INGOTS, THERMION_ALLOY_INGOT_ITEM_TAG))
                        .generatedItem("item/thermion_alloy_ingot")
                        .shaped(builder -> builder
                                        .define('v', THERMION_ALLOY_INGOT_ITEM_TAG)
                                        .unlockedBy("has_thermion_alloy_nugget", has(THERMION_ALLOY_NUGGET_ITEM_TAG)),
                                "vvv", "vvv", "vvv"
                        )
                        .name("Thermion Alloy Ingot")
                        .recipeConsumer(() -> (recipeOutput) -> {
                            AlloyRecipeBuilder.build(new ItemStack(THERMION_ALLOY_INGOT.get()),
                                    List.of(SizedIngredient.of(THERMONITE_DUST, 4), SizedIngredient.of(Tags.Items.INGOTS_COPPER, 1)),
                                    3600, 0.3f, recipeOutput);
                        })
                ,
                Dob.itemBuilder(THERMION_ALLOY_NUGGET)
                        .itemTags(List.of(Tags.Items.NUGGETS, THERMION_ALLOY_NUGGET_ITEM_TAG))
                        .generatedItem("item/thermion_alloy_nugget")
                        .name("Thermion Alloy Nugget")
                        .recipeConsumer(() -> (output) -> {
                            ShapelessRecipeBuilder
                                    .shapeless(RecipeCategory.MISC, THERMION_ALLOY_NUGGET, 9)
                                    .requires(THERMION_ALLOY_INGOT_ITEM_TAG)
                                    .unlockedBy("has_thermion_alloy_ingot", has(THERMION_ALLOY_INGOT_ITEM_TAG))
                                    .save(output);
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
