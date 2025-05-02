package com.kjmaster.resonantia.tileentity;

import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import static com.kjmaster.resonantia.Resonantia.MODID;
import static com.kjmaster.resonantia.setup.Registration.RESONANTIA_BE_DATA;

public record PacketUpdateResonantiaBEData(BlockPos pos, ResonantiaBEData resonantiaBEData) implements CustomPacketPayload {

    public static final ResourceLocation ID = ResourceLocation.fromNamespaceAndPath(MODID, "update_resonantia_be_data");
    public static final CustomPacketPayload.Type<PacketUpdateResonantiaBEData> TYPE = new CustomPacketPayload.Type<>(ID);

    public static final StreamCodec<RegistryFriendlyByteBuf, PacketUpdateResonantiaBEData> CODEC = StreamCodec.composite(
            BlockPos.STREAM_CODEC, PacketUpdateResonantiaBEData::pos,
            ResonantiaBEData.STREAM_CODEC, PacketUpdateResonantiaBEData::resonantiaBEData,
            PacketUpdateResonantiaBEData::create);

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static PacketUpdateResonantiaBEData create(BlockPos fromPos, ResonantiaBEData resonantiaBEData) {
        return new PacketUpdateResonantiaBEData(fromPos, resonantiaBEData);
    }

    public void handle(IPayloadContext ctx) {
        ctx.enqueueWork(() -> {
            Player player = ctx.player();
            Level world = player.getCommandSenderWorld();
            BlockEntity te = world.getBlockEntity(pos);
            if (te instanceof ResonatingMachineTE) {
                te.setData(RESONANTIA_BE_DATA, resonantiaBEData);
            }
        });
    }
}
