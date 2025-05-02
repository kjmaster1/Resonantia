package com.kjmaster.resonantia.blocks;

import com.kjmaster.resonantia.tileentity.ResonantiaAnnotationHolder;
import com.kjmaster.resonantia.tileentity.ResonantiaAnnotationTools;
import com.kjmaster.resonantia.tileentity.ResonantiaTileEntity;
import com.mojang.datafixers.types.Type;
import mcjty.lib.blocks.BaseBlock;
import mcjty.lib.blocks.RBlock;
import mcjty.lib.setup.DeferredBlocks;
import mcjty.lib.setup.DeferredItems;
import mcjty.lib.tileentity.AnnotationHolder;
import mcjty.lib.tileentity.AnnotationTools;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class ResonantiaBlockRegistry {
    private final DeferredBlocks BLOCKS;
    private final DeferredItems ITEMS;
    private final DeferredRegister<BlockEntityType<?>> TILES;
    private final Consumer<Supplier<ItemStack>> tab;
    private final List<ResonantiaAnnotationHolder> resonantiaAnnotationHolders = new ArrayList();
    private final List<AnnotationHolder> holders = new ArrayList<>();

    public ResonantiaBlockRegistry(String modid, Consumer<Supplier<ItemStack>> tab) {
        this.BLOCKS = DeferredBlocks.create(modid);
        this.ITEMS = DeferredItems.create(modid);
        this.TILES = DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, modid);
        this.tab = tab;
    }

    public void register(IEventBus bus) {
        this.BLOCKS.register(bus);
        this.ITEMS.register(bus);
        this.TILES.register(bus);
    }

    public List<ResonantiaAnnotationHolder> getResonantiaAnnotationHolders() {
        return this.resonantiaAnnotationHolders;
    }

    public List<AnnotationHolder> getHolders() {
        return this.holders;
    }

    public <B extends BaseBlock, I extends BlockItem, E extends ResonantiaTileEntity> RBlock<B, I, E> registerBlock(String name, Class<E> clazz, Supplier<B> blockSupplier, @Nullable Function<Supplier<? extends Block>, I> itemSupplier, BlockEntityType.BlockEntitySupplier<E> tileSupplier) {
        DeferredBlock<B> block = this.BLOCKS.register(name, blockSupplier);
        DeferredItem<I> item;
        if (itemSupplier != null) {
            item = (DeferredItem<I>) this.ITEMS.register(name, () -> (BlockItem)itemSupplier.apply(block));
            this.tab.accept((Supplier)() -> new ItemStack((ItemLike)item.get()));
        } else {
            item = null;
        }

        DeferredHolder<BlockEntityType<?>, BlockEntityType<E>> tile = this.TILES.register(name, () -> BlockEntityType.Builder.of(tileSupplier, new Block[]{(Block)block.get()}).build((Type)null));

        AnnotationHolder holder = AnnotationTools.createAnnotationHolder(clazz, block);
        this.holders.add(holder);

        ResonantiaAnnotationHolder resonantiaAnnotationHolder = ResonantiaAnnotationTools.createAnnotationHolder(clazz, block);
        this.resonantiaAnnotationHolders.add(resonantiaAnnotationHolder);
        return new RBlock(block, item, tile);
    }

    public <B extends BaseBlock, I extends BlockItem, E extends ResonantiaTileEntity> RBlock<B, I, E> registerBlockWIP(String name, Class<E> clazz, Supplier<B> blockSupplier, Function<Supplier<? extends Block>, I> itemSupplier, BlockEntityType.BlockEntitySupplier<E> tileSupplier) {
        DeferredBlock<B> block = this.BLOCKS.register(name, blockSupplier);
        DeferredItem<I> item = (DeferredItem<I>) this.ITEMS.register(name, () -> (BlockItem)itemSupplier.apply(block));
        DeferredHolder<BlockEntityType<?>, BlockEntityType<E>> tile = this.TILES.register(name, () -> BlockEntityType.Builder.of(tileSupplier, new Block[]{(Block)block.get()}).build((Type)null));

        AnnotationHolder holder = AnnotationTools.createAnnotationHolder(clazz, block);
        this.holders.add(holder);

        ResonantiaAnnotationHolder resonantiaAnnotationHolder = ResonantiaAnnotationTools.createAnnotationHolder(clazz, block);
        this.resonantiaAnnotationHolders.add(resonantiaAnnotationHolder);
        return new RBlock(block, item, tile);
    }

    public <T extends Item> DeferredItem<T> registerItem(String name, Supplier<T> itemSupplier) {
        DeferredItem<T> item = this.ITEMS.register(name, itemSupplier);
        this.tab.accept((Supplier)() -> new ItemStack((ItemLike)item.get()));
        return item;
    }

    public <T extends Item> DeferredItem<T> registerItemWIP(String name, Supplier<T> itemSupplier) {
        DeferredItem<T> item = this.ITEMS.register(name, itemSupplier);
        return item;
    }
}
