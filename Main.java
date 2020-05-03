import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Random;

import static javax.swing.WindowConstants.EXIT_ON_CLOSE;

/**
 * The Main class of the application
 */
public class Main extends JPanel implements ActionListener {
	static JScrollBar scrollerY;
	private static JScrollBar scrollerX;
	private static JFrame frame;
	private static int transY;
	private static int transX;
	private static boolean following;
	private static JButton followLift;
	private static double scale;
	private static double values = 1;
	private static boolean updateValid = false;
	private final Timer timer;
	private final Person[] person;
	private final Lift lift;
	private final int shaftSize;
	private final int liftSize;
	private final int numberOfFloors;
	private final int[][] floorsArray;
	private final String[] realFloor;
	private final boolean selected;
	private final StatChecker stats;
	private int[] getFull;
	private int des;
	private boolean initialize = true;
	private int width;
	private boolean tempBool;
	private int totalCost;

	/**
	 * Instantiates a new Main.
	 *
	 * @param caseChoice         the case choice
	 * @param numbFloors         the numb floors
	 * @param numbPeople         the numb people
	 * @param select             the select
	 * @param cap                the cap
	 * @param thisWidth          the this width
	 * @param peopleDistribution the people distribution
	 * @param stats              the stats
	 */
	public Main(String caseChoice, int numbFloors, int numbPeople, boolean select,
	            int cap, int thisWidth, String peopleDistribution,
	            StatChecker stats) {
		NumberFormat formatter = new DecimalFormat("0.#E0");
		person = new Person[numbPeople];
		this.stats = stats;
		tempBool = true;
		scale = 1;
		des = 0;
		totalCost = 0;
		this.numberOfFloors = numbFloors;
		realFloor = new String[numbFloors];
		selected = select;
		for (int i = 0; i < numbFloors; i++) {
			realFloor[i] = (numbFloors - i - 1) + "";
			if ((numbFloors - i - 1) > 9999) {
				realFloor[i] = formatter.format(Integer.parseInt(realFloor[i]));
			}
		}
		width = thisWidth;
		floorsArray = new int[numbFloors][3];
		this.liftSize = 40;
		boolean caseChoices = caseChoice.equals("Base Case");
		shaftSize = numberOfFloors * liftSize;
		lift = new Lift(numberOfFloors, liftSize, floorsArray, width, caseChoices, cap, scale);
		timer = new Timer(40, this);
		timer.setInitialDelay(1);
		timer.start();
		int[] numberOnEachFloorThisFloor = new int[this.numberOfFloors];
		int[] numberOnEachFloorTarget = new int[this.numberOfFloors];
		for (int i = 0; i < this.numberOfFloors; i++) {
			for (int j = 0; j < 3; j++) {
				floorsArray[i][j] = 0;
			}

		}
		int capacity = lift.getCapacity();
		peopleSetup(numberOnEachFloorThisFloor, numberOnEachFloorTarget, peopleDistribution,
		            scale);
		getFull = new int[capacity];
		for (int i = 0; i < capacity; i++) {
			this.getFull[i] = 0;

		}
	}

