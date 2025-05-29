package com.kjmaster.resonantia.modules.pump.blocks;

import com.kjmaster.resonantia.Resonantia;
import com.kjmaster.resonantia.api.machine.ResonantMachineRole;
import com.kjmaster.resonantia.data.Mode;
import com.kjmaster.resonantia.tileentity.ILitOverride;
import com.kjmaster.resonantia.tileentity.ModularResonatingMachineTE;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import mcjty.lib.container.ContainerFactory;
import mcjty.lib.container.GenericContainer;
import mcjty.lib.varia.CustomTank;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.SectionPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.IntArrayTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.BucketPickup;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.chunk.status.ChunkStatus;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.neoforge.common.util.Lazy;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.FluidType;
import net.neoforged.neoforge.fluids.FluidUtil;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Supplier;

import static com.kjmaster.resonantia.Resonantia.MODID;
import static com.kjmaster.resonantia.modules.pump.PumpModule.CONTAINER_RESONANT_PUMP;

/**
 * <a href="https://github.com/mekanism/Mekanism/blob/1.21.x/src/main/java/mekanism/common/tile/machine/TileEntityElectricPump.java#L75">Uses pump logic from Mekanism</a>
 */

public abstract class ResonantPumpTE extends ModularResonatingMachineTE implements ILitOverride {

    /**
     * How many ticks it takes to run an operation.
     */
    private final int BASE_TICKS_REQUIRED;
    private final int BASE_OUTPUT_RATE;

    private final int ENERGY_PER_TICK;

    @NotNull
    private FluidStack activeType = FluidStack.EMPTY;
    public int ticksRequired;

    public int operatingTicks;
    private int outputRate;

    /**
     * The nodes that have full sources near them or in them
     */
    private final Set<BlockPos> recurringNodes = new ObjectOpenHashSet<>();

    public static final Lazy<ContainerFactory> CONTAINER_FACTORY = Lazy.of(() -> new ContainerFactory(0)
            .playerSlots(10, 70));

    private int litOverrideTicks = 0;
    private boolean isPumping = false;

    public ResonantPumpTE(BlockEntityType<?> type, BlockPos pos, BlockState state,
                          int energyPerTick, int ticksRequired, int outputRate) {
        super(type, pos, state);
        ENERGY_PER_TICK = energyPerTick;
        BASE_TICKS_REQUIRED = ticksRequired;
        BASE_OUTPUT_RATE = outputRate;
        this.ticksRequired = BASE_TICKS_REQUIRED;
        this.outputRate = outputRate;
    }

    @Override
    protected Lazy<ContainerFactory> getContainerFactory() {
        return CONTAINER_FACTORY;
    }

    @Override
    protected String getContainerTitle() {
        return "Resonant Pump";
    }

    @Override
    protected Supplier<MenuType<GenericContainer>> getMenuType() {
        return CONTAINER_RESONANT_PUMP;
    }

    @Override
    public ResourceLocation getGui() {
        return ResourceLocation.fromNamespaceAndPath(MODID, "textures/gui/resonant_pump_gui.png");
    }

    @Override
    public ResonantMachineRole getMachineRole() {
        return ResonantMachineRole.PROCESSOR;
    }

    @Override
    protected void tickServer() {
        super.tickServer();

        if (!isMachineEnabled() && isPumping) {
            isPumping = false;
        }

        boolean lit = isPumping || litOverrideTicks > 0;
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

        for (Direction direction : Direction.values()) {
            Mode mode = getFluidMode(direction);
            if (mode == Mode.MODE_BOTH || mode == Mode.MODE_OUTPUT) {
                Optional<IFluidHandler> optionalIFluidHandler = FluidUtil.getFluidHandler(level, worldPosition.relative(direction), direction.getOpposite());
                System.out.println(direction + ":" + optionalIFluidHandler);
                optionalIFluidHandler.ifPresent(fluidHandler -> {
                    System.out.println(FluidUtil.tryFluidTransfer(fluidHandler, this.fluidHandler, outputRate, true));
                });
            }
        }

        int energyCost = unstable ? (int) (ENERGY_PER_TICK * 1.5f) : ENERGY_PER_TICK;

        if (fluidHandler.isEmpty() || fluidHandler.getSpace() >= FluidType.BUCKET_VOLUME) {
            if (energyStorage.getEnergyStored() >= energyCost) {
                boolean activeTypeEmpty = activeType.isEmpty();
                if (!activeTypeEmpty) {
                    energyStorage.consumeEnergy(energyCost);
                    isPumping = true;
                }
                operatingTicks++;
                if (operatingTicks >= ticksRequired) {
                    operatingTicks = 0;
                    if (suck()) {
                        isPumping = true;
                        if (activeTypeEmpty) {
                            energyStorage.consumeEnergy(energyCost);
                        }
                    } else {
                        reset();
                    }
                }
            } else {
                isPumping = false;
            }
        } else {
            isPumping = false;
        }
    }

