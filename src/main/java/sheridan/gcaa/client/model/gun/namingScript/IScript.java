package sheridan.gcaa.client.model.gun.namingScript;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import sheridan.gcaa.client.model.gun.GunModel;
import sheridan.gcaa.client.model.modelPart.ModelPart;
import sheridan.gcaa.client.render.GunRenderContext;

@OnlyIn(Dist.CLIENT)
public interface IScript<T> {
    boolean value(GunRenderContext context);
    boolean valueLowQuality(GunRenderContext context);
    /**
     * return null if the script is invalid!!!
     * */
    T parse(String script, GunModel gunModel);

    default ModelPart getFrontDepend() {return null;}
}
