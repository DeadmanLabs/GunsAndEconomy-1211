package sheridan.gcaa.items;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.TooltipFlag;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class UnknownAttachment extends NoRepairNoEnchantmentItem{
    public UnknownAttachment() {}

    public static ItemStack get(String id) {
        ItemStack stack = new ItemStack(ModItems.UNKNOWN_ATTACHMENT.get(), 1);
        stack.set(ModDataComponents.ATTACHMENT_ID, id);
        return stack;
    }

    @Override
    public void appendHoverText(ItemStack pStack, @NotNull Item.TooltipContext pLevel, List<Component> pTooltipComponents, TooltipFlag pIsAdvanced) {
        super.appendHoverText(pStack, pLevel, pTooltipComponents, pIsAdvanced);
        String id = pStack.get(ModDataComponents.ATTACHMENT_ID);
        if (id == null || id.isEmpty()) {
            pTooltipComponents.add(Component.translatable("tooltip.item.useless"));
        } else {
            String s = Component.translatable("tooltip.item.unknown_attachment_1").getString();
            String s2 = Component.translatable("tooltip.item.unknown_attachment_3").getString();
            s = s.replace("$id", id);
            s2 = s2.replace("$id", id);
            pTooltipComponents.add(Component.literal(s));
            pTooltipComponents.add(Component.translatable("tooltip.item.unknown_attachment_2"));
            pTooltipComponents.add(Component.literal(s2));
        }
    }
}
