/*
 * Notes:
 * many of the variables used are defined in the surrounding class and thus are not declared here
 * they are as follows:
 * "original" is the BufferedImage that stores the original picture with the cell membrane
 * "tracedImage" is the BufferedImage that stores the result of the tracing
 * "g" is the Graphics object that draws to tracedImage
 * "visited" is a boolean array the same size as original and tracedImage and indicates whether a pixel has been visited yet
 * distance(x, y, a, b) is simply the distance formula, implemented elsewhere
 * Pixel is a class used to store data (x, y, origX, origY as defined in recursiveTrace)
 * TOLERANCE, COLOR_TOLERANCE, and GREEN_TOLERANCE are constants defined in the surrounding class
 */


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