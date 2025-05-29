package com.kjmaster.resonantia.client.renderer.blockentity;

import com.kjmaster.resonantia.modules.resonantenergy.client.renderer.ResonantEnergyRenderer;
import com.kjmaster.resonantia.tileentity.ModularResonatingMachineTE;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec3;

public class ResonantEnergyMachineRenderer implements BlockEntityRenderer<ModularResonatingMachineTE> {

    private static final ResourceLocation CORE_TEXTURE = ResourceLocation.fromNamespaceAndPath("resonantia", "textures/block/receiver_core.png");

    public ResonantEnergyMachineRenderer(BlockEntityRendererProvider.Context context) {
    }

    @Override
    public void render(ModularResonatingMachineTE blockEntity, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
        ResonantEnergyRenderer.render(blockEntity, partialTick, poseStack, bufferSource, packedLight, CORE_TEXTURE, new Vec3(0.5, 0.5, 0.5));
    }
}
