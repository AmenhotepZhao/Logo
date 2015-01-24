package parser;

import static org.junit.Assert.*;

import java.io.Reader;
import java.io.StringReader;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;

import tokenizer.Token;
import tokenizer.Tokenizer;
import tree.Tree;

/**
 * @author David Matuszek
 * @author // TODO fill in your name
 * @version February 2, 2014
 */
public class ParserTest {
    private Parser parser;
    private HashSet<String> keywords = new HashSet<String>(Arrays.asList("penup", "pendown",
        	"home", "jump", "set", "repeat", "while", "if", "else", "do", "forward", "left",
        	"right", "face", "red", "orange", "yellow", "green", "cyan", "blue", "purple",
        	"magenta", "pink", "olive", "black", "gray", "white", "brown", "tan", "color", "def", 
        	"getX", "getY"));

    /**
     * @throws java.lang.Exception if an error occurs.
     */
    @Before
    public void setUp() throws Exception {
        // Nothing to do (yet)
    }    

    /**
     * Test method for {@link parser.Parser#Parser(java.lang.String)}.
     */
    @Test
    public void testParser() {
        // Not much to test here; mostly that the Parser constructor doesn't crash
        parser = new Parser("");
        parser = new Parser("2 + 2");
    }
    
    /**
     * Test method for {@link parser.Parser#isMove()}.
     */
    @Test
    public void testIsMove() {        
        use("forward 3\n");
        assertTrue(parser.isMove());
        assertStackTop(tree("forward(3)"));
        
        use("left 3+2\n");
        assertTrue(parser.isMove());
        assertStackTop(tree("left(+(3 2))"));
        
        use("right 3*a+2\n");
        assertTrue(parser.isMove());
        assertStackTop(tree("right(+(*(3 a) 2))"));
        
        use("face abc\n");
        assertTrue(parser.isMove());
        assertStackTop(tree("face(abc)"));
        
        use("#");
        assertFalse(parser.isMove());
        unconsumedTokensShouldBe("#");
    }
    
    /**
     * Test method for {@link parser.Parser#isEOL()}.
     */
    @Test
    public void testIsEOL() {        
        use("\n \n \n");
        assertTrue(parser.isEOL());
        assertTrue(parser.stack.isEmpty());
        
        use("#");
        assertFalse(parser.isEOL());
        unconsumedTokensShouldBe("#");
    }
    
    /**
     * Test method for {@link parser.Parser#isColor()}.
     */
    @Test
    public void testIsColor() {        
    	use("color 125 0 225 \n");
        assertTrue(parser.isColor());
        assertStackTop(tree("color(125 0 225)"));
        
        use("red \n");
        assertTrue(parser.isColor());
        assertStackTop(tree("red"));
        
        use("blue \n");
        assertTrue(parser.isColor());
        assertStackTop(tree("blue"));
        
        use("black \n");
        assertTrue(parser.isColor());
        assertStackTop(tree("black"));
        
        use("tan \n");
        assertTrue(parser.isColor());
        assertStackTop(tree("tan"));
        
        use("color x 0 x+y*z \n");
        assertTrue(parser.isColor());
        assertStackTop(tree("color(x 0 +(x *(y z)))"));
        
        use("#");
        assertFalse(parser.isColor());
        unconsumedTokensShouldBe("#");
    }
    
    /**
     * Test method for {@link parser.Parser#isPenup()}.
     */
    @Test
    public void testIsPenup() {        
        use("penup \n");
        assertTrue(parser.isPenup());
        assertStackTop(tree("penup"));
        
        use("#");
        assertFalse(parser.isPenup());
        unconsumedTokensShouldBe("#");
    }
    
    /**
     * Test method for {@link parser.Parser#isPendown()}.
     */
    @Test
    public void testIsPendown() {        
        use("pendown \n");
        assertTrue(parser.isPendown());
        assertStackTop(tree("pendown"));
        
        use("#");
        assertFalse(parser.isPendown());
        unconsumedTokensShouldBe("#");
    }
    
