package test;

import java.awt.image.BufferedImage;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import javax.imageio.ImageIO;

public class TestColors {
	public static void main(String[] args) {
		try {
			File file = new File("input.jpg");
			BufferedImage image = ImageIO.read(file);
			PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter("colors.txt")));

			for (int y = 0; y < image.getHeight(); y++) {
				int x = 455;
				final int clr = image.getRGB(x, y);
				final int red = (clr & 0x00ff0000) >> 16;
				final int green = (clr & 0x0000ff00) >> 8;
				final int blue = clr & 0x000000ff;

				// Color Red get cordinates
				/*
				 * if (red == 255) { out.println(String.format(
				 * "Coordinate %d %d", x, y)); } else if (red > 50){
				 * out.println(String.format("Coordinate %d %d", x, y));
				 * out.println("Red Color value = " + red); out.println(
				 * "Green Color value = " + green); out.println(
				 * "Blue Color value = " + blue); out.println(""); }
				 */
				if (red > green && red > 40) {
					out.println(String.format("Coordinate %d %d", x, y));
					out.println("Red Color value = " + red);
					out.println("Green Color value = " + green);
					out.println("Blue Color value = " + blue);
					out.println("");
				}
			}

			out.close();
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}
}
