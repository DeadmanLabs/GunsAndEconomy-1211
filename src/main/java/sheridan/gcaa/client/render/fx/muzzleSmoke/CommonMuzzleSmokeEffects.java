package sheridan.gcaa.client.render.fx.muzzleSmoke;

import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.joml.Vector2f;
import sheridan.gcaa.GCAA;

@OnlyIn(Dist.CLIENT)
public class CommonMuzzleSmokeEffects {
    public static final MuzzleSmoke COMMON = new MuzzleSmoke(150, 3.5f, 3f, new Vector2f(0.9f, 0.6f),
            ResourceLocation.fromNamespaceAndPath(GCAA.MODID, "textures/fx/muzzle_smoke/common_0.png"), 4).randomRotate();
}
