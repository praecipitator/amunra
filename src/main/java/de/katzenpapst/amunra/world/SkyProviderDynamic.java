package de.katzenpapst.amunra.world;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

import javax.vecmath.Vector3f;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import de.katzenpapst.amunra.AmunRa;
import de.katzenpapst.amunra.astronomy.AstronomyHelper;
import micdoodle8.mods.galacticraft.api.galaxies.CelestialBody;
import micdoodle8.mods.galacticraft.api.galaxies.GalaxyRegistry;
import micdoodle8.mods.galacticraft.api.galaxies.Moon;
import micdoodle8.mods.galacticraft.api.galaxies.Planet;
import micdoodle8.mods.galacticraft.api.galaxies.SolarSystem;
import micdoodle8.mods.galacticraft.api.galaxies.Star;
import micdoodle8.mods.galacticraft.api.world.IGalacticraftWorldProvider;
import micdoodle8.mods.galacticraft.core.GalacticraftCore;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.renderer.GLAllocation;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Vec3;
import net.minecraftforge.client.IRenderHandler;

public class SkyProviderDynamic extends IRenderHandler {

    public class BodyRenderTask implements Comparable<BodyRenderTask> {
        // angle at which the body should be rendered in the sky of the current body
        public double angle;
        // zIndex in order to make the bodies overlap
        public double zIndex;
        // size of the body in the sky
        public double scale;

        /*
        this *should* be the current body's phase on the current sky
            0 = new, 0 < x < 180 = waxing, 180 = full, 180 < 360 = waning
        it should even automatically work for moons
         */
        public double phaseAngle;
        // the body to render
        public CelestialBody body;

        public BodyRenderTask(CelestialBody body, double angle, double zIndex, double scale, double phaseAngle) {
            this.body = body;
            this.angle = fixAngle(angle);
            this.zIndex = zIndex;
            this.scale = scale;
            this.phaseAngle = fixAngle(phaseAngle);
        }

        private double fixAngle(double angle2) {
            while(angle2 > Math.PI*2) {
                angle2 -= Math.PI*2;
            }
            while(angle2 < 0) {
                angle2 += Math.PI*2;
            }
            return angle2;
        }

        @Override
        public int compareTo(BodyRenderTask other) {

            if(this.zIndex > other.zIndex) {
                return -1;
            } else if(this.zIndex < other.zIndex) {
                return 1;
            }
            return 0;
        }

        //Tessellator tessellator1, CelestialBody body, float angle, float zIndex, float scale)
    }

    // how to render the sky
    protected enum RenderType {
        // render as if we are on a star, whenever that makes sense or not
        STAR,
        // render as if we are on a planet
        PLANET,
        // render as if we are on a moon
        MOON
    }

    protected RenderType rType = RenderType.PLANET;

    protected boolean hasHorizon = true;

    public static final double PI_HALF = Math.PI/2;
    public static final double PI_DOUBLE = Math.PI*2;

    protected ArrayList<BodyRenderTask> farBodiesToRender = new ArrayList<BodyRenderTask>();
    protected ArrayList<BodyRenderTask> nearBodiesToRender = new ArrayList<BodyRenderTask>();

    private static final ResourceLocation overworldTexture = new ResourceLocation(GalacticraftCore.ASSET_PREFIX, "textures/gui/celestialbodies/earth.png");
    private static final ResourceLocation sunTexture = new ResourceLocation("textures/environment/sun.png");

    // angle of the system in the sky
    private static final float planetAxisAngle = -19.0F;
    // angle of the moons' orbits relative to the equator
    private static float moonAxisAngle = 10.0F;


    private double parentSunFactor = 6.0D;
    private double parentPlanetFactor = 80.0D;
    private double siblingPlanetFactor = 1.0D;
    private double siblingStarFactor = 4.0D;

    private double siblingMoonFactor = 80.0D;
    private double childMoonFactor = 400.0D;
    private double childPlanetFactor = 5.0D;

    public int starList;
    public int glSkyList;
    public int glSkyList2;
    protected float sunSize;




    // system to render in the sky
    protected SolarSystem curSystem;
    // the body to render it around
    protected CelestialBody curBody;
    // this is either the same as curBody, or it's parent, if its a moon
    protected CelestialBody curBodyPlanet;
    // the distance of this body or it's parent from the sun
    protected float curBodyDistance;
    protected float boxWidthHalf = 311;

    protected boolean hasAtmosphere = true;

    protected Vec3 planetSkyColor = null; // this will be set at the beginning of each render() call

    protected float currentCelestialAngle = 0;

    protected IGalacticraftWorldProvider worldProvider;


