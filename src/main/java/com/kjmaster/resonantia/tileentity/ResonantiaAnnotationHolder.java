package com.kjmaster.resonantia.tileentity;

import net.neoforged.neoforge.capabilities.BlockCapability;
import net.neoforged.neoforge.registries.DeferredBlock;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;

public class ResonantiaAnnotationHolder {
    public static final Map<Class<? extends ResonantiaTileEntity>, ResonantiaAnnotationHolder> annotations = new HashMap();
    final List<CapHolder> caps = new ArrayList();

    public ResonantiaAnnotationHolder() {
    }

    public int getCapSize() {
        return this.caps.size();
    }

    public <B, C> CapHolder<B, C> getCapHolder(int i) {
        return (CapHolder) this.caps.get(i);
    }

    public static record CapHolder<B, C>(BlockCapability<B, C> capability,
                                         BiFunction<? super ResonantiaTileEntity, Object, Object> function,
                                         DeferredBlock<?> block) {
        public CapHolder(BlockCapability<B, C> capability, BiFunction<? super ResonantiaTileEntity, Object, Object> function, DeferredBlock<?> block) {
            this.capability = capability;
            this.function = function;
            this.block = block;
        }

        public BlockCapability<B, C> capability() {
            return this.capability;
        }

        public BiFunction<? super ResonantiaTileEntity, Object, Object> function() {
            return this.function;
        }

        public DeferredBlock<?> block() {
            return this.block;
        }
    }
}