package com.kjmaster.resonantia.resonance.client;

import net.minecraft.core.GlobalPos;

import java.util.HashSet;
import java.util.Set;

public class ClientEnabledCache {

    private static final Set<GlobalPos> enabledSet = new HashSet<>();

    public static void setEnabled(GlobalPos source, boolean enabled) {
        if (enabled) {
            enabledSet.add(source);
        } else {
            enabledSet.remove(source);
        }
    }

    public static boolean getEnabled(GlobalPos source) {
        return enabledSet.contains(source);
    }
}

