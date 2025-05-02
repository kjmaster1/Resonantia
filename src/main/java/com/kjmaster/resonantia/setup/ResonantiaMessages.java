package com.kjmaster.resonantia.setup;

import com.kjmaster.resonantia.Resonantia;
import com.kjmaster.resonantia.resonance.PacketLinkVisualization;
import com.kjmaster.resonantia.modules.tuner.network.PacketUpdateResonanceTuner;
import com.kjmaster.resonantia.resonance.PacketSyncEnabled;
import com.kjmaster.resonantia.tileentity.PacketUpdateResonantiaBEData;
import mcjty.lib.network.Networking;
import mcjty.lib.network.PacketSendServerCommand;
import mcjty.lib.typed.TypedMap;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ChunkPos;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class ResonantiaMessages {

    public static void registerMessages(RegisterPayloadHandlersEvent event) {
        final PayloadRegistrar registrar = event.registrar(Resonantia.MODID)
                .versioned("1.0")
                .optional();

        registrar.playToClient(PacketSyncEnabled.TYPE, PacketSyncEnabled.CODEC, PacketSyncEnabled::handle);
        registrar.playToClient(PacketLinkVisualization.TYPE, PacketLinkVisualization.CODEC, PacketLinkVisualization::handle);
        registrar.playToClient(PacketUpdateResonantiaBEData.TYPE, PacketUpdateResonantiaBEData.CODEC, PacketUpdateResonantiaBEData::handle);

        registrar.playToServer(PacketUpdateResonanceTuner.TYPE, PacketUpdateResonanceTuner.CODEC, PacketUpdateResonanceTuner::handle);
    }

    public static void sendToServer(String command, @Nonnull TypedMap.Builder argumentBuilder) {
        Networking.sendToServer(new PacketSendServerCommand(Resonantia.MODID, command, argumentBuilder.build()));
    }

    public static void sendToServer(String command) {
        Networking.sendToServer(new PacketSendServerCommand(Resonantia.MODID, command, TypedMap.EMPTY));
    }

    public static <T extends CustomPacketPayload> void sendToPlayer(T packet, Player player) {
        PacketDistributor.sendToPlayer((ServerPlayer)player, packet);
    }

    public static <T extends CustomPacketPayload> void sendToPlayersNear(T packet, @Nonnull ServerLevel level, @Nullable Player player, double x, double y, double z, double radius) {
        PacketDistributor.sendToPlayersNear(level, (ServerPlayer)player, x, y, z, radius, packet);
    }

    public static <T extends CustomPacketPayload> void sendToPlayersTrackingChunk(T packet, @Nonnull ServerLevel level, ChunkPos chunkPos) {
        PacketDistributor.sendToPlayersTrackingChunk(level, chunkPos, packet);
    }

    public static <T extends CustomPacketPayload> void sendToServer(T packet) {
        PacketDistributor.sendToServer(packet);
    }
}
