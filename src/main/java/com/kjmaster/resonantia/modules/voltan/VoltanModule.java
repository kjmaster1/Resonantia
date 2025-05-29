package com.kjmaster.resonantia.modules.voltan;

import com.kjmaster.resonantia.Resonantia;
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
import net.minecraft.world.level.block.SoundType;
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

public class VoltanModule implements IModule {

    public static final DeferredBlock<Block> VOLTANITE_ORE = BLOCKS.register("voltanite_ore", () -> new DropExperienceBlock(UniformInt.of(0, 2), BlockBehaviour.Properties.of().mapColor(MapColor.STONE).instrument(NoteBlockInstrument.BASEDRUM).requiresCorrectToolForDrops().strength(3.0F, 3.0F)));
    public static final DeferredItem<Item> VOLTANITE_ORE_ITEM = ITEMS.register("voltanite_ore", tab(() -> new BlockItem(VOLTANITE_ORE.get(), Registration.createStandardProperties())));

    public static final DeferredBlock<Block> DEEPSLATE_VOLTANITE_ORE = BLOCKS.register("deepslate_voltanite_ore", () -> new DropExperienceBlock(UniformInt.of(0, 2), BlockBehaviour.Properties.ofLegacyCopy(VOLTANITE_ORE.get()).mapColor(MapColor.DEEPSLATE).strength(4.5F, 3.0F).sound(SoundType.DEEPSLATE)));
    public static final DeferredItem<Item> DEEPSLATE_VOLTANITE_ORE_ITEM = ITEMS.register("deepslate_voltanite_ore", tab(() -> new BlockItem(DEEPSLATE_VOLTANITE_ORE.get(), Registration.createStandardProperties())));

    public static final DeferredItem<Item> VOLTANITE_DUST = ITEMS.register("voltanite_dust", tab(VoltanModule::createItem64));

    public static final DeferredItem<Item> VOLTALIUM_NUGGET = ITEMS.register("voltalium_nugget", tab(VoltanModule::createItem64));
    public static final DeferredItem<Item> VOLTALIUM_INGOT = ITEMS.register("voltalium_ingot", tab(VoltanModule::createItem64));
    public static final DeferredBlock<Block> VOLTALIUM_BLOCK = BLOCKS.register("voltalium_block", () -> new Block(BlockBehaviour.Properties.of().mapColor(MapColor.METAL).instrument(NoteBlockInstrument.IRON_XYLOPHONE).requiresCorrectToolForDrops().strength(5.0F, 6.0F).sound(SoundType.METAL)));
    public static final DeferredItem<Item> VOLTALIUM_BLOCK_ITEM = ITEMS.register("voltalium_block", tab(() -> new BlockItem(VOLTALIUM_BLOCK.get(), Registration.createStandardProperties())));

    public static final TagKey<Block> VOLTANITE_ORE_BLOCK_TAG = TagTools.createBlockTagKey(ResourceLocation.fromNamespaceAndPath("c", "ores/voltanite"));
    public static final TagKey<Item> VOLTANITE_ORE_ITEM_TAG = TagTools.createItemTagKey(ResourceLocation.fromNamespaceAndPath("c", "ores/voltanite"));

    public static final TagKey<Item> VOLTANITE_DUST_ITEM_TAG = TagTools.createItemTagKey(ResourceLocation.fromNamespaceAndPath("c", "dusts/voltanite"));

    public static final TagKey<Item> VOLTALIUM_NUGGET_ITEM_TAG = TagTools.createItemTagKey(ResourceLocation.fromNamespaceAndPath("c", "nuggets/voltalium"));
    public static final TagKey<Item> VOLTALIUM_INGOT_ITEM_TAG = TagTools.createItemTagKey(ResourceLocation.fromNamespaceAndPath("c", "ingots/voltalium"));
    public static final TagKey<Block> VOLTALIUM_BLOCK_BLOCK_TAG = TagTools.createBlockTagKey(ResourceLocation.fromNamespaceAndPath("c", "storage_blocks/voltalium"));
    public static final TagKey<Item> VOLTALIUM_BLOCK_ITEM_TAG = TagTools.createItemTagKey(ResourceLocation.fromNamespaceAndPath("c", "storage_blocks/voltalium"));

