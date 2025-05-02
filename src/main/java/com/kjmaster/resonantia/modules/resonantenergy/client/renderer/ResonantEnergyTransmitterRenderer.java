package com.kjmaster.resonantia.modules.resonantenergy.client.renderer;

import com.kjmaster.resonantia.modules.resonantenergy.ResonantEnergyModule;
import com.kjmaster.resonantia.modules.resonantenergy.blocks.transmitter.ResonantEnergyTransmitterTileEntity;
import com.kjmaster.resonantia.resonance.client.ClientLinkBeamCache;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FastColor;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import java.util.List;

public class ResonantEnergyTransmitterRenderer implements BlockEntityRenderer<ResonantEnergyTransmitterTileEntity> {

    private static final ResourceLocation CORE_TEXTURE = ResourceLocation.fromNamespaceAndPath("resonantia", "textures/block/transmitter_core.png");

    public ResonantEnergyTransmitterRenderer(BlockEntityRendererProvider.Context context) {
    }

    public static void register() {
        BlockEntityRenderers.register(ResonantEnergyModule.SIMPLE_RESONANT_ENERGY_TRANSMITTER.be().get(), ResonantEnergyTransmitterRenderer::new);
    }

    @Override
    public void render(ResonantEnergyTransmitterTileEntity te, float partialTicks, PoseStack poseStack, MultiBufferSource bufferSource, int combinedLight, int combinedOverlay) {
        ResonantEnergyRenderer.render(te, partialTicks, poseStack, bufferSource, combinedLight, CORE_TEXTURE);
        renderLinks(te, partialTicks, poseStack, bufferSource);
    }

    private void renderLinks(ResonantEnergyTransmitterTileEntity te, float partialTicks, PoseStack poseStack, MultiBufferSource bufferSource) {
        Level level = Minecraft.getInstance().level;
        if (level == null) return;
        List<BlockPos> targets = ClientLinkBeamCache.getLinks(new GlobalPos(level.dimension(), te.getBlockPos()));
        System.out.println(targets);
        if (targets.isEmpty()) return;

        poseStack.pushPose();
        poseStack.translate(0, 0.5, 0);

        Vec3 blockCenter = new Vec3(0.5, 0.5, 0.5);

        float time = (level.getGameTime() + partialTicks) % 1000;

        VertexConsumer beamBuffer = bufferSource.getBuffer(RenderType.entityTranslucentEmissive(CORE_TEXTURE));

        int color = FastColor.ARGB32.color(128, 255, 255, 255); // Example: opaque bluish beam
        int light = LightTexture.pack(15, 15); // Maximum brightness
        float radius = 0.05f;
        BlockPos tePos = te.getBlockPos();

        for (BlockPos targetPos : targets) {
            Vec3 to = Vec3.atCenterOf(targetPos).subtract(new Vec3(tePos.getX(), tePos.getY(), tePos.getZ()));
            System.out.println("From: " + blockCenter + ", To: " + to + ", Length: " + to.subtract(blockCenter).length());
            renderBeam(
                    poseStack,
                    beamBuffer,
                    (float) blockCenter.x, (float) blockCenter.y, (float) blockCenter.z,
                    (float) to.x,   (float) to.y,   (float) to.z,
                    radius, color, light, time
            );
        }

        poseStack.popPose();
    }

    public static void renderBeam(PoseStack poseStack, VertexConsumer consumer,
                                  float x1, float y1, float z1,
                                  float x2, float y2, float z2,
                                  float radius, int color, int light, float time) {

        Vec3 start = new Vec3(x1, y1, z1);
        Vec3 end = new Vec3(x2, y2, z2);
        Vec3 direction = end.subtract(start).normalize();

        Vec3 up = new Vec3(0, 1, 0);
        if (Math.abs(direction.dot(up)) > 0.99) up = new Vec3(1, 0, 0);
        Vec3 right = direction.cross(up).normalize().scale(radius);

        Vec3 v1 = start.add(right);
        Vec3 v2 = start.subtract(right);
        Vec3 v3 = end.subtract(right);
        Vec3 v4 = end.add(right);

        PoseStack.Pose pose = poseStack.last();

        float nx = (float) direction.x;
        float ny = (float) direction.y;
        float nz = (float) direction.z;

        putVertex(consumer, pose, v1, color, 0f, 0f, light, nx, ny, nz);
        putVertex(consumer, pose, v2, color, 1f, 0f, light, nx, ny, nz);
        putVertex(consumer, pose, v3, color, 1f, 1f, light, nx, ny, nz);
        putVertex(consumer, pose, v4, color, 0f, 1f, light, nx, ny, nz);
    }

    private static void putVertex(VertexConsumer consumer, PoseStack.Pose pose,
                                  Vec3 pos, int color, float u, float v,
                                  int light, float nx, float ny, float nz) {
        consumer.addVertex(pose, (float) pos.x, (float) pos.y, (float) pos.z)
                .setColor(color)
                .setUv(u, v)
                .setOverlay(OverlayTexture.NO_OVERLAY)
                .setLight(light)
                .setNormal(pose, nx, ny, nz);
    }
}
