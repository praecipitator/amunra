package de.katzenpapst.amunra.client.gui.tabs;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.katzenpapst.amunra.AmunRa;
import de.katzenpapst.amunra.client.gui.GuiMothershipSettings;
import de.katzenpapst.amunra.client.gui.GuiMothershipSettings.IMothershipSettingsTab;
import de.katzenpapst.amunra.client.gui.elements.StringSelectBox;
import de.katzenpapst.amunra.client.gui.elements.StringSelectBox.ISelectBoxCallback;
import de.katzenpapst.amunra.helper.PlayerID;
import de.katzenpapst.amunra.mothership.Mothership;
import de.katzenpapst.amunra.mothership.Mothership.PermissionMode;
import de.katzenpapst.amunra.network.packet.PacketSimpleAR;
import de.katzenpapst.amunra.network.packet.PacketSimpleAR.EnumSimplePacket;
import de.katzenpapst.amunra.tile.TileEntityMothershipSettings;
import micdoodle8.mods.galacticraft.core.client.gui.element.GuiElementDropdown;
import micdoodle8.mods.galacticraft.core.client.gui.element.GuiElementTextBox;
import micdoodle8.mods.galacticraft.core.client.gui.element.GuiElementTextBox.ITextBoxCallback;
import micdoodle8.mods.galacticraft.core.client.gui.element.GuiElementDropdown.IDropboxCallback;
import micdoodle8.mods.galacticraft.core.util.ColorUtil;
import micdoodle8.mods.galacticraft.core.util.GCCoreUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;

public class TabMothershipPermission extends AbstractTab implements IDropboxCallback, ITextBoxCallback, ISelectBoxCallback, IMothershipSettingsTab {

    protected static final ResourceLocation icon = new ResourceLocation(AmunRa.ASSETPREFIX, "textures/gui/permission.png");

    private final TileEntityMothershipSettings tile;

    private GuiElementDropdown modeDropdown;
    private GuiElementTextBox textBoxUsername;
    private StringSelectBox selectBox;

    private GuiButton addBtn;
    private GuiButton rmBtn;

    private List<PlayerID> playerIdList =  new ArrayList<PlayerID>();

    private Map<Mothership.PermissionMode, String> permissionModeMap = new HashMap<Mothership.PermissionMode, String>();

    private String error = "";
    private float errorTime = 0;

    public TabMothershipPermission(TileEntityMothershipSettings tile, GuiMothershipSettings parent, Minecraft mc, int width, int height, int xSize, int ySize) {
        super(parent, mc, width, height, xSize, ySize);
        this.tile = tile;

        permissionModeMap.put(Mothership.PermissionMode.ALL, GCCoreUtil.translate("tile.mothershipSettings.permission.allowAll"));
        permissionModeMap.put(Mothership.PermissionMode.NONE, GCCoreUtil.translate("tile.mothershipSettings.permission.allowNone"));
        permissionModeMap.put(Mothership.PermissionMode.WHITELIST, GCCoreUtil.translate("tile.mothershipSettings.permission.whitelist"));
        permissionModeMap.put(Mothership.PermissionMode.BLACKLIST, GCCoreUtil.translate("tile.mothershipSettings.permission.blacklist"));
    }

    @Override
    public boolean actionPerformed(GuiButton btn)
    {
        if(btn == addBtn) {
            //
            AmunRa.packetPipeline.sendToServer(new PacketSimpleAR(EnumSimplePacket.S_ADD_MOTHERSHIP_PLAYER, this.tile.getMothership().getID(), textBoxUsername.text));
            textBoxUsername.text = "";
            addBtn.enabled = false;
            return true;
        }
        if(btn == rmBtn) {
            int selection = selectBox.getSelectedStringIndex();
            if(selection != -1) {
                playerIdList.remove(selection);
                selectBox.clearSelection();
                tile.getMothership().setPlayerList(playerIdList);
                applyData();
            }
            return true;
        }
        return false;
    }

    public void resetData() {
        Mothership.PermissionMode pm = this.tile.getMothership().getPermissionMode();
        modeDropdown.selectedOption = pm.ordinal();
        playerIdList = this.tile.getMothership().getPlayerList();
        selectBox.clear();
        for(PlayerID pid: playerIdList) {
            selectBox.addString(pid.getName());
        }
    }

    @Override
    public void mothershipResponsePacketRecieved() {
        // T
        resetData();
    }

