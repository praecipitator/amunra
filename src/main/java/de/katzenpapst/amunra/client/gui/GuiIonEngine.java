package de.katzenpapst.amunra.client.gui;

import java.util.ArrayList;
import java.util.List;

import de.katzenpapst.amunra.AmunRa;
import de.katzenpapst.amunra.inventory.ContainerIonEngine;
import de.katzenpapst.amunra.tile.TileEntityMothershipEngineAbstract;
import micdoodle8.mods.galacticraft.core.client.gui.element.GuiElementInfoRegion;
import micdoodle8.mods.galacticraft.core.energy.EnergyDisplayHelper;
import micdoodle8.mods.galacticraft.core.util.GCCoreUtil;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.ResourceLocation;

public class GuiIonEngine extends GuiRocketEngine {

    protected GuiElementInfoRegion electricInfoRegion;

    public GuiIonEngine(InventoryPlayer player, TileEntityMothershipEngineAbstract tileEngine) {
        super(new ContainerIonEngine(player, tileEngine), tileEngine, new ResourceLocation(AmunRa.ASSETPREFIX, "textures/gui/ms_ion.png"));
    }

    @Override
    public void initGui() {
        super.initGui();
        electricInfoRegion = new GuiElementInfoRegion(
                (this.width - this.xSize) / 2 + 113,
                (this.height - this.ySize) / 2 + 29,
                56, 9, new ArrayList<String>(), this.width, this.height, this);

        this.infoRegions.add(this.electricInfoRegion);
    };

    @Override
    protected void drawGuiContainerBackgroundLayer(float var1, int var2, int var3) {
        super.drawGuiContainerBackgroundLayer(var1, var2, var3);

        int containerWidth = (this.width - this.xSize) / 2;
        int containerHeight = (this.height - this.ySize) / 2;
        //this.drawTexturedModalRect(containerWidth, containerHeight, 0, 0, this.xSize, this.ySize);
        int scale;

        List<String> electricityDesc = new ArrayList<String>();
        electricityDesc.add(GCCoreUtil.translate("gui.energyStorage.desc.0"));
        EnergyDisplayHelper.getEnergyDisplayTooltip(this.tileEngine.getEnergyStoredGC(), this.tileEngine.getMaxEnergyStoredGC(), electricityDesc);
        this.electricInfoRegion.tooltipStrings = electricityDesc;

        if (this.tileEngine.getEnergyStoredGC() > 0)
        {
            scale = this.tileEngine.getScaledElecticalLevel(54);
            this.drawTexturedModalRect(containerWidth + 114, containerHeight + 30, 176, 74, scale, 7);
            this.drawTexturedModalRect(containerWidth + 101, containerHeight + 29, 192, 64, 11, 10);
        }
    };

    @Override
    protected void drawGuiContainerForegroundLayer(int par1, int par2) {
        super.drawGuiContainerForegroundLayer(par1, par2);


    }

}
