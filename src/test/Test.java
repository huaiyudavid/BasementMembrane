package test;

import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.File;

import javax.imageio.ImageIO;

import mechanics.MembraneImage;

public class Test {
	public static void main(String[] args) {
		try {
			File file = new File("input.jpg");
			MembraneImage image = new MembraneImage(file);
			image.trace(0, image.getWidth(), true);
			image.trace(0, image.getWidth(), false);
			BufferedImage resultImg = image.getTracedImage();
			File output = new File("output.png");
			ImageIO.write(resultImg, "png", output);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
}