    /**
     * Test method for {@link parser.Parser#isHome()}.
     */
    @Test
    public void testIsHome() {        
        use("home \n");
        assertTrue(parser.isHome());
        assertStackTop(tree("home"));
        
        use("#");
        assertFalse(parser.isHome());
        unconsumedTokensShouldBe("#");
    }
    
    /**
     * Test method for {@link parser.Parser#isJump()}.
     */
    @Test
    public void testIsJump() {        
        use("jump 2 3 \n");
        assertTrue(parser.isJump());
        assertStackTop(tree("jump(2 3)"));
        
        use("jump 2+3 3*5+4 \n");
        assertTrue(parser.isJump());
        assertStackTop(tree("jump(+(2 3) +(*(3 5) 4))"));
        
        use("jump abc 3+x \n");
        assertTrue(parser.isJump());
        assertStackTop(tree("jump(abc +(3 x))"));
        
        use("#");
        assertFalse(parser.isJump());
        unconsumedTokensShouldBe("#");
    }
    
    /**
     * Test method for {@link parser.Parser#isSet()}.
     */
    @Test
    public void testIsSet() {        
        use("set w 5\n");
        assertTrue(parser.isSet());
        assertStackTop(tree("set(w 5)"));
        
        use("set w 5+2\n");
        assertTrue(parser.isSet());
        assertStackTop(tree("set(w +(5 2))"));
        
        use("set w a+b*c\n");
        assertTrue(parser.isSet());
        assertStackTop(tree("set(w +(a *(b c)))"));
        
        use("#");
        assertFalse(parser.isSet());
        unconsumedTokensShouldBe("#");
    }
    
    /**
     * Test method for {@link parser.Parser#isRepeat()}.
     */
    @Test
    public void testIsRepeat() {        
        use("repeat 2 {\n penup\n }\n \n");
        assertTrue(parser.isRepeat());
        assertStackTop(tree("repeat(2 block(penup))"));
        
        use("repeat 2+abc {\n  }\n \n");
        assertTrue(parser.isRepeat());
        assertStackTop(tree("repeat(+(2 abc) block())"));
        
        use("repeat 2+abc {\n penup\n pendown\n home\n }\n \n");
        assertTrue(parser.isRepeat());
        assertStackTop(tree("repeat(+(2 abc) block(penup pendown home))"));
        
        use("#");
        assertFalse(parser.isRepeat());
        unconsumedTokensShouldBe("#");
    }
    
    /**
     * Test method for {@link parser.Parser#isWhile()}.
     */
    @Test
    public void testIsWhile() {        
        use("while 3 > 2 {\n penup\n }\n");
        assertTrue(parser.isWhile());
        assertStackTop(tree("while(>(3 2) block(penup))"));
        
        use("while a = 2 {\n penup\n pendown\n }\n");
        assertTrue(parser.isWhile());
        assertStackTop(tree("while(=(a 2) block(penup pendown))"));

        use("while a < b {\n }\n");
        assertTrue(parser.isWhile());
        assertStackTop(tree("while(<(a b) block())"));

        use("#");
        assertFalse(parser.isWhile());
        unconsumedTokensShouldBe("#");
    }
    
    /**
     * Test method for {@link parser.Parser#isIf()}.
     */
    @Test
    public void testIsIf() {        
        use("if 2 < 3 {\n penup\n }\n");
        assertTrue(parser.isIf());
        assertStackTop(tree("if(<(2 3) block(penup))"));
        
        use("if a < 3 {\n penup\n pendown\n }\n");
        assertTrue(parser.isIf());
        assertStackTop(tree("if(<(a 3) block(penup pendown))"));
        
        use("if 2 < 3 {\n penup\n }\n else {\n pendown\n }\n");
        assertTrue(parser.isIf());
        assertStackTop(tree("if(<(2 3) block(penup) block(pendown))"));
        
        use("if a < 3 {\n penup\n }\n else {\n }\n");
        assertTrue(parser.isIf());
        assertStackTop(tree("if(<(a 3) block(penup) block())"));

        use("#");
        assertFalse(parser.isIf());
        unconsumedTokensShouldBe("#");
    }
    
