package de.katzenpapst.amunra.client.gui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.client.FMLClientHandler;
import de.katzenpapst.amunra.AmunRa;
import de.katzenpapst.amunra.GuiIds;
import de.katzenpapst.amunra.RecipeHelper;
import de.katzenpapst.amunra.mothership.Mothership;
import de.katzenpapst.amunra.mothership.MothershipWorldProvider;
import de.katzenpapst.amunra.mothership.MothershipWorldProvider.TransitData;
import de.katzenpapst.amunra.network.packet.PacketSimpleAR;
import de.katzenpapst.amunra.tile.TileEntityMothershipController;
import de.katzenpapst.amunra.vec.BlockVector;
import micdoodle8.mods.galacticraft.api.galaxies.CelestialBody;
import micdoodle8.mods.galacticraft.api.galaxies.GalaxyRegistry;
import micdoodle8.mods.galacticraft.api.galaxies.Planet;
import micdoodle8.mods.galacticraft.api.galaxies.Satellite;
import micdoodle8.mods.galacticraft.api.recipe.SpaceStationRecipe;
import micdoodle8.mods.galacticraft.api.vector.BlockVec3;
import micdoodle8.mods.galacticraft.api.vector.BlockVec3Dim;
import micdoodle8.mods.galacticraft.api.vector.Vector3;
import micdoodle8.mods.galacticraft.core.GalacticraftCore;
import micdoodle8.mods.galacticraft.core.client.gui.screen.GuiCelestialSelection;
// import micdoodle8.mods.galacticraft.core.client.gui.screen.GuiCelestialSelection.EnumSelectionState;
import micdoodle8.mods.galacticraft.core.client.gui.screen.GuiCelestialSelection.StationDataGUI;
import micdoodle8.mods.galacticraft.core.util.ColorUtil;
import micdoodle8.mods.galacticraft.core.util.GCCoreUtil;
import micdoodle8.mods.galacticraft.core.util.WorldUtil;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

public class GuiMothershipSelection extends GuiARCelestialSelection {

    // block from which the gui was originally opened
    protected BlockVector triggerBlock = null;
    protected Mothership curMothership;
    protected World world;
    protected MothershipWorldProvider provider;

    // x coordinate of the rightmost screen edge before the borders
    protected int offsetX;
    // y coordinate of the topmost screen edge after the borders
    protected int offsetY;

    // launch button
    protected final int LAUNCHBUTTON_X = -95;
    protected final int LAUNCHBUTTON_Y = 133;
    protected final int LAUNCHBUTTON_W = 93;
    protected final int LAUNCHBUTTON_H = 12;

    // rename button
    protected final int RENAMEBUTTON_X = -95;
    protected final int RENAMEBUTTON_Y = 53;
    protected final int RENAMEBUTTON_W = 93;
    protected final int RENAMEBUTTON_H = 12;

    // transit info box
    protected final int TRANSIT_INFO_U = 0;
    protected final int TRANSIT_INFO_V = 16;
    protected final int TRANSIT_INFO_W = 179;
    protected final int TRANSIT_INFO_H = 20;
    /*/*int TRANSIT_INFO_U = 0;
        int TRANSIT_INFO_V = 16;
        int TRANSIT_INFO_W = 179;
        int TRANSIT_INFO_H = 20;*/

    protected float ticksSinceLaunch = -1;

    protected boolean hasMothershipStats = false;

    public static ResourceLocation guiExtra = new ResourceLocation(AmunRa.ASSETPREFIX, "textures/gui/celestialselection_extra.png");

    protected CelestialBody travelCacheForStart;
    // protected Map<CelestialBody, Double> travelTimeCache;
    protected Map<CelestialBody, MothershipWorldProvider.TransitData> transitDataCache;

    public GuiMothershipSelection(List<CelestialBody> possibleBodies, TileEntityMothershipController title, World world) {
        // possibleBodies should be largely irrelevant here
        // hack
        super(true, possibleBodies);
        //triggerBlock = blockToReturn;

        this.world = world;
        this.provider =(MothershipWorldProvider) world.provider;
        this.curMothership = (Mothership) (provider).getCelestialBody();
        // this.travelTimeCache = new HashMap<CelestialBody, Double>();
        this.transitDataCache = new HashMap<CelestialBody, MothershipWorldProvider.TransitData>();
    }
    @Override
    public void drawButtons(int mousePosX, int mousePosY)
    {
        this.possibleBodies = this.shuttlePossibleBodies;
        super.drawButtons(mousePosX, mousePosY);

        // meh
        drawMothershipGuiParts(mousePosX, mousePosY);
    }

