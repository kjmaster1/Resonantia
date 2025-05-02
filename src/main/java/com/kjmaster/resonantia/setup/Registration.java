package com.kjmaster.resonantia.setup;

import com.kjmaster.resonantia.Resonantia;
import com.kjmaster.resonantia.api.frequency.ItemFrequency;
import com.kjmaster.resonantia.blocks.ResonantiaBlockRegistry;
import com.kjmaster.resonantia.tileentity.ResonantiaBEData;
import mcjty.lib.api.infusable.ItemInfusable;
import mcjty.lib.blocks.RBlockRegistry;
import mcjty.lib.setup.DeferredBlocks;
import mcjty.lib.setup.DeferredItems;
import mcjty.lib.tileentity.BaseBEData;
import mcjty.lib.varia.RedstoneMode;
import mcjty.lib.varia.Tools;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.*;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.levelgen.placement.PlacementModifierType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import java.util.UUID;
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
    public static final DeferredRegister<AttachmentType<?>> ATTACHMENT_TYPES = DeferredRegister.create(NeoForgeRegistries.Keys.ATTACHMENT_TYPES, MODID);
    public static final DeferredRegister.DataComponents COMPONENTS = DeferredRegister.createDataComponents(Registries.DATA_COMPONENT_TYPE, MODID);

    public static final Supplier<AttachmentType<ResonantiaBEData>> RESONANTIA_BE_DATA;
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<ResonantiaBEData>> ITEM_RESONANTIA_BE_DATA;
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<ItemFrequency>> ITEM_FREQUENCY;

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
        ATTACHMENT_TYPES.register(bus);
        COMPONENTS.register(bus);
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
        ITEM_FREQUENCY = COMPONENTS.registerComponentType("frequency", (builder) -> builder.persistent(ItemFrequency.ITEM_FREQUENCY_CODEC).networkSynchronized(ItemFrequency.ITEM_FREQUENCY_STREAM_CODEC));
    }
}
