package sheridan.gcaa.items.ammunition;

import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.apache.commons.lang3.NotImplementedException;
import sheridan.gcaa.items.gun.Gun;
import sheridan.gcaa.items.gun.IGun;

import java.util.*;

public class AmmunitionHandler {

    public static void checkAndUpdateAmmunitionBind(Player player, ItemStack gunStack, IGun gun) {
        String modsUUID = gun.getSelectedAmmunitionTypeID(gunStack);
        NonNullList<ItemStack> items = player.getInventory().items;
        IAmmunition gunAmmunition = gun.getGunProperties().caliber.ammunition;
        ItemStack useAmmo = null;
        for (ItemStack stack : items) {
            if (stack.getItem() instanceof IAmmunition ammo && ammo == gunAmmunition) {
                if (useAmmo == null) {
                    useAmmo = stack;
                }
                String stackAmmoModsUUID = ammo.getModsUUID(stack);
                if (Objects.equals(stackAmmoModsUUID, modsUUID)) {
                    useAmmo = stack;
                    break;
                }
            }
        }
        if (useAmmo != null) {
            gun.bindAmmunition(gunStack, useAmmo, gunAmmunition);
        }
    }

    public static void manageAmmunition(Player player, ItemStack ammunitionStack) {
        if (ammunitionStack.getItem() instanceof IAmmunition ammunition) {
            NonNullList<ItemStack> items = player.getInventory().items;
            int totalCount = 0;
            CompoundTag mods = ammunition.get().checkAndGet(ammunitionStack);
            for (int i = 0; i < items.size(); i ++) {
                ItemStack itemStack = items.get(i);
                if (itemStack.getItem() instanceof IAmmunition stackAmmunition) {
                    stackAmmunition.get().checkAndGet(itemStack);
                    if (ammunition.canMerge(ammunitionStack, itemStack)) {
                        totalCount += ammunition.getAmmoLeft(itemStack);
                        items.set(i, new ItemStack(Items.AIR));
                    }
                }
            }
            if (totalCount == 0) {
                return;
            }
            int capacity = ammunition.getMaxCapacity(ammunitionStack);
            while (totalCount > 0) {
                int ammoCount = Math.min(totalCount, capacity);
                ItemStack itemStack = new ItemStack(ammunitionStack.getItem());
                itemStack.set(sheridan.gcaa.items.ModDataComponents.AMMO_DATA, mods.copy());
                ammunition.setAmmoLeft(itemStack, ammoCount);
                totalCount -= ammoCount;
                if (!player.addItem(itemStack)) {
                    player.drop(itemStack, false);
                }
            }
        }
    }

    public static void andAmmunition(Player player, IAmmunition ammunition, int count) {
        NonNullList<ItemStack> items = player.getInventory().items;
        int left = count;
        for (ItemStack itemStack : items) {
            if (itemStack.getItem() instanceof IAmmunition ammo && ammo == ammunition && Objects.equals(ammo.getModsUUID(itemStack), "")) {
                int capacityLeft = ammo.getMaxCapacity(itemStack) - ammo.getAmmoLeft(itemStack);
                if (capacityLeft > 0) {
                    int add = Math.min(capacityLeft, left);
                    ammo.setAmmoLeft(itemStack, ammo.getAmmoLeft(itemStack) + add);
                    left -= add;
                    if (left == 0) {
                        return;
                    }
                }
            }
        }
        if (ammunition instanceof Ammunition ammo) {
            ItemStack tempStack = new ItemStack(ammo);
            int capacity = ammo.getMaxDamage(tempStack);
            while (left > 0) {
                int ammoCount = Math.min(left, capacity);
                ItemStack itemStack = new ItemStack(ammo);
                ammunition.setAmmoLeft(itemStack, ammoCount);
                if (!player.addItem(itemStack)) {
                    player.drop(itemStack, false);
                }
                left -= ammoCount;
            }
        }
    }

    public static void andAmmunition(Player player, IAmmunition ammunition, ItemStack ammoTemp) {
        throw new NotImplementedException();
    }

    public static void andAmmunition(Player player, IAmmunition ammunition, CompoundTag modsTag) {
        throw new NotImplementedException();
    }

    public static void andAmmunition(Player player, IAmmunition ammunition, List<IAmmunitionMod> mods) {
        throw new NotImplementedException();
    }

    /*
    * 清空枪械中的弹药，并将弹药还给玩家
    * */
    public static void clearGun(Player player, IGun gun, ItemStack gunStack) {
        if (gunStack != player.getMainHandItem()) {
            return;
        }
        int count = gun.getAmmoLeft(gunStack);
        if (count == 0) {
            return;
        }
        IAmmunition ammunition = gun.getGunProperties().caliber.ammunition;
        List<IAmmunitionMod> mods = new ArrayList<>();
        CompoundTag ammunitionData = gun.getAmmunitionData(gunStack);
        if (ammunitionData.contains("using")) {
            CompoundTag modsTag = ammunitionData.getCompound("using").getCompound("mods");
            mods = ammunition.getMods(modsTag);
        }
        int ammoBoxSize = 1;
        if (ammunition instanceof Ammunition ammo) {
            ItemStack tempStack = new ItemStack(ammo);
            ammoBoxSize = ammo.getMaxDamage(tempStack);
        }
        int boxCount = count / ammoBoxSize;
        int left = count % ammoBoxSize;
        List<ItemStack> ammoList = new ArrayList<>();
        if (ammunition instanceof Ammunition ammo) {
            if (boxCount > 0) {
                for (int i = 0; i < boxCount; i++) {
                    ItemStack itemStack = new ItemStack(ammo);
                    ammo.checkAndGet(itemStack);
                    if (mods.size() > 0) {
                        ammunition.addMods(mods, itemStack);
                    }
                    ammoList.add(itemStack);
                }
            }
            if (left > 0) {
                ItemStack itemStack = new ItemStack(ammo);
                ammo.checkAndGet(itemStack);
                ammunition.setAmmoLeft(itemStack, left);
                if (mods.size() > 0) {
                    ammunition.addMods(mods, itemStack);
                }
                ammoList.add(itemStack);
            }
        }
        for (ItemStack stack : ammoList) {
            if (!player.addItem(stack)) {
                player.drop(stack, false);
            }
        }
        gun.setAmmoLeft(gunStack, 0);
    }

