import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class SettingsPanel extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public static int WIDTH = MainFrame.WIDTH;
	public static int HEIGHT = MainFrame.HEIGHT * 3 / 4;
	public int space = HEIGHT / 30;
	public int componentHeight = (HEIGHT - space) / 3 - space;
	public int componentWidth = (WIDTH - space) / 2 - space;
	public BufferedImage profileImage;
	public JPanel circlePanel;

	public SettingsPanel() {
		setBackground(MainFrame.FIRST_COLOR);
		setLayout(null);

		try {
			profileImage = ImageIO.read(new File("src/images/profileImage.png"));
		} catch (IOException e) {
			try {
				profileImage = ImageIO.read(new File("src/images/profileImage.jpg"));
			} catch (IOException e2) {
				e2.printStackTrace();
			}
		}

		JPanel topPanel = new JPanel();
		topPanel.setBackground(MainFrame.THIRD_COLOR);
		topPanel.setLayout(null);
		topPanel.setBounds(space, space, WIDTH - 2 * space, componentHeight);

		circlePanel = new JPanel() {
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
			protected void paintComponent(Graphics g) {
				super.paintComponent(g);
				// Draw the circle shape
				g.setColor(Color.WHITE);
				g.fillOval(0, 0, getWidth(), getHeight());
				if (profileImage != null) {
					g.drawImage(profileImage, circlePanel.getX(), circlePanel.getY(), circlePanel.getWidth(),
							circlePanel.getHeight(), null);
				}
			}
		};
		circlePanel.setBackground(MainFrame.THIRD_COLOR);
		circlePanel.setBounds(topPanel.getWidth() - componentHeight, componentHeight / 10, componentHeight * 4 / 5,
				componentHeight * 4 / 5);

		circlePanel.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				// Prompt the user to select an image file
				JFileChooser fileChooser = new JFileChooser();
				fileChooser.showOpenDialog(SettingsPanel.this);
				File selectedFile = fileChooser.getSelectedFile();
				if (selectedFile != null) {
					try {
						// Read the selected image file and save it to a BufferedImage
						profileImage = ImageIO.read(selectedFile);
						// Repaint the circle panel to display the new image
						circlePanel.repaint();
						// Save the image to a file in the "src/images" directory
						String fileName = selectedFile.getName();
						String fileExtension = fileName.substring(fileName.lastIndexOf(".") + 1);
						File outputFile = new File("src/images/profileImage." + fileExtension);
						ImageIO.write(profileImage, fileExtension, outputFile);
					} catch (IOException ex) {
						ex.printStackTrace();
					}
				}
			}
		});

		topPanel.add(circlePanel);

		JPanel leftPanel = new JPanel();
		leftPanel.setBackground(MainFrame.THIRD_COLOR);
		leftPanel.setLayout(null);
		leftPanel.setBounds(space, topPanel.getY() + componentHeight + space, componentWidth,
				2 * componentHeight + space);

		JPanel rightPanel = new JPanel();
		rightPanel.setBackground(MainFrame.THIRD_COLOR);
		rightPanel.setLayout(null);
		rightPanel.setBounds(leftPanel.getX() + leftPanel.getWidth() + space, topPanel.getY() + componentHeight + space,
				componentWidth, componentHeight * 2 + space);

		add(topPanel);
		add(leftPanel);
		add(rightPanel);
	}

}
