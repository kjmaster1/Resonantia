package com.kjmaster.resonantia.tileentity;

import com.kjmaster.resonantia.api.machine.ResonantMachineRole;
import com.kjmaster.resonantia.crafting.MachineRecipe;
import com.kjmaster.resonantia.crafting.MachineRecipeData;
import com.kjmaster.resonantia.data.ProcessingMachineData;
import com.kjmaster.resonantia.recipes.OutputStack;
import mcjty.lib.api.container.DefaultContainerProvider;
import mcjty.lib.api.container.IGenericContainer;
import mcjty.lib.container.GenericItemHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;

import static com.kjmaster.resonantia.setup.Registration.MACHINE_RECIPE_DATA;
import static com.kjmaster.resonantia.setup.Registration.PROCESSING_MACHINE_DATA;

public abstract class ResonantiaProcessingMachineTE<I extends RecipeInput, R extends MachineRecipe<I>>
        extends ModularResonatingMachineTE implements ILitOverride {

    public final RecipeManager.CachedCheck<I, R> quickCheck;

    private int litOverrideTicks = 0;

    public ResonantiaProcessingMachineTE(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
        this.quickCheck = getRecipeCheck();
    }

    protected abstract RecipeManager.CachedCheck<I, R> getRecipeCheck();

    protected abstract I getRecipeInput();

    protected abstract int getEnergyPerTick();

    protected abstract boolean canInsertAll(List<OutputStack> outputStacks);

    protected abstract void insertAll(List<OutputStack> outputStacks);

    protected abstract void extractAll(R machineRecipe);

    @Override
    protected DefaultContainerProvider<IGenericContainer> getContainerProvider() {
        DefaultContainerProvider<IGenericContainer> defaultContainerProvider = super.getContainerProvider();
        defaultContainerProvider
                .data(PROCESSING_MACHINE_DATA, ProcessingMachineData.STREAM_CODEC, ProcessingMachineData.CODEC)
                .data(MACHINE_RECIPE_DATA, MachineRecipeData.STREAM_CODEC, MachineRecipeData.CODEC);
        return defaultContainerProvider;
    }

    @Override
    protected @Nullable GenericItemHandler getItemHandler() {
        return GenericItemHandler.create(this, getContainerFactory()).build();
    }

    @Override
    protected void tickServer() {
        super.tickServer();
        if (level == null || level.isClientSide()) return;

        boolean lit = isWorking() || litOverrideTicks > 0;
        BlockState state = level.getBlockState(worldPosition);

        if (state.getValue(BlockStateProperties.LIT) != lit) {
            level.setBlock(worldPosition, state.setValue(BlockStateProperties.LIT, lit), Block.UPDATE_ALL);
            setChanged(level, worldPosition, state);
        }

        if (litOverrideTicks > 0) {
            litOverrideTicks--;
        }
    }

    @Override
    public void applyLitOverride(int ticks) {
        litOverrideTicks = Math.max(litOverrideTicks, ticks);
    }

    @Override
    public void performMachineOperations(List<BlockPos> linkedMachines, boolean unstable) {
        if (level == null || level.isClientSide()) return;
        I input = getRecipeInput();

        if (input.isEmpty()) {
            setProgress(0);
            setEnergyCost(0);
            return;
        }

        Optional<? extends RecipeHolder<R>> holder = quickCheck.getRecipeFor(input, level);

        if (holder.isEmpty()) {
            setProgress(0);
            setEnergyCost(0);
            return;
        }

        R recipe = holder.get().value();

        if (!recipe.matches(input, level)) {
            setProgress(0);
            setEnergyCost(0);
            return;
        }

        List<OutputStack> outputStacks = recipe.craft(input, level.registryAccess());
        setEnergyCost(recipe.getEnergyCost(input));

        if (!canInsertAll(outputStacks)) return;

        int energyCost = unstable ? (int) (getEnergyPerTick() * 1.5f) : getEnergyPerTick();
        if (energyStorage.getEnergyStored() < energyCost) return;

        energyStorage.consumeEnergy(energyCost);
        setProgress(getProgress() + energyCost);

        if (getProgress() >= recipe.getEnergyCost(input)) {
            extractAll(recipe);
            insertAll(outputStacks);
            setProgress(hasMoreWork() ? 1 : 0);
        }
    }

    public boolean hasMoreWork() {
        I input = getRecipeInput();
        if (input.isEmpty()) return false;

        Optional<? extends RecipeHolder<R>> holder = quickCheck.getRecipeFor(input, level);
        if (holder.isEmpty()) return false;

        R recipe = holder.get().value();
        List<OutputStack> outputs = recipe.craft(input, level.registryAccess());
        return canInsertAll(outputs);
    }

    public int getProgress() {
        return getData(PROCESSING_MACHINE_DATA).progress();
    }

    public void setProgress(int progress) {
        setData(PROCESSING_MACHINE_DATA, getData(PROCESSING_MACHINE_DATA).withProgress(progress));
    }

    public int getEnergyCost() {
        return getData(MACHINE_RECIPE_DATA).energyCost();
    }

    public void setEnergyCost(int energyCost) {
        setData(MACHINE_RECIPE_DATA, getData(MACHINE_RECIPE_DATA).withEnergyCost(energyCost));
    }

    public boolean isWorking() {
        return getProgress() > 0 && isMachineEnabled() && energyStorage.getEnergyStored() > 0;
    }

    @Override
    public ResonantMachineRole getMachineRole() {
        return ResonantMachineRole.PROCESSOR;
    }
}
