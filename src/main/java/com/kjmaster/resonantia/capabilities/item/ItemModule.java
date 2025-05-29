package com.kjmaster.resonantia.capabilities.item;

import com.kjmaster.resonantia.capabilities.ICapabilityModule;
import com.kjmaster.resonantia.data.Mode;
import com.kjmaster.resonantia.data.SidedItemModeData;
import com.kjmaster.resonantia.tileentity.ModularResonatingMachineTE;
import mcjty.lib.api.container.DefaultContainerProvider;
import mcjty.lib.api.container.IGenericContainer;
import mcjty.lib.container.GenericItemHandler;
import mcjty.lib.setup.Registration;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.client.model.data.ModelData;
import net.neoforged.neoforge.common.util.Lazy;
import net.neoforged.neoforge.items.IItemHandler;

import java.util.function.BiFunction;

import static com.kjmaster.resonantia.setup.Registration.*;

public class ItemModule implements ICapabilityModule<GenericItemHandler, IItemHandler, SidedItemModeData> {

    private final GenericItemHandler masterItemHandler;
    private Lazy<SidedItemHandler>[] sidedItemHandlers;
    private final AttachmentType<SidedItemModeData> sidedModeDataAttachment;

    @SuppressWarnings("unchecked")
    public ItemModule(GenericItemHandler itemHandler, AttachmentType<SidedItemModeData> sidedModeDataAttachment) {
        this.masterItemHandler = itemHandler;
        this.sidedModeDataAttachment = sidedModeDataAttachment;
        if (this.masterItemHandler != null) {
            this.sidedItemHandlers = (Lazy<SidedItemHandler>[]) new Lazy[Direction.values().length];
        }
    }

    @Override
    public void initializeSidedHandlers(ModularResonatingMachineTE tileEntity, GenericItemHandler masterHandler) {
        if (!isActive() || masterHandler == null) return;
        for (Direction dir : Direction.values()) {
            this.sidedItemHandlers[dir.ordinal()] = Lazy.of(() -> new SidedItemHandler(tileEntity, masterHandler, dir));
        }
    }

    @Override
    public boolean isActive() {
        return this.masterItemHandler != null;
    }

    @Override
    public IItemHandler getSidedHandler(ModularResonatingMachineTE tileEntity, Direction facing) {
        if (!isActive()) return null;
        if (facing == null) {
            return masterItemHandler;
        }
        Mode mode = getMode(tileEntity, facing);
        if (mode != Mode.MODE_NONE && sidedItemHandlers != null && sidedItemHandlers[facing.ordinal()] != null) {
            return sidedItemHandlers[facing.ordinal()].get();
        }
        return null;
    }

    @Override
    public GenericItemHandler getMasterHandler() {
        return masterItemHandler;
    }

    @Override
    public Mode getMode(ModularResonatingMachineTE tileEntity, Direction side) {
        if (!isActive()) return Mode.MODE_NONE;
        SidedItemModeData data = tileEntity.getData(this.sidedModeDataAttachment);
        return data.getItemModeFromDirection(side);
    }

    @Override
    public void setMode(ModularResonatingMachineTE tileEntity, Mode mode, Direction side) {
        if (!isActive()) return;
        SidedItemModeData oldData = tileEntity.getData(this.sidedModeDataAttachment);
        SidedItemModeData newData;

        switch (side) {
            case UP -> newData = oldData.withItemModeUp(mode);
            case DOWN -> newData = oldData.withItemModeDown(mode);
            case NORTH -> newData = oldData.withItemModeNorth(mode);
            case SOUTH -> newData = oldData.withItemModeSouth(mode);
            case WEST -> newData = oldData.withItemModeWest(mode);
            case EAST -> newData = oldData.withItemModeEast(mode);
            default -> {
                return;
            }
        }

        if (oldData != newData) {
            tileEntity.setData(this.sidedModeDataAttachment, newData);
            tileEntity.onDataChanged(this.sidedModeDataAttachment, oldData, newData);
        }
    }

    @Override
    public AttachmentType<SidedItemModeData> getSidedModeDataAttachmentType() {
        return this.sidedModeDataAttachment;
    }

    @Override
    public void onSidedModeDataChanged(Object oldData, Object newData, ModularResonatingMachineTE tileEntity) {
        if (tileEntity.getLevel() == null || tileEntity.getLevel().isClientSide()) return;
        for (Direction dir : Direction.values()) {
            if (((SidedItemModeData) newData).getItemModeFromDirection(dir) != ((SidedItemModeData) oldData).getItemModeFromDirection(dir)) {
                tileEntity.invalidateCapabilities();
                tileEntity.markDirtyClient();
                tileEntity.getLevel().updateNeighborsAt(tileEntity.getBlockPos(), tileEntity.getBlockState().getBlock());
                break;
            }
        }
    }

