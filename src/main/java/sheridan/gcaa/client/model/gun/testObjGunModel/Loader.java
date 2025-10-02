package sheridan.gcaa.client.model.gun.testObjGunModel;

import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.client.model.obj.ObjLoader;
import net.neoforged.neoforge.client.model.obj.ObjModel;

public class Loader {
    public static void load(ResourceLocation location) {
        ObjModel objModel = ObjLoader.INSTANCE.loadModel(new ObjModel.ModelSettings(location, true, true, true, true, null));

    }
}
