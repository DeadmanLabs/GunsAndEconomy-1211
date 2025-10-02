package sheridan.gcaa.client.model.gun.guns;

import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import sheridan.gcaa.GCAA;
import sheridan.gcaa.client.model.gun.GlockModel;
import sheridan.gcaa.client.model.gun.IGunModel;
import sheridan.gcaa.client.model.gun.RevolverWithLoaderModel;

@OnlyIn(Dist.CLIENT)
public class PistolModels {

    public static final IGunModel G19_MODEL = new GlockModel(
            ResourceLocation.fromNamespaceAndPath(GCAA.MODID, "model_assets/guns/g19/g19.geo.json"),
            ResourceLocation.fromNamespaceAndPath(GCAA.MODID, "model_assets/guns/g19/g19.animation.json"),
            ResourceLocation.fromNamespaceAndPath(GCAA.MODID, "model_assets/guns/g19/g19.png"),
            ResourceLocation.fromNamespaceAndPath(GCAA.MODID, "model_assets/guns/g19/g19_low.geo.json"),
            ResourceLocation.fromNamespaceAndPath(GCAA.MODID, "model_assets/guns/g19/g19_low.png"),
            5, 0.4f, 0.28f, 4.5f);

    public static final IGunModel PYTHON_357_MODEL = new RevolverWithLoaderModel(
            ResourceLocation.fromNamespaceAndPath(GCAA.MODID, "model_assets/guns/python_357/python_357.geo.json"),
            ResourceLocation.fromNamespaceAndPath(GCAA.MODID, "model_assets/guns/python_357/python_357.animation.json"),
            ResourceLocation.fromNamespaceAndPath(GCAA.MODID, "model_assets/guns/python_357/python_357.png"),
            ResourceLocation.fromNamespaceAndPath(GCAA.MODID, "model_assets/guns/python_357/python_357_low.geo.json"),
            ResourceLocation.fromNamespaceAndPath(GCAA.MODID, "model_assets/guns/python_357/python_357_low.png"),
            6, 45);

    public static final IGunModel FN57_MODEL = new GlockModel(
            ResourceLocation.fromNamespaceAndPath(GCAA.MODID, "model_assets/guns/fn57/fn57.geo.json"),
            ResourceLocation.fromNamespaceAndPath(GCAA.MODID, "model_assets/guns/fn57/fn57.animation.json"),
            ResourceLocation.fromNamespaceAndPath(GCAA.MODID, "model_assets/guns/fn57/fn57.png"),
            7, 0.5f, 0, 0f);

}
