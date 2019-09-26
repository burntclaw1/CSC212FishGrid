//CITATION: https://github.com/jjfiv/CSC212FishGrid. 

package edu.smith.cs.csc212.fishgrid;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

import me.jjfoley.gfx.IntPoint;

/**
 * A World is a 2d grid, represented as a width, a height, and a list of WorldObjects in that world.
 * @author jfoley
 *
 */
public class World {
	/**
	 * The size of the grid (x-tiles).
	 */
	private int width;
	/**
	 * The size of the grid (y-tiles).
	 */
	private int height;
	/**
	 * A list of objects in the world (Fish, Snail, Rock, etc.).
	 */
	private List<WorldObject> items;
	/**
	 * A reference to a random object, so we can randomize placement of objects in this world.
	 */
	private Random rand = ThreadLocalRandom.current();
	
	private Fish[] home;

	/**
	 * Create a new world of a given width and height.
	 * @param w - width of the world.
	 * @param h - height of the world.
	 */
	public World(int w, int h) {
		items = new ArrayList<>();
		width = w;
		height = h;
	}

	/**
	 * What is under this point?
	 * @param x - the tile-x.
	 * @param y - the tile-y.
	 * @return a list of objects!
	 */
	public List<WorldObject> find(int x, int y) {
		List<WorldObject> found = new ArrayList<>();
		
		// Check out every object in the world to find the ones at a particular point.
		for (WorldObject w : this.items) {
			// But only the ones that match are "found".
			if (x == w.getX() && y == w.getY()) {
				found.add(w);
			}
		}
		
		// Give back the list, even if empty.
		return found;
	}
	
	
	/**
	 * This is used by PlayGame to draw all our items!
	 * @return the list of items.
	 */
	public List<WorldObject> viewItems() {
		// Don't let anybody add to this list!
		// Make them use "register" and "remove".

		// This is kind of an advanced-Java trick to return a list where add/remove crash instead of working.
		return Collections.unmodifiableList(items);
	}

	/**
	 * Add an item to this World.
	 * @param item - the Fish, Rock, Snail, or other WorldObject.
	 */
	public void register(WorldObject item) {
		// Print out what we've added, for our sanity.
		System.out.println("register: "+item.getClass().getSimpleName());
		items.add(item);
	}
	
	/**
	 * This is the opposite of register. It removes an item (like a fish) from the World.
	 * @param item - the item to remove.
	 */
	public void remove(WorldObject item) {
		// Print out what we've removed, for our sanity.
		System.out.println("remove: "+item.getClass().getSimpleName());
		items.remove(item);
	}
	
	/**
	 * How big is the world we model?
	 * @return the width.
	 */
	public int getWidth() {
		return width;
	}
	/**
	 * How big is the world we model?
	 * @return the height.
	 */
	public int getHeight() {
		return height;
	}
	
	/**
	 * Try to find an unused part of the World for a new object!
	 * @return a point (x,y) that has nothing else in the grid.
	 */
	public IntPoint pickUnusedSpace() {
		// Build a set of all available spaces:
		Set<IntPoint> available = new HashSet<>();
		for (int x=0; x<getWidth(); x++) {
			for (int y=0; y<getHeight(); y++) {
				available.add(new IntPoint(x, y));
			}
		}
		// Remove any spaces that are in use:
		for (WorldObject item : this.items) {
			available.remove(item.getPosition());
		}

		// If we get here, we have too much stuff.
		// Let's crash our Java program!
		if (available.size() == 0) {
			throw new IllegalStateException("The world is too small! Trying to pick an unused space but there's nothing left.");
		}

		// Return an unused space at random: Need to copy to a list since sets do not have orders.
		List<IntPoint> unused = new ArrayList<>(available);
		int which = rand.nextInt(unused.size());
		return unused.get(which);
	}
	
	/**
	 * Insert an item randomly into the grid.
	 * @param item - the rock, fish, snail or other WorldObject.
	 */
	public void insertRandomly(WorldObject item) {
		item.setPosition(pickUnusedSpace());
		this.register(item);
		item.checkFindMyself();
	}
	
	/**
	 * Insert a new Rock into the world at random.
	 * @return the Rock.
	 */
	public Rock insertRockRandomly() {
		if(Math.random()<0.5)//less than 50% of the time, the rock inserted will be a fallingrock instead of a regular rock.
		{
			Rock a=new FallingRock(this);//instantiate a new falling rock.
			insertRandomly(a);//insert that rock randomly into the map.
			return a;//then return the rock.
		}
		else
		{
		Rock r = new Rock(this);
		insertRandomly(r);
		return r;
		}
	}
	
	/**
	 * Insert a new Fish into the world at random of a specific color.
	 * @param color - the color of the fish.
	 * @return the new fish itself.
	 */
	public Fish insertFishRandomly(int color) {
		Fish f = new Fish(color, this, Math.random());
		insertRandomly(f);
		return f;
	}
	
