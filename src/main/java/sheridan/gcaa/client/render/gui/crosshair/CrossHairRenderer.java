package sheridan.gcaa.client.render.gui.crosshair;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import sheridan.gcaa.Clients;
import sheridan.gcaa.GCAA;
import sheridan.gcaa.items.gun.IGun;

@OnlyIn(Dist.CLIENT)
public class CrossHairRenderer{
    private static final float BASE_SCALE = 3;
    private static final float SPREAD_SIZE_FACTOR = 5f;
    public static final CrossHairRenderer INSTANCE = new CrossHairRenderer();
    public static final ResourceLocation CROSSHAIR = ResourceLocation.fromNamespaceAndPath(GCAA.MODID, "textures/gui/crosshair/crosshair.png");
    public static final ResourceLocation GUI_ICONS_LOCATION_MINECRAFT = ResourceLocation.parse("textures/gui/icons.png");
    private static final ResourceLocation FEED_BACK = ResourceLocation.fromNamespaceAndPath(GCAA.MODID, "textures/gui/crosshair/feed_back.png");
    private static float tempSpread;

    public void render(int singleQuadSize, IGun gun, GuiGraphics guiGraphics, Player player, ItemStack itemStack, float particleTick, boolean feedBack, boolean headShot) {
        int index = gun.getGun().getCrosshairType();
        if (index == -1) {
            defaultCrosshair(guiGraphics);
            return;
        }
        index = Mth.clamp(index, 0, 5);
        int textureSize = singleQuadSize * 5;
        int partSize = singleQuadSize - 1;
        float vOffset = singleQuadSize * index;
        int centerX = (int) ((guiGraphics.guiWidth() - partSize) / 2f);
        int centerY = (int) ((guiGraphics.guiHeight() - partSize) / 2f);
        int spread = (int) (Clients.MAIN_HAND_STATUS.spread * SPREAD_SIZE_FACTOR + BASE_SCALE + partSize / 2f);
        int currentSpread = (int) Mth.lerp(particleTick, tempSpread, spread);
        tempSpread = spread;
        RenderSystem.enableBlend();
        RenderSystem.blendFuncSeparate(GlStateManager.SourceFactor.ONE_MINUS_DST_COLOR, GlStateManager.DestFactor.ONE_MINUS_SRC_COLOR, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        guiGraphics.blit(CROSSHAIR, centerX, centerY, 0, vOffset, partSize, partSize, textureSize, textureSize);
        guiGraphics.blit(CROSSHAIR, centerX, centerY - currentSpread, singleQuadSize, vOffset, partSize, partSize, textureSize, textureSize);
        guiGraphics.blit(CROSSHAIR, centerX - currentSpread, centerY, singleQuadSize * 2, vOffset, partSize, partSize, textureSize, textureSize);
        guiGraphics.blit(CROSSHAIR, centerX, centerY + currentSpread, singleQuadSize * 3, vOffset, partSize, partSize, textureSize, textureSize);
        guiGraphics.blit(CROSSHAIR, centerX + currentSpread, centerY, singleQuadSize * 4, vOffset, partSize, partSize, textureSize, textureSize);
        RenderSystem.defaultBlendFunc();
        RenderSystem.disableBlend();
        if (feedBack) {
            if (headShot) {
                guiGraphics.blit(FEED_BACK, centerX, centerY, 16, 0, 15, 15, 32, 16);
            } else {
                guiGraphics.blit(FEED_BACK, centerX, centerY, 0, 0, 15, 15, 32, 16);
            }
        }
    }

    public void defaultCrosshair(GuiGraphics guiGraphics) {
        RenderSystem.enableBlend();
        RenderSystem.blendFuncSeparate(GlStateManager.SourceFactor.ONE_MINUS_DST_COLOR, GlStateManager.DestFactor.ONE_MINUS_SRC_COLOR, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        guiGraphics.blit(GUI_ICONS_LOCATION_MINECRAFT, (guiGraphics.guiWidth() - 15) / 2, (guiGraphics.guiHeight() - 15) / 2, 0, 0, 15, 15);
        RenderSystem.defaultBlendFunc();
        RenderSystem.disableBlend();
    }

}
