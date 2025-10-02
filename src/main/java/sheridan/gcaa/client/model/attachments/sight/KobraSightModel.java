package sheridan.gcaa.client.model.attachments.sight;

import net.minecraft.resources.ResourceLocation;
import sheridan.gcaa.GCAA;
import sheridan.gcaa.client.model.CommonSightModel;

public class KobraSightModel extends CommonSightModel {
    public KobraSightModel() {
        super(ResourceLocation.fromNamespaceAndPath(GCAA.MODID, "model_assets/attachments/sights/kobra_sight.geo.json"),
                ResourceLocation.fromNamespaceAndPath(GCAA.MODID, "model_assets/attachments/sights/kobra_sight.png"),
                ResourceLocation.fromNamespaceAndPath(GCAA.MODID, "model_assets/attachments/sights/kobra_sight.png"),
                ResourceLocation.fromNamespaceAndPath(GCAA.MODID, "model_assets/attachments/sights/kobra.png"),
                "low", "min_z_dis", "body", "crosshair", 0.11f);
    }
}
