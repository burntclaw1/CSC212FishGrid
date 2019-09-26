//CITATION: https://github.com/jjfiv/CSC212FishGrid. 

package edu.smith.cs.csc212.fishgrid;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.Ellipse2D;

/**
 * Most Fish behavior lives up in WorldObject (a Fish just looks special!).
 * Or it is in Main, where the missing/found and player fish all act different!
 * 
 * @author jfoley
 */
public class Fish extends WorldObject {
	/**
	 * A fish is only special because of its color now!
	 */
	static Color[] COLORS = {
			Color.red,
			Color.green,
			Color.yellow,
			Color.gray,
			Color.pink
	};
	/**
	 * This is an index into the {@link #COLORS} array.
	 */
	int color;
	/**
	 * Whether or not this is the player;
	 */
	boolean player = false;
	
	private boolean isScared; //whether or not the fish in question is scared or not. Each fish is either isScared or !isScared, hence why I put it as an instance variable.
	/**
	 * Called only on the Fish that is the player!
	 */
	public void markAsPlayer() {
		this.player = true;
	}

	private int points;

	/**
	 * A Fish knows what World it belongs to, because all WorldObjects do.
	 * @param color Color by number.
	 * @param world The world itself.
	 */
	public Fish(int color, World world, double isScared) {//here, isScared is a parameter of type double because I want each fish to be randomized as isScared, and Math.random(), which returns a double, is a good means of doing this.
		super(world);
		this.color = color;
		if(isScared<0.5)//half of the time, the parameter Math.random() will return a double less than 0.5. This means that half of the time, the fish are scared and half of the time, the fish will be lazy.
		{
		this.isScared=false;
		}
		else
		{
			this.isScared=true;
		}
		if(COLORS[color]==Color.yellow)//defining the points collected by the player depending on the color of the fish.
		{
			points=50;
		}
		else if(COLORS[color]==Color.green)
		{
			points=10;
		}
		else if(COLORS[color]==Color.gray)
		{
			points=20;
		}
		else if(COLORS[color]==Color.pink)
		{
			points=40;
		}
		else
		{
			points=10;
		}
	}
	
	/**
	 * What actual color is this fish? We store an index, so get it here.
	 * @return the Color object from our array.
	 */
	public Color getColor() {
		return COLORS[this.color];
	}
	
	/**
	 * Animate our fish by facing left and then right over time.
	 */
	private int dt = 0;
	
	/**
	 * Go ahead and ignore this method if you're not into graphics.
	 * We use "dt" as a trick to make the fish change directions every second or so; this makes them feel a little more alive.
	 */
	@Override
	public void draw(Graphics2D g) {
		dt += 1;
		if (dt > 100) {
			dt = 0;
		}
		Shape circle = new Ellipse2D.Double(-0.6, -0.6, 1.2, 1.2);
		Shape body = new Ellipse2D.Double(-.40, -.2, .8, .4);
		Shape tail = new Ellipse2D.Double(+.2, -.3, .2, .6);
		Shape eye = new Ellipse2D.Double(-.25, -.1, .1, .1);
		
		Color color = getColor();//Selects the color by pulling out the color in the COLORS array at the index color. 
		Color tailColor = color.darker();

		
		Graphics2D flipped = (Graphics2D) g.create();
		if (dt < 50) {
			flipped.scale(-1, 1);
		}
		
		if (this.player) {
			flipped.setColor(new Color(1f,1f,1f,0.5f));
			flipped.fill(circle);
		}

		// Draw the fish of size (1x1, roughly, at 0,0).
		flipped.setColor(color);
		flipped.fill(body);

		flipped.setColor(Color.black);
		flipped.fill(eye);

		// draw tail:
		flipped.setColor(tailColor);
		flipped.fill(tail);
		
		flipped.dispose();
	}
	
	@Override
	public void step() {
		// Fish are controlled at a higher level; see FishGame.
	}
	//getters.
	public boolean isScared()
	{
		return isScared;
	}

	public int getPoints() {
		return points;
	}
}
