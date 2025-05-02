package com.kjmaster.resonantia.modules.tuner.items;

import com.kjmaster.resonantia.api.frequency.CapabilityFrequency;
import com.kjmaster.resonantia.api.frequency.IFrequency;
import com.kjmaster.resonantia.api.frequency.ItemFrequency;
import com.kjmaster.resonantia.modules.tuner.client.GuiResonanceTuner;
import com.kjmaster.resonantia.setup.Registration;
import mcjty.lib.items.BaseItem;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jetbrains.annotations.NotNull;

public class ResonanceTunerItem extends BaseItem {

    public ResonanceTunerItem(Properties properties) {
        super(properties);
    }

    @Override
    public @NotNull InteractionResult onItemUseFirst(@NotNull ItemStack stack, UseOnContext context) {
        Level level = context.getLevel();
        if (level.isClientSide()) return InteractionResult.SUCCESS;

        BlockPos pos = context.getClickedPos();
        Player player = context.getPlayer();

        BlockEntity be = level.getBlockEntity(pos);
        IFrequency frequency = level.getCapability(CapabilityFrequency.FREQUENCY_CAPABILITY, pos, null, be, null);

        if (frequency != null && player != null) {
            int currentFrequency = frequency.getFrequency();

            if (player.isShiftKeyDown()) {
                player.displayClientMessage(Component.literal("Frequency: " + currentFrequency), true);
            } else {
                int newFreq = getFrequency(stack);
                frequency.setFrequency(newFreq);
                player.displayClientMessage(Component.literal("Set frequency to: " + newFreq), true);
            }
            return InteractionResult.SUCCESS;
        }

        return InteractionResult.PASS;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level world, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (world.isClientSide) {
            GuiResonanceTuner.open();
            return new InteractionResultHolder<>(InteractionResult.SUCCESS, stack);
        }
        return new InteractionResultHolder<>(InteractionResult.SUCCESS, stack);
    }

    public static int getFrequency(ItemStack stack) {
        return stack.getOrDefault(Registration.ITEM_FREQUENCY.get(), new ItemFrequency(0)).frequency();
    }

    public static void setFrequency(ItemStack stack, int frequency) {
        stack.set(Registration.ITEM_FREQUENCY.get(), new ItemFrequency(frequency));
    }

}
