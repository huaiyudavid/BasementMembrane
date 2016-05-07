package test;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.File;

import javax.swing.JFrame;

import mechanics.MembraneImage;

public class TestGraphics extends JFrame{
	public static void main(String[] args) {
		TestGraphics ayyy = new TestGraphics();
	}
	
	private BufferedImage resultImg;
	
	public TestGraphics() {
		super("Testing");
		setBounds(0, 0, 1000, 1000);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		try {
			File file = new File("input.jpg");
			MembraneImage image = new MembraneImage(file);
			image.trace(0, 1387, true);
			resultImg = image.getTracedImage();
			AffineTransform at = AffineTransform.getScaleInstance(.5, .5);
			AffineTransformOp atp = new AffineTransformOp(at, AffineTransformOp.TYPE_BILINEAR);
			resultImg = atp.filter(resultImg, null);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		setVisible(true);
	}
	 public void paint(Graphics g) {
		 g.drawImage(resultImg, 0, 0, null);
	 }
}
