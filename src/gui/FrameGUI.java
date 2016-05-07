package gui;

import java.awt.Component;
import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JTextField;

import mechanics.MembraneImage;

public class FrameGUI extends JFrame{
	JFileChooser fileChooser;
	JComboBox selector;
	JButton submit, chooseFile;
	String[] selections = {"Top", "Bottom", "Both"};
	JTextField fileSelectLabel;
	
	public FrameGUI(int width, int height) {
		super("Membrane Tracer");
		setBounds(0, 0, width, height);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		
		fileSelectLabel = new JTextField("Choose your desired image:");
		fileSelectLabel.setEditable(false);
		JTextField selectionLabel = new JTextField("Choose trace mode:");
		selectionLabel.setEditable(false);
		fileChooser = new JFileChooser();
		fileChooser.addActionListener(new AfterChooseListener());
		chooseFile = new JButton("Click here to choose");
		chooseFile.addActionListener(new ChooseListener(this));
		selector = new JComboBox<String>(selections);
		submit = new JButton("Trace");
		SubmitListener listener = new SubmitListener(this);
		submit.addActionListener(listener);
		
		Container content = getContentPane();
		content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
		content.add(fileSelectLabel);
		content.add(chooseFile);
		content.add(selectionLabel);
		content.add(selector);
		content.add(submit);
		
		setVisible(true);
	}
		
	private class SubmitListener implements ActionListener {
		Container frame;
		
		public SubmitListener(Container frame) {
			this.frame = frame;
		}
		
		public void actionPerformed(ActionEvent e) {
			if (fileChooser.getSelectedFile() != null)
				trace();
			else
				JOptionPane.showMessageDialog(frame, "Error: Please choose an image.");
		}
		
		public void trace() {
			File input = fileChooser.getSelectedFile();
			File output = new File("output.png");
			MembraneImage img = new MembraneImage(input);
			switch (selector.getSelectedIndex()) {
				case 0:
					img.trace(0, img.getWidth(), true);
					break;
				case 2:
					img.trace(0, img.getWidth(), true);
				case 1:
					img.trace(0, img.getWidth(), false);
					break;
			}
			try {
				ImageIO.write(img.getTracedImage(), "png", output);
			} catch (IOException ex) {
				ex.printStackTrace();
				JOptionPane.showMessageDialog(frame, "Error: Could not write file \"output.png\".");
			}
		}
	}
	
	private class ChooseListener implements ActionListener {
		Component frame;
		
		public ChooseListener(Component frame) {
			this.frame = frame;
		}
		
		public void actionPerformed(ActionEvent e) {
			fileChooser.showOpenDialog(frame);
		}
	}
	
	private class AfterChooseListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			fileSelectLabel.setText("Choose your desired image: " + fileChooser.getSelectedFile().getName());
		}
	}
}
