package com.kjmaster.resonantia.api.frequency;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;

public record DriftContext(ServerLevel level, BlockPos pos, float stabilizationFactor) {}
