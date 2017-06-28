package de.katzenpapst.amunra.client.gui.elements;

import java.util.List;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import net.minecraftforge.fml.client.FMLClientHandler;
import de.katzenpapst.amunra.AmunRa;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.ResourceLocation;

public class TabButton extends GuiButton {

    protected static final ResourceLocation textures = new ResourceLocation(AmunRa.ASSETPREFIX, "textures/gui/gui-extra.png");

    public boolean isActive;

    protected final ResourceLocation texture;

    protected String extraInfo = null;

    public TabButton(int id, int xPos, int yPos, String displayString, ResourceLocation texture) {
        super(id, xPos, yPos, displayString);

        this.width = 30;
        this.height = 28;
        this.texture = texture;
    }

    public TabButton(int id, int xPos, int yPos, String displayString, String infoString, ResourceLocation texture) {
        this(id, xPos, yPos, displayString, texture);

        extraInfo = infoString;
    }

    /**
     * Draws this button to the screen.
     */
    @Override
    public void drawButton(Minecraft mc, int mouseX, int mouseY)
    {
        if (this.visible)
        {
            RenderHelper.disableStandardItemLighting();

            this.hovered = mouseX >= this.xPosition && mouseY >= this.yPosition && mouseX < this.xPosition + this.width && mouseY < this.yPosition + this.height;

            int k = this.getHoverState(this.hovered);


            mc.getTextureManager().bindTexture(textures);
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);

            GL11.glEnable(GL11.GL_BLEND);
            OpenGlHelper.glBlendFunc(770, 771, 1, 0);
            GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

            this.drawTexturedModalRect(this.xPosition, this.yPosition, 0, 0 + k * 28, 30, 28);

            this.mouseDragged(mc, mouseX, mouseY);


            //
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            mc.getTextureManager().bindTexture(texture);
            drawFullSizedTexturedRect(xPosition+7, yPosition+5, 18, 18);

            //this.drawCenteredString(fontrenderer, this.displayString, this.xPosition + this.width / 2, this.yPosition + (this.height - 8) / 2, l);
            RenderHelper.enableStandardItemLighting();
        }
    }

    protected void drawFullSizedTexturedRect(int x, int y, int width, int height)
    {
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        Tessellator tessellator = Tessellator.getInstance();
        // TODO fix this
        /*
        tessellator.startDrawingQuads();
        tessellator.addVertexWithUV(x, y+height, this.zLevel, 0, 1);
        tessellator.addVertexWithUV(x+width, y+height, this.zLevel, 1, 1);
        tessellator.addVertexWithUV(x+width, y, this.zLevel, 1, 0);
        tessellator.addVertexWithUV(x, y, this.zLevel, 0, 0);
        tessellator.draw();
        */
    }

    /**
     * Returns 0 if the button is disabled, 1 if the mouse is NOT hovering over this button and 2 if it IS hovering over
     * this button.
     */
    @Override
    public int getHoverState(boolean isMouseOver)
    {
        if(isActive) {
            return 0;
        }
        if(!enabled) {
            return 2;
        }
        return 1;
    }

    public void drawTooltip(int mouseX, int mouseY)
    {
        if(!this.visible || !this.hovered) {
            return;
        }
        GL11.glDisable(GL12.GL_RESCALE_NORMAL);
        /*
        RenderHelper.disableStandardItemLighting();
        GL11.glDisable(GL11.GL_LIGHTING);
        */
        GL11.glDisable(GL11.GL_DEPTH_TEST);

        boolean withinRegion = mouseX >= this.xPosition && mouseY >= this.yPosition && mouseX < this.xPosition + this.width && mouseY < this.yPosition + this.height;
        List<String> extraStrings = null;


        if (this.displayString != null && !this.displayString.isEmpty() && withinRegion)
        {
            //FontRenderer fontrenderer = mc.fontRendererObj;
            FontRenderer fontRenderer = FMLClientHandler.instance().getClient().fontRendererObj;
            int stringWidth = fontRenderer.getStringWidth(displayString);



            int tooltipX = mouseX + 12;
            int tooltipY = mouseY - 12;
            int stringHeight = 8;

            if(this.extraInfo != null) {
                stringWidth = Math.max(stringWidth, 150);
                extraStrings = fontRenderer.listFormattedStringToWidth(extraInfo, stringWidth);
                stringHeight += extraStrings.size() * 10;
            }

            this.zLevel = 300.0F;
            //GuiElementInfoRegion.itemRenderer.zLevel = 300.0F;
            int colorSomething = -267386864;
            this.drawGradientRect(tooltipX - 3, tooltipY - 4, tooltipX + stringWidth + 3, tooltipY - 3, colorSomething, colorSomething);
            this.drawGradientRect(tooltipX - 3, tooltipY + stringHeight + 3, tooltipX + stringWidth + 3, tooltipY + stringHeight + 4, colorSomething, colorSomething);
            this.drawGradientRect(tooltipX - 3, tooltipY - 3, tooltipX + stringWidth + 3, tooltipY + stringHeight + 3, colorSomething, colorSomething);
            this.drawGradientRect(tooltipX - 4, tooltipY - 3, tooltipX - 3, tooltipY + stringHeight + 3, colorSomething, colorSomething);
            this.drawGradientRect(tooltipX + stringWidth + 3, tooltipY - 3, tooltipX + stringWidth + 4, tooltipY + stringHeight + 3, colorSomething, colorSomething);
            int otherColorSomething = 1347420415;
            int j2 = (otherColorSomething & 16711422) >> 1 | otherColorSomething & -16777216;
            this.drawGradientRect(tooltipX - 3, tooltipY - 3 + 1, tooltipX - 3 + 1, tooltipY + stringHeight + 3 - 1, otherColorSomething, j2);
            this.drawGradientRect(tooltipX + stringWidth + 2, tooltipY - 3 + 1, tooltipX + stringWidth + 3, tooltipY + stringHeight + 3 - 1, otherColorSomething, j2);
            this.drawGradientRect(tooltipX - 3, tooltipY - 3, tooltipX + stringWidth + 3, tooltipY - 3 + 1, otherColorSomething, otherColorSomething);
            this.drawGradientRect(tooltipX - 3, tooltipY + stringHeight + 2, tooltipX + stringWidth + 3, tooltipY + stringHeight + 3, j2, j2);

            fontRenderer.drawStringWithShadow(displayString, tooltipX, tooltipY, -1);

            //EnumColor.RED


            if(extraStrings != null) {
                for(int i=0;i<extraStrings.size();i++) {
                    fontRenderer.drawStringWithShadow(extraStrings.get(i), tooltipX, tooltipY+(i+1)*10, 0x7777FF);
                }
            }



            this.zLevel = 0.0F;
            //GuiElementInfoRegion.itemRenderer.zLevel = 0.0F;
        }

        /*GL11.glEnable(GL11.GL_LIGHTING);

        RenderHelper.enableStandardItemLighting();
        */
        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL11.glEnable(GL12.GL_RESCALE_NORMAL);
    }

}
