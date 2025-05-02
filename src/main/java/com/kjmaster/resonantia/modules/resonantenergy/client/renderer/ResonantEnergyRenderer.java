package com.kjmaster.resonantia.modules.resonantenergy.client.renderer;

import com.kjmaster.resonantia.resonance.client.ClientEnabledCache;
import com.kjmaster.resonantia.tileentity.ResonatingMachineTE;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.GlobalPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import org.joml.Matrix4f;
import org.joml.Vector3f;

public class ResonantEnergyRenderer {

    public static void render(ResonatingMachineTE te, float partialTicks, PoseStack poseStack, MultiBufferSource bufferSource, int combinedLight, ResourceLocation coreTexture) {
        poseStack.pushPose();
        poseStack.translate(0.5, 1.0, 0.5);

        Level level = Minecraft.getInstance().level;
        if (level == null) return;

        float baseTime = level.getGameTime() + partialTicks - 1;

        boolean isEnabled = ClientEnabledCache.getEnabled(new GlobalPos(level.dimension(), te.getBlockPos()));

        float coreRotation = baseTime * (isEnabled ? (te.isUnstable() ? 8f : 1f) : 0f) % 360;

        // Core cube
        poseStack.pushPose();
        poseStack.mulPose(Axis.YP.rotationDegrees(coreRotation));
        poseStack.mulPose(Axis.XP.rotationDegrees(45f));
        poseStack.scale(0.35f, 0.35f, 0.35f);
        VertexConsumer coreBuffer = bufferSource.getBuffer(RenderType.entityTranslucentEmissive(coreTexture));
        renderGlowingCube(poseStack, coreBuffer, combinedLight);
        poseStack.popPose();

        poseStack.popPose();
    }

    private static void renderGlowingCube(PoseStack poseStack, VertexConsumer buffer, int light) {
        PoseStack.Pose pose = poseStack.last();
        Matrix4f matrix = pose.pose();

        float min = -0.5f;
        float max = 0.5f;

        // Front (Z-)
        addQuad(buffer, matrix, min, min, min, max, min, min, max, max, min, min, max, min, new Vector3f(0, 0, -1), light);
        // Back (Z+)
        addQuad(buffer, matrix, max, min, max, min, min, max, min, max, max, max, max, max, new Vector3f(0, 0, 1), light);
        // Bottom (Y-)
        addQuad(buffer, matrix, min, min, max, max, min, max, max, min, min, min, min, min, new Vector3f(0, -1, 0), light);
        // Top (Y+)
        addQuad(buffer, matrix, min, max, min, max, max, min, max, max, max, min, max, max, new Vector3f(0, 1, 0), light);
        // Left (X-)
        addQuad(buffer, matrix, min, min, max, min, min, min, min, max, min, min, max, max, new Vector3f(-1, 0, 0), light);
        // Right (X+)
        addQuad(buffer, matrix, max, min, min, max, min, max, max, max, max, max, max, min, new Vector3f(1, 0, 0), light);
    }

    private static void addQuad(VertexConsumer buffer, Matrix4f matrix,
                                float x1, float y1, float z1,
                                float x2, float y2, float z2,
                                float x3, float y3, float z3,
                                float x4, float y4, float z4,
                                Vector3f normal, int light) {
        int r = 255, g = 255, b = 255, a = 128;
        float u1 = 0f, v1 = 0f;
        float u2 = 1f, v2 = 0f;
        float u3 = 1f, v3 = 1f;
        float u4 = 0f, v4 = 1f;

        buffer.addVertex(matrix, x1, y1, z1).setColor(r, g, b, a).setUv(u1, v1)
                .setOverlay(OverlayTexture.NO_OVERLAY).setLight(light).setNormal(normal.x(), normal.y(), normal.z());

        buffer.addVertex(matrix, x2, y2, z2).setColor(r, g, b, a).setUv(u2, v2)
                .setOverlay(OverlayTexture.NO_OVERLAY).setLight(light).setNormal(normal.x(), normal.y(), normal.z());

        buffer.addVertex(matrix, x3, y3, z3).setColor(r, g, b, a).setUv(u3, v3)
                .setOverlay(OverlayTexture.NO_OVERLAY).setLight(light).setNormal(normal.x(), normal.y(), normal.z());

        buffer.addVertex(matrix, x4, y4, z4).setColor(r, g, b, a).setUv(u4, v4)
                .setOverlay(OverlayTexture.NO_OVERLAY).setLight(light).setNormal(normal.x(), normal.y(), normal.z());
    }
}
