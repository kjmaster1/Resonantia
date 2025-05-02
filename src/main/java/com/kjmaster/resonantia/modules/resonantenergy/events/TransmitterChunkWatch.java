package com.kjmaster.resonantia.modules.resonantenergy.events;

import com.kjmaster.resonantia.modules.resonantenergy.blocks.transmitter.ResonantEnergyTransmitterTileEntity;
import com.kjmaster.resonantia.modules.resonantenergy.data.TransmitterIndex;
import com.kjmaster.resonantia.resonance.PacketLinkVisualization;
import com.kjmaster.resonantia.resonance.PacketSyncEnabled;
import com.kjmaster.resonantia.setup.ResonantiaMessages;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.level.ChunkWatchEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@EventBusSubscriber
public class TransmitterChunkWatch {

    @SubscribeEvent
    public static void onChunkWatch(ChunkWatchEvent.Sent event) {
        ServerPlayer player = event.getPlayer();
        ChunkPos watchedChunk = event.getPos();

        for (ResonantEnergyTransmitterTileEntity te : getTransmittersInChunk(watchedChunk, player.serverLevel())) {
            if (te.isMachineEnabled()) {
                List<BlockPos> links = te.findLinkedMachines(te.getBlockPos(), te.frequency.getFrequency(), te.frequency.getRadius(), te.frequency.getLinkTolerance());
                ResonantiaMessages.sendToPlayer(PacketLinkVisualization.create(te.getBlockPos(), links), player);
            } else {
                ResonantiaMessages.sendToPlayer(PacketLinkVisualization.create(te.getBlockPos(), List.of()), player);
            }
            ResonantiaMessages.sendToPlayersTrackingChunk(PacketSyncEnabled.create(te.getBlockPos(), te.isMachineEnabled()), event.getLevel(), new ChunkPos(te.getBlockPos()));
        }
    }

    public static List<ResonantEnergyTransmitterTileEntity> getTransmittersInChunk(ChunkPos chunkPos, ServerLevel level) {
        Set<BlockPos> positions = TransmitterIndex.get(level, chunkPos);
        List<ResonantEnergyTransmitterTileEntity> result = new ArrayList<>();

        for (BlockPos pos : positions) {
            BlockEntity be = level.getBlockEntity(pos);
            if (be instanceof ResonantEnergyTransmitterTileEntity transmitter) {
                result.add(transmitter);
            }
        }

        return result;
    }
}
