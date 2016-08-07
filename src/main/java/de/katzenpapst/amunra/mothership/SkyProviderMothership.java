package de.katzenpapst.amunra.mothership;

import java.util.Random;

import org.lwjgl.opengl.GL11;

import de.katzenpapst.amunra.AmunRa;
import de.katzenpapst.amunra.world.SkyProviderDynamic;
import de.katzenpapst.amunra.world.SkyProviderDynamic.BodyRenderTask;
import micdoodle8.mods.galacticraft.api.galaxies.CelestialBody;
import micdoodle8.mods.galacticraft.api.galaxies.Moon;
import micdoodle8.mods.galacticraft.api.galaxies.Planet;
import micdoodle8.mods.galacticraft.api.galaxies.Star;
import micdoodle8.mods.galacticraft.api.world.IGalacticraftWorldProvider;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.renderer.GLAllocation;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.MathHelper;

public class SkyProviderMothership extends SkyProviderDynamic {

    protected CelestialBody mothershipParent;
    protected boolean isInTransit = false;

    protected float transitOffset = 0;
    protected long curWorldTime = -1;

    protected final double skyBoxLength = 100.0D;
    protected final double cylinderLength = skyBoxLength * 12;
    protected final double angleWidth   = 0.5D/skyBoxLength;
    protected final int numStarLines;



    protected final float starLineSpeed = 20;


    public SkyProviderMothership(IGalacticraftWorldProvider worldProvider) {
        super(worldProvider);
        numStarLines = AmunRa.instance.confMothershipStarLines;

        hasHorizon = false;
    }

    protected void initStarLines(int list, double radius) {

        GL11.glPushMatrix();
        GL11.glNewList(list, GL11.GL_COMPILE);

        final Random rand = new Random(); // 10842L
        final Tessellator tess = Tessellator.instance;
        tess.startDrawingQuads();

        double size = 0.5D;
        double skyRadius = 100.0D;
        for (int starIndex = 0; starIndex < 400; ++starIndex)
        {

            double theta        = rand.nextDouble()*Math.PI*2;
            double angleWidth   = size/skyRadius; // should be


            double x1 = Math.cos(theta-angleWidth)*skyRadius;
            double y1 = Math.sin(theta-angleWidth)*skyRadius;
            double x2 = Math.cos(theta+angleWidth)*skyRadius;
            double y2 = Math.sin(theta+angleWidth)*skyRadius;
            double zBase = (rand.nextDouble()*skyRadius*2)-skyRadius;
            double length = rand.nextDouble()*20.0D;

            // DC is in front

            // paint
            // A
            tess.addVertex(x1, y1, zBase);
            // B
            tess.addVertex(x1, y1, zBase+length);
            // C
            tess.addVertex(x2, y2, zBase+length);
            // D
            tess.addVertex(x2, y2, zBase);
        }

        tess.draw();


        GL11.glEndList();
        GL11.glPopMatrix();

    }

    /*protected void renderMothershipParent() {
        double distanceToParent = 1;


        float zIndex = (float) (20/distanceToParent);
        float distance = (float) (curBodyPlanet.getRelativeSize() / distanceToParent);
        // my parent
        this.nearBodiesToRender.add(
            new BodyRenderTask(curBodyPlanet, 0, zIndex, distance, (float) Math.PI)
        );
    }*/

    private float fixAngle(float angle) {
        while(angle > Math.PI*2) {
            angle -= Math.PI*2;
        }
        while(angle < 0) {
            angle += Math.PI*2;
        }
        return angle;
    }

    @Override
    protected void renderSystem(float partialTicks, WorldClient world, Tessellator tess, Minecraft mc) {
        super.renderSystem(partialTicks, world, tess, mc);


        // now do the planet we are orbiting

        GL11.glPushMatrix();
        // rotate back
        if(this.rType != RenderType.STAR && !AmunRa.instance.confSunColorMap.containsKey(mothershipParent.getName())) {
            GL11.glRotatef(180-(currentCelestialAngle * 360), 1.0F, 0.0F, 0.0F);
            renderPlanetByAngle(tess, mothershipParent, 0, 20, 60, fixAngle((float) (-currentCelestialAngle*Math.PI*2+Math.PI)));
        } else {
            GL11.glRotatef(-currentCelestialAngle * 360, 1.0F, 0.0F, 0.0F);
            renderPlanetByAngle(tess, mothershipParent, 0, 20, 60, 0);
        }

        GL11.glPopMatrix();
    }
/*
    @Override
    protected void prepareSystemForRender(long curWorldTime, float partialTicks) {
        super.prepareSystemForRender(curWorldTime, partialTicks);
        renderMothershipParent();
    }*/

    @Override
    protected void initVars() {

        if(((Mothership)curBody).isInTransit()) {
            curBodyPlanet = null;
            curSystem = null;
            mothershipParent = null;
            isInTransit = true;
            curWorldTime = -1;
        } else {
            mothershipParent = ((Mothership)curBody).getParent();
            if(mothershipParent instanceof Planet) {
                // pretend we are the planet itself
                this.rType = RenderType.PLANET;
                curBodyPlanet = ((Mothership)curBody).getParent();
                curSystem = ((Planet)mothershipParent).getParentSolarSystem();

                // but use the distance from the planet
                curBodyDistance = mothershipParent.getRelativeDistanceFromCenter().unScaledDistance;

                this.sunSize = 1.0F / curBodyDistance;
            } else if(mothershipParent instanceof Moon) {
                // pretend we are a sibling moon
                this.rType = RenderType.MOON;
                curBodyPlanet = ((Moon)mothershipParent).getParentPlanet();
                curSystem = ((Moon)mothershipParent).getParentPlanet().getParentSolarSystem();

                curBodyDistance = curBodyPlanet.getRelativeDistanceFromCenter().unScaledDistance;

                this.sunSize = 1.0F / curBodyDistance;
            } else if(mothershipParent instanceof Star) {
                // pretend we are a star?
                this.rType = RenderType.STAR;
                curBodyPlanet = curBody;
                curSystem = ((Star)mothershipParent).getParentSolarSystem();
                curBodyDistance = curBody.getRelativeDistanceFromCenter().unScaledDistance;

                this.sunSize = 5;
            }

            isInTransit = false;
        }

        this.hasAtmosphere = false;

    }

    @Override
    public void render(float partialTicks, WorldClient world, Minecraft mc) {
        if(isInTransit != ((Mothership)curBody).isInTransit()) {
            initVars();
        }

        if(!isInTransit) {
            super.render(partialTicks, world, mc);
        } else {
            renderTransitSky(partialTicks, world, mc);
        }
    }


    protected void renderTransitSky(float partialTicks, WorldClient world, Minecraft mc) {

        // try stuff
        if(curWorldTime == -1) {
            curWorldTime = world.getWorldTime();
        }
        // I need the actual time here
        transitOffset = (partialTicks+world.getWorldTime() - curWorldTime);

        // renderStars(0);

        //renderStarLines(speedSlow, renderListStarLinesSlow);
        // renderStarLines(speedMedium, renderListStarLinesMedium);
        //renderStarLines(speedFast, renderListStarLinesFast);
        renderStarLines(transitOffset);


    }

    protected void renderStarLines(float curTime) {
        //// BEGIN

        final Random starLineRand = new Random(10842L);
        GL11.glPushMatrix();


        final Tessellator tess = Tessellator.instance;
        tess.startDrawingQuads();


        for (int starIndex = 0; starIndex < 400; ++starIndex)
        {

            double theta        = starLineRand.nextDouble()*Math.PI*2;

            double x1 = 0;
            double y1 = 0;
            double x2 = 0;
            double y2 = 0;

            double zBase = MathHelper.getRandomDoubleInRange(starLineRand, 0, cylinderLength*2);



            // motion offset
            zBase += curTime*this.starLineSpeed;
            zBase = (zBase % (cylinderLength*2)) - cylinderLength;


            x1 = Math.cos(theta-angleWidth)*skyBoxLength;
            y1 = Math.sin(theta-angleWidth)*skyBoxLength;
            x2 = Math.cos(theta+angleWidth)*skyBoxLength;
            y2 = Math.sin(theta+angleWidth)*skyBoxLength;
            double length = starLineRand.nextDouble()*20.0D;

            // project the lines onto the cylinder's circles if necessary
            if(zBase < -skyBoxLength || zBase+length > skyBoxLength) {
                // zBase = skyRadius
                x1 = x1/zBase*skyBoxLength;
                y1 = y1/zBase*skyBoxLength;
                x2 = x2/zBase*skyBoxLength;
                y2 = y2/zBase*skyBoxLength;
                // zBase = skyRadius;
            }


            // paint
            // A
            tess.addVertex(x1, y1, zBase);
            // B
            tess.addVertex(x1, y1, zBase+length);
            // C
            tess.addVertex(x2, y2, zBase+length);
            // D
            tess.addVertex(x2, y2, zBase);
        }

        tess.draw();

        GL11.glPopMatrix();
        //// END
    }
}