    public SkyProviderDynamic(IGalacticraftWorldProvider worldProvider) {
        this.sunSize = 2*worldProvider.getSolarSize();
        curBody = worldProvider.getCelestialBody();
        this.worldProvider = worldProvider;
        // find the current system

        this.initVars();



        //curSystem = curBody.getPhaseShift(

        int displayLists = GLAllocation.generateDisplayLists(3);
        this.starList = displayLists;
        this.glSkyList = displayLists + 1;
        this.glSkyList2 = displayLists + 2;

        // Bind stars to display list
        GL11.glPushMatrix();
        GL11.glNewList(this.starList, GL11.GL_COMPILE);
        this.prepareStars();
        GL11.glEndList();
        GL11.glPopMatrix();

        final Tessellator tessellator = Tessellator.instance;
        // begin of glSkyList
        GL11.glNewList(this.glSkyList, GL11.GL_COMPILE);
        final byte byte2 = 64;
        final int i = 256 / byte2 + 2;
        float f = 16F;

        for (int j = -byte2 * i; j <= byte2 * i; j += byte2)
        {
            for (int l = -byte2 * i; l <= byte2 * i; l += byte2)
            {
                tessellator.startDrawingQuads();
                tessellator.addVertex(j + 0, f, l + 0);
                tessellator.addVertex(j + byte2, f, l + 0);
                tessellator.addVertex(j + byte2, f, l + byte2);
                tessellator.addVertex(j + 0, f, l + byte2);
                tessellator.draw();
            }
        }

        GL11.glEndList();
        // end of glSkyList

        // begin of glSkyList2
        GL11.glNewList(this.glSkyList2, GL11.GL_COMPILE);
        f = -16F;
        tessellator.startDrawingQuads();

        for (int k = -byte2 * i; k <= byte2 * i; k += byte2)
        {
            for (int i1 = -byte2 * i; i1 <= byte2 * i; i1 += byte2)
            {
                tessellator.addVertex(k + byte2, f, i1 + 0);
                tessellator.addVertex(k + 0, f, i1 + 0);
                tessellator.addVertex(k + 0, f, i1 + byte2);
                tessellator.addVertex(k + byte2, f, i1 + byte2);
            }
        }

        tessellator.draw();
        GL11.glEndList();
        // end of glSkyList2
    }

    protected void initVars() {
        if(curBody instanceof Planet) {
            rType = RenderType.PLANET;
            curBodyPlanet = ((Planet)curBody);
            curSystem = ((Planet)curBody).getParentSolarSystem();
        } else if(curBody instanceof Moon) {
            rType = RenderType.MOON;
            curBodyPlanet = ((Moon)curBody).getParentPlanet();
            curSystem = (((Moon)curBody).getParentPlanet()).getParentSolarSystem();
        } else if(curBody instanceof Star){
            rType = RenderType.STAR;
            curSystem = ((Star)curBody).getParentSolarSystem();
            // this skyprovider is only for moons and planets
        }

        this.hasAtmosphere = curBody.atmosphere.size() > 0;
        curBodyDistance = curBodyPlanet.getRelativeDistanceFromCenter().unScaledDistance;
    }


    @Override
    public void render(float partialTicks, WorldClient world, Minecraft mc) {
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glDisable(GL12.GL_RESCALE_NORMAL);
        RenderHelper.enableStandardItemLighting();
        planetSkyColor = world.getSkyColor(mc.renderViewEntity, partialTicks);
        float skyR = (float) planetSkyColor.xCoord;
        float skyG = (float) planetSkyColor.yCoord;
        float skyB = (float) planetSkyColor.zCoord;
        float f6;


        if (mc.gameSettings.anaglyph)
        {
            float f4 = (skyR * 30.0F + skyG * 59.0F + skyB * 11.0F) / 100.0F;
            float f5 = (skyR * 30.0F + skyG * 70.0F) / 100.0F;
            f6 = (skyR * 30.0F + skyB * 70.0F) / 100.0F;
            skyR = f4;
            skyG = f5;
            skyB = f6;
        }




        GL11.glColor3f(skyR, skyG, skyB);
        Tessellator tessellator1 = Tessellator.instance;
        GL11.glDepthMask(false);
        GL11.glEnable(GL11.GL_FOG);

        GL11.glColor3f(skyR, skyG, skyB);
        // doing something with glSkyList...
        GL11.glCallList(this.glSkyList);
        GL11.glDisable(GL11.GL_FOG);
        GL11.glDisable(GL11.GL_ALPHA_TEST);
        GL11.glEnable(GL11.GL_BLEND);
        OpenGlHelper.glBlendFunc(770, 771, 1, 0);
        RenderHelper.disableStandardItemLighting();
        float f7;
        float f8;
        float f9;
        float f10;

        // AH this seems to be what prevents the stars to be visible at day
        float curBrightness = world.getStarBrightness(partialTicks);



        float[] afloat = new float[4];
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glShadeModel(GL11.GL_SMOOTH);


        GL11.glPushMatrix();
        GL11.glRotatef(-90.0F, 0.0F, 1.0F, 0.0F);
        currentCelestialAngle = world.getCelestialAngle(partialTicks);
        GL11.glRotatef(currentCelestialAngle  * 360.0F, 1.0F, 0.0F, 0.0F);
        GL11.glRotatef(this.planetAxisAngle, 0, 1.0F, 0);
        renderStars(curBrightness);
        afloat[0] = 255 / 255.0F;
        afloat[1] = 194 / 255.0F;
        afloat[2] = 180 / 255.0F;
        afloat[3] = 0.3F;
        f6 = afloat[0];
        f7 = afloat[1];
        f8 = afloat[2];
        float f11;

        if (mc.gameSettings.anaglyph)
        {
            f9 = (f6 * 30.0F + f7 * 59.0F + f8 * 11.0F) / 100.0F;
            f10 = (f6 * 30.0F + f7 * 70.0F) / 100.0F;
            f11 = (f6 * 30.0F + f8 * 70.0F) / 100.0F;
            f6 = f9;
            f7 = f10;
            f8 = f11;
        }

        curBrightness = 1.0F - curBrightness;

        GL11.glPopMatrix();

        // BEGIN?
        GL11.glShadeModel(GL11.GL_FLAT);

        GL11.glEnable(GL11.GL_TEXTURE_2D);
        //renderSystem(partialTicks, world, tessellator1, mc);
        //OpenGlHelper.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE, GL11.GL_ONE, GL11.GL_ZERO);
        GL11.glPushMatrix();
        f7 = 0.0F;
        f8 = 0.0F;
        f9 = 0.0F;
        GL11.glTranslatef(f7, f8, f9);
        GL11.glRotatef(-90.0F, 0.0F, 1.0F, 0.0F);
        // rotates the sky by the celestial angle on the x axis
        // this seems to mean that the x-axis is the rotational axis of the planet
        // does the sun move from -z to z or the other way round?
        GL11.glRotatef(world.getCelestialAngle(partialTicks) * 360.0F, 1.0F, 0.0F, 0.0F);

        // so at this point, I'm where the sun is supposed to be. This is where I have to start.

     // Render system
        renderSystem(partialTicks, world, tessellator1, mc);





        GL11.glDisable(GL11.GL_TEXTURE_2D);

        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        GL11.glDisable(GL11.GL_BLEND);
        //OpenGlHelper.glBlendFunc(770, 771, 1, 0);

        GL11.glEnable(GL11.GL_ALPHA_TEST);
        GL11.glEnable(GL11.GL_FOG);
        GL11.glPopMatrix();
        // END?


        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glColor3f(0.0F, 0.0F, 0.0F);

        double d0 = mc.thePlayer.getPosition(partialTicks).yCoord - world.getHorizon();

        // WTF is this doing?
        // I think this obscures stuff below the horizon
        if(hasHorizon) {



            if (d0 < 0.0D)
            {
                GL11.glPushMatrix();
                GL11.glTranslatef(0.0F, 12.0F, 0.0F);
                GL11.glCallList(this.glSkyList2);
                GL11.glPopMatrix();
                f8 = 1.0F;
                f9 = -((float) (d0 + 65.0D));
                f10 = -f8;
                tessellator1.startDrawingQuads();
                tessellator1.setColorRGBA_I(0, 255);
                tessellator1.addVertex(-f8, f9, f8);
                tessellator1.addVertex(f8, f9, f8);
                tessellator1.addVertex(f8, f10, f8);
                tessellator1.addVertex(-f8, f10, f8);
                tessellator1.addVertex(-f8, f10, -f8);
                tessellator1.addVertex(f8, f10, -f8);
                tessellator1.addVertex(f8, f9, -f8);
                tessellator1.addVertex(-f8, f9, -f8);
                tessellator1.addVertex(f8, f10, -f8);
                tessellator1.addVertex(f8, f10, f8);
                tessellator1.addVertex(f8, f9, f8);
                tessellator1.addVertex(f8, f9, -f8);
                tessellator1.addVertex(-f8, f9, -f8);
                tessellator1.addVertex(-f8, f9, f8);
                tessellator1.addVertex(-f8, f10, f8);
                tessellator1.addVertex(-f8, f10, -f8);
                tessellator1.addVertex(-f8, f10, -f8);
                tessellator1.addVertex(-f8, f10, f8);
                tessellator1.addVertex(f8, f10, f8);
                tessellator1.addVertex(f8, f10, -f8);
                tessellator1.draw();
            }

            if (world.provider.isSkyColored())
            {
                GL11.glEnable(GL11.GL_BLEND);
                GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
                GL11.glColor3f(skyR * 0.2F + 0.04F, skyG * 0.2F + 0.04F, skyB * 0.6F + 0.1F);
            }
            else
            {
                GL11.glColor3f(skyR, skyG, skyB);
            }

            GL11.glPushMatrix();
            GL11.glTranslatef(0.0F, -((float) (d0 - 16.0D)), 0.0F);
            GL11.glCallList(this.glSkyList2);
            GL11.glPopMatrix();

        }
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glDepthMask(true);


    }

