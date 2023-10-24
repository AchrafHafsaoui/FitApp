import java.awt.Color;
import java.awt.Font;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import java.awt.Toolkit;


public class MainFrame extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public static final int HEIGHT = (int) Toolkit.getDefaultToolkit().getScreenSize().getHeight();
	public static final int WIDTH = (int) Toolkit.getDefaultToolkit().getScreenSize().getWidth();
	public static final Color FIRST_COLOR = new Color(0x433751);
	public static final Color SECOND_COLOR = new Color(0x77ABB7);
	public static final Color THIRD_COLOR = new Color(0x748B9C);
	public static final Color TEXT_COLOR = FIRST_COLOR;
	public static JPanel mainPanel;

	public MainFrame() {
		setTitle("Athleteo");
		setIconImage(new ImageIcon("src/images/fit.png").getImage());
		setSize(WIDTH+8, HEIGHT+8);
		setLocationRelativeTo(null);
		setResizable(false);
		setLayout(null);

		JPanel topPanel = new JPanel();
		topPanel.setBackground(SECOND_COLOR);
		JLabel title = new JLabel(getTitle().toUpperCase());
		title.setFont(new Font("Helvetica", Font.BOLD, HEIGHT/10));
		title.setForeground(FIRST_COLOR);
		topPanel.add(title);

		mainPanel = new JPanel();
		mainPanel.setLayout(null);
		
		JPanel lowerPanel = new JPanel();
		lowerPanel.setBackground(FIRST_COLOR);

		topPanel.setBounds(0, 0, WIDTH, HEIGHT / 8);
		add(topPanel);
		mainPanel.setBounds(0, HEIGHT / 8, WIDTH, HEIGHT * 3 / 4);
		CaloriesPanel caloriesPanel=new CaloriesPanel();
		caloriesPanel.setBounds(0, 0, mainPanel.getWidth()/2, mainPanel.getHeight());
		FoodPanel foodPanel=new FoodPanel();
		foodPanel.setBounds(mainPanel.getWidth()/2, 0, mainPanel.getWidth()/2, mainPanel.getHeight());
		mainPanel.add(caloriesPanel);
		mainPanel.add(foodPanel);
		add(mainPanel);

		lowerPanel.setBounds(0, HEIGHT*7/8, WIDTH, HEIGHT / 8);
		add(lowerPanel);
		
	}
}
