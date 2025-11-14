package sheridan.gcaa.items;

import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public class NoRepairNoEnchantmentItem extends BaseItem{
    public NoRepairNoEnchantmentItem(Properties properties) {
        super(properties);
    }

    public NoRepairNoEnchantmentItem() {
        super();
    }

    // Allow enchantments - enchantability value of 1 (standard for tools/armor)
    // This allows KubeJS and other mods to control which enchantments can be applied
    @Override
    public int getEnchantmentValue() {
        return 1;
    }

    // Allow enchanting books to be applied via anvil
    @Override
    public boolean isBookEnchantable(ItemStack stack, ItemStack book) {
        return true;
    }

    // Allow items to be enchanted in enchanting table
    @Override
    public boolean isEnchantable(@NotNull ItemStack itemStack) {
        return true;
    }

    // Keep repair blocking - guns shouldn't be repairable with materials
    @Override
    public boolean isRepairable(@NotNull ItemStack stack) {
        return false;
    }
}
