package com.kjmaster.resonantia.api.frequency;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

public record ItemFrequency(int frequency) {
    public static final Codec<ItemFrequency> ITEM_FREQUENCY_CODEC = RecordCodecBuilder.create((instance) -> instance.group(Codec.INT.fieldOf("frequency").forGetter(ItemFrequency::frequency)).apply(instance, ItemFrequency::new));
    public static final StreamCodec<ByteBuf, ItemFrequency> ITEM_FREQUENCY_STREAM_CODEC;

    public ItemFrequency(int frequency) {
        this.frequency = frequency;
    }

    public int frequency() {
        return this.frequency;
    }

    static {
        ITEM_FREQUENCY_STREAM_CODEC = StreamCodec.composite(ByteBufCodecs.INT, ItemFrequency::frequency, ItemFrequency::new);
    }
}
