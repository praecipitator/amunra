package de.katzenpapst.amunra.client.gui.elements;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.util.ResourceLocation;

public class DynamicTexturedButton extends GuiButton {

    protected ResourceLocation texture;
    protected boolean isSelected;

    public DynamicTexturedButton(int id, int x, int y, ResourceLocation initialTexture) {
        super(id, x, y, "");
        texture = initialTexture;
    }

    public DynamicTexturedButton(
            int id,
            int x,
            int y,
            int width,
            int height,
            ResourceLocation initialTexture) {
        super(id, x, y, width, height, "");
        texture = initialTexture;
    }

    public void setTexture(ResourceLocation texture) {
        this.texture = texture;
    }

    public ResourceLocation getTexture() {
        return texture;
    }

    public void setSelected(boolean set) {
        this.isSelected = set;
    }

    public boolean getSelected() {
        return isSelected;
    }

    @Override
    public void drawButton(Minecraft mc, int mouseX, int mouseY)
    {
        if (this.visible)
        {
            //FontRenderer fontrenderer = mc.fontRenderer;
            mc.getTextureManager().bindTexture(buttonTextures);
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            if(isSelected) {
                GL11.glColor4f(1.0F, 0.5F, 0.5F, 1.0F);
            }
            this.hovered = mouseX >= this.xPosition && mouseY >= this.yPosition && mouseX < this.xPosition + this.width && mouseY < this.yPosition + this.height;
            int k = this.getHoverState(this.hovered);
            GL11.glEnable(GL11.GL_BLEND);
            OpenGlHelper.glBlendFunc(770, 771, 1, 0);
            GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
            this.drawTexturedModalRect(this.xPosition,                  this.yPosition, 0,                    46 + k * 20, this.width / 2, this.height);
            this.drawTexturedModalRect(this.xPosition + this.width / 2, this.yPosition, 200 - this.width / 2, 46 + k * 20, this.width / 2, this.height);
            this.mouseDragged(mc, mouseX, mouseY);

            if(texture != null) {
                GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
                mc.getTextureManager().bindTexture(texture);
                //this.drawTexturedModalRect(xPosition, yPosition, 0, 0, width, height);

                drawFullSizedTexturedRect(xPosition+2, yPosition+2, width-4, height-4);
                //this.drawCenteredString(fontrenderer, this.displayString, this.xPosition + this.width / 2, this.yPosition + (this.height - 8) / 2, l);
            }
        }
    }

    protected void drawFullSizedTexturedRect(int x, int y, int width, int height)
    {
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        /* TODO fix
        Tessellator tessellator = Tessellator.instance;
        tessellator.startDrawingQuads();
        tessellator.addVertexWithUV(x, y+height, this.zLevel, 0, 1);
        tessellator.addVertexWithUV(x+width, y+height, this.zLevel, 1, 1);
        tessellator.addVertexWithUV(x+width, y, this.zLevel, 1, 0);
        tessellator.addVertexWithUV(x, y, this.zLevel, 0, 0);
        tessellator.draw();*/
    }

}
