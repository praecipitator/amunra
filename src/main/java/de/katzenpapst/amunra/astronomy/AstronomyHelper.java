package de.katzenpapst.amunra.astronomy;

import de.katzenpapst.amunra.mothership.Mothership;
import micdoodle8.mods.galacticraft.api.galaxies.CelestialBody;
import micdoodle8.mods.galacticraft.api.galaxies.IChildBody;
import micdoodle8.mods.galacticraft.api.galaxies.Planet;
import micdoodle8.mods.galacticraft.api.galaxies.SolarSystem;
import micdoodle8.mods.galacticraft.api.galaxies.Star;
import micdoodle8.mods.galacticraft.api.vector.Vector3;

public class AstronomyHelper {

    public static final long yearFactor = 8640000L; // technically, 8640000L would be true
    public static final long monthFactor = 192000L;

    // try doing this in AUs
    public static final double moonDistanceFactor   =  0.00015F;
    // earth<-->sun = 1 -> 149598023 => 39
    public static final double planetDistanceFactor =  1.0F;
    // sun<-->ra = 1069,17 -> 1841206437 => 480
    public static final double systemDistanceFactor = 12.3F;


    /**
     * Get angle and size of otherBody in curBody's sky
     *
     * @param curBody
     * @param otherBody
     * @param partialTicks
     * @param world
     * @return
     */
    public static AngleDistance projectBodyToSky(CelestialBody curBody, CelestialBody otherBody, float partialTicks, long worldTime) {
        //long curWorldTime = world.getWorldTime();

        double curBodyDist = curBody.getRelativeDistanceFromCenter().unScaledDistance;
        double otherBodyDist = otherBody.getRelativeDistanceFromCenter().unScaledDistance;

        double curBodyOrbitalAngle = getOrbitalAngle(
                curBodyDist,
                curBody.getPhaseShift(),
                worldTime , partialTicks, yearFactor);

        double otherBodyOrbitalAngle = getOrbitalAngle(
                otherBody.getRelativeOrbitTime(),
                otherBody.getPhaseShift(),
                worldTime, partialTicks, yearFactor);

        // make it relative to my own angle
        otherBodyOrbitalAngle -= (Math.PI*2-curBodyOrbitalAngle);

        // angle between connection line curBody<-->sun and amun<-->sun
        double innerAngle = Math.PI-otherBodyOrbitalAngle;

        // distance between curBody<-->planet, also needed for scaling
        float distanceToPlanet = (float) getDistanceBetweenBodies(innerAngle, curBodyDist, otherBodyDist);

        float projectedAngle = (float) projectAngle(innerAngle, curBodyDist, otherBodyDist, distanceToPlanet);

        return new AngleDistance(projectedAngle, distanceToPlanet);
    }

    ////// helper functions for celestial body angle calculation //////
    /**
     * Returns the current angle of a celestial body
     *
     * @param relOrbitTime	property of celestialbody
     * @param phaseShift	property of celestialbody
     * @param worldTime		current total world time
     * @param partialTicks	current partialticks
     * @param orbitFactor	theoretically this should be one overworld year
     * @return
     */
    public static double getOrbitalAngle(double relOrbitTime, double phaseShift, long worldTime, double partialTicks, double orbitFactor) {


        double curYearLength = relOrbitTime * orbitFactor;
        int j = (int)(worldTime % (long)curYearLength);
        double orbitPos = (j + partialTicks) / curYearLength - 0.25F;
        return orbitPos*2*Math.PI + phaseShift;
    }

    /**
     * Calculates the distance between two bodies from their distances from the sun/parent planet and
     * the angle between them
     *
     * @param innerAngle
     * @param body1distance
     * @param body2distance
     * @return
     */
    public static double getDistanceBetweenBodies(double innerAngle, double body1distance, double body2distance) {
        return Math.sqrt(
                Math.pow(body2distance, 2) +
                Math.pow(body1distance, 2) -
                2 * body2distance * body1distance * Math.cos(innerAngle));
    }

    /**
     * Calculate the angle at which body2 is in body1's sky if both orbit the same parent
     *
     *
     * @param innerAngle			in radians, the angle between curBody<-->sun and otherBody<-->sun
     * @param body1distance			this body's orbital radius
     * @param body2distance			other body's orbital radius
     * @param distanceBetweenBodies	distance between the two, can be calculated by getDistanceBetweenBodies
     * @return
     */
    public static double projectAngle(double innerAngle, double body1distance, double body2distance, double distanceBetweenBodies) {
        // omg now do dark mathemagic


        double sinBeta = Math.sin(innerAngle);

        // distFromThisToOtherBody = x
        // curBodyDistance = d
        // otherBodyDistance = r

        // gamma
        double angleAroundCurBody = Math.asin(
                body2distance * sinBeta / distanceBetweenBodies
                );

        if ( body1distance > body2distance) {
            return angleAroundCurBody;
        }

        // now fix this angle...
        // for this, I need the third angle, too
        double delta = Math.asin(sinBeta / distanceBetweenBodies * body1distance);


        double angleSum = innerAngle+delta+angleAroundCurBody;
        double otherAngleSum =innerAngle+delta+(Math.PI-angleAroundCurBody);
        if(Math.abs(Math.abs(angleSum)/Math.PI - 1) < 0.001) {
            // aka angleSUm = 180 or -180
            return angleAroundCurBody;
        } else {
            return Math.PI-angleAroundCurBody;
        }
    }

    /**
     * Finds the closest common parent of the given bodies. For two moons, it should be their parent planet,
     * for a planet and it's moon the planet, for two planets in the same solar system or two moons of different planetes
     * in the same system the star, for objects of different systems null.
     * @param body1
     * @param body2
     * @return
     */
    public static CelestialBody getClosestCommonParent(CelestialBody body1, CelestialBody body2) {

        // trivial cases
        if(body1 == null || body2 == null) {
            return null;
        }
        if(body1.equals(body2)) {
            return body1;
        }


        // death by recursion!

        // motherships
        if(body1 instanceof Mothership && !(body2 instanceof Mothership)) {

            return getClosestCommonParent(((Mothership)body1).getParent(), body2);

        } else if(body2 instanceof Mothership && !(body1 instanceof Mothership)) {

            return getClosestCommonParent(body1, ((Mothership)body2).getParent());

        } else if(body1 instanceof Mothership && body2 instanceof Mothership) {

            return getClosestCommonParent(((Mothership)body1).getParent(), ((Mothership)body2).getParent());

        }

        // child bodies
        if(body1 instanceof IChildBody && !(body2 instanceof IChildBody)) {

            return getClosestCommonParent(((IChildBody)body1).getParentPlanet(), body2);

        } else if(body2 instanceof IChildBody && !(body1 instanceof IChildBody)) {

            return getClosestCommonParent(body1, ((IChildBody)body2).getParentPlanet());

        } else if(body1 instanceof IChildBody && body2 instanceof IChildBody) {

            return getClosestCommonParent(((IChildBody)body1).getParentPlanet(), ((IChildBody)body2).getParentPlanet());

        }

        // planets
        if(body1 instanceof Planet && !(body2 instanceof Planet)) {

            return getClosestCommonParent(((Planet)body1).getParentSolarSystem().getMainStar(), body2);

        } else if(body2 instanceof Planet && !(body1 instanceof Planet)) {

            return getClosestCommonParent(body1, ((Planet)body2).getParentSolarSystem().getMainStar());

        } else if(body1 instanceof Planet && body2 instanceof Planet) {

            return getClosestCommonParent(((Planet)body1).getParentSolarSystem().getMainStar(), ((Planet)body2).getParentSolarSystem().getMainStar());

        }

        // if we came down here, then body1 and body2 should be stars. if both things are from the same system, then the equals up there should have
        // taken care of that already.

        return null;
    }

    public static CelestialBody getCelestialBodyParent(CelestialBody body) {
        if(body instanceof Planet) {
            return ((Planet)body).getParentSolarSystem().getMainStar();
        } else if(body instanceof IChildBody) {
            return ((IChildBody)body).getParentPlanet();
        } else if(body instanceof Mothership) {
            return ((Mothership)body).getParent();
        } else if(body instanceof Star) {
            return body;
        }
        return null;
    }

    public static SolarSystem getSolarSystem(CelestialBody body) {
        if(body instanceof Star) {
            return ((Star)body).getParentSolarSystem();
        }
        if(body instanceof Planet) {
            return ((Planet)body).getParentSolarSystem();
        }
        if(body instanceof IChildBody) {
            return ((IChildBody)body).getParentPlanet().getParentSolarSystem();
        }
        if(body instanceof Mothership) {
            return getSolarSystem(((Mothership)body).getDestination());
        }
        return null;
    }

    /**
     *
     * @param parent
     * @param child
     * @return
     */
    private static double getDistanceToParent(CelestialBody parent, CelestialBody child) {
        double result = 0;
        while(!parent.equals(child)) {
            if(child instanceof IChildBody) {
                result += child.getRelativeDistanceFromCenter().unScaledDistance*moonDistanceFactor;
            } else if(child instanceof Planet) {
                result += child.getRelativeDistanceFromCenter().unScaledDistance*planetDistanceFactor;
            }
            child = getCelestialBodyParent(child);
        }

        return result;
    }

    /**
     * Calculates the distance between two bodies, for mothership travelling mostly
     *
     * @param body1
     * @param body2
     * @return
     */
    public static double getDistance(CelestialBody body1, CelestialBody body2) {

        if(body1.equals(body2)) {
            return 0;
        }
        if(body1 instanceof Mothership) {
            body1 = getCelestialBodyParent(body1);
        }
        if(body2 instanceof Mothership) {
            body2 = getCelestialBodyParent(body2);
        }
        // earth<-->moon = 13 -> 384399
        // earth<-->sun = 1 -> 149598023 => 39
        // (neptune<-->sun = 2.25F -> 4504454366964901)
        // sun<-->ra = 1069,17 -> 1841206437 => 480

        CelestialBody commonParent = getClosestCommonParent(body1, body2);
        if(commonParent == null) {
            // different solar systems
            SolarSystem sys1 = getSolarSystem(body1);
            SolarSystem sys2 = getSolarSystem(body2);
            Vector3 pos1 = sys1.getMapPosition();
            Vector3 pos2 = sys2.getMapPosition();
            double result = 0;
            result = pos1.distance(pos2)*systemDistanceFactor;
            result += getDistanceToParent(sys1.getMainStar(), body1);
            result += getDistanceToParent(sys2.getMainStar(), body2);
            return result;
        } else {
            // check if one of the bodies is the parent
            if(body1.equals(commonParent)) {
                return getDistanceToParent(commonParent, body2);
            } else if(body2.equals(commonParent)) {
                return getDistanceToParent(commonParent, body1);
            }

            if(body1 instanceof IChildBody && body2 instanceof IChildBody) {
                // are the two siblings?
                if(commonParent instanceof Planet) {
                    return Math.abs(body1.getRelativeDistanceFromCenter().unScaledDistance-body2.getRelativeDistanceFromCenter().unScaledDistance)*moonDistanceFactor;
                }
            }

            // here we either are different planets of the same system, or orbit them
            double result = 0;
            if(body1 instanceof IChildBody) {
                result += body1.getRelativeDistanceFromCenter().unScaledDistance*moonDistanceFactor;
                body1 = getCelestialBodyParent(body1);
            }
            if(body2 instanceof IChildBody) {
                result += body2.getRelativeDistanceFromCenter().unScaledDistance*moonDistanceFactor;
                body2 = getCelestialBodyParent(body2);
            }

            // at this point, we have to be planets

            // are we sibling planets?
            //if(body1 instanceof Planet && body2 instanceof Planet) {
                float dist1 = body1.getRelativeDistanceFromCenter().unScaledDistance;
                float dist2 = body2.getRelativeDistanceFromCenter().unScaledDistance;

                result += Math.abs(dist1-dist2)*planetDistanceFactor;

            //}
            return result;
        }


    }

}
