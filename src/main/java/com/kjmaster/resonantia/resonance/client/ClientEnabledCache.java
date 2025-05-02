package com.kjmaster.resonantia.resonance.client;

import net.minecraft.core.GlobalPos;

import java.util.HashMap;
import java.util.Map;

public class ClientEnabledCache {

    private static final Map<GlobalPos, Boolean> enabledCache = new HashMap<>();

    public static void setEnabled(GlobalPos source, boolean enabled) {
        enabledCache.put(source, enabled);
    }

    public static Boolean getEnabled(GlobalPos source) {
        return enabledCache.getOrDefault(source, Boolean.FALSE);
    }
}

