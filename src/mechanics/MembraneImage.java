package mechanics;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Stack;

import javax.imageio.ImageIO;

public class MembraneImage {
	private BufferedImage original;
	private int TOLERANCE = 18; // 18
	private int COLOR_TOLERANCE = 60; // 60
	private int GREEN_TOLERANCE = 30;
	private boolean[][] visited;
	private BufferedImage tracedImage;

	public MembraneImage(File imgFile) {
		loadImage(imgFile);
		visited = new boolean[original.getWidth()][original.getHeight()];
		for (int i = 0; i < visited.length; i++) {
			for (int j = 0; j < visited[0].length; j++) {
				visited[i][j] = false;
			}
		}
		tracedImage = new BufferedImage(original.getWidth(), original.getHeight(), BufferedImage.TYPE_INT_ARGB);
	}

	private void loadImage(File imgFile) {
		try {
			original = ImageIO.read(imgFile);
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}

	public int getHeight() {
		return original.getHeight();
	}

	public int getWidth() {
		return original.getWidth();
	}

	public BufferedImage getTracedImage() {
		return tracedImage;
	}

	public void trace(int startWidth, int endWidth, boolean up) {

		int col = startWidth, row = 0;

		if (up) {
			row = 0;
			int color = original.getRGB(col, row);
			int red = (color & 0x00ff0000) >> 16;
			int green = (color & 0x0000ff00) >> 8;

			while ((red < COLOR_TOLERANCE || red <= green) && row < original.getHeight() - 1) {
				row++;
				// System.out.println(original.getHeight());
				// System.out.println("x: " + col + " y: " + row);
				color = original.getRGB(col, row);
				red = (color & 0x00ff0000) >> 16;
				green = (color & 0x0000ff00) >> 8;
			}
		} else {
			row = original.getHeight() - 1;
			int color = original.getRGB(col, row);
			int red = (color & 0x00ff0000) >> 16;
			int green = (color & 0x0000ff00) >> 8;

			while ((red < COLOR_TOLERANCE || red <= green) && row > 0) {
				row--;
				// System.out.println(original.getHeight());
				// System.out.println("x: " + col + " y: " + row);
				color = original.getRGB(col, row);
				red = (color & 0x00ff0000) >> 16;
				green = (color & 0x0000ff00) >> 8;
			}
		}

		iterativeTrace(tracedImage.createGraphics(), col, row, endWidth, up);
		// recursiveTrace(tracedImage.createGraphics(), col, row, col, row,
		// true);

		for (int i = 0; i < visited.length; i++) {
			for (int j = 0; j < visited[0].length; j++) {
				visited[i][j] = false;
			}
		}
	}

	public BufferedImage testImage() {
		Graphics g = original.createGraphics();
		g.setColor(Color.white);
		g.drawLine(0, 0, 100, 100);
		return original;
	}

	// purpose: recursive tracing of cell membrane
	// pre: (origX, origY) is a red point
	// 			visited must be reset for initial call
	//			original has been loaded
	// post: returns whether (x, y) is a red point with appropriate distance;
	// 			if so, colors (x, y) blue and draws blue line to the next red point to the right
	//			and colors that one recursively, prioritizes up;
	//			visited is not reset
	// param: g - used to draw onto a new image
	// 			x, y - coordinates to check / color
	// 			origX, origY - the previous red point
	private boolean recursiveTrace(Graphics g, int x, int y, int origX, int origY) {
		//make sure not to run out of bounds or stray too far from previous red point
		if (y >= visited[0].length || x >= visited.length || visited[x][y]
				|| distance(origX, origY, x, y) > TOLERANCE) {
			return false;
		}

		//obtain color values for original image
		int color = original.getRGB(x, y);
		int red = (color & 0x00ff0000) >> 16;
		int green = (color & 0x0000ff00) >> 8;

		//mark (x, y) as visited
		visited[x][y] = true;
		boolean found = false;

		//red point found, color it blue and set (x, y) to new (origX, origY)
		if (red >= COLOR_TOLERANCE && red > green) {
			g.setColor(Color.BLUE);
			g.drawLine(origX, origY, x, y);
			origX = x;
			origY = y;
			found = true;
		}

		//Recursively color a single blue path, looking up, up-right, right, down-right,
		//and down, returning early when necessary to guarantee only one path
		if (recursiveTrace(g, x, y - 1, origX, origY)) {
			return true;
		}
		if (recursiveTrace(g, x + 1, y - 1, origX, origY)) {
			return true;
		}
		if (recursiveTrace(g, x + 1, y, origX, origY)) {
			return true;
		}
		if (recursiveTrace(g, x + 1, y + 1, origX, origY)) {
			return true;
		}
		if (recursiveTrace(g, x, y + 1, origX, origY)) {
			return true;
		}

		return found;
	}

	//purpose: iterative tracing of cell membrane
	//pre: visited is reset; original image has been loaded
	//		(x, y) is a red point
	//post: tracedImage image has outline of cell membrane in original (red blob) traced in blue
	//		from (x, y) to endWidth;
	//		visited is not reset
	//param: g - used to draw onto tracedImage
	//			x, y - coordinates to begin
	//			endWidth - column to stop drawing at
	//			up - whether to prioritize up or down
	private void iterativeTrace(Graphics g, int x, int y, int endWidth, boolean up) {
		//use a Stack to do an early terminating depth-first search for red points
		//this allows for only one blue path to be drawn
		Stack<Pixel> stack = new Stack<Pixel>();
		
		//put the starting Pixel on the Stack
		//Pixel holds (x, y, origX, origY) as defined in recursiveTrace
		stack.push(new Pixel(x, y, x, y));
		Pixel next = stack.peek();
		
		//keep checking while there are more points to check and we have not
		//hit the end yet
		while (next.x <= endWidth && !stack.isEmpty()) {
			boolean found = false;
			
			//search for the next red point after this one
			while (!stack.isEmpty() && !found) {
				next = stack.pop();
				
				//check bounds, check if already visited, and check if distance is over TOLERANCE
				if (next.x < endWidth && next.y < visited[0].length && next.y >=0 
						&& !visited[next.x][next.y]
						&& distance(next.origX, next.origY, next.x, next.y) <= TOLERANCE) {
					int color = original.getRGB(next.x, next.y);
					int red = (color & 0x00ff0000) >> 16;
					int green = (color & 0x0000ff00) >> 8;
					//(x, y) has now been visited
					visited[next.x][next.y] = true;

					//red point is found
					if (red >= COLOR_TOLERANCE && red > green && green < GREEN_TOLERANCE) {
						//color the line between it and the previous point blue
						g.setColor(Color.BLUE);
						g.drawLine(next.origX, next.origY, next.x, next.y);
						
						//update previous point
						next.origX = next.x;
						next.origY = next.y;
						found = true;
						
						//reset the Stack, no need to search alternative paths
						stack.clear();
					}
					
					if (up) {
						//search the surrounding points, starting with up first
						//(up is added last to the Stack)
						stack.push(new Pixel(next.x, next.y + 1, next.origX, next.origY));
						stack.push(new Pixel(next.x + 1, next.y + 1, next.origX, next.origY));
						stack.push(new Pixel(next.x + 1, next.y, next.origX, next.origY));
						stack.push(new Pixel(next.x + 1, next.y - 1, next.origX, next.origY));
						stack.push(new Pixel(next.x, next.y - 1, next.origX, next.origY));
					} else {
						//search the surrounding points, starting with down first
						//(down is added last to the Stack)
						stack.push(new Pixel(next.x - 1, next.y - 1, next.origX, next.origY));
						stack.push(new Pixel(next.x - 1, next.y + 1, next.origX, next.origY));
						stack.push(new Pixel(next.x, next.y - 1, next.origX, next.origY));
						stack.push(new Pixel(next.x + 1, next.y - 1, next.origX, next.origY));
						stack.push(new Pixel(next.x + 1, next.y, next.origX, next.origY));
						stack.push(new Pixel(next.x + 1, next.y + 1, next.origX, next.origY));
						stack.push(new Pixel(next.x, next.y + 1, next.origX, next.origY));
					}
				}
			}
		}
	}

	private double distance(double x1, double y1, double x2, double y2) {
		return Math.sqrt((x2 - x1) * (x2 - x1) + (y2 - y1) * (y2 - y1));
	}
}
