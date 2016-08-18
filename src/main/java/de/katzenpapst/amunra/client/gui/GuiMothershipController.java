package de.katzenpapst.amunra.client.gui;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.lwjgl.opengl.GL11;

import com.google.common.collect.Maps;

import cpw.mods.fml.client.FMLClientHandler;
import de.katzenpapst.amunra.AmunRa;
import de.katzenpapst.amunra.mothership.Mothership;
import de.katzenpapst.amunra.mothership.MothershipWorldProvider;
import de.katzenpapst.amunra.tile.TileEntityMothershipController;
import de.katzenpapst.amunra.vec.BlockVector;
import micdoodle8.mods.galacticraft.api.galaxies.CelestialBody;
import micdoodle8.mods.galacticraft.core.GalacticraftCore;
import micdoodle8.mods.galacticraft.core.client.gui.container.GuiAirLockController;
import micdoodle8.mods.galacticraft.core.client.gui.screen.GuiCelestialSelection;
import micdoodle8.mods.galacticraft.core.network.PacketSimple;
import micdoodle8.mods.galacticraft.core.network.PacketSimple.EnumSimplePacket;
import micdoodle8.mods.galacticraft.core.util.ColorUtil;
import micdoodle8.mods.galacticraft.core.util.EnumColor;
import micdoodle8.mods.galacticraft.core.util.GCCoreUtil;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

public class GuiMothershipController extends GuiScreen {

    private static final ResourceLocation mothershipControllerGui = new ResourceLocation(AmunRa.ASSETPREFIX, "textures/gui/mothershipController.png");

    protected GuiButton selectTargetButton;
    protected GuiButton launchButton;

    protected final int xSize;
    protected final int ySize;


    protected int screenStartX;
    protected int screenStartY;

    protected World mWorld;

    protected CelestialBody selectedBody;

    protected BlockVector originBlock;

    public GuiMothershipController(TileEntityMothershipController title, World world, int x, int y, int z) {
        // TODO Auto-generated constructor stub
        this.xSize = 230;
        this.ySize = 139;
        this.mWorld = world;
        this.originBlock = new BlockVector(world, x, y, z);
    }

    @Override
    public void initGui() {

        screenStartX = (this.width - this.xSize) / 2;
        screenStartY = (this.height - this.ySize) / 2;

        this.buttonList.add(this.selectTargetButton = new GuiButton(
            0,                      // ID
            screenStartX+18,    // posX
            screenStartY+100,   // posY
            82,                     // width
            20,                     // height
            GCCoreUtil.translate("gui.message.mothership.selectbutton"))
        );

        this.buttonList.add(this.launchButton = new GuiButton(
                1,                      // ID
                screenStartX+18+100,    // posX
                screenStartY+100,   // posY
                82,                     // width
                20,                     // height
                GCCoreUtil.translate("gui.message.mothership.launchbutton"))
            );

        super.initGui();
    }

    protected int getScaledTravelTime(Mothership ship, int barLength) {
        float remain = ship.getRemainingTravelTime();
        float total = ship.getTotalTravelTime();
        float relative = remain/total;
        float scaled = (1-relative)*barLength;
        return (int)(scaled);
    }

    /**
     * Returns true if this GUI should pause the game when it is displayed in single-player
     */
    @Override
    public boolean doesGuiPauseGame()
    {
        return false;
    }

    /**
     * mouseX, mouseY, partialTicks
     */
    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks)
    {
        //final int screenStartX = (this.width - this.xSize) / 2;
        //final int screenStartY = (this.height - this.ySize) / 2;

        this.mc.renderEngine.bindTexture(mothershipControllerGui);
        this.drawTexturedModalRect(screenStartX, screenStartY, 0, 0, this.xSize, this.ySize);

        if(mWorld.provider instanceof MothershipWorldProvider) {
            MothershipWorldProvider provider = (MothershipWorldProvider)mWorld.provider;
            Mothership body = (Mothership) provider.getCelestialBody();
            String owner = body.getOwnerName();

            // bars
            if(body.isInTransit()) {
                // dark bar
                this.drawTexturedModalRect(screenStartX+42, screenStartY+34, 0, 139, 146, 4);
                // progress
                // red bar
                this.drawTexturedModalRect(screenStartX+42, screenStartY+34, 0, 143, getScaledTravelTime(body, 146), 4);

            }


            // texts
            this.fontRendererObj.drawString(body.getName(), this.width / 2 - this.fontRendererObj.getStringWidth(body.getLocalizedName()) / 2, this.height / 2 - 65, 4210752);
            String statusString;
            String selectedBodyName;
            if(body.isInTransit()) {
                statusString = GCCoreUtil.translateWithFormat("gui.message.mothership.transit", body.getSource().getLocalizedName(), body.getDestination().getLocalizedName());
                selectedBodyName = GCCoreUtil.translateWithFormat("gui.message.mothership.selected", body.getDestination().getLocalizedName());
            } else {
                statusString = GCCoreUtil.translateWithFormat("gui.message.mothership.stationary", body.getDestination().getLocalizedName());
                if(selectedBody == null) {
                    selectedBodyName = GCCoreUtil.translateWithFormat("gui.message.mothership.selected.none");
                } else {
                    selectedBodyName = GCCoreUtil.translateWithFormat("gui.message.mothership.selected", selectedBody.getLocalizedName());
                }

                this.fontRendererObj.drawString(statusString, screenStartX+18, screenStartY+60, 4210752);
            }
            String selectedBodyString = GCCoreUtil.translateWithFormat("gui.message.mothership.selected", selectedBodyName);
            this.fontRendererObj.drawString(statusString, screenStartX+18, screenStartY+60, 4210752);
            this.fontRendererObj.drawString(selectedBodyString, screenStartX+18, screenStartY+60+16, 4210752);


            // now bodies
            if(body.isInTransit()) {
                // render the other and my body
                // source
                this.mc.renderEngine.bindTexture(body.getSource().getBodyIcon());
                drawFullSizedTexturedRect(screenStartX+30, screenStartY+30, 12, 12);

                // dest
                this.mc.renderEngine.bindTexture(body.getDestination().getBodyIcon());
                drawFullSizedTexturedRect(screenStartX+188, screenStartY+30, 12, 12);
            } else {
                // only parent
                this.mc.renderEngine.bindTexture(body.getParent().getBodyIcon());
                drawFullSizedTexturedRect(screenStartX+109, screenStartY+30, 12, 12);
            }



            // source
            // this.drawTexturedModalRect(screenStartX+30, screenStartY+30, 0, 0, 12, 12);
            // dest
            // this.drawTexturedModalRect(screenStartX+188, screenStartY+30, 0, 0, 12, 12);
            // center
            // this.drawTexturedModalRect(screenStartX+109, screenStartY+30, 0, 0, 12, 12);




        }
        /*

        // this.drawTexturedModalRect(var5 + 15, var6 + 51, 176, 0, 7, 9);

        String displayString = "la test";//GCCoreUtil.translateWithFormat("gui.title.airLock.name", this.controller.ownerName);

        //this.fontRendererObj.drawString("la test", 8, 6, 4210752);
        this.fontRendererObj.drawString(displayString, this.width / 2 - this.fontRendererObj.getStringWidth(displayString) / 2, this.height / 2 - 65, 4210752);

*/
        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    /**
     * Draws a textured rectangle at the stored z-value with the full texture size. Args: x, y,width, height
     */
    public void drawFullSizedTexturedRect(int x, int y, int width, int height)
    {
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        Tessellator tessellator = Tessellator.instance;
        tessellator.startDrawingQuads();
        /*float f = 0.00390625F;
        float f1 = 0.00390625F;
        tessellator.addVertexWithUV(
                (double)(x + 0),
                (double)(y + height),
                (double)this.zLevel,
                (double)((float)(u + 0) * f),
                (double)((float)(v + height) * f1)
        );

        tessellator.addVertexWithUV((double)(x + width), (double)(y + height), (double)this.zLevel, (double)((float)(u + width) * f), (double)((float)(v + height) * f1));
        tessellator.addVertexWithUV((double)(x + width), (double)(y + 0), (double)this.zLevel, (double)((float)(u + width) * f), (double)((float)(v + 0) * f1));
        tessellator.addVertexWithUV((double)(x + 0), (double)(y + 0), (double)this.zLevel, (double)((float)(u + 0) * f), (double)((float)(v + 0) * f1));
        */
        tessellator.addVertexWithUV(x, y+height, this.zLevel, 0, 0);
        tessellator.addVertexWithUV(x+width, y+height, this.zLevel, 1, 0);
        tessellator.addVertexWithUV(x+width, y, this.zLevel, 1, 1);
        tessellator.addVertexWithUV(x, y, this.zLevel, 0, 1);
        tessellator.draw();
    }


    @Override
    protected void actionPerformed(GuiButton par1GuiButton)
    {
        switch (par1GuiButton.id)
        {
        case 0:
            // show the celestial screen
            Map<Integer, Map<String, GuiCelestialSelection.StationDataGUI>> spaceStationData = Maps.newHashMap();
            List<CelestialBody> possibleCelestialBodies = new ArrayList<CelestialBody>();

            //GuiMothershipSelection gui = new GuiMothershipSelection(possibleCelestialBodies, this.originBlock);
            //gui.spaceStationMap = spaceStationData;
            //gui.setTriggerBlock(mWorld, x, y, z);
            //FMLClientHandler.instance().getClient().displayGuiScreen(gui);
            break;
        }
    }

}