    private boolean suck() {
        //First see if there are any fluid blocks under the pump - if so, suck and adds the location to the recurring list
        if (suck(worldPosition.relative(Direction.DOWN), true)) {
            return true;
        }
        //Even though we can add to recurring in the above for loop, we always then exit and don't get to here if we did so
        List<BlockPos> tempPumpList = new ArrayList<>(recurringNodes);
        Collections.shuffle(tempPumpList);
        //Finally, go over the recurring list of nodes and see if there is a fluid block available to suck - if not, will iterate around the recurring block, attempt to suck,
        //and then add the adjacent block to the recurring list
        BlockPos.MutableBlockPos mutable = new BlockPos.MutableBlockPos();
        for (BlockPos tempPumpPos : tempPumpList) {
            if (suck(tempPumpPos, false)) {
                return true;
            }
            //Add all the blocks surrounding this recurring node to the recurring node list
            for (Direction orientation : Direction.values()) {
                mutable.setWithOffset(tempPumpPos, orientation);
                if (distanceBetween(worldPosition, mutable) <= 80) {
                    if (suck(mutable, true)) {
                        return true;
                    }
                }
            }
            recurringNodes.remove(tempPumpPos);
        }
        return false;
    }

    private boolean suck(BlockPos pos, boolean addRecurring) {
        //Note: we get the block state from the world so that we can get the proper block in case it is fluid logged
        Optional<BlockState> state = getBlockState(level, pos);
        if (state.isPresent()) {
            BlockState blockState = state.get();
            FluidState fluidState = blockState.getFluidState();
            if (!fluidState.isEmpty() && fluidState.isSource()) {
                //Just in case someone does weird things and has a fluid state that is empty and a source
                // only allow collecting from non-empty sources
                Block block = blockState.getBlock();
                if (block instanceof BucketPickup bucketPickup) {
                    Fluid sourceFluid = fluidState.getType();
                    FluidStack fluidStack = getOutput(sourceFluid);
                    if (validFluid(fluidStack)) {
                        //If it can be picked up by a bucket, and we actually want to pick it up, do so to update the fluid type we are doing
                        if (shouldPump(level, sourceFluid)) {
                            //Note we only attempt taking if it is not water, or we want to pump water sources
                            // otherwise we assume the type from the fluid state is correct
                            ItemStack pickedUpStack = bucketPickup.pickupBlock(null, level, pos, blockState);
                            if (pickedUpStack.isEmpty()) {
                                //Couldn't actually pick it up, exit
                                return false;
                            } else if (pickedUpStack.getItem() instanceof BucketItem bucket) {
                                //This isn't the best validation check given it may not return a bucket, but it is good enough for now
                                sourceFluid = bucket.content;
                                //Update the fluid stack in case something somehow changed about the type
                                // making sure that we replace to heavy water if we got heavy water
                                fluidStack = getOutput(sourceFluid);
                                if (!validFluid(fluidStack)) {
                                    Resonantia.LOGGER.warn("Fluid removed without successfully picking up. Fluid {} at {} in {} was valid, but after picking up was {}.",
                                            fluidState.getType(), pos, level, sourceFluid);
                                    return false;
                                }
                            }
                        }

                        suck(fluidStack, pos, addRecurring);
                        return true;
                    }
                }
                //Otherwise, we do not know how to drain from the block, or it is not valid, and we shouldn't take it so don't handle it
            }
        }
        return false;
    }

    public void reset() {
        isPumping = false;
        activeType = FluidStack.EMPTY;
        recurringNodes.clear();
    }

    private boolean shouldPump(Level level, Fluid sourceFluid) {
        if (sourceFluid == Fluids.WATER) {
            //If we don't pump infinite sources, only pump it if water conversion is turned off
            return !level.getGameRules().getBoolean(GameRules.RULE_WATER_SOURCE_CONVERSION);
        } else if (sourceFluid == Fluids.LAVA) {
            //If we don't pump infinite sources, only pump it if lava conversion is turned off
            return !level.getGameRules().getBoolean(GameRules.RULE_LAVA_SOURCE_CONVERSION);
        }
        return true;
    }

