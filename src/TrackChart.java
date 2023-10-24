import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.time.LocalDate;
import java.util.HashMap;

import javax.swing.JPanel;

public class TrackChart extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public HashMap<LocalDate, Double> map;
	public int width;
	public int height;
	public int maxWeight;
	public int maxDate;

	public TrackChart(String line, int width, int height) {
		setBackground(Color.white);
		this.width = width;
		this.height = height;
		setPreferredSize(new Dimension(width, height));
		String[] array = line.split(":");
		map = new HashMap<>();
		for (int i = 1; i < array.length; i++) {
			map.put(LocalDate.parse(array[i].split("@")[0]), Double.parseDouble(array[i].split("@")[1]));
		}
	}

	@Override
	protected void paintComponent(Graphics g) {
		g.setColor(Color.black);
		g.fillRect(width / 10, height / 10, width / 100, height * 7 / 10);
		g.fillRect(width / 10, height * 8 / 10, width * 7 / 10, height / 100);
		// Draw left triangle
		int[] xPointsLeft = { width / 10 - width / 50 + width / 200, width / 10 + width / 200,
				width / 10 + width / 50 + width / 200 };
		int[] yPointsLeft = { height / 10, height / 10 - height / 50, height / 10 };
		g.fillPolygon(xPointsLeft, yPointsLeft, 3);

		// Draw bottom triangle
		int[] xPointsBottom = { width * 8 / 10, width * 8 / 10, width * 8 / 10 + width / 50 };
		int[] yPointsBottom = { height * 8 / 10 - height / 50 + height / 200,
				height * 8 / 10 + height / 50 + height / 200, height * 8 / 10 + height / 200 };
		g.fillPolygon(xPointsBottom, yPointsBottom, 3);
		
		
		
	}
	
	

}