    protected void renderStars(float curBrightness) {
        if(hasAtmosphere) {
            if (curBrightness > 0.0F)
            {
                GL11.glColor4f(curBrightness, curBrightness, curBrightness, curBrightness);
                GL11.glCallList(this.starList);
            }
        } else {
            GL11.glColor4f(0.7F, 0.7F, 0.7F, 0.7F);
            GL11.glCallList(this.starList);
        }
    }



    /**
     * Render other planets of the current system
     *
     * @param curBodyOrbitalAngle
     * @param curWorldTime
     * @param partialTicks
     */
    protected void renderSiblingPlanets(double curBodyOrbitalAngle, long curWorldTime, float partialTicks) {
        for (Planet planet : GalaxyRegistry.getRegisteredPlanets().values()) {
            // oh well I hope this doesn't kill the performance

            if(planet.getParentSolarSystem() != curSystem || planet.equals(curBodyPlanet)) {
                continue;
            }
            if(AmunRa.instance.confBodiesNoRender.contains(planet.getName())) {
                continue;
            }

            float dist = planet.getRelativeDistanceFromCenter().unScaledDistance;

            // orbital angle of the planet
            double curOrbitalAngle = getOrbitalAngle(
                    planet.getRelativeOrbitTime(), planet.getPhaseShift(), curWorldTime, partialTicks, AstronomyHelper.yearFactor);
            // but I need it relative to curOrbitalAngle, or actually to curOrbitalAngle rotated by 180°,
            // just because that's how I calculated that stuff

            curOrbitalAngle -= (Math.PI*2-curBodyOrbitalAngle);



            // angle between connection line curBody<-->sun and planet<-->sun
            double innerAngle = Math.PI-curOrbitalAngle;

            // distance between curBody<-->planet, also needed for scaling
            double distanceToPlanet = getDistanceToBody(innerAngle, dist);

            double projectedAngle = projectAngle(innerAngle, dist, distanceToPlanet, curBodyDistance);

            // float zIndex = distanceToPlanet / 400.0F; // I DUNNO

            //if(planet.equals(other))
            double distance;
            if(AstronomyHelper.isStar(planet)) {
                distance = planet.getRelativeSize() / distanceToPlanet / 4.0D * siblingStarFactor;
            } else {
                distance = planet.getRelativeSize() / distanceToPlanet / 4.0D * siblingPlanetFactor;
            }
            this.farBodiesToRender.add(
                    new BodyRenderTask(
                            planet,
                            projectedAngle,
                            distanceToPlanet,
                            distance,
                            innerAngle
                        )
                );

        }
    }

    protected void renderChildPlanets(long curWorldTime, float partialTicks) {
        double curOrbitalAngle;
        for (Planet planet : GalaxyRegistry.getRegisteredPlanets().values()) {
            // oh well I hope this doesn't kill the performance
            if(planet.getParentSolarSystem() != curSystem || planet.equals(curBodyPlanet)) {
                continue;
            }
            if(AmunRa.instance.confBodiesNoRender.contains(planet.getName())) {
                continue;
            }

            double zIndex = planet.getRelativeDistanceFromCenter().unScaledDistance;

            // orbital angle of the planet
            curOrbitalAngle = getOrbitalAngle(
                    planet.getRelativeOrbitTime(), planet.getPhaseShift(), curWorldTime, partialTicks, AstronomyHelper.yearFactor);

            // float zIndex = dist / 400.0F; // I DUNNO

            //if(planet.equals(other))
            double scale = planet.getRelativeSize() / zIndex / 4.0D * childPlanetFactor;

            this.farBodiesToRender.add(
                    new BodyRenderTask(
                            planet,
                            curOrbitalAngle,
                            zIndex,
                            scale,
                            0 // always fully lighted
                        )
                );

        }
    }

    /**
     * Render the moons around a planet
     *
     * @param curWorldTime
     * @param partialTicks
     */
    protected void renderChildMoons(long curWorldTime, float partialTicks) {
        double curOrbitalAngle;
        for (Moon moon : GalaxyRegistry.getRegisteredMoons().values()) {
            if(!moon.getParentPlanet().equals(curBodyPlanet)) {
                continue;
            }
            if(AmunRa.instance.confBodiesNoRender.contains(moon.getName())) {
                continue;
            }
            curOrbitalAngle = getOrbitalAngle(moon.getRelativeOrbitTime()/100, moon.getPhaseShift(), curWorldTime, partialTicks, AstronomyHelper.monthFactor);
            // not projecting the angle here
            double zIndex = moon.getRelativeDistanceFromCenter().unScaledDistance/20;
            double distance = moon.getRelativeSize()/moon.getRelativeDistanceFromCenter().unScaledDistance*childMoonFactor;
            this.nearBodiesToRender.add(
                new BodyRenderTask(moon, curOrbitalAngle, zIndex, distance,(Math.PI-curOrbitalAngle))
            );
        }
    }

