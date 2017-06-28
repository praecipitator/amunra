package de.katzenpapst.amunra.client.gui.elements;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.opengl.GL11;

import net.minecraftforge.fml.client.FMLClientHandler;
import de.katzenpapst.amunra.AmunRa;
import micdoodle8.mods.galacticraft.core.client.gui.screen.SmallFontRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.util.ResourceLocation;

public class StringSelectBox extends GuiButton {

    protected static final ResourceLocation textures = new ResourceLocation(AmunRa.ASSETPREFIX, "textures/gui/gui-extra.png");

    protected List<String> strings = new ArrayList<String>();

    protected int selectedStringIndex = -1;

    protected final int textSize = 8;

    protected int scrollOffset = 0;

    protected int maxLines = 0;

    protected final ISelectBoxCallback parent;

    public SmallFontRenderer font;

    public StringSelectBox(
            ISelectBoxCallback parent,
            int id,
            int xPos,
            int yPos,
            int width,
            int height) {
        super(id, xPos, yPos, width, height, "");

        this.parent = parent;

        Minecraft mc = FMLClientHandler.instance().getClient();

        this.font = new SmallFontRenderer(mc.gameSettings, new ResourceLocation("textures/font/ascii.png"), mc.renderEngine, false);

        // find out how many lines we can have
        maxLines = height / textSize;
    }

    public void clear() {
        strings.clear();
        clearSelection();
    }

    public void addString(String str) {
        strings.add(str);

        //
    }

    public void setSelection(int selection) {
        if(selection >= 0 && selection < strings.size() && this.selectedStringIndex != selection) {
            this.selectedStringIndex =  selection;
            parent.onSelectionChanged(this, selectedStringIndex);
        }
    }

    public void clearSelection() {
        if(selectedStringIndex != -1) {
            selectedStringIndex = -1;
            parent.onSelectionChanged(this, selectedStringIndex);
        }
    }

    public boolean hasSelection() {
        return selectedStringIndex != -1;
    }

    public int getSelectedStringIndex() {
        return selectedStringIndex;
    }

    public String getSelectedString() {
        if(selectedStringIndex >= 0) {
            return strings.get(selectedStringIndex);
        }
        return null;
    }

    public void deleteStringAt(int index) {
        strings.remove(index);
        clearSelection();
    }

    /**
     * Draws this button to the screen.
     */
    @Override
    public void drawButton(Minecraft mc, int mouseX, int mouseY)
    {
        if (this.visible)
        {
            int color = 0xFFa6a6a6;
            // outer box
            this.drawGradientRect(this.xPosition, this.yPosition, this.xPosition+this.width, this.yPosition+this.height, color, color);
            int colorBlack = 0xFF000000;
            // inner box
            this.drawGradientRect(this.xPosition+1, this.yPosition+1, this.xPosition+this.width-1, this.yPosition+this.height-1, colorBlack, colorBlack);

            // strings
            int colorGreen = 0xFF00FF00;
            //selectedStringIndex = 1;
            int colorSelection = 0x99008AFF;
            int displayLines = Math.min(strings.size(), maxLines);
            for(int i=0; i<displayLines;i++) {
                int curYoffset = i*textSize;
                int actualIndex = i+scrollOffset;

                int colorText = colorGreen;

                if(actualIndex==selectedStringIndex) {
                    this.drawGradientRect(this.xPosition+1, curYoffset+this.yPosition+1, this.xPosition+this.width-1, curYoffset+this.yPosition+1+textSize, colorSelection, colorSelection);
                    //colorText = 0xFF555555;
                }

                font.drawStringWithShadow(strings.get(actualIndex), this.xPosition+2, curYoffset+this.yPosition, colorText);
                //FMLClientHandler.instance().getClient().fontRenderer.drawStringWithShadow("le test", this.xPosition+2, curYoffset+this.yPosition+2, colorGreen);
            }

            if(maxLines < strings.size()) {
                // draw arrows
                mc.getTextureManager().bindTexture(textures);
                GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
                if(scrollOffset == 0) {
                    this.drawTexturedModalRect(this.xPosition+this.width-1-8, this.yPosition+1, 8, 84+8, 8, 8);
                } else {
                    this.drawTexturedModalRect(this.xPosition+this.width-1-8, this.yPosition+1, 8, 84, 8, 8);
                }
                if(scrollOffset >= strings.size()-maxLines) {
                    this.drawTexturedModalRect(this.xPosition+this.width-1-8, this.yPosition+this.height-1-8, 0, 84+8, 8, 8);
                } else {
                    this.drawTexturedModalRect(this.xPosition+this.width-1-8, this.yPosition+this.height-1-8, 0, 84, 8, 8);
                }
            }


        }
    }

    /**
     * Returns true if the mouse has been pressed on this control. Equivalent of MouseListener.mousePressed(MouseEvent
     * e).
     */
    @Override
    public boolean mousePressed(Minecraft mc, int mouseX, int mouseY)
    {
        if(!super.mousePressed(mc, mouseX, mouseY)) {
            return false;
        }
        // otherwise do stuff
        // first, try the arrows
        if(maxLines < strings.size()) {
            int btnMaxX = this.xPosition+this.width-1;
            int btnMinX = this.xPosition+this.width-1-8;

            if(btnMinX <= mouseX && mouseX <= btnMaxX) {

                int btnTopMinY = this.yPosition+1;
                int btnTopMaxY = this.yPosition+8+1;

                if(btnTopMinY <= mouseY && mouseY <= btnTopMaxY) {
                    // top button
                    if(scrollOffset > 0) {
                        scrollOffset--;
                    }
                    return true;
                }

                btnTopMinY = this.yPosition+this.height-1-8;
                btnTopMaxY = this.yPosition+this.height-1;
                if(btnTopMinY <= mouseY && mouseY <= btnTopMaxY) {
                    // bottom button
                    if(scrollOffset < strings.size()-maxLines) {
                        scrollOffset++;
                    }
                    return true;
                }

                // buttons are 8x8
                // top right
            }

        }

        // are we changing selection?
        int relativeY = mouseY-this.yPosition;
        int lineClicked = relativeY / textSize;
        if(lineClicked < strings.size()) {
            int newIndex = lineClicked+scrollOffset;
            this.setSelection(newIndex);
            return true;
        }

        return true;
    }

    public interface ISelectBoxCallback {
        public void onSelectionChanged(StringSelectBox box, int selection);
    }

}
