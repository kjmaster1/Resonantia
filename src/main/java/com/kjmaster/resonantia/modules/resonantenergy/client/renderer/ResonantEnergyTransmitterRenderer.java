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

        int color = FastColor.ARGB32.color(128, 255, 255, 255);
        int light = LightTexture.pack(15, 15); // Maximum brightness
        float radius = 0.05f;
        BlockPos tePos = te.getBlockPos();

        for (BlockPos targetPos : targets) {
            MultiBufferSource.BufferSource immediate = Minecraft.getInstance().renderBuffers().bufferSource();
            VertexConsumer beamBuffer = immediate.getBuffer(RenderType.entityTranslucentEmissive(CORE_TEXTURE));
            Vec3 to = Vec3.atCenterOf(targetPos).subtract(new Vec3(tePos.getX(), tePos.getY(), tePos.getZ()));
            renderBeam(
                    poseStack,
                    beamBuffer,
                    (float) blockCenter.x, (float) blockCenter.y, (float) blockCenter.z,
                    (float) to.x,   (float) to.y,   (float) to.z,
                    radius, color, light, time
            );
            immediate.endBatch();
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

        // Create a unit up vector to calculate perpendicular direction for the beam
        Vec3 up = new Vec3(0, 1, 0);
        if (Math.abs(direction.dot(up)) > 0.99) up = new Vec3(1, 0, 0);
        Vec3 right = direction.cross(up).normalize().scale(radius);

        // Determine the length of the beam
        float beamLength = (float) start.distanceTo(end);

        // Sine wave frequency and amplitude to control oscillation
        float frequency = 2.0f;  // How many oscillations per unit length
        float amplitude = 0.1f;  // How far the beam "waves" from its original path

        // Loop through the length of the beam to create the oscillating effect
        int segments = 40; // More segments for smoother oscillation
        for (int i = 0; i < segments; i++) {
            float t = (float) i / (segments - 1);  // Interpolation factor along the beam

            float x = (float) (start.x + direction.x * t * beamLength);
            float y = (float) (start.y + direction.y * t * beamLength);
            float z = (float) (start.z + direction.z * t * beamLength);

            // Apply sine wave offset to the y-axis (or whichever axis you prefer to oscillate)
            float offset = (float) Math.sin(time + t * frequency * Math.PI * 2) * amplitude;

            // Calculate positions with offset
            Vec3 offsetPosition = new Vec3(x, y + offset, z);  // Apply offset along the y-axis (vertical)

            // Compute the perpendicular vertices
            Vec3 v1 = offsetPosition.add(right);
            Vec3 v2 = offsetPosition.subtract(right);

            // Continue rendering the beam with smooth transitions
            float nx = (float) direction.x;
            float ny = (float) direction.y;
            float nz = (float) direction.z;

            // Render the two vertices at this position (forming a smooth, continuous curve)
            putVertex(consumer, poseStack.last(), v1, color, 0f, 0f, light, nx, ny, nz);
            putVertex(consumer, poseStack.last(), v2, color, 1f, 0f, light, nx, ny, nz);

            // Render the next point in the beam (to continue the curve)
            if (i < segments - 1) {
                float nextT = (float) (i + 1) / (segments - 1);
                float nextX = (float) (start.x + direction.x * nextT * beamLength);
                float nextY = (float) (start.y + direction.y * nextT * beamLength);
                float nextZ = (float) (start.z + direction.z * nextT * beamLength);

                float nextOffset = (float) Math.sin(time + nextT * frequency * Math.PI * 2) * amplitude;
                Vec3 nextOffsetPosition = new Vec3(nextX, nextY + nextOffset, nextZ);

                // Calculate next set of perpendicular vertices
                Vec3 nextV1 = nextOffsetPosition.add(right);
                Vec3 nextV2 = nextOffsetPosition.subtract(right);

                // Continue drawing the continuous wave by connecting the points
                putVertex(consumer, poseStack.last(), nextV1, color, 0f, 1f, light, nx, ny, nz);
                putVertex(consumer, poseStack.last(), nextV2, color, 1f, 1f, light, nx, ny, nz);
            }
        }
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
