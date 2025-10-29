package com.kjmaster.resonantia.setup;

import com.kjmaster.resonantia.Resonantia;
import com.kjmaster.resonantia.api.frequency.ItemFrequency;
import com.kjmaster.resonantia.blocks.ResonantiaBlockRegistry;
import com.kjmaster.resonantia.crafting.MachineRecipeData;
import com.kjmaster.resonantia.data.*;
import com.kjmaster.resonantia.recipes.RecipeTypeSerializerPair;
import com.kjmaster.resonantia.tileentity.ResonantiaBEData;
import mcjty.lib.setup.DeferredBlocks;
import mcjty.lib.setup.DeferredItems;
import mcjty.lib.varia.TagTools;
import mcjty.lib.varia.Tools;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.*;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.levelgen.placement.PlacementModifierType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.client.model.data.ModelProperty;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import java.util.function.Supplier;

import static com.kjmaster.resonantia.Resonantia.MODID;

public class Registration {

    public static final ResonantiaBlockRegistry RBLOCKS = new ResonantiaBlockRegistry(MODID, Resonantia.setup::addTabItem);
    public static final DeferredBlocks BLOCKS = DeferredBlocks.create(MODID);
    public static final DeferredItems ITEMS = DeferredItems.create(MODID);
    public static final DeferredRegister<BlockEntityType<?>> TILES = DeferredRegister.create(BuiltInRegistries.BLOCK_ENTITY_TYPE, MODID);
    public static final DeferredRegister<MenuType<?>> CONTAINERS = DeferredRegister.create(BuiltInRegistries.MENU, MODID);
    public static final DeferredRegister<SoundEvent> SOUNDS = DeferredRegister.create(BuiltInRegistries.SOUND_EVENT, MODID);
    public static final DeferredRegister<EntityType<?>> ENTITIES = DeferredRegister.create(BuiltInRegistries.ENTITY_TYPE, MODID);
    public static final DeferredRegister<PlacementModifierType<?>> PLACEMENT_MODIFIERS = Tools.createPlacementRegistry(MODID);
    public static final DeferredRegister<CreativeModeTab> TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MODID);
    public static final DeferredRegister<RecipeSerializer<?>> RECIPE_SERIALIZERS = DeferredRegister.create(Registries.RECIPE_SERIALIZER, MODID);
    public static final DeferredRegister<RecipeType<?>> RECIPE_TYPES = DeferredRegister.create(Registries.RECIPE_TYPE, MODID);
    public static final DeferredRegister<AttachmentType<?>> ATTACHMENT_TYPES = DeferredRegister.create(NeoForgeRegistries.Keys.ATTACHMENT_TYPES, MODID);
    public static final DeferredRegister.DataComponents COMPONENTS = DeferredRegister.createDataComponents(Registries.DATA_COMPONENT_TYPE, MODID);

    public static final Supplier<AttachmentType<ResonantiaBEData>> RESONANTIA_BE_DATA;
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<ResonantiaBEData>> ITEM_RESONANTIA_BE_DATA;
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<ItemFrequency>> ITEM_FREQUENCY;

    public static final Supplier<AttachmentType<SidedEnergyModeData>> SIDED_ENERGY_MODE_DATA;
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<SidedEnergyModeData>> ITEM_SIDED_ENERGY_MODE_DATA;

    public static final Supplier<AttachmentType<SidedItemModeData>> SIDED_ITEM_MODE_DATA;
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<SidedItemModeData>> ITEM_SIDED_ITEM_MODE_DATA;

    public static final Supplier<AttachmentType<SidedFluidModeData>> SIDED_FLUID_MODE_DATA;
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<SidedFluidModeData>> ITEM_SIDED_FLUID_MODE_DATA;

    public static final DeferredHolder<AttachmentType<?>, AttachmentType<ProcessingMachineData>> PROCESSING_MACHINE_DATA;
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<ProcessingMachineData>> ITEM_PROCESSING_MACHINE_DATA;

    public static final DeferredHolder<AttachmentType<?>, AttachmentType<MachineRecipeData>> MACHINE_RECIPE_DATA;
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<MachineRecipeData>> ITEM_MACHINE_RECIPE_DATA;

    public static final TagKey<Item> GLOBULES_ITEM_TAG;
    public static final TagKey<Item> CAPACITORS_ITEM_TAG;
    public static final TagKey<Item> FRAGMENTS_ITEM_TAG;
    public static final TagKey<Item> STABILIZERS_ITEM_TAG;

    public static final ModelProperty<Mode> ENERGY_NORTH = new ModelProperty<>();
    public static final ModelProperty<Mode> ENERGY_SOUTH = new ModelProperty<>();
    public static final ModelProperty<Mode> ENERGY_WEST = new ModelProperty<>();
    public static final ModelProperty<Mode> ENERGY_EAST = new ModelProperty<>();
    public static final ModelProperty<Mode> ENERGY_UP = new ModelProperty<>();
    public static final ModelProperty<Mode> ENERGY_DOWN = new ModelProperty<>();

    public static final ModelProperty<Mode> ITEM_NORTH = new ModelProperty<>();
    public static final ModelProperty<Mode> ITEM_SOUTH = new ModelProperty<>();
    public static final ModelProperty<Mode> ITEM_WEST = new ModelProperty<>();
    public static final ModelProperty<Mode> ITEM_EAST = new ModelProperty<>();
    public static final ModelProperty<Mode> ITEM_UP = new ModelProperty<>();
    public static final ModelProperty<Mode> ITEM_DOWN = new ModelProperty<>();

    public static final ModelProperty<Mode> FLUID_NORTH = new ModelProperty<>();
    public static final ModelProperty<Mode> FLUID_SOUTH = new ModelProperty<>();
    public static final ModelProperty<Mode> FLUID_WEST = new ModelProperty<>();
    public static final ModelProperty<Mode> FLUID_EAST = new ModelProperty<>();
    public static final ModelProperty<Mode> FLUID_UP = new ModelProperty<>();
    public static final ModelProperty<Mode> FLUID_DOWN = new ModelProperty<>();

    public static void register(IEventBus bus) {
        RBLOCKS.register(bus);
        BLOCKS.register(bus);
        ITEMS.register(bus);
        TILES.register(bus);
        CONTAINERS.register(bus);
        SOUNDS.register(bus);
        ENTITIES.register(bus);
        PLACEMENT_MODIFIERS.register(bus);
        TABS.register(bus);
        RECIPE_SERIALIZERS.register(bus);
        RECIPE_TYPES.register(bus);
        ATTACHMENT_TYPES.register(bus);
        COMPONENTS.register(bus);
    }

    public static <R extends Recipe<?>, S extends RecipeSerializer<? extends R>> RecipeTypeSerializerPair<R, S> registerRecipePair(
            String name, Supplier<S> serializerFactory) {
        var type = RECIPE_TYPES.<RecipeType<R>>register(name, () -> RecipeType.simple(ResourceLocation.fromNamespaceAndPath(MODID, name)));
        var serializer = RECIPE_SERIALIZERS.register(name, serializerFactory);
        return new RecipeTypeSerializerPair<>(type, serializer);
    }

    public static Item.Properties createStandardProperties() {
        return Resonantia.setup.defaultProperties();
    }

    public static Supplier<CreativeModeTab> TAB = TABS.register(MODID, () -> CreativeModeTab.builder()
            .title(Component.translatable("itemGroup." + MODID))
            .icon(() -> new ItemStack(Items.STICK))
            .withTabsBefore(CreativeModeTabs.SPAWN_EGGS)
            .displayItems((featureFlags, output) -> {
                Resonantia.setup.populateTab(output);
            })
            .build());

    static {
        RESONANTIA_BE_DATA = ATTACHMENT_TYPES.register("resonantia_be_data", () -> AttachmentType.builder(() -> new ResonantiaBEData(false)).serialize(ResonantiaBEData.CODEC).build());
        ITEM_RESONANTIA_BE_DATA = COMPONENTS.registerComponentType("resonantia_be_data", (builder) -> builder.persistent(ResonantiaBEData.CODEC).networkSynchronized(ResonantiaBEData.STREAM_CODEC));

        SIDED_ENERGY_MODE_DATA = ATTACHMENT_TYPES.register("sided_energy_mode_data", () -> AttachmentType.builder(() -> new SidedEnergyModeData(Mode.MODE_NONE, Mode.MODE_NONE, Mode.MODE_NONE, Mode.MODE_NONE, Mode.MODE_NONE, Mode.MODE_NONE)).serialize(SidedEnergyModeData.CODEC).build());
        ITEM_SIDED_ENERGY_MODE_DATA = COMPONENTS.registerComponentType("sided_energy_mode_data", (builder) -> builder.persistent(SidedEnergyModeData.CODEC).networkSynchronized(SidedEnergyModeData.STREAM_CODEC));

        SIDED_ITEM_MODE_DATA = ATTACHMENT_TYPES.register("sided_item_mode_data", () -> AttachmentType.builder(() -> new SidedItemModeData(Mode.MODE_NONE, Mode.MODE_NONE, Mode.MODE_NONE, Mode.MODE_NONE, Mode.MODE_NONE, Mode.MODE_NONE)).serialize(SidedItemModeData.CODEC).build());
        ITEM_SIDED_ITEM_MODE_DATA = COMPONENTS.registerComponentType("sided_item_mode_data", (builder) -> builder.persistent(SidedItemModeData.CODEC).networkSynchronized(SidedItemModeData.STREAM_CODEC));

        SIDED_FLUID_MODE_DATA = ATTACHMENT_TYPES.register("sided_fluid_mode_data", () -> AttachmentType.builder(() -> new SidedFluidModeData(Mode.MODE_NONE, Mode.MODE_NONE, Mode.MODE_NONE, Mode.MODE_NONE, Mode.MODE_NONE, Mode.MODE_NONE)).serialize(SidedFluidModeData.CODEC).build());
        ITEM_SIDED_FLUID_MODE_DATA = COMPONENTS.registerComponentType("sided_fluid_mode_data", (builder) -> builder.persistent(SidedFluidModeData.CODEC).networkSynchronized(SidedFluidModeData.STREAM_CODEC));

        PROCESSING_MACHINE_DATA = ATTACHMENT_TYPES.register("processing_machine_data", () -> AttachmentType.builder(() -> new ProcessingMachineData(0)).serialize(ProcessingMachineData.CODEC).build());
        ITEM_PROCESSING_MACHINE_DATA = COMPONENTS.registerComponentType("processing_machine_data", builder -> builder.persistent(ProcessingMachineData.CODEC).networkSynchronized(ProcessingMachineData.STREAM_CODEC));

        MACHINE_RECIPE_DATA = ATTACHMENT_TYPES.register("machine_recipe_data", () -> AttachmentType.builder(() -> new MachineRecipeData(0)).serialize(MachineRecipeData.CODEC).build());
        ITEM_MACHINE_RECIPE_DATA = COMPONENTS.registerComponentType("machine_recipe_data", builder -> builder.persistent(MachineRecipeData.CODEC).networkSynchronized(MachineRecipeData.STREAM_CODEC));

        ITEM_FREQUENCY = COMPONENTS.registerComponentType("frequency", (builder) -> builder.persistent(ItemFrequency.ITEM_FREQUENCY_CODEC).networkSynchronized(ItemFrequency.ITEM_FREQUENCY_STREAM_CODEC));

        GLOBULES_ITEM_TAG = TagTools.createItemTagKey(ResourceLocation.fromNamespaceAndPath("resonantia", "globules"));
        CAPACITORS_ITEM_TAG = TagTools.createItemTagKey(ResourceLocation.fromNamespaceAndPath("resonantia", "capacitors"));
        FRAGMENTS_ITEM_TAG = TagTools.createItemTagKey(ResourceLocation.fromNamespaceAndPath("resonantia", "fragments"));
        STABILIZERS_ITEM_TAG = TagTools.createItemTagKey(ResourceLocation.fromNamespaceAndPath("resonantia", "stabilizers"));
    }
}
