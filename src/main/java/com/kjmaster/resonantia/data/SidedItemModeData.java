package com.kjmaster.resonantia.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Direction;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

public record SidedItemModeData(
        Mode itemModeUp,
        Mode itemModeDown,
        Mode itemModeNorth,
        Mode itemModeSouth,
        Mode itemModeWest,
        Mode itemModeEast
) {
    public static final Codec<SidedItemModeData> CODEC = RecordCodecBuilder.create((instance) -> instance.group(
            Mode.CODEC.fieldOf("itemModeUp").forGetter(SidedItemModeData::itemModeUp),
            Mode.CODEC.fieldOf("itemModeDown").forGetter(SidedItemModeData::itemModeDown),
            Mode.CODEC.fieldOf("itemModeNorth").forGetter(SidedItemModeData::itemModeNorth),
            Mode.CODEC.fieldOf("itemModeSouth").forGetter(SidedItemModeData::itemModeSouth),
            Mode.CODEC.fieldOf("itemModeWest").forGetter(SidedItemModeData::itemModeWest),
            Mode.CODEC.fieldOf("itemModeEast").forGetter(SidedItemModeData::itemModeEast)
    ).apply(instance, (SidedItemModeData::new)));
    public static final StreamCodec<FriendlyByteBuf, SidedItemModeData> STREAM_CODEC;

    public Mode getItemModeFromDirection(Direction direction) {
        switch (direction) {
            case UP -> {
                return itemModeUp;
            }
            case DOWN -> {
                return itemModeDown;
            }
            case NORTH ->
            {
                return itemModeNorth;
            }
            case SOUTH ->
            {
                return itemModeSouth;
            }
            case WEST ->
            {
                return itemModeWest;
            }
            case EAST ->
            {
                return itemModeEast;
            }
            case null, default -> {
                return Mode.MODE_NONE;
            }
        }
    }

    public SidedItemModeData withItemModeUp(Mode mode) {
        return new SidedItemModeData(mode, itemModeDown, itemModeNorth, itemModeSouth, itemModeWest, itemModeEast);
    }

    public SidedItemModeData withItemModeDown(Mode mode) {
        return new SidedItemModeData(itemModeUp, mode, itemModeNorth, itemModeSouth, itemModeWest, itemModeEast);
    }

    public SidedItemModeData withItemModeNorth(Mode mode) {
        return new SidedItemModeData(itemModeUp, itemModeDown, mode, itemModeSouth, itemModeWest, itemModeEast);
    }

    public SidedItemModeData withItemModeSouth(Mode mode) {
        return new SidedItemModeData(itemModeUp, itemModeDown, itemModeNorth, mode, itemModeWest, itemModeEast);
    }

    public SidedItemModeData withItemModeWest(Mode mode) {
        return new SidedItemModeData(itemModeUp, itemModeDown, itemModeNorth, itemModeSouth, mode, itemModeEast);
    }

    public SidedItemModeData withItemModeEast(Mode mode) {
        return new SidedItemModeData(itemModeUp, itemModeDown, itemModeNorth, itemModeSouth, itemModeWest, mode);
    }

    @Override
    public Mode itemModeUp() {
        return itemModeUp;
    }

    @Override
    public Mode itemModeDown() {
        return itemModeDown;
    }

    @Override
    public Mode itemModeNorth() {
        return itemModeNorth;
    }

    @Override
    public Mode itemModeWest() {
        return itemModeWest;
    }

    @Override
    public Mode itemModeSouth() {
        return itemModeSouth;
    }

    @Override
    public Mode itemModeEast() {
        return itemModeEast;
    }

    static {
        STREAM_CODEC = StreamCodec.composite(
                Mode.STREAM_CODEC, SidedItemModeData::itemModeUp,
                Mode.STREAM_CODEC, SidedItemModeData::itemModeDown,
                Mode.STREAM_CODEC, SidedItemModeData::itemModeNorth,
                Mode.STREAM_CODEC, SidedItemModeData::itemModeSouth,
                Mode.STREAM_CODEC, SidedItemModeData::itemModeWest,
                Mode.STREAM_CODEC, SidedItemModeData::itemModeEast,
                (SidedItemModeData::new)
        );
    }
}
