package com.kjmaster.resonantia.resonance;

import com.kjmaster.resonantia.Resonantia;
import com.kjmaster.resonantia.resonance.client.ClientEnabledCache;
import com.kjmaster.resonantia.resonance.client.ClientLinkBeamCache;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.core.NonNullList;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public record PacketLinkVisualization(BlockPos source, List<BlockPos> targets) implements CustomPacketPayload {

    public static final ResourceLocation ID = ResourceLocation.fromNamespaceAndPath(Resonantia.MODID, "link_visualization");
    public static final CustomPacketPayload.Type<PacketLinkVisualization> TYPE = new CustomPacketPayload.Type<>(ID);

    public static final StreamCodec<RegistryFriendlyByteBuf, PacketLinkVisualization> CODEC = StreamCodec.composite(
            BlockPos.STREAM_CODEC, PacketLinkVisualization::source,
            ByteBufCodecs.collection(NonNullList::createWithCapacity, BlockPos.STREAM_CODEC), PacketLinkVisualization::targets,
            PacketLinkVisualization::create);

    public static PacketLinkVisualization create(BlockPos source, List<BlockPos> targets) {
        return new PacketLinkVisualization(source, targets);
    }

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public void handle(IPayloadContext ctx) {
        ctx.enqueueWork(() -> {
            Player player = ctx.player();
            Level world = player.getCommandSenderWorld();
            GlobalPos globalPos = new GlobalPos(world.dimension(), source);
            ClientLinkBeamCache.setLinks(globalPos, targets);
        });
    }
}
