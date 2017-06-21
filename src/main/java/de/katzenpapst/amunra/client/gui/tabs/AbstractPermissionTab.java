package de.katzenpapst.amunra.client.gui.tabs;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import de.katzenpapst.amunra.client.gui.GuiMothershipSettings;
import de.katzenpapst.amunra.client.gui.GuiMothershipSettings.IMothershipSettingsTab;
import de.katzenpapst.amunra.client.gui.elements.StringSelectBox;
import de.katzenpapst.amunra.client.gui.elements.StringSelectBox.ISelectBoxCallback;
import de.katzenpapst.amunra.helper.PlayerID;
import de.katzenpapst.amunra.mothership.Mothership;
import de.katzenpapst.amunra.tile.TileEntityMothershipSettings;
import micdoodle8.mods.galacticraft.core.client.gui.element.GuiElementDropdown;
import micdoodle8.mods.galacticraft.core.client.gui.element.GuiElementTextBox;
import micdoodle8.mods.galacticraft.core.client.gui.element.GuiElementDropdown.IDropboxCallback;
import micdoodle8.mods.galacticraft.core.client.gui.element.GuiElementTextBox.ITextBoxCallback;
import micdoodle8.mods.galacticraft.core.util.ColorUtil;
import micdoodle8.mods.galacticraft.core.util.GCCoreUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.entity.player.EntityPlayer;

abstract public class AbstractPermissionTab extends AbstractTab implements IDropboxCallback, ITextBoxCallback, ISelectBoxCallback, IMothershipSettingsTab {


    protected final TileEntityMothershipSettings tile;

    protected GuiElementDropdown modeDropdown;
    protected GuiElementTextBox textBoxUsername;
    protected StringSelectBox selectBox;

    protected GuiButton addBtn;
    protected GuiButton rmBtn;

    //protected Set<PlayerID> playerIdList =  new HashSet<PlayerID>();
    protected List<PlayerID> playerIdList = new ArrayList<PlayerID>();

    protected Map<Mothership.PermissionMode, String> permissionModeMap = new HashMap<Mothership.PermissionMode, String>();

    protected String error = "";
    protected float errorTime = 0;

    public AbstractPermissionTab(TileEntityMothershipSettings tile, GuiMothershipSettings parent, Minecraft mc, int width, int height, int xSize, int ySize) {
        super(parent, mc, width, height, xSize, ySize);
        this.tile = tile;

        permissionModeMap.put(Mothership.PermissionMode.ALL, GCCoreUtil.translate("tile.mothershipSettings.permission.allowAll"));
        permissionModeMap.put(Mothership.PermissionMode.NONE, GCCoreUtil.translate("tile.mothershipSettings.permission.allowNone"));
        permissionModeMap.put(Mothership.PermissionMode.WHITELIST, GCCoreUtil.translate("tile.mothershipSettings.permission.whitelist"));
        permissionModeMap.put(Mothership.PermissionMode.BLACKLIST, GCCoreUtil.translate("tile.mothershipSettings.permission.blacklist"));
    }

    protected abstract void addUsername(Mothership mothership, String userName);

    protected abstract void removeUsernameFromList(int position);

    @Override
    public boolean actionPerformed(GuiButton btn)
    {
        if(btn == addBtn) {
            //
            // AmunRa.packetPipeline.sendToServer(new PacketSimpleAR(EnumSimplePacket.S_ADD_MOTHERSHIP_PLAYER, this.tile.getMothership().getID(), textBoxUsername.text));
            addUsername(this.tile.getMothership(), textBoxUsername.text);
            textBoxUsername.text = "";
            addBtn.enabled = false;
            return true;
        }
        if(btn == rmBtn) {
            int selection = selectBox.getSelectedStringIndex();
            if(selection != -1) {
                removeUsernameFromList(selection);
                selectBox.clearSelection();
                applyData();
            }
            return true;
        }
        return false;
    }

    abstract public void resetData();

    @Override
    public void mothershipResponsePacketRecieved() {
        resetData();
    }

    protected String[] getDropdownOptions() {
        int num = Mothership.PermissionMode.values().length;
        String[] result = new String[num];

        for(int i=0;i<num;i++) {
            result[i] = permissionModeMap.get(Mothership.PermissionMode.values()[i]);
        }

        return result;
    }

    protected void applyData() {
        GuiMothershipSettings actualParent = ((GuiMothershipSettings) this.parent);
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

    // DROPDOWN SHIT
    @Override
    public boolean canBeClickedBy(GuiElementDropdown dropdown, EntityPlayer player) {
        return true;
    }

    @Override
    abstract public int getInitialSelection(GuiElementDropdown dropdown);

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
