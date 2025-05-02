package com.kjmaster.resonantia;

import com.kjmaster.resonantia.modules.resonantenergy.ResonantEnergyModule;
import com.kjmaster.resonantia.modules.tuner.TunerModule;
import com.kjmaster.resonantia.setup.*;
import com.mojang.logging.LogUtils;
import mcjty.lib.datagen.DataGen;
import mcjty.lib.modules.Modules;
import net.minecraft.world.item.Item;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import org.slf4j.Logger;

import java.util.function.Supplier;

@Mod(Resonantia.MODID)
public class Resonantia {
    public static final String MODID = "resonantia";
    private static final Logger LOGGER = LogUtils.getLogger();

    @SuppressWarnings("PublicField")
    public static final ModSetup setup = new ModSetup();

    @SuppressWarnings("PublicField")
    public static Resonantia instance;
    private final Modules modules = new Modules();

    public Resonantia(IEventBus bus, ModContainer mod, Dist dist) {
        instance = this;
        setupModules(bus);

        Config.register(mod, bus, modules);

        Registration.register(bus);

        bus.addListener(setup::init);
        bus.addListener(modules::init);
        bus.addListener(this::onDataGen);
        bus.addListener(ResonantiaMessages::registerMessages);
        bus.addListener(setup.getBlockCapabilityRegistrar(Registration.RBLOCKS));

        if (dist.isClient()) {
            bus.addListener(ClientSetup::init);
            bus.addListener(modules::initClient);
            mod.registerExtensionPoint(IConfigScreenFactory.class, ConfigurationScreen::new);
        }
    }

    public static <T extends Item> Supplier<T> tab(Supplier<T> supplier) {
        return instance.setup.tab(supplier);
    }

    private void onDataGen(GatherDataEvent event) {
        DataGen datagen = new DataGen(MODID, event);
        modules.datagen(datagen, event.getLookupProvider());
        datagen.generate();
    }

    private void setupModules(IEventBus bus) {
        modules.register(new TunerModule());
        modules.register(new ResonantEnergyModule(bus));
    }
}
