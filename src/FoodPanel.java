import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;
import java.util.List;

public class FoodPanel extends JPanel implements AdjustmentListener {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static final int WIDTH = MainFrame.WIDTH/2;
	private static final int SPACE = 10;
	private List<String> foods;
	private JPanel scrollablePanel;
	private JScrollPane scrollPane;
	private JTextField searchField;

	public FoodPanel() {
		setBackground(MainFrame.FIRST_COLOR);
		setLayout(new BorderLayout());

		// Create the search panel
		JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
		searchPanel.setBackground(MainFrame.FIRST_COLOR);
		searchField = new JTextField(20);
		searchField.setText("Search...");
		searchField.addFocusListener(new FocusListener() {
			@Override
			public void focusGained(FocusEvent e) {
				searchField.setText("");
			}

			@Override
			public void focusLost(FocusEvent e) {
				searchField.setText("Search...");
			}
		});

		searchField.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				filterFoods(searchField.getText());
			}
		});
		searchPanel.add(searchField);
		
		JButton button = new JButton() {
		    /**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
		    protected void paintComponent(Graphics g) {
		        super.paintComponent(g);
		        g.setColor(MainFrame.THIRD_COLOR);
		        g.fillOval(0, 0, getWidth(), getHeight());
		        g.setColor(MainFrame.FIRST_COLOR);
		        g.fillRect(getWidth() / 2-getWidth()/10, getHeight() / 5, getWidth() / 5, getHeight() *3/5);
		        g.fillRect(getWidth() / 5, getHeight() / 2-getHeight()/10, getWidth()*3 /5, getHeight() / 5);
		    }
		};

		button.setPreferredSize(new Dimension(20, 20));
		button.setBorder(null);
		button.setContentAreaFilled(false);
		button.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				editOrAddFood(null);
				
			}
		});
		searchPanel.add(button);
		
		add(searchPanel, BorderLayout.NORTH);

		// Add a document listener to the search field
		searchField.getDocument().addDocumentListener(new DocumentListener() {
			@Override
			public void insertUpdate(DocumentEvent e) {
				updateFoodPanel();
			}

			@Override
			public void removeUpdate(DocumentEvent e) {
				updateFoodPanel();
			}

			@Override
			public void changedUpdate(DocumentEvent e) {
				updateFoodPanel();
			}

			private void updateFoodPanel() {
				String query = searchField.getText().trim().toLowerCase();
				scrollablePanel.removeAll();
				for (String food : foods) {
					if (food.toLowerCase().contains(query)) {
						addFoodToPanel(food);
					}
				}
				scrollablePanel.revalidate();
				scrollablePanel.repaint();
			}
		});

		scrollablePanel = new JPanel();
		scrollablePanel.setBackground(MainFrame.FIRST_COLOR);
//		scrollablePanel.setBorder(null);
		scrollablePanel.setLayout(new BoxLayout(scrollablePanel, BoxLayout.Y_AXIS));
		loadFoods();
		scrollablePanel.setPreferredSize(new Dimension(WIDTH, foods.size() * 30));

		// Create the scroll pane and add the content panel to it
		scrollPane = new JScrollPane(scrollablePanel);
		scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		scrollPane.getVerticalScrollBar().setUnitIncrement(10);
		scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		scrollPane.setBorder(null);
		add(scrollPane, BorderLayout.CENTER);

		// Add an adjustment listener to the scroll bar to detect when it is scrolled
		scrollPane.getVerticalScrollBar().addAdjustmentListener(this);
	}
	
	private void addFoodToPanel(String food) {
		JPanel panel = new JPanel();
		panel.setBackground(MainFrame.FIRST_COLOR);
		panel.setMaximumSize(new Dimension(WIDTH, 30));
		JPanel smallerPanel = new JPanel();
		smallerPanel.setBackground(MainFrame.THIRD_COLOR);
		smallerPanel.setPreferredSize(new Dimension(WIDTH - 2 * SPACE, 30));
		smallerPanel.add(new JLabel(food.split(":")[0]));
		panel.add(smallerPanel);
		panel.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				editOrAddFood(food);
			}
		});
		scrollablePanel.add(panel);
	}
	
	private void editOrAddFood(String food) {
	    String[] inputs = { "Name:", "Carbs:", "Protein:", "Fat:", "Sugar:", "Fiber:", "Saturated Fat:",
	        "Cholesterol:", "Sodium:", "Potassium:" };
	    JTextField[] fields = new JTextField[inputs.length];

	    JPanel dialogPanel = new JPanel(new GridLayout(inputs.length, 1));
	    for (int i = 0; i < inputs.length; i++) {
	        fields[i] = new JTextField();
	        fields[i].setText(food==null?i==0?"Name":"0.0":food.split(":")[i]);
	        dialogPanel.add(new JLabel(inputs[i]));
	        dialogPanel.add(fields[i]);
	    }

	    int option = JOptionPane.showConfirmDialog(null, dialogPanel, "Nutrition Values",
	        JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
	    if (option == JOptionPane.OK_OPTION) {
	        String[] correctedFood = food==null?new String[10]:food.split(":");
	        if (!fields[0].getText().equals(correctedFood[0])) {
	            // Check for duplicate food names
	        	
	            boolean nameExists = false;
	            for (String line : foods) {
	                if (line.toLowerCase().startsWith(fields[0].getText().toLowerCase()+":")) {
	                    nameExists = true;
	                    break;
	                }
	            }
	            if (nameExists) {
	                int result = JOptionPane.showConfirmDialog(null, "A food with this name already exists. Do you want to override it?",
	                    "Warning", JOptionPane.YES_NO_OPTION, JOptionPane.PLAIN_MESSAGE);
	                if (result == JOptionPane.NO_OPTION) {
	                    return; // Do not add the food to the list
	                }
	            }
	            correctedFood[0] = "";
				char[] array=fields[0].getText().toCharArray();
				boolean capitalized=true;
				for (int i = 0; i < array.length; i++) {
					if(capitalized)correctedFood[0]+=Character.toString(array[i]).toUpperCase();
					else correctedFood[0]+=Character.toString(array[i]).toLowerCase();
					if(Character.toString(array[i]).equals(" "))capitalized=true;
					else capitalized=false;
				}
	        }
	        for (int i = 1; i < inputs.length; i++) {
	            if (fields[i].getText().matches("\\d+(\\.\\d+)?")&&(correctedFood[i]==null||Double.parseDouble(fields[i].getText()) != Double.parseDouble(correctedFood[i]))) {
	                correctedFood[i] = fields[i].getText();
	            }
	            else if(!fields[i].getText().matches("\\d+(\\.\\d+)?")) {
	                JOptionPane.showMessageDialog(dialogPanel, "Invalid Nutrition Value(s)!", "Error", JOptionPane.ERROR_MESSAGE);
	            }
	        }
	        String newLine = String.join(":", correctedFood);
	        foods.add(newLine);
	        if(food!=null)foods.remove(food);
	        updateFoodsFile();
	        scrollablePanel.removeAll();
	        for (String line : foods) {
	            addFoodToPanel(line);
	        }
	        scrollablePanel.revalidate();
	        scrollablePanel.repaint();
	    }
	}

	private void updateFoodsFile() {
		// Sort the list
		Collections.sort(foods);

		// Save the sorted list to a file
		try {
			BufferedWriter writer = new BufferedWriter(new FileWriter("src/foods"));
			for (String s : foods) {
				writer.write(s + "\n");
			}
			writer.close();
		} catch (IOException e) {
			System.err.println("Error saving file: " + e.getMessage());
		}
	}

	private void loadFoods() {
		foods = new ArrayList<>();
		try (BufferedReader reader = new BufferedReader(new FileReader("src/foods"))) {
			String line;
			while ((line = reader.readLine()) != null) {
				foods.add(line);
				addFoodToPanel(line);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void filterFoods(String query) {
		scrollablePanel.removeAll();
		for (String food : foods) {
			if (food.toLowerCase().contains(query.toLowerCase())) {
				JPanel panel = new JPanel();
				panel.setBackground(Color.WHITE);
				panel.setMaximumSize(new Dimension(WIDTH, 30));
				panel.add(new JLabel(food));
				scrollablePanel.add(panel);
			}
		}
		scrollablePanel.revalidate();
		scrollablePanel.repaint();
	}

	@Override
	public void adjustmentValueChanged(AdjustmentEvent e) {
		scrollablePanel.repaint();
	}

}