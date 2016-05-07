package gui;

import java.awt.Dimension;

import javax.swing.JPanel;

public class Display extends JPanel {
	public Display(int width, int height) {
		super();
		setMinimumSize(new Dimension(width, height));
	}
}
