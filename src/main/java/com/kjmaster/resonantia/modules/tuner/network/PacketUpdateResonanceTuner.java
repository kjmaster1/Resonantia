package com.kjmaster.resonantia.modules.tuner.network;

import com.kjmaster.resonantia.modules.tuner.items.ResonanceTunerItem;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import static com.kjmaster.resonantia.Resonantia.MODID;

public record PacketUpdateResonanceTuner(ItemStack stack) implements CustomPacketPayload {

    public static final ResourceLocation ID = ResourceLocation.fromNamespaceAndPath(MODID, "update_resonance_tuner");
    public static final Type<PacketUpdateResonanceTuner> TYPE = new Type<>(ID);

    public static final StreamCodec<RegistryFriendlyByteBuf, PacketUpdateResonanceTuner> CODEC = StreamCodec.composite(
            ItemStack.STREAM_CODEC, PacketUpdateResonanceTuner::stack,
            PacketUpdateResonanceTuner::new);

    public static PacketUpdateResonanceTuner create(ItemStack stack) {
        return new PacketUpdateResonanceTuner(stack);
    }

    private boolean isValidItem(ItemStack stack) {
        return stack.getItem() instanceof ResonanceTunerItem;
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public void handle(IPayloadContext ctx) {
        ctx.enqueueWork(() -> {
            Player player = ctx.player();
            ItemStack heldItem = player.getItemInHand(InteractionHand.MAIN_HAND);
            if (heldItem.isEmpty()) {
                return;
            }
            // To avoid people messing with packets
            if (isValidItem(heldItem) && isValidItem(this.stack)) {
                player.setItemInHand(InteractionHand.MAIN_HAND, this.stack);
            }
        });
    }
}
