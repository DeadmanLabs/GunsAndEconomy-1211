package sheridan.gcaa.service.product;

import com.google.gson.JsonObject;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import sheridan.gcaa.items.attachments.Attachment;
import sheridan.gcaa.items.attachments.IAttachment;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AttachmentProduct extends CommonProduct implements IRecycleProduct {
    private static final Map<Attachment, AttachmentProduct> ATTACHMENT_PRODUCT_MAP = new HashMap<>();
    public IAttachment attachment;

    public AttachmentProduct(Attachment attachment, int price) {
        super(attachment, price);
        ATTACHMENT_PRODUCT_MAP.put(attachment, this);
        this.attachment = attachment;
    }

    @Override
    public void onRemoveRegistry() {
        if (attachment instanceof Attachment attach) {
            ATTACHMENT_PRODUCT_MAP.remove(attach);
        }
    }

    public static AttachmentProduct get(Attachment attachment) {
        return ATTACHMENT_PRODUCT_MAP.get(attachment);
    }

    @Override
    public int getMaxBuyCount() {
        return 1;
    }

    @Override
    public long getRecyclePrice(ItemStack gunStack, List<Component> tooltip) {
        if (attachment instanceof Attachment attach) {
            tooltip.add(Component.translatable(attach.getDescriptionId()).append(" = " + getDefaultPrice()));
        }
        return getDefaultPrice();
    }

    @Override
    public IProduct get() {
        return this;
    }

    @Override
    public void loadData(JsonObject jsonObject) {
        super.loadData(jsonObject);
        if (item instanceof Attachment attachment) {
            this.attachment = attachment;
            ATTACHMENT_PRODUCT_MAP.put(attachment, this);
        }
    }
}