package com.kjmaster.resonantia.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

public record ProcessingMachineData(int progress) {

    public static final Codec<ProcessingMachineData> CODEC = RecordCodecBuilder.create((instance) -> instance.group(Codec.INT.fieldOf("progress").forGetter(ProcessingMachineData::progress))
            .apply(instance, (progress) -> new ProcessingMachineData(progress)));
    public static final StreamCodec<FriendlyByteBuf, ProcessingMachineData> STREAM_CODEC;

    @Override
    public int progress() {
        return progress;
    }

    public ProcessingMachineData withProgress(int progress) {
        return new ProcessingMachineData(progress);
    }

    static {
        STREAM_CODEC = StreamCodec.composite(ByteBufCodecs.INT, ProcessingMachineData::progress, (progress) -> new ProcessingMachineData(progress));
    }
}