    public void mothershipUpdateRecieved() {
        // TODO
        System.out.println("Mothership GUI got the update");
        hasMothershipStats = true;
    }

    @Override
    public void initGui() {
        super.initGui();

        selectAndZoom(curMothership.getDestination());

        AmunRa.packetPipeline.sendToServer(new PacketSimpleAR(PacketSimpleAR.EnumSimplePacket.S_MOTHERSHIP_UPDATE, world.provider.dimensionId));
    }

    @Override
    public void drawScreen(int mousePosX, int mousePosY, float partialTicks)
    {
        super.drawScreen(mousePosX, mousePosY, partialTicks);
        if(ticksSinceLaunch >= 0) {
            ticksSinceLaunch += partialTicks;
            if(ticksSinceLaunch > 1.0F) {
                ticksSinceLaunch = -1;
            }
        }
    }

    protected void drawBodyOnGUI(CelestialBody body, int x, int y, int w, int h) {
        if(body == null) {
            return;
        }
        this.mc.renderEngine.bindTexture(body.getBodyIcon());

        this.drawFullSizedTexturedRect(x, y, w, h);
    }

    public void drawFullSizedTexturedRect(int x, int y, int width, int height)
    {
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        Tessellator tessellator = Tessellator.instance;
        tessellator.startDrawingQuads();
        tessellator.addVertexWithUV(x, y+height, this.zLevel, 0, 0);
        tessellator.addVertexWithUV(x+width, y+height, this.zLevel, 1, 0);
        tessellator.addVertexWithUV(x+width, y, this.zLevel, 1, 1);
        tessellator.addVertexWithUV(x, y, this.zLevel, 0, 1);
        tessellator.draw();
    }

    protected void drawTransitBar(int length)
    {
        // max length = 124
        this.drawTexturedModalRect(
                width/2-90+27,
                height-11-GuiCelestialSelection.BORDER_WIDTH+2,
                1, //displayed w
                4,  //displayed h
                0,    // u aka x in tex
                0,      // v
                1,     // w in texture
                4,      // h in texture
                false, false);
        // bar
        this.drawTexturedModalRect(
                width/2-90+28,
                height-11-GuiCelestialSelection.BORDER_WIDTH+2,
                length, //displayed w
                4,  //displayed h
                1,    // u aka x in tex
                0,      // v
                43,     // w in texture
                4,      // h in texture
                false, false);
        // right cap
        this.drawTexturedModalRect(
                width/2-90+28+length,
                height-11-GuiCelestialSelection.BORDER_WIDTH+2,
                1, //displayed w
                4,  //displayed h
                56,    // u aka x in tex
                0,      // v
                1,     // w in texture
                4,      // h in texture
                false, false);
    }

    protected void drawTransitInfo(int mousePosX, int mousePosY)
    {
        GL11.glColor4f(0.0F, 0.6F, 1.0F, 1);
        this.mc.renderEngine.bindTexture(guiExtra);


        this.drawTexturedModalRect(
                width/2-90,
                height-11-GuiCelestialSelection.BORDER_WIDTH-4,
                TRANSIT_INFO_W, //w
                TRANSIT_INFO_H,  //h
                TRANSIT_INFO_U,    // u
                TRANSIT_INFO_V,      // v
                TRANSIT_INFO_W,
                TRANSIT_INFO_H,
                false, false);

     // bar
        //this.mc.renderEngine.bindTexture(GuiCelestialSelection.guiMain0);

        GL11.glColor4f(0.35F, 0.01F, 0.01F, 1);
        //GL11.glEnable(GL11.GL_BLEND);

        drawTransitBar(124);

        GL11.glColor4f(0.95F, 0.01F, 0.01F, 1);
        drawTransitBar(this.curMothership.getScaledTravelTime(124));

        /*this.drawTexturedModalRect(
                width/2-90+28,
                height-11-GuiCelestialSelection.BORDER_WIDTH+2,
                length, //displayed w
                4,  //displayed h
                1,    // u aka x in tex
                0,      // v
                43,     // w in texture
                4,      // h in texture
                false, false);*/
        if(isMouseWithin(mousePosX, mousePosY, width/2-90+28, height-11-GuiCelestialSelection.BORDER_WIDTH+2, 124, 4)) {
            //GCCoreUtil.translate("gui.message.mothership.travelTimeRemain")
            this.showTooltip(GCCoreUtil.translateWithFormat("gui.message.mothership.travelTimeRemain", curMothership.getRemainingTravelTime()), mousePosX, mousePosY);
        }


        // test place planet symbols
        // source
        int bodyX = width/2-90+14;
        int bodyY = height-11-GuiCelestialSelection.BORDER_WIDTH;
        this.drawBodyOnGUI(curMothership.getSource(), bodyX, bodyY, 8, 8);
        if(isMouseWithin(mousePosX, mousePosY, bodyX, bodyY, 8, 8)) {
            this.showTooltip(curMothership.getSource().getLocalizedName(), mousePosX, mousePosY);
        }
        // dest
        bodyX = width/2+90-14-8;
        bodyY = height-11-GuiCelestialSelection.BORDER_WIDTH;
        this.drawBodyOnGUI(curMothership.getDestination(), bodyX, bodyY, 8, 8);
        if(isMouseWithin(mousePosX, mousePosY, bodyX, bodyY, 8, 8)) {
            this.showTooltip(curMothership.getDestination().getLocalizedName(), mousePosX, mousePosY);
        }
    }

    protected int getScaledTravelTime(Mothership ship, int barLength) {
        float remain = ship.getRemainingTravelTime();
        float total = ship.getTotalTravelTime();
        float relative = remain/total;
        float scaled = (1-relative)*barLength;
        return (int)(scaled);
    }



    protected void drawMothershipGuiParts(int mousePosX, int mousePosY)
    {
        int offset=0;
        String str;

        GL11.glColor4f(0.0F, 0.6F, 1.0F, 1);
        //this.mc.renderEngine.bindTexture(GuiCelestialSelection.guiMain1);
        //this.mc.renderEngine.bindTexture(guiExtra);
        /*int canCreateLength = Math.max(0, this.drawSplitString(GCCoreUtil.translate("gui.message.canCreateMothership.name"), 0, 0, 91, 0, true, true) - 2);
        int canCreateOffset = canCreateLength * this.smallFontRenderer.FONT_HEIGHT;*/

        offsetX = width - GuiCelestialSelection.BORDER_WIDTH - GuiCelestialSelection.BORDER_EDGE_WIDTH;
        offsetY = GuiCelestialSelection.BORDER_WIDTH + GuiCelestialSelection.BORDER_EDGE_WIDTH;

        if(this.curMothership.isInTransit()) {
            drawTransitInfo(mousePosX, mousePosY);
        }
        if(this.hasMothershipStats) {
            drawMothershipInfo();
        }
        if(this.selectedBody != null) {
            drawTargetBodyInfo();
        }
    }

    protected void drawMothershipInfo() {

        this.mc.renderEngine.bindTexture(guiExtra);

        int boxWidth = 95;
        int boxHeight = 65;

        int bottomOffset = height - offsetY;
        int totalOffset = 1;//offsetY+141;

        float totalMass = provider.getTotalMass();

        TransitData tData = provider.getTheoreticalTransitData();

        // draw the main texture
        totalOffset = boxHeight+1;
        this.drawTexturedModalRect(
                offsetX-boxWidth,
                bottomOffset-totalOffset,
                boxWidth, //displayed w
                boxHeight,  //displayed h
                0,    // u aka x in tex
                70,      // v
                boxWidth,     // w in texture
                boxHeight,      // h in texture
                false, false);


        totalOffset = boxHeight-12;

        // draw the ship's name
        this.drawSplitString(
                this.curMothership.getLocalizedName(),
                offsetX-boxWidth/2, // x?
                bottomOffset-totalOffset,        // y
                91,                 // width?
                ColorUtil.to32BitColor(255, 255, 255, 255), false, false);

        /*protected final int RENAMEBUTTON_X = -95;
    protected final int RENAMEBUTTON_Y = 53;
    protected final int RENAMEBUTTON_W = 93;
    protected final int RENAMEBUTTON_H = 12;*/

        totalOffset -= 10;
        if(hasMothershipStats) {
            this.smallFontRenderer.drawString(GCCoreUtil.translate("gui.message.mothership.totalMass")+": "+GuiHelper.formatKilogram(totalMass),
                    offsetX - 90,
                    bottomOffset-totalOffset,
                    ColorUtil.to32BitColor(255, 255, 255, 255),
                    false);
        }

        totalOffset -= 10;
        if(hasMothershipStats) {
            this.smallFontRenderer.drawString(GCCoreUtil.translate("gui.message.mothership.totalBlocks")+": "+GuiHelper.formatMetric(provider.getNumBlocks()),
                    offsetX - 90,
                    bottomOffset-totalOffset,
                    ColorUtil.to32BitColor(255, 255, 255, 255),
                    false);
        }

        totalOffset -= 10;
        if(hasMothershipStats) {
            this.smallFontRenderer.drawString(GCCoreUtil.translate("gui.message.mothership.travelSpeed")+": "+GuiHelper.formatSpeed(tData.speed),
                    offsetX - 90,
                    bottomOffset-totalOffset,
                    ColorUtil.to32BitColor(255, 255, 255, 255),
                    false);
        }

        totalOffset -= 10;
        int thrustColor = ColorUtil.to32BitColor(255, 255, 255, 255);
        if(tData.thrust < totalMass) {
            thrustColor = ColorUtil.to32BitColor(255, 255, 126, 126);
        }
        if(hasMothershipStats) {
            this.smallFontRenderer.drawString(GCCoreUtil.translate("gui.message.mothership.travelThrust")+": "+GuiHelper.formatKilogram(tData.thrust),
                    offsetX - 90,
                    bottomOffset-totalOffset,
                    thrustColor,
                    false);
        }
    }

    protected void drawTargetBodyInfo() {
        int offset = 0;

        GL11.glColor4f(0.0F, 0.6F, 1.0F, 1);
        this.mc.renderEngine.bindTexture(guiExtra);

        MothershipWorldProvider.TransitData tData = null;
        boolean canBeOrbited = Mothership.canBeOrbited(selectedBody);

        boolean canReach = true;

        if(canBeOrbited) {
            tData = getTransitDataFor(selectedBody);

            if(tData.isEmpty() || !canTravelTo(selectedBody, tData)) {
                canReach = false;
            }
        }

        if(canBeOrbited && canReach) {
            // green
            GL11.glColor4f(0.0F, 1.0F, 0.1F, 1);
        } else {
            // red
            GL11.glColor4f(1.0F, 0.0F, 0.0F, 1);
        }

        this.drawTexturedModalRect(
                offsetX + LAUNCHBUTTON_X,
                offsetY + LAUNCHBUTTON_Y,
                LAUNCHBUTTON_W,
                LAUNCHBUTTON_H,
                0,  // u
                4,// v
                LAUNCHBUTTON_W, LAUNCHBUTTON_H, false, false);

        this.drawSplitString(
                GCCoreUtil.translate("gui.message.mothership.launchbutton").toUpperCase(),
                offsetX - 48,
                offsetY + 135, 91, ColorUtil.to32BitColor(255, 255, 255, 255), false, false);

        // name of selected body
        offset = 17;
        this.drawSplitString(
                selectedBody.getLocalizedName(),
                offsetX - 48,
                offset + offsetY, 91, ColorUtil.to32BitColor(255, 255, 255, 255), false, false);

        offset += 12;

        if(canBeOrbited) {
            double travelDistance = curMothership.getTravelDistanceTo(selectedBody);
            int travelTime = curMothership.getTravelTimeTo(travelDistance, tData.speed);


            this.smallFontRenderer.drawString(GCCoreUtil.translate("gui.message.mothership.travelTime")+": "+
                    (
                            canBeOrbited ? GuiHelper.formatTime(travelTime) : GCCoreUtil.translate("gui.message.misc.n_a")
                    ),
                    offsetX - 90,
                    offsetY + offset,
                    ColorUtil.to32BitColor(255, 255, 255, 255),
                    false);
            offset += 10;

            this.smallFontRenderer.drawString(GCCoreUtil.translate("gui.message.mothership.travelDistance")+": "+
                    (
                            canBeOrbited ? GuiHelper.formatMetric(travelDistance, "AU") : GCCoreUtil.translate("gui.message.misc.n_a")
                    ),
                    offsetX - 90,
                    offsetY + offset,
                    ColorUtil.to32BitColor(255, 255, 255, 255),
                    false);
            offset += 10;

        } else  {
            this.smallFontRenderer.drawSplitString(GCCoreUtil.translate("gui.message.mothership.unreachableBody"),
                    offsetX - 90,
                    offsetY + offset, 90, ColorUtil.to32BitColor(255, 255, 128, 128));

        }
        if(!canReach) {
            // par1Str = str, par5 = color
            // public void drawSplitString(String par1Str, int par2, int par3, int par4, int par5)
            if(this.curMothership.getParent() == selectedBody) {
                this.smallFontRenderer.drawSplitString(GCCoreUtil.translate("gui.message.mothership.alreadyOrbiting"),
                        offsetX - 90,
                        offsetY + offset, 90, ColorUtil.to32BitColor(255, 255, 255, 255));
            } else {

                this.smallFontRenderer.drawSplitString(GCCoreUtil.translate("gui.message.mothership.notEnoughEngines"),
                        offsetX - 90,
                        offsetY + offset, 90, ColorUtil.to32BitColor(255, 255, 128, 128));
            }
        }
    }

    protected TransitData getTransitDataFor(CelestialBody body) {
        TransitData result;
        if(!transitDataCache.containsKey(selectedBody)) {
            result = provider.getTransitDataTo(selectedBody);
            transitDataCache.put(selectedBody, result);
        } else {
            result = transitDataCache.get(selectedBody);
        }
        return result;
    }

    protected boolean canTravelTo(CelestialBody body, TransitData tData)
    {
        // simple stuff
        if (
                body == null ||
                body == curMothership.getParent() ||
                !Mothership.canBeOrbited(body)
                ) {
            return false;
        }
        // more complicated stuff
        if(tData == null) {
            tData = getTransitDataFor(this.selectedBody);
        }
        if(tData.isEmpty()) {
            return false;
        }
        // most complicated stuff
        double distance = curMothership.getTravelDistanceTo(body);
        int travelTime = curMothership.getTravelTimeTo(distance, tData.speed);

        if(travelTime > 24000) {
            return false;
        }
        return true;
    }

    @Override
    protected void mouseClicked(int x, int y, int button)
    {

        // simple checks first
        int actualBtnX = offsetX + LAUNCHBUTTON_X;
        int actualBtnY = offsetY + LAUNCHBUTTON_Y;
        if(actualBtnX <= x && x <= actualBtnX+LAUNCHBUTTON_W  && actualBtnY <= y && y <= actualBtnY+LAUNCHBUTTON_H) {
            if(canTravelTo(this.selectedBody, null)) {

                // spam protection?
                if(ticksSinceLaunch > -1) {
                    return;
                }
                ticksSinceLaunch = 0;
                // send packet?
                AmunRa.packetPipeline.sendToServer(new PacketSimpleAR(
                        PacketSimpleAR.EnumSimplePacket.S_MOTHERSHIP_TRANSIT_START,
                        this.curMothership.getID(),
                        Mothership.getOrbitableBodyName(this.selectedBody)
                        )
                        );

            }
            return; // return anyway
        }

        if(this.hasMothershipStats) {
            actualBtnX = offsetX + RENAMEBUTTON_X;
            actualBtnY = height - offsetY - RENAMEBUTTON_Y;
            if(actualBtnX <= x && x <= actualBtnX+RENAMEBUTTON_W  && actualBtnY <= y && y <= actualBtnY+RENAMEBUTTON_H) {
                System.out.println("yes clicked");
                return;
            }
        }

        super.mouseClicked(x, y, button);
    }

}
