package de.katzenpapst.amunra.astronomy;

import micdoodle8.mods.galacticraft.api.galaxies.CelestialBody;
import net.minecraft.client.multiplayer.WorldClient;
import de.katzenpapst.amunra.AmunRa;

public class AstronomyHelper {
	
	
	
	public static final long yearFactor = 40000L; // technically, 8640000L would be true
	public static final long monthFactor = 192000L;

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
}