    private void suck(@NotNull FluidStack fluidStack, BlockPos pos, boolean addRecurring) {
        //Size doesn't matter, but we do want to take the NBT into account
        activeType = fluidStack.copyWithAmount(1);
        if (addRecurring) {
            pos = pos.immutable();
            recurringNodes.add(pos);
        }
        int amountOffered = fluidStack.getAmount();
        if (insert(fluidHandler, fluidStack, IFluidHandler.FluidAction.EXECUTE).getAmount() != amountOffered) {
            level.gameEvent(null, GameEvent.FLUID_PICKUP, pos);
        }
    }

    private boolean validFluid(@NotNull FluidStack fluidStack) {
        if (!fluidStack.isEmpty() && (activeType.isEmpty() || FluidStack.isSameFluidSameComponents(activeType, fluidStack))) {
            if (fluidHandler.isEmpty()) {
                return true;
            } else if (FluidStack.isSameFluidSameComponents(fluidHandler.getFluid(), fluidStack)) {
                return fluidStack.getAmount() <= fluidHandler.getSpace();
            }
        }
        return false;
    }

    private FluidStack insert(CustomTank tank, FluidStack stack, IFluidHandler.FluidAction action) {

        if (stack.isEmpty() || !tank.isFluidValid(stack)) {
            //"Fail quick" if the given stack is empty, or we can never insert the item or currently are unable to insert it
            return stack;
        }
        int needed = tank.getSpace();
        if (needed <= 0) {
            //Fail if we are a full tank
            return stack;
        }
        boolean sameType = false;
        if (tank.isEmpty() || (sameType = FluidStack.isSameFluidSameComponents(stack, tank.getFluid()))) {
            int toAdd = Math.min(stack.getAmount(), needed);
            if (action.execute()) {
                //If we want to actually insert the fluid, then update the current fluid
                if (sameType) {
                    //We can just grow our stack by the amount we want to increase it
                    // Note: this also will mark that the contents changed
                    growStack(tank, toAdd, action);
                } else {
                    //If we are not the same type then we have to copy the stack and set it
                    // Note: this also will mark that the contents changed
                    tank.setFluid(stack.copyWithAmount(toAdd));
                }
            }
            return stack.copyWithAmount(stack.getAmount() - toAdd);
        }
        //If we didn't accept this fluid, then just return the given stack
        return stack;
    }

    private int growStack(CustomTank tank, int amount, IFluidHandler.FluidAction action) {
        int current = tank.getFluidAmount();
        if (current == 0) {
            //"Fail quick" if our stack is empty, so we can't grow it
            return 0;
        } else if (amount > 0) {
            //Cap adding amount at how much we need, so that we don't risk integer overflow
            amount = Math.min(amount, tank.getSpace());
        }
        int newSize = setStackSize(tank, current + amount, action);
        return newSize - current;
    }

    private int setStackSize(CustomTank tank, int amount, IFluidHandler.FluidAction action) {
        if (tank.isEmpty()) {
            return 0;
        } else if (amount <= 0) {
            if (action.execute()) {
                tank.setFluid(FluidStack.EMPTY);
            }
            return 0;
        }
        int maxStackSize = tank.getCapacity();
        if (amount > maxStackSize) {
            amount = maxStackSize;
        }
        if (tank.getFluidAmount() == amount || action.simulate()) {
            //If our size is not changing, or we are only simulating the change, don't do anything
            return amount;
        }
        tank.setFluid(tank.getFluid().copyWithAmount(amount));
        return amount;
    }


    private FluidStack getOutput(Fluid sourceFluid) {
        return new FluidStack(sourceFluid, FluidType.BUCKET_VOLUME);
    }

    public static double distanceBetween(BlockPos start, BlockPos end) {
        return Math.sqrt(start.distSqr(end));
    }

    /**
     * Gets a blockstate if the location is loaded
     *
     * @param world world
     * @param pos   position
     * @return optional containing the blockstate if found, empty optional if not loaded
     */
    @NotNull
    public static Optional<BlockState> getBlockState(@Nullable BlockGetter world, @NotNull BlockPos pos) {
        if (!isBlockLoaded(world, pos)) {
            //If the world is null, or it is a world reader and the block is not loaded, return empty
            return Optional.empty();
        }
        return Optional.of(world.getBlockState(pos));
    }

