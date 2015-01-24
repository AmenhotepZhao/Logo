package parser;

import java.io.StringReader;
import java.util.*;

import tokenizer.Tokenizer;
import tokenizer.TokenType;
import tokenizer.Token;
import tree.Tree;
/**
 * @author David Matuszek
 * @author Xiaolu
 * @version March 29, 2014
 */
public class Parser {
    private Tokenizer tokenizer = null; 
    private static final boolean DEBUG = false;
    /**
     * HashSet of keywords recognized
     */
    private static final HashSet<String> keywords = new HashSet<String>(Arrays.asList("penup", "pendown",
        	"home", "jump", "set", "repeat", "while", "if", "else", "do", "forward", "left",
        	"right", "face", "red", "orange", "yellow", "green", "cyan", "blue", "purple",
        	"magenta", "pink", "olive", "black", "gray", "white", "brown", "tan", "color", "def", 
        	"getX", "getY"));
  
    
    /**
     * The stack used for holding Trees as they are created.
     */
    public Stack<Tree<Token>> stack = new Stack<Tree<Token>>();

    /**
     * Constructs a Parser for the given string.
     * @param text The string to be parsed.
     */
    public Parser(String text) {
        tokenizer = new Tokenizer(new StringReader(text), keywords);
    }
    
    /**
     * Returns this Parser's Tokenizer. Should be used <i>only</i>
     * for testing this Parser; external use of the Tokenizer will
     * change its state and invalidate this Parser.
     * 
     * @return This Parser's Tokenizer.
     */
    public Tokenizer getTokenizer() {
        return tokenizer;
    }

    /**
     * Tries to parse a program;
     *
     * @return true if a program is parsed.
     */
    public boolean isProgram() {
    	if (!isCommand()) return false;
    	Tree<Token> list1 = new Tree<Token>(makeOneToken("block"));
    	list1.addChild(stack.pop());
    	while (isCommand()) list1.addChild(stack.pop());
    	Tree<Token> list2 = new Tree<Token>(makeOneToken("list"));
    	while (isProcedure()) list2.addChild(stack.pop());
    	Tree<Token> program = new Tree<Token>(makeOneToken("program"));
    	program.addChildren(list1, list2);
    	if (!nextTokenMatches(TokenType.EOI)) error("Unexpected terms in program!");
    	stack.push(program);
    	return true;
    }
    
    /**
     * Tries to parse a command;
     *
     * @return true if a command is parsed.
     */
    public boolean isCommand() {
    	if (isMove()) return true;
    	if (isPenup()) return true;
    	if (isPendown()) return true;
    	if (isHome()) return true;
    	if (isJump()) return true;
    	if (isSet()) return true;
    	if (isRepeat()) return true;
    	if (isWhile()) return true;
    	if (isIf()) return true;
    	if (isDo()) return true;
    	if (!isColor()) return false; 
    	if (!isEOL()) error("Expect newline!");
    	return true;
    }
    
    /**
     * Tries to parse a move;
     *
     * @return true if a move is parsed.
     */
    public boolean isMove() {
    	if (!isKeyword("forward") && !isKeyword("right") && 
    			!isKeyword("left") && !isKeyword("face")) return false;
    	if (!isExpression()) error("No expression found after 'move'!");
    	if (!isEOL()) error("No newline found after 'move'!");
    	makeTree(2,1);
    	return true;
    }
    
    /**
     * Tries to parse a color;
     *
     * @return true if a color is parsed.
     */
    public boolean isColor() {
    	if (isKeyword("red")) return true; 
    	if (isKeyword("orange")) return true; 
    	if (isKeyword("yellow")) return true; 
    	if (isKeyword("green")) return true; 
    	if (isKeyword("cyan")) return true; 
    	if (isKeyword("blue")) return true; 
    	if (isKeyword("purple")) return true; 
    	if (isKeyword("magenta")) return true; 
    	if (isKeyword("pink")) return true; 
    	if (isKeyword("olive")) return true; 
    	if (isKeyword("black")) return true; 
    	if (isKeyword("grey")) return true; 
    	if (isKeyword("white")) return true; 
    	if (isKeyword("brown")) return true; 
    	if (isKeyword("tan")) return true; 
    	if (!isKeyword("color")) return false;
    	if (!isExpression()) error("No expression found in color defination!");
    	if (!isExpression()) error("Second expression not found in color defination!");
    	if (!isExpression()) error("Third expression not found in color defination!");
    	makeTree(4, 3, 2, 1);
    	return true;
    }
    
