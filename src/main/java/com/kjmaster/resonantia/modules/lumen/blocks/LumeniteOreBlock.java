package com.kjmaster.resonantia.modules.lumen.blocks;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RedstoneTorchBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

public class LumeniteOreBlock extends Block {

    public static final MapCodec<LumeniteOreBlock> CODEC = simpleCodec(LumeniteOreBlock::new);
    public static final BooleanProperty LIT;

    public MapCodec<LumeniteOreBlock> codec() {
        return CODEC;
    }

    public LumeniteOreBlock(BlockBehaviour.Properties properties) {
        super(properties);
        this.registerDefaultState(this.defaultBlockState().setValue(LIT, false));
    }

    protected void attack(BlockState state, Level level, BlockPos pos, Player player) {
        interact(state, level, pos);
        super.attack(state, level, pos, player);
    }

    public void stepOn(Level level, BlockPos pos, BlockState state, Entity entity) {
        if (!entity.isSteppingCarefully()) {
            interact(state, level, pos);
        }

        super.stepOn(level, pos, state, entity);
    }

    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        if (level.isClientSide) {
            spawnParticles(level, pos);
        } else {
            interact(state, level, pos);
        }

        return stack.getItem() instanceof BlockItem && (new BlockPlaceContext(player, hand, stack, hitResult)).canPlace() ? ItemInteractionResult.SKIP_DEFAULT_BLOCK_INTERACTION : ItemInteractionResult.SUCCESS;
    }

    private static void interact(BlockState state, Level level, BlockPos pos) {
        spawnParticles(level, pos);
        if (!(Boolean) state.getValue(LIT)) {
            level.setBlock(pos, state.setValue(LIT, true), 3);
        }

    }

    protected boolean isRandomlyTicking(BlockState state) {
        return (Boolean) state.getValue(LIT);
    }

    protected void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        if (state.getValue(LIT)) {
            level.setBlock(pos, state.setValue(LIT, false), 3);
        }

    }

    protected void spawnAfterBreak(BlockState state, ServerLevel level, BlockPos pos, ItemStack stack, boolean dropExperience) {
        super.spawnAfterBreak(state, level, pos, stack, dropExperience);
    }

    public int getExpDrop(BlockState state, LevelAccessor level, BlockPos pos, @Nullable BlockEntity blockEntity, @Nullable Entity breaker, ItemStack tool) {
        return UniformInt.of(1, 5).sample(level.getRandom());
    }

    public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
        if (state.getValue(LIT)) {
            spawnParticles(level, pos);
        }

    }

    private static void spawnParticles(Level level, BlockPos pos) {
        RandomSource randomsource = level.random;

        for (Direction direction : Direction.values()) {
            BlockPos blockpos = pos.relative(direction);
            if (!level.getBlockState(blockpos).isSolidRender(level, blockpos)) {
                Direction.Axis direction$axis = direction.getAxis();
                double d1 = direction$axis == Direction.Axis.X ? (double) 0.5F + (double) 0.5625F * (double) direction.getStepX() : (double) randomsource.nextFloat();
                double d2 = direction$axis == Direction.Axis.Y ? (double) 0.5F + (double) 0.5625F * (double) direction.getStepY() : (double) randomsource.nextFloat();
                double d3 = direction$axis == Direction.Axis.Z ? (double) 0.5F + (double) 0.5625F * (double) direction.getStepZ() : (double) randomsource.nextFloat();
                level.addParticle(new DustParticleOptions(Vec3.fromRGB24(16727354).toVector3f(), 1.0f), (double) pos.getX() + d1, (double) pos.getY() + d2, (double) pos.getZ() + d3, 0.0F, 0.0F, 0.0F);
            }
        }

    }

    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(LIT);
    }

    static {
        LIT = RedstoneTorchBlock.LIT;
    }
}
