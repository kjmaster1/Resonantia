package com.kjmaster.resonantia.modules.smelter.client;

import com.kjmaster.resonantia.client.gui.GuiModularResonatingMachine;
import com.kjmaster.resonantia.data.ProcessingMachineData;
import com.kjmaster.resonantia.modules.smelter.SmelterModule;
import com.kjmaster.resonantia.modules.smelter.blocks.ResonantSmelterTE;
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

public class GuiResonantSmelter extends GuiModularResonatingMachine<ResonantSmelterTE, GenericContainer> {

    private static final ResourceLocation BURN_PROGRESS_SPRITE = ResourceLocation.withDefaultNamespace("container/furnace/burn_progress");

    public GuiResonantSmelter(GenericContainer container, Inventory inventory, Component title) {
        super(container, inventory, title, ManualEntry.EMPTY);
    }

    public static void register(RegisterMenuScreensEvent event) {
        event.register(SmelterModule.CONTAINER_RESONANT_SMELTER.get(), GuiResonantSmelter::new);
    }

    @Override
    protected void renderBg(@NotNull GuiGraphics graphics, float partialTicks, int x, int y) {
        updateFields();
        drawWindow(graphics, partialTicks, x, y);
        ProcessingMachineData processingMachineData = this.menu.getAttachmentData(PROCESSING_MACHINE_DATA.get());
        ResonantSmelterTE te = (ResonantSmelterTE) this.menu.getBe();
        int ticksPerSmelt = te.getTicksPerSmelt();
        if (processingMachineData != null) {
            int progress = processingMachineData.progress();
            float burnProgress = ticksPerSmelt != 0 && progress != 0 ? Mth.clamp((float) progress / (float) ticksPerSmelt, 0.0F, 1.0F) : 0.0F;
            int j1 = Mth.ceil(burnProgress * 24.0F);
            graphics.blitSprite(BURN_PROGRESS_SPRITE, 24, 16, 0, 0, this.leftPos + 73, this.topPos + 28, j1, 16);
        }
    }
}
