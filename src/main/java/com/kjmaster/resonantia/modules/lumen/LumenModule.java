package com.kjmaster.resonantia.modules.lumen;

import com.kjmaster.resonantia.Resonantia;
import com.kjmaster.resonantia.modules.lumen.blocks.LumeniteOreBlock;
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
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
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
import java.util.function.ToIntFunction;

import static com.kjmaster.resonantia.Resonantia.tab;
import static com.kjmaster.resonantia.modules.resonantquartz.ResonantQuartzModule.RESONANT_QUARTZ_DUST_ITEM_TAG;
import static com.kjmaster.resonantia.setup.Registration.BLOCKS;
import static com.kjmaster.resonantia.setup.Registration.ITEMS;
import static mcjty.lib.datagen.Dob.has;

public class LumenModule implements IModule {

    public static final DeferredBlock<LumeniteOreBlock> LUMENITE_ORE = BLOCKS.register("lumenite_ore", () -> new LumeniteOreBlock(BlockBehaviour.Properties.of().mapColor(MapColor.STONE).instrument(NoteBlockInstrument.BASEDRUM).requiresCorrectToolForDrops().randomTicks().lightLevel(litBlockEmission(9)).strength(3.0F, 3.0F)));
    public static final DeferredItem<Item> LUMENITE_ORE_ITEM = ITEMS.register("lumenite_ore", tab(() -> new BlockItem(LUMENITE_ORE.get(), Registration.createStandardProperties())));

    public static final DeferredBlock<LumeniteOreBlock> DEEPSLATE_LUMENITE_ORE = BLOCKS.register("deepslate_lumenite_ore", () -> new LumeniteOreBlock(BlockBehaviour.Properties.ofLegacyCopy(LUMENITE_ORE.get()).mapColor(MapColor.DEEPSLATE).strength(4.5F, 3.0F).sound(SoundType.DEEPSLATE)));
    public static final DeferredItem<Item> DEEPSLATE_LUMENITE_ORE_ITEM = ITEMS.register("deepslate_lumenite_ore", tab(() -> new BlockItem(DEEPSLATE_LUMENITE_ORE.get(), Registration.createStandardProperties())));

    public static final DeferredItem<Item> LUMENITE_DUST = ITEMS.register("lumenite_dust", tab(LumenModule::createItem64));
    public static final DeferredItem<Item> LUMEN_CRYSTAL = ITEMS.register("lumen_crystal", tab(LumenModule::createItem64));
    public static final DeferredItem<Item> LUMEN_CAPACITOR = ITEMS.register("lumen_capacitor", tab(LumenModule::createItem64));

    public static final DeferredItem<Item> LUMESTEEL_NUGGET = ITEMS.register("lumesteel_nugget", tab(LumenModule::createItem64));
    public static final DeferredItem<Item> LUMESTEEL_INGOT = ITEMS.register("lumesteel_ingot", tab(LumenModule::createItem64));
    public static final DeferredBlock<Block> LUMESTEEL_BLOCK = BLOCKS.register("lumesteel_block", () -> new Block(BlockBehaviour.Properties.of().mapColor(MapColor.METAL).instrument(NoteBlockInstrument.IRON_XYLOPHONE).requiresCorrectToolForDrops().strength(5.0F, 6.0F).sound(SoundType.METAL)));
    public static final DeferredItem<Item> LUMESTEEL_BLOCK_ITEM = ITEMS.register("lumesteel_block", tab(() -> new BlockItem(LUMESTEEL_BLOCK.get(), Registration.createStandardProperties())));

    public static final DeferredItem<Item> RESONITE_BLEND = ITEMS.register("resonite_blend", tab(LumenModule::createItem64));

