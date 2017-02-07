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

    // ** dimension IDs **
    public int dimNeper     = 20;
    public int dimMaahes    = 21;
    public int dimAnubis    = 22;
    public int dimHorus     = 23;
    public int dimSeth      = 24;

    // default tier for my planets and moons
    public int planetDefaultTier = 3;

    public boolean villageAdvancedMachines = false;

    // ** motherships **
    public int maxNumMotherships = -1;
    public int mothershipMaxTier = 10;
    public int mothershipProviderID = -39;

    // motherships will refuse to start transit, if the time is > than this
    public int mothershipMaxTravelTime = 24000;

    public float mothershipSpeedFactor = 1.0F;

    public float mothershipFuelFactor = 1.0F;

    // bodies which motherships cannot orbit
    public Set<String> mothershipBodiesNoOrbit;

    // *** sky rendering and related ***
    // bodies not to render
    public Set<String> bodiesNoRender;

    public Set<String> asteroidBeltBodies;

    // star lines for transit sky
    public int mothershipNumStarLines = 400;

    public int numAsteroids = 600;

    // bodies to render as suns
    public HashMap<String, Vector3> sunColorMap = new HashMap<String, Vector3>();

    public HashMap<String, RingsRenderInfo> ringMap = new HashMap<String, RingsRenderInfo>();

    // ** IDs **
    public int schematicIdShuttle = 6;

    public int guiIdShuttle = 8;

    public float hydroponicsFactor = 1.0F;


    // ** extra default stuff **
    private final String[] defaultExtraSuns = {
        "tbn36b:0/0.1/1",
        "selpin:0/0.1/1",
        "tbn36a:1/0/0",
        "centaurib:1/0.7/0.8",
        "vega:0.8/0.8/1",
        "sirius:0.6/0.8/1",
        "siriusb:1/1/1",
        "dark:0.1/0.1/0.1",
        "kapteyn:0.70/0.1/0.1"
    };

    private final String[] defaultPlanetsWithRings = {
        "barnarda5:171:301:galaxyspace:textures/gui/celestialbodies/barnardaRings.png",
        "barnarda6:177:305:galaxyspace:textures/gui/celestialbodies/barnardaRings2.png",
        "appleapachia:8:20:extendedplanets:textures/gui/celestialbodies/appleapachiaRings.png"
    };

    private final String[] defaultAsteroidBelts = {
        "okblekbelt",
        "saturnrings"
    };

    public ARConfig() { }

    public void processConfig(Configuration config) {


        config.load();
        String[] emptySet = {};

        // Configuration goes here.
        //config.getInt(name, category, defaultValue, minValue, maxValue, comment)
        dimNeper    = config.get("dimension_ids", "Neper",  dimNeper).getInt();
        dimMaahes   = config.get("dimension_ids", "Maahes", dimMaahes).getInt();
        dimAnubis   = config.get("dimension_ids", "Anubis", dimAnubis).getInt();
        dimHorus    = config.get("dimension_ids", "Horus",  dimHorus).getInt();
        dimSeth     = config.get("dimension_ids", "Seth",   dimSeth).getInt();

        // villages
        villageAdvancedMachines = config.get("villages", "UseAdvancedMachines", false,
                "If true, robot villages will have advanced solar collectors, storage clusters and heavy wires").getBoolean();

        // general
        planetDefaultTier = config.getInt("default_tier", "general", planetDefaultTier, 0, 1000,
                "Default tier for AmunRa planets and moons");

        hydroponicsFactor = config.getFloat("hydroponicsFactor", "general", hydroponicsFactor, Float.MIN_VALUE, Float.MAX_VALUE,
                "Multiplier for the oxygen production of the hydroponics unit");


        // motherships
        maxNumMotherships = config.getInt("numMothershipsPerPlayer", "motherships", maxNumMotherships, -1, 1000,
                "Maximal amount of motherships one single player can have. Set to -1 to remove the restriction.");

        mothershipProviderID = config.getInt("mothershipProviderID", "motherships", mothershipProviderID, Integer.MIN_VALUE, Integer.MAX_VALUE,
                "ID for the Mothership World Provider");

        mothershipMaxTier = config.getInt("maxMothershipTier", "motherships", mothershipMaxTier, 1, Integer.MAX_VALUE,
                "Maximal tier which can be reached from a mothership. Motherships will pretty much ignore the tier system otherwise.");

        mothershipMaxTravelTime = config.getInt("maxMothershipTravelTime", "motherships", mothershipMaxTravelTime, 1, Integer.MAX_VALUE,
                "Maximal travel time (in ticks) for a mothership. Destinations with a longer travel time are unreachable. 24000 = one Overworld day");

        mothershipSpeedFactor = config.getFloat("mothershipSpeedFactor", "motherships", mothershipSpeedFactor, Float.MIN_VALUE, Float.MAX_VALUE,
                "A factor to be multiplied onto the mothership speed. Higher values = faster motherships.");

        mothershipFuelFactor = config.getFloat("mothershipFuelFactor", "motherships", mothershipFuelFactor, Float.MIN_VALUE, Float.MAX_VALUE,
                "A factor to be multiplied onto the fuel usages of mothership engines. Higher values = higher fuel usage");

        mothershipBodiesNoOrbit = configGetStringHashSet(config, "bodiesNoOrbit", "motherships", emptySet, "Bodies which should not be orbitable by motherships");

        // rendering
        mothershipNumStarLines = config.getInt("mothershipStarLines", "rendering", mothershipNumStarLines, 0, Integer.MAX_VALUE,
                "Number of speed lines to display while in transit. A lower number might improve performance, while a higher might look nicer.");

        numAsteroids = config.getInt("numAsteroids", "rendering", numAsteroids, 0, Integer.MAX_VALUE,
                "Approximate number of asteroids drawn in the sky when 'orbiting' an asteroid belt.");

        // excluded bodies
        bodiesNoRender = configGetStringHashSet(config, "skyRenderExclude", "rendering", emptySet, "Names of bodies to exclude from rendering in the sky, for reasons other than being asteroid belts");


        // asteroidBeltBodies
        asteroidBeltBodies = configGetStringHashSet(config, "asteroidBelts", "rendering", defaultAsteroidBelts, "Names of bodies to be considered asteroid belts. These values are automatically added to skyRenderExclude, so it is not necessary to add them to both.");

        // suns

        String[] sunData = config.getStringList("additionalSuns", "rendering", defaultExtraSuns, "Additional bodies to render with a colored aura, or set the aura of a specific star. \nThe bodies in here will be considered stars on motherships as well. \nFormat: '<bodyName>:<r>/<g>/<b>' with the colors as floats between 0 and 1. \nExample: 'myPlanet:1/0.6/0.1'");
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

        String[] ringData = config.getStringList("planetsWithRings", "rendering", defaultPlanetsWithRings, "Bodies to render with rings. \nThe format is: <bodyName>:<gapStart>:<gapEnd>:<Mod_Asset_Prefix>:<textureName>. \nThe 'gapStart' and 'gapEnd' is the number of pixels from the left or the top to the start of the gap for the planet and the end, respectively. \nExample: 'uranus:8:20:galacticraftcore:textures/gui/celestialbodies/uranusRings.png'");
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

    /**
     * Add some things to the config which should always be in there
     */
    public void setStaticConfigValues() {


        asteroidBeltBodies.add(AmunRa.instance.asteroidBeltMehen.getName());
        asteroidBeltBodies.add(AmunRa.instance.moonBaalRings.getName());
        asteroidBeltBodies.add(AsteroidsModule.planetAsteroids.getName());

        bodiesNoRender.addAll(asteroidBeltBodies);
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

    public boolean isAsteroidBelt(CelestialBody body) {
        return asteroidBeltBodies.contains(body.getName());
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
