package de.katzenpapst.amunra.client.gui.tabs;

import java.util.List;

import de.katzenpapst.amunra.AmunRa;
import de.katzenpapst.amunra.client.gui.GuiMothershipSettings;
import de.katzenpapst.amunra.client.gui.GuiMothershipSettings.IMothershipSettingsTab;
import de.katzenpapst.amunra.client.gui.elements.DynamicTexturedButton;
import de.katzenpapst.amunra.mothership.Mothership;
import de.katzenpapst.amunra.tile.TileEntityMothershipSettings;
import micdoodle8.mods.galacticraft.core.client.gui.element.GuiElementTextBox;
import micdoodle8.mods.galacticraft.core.client.gui.element.GuiElementTextBox.ITextBoxCallback;
import micdoodle8.mods.galacticraft.core.util.ColorUtil;
import micdoodle8.mods.galacticraft.core.util.GCCoreUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;

public class TabMothershipCustom extends AbstractTab implements ITextBoxCallback, IMothershipSettingsTab {

    private final ResourceLocation icontexture = new ResourceLocation(AmunRa.ASSETPREFIX, "textures/gui/mothership_icons/0.png");

    private final TileEntityMothershipSettings tile;
    private Mothership ship;

/*    private GuiButton applyButton;
    private GuiButton resetButton;*/
    private GuiButton texturesPrev;
    private GuiButton texturesNext;

    private int texButtonOffset = 0;

    private GuiElementTextBox nameField;

    //private String changedName;

    // private ResourceLocation changedIcon;

    private DynamicTexturedButton[] textureButtons = new DynamicTexturedButton[6];

    protected List<ResourceLocation> mothershipTextures;

    public TabMothershipCustom(TileEntityMothershipSettings tile, GuiMothershipSettings parent, Minecraft mc, int width, int height, int xSize, int ySize) {
        super(parent, mc, width, height, xSize, ySize);
        this.tile = tile;
        this.ship = tile.getMothership();
        mothershipTextures = AmunRa.instance.getPossibleMothershipTextures();

        //changedName = ship.getLocalizedName();
        //changedIcon = ship.getBodyIcon();
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float ticks)
    {
        for(DynamicTexturedButton btn : textureButtons) {
            if(btn == null) continue;
            if(btn.getTexture().equals(this.ship.getBodyIcon())) {
                btn.setSelected(true);
            } else {
                btn.setSelected(false);
            }
        }

        super.drawScreen(mouseX, mouseY, ticks);
    }

    @Override
    protected void drawExtraScreenElements(int mouseX, int mouseY, float ticks)
    {
        final int guiX = (this.width - this.xSize) / 2;
        final int guiY = (this.height - this.ySize) / 2;

        this.fontRendererObj.drawString(this.getTooltip(), guiX+5, guiY+5, 4210752);

        this.fontRendererObj.drawString(GCCoreUtil.translate("container.inventory"), guiX+8, guiY+this.ySize - 94, 4210752);
    }

    @Override
    public void initGui() {

        this.buttonList.clear();
        final int guiX = (this.width - this.xSize) / 2;
        final int guiY = (this.height - this.ySize) / 2;

        //this.applyButton = new GuiButton(0, guiX + 120 - 50, guiY + 95, 48, 20, GCCoreUtil.translate("gui.message.mothership.apply"));
        //this.resetButton = new GuiButton(1, guiX + 120, guiY + 95, 48, 20, GCCoreUtil.translate("gui.message.mothership.reset"));



        //int id, ITextBoxCallback parentGui, int x, int y, int width, int height, String initialText, boolean numericOnly, int maxLength, boolean centered
        this.nameField = new GuiElementTextBox(2, this, guiX + 4, guiY + 4+20, 168, 20, "", false, 14, true);

        texturesPrev = new GuiButton(3, guiX + 6, guiY + 26+20, 20, 20, GCCoreUtil.translate("<"));
        texturesNext = new GuiButton(4, guiX + 150, guiY + 26+20, 20, 20, GCCoreUtil.translate(">"));

        //testBtn = new DynamicTexturedButton(5, guiX + 6 + 20, guiY+26, 20, 20, mothershipTextures.get(0));

        //addButton(this.applyButton);
        //addButton(this.resetButton);
        addTextBox(this.nameField);
        addButton(this.texturesPrev);
        addButton(this.texturesNext);
        //
        initTextureButtons(5, guiX+2, guiY+20);

    }