    /**
     * Tries to parse a block;
     *
     * @return true if a block is parsed.
     */
    public boolean isBlock() {
    	if (!isSymbol("{")) return false;
    	stack.pop();
    	if (!isEOL()) error("Expected newline after '{'");
    	Tree<Token> root = new Tree<Token>(makeOneToken("block"));
    	while(isCommand()) {
    		root.addChild(stack.pop());
    	}
    	stack.push(root);
    	if (!isSymbol("}")) error("No '}' found in block!");
    	stack.pop();
    	if (!isEOL()) error("Expected newline after '}'");
    	return true;
    }
    
    /**
     * Tries to parse a condition;
     * 
     * @return true if a condition is parsed.
     */
    public boolean isCondition() {
    	if (!isExpression()) return false; 
    	if (!isComparator()) error("No comparator found!");
    	if (!isExpression()) error("No expression found after comparator!");
    	makeTree(2,3,1);
    	return true;
    }
    
    /**
     * Tries to parse a comparator;
     * 
     * @return true if a comparator is parsed.
     */
    public boolean isComparator() {
    	return isSymbol("<") || isSymbol("=") || isSymbol(">");
    }
    
    /**
     * Tries to parse a procedure;
     * 
     * @return true if a procedure is parsed.
     */
    public boolean isProcedure() {
    	if (!isKeyword("def")) return false;
    	Tree<Token> header = new Tree<Token>(makeOneToken("header"));
    	if (!isName()) error("No name found after 'def'");
    	header.addChild(stack.pop());
    	Tree<Token> list = new Tree<Token>(makeOneToken("list"));
    	while (isVariable()) list.addChild(stack.pop());
    	header.addChild(list);
    	stack.push(header);
    	if (!isBlock()) error("Missing block in procedure!");
    	makeTree(3, 2, 1);
    	return true;
    }
    
    /**
     * Tries to recognize an EOL;
     * 
     * @return true if an EOL.
     */
    public boolean isEOL() {
    	if (!nextTokenMatches(TokenType.EOL)) return false;
    	stack.pop();
    	while (nextTokenMatches(TokenType.EOL)) stack.pop();
    	
    	return true;
    }
    
    /**
     * Tries to parse a penup command;
     * 
     * @return true if a penup command is parsed.
     */
    public boolean isPenup() {
    	if (!isKeyword("penup")) return false;
    	if (!isEOL()) error("No newline found after 'penup'!");
    	return true;
    }
    
    /**
     * Tries to parse a pendown command;
     * 
     * @return true if a pendown command is parsed.
     */
    public boolean isPendown() {
    	if (!isKeyword("pendown")) return false;
    	if (!isEOL()) error("No newline found after 'pendown'!");
    	return true;
    }
    
    /**
     * Tries to parse a home command;
     * 
     * @return true if a home command is parsed.
     */
    public boolean isHome() {
    	if (!isKeyword("home")) return false;
    	if (!isEOL()) error("No newline found after 'home'!");
    	return true;
    }
    
    /**
     * Tries to parse a jump command;
     * 
     * @return true if a jump command is parsed.
     */
    public boolean isJump() {
    	if (!isKeyword("jump")) return false;
    	if (!isExpression()) error("No expression found after 'jump'!");
    	if (!isExpression()) error("Second expression not found in 'jump'!");
    	if (!isEOL()) error("No newline found in 'jump'!");
    	makeTree(3, 2, 1);
    	return true;
    }
    