    /**
     * Checks if a position is in bounds of the world, and is loaded
     *
     * @param world world
     * @param pos   position
     * @return True if the position is loaded or the given world is of a superclass of IWorldReader that does not have a concept of being loaded.
     */
    @Contract("null, _ -> false")
    public static boolean isBlockLoaded(@Nullable BlockGetter world, @NotNull BlockPos pos) {
        if (world == null) {
            return false;
        } else if (world instanceof LevelReader reader) {
            if (reader instanceof Level level && !level.isInWorldBounds(pos)) {
                return false;
            }
            //TODO: If any cases come up where things are behaving oddly due to the change from reader.hasChunkAt(pos)
            // re-evaluate this and if the specific case is being handled properly
            return isChunkLoaded(reader, pos);
        }
        return true;
    }

    /**
     * Checks if the chunk at the given position is loaded but does not validate the position is in bounds of the world.
     *
     * @param world world
     * @param pos   position
     * @see #isBlockLoaded(BlockGetter, BlockPos)
     */
    @Contract("null, _ -> false")
    public static boolean isChunkLoaded(@Nullable LevelReader world, @NotNull BlockPos pos) {
        return isChunkLoaded(world, SectionPos.blockToSectionCoord(pos.getX()), SectionPos.blockToSectionCoord(pos.getZ()));
    }

    /**
     * Checks if the chunk at the given position is loaded.
     *
     * @param world  world
     * @param chunkX Chunk X coordinate
     * @param chunkZ Chunk Z coordinate
     */
    @Contract("null, _, _ -> false")
    public static boolean isChunkLoaded(@Nullable LevelReader world, int chunkX, int chunkZ) {
        if (world == null) {
            return false;
        } else if (world instanceof LevelAccessor accessor) {
            if (!(accessor instanceof Level level) || !level.isClientSide) {
                return accessor.hasChunk(chunkX, chunkZ);
            }
            //Don't allow the client level to just return true for all cases, as we actually care if it is present
            // and instead use the fallback logic that we have
        }
        return world.getChunk(chunkX, chunkZ, ChunkStatus.FULL, false) != null;
    }

    public static void setFluidStackIfPresent(HolderLookup.Provider provider, CompoundTag nbt, String key, Consumer<FluidStack> setter) {
        if (nbt.contains(key, Tag.TAG_COMPOUND)) {
            setter.accept(FluidStack.parseOptional(provider, nbt.getCompound(key)));
        }
    }

    public static IntArrayTag writeBlockPositions(Collection<BlockPos> positions) {
        int[] list = new int[3 * positions.size()];
        int i = 0;
        for (BlockPos pos : positions) {
            list[i++] = pos.getX();
            list[i++] = pos.getY();
            list[i++] = pos.getZ();
        }
        return new IntArrayTag(list);
    }

    public static void readBlockPositions(CompoundTag nbt, String key, Collection<BlockPos> positions) {
        if (nbt.contains(key, Tag.TAG_INT_ARRAY)) {
            int[] list = nbt.getIntArray(key);
            if (list.length % 3 == 0) {
                for (int i = 0; i < list.length; ) {
                    positions.add(new BlockPos(list[i++], list[i++], list[i++]));
                }
            }
        }
    }

    @Override
    protected void saveAdditional(CompoundTag nbtTags, HolderLookup.Provider provider) {
        super.saveAdditional(nbtTags, provider);
        nbtTags.putInt("progress", operatingTicks);
        if (!activeType.isEmpty()) {
            nbtTags.put("fluid", activeType.save(provider));
        }
        if (!recurringNodes.isEmpty()) {
            nbtTags.put("recurring_nodes", writeBlockPositions(recurringNodes));
        }
    }

    @Override
    protected void loadAdditional(CompoundTag nbt, HolderLookup.Provider provider) {
        super.loadAdditional(nbt, provider);
        operatingTicks = nbt.getInt("progress");
        setFluidStackIfPresent(provider, nbt, "fluid", fluid -> activeType = fluid);
        readBlockPositions(nbt, "recurring_nodes", recurringNodes);
    }



    @Override
    public void removeComponentsFromTag(CompoundTag tag) {
        super.removeComponentsFromTag(tag);
        tag.remove("recurring_nodes");
    }
}
