package com.kjmaster.resonantia.crafting;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

public record MachineRecipeData(int energyCost) {

    public static final Codec<MachineRecipeData> CODEC = RecordCodecBuilder.create((instance) -> instance.group(Codec.INT.fieldOf("energyCost").forGetter(MachineRecipeData::energyCost))
            .apply(instance, (energyCost) -> new MachineRecipeData(energyCost)));
    public static final StreamCodec<FriendlyByteBuf, MachineRecipeData> STREAM_CODEC;

    @Override
    public int energyCost() {
        return energyCost;
    }

    public MachineRecipeData withEnergyCost(int energyCost) {
        return new MachineRecipeData(energyCost);
    }

    static {
        STREAM_CODEC = StreamCodec.composite(ByteBufCodecs.INT, MachineRecipeData::energyCost, (energyCost) -> new MachineRecipeData(energyCost));
    }
}

