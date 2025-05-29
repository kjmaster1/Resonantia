package com.kjmaster.resonantia.setup;

import com.kjmaster.resonantia.client.model.SimpleModel;
import com.kjmaster.resonantia.client.renderer.model.ReconfigurableBakedModel;
import com.kjmaster.resonantia.client.renderer.model.ReconfigurableSlabBakedModel;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.ModelEvent;

import static com.kjmaster.resonantia.Resonantia.MODID;

public class ClientSetup {

    public static SimpleModel.Loader RECONFIGURABLE_LOADER = new SimpleModel.Loader(ReconfigurableBakedModel::new);
    public static SimpleModel.Loader RECONFIGURABLE_SLAB_LOADER = new SimpleModel.Loader(ReconfigurableSlabBakedModel::new);

    public static void init(FMLClientSetupEvent e) {}

    public static void registerModels(final ModelEvent.RegisterGeometryLoaders event) {
        event.register(ResourceLocation.fromNamespaceAndPath(MODID, "reconfigurable"), RECONFIGURABLE_LOADER);
        event.register(ResourceLocation.fromNamespaceAndPath(MODID, "reconfigurable_slab"), RECONFIGURABLE_SLAB_LOADER);
    }
}
