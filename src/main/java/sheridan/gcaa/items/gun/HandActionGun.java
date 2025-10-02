package sheridan.gcaa.items.gun;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import sheridan.gcaa.Clients;
import sheridan.gcaa.client.*;
import sheridan.gcaa.items.gun.propertyExtensions.HandActionExtension;
import sheridan.gcaa.utils.RenderAndMathUtils;

public class HandActionGun extends Gun{
    protected final HandActionExtension handActionExtension;

    public HandActionGun(GunProperties gunProperties, HandActionExtension extension) {
        super(gunProperties.addExtension(extension));
        this.handActionExtension = extension;
    }

    @Override
    public void onCraftedBy(ItemStack pStack, Level pLevel, Player pPlayer) {
        super.onCraftedBy(pStack, pLevel, pPlayer);
        CompoundTag tag = checkAndGet(pStack);
        if (!tag.contains("need_hand_action")) {
            tag.putBoolean("need_hand_action", false);
            pStack.set(sheridan.gcaa.items.ModDataComponents.GUN_DATA, tag);
        }
    }

    public boolean needHandAction(ItemStack itemStack) {
        CompoundTag tag = checkAndGet(itemStack);
        return tag.contains("need_hand_action") && tag.getBoolean("need_hand_action");
    }

    public void setNeedHandAction(ItemStack itemStack, boolean val) {
        CompoundTag tag = checkAndGet(itemStack);
        tag.putBoolean("need_hand_action", val);
        itemStack.set(sheridan.gcaa.items.ModDataComponents.GUN_DATA, tag);
    }

    @Override
    public void clientShoot(ItemStack stack, Player player, IGunFireMode fireMode) {
        super.clientShoot(stack, player, fireMode);
        afterClientShoot(stack, player, fireMode);
    }

    protected void afterClientShoot(ItemStack stack, Player player, IGunFireMode fireMode) {
        setNeedHandAction(stack, true);
        HandActionHandler.INSTANCE.setHandActionTask(getHandActionTask(stack, false));
    }

    @Override
    public void shoot(ItemStack stack, Player player, IGunFireMode fireMode, float spread) {
        super.shoot(stack, player, fireMode, spread);
        afterShoot(stack, player, fireMode, spread);
    }

    protected void afterShoot(ItemStack stack, Player player, IGunFireMode fireMode, float spread) {
        setNeedHandAction(stack, true);
    }

    @Override
    public IReloadTask getReloadingTask(ItemStack stack, Player player) {
        return new HandActionReloadingTask(stack, this);
    }

    public IHandActionTask getHandActionTask(ItemStack stack, boolean immediate) {
        return new HandActionTask(
                stack, this,
                immediate ? 0 : handActionExtension.startDelay,
                handActionExtension.length,
                Clients.MAIN_HAND_STATUS.ads ?
                        handActionExtension.adsHandActionAnimationName : handActionExtension.handActionAnimationName,
                handActionExtension.throwBulletShellDelay);
    }

    @Override
    public void reload(ItemStack stack, Player player) {
        super.reload(stack, player);
        setNeedHandAction(stack, false);
    }

    @Override
    public boolean shouldHandleAds(ItemStack stack) {
        if (!handActionExtension.allowAds) {
            boolean needHandAction = needHandAction(stack);
            if (needHandAction && !HandActionHandler.INSTANCE.hasTask()
                    && getAmmoLeft(stack) > 0 && HandActionHandler.INSTANCE.secondsSinceLastTask() > 0.5f) {
                HandActionHandler.INSTANCE.setHandActionTask(getHandActionTask(stack, true));
                return false;
            }
            if (HandActionHandler.INSTANCE.hasTask() && RenderAndMathUtils.secondsFromNow(Clients.lastShootMain()) > 0.5f) {
                return false;
            }
        } else {
            if (needHandAction(stack) && getAmmoLeft(stack) > 0 && HandActionHandler.INSTANCE.secondsSinceLastTask() > 0.5f) {
                HandActionHandler.INSTANCE.setHandActionTask(getHandActionTask(stack, true));
            }
        }
        return super.shouldHandleAds(stack);
    }
}
