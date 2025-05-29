package com.kjmaster.resonantia.data;

import com.mojang.serialization.Codec;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.StringRepresentable;
import net.neoforged.neoforge.network.codec.NeoForgeStreamCodecs;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.Map;

public enum Mode implements StringRepresentable {
    MODE_NONE("none", "overlay_none"),
    MODE_INPUT("input", "overlay_in"),   // Blue
    MODE_OUTPUT("output", "overlay_out"), // Yellow
    MODE_BOTH("both", "overlay_both");

    private static final Map<String, Mode> modeToMode = new HashMap<>();
    public static final Codec<Mode> CODEC = StringRepresentable.fromEnum(Mode::values);
    public static final StreamCodec<FriendlyByteBuf, Mode> STREAM_CODEC = NeoForgeStreamCodecs.enumCodec(Mode.class);
    private final String name;
    private final String overlayName;

    Mode(String name, String overlayName) {
        this.name = name;
        this.overlayName = overlayName;
    }

    public static Mode getMode(String mode) {
        return modeToMode.get(mode);
    }

    public String getOverlayName() {
        return overlayName;
    }

    @Override
    @Nonnull
    public String getSerializedName() {
        return name;
    }

    @Override
    public String toString() {
        return getSerializedName();
    }

    static {
        for (Mode mode : values()) {
            modeToMode.put(mode.getSerializedName(), mode);
        }
    }
}