    private String[] getDropdownOptions() {
        int num = Mothership.PermissionMode.values().length;
        String[] result = new String[num];

        for(int i=0;i<num;i++) {
            result[i] = permissionModeMap.get(Mothership.PermissionMode.values()[i]);
        }

        return result;
    }

    private void applyData() {
        GuiMothershipSettings actualParent = ((GuiMothershipSettings) this.parent);
        /*tile.getMothership().setPlayerList(playerIdList);
        tile.getMothership().setPermissionMode(mode);*/
        actualParent.sendMothershipSettingsPacket();
    }

    @Override
    public void initGui() {

        final int guiX = (this.width - this.xSize) / 2;
        final int guiY = (this.height - this.ySize) / 2;



        modeDropdown = new GuiElementDropdown(1, this, guiX+90, guiY+14, getDropdownOptions());

        textBoxUsername = new GuiElementTextBox(2, this, guiX+5, guiY+30, 95, 20, "", false, 50, false);

        selectBox = new StringSelectBox(this, 3, guiX+5, guiY+50, 95, 50);


        addBtn = new GuiButton(4, guiX+100, guiY+30, 70, 20, GCCoreUtil.translate("tile.mothershipSettings.permission.addUser"));
        rmBtn = new GuiButton(5, guiX+100, guiY+50, 70, 20, GCCoreUtil.translate("tile.mothershipSettings.permission.removeUser"));
        rmBtn.enabled = false;
        addBtn.enabled = false;

        /*this.addButton(applyButton);*/
        this.addButton(modeDropdown);
        this.addButton(selectBox);
        this.addButton(addBtn);
        this.addButton(rmBtn);
        this.addTextBox(textBoxUsername);


        resetData();
    }

    @Override
    public void onTabActivated() {
        resetData();
    }

    @Override
    protected void drawExtraScreenElements(int mouseX, int mouseY, float ticks)
    {
        final int guiX = (this.width - this.xSize) / 2;
        final int guiY = (this.height - this.ySize) / 2;

        this.fontRendererObj.drawString(this.getTooltip(), guiX+5, guiY+5, 4210752);


        this.fontRendererObj.drawString(GCCoreUtil.translate("tile.mothershipSettings.permission.allowLabel") + ":", guiX+9, guiY+16, 4210752);

        if(errorTime > 0) {
            this.fontRendererObj.drawSplitString(error, guiX+102, guiY+80, 70, 4210752);
            errorTime -= ticks;
        }
        //this.fontRendererObj.drawString("fooo", guiX+102, guiY+80, 4210752);

        this.fontRendererObj.drawString(GCCoreUtil.translate("container.inventory"), guiX+8, guiY+this.ySize - 94, 4210752);
    }

    @Override
    public ResourceLocation getIcon() {
        return icon;
    }

    @Override
    public String getTooltip() {
        return GCCoreUtil.translate("tile.mothershipSettings.permission");
    }

    // DROPDOWN SHIT
    @Override
    public boolean canBeClickedBy(GuiElementDropdown dropdown, EntityPlayer player) {
        return true;
    }

    @Override
    public void onSelectionChanged(GuiElementDropdown dropdown, int selection) {
        if(dropdown == modeDropdown) {
            PermissionMode mode = PermissionMode.values()[selection];
            tile.getMothership().setPermissionMode(mode);
            this.applyData();
        }

    }

    @Override
    public int getInitialSelection(GuiElementDropdown dropdown) {
        return this.tile.getMothership().getPermissionMode().ordinal();
    }

    @Override
    public void onIntruderInteraction() {

    }

    // TEXTBOX SHIT
    @Override
    public boolean canPlayerEdit(GuiElementTextBox textBox, EntityPlayer player) {
        return true;
    }

    @Override
    public void onTextChanged(GuiElementTextBox textBox, String newText) {
        addBtn.enabled = (newText != null && !newText.isEmpty());

    }

    @Override
    public String getInitialText(GuiElementTextBox textBox) {
        return "";
        //return "";
    }

    @Override
    public int getTextColor(GuiElementTextBox textBox) {
        return ColorUtil.to32BitColor(255, 20, 255, 20);
    }

    @Override
    public void onIntruderInteraction(GuiElementTextBox textBox) {

    }

    // STRINGSELECTBOX SHIT
    @Override
    public void onSelectionChanged(StringSelectBox box, int selection) {
        rmBtn.enabled = box.hasSelection();
    }

    @Override
    public void mothershipOperationFailed(String message) {
        error = message;
        errorTime = 60.0F;
    }

}
