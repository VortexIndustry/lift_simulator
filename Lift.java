import java.awt.*;
import java.awt.geom.Rectangle2D;

/**
 * The type Lift.
 */
class Lift {
	private final Rectangle2D.Float rect = new Rectangle2D.Float();
	private final int w;
	private final int h;
	private final long shaftSize;
	private final int numberOfFloors;
	private final int[][] floors;
	private final int capacity;
	private final double speed;
	private final boolean isBaseCase;
	private final double lowLimit;
	private double dir;
	private int totalPeople;
	private int counter;
	private double scale;

	/**
	 * Instantiates a new Lift.
	 *
	 * @param numberOfFloors the number of floors
	 * @param liftSize       the lift size is the lift height
	 * @param floorArray     the floor array for which floors have pressed the button for up or
	 *                       down on.
	 * @param whereX         whereX is the x coordinate of the lift
	 * @param baseCase       whether it is the base case(true) or improved case(false)
	 * @param capacity       the capacity of the lift
	 * @param scale          the scale for magnification
	 */
	Lift(int numberOfFloors, int liftSize, int[][] floorArray, int whereX,
	     boolean baseCase, int capacity, double scale) {
		this.floors = floorArray;
		this.scale = scale;
		w = 35;
		rect.x = (float) (whereX) / 2 - (float) (w) / 2;
		h = liftSize;
		this.shaftSize = liftSize * numberOfFloors;
		this.numberOfFloors = numberOfFloors;
		this.capacity = capacity;
		this.speed = 40;
		counter = 0;
		dir = 1 * this.speed;
		isBaseCase = baseCase;
		lowLimit = 20;
		rect.y = (int) lowLimit;
		totalPeople = 0;
	}

	/**
	 * Set width of the lift
	 *
	 * @param width the width of the new lift
	 */
	public void setWidth(float width) {
		rect.x = (width - 5);
	}

	/**
	 * Reverse the final step at the end so the lift ends in the correct place as step is called
	 * before the update function
	 */
	public void reverseStep() {
		dir *= -1;
		rect.setFrame(rect.getX(), rect.getY() + scale * (dir), scale * w, scale * h);
		counter--;
	}

	/**
	 * Gets speed of the lift so that calculations for how many floors the lift had to traverse
	 * is calculated correctly.
	 *
	 * @return the double
	 */
	public double getSpeed() {
		return speed;
	}

	/**
	 * Get capacity of the lift.
	 *
	 * @return the int
	 */
	public int getCapacity() {
		return this.capacity;
	}

	/**
	 * Sets scale (for zooming in and out).
	 *
	 * @param scale the scale that will be used for the zooming (0.5,1)
	 */
	public void setScale(double scale) {
		this.scale = scale;
	}

	/**
	 * Location of the lift
	 * Either on a floor then the floor number will be returned
	 * Or if not on a floor(between floors) then -1 will be returned
	 *
	 * @return Either on a floor then the floor number will be returned Or if not on a floor
	 * (between floors) then -1 will be returned
	 */
	public float location() {
		float currentFloor = rect.y - 20;
		currentFloor /= (scale * (double) shaftSize / numberOfFloors);
		if (currentFloor == (int) currentFloor && currentFloor >= 0) {
			return currentFloor;
		}
		return -1f;
	}

	/**
	 * Paint the lift object.
	 *
	 * @param g the graphics object
	 */
	public void paintMe(Graphics g) {
		Graphics2D g2 = (Graphics2D) g;
		meRender(g2);
	}

	/**
	 * Getting the x coordinate of the lift
	 *
	 * @return the int
	 */
	public int getX() {
		return (int) rect.x;
	}

	/**
	 * Getting the y coordinate of the lift
	 *
	 * @return the int
	 */
	public int getY() {
		return (int) rect.y;
	}

	/**
	 * For rendering the lift
	 *
	 * @param g2 the graphics2D object
	 */
	public void meRender(Graphics2D g2) {
		g2.setColor(Color.BLUE);
		g2.setStroke(new BasicStroke((int) (scale * 10)));
		rect.setRect(rect.getX(), rect.getY(), scale * w, scale * h);
		g2.draw(rect);
		g2.setStroke(new BasicStroke(5));

	}

	/**
	 * Gets counter.
	 *
	 * @return the counter
	 */
	public int getCounter() {
		return counter;
	}

	/**
	 * Gets direction.
	 *
	 * @return the direction
	 */
	public int getDirection() {
		return (int) ((dir / Math.abs(dir)));
	}


	/**
	 * Set people.
	 *
	 * @param totalPeople the total people
	 */
	public void setPeople(int totalPeople) {
		this.totalPeople = totalPeople;
	}

	/**
	 * Step the lift
	 */
	public void step() {

		if (isBaseCase) {
			long maxLimits = shaftSize - (shaftSize / numberOfFloors);
			if (rect.getY() >= 20 + (scale * maxLimits) && (int) (dir / Math.abs(dir)) != -1) {
				dir *= -1;
			} else if (rect.getY() <= lowLimit && dir != speed) {
				dir *= -1;
			}
		}
		rect.setFrame(rect.getX(), rect.getY() + (scale * dir), scale * w, scale * h);
		if (!isBaseCase) {
			int directAbs = (int) (dir / Math.abs(dir));
			if ((int) location() == -1 && directAbs == -1) {
				dir *= -1;
			} else if ((int) location() >= numberOfFloors - 1) {
				if (directAbs == 1) {
					dir *= -1;
				}
			} else if ((int) location() != -1 && totalPeople == 0 && peopleWaiting(directAbs,
			                                                                       dir) != 0) {
				dir = peopleWaiting(directAbs, dir);
			}
		}
		counter++;
	}

	/**
	 * Update scaled location.
	 *
	 * @param values is the values to be scaled
	 */
	public void updateScaledLocation(double values) {
		rect.setRect(rect.getX() * values, rect.getY() * values, scale * w, scale * h);
	}

	private double peopleWaiting(int directAbs, double direction) {
		int whereLift = (int) (this.location());
		if (whereLift <= numberOfFloors - 1) {
			for (int i = whereLift; i < numberOfFloors; i++) {
				if (floors[i][0] == 1) {
					if (directAbs == -1) {
						return direction;
					}
				} else if (floors[i][1] == 1) {
					if (directAbs == -1) {
						return direction;
					}
				}
			}
		} else {
			return -1 * direction;
		}
		for (int i = whereLift; i > 0; i--) {
			if (floors[i][0] == 1 || floors[i][1] == 1) {
				if (directAbs == 1) {
					return direction;
				}
			}
		}
		return 0;
	}
}