package sheridan.gcaa.service.product;

import com.google.gson.JsonObject;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import sheridan.gcaa.attachmentSys.common.AttachmentsHandler;
import sheridan.gcaa.items.ammunition.Ammunition;
import sheridan.gcaa.items.ammunition.IAmmunition;
import sheridan.gcaa.items.attachments.IAttachment;
import sheridan.gcaa.items.gun.Gun;
import sheridan.gcaa.items.gun.IGun;

import java.util.List;

public class GunProduct extends CommonProduct implements IRecycleProduct{
    public Gun gun;
    public GunProduct(Gun gun, int price) {
        super(gun, price);
        this.gun = gun;
    }

    @Override
    public int getMaxBuyCount() {
        return 1;
    }

    public IGun getGun() {
        return ((Gun)getItem());
    }

    @Override
    public long getRecyclePrice(ItemStack gunStack, List<Component> tooltip) {
        long price = getPrice(gunStack);
        IGun gun = getGun();
        tooltip.add(Component.translatable(gun.getGun().getDescriptionId()).append(" = " + price));
        List<IAttachment> attachments = AttachmentsHandler.INSTANCE.getAttachments(gunStack, gun);
        for (IAttachment attachment : attachments) {
            AttachmentProduct attachmentProduct = AttachmentProduct.get(attachment.get());
            if (attachmentProduct != null) {
                price += attachmentProduct.getRecyclePrice(gunStack, tooltip);
            }
        }
        int ammoLeft = gun.getAmmoLeft(gunStack);
        if (ammoLeft > 0) {
            IAmmunition ammunition = gun.getGunProperties().caliber.ammunition;
            if (ammunition instanceof Ammunition ammo) {
                AmmunitionProduct ammunitionProduct = AmmunitionProduct.get(ammo);
                if (ammunitionProduct != null) {
                    price += ammunitionProduct.getRecyclePrice(gunStack, tooltip);
                }
            }
        }
        return price;
    }

    @Override
    public IProduct get() {
        return this;
    }

    public String getIntroduction() {
        return Component.translatable(gun.getDescriptionId() + ".introduction").getString();
    }

    @Override
    public void loadData(JsonObject jsonObject) {
        super.loadData(jsonObject);
        if (this.item instanceof Gun gun) {
            this.gun = gun;
        }
    }
}
