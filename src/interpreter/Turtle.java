package interpreter;

/**
 * Turtle class which indicate the state of the turtle on screen.
 * 
 * @author David Matuszek
 * @author Xiaolu Xu
 * @version March 30, 2009
 */
public class Turtle {
    private DrawingArea canvas;
    private double x;
    private double y;
    private double degrees;
    private boolean penIsDown;
    private int paintDelay;
    private boolean paused;
    
    /**
     * Creates a Turtle in the center of the specified DrawingCanvas,
     * facing right.
     * 
     * @param canvas The DrawingCanvas for this turtle.
     */
    public Turtle(DrawingArea canvas) {
        this.canvas = canvas;
        initialize();
    }

    /**
     * Make sure this turtle (and its associated canvas)
     * is in a known initial state.
     */
    public void initialize() {
        penIsDown = true;
        paintDelay = 0;
        paused = false;
        home();
    }
    
    /**
     * Make the turtle jump to the specified position.
     * 
     * @param newX x coordinates
     * @param newY y coordinates
     */
    public void setPosition(double newX, double newY) {
        x = newX + canvas.getWidth() / 2.0;
        y = canvas.getHeight() / 2.0 - newY;
        finish();
    }
    
    /**
     * gets the x coordinate of the turtle.
     * 
     * @return x coordinate of the turtle. 
     */
    public double getX() {
        return x - canvas.getWidth() / 2.0;
    }
    
    /**
     * gets the y coordinate of the turtle.
     * 
     * @return y coordinate of the turtle. 
     */
    public double getY() {
        return canvas.getHeight() / 2.0 - y;
    }
    
    /**
     * Moves this Turtle forward the specified distance.
     * 
     * @param distance The approximate number of pixels to move.
     */
    public void forward(double distance) {
        // Compute this Turtle's new location
        double newX = x + computeDeltaX(distance, degrees);
        double newY = y + computeDeltaY(distance, degrees);
        if (penIsDown) {
            canvas.addCommand(new DrawLineCommand(x, y, newX, newY));
        }
        x = newX;
        y = newY;
        finish();
    }
    
    /**
     * Turns this Turtle to the right by the specified angle.
     * 
     * @param angle The number of degrees to turn to the right.
     */
    public void right(double angle) {
        degrees = bringIntoRange(degrees + angle);
        finish();
    }

    /**
     * Turns this Turtle to the left by the specified angle.
     * 
     * @param angle The number of degrees to turn to the left.
     */
    public void left(double angle) {
        degrees = bringIntoRange(degrees - angle);
        finish();
    }
    
    /**
     * Sets the degrees that this turtle faces. 
     * @param degrees the new degrees turtle faces.
     */
    public void face(double degrees) {
        this.degrees = bringIntoRange(-degrees);
        finish();
    }
    
    /**
     * Given a number of degrees, adjust the number to be in
     * the range 0 to 360.
     * @param oldDegrees Some number of degrees.
     * @return An equivalent number of degrees.
     */
    private static double bringIntoRange(double oldDegrees) {
        double newDegrees = oldDegrees;
        while (newDegrees < 0) newDegrees += 360;
        while (newDegrees > 360) newDegrees -= 360;
        return newDegrees;
    }
    
    /**
     * Moves this Turtle to the center of the drawing area, facing right.
     */
    public void home() {
        x = canvas.getWidth() / 2.0;
        y = canvas.getHeight() / 2.0;
        degrees = 0;
        finish();
    }
    
    /**
     * Tells this turtle to raise the pen.
     */
    public void penup() {
        penIsDown = false;
    }

    /**
     * Tells this turtle to lower the pen.
     */
    public void pendown() {
        penIsDown = true;
    }
    
    /**
     * Tells this turtle which color pen to use.
     * 
     * @param colorName The name of the color to use.
     */
    public void color(String colorName) {
        canvas.addCommand(new ColorCommand(colorName));
    }
    
    /**
     * Tells this turtle which color pen to use.
     * @param number The numeric value of the color.
     */
    public void color(int number) {
        canvas.addCommand(new ColorCommand(number));
    }
    
    /**
     * Tells this turtle how log to pause between turtle operations.
     * 
     * @param ms The number of milliseconds (1/1000s of a second)
     * to pause.
     */
    public void setPaintDelay(int ms) {
        paintDelay = ms;
    }

    
// ------------------------ private helper methods
    
    /**
     * Computes how much to move to add to this Turtle's x-coordinate,
     * in order to displace the Turtle by (approximately) <code>distance</code>
     * pixels in direction <code>degrees</code>.
     * 
     * @param distance The distance to move.
     * @param degrees The direction in which to move.
     * @return The amount to be added to the x-coordinate.
     */
    private static double computeDeltaX(double distance, double degrees) {
        double radians = Math.toRadians(degrees);
        return distance * Math.cos(radians);
    }
    
    /**
     * Computes how much to move to add to this Turtle's y-coordinate,
     * in order to displace the Turtle by (approximately) <code>distance</code>
     * pixels in direction <code>degrees</code>.
     * 
     * @param distance The distance to move.
     * @param degrees The direction in which to move.
     * @return The amount to be added to the y-coordinate.
     */
    private static double computeDeltaY(double distance, double degrees) {
        double radians = Math.toRadians(degrees);
        return distance * Math.sin(radians);
    }
    
    /**
     * Updates the drawing area. This method should be called
     * at the conclusion of every Turtle command.
     */
    private void finish() {
        tellCanvasWhereIAm();
        do {
            try { Thread.sleep(paintDelay); }
            catch (InterruptedException e) {}
        } while (paused || paintDelay >= 10000);
    }
    
    /**
     * If the parameter is true, causes the turtle to stop whatever
     * it is doing and wait for another call of this method with
     * the parameter false.
     * 
     * @param paused Whether the turtle should be paused.
     */
    public void setPaused(boolean paused) {
        this.paused = paused;
    }

    /**
     * Sets the turtle speed. 
     * @param oldSpeed A value from 0 (stopped) to 100 (full speed).
     */
    public void setSpeed(int oldSpeed) {
        int speed = Math.min(Math.max(oldSpeed, 0), 100);
        paintDelay = 10000 - 100 * speed;
    }
    
    /**
     * Computes the coordinates of the points of a triangle to represent
     * the current position and direction of this Turtle. The triangle
     * points are sent to the DrawingCanvas but do <i>not</i> go into
     * the DrawingCanvas's list of commands.
     */
    private void tellCanvasWhereIAm() {
        int x1 = (int) (x + computeDeltaX(12, degrees));
        int y1 = (int) (y + computeDeltaY(12, degrees));
        int x2 = (int) (x + computeDeltaX(6, degrees - 135));
        int y2 = (int) (y + computeDeltaY(6, degrees - 135));
        int x3 = (int) (x + computeDeltaX(6, degrees + 135));
        int y3 = (int) (y + computeDeltaY(6, degrees + 135));
        canvas.setTurtleData(x1, y1, x2, y2, x3, y3);
    }
}
