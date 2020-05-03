import javax.swing.*;
import java.awt.*;
import java.awt.geom.Rectangle2D;

/**
 * The type Person.
 */
class Person extends JPanel {
	private final Rectangle2D.Float rect = new Rectangle2D.Float();
	private final int target;
	private final int liftSize;
	private final int numberOfFloors;
	private final int shaftSize;
	private final int[][] floors;
	private final Color color;
	private final int heightPerson;
	private final int widthPerson;
	private final int[] rightPeople;
	private final int spacer;
	private final int startingFloor;
	private int dir;
	private int leftAmount;
	private boolean inLift;
	private boolean reachedDest;
	private int[] howFull;
	private int whereNew;
	private int mx;
	private int my;
	private int waitTime;
	private int floorNo;
	private double liftX;
	private double scale;
	private double transX;
	private double transY;

	/**
	 * Instantiates a new Person.
	 *
	 * @param rightAmount    the right amount
	 * @param floorNo        the floor no
	 * @param target         the target
	 * @param liftSize       the lift size
	 * @param numberOfFloors the number of floors
	 * @param shaft          the shaft
	 * @param floorArray     the floor array
	 * @param rightPeople    the right people
	 * @param color          the color
	 * @param scale          the scale
	 */
	Person(int rightAmount, int floorNo, int target, int liftSize, int numberOfFloors, int shaft,
	       int[][] floorArray, int[] rightPeople, Color color, double scale) {
		spacer = rightAmount;
		this.scale = scale;
		this.rightPeople = rightPeople;
		setSize(spacer, 0);
		this.floorNo = floorNo;
		startingFloor = floorNo;
		this.target = target;
		this.dir = target - floorNo;
		this.floors = floorArray;
		this.dir = (Math.abs(this.dir)) / this.dir;
		if (this.dir == -1) {
			this.dir = 0;
		}
		this.color = color;
		floors[floorNo][dir] = 1;
		this.liftSize = liftSize;
		this.numberOfFloors = numberOfFloors;
		this.shaftSize = shaft;
		this.inLift = false;
		heightPerson = liftSize / 2;
		widthPerson = liftSize / 5;
		this.reachedDest = false;
	}

	/**
	 * Set size.
	 *
	 * @param rightAmount the right amount
	 * @param yPlace      the y place
	 */
	@Override
	public void setSize(int rightAmount, int yPlace) {
		rect.setFrame((liftX - (rightAmount)), scale * yPlace, scale * widthPerson,
		              scale * heightPerson);
	}

	/**
	 * Set mx.
	 *
	 * @param mx the mx
	 */
	public void setMx(int mx) {
		this.mx = mx;
	}

	/**
	 * Set my.
	 *
	 * @param my the my
	 */
	public void setMy(int my) {
		this.my = my;
	}

	/**
	 * Set scale.
	 *
	 * @param scale the scale
	 */
	public void setScale(double scale) {
		this.scale = scale;
	}

	/**
	 * Set lift x.
	 *
	 * @param liftX the lift x
	 */
	public void setLiftX(double liftX) {
		this.liftX = liftX;
	}

	/**
	 * Paint me.
	 *
	 * @param g the g
	 */
	public void paintMe(Graphics g) {
		Graphics2D g2 = (Graphics2D) g;
		meRender(g2);

	}

	/**
	 * Me render.
	 *
	 * @param g2 the g 2
	 */
	public void meRender(Graphics2D g2) {
		if (!this.inLift && !this.reachedDest) {
			floors[floorNo][dir] = 1;
			waitTime++;
		} else if (this.reachedDest) {
			floors[floorNo][dir] = 0;
		}

		g2.setColor(color);
		float mxTemp;
		float myTemp;
		mxTemp = mx - 9f;
		myTemp = my - 58f;
		if (mxTemp >= rect.x - 2 && mxTemp <= rect.x + rect.getWidth() + 2 && myTemp >= rect.y - 2 &&
		    myTemp <= rect.y + rect.getHeight() + 2) {
			g2.setColor(Color.BLACK); //Show stats for person
			showStats(g2);
		}
		g2.fill(rect);
		g2.draw(rect);
		g2.setColor(Color.BLACK);
	}

