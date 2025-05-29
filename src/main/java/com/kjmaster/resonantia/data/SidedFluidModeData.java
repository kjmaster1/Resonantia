package com.kjmaster.resonantia.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Direction;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

public record SidedFluidModeData(
        Mode fluidModeUp,
        Mode fluidModeDown,
        Mode fluidModeNorth,
        Mode fluidModeSouth,
        Mode fluidModeWest,
        Mode fluidModeEast
) {
    public static final Codec<SidedFluidModeData> CODEC = RecordCodecBuilder.create((instance) -> instance.group(
            Mode.CODEC.fieldOf("fluidModeUp").forGetter(SidedFluidModeData::fluidModeUp),
            Mode.CODEC.fieldOf("fluidModeDown").forGetter(SidedFluidModeData::fluidModeDown),
            Mode.CODEC.fieldOf("fluidModeNorth").forGetter(SidedFluidModeData::fluidModeNorth),
            Mode.CODEC.fieldOf("fluidModeSouth").forGetter(SidedFluidModeData::fluidModeSouth),
            Mode.CODEC.fieldOf("fluidModeWest").forGetter(SidedFluidModeData::fluidModeWest),
            Mode.CODEC.fieldOf("fluidModeEast").forGetter(SidedFluidModeData::fluidModeEast)
    ).apply(instance, (SidedFluidModeData::new)));
    public static final StreamCodec<FriendlyByteBuf, SidedFluidModeData> STREAM_CODEC;

    public Mode getFluidModeFromDirection(Direction direction) {
        switch (direction) {
            case UP -> {
                return fluidModeUp;
            }
            case DOWN -> {
                return fluidModeDown;
            }
            case NORTH ->
            {
                return fluidModeNorth;
            }
            case SOUTH ->
            {
                return fluidModeSouth;
            }
            case WEST ->
            {
                return fluidModeWest;
            }
            case EAST ->
            {
                return fluidModeEast;
            }
            case null, default -> {
                return Mode.MODE_NONE;
            }
        }
    }

    public SidedFluidModeData withFluidModeUp(Mode mode) {
        return new SidedFluidModeData(mode, fluidModeDown, fluidModeNorth, fluidModeSouth, fluidModeWest, fluidModeEast);
    }

    public SidedFluidModeData withFluidModeDown(Mode mode) {
        return new SidedFluidModeData(fluidModeUp, mode, fluidModeNorth, fluidModeSouth, fluidModeWest, fluidModeEast);
    }

    public SidedFluidModeData withFluidModeNorth(Mode mode) {
        return new SidedFluidModeData(fluidModeUp, fluidModeDown, mode, fluidModeSouth, fluidModeWest, fluidModeEast);
    }

    public SidedFluidModeData withFluidModeSouth(Mode mode) {
        return new SidedFluidModeData(fluidModeUp, fluidModeDown, fluidModeNorth, mode, fluidModeWest, fluidModeEast);
    }

    public SidedFluidModeData withFluidModeWest(Mode mode) {
        return new SidedFluidModeData(fluidModeUp, fluidModeDown, fluidModeNorth, fluidModeSouth, mode, fluidModeEast);
    }

    public SidedFluidModeData withFluidModeEast(Mode mode) {
        return new SidedFluidModeData(fluidModeUp, fluidModeDown, fluidModeNorth, fluidModeSouth, fluidModeWest, mode);
    }

    @Override
    public Mode fluidModeUp() {
        return fluidModeUp;
    }

    @Override
    public Mode fluidModeDown() {
        return fluidModeDown;
    }

    @Override
    public Mode fluidModeNorth() {
        return fluidModeNorth;
    }

    @Override
    public Mode fluidModeWest() {
        return fluidModeWest;
    }

    @Override
    public Mode fluidModeSouth() {
        return fluidModeSouth;
    }

    @Override
    public Mode fluidModeEast() {
        return fluidModeEast;
    }

    static {
        STREAM_CODEC = StreamCodec.composite(
                Mode.STREAM_CODEC, SidedFluidModeData::fluidModeUp,
                Mode.STREAM_CODEC, SidedFluidModeData::fluidModeDown,
                Mode.STREAM_CODEC, SidedFluidModeData::fluidModeNorth,
                Mode.STREAM_CODEC, SidedFluidModeData::fluidModeSouth,
                Mode.STREAM_CODEC, SidedFluidModeData::fluidModeWest,
                Mode.STREAM_CODEC, SidedFluidModeData::fluidModeEast,
                (SidedFluidModeData::new)
        );
    }
}
