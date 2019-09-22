package edu.smith.cs.csc212.fishgrid;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

/**
 * This class manages our model of gameplay: missing and found fish, etc.
 * @author jfoley
 *
 */
public class FishGame {
	/**
	 * This is the world in which the fish are missing. (It's mostly a List!).
	 */
	World world;
	/**
	 * The player (a Fish.COLORS[0]-colored fish) goes seeking their friends.
	 */
	Fish player;
	/**
	 * The home location.
	 */
	FishHome home;
	/**
	 * These are the missing fish!
	 */
	List<Fish> missing;
	
	/**
	 * These are fish we've found!
	 */
	List<Fish> found;
	
	List<FishFood> food;
	
	
	/**
	 * Number of steps!
	 */
	int stepsTaken;
	
	int i=0;

	
	private ArrayList<Fish> isHome;
	
	/**
	 * Score!
	 */
	int score;
	
	private int NUM_ROCKS;
	private int NUM_SNAILS;
	
	/**
	 * Create a FishGame of a particular size.
	 * @param w how wide is the grid?
	 * @param h how tall is the grid?
	 */
	public FishGame(int w, int h, int maxNum, int maxSnails) {
		world = new World(w, h);
		NUM_ROCKS=(int)(Math.random()*maxNum);
		NUM_SNAILS=(int)(Math.random()*maxSnails);
		
		missing = new ArrayList<Fish>();
		found = new ArrayList<Fish>();
		food=new ArrayList<FishFood>();
		
		// Add a home!
		home = world.insertFishHome();
		
		for (int i=0; i<NUM_ROCKS; i++) {
			world.insertRockRandomly();
		}
		
		for(int i=0;i<NUM_SNAILS;i++)
		{
			world.insertSnailRandomly();
		}
				
		// Make the player out of the 0th fish color.
		player = new Fish(0, world, Math.random());
		// Start the player at "home".
		player.setPosition(home.getX(), home.getY());
		player.markAsPlayer();
		world.register(player);
		
		// Generate fish of all the colors but the first into the "missing" List.
		for (int ft = 1; ft < Fish.COLORS.length; ft++) {
			Fish friend = world.insertFishRandomly(ft);
			missing.add(friend);
		}
		
		isHome=new ArrayList<Fish>();
	}
	
	
	/**
	 * How we tell if the game is over: if missingFishLeft() == 0.
	 * @return the size of the missing list.
	 */
	public int missingFishLeft() {
		return missing.size();
	}
	
	/**
	 * This method is how the Main app tells whether we're done.
	 * @return true if the player has won (or maybe lost?).
	 */
	public boolean gameOver() {
		// TODO(FishGrid) We want to bring the fish home before we win!
	return isHome.size()==Fish.COLORS.length-1;//all except for the player
		
}

	/**
	 * Update positions of everything (the user has just pressed a button).
	 */
	public void step() {
		// Keep track of how long the game has run.
		this.stepsTaken += 1;
				
		// These are all the objects in the world in the same cell as the player.
		List<WorldObject> overlap = this.player.findSameCell();
		// The player is there, too, let's skip them.
		overlap.remove(this.player);
		
		
		if(stepsTaken>=20)
		{
			
			for(int i=found.size()-1;i>=0;i--)
			{
			if(Math.random()<=0.2&&i!=0)//prevent the first fish from getting lost. 
			{
				missing.add(found.remove(found.size()-1));
			}
			}
		}
		
		if(this.player.getX()==home.getX()&&this.player.getY()==home.getY())
		{
			while(found.size()!=0)
			{
					world.remove(found.get(0));
					isHome.add(found.remove(0));
			}
		}
	

		
		// If we find a fish, remove it from missing.
		for (WorldObject wo : overlap) {
			// It is missing if it's in our missing list.
			if (wo instanceof Fish&&missing.contains(wo)) {
				// Remove this fish from the missing list.
				found.add((Fish)wo);	
				missing.remove(wo);
				score+=found.get(found.indexOf(wo)).getPoints();
				
			}
			else if (wo instanceof FishFood)
			{
				world.remove(wo);
				food.remove(wo);
				score+=10;
			}
			
		}

		System.out.println(isHome.size());
		// Make sure missing fish *do* something.
		wanderMissingFish();
		// When fish get added to "found" they will follow the player around.
		World.objectsFollow(player, found);
		// Step any world-objects that run themselves.
		world.stepAll();
		
		if(Math.random()<0.1)
		{
			food.add(world.insertFoodRandomly());
		}
		
		for(int i=0;i<missing.size();i++)
		{
			if(missing.get(i).getX()==home.getX()&&missing.get(i).getY()==home.getY())
			{
				world.remove(missing.get(i));
				isHome.add(missing.remove(i));
				i--;
			}
		}
		//broken
		for(int i=0;i<missing.size();i++)
		{
			for(int j=0;j<food.size();j++)
			{
			if(missing.get(i).getX()==food.get(j).getX()&&missing.get(i).getY()==food.get(j).getY())
			{
				world.remove(food.get(j));
				food.remove(food.get(j));
				j--;
			}
			}
		}
	
	}
	
	/**
	 * Call moveRandomly() on all of the missing fish to make them seem alive.
	 */
	private void wanderMissingFish() {
		Random rand = ThreadLocalRandom.current();
		for (Fish lost : missing) {
			// 30% of the time, lost fish move randomly.
			if(lost.isScared())
				
			{
				if(rand.nextDouble() < 0.8)
				{
					lost.moveRandomly();
				}
			}
			else 
				{
				if(rand.nextDouble() < 0.3)
					lost.moveRandomly();
				}
				// TODO(lab): What goes here?
				
			}
		}
	

	/**
	 * This gets a click on the grid. We want it to destroy rocks that ruin the game.
	 * @param x - the x-tile.
	 * @param y - the y-tile.
	 */
	public void click(int x, int y) {
		// TODO(FishGrid) use this print to debug your World.canSwim changes!
		System.out.println("Clicked on: "+x+","+y+ " world.canSwim(player,...)="+world.canSwim(player, x, y));
		List<WorldObject> atPoint = world.find(x, y);
		// TODO(FishGrid) allow the user to click and remove rocks.
for(int i=0;i<atPoint.size();i++)
{
	if(atPoint.get(i) instanceof Rock)
	world.remove(atPoint.get(i));
}
	}
	
}