	/**
	 * The initial Graphical User Interface for the program.
	 * For selecting number of floors, capacity of lift, etc
	 *
	 * @param frame the current frame of the application
	 */
	public static void gui(JFrame frame) {
		// JLabels
		Font defaultFont = new Font("Calibri", Font.BOLD, 28);

		JLabel title = new JLabel("<HTML><U>Lift Simulator</U></HTML>");
		title.setFont(new Font("Calibri", Font.BOLD, 80));
		JLabel numbFloorsInputText = new JLabel("Enter the number of Floors: ");
		numbFloorsInputText.setFont(defaultFont);

		JLabel numbPeopleInputText = new JLabel("Enter the number of People: ");
		numbPeopleInputText.setFont(defaultFont);
		JLabel whichCase = new JLabel("Please select which algorithm to use: ");
		whichCase.setFont(defaultFont);

		JLabel capacityInputText = new JLabel("Enter the capacity of the lift: ");
		capacityInputText.setFont(defaultFont);
		JLabel isContinuous = new JLabel("People appear in real time?");
		isContinuous.setFont(defaultFont);


		JLabel validText = new JLabel("");
		validText.setFont(defaultFont);

		//Input fields
		JTextField capacityInput = new JTextField(40);
		capacityInput.setFont(defaultFont);
		JTextField numbFloorsInput = new JTextField(40);
		numbFloorsInput.setFont(defaultFont);

		JTextField numbPeopleInput = new JTextField(40);
		numbPeopleInput.setFont(defaultFont);
		JCheckBox continuous = new JCheckBox();
		continuous.setSize(new Dimension(100, 100));
		continuous.setFont(defaultFont);

		JLabel peopleDistribution = new JLabel("Which Distribution for the people?");
		peopleDistribution.setFont(defaultFont);
		String[] distributionChoices = {"Random-Uniform Distribution",
		                                "Exponential Distribution",
		                                "Morning Distribution", "Late-Afternoon Distribution"};
		JComboBox<String> whichDistributionInput = new JComboBox<>(distributionChoices);
		whichDistributionInput.setFont(defaultFont);

		String[] choices = {"Base Case", "Improved Case"};
		JComboBox<String> whichCaseInput = new JComboBox<>(choices);
		whichCaseInput.setFont(defaultFont);
		JButton submit = new JButton("Submit");
		submit.setFont(defaultFont);

		//Setting initial values
		capacityInput.setText("10");
		numbFloorsInput.setText("40");
		numbPeopleInput.setText("300");
		//setting layout to be a simple grid
		GridLayout guiLayout = new GridLayout(10, 1);
		guiLayout.setHgap(50);
		guiLayout.setVgap(20);
		JPanel panel = new JPanel(guiLayout);
		panel.setBorder(new EmptyBorder(10, 10, 10, 10)); //removing the border
		//adding the inputs and the prompts to the panel
		panel.add(isContinuous);
		panel.add(continuous);
		panel.add(numbFloorsInputText);
		panel.add(numbFloorsInput);

		panel.add(capacityInputText);
		panel.add(capacityInput);
		panel.add(numbPeopleInputText);
		panel.add(numbPeopleInput);

		panel.add(whichCase);
		panel.add(whichCaseInput);
		panel.add(peopleDistribution);
		panel.add(whichDistributionInput);
		//adding the panel to the frame
		panel.add(submit);
		panel.add(validText);
		frame.add(panel, BorderLayout.CENTER);
		frame.add(title, BorderLayout.NORTH);


		submit.addActionListener(e -> {
			String caseChoice = (String) (whichCaseInput.getSelectedItem());
			if (!checkIfValid(capacityInput.getText(), 0) ||
			    !checkIfValid(numbPeopleInput.getText(), 0) ||
			    !checkIfValid(numbFloorsInput.getText(), 1)) {
				validText.setText("Not valid please try again..");

			} else {
				nextLine(caseChoice, Integer.parseInt(numbFloorsInput.getText()),
				         Integer.parseInt(numbPeopleInput.getText()),
				         continuous.isSelected(), Integer.parseInt(capacityInput.getText()),
				         (String) whichDistributionInput.getSelectedItem());
				frame.dispose();
			}

		});
	}

	private static boolean checkIfValid(String input, int lowerLimit) {
		try {
			if (Integer.parseInt(input) > lowerLimit) {
				return true;
			}
		} catch (NumberFormatException e) {
			return false;
		}
		return false;
	}

	/**
	 * Next line.
	 *
	 * @param caseChoice         the case choice
	 * @param numberOfFloors     the number of floors
	 * @param numberOfPeople     the number of people
	 * @param selected           the selected
	 * @param cap                the cap
	 * @param peopleDistribution the peopleDistribution
	 */
	public static void nextLine(String caseChoice, int numberOfFloors, int numberOfPeople,
	                            boolean selected, int cap, String peopleDistribution) {

		JPanel panelX = new JPanel(new BorderLayout());
		JPanel panelY = new JPanel(new BorderLayout());
		GridLayout topLayout = new GridLayout(0, 3);
		topLayout.setHgap(50);
		JPanel panelCheck = new JPanel(topLayout);
		if (numberOfPeople / numberOfFloors > 6) {
			scrollerX = new JScrollBar(Adjustable.HORIZONTAL);
			scrollerX.setValues(0, 10,
			                    (int) -(numberOfPeople + (0.5 * numberOfPeople)),
			                    (int) (numberOfPeople + (0.5 * numberOfPeople)));
			panelX.add(scrollerX);
			setTransX(-scrollerX.getValue());
			scrollerX.setBounds(0, 0, 10000, 10);
			scrollerX.addAdjustmentListener(e -> {
				int scrollerValue = -(scrollerX.getValue());
				setTransX(scrollerValue);
			});
		}

		frame = new JFrame("TimerBasedAnimation");
		JButton liftSimulator = new JButton("Restart Lift Simulator");
		panelCheck.add(liftSimulator);
		liftSimulator.addActionListener(e -> {
			frame.dispose();
			if (numberOfFloors > 23) {
				setFollowMode(false);
			}
			main(new String[]{""});
		});
		if (numberOfFloors > 23) {
			String startStr = "Start following the lift";
			String stopStr = "Stop following the lift";
			followLift = new JButton(startStr);
			JButton zoomOut = new JButton("Zoom out");
			panelCheck.add(followLift);
			panelCheck.add(liftSimulator);
			panelCheck.add(zoomOut);
			followLift.addActionListener(e -> {
				if (followLift.getText().equals(startStr)) {
					setFollowMode(true);
					followLift.setText(stopStr);
				} else {
					setFollowMode(false);
					followLift.setText(startStr);
				}
			});
			zoomOut.addActionListener(e -> {
				boolean flip = (zoomOut.getText().equals("Zoom out"));
				setScale(getScale() * (flip ? 0.5 : 2));
				if (flip) {
					setJustUpdated(0.5);
					zoomOut.setText("Zoom in");
				} else {
					setJustUpdated(2);
					zoomOut.setText("Zoom out");
				}
			});
			scrollerY = new JScrollBar();
			panelY.add(scrollerY);
			scrollerY.setBounds(990, 0, 10, 1000);
			if (numberOfFloors - 20 < 10) {
				scrollerY.setMaximum(15);
			} else {
				scrollerY.setMaximum(numberOfFloors - 12);
			}
			scrollerY.addAdjustmentListener(e -> {
				int scrollerValue = -(scrollerY.getValue() * 40);
				setTransY(scrollerValue);
			});
		}
		frame.setDefaultCloseOperation(EXIT_ON_CLOSE);
		StatChecker stats = new StatChecker();
		int thisWidth = 1000;
		frame.add(new Main(caseChoice, numberOfFloors, numberOfPeople, selected,
		                   cap, thisWidth, peopleDistribution, stats));


		frame.add(panelY, BorderLayout.EAST);
		frame.add(panelX, BorderLayout.SOUTH);
		frame.add(panelCheck, BorderLayout.PAGE_START);
		frame.addMouseMotionListener(stats);
		frame.setSize(thisWidth, 1000);
		frame.setMinimumSize(new Dimension(500, 500));
		frame.setVisible(true);

	}

	/**
	 * Sets trans y.
	 *
	 * @param transYs the trans ys
	 */
	public static void setTransY(int transYs) {
		transY = transYs;
	}

	/**
	 * Gets scale.
	 *
	 * @return the scale
	 */
	public static double getScale() {
		return scale;
	}

	/**
	 * Sets scale.
	 *
	 * @param scalePar the scale par
	 */
	public static void setScale(double scalePar) {
		scale = scalePar;
	}

	/**
	 * Sets just updated.
	 *
	 * @param valid the valid
	 */
	public static void setJustUpdated(double valid) {
		updateValid = true;
		values = valid;
	}

	/**
	 * Sets trans x.
	 *
	 * @param transXs the trans xs
	 */
	public static void setTransX(int transXs) {
		transX = transXs;
	}

	private static void setFollowMode(boolean value) {
		following = value;
		if (!value) {
			followLift.setText("Start following the lift");
		} else {
			followLift.setText("Stop following the lift");
		}
	}

	/**
	 * The initially called function
	 *
	 * @param args the input arguments
	 */
	public static void main(String[] args) {
		JFrame frame = new JFrame("TimerBasedAnimation");
		frame.setDefaultCloseOperation(EXIT_ON_CLOSE);
		frame.setSize(1000, 1000);
		gui(frame);
		frame.setVisible(true);
	}

	private void peopleSetup(int[] numberOnEachFloorThisFloor, int[] numberOnEachFloorTarget,
	                         String peopleDistribution, double scale) {
		for (int count = 0; count < numberOnEachFloorThisFloor.length; count++) {
			numberOnEachFloorTarget[count] = 0;
			numberOnEachFloorThisFloor[count] = 0;
		}
		for (int i = 0; i < person.length; i++) {
			int initFloor = 0;
			int initTarget = 0;
			switch (peopleDistribution) {
				case "Random-Uniform Distribution":
					while (initFloor == initTarget) {
						initFloor = (int) (this.numberOfFloors * Math.random());
						initTarget = (int) (this.numberOfFloors * Math.random());
					}
					break;
				case "Exponential Distribution": {
					ArrayList<Integer> probList = generateProbList();
					while (initFloor == initTarget) {
						initFloor = probList.get((int) (probList.size() * Math.random()));
						initTarget =
								numberOfFloors - 1 - probList.get((int) (probList.size() * Math.random()));
					}
					break;
				}
				case "Morning Distribution":
					if (Math.random() < ((float) 1 / 3)) {
						initTarget = this.numberOfFloors - 1;
						initFloor = (int) ((this.numberOfFloors) * Math.random());
					}
					while (initFloor == initTarget) {
						initFloor = (int) (this.numberOfFloors * Math.random());
						initTarget = (int) (this.numberOfFloors * Math.random());
					}
					break;
				case "Late-Afternoon Distribution": {
					ArrayList<Integer> probList = generateProbList();
					while (initFloor == initTarget) {
						initFloor = probList.get((int) (probList.size() * Math.random()));
						initTarget = (int) (numberOfFloors * Math.random());
					}
					break;
				}
			} //chooses the correct distribution based on what the user inputs
			Color colors = colorGenerator();
			person[i] = new Person(numberOnEachFloorThisFloor[initFloor] * 20, initFloor,
			                       initTarget, liftSize, numberOfFloors, shaftSize, floorsArray,
			                       numberOnEachFloorTarget, colors, scale);
			numberOnEachFloorThisFloor[initFloor] += 1;
		}
	}

	private ArrayList<Integer> generateProbList() {
		ArrayList<Integer> probList = new ArrayList<>();
		long[] array = new long[numberOfFloors];
		for (int j = 0; j < array.length; j++) {
			array[j] = (long) Math.pow(1.1, j);
		}
		for (int count = 0; count < numberOfFloors; count++) {
			for (int j = 0; j < array[count]; j++) {
				probList.add(count);
			}
		}
		return probList;
	}

	/**
	 * Render.
	 *
	 * @param g2 the g2 object
	 */
	public void render(Graphics2D g2) {
		width = getWidth();

		if (following) {
			if (scrollerX != null) {
				if (scrollerX.getValueIsAdjusting()) {
					setFollowMode(false);
				}
				scrollerX.setValue((lift.getX() - width / 2));
			}
			if (scrollerY != null) {
				if (scrollerY.getValueIsAdjusting()) {
					setFollowMode(false);
				}
				scrollerY.setValue((lift.getY() - width / 2) / 40);
			}
		}
		g2.translate(transX, transY);
		g2.setColor(Color.BLACK);
		g2.setStroke(new BasicStroke(5));
		int newStore = liftSize + shaftSize - (shaftSize / numberOfFloors);
		g2.drawRect((int) (scale * width / 2), 20, (int) (scale * (liftSize - 5))
				, (int) (scale * newStore));
	}

	/**
	 * Color generator color.
	 *
	 * @return the color
	 */
	public Color colorGenerator() {
		Random random = new Random();
		final float hue = random.nextFloat();
		final float saturation = (random.nextInt(7000) + 3000) / 10000f;
		final float luminance = 1f;
		return Color.getHSBColor(hue, saturation, luminance);
	}

	/**
	 * @param limit the amount of people who need to reach their destination
	 * @param dest  the initial value to start the search at
	 * @return whether all the people who have been spawned have reached their destination
	 */
	private boolean everyoneReachDest(int limit, int dest) {
		for (int count = dest; count < limit; count++) {
			if (!person[count].getReachedDest()) {
				return false;
			}
		}
		return true;
	}

