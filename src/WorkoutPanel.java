import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

public class WorkoutPanel extends JPanel implements AdjustmentListener {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static final int WIDTH = MainFrame.WIDTH;
	private static final int HEIGHT = MainFrame.HEIGHT * 3 / 4;
	private static final int SPACE = 10;
	private List<String> exercices;
	private JPanel scrollablePanel;
	private JScrollPane scrollPane;
	private JTextField searchField;

	public WorkoutPanel() {
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
				filterExercices(searchField.getText());
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
				g.fillRect(getWidth() / 2 - getWidth() / 10, getHeight() / 5, getWidth() / 5, getHeight() * 3 / 5);
				g.fillRect(getWidth() / 5, getHeight() / 2 - getHeight() / 10, getWidth() * 3 / 5, getHeight() / 5);
			}
		};

		button.setPreferredSize(new Dimension(20, 20));
		button.setBorder(null);
		button.setContentAreaFilled(false);
		button.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				addExercice();

			}
		});
		searchPanel.add(button);

		add(searchPanel, BorderLayout.NORTH);

		// Add a document listener to the search field
		searchField.getDocument().addDocumentListener(new DocumentListener() {
			@Override
			public void insertUpdate(DocumentEvent e) {
				updateExercicePanel();
			}

			@Override
			public void removeUpdate(DocumentEvent e) {
				updateExercicePanel();
			}

			@Override
			public void changedUpdate(DocumentEvent e) {
				updateExercicePanel();
			}

			private void updateExercicePanel() {
				String query = searchField.getText().trim().toLowerCase();
				scrollablePanel.removeAll();
				for (String exercice : exercices) {
					if (exercice.toLowerCase().contains(query)) {
						addExerciceToPanel(exercice);
					}
				}
				scrollablePanel.revalidate();
				scrollablePanel.repaint();
			}
		});

		scrollablePanel = new JPanel();
		scrollablePanel.setBackground(MainFrame.FIRST_COLOR);
		scrollablePanel.setLayout(new BoxLayout(scrollablePanel, BoxLayout.Y_AXIS));
		loadExercices();
		scrollablePanel.setPreferredSize(new Dimension(WIDTH, exercices.size() * 30));

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

	private void addExerciceToPanel(String exercice) {
		JPanel panel = new JPanel();
		panel.setBackground(MainFrame.FIRST_COLOR);
		panel.setMaximumSize(new Dimension(WIDTH, 30));
		JPanel smallerPanel = new JPanel();
		smallerPanel.setBackground(MainFrame.THIRD_COLOR);
		smallerPanel.setPreferredSize(new Dimension(WIDTH - 2 * SPACE, 30));
		smallerPanel.add(new JLabel(exercice.split(":")[0]));
		panel.add(smallerPanel);
		panel.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				//write code here
			}
		});
		scrollablePanel.add(panel);
	}

	private void addExercice() {
		String input = "Name:";
		JPanel dialogPanel = new JPanel(new GridLayout(1, 1));
		JTextField field = new JTextField();
		field.setText("Name");
		dialogPanel.add(new JLabel(input));
		dialogPanel.add(field);

		int option = JOptionPane.showConfirmDialog(null, dialogPanel, "Add Exercice", JOptionPane.OK_CANCEL_OPTION,
				JOptionPane.PLAIN_MESSAGE);
		if (option == JOptionPane.OK_OPTION) {
				for (String line : exercices) {
					if (line.toLowerCase().startsWith(field.getText().toLowerCase() + ":")) {
						return;
					}
				}
			String newLine = "";
			char[] array=field.getText().toCharArray();
			boolean capitalized=true;
			for (int i = 0; i < array.length; i++) {
				if(capitalized)newLine+=Character.toString(array[i]).toUpperCase();
				else newLine+=Character.toString(array[i]).toLowerCase();
				if(Character.toString(array[i]).equals(" "))capitalized=true;
				else capitalized=false;
			}
			exercices.add(newLine+":");
			updateExercicesFile();
			scrollablePanel.removeAll();
			for (String line : exercices) {
				addExerciceToPanel(line);
			}
			scrollablePanel.revalidate();
			scrollablePanel.repaint();
		}
	}

	private void updateExercicesFile() {
		// Sort the list
		Collections.sort(exercices);

		// Save the sorted list to a file
		try {
			BufferedWriter writer = new BufferedWriter(new FileWriter("src/exercices"));
			for (String s : exercices) {
				writer.write(s + "\n");
			}
			writer.close();
		} catch (IOException e) {
			System.err.println("Error saving file: " + e.getMessage());
		}
	}

	private void loadExercices() {
		exercices = new ArrayList<>();
		try (BufferedReader reader = new BufferedReader(new FileReader("src/exercices"))) {
			String line;
			while ((line = reader.readLine()) != null) {
				exercices.add(line);
				addExerciceToPanel(line);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void filterExercices(String query) {
		scrollablePanel.removeAll();
		for (String exercice : exercices) {
			if (exercice.toLowerCase().contains(query.toLowerCase())) {
				JPanel panel = new JPanel();
				panel.setBackground(Color.WHITE);
				panel.setMaximumSize(new Dimension(WIDTH, 30));
				panel.add(new JLabel(exercice));
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
