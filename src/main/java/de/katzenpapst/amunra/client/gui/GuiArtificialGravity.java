package de.katzenpapst.amunra.client.gui;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import de.katzenpapst.amunra.AmunRa;
import de.katzenpapst.amunra.inventory.ContainerArtificalGravity;
import de.katzenpapst.amunra.network.packet.PacketSimpleAR;
import de.katzenpapst.amunra.tile.TileEntityGravitation;
import micdoodle8.mods.galacticraft.api.vector.BlockVec3;
import micdoodle8.mods.galacticraft.api.vector.Vector3;
import micdoodle8.mods.galacticraft.core.GalacticraftCore;
import micdoodle8.mods.galacticraft.core.client.gui.container.GuiContainerGC;
import micdoodle8.mods.galacticraft.core.client.gui.element.GuiElementCheckbox;
import micdoodle8.mods.galacticraft.core.client.gui.element.GuiElementCheckbox.ICheckBoxCallback;
import micdoodle8.mods.galacticraft.core.client.gui.element.GuiElementInfoRegion;
import micdoodle8.mods.galacticraft.core.client.gui.element.GuiElementTextBox;
import micdoodle8.mods.galacticraft.core.client.gui.element.GuiElementTextBox.ITextBoxCallback;
import micdoodle8.mods.galacticraft.core.energy.EnergyDisplayHelper;
import micdoodle8.mods.galacticraft.core.network.PacketSimple;
import micdoodle8.mods.galacticraft.core.network.PacketSimple.EnumSimplePacket;
import micdoodle8.mods.galacticraft.core.util.ColorUtil;
import micdoodle8.mods.galacticraft.core.util.GCCoreUtil;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.ResourceLocation;

public class GuiArtificialGravity extends GuiContainerGC implements ITextBoxCallback, ICheckBoxCallback {

    private static final ResourceLocation guiTexture = new ResourceLocation(AmunRa.ASSETPREFIX, "textures/gui/gravity.png");

    protected List<GuiElementTextBox> inputFieldList = new ArrayList();

    private GuiElementTextBox leftValueField;
    private GuiElementTextBox rightValueField;
    private GuiElementTextBox frontValueField;
    private GuiElementTextBox backValueField;
    private GuiElementTextBox topValueField;
    private GuiElementTextBox bottomValueField;

    private GuiElementTextBox strengthField;
    private GuiElementCheckbox checkboxInvert;

    private GuiElementCheckbox checkboxVisualGuide;

    private GuiButton disableButton;
    //private GuiButton applyButton;
    //private GuiButton resetButton;

    private AxisAlignedBB tempBox;
    private double tempGravityStrength;
    private boolean tempIsInverted;


    public final int FIELD_TOP   = 0;
    public final int FIELD_LEFT  = 1;
    public final int FIELD_FRONT = 2;

    public final int FIELD_BACK  = 3;
    public final int FIELD_RIGHT = 4;
    public final int FIELD_BOTTOM= 5;

    public final int FIELD_STRENGTH= 10;

    public final int BTN_ENABLE = 6;
    //public final int BTN_APPLY  = 7;
    //public final int BTN_RESET  = 9;
    public final int CHECKBOX_VISUAL  = 8;
    public final int CHECKBOX_INVERT  = 11;


    private GuiElementInfoRegion electricInfoRegion = new GuiElementInfoRegion(
            (this.width - this.xSize) / 2 + 112,
            (this.height - this.ySize) / 2 + 87,
            52, 9, new ArrayList<String>(), this.width, this.height, this);

    private TileEntityGravitation tile;

    public GuiArtificialGravity(InventoryPlayer player, TileEntityGravitation tile) {
        super(new ContainerArtificalGravity(player, tile));
        this.xSize = 176;
        this.ySize = 231;
        this.tile = tile;


        // tempBox = cloneAABB(tile.getGravityBox());
        tempGravityStrength = tile.getGravityVector().y * 100.0;
        tempIsInverted = tempGravityStrength > 0;
        tempGravityStrength = Math.abs(tempGravityStrength);

    }

    private AxisAlignedBB cloneAABB(AxisAlignedBB box)
    {
        return AxisAlignedBB.getBoundingBox(box.minX, box.minY, box.minZ, box.maxX, box.maxY, box.maxZ);
    }

