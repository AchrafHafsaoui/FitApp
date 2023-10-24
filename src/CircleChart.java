import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.JPanel;

public class CircleChart extends JPanel {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private double dailyCalories;
	private double consumedCalories;
    private double protein;
    private double carbs;
    private double fat;

    public CircleChart(double dailyCalories, double carbs, double protein, double fat) {
    	consumedCalories=fat+carbs+protein;
        this.dailyCalories = consumedCalories>dailyCalories?consumedCalories:dailyCalories;
        this.carbs=carbs;
        this.protein=protein;
        this.fat=fat;
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;

        int diameter = Math.min(getWidth(), getHeight());
        int x = (getWidth() - diameter) / 2;
        int y = (getHeight() - diameter) / 2;

        // Draw the outer circle
        g2d.setColor(MainFrame.THIRD_COLOR);
        g2d.drawOval(x, y, diameter, diameter);
        g2d.fillOval(x, y, diameter, diameter);


        // Calculate the angles for each segment of the pie chart
        double carbAngle = carbs / dailyCalories * 360;
        double proteinAngle = protein / dailyCalories * 360;
        double fatAngle = fat / dailyCalories * 360;

        g2d.setColor(CaloriesPanel.CARBS_COLOR);
        g2d.fillArc(x, y, diameter, diameter, 90, -(int) carbAngle);

        g2d.setColor(CaloriesPanel.PROTEIN_COLOR);
        g2d.fillArc(x, y, diameter, diameter, 90 - (int) carbAngle, -(int) proteinAngle);

        g2d.setColor(CaloriesPanel.FAT_COLOR);
        g2d.fillArc(x, y, diameter, diameter, 90 - (int) carbAngle - (int) proteinAngle, -(int) fatAngle);
        
        g2d.setColor(MainFrame.THIRD_COLOR);
        int smallerDiameter = (int) (diameter / 1.5);
        int newX = x + (diameter - smallerDiameter) / 2;
        int newY = y + (diameter - smallerDiameter) / 2;
        g2d.fillOval(newX, newY, smallerDiameter, smallerDiameter);
        g2d.setColor(MainFrame.TEXT_COLOR);
        g2d.setFont(new Font("Arial", Font.BOLD, smallerDiameter / 5));
        String text = ""+(int)(consumedCalories>dailyCalories?consumedCalories:dailyCalories)+" kcal";
        FontMetrics metrics = g2d.getFontMetrics();
        int textX = newX + (smallerDiameter - metrics.stringWidth(text)) / 2;
        int textY = newY + ((smallerDiameter - metrics.getHeight()) / 2) + metrics.getAscent();
        g2d.drawString(text, textX, textY);
    }
}