package de.katzenpapst.amunra.client.gui;

import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.lwjgl.BufferUtils;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.ibm.icu.text.ArabicShaping;
import com.ibm.icu.text.ArabicShapingException;
import com.ibm.icu.text.Bidi;

import cpw.mods.fml.client.FMLClientHandler;
import de.katzenpapst.amunra.AmunRa;
import de.katzenpapst.amunra.network.packet.PacketSimpleAR;
import micdoodle8.mods.galacticraft.api.GalacticraftRegistry;
import micdoodle8.mods.galacticraft.api.event.client.CelestialBodyRenderEvent;
import micdoodle8.mods.galacticraft.api.galaxies.CelestialBody;
import micdoodle8.mods.galacticraft.api.galaxies.GalaxyRegistry;
import micdoodle8.mods.galacticraft.api.galaxies.IChildBody;
import micdoodle8.mods.galacticraft.api.galaxies.Moon;
import micdoodle8.mods.galacticraft.api.galaxies.Planet;
import micdoodle8.mods.galacticraft.api.galaxies.Satellite;
import micdoodle8.mods.galacticraft.api.galaxies.SolarSystem;
import micdoodle8.mods.galacticraft.api.galaxies.Star;
import micdoodle8.mods.galacticraft.api.recipe.SpaceStationRecipe;
import micdoodle8.mods.galacticraft.api.world.SpaceStationType;
import micdoodle8.mods.galacticraft.core.GalacticraftCore;
import micdoodle8.mods.galacticraft.core.client.gui.screen.GuiCelestialSelection;
import micdoodle8.mods.galacticraft.core.client.gui.screen.SmallFontRenderer;
// import micdoodle8.mods.galacticraft.core.client.gui.screen.GuiShuttleSelection.EnumSelectionState;

import micdoodle8.mods.galacticraft.core.network.PacketSimple;
import micdoodle8.mods.galacticraft.core.network.PacketSimple.EnumSimplePacket;
import micdoodle8.mods.galacticraft.core.proxy.ClientProxyCore;
import micdoodle8.mods.galacticraft.core.tick.KeyHandlerClient;
import micdoodle8.mods.galacticraft.core.util.ColorUtil;
import micdoodle8.mods.galacticraft.core.util.ConfigManagerCore;
import micdoodle8.mods.galacticraft.core.util.GCCoreUtil;
import micdoodle8.mods.galacticraft.core.util.GCLog;
import micdoodle8.mods.galacticraft.core.util.WorldUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ChatAllowedCharacters;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.WorldProvider;
import net.minecraftforge.common.MinecraftForge;

/***
 * FFUUU... I guess I will have to copy most of the original code...
 * I will remove most of the parts dealing with space stations, though. Maybe some day I will be able to actually override GuiCelestialSelection
 *
 */
public class GuiShuttleSelection extends GuiCelestialSelection {



    public GuiShuttleSelection(boolean mapMode, List<CelestialBody> possibleBodies)
    {
        super(mapMode, possibleBodies);
    }

    @Override
    public void initGui()
    {
        // OMG, I can actually override this one. WOOHOO!
        super.initGui();
        /*
        for (Planet planet : GalaxyRegistry.getRegisteredPlanets().values())
        {
            this.celestialBodyTicks.put(planet, 0);
        }

        for (Moon moon : GalaxyRegistry.getRegisteredMoons().values())
        {
            this.celestialBodyTicks.put(moon, 0);
        }

        for (Satellite satellite : GalaxyRegistry.getRegisteredSatellites().values())
        {
            this.celestialBodyTicks.put(satellite, 0);
        }*/

        //GuiShuttleSelection.BORDER_WIDTH = this.width / 65;
        //GuiShuttleSelection.BORDER_EDGE_WIDTH = GuiShuttleSelection.BORDER_WIDTH / 4;
    }



    @Override
    protected void keyTyped(char keyChar, int keyID)
    {
        super.keyTyped(keyChar, keyID);

    }




    @Override
    protected boolean teleportToSelectedBody()
    {
        // now this is important to override
        if (this.selectedBody != null)
        {
            if (this.selectedBody.getReachable() && this.possibleBodies != null && this.possibleBodies.contains(this.selectedBody))
            {
                try
                {
                    String dimension;

                    if (this.selectedBody instanceof Satellite)
                    {
                        if (this.spaceStationMap == null)
                        {
                            GCLog.severe("Please report as a BUG: spaceStationIDs was null.");
                            return false;
                        }
                        Satellite selectedSatellite = (Satellite) this.selectedBody;
                        Integer mapping = this.spaceStationMap.get(getSatelliteParentID(selectedSatellite)).get(this.selectedStationOwner).getStationDimensionID();
                        //No need to check lowercase as selectedStationOwner is taken from keys.
                        if (mapping == null)
                        {
                            GCLog.severe("Problem matching player name in space station check: " + this.selectedStationOwner);
                            return false;
                        }
                        int spacestationID = mapping;
                        WorldProvider spacestation = WorldUtil.getProviderForDimensionClient(spacestationID);
                        if (spacestation != null)
                        {
                            dimension = spacestation.getDimensionName();
                        }
                        else
                        {
                            GCLog.severe("Failed to find a spacestation with dimension " + spacestationID);
                            return false;
                        }
                    }
                    else
                    {
                        dimension = WorldUtil.getProviderForDimensionClient(this.selectedBody.getDimensionID()).getDimensionName();
                    }

                    if (dimension.contains("$"))
                    {
                        this.mc.gameSettings.thirdPersonView = 0;
                    }

                    AmunRa.packetPipeline.sendToServer(new PacketSimpleAR(PacketSimpleAR.EnumSimplePacket.S_TELEPORT_SHUTTLE, new Object[] { dimension }));
                    //TODO   Some type of clientside "in Space" holding screen here while waiting for the server to do the teleport
                    //(Otherwise the client will be returned to the destination he was in until now, which looks weird)
                    mc.displayGuiScreen(null);
                    return true;
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
        }
        return false;
    }

    @Override
    protected void mouseClicked(int x, int y, int button) {
        super.mouseClicked(x, y, button);
    }


}