    @Override
    public void configureContainerProvider(DefaultContainerProvider<IGenericContainer> provider, ModularResonatingMachineTE tileEntity) {
        if (!isActive()) return;
        provider.itemHandler(() -> this.masterItemHandler)
                .data(() -> this.sidedModeDataAttachment, SidedItemModeData.STREAM_CODEC, SidedItemModeData.CODEC);
    }

    @Override
    public void loadClientData(CompoundTag tag, HolderLookup.Provider registries, ModularResonatingMachineTE tileEntity) {
        if (!isActive()) return;
        if (tag.contains("sided_item_mode_data")) {
            SidedItemModeData data = SidedItemModeData.CODEC.parse(NbtOps.INSTANCE, tag.getCompound("sided_item_mode_data")).getOrThrow();
            tileEntity.setData(this.sidedModeDataAttachment, data); // Use this.sidedModeDataAttachment
            tileEntity.requestModelDataUpdate();
        }
    }

    @Override
    public void saveClientData(CompoundTag tag, HolderLookup.Provider registries, ModularResonatingMachineTE tileEntity) {
        if (!isActive()) return;
        tag.put("sided_item_mode_data", SidedItemModeData.CODEC.encodeStart(NbtOps.INSTANCE, tileEntity.getData(this.sidedModeDataAttachment)).getOrThrow());
    }

    @Override
    public void loadAdditional(CompoundTag tag, HolderLookup.Provider registries, ModularResonatingMachineTE tileEntity) {
        if (!isActive()) return;
        this.masterItemHandler.load(tag, "items", registries);
    }

    @Override
    public void saveAdditional(CompoundTag tag, HolderLookup.Provider registries, ModularResonatingMachineTE tileEntity) {
        if (!isActive()) return;
        this.masterItemHandler.save(tag, "items", registries);
    }

    @Override
    public void applyImplicitComponents(BlockEntity.DataComponentInput input, ModularResonatingMachineTE tileEntity) {
        if (!isActive()) return;
        this.masterItemHandler.applyImplicitComponents(input.get(Registration.ITEM_INVENTORY));
        SidedItemModeData itemModeData = input.get(ITEM_SIDED_ITEM_MODE_DATA);
        if (itemModeData != null) {
            tileEntity.setData(this.sidedModeDataAttachment, itemModeData);
        }
    }

    @Override
    public void collectImplicitComponents(DataComponentMap.Builder components, ModularResonatingMachineTE tileEntity) {
        if (!isActive()) return;
        this.masterItemHandler.collectImplicitComponents(components);
        components.set(ITEM_SIDED_ITEM_MODE_DATA, tileEntity.getData(this.sidedModeDataAttachment));
    }

    @Override
    public void onDataPacket(CompoundTag tag, ModularResonatingMachineTE tileEntity, Object oldData) {
        if (!tag.isEmpty() && tag.contains("sided_item_mode_data")) {
            SidedItemModeData newData = SidedItemModeData.CODEC.parse(NbtOps.INSTANCE, tag.getCompound("sided_item_mode_data")).getOrThrow();
            for (Direction dir : Direction.values()) {
                if (newData.getItemModeFromDirection(dir) != ((SidedItemModeData) oldData).getItemModeFromDirection(dir)) {
                    tileEntity.requestModelDataUpdate();
                    if (tileEntity.getLevel() == null) return;
                    tileEntity.invalidateCapabilities();
                    tileEntity.getLevel().sendBlockUpdated(tileEntity.getBlockPos(), tileEntity.getBlockState(), tileEntity.getBlockState(), Block.UPDATE_ALL);
                    break;
                }
            }
        }
    }

    @Override
    public void populateModelData(ModelData.Builder builder, ModularResonatingMachineTE tileEntity) {
        if (!isActive()) return;
        builder.with(ITEM_NORTH, this.getMode(tileEntity, Direction.NORTH));
        builder.with(ITEM_SOUTH, this.getMode(tileEntity, Direction.SOUTH));
        builder.with(ITEM_WEST, this.getMode(tileEntity, Direction.WEST));
        builder.with(ITEM_EAST, this.getMode(tileEntity, Direction.EAST));
        builder.with(ITEM_UP, this.getMode(tileEntity, Direction.UP));
        builder.with(ITEM_DOWN, this.getMode(tileEntity, Direction.DOWN));
    }

    @Override
    public BiFunction<ModularResonatingMachineTE, Object, IItemHandler> getCapabilityProviderFunction() {
        return (tile, context) -> getSidedHandler(tile, (Direction) context);
    }
}
