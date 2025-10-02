package sheridan.gcaa.service.product;

import com.google.gson.JsonObject;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import sheridan.gcaa.items.ammunition.Ammunition;
import sheridan.gcaa.items.ammunition.IAmmunition;
import sheridan.gcaa.items.ammunition.IAmmunitionMod;
import sheridan.gcaa.items.gun.IGun;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AmmunitionProduct extends CommonProduct implements IRecycleProduct{
    private static final Map<Ammunition, AmmunitionProduct> AMMUNITION_PRODUCT_MAP = new HashMap<>();
    public IAmmunition ammunition;

    public AmmunitionProduct(Ammunition ammunition, int price) {
        super(ammunition, price);
        this.ammunition =  ammunition;
        AMMUNITION_PRODUCT_MAP.put(ammunition, this);
    }

    @Override
    public void onRemoveRegistry() {
        if (ammunition instanceof Ammunition ammo) {
            AMMUNITION_PRODUCT_MAP.remove(ammo);
        }
    }

    public static AmmunitionProduct get(Ammunition ammunition) {
        return AMMUNITION_PRODUCT_MAP.get(ammunition);
    }

    @Override
    public int getMaxBuyCount() {
        if (ammunition instanceof Ammunition ammo) {
            ItemStack tempStack = new ItemStack(ammo);
            return ammo.getMaxDamage(tempStack);
        }
        return 1;
    }

    @Override
    public ItemStack getItemStack(int count) {
        if (ammunition instanceof Ammunition ammo) {
            ItemStack stack = new ItemStack(ammo);
            ammo.checkAndGet(stack);
            ammunition.setAmmoLeft(stack, count);
            return stack;
        }
        return ItemStack.EMPTY;
    }

    @Override
    public ItemStack getDisplayItem() {
        return getItemStack(getMaxBuyCount());
    }

    @Override
    public int getPrice(ItemStack itemStack) {
        double singlePrice = getDefaultPrice() / (double) getMaxBuyCount();
        int ammoLeft = ammunition.getAmmoLeft(itemStack);
        return (int) (ammoLeft * singlePrice);
    }

    @Override
    public int getMinBuyCount() {
        return Math.min((int) Math.ceil(getMaxBuyCount() / (double) getDefaultPrice()), getMaxBuyCount());
    }

    @Override
    public long getRecyclePrice(ItemStack itemStack, List<Component> tooltip) {
        if (itemStack.getItem() instanceof IGun gun) {
            int ammoLeft = gun.getAmmoLeft(itemStack);
            if (ammoLeft > 0) {
                double singlePrice = getDefaultPrice() / (double) getMaxBuyCount();
                long price = (int) (ammoLeft * singlePrice);
                StringBuilder name = new StringBuilder(Component.translatable(((Ammunition)ammunition).getDescriptionId()).getString());
                CompoundTag usingAmmunitionData = gun.getUsingAmmunitionData(itemStack);
                if (usingAmmunitionData != null && usingAmmunitionData.contains("mods")) {
                    CompoundTag modTag = usingAmmunitionData.getCompound("mods");
                    List<IAmmunitionMod> mods = ammunition.getMods(modTag);
                    long modPrice = 0;
                    if (mods.size() > 0) {
                        name.append("-");
                        for (IAmmunitionMod mod : mods) {
                            modPrice += mod.getPrice();
                            name.append(Component.translatable(mod.getDescriptionId()).getString()).append(" ");
                        }
                    }
                    modPrice *= ((double) ammoLeft / ((Ammunition)ammunition).getMaxDamage(itemStack));
                    price += modPrice;
                }
                tooltip.add(Component.literal(name.toString()).append(" x " + ammoLeft).append(" = " + price));
                return price;
            }
        } else if (itemStack.getItem() instanceof IAmmunition ammunition && ammunition == this.ammunition) {
            List<IAmmunitionMod> mods = ammunition.getMods(itemStack);
            int price = getPrice(itemStack);
            int ammoLeft = ammunition.getAmmoLeft(itemStack);
            StringBuilder name = new StringBuilder(Component.translatable(((Ammunition)ammunition).getDescriptionId()).getString());
            if (!mods.isEmpty() && ammoLeft > 0) {
                long modPrice = 0;
                name.append("-");
                for (IAmmunitionMod mod : mods) {
                    modPrice += mod.getPrice();
                    name.append(Component.translatable(mod.getDescriptionId()).getString()).append(" ");
                }
                modPrice *= ((double) ammoLeft / ((Ammunition)ammunition).getMaxDamage(itemStack));
                price += modPrice;
            }
            tooltip.add(Component.literal(name.toString()).append(" x " + ammoLeft).append(" = " + price));
            return price;
        }
        return 0;
    }

    @Override
    public IProduct get() {
        return this;
    }

    @Override
    public void loadData(JsonObject jsonObject) {
        super.loadData(jsonObject);
        if (item instanceof Ammunition ammunition) {
            this.ammunition = ammunition;
            AMMUNITION_PRODUCT_MAP.put(ammunition, this);
        }
    }
}
