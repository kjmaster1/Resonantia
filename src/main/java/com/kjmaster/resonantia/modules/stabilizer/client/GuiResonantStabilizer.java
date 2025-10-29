package com.kjmaster.resonantia.modules.stabilizer.client;

import com.kjmaster.resonantia.client.gui.GuiModularResonatingMachine;
import com.kjmaster.resonantia.data.ProcessingMachineData;
import com.kjmaster.resonantia.modules.crusher.CrusherModule;
import com.kjmaster.resonantia.modules.crusher.blocks.ResonantCrusherTE;
import com.kjmaster.resonantia.modules.crusher.client.GuiResonantCrusher;
import com.kjmaster.resonantia.modules.stabilizer.StabilizerModule;
import com.kjmaster.resonantia.modules.stabilizer.blocks.ResonantStabilizerTE;
import mcjty.lib.container.GenericContainer;
import mcjty.lib.gui.ManualEntry;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Inventory;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import org.jetbrains.annotations.NotNull;

import static com.kjmaster.resonantia.setup.Registration.PROCESSING_MACHINE_DATA;

public class GuiResonantStabilizer extends GuiModularResonatingMachine<ResonantStabilizerTE, GenericContainer> {

    private static final ResourceLocation BURN_PROGRESS_SPRITE = ResourceLocation.withDefaultNamespace("container/furnace/burn_progress");

    public GuiResonantStabilizer(GenericContainer container, Inventory inventory, Component title) {
        super(container, inventory, title, ManualEntry.EMPTY);
    }

    public static void register(RegisterMenuScreensEvent event) {
        event.register(StabilizerModule.CONTAINER_RESONANT_STABILIZER.get(), GuiResonantStabilizer::new);
    }

    @Override
    protected void renderBg(@NotNull GuiGraphics graphics, float partialTicks, int x, int y) {
        updateFields();
        drawWindow(graphics, partialTicks, x, y);
        ProcessingMachineData processingMachineData = this.menu.getAttachmentData(PROCESSING_MACHINE_DATA.get());
        ResonantStabilizerTE te = (ResonantStabilizerTE) this.menu.getBe();
        int energyCost = te.getEnergyCost();
        if (processingMachineData != null) {
            int progress = processingMachineData.progress();
            float burnProgress = energyCost != 0 && progress != 0 ? Mth.clamp((float) progress / (float) energyCost, 0.0F, 1.0F) : 0.0F;
            int j1 = Mth.ceil(burnProgress * 24.0F);
            graphics.blitSprite(BURN_PROGRESS_SPRITE, 24, 16, 0, 0, this.leftPos + 77, this.topPos + 43, j1, 16);
        }
    }
}