    public static final TagKey<Block> LUMENITE_ORE_BLOCK_TAG = TagTools.createBlockTagKey(ResourceLocation.fromNamespaceAndPath("c", "ores/lumenite"));
    public static final TagKey<Item> LUMENITE_ORE_ITEM_TAG = TagTools.createItemTagKey(ResourceLocation.fromNamespaceAndPath("c", "ores/lumenite"));
    public static final TagKey<Item> LUMENITE_DUST_ITEM_TAG = TagTools.createItemTagKey(ResourceLocation.fromNamespaceAndPath("c", "dusts/lumenite"));
    public static final TagKey<Item> LUMEN_CRYSTAL_ITEM_TAG = TagTools.createItemTagKey(ResourceLocation.fromNamespaceAndPath("c", "gems/lumen"));
    public static final TagKey<Item> LUMEN_CAPACITOR_ITEM_TAG = TagTools.createItemTagKey(ResourceLocation.fromNamespaceAndPath("resonantia", "capacitors/lumen"));

    public static final TagKey<Item> LUMESTEEL_NUGGET_ITEM_TAG = TagTools.createItemTagKey(ResourceLocation.fromNamespaceAndPath("c", "nuggets/lumesteel"));
    public static final TagKey<Item> LUMESTEEL_INGOT_ITEM_TAG = TagTools.createItemTagKey(ResourceLocation.fromNamespaceAndPath("c", "ingots/lumesteel"));
    public static final TagKey<Block> LUMESTEEL_BLOCK_BLOCK_TAG = TagTools.createBlockTagKey(ResourceLocation.fromNamespaceAndPath("c", "storage_blocks/lumesteel"));
    public static final TagKey<Item> LUMESTEEL_BLOCK_ITEM_TAG = TagTools.createItemTagKey(ResourceLocation.fromNamespaceAndPath("c", "storage_blocks/lumesteel"));

    public static final TagKey<Item> RESONITE_BLEND_ITEM_TAG = TagTools.createItemTagKey(ResourceLocation.fromNamespaceAndPath("c", "dusts/resonite"));

