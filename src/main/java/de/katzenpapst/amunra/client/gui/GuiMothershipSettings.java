package de.katzenpapst.amunra.client.gui;

import java.util.List;

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import de.katzenpapst.amunra.AmunRa;
import de.katzenpapst.amunra.client.gui.elements.DynamicTexturedButton;
import de.katzenpapst.amunra.inventory.ContainerMothershipSettings;
import de.katzenpapst.amunra.mothership.Mothership;
import de.katzenpapst.amunra.network.packet.PacketSimpleAR;
import de.katzenpapst.amunra.network.packet.PacketSimpleAR.EnumSimplePacket;
import de.katzenpapst.amunra.tile.TileEntityMothershipSettings;
import micdoodle8.mods.galacticraft.core.client.gui.container.GuiContainerGC;
import micdoodle8.mods.galacticraft.core.client.gui.element.GuiElementTextBox;
import micdoodle8.mods.galacticraft.core.client.gui.element.GuiElementTextBox.ITextBoxCallback;
import micdoodle8.mods.galacticraft.core.util.ColorUtil;
import micdoodle8.mods.galacticraft.core.util.GCCoreUtil;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;

public class GuiMothershipSettings extends GuiContainerGC implements ITextBoxCallback {

    private static final ResourceLocation guiTexture = new ResourceLocation(AmunRa.ASSETPREFIX, "textures/gui/ms_settings.png");

    private final TileEntityMothershipSettings tile;
    private Mothership ship;

    private GuiButton applyButton;
    private GuiButton resetButton;
    private GuiButton texturesPrev;
    private GuiButton texturesNext;
    private DynamicTexturedButton[] textureButtons = new DynamicTexturedButton[6];
    //private GuiElementCheckbox enablePadRemovalButton;
    //private GuiElementDropdown dropdownTest;
    private GuiElementTextBox nameField;
    private String changedName;
    private ResourceLocation changedIcon;
    private int texButtonOffset = 0;

    private boolean waitingForResponse = false;

    protected List<ResourceLocation> mothershipTextures;

    public GuiMothershipSettings(InventoryPlayer par1InventoryPlayer, TileEntityMothershipSettings tile) {
        super(new ContainerMothershipSettings(par1InventoryPlayer, tile));
        this.ySize = 201;
        this.xSize = 176;
        this.tile = tile;
        mothershipTextures = AmunRa.instance.getPossibleMothershipTextures();
        ship = tile.getMothership();
        changedName = ship.getLocalizedName();
        changedIcon = ship.getBodyIcon();
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float ticksProbably, int somethingX, int somethingY) {
        GL11.glPushMatrix();
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.mc.renderEngine.bindTexture(guiTexture);
        final int xOffset = (this.width - this.xSize) / 2;
        final int yOffset = (this.height - this.ySize) / 2;
        this.drawTexturedModalRect(xOffset, yOffset, 0, 0, this.xSize, this.ySize);

        for(DynamicTexturedButton btn : textureButtons) {
            if(btn == null) continue;
            if(btn.getTexture().equals(changedIcon)) {
                btn.setSelected(true);
            } else {
                btn.setSelected(false);
            }
        }

        GL11.glPopMatrix();
    }

    protected void setGuiEnabled(boolean set) {
        applyButton.enabled = set;
        resetButton.enabled = set;
        texturesPrev.enabled = set;
        texturesNext.enabled = set;
        for(DynamicTexturedButton btn: textureButtons) {
            btn.enabled = set;
        }


        nameField.enabled = set;
    }

    public void mothershipResponsePacketRecieved() {
        resetData();
        this.setGuiEnabled(true);
    }

    protected void resetData() {
        this.changedIcon = this.tile.getMothership().getBodyIcon();
        this.changedName = this.tile.getMothership().getLocalizedName();
    }

    @Override
    protected void actionPerformed(GuiButton btn)
    {
        if(btn.equals(applyButton)) {
            NBTTagCompound nbt = new NBTTagCompound ();
            nbt.setString("name", changedName);
            nbt.setString("bodyIcon", changedIcon.toString());
            this.setGuiEnabled(false);
            AmunRa.packetPipeline.sendToServer(new PacketSimpleAR(EnumSimplePacket.S_SET_MOTHERSHIP_SETTINGS, ship.getID(), nbt));
            return;
        }
        if(btn.equals(resetButton)) {
            resetData();
            return;
        }
        if(btn.equals(texturesNext)) {
            if(texButtonOffset+textureButtons.length < mothershipTextures.size()) {
                texButtonOffset++;
                updateTextureButtons();
                return;
            }
        }
        if(btn.equals(texturesPrev)) {
            if(texButtonOffset > 0) {
                texButtonOffset--;
                updateTextureButtons();
                return;
            }
        }
        for(DynamicTexturedButton texButton: textureButtons) {
            if(btn.equals(texButton)) {
                changedIcon = texButton.getTexture();

                return;
            }
        }
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


    @SuppressWarnings("unchecked")
    @Override
    public void initGui()
    {
        super.initGui();
        this.buttonList.clear();
        final int guiX = (this.width - this.xSize) / 2;
        final int guiY = (this.height - this.ySize) / 2;

        this.applyButton = new GuiButton(0, guiX + 120 - 50, guiY + 95, 48, 20, GCCoreUtil.translate("gui.message.mothership.apply"));
        this.resetButton = new GuiButton(1, guiX + 120, guiY + 95, 48, 20, GCCoreUtil.translate("gui.message.mothership.reset"));



        //int id, ITextBoxCallback parentGui, int x, int y, int width, int height, String initialText, boolean numericOnly, int maxLength, boolean centered
        this.nameField = new GuiElementTextBox(2, this, guiX + 4, guiY + 4, 168, 20, "", false, 14, true);

        texturesPrev = new GuiButton(3, guiX + 6, guiY + 26, 20, 20, GCCoreUtil.translate("<"));
        texturesNext = new GuiButton(4, guiX + 150, guiY + 26, 20, 20, GCCoreUtil.translate(">"));

        //testBtn = new DynamicTexturedButton(5, guiX + 6 + 20, guiY+26, 20, 20, mothershipTextures.get(0));

        this.buttonList.add(this.applyButton);
        this.buttonList.add(this.resetButton);
        this.buttonList.add(this.nameField);
        this.buttonList.add(this.texturesPrev);
        this.buttonList.add(this.texturesNext);
        //this.buttonList.add(this.testBtn);
        initTextureButtons(5, guiX+2, guiY);
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
    public boolean canPlayerEdit(GuiElementTextBox textBox, EntityPlayer player) {
        return true; // for now
    }

    @Override
    public void onTextChanged(GuiElementTextBox textBox, String newText) {
        //
        if(textBox.equals(nameField)) {
            changedName = newText;
            this.applyButton.enabled = isValidName(changedName);
        }
    }

    protected boolean isValidName(String name) {
        return !name.trim().isEmpty();
    }

    @Override
    protected void keyTyped(char keyChar, int keyID)
    {
        if (keyID != Keyboard.KEY_ESCAPE /*&& keyID != this.mc.gameSettings.keyBindInventory.getKeyCode()*/)
        {
            if (this.nameField.keyTyped(keyChar, keyID))
            {
                return;
            }
        }

        super.keyTyped(keyChar, keyID);
    }

    @Override
    public String getInitialText(GuiElementTextBox textBox) {
        if(textBox.equals(this.nameField)) {
            return changedName;
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
}