	/**
	 * Gets wait time.
	 *
	 * @return the wait time
	 */
	public int getWaitTime() {
		return waitTime;
	}

	private void showStats(Graphics2D g2) {
		g2.translate(-transX, -transY);
		g2.drawString("Target is : " + (numberOfFloors - target - 1), 890, 15);
		g2.drawString("Wait time is : " + waitTime, 770, 15);
		g2.drawString("Starting floor is : " + (numberOfFloors - startingFloor - 1), 650, 15);
		g2.translate(transX, transY);
	}

	/**
	 * Gets if the person has reached their destination.
	 *
	 * @return the boolean
	 */
	public boolean getReachedDest() {
		return reachedDest;
	}

	/**
	 * Set translate.
	 *
	 * @param translateX the translate x
	 * @param translateY the translate y
	 */
	public void setTranslate(double translateX, double translateY) {
		transX = translateX;
		transY = translateY;
	}

	/**
	 * Get how full int [ ].
	 *
	 * @return the int [ ]
	 */
	public int[] getHowFull() {
		return howFull;
	}

	/**
	 * Move in lift.
	 *
	 * @param liftX the lift x
	 * @param liftY the lift y
	 */
	public void moveInLift(int liftX, int liftY) {
		if (this.inLift) {
			rect.setFrame(liftX, liftY, widthPerson, heightPerson);
		}
	}

	/**
	 * Update.
	 *
	 * @param whereLift       the where lift
	 * @param howFulls        the how fulls
	 * @param capacity        the capacity
	 * @param directionOfLift the direction of lift
	 */
	public void update(int whereLift, int[] howFulls, int capacity, int directionOfLift) {
		this.howFull = howFulls;
		double doubleSum =
				(float) (liftSize - 20) + scale * (this.floorNo * ((double) shaftSize / numberOfFloors));
		if (this.inLift && (whereLift == numberOfFloors - this.target - 1)) {
			this.reachedDest = true;
			this.inLift = false;
			this.howFull[whereNew] = 0;
			this.leftAmount = 20 * rightPeople[whereLift];
			setSize(-leftAmount - 80, 0);
			rightPeople[whereLift]++;
			this.floorNo = this.target;
			doubleSum =
					(float) (liftSize - 20) + scale * (this.floorNo * ((double) shaftSize / numberOfFloors));
			rect.setFrame(scale * rect.getX(), doubleSum, scale * widthPerson,
			              scale * heightPerson);
		}
		if (this.reachedDest) {
			setSize(-leftAmount - 80, 0);
			this.floorNo = this.target;
			rect.setFrame(rect.getX(), doubleSum, scale * widthPerson,
			              scale * heightPerson);
		}
		if (!reachedDest && !inLift) {
			if (whereLift == (numberOfFloors - this.floorNo - 1) && capFun(howFull) < capacity) {
				int tempVal = dir == 1 ? 1 : -1;
				if (directionOfLift == tempVal || whereLift == 0 || whereLift == numberOfFloors - 1) {
					inLift = true;
					for (int i = 0; i < howFull.length; i++) {
						if (howFull[i] == 0) {
							howFull[i] = 1;
							this.whereNew = i;
							break;
						}
					}
				}
			}
			doubleSum =
					(float) (liftSize - 20) + scale * (this.floorNo * ((double) shaftSize / numberOfFloors));
			setSize(spacer, 0);
			rect.setFrame((rect.getX() - 60), doubleSum, scale * widthPerson,
			              scale * heightPerson);
		}
	}

	/**
	 * Number in lift int.
	 *
	 * @return the int
	 */
	public int numberInLift() {
		return whereNew;
	}

	/**
	 * Cap fun int.
	 *
	 * @param getFull the get full
	 * @return the int
	 */
	int capFun(int[] getFull) {
		int getManyFull = 0;
		for (int i : getFull) {
			getManyFull += i;
		}
		return getManyFull;
	}
}
