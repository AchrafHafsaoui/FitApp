import java.awt.Color;
import java.awt.Graphics;
import javax.swing.JPanel;

public class RoundedPanel extends JPanel {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int arcWidth;
    private int arcHeight;

    public RoundedPanel(int arcWidth, int arcHeight) {
        this.arcWidth = arcWidth;
        this.arcHeight = arcHeight;
        setBackground(Color.WHITE);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.setColor(getBackground());
        g.fillRoundRect(0, 0, getWidth(), getHeight(), arcWidth, arcHeight);
    }

}