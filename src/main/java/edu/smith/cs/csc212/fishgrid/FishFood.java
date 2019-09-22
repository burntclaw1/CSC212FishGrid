package edu.smith.cs.csc212.fishgrid;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.RoundRectangle2D;

public class FishFood extends WorldObject {
	
	private Color color=Color.orange;

	public FishFood(World world)
	{
		super(world);
	}
	
	public void draw(Graphics2D g) {
		g.setColor(color);
		RoundRectangle2D food = new RoundRectangle2D.Double(-.5,-.5,1,1,0.3,0.3);
		g.fill(food);
	}
	
	public void step()
	{
		
	}
}