	@Override
	public void paint(Graphics g) {
		super.paintComponent(g);
		Graphics2D g2 = (Graphics2D) g;
		this.render(g2);
		int reachedFinalDestination = 0;
		lift.setScale(scale);
		int[] mouseCoords = stats.getMouseCoords();
		for (Person item : person) {
			int val = item.getReachedDest() ? 1 : 0;
			reachedFinalDestination += val;
			item.setScale(scale);
			item.setMx(mouseCoords[0] - transX);
			item.setMy(mouseCoords[1] - transY);
			item.setTranslate(transX, transY);
		}
		drawStats(g2, reachedFinalDestination);
		if (updateValid) {
			lift.updateScaledLocation(values);
			updateValid = false;
		}
		if (reachedFinalDestination < person.length) {

			RenderingHints rh = new RenderingHints(RenderingHints.KEY_ANTIALIASING,
			                                       RenderingHints.VALUE_ANTIALIAS_ON);
			rh.put(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
			g2.setRenderingHints(rh);
			if (initialize) {
				this.render(g2);
				initialize = false;
			}
			drawObjects(g, g2);
			lift.step();
		} else {
			drawLastFrame(g, g2);
		}
		lift.setWidth((float) (scale * (float) (width) / 2 + 5));
		lift.paintMe(g);
	}

	private void drawObjects(Graphics g, Graphics2D g2) {
		int whereX = (int) (getScale() * width / 2) + 2;
		drawFloorNumbers(g2, whereX);
		int whereLiftAt;
		int getCapacity = lift.getCapacity();
		whereLiftAt = (int) (numberOfFloors - lift.location() - 1);
		if (selected) {
			if (des - 2 < person.length) {
				updatePeopleRealTime(whereLiftAt, getCapacity, g);
			} else if (des - 2 != person.length) {
				des--;
			} else {
				updatePeopleRealTime(whereLiftAt, getCapacity, g);
			}
		} else {
			updatePeople(g, whereLiftAt, getCapacity);
		}
	}

	private void drawLastFrame(Graphics g, Graphics2D g2) {
		if (tempBool) {
			lift.reverseStep();
			tempBool = false;
			int newTotal = 0;
			for (Person value : person) {
				newTotal += value.getWaitTime();
			}
		}
		int whereX = (int) (getScale() * width / 2) + 5;
		for (Person value : person) {
			value.setLiftX(whereX);
			value.update((int) (numberOfFloors - lift.location() - 1), getFull, 0,
			             lift.getDirection());
			value.moveInLift(lift.getX(), lift.getY());
			value.paintMe(g);
		}
		drawFloorNumbers(g2, whereX);
	}

	private void updatePeople(Graphics g, int whereLiftAt, int getCapacity) {
		int getF;
		person[0].setLiftX(lift.getX());
		if (!person[0].getReachedDest()) {
			person[0].moveInLift(lift.getX(), lift.getY());
		}
		paintPerson(g, whereLiftAt, getCapacity, person[0]);
		for (int i = 1; i < person.length; i++) {
			person[i].setLiftX(lift.getX());
			if (!person[i].getReachedDest()) {
				getFull = person[i - 1].getHowFull();
				getF = person[i].numberInLift();
				person[i].moveInLift(lift.getX() + getF * 6,
				                     lift.getY() + 20);
			}
			paintPerson(g, whereLiftAt, getCapacity, person[i]);

		}
		int totalAmount = 0;
		for (int value : getFull) {
			totalAmount += value;
		}
		lift.setPeople(totalAmount);
	}

	private void paintPerson(Graphics g, int whereLiftAt, int getCapacity, Person person1) {
		person1.update(whereLiftAt, getFull, getCapacity, lift.getDirection());
		person1.paintMe(g);
	}

	private void drawStats(Graphics2D g2, int reachedFinalDestination) {
		g2.translate(-transX, -transY);
		g2.drawString("Total cost for Lift : " + (((float) lift.getCounter()) * lift.getSpeed()) / 40,
		              10, 15);
		g2.drawString("Number who have reached destination: " + reachedFinalDestination,
		              400, 15);
		if (person.length - reachedFinalDestination - lift.getCapacity() > 0) {
			totalCost += person.length - reachedFinalDestination - lift.getCapacity();
		}
		g2.drawString("Average cost per person: " + String.format("%.2f",
		                                                          (lift.getSpeed() * totalCost) / (40 * person.length)),
		              200, 15);
		g2.translate(transX, transY);
	}

	private void drawFloorNumbers(Graphics2D g2, int whereX) {
		g2.setFont(new Font("Arial", Font.PLAIN, (int) (12 * scale)));
		for (int i = 0; i < realFloor.length; i++) {
			int tempX = 4 - realFloor[i].length();
			g2.drawString("" + realFloor[i], whereX + tempX,
			              (int) (scale * (i * (double) (shaftSize) / numberOfFloors)) + (int) ((double) (liftSize) / 2 * scale) + liftSize / 2);
		}
	}

	/**
	 * @param whereLiftAt where the lift is located
	 * @param getCapacity the amount of people in the lift
	 * @param g           the graphics object
	 */
	private void updatePeopleRealTime(int whereLiftAt, int getCapacity, Graphics g) {
		for (int i = 0; i < des + 5; i++) {
			paintPerson(g, whereLiftAt, getCapacity, person[i]);
			person[i].moveInLift(lift.getX(), lift.getY());
			if (everyoneReachDest(des + 5, des)) {
				des += 5;
				break;
			}
		}
	}

	public void actionPerformed(ActionEvent e) {
		repaint();
	}
}
