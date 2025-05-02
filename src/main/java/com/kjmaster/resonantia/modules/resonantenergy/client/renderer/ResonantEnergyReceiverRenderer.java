package com.kjmaster.resonantia.modules.resonantenergy.client.renderer;

import com.kjmaster.resonantia.modules.resonantenergy.ResonantEnergyModule;
import com.kjmaster.resonantia.modules.resonantenergy.blocks.receiver.ResonantEnergyReceiverTileEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraft.resources.ResourceLocation;

public class ResonantEnergyReceiverRenderer implements BlockEntityRenderer<ResonantEnergyReceiverTileEntity> {

    private static final ResourceLocation CORE_TEXTURE = ResourceLocation.fromNamespaceAndPath("resonantia", "textures/block/receiver_core.png");

    public ResonantEnergyReceiverRenderer(BlockEntityRendererProvider.Context context) {
    }

    public static void register() {
        BlockEntityRenderers.register(ResonantEnergyModule.SIMPLE_RESONANT_ENERGY_RECEIVER.be().get(), ResonantEnergyReceiverRenderer::new);
    }

    @Override
    public void render(ResonantEnergyReceiverTileEntity te, float partialTicks, PoseStack poseStack, MultiBufferSource bufferSource, int combinedLight, int combinedOverlay) {
        ResonantEnergyRenderer.render(te, partialTicks, poseStack, bufferSource, combinedLight, CORE_TEXTURE);
    }
}