    public static void reloadFor(Player player, ItemStack gunStack, IGun gun, int exceptedReloadNum) {
        if (exceptedReloadNum <= 0 || gunStack != player.getMainHandItem()) {
            return;
        }
        if (gun.getGun().isNotUsingSelectedAmmo(gunStack)) {
            clearGun(player, gun, gunStack);
        }
        exceptedReloadNum = Math.min(exceptedReloadNum, gun.getMagSize(gunStack) - gun.getAmmoLeft(gunStack));

        // If gun has Infinity enchantment, reload without consuming ammo
        if (Gun.hasInfinityEnchantment(gunStack)) {
            gun.setAmmoLeft(gunStack, gun.getAmmoLeft(gunStack) + exceptedReloadNum);
            CompoundTag ammunitionData = gun.getAmmunitionData(gunStack);
            CompoundTag selected = ammunitionData.getCompound("selected");
            ammunitionData.put("using", selected.copy());
            return;
        }

        int findCount = 0;
        int exceptedReloadLeft = exceptedReloadNum;
        boolean isAmmunitionBind = gun.getGun().isAmmunitionBind(gunStack);
        NonNullList<ItemStack> items = player.getInventory().items;
        IAmmunition gunAmmunition = gun.getGunProperties().caliber.ammunition;
        for (int i = 0; i < items.size(); i++) {
            ItemStack stack = items.get(i);
            if (stack.getItem() instanceof IAmmunition ammunition) {
                boolean isSameAmmunition = ammunition == gunAmmunition;
                if (!isSameAmmunition) {
                    continue;
                }
                if (!isAmmunitionBind) {
                    gun.bindAmmunition(gunStack, stack, gunAmmunition);
                }
                if (Objects.equals(ammunition.getModsUUID(stack), gun.getSelectedAmmunitionTypeID(gunStack))) {
                    int ammoLeft = ammunition.getAmmoLeft(stack);
                    if (ammoLeft >= exceptedReloadLeft) {
                        findCount += exceptedReloadLeft;
                        if (ammoLeft - exceptedReloadLeft == 0) {
                            items.set(i, new ItemStack(Items.AIR));
                        } else {
                            ammunition.setAmmoLeft(stack, ammoLeft - exceptedReloadLeft);
                        }
                        break;
                    } else {
                        findCount += ammoLeft;
                        exceptedReloadLeft -= ammoLeft;
                        items.set(i, new ItemStack(Items.AIR));
                    }
                }
            }
        }
        if (findCount != 0) {
            gun.setAmmoLeft(gunStack, gun.getAmmoLeft(gunStack) + findCount);
            CompoundTag ammunitionData = gun.getAmmunitionData(gunStack);
            CompoundTag selected = ammunitionData.getCompound("selected");
            ammunitionData.put("using", selected.copy());
        }
    }

    public static int getAmmunitionCount(ItemStack gunStack, IGun gun, Player player) {
        int findCount = 0;
        NonNullList<ItemStack> items = player.getInventory().items;
        IAmmunition gunAmmunition = gun.getGunProperties().caliber.ammunition;
        for (ItemStack stack : items) {
            if (stack.getItem() instanceof IAmmunition ammunition &&
                    ammunition == gunAmmunition && Objects.equals(ammunition.getModsUUID(stack), gun.getSelectedAmmunitionTypeID(gunStack))) {
                int ammoLeft = ammunition.getAmmoLeft(stack);
                findCount += ammoLeft;
            }
        }
        return findCount;
    }

    public static boolean hasAmmunitionItem(IAmmunition ammunition, Player player) {
        NonNullList<ItemStack> items = player.getInventory().items;
        for (ItemStack stack : items) {
            if (stack.getItem() == ammunition) {
                return ammunition.getAmmoLeft(stack) > 0;
            }
        }
        return false;
    }

    public static boolean hasAmmunition(IGun gun, ItemStack gunStack, IAmmunition ammunition, Player player) {
        // If gun has Infinity enchantment, always return true (no ammo required in inventory)
        if (Gun.hasInfinityEnchantment(gunStack)) {
            return true;
        }

        NonNullList<ItemStack> items = player.getInventory().items;
        boolean isAmmunitionBind = gun.getGun().isAmmunitionBind(gunStack);
        for (ItemStack stack : items) {
            if (stack.getItem() == ammunition) {
                if (isAmmunitionBind) {
                    if (Objects.equals(ammunition.getModsUUID(stack), gun.getSelectedAmmunitionTypeID(gunStack))) {
                        return ammunition.getAmmoLeft(stack) > 0;
                    }
                } else {
                    return ammunition.getAmmoLeft(stack) > 0;
                }
            }
        }
        return false;
    }
}
