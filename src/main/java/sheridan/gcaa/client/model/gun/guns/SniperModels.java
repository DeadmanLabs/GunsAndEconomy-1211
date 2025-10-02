package sheridan.gcaa.client.model.gun.guns;

import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import sheridan.gcaa.GCAA;
import sheridan.gcaa.client.model.gun.IGunModel;
import sheridan.gcaa.client.model.gun.SniperModel;

@OnlyIn(Dist.CLIENT)
public class SniperModels {
    public static IGunModel AWP_MODEL = new SniperModel(
            ResourceLocation.fromNamespaceAndPath(GCAA.MODID, "model_assets/guns/awp/awp.geo.json"),
            ResourceLocation.fromNamespaceAndPath(GCAA.MODID, "model_assets/guns/awp/awp.animation.json"),
            ResourceLocation.fromNamespaceAndPath(GCAA.MODID, "model_assets/guns/awp/awp.png"));

    public static IGunModel FN_BALLISTA_MODEL = new SniperModel(
            ResourceLocation.fromNamespaceAndPath(GCAA.MODID, "model_assets/guns/fn_ballista/fn_ballista.geo.json"),
            ResourceLocation.fromNamespaceAndPath(GCAA.MODID, "model_assets/guns/fn_ballista/fn_ballista.animation.json"),
            ResourceLocation.fromNamespaceAndPath(GCAA.MODID, "model_assets/guns/fn_ballista/fn_ballista.png"))
            .setOptions("renderType", "solidMipMap");
}
