package de.katzenpapst.amunra.client.gui;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.opengl.GL11;

import de.katzenpapst.amunra.AmunRa;
import de.katzenpapst.amunra.inventory.ContainerHydroponics;
import de.katzenpapst.amunra.network.packet.PacketSimpleAR;
import de.katzenpapst.amunra.tile.TileEntityHydroponics;
import micdoodle8.mods.galacticraft.core.client.gui.container.GuiContainerGC;
import micdoodle8.mods.galacticraft.core.client.gui.element.GuiElementInfoRegion;
import micdoodle8.mods.galacticraft.core.energy.EnergyDisplayHelper;
import micdoodle8.mods.galacticraft.core.util.EnumColor;
import micdoodle8.mods.galacticraft.core.util.GCCoreUtil;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

public class GuiHydroponics extends GuiContainerGC {

    private static final ResourceLocation guiTexture = new ResourceLocation(AmunRa.ASSETPREFIX, "textures/gui/hydroponics.png");

    private GuiElementInfoRegion oxygenInfoRegion = new GuiElementInfoRegion((this.width - this.xSize) / 2 + 112, (this.height - this.ySize) / 2 + 24, 56, 9, new ArrayList<String>(), this.width, this.height, this);
    private GuiElementInfoRegion electricInfoRegion = new GuiElementInfoRegion((this.width - this.xSize) / 2 + 112, (this.height - this.ySize) / 2 + 37, 56, 9, new ArrayList<String>(), this.width, this.height, this);

    private TileEntityHydroponics tile;

    private GuiButton button;

    public GuiHydroponics(InventoryPlayer player, TileEntityHydroponics tile) {
        super(new ContainerHydroponics(player, tile));
        this.ySize = 201;
        this.xSize = 176;
        this.tile = tile;
    }

    @Override
    protected void actionPerformed(GuiButton par1GuiButton)
    {
        if(par1GuiButton.id == 0) {
            // do the stuff
            float growthStatus = tile.getPlantGrowthStatus();
            if(growthStatus < 0) {
                AmunRa.packetPipeline.sendToServer(new PacketSimpleAR(PacketSimpleAR.EnumSimplePacket.S_HYDROPONICS_OPERATION, tile.xCoord, tile.yCoord, tile.zCoord, TileEntityHydroponics.OperationType.PLANT_SEED.ordinal()));
            } else if(growthStatus < 1.0F) {
                AmunRa.packetPipeline.sendToServer(new PacketSimpleAR(PacketSimpleAR.EnumSimplePacket.S_HYDROPONICS_OPERATION, tile.xCoord, tile.yCoord, tile.zCoord, TileEntityHydroponics.OperationType.FERTILIZE.ordinal()));
            } else if(growthStatus == 1) {
                AmunRa.packetPipeline.sendToServer(new PacketSimpleAR(PacketSimpleAR.EnumSimplePacket.S_HYDROPONICS_OPERATION, tile.xCoord, tile.yCoord, tile.zCoord, TileEntityHydroponics.OperationType.HARVEST.ordinal()));
            }
        }

    }

    @Override
    public void initGui()
    {
        super.initGui();
        List<String> batterySlotDesc = new ArrayList<String>();
        batterySlotDesc.add(GCCoreUtil.translate("gui.batterySlot.desc.0"));
        batterySlotDesc.add(GCCoreUtil.translate("gui.batterySlot.desc.1"));
        this.infoRegions.add(new GuiElementInfoRegion((this.width - this.xSize) / 2 + 31, (this.height - this.ySize) / 2 + 26, 18, 18, batterySlotDesc, this.width, this.height, this));
        this.oxygenInfoRegion.xPosition = (this.width - this.xSize) / 2 + 112;
        this.oxygenInfoRegion.yPosition = (this.height - this.ySize) / 2 + 24;
        this.oxygenInfoRegion.parentWidth = this.width;
        this.oxygenInfoRegion.parentHeight = this.height;
        this.infoRegions.add(this.oxygenInfoRegion);
        this.electricInfoRegion.xPosition = (this.width - this.xSize) / 2 + 112;
        this.electricInfoRegion.yPosition = (this.height - this.ySize) / 2 + 37;
        this.electricInfoRegion.parentWidth = this.width;
        this.electricInfoRegion.parentHeight = this.height;
        this.infoRegions.add(this.electricInfoRegion);

        float growStatus = tile.getPlantGrowthStatus();

        button = new GuiButton(0,
                (this.width - this.xSize)/2 + 82,
                (this.height - this.ySize)/2+ 88,
                72, 20 , GCCoreUtil.translate("tile.hydroponics.plant"));

        updateTheButton(growStatus);

        this.buttonList.add(button);
    }

    private void updateTheButton(float growStatus) {
        // tile.hydroponics.fertilize
        if(growStatus < 0) {
            button.displayString = GCCoreUtil.translate("tile.hydroponics.plant");
            ItemStack stack = tile.getStackInSlot(1);
            button.enabled = (stack != null) && stack.stackSize > 0 && TileEntityHydroponics.seeds.isSameItem(stack);
        } else if(growStatus < 1.0F) {
            button.displayString = GCCoreUtil.translate("tile.hydroponics.fertilize");
            ItemStack stack = tile.getStackInSlot(1);
            button.enabled = (stack != null) && stack.stackSize > 0 && TileEntityHydroponics.bonemeal.isSameItem(stack);
        }
        else {
            button.displayString = GCCoreUtil.translate("tile.hydroponics.harvest");
            button.enabled = true;
        }
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int par1, int par2)
    {
        float growStatus = tile.getPlantGrowthStatus();
        this.fontRendererObj.drawString(this.tile.getInventoryName(), 8, 10, 4210752);
        GCCoreUtil.drawStringRightAligned(GCCoreUtil.translate("gui.message.out.name") + ":", 99, 25, 4210752, this.fontRendererObj);
        GCCoreUtil.drawStringRightAligned(GCCoreUtil.translate("gui.message.in.name") + ":", 99, 37, 4210752, this.fontRendererObj);

        String plantStatus = getPlantStatus(growStatus);
        if(growStatus < 0) {
            GCCoreUtil.drawStringCentered(GCCoreUtil.translate("gui.message.status.name") + ": " + plantStatus, this.xSize / 2, 50, 4210752, this.fontRendererObj);

        } else {
            GCCoreUtil.drawStringCentered(GCCoreUtil.translate("gui.message.status.name") + ": " + this.getStatus(), this.xSize / 2, 50, 4210752, this.fontRendererObj);
            GCCoreUtil.drawStringCentered(GCCoreUtil.translate("tile.hydroponics.plantstatus")+": "+plantStatus, this.xSize / 2, 60, 4210752, this.fontRendererObj);
            String status = GCCoreUtil.translate("gui.status.collecting.name") + ": " + (int) (0.5F + Math.min(this.tile.lastOxygenCollected * 20F, TileEntityHydroponics.OUTPUT_PER_TICK * 20F)) + GCCoreUtil.translate("gui.perSecond");
            GCCoreUtil.drawStringCentered(status, this.xSize / 2, 70, 4210752, this.fontRendererObj);
        }

        GCCoreUtil.drawStringCentered("IsMaster "+tile.isMaster(), this.xSize / 2, 60, 4210752, this.fontRendererObj);
        GCCoreUtil.drawStringCentered("NumBlocks "+tile.getNumBlocks(), this.xSize / 2, 70, 4210752, this.fontRendererObj);

        updateTheButton(growStatus);

        this.fontRendererObj.drawString(GCCoreUtil.translate("container.inventory"), 8, this.ySize - 90 + 2, 4210752);
    }

    private String getPlantStatus(float growStatus) {
        if(growStatus < 0) {
            return EnumColor.DARK_RED + GCCoreUtil.translate("tile.hydroponics.noplant");
        } else {
            if(growStatus < 1.0F) {
                return EnumColor.YELLOW.getCode() + Math.floor(tile.getPlantGrowthStatus()*100) + "%";
            }
        }
        return EnumColor.DARK_GREEN+"100%";
    }

    private String getStatus()
    {
        String returnValue = this.tile.getGUIstatus();
/*
        if (returnValue.equals(EnumColor.DARK_GREEN + GCCoreUtil.translate("gui.status.active.name")) && this.tile.lastOxygenCollected <= 0.0F)
        {
            return EnumColor.DARK_RED + GCCoreUtil.translate("gui.status.missingleaves.name");
        }*/

        return returnValue;
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float var1, int var2, int var3)
    {
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.mc.getTextureManager().bindTexture(guiTexture);
        final int var5 = (this.width - this.xSize) / 2;
        final int var6 = (this.height - this.ySize) / 2;
        this.drawTexturedModalRect(var5, var6 + 5, 0, 0, this.xSize, this.ySize);

        if (this.tile != null)
        {
            int scale = this.tile.getCappedScaledOxygenLevel(54);
            this.drawTexturedModalRect(var5 + 113, var6 + 25, 197, 7, Math.min(scale, 54), 7);
            scale = this.tile.getScaledElecticalLevel(54);
            this.drawTexturedModalRect(var5 + 113, var6 + 38, 197, 0, Math.min(scale, 54), 7);

            if (this.tile.getEnergyStoredGC() > 0)
            {
                this.drawTexturedModalRect(var5 + 99, var6 + 37, 176, 0, 11, 10);
            }

            if (this.tile.storedOxygen > 0)
            {
                this.drawTexturedModalRect(var5 + 100, var6 + 24, 187, 0, 10, 10);
            }

            List<String> oxygenDesc = new ArrayList<String>();
            oxygenDesc.add(GCCoreUtil.translate("gui.oxygenStorage.desc.0"));
            oxygenDesc.add(EnumColor.YELLOW + GCCoreUtil.translate("gui.oxygenStorage.desc.1") + ": " + ((int) Math.floor(this.tile.storedOxygen) + " / " + (int) Math.floor(this.tile.maxOxygen)));
            this.oxygenInfoRegion.tooltipStrings = oxygenDesc;

            List<String> electricityDesc = new ArrayList<String>();
            electricityDesc.add(GCCoreUtil.translate("gui.energyStorage.desc.0"));
            EnergyDisplayHelper.getEnergyDisplayTooltip(this.tile.getEnergyStoredGC(), this.tile.getMaxEnergyStoredGC(), electricityDesc);
//          electricityDesc.add(EnumColor.YELLOW + GCCoreUtil.translate("gui.energyStorage.desc.1") + ((int) Math.floor(this.collector.getEnergyStoredGC()) + " / " + (int) Math.floor(this.collector.getMaxEnergyStoredGC())));
            this.electricInfoRegion.tooltipStrings = electricityDesc;
        }
    }

}
