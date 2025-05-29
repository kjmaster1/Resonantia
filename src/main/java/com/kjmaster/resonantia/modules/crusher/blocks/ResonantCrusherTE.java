package com.kjmaster.resonantia.modules.crusher.blocks;

import com.kjmaster.resonantia.modules.crusher.CrusherModule;
import com.kjmaster.resonantia.modules.crusher.crafting.CrushingRecipe;
import com.kjmaster.resonantia.recipes.OutputStack;
import com.kjmaster.resonantia.tileentity.ResonantiaProcessingMachineTE;
import mcjty.lib.container.ContainerFactory;
import mcjty.lib.container.GenericContainer;
import mcjty.lib.container.SlotDefinition;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.common.util.Lazy;

import java.util.List;
import java.util.function.Supplier;

import static com.kjmaster.resonantia.Resonantia.MODID;
import static com.kjmaster.resonantia.modules.crusher.CrusherModule.CONTAINER_RESONANT_CRUSHER;

public abstract class ResonantCrusherTE extends ResonantiaProcessingMachineTE<SingleRecipeInput, CrushingRecipe> {

    public static final int SLOT_INPUT = 0;
    public static final int SLOT_OUTPUT = 1;

    public static final Lazy<ContainerFactory> CONTAINER_FACTORY = Lazy.of(() -> new ContainerFactory(3)
            .slot(SlotDefinition.generic().in(), SLOT_INPUT, 49, 28)
            .slot(SlotDefinition.craftResult().out(), SLOT_OUTPUT, 111, 28)
            .playerSlots(10, 70));

    private final int ENERGY_PER_TICK;

    public ResonantCrusherTE(BlockEntityType<?> type, BlockPos pos, BlockState state, int energyPerTick) {
        super(type, pos, state);
        ENERGY_PER_TICK = energyPerTick;
    }

    @Override
    protected RecipeManager.CachedCheck<SingleRecipeInput, CrushingRecipe> getRecipeCheck() {
        return RecipeManager.createCheck(CrusherModule.CRUSHING.type().get());
    }

    @Override
    protected SingleRecipeInput getRecipeInput() {
        return new SingleRecipeInput(items.getStackInSlot(SLOT_INPUT));
    }

    @Override
    protected int getEnergyPerTick() {
        return ENERGY_PER_TICK;
    }

    @Override
    protected boolean canInsertAll(List<OutputStack> outputStacks) {
        ItemStack simulated = items.insertItem(SLOT_OUTPUT, outputStacks.getFirst().getItem(), true);
        return simulated.isEmpty();
    }

    @Override
    protected void insertAll(List<OutputStack> outputStacks) {
        items.insertItem(SLOT_OUTPUT, outputStacks.getFirst().getItem(), false);
    }

    @Override
    protected void extractAll(CrushingRecipe machineRecipe) {
        items.extractItem(SLOT_INPUT, machineRecipe.input().count(), false);
    }

    @Override
    protected Lazy<ContainerFactory> getContainerFactory() {
        return CONTAINER_FACTORY;
    }

    @Override
    protected String getContainerTitle() {
        return "Resonant Crusher";
    }

    @Override
    protected Supplier<MenuType<GenericContainer>> getMenuType() {
        return CONTAINER_RESONANT_CRUSHER;
    }

    @Override
    public ResourceLocation getGui() {
        return ResourceLocation.fromNamespaceAndPath(MODID, "textures/gui/resonant_crusher_gui.png");
    }
}