	public FishHome insertFishHome() {
		FishHome home = new FishHome(this);
		insertRandomly(home);
		return home;
	}
	
	/**
	 * Insert a new Snail at random into the world.
	 * @return the snail!
	 */
	public Snail insertSnailRandomly() {
		Snail snail = new Snail(this);
		insertRandomly(snail);
		return snail;
	}
	
	public FishFood insertFoodRandomly() {
		FishFood food = new FishFood(this);
		insertRandomly(food);
		return food;
	}
	
	/**
	 * Determine if a WorldObject can swim to a particular point.
	 * 
	 * @param whoIsAsking - the object (not just the player!)
	 * @param x - the x-tile.
	 * @param y - the y-tile.
	 * @return true if they can move there.
	 */
	public boolean canSwim(WorldObject whoIsAsking, int x, int y) {
		if (x < 0 || x >= width || y < 0 || y >= height) {
			return false;
		}
		
		boolean isPlayer = whoIsAsking.isPlayer();
		
		List<WorldObject> inSpot = this.find(x, y);
		if(y<height)
		
		for (WorldObject it : inSpot) {
			if (it instanceof Snail) {
				return false;
			}
			else if(it instanceof Rock) //if the object that the player wants to step on is an instance of a rock, the canSwim() method should return false, as you cannot swim onto a rock.
			{
				return false;
			}
			else if(it instanceof Fish && !isPlayer)//if you are not the player, you cannot step onto "it" if it is a fish. This is no longer true if it is the player, hence I needed to put the statement !isPlayer.
			{
				return false;
			}
		}
		
		// If we didn't see an obstacle, we can move there!
		return true;
	}
	
	/**
	 * This is how objects may move. Only Snails do right now.
	 */
	public void stepAll() {
		for (WorldObject it : this.items) {
			it.step();
		}
	}
	
	/**
	 * This signature is a little scary, but we need to support any subclass of WorldObject.
	 * We don't know followers is a {@code List<Fish>} but it should work no matter what!
	 * @param target the leader.
	 * @param followers a set of objects to follow the leader.
	 */
	public static void objectsFollow(WorldObject target, List<? extends WorldObject> followers) {//? extends WorldObject means that the List can hold objects of type WorldObject. Since any subclass of WorldObject has an is-a relationship with the superclass WorldObject, this means that this list can hold any object created from a subclass of WorldObject.
		// What is recentPositions? recentPositions is a queue of x and y coordinates (i.e. positions) that the player has been through for the past 64 movements. 
		// What is followers? A list of the found fish in the game. These are the fish that the player at some point has overlapped with and are no longer lost. These fish are supposed to follow the player by filling in the spaces behind the player in order.
		// What is target? In the FishGame class, the target is the Player, which is what the arraylist of followers, the found fish, follow. 
		// Why is past = putWhere[i+1]? Why not putWhere[i]? i by itself indicates the follower's position in the list of followers. We are using the same iterator for the list of available positions behind the player, and since the list of available positions includes the player's current position, the fish have to follow the player at a position that is after their own position in the followers list. In the 0th position in the ArrayList lies the Player's position. Therefore, the first following fish, which is at array position 0 in the follower's list, has to be at position 1 in the player's recent positions in order for it to be trailing after the player and take the position immediately after the player. The second fish at position 1 of the follower array has to be at position 2 in the recentPositions array, and so on. Therefore, it is i+1 and not i.
		List<IntPoint> putWhere = new ArrayList<>(target.recentPositions);
		for (int i=0; i < followers.size() && i+1 < putWhere.size(); i++) {//for each follower in the arraylist, the follower's position is set to the position that corresponds to where the player has been. If the follower is the first in the list, it will be in the block where the player had immediately been to last, and if it is second, it will be the second one that the player had been too, etc. 
			// What is the deal with the two conditions in this for-loop? Since each follower has to be put in the i+1 position behind the player (As explained before, since the follower cannot overlap with the player), you have to check if i+1 is less than the position array's size itself, otherwise you will get an index out of bounds error (which also means that the game didn't remember enough of the player's recent positions to support anymore following fish). You also still have to make sure you are still doing the operation on each follower, in order, in the followers array, hence the statement i<followers.size().
			IntPoint past = putWhere.get(i+1);//ensures that the follower never selects putwhere.get(0), which is the player's current position. This means that the follower will never overlap with the player. Furthermore, the first follower in position 0 will be in the 1st position in recent positions, which is the position that the player had just been to. The second follower in the 1st position in the followers array will be in the 2nd position in the recent positions array. Hence the ith follower will be in the i+1st position in the player's recent positions list.
			followers.get(i).setPosition(past.x, past.y);//set the follower's position to the position behind the player depending on the follower's index in the arraylist. This updates each follower's position so that they are in the corresponding position (As noted several times above) behind the player.
		}
	}
	
	
}
