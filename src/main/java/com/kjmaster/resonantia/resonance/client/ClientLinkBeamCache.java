package com.kjmaster.resonantia.resonance.client;

import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ClientLinkBeamCache {

    private static final Map<GlobalPos, List<BlockPos>> linkCache = new HashMap<>();

    public static void setLinks(GlobalPos source, List<BlockPos> targets) {
        if (targets.isEmpty()) {
            linkCache.remove(source);
        } else {
            linkCache.put(source, targets);
        }
    }

    public static List<BlockPos> getLinks(GlobalPos source) {
        return linkCache.getOrDefault(source, List.of());
    }
}
