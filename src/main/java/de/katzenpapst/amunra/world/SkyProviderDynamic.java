package de.katzenpapst.amunra.world;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

import javax.vecmath.Vector3f;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import cpw.mods.fml.client.FMLClientHandler;
import de.katzenpapst.amunra.AmunRa;
import de.katzenpapst.amunra.astronomy.AstronomyHelper;
import micdoodle8.mods.galacticraft.api.galaxies.CelestialBody;
import micdoodle8.mods.galacticraft.api.galaxies.GalaxyRegistry;
import micdoodle8.mods.galacticraft.api.galaxies.Moon;
import micdoodle8.mods.galacticraft.api.galaxies.Planet;
import micdoodle8.mods.galacticraft.api.galaxies.SolarSystem;
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

	protected class BodyRenderTask implements Comparable<BodyRenderTask> {
		public float angle;
		public float zIndex;
		public float scale;
		public CelestialBody body;

		public BodyRenderTask(CelestialBody body, float angle, float zIndex, float scale) {
			this.body = body;
			this.angle = angle;
			this.zIndex = zIndex;
			this.scale = scale;
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

	protected ArrayList<BodyRenderTask> farBodiesToRender = new ArrayList<BodyRenderTask>();
	protected ArrayList<BodyRenderTask> nearBodiesToRender = new ArrayList<BodyRenderTask>();

	private static final ResourceLocation overworldTexture = new ResourceLocation(GalacticraftCore.ASSET_PREFIX, "textures/gui/celestialbodies/earth.png");
    private static final ResourceLocation sunTexture = new ResourceLocation("textures/environment/sun.png");

    public int starList;
    public int glSkyList;
    public int glSkyList2;
    private float sunSize;

    //protected double yearFactor = 40000L; // technically, 8640000L would be true
//    protected double moonFactor = 192000L;

    // angle of the system in the sky
    protected float systemAngle = 24;
    // angle of the moons' orbits relative to the equator
    protected float moonAngle = 19;

    // system to render in the sky
    protected SolarSystem curSystem;
    // the body to render it around
    protected CelestialBody curBody;
    // this is either the same as curBody, or it's parent, if its a moon
    protected Planet curBodyPlanet;
    // the distance of this body or it's parent from the sun
    protected float curBodyDistance;
	private float boxWidthHalf = 311;

	protected boolean hasAtmosphere = true;

	protected Vec3 planetSkyColor = null; // this will be set at the beginning of each render() call

    public SkyProviderDynamic(IGalacticraftWorldProvider worldProvider) {
    	this.sunSize = 2*worldProvider.getSolarSize();
    	curBody = worldProvider.getCelestialBody();
    	// find the current system

    	if(curBody instanceof Planet) {
    		curBodyPlanet = ((Planet)curBody);
    		curSystem = curBodyPlanet.getParentSolarSystem();
    	} else if(curBody instanceof Moon) {
    		curBodyPlanet = ((Moon)curBody).getParentPlanet();
    		curSystem = curBodyPlanet.getParentSolarSystem();
    	} else {
    		// todo do somethign
    	}
    	this.hasAtmosphere = curBody.atmosphere.size() > 0;
    	curBodyDistance = curBodyPlanet.getRelativeDistanceFromCenter().unScaledDistance;
    	//curSystem = curBody.getPhaseShift(

    	int displayLists = GLAllocation.generateDisplayLists(3);
        this.starList = displayLists;
        this.glSkyList = displayLists + 1;
        this.glSkyList2 = displayLists + 2;

        // Bind stars to display list
        GL11.glPushMatrix();
        GL11.glNewList(this.starList, GL11.GL_COMPILE);
        this.renderStars();
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

        float[] afloat = new float[4];
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glShadeModel(GL11.GL_SMOOTH);
        GL11.glPushMatrix();
        GL11.glRotatef(-90.0F, 0.0F, 1.0F, 0.0F);
        GL11.glRotatef(world.getCelestialAngle(partialTicks) * 360.0F, 1.0F, 0.0F, 0.0F);
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
        OpenGlHelper.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE, GL11.GL_ONE, GL11.GL_ZERO);
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
        	//GL11.glEnable(GL11.GL_BLEND);
        	//GL11.glBlendFunc(GL11.GL_DST_ALPHA, GL11.GL_SRC_ALPHA);
            GL11.glColor3f(skyR * 0.2F + 0.04F, skyG * 0.2F + 0.04F, skyB * 0.6F + 0.1F);
        }
        else
        {
            GL11.glColor3f(skyR, skyG, skyB);
        }

        /*GL11.glPushMatrix();
        GL11.glTranslatef(0.0F, -((float) (d0 - 16.0D)), 0.0F);
        GL11.glCallList(this.glSkyList2);
        GL11.glPopMatrix();*/

        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glDepthMask(true);


	}


	protected void renderSystem(float partialTicks, WorldClient world, Tessellator tess, Minecraft mc) {
		// assume we are at the position of the sun

		this.farBodiesToRender.clear();
		this.nearBodiesToRender.clear();



		//renderPlanetByAngle(tess, curSystem.getMainStar(), 0, (float)sunY, this.sunSize*5);

		this.farBodiesToRender.add(new BodyRenderTask(curSystem.getMainStar(), 0,
				curBodyDistance,
				this.sunSize*3));




        long curWorldTime = world.getWorldTime();
        // get my own angle
        double curBodyOrbitalAngle = getOrbitalAngle(
        		curBodyPlanet.getRelativeDistanceFromCenter().unScaledDistance,
        		curBodyPlanet.getPhaseShift(), curWorldTime , partialTicks, AstronomyHelper.yearFactor);




        // now render planets. wait wat. you can't just iterate through all the planets in a system?!

        for (Planet planet : GalaxyRegistry.getRegisteredPlanets().values()) {
        	// oh well I hope this doesn't kill the performance
        	if(planet.getParentSolarSystem() != curSystem || planet.equals(curBodyPlanet) || planet.equals(AmunRa.instance.asteroidBeltMehen)) {
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
        	float distanceToPlanet = (float) getDistanceToBody(innerAngle, dist);

        	float projectedAngle = (float) projectAngle(innerAngle, dist, distanceToPlanet, curBodyDistance);

        	float zIndex = distanceToPlanet / 400.F; // I DUNNO

        	this.farBodiesToRender.add(
        			new BodyRenderTask(planet, projectedAngle, distanceToPlanet, 1.0F / distanceToPlanet)
    			);
        	// renderPlanetByAngle(tess, planet, (float)projectedAngle, zIndex, 1.0F / (float)distanceToPlanet);

        }



        if(this.curBody instanceof Planet) {
        	// oh my...
        	double curOrbitalAngle;
        	// moons of the current planet
        	for (Moon moon : GalaxyRegistry.getRegisteredMoons().values()) {
        		if(!moon.getParentPlanet().equals(curBody)) {
        			continue;
        		}
        		curOrbitalAngle = getOrbitalAngle(moon.getRelativeOrbitTime()/100, moon.getPhaseShift(), curWorldTime, partialTicks, AstronomyHelper.monthFactor);
        		// not projecting the angle here
        		float zIndex = 20/moon.getRelativeDistanceFromCenter().unScaledDistance;
        		this.nearBodiesToRender.add(
            			new BodyRenderTask(moon, (float)curOrbitalAngle, zIndex, zIndex)
        			);
        		// renderPlanetByAngle(tess, moon, (float) curOrbitalAngle, 0, 20/moon.getRelativeDistanceFromCenter().unScaledDistance);


        	}
        } else {

        	double distanceToParent = curBody.getRelativeDistanceFromCenter().unScaledDistance;
        	double curOrbitalAngle = getOrbitalAngle(curBody.getRelativeOrbitTime()/100, curBody.getPhaseShift(), curWorldTime, partialTicks, AstronomyHelper.monthFactor);
        	// render my parent body
        	// 180°-my angle around my parent, should be it's angle in my sky
        	double mainBodyOrbitalAngle = Math.PI-curOrbitalAngle;
        	float zIndex = (float) (20/distanceToParent);
        	// my parent
        	this.nearBodiesToRender.add(
        			new BodyRenderTask(curBodyPlanet, (float)mainBodyOrbitalAngle, zIndex, zIndex*5)
    			);
        	//renderPlanetByAngle(tess, curBodyPlanet, (float) mainBodyOrbitalAngle, -5.0F, (float) (60/distanceToParent));


        	// now do my sibling moons
        	for (Moon moon : GalaxyRegistry.getRegisteredMoons().values()) {
        		if(!moon.getParentPlanet().equals(curBodyPlanet) || moon.equals(curBody) || moon.equals(AmunRa.instance.moonBaalRings)) {
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

	        	float zIndexMoon = (float) (distanceToPlanet/20);

	        	double projectedAngle = projectAngle(innerAngle, dist, distanceToPlanet, distanceToParent);

	        	this.nearBodiesToRender.add(
            			new BodyRenderTask(moon, (float)curOrbitalAngle, zIndexMoon, zIndexMoon)
        			);

	        	// renderPlanetByAngle(tess, moon, (float)projectedAngle, (float) (zIndex-5.0F), 10.0F / (float)distanceToPlanet);


        	}



        }

        Collections.sort(this.farBodiesToRender);
        Collections.sort(this.nearBodiesToRender);
        // Collections.reverse(this.bodysToRender);

        // TESTING

         // try to rotate it
        // GL11.glRotatef(10, 0, 1.0F, 0);

 		/*GL11.glPushMatrix();
 		// sun aura

        GL11.glPopMatrix();*/

        // now do moons
        GL11.glPushMatrix();
        // try to rotate it
        GL11.glRotatef(-19, 0, 1.0F, 0);
        GL11.glEnable(GL11.GL_BLEND);
        // actually render the stuff
        for(BodyRenderTask task: this.farBodiesToRender) {
        	renderPlanetByAngle(tess, task.body, task.angle, task.zIndex, task.scale);
        }

        GL11.glPopMatrix();

        GL11.glPushMatrix();
        GL11.glRotatef(10, 0, 1.0F, 0);
        for(BodyRenderTask task: this.nearBodiesToRender) {
        	renderPlanetByAngle(tess, task.body, task.angle, task.zIndex, task.scale);
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
	 * @param innerAngle				in radians, the angle between curBody<-->sun and otherBody<-->sun
	 * @param otherBodyDistance			other body's orbital radius
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

	protected void renderSunAura(Tessellator tessellator1, Vector3f color, float size, float brightness, float zIndex) {
		GL11.glPushMatrix();
		// Vector3f basecolor = new Vector3f(0.94890916F, 0.72191525F, 0.6698182F);
		GL11.glShadeModel(GL11.GL_SMOOTH);

        OpenGlHelper.glBlendFunc(770, 771, 1, 0);

		// small sun aura START
		zIndex += 95.0F;

		float maxOpacity = brightness/18.0F;
		if(maxOpacity > 1) {
			maxOpacity = 1;
		}
		maxOpacity = 0.4F;
        tessellator1.startDrawing(GL11.GL_TRIANGLE_FAN);
        tessellator1.setColorRGBA_F(color.x, color.y, color.z, maxOpacity );
        tessellator1.addVertex(0.0D, zIndex, 0.0D);
        byte b0 = 16;
        tessellator1.setColorRGBA_F(color.x, color.y, color.z, 0.0F);


        // Render sun aura

        tessellator1.addVertex(-size, zIndex, -size);
        tessellator1.addVertex(0, zIndex, (double) -size * 1.5F);
        tessellator1.addVertex(size, zIndex, -size);
        tessellator1.addVertex((double) size * 1.5F, zIndex, 0);
        tessellator1.addVertex(size, zIndex, size);
        tessellator1.addVertex(0, zIndex, (double) size * 1.5F);
        tessellator1.addVertex(-size, zIndex, size);
        tessellator1.addVertex((double) -size * 1.5F, zIndex, 0);
        tessellator1.addVertex(-size, zIndex, -size);

        tessellator1.draw();

        // GL11.glBlendFunc(GL11.GL_DST_ALPHA, GL11.GL_ONE_MINUS_DST_ALPHA);

        GL11.glPopMatrix();
	}


	private void renderPlanetByAngle(Tessellator tessellator1, CelestialBody body, float angle, float zIndex, float scale) {

		GL11.glPushMatrix();




		//GL11.glColor4f(1.0F, 1.0F, 1.0F, 1F);   // change this for your colour
		//GL11.glLineWidth(2.0F);
		// rotate on x
		GL11.glRotatef((float) (angle/Math.PI*180), 1.0F, 0.0F, 0.0F);

		if(body.equals(AmunRa.instance.starAmun)) {
    		renderSunAura(tessellator1, new Vector3f(0.0F, 0.2F, 0.7F), scale*5, scale, zIndex-0.1F);
    	} else if(body.equals(curSystem.getMainStar())) {
			renderSunAura(tessellator1, new Vector3f(1.0F, 0.4F, 0.1F), scale*5, scale, zIndex-0.1F);
		}

		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_DST_ALPHA);
		//OpenGlHelper.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE, GL11.GL_ONE, GL11.GL_ZERO);

		//Some blanking to conceal the stars
		GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glColor4f(0.0F, 0.0F, 0.0F, 1.0F);

        tessellator1.startDrawingQuads();
        tessellator1.addVertexWithUV(-scale, 95.0F+zIndex-0.01F, -scale, 0, 0);
        tessellator1.addVertexWithUV(scale, 95.0F+zIndex-0.01F, -scale, 1, 0);
        tessellator1.addVertexWithUV(scale, 95.0F+zIndex-0.01F, scale, 1, 1);
        tessellator1.addVertexWithUV(-scale, 95.0F+zIndex-0.01F, scale, 0, 1);
        tessellator1.draw();
		// END of star concealing


		// actual planet
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1F);
		// tessellator1.setColorRGBA_F(1.0F, 1.0F, 1.0F, 1.0F);
		FMLClientHandler.instance().getClient().renderEngine.bindTexture(body.getBodyIcon());



        GL11.glTranslatef(0, 0, 0);

        tessellator1.startDrawingQuads();
        tessellator1.setColorRGBA_F(1.0F, 1.0F, 1.0F, 1.0F);
        tessellator1.addVertexWithUV(-scale, 95.0F+zIndex, -scale, 0, 0);
        tessellator1.addVertexWithUV(scale, 95.0F+zIndex, -scale, 1, 0);
        tessellator1.addVertexWithUV(scale, 95.0F+zIndex, scale, 1, 1);
        tessellator1.addVertexWithUV(-scale, 95.0F+zIndex, scale, 0, 1);


        tessellator1.draw();
        // actual planet END


        if(hasAtmosphere) {
	        GL11.glDisable(GL11.GL_TEXTURE_2D);

	        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_DST_ALPHA);

	        GL11.glColor4f(
	        		(float) planetSkyColor.xCoord,
	        		(float) planetSkyColor.yCoord,
	        		(float) planetSkyColor.zCoord, 0.4F);

	        // it could be possible to adjust the colors and uv values at specific vertices in order
	        // to simulate halfmoon etc
	        tessellator1.startDrawingQuads();
	        tessellator1.addVertexWithUV(-scale, 95.0F+zIndex+0.01F, -scale, 0, 0);
	        tessellator1.addVertexWithUV(scale, 95.0F+zIndex+0.01F, -scale, 1, 0);
	        tessellator1.addVertexWithUV(scale, 95.0F+zIndex+0.01F, scale, 1, 1);
	        tessellator1.addVertexWithUV(-scale, 95.0F+zIndex+0.01F, scale, 0, 1);
	        tessellator1.draw();
        }


		GL11.glPopMatrix();


	}

	private void renderStars()
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
