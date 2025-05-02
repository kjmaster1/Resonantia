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
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class ResonantEnergyTransmitterRenderer implements BlockEntityRenderer<ResonantEnergyTransmitterTileEntity> {

    private static final ResourceLocation CORE_TEXTURE = ResourceLocation.fromNamespaceAndPath("resonantia", "textures/block/transmitter_core.png");

    public ResonantEnergyTransmitterRenderer(BlockEntityRendererProvider.Context context) {
    }

    public static void register() {
        BlockEntityRenderers.register(ResonantEnergyModule.SIMPLE_RESONANT_ENERGY_TRANSMITTER.be().get(), ResonantEnergyTransmitterRenderer::new);
    }

    @Override
    public @NotNull AABB getRenderBoundingBox(ResonantEnergyTransmitterTileEntity te) {
        Level level = te.getLevel();
        if (level == null) return new AABB(te.getBlockPos());
        AABB bounds = new AABB(te.getBlockPos());
        for (BlockPos target : ClientLinkBeamCache.getLinks(new GlobalPos(level.dimension(), te.getBlockPos()))) {
            bounds = bounds.minmax(new AABB(target));
        }
        return bounds.inflate(1.0);
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

        float beamLength = (float) start.distanceTo(end);

        float frequency = -2.0f;
        float amplitude = 0.3f;

        int segments = 40;
        int circlePoints = 12;
        Vec3[] prevRing = null;

        Vec3 beamRight = direction.cross(new Vec3(0, 1, 0)).normalize();
        Vec3 beamUp = direction.cross(beamRight).normalize();

        float nx = (float) direction.x;
        float ny = (float) direction.y;
        float nz = (float) direction.z;

        for (int i = 0; i < segments; i++) {
            float t = (float) i / (segments - 1);
            float x = (float) (start.x + direction.x * t * beamLength);
            float y = (float) (start.y + direction.y * t * beamLength);
            float z = (float) (start.z + direction.z * t * beamLength);

            float offset = (float) Math.sin(time + t * frequency * Math.PI * 2) * amplitude;
            Vec3 center = new Vec3(x, y + offset, z);

            Vec3[] ring = new Vec3[circlePoints];
            for (int j = 0; j < circlePoints; j++) {
                float angle = (float) (2 * Math.PI * j / circlePoints);
                Vec3 radial = beamRight.scale(Math.cos(angle)).add(beamUp.scale(Math.sin(angle))).scale(radius);
                ring[j] = center.add(radial);
            }

            if (prevRing != null) {
                for (int j = 0; j < circlePoints; j++) {
                    int next = (j + 1) % circlePoints;

                    Vec3 v0 = prevRing[j];
                    Vec3 v1 = prevRing[next];
                    Vec3 v2 = ring[next];
                    Vec3 v3 = ring[j];

                    putVertex(consumer, poseStack.last(), v0, color, 0f, 0f, light, nx, ny, nz);
                    putVertex(consumer, poseStack.last(), v1, color, 1f, 0f, light, nx, ny, nz);
                    putVertex(consumer, poseStack.last(), v2, color, 1f, 1f, light, nx, ny, nz);
                    putVertex(consumer, poseStack.last(), v3, color, 0f, 1f, light, nx, ny, nz);
                }
            }

            prevRing = ring;
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