    @Override
    public void initDatagen(DataGen dataGen, HolderLookup.Provider provider) {
        dataGen.add(
                Dob.builder(VOLTANITE_ORE, VOLTANITE_ORE_ITEM)
                        .ironPickaxeTags()
                        .silkTouchLoot(provider, VOLTANITE_DUST, 1f, 3f)
                        .blockTags(List.of(Tags.Blocks.ORES, VOLTANITE_ORE_BLOCK_TAG))
                        .itemTags(List.of(Tags.Items.ORES, VOLTANITE_ORE_ITEM_TAG))
                        .simpleBlockState()
                        .name("Voltanite Ore")
                        .parentedItem(),
                Dob.builder(DEEPSLATE_VOLTANITE_ORE, DEEPSLATE_VOLTANITE_ORE_ITEM)
                        .ironPickaxeTags()
                        .silkTouchLoot(provider, VOLTANITE_DUST, 1f, 3f)
                        .blockTags(List.of(Tags.Blocks.ORES, VOLTANITE_ORE_BLOCK_TAG))
                        .itemTags(List.of(Tags.Items.ORES, VOLTANITE_ORE_ITEM_TAG))
                        .simpleBlockState()
                        .name("Deepslate Voltanite Ore")
                        .parentedItem(),
                Dob.itemBuilder(VOLTANITE_DUST)
                        .itemTags(List.of(Tags.Items.DUSTS, VOLTANITE_DUST_ITEM_TAG))
                        .generatedItem("item/voltanite_dust")
                        .name("Voltanite Dust"),
                Dob.builder(VOLTALIUM_BLOCK, VOLTALIUM_BLOCK_ITEM)
                        .ironPickaxeTags()
                        .simpleLoot()
                        .blockTags(List.of(Tags.Blocks.STORAGE_BLOCKS, VOLTALIUM_BLOCK_BLOCK_TAG))
                        .itemTags(List.of(Tags.Items.STORAGE_BLOCKS, VOLTALIUM_BLOCK_ITEM_TAG))
                        .simpleBlockState()
                        .parentedItem()
                        .name("Voltalium Block")
                        .shaped(builder -> builder
                                        .define('v', VOLTALIUM_INGOT_ITEM_TAG)
                                        .unlockedBy("has_voltalium_ingot", has(VOLTALIUM_INGOT_ITEM_TAG)),
                                "vvv", "vvv", "vvv"
                        ),
                Dob.itemBuilder(VOLTALIUM_INGOT)
                        .itemTags(List.of(Tags.Items.INGOTS, VOLTALIUM_INGOT_ITEM_TAG))
                        .generatedItem("item/voltalium_ingot")
                        .shaped(builder -> builder
                                        .define('v', VOLTALIUM_INGOT_ITEM_TAG)
                                        .unlockedBy("has_voltalium_nugget", has(VOLTALIUM_NUGGET_ITEM_TAG)),
                                "vvv", "vvv", "vvv"
                        )
                        .name("Voltalium Ingot")
                        .recipeConsumer(() -> (recipeOutput) -> {
                            AlloyRecipeBuilder.build(new ItemStack(VOLTALIUM_INGOT.get()),
                                    List.of(SizedIngredient.of(VOLTANITE_DUST_ITEM_TAG, 4), SizedIngredient.of(Tags.Items.INGOTS_IRON, 1)),
                                    4800, 0.3f, recipeOutput);
                        })
                ,
                Dob.itemBuilder(VOLTALIUM_NUGGET)
                        .itemTags(List.of(Tags.Items.NUGGETS, VOLTALIUM_NUGGET_ITEM_TAG))
                        .generatedItem("item/voltalium_nugget")
                        .name("Voltalium Nugget")
                        .recipeConsumer(() -> (output) -> {
                            ShapelessRecipeBuilder
                                    .shapeless(RecipeCategory.MISC, VOLTALIUM_NUGGET, 9)
                                    .requires(VOLTALIUM_INGOT_ITEM_TAG)
                                    .unlockedBy("has_voltalium_ingot", has(VOLTALIUM_INGOT_ITEM_TAG))
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
