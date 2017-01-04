package de.katzenpapst.amunra.client.gui;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.opengl.GL11;

import de.katzenpapst.amunra.AmunRa;
import de.katzenpapst.amunra.inventory.ContainerRocketEngine;
import de.katzenpapst.amunra.tile.TileEntityIsotopeGenerator;
import de.katzenpapst.amunra.tile.TileEntityMothershipEngineJet;
import micdoodle8.mods.galacticraft.core.GalacticraftCore;
import micdoodle8.mods.galacticraft.core.client.gui.container.GuiContainerGC;
import micdoodle8.mods.galacticraft.core.client.gui.element.GuiElementInfoRegion;
import micdoodle8.mods.galacticraft.core.energy.EnergyDisplayHelper;
import micdoodle8.mods.galacticraft.core.network.PacketSimple;
import micdoodle8.mods.galacticraft.core.network.PacketSimple.EnumSimplePacket;
import micdoodle8.mods.galacticraft.core.util.EnumColor;
import micdoodle8.mods.galacticraft.core.util.GCCoreUtil;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.util.ResourceLocation;

public class GuiRocketEngine extends GuiContainerGC {

    private static final ResourceLocation guiTexture = new ResourceLocation(AmunRa.ASSETPREFIX, "textures/gui/ms_rocket.png");

    private final TileEntityMothershipEngineJet tileEngine;

    private GuiButton buttonEnable;
    private GuiElementInfoRegion electricInfoRegion = new GuiElementInfoRegion((this.width - this.xSize) / 2 + 107, (this.height - this.ySize) / 2 + 101, 56, 9, new ArrayList<String>(), this.width, this.height, this);

    public GuiRocketEngine(InventoryPlayer par1InventoryPlayer, TileEntityMothershipEngineJet tileEngine) {
        super(new ContainerRocketEngine(par1InventoryPlayer, tileEngine));
        this.tileEngine = tileEngine;
        this.ySize = 201;
        this.xSize = 176;

        if(tileEngine == null) {
            throw new RuntimeException("TileEntity of engine is null");
        }
    }

    @Override
    protected void actionPerformed(GuiButton par1GuiButton)
    {
        switch (par1GuiButton.id)
        {
        case 0:
            GalacticraftCore.packetPipeline.sendToServer(new PacketSimple(EnumSimplePacket.S_UPDATE_DISABLEABLE_BUTTON, new Object[] { this.tileEngine.xCoord, this.tileEngine.yCoord, this.tileEngine.zCoord, 0 }));
            break;
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public void initGui()
    {
        super.initGui();

        this.buttonList.add(this.buttonEnable = new GuiButton(0, this.width / 2 - 36, this.height / 2 - 19, 72, 20, GCCoreUtil.translate("gui.button.enable.name")));
        /*
        List<String> fuelTankDesc = new ArrayList<String>();
        fuelTankDesc.add(GCCoreUtil.translate("gui.fuelTank.desc.2"));
        fuelTankDesc.add(GCCoreUtil.translate("gui.fuelTank.desc.3"));
        this.infoRegions.add(
                new GuiElementInfoRegion(
                        (this.width - this.xSize) / 2 + 7,
                        (this.height - this.ySize) / 2 + 33,
                        16,
                        38, fuelTankDesc, this.width, this.height, this
                )
        );*/
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int par1, int par2)
    {
        int offsetY = 35;

        this.buttonEnable.displayString = !this.tileEngine.getDisabled(0) ? GCCoreUtil.translate("gui.button.disable.name") : GCCoreUtil.translate("gui.button.enable.name");

        String displayString = this.tileEngine.getInventoryName();
        this.fontRendererObj.drawString(displayString, this.xSize / 2 - this.fontRendererObj.getStringWidth(displayString) / 2, 7, 4210752);

        displayString = GCCoreUtil.translate("gui.message.mothership.status.name")+": "+this.getStatus();
        this.fontRendererObj.drawString(displayString, 32, 9 + offsetY, 4210752);

        offsetY += 10;

        displayString = GCCoreUtil.translate("gui.message.mothership.numEngineParts")+": "+this.tileEngine.getNumBoosters();
        this.fontRendererObj.drawString(displayString, 32, 9 + offsetY, 4210752);

        offsetY += 10;


        displayString = GCCoreUtil.translate("gui.message.mothership.fuel")+": "+
        GuiHelper.formatMetric(this.tileEngine.fuelTank.getFluidAmount(), "B")
             + "/" + GuiHelper.formatMetric(tileEngine.fuelTank.getCapacity(),"B");
        this.fontRendererObj.drawString(displayString,
                32,
                9 + offsetY,
                4210752);



        // this.fontRendererObj.drawString(GCCoreUtil.translate("container.inventory"), 8, this.ySize - 94, 4210752);
    }

    private String getStatus()
    {
        if (this.tileEngine.isInUse())
        {
            return EnumColor.DARK_GREEN + GCCoreUtil.translate("gui.message.mothership.status.active");
        }

        if (this.tileEngine.getDisabled(0))
        {
            return EnumColor.DARK_RED + GCCoreUtil.translate("gui.status.disabled.name");
        }


        return EnumColor.ORANGE + GCCoreUtil.translate("gui.message.mothership.status.idle");
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float var1, int var2, int var3)
    {
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.mc.getTextureManager().bindTexture(guiTexture);
        final int xPos = (this.width - this.xSize) / 2;
        final int yPos = (this.height - this.ySize) / 2;
        this.drawTexturedModalRect(xPos, yPos, 0, 0, this.xSize, this.ySize);


        final int fuelLevel = this.tileEngine.getScaledFuelLevel(74);
        this.drawTexturedModalRect(
                (this.width - this.xSize) / 2 + 8,      // x
                (this.height - this.ySize) / 2 + 28 + 74 - fuelLevel,   //y
                176,    // u
                74-fuelLevel,//0 - fuelLevel, //v
                16, //w
                fuelLevel//h
        );

        // other stuff
        // jet
        int jetX = 32;
        int jetY = 28;
        this.drawTexturedModalRect(xPos + jetX, yPos + jetY, 192, 0, 22, 11);
        if(this.tileEngine.isInUse()) {
            // fire
            this.drawTexturedModalRect(xPos + jetX + 22, yPos + jetY, 214, 0, 12, 11);
        } else if(this.tileEngine.getDisabled(0)) {
            // red x
            this.drawTexturedModalRect(xPos + jetX+1, yPos + jetY-1, 192, 11, 13, 13);
        }

    }



}
