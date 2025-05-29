package com.kjmaster.resonantia.modules.smelter.blocks;

import com.kjmaster.resonantia.modules.smelter.SmelterModule;
import com.kjmaster.resonantia.modules.smelter.crafting.AlloySmeltingRecipe;
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
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.common.util.Lazy;

import java.util.List;
import java.util.function.Supplier;

import static com.kjmaster.resonantia.Resonantia.MODID;
import static com.kjmaster.resonantia.modules.smelter.SmelterModule.CONTAINER_RESONANT_ALLOY_SMELTER;

public abstract class ResonantAlloySmelterTE extends ResonantiaProcessingMachineTE<AlloySmeltingRecipe.Input, AlloySmeltingRecipe> {

    public static final int SLOT_INPUT = 0;
    public static final int SLOT_INPUT_2 = 1;
    public static final int SLOT_OUTPUT = 2;

    public static final Lazy<ContainerFactory> CONTAINER_FACTORY = Lazy.of(() -> new ContainerFactory(3)
            .slot(SlotDefinition.generic().in(), SLOT_INPUT, 49, 12)
            .slot(SlotDefinition.generic().in(), SLOT_INPUT_2, 49, 43)
            .slot(SlotDefinition.craftResult().out(), SLOT_OUTPUT, 111, 28)
            .playerSlots(10, 70));

    private final int ENERGY_PER_TICK;

    public ResonantAlloySmelterTE(BlockEntityType<?> type, BlockPos pos, BlockState state, int energyPerTick) {
        super(type, pos, state);
        ENERGY_PER_TICK = energyPerTick;
    }

    @Override
    protected int getEnergyPerTick() {
        return ENERGY_PER_TICK;
    }

    @Override
    protected RecipeManager.CachedCheck<AlloySmeltingRecipe.Input, AlloySmeltingRecipe> getRecipeCheck() {
        return RecipeManager.createCheck(SmelterModule.ALLOY_SMELTING.type().get());
    }

    @Override
    protected AlloySmeltingRecipe.Input getRecipeInput() {
        ItemStack input = items.getStackInSlot(SLOT_INPUT);
        ItemStack input2 = items.getStackInSlot(SLOT_INPUT_2);
        return new AlloySmeltingRecipe.Input(List.of(input, input2));
    }

    @Override
    protected boolean canInsertAll(List<OutputStack> outputStacks) {
        ItemStack simulated = items.insertItem(SLOT_OUTPUT, outputStacks.getFirst().getItem(), true);
        return simulated.isEmpty();
    }

    @Override
    protected void insertAll(List<OutputStack> outputStacks) {
        items.insertItem(SLOT_OUTPUT, outputStacks.getFirst().getItem().copy(), false);
    }

    @Override
    protected void extractAll(AlloySmeltingRecipe machineRecipe) {
        items.extractItem(SLOT_INPUT, machineRecipe.inputs().getFirst().count(), false);
        items.extractItem(SLOT_INPUT_2, machineRecipe.inputs().get(SLOT_INPUT_2).count(), false);
    }

    @Override
    protected Lazy<ContainerFactory> getContainerFactory() {
        return CONTAINER_FACTORY;
    }

    @Override
    protected String getContainerTitle() {
        return "Resonant Alloy Smelter";
    }

    @Override
    protected Supplier<MenuType<GenericContainer>> getMenuType() {
        return CONTAINER_RESONANT_ALLOY_SMELTER;
    }

    @Override
    public ResourceLocation getGui() {
        return ResourceLocation.fromNamespaceAndPath(MODID, "textures/gui/resonant_alloy_smelter_gui.png");
    }
}
