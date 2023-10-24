import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

public class CaloriesPanel extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public static int WIDTH = MainFrame.WIDTH/2;
	public static int HEIGHT = MainFrame.HEIGHT * 3 / 4;
	public static Color PROTEIN_COLOR = new Color(130, 0, 0);
	public static Color CARBS_COLOR = new Color(65, 80, 110);
	public static Color FAT_COLOR = new Color(210, 180, 135);
	public int space = HEIGHT / 30;
	public int componentHeight = (HEIGHT - space) / 3 - space;
	public int componentWidth = (WIDTH - space) / 2 - space;
	public List<String> foodsList = new ArrayList<>();
	public List<String> mealsHistory = new ArrayList<>();
	public String[] userData;
	public String todaysFood = "";
	public int todaysline;
	double[] eaten = { 0, 0, 0, 0, 0, 0, 0, 0, 0 };;

	public CaloriesPanel() {
		try (BufferedReader reader = new BufferedReader(new FileReader("src/foods"))) {
			String line;
			while ((line = reader.readLine()) != null) {
				foodsList.add(line);
			}
			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

		try {
			List<String> list = Files.readAllLines(Paths.get("src/user_data"));
			userData = list.toArray(new String[0]);
		} catch (IOException e) {
			e.printStackTrace();
		}

		try (BufferedReader reader = new BufferedReader(new FileReader("src/history"))) {
			String line;
			int i = 0;
			while ((line = reader.readLine()) != null) {
				mealsHistory.add(line);
				if (line.contains(LocalDate.now().toString())) {
					todaysFood = line;
					todaysline = i;
				}
				i++;
			}
			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

		double needed = Double.parseDouble(userData[1]);
		calculateEaten(foodsList, todaysFood, eaten);

		setBackground(MainFrame.FIRST_COLOR);
		setLayout(null);

		JPanel stats = new JPanel();
		statsPanel(stats, needed);

		JPanel nutrition = new JPanel();
		nutritionPanel(nutrition, stats.getY(), "Sugar", "Fiber", "Saturated Fat", "Cholesterol", "Sodium",
				"Potassium");

		JPanel food = new JPanel();
		foodPanel(food, nutrition.getX(), nutrition.getWidth(), stats.getY());

		add(stats);
		add(nutrition);
		add(food);

	}

	private void calculateEaten(List<String> list, String day, double[] eaten) {
		Map<String, Double> map = new HashMap<>();
		for (int i = 1; i < day.split(":").length; i++) {
			if (day.split(":")[i].equals(" "))
				continue;
			for (int j = 0; j < day.split(":")[i].split("&").length; j++) {
				if(day.split(":")[i].split("&")[j]=="")continue;
				for (int k = 0; k < day.split(":")[i].split("&")[j].split("@").length; k += 2) {
					if (map.containsKey(day.split(":")[i].split("&")[j].split("@")[k]))
						map.put(day.split(":")[i].split("&")[j].split("@")[k],
								Double.parseDouble(day.split(":")[i].split("&")[j].split("@")[k+1])
										+ map.get(day.split(":")[i].split("&")[j].split("@")[k]));
					else
						map.put(day.split(":")[i].split("&")[j].split("@")[k],
								Double.parseDouble(day.split(":")[i].split("&")[j].split("@")[k+1]));
				}
			}
		}
		for (Entry<String, Double> entry : map.entrySet()) {
			for (String line : list) {
				if (line.contains(entry.getKey())) {
					for (int i = 0; i < eaten.length; i++) {
						eaten[i] += Double.parseDouble(line.split(":")[i + 1]) * entry.getValue();
					}
				}
				;
			}
		}

	}

	private void statsPanel(JPanel stats, double needed) {
		stats.setBackground(MainFrame.THIRD_COLOR);
		stats.setLayout(null);
		stats.setBounds(space, 2*space, WIDTH - 2 * space, componentHeight);
		double total = eaten[0] * 4 + eaten[1] * 4 + eaten[2] * 9;
		JLabel greeting = new JLabel("Hello " + userData[0].split(" ")[0] + ",");
		greeting.setBounds(stats.getHeight() / 10, stats.getHeight() / 10, stats.getWidth() * 3 / 4,
				stats.getHeight() / 5);
		greeting.setForeground(MainFrame.TEXT_COLOR);
		stats.add(greeting);
		JLabel remaining = new JLabel(total <= needed ? "Only " + (int) (needed - total) + "kcal to achieve your goal!"
				: "Exceeded set calories by " + (int) (total - needed) + "kcal!");
		remaining.setBounds(stats.getHeight() / 10, stats.getHeight() / 5, stats.getWidth() * 3 / 4,
				stats.getHeight() / 5);
		remaining.setForeground(MainFrame.TEXT_COLOR);
		stats.add(remaining);

		CircleChart caloriesChart = new CircleChart(needed, eaten[0] * 4, eaten[1] * 4, eaten[2] * 9);
		caloriesChart.setBounds(stats.getWidth() * 3 / 4, stats.getHeight() / 10, stats.getHeight() * 4 / 5,
				stats.getHeight() * 4 / 5);
		stats.add(caloriesChart);

		LineChart carbsChart = new LineChart(Double.parseDouble(userData[2]), eaten[0], CARBS_COLOR, WIDTH / 2,
				stats.getHeight() / 10);
		carbsChart.setBounds(stats.getHeight() / 10, stats.getHeight() * 2 / 5, stats.getWidth() * 3 / 4,
				stats.getHeight() / 5);
		stats.add(carbsChart);

		JLabel carbsLeft = new JLabel((int) (Double.parseDouble(userData[2]) - eaten[0]) + "g Carbs");
		carbsLeft.setBounds(stats.getHeight() / 10 + stats.getWidth() * 11 / 20, stats.getHeight() * 7 / 20,
				stats.getWidth(), stats.getHeight() / 5);
		carbsLeft.setForeground(MainFrame.TEXT_COLOR);
		stats.add(carbsLeft);

		LineChart proteinChart = new LineChart(Double.parseDouble(userData[3]), eaten[1], PROTEIN_COLOR, WIDTH / 2,
				stats.getHeight() / 10);
		proteinChart.setBounds(stats.getHeight() / 10, stats.getHeight() * 3 / 5, stats.getWidth() * 3 / 4,
				stats.getHeight() / 5);
		stats.add(proteinChart);

		JLabel protLeft = new JLabel((int) (Double.parseDouble(userData[3]) - eaten[1]) + "g Protein");
		protLeft.setBounds(stats.getHeight() / 10 + stats.getWidth() * 11 / 20,
				stats.getHeight() * 7 / 20 + stats.getHeight() / 5, stats.getWidth(), stats.getHeight() / 5);
		protLeft.setForeground(MainFrame.TEXT_COLOR);
		stats.add(protLeft);

		LineChart fatChart = new LineChart(Double.parseDouble(userData[4]), eaten[2], FAT_COLOR, WIDTH / 2,
				stats.getHeight() / 10);
		fatChart.setBounds(stats.getHeight() / 10, stats.getHeight() * 4 / 5, stats.getWidth() * 3 / 4,
				stats.getHeight() / 5);
		stats.add(fatChart);

		JLabel fatLeft = new JLabel((int) (Double.parseDouble(userData[4]) - eaten[2]) + "g Fat");
		fatLeft.setBounds(stats.getHeight() / 10 + stats.getWidth() * 11 / 20,
				stats.getHeight() * 7 / 20 + stats.getHeight() * 2 / 5, stats.getWidth(), stats.getHeight() / 5);
		fatLeft.setForeground(MainFrame.TEXT_COLOR);
		stats.add(fatLeft);
	}

	private void foodPanel(JPanel food, int nutritionX, int nutritionWidth, int statsY) {
		food.setBackground(MainFrame.THIRD_COLOR);
		food.setLayout(null);
		food.setBounds(nutritionX + nutritionWidth + space, statsY + componentHeight + space, componentWidth,
				componentHeight * 2 + space);

		JPanel breakfast = new JPanel();
		breakfast.setBounds(0, 0, food.getWidth() / 2, food.getHeight() / 2);
		mealPanel(breakfast, todaysFood, "Breakfast");
		JPanel lunch = new JPanel();
		lunch.setBounds(food.getWidth() / 2, 0, food.getWidth() / 2, food.getHeight() / 2);
		mealPanel(lunch, todaysFood, "Lunch");
		JPanel dinner = new JPanel();
		dinner.setBounds(0, food.getHeight() / 2, food.getWidth() / 2, food.getHeight() / 2);
		mealPanel(dinner, todaysFood, "Dinner");
		JPanel snacks = new JPanel();
		snacks.setBounds(food.getWidth() / 2, food.getHeight() / 2, food.getWidth() / 2, food.getHeight() / 2);
		mealPanel(snacks, todaysFood, "Snacks");

		JPanel separator1 = new JPanel();
		separator1.setBackground(MainFrame.FIRST_COLOR);
		separator1.setBounds(food.getWidth() / 2 - food.getWidth() / 100, food.getHeight() / 15, food.getWidth() / 100,
				food.getHeight() * 13 / 15);
		JPanel separator2 = new JPanel();
		separator2.setBackground(MainFrame.FIRST_COLOR);
		separator2.setBounds(food.getWidth() / 15, food.getHeight() / 2 - separator1.getWidth() / 2,
				food.getWidth() * 13 / 15, separator1.getWidth());

		food.add(breakfast);
		food.add(lunch);
		food.add(dinner);
		food.add(snacks);
		food.add(separator1);
		food.setComponentZOrder(separator1, 0);
		food.setComponentZOrder(separator2, 0);
	}

	private void nutritionPanel(JPanel nutrition, int statsY, String... strings) {
		nutrition.setBackground(MainFrame.THIRD_COLOR);
		nutrition.setLayout(null);
		nutrition.setBounds(space, statsY + componentHeight + space, componentWidth, 2 * componentHeight + space);

		for (int i = 0; i < 6; i++) {
			JPanel panel = new JPanel();
			panel.setLayout(null);
			panel.setBounds(0, i * nutrition.getHeight() / 6, nutrition.getWidth(), nutrition.getHeight() / 6);
			panel.setBackground(MainFrame.THIRD_COLOR);
			panel.setPreferredSize(new Dimension(nutrition.getWidth(), nutrition.getHeight() / 6));
			panel.setToolTipText((int) eaten[3 + i] + " out of " + Integer.parseInt(userData[5 + i]));
			JLabel element = new JLabel(strings[i] + "");
			element.setHorizontalAlignment(SwingConstants.RIGHT);
			element.setForeground(MainFrame.TEXT_COLOR);
			element.setBounds(panel.getWidth() * 17 / 30, panel.getHeight() / 4, panel.getWidth() / 3,
					panel.getHeight() / 4);
			panel.add(element);
			LineChart chart = new LineChart(Double.parseDouble(userData[5 + i]), eaten[3 + i], new Color(180, 180, 180),
					panel.getWidth() * 4 / 5, panel.getHeight() / 8);
			chart.setBounds(panel.getWidth() / 10, panel.getHeight() / 2, panel.getWidth() * 4 / 5,
					panel.getHeight() / 4);
			panel.add(chart);
			nutrition.add(panel);
		}
	}

	private void mealPanel(JPanel panel, String line, String name) {
		panel.setBackground(MainFrame.THIRD_COLOR);
		panel.setLayout(null);
		JLabel title = new JLabel(name);
		title.setFont(new Font(title.getFont().getName(), Font.BOLD, 16));
		title.setForeground(MainFrame.TEXT_COLOR);
		title.setHorizontalAlignment(SwingConstants.CENTER);
		title.setBounds(0, 0, panel.getWidth(), panel.getHeight() / 6);
		panel.add(title);

		JButton addButton = new JButton(){
		    /**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
		    protected void paintComponent(Graphics g) {
		        super.paintComponent(g);
		        g.setColor(MainFrame.FIRST_COLOR);
		        g.fillOval(0, 0, getWidth(), getHeight());
		        g.setColor(MainFrame.THIRD_COLOR);
		        g.fillRect(getWidth() / 2-getWidth()/10, getHeight() / 5, getWidth() / 5, getHeight() *3/5);
		        g.fillRect(getWidth() / 5, getHeight() / 2-getHeight()/10, getWidth()*3 /5, getHeight() / 5);
		    }
		};;
		addButton.setBounds(panel.getWidth() * 4 / 5, title.getHeight() / 5, panel.getWidth() / 8,
				panel.getWidth() / 8);
		addButton.setBorder(null);
		addButton.setContentAreaFilled(false);
//		addButton.setIcon(new Icon() {
//			Image image;
//
//			@Override
//			public void paintIcon(Component c, Graphics g, int x, int y) {
//				try {
//					image = ImageIO.read(new File("src/images/add4.png"));
//				} catch (IOException e1) {
//					e1.printStackTrace();
//				}
//				g.drawImage(image, 0, 0, getIconWidth(), getIconWidth(), null);
//			}
//
//			@Override
//			public int getIconWidth() {
//				return panel.getWidth() / 7;
//			}
//
//			@Override
//			public int getIconHeight() {
//				return panel.getWidth() / 7;
//			}
//		});

		addButton.addActionListener(e -> {
			JPanel inputPanel = new JPanel(new GridLayout(2, 1));
			inputPanel.add(new JLabel("Food:"));
			String[] foods = readFoodsFromFile();
			JComboBox<String> foodComboBox = new JComboBox<String>(foods);
			inputPanel.add(foodComboBox);
			inputPanel.add(new JLabel("Quantity:"));
			JTextField quantityField = new JTextField(5);
			inputPanel.add(quantityField);
			
			
			int result = JOptionPane.showConfirmDialog(panel, inputPanel, "Add Food", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
			if (result == JOptionPane.OK_OPTION) {
				String selectedFood = (String) foodComboBox.getSelectedItem();
				String quantityStr = quantityField.getText();
				if (quantityStr.matches("\\d+(\\.\\d+)?")) {
					double quantity = Double.parseDouble(quantityStr);
					updateHistoryFile(todaysline, name, selectedFood, quantity);
					Container parent = MainFrame.mainPanel.getParent();
					parent.remove(MainFrame.mainPanel);
					MainFrame.mainPanel = new CaloriesPanel();
					MainFrame.mainPanel.setBounds(0, MainFrame.HEIGHT / 12, MainFrame.WIDTH, MainFrame.HEIGHT * 3 / 4);
					parent.add(MainFrame.mainPanel);
					parent.repaint();
				} else {
					JOptionPane.showMessageDialog(panel, "Invalid Quantity!", "Error", JOptionPane.ERROR_MESSAGE);
				}
			}
		});

		panel.add(addButton);

		String meal;
		try {
			meal = line.split(":")[getMealIndex(name)];
		} catch (Exception e) {
			meal = "";
		}
		for (int j = 0; j < meal.split("&").length; j++) {
			String[] parts = meal.split("&")[j].split("@");
			JLabel label = new JLabel(parts[0]);
			label.setForeground(MainFrame.TEXT_COLOR);
			label.setHorizontalAlignment(SwingConstants.CENTER);
			label.setBounds(panel.getWidth() / 10, title.getHeight() + panel.getHeight() / 6 * j,
					panel.getWidth() * 4 / 5, panel.getHeight() / 6);
			if (parts.length > 1) {
				label.setToolTipText(parts[0] + " (" + parts[1] + "g)");
			}
			label.addMouseListener(new MouseAdapter() {
			    @Override
			    public void mouseClicked(MouseEvent e) {
			        // Display a custom dialog box for quantity input
			        JPanel inputPanel = new JPanel();
			        inputPanel.setLayout(new GridLayout(0, 2));
			        inputPanel.add(new JLabel("New Quantity:"));
			        JTextField quantityField = new JTextField(parts[1], 5);
			        inputPanel.add(quantityField);

			        int result = JOptionPane.showConfirmDialog(panel, inputPanel,
			                "Edit Quantity", JOptionPane.OK_CANCEL_OPTION);

			        if (result == JOptionPane.OK_OPTION) {
			            String newQuantityStr = quantityField.getText();
			            if (newQuantityStr.matches("\\d+(\\.\\d+)?")) {
			                double newQuantity = Double.parseDouble(newQuantityStr);
			                updateHistoryFile(todaysline, name, parts[0], -newQuantity);
			                Container parent = MainFrame.mainPanel.getParent();
							parent.remove(MainFrame.mainPanel);
							MainFrame.mainPanel = new CaloriesPanel();
							MainFrame.mainPanel.setBounds(0, MainFrame.HEIGHT / 12, MainFrame.WIDTH, MainFrame.HEIGHT * 3 / 4);
							parent.add(MainFrame.mainPanel);
							parent.repaint();
			            } else {
			                JOptionPane.showMessageDialog(panel, "Invalid quantity input!", "Error",
			                        JOptionPane.ERROR_MESSAGE);
			            }
			        }
			    }
			});
			panel.add(label);
		}
	}

	private int getMealIndex(String name) {
		switch (name) {
		case "Breakfast":
			return 1;
		case "Lunch":
			return 2;
		case "Dinner":
			return 3;
		case "Snacks":
			return 4;
		default:
			return -1;
		}
	}

	private String[] readFoodsFromFile() {
		try (BufferedReader reader = new BufferedReader(new FileReader("src/foods"))) {
			String line;
			List<String> foods = new ArrayList<>();
			while ((line = reader.readLine()) != null) {
				foods.add(line.split(":")[0]);
			}
			reader.close();
			return foods.toArray(new String[0]);
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}

	private void updateHistoryFile(int day, String mealName, String food, double quantity) {

		String[] meals = mealsHistory.get(day).split(":");
		if (meals[getMealIndex(mealName)].equals(" "))
			meals[getMealIndex(mealName)] = "";
		if(quantity<0) {
			String[] foods=meals[getMealIndex(mealName)].split("&");
			for (int i = 0; i < foods.length; i++) {
				if(foods[i].contains(food)) {
					foods[i]=foods[i].split("@")[0]+"@"+(-quantity);
					meals[getMealIndex(mealName)]=String.join("&", foods);
					break;
				}
			}
		}
		else if(meals[getMealIndex(mealName)].contains(food)&&quantity==0) {
			String[] foods=meals[getMealIndex(mealName)].split("&");
			String[] newFoods=new String[foods.length-1];
			int j=0;
			for (int i = 0; i < foods.length; i++) {
				if(!foods[i].contains(food)) {
					newFoods[j]=foods[i];
					j++;
				}
			}
			meals[getMealIndex(mealName)]=String.join("&", newFoods);
			if(meals[getMealIndex(mealName)].equals(""))meals[getMealIndex(mealName)]=" ";
		}
		else if(meals[getMealIndex(mealName)].contains(food)) {
			String[] foods=meals[getMealIndex(mealName)].split("&");
			for (int i = 0; i < foods.length; i++) {
				if(foods[i].contains(food)) {
					foods[i]=foods[i].split("@")[0]+"@"+(Double.parseDouble(foods[i].split("@")[1])+quantity);
					meals[getMealIndex(mealName)]=String.join("&", foods);
					break;
				}
			}
		}
		else meals[getMealIndex(mealName)] = meals[getMealIndex(mealName)] == "" ? food + "@" + quantity
				: meals[getMealIndex(mealName)] + "&" + food + "@" + quantity;

		String newMeal = String.join(":", meals);
		mealsHistory.add(day, newMeal);
		mealsHistory.remove(day + 1);
		try (BufferedWriter writer = new BufferedWriter(new FileWriter("src/history"))) {
			for (String line : mealsHistory) {
				writer.write(line);
				writer.newLine();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
