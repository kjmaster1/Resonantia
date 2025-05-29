package com.kjmaster.resonantia.client.renderer.model;

import com.kjmaster.resonantia.data.Mode;
import mcjty.lib.client.RenderHelper;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.client.model.BakedModelWrapper;
import net.neoforged.neoforge.client.model.IDynamicBakedModel;
import net.neoforged.neoforge.client.model.data.ModelData;
import net.neoforged.neoforge.client.model.pipeline.QuadBakingVertexConsumer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedList;
import java.util.List;

import static com.kjmaster.resonantia.Resonantia.MODID;
import static com.kjmaster.resonantia.setup.Registration.*;
import static mcjty.lib.client.AbstractDynamicBakedModel.getTexture;

public class ReconfigurableBakedModel extends BakedModelWrapper<BakedModel> implements IDynamicBakedModel {

    public static final ResourceLocation TEXTURE_INPUT_MASK = ResourceLocation.fromNamespaceAndPath(MODID, "block/machine/input_mask");
    public static final ResourceLocation TEXTURE_OUTPUT_MASK = ResourceLocation.fromNamespaceAndPath(MODID, "block/machine/output_mask");
    public static final ResourceLocation TEXTURE_BOTH_MASK = ResourceLocation.fromNamespaceAndPath(MODID, "block/machine/both_mask");

    private static TextureAtlasSprite inputMask;
    private static TextureAtlasSprite outputMask;
    private static TextureAtlasSprite bothMask;

    private static TextureAtlasSprite getInputMask() {
        if (inputMask == null) {
            inputMask = getTexture(TEXTURE_INPUT_MASK);
        }
        return inputMask;
    }

    private static TextureAtlasSprite getOutputMask() {
        if (outputMask == null) {
            outputMask = getTexture(TEXTURE_OUTPUT_MASK);
        }
        return outputMask;
    }

    private static TextureAtlasSprite getBothMask() {
        if (bothMask == null) {
            bothMask = getTexture(TEXTURE_BOTH_MASK);
        }
        return bothMask;
    }

    public ReconfigurableBakedModel(BakedModel originalModel) {
        super(originalModel);
    }

    @Override
    public @NotNull List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, RandomSource rand, ModelData extraData, @Nullable RenderType renderType) {
        LinkedList<BakedQuad> quads = new LinkedList<>(originalModel.getQuads(state, side, rand, extraData, renderType));

        if (state == null || quads.isEmpty()) return quads;

        Mode energyUp = extraData.get(ENERGY_UP);
        Mode energyDown = extraData.get(ENERGY_DOWN);
        Mode energyNorth = extraData.get(ENERGY_NORTH);
        Mode energySouth = extraData.get(ENERGY_SOUTH);
        Mode energyWest = extraData.get(ENERGY_WEST);
        Mode energyEast = extraData.get(ENERGY_EAST);

        float highlight = 1.0f;

        float o = .4f;

        TextureAtlasSprite upMask = getMask(energyUp);
        TextureAtlasSprite downMask = getMask(energyDown);
        TextureAtlasSprite northMask = getMask(energyNorth);
        TextureAtlasSprite southMask = getMask(energySouth);
        TextureAtlasSprite westMask = getMask(energyWest);
        TextureAtlasSprite eastMask = getMask(energyEast);

        if (upMask != null) {
            quads.add(createQuad(v(o, 1.03, o), v(o, 1.03, 1 - o), v(1 - o, 1.03, 1 - o), v(1 - o, 1.03, o), upMask, highlight));
        }
        if (downMask != null) {
            quads.add(createQuad(v(o, -.03, o), v(1 - o, -.03, o), v(1 - o, -.03, 1 - o), v(o, -.03, 1 - o), downMask, highlight));
        }
        if (northMask != null) {
            quads.add(createQuad(v(1 - o, 1 - o, -.03), v(1 - o, o, -.03), v(o, o, -.03), v(o, 1 - o, -.03), northMask, highlight));
        }
        if (southMask != null) {
            quads.add(createQuad(v(o, 1 - o, 1.03), v(o, o, 1.03), v(1 - o, o, 1.03), v(1 - o, 1 - o, 1.03), southMask, highlight));
        }
        if (westMask != null) {
            quads.add(createQuad(v(-.03, 1 - o, o), v(-.03, o, o), v(-.03, o, 1 - o), v(-.03, 1 - o, 1 - o), westMask, highlight));
        }
        if (eastMask != null) {
            quads.add(createQuad(v(1.03, 1 - o, 1 - o), v(1.03, o, 1 - o), v(1.03, o, o), v(1.03, 1 - o, o), eastMask, highlight));
        }

        Mode itemUp = extraData.get(ITEM_UP);
        Mode itemDown = extraData.get(ITEM_DOWN);
        Mode itemNorth = extraData.get(ITEM_NORTH);
        Mode itemSouth = extraData.get(ITEM_SOUTH);
        Mode itemWest = extraData.get(ITEM_WEST);
        Mode itemEast = extraData.get(ITEM_EAST);

        upMask = getMask(itemUp);
        downMask = getMask(itemDown);
        northMask = getMask(itemNorth);
        southMask = getMask(itemSouth);
        westMask = getMask(itemWest);
        eastMask = getMask(itemEast);

        o = .3f;

        if (upMask != null) {
            quads.add(createQuad(v(o, 1.02, o), v(o, 1.02, 1 - o), v(1 - o, 1.02, 1 - o), v(1 - o, 1.02, o), upMask, highlight));
        }
        if (downMask != null) {
            quads.add(createQuad(v(o, -.02, o), v(1 - o, -.02, o), v(1 - o, -.02, 1 - o), v(o, -.02, 1 - o), downMask, highlight));
        }
        if (northMask != null) {
            quads.add(createQuad(v(1 - o, 1 - o, -.02), v(1 - o, o, -.02), v(o, o, -.02), v(o, 1 - o, -.02), northMask, highlight));
        }
        if (southMask != null) {
            quads.add(createQuad(v(o, 1 - o, 1.02), v(o, o, 1.02), v(1 - o, o, 1.02), v(1 - o, 1 - o, 1.02), southMask, highlight));
        }
        if (westMask != null) {
            quads.add(createQuad(v(-.02, 1 - o, o), v(-.02, o, o), v(-.02, o, 1 - o), v(-.02, 1 - o, 1 - o), westMask, highlight));
        }
        if (eastMask != null) {
            quads.add(createQuad(v(1.02, 1 - o, 1 - o), v(1.02, o, 1 - o), v(1.02, o, o), v(1.02, 1 - o, o), eastMask, highlight));
        }


        Mode fluidUp = extraData.get(FLUID_UP);
        Mode fluidDown = extraData.get(FLUID_DOWN);
        Mode fluidNorth = extraData.get(FLUID_NORTH);
        Mode fluidSouth = extraData.get(FLUID_SOUTH);
        Mode fluidWest = extraData.get(FLUID_WEST);
        Mode fluidEast = extraData.get(FLUID_EAST);

        upMask = getMask(fluidUp);
        downMask = getMask(fluidDown);
        northMask = getMask(fluidNorth);
        southMask = getMask(fluidSouth);
        westMask = getMask(fluidWest);
        eastMask = getMask(fluidEast);

        o = .1f;

        if (upMask != null) {
            quads.add(createQuad(v(o, 1.01, o), v(o, 1.01, 1 - o), v(1 - o, 1.01, 1 - o), v(1 - o, 1.01, o), upMask, highlight));
        }
        if (downMask != null) {
            quads.add(createQuad(v(o, -.01, o), v(1 - o, -.01, o), v(1 - o, -.01, 1 - o), v(o, -.01, 1 - o), downMask, highlight));
        }
        if (northMask != null) {
            quads.add(createQuad(v(1 - o, 1 - o, -.01), v(1 - o, o, -.01), v(o, o, -.01), v(o, 1 - o, -.01), northMask, highlight));
        }
        if (southMask != null) {
            quads.add(createQuad(v(o, 1 - o, 1.01), v(o, o, 1.01), v(1 - o, o, 1.01), v(1 - o, 1 - o, 1.01), southMask, highlight));
        }
        if (westMask != null) {
            quads.add(createQuad(v(-.01, 1 - o, o), v(-.01, o, o), v(-.01, o, 1 - o), v(-.01, 1 - o, 1 - o), westMask, highlight));
        }
        if (eastMask != null) {
            quads.add(createQuad(v(1.01, 1 - o, 1 - o), v(1.01, o, 1 - o), v(1.01, o, o), v(1.01, 1 - o, o), eastMask, highlight));
        }

        return quads;
    }

    private TextureAtlasSprite getMask(Mode mode) {
        switch (mode) {
            case MODE_INPUT -> {
                return getInputMask();
            }
            case MODE_BOTH -> {
                return getBothMask();
            }
            case MODE_OUTPUT -> {
                return getOutputMask();
            }
            case null, default -> {
                return null;
            }
        }
    }

    protected BakedQuad createQuad(Vec3 v1, Vec3 v2, Vec3 v3, Vec3 v4, TextureAtlasSprite sprite, float highlight) {
        Vec3 normal = v3.subtract(v2).cross(v1.subtract(v2)).normalize();
        QuadBakingVertexConsumer builder = new QuadBakingVertexConsumer();
        builder.setSprite(sprite);
        builder.setDirection(Direction.getNearest(normal.x, normal.y, normal.z));
        RenderHelper.putVertex(builder, normal, v1.x, v1.y, v1.z, 0.0F, 0.0F, sprite, highlight, highlight, highlight, highlight);
        RenderHelper.putVertex(builder, normal, v2.x, v2.y, v2.z, 0.0F, 1.0F, sprite, highlight, highlight, highlight, highlight);
        RenderHelper.putVertex(builder, normal, v3.x, v3.y, v3.z, 1.0F, 1.0F, sprite, highlight, highlight, highlight, highlight);
        RenderHelper.putVertex(builder, normal, v4.x, v4.y, v4.z, 1.0F, 0.0F, sprite, highlight, highlight, highlight, highlight);
        return builder.bakeQuad();
    }

    protected static Vec3 v(double x, double y, double z) {
        return new Vec3(x, y, z);
    }
}
