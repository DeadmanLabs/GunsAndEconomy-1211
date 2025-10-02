package sheridan.gcaa.client.model.gun.guns;

import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import sheridan.gcaa.GCAA;
import sheridan.gcaa.client.model.gun.CommonRifleModel;
import sheridan.gcaa.client.model.gun.IGunModel;

@OnlyIn(Dist.CLIENT)
public class CommonSMGModels {
    public static final IGunModel MP5_MODEL = new CommonRifleModel(
            ResourceLocation.fromNamespaceAndPath(GCAA.MODID, "model_assets/guns/mp5/mp5.geo.json"),
            ResourceLocation.fromNamespaceAndPath(GCAA.MODID, "model_assets/guns/mp5/mp5.animation.json"),
            ResourceLocation.fromNamespaceAndPath(GCAA.MODID, "model_assets/guns/mp5/mp5.png"));

    public static final IGunModel ANNIHILATOR_MODEL = new CommonRifleModel(
            ResourceLocation.fromNamespaceAndPath(GCAA.MODID, "model_assets/guns/annihilator/annihilator.geo.json"),
            ResourceLocation.fromNamespaceAndPath(GCAA.MODID, "model_assets/guns/annihilator/annihilator.animation.json"),
            ResourceLocation.fromNamespaceAndPath(GCAA.MODID, "model_assets/guns/annihilator/annihilator.png"));
}
