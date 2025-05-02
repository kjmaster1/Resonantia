package com.kjmaster.resonantia.tileentity;

import net.neoforged.neoforge.registries.DeferredBlock;
import org.apache.commons.lang3.reflect.FieldUtils;

import javax.annotation.Nullable;
import java.lang.reflect.Field;
import java.util.function.Function;

public class ResonantiaAnnotationTools {

    public ResonantiaAnnotationTools() {
    }

    public static ResonantiaAnnotationHolder createAnnotationHolder(Class<? extends ResonantiaTileEntity> clazz, @Nullable DeferredBlock<?> block) {
        ResonantiaAnnotationHolder holder = new ResonantiaAnnotationHolder();
        ResonantiaAnnotationHolder.annotations.put(clazz, holder);
        if (block != null) {
            scanCaps(clazz, holder, block);
        }

        return holder;
    }

    private static void scanCaps(Class<? extends ResonantiaTileEntity> clazz, ResonantiaAnnotationHolder holder, DeferredBlock<?> block) {
        Field[] caps = FieldUtils.getFieldsWithAnnotation(clazz, ResonantiaCap.class);

        for (Field cap : caps) {
            ResonantiaCap annotation = cap.getAnnotation(ResonantiaCap.class);
            ResonantiaCapTypes type = annotation.type();
            Object instance = null;

            try {
                instance = FieldUtils.readField(cap, clazz, true);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }

            Function<? super ResonantiaTileEntity, Object> function;
            function = (Function) instance;

            holder.caps.add(new ResonantiaAnnotationHolder.CapHolder(type.getCapability(), function, block));
        }
    }
}
