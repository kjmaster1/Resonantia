package com.kjmaster.resonantia.modules.smelter.blocks;

import com.kjmaster.resonantia.api.machine.ResonantMachineRole;
import com.kjmaster.resonantia.data.ProcessingMachineData;
import com.kjmaster.resonantia.data.SidedEnergyModeData;
import com.kjmaster.resonantia.tileentity.ILitOverride;
import com.kjmaster.resonantia.tileentity.ModularResonatingMachineTE;
import mcjty.lib.api.container.DefaultContainerProvider;
import mcjty.lib.api.container.IGenericContainer;
import mcjty.lib.container.ContainerFactory;
import mcjty.lib.container.GenericContainer;
import mcjty.lib.container.GenericItemHandler;
import mcjty.lib.container.SlotDefinition;
import mcjty.lib.tileentity.BaseBEData;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.neoforged.neoforge.common.util.Lazy;
import net.neoforged.neoforge.items.IItemHandler;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Supplier;

import static com.kjmaster.resonantia.Resonantia.MODID;
import static com.kjmaster.resonantia.modules.smelter.SmelterModule.CONTAINER_RESONANT_SMELTER;
import static com.kjmaster.resonantia.setup.Registration.*;
import static mcjty.lib.setup.Registration.BASE_BE_DATA;

public abstract class ResonantSmelterTE extends ModularResonatingMachineTE implements ILitOverride {

    public static final int SLOT_INPUT = 0;
    public static final int SLOT_OUTPUT = 1;

    private final RecipeManager.CachedCheck<SingleRecipeInput, ? extends SmeltingRecipe> quickCheck;

    public static final Lazy<ContainerFactory> CONTAINER_FACTORY = Lazy.of(() -> new ContainerFactory(2)
            .slot(SlotDefinition.generic().in(), SLOT_INPUT, 49, 28)
            .slot(SlotDefinition.craftResult().out(), SLOT_OUTPUT, 111, 28)
            .playerSlots(10, 70));

    private final int ENERGY_PER_TICK;
    private final int TICKS_PER_SMELT;

    private int litOverrideTicks = 0;

    public ResonantSmelterTE(BlockEntityType<?> type, BlockPos pos, BlockState state, int energyPerTick, int ticksPerSmelt) {
        super(type, pos, state);
        ENERGY_PER_TICK = energyPerTick;
        TICKS_PER_SMELT = ticksPerSmelt;
        this.quickCheck = RecipeManager.createCheck(RecipeType.SMELTING);
    }

    @Override
    protected @Nullable GenericItemHandler getItemHandler() {
        return GenericItemHandler.create(this, CONTAINER_FACTORY)
                .itemValid((slot, stack) -> slot != SLOT_INPUT || isValidSmeltingInput(stack, this))
                .build();
    }

    @Override
    protected DefaultContainerProvider<IGenericContainer> getContainerProvider() {
        DefaultContainerProvider<IGenericContainer> defaultContainerProvider = super.getContainerProvider();
        return defaultContainerProvider.data(PROCESSING_MACHINE_DATA, ProcessingMachineData.STREAM_CODEC, ProcessingMachineData.CODEC);
    }

    @Override
    protected String getContainerTitle() {
        return "Resonant Smelter";
    }

    @Override
    protected Supplier<MenuType<GenericContainer>> getMenuType() {
        return CONTAINER_RESONANT_SMELTER;
    }

    @Override
    protected Lazy<ContainerFactory> getContainerFactory() {
        return CONTAINER_FACTORY;
    }

    @Override
    public ResourceLocation getGui() {
        return ResourceLocation.fromNamespaceAndPath(MODID, "textures/gui/resonant_smelter_gui.png");
    }

    public int getTicksPerSmelt() {
        return TICKS_PER_SMELT;
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

        ItemStack input = items.getStackInSlot(SLOT_INPUT);

        if (input.isEmpty()) {
            setProgress(0);
            return;
        }

        RecipeHolder<?> recipeHolder = quickCheck.getRecipeFor(new SingleRecipeInput(input), level).orElse(null);

        if (recipeHolder == null) {
            setProgress(0);
            return;
        }

        SmeltingRecipe recipe = (SmeltingRecipe) recipeHolder.value();

        if (!recipe.matches(new SingleRecipeInput(input), level)) {
            setProgress(0);
            return;
        }

        ItemStack output = recipe.getResultItem(level.registryAccess());

        ItemStack simulated = items.insertItem(SLOT_OUTPUT, output.copy(), true);
        if (!simulated.isEmpty()) {
            return;
        }

        int energyCost = unstable ? (int) (ENERGY_PER_TICK * 1.5f) : ENERGY_PER_TICK;

        if (energyStorage.getEnergyStored() < energyCost) return;

        energyStorage.consumeEnergy(energyCost);

        setProgress(getProgress() + 1);

        if (getProgress() >= TICKS_PER_SMELT) {
            items.extractItem(SLOT_INPUT, 1, false);
            items.insertItem(SLOT_OUTPUT, output.copy(), false);
            setProgress(hasMoreWork() ? 1 : 0);
        }
    }

    private boolean hasMoreWork() {
        ItemStack input = items.getStackInSlot(SLOT_INPUT);
        if (input.isEmpty()) return false;
        RecipeHolder<?> recipeHolder = quickCheck.getRecipeFor(new SingleRecipeInput(input), level).orElse(null);
        if (recipeHolder == null) return false;
        ItemStack result = recipeHolder.value().getResultItem(level.registryAccess());
        return items.insertItem(SLOT_OUTPUT, result.copy(), true).isEmpty();
    }

    public int getProgress() {
        return this.getData(PROCESSING_MACHINE_DATA).progress();
    }

    public void setProgress(int progress) {
        ProcessingMachineData data = getData(PROCESSING_MACHINE_DATA);
        this.setData(PROCESSING_MACHINE_DATA, data.withProgress(progress));
    }

    public boolean isWorking() {
        return getProgress() > 0 && isMachineEnabled();
    }

    @Override
    public ResonantMachineRole getMachineRole() {
        return ResonantMachineRole.PROCESSOR;
    }

    private static boolean isValidSmeltingInput(ItemStack stack, ResonantSmelterTE te) {
        if (stack.isEmpty() || te.level == null) return false;
        RecipeHolder<?> recipeHolder = te.quickCheck.getRecipeFor(new SingleRecipeInput(stack), te.level).orElse(null);
        return recipeHolder != null;
    }

    @Override
    protected void applyImplicitComponents(DataComponentInput input) {
        super.applyImplicitComponents(input);
        ProcessingMachineData processingMachineData = input.get(ITEM_PROCESSING_MACHINE_DATA);
        if (processingMachineData != null) {
            setData(PROCESSING_MACHINE_DATA, processingMachineData);
        }
    }

    @Override
    protected void collectImplicitComponents(DataComponentMap.Builder components) {
        super.collectImplicitComponents(components);
        components.set(ITEM_PROCESSING_MACHINE_DATA, getData(PROCESSING_MACHINE_DATA));
    }
}
