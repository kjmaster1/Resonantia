package com.kjmaster.resonantia.modules.stabilizer.blocks;

import com.kjmaster.resonantia.api.frequency.DriftContext;
import com.kjmaster.resonantia.modules.stabilizer.StabilizerModule;
import com.kjmaster.resonantia.modules.stabilizer.crafting.StabilizingRecipe;
import com.kjmaster.resonantia.recipes.OutputStack;
import com.kjmaster.resonantia.setup.Registration;
import com.kjmaster.resonantia.tileentity.ResonantiaProcessingMachineTE;
import mcjty.lib.container.ContainerFactory;
import mcjty.lib.container.GenericContainer;
import mcjty.lib.container.SlotDefinition;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.common.util.Lazy;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Supplier;

import static com.kjmaster.resonantia.Resonantia.MODID;
import static com.kjmaster.resonantia.modules.stabilizer.StabilizerModule.CONTAINER_RESONANT_STABILIZER;

public abstract class ResonantStabilizerTE extends ResonantiaProcessingMachineTE<SingleRecipeInput, StabilizingRecipe> {

    public static final int SLOT_INPUT = 0;
    private final int ENERGY_PER_TICK;

    public static final Lazy<ContainerFactory> CONTAINER_FACTORY = Lazy.of(() -> new ContainerFactory(1)
            .slot(SlotDefinition.specific((stack -> stack.is(Registration.STABILIZERS_ITEM_TAG))).in(), SLOT_INPUT, 82, 24)
            .playerSlots(10, 70));

    public ResonantStabilizerTE(BlockEntityType<?> type, BlockPos pos, BlockState state, int energyPerTick) {
        super(type, pos, state);
        ENERGY_PER_TICK = energyPerTick;
    }

    public abstract float getStabilizationFactor();

    public boolean isStabilizing() {
        return isMachineEnabled() && !items.getStackInSlot(SLOT_INPUT).isEmpty();
    }

    @Override
    protected RecipeManager.CachedCheck<SingleRecipeInput, StabilizingRecipe> getRecipeCheck() {
        return RecipeManager.createCheck(StabilizerModule.STABILIZING.type().get());
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
        return true;
    }

    @Override
    protected void insertAll(List<OutputStack> outputStacks) {
    }

    @Override
    protected void extractAll(StabilizingRecipe machineRecipe) {
        items.extractItem(SLOT_INPUT, machineRecipe.input().count(), false);
    }

    @Override
    protected Lazy<ContainerFactory> getContainerFactory() {
        return CONTAINER_FACTORY;
    }

    @Override
    protected String getContainerTitle() {
        return "Resonant Stabilizer";
    }

    @Override
    protected Supplier<MenuType<GenericContainer>> getMenuType() {
        return CONTAINER_RESONANT_STABILIZER;
    }

    @Override
    public @Nullable ResourceLocation getGui() {
        return ResourceLocation.fromNamespaceAndPath(MODID, "textures/gui/resonant_stabilizer_gui.png");
    }
}