    /**
     * Tries to parse a set command;
     * 
     * @return true if a set command is parsed.
     */
    public boolean isSet() {
    	if (!isKeyword("set")) return false;
    	if (!isVariable()) error("No variable found after 'set'!");
    	if (!isExpression()) error("No expression found in 'set'!");
    	if (!isEOL()) error("No newline found in 'set'!");
    	makeTree(3, 2, 1);
    	return true;
    }
    
    /**
     * Tries to parse a repeat command;
     * 
     * @return true if a repeat command is parsed.
     */
    public boolean isRepeat() {
    	if (!isKeyword("repeat")) return false;
    	if (!isExpression()) error("No expression found after 'repeat'!");
    	if (!isBlock()) error("No block found in 'repeat'!");
    	makeTree(3, 2, 1);
    	return true;
    }
    
    /**
     * Tries to parse a while command;
     * 
     * @return true if a while command is parsed.
     */
    public boolean isWhile() {
    	if (!isKeyword("while")) return false;
    	if (!isCondition()) error("No condition found after 'while'!");
    	if (!isBlock()) error("No block found in 'while'!");
    	makeTree(3, 2, 1);
    	return true;
    }
    
    /**
     * Tries to parse a if command;
     * 
     * @return true if a if command is parsed.
     */
    public boolean isIf() {
    	if (!isKeyword("if")) return false;
    	if (!isCondition()) error("No condition found after 'if'!");
    	if (!isBlock()) error("No block found after 'if'!");
    	if (isKeyword("else")) {
    		stack.pop();
    		if (!isBlock()) error("No block found after 'else'!");
    		makeTree(4, 3, 2, 1);
    		return true;
    	}
    	makeTree(3, 2, 1);
    	return true;
    }
    
    /**
     * Tries to parse a do command;
     * 
     * @return true if a do command is parsed.
     */
    public boolean isDo() {
    	if (!isKeyword("do")) return false;
    	if (!isName()) error("No name found after 'do'!");
    	Tree<Token> root = new Tree<Token>(makeOneToken("list"));
    	while (isExpression()) root.addChild(stack.pop());
    	stack.push(root);
    	if (!isEOL()) error("No newline found after 'do'!");
    	makeTree(3, 2, 1);
    	return true;
    }
    
    /**
     * Tries to parse an &lt;expression&gt;.
     * <pre>&lt;expression&gt; ::= [ &lt;add_operator&gt; ] &lt;term&gt; { &lt;add_operator&gt; &lt;unsignedTerm&gt; }</pre>
     * A <code>SyntaxException</code> will be thrown if the add_operator
     * is present but not followed by a valid &lt;expression&gt;.
     * @return <code>true</code> if an expression is recognized.
     */
    public boolean isExpression() {
        if (!isTerm()) {
            return false;
        }
        while (isAddOperator()) {
            if (!isUnsignedTerm())
                error("Error in expression after '+' or '-'");
            makeTree(2, 3, 1);
        }
        return true;
    }
    
    /**
     * Tries to parse a &lt;term&gt;.
     * <pre>&lt;term&gt; ::= &lt;factor&gt; { &lt;multiply_operator&gt; &lt;factor&gt; }</pre>
     * A <code>SyntaxException</code> will be thrown if the multiply_operator
     * is present but not followed by a valid &lt;term&gt;.
     * @return <code>true</code> if a term is recognized.
     */
    public boolean isTerm() {
        if (!isFactor())
            return false;
        while (isMultiplyOperator()) {
            if (!isFactor())
                error("No term after '*' or '/'");
            makeTree(2, 3, 1);
        }
        return true;
    }
    
    /**
     * Tries to parse an &lt;unsignedTerm&gt;.
     * <pre>&lt;unsignedTerm&gt; ::= &lt;unsignedFactor&gt; { &lt;multiply_operator&gt; &lt;factor&gt; }</pre>
     * A <code>SyntaxException</code> will be thrown if the multiply_operator
     * is present but not followed by a valid &lt;term&gt;.
     * @return <code>true</code> if an unsignedTerm is recognized.
     */
    public boolean isUnsignedTerm() {
        if (!isUnsignedFactor())
            return false;
        while (isMultiplyOperator()) {
            if (!isUnsignedFactor())
                error("No term after '*' or '/'");
            makeTree(2, 3, 1);
        }
        return true;
    }

