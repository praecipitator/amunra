package de.katzenpapst.amunra.config;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import org.apache.logging.log4j.Level;

import cpw.mods.fml.relauncher.FMLRelaunchLog;
import de.katzenpapst.amunra.AmunRa;
import de.katzenpapst.amunra.client.RingsRenderInfo;
import micdoodle8.mods.galacticraft.api.galaxies.CelestialBody;
import micdoodle8.mods.galacticraft.api.vector.Vector3;
import micdoodle8.mods.galacticraft.core.Constants;
import micdoodle8.mods.galacticraft.core.GalacticraftCore;
import micdoodle8.mods.galacticraft.planets.asteroids.AsteroidsModule;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.config.Configuration;

public class ARConfig {

    public int dimNeper;
    public int dimMaahes;
    public int dimAnubis;
    public int dimHorus;
    public int dimSeth;

    public boolean advancedVillageMachines = false;
    public int defaultTier = 3;

    public int maxMothershipTier = 10;
    public int maxNumMotherships = -1;
    public int mothershipProviderID = -39;

    public int mothershipNumStarLines = 400;

    public int mothershipMaxTravelTime = 24000;
    // bodies which motherships cannot orbit
    public Set<String> bodiesNoOrbit;

    // bodies not to render
    public Set<String> bodiesNoRender;

    // bodies to render as suns
    public HashMap<String, Vector3> sunColorMap = new HashMap<String, Vector3>();

    public HashMap<String, RingsRenderInfo> ringMap = new HashMap<String, RingsRenderInfo>();

    public int schematicIdShuttle = 6;

    public int guiIdShuttle = 8;

    public ARConfig() { }

    public void processConfig(Configuration config) {


        config.load();
        String[] emptySet = {};

        // Configuration goes here.
        //config.getInt(name, category, defaultValue, minValue, maxValue, comment)
        dimNeper    = config.get("dimension_ids", "Neper",  20).getInt();
        dimMaahes   = config.get("dimension_ids", "Maahes", 21).getInt();
        dimAnubis   = config.get("dimension_ids", "Anubis", 22).getInt();
        dimHorus    = config.get("dimension_ids", "Horus",  23).getInt();
        dimSeth     = config.get("dimension_ids", "Seth",   24).getInt();

        // villages
        advancedVillageMachines = config.get("villages", "UseAdvancedMachines", false,
                "If true, robot villages will have advanced solar collectors, storage clusters and heavy wires").getBoolean();

        // general
        defaultTier = config.getInt("default_tier", "general", defaultTier, 0, 1000,
                "Default tier for AmunRa planets and moons");

        // motherships
        maxNumMotherships = config.getInt("numMothershipsPerPlayer", "motherships", maxNumMotherships, -1, 1000,
                "Maximal amount of motherships one single player can have. Set to -1 to remove the restriction.");

        mothershipProviderID = config.getInt("mothershipProviderID", "motherships", mothershipProviderID, Integer.MIN_VALUE, Integer.MAX_VALUE,
                "ID for the Mothership World Provider");

        maxMothershipTier = config.getInt("maxMothershipTier", "motherships", maxMothershipTier, 1, Integer.MAX_VALUE,
                "Maximal tier which can be reached from a mothership. Motherships will pretty much ignore the tier system otherwise.");

        mothershipMaxTravelTime = config.getInt("maxMothershipTravelTime", "motherships", mothershipMaxTravelTime, 1, Integer.MAX_VALUE,
                "Maximal travel time (in ticks) for a mothership. Destinations with a longer travel time are unreachable. 24000 = one Overworld day");

        bodiesNoOrbit = configGetStringHashSet(config, "bodiesNoOrbit", "motherships", emptySet, "Bodies which should not be orbitable by motherships");

        // rendering
        mothershipNumStarLines = config.getInt("mothershipStarLines", "rendering", mothershipNumStarLines, 0, Integer.MAX_VALUE,
                "Number of speed lines to display while in transit. A lower number might improve performance, while a higher might look nicer.");

        // excluded bodies
        bodiesNoRender = configGetStringHashSet(config, "skyRenderExclude", "rendering", emptySet, "Names of bodies to exclude from rendering in the sky, usually for asteroid belts and stuff");

        // suns

        String[] sunData = config.getStringList("additionalSuns", "rendering", emptySet, "Additional bodies to render with a colored aura, or set the aura of a specific star. \nThe bodies in here will be considered stars on motherships as well. \nFormat: '<bodyName>:<r>/<g>/<b>' with the colors as floats between 0 and 1. \nExample: 'myPlanet:1/0.6/0.1'");
        for(String str: sunData) {
            String[] parts1 = str.split(":", 2);
            if(parts1.length < 2) {
                FMLRelaunchLog.log(Constants.MOD_NAME_SIMPLE, Level.WARN, "'"+parts1+"' is not a valid sun configuration");
                continue;
            }
            String body  = parts1[0];
            String color = parts1[1];

            String[] parts2 = color.split("/",3);
            if(parts2.length < 3) {
                continue;
            }


            Vector3 colorVec = new Vector3 (
                    Double.parseDouble(parts2[0]),
                    Double.parseDouble(parts2[1]),
                    Double.parseDouble(parts2[2])
            );

            sunColorMap.put(body, colorVec);

        }

        // rings

        String[] ringData = config.getStringList("planetsWithRings", "rendering", emptySet, "Bodies to render with rings. \nThe format is: <bodyName>:<gapStart>:<gapEnd>:<Mod_Asset_Prefix>:<textureName>. \nThe 'gapStart' and 'gapEnd' is the number of pixels from the left or the top to the start of the gap for the planet and the end, respectively. \nExample: 'uranus:8:20:galacticraftcore:textures/gui/celestialbodies/uranusRings.png'");
        for(String str: ringData) {
            String[] parts1 = str.split(":", 5);
            if(parts1.length < 5) {
                FMLRelaunchLog.log(Constants.MOD_NAME_SIMPLE, Level.WARN, "'"+str+"' is not a valid ring configuration");
                continue;
            }
            String body = parts1[0];
            int gapStart = Integer.valueOf(parts1[1]);
            int gapEnd = Integer.valueOf(parts1[2]);
            String assetPrefix = parts1[3];
            String textureName = parts1[4];



            if(gapStart <= 0 || gapEnd <= 0 || gapEnd <= gapStart) {
                FMLRelaunchLog.log(Constants.MOD_NAME_SIMPLE, Level.WARN, "'"+str+"' is not a valid ring configuration");
                continue;
            }

            ringMap.put(body, new RingsRenderInfo(new ResourceLocation(assetPrefix, textureName), gapStart, gapEnd));
        }
        //

        // schematics
        schematicIdShuttle = config.getInt("shuttleSchematicsId", "schematics", schematicIdShuttle, 6, Integer.MAX_VALUE,
                "ID of the Shuttle schematics, must be unique. 0-5 are used by Galacticraft already.");

        guiIdShuttle = config.getInt("shuttleGuiId", "schematics", guiIdShuttle, 8, Integer.MAX_VALUE,
                "ID of the Shuttle schematics GUI, must be unique. 0-7 are used by Galacticraft already.");

        //config.get

        // confMaxMothershipTier

        config.save();
    }

    public void setStaticConfigValues() {
        bodiesNoRender.add(AmunRa.instance.asteroidBeltMehen.getName());
        bodiesNoRender.add(AmunRa.instance.moonBaalRings.getName());
        bodiesNoRender.add(AsteroidsModule.planetAsteroids.getName());

        // suns
        sunColorMap.put(AmunRa.instance.starAmun.getName(), new Vector3(0.0D, 0.2D, 0.7D));

        // rings. do not override config settings, though
        // the actual planets from GCCore don't even exist at this point oO
        if(!ringMap.containsKey("uranus")) {
            ringMap.put("uranus", new RingsRenderInfo(new ResourceLocation(GalacticraftCore.ASSET_PREFIX, "textures/gui/celestialbodies/uranusRings.png"), 8, 20));
        }
        if(!ringMap.containsKey("saturn")) {
            ringMap.put("saturn", new RingsRenderInfo(new ResourceLocation(GalacticraftCore.ASSET_PREFIX, "textures/gui/celestialbodies/saturnRings.png"), 9, 21));
        }
    }

    public boolean isSun(CelestialBody body) {
        return sunColorMap.containsKey(body.getName());
    }



    private HashSet<String> configGetStringHashSet(Configuration config, String name, String category, String[] defaultValues, String comment) {
        String[] data = config.getStringList(name, category, defaultValues, comment);
        HashSet<String> result = new HashSet<String>();
        for(String str: data) {
            result.add(str);
        }
        return result;
    }

}
