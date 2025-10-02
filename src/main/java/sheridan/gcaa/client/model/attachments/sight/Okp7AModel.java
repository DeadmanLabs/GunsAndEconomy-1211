package sheridan.gcaa.client.model.attachments.sight;

import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import sheridan.gcaa.GCAA;
import sheridan.gcaa.client.model.CommonSightModel;

@OnlyIn(Dist.CLIENT)
public class Okp7AModel extends CommonSightModel {
    public Okp7AModel() {
        super(ResourceLocation.fromNamespaceAndPath(GCAA.MODID, "model_assets/attachments/sights/okp7_a.geo.json"),
                ResourceLocation.fromNamespaceAndPath(GCAA.MODID, "model_assets/attachments/sights/okp7_a.png"),
                ResourceLocation.fromNamespaceAndPath(GCAA.MODID, "model_assets/attachments/sights/okp7_a.png"),
                ResourceLocation.fromNamespaceAndPath(GCAA.MODID, "model_assets/attachments/sights/okp7.png"),
                "low", "min_z_dis", "body", "crosshair_rifle", 0.2f);
    }
}
