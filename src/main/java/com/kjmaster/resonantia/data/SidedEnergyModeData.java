package com.kjmaster.resonantia.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Direction;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

public record SidedEnergyModeData(
        Mode energyModeUp,
        Mode energyModeDown,
        Mode energyModeNorth,
        Mode energyModeSouth,
        Mode energyModeWest,
        Mode energyModeEast
) {
    public static final Codec<SidedEnergyModeData> CODEC = RecordCodecBuilder.create((instance) -> instance.group(
            Mode.CODEC.fieldOf("energyModeUp").forGetter(SidedEnergyModeData::energyModeUp),
            Mode.CODEC.fieldOf("energyModeDown").forGetter(SidedEnergyModeData::energyModeDown),
            Mode.CODEC.fieldOf("energyModeNorth").forGetter(SidedEnergyModeData::energyModeNorth),
            Mode.CODEC.fieldOf("energyModeSouth").forGetter(SidedEnergyModeData::energyModeSouth),
            Mode.CODEC.fieldOf("energyModeWest").forGetter(SidedEnergyModeData::energyModeWest),
            Mode.CODEC.fieldOf("energyModeEast").forGetter(SidedEnergyModeData::energyModeEast)
    ).apply(instance, (SidedEnergyModeData::new)));
    public static final StreamCodec<FriendlyByteBuf, SidedEnergyModeData> STREAM_CODEC;

    public Mode getEnergyModeFromDirection(Direction direction) {
        switch (direction) {
            case UP -> {
                return energyModeUp;
            }
            case DOWN -> {
                return energyModeDown;
            }
            case NORTH ->
            {
                return energyModeNorth;
            }
            case SOUTH ->
            {
                return energyModeSouth;
            }
            case WEST ->
            {
                return energyModeWest;
            }
            case EAST ->
            {
                return energyModeEast;
            }
            case null, default -> {
                return Mode.MODE_NONE;
            }
        }
    }

    public SidedEnergyModeData withEnergyModeUp(Mode mode) {
        return new SidedEnergyModeData(mode, energyModeDown, energyModeNorth, energyModeSouth, energyModeWest, energyModeEast);
    }

    public SidedEnergyModeData withEnergyModeDown(Mode mode) {
        return new SidedEnergyModeData(energyModeUp, mode, energyModeNorth, energyModeSouth, energyModeWest, energyModeEast);
    }

    public SidedEnergyModeData withEnergyModeNorth(Mode mode) {
        return new SidedEnergyModeData(energyModeUp, energyModeDown, mode, energyModeSouth, energyModeWest, energyModeEast);
    }

    public SidedEnergyModeData withEnergyModeSouth(Mode mode) {
        return new SidedEnergyModeData(energyModeUp, energyModeDown, energyModeNorth, mode, energyModeWest, energyModeEast);
    }

    public SidedEnergyModeData withEnergyModeWest(Mode mode) {
        return new SidedEnergyModeData(energyModeUp, energyModeDown, energyModeNorth, energyModeSouth, mode, energyModeEast);
    }

    public SidedEnergyModeData withEnergyModeEast(Mode mode) {
        return new SidedEnergyModeData(energyModeUp, energyModeDown, energyModeNorth, energyModeSouth, energyModeWest, mode);
    }

    @Override
    public Mode energyModeUp() {
        return energyModeUp;
    }

    @Override
    public Mode energyModeDown() {
        return energyModeDown;
    }

    @Override
    public Mode energyModeNorth() {
        return energyModeNorth;
    }

    @Override
    public Mode energyModeWest() {
        return energyModeWest;
    }

    @Override
    public Mode energyModeSouth() {
        return energyModeSouth;
    }

    @Override
    public Mode energyModeEast() {
        return energyModeEast;
    }

    static {
        STREAM_CODEC = StreamCodec.composite(
                Mode.STREAM_CODEC, SidedEnergyModeData::energyModeUp,
                Mode.STREAM_CODEC, SidedEnergyModeData::energyModeDown,
                Mode.STREAM_CODEC, SidedEnergyModeData::energyModeNorth,
                Mode.STREAM_CODEC, SidedEnergyModeData::energyModeSouth,
                Mode.STREAM_CODEC, SidedEnergyModeData::energyModeWest,
                Mode.STREAM_CODEC, SidedEnergyModeData::energyModeEast,
                (SidedEnergyModeData::new)
        );
    }
}