    /**
     * Tries to parse an &lt;unsignedFactor&gt;.
     * <pre>&lt;unsignedFactor&gt; ::= &lt;name&gt;
     *                   | &lt;number&gt;
     *                   | "getX"
     *                   | "getY"
     *                   | "(" &lt;expression&gt; ")"</pre>
     * A <code>SyntaxException</code> will be thrown if the opening
     * parenthesis is present but not followed by a valid
     * &lt;expression&gt; and a closing parenthesis.
     * @return <code>true</code> if an unsignedFactor is recognized.
     */
    public boolean isUnsignedFactor() {
        if (isName()) {
            return true;
        }
        if (isNumber()) {
            return true;
        }
        if (isKeyword("getX") || isKeyword("getY")) {
            return true;
        }
        if (isSymbol("(")) {
            stack.pop();
            if (!isExpression()) {
                error("Error in parenthesized expression");
            }
            if (!isSymbol(")")) {
                error("Unclosed parenthetical expression");
            }
            stack.pop();
            return true;
        }
        return false;
    }
    
    /**
     * Tries to parse a &lt;factor&gt;.
     * <pre>&lt;factor&gt; ::= [ &lt;addOperator&gt; ] &lt;unsignedFactor&gt;</pre>
     * @return <code>true</code> if a factor is recognized.
     */
    public boolean isFactor() {
        boolean prefix = isSymbol("-");
        if (isUnsignedFactor()) {
            if (prefix) makeTree(2, 1);
            return true;
        }
        if (prefix) error("Unary minus not followed by a factor.");
        return false;
    }

    /**
     * Tries to parse an &lt;add_operator&gt;.
     * <pre>&lt;add_operator&gt; ::= "+" | "-"</pre>
     * @return <code>true</code> if an addop is recognized.
     */
    public boolean isAddOperator() {
        return isSymbol("+") || isSymbol("-");
    }

    /**
     * Tries to parse a &lt;multiply_operator&gt;.
     * <pre>&lt;multiply_operator&gt; ::= "*" | "/"</pre>
     * @return <code>true</code> if a multiply_operator is recognized.
     */
    public boolean isMultiplyOperator() {
        return isSymbol("*") || isSymbol("/");
    }
    
    /**
     * Tries to parse a &lt;variable&gt;, which is just a "name".
     * 
     * @return <code>true</code> if a variable is recognized.
     */
    public boolean isVariable() {
        return isName();
    }
    
    //------------------------- Private "helper" methods

    /**
     * Tests whether the next token is a number. If it is, the token
     * is consumed, otherwise it is not.
     * 
     * @return <code>true</code> if the next token is a number.
     */
    private boolean isNumber() {
        return nextTokenMatches(TokenType.NUMBER);
    }

    /**
     * Tests whether the next token is a name. If it is, the token
     * is consumed, otherwise it is not.
     * 
     * @return <code>true</code> if the next token is a name.
     */
    private boolean isName() {
        return nextTokenMatches(TokenType.NAME);
    }

    /**
     * Tests whether the next token is the expected name. If it is, the token
     * is consumed, otherwise it is not.
     * 
     * @param expectedName The String value of the expected next token.
     * @return <code>true</code> if the next token is a name with the expected value.
     */
    private boolean isName(String expectedName) {
        return nextTokenMatches(TokenType.NAME, expectedName);
    }

    /**
     * Tests whether the next token is the expected keyword. If it is,
     * the token is consumed, otherwise it is not.
     * 
     * @param expectedName The String value of the expected next token.
     * @return <code>true</code> if the next token is a name with the
     *        expected value.
     */
    private boolean isKeyword(String expectedName) {
        return nextTokenMatches(TokenType.KEYWORD, expectedName);
    }

