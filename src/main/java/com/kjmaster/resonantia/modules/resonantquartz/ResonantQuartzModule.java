package com.kjmaster.resonantia.modules.resonantquartz;

import com.kjmaster.resonantia.Resonantia;
import com.kjmaster.resonantia.modules.crusher.crafting.CrushingRecipeBuilder;
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

public class ResonantQuartzModule implements IModule {

    public static final DeferredBlock<Block> RESONANT_QUARTZ_ORE = BLOCKS.register("resonant_quartz_ore", () -> new DropExperienceBlock(UniformInt.of(2, 5), BlockBehaviour.Properties.of().mapColor(MapColor.NETHER).instrument(NoteBlockInstrument.BASEDRUM).requiresCorrectToolForDrops().strength(3.0F, 3.0F).sound(SoundType.NETHER_ORE)));
    public static final DeferredItem<Item> RESONANT_QUARTZ_ORE_ITEM = ITEMS.register("resonant_quartz_ore", tab(() -> new BlockItem(RESONANT_QUARTZ_ORE.get(), Registration.createStandardProperties())));

    public static final DeferredItem<Item> RESONANT_QUARTZ = ITEMS.register("resonant_quartz", tab(ResonantQuartzModule::createItem64));
    public static final DeferredItem<Item> RESONANT_QUARTZ_DUST = ITEMS.register("resonant_quartz_dust", tab(ResonantQuartzModule::createItem64));

    public static final TagKey<Block> RESONANT_QUARTZ_ORE_BLOCK_TAG = TagTools.createBlockTagKey(ResourceLocation.fromNamespaceAndPath("c", "ores/resonant_quartz"));
    public static final TagKey<Item> RESONANT_QUARTZ_ORE_ITEM_TAG = TagTools.createItemTagKey(ResourceLocation.fromNamespaceAndPath("c", "ores/resonant_quartz"));
    public static final TagKey<Item> RESONANT_QUARTZ_ITEM_TAG = TagTools.createItemTagKey(ResourceLocation.fromNamespaceAndPath("c", "gems/resonant_quartz"));
    public static final TagKey<Item> RESONANT_QUARTZ_DUST_ITEM_TAG = TagTools.createItemTagKey(ResourceLocation.fromNamespaceAndPath("c", "dusts/resonant_quartz"));

    @Override
    public void initDatagen(DataGen dataGen, HolderLookup.Provider provider) {
        dataGen.add(
                Dob.builder(RESONANT_QUARTZ_ORE, RESONANT_QUARTZ_ORE_ITEM)
                        .name("Resonant Quartz Ore")
                        .ironPickaxeTags()
                        .silkTouchLoot(provider, RESONANT_QUARTZ, 1f, 1f)
                        .blockTags(List.of(Tags.Blocks.ORES, RESONANT_QUARTZ_ORE_BLOCK_TAG))
                        .itemTags(List.of(Tags.Items.ORES, RESONANT_QUARTZ_ORE_ITEM_TAG))
                        .simpleBlockState()
                        .parentedItem(),
                Dob.itemBuilder(RESONANT_QUARTZ)
                        .name("Resonant Quartz")
                        .itemTags(List.of(Tags.Items.GEMS, RESONANT_QUARTZ_ITEM_TAG))
                        .generatedItem("item/resonant_quartz"),
                Dob.itemBuilder(RESONANT_QUARTZ_DUST)
                        .name("Resonant Quartz Dust")
                        .itemTags(List.of(Tags.Items.DUSTS, RESONANT_QUARTZ_DUST_ITEM_TAG))
                        .generatedItem("item/resonant_quartz_dust")
                        .recipeConsumer(() -> (recipeOutput) -> {
                            CrushingRecipeBuilder.build(new ItemStack(RESONANT_QUARTZ_DUST.get(), 1),
                                    SizedIngredient.of(RESONANT_QUARTZ_ITEM_TAG, 1),
                                    3600, 0.6f, recipeOutput);
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
