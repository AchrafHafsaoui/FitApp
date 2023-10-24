import java.awt.Color;
import java.awt.Graphics;

import javax.swing.JPanel;

public class LineChart extends JPanel {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private double needed;
	private double taken;
	private int width;
	private int round;
	private Color color;

    public LineChart(double needed, double taken, Color color, int width, int round) {
        this.needed=needed;
        this.taken=taken>needed?needed:taken;
        this.width=width;
        this.round=round;
        this.color=color;
    }

    @Override
    protected void paintComponent(Graphics g) {

    	g.setColor(MainFrame.FIRST_COLOR);
        g.fillRoundRect(0, 0, width, CaloriesPanel.HEIGHT/30, round, round);
        g.setColor(color);
        g.fillRoundRect(0, 0, (int)(taken/needed*width), CaloriesPanel.HEIGHT/30, round, round);
    }
}