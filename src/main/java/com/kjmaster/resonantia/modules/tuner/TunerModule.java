package com.kjmaster.resonantia.modules.tuner;

import com.kjmaster.resonantia.modules.tuner.items.ResonanceTunerItem;
import mcjty.lib.datagen.DataGen;
import mcjty.lib.datagen.Dob;
import mcjty.lib.modules.IModule;
import net.minecraft.core.HolderLookup;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.registries.DeferredItem;

import static com.kjmaster.resonantia.Resonantia.tab;
import static com.kjmaster.resonantia.setup.Registration.ITEMS;
import static com.kjmaster.resonantia.setup.Registration.createStandardProperties;

public class TunerModule implements IModule {

    public static final DeferredItem<ResonanceTunerItem> RESONANCE_TUNER = ITEMS.register("resonance_tuner", tab(() -> new ResonanceTunerItem(createStandardProperties())));

    public TunerModule() {}

    @Override
    public void initDatagen(DataGen dataGen, HolderLookup.Provider provider) {
        dataGen.add(
                Dob.itemBuilder(RESONANCE_TUNER)
                        .generatedItem("item/resonance_tuner")
        );
    }

    @Override
    public void initConfig(IEventBus iEventBus) {

    }

    @Override
    public void initClient(FMLClientSetupEvent fmlClientSetupEvent) {

    }

    @Override
    public void init(FMLCommonSetupEvent fmlCommonSetupEvent) {

    }
}
