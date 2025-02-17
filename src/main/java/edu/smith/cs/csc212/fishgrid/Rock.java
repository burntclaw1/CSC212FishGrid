//CITATION: https://github.com/jjfiv/CSC212FishGrid. 

package edu.smith.cs.csc212.fishgrid;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.RoundRectangle2D;

/**
 * It would be awful nice to have multi-colored rocks at random.
 * This is not <a href="https://en.wikipedia.org/wiki/Dwayne_Johnson">the Rock</a>, but a Rock.
 * @author jfoley
 */
public class Rock extends WorldObject {
	/**
	 * I took these colors from Wikipedia's Cool and Warm Gray sections.
	 * https://en.wikipedia.org/wiki/Shades_of_gray#Cool_grays
	 * https://en.wikipedia.org/wiki/Shades_of_gray#Warm_grays
	 */
	private static Color[] ROCK_COLORS = new Color[] {
			new Color(144,144,192),
			new Color(145,163,176),
			new Color(112,128,144),
			new Color(94,113,106),
			new Color(76,88,102),
			new Color(170,152,169),
			new Color(152,129,123),
			new Color(138,129,141),
			new Color(72,60,50)
	};
	
	private Color color;
	/**
	 * Construct a Rock in our world.
	 * @param world - the grid world.
	 */
	public Rock(World world) {
		super(world);
		int index=(int)(Math.random()*ROCK_COLORS.length);//It is Math.random()*ROCK_COLORS.length and not length-1 because Math.random() returns a number from 0 to 1 exclusive of 1, meaning that Math.random()*length will never return the length of the array. When it is casted as an integer, the way java rounds integers causes the number to range from 0 to ROCK_COLORS.length-1, which is exactly from the first element 0 to the last element ROCK_COLORS.length
		color=ROCK_COLORS[index];//this rock object therefore is assigned a  color value from a randomly generated position in the array.
	}

	/**
	 * Draw a rock!
	 */
	@Override
	public void draw(Graphics2D g) {
		g.setColor(color);//Set the color now to the newly declared instance variable color.
		RoundRectangle2D rock = new RoundRectangle2D.Double(-.5,-.5,1,1,0.3,0.3);
		g.fill(rock);
	}

	@Override
	public void step() {
		// Rocks don't actually *do* anything.		
	}

}