    @Override
    public void initDatagen(DataGen dataGen, HolderLookup.Provider provider) {
        dataGen.add(
                Dob.builder(LUMENITE_ORE, LUMENITE_ORE_ITEM)
                        .ironPickaxeTags()
                        .silkTouchLoot(provider, LUMENITE_DUST, 1f, 3f)
                        .blockTags(List.of(Tags.Blocks.ORES, LUMENITE_ORE_BLOCK_TAG))
                        .itemTags(List.of(Tags.Items.ORES, LUMENITE_ORE_ITEM_TAG))
                        .simpleBlockState()
                        .name("Lumenite Ore")
                        .parentedItem(),
                Dob.builder(DEEPSLATE_LUMENITE_ORE, DEEPSLATE_LUMENITE_ORE_ITEM)
                        .ironPickaxeTags()
                        .silkTouchLoot(provider, LUMENITE_DUST, 1f, 3f)
                        .blockTags(List.of(Tags.Blocks.ORES, LUMENITE_ORE_BLOCK_TAG))
                        .itemTags(List.of(Tags.Items.ORES, LUMENITE_ORE_ITEM_TAG))
                        .simpleBlockState()
                        .name("Deepslate Lumenite Ore")
                        .parentedItem(),
                Dob.itemBuilder(LUMENITE_DUST)
                        .itemTags(List.of(Tags.Items.DUSTS, LUMENITE_DUST_ITEM_TAG))
                        .name("Lumenite Dust")
                        .generatedItem("item/lumenite_dust"),
                Dob.itemBuilder(LUMEN_CRYSTAL)
                        .itemTags(List.of(Tags.Items.GEMS, LUMEN_CRYSTAL_ITEM_TAG))
                        .name("Lumen Crystal")
                        .generatedItem("item/lumen_crystal")
                        .shaped(builder -> builder
                                        .define('l', LUMENITE_DUST_ITEM_TAG)
                                        .unlockedBy("lumen_dust", has(LUMENITE_DUST_ITEM_TAG)),
                                "lll", "lol", "lll"
                        ),
                Dob.itemBuilder(LUMEN_CAPACITOR)
                        .itemTags(List.of(Registration.CAPACITORS_ITEM_TAG, LUMEN_CAPACITOR_ITEM_TAG))
                        .name("Lumen Capacitor")
                        .generatedItem("item/lumen_capacitor")
                        .shaped(builder -> builder
                                        .define('l', LUMENITE_DUST_ITEM_TAG)
                                        .define('g', Tags.Items.NUGGETS_GOLD)
                                        .define('C', Tags.Items.INGOTS_COPPER)
                                        .unlockedBy("lumen_dust", has(LUMENITE_DUST_ITEM_TAG)),
                                " gl", "gCg", "lg "
                        ),
                Dob.builder(LUMESTEEL_BLOCK, LUMESTEEL_BLOCK_ITEM)
                        .ironPickaxeTags()
                        .simpleLoot()
                        .blockTags(List.of(Tags.Blocks.STORAGE_BLOCKS, LUMESTEEL_BLOCK_BLOCK_TAG))
                        .itemTags(List.of(Tags.Items.STORAGE_BLOCKS, LUMESTEEL_BLOCK_ITEM_TAG))
                        .simpleBlockState()
                        .parentedItem()
                        .name("Lumesteel Block")
                        .shaped(builder -> builder
                                        .define('v', LUMESTEEL_INGOT_ITEM_TAG)
                                        .unlockedBy("has_lumesteel_ingot", has(LUMESTEEL_INGOT_ITEM_TAG)),
                                "vvv", "vvv", "vvv"
                        ),
                Dob.itemBuilder(LUMESTEEL_INGOT)
                        .itemTags(List.of(Tags.Items.INGOTS, LUMESTEEL_INGOT_ITEM_TAG))
                        .generatedItem("item/lumesteel_ingot")
                        .shaped(builder -> builder
                                        .define('v', LUMESTEEL_INGOT_ITEM_TAG)
                                        .unlockedBy("has_lumesteel_nugget", has(LUMESTEEL_NUGGET_ITEM_TAG)),
                                "vvv", "vvv", "vvv"
                        )
                        .name("Lumesteel Ingot")
                        .recipeConsumer(() -> (recipeOutput) -> {
                            AlloyRecipeBuilder.build(new ItemStack(LUMESTEEL_INGOT.get()),
                                    List.of(SizedIngredient.of(LUMENITE_DUST, 4), SizedIngredient.of(Tags.Items.INGOTS_IRON, 1)),
                                    3600, 0.3f, recipeOutput);
                        }),
                Dob.itemBuilder(LUMESTEEL_NUGGET)
                        .itemTags(List.of(Tags.Items.NUGGETS, LUMESTEEL_NUGGET_ITEM_TAG))
                        .generatedItem("item/lumesteel_nugget")
                        .name("Lumesteel Nugget")
                        .recipeConsumer(() -> (output) -> {
                            ShapelessRecipeBuilder
                                    .shapeless(RecipeCategory.MISC, LUMESTEEL_NUGGET, 9)
                                    .requires(LUMESTEEL_INGOT_ITEM_TAG)
                                    .unlockedBy("has_lumesteel_ingot", has(LUMESTEEL_INGOT_ITEM_TAG))
                                    .save(output);
                        }),
                Dob.itemBuilder(RESONITE_BLEND)
                        .itemTags(List.of(Tags.Items.DUSTS, RESONITE_BLEND_ITEM_TAG))
                        .generatedItem("item/resonite_blend")
                        .name("Resonite Blend")
                        .recipeConsumer(() -> (recipeOutput) -> {
                            AlloyRecipeBuilder.build(new ItemStack(RESONITE_BLEND.get()),
                                    List.of(SizedIngredient.of(RESONANT_QUARTZ_DUST_ITEM_TAG, 1), SizedIngredient.of(LUMENITE_DUST_ITEM_TAG, 1)),
                                    4800, 0.3f, recipeOutput);
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

    private static ToIntFunction<BlockState> litBlockEmission(int lightValue) {
        return (blockState) -> (Boolean) blockState.getValue(BlockStateProperties.LIT) ? lightValue : 0;
    }

    private static Item createItem64() {
        return new Item(Resonantia.setup.defaultProperties().stacksTo(64));
    }
}
