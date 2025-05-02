package com.kjmaster.resonantia.resonance;

import com.kjmaster.resonantia.api.frequency.CapabilityFrequency;
import com.kjmaster.resonantia.api.frequency.IFrequency;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.LongTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.datafix.DataFixTypes;
import net.minecraft.world.level.saveddata.SavedData;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.function.Predicate;

public class ResonanceNetworkSavedData extends SavedData {

    public static final String FILE_ID = "resonance_network";

    // frequency -> set of machine positions
    private final Map<Integer, Set<BlockPos>> frequencyMap = new HashMap<>();

    // machine position -> frequency
    private final Map<BlockPos, Integer> machineMap = new HashMap<>();

    private final Map<BlockPos, CachedLinks> linkCache = new HashMap<>();

    public ResonanceNetworkSavedData() {
    }

    private ResonanceNetworkSavedData(Map<Integer, Set<BlockPos>> frequencyMap, Map<BlockPos, Integer> machineMap) {
        this.frequencyMap.putAll(frequencyMap);
        this.machineMap.putAll(machineMap);
    }

    public static ResonanceNetworkSavedData get(ServerLevel level) {
        return level.getDataStorage().computeIfAbsent(ResonanceNetworkSavedData.factory(), ResonanceNetworkSavedData.FILE_ID);
    }

    public static SavedData.Factory<ResonanceNetworkSavedData> factory() {
        return new SavedData.Factory<>(ResonanceNetworkSavedData::new, ResonanceNetworkSavedData::load, DataFixTypes.LEVEL); // Use LEVEL unless you define a specific DataFixType
    }

    public static ResonanceNetworkSavedData load(CompoundTag tag, HolderLookup.Provider registries) {
        ResonanceNetworkSavedData data = new ResonanceNetworkSavedData();

        ListTag freqList = tag.getList("Frequencies", Tag.TAG_COMPOUND);
        for (Tag t : freqList) {
            CompoundTag freqTag = (CompoundTag) t;
            int frequency = freqTag.getInt("Frequency");
            ListTag posList = freqTag.getList("Positions", Tag.TAG_LONG);

            Set<BlockPos> positions = new HashSet<>();
            for (Tag p : posList) {
                positions.add(BlockPos.of(((LongTag) p).getAsLong()));
            }
            data.frequencyMap.put(frequency, positions);
        }

        ListTag machineList = tag.getList("Machines", Tag.TAG_COMPOUND);
        for (Tag t : machineList) {
            CompoundTag machineTag = (CompoundTag) t;
            BlockPos pos = BlockPos.of(machineTag.getLong("Pos"));
            int frequency = machineTag.getInt("Frequency");
            data.machineMap.put(pos, frequency);
        }

        return data;
    }

    @Override
    public @NotNull CompoundTag save(@NotNull CompoundTag tag, HolderLookup.@NotNull Provider provider) {
        ListTag freqList = new ListTag();
        for (Map.Entry<Integer, Set<BlockPos>> entry : frequencyMap.entrySet()) {
            CompoundTag freqTag = new CompoundTag();
            freqTag.putInt("Frequency", entry.getKey());

            ListTag posList = new ListTag();
            for (BlockPos pos : entry.getValue()) {
                posList.add(LongTag.valueOf(pos.asLong()));
            }
            freqTag.put("Positions", posList);

            freqList.add(freqTag);
        }
        tag.put("Frequencies", freqList);

        ListTag machineList = new ListTag();
        for (Map.Entry<BlockPos, Integer> entry : machineMap.entrySet()) {
            CompoundTag machineTag = new CompoundTag();
            machineTag.putLong("Pos", entry.getKey().asLong());
            machineTag.putInt("Frequency", entry.getValue());
            machineList.add(machineTag);
        }
        tag.put("Machines", machineList);

        return tag;
    }

    public void registerMachine(IFrequency machine) {
        int freq = machine.getFrequency();
        BlockPos pos = machine.getBlockPos();

        // Detect stale link usage of old machine
        if (machineMap.containsKey(pos)) {
            invalidateLinkCache(pos);
            List<BlockPos> toInvalidate = new ArrayList<>();
            for (Map.Entry<BlockPos, CachedLinks> entry : linkCache.entrySet()) {
                if (entry.getValue().linked.contains(pos)) {
                    toInvalidate.add(entry.getKey());
                }
            }
            for (BlockPos origin : toInvalidate) {
                invalidateLinkCache(origin);
            }
        }

        frequencyMap.computeIfAbsent(freq, k -> new HashSet<>()).add(pos);
        machineMap.put(pos, freq);

        invalidateLinkCache(pos);

        List<BlockPos> toInvalidate = new ArrayList<>();
        for (Map.Entry<BlockPos, CachedLinks> entry : linkCache.entrySet()) {
            BlockPos origin = entry.getKey();
            CachedLinks cached = entry.getValue();

            if (pos.closerThan(origin, cached.radius) && Math.abs(freq - cached.frequency) <= cached.tolerance) {
                toInvalidate.add(origin);
            }
        }
        for (BlockPos origin : toInvalidate) {
            invalidateLinkCache(origin);
        }

        setDirty();
    }

