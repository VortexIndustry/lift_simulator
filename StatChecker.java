import javax.swing.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;

/**
 * The type Stat checker.
 */
public class StatChecker extends JPanel implements MouseMotionListener {
	private int mx;
	private int my;

	/**
	 * Instantiates a new Stat checker.
	 */
	StatChecker() {
		addMouseMotionListener(this);
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		mx = e.getX() - 10;
		my = e.getY() - 10;
		e.consume();
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		mx = e.getX();
		my = e.getY();
		e.consume();
	}

	/**
	 * Get mouse coords packaged into an array
	 *
	 * @return the packaged coords
	 */
	public int[] getMouseCoords() {
		return new int[]{mx, my};
	}
}
