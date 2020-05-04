package de.katzenpapst.amunra.client.gui;

import java.util.List;

import org.lwjgl.opengl.GL11;

import de.katzenpapst.amunra.AmunRa;
import de.katzenpapst.amunra.client.gui.tabs.AbstractTab;
import de.katzenpapst.amunra.client.gui.tabs.TabMothershipCustom;
import de.katzenpapst.amunra.client.gui.tabs.TabMothershipLanding;
import de.katzenpapst.amunra.client.gui.tabs.TabMothershipUsage;
import de.katzenpapst.amunra.inventory.ContainerMothershipSettings;
import de.katzenpapst.amunra.mothership.Mothership;
import de.katzenpapst.amunra.network.packet.PacketSimpleAR;
import de.katzenpapst.amunra.network.packet.PacketSimpleAR.EnumSimplePacket;
import de.katzenpapst.amunra.tile.TileEntityMothershipSettings;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;

public class GuiMothershipSettings extends GuiContainerTabbed {

    public interface IMothershipSettingsTab {
        public void mothershipResponsePacketRecieved();

        public void mothershipOperationFailed(String message);
    }

    private static final ResourceLocation guiTexture = new ResourceLocation(AmunRa.ASSETPREFIX, "textures/gui/ms_settings.png");

    private final TileEntityMothershipSettings tile;
    private Mothership ship;

    protected List<ResourceLocation> mothershipTextures;

    public GuiMothershipSettings(InventoryPlayer par1InventoryPlayer, TileEntityMothershipSettings tile) {
        super(new ContainerMothershipSettings(par1InventoryPlayer, tile));
        this.ySize = 201;
        this.xSize = 176;
        this.tile = tile;
        mothershipTextures = AmunRa.instance.getPossibleMothershipTextures();
        ship = tile.getMothership();

    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float ticksProbably, int somethingX, int somethingY) {
        GL11.glPushMatrix();
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.mc.renderEngine.bindTexture(guiTexture);
        final int xOffset = (this.width - this.xSize) / 2;
        final int yOffset = (this.height - this.ySize) / 2;
        this.drawTexturedModalRect(xOffset, yOffset, 0, 0, this.xSize, this.ySize);

        GL11.glPopMatrix();
    }

    public void mothershipOperationFailed(String message) {
        AbstractTab curTab = this.getActiveTab();
        if(curTab instanceof IMothershipSettingsTab) {
            ((IMothershipSettingsTab)curTab).mothershipOperationFailed(message);
        }
    }

    public void mothershipResponsePacketRecieved() {
        AbstractTab curTab = this.getActiveTab();
        if(curTab instanceof IMothershipSettingsTab) {
            ((IMothershipSettingsTab)curTab).mothershipResponsePacketRecieved();
        }
    }

    public void sendMothershipSettingsPacket() {
        NBTTagCompound nbt = new NBTTagCompound ();
        ship.writeSettingsToNBT(nbt);
        AmunRa.packetPipeline.sendToServer(new PacketSimpleAR(EnumSimplePacket.S_SET_MOTHERSHIP_SETTINGS, ship.getID(), nbt));
    }


    @SuppressWarnings("unchecked")
    @Override
    public void initGui()
    {
        super.initGui();

        this.addTab(new TabMothershipCustom(tile, this, mc, width, height, xSize, ySize));
        this.addTab(new TabMothershipLanding(tile, this, mc, width, height, xSize, ySize));
        this.addTab(new TabMothershipUsage(tile, this, mc, width, height, xSize, ySize));

    }


}