    /**
     * Tests whether the next token is the expected symbol. If it is,
     * the token is consumed, otherwise it is not.
     * @param expectedSymbol  The expected symbol.
     * 
     * @return <code>true</code> if the next token is the expected symbol.
     */
    private boolean isSymbol(String expectedSymbol) {
        return nextTokenMatches(TokenType.SYMBOL, expectedSymbol);
    }

    /**
     * If the next Token has the expected type, it is used as the
     * value of a new (childless) Tree node, and that node
     * is then pushed onto the stack. If the next Token does not
     * have the expected type, this method effectively does nothing.
     * 
     * @param type The expected type of the next token.
     * @return <code>true</code> if the next token has the expected type.
     */
    private boolean nextTokenMatches(TokenType type) {
        if (!tokenizer.hasNext()) {
            return false;
        }
        Token t = tokenizer.next();
        if (t.getType() == type) {
            stack.push(new Tree<Token>(t));
            return true;
        }
        tokenizer.pushBack();
        return false;
    }

    /**
     * If the next Token has the expected type and value, it is used as
     * the value of a new (childless) Tree node, and that node
     * is then pushed onto the stack; otherwise, this method does
     * nothing.
     *
     * @param type The expected type of the next token.
     * @param value The expected value of the next token; must
     *              not be <code>null</code>.
     * @return <code>true</code> if the next token has the expected type.
     */
    private boolean nextTokenMatches(TokenType type, String value) {
        if (!tokenizer.hasNext()) {
            return false;
        }
        Token t = tokenizer.next();
        if (type == t.getType() && value.equals(t.getValue())) {
            stack.push(new Tree<Token>(t));
            return true;
        }
        tokenizer.pushBack();
        return false;
    }

    /**
     * Assembles some number of elements from the top of the global stack
     * into a new Tree, and replaces those elements with the new Tree.<p>
     * <b>Caution:</b> The arguments must be consecutive integers 1..N,
     * in any order, but with no gaps; for example, makeTree(2,4,1,5)
     * would cause problems (3 was omitted).
     * <p>Example: If the stack contains</p><pre>
     * 1  |  A  |                                     |    B    |       
     * 2  |  B  |                                     |   / \   |
     * 3  |  C  |  then makeTree(2, 3, 1) results in  |  C   A  |
     *    | ... |                                     |   ...   |
     *    |_____|                                     |_________|
     *    </pre>
     * @param rootIndex Which stack element (counting from top=1) to use as
     * the root of the new Tree.
     * @param childIndices Which stack elements to use as the children
     * of the root.
     */    
    private void makeTree(int rootIndex, int... childIndices) {
        // Get root from stack
        Tree<Token> root = getStackItem(rootIndex);
        // Get other trees from stack and add them as children of root
        for (int i = 0; i < childIndices.length; i++) {
            root.addChildren(getStackItem(childIndices[i]));
        }
        // Pop root and all children from stack
        for (int i = 0; i <= childIndices.length; i++) {
            stack.pop();
        }
        // Put the root back on the stack
        stack.push(root);
    }
      
    /**
     * Returns, as a Tree, the nth element from the top of the
     * instance variable <code>stack</code> (the top element is 1).
     * 
     * @param n Which element of the stack, counting
     *          1 as the top element, to return.
     * @return The indicated stack element.
     */
    private Tree<Token> getStackItem(int n) {
        return stack.get(stack.size() - n);
    }

    /**
     * Utility routine to throw a <code>SyntaxException</code> with the
     * given message.
     * @param message The text to put in the <code>SyntaxException</code>.
     */
    private void error(String message) {
        if (DEBUG) {
            System.out.println("Stack = " + stack + " <--(top)");
            for (int i = 0; i < stack.size(); i++) {
                System.out.println("Stack " + i + ":");
                System.out.println(stack.get(i));
            }
        }
        throw new SyntaxException(message + "; stack = " + stack);
    }
    
    /**
     * Returns a single token containing the given string.
     * 
     * @param word The thing to be turned into a Token.
     * @return The corresponding Token.
     */
    private Token makeOneToken(String word) {
        Tokenizer tokenizer = new Tokenizer(new StringReader(word), keywords);;
        return tokenizer.next();
    }
}