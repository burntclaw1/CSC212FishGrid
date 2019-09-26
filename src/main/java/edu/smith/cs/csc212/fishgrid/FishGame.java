//CITATION: https://github.com/jjfiv/CSC212FishGrid. 

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
	
	List<FishFood> food; //The array of fish food that appears in the game. Here I choose a list instead of an array because fish food continues to randomly appear throughout the game and it is more convenient if the list can change size with the addition and removal of fish food so that I can reduce the use of a bunch of extraneous variables that I would need to create for an array to keep track of the positions.
	
	
	/**
	 * Number of steps!
	 */
	int stepsTaken;
	

	
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
		NUM_ROCKS=(int)(Math.random()*maxNum); //every world has a random number of rocks up to maxNum-1. I decided to do this to make each world have more variety in not just the placement of the rocks, but also the number of rocks.
		NUM_SNAILS=(int)(Math.random()*maxSnails);//same as above. I wanted each world to have more variety.
		
		missing = new ArrayList<Fish>();
		found = new ArrayList<Fish>();
		food=new ArrayList<FishFood>();
		
		// Add a home!
		home = world.insertFishHome();
		
		for (int i=0; i<NUM_ROCKS; i++) {
			world.insertRockRandomly();
		} //insert a rock randomly for the number of rocks this world has. 
		
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
	return isHome.size()==Fish.COLORS.length-1;//if the number of fish in the home is equals to the number of Fish colors-1 (i.e. the number of fish, since the player doesn't have to be returned home), then that must mean that all the missing fish were home.
		
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
		
		
		if(stepsTaken>=20)//steps specified in the assignment.
		{
			
			for(int i=found.size()-1;i>=0;i--)
			{
			if(Math.random()<=0.2&&i!=0)//There is a 20% chance that a fish far behind the player will wander off every turn. I chose 20% because it seemed like a reasonable chance that wouldn't be too absurd or too low. The condition that i!=0 is to prevent the fish that is right behind the player from getting lost, which is the fish at index i=0. Therefore, we must prevent the array from reaching that point and giving that fish a chance to become lost.
			{
				missing.add(found.remove(i));//the fish become missing again and get removed from the found list.
			}
			}
		}
		

	

		
		// If we find a fish, remove it from missing.
		for (WorldObject wo : overlap) {
			// It is missing if it's in our missing list.
			if (wo instanceof Fish&&missing.contains(wo)) {//if the object the player overlaps is a fish, then it must be a missing fish.
				// Remove this fish from the missing list.
				found.add((Fish)wo);	
				missing.remove(wo);
				score+=found.get(found.indexOf(wo)).getPoints();
				
			}
			else if (wo instanceof FishFood)//if the player overlaps with a FishFood object, however, remove the fish food and add to the player's score.
			{
				world.remove(wo);
				food.remove(wo);
				score+=10;
			}
			else if(wo instanceof FishHome)//if the player steps on the home, we must remove all the found fish into the isHome list.
			{
				while(found.size()!=0)//loop through every fish in the found list and put them in the isHome list. 
				{
						world.remove(found.get(0));//remove them from the world
						isHome.add(found.remove(0));//then add them to the isHome list
				}
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
			food.add(world.insertFoodRandomly());//for every turn, there is a 10% chance that a fish food is randomly added. After playing the game a few times, 10% was a reasonable probability that didn't overflow the world with fish food, but it also didn't make the fish food be overly exotic.
		}

		for(int i=0;i<missing.size();i++)//checking the status of each missing fish
		{
			if(missing.get(i).getX()==home.getX()&&missing.get(i).getY()==home.getY())//if the missing fish steps on the home, we need to remove it.
			{
				world.remove(missing.get(i));
				isHome.add(missing.remove(i));
				i--;//since we are using the remove() method to remove fish in the array we are looping over, we must subtract 1 from the iterator and prevent it from skipping the next fish in line if we do remove a fish from the array.
			}
			for(int j=0;j<food.size();j++)//now, for every fish, we must check every fish food and see if it is overlapping with the fishfood.
			{
			if(missing.get(i).getX()==food.get(j).getX()&&missing.get(i).getY()==food.get(j).getY())
			{
				world.remove(food.get(j));//if it is, remove the fishfood.
				food.remove(food.get(j));//remove that food from the fishfood.
				j--;//again, to prevent us from skipping over any fishfood. 
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
			if(lost.isScared())//if the fish is scared, the fish has an 80% probability of moving randomly.
				
			{
				if(rand.nextDouble() < 0.8)
				{
					lost.moveRandomly();
				}
			}
			else //a lazy fish will only have a 30% chance of doing so.
				{
				if(rand.nextDouble() < 0.3)
					lost.moveRandomly();
				}				
			}
		}
	

	/**
	 * This gets a click on the grid. We want it to destroy rocks that ruin the game.
	 * @param x - the x-tile.
	 * @param y - the y-tile.
	 */
	public void click(int x, int y) {
		System.out.println("Clicked on: "+x+","+y+ " world.canSwim(player,...)="+world.canSwim(player, x, y));
		List<WorldObject> atPoint = world.find(x, y);
for(int i=0;i<atPoint.size();i++) //for every object that is on the tile that the player clicked on
{
	if(atPoint.get(i) instanceof Rock)//check to see if there are rocks that the player clicked on.
	world.remove(atPoint.get(i));//if so, remove it from the world.
}
	}
	
}