    /**
     * Test method for {@link parser.Parser#isDo()}.
     */
    @Test
    public void testIsDo() {        
        use("do foo 1 2 3\n");
        assertTrue(parser.isDo());
        assertStackTop(tree("do(foo list(1 2 3))"));
        
        use("do foo \n");
        assertTrue(parser.isDo());
        assertStackTop(tree("do(foo list())"));
        
        use("do foo 1 2+3*4\n");
        assertTrue(parser.isDo());
        assertStackTop(tree("do(foo list(1 +(2 *(3 4))))"));

        use("#");
        assertFalse(parser.isDo());
        unconsumedTokensShouldBe("#");
    }
    
    /**
     * Test method for {@link parser.Parser#isBlock()}.
     */
    @Test
    public void testIsBlock() {        
        use("{\n }\n");
        assertTrue(parser.isBlock());
        assertStackTop(tree("block()"));
        
        use("{\n penup\n pendown\n }\n");
        assertTrue(parser.isBlock());
        assertStackTop(tree("block(penup pendown)"));
        
        use("{\n repeat 5 {\n penup\n pendown\n }\n }\n");
        assertTrue(parser.isBlock());
        assertStackTop(tree("block(repeat(5 block(penup pendown)))"));

        use("#");
        assertFalse(parser.isBlock());
        unconsumedTokensShouldBe("#");
    }
    
    /**
     * Test method for {@link parser.Parser#isComparator()}.
     */
    @Test
    public void testIsComparator() {        
        use("< > =");
        assertTrue(parser.isComparator());
        assertStackTop(tree("<"));
        assertTrue(parser.isComparator());
        assertStackTop(tree(">"));
        assertTrue(parser.isComparator());
        assertStackTop(tree("="));

        use("#");
        assertFalse(parser.isComparator());
        unconsumedTokensShouldBe("#");
    }
    
    /**
     * Test method for {@link parser.Parser#isCondition()}.
     */
    @Test
    public void testIsCondition() {        
        use("a > b");
        assertTrue(parser.isCondition());
        assertStackTop(tree(">(a b)"));
        
        use("a = 3 + 2");
        assertTrue(parser.isCondition());
        assertStackTop(tree("=(a +(3 2))"));
        
        use("a*b+4 = 3 + 2");
        assertTrue(parser.isCondition());
        assertStackTop(tree("=(+(*(a b) 4) +(3 2))"));

        use("#");
        assertFalse(parser.isCondition());
        unconsumedTokensShouldBe("#");
    }
    
    /**
     * Test method for {@link parser.Parser#isProcedure()}.
     */
    @Test
    public void testIsProcedure() {        
        use("def foo a b c { \n forward b \n}\n");
        assertTrue(parser.isProcedure());
        assertStackTop(tree("def(header(foo list(a b c)) block(forward(b)))"));
        
        use("def foo { \n penup\n }\n");
        assertTrue(parser.isProcedure());
        assertStackTop(tree("def(header(foo list()) block(penup))"));
       
        use("#");
        assertFalse(parser.isProcedure());
        unconsumedTokensShouldBe("#");
    }
    
    /**
     * Test method for {@link parser.Parser#isCommand()}.
     */
    @Test
    public void testIsCommand() {        
    	use("penup \n");
        assertTrue(parser.isCommand());
        assertStackTop(tree("penup"));
        
        use("red \n");
        assertTrue(parser.isCommand());
        assertStackTop(tree("red"));
        
        use("if a > 3 {\n face 4\n }\n \n");
        assertTrue(parser.isCommand());
        assertStackTop(tree("if(>(a 3) block(face(4)))"));
        
        use("while a = 2 {\n face 4\n }\n \n");
        assertTrue(parser.isCommand());
        assertStackTop(tree("while(=(a 2) block(face(4)))"));
        
        use("set x 5 \n");
        assertTrue(parser.isCommand());
        assertStackTop(tree("set(x 5)"));
        
        use("#");
        assertFalse(parser.isCommand());
        unconsumedTokensShouldBe("#");
    }
    
