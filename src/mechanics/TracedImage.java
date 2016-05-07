package mechanics;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;

public class TracedImage {
	private BufferedImage tracedimg;
	
	public TracedImage(int width, int height) {
		tracedimg = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		Graphics g = tracedimg.createGraphics();
		g.setColor(Color.BLUE);
		g.drawLine(0, 0, 100, 100);
	}

	public BufferedImage getImage() {
		return tracedimg;
	}
	
	public void draw(Graphics g) {
		g.drawImage(tracedimg, 0, 0, null);
	}
}
