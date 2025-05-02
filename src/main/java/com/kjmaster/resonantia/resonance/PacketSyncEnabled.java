package com.kjmaster.resonantia.resonance;

import com.kjmaster.resonantia.Resonantia;
import com.kjmaster.resonantia.resonance.client.ClientEnabledCache;
import com.kjmaster.resonantia.resonance.client.ClientLinkBeamCache;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.core.NonNullList;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

public record PacketSyncEnabled(BlockPos source, boolean enabled) implements CustomPacketPayload {

    public static final ResourceLocation ID = ResourceLocation.fromNamespaceAndPath(Resonantia.MODID, "sync_enabled");
    public static final CustomPacketPayload.Type<PacketSyncEnabled> TYPE = new CustomPacketPayload.Type<>(ID);

    public static final StreamCodec<RegistryFriendlyByteBuf, PacketSyncEnabled> CODEC = StreamCodec.composite(
            BlockPos.STREAM_CODEC, PacketSyncEnabled::source,
            ByteBufCodecs.BOOL, PacketSyncEnabled::enabled,
            PacketSyncEnabled::create);

    public static PacketSyncEnabled create(BlockPos source, boolean enabled) {
        return new PacketSyncEnabled(source, enabled);
    }

    @Override
    public @NotNull CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public void handle(IPayloadContext ctx) {
        ctx.enqueueWork(() -> {
            Player player = ctx.player();
            Level world = player.getCommandSenderWorld();
            GlobalPos globalPos = new GlobalPos(world.dimension(), source);
            ClientEnabledCache.setEnabled(globalPos, enabled);
        });
    }
}