    /**
     * Test method for {@link parser.Parser#isProgram()}.
     */
    @Test
    public void testIsProgram() {        
    	use("penup \n home \n");
        assertTrue(parser.isProgram());
        assertStackTop(tree("program(block(penup home) list())"));
        
        use("penup \n left x+3 \n");
        assertTrue(parser.isProgram());
        assertStackTop(tree("program(block(penup left(+(x 3))) list())"));
        
        use("penup \n home \n \n def foo x y z { \n left x \n } \n");
        assertTrue(parser.isProgram());
        assertStackTop(tree("program(block(penup home) list(def(header(foo list(x y z)) block(left(x)))))"));
        
        use("#");
        assertFalse(parser.isProgram());
        unconsumedTokensShouldBe("#");
    }
    
    /**
     * Test method for {@link parser.Parser#isExpression()}.
     */
    @Test
    public void testIsExpression() {
        Tree<Token> expected;
        
        use("250");
        assertTrue(parser.isExpression());
        assertStackTop(tree("250"));
        
        use("hello");
        assertTrue(parser.isExpression());
        assertStackTop(tree("hello"));

        use("(xyz + 3)");
        assertTrue(parser.isExpression());
        assertStackTop(tree("+(xyz 3)"));

        use("a + b + c");
        assertTrue(parser.isExpression());
        assertStackTop(tree("+(+(a b) c)"));

        use("3 * 12 - 7");
        assertTrue(parser.isExpression());
        assertStackTop(tree("-(*(3 12) 7)"));

        use("12 * 5 - 3 * 4 / 6 + 8");
        assertTrue(parser.isExpression());
        expected = tree("+( -(*(12 5) /(*(3 4) 6)) 8)");
        assertStackTop(expected);
                     
        use("12 * ((5 - 3) * 4) / 6 + (8)");
        assertTrue(parser.isExpression());
        expected = tree("+(/(*(12 *(-(5 3) 4)) 6) 8)");
        assertStackTop(expected);
        
        use("");
        assertFalse(parser.isExpression());
        
        use("#");
        assertFalse(parser.isExpression());
    }

    /**
     * Test method for {@link parser.Parser#isExpression()}.
     */
    @Test(expected=RuntimeException.class)
    public void testIsBadExpression1() {
        use("17 +");
        parser.isExpression();
    }

    /**
     * Test method for {@link parser.Parser#isExpression()}.
     */
    @Test(expected=RuntimeException.class)
    public void testIsBadExpression2() {
        use("22 *");
        parser.isExpression();
    }

    /**
     * Test method for {@link parser.Parser#isTerm()}.
     */
    @Test
    public void testIsTerm() {        
        use("12");
        assertTrue(parser.isTerm());
        assertStackTop(tree("12"));

        use("3*12");
        assertTrue(parser.isTerm());
        assertStackTop(tree("*(3 12)"));

        use("u * v * z");
        assertTrue(parser.isTerm());
        assertStackTop(tree("*(*(u v) z)"));
        
        use("20 * 3 / 4");
        assertTrue(parser.isTerm());
        assertStackTop(tree("/(*(20 3) 4)"));

        use("20 * 3 / 4 + 5");
        assertTrue(parser.isTerm());
        assertStackTop(tree("/(*(20 3) 4)"));
        unconsumedTokensShouldBe("+ 5");
        
        use("");
        assertFalse(parser.isTerm());
        unconsumedTokensShouldBe("");
        
        use("#");
        assertFalse(parser.isTerm());
        unconsumedTokensShouldBe("#");

    }

    /**
     * Test method for {@link parser.Parser#isFactor()}.
     */
    @Test
    public void testIsFactor() {
        use("12");
        assertTrue(parser.isFactor());
        assertStackTop(tree("12"));

        use("hello");
        assertTrue(parser.isFactor());
        assertStackTop(tree("hello"));
        
        use("(xyz + 3)");
        assertTrue(parser.isFactor());
        assertStackTop(tree("+(xyz 3)"));
        
        use("12 * 5");
        assertTrue(parser.isFactor());
        assertStackTop(tree("12"));
        unconsumedTokensShouldBe("* 5");
        
        use("17 +");
        assertTrue(parser.isFactor());
        assertStackTop(tree("17"));
        unconsumedTokensShouldBe("+");

        use("");
        assertFalse(parser.isFactor());
        unconsumedTokensShouldBe("");
        
        use("#");
        assertFalse(parser.isFactor());
        unconsumedTokensShouldBe("#");
    }

    /**
     * Test method for {@link parser.Parser#isAddOperator()}.
     */
    @Test
    public void testIsAddOperator() {
        use("+ - + $");
        assertTrue(parser.isAddOperator());
//        System.out.print(parser.stack.peek().getValue().getValue());
        assertStackTop(tree("+"));
        assertTrue(parser.isAddOperator());
        assertStackTop(tree("-"));
        assertTrue(parser.isAddOperator());
        assertFalse(parser.isAddOperator());
        unconsumedTokensShouldBe("$");
    }

    /**
     * Test method for {@link parser.Parser#isMultiplyOperator()}.
     */
    @Test
    public void testIsMultiplyOperator() {
        use("* / $");
        assertTrue(parser.isMultiplyOperator());
        assertTrue(parser.isMultiplyOperator());
        assertFalse(parser.isMultiplyOperator());
        unconsumedTokensShouldBe("$");
    }

    /**
     * Test method for {@link parser.Parser#isVariable()}.
     */
    @Test
    final public void testIsVariable() {
        use("hello   list abc123 header _");
        assertTrue(parser.isVariable());
        assertTrue(parser.isVariable());
        assertTrue(parser.isVariable());
        assertTrue(parser.isVariable());
        assertTrue(parser.isVariable());
    }

//  ----- "Helper" methods
    
    /**
     * The "isX" Parser methods try to recognize an X at the beginning
     * of the input, but should not consume tokens after the X. For example,
     * a factor may contain multiplications but not additions, so given a
     * string such as "3*x+5*y", the isFactor method should consume and
     * accept the "3*x" but leave the "+5*y" for later.
     * <p>
     * This method tests the input string to see whether the correct tokens
     * are left unconsumed. 
     * 
     * @param expectedTokens The following Tokens that should remain in the input
     *        string after the X has been consumed. 
     */
    private void unconsumedTokensShouldBe(String expectedTokens) {
        Token expectedToken;
        Token actualToken;
        
        Tokenizer unconsumedTokens = parser.getTokenizer();
        Tokenizer expected =
                new Tokenizer(new StringReader(expectedTokens), keywords);

        try {
            while (expected.hasNext()) {
                expectedToken = expected.next();
                assertTrue(unconsumedTokens.hasNext());
                actualToken = unconsumedTokens.next();
                assertEquals(expectedToken, actualToken);
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    /**
     * Asserts that the parameter is equal to the top of the stack.
     * 
     * @param t The Tree to compare against the top of the stack.
     */
    private void assertStackTop(Tree<Token> t) {
        assertEquals(t, parser.stack.peek());
    }
    
    /**
     * Creates a Tree of Tokens from a String.
     * 
     * @param description The string representation of the Tree
     * (Note: Cannot include parentheses as values.)
     * 
     * @return A Tree of Tokens.
     */
    private Tree<Token> tree(String description) {
        Tree<String> treeOfStrings = Tree.parse(description);
        return convertToTreeOfTokens(treeOfStrings);
    }
    
    /**
     * Creates a Tree of Tokens from a Tree of Strings. The given
     * Tree of Strings is unchanged.
     * 
     * @param tree The tree to be translated.
     * @return The resultant tree of tokens.
     */
    private Tree<Token> convertToTreeOfTokens(Tree<String> tree) {
        Tree<Token> root = new Tree<Token>(makeOneToken(tree.getValue()));

        Iterator<Tree<String>> iter = tree.children();
        
        while (iter.hasNext()) {
            Tree<String> child = iter.next();
            root.addChildren(convertToTreeOfTokens(child));
        }
        return root;
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

    /**
     * 
     * @param s The string to be parsed.
     */
    private void use(String s) {
        parser = new Parser(s);
    }
}