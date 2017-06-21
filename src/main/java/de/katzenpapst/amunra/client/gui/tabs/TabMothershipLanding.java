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

public class TabMothershipLanding extends AbstractPermissionTab {

    protected static final ResourceLocation icon = new ResourceLocation(AmunRa.ASSETPREFIX, "textures/gui/landing-permission.png");

    public TabMothershipLanding(TileEntityMothershipSettings tile, GuiMothershipSettings parent, Minecraft mc, int width, int height, int xSize, int ySize) {
        super(tile, parent, mc, width, height, xSize, ySize);
    }

    @Override
    public void resetData() {
        Mothership.PermissionMode pm = this.tile.getMothership().getLandingPermissionMode();
        modeDropdown.selectedOption = pm.ordinal();
        playerIdList.clear();

        Mothership m = this.tile.getMothership();

        playerIdList.addAll(m.getPlayerListLanding());
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
        return GCCoreUtil.translate("tile.mothershipSettings.permissionLand");
    }

    @Override
    public void onSelectionChanged(GuiElementDropdown dropdown, int selection) {
        if(dropdown == modeDropdown) {
            PermissionMode mode = PermissionMode.values()[selection];
            tile.getMothership().setLandingPermissionMode(mode);
            this.applyData();
        }

    }

    @Override
    public int getInitialSelection(GuiElementDropdown dropdown) {
        return this.tile.getMothership().getLandingPermissionMode().ordinal();
    }

    @Override
    protected void addUsername(Mothership mothership, String userName) {
        AmunRa.packetPipeline.sendToServer(new PacketSimpleAR(
                EnumSimplePacket.S_ADD_MOTHERSHIP_PLAYER,
                this.tile.getMothership().getID(),
                textBoxUsername.text, 0));
    }

    @Override
    protected void removeUsernameFromList(int position) {
        playerIdList.remove(position);
        tile.getMothership().setPlayerListLanding(playerIdList);
    }

    @Override
    public String getTooltipDescription()
    {
        return GCCoreUtil.translate("tile.mothershipSettings.permissionLandDesc");
    }
}