    protected void sendDataToServer()
    {
        BlockVec3 pos = new BlockVec3(tile);
        BlockVec3 min = new BlockVec3((int)tempBox.minX, (int)tempBox.minY, (int)tempBox.minZ);
        BlockVec3 max = new BlockVec3((int)tempBox.maxX, (int)tempBox.maxY, (int)tempBox.maxZ);
        double actualStrength = this.tempGravityStrength / 100;
        if(!tempIsInverted) {
            actualStrength *= -1;
        }
        AmunRa.packetPipeline.sendToServer(new PacketSimpleAR(PacketSimpleAR.EnumSimplePacket.S_ARTIFICIAL_GRAVITY_SETTINGS, pos, min, max, actualStrength));
        tile.setGravityBox(cloneAABB(tempBox));
        Vector3 gravVec = tile.getGravityVector().clone();
        gravVec.y = actualStrength;
        tile.setGravityVector(gravVec);
    }

    protected void resetDataFromTile()
    {
        tempBox = cloneAABB(tile.getGravityBox());
        topValueField.text = Integer.toString((int)tempBox.maxY);
        backValueField.text = Integer.toString((int)tempBox.maxZ);
        rightValueField.text = Integer.toString((int)tempBox.maxX);

        bottomValueField.text = Integer.toString((int)tempBox.minY * -1);
        frontValueField.text = Integer.toString((int)tempBox.minZ * -1);
        leftValueField.text = Integer.toString((int)tempBox.minX * -1);

        tempGravityStrength = tile.getGravityVector().y * 100.0;
        tempIsInverted = tempGravityStrength > 0;
        tempGravityStrength = Math.abs(tempGravityStrength);
    }

    @Override
    protected void actionPerformed(GuiButton btn)
    {
        switch(btn.id) {
        case BTN_ENABLE:
            GalacticraftCore.packetPipeline.sendToServer(new PacketSimple(EnumSimplePacket.S_UPDATE_DISABLEABLE_BUTTON, new Object[] { this.tile.xCoord, this.tile.yCoord, this.tile.zCoord, 0 }));
            break;
        /*case BTN_APPLY:
            BlockVec3 pos = new BlockVec3(tile);
            BlockVec3 min = new BlockVec3((int)tempBox.minX, (int)tempBox.minY, (int)tempBox.minZ);
            BlockVec3 max = new BlockVec3((int)tempBox.maxX, (int)tempBox.maxY, (int)tempBox.maxZ);
            double actualStrength = this.tempGravityStrength / 100;
            if(!tempIsInverted) {
                actualStrength *= -1;
            }
            AmunRa.packetPipeline.sendToServer(new PacketSimpleAR(PacketSimpleAR.EnumSimplePacket.S_ARTIFICIAL_GRAVITY_SETTINGS, pos, min, max, actualStrength));
            tile.setGravityBox(cloneAABB(tempBox));
            Vector3 gravVec = tile.getGravityVector().clone();
            gravVec.y = actualStrength;
            tile.setGravityVector(gravVec);
            break;* /
        case BTN_RESET:
            tempBox = cloneAABB(tile.getGravityBox());
            topValueField.text = Integer.toString((int)tempBox.maxY);
            backValueField.text = Integer.toString((int)tempBox.maxZ);
            rightValueField.text = Integer.toString((int)tempBox.maxX);

            bottomValueField.text = Integer.toString((int)tempBox.minY * -1);
            frontValueField.text = Integer.toString((int)tempBox.minZ * -1);
            leftValueField.text = Integer.toString((int)tempBox.minX * -1);

            tempGravityStrength = tile.getGravityVector().y * 100.0;
            tempIsInverted = tempGravityStrength > 0;
            tempGravityStrength = Math.abs(tempGravityStrength);
            break;*/
        }
    }

    @Override
    public void initGui()
    {
        super.initGui();
        List<String> batterySlotDesc = new ArrayList<String>();
        batterySlotDesc.add(GCCoreUtil.translate("gui.batterySlot.desc.0"));
        batterySlotDesc.add(GCCoreUtil.translate("gui.batterySlot.desc.1"));
        this.infoRegions.add(
                new GuiElementInfoRegion(
                        (this.width - this.xSize) / 2 + 152,
                        (this.height - this.ySize) / 2 + 134,
                        18,
                        18, batterySlotDesc, this.width, this.height, this)
                );

        this.electricInfoRegion.xPosition = (this.width - this.xSize) / 2 + 98;
        this.electricInfoRegion.yPosition = (this.height - this.ySize) / 2 + 118 + 22;
        this.electricInfoRegion.parentWidth = this.width;
        this.electricInfoRegion.parentHeight = this.height;
        this.infoRegions.add(this.electricInfoRegion);


        final int guiX = (this.width - this.xSize) / 2;
        final int guiY = (this.height - this.ySize) / 2;

        // int inputOffset = 28;
        int offsetX1 = 10;
        int offsetX2 = 50;
        topValueField   = new GuiElementTextBox(FIELD_TOP,    this, offsetX1+guiX+32,   guiY+28-4, 28, 18, "0", true, 2, true);
        leftValueField  = new GuiElementTextBox(FIELD_LEFT,   this, offsetX1+guiX+2,    guiY+44, 28, 18, "0", true, 2, true);
        frontValueField = new GuiElementTextBox(FIELD_FRONT,  this, offsetX2+guiX+62-40,guiY+28+16, 28, 18, "0", true, 2, true);

        backValueField  = new GuiElementTextBox(FIELD_BACK,   this, offsetX1+guiX+2,    guiY+64, 28, 18, "0", true, 2, true);
        rightValueField = new GuiElementTextBox(FIELD_RIGHT,  this, offsetX2+guiX+22,   guiY+48+16, 28, 18, "0", true, 2, true);
        bottomValueField= new GuiElementTextBox(FIELD_BOTTOM, this, offsetX1+guiX+32,   guiY+68+16, 28, 18, "0", true, 2, true);

        this.addInputField(leftValueField);
        this.addInputField(backValueField);
        this.addInputField(bottomValueField);

        this.addInputField(rightValueField);
        this.addInputField(frontValueField);
        this.addInputField(topValueField);

        // buttons
        int yOffsetBtns = -10+3;
        //applyButton     = new GuiButton(BTN_APPLY,  guiX + 110, guiY + 50+yOffsetBtns, 50, 20, GCCoreUtil.translate("gui.message.mothership.apply"));
        //resetButton     = new GuiButton(BTN_RESET,  guiX + 110, guiY + 70+yOffsetBtns, 50, 20, GCCoreUtil.translate("gui.message.mothership.reset"));
        disableButton   = new GuiButton(BTN_ENABLE, guiX + 110, guiY + 90+yOffsetBtns, 50, 20, GCCoreUtil.translate("gui.button.disable.name"));

        checkboxVisualGuide = new GuiElementCheckbox(CHECKBOX_VISUAL, this, guiX + 80, guiY + 24, GCCoreUtil.translate("gui.checkbox.show_visual_guide"));

        //this.buttonList.add(applyButton);
        this.buttonList.add(disableButton);
        //this.buttonList.add(resetButton);
        this.buttonList.add(checkboxVisualGuide);


        this.strengthField = new GuiElementTextBox(FIELD_STRENGTH, this, guiX + 60, guiY + 110, 38, 18, "0", true, 2, true);
        this.addInputField(this.strengthField);

        checkboxInvert = new GuiElementCheckbox(CHECKBOX_INVERT, this, guiX + 100, guiY + 112, GCCoreUtil.translate("gui.checkbox.invert_force"));
        this.buttonList.add(checkboxInvert);
    }

    protected void addInputField(GuiElementTextBox box)
    {
        this.buttonList.add(box);
        this.inputFieldList.add(box);
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int par1, int par2)
    {
        this.fontRendererObj.drawString(this.tile.getInventoryName(), 8, 10, 4210752);

        this.fontRendererObj.drawString(GCCoreUtil.translate("container.inventory"), 8, this.ySize - 90 + 2, 4210752);
    }

    @Override
    public void drawScreen(int par1, int par2, float par3)
    {
        if (this.tile.disableCooldown > 0)
        {
            //disableButton.enabled = false;
            //applyButton.enabled = false;
            //resetButton.enabled = false;


        } else {
            //disableButton.enabled = true;
            //applyButton.enabled = true;
            //resetButton.enabled = true;
        }

        super.drawScreen(par1, par2, par3);
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float p_146976_1_, int p_146976_2_, int p_146976_3_) {
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.mc.getTextureManager().bindTexture(guiTexture);
        final int xOffset = (this.width - this.xSize) / 2;
        final int yOffset = (this.height - this.ySize) / 2;
        this.drawTexturedModalRect(xOffset, yOffset + 5, 0, 0, this.xSize, this.ySize);

        if (this.tile != null)
        {
            if(tile.getDisabled(0)) {

                disableButton.displayString = GCCoreUtil.translate("gui.button.enable.name");
            } else {
                disableButton.displayString = GCCoreUtil.translate("gui.button.disable.name");

            }

            int scale = this.tile.getScaledElecticalLevel(54);
            this.drawTexturedModalRect(xOffset + 99, yOffset + 119 + 22, 176, 0, Math.min(scale, 54), 7);



            List<String> electricityDesc = new ArrayList<String>();
            electricityDesc.add(GCCoreUtil.translate("gui.energyStorage.desc.0"));
            EnergyDisplayHelper.getEnergyDisplayTooltip(this.tile.getEnergyStoredGC(), this.tile.getMaxEnergyStoredGC(), electricityDesc);
            electricityDesc.add(EnumChatFormatting.AQUA + GCCoreUtil.translate("gui.message.energy_usage") + ": " + EnergyDisplayHelper.getEnergyDisplayS(tile.storage.getMaxExtract())+"/t");
//          electricityDesc.add(EnumColor.YELLOW + GCCoreUtil.translate("gui.energyStorage.desc.1") + ((int) Math.floor(this.collector.getEnergyStoredGC()) + " / " + (int) Math.floor(this.collector.getMaxEnergyStoredGC())));
            this.electricInfoRegion.tooltipStrings = electricityDesc;


            this.fontRendererObj.drawString(GCCoreUtil.translate("gui.message.status.name") + ": " + this.getStatus(),
                    xOffset + 8 , yOffset+130, 4210752);

            this.fontRendererObj.drawString(GCCoreUtil.translate("gui.message.force.strength") + ": " ,
                    xOffset + 8 , yOffset+116, 4210752);

        }
    }

