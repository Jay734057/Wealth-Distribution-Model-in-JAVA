
/**
 * People Class: represent each person on the land, to simulate the movement of persons, 
 * 		including their harvest, move, metabolism, aging and death. This class also contains
 * 		wealth, age, lifespan, metabolism rate, vision and position information for each 
 * 		object
 * 
 */
import java.util.Random;

public class People implements Comparable<People> {
	private int wealth;
	private int age;
	private int lifespan;// people's maximum age
	private int metabolism;// the amount of wealth that is consumed each turn
	private int vision;
	private int position_x, position_y;// indicate the location of people
	private int maxposition;// the max x/y axis in the landscape
	private int direction;

	public People(int maxMetabolism, int maxVision, int minLifespan,
			int maxLifespan, int maxPosition) {
		Random r = new Random();// random class for all random process
		metabolism = r.nextInt(maxMetabolism);
		wealth = r.nextInt(50) + metabolism;
		lifespan = minLifespan + r.nextInt(maxLifespan - minLifespan + 1);
		age = r.nextInt(lifespan);
		vision = r.nextInt(maxVision) + 1;
		maxposition = maxPosition;
		position_x = r.nextInt(maxposition + 1);
		position_y = r.nextInt(maxposition + 1);
	}

	public People(People p) {
		wealth = p.getWealth();
	}

	void move(Land[][] landscape) {
		// turn to a direction with most wealth, then move one step according to
		// grain in vision.
		turn(landscape);
		landscape[position_x][position_y].leave();
		switch (direction) {
		case 0:
			position_x = Math.floorMod(position_x - 1, maxposition + 1);
		case 1:
			position_y = Math.floorMod(position_y + 1, maxposition + 1);
		case 2:
			position_x = Math.floorMod(position_x + 1, maxposition + 1);
		case 3:
			position_y = Math.floorMod(position_y - 1, maxposition + 1);
		}
		landscape[position_x][position_y].arrive();
	}

	void harvest(Land[][] landscape) {
		// harvest the grain on the position evenly
		wealth = (int) (wealth
				+ Math.floor(landscape[position_x][position_y].getAct()
						/ landscape[position_x][position_y]
								.getOccupation()));
	}

	void metabolism() {
		wealth -= metabolism;
	}

	void aging() {
		age++;
	}

	void die(int maxMetabolism, int maxVision, int minLifespan,
			int maxLifespan) {
		// if a person runs out of his/her wealth or lifespan, he/she dies and
		// generates a new one
		if (wealth <= 0 || age > lifespan) {
			Random r = new Random();
			metabolism = r.nextInt(maxMetabolism);
			wealth = r.nextInt(50) + metabolism;
			lifespan = minLifespan
					+ r.nextInt(maxLifespan - minLifespan + 1);
			age = 0;
			vision = r.nextInt(maxVision) + 1;
		}
	}

	public int maxWealth(People[] whole) {
		// find the person with maximum wealth and return the amount of wealth
		// of he/she
		int maxWealth = 0;
		for (People p : whole)
			if (p.getWealth() > maxWealth)
				maxWealth = p.getWealth();
		return maxWealth;
	}

	public int getWealth() {
		return wealth;
	}

	void setAge() {
		// set age for each person in the initialization process
		Random r = new Random();
		age = r.nextInt(lifespan);
	}

	int getPosX() {
		// get x axis of position
		return position_x;
	}

	int getPosY() {
		// get y axis of position
		return position_y;
	}

	void turn(Land[][] landscape) {
		// turn person's direction to the one with most wealth
		direction = 0;
		double grainInVision = 0;
		double grainInVision_tmp = 0;
		for (int i = 1; i <= vision; i++) {
			// calculate the total grain amount of in north direction
			grainInVision_tmp = grainInVision_tmp
					+ landscape[Math.floorMod(position_x - i,
							maxposition + 1)][position_y].getAct();
		}
		if (grainInVision < grainInVision_tmp) {
			grainInVision = grainInVision_tmp;
			direction = 0;
		}
		for (int i = 1; i <= vision; i++) {
			// calculate the total grain amount of in east direction
			grainInVision_tmp = grainInVision_tmp
					+ landscape[position_x][Math.floorMod(position_y + 1,
							maxposition + 1)].getAct();
		}
		if (grainInVision < grainInVision_tmp) {
			grainInVision = grainInVision_tmp;
			direction = 1;
		}
		for (int i = 1; i <= vision; i++) {
			// calculate the total grain amount of in south direction
			grainInVision_tmp = grainInVision_tmp
					+ landscape[Math.floorMod(position_x + 1,
							maxposition + 1)][position_y].getAct();
		}
		if (grainInVision < grainInVision_tmp) {
			grainInVision = grainInVision_tmp;
			direction = 2;
		}
		for (int i = 1; i <= vision; i++) {
			// calculate the total grain amount of in west direction
			grainInVision_tmp = grainInVision_tmp
					+ landscape[position_x][Math.floorMod(position_y - 1,
							maxposition + 1)].getAct();
		}
		if (grainInVision < grainInVision_tmp) {
			grainInVision = grainInVision_tmp;
			direction = 3;
		}
	}

	@Override
	public int compareTo(People p) {
		// each object can be compared to each other according to its wealth
		if (this.getWealth() < p.getWealth()) {
			return -1;
		} else if (this.getWealth() > p.getWealth()) {
			return 1;
		} else
			return 0;
	}
}
