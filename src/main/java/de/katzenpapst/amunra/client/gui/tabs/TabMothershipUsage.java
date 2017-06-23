package de.katzenpapst.amunra.client.gui.tabs;

import de.katzenpapst.amunra.AmunRa;
import de.katzenpapst.amunra.client.gui.GuiMothershipSettings;
import de.katzenpapst.amunra.helper.PlayerID;
import de.katzenpapst.amunra.mothership.Mothership;
import de.katzenpapst.amunra.mothership.Mothership.PermissionMode;
import de.katzenpapst.amunra.network.packet.PacketSimpleAR;
import de.katzenpapst.amunra.network.packet.PacketSimpleAR.EnumSimplePacket;
import de.katzenpapst.amunra.tile.TileEntityMothershipSettings;
import micdoodle8.mods.galacticraft.core.client.gui.element.GuiElementDropdown;
import micdoodle8.mods.galacticraft.core.util.GCCoreUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;

public class TabMothershipUsage extends AbstractPermissionTab {

    protected static final ResourceLocation icon = new ResourceLocation(AmunRa.ASSETPREFIX, "textures/gui/usage-permission.png");

    public TabMothershipUsage(TileEntityMothershipSettings tile, GuiMothershipSettings parent, Minecraft mc, int width, int height, int xSize, int ySize) {
        super(tile, parent, mc, width, height, xSize, ySize);
    }

    @Override
    public void resetData() {
        Mothership.PermissionMode pm = this.tile.getMothership().getUsagePermissionMode();
        modeDropdown.selectedOption = pm.ordinal();
        playerIdList.clear();

        Mothership m = this.tile.getMothership();

        playerIdList.addAll(m.getPlayerListUsage());
        selectBox.clear();
        for(PlayerID pid: playerIdList) {
            selectBox.addString(pid.getName());
        }
    }

    @Override
    public ResourceLocation getIcon() {
        return icon;
    }

    @Override
    public String getTooltip() {
        return GCCoreUtil.translate("tile.mothershipSettings.permissionUse");
    }

    @Override
    public void onSelectionChanged(GuiElementDropdown dropdown, int selection) {
        if(dropdown == modeDropdown) {
            PermissionMode mode = PermissionMode.values()[selection];
            tile.getMothership().setUsagePermissionMode(mode);
            this.applyData();
        }
    }

    @Override
    public int getInitialSelection(GuiElementDropdown dropdown) {
        return this.tile.getMothership().getUsagePermissionMode().ordinal();
    }

    @Override
    protected void addUsername(Mothership mothership, String userName) {
        AmunRa.packetPipeline.sendToServer(new PacketSimpleAR(
                EnumSimplePacket.S_ADD_MOTHERSHIP_PLAYER,
                this.tile.getMothership().getID(),
                textBoxUsername.text, 1));
    }

    @Override
    protected void removeUsernameFromList(int position) {
        playerIdList.remove(position);
        tile.getMothership().setPlayerListUsage(playerIdList);
    }

    @Override
    public String getTooltipDescription()
    {
        return GCCoreUtil.translate("tile.mothershipSettings.permissionUseDesc");
    }
}