    protected int initTextureButtons(int startId, int guiX, int guiY) {
        int curId = startId;
        for(int i=0;i<textureButtons.length;i++) {

            DynamicTexturedButton btn = new DynamicTexturedButton(curId, guiX + 6 + 20 + 20*i, guiY+26, 20, 20, null);
            curId++;
            if(mothershipTextures.size() > i) {
                btn.setTexture(mothershipTextures.get(i));
            } else {
                btn.enabled = false;
            }
            //btn.setd
            this.buttonList.add(btn);
            textureButtons[i] = btn;
        }
        return curId;
    }

    @Override
    public void mothershipResponsePacketRecieved() {
        resetData();
        setGuiEnabled(true);
    }

    protected boolean isValidName(String name) {
        return name != null && !name.trim().isEmpty();
    }

    @Override
    public boolean canPlayerEdit(GuiElementTextBox textBox, EntityPlayer player) {
        return true;
    }

    @Override
    public void onTextChanged(GuiElementTextBox textBox, String newText) {
        if(textBox.equals(nameField)) {
            if(isValidName(newText) && !newText.equals(ship.getLocalizedName())) {
                ship.setLocalizedName(newText);
                ((GuiMothershipSettings )this.parent).sendMothershipSettingsPacket();
            }
        }
    }

    @Override
    public String getInitialText(GuiElementTextBox textBox) {
        if(textBox.equals(this.nameField)) {
            return ship.getLocalizedName();
        }
        return "";
    }

    @Override
    public int getTextColor(GuiElementTextBox textBox) {
        return ColorUtil.to32BitColor(255, 20, 255, 20);
    }

    @Override
    public void onIntruderInteraction(GuiElementTextBox textBox) {
        // TODO Auto-generated method stub
    }

    public void setGuiEnabled(boolean set) {
        //applyButton.enabled = set;
        //resetButton.enabled = set;
        texturesPrev.enabled = set;
        texturesNext.enabled = set;
        for(DynamicTexturedButton btn: textureButtons) {
            btn.enabled = set;
        }


        nameField.enabled = set;
    }

    public void resetData() {
        // this.changedIcon = this.tile.getMothership().getBodyIcon();
        // this.changedName = this.tile.getMothership().getLocalizedName();
    }

    @Override
    public boolean actionPerformed(GuiButton btn)
    {
        /*if(btn.equals(applyButton)) {
            NBTTagCompound nbt = new NBTTagCompound ();
            nbt.setString("name", changedName);
            nbt.setString("bodyIcon", changedIcon.toString());
            this.setGuiEnabled(false);
            AmunRa.packetPipeline.sendToServer(new PacketSimpleAR(EnumSimplePacket.S_SET_MOTHERSHIP_SETTINGS, ship.getID(), nbt));
            return true;
        }*/
        /*if(btn.equals(resetButton)) {
            resetData();
            return true;
        }*/
        if(btn.equals(texturesNext)) {
            if(texButtonOffset+textureButtons.length < mothershipTextures.size()) {
                texButtonOffset++;
                updateTextureButtons();
                return true;
            }
        }
        if(btn.equals(texturesPrev)) {
            if(texButtonOffset > 0) {
                texButtonOffset--;
                updateTextureButtons();
                return true;
            }
        }
        for(DynamicTexturedButton texButton: textureButtons) {
            if(btn.equals(texButton)) {
                ship.setBodyIcon(texButton.getTexture());
                ((GuiMothershipSettings)parent).sendMothershipSettingsPacket();
                //changedIcon = texButton.getTexture();

                return true;
            }
        }
        return false;
    }

    @Override
    public void onTabActivated() {
        resetData();
    }

    protected void updateTextureButtons() {

        for(int i=0;i<textureButtons.length;i++) {

            int textureOffset = i+texButtonOffset;
            if(textureOffset < 0 || textureOffset >= mothershipTextures.size()) {
                textureButtons[i].enabled = false;
            } else {
                textureButtons[i].enabled = true;
                textureButtons[i].setTexture(mothershipTextures.get(textureOffset));
            }
        }
    }

    @Override
    public String getTooltip() {
        return GCCoreUtil.translate("tile.mothershipSettings.customize");
    }

    @Override
    public ResourceLocation getIcon() {
        return icontexture;
    }

    @Override
    public void mothershipOperationFailed(String message) {
        // TODO Auto-generated method stub

    }
}
