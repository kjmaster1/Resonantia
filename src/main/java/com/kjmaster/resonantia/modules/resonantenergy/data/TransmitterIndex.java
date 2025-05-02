package com.kjmaster.resonantia.modules.resonantenergy.data;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;

import java.util.*;

public class TransmitterIndex {
    private static final Map<DimensionAndChunk, Set<BlockPos>> INDEX = new HashMap<>();

    public static void add(ServerLevel level, BlockPos pos) {
        INDEX.computeIfAbsent(DimensionAndChunk.of(level, pos), k -> new HashSet<>()).add(pos);
    }

    public static void remove(ServerLevel level, BlockPos pos) {
        DimensionAndChunk key = DimensionAndChunk.of(level, pos);
        Set<BlockPos> positions = INDEX.get(key);
        if (positions != null) {
            positions.remove(pos);
            if (positions.isEmpty()) {
                INDEX.remove(key);
            }
        }
    }

    public static Set<BlockPos> get(ServerLevel level, ChunkPos chunkPos) {
        return INDEX.getOrDefault(DimensionAndChunk.of(level, chunkPos), Collections.emptySet());
    }

    private record DimensionAndChunk(ResourceKey<Level> dimension, ChunkPos chunkPos) {
        static DimensionAndChunk of(ServerLevel level, BlockPos pos) {
            return new DimensionAndChunk(level.dimension(), new ChunkPos(pos));
        }

        static DimensionAndChunk of(ServerLevel level, ChunkPos chunkPos) {
            return new DimensionAndChunk(level.dimension(), chunkPos);
        }
    }
}
