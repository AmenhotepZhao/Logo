package interpreter;

import java.awt.Color;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
import java.util.Stack;

import tree.Tree;
import tokenizer.Token;
import tokenizer.TokenType;

/**
 * Interprets the Logo program
 * 
 * @author David Matuszek
 * @author Xiaolu Xu
 * @version 9th April 2014
 */
public class Interpreter {
    private Turtle turtle;
    private DrawingArea canvas;
    private Hashtable<String, Double> globalVariables;
    private Map<String, Tree<Token>> procedures;
    private Stack<Hashtable<String, Double>> stackFrames;
    private HashSet<String> definedColor = new HashSet<String>(Arrays.asList("red", "orange", "yellow",
    		"green", "cyan", "blue", "purple", "magenta", "pink", "olive", "black", "gray", 
    		"white", "brown", "tan"));

    private boolean endDrawing = false;
    
    /**
     * Creates an Interpreter.
     * 
     * @param canvas The area on which to do the drawing.
     */
    public Interpreter(DrawingArea canvas) {
        this.canvas = canvas;
        turtle = new Turtle(canvas);
        globalVariables = new Hashtable<String, Double>();
        procedures = new HashMap<String, Tree<Token>>();
    }
    
    /**
     * Stops drawing on the canvas.
     */
    void stopDrawing(){
    	endDrawing = true;
    }
    
    /**
     * Erases the canvas and initializes or re-initializes all values.
     */
    void initialize() {
        canvas.setBackground(Color.WHITE);
        turtle.home();
        globalVariables.clear();
        procedures.clear();
        stackFrames = new Stack<Hashtable<String, Double>>();
        stackFrames.push(globalVariables);
    }

    /**
     * Interprets the tree rooted at the given node.
     * 
     * @param node The root of the tree to be interpreted.
     */
    void interpret(Tree<Token> node) {
        if (node == null || endDrawing) 
            return;

        String command = getStringFrom(node);

        if ("program".equals(command)) {
        	if (node.numberOfChildren() != 2) error("Program is incorrect!");
            findAllProcedures(node.child(1));
            interpret(node.child(0));
        }
        else if ("penup".equals(command)) {
        	turtle.penup();
        }
        else if ("pendown".equals(command)) {
        	turtle.pendown();
        }
        else if ("forward".equals(command)) {
        	if (node.numberOfChildren() != 1) error("Argument number is wrong in forward command!");
        	turtle.forward(evaluateExpression(node.child(0)));
        }
        else if ("left".equals(command)) {
        	if (node.numberOfChildren() != 1) error("Argument number is wrong in left command!");
            turtle.left(evaluateExpression(node.child(0)));
        }
        else if ("right".equals(command)) {
        	if (node.numberOfChildren() != 1) error("Argument number is wrong in right command!");
        	turtle.right(evaluateExpression(node.child(0)));
        }
        else if ("face".equals(command)) {
        	if (node.numberOfChildren() != 1) error("Argument number is wrong in face command!");
        	turtle.face(evaluateExpression(node.child(0)));
        }
        else if ("home".equals(command)) {
        	turtle.home();
        }        
        else if ("set".equals(command)) {
        	if (node.numberOfChildren() != 2) error("Argument number is wrong in set command!");
        	store(getStringFrom(node.child(0)), evaluateExpression(node.child(1)));
        }
        else if ("jump".equals(command)) {
        	if (node.numberOfChildren() != 2) error("Argument number is wrong in jump command!");
        	turtle.setPosition(evaluateExpression(node.child(0)), evaluateExpression(node.child(1)));
        }
        else if ("repeat".equals(command)) {
        	if (node.numberOfChildren() != 2) error("Error occured in interpreting 'repeat' command!");
        	for (int i = 0; i < evaluateExpression(node.child(0)); i++) 
        		interpret(node.child(1));
        }
        else if ("do".equals(command)) {
        	if (node.numberOfChildren() != 2) error("Error occured in interpreting 'do' command!");
        	callProcedure(getStringFrom(node.child(0)), node.child(1));
        }
        else if (definedColor.contains(command)) {
        	turtle.color(command); 
        }
        else if ("color".equals(command)) {
        	if (node.numberOfChildren() != 3) error("Argument number is wrong in color command!");
        	int r = (int) evaluateExpression(node.child(0));
        	int g = (int) evaluateExpression(node.child(1));
        	int b = (int) evaluateExpression(node.child(2));
        	if (r > 255 || r < 0 || g > 255 || g < 0 || b > 255 || b < 0)
        		error("Input color is out of range!");
        	turtle.color((r << 16) + (g << 8) + b); 
        }
        else if ("block".equals(command)) {
        	Iterator<Tree<Token>> itr = node.children();
        	while (itr.hasNext()) {
        		interpret(itr.next());
        	}
        }
        else if ("while".equals(command)) {
        	if (node.numberOfChildren() != 2) error("Argument number is wrong in while command!");
        	while (evaluateCondition(node.child(0))) 
        		interpret(node.child(1));
        }
        else if ("if".equals(command)) {
        	if (node.numberOfChildren() != 3) error("Argument number is wrong in if command!");
        	if (evaluateCondition(node.child(0))) 
        		interpret(node.child(1));
        	else {
        		if (node.numberOfChildren() > 2) 
        			interpret(node.child(2));
        	}
        }
        else {
            error("Unimplemented command:\n" + command);
        }
        canvas.repaint();
    }
    
	/**
     * Tells the turtle to pause.
     */
    void pauseTurtle() {
        turtle.setPaused(true);
    }
    
    /**
     * Tells the turtle to resume after being paused.
     */
    void resumeTurtle() {
        turtle.setPaused(false);
    }
    
    /**
     * Tells the turtle to set its speed.
     * 
     * @param speedControlValue The new speed (0 to 100).
     */
    void setTurtleSpeed(int speedControlValue) {
        turtle.setSpeed(speedControlValue);
    }
    
    /**
     * Given the root of a Tree representing an expression (which may
     * be a simple variable or number, or something much more complex),
     * evaluate the Tree and return the computed value.
     * 
     * @param node The root of the expression Tree.
     * @return The value of the expression.
     */
    private double evaluateExpression(Tree<Token> node) {
    	if (node == null) error("Empty expression!");
    	
        if (getTokenType(node) == TokenType.NUMBER) 
        	return Double.valueOf(getStringFrom(node)); 
        if (getTokenType(node) == TokenType.NAME) {
        	return fetch(getStringFrom(node));
        }
        if ("getX".equals(getStringFrom(node))) return turtle.getX();
        if ("getY".equals(getStringFrom(node))) return turtle.getY();
        if (getTokenType(node) == TokenType.SYMBOL) {
        	switch (getStringFrom(node)) {
        	case "+" :
        		if (node.numberOfChildren() == 1) return evaluateExpression(node.child(0));
        		if (node.numberOfChildren() != 2) error("Error occured in evaluating '+'");
        		return evaluateExpression(node.child(0)) + evaluateExpression(node.child(1));
        	case "-" :
        		if (node.numberOfChildren() == 1) return -evaluateExpression(node.child(0));
        		if (node.numberOfChildren() != 2) error("Error occured in evaluating '-'");
        		return evaluateExpression(node.child(0)) - evaluateExpression(node.child(1));
        	case "*" :
        		if (node.numberOfChildren() != 2) error("Error occured in evaluating '*'");
        		return evaluateExpression(node.child(0)) * evaluateExpression(node.child(1));
        	case "/" :
        		if (node.numberOfChildren() != 2) error("Error occured in evaluating '/'");
        		if (evaluateExpression(node.child(1)) != 0.0)
        			return evaluateExpression(node.child(0)) / evaluateExpression(node.child(1));
        		else 
        			error("Divided by 0!");
        	default :
        		error("Can't evaluate the expression!");
        	}
        }
        return -666;
    }
    
    /**
     * Given the root of a Tree representing a condition,
     * evaluate the Tree and return the result.
     * 
     * @param node The root of the condition Tree.
     * @return The value (true or false) of the condition.
     */
    private boolean evaluateCondition(Tree<Token> node) {
    	if (node == null) error("Empty condition!");
        switch (getStringFrom(node)) {
        	case "=" :
        		return evaluateExpression(node.child(0)) == evaluateExpression(node.child(1));
        	case "<" :
        		return evaluateExpression(node.child(0)) < evaluateExpression(node.child(1));
        	case ">" :
        		return evaluateExpression(node.child(0)) > evaluateExpression(node.child(1));
        	default :
        		error("Error codition!");		
        }
        return false;
    }

    /**
     * Given the name of a variable, return its value. It is an error
     * if the variable has not been given a value.
     * 
     * @param name A named variable.
     * @return The value of that variable.
     */
    private double fetch(String name) {
    	if (!stackFrames.isEmpty()) {
    		if (stackFrames.peek().containsKey(name)) return stackFrames.peek().get(name);
    	}
    	if (globalVariables.containsKey(name)) return globalVariables.get(name);
    	else error("The variable " + name + " is undefined!");
    	return -666;
    }

    /**
     * Stores the given value in the named variable. If the variable
     * does not previously exist, it is created.
     * 
     * @param name The variable whose value is to be changed.
     * @param value The value to be given to the variable.
     */
    private void store(String name, double value) {
    	if (!stackFrames.isEmpty()) {
    		if (stackFrames.peek().containsKey(name)) 
    			stackFrames.peek().put(name, value);
    		else if (globalVariables.containsKey(name)) 
    			globalVariables.put(name, value);
    		else
    			stackFrames.peek().put(name, value);
    	}
    	else
    		globalVariables.put(name, value);
    }

    /**
     * Does all the work required to call a Logo procedure. Specifically, it:
     * <ul><li>Uses the procedure name to find its procedure body,</li>
     *     <li>Creates a HashMap for the procedure's local variable,</li>
     *     <li>Pushes the HashMap onto the stack of HashMaps,</li>
     *     <li>Evaluates each actual parameter, and store the value in
     *         the HashMap for the corresponding formal parameter,</li>
     *     <li>Interprets the procedure body, and</li>
     *     <li>Pops the procedure's HashMap from the stack of HashMaps.</li>
     * </ul>
     * @param name The name of the procedure to be interpreted.
     * @param actualParameterListNode The list of actual parameters.
     */
    private void callProcedure(String name, Tree<Token> actualParameterListNode) {
    	if (name == null || actualParameterListNode == null) error("Error in calling procedure!");
    	if (!procedures.containsKey(name)) 
    		error("Procedure '" + name + "' not defined!");
    	Tree<Token> procedure = procedures.get(name);
		Hashtable<String, Double> localVariables = new Hashtable<String, Double>();
		Tree<Token> formalParameters = procedure.child(0).child(1);
		if (formalParameters.numberOfChildren() != actualParameterListNode.numberOfChildren())
			error("The number of arguments in calling procedure" + name + " doesn't match");
		Iterator<Tree<Token>> itFormal = formalParameters.children();
		Iterator<Tree<Token>> itActual = actualParameterListNode.children();
		while (itFormal.hasNext()) {
			localVariables.put(getStringFrom(itFormal.next()), evaluateExpression(itActual.next()));
		}
		stackFrames.push(localVariables);
		interpret(procedure.child(1));
		stackFrames.pop();
    }

    /**
     * Finds all the procedures in the parse tree and puts references to them in
     * the global variable <code>procedures</code>.
     * 
     * @param listOfProcedures
     *            The root of the tree of procedures.
     */
    private void findAllProcedures(Tree<Token> listOfProcedures) { 
    	if (listOfProcedures == null) error("Error in defining precedure!");
    	for (Iterator<Tree<Token>> it = listOfProcedures.children(); it.hasNext();) {
        	Tree<Token> procedure = it.next();
        	procedures.put(getStringFrom(procedure.firstChild().firstChild()), procedure);
        }
    }

    /**
     * Returns the String in the Token in the given node.
     * 
     * @param node
     *            The Tree<Token> from which to extract the String.
     * @return The String value in this Tree<Token> node.
     */
    private static String getStringFrom(Tree<Token> node) {
        return node.getValue().getValue();
    }
    
    /**
     * Returns the Type of the Token in the given node.
     * 
     * @param node
     *            The Tree<Token> from which to extract the TokenType.
     * @return The Type of Token in this Tree<Token> node.
     */
    private static TokenType getTokenType(Tree<Token> node) {
        return node.getValue().getType();
    }

    /**
     * Throws a RuntimeException containing a message.
     * @param string The message to be displayed.
     */
    private void error(String string) {
        throw new RuntimeException(string);
    }
}
