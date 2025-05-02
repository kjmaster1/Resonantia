package com.kjmaster.resonantia.tileentity;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

public record ResonantiaBEData(boolean unstable) {

    public static final Codec<ResonantiaBEData> CODEC = RecordCodecBuilder.create((instance) -> instance.group(Codec.BOOL.fieldOf("unstable").forGetter(ResonantiaBEData::unstable))
            .apply(instance, (unstable) -> new ResonantiaBEData(unstable)));
    public static final StreamCodec<FriendlyByteBuf, ResonantiaBEData> STREAM_CODEC;

    @Override
    public boolean unstable() {
        return unstable;
    }

    public ResonantiaBEData withUnstable(boolean unstable) {
        return new ResonantiaBEData(unstable);
    }

    static {
        STREAM_CODEC = StreamCodec.composite(ByteBufCodecs.BOOL, ResonantiaBEData::unstable, (unstable) -> new ResonantiaBEData(unstable));
    }
}