    @Override
    public boolean canPlayerEdit(GuiElementTextBox textBox, EntityPlayer player) {
        return true;
    }

    @Override
    public void onTextChanged(GuiElementTextBox textBox, String newText) {

        if(newText == null) {
            // don't do anything
            return;
        }
        double newValue;
        try
        {
            newValue = Double.parseDouble(newText);
            //newValue = Integer.parseInt(newText);
        } catch(NumberFormatException wat) {
            // this is ridiculous
            return;
        }
        if(newValue < 0) {
            return;
        }



        switch(textBox.id) {
        case FIELD_TOP:
            tempBox.maxY = (int)newValue;
            break;
        case FIELD_BACK:
            tempBox.maxZ = (int)newValue;
            break;
        case FIELD_RIGHT:
            tempBox.maxX = (int)newValue;
            break;

        case FIELD_BOTTOM:
            tempBox.minY = (int)newValue * -1;
            break;
        case FIELD_FRONT:
            tempBox.minZ = (int)newValue * -1;
            break;
        case FIELD_LEFT:
            tempBox.minX = (int)newValue * -1;
            break;
        case FIELD_STRENGTH:
            tempGravityStrength = Math.abs(newValue);
            break;
        default:
            return;
        }
        this.sendDataToServer();
    }

    @Override
    public String getInitialText(GuiElementTextBox textBox)
    {
        tempBox = tile.getGravityBox();

        switch(textBox.id) {
        case FIELD_TOP:
            return Integer.toString((int)tempBox.maxY);
        case FIELD_BACK:
            return Integer.toString((int)tempBox.maxZ);
        case FIELD_RIGHT:
            return Integer.toString((int)tempBox.maxX);

        case FIELD_BOTTOM:
            return Integer.toString((int)tempBox.minY * -1);
        case FIELD_FRONT:
            return Integer.toString((int)tempBox.minZ * -1);
        case FIELD_LEFT:
            return Integer.toString((int)tempBox.minX * -1);
        case FIELD_STRENGTH:
            return Integer.toString((int) Math.abs(tempGravityStrength));
        }
        return Integer.toString(textBox.id);


    }

    @Override
    protected void keyTyped(char keyChar, int keyID)
    {
        if (keyID != Keyboard.KEY_ESCAPE /*&& keyID != this.mc.gameSettings.keyBindInventory.getKeyCode()*/)
        {
            // do the fields
            for(GuiElementTextBox box: inputFieldList) {
                if(box.keyTyped(keyChar, keyID)) {
                    return;
                }
            }
        }

        super.keyTyped(keyChar, keyID);
    }

    @Override
    public int getTextColor(GuiElementTextBox textBox) {
        return ColorUtil.to32BitColor(255, 20, 255, 20);
    }

    @Override
    public void onIntruderInteraction(GuiElementTextBox textBox) {

    }

    @Override
    public void onSelectionChanged(GuiElementCheckbox checkbox, boolean newSelected) {

        switch(checkbox.id) {
        case CHECKBOX_VISUAL:
            this.tile.isBoxShown = newSelected;
            this.sendDataToServer();
            break;
        case CHECKBOX_INVERT:
            tempIsInverted = newSelected;
            this.sendDataToServer();
            break;
        }
    }

    @Override
    public boolean canPlayerEdit(GuiElementCheckbox checkbox, EntityPlayer player) {
        return true;
    }

    @Override
    public boolean getInitiallySelected(GuiElementCheckbox checkbox) {
        switch(checkbox.id) {
        case CHECKBOX_VISUAL:
            return this.tile.isBoxShown;
        case CHECKBOX_INVERT:
            return tempIsInverted;
        }
        return false;
    }

    @Override
    public void onIntruderInteraction() {

    }

    protected String getStatus() {
        return this.tile.getGUIstatus();
    }

}
