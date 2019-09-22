package edu.smith.cs.csc212.fishgrid;

public class FallingRock extends Rock {

	public FallingRock(World world)
	{
		super(world);
	}
	@Override
	public void step()
	{
		if(world.canSwim(this, getX(), getY()-1))
				{
		this.moveDown();
	}
	
}
}