    public void unregisterMachine(IFrequency machine) {
        BlockPos pos = machine.getBlockPos();
        Integer freq = machineMap.remove(pos);
        if (freq != null) {
            Set<BlockPos> set = frequencyMap.get(freq);
            if (set != null) {
                set.remove(pos);
                if (set.isEmpty()) {
                    frequencyMap.remove(freq);
                }
            }
        }

        invalidateLinkCache(pos);

        List<BlockPos> affectedOrigins = new ArrayList<>();
        for (Map.Entry<BlockPos, ResonanceNetworkSavedData.CachedLinks> entry : getLinkCache().entrySet()) {
            if (entry.getValue().linked.contains(pos)) {
                affectedOrigins.add(entry.getKey());
            }
        }
        for (BlockPos origin : affectedOrigins) {
            invalidateLinkCache(origin);
        }

        setDirty();
    }

    public void updateMachine(IFrequency machine) {
        BlockPos pos = machine.getBlockPos();
        int newFrequency = machine.getFrequency();
        Integer oldFrequency = machineMap.get(pos);

        if (oldFrequency == null) {
            // Not registered yet, treat as fresh
            registerMachine(machine);
            return;
        }

        if (oldFrequency != newFrequency) {
            // Only move between frequency buckets
            Set<BlockPos> oldSet = frequencyMap.get(oldFrequency);
            if (oldSet != null) {
                oldSet.remove(pos);
                if (oldSet.isEmpty()) {
                    frequencyMap.remove(oldFrequency);
                }
            }
            frequencyMap.computeIfAbsent(newFrequency, k -> new HashSet<>()).add(pos);
            machineMap.put(pos, newFrequency);
            invalidateLinkCache(pos);

            List<BlockPos> toInvalidate = new ArrayList<>();
            for (Map.Entry<BlockPos, CachedLinks> entry : linkCache.entrySet()) {
                BlockPos origin = entry.getKey();
                CachedLinks cached = entry.getValue();

                if (pos.closerThan(origin, cached.radius) && Math.abs(newFrequency - cached.frequency) <= cached.tolerance) {
                    toInvalidate.add(origin);
                }
            }
            for (BlockPos origin : toInvalidate) {
                invalidateLinkCache(origin);
            }

            setDirty();
        }
    }

    public List<BlockPos> findLinkedMachines(ServerLevel level, BlockPos origin, int frequency, int radius, int tolerance, Predicate<BlockPos> testAtPos) {
        CachedLinks cache = linkCache.get(origin);
        long gameTime = level.getGameTime();

        if (cache != null
                && cache.frequency == frequency
                && cache.tolerance == tolerance
                && cache.radius == radius
                && !cache.linked.isEmpty()) {
            return List.copyOf(cache.linked);
        }

        Set<BlockPos> results = new HashSet<>();
        for (int freq = frequency - tolerance; freq <= frequency + tolerance; freq++) {
            Set<BlockPos> candidates = frequencyMap.get(freq);
            if (candidates != null) {
                for (BlockPos pos : candidates) {
                    if (!testAtPos.test(pos)) continue;
                    if (pos.equals(origin)) continue;
                    if (!pos.closerThan(origin, radius)) continue;
                    IFrequency otherFrequencyCap = level.getCapability(CapabilityFrequency.FREQUENCY_CAPABILITY, pos, null);
                    if (otherFrequencyCap == null) continue;
                    if (Math.abs(otherFrequencyCap.getFrequency() - frequency) > otherFrequencyCap.getLinkTolerance())
                        continue;
                    results.add(pos);
                }
            }
        }

        linkCache.put(origin, new CachedLinks(frequency, tolerance, radius, results, gameTime));

        return List.copyOf(results);
    }

    public boolean isMachineUnstable(List<BlockPos> testList, IFrequency frequencyCap) {
        for (BlockPos otherPos : testList) {
            int otherFrequency = machineMap.get(otherPos);
            if (Math.abs(frequencyCap.getFrequency() - otherFrequency) > frequencyCap.getDestabilizationTolerance()) {
                return true;
            }
        }
        return false;
    }

    public void invalidateLinkCache(BlockPos changedPos) {
        linkCache.entrySet().removeIf(e -> {
            BlockPos origin = e.getKey();
            CachedLinks cached = e.getValue();
            return origin.equals(changedPos) || cached.linked.contains(changedPos);
        });
    }

    public Map<BlockPos, CachedLinks> getLinkCache() {
        return this.linkCache;
    }

    public static class CachedLinks {
        final int frequency;
        final int tolerance;
        final int radius;
        public final Set<BlockPos> linked;
        final long lastUpdateTick;

        CachedLinks(int frequency, int tolerance, int radius, Set<BlockPos> linked, long tick) {
            this.frequency = frequency;
            this.tolerance = tolerance;
            this.radius = radius;
            this.linked = linked;
            this.lastUpdateTick = tick;
        }
    }
}
