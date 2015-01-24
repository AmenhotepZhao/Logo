package interpreter;
import java.awt.*;

/**
 * Sets a color based on the Logo program instructions
 * 
 * @author David Matuszek
 * @version March 31, 2009
 */
public class ColorCommand implements TurtleCommand {
    Color color;
    
    /**
     * Constructs a ColorCommand.
     * @param colorName The name of the color.
     */
    public ColorCommand(String colorName) {
    	switch (colorName) {
    		case "red" :
    			color = Color.RED;
    			break;
    		case "blue" :
    			color = Color.BLUE;
    			break;
    		case "black" :
    			color = Color.BLACK;
    			break;
    		case "orange" :
    			color = Color.ORANGE;
    			break;
    		case "purple" :
    			color = new Color(128, 0, 255);
    			break;
    		case "gray" :
    			color = Color.GRAY;
    			break;
    		case "yellow" :
    			color = Color.YELLOW;
    			break;
    		case "magenta" :
    			color = Color.MAGENTA;
    			break;
    		case "while" :
    			color = Color.WHITE;
    			break;
    		case "green" :
    			color = Color.GREEN;
    			break;
    		case "pink" :
    			color = Color.PINK;
    			break;
    		case "brown" :
    			color = new Color(128, 64, 0);
    			break;
    		case "cyan" :
    			color = Color.CYAN;
    			break;
    		case "olive" :
    			color = new Color(128, 128, 0);
    			break;
    		case "tan" :
    			color = new Color(210, 180, 140);
    			break;
    		default :
    			throw new RuntimeException("Color not defined!");
        }
    }
    
    /**
     * Constructs a ColorCommand.
     * @param number The numeric value of the color.
     */
    public ColorCommand(int number) {
    	if (number > 16777216) throw new RuntimeException("Invalid input in color command!");
        color = new Color(number);
    }

    /**
     * @see TurtleCommand#execute(java.awt.Graphics)
     */
    @Override
    public void execute(Graphics g) {
        g.setColor(color);
    }
}