    /**
     * Render siblings, if we are around a planet
     *
     * @param curOrbitalAngle
     * @param curWorldTime
     * @param partialTicks
     */
    protected void renderSiblingMoons(double curOrbitalAngle, long curWorldTime, float partialTicks) {
        double distanceToParent = curBody.getRelativeDistanceFromCenter().unScaledDistance;
        for (Moon moon : GalaxyRegistry.getRegisteredMoons().values()) {
            if(!moon.getParentPlanet().equals(curBodyPlanet) || moon.equals(curBody) || excludeBodyFromRendering(moon)) {
                continue;
            }

            if(AmunRa.instance.confBodiesNoRender.contains(moon.getName())) {
                continue;
            }

            // this is what I do for planets
            float dist = moon.getRelativeDistanceFromCenter().unScaledDistance;

            // orbital angle of the moon
            double moonOrbitalAngle = getOrbitalAngle(moon.getRelativeOrbitTime(), moon.getPhaseShift(), curWorldTime, partialTicks, AstronomyHelper.monthFactor);

            // but I need it relative to curOrbitalAngle, or actually to curOrbitalAngle rotated by 180°,
            // just because that's how I calculated that stuff

            moonOrbitalAngle -= (Math.PI*2-curOrbitalAngle);

            // angle between connection line curBody<-->parent and moon<-->parent
            double innerAngle = Math.PI-moonOrbitalAngle;

            // distance between curBody<-->moon, also needed for scaling
            double distanceToPlanet = getDistanceToBody(innerAngle, dist);

            double zIndexMoon = (distanceToPlanet/20.0D);

            double projectedAngle = projectAngle(innerAngle, dist, distanceToPlanet, distanceToParent);

            double distance = (moon.getRelativeSize() / distanceToPlanet) * siblingMoonFactor;

            this.nearBodiesToRender.add(
                new BodyRenderTask(moon, projectedAngle,
                        zIndexMoon,
                        distance,
                        innerAngle)
            );
        }
    }

    /**
     * Hack for motherships >_>
     * @param body
     * @return
     */
    protected boolean excludeBodyFromRendering(CelestialBody body) {
        return false;
    }

    /**
     * If we are around a planet, render that.
     *
     * @param curOrbitalAngle
     */
    protected void renderParentPlanet(double curOrbitalAngle) {
        double distanceToParent = curBody.getRelativeDistanceFromCenter().unScaledDistance;

        double mainBodyOrbitalAngle = Math.PI-curOrbitalAngle;
        double zIndex = (float) (20/distanceToParent);
        double distance = (float) (curBodyPlanet.getRelativeSize() / distanceToParent) * parentPlanetFactor;
        // my parent
        this.nearBodiesToRender.add(
            new BodyRenderTask(curBodyPlanet, mainBodyOrbitalAngle, zIndex, distance, mainBodyOrbitalAngle)
        );
    }

    protected void renderMainStar() {
        double distance = this.sunSize/curBodyDistance * parentSunFactor;


        this.farBodiesToRender.add(
            new BodyRenderTask(
                curSystem.getMainStar(),  // body
                0.0D,  // angle
                curBodyDistance, // zIndex
                distance,   // scale
                0.0D  // phaseAngle
            )
        ); // phaseAngle = 0 for the sun
    }

    protected void prepareSystemForRender(long curWorldTime, float partialTicks) {
        double curOrbitalAngle;

        switch(this.rType) {
        case STAR:
            // only child planets
            renderChildPlanets(curWorldTime, partialTicks);
            break;
        case PLANET:
            // star, sibling planets and child moons
            // get my own angle
            curOrbitalAngle = getOrbitalAngle(
                    curBodyPlanet.getRelativeDistanceFromCenter().unScaledDistance,
                    curBodyPlanet.getPhaseShift(), curWorldTime , partialTicks, AstronomyHelper.yearFactor);

            renderMainStar();
            renderSiblingPlanets(curOrbitalAngle, curWorldTime, partialTicks);
            renderChildMoons(curWorldTime, partialTicks);
            break;
        case MOON:
            curOrbitalAngle = getOrbitalAngle(curBody.getRelativeOrbitTime()/100, curBody.getPhaseShift(), curWorldTime, partialTicks, AstronomyHelper.monthFactor);

            renderMainStar();
            renderParentPlanet(curOrbitalAngle);
            renderSiblingMoons(curOrbitalAngle, curWorldTime, partialTicks);
            break;
        }
    }


    protected void renderSystem(float partialTicks, WorldClient world, Tessellator tess, Minecraft mc) {
        // assume we are at the position of the sun

        this.farBodiesToRender.clear();
        this.nearBodiesToRender.clear();
        GL11.glDisable(GL11.GL_TEXTURE_2D); // important, stuff flickers otherwise


        long curWorldTime = world.getWorldTime();

        prepareSystemForRender(curWorldTime, partialTicks);

        Collections.sort(this.farBodiesToRender);
        Collections.sort(this.nearBodiesToRender);

        // now do moons
        GL11.glPushMatrix();
        // try to rotate it
        GL11.glRotatef(this.planetAxisAngle, 0, 1.0F, 0);
        GL11.glEnable(GL11.GL_BLEND);
        // actually render the stuff

        for(BodyRenderTask task: this.farBodiesToRender) {
            renderPlanetByAngle(tess, task.body, task.angle, task.zIndex+120.0F, task.scale, task.phaseAngle);

        }

        GL11.glPopMatrix();

        GL11.glPushMatrix();
        GL11.glRotatef(this.moonAxisAngle , 0, 1.0F, 0);
        for(BodyRenderTask task: this.nearBodiesToRender) {
            renderPlanetByAngle(tess, task.body, task.angle, task.zIndex+100.0F, task.scale, task.phaseAngle);
        }
        GL11.glPopMatrix();


    }

    protected double getOrbitalAngle(double relOrbitTime, double phaseShift, long worldTime, double partialTicks, double orbitFactor) {


        double curYearLength = relOrbitTime * orbitFactor;
        int j = (int)(worldTime % (long)curYearLength);
        double orbitPos = (j + partialTicks) / curYearLength - 0.25F;
        return orbitPos*2*Math.PI + phaseShift;
    }

    private double getDistanceToBody(double innerAngle, double otherBodyDistance) {
        return Math.sqrt(
                Math.pow(otherBodyDistance, 2) +
                Math.pow(curBodyDistance, 2) -
                2 * otherBodyDistance * curBodyDistance * Math.cos(innerAngle));
    }



    /**
     * Should convert an angle around the sun into an angle around this body
     *
     *
     * @param innerAngle                in radians, the angle between curBody<-->sun and otherBody<-->sun
     * @param otherBodyDistance            other body's orbital radius
     * @param distFromThisToOtherBody
     * @return
     */
    private double projectAngle(double innerAngle, double otherBodyDistance, double distFromThisToOtherBody, double curBodyDistance) {
        // omg now do dark mathemagic


        double sinBeta = Math.sin(innerAngle);

        // distFromThisToOtherBody = x
        // curBodyDistance = d
        // otherBodyDistance = r

        // gamma
        double angleAroundCurBody = Math.asin(
                    otherBodyDistance * sinBeta / distFromThisToOtherBody
                );

        if ( curBodyDistance > otherBodyDistance) {
            return angleAroundCurBody;
        }

        // now fix this angle...
        // for this, I need the third angle, too
        double delta = Math.asin(sinBeta / distFromThisToOtherBody * curBodyDistance);


        double angleSum = innerAngle+delta+angleAroundCurBody;
        double otherAngleSum =innerAngle+delta+(Math.PI-angleAroundCurBody);
        if(Math.abs(Math.abs(angleSum)/Math.PI - 1) < 0.001) {
            // aka angleSUm = 180 or -180
            return angleAroundCurBody;
        } else {
            return Math.PI-angleAroundCurBody;
        }
    }

    protected void renderSunAura(Tessellator tessellator1, Vector3f color, double size, double brightness, double zIndex) {
        GL11.glPushMatrix();
        // Vector3f basecolor = new Vector3f(0.94890916F, 0.72191525F, 0.6698182F);
        GL11.glShadeModel(GL11.GL_SMOOTH);
        //GL11.glBlendFunc(GL11.GL_ONE, GL11.GL_ZERO);
        OpenGlHelper.glBlendFunc(770, 771, 1, 0);
        /*GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL11.glEnable(GL11.GL_ALPHA_TEST);*/


        // small sun aura START
        zIndex += 0.01F;

        float maxOpacity = (float) (brightness/18.0F);
        if(maxOpacity > 1) {
            maxOpacity = 1.0F;
        }
        //maxOpacity = 0.4D;
        tessellator1.startDrawing(GL11.GL_TRIANGLE_FAN);
        tessellator1.setColorRGBA_F(color.x, color.y, color.z, maxOpacity );
        tessellator1.addVertex(0.0D, zIndex, 0.0D);
        //byte b0 = 16;
        tessellator1.setColorRGBA_F(color.x, color.y, color.z, 0.0F);



        // Render sun aura

        tessellator1.addVertex(-size, zIndex, -size);
        tessellator1.addVertex(0.0D,zIndex, -size * 1.5F);
        tessellator1.addVertex(size, zIndex, -size);
        tessellator1.addVertex(size * 1.5F, zIndex, 0.0D);
        tessellator1.addVertex(size, zIndex, size);
        tessellator1.addVertex(0.0D, zIndex, size * 1.5F);
        tessellator1.addVertex(-size, zIndex, size);
        tessellator1.addVertex( -size * 1.5F,  zIndex, 0.0D);
        tessellator1.addVertex(  -size, zIndex, -size);

        tessellator1.draw();

        // GL11.glBlendFunc(GL11.GL_DST_ALPHA, GL11.GL_ONE_MINUS_DST_ALPHA);

        GL11.glPopMatrix();
    }


    protected void renderPlanetByAngle(Tessellator tessellator1, CelestialBody body, double angle, double zIndex,
            double scale, double phaseAngle) {

        // at a scale of 0.15, the body is about 2x2 pixels
        // so this is rather generous, I think
        if(scale < 0.13D) {
            return;
        }


        boolean usePhaseOverlay = true;
        GL11.glPushMatrix();

        final double overlayScale = scale+0.001D;



        //GL11.glColor4f(1.0F, 1.0F, 1.0F, 1F);   // change this for your colour
        //GL11.glLineWidth(2.0F);
        // rotate on x
        GL11.glRotatef((float) (angle/Math.PI*180), 1.0F, 0.0F, 0.0F);

        Vector3f color = AmunRa.instance.confSunColorMap.get(body.getName());
        if(body instanceof Star && color == null) {
            color = new Vector3f(1.0F, 0.4F, 0.1F);
        }
        if(color != null) {
            renderSunAura(tessellator1, color, scale*5.0D, scale, zIndex);
            usePhaseOverlay = false;
        }

        //GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_DST_ALPHA);
        //OpenGlHelper.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE, GL11.GL_ONE, GL11.GL_ZERO);



        // actual planet
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);

        /*GL11.glColor4f(
                (float) planetSkyColor.xCoord,
                (float) planetSkyColor.yCoord,
                (float) planetSkyColor.zCoord, (float) (0.6));*/

        // tessellator1.setColorRGBA_F(1.0F, 1.0F, 1.0F, 1.0F);
        FMLClientHandler.instance().getClient().renderEngine.bindTexture(body.getBodyIcon());


        GL11.glTranslatef(0, 0, 0);

        tessellator1.startDrawingQuads();
        tessellator1.setColorRGBA_F(1.0F, 1.0F, 1.0F, 1.0F);
        tessellator1.addVertexWithUV(-scale, zIndex, -scale, 0, 0);
        tessellator1.addVertexWithUV(scale, zIndex, -scale, 1, 0);
        tessellator1.addVertexWithUV(scale, zIndex, scale, 1, 1);
        tessellator1.addVertexWithUV(-scale, zIndex, scale, 0, 1);
        tessellator1.draw();

        // actual planet END

        // phase overlay
        if(usePhaseOverlay) {
            drawPhaseOverlay(phaseAngle, body, scale+0.01D, tessellator1, zIndex);
        }
        // phase overlay END
/*
        if(hasAtmosphere) {
            drawAtmosphereOverlay(scale+0.1D, zIndex+0.02D, tessellator1);
        }*/
        GL11.glDisable(GL11.GL_TEXTURE_2D);

        GL11.glPopMatrix();


    }

    private void drawAtmosphereOverlay(double scale, double zIndex, Tessellator tessellator1) {
        double factor = 0;
        /* TODO make this work. or maybe actually find a way to make the fog obscure the planets?
                    double horizonOffse = 0.02;

                    if(angle < PI_HALF-horizonOffse) {
                        factor = angle;
                    } else if(angle > PI_DOUBLE-PI_HALF-horizonOffse) {
                        factor = (PI_DOUBLE-angle);
                    }

                    factor = (PI_HALF-horizonOffse-factor)/(PI_HALF-horizonOffse);
        */
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

        if(planetSkyColor.xCoord < 0.01F && planetSkyColor.yCoord < 0.01F && planetSkyColor.zCoord < 0.01) {
            return;
        }

        GL11.glColor4f(
                (float) planetSkyColor.xCoord,
                (float) planetSkyColor.yCoord,
                (float) planetSkyColor.zCoord, (float) (1));

        // it could be possible to adjust the colors and uv values at specific vertices in order
        // to simulate halfmoon etc
        tessellator1.startDrawingQuads();
        tessellator1.addVertexWithUV(-scale,zIndex+0.01F, -scale, 0, 0);
        tessellator1.addVertexWithUV(scale, zIndex+0.01F, -scale, 1, 0);
        tessellator1.addVertexWithUV(scale, zIndex+0.01F, scale, 1, 1);
        tessellator1.addVertexWithUV(-scale, zIndex+0.01F, scale, 0, 1);
        tessellator1.draw();
    }

    private void drawPhaseOverlay(double phaseAngle, CelestialBody body, double overlayScale, Tessellator tessellator1, double zIndex) {
        double startOffset = 0;
        double stopOffset = 0;

        // this is wrong for moons, TODO fix
        if((phaseAngle < PI_HALF || phaseAngle > (PI_DOUBLE-PI_HALF)) &&
                body.getRelativeDistanceFromCenter().unScaledDistance > this.curBodyPlanet.getRelativeDistanceFromCenter().unScaledDistance) {
            // this means, body is behind the current body
            if(phaseAngle < PI_HALF) {
                // larger angle -> smaller offset
                startOffset = overlayScale+(1-phaseAngle/PI_HALF)*overlayScale;
            } else {
                // smaller ange -> larger offset
                stopOffset = overlayScale+(1-(PI_DOUBLE-phaseAngle)/PI_HALF)*overlayScale;
            }

        } else {
            if(phaseAngle < Math.PI) {
                // more phaseAngle -> largerStartOffset
                startOffset = phaseAngle/Math.PI * overlayScale * 2;
                // since stopOffset is substracted from the end coord, 0 is ok here
            } else {
                // since start is added, 0 should work here
                // more phaseAngle -> SMALLER stopOffset
                stopOffset = (Math.PI*2-phaseAngle)/Math.PI * overlayScale * 2;
            }
        }

        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glColor4f(0.0F, 0.0F, 0.0F, 0.85F);


        tessellator1.startDrawingQuads();
        /*
         * Z    stopOffset
         * ^         V
         * |       D---C
         * |       |   |
         * |       A---B
         * |         ^
         * |   startOffset
         * +------------------> X
         * */
        double length = 2.0D*overlayScale;
        double texStartOffset = startOffset/length;
        double texStopOffset = (length-stopOffset)/length;

        // length = 2*overlayScale
        // texStartOffset =

        // A
        tessellator1.addVertexWithUV(-overlayScale, zIndex+0.01F, -overlayScale + startOffset, 0, texStartOffset);
        // B
        tessellator1.addVertexWithUV( overlayScale, zIndex+0.01F, -overlayScale + startOffset, 1, texStartOffset);

        // C
        tessellator1.addVertexWithUV( overlayScale, zIndex+0.01F,  overlayScale - stopOffset, 1, texStopOffset);
        // D
        tessellator1.addVertexWithUV(-overlayScale, zIndex+0.01F,  overlayScale - stopOffset, 0, texStopOffset);
        tessellator1.draw();
    }

    private void prepareStars()
    {
        final Random rand = new Random(10842L);
        final Tessellator var2 = Tessellator.instance;
        var2.startDrawingQuads();

        for (int starIndex = 0; starIndex < (6000); ++starIndex)
        {
            double var4 = rand.nextFloat() * 2.0F - 1.0F;
            double var6 = rand.nextFloat() * 2.0F - 1.0F;
            double var8 = rand.nextFloat() * 2.0F - 1.0F;
            final double var10 = 0.15F + rand.nextFloat() * 0.1F;
            double var12 = var4 * var4 + var6 * var6 + var8 * var8;

            if (var12 < 1.0D && var12 > 0.01D)
            {
                var12 = 1.0D / Math.sqrt(var12);
                var4 *= var12;
                var6 *= var12;
                var8 *= var12;
                final double var14 = var4 * 100.0D;//(ConfigManagerCore.moreStars ? rand.nextDouble() * 150D + 130D : 100.0D);
                final double var16 = var6 * 100.0D;//(ConfigManagerCore.moreStars ? rand.nextDouble() * 150D + 130D : 100.0D);
                final double var18 = var8 * 100.0D;//(ConfigManagerCore.moreStars ? rand.nextDouble() * 150D + 130D : 100.0D);
                final double var20 = Math.atan2(var4, var8);
                final double var22 = Math.sin(var20);
                final double var24 = Math.cos(var20);
                final double var26 = Math.atan2(Math.sqrt(var4 * var4 + var8 * var8), var6);
                final double var28 = Math.sin(var26);
                final double var30 = Math.cos(var26);
                final double var32 = rand.nextDouble() * Math.PI * 2.0D;
                final double var34 = Math.sin(var32);
                final double var36 = Math.cos(var32);

                for (int var38 = 0; var38 < 4; ++var38)
                {
                    final double var39 = 0.0D;
                    final double var41 = ((var38 & 2) - 1) * var10;
                    final double var43 = ((var38 + 1 & 2) - 1) * var10;
                    final double var47 = var41 * var36 - var43 * var34;
                    final double var49 = var43 * var36 + var41 * var34;
                    final double var53 = var47 * var28 + var39 * var30;
                    final double var55 = var39 * var28 - var47 * var30;
                    final double var57 = var55 * var22 - var49 * var24;
                    final double var61 = var49 * var22 + var55 * var24;
                    var2.addVertex(var14 + var57, var16 + var53, var18 + var61);
                }
            }
        }

        var2.draw();
    }

    private Vec3 getCustomSkyColor()
    {
        return Vec3.createVectorHelper(0.26796875D, 0.1796875D, 0.0D);
    }

    public float getSkyBrightness(float par1)
    {
        final float var2 = FMLClientHandler.instance().getClient().theWorld.getCelestialAngle(par1);
        float var3 = 1.0F - (MathHelper.sin(var2 * (float) Math.PI * 2.0F) * 2.0F + 0.25F);

        if (var3 < 0.0F)
        {
            var3 = 0.0F;
        }

        if (var3 > 1.0F)
        {
            var3 = 1.0F;
        }

        return var3 * var3 * 1F;
    }
}
