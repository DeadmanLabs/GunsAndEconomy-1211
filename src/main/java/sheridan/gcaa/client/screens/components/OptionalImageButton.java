package sheridan.gcaa.client.screens.components;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class OptionalImageButton extends ImageButton {
    private boolean prevented;
    protected boolean mouseDown;
    private Tooltip preventedTooltip;
    private Tooltip normalTooltip;
    private ResourceLocation currentTexture;
    private final ResourceLocation resourceLocation;
    private final int xTexStart;
    private final int yTexStart;
    private final int yDiffTex;
    private final int textureWidth;
    private final int textureHeight;

    public OptionalImageButton(int pX, int pY, int pWidth, int pHeight, int pXTexStart, int pYTexStart, int pYDiffTex, ResourceLocation pResourceLocation, int pTextureWidth, int pTextureHeight, OnPress pOnPress) {
        super(pX, pY, pWidth, pHeight,
                new net.minecraft.client.gui.components.WidgetSprites(pResourceLocation, pResourceLocation),
                pOnPress);
        this.resourceLocation = pResourceLocation;
        this.xTexStart = pXTexStart;
        this.yTexStart = pYTexStart;
        this.yDiffTex = pYDiffTex;
        this.textureWidth = pTextureWidth;
        this.textureHeight = pTextureHeight;
    }

    public void enableIf(boolean condition) {
        this.active = this.visible = condition;
    }

    public boolean isPrevented() {
        return prevented;
    }

    public void setPrevented(boolean prevented) {
        this.prevented = prevented;
        if (prevented) {
            if (preventedTooltip != null) {
                this.setTooltip(preventedTooltip);
            }
        } else {
            if (normalTooltip != null) {
                this.setTooltip(normalTooltip);
            }
        }
    }

    public void setCurrentTexture(ResourceLocation currentTexture) {
        this.currentTexture = currentTexture;
    }

    public void resetCurrentTexture() {
        this.currentTexture = null;
    }

    public void setPreventedTooltip(String key) {
        preventedTooltip = Tooltip.create(Component.translatable(key));
    }

    public void setPreventedTooltipStr(String message) {
        preventedTooltip = Tooltip.create(Component.literal(message));
    }

    public void reset() {
        preventedTooltip = null;
        prevented = false;
        mouseDown = false;
    }

    public boolean isMouseDown() {
        return mouseDown;
    }

    public void setNormalTooltip(String key) {
        normalTooltip = Tooltip.create(Component.translatable(key));
    }

    @Override
    public void renderWidget(@NotNull GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
        if (prevented) {
            pGuiGraphics.setColor(0.5f, 0.5f, 0.5f, 0.5f);
        } else {
            if (mouseDown) {
                pGuiGraphics.setColor(27 / 255f, 161 / 255f, 226 / 255f, 1);
            }
        }
        ResourceLocation texture = currentTexture != null ? currentTexture : resourceLocation;
        int yTex = this.yTexStart;
        if (!this.isActive()) {
            yTex += this.yDiffTex * 2;
        } else if (this.isHoveredOrFocused()) {
            yTex += this.yDiffTex;
        }
        pGuiGraphics.blit(texture, this.getX(), this.getY(), this.xTexStart, yTex, this.width, this.height, this.textureWidth, this.textureHeight);
        pGuiGraphics.setColor(1, 1, 1, 1);
    }

    @Override
    public boolean mouseClicked(double pMouseX, double pMouseY, int pButton) {
        mouseDown = super.mouseClicked(pMouseX, pMouseY, pButton);
        return mouseDown;
    }

    @Override
    public void setTooltip(@Nullable Tooltip pTooltip) {
        super.setTooltip(pTooltip);
    }

    @Override
    public boolean mouseReleased(double pMouseX, double pMouseY, int pButton) {
        mouseDown = false;
        return super.mouseReleased(pMouseX, pMouseY, pButton);
    }


    @Override
    public boolean isMouseOver(double pMouseX, double pMouseY) {
        boolean over = super.isMouseOver(pMouseX, pMouseY);
        if (!over) {
            mouseDown = false;
        }
        return over;
    }

    @Override
    public void onPress() {
        if (!prevented) {
            super.onPress();
        }
    }
}
