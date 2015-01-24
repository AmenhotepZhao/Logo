package tokenizer;

import static org.junit.Assert.*;

import java.io.StringReader;
import java.util.HashSet;
import java.util.Set;
import org.junit.Test;

/**
 * Test for tokenizer class.
 */

public class TokenizerTest {
	Tokenizer tokenizer;
	
	@Test
	public void testTokenizerSymbol() {
		StringReader reader = new StringReader("<= +3");
		Set<String> keywords = new HashSet<String>();
		tokenizer = new Tokenizer(reader, keywords);
		Token token = tokenizer.next();
		assertEquals("<", token.getValue());
		assertTrue(token.getType() == TokenType.SYMBOL);
		token = tokenizer.next();
		assertEquals("=", token.getValue());
		assertTrue(token.getType() == TokenType.SYMBOL);
		token = tokenizer.next();
		assertEquals("+", token.getValue());
		assertTrue(token.getType() == TokenType.SYMBOL);		
		token = tokenizer.next();
		assertEquals("3", token.getValue());
		assertTrue(token.getType() == TokenType.NUMBER);
		token = tokenizer.next();
		assertEquals("", token.getValue());
		assertTrue(token.getType() == TokenType.EOI);
	}
	
	@Test
	public void testTokenizerNumber() {
		StringReader reader = new StringReader(" 1 arrr d e fg100 600");
		Set<String> keywords = new HashSet<String>();
		tokenizer = new Tokenizer(reader, keywords);
		Token token = tokenizer.next();
		assertEquals("1", token.getValue());
		assertTrue(token.getType() == TokenType.NUMBER);
		token = tokenizer.next();
		assertEquals("arrr", token.getValue());
		assertTrue(token.getType() == TokenType.NAME);
		token = tokenizer.next();
		assertEquals("d", token.getValue());
		assertTrue(token.getType() == TokenType.NAME);
		token = tokenizer.next();
		assertEquals("e", token.getValue());
		assertTrue(token.getType() == TokenType.NAME);
		token = tokenizer.next();
		assertEquals("fg100", token.getValue());
		assertTrue(token.getType() == TokenType.NAME);	
		token = tokenizer.next();
		assertEquals("600", token.getValue());
		assertTrue(token.getType() == TokenType.NUMBER);
		token = tokenizer.next();
		assertEquals("", token.getValue());
		assertTrue(token.getType() == TokenType.EOI);
	}
	
	@Test
	public void testTokenizerName() {
		StringReader reader = new StringReader(" arrr d e fg");
		Set<String> keywords = new HashSet<String>();
		tokenizer = new Tokenizer(reader, keywords);
		Token token = tokenizer.next();
		assertEquals("arrr", token.getValue());
		assertTrue(token.getType() == TokenType.NAME);
		token = tokenizer.next();
		assertEquals("d", token.getValue());
		assertTrue(token.getType() == TokenType.NAME);
		token = tokenizer.next();
		assertEquals("e", token.getValue());
		assertTrue(token.getType() == TokenType.NAME);
		token = tokenizer.next();
		assertEquals("fg", token.getValue());
		assertTrue(token.getType() == TokenType.NAME);		
		token = tokenizer.next();
		assertEquals("", token.getValue());
		assertTrue(token.getType() == TokenType.EOI);
	}
	
	@Test
	public void testTokenizerKeyword() {
		StringReader reader = new StringReader(" ifarrr else d e f while             ");
		Set<String> keywords = new HashSet<String>();
		keywords.add("if");
		keywords.add("else");
		keywords.add("while");
		tokenizer = new Tokenizer(reader, keywords);
		Token token = tokenizer.next();
		assertEquals("ifarrr", token.getValue());
		assertTrue(token.getType() == TokenType.NAME);
		token = tokenizer.next();
		assertEquals("else", token.getValue());
		assertTrue(token.getType() == TokenType.KEYWORD);
		token = tokenizer.next();
		assertEquals("d", token.getValue());
		assertTrue(token.getType() == TokenType.NAME);
		token = tokenizer.next();
		assertEquals("e", token.getValue());
		assertTrue(token.getType() == TokenType.NAME);
		token = tokenizer.next();
		assertEquals("f", token.getValue());
		assertTrue(token.getType() == TokenType.NAME);	
		token = tokenizer.next();
		assertEquals("while", token.getValue());
		assertTrue(token.getType() == TokenType.KEYWORD);	
		token = tokenizer.next();
		assertEquals("", token.getValue());
		assertTrue(token.getType() == TokenType.EOI);
	}
	
	@Test
	public void testTokenizerComment() {
		StringReader reader = new StringReader("//this is a comment \n and //so is this \n");
		Set<String> keywords = new HashSet<String>();
		tokenizer = new Tokenizer(reader, keywords);
		Token token = tokenizer.next();
		assertEquals("\n", token.getValue());
		assertTrue(token.getType() == TokenType.EOL);
		token = tokenizer.next();
		assertEquals("and", token.getValue());
		assertTrue(token.getType() == TokenType.NAME);
		token = tokenizer.next();
		assertEquals("\n", token.getValue());
		assertTrue(token.getType() == TokenType.EOL);
		token = tokenizer.next();
		assertEquals("", token.getValue());
		assertTrue(token.getType() == TokenType.EOI);
	}
	
	@Test
	public void testTokenizerLineEnd() {
		StringReader reader = new StringReader(" if \n 30t h while \n");
		Set<String> keywords = new HashSet<String>();
		keywords.add("if");
		keywords.add("else");
		keywords.add("while");
		tokenizer = new Tokenizer(reader, keywords);
		Token token = tokenizer.next();
		assertEquals("if", token.getValue());
		assertTrue(token.getType() == TokenType.KEYWORD);
		token = tokenizer.next();
		assertEquals("\n", token.getValue());
		assertTrue(token.getType() == TokenType.EOL);
		token = tokenizer.next();
		assertEquals("30", token.getValue());
		assertTrue(token.getType() == TokenType.NUMBER);
		token = tokenizer.next();
		assertEquals("t", token.getValue());
		assertTrue(token.getType() == TokenType.NAME);
		token = tokenizer.next();
		assertEquals("h", token.getValue());
		assertTrue(token.getType() == TokenType.NAME);	
		token = tokenizer.next();
		assertEquals("while", token.getValue());
		assertTrue(token.getType() == TokenType.KEYWORD);	
		token = tokenizer.next();
		assertEquals("\n", token.getValue());
		assertTrue(token.getType() == TokenType.EOL);
		token = tokenizer.next();
		assertEquals("", token.getValue());
		assertTrue(token.getType() == TokenType.EOI);
	}
	
	@Test
	public void testNextExponent() {
		StringReader reader = new StringReader("   123e10 123e10f 0.2 123.123f 123.123e10 123.e10f .123 123e+10 1.123e10d .123D"
				+ "  .123ff  12ef 12f 45D 23.0e-5 12.23.4e10f  ..");
		Set<String> keywords = new HashSet<String>();
		tokenizer = new Tokenizer(reader, keywords);
		Token t = tokenizer.next();
		assertEquals("123e10", t.getValue());
		assertTrue(t.getType() == TokenType.NUMBER);
		t = tokenizer.next();
		assertEquals("123e10f", t.getValue());
		assertTrue(t.getType() == TokenType.NUMBER);
		t = tokenizer.next();
		assertEquals("0.2", t.getValue());
		assertTrue(t.getType() == TokenType.NUMBER);
		t = tokenizer.next();
		assertEquals("123.123f", t.getValue());
		assertTrue(t.getType() == TokenType.NUMBER);
		t = tokenizer.next();
		assertEquals("123.123e10", t.getValue());
		assertTrue(t.getType() == TokenType.NUMBER);
		t = tokenizer.next();
		assertEquals("123.e10f", t.getValue());
		assertTrue(t.getType() == TokenType.NUMBER);
		t = tokenizer.next();
		assertEquals(".123", t.getValue());
		assertTrue(t.getType() == TokenType.NUMBER);
		t = tokenizer.next();
		assertEquals("123e+10", t.getValue());
		assertTrue(t.getType() == TokenType.NUMBER);
		t = tokenizer.next();
		assertEquals("1.123e10d", t.getValue());
		assertTrue(t.getType() == TokenType.NUMBER);
		t = tokenizer.next();
		assertEquals(".123D", t.getValue());
		assertTrue(t.getType() == TokenType.NUMBER);
		t = tokenizer.next();
		assertEquals(".123f", t.getValue());
		assertTrue(t.getType() == TokenType.NUMBER);
		t = tokenizer.next();
		assertEquals("f", t.getValue());
		assertTrue(t.getType() == TokenType.NAME);
		t = tokenizer.next();
		assertEquals("", t.getValue());
		assertTrue(TokenType.ERROR == t.getType());
		t = tokenizer.next();
		assertEquals("12f", t.getValue());
		assertTrue(t.getType() == TokenType.NUMBER);
		t = tokenizer.next();
		assertEquals("45D", t.getValue());
		assertTrue(t.getType() == TokenType.NUMBER);
		t = tokenizer.next();
		assertEquals("23.0e-5", t.getValue());
		assertTrue(t.getType() == TokenType.NUMBER);
		t = tokenizer.next();
		assertEquals("12.23", t.getValue());
		assertTrue(t.getType() == TokenType.NUMBER);
		t = tokenizer.next();
		assertEquals(".4e10f", t.getValue());
		assertTrue(t.getType() == TokenType.NUMBER);
		t = tokenizer.next();
		assertEquals(".", t.getValue());
		assertTrue(t.getType() == TokenType.SYMBOL);
		t = tokenizer.next();
		assertEquals(".", t.getValue());
		assertTrue(t.getType() == TokenType.SYMBOL);
		t = tokenizer.next();
		assertEquals("", t.getValue());
		assertTrue(t.getType() == TokenType.EOI);
	}
	
	@Test(expected = Exception.class)
	public void testRuntimeException() {
		StringReader reader = new StringReader("");
		Set<String> keywords = new HashSet<String>();
		tokenizer = new Tokenizer(reader, keywords);
		assertTrue(tokenizer.hasNext());
		tokenizer.next();
		assertFalse(tokenizer.hasNext());
		tokenizer.next();
	}

	@Test
	public void testHasNext() {
		StringReader reader = new StringReader("");
		Set<String> keywords = new HashSet<String>();
		tokenizer = new Tokenizer(reader, keywords);
		assertTrue(tokenizer.hasNext());
		tokenizer.next();
		assertFalse(tokenizer.hasNext());
		tokenizer.pushBack();
		assertTrue(tokenizer.hasNext());
		tokenizer.next();
		assertFalse(tokenizer.hasNext());
	}

	@Test
	public void testPushBack() {
		StringReader reader = new StringReader(" if - t 10n h2h \n");
		Set<String> keywords = new HashSet<String>();
		tokenizer = new Tokenizer(reader, keywords);
		tokenizer.pushBack();
		Token token = tokenizer.next();
		assertEquals("if", token.getValue());
		assertTrue(token.getType() == TokenType.NAME);
		tokenizer.pushBack();
		token = tokenizer.next();
		assertEquals("if", token.getValue());
		assertTrue(token.getType() == TokenType.NAME);
		token = tokenizer.next();
		assertEquals("-", token.getValue());
		assertTrue(token.getType() == TokenType.SYMBOL);
		token = tokenizer.next();
		assertEquals("t", token.getValue());
		assertTrue(token.getType() == TokenType.NAME);
		token = tokenizer.next();
		assertEquals("10", token.getValue());
		assertTrue(token.getType() == TokenType.NUMBER);
		token = tokenizer.next();
		assertEquals("n", token.getValue());
		assertTrue(token.getType() == TokenType.NAME);
		token = tokenizer.next();
		assertEquals("h2h", token.getValue());
		assertTrue(token.getType() == TokenType.NAME);
		token = tokenizer.next();
		assertEquals("\n", token.getValue());
		assertTrue(token.getType() == TokenType.EOL);
		token = tokenizer.next();
		assertEquals("", token.getValue());
		assertTrue(token.getType() == TokenType.EOI);
		tokenizer.pushBack();
		token = tokenizer.next();
		assertEquals("", token.getValue());
		assertTrue(token.getType() == TokenType.EOI);
		tokenizer.pushBack();
		token = tokenizer.next();
		assertEquals("", token.getValue());
		assertTrue(token.getType() == TokenType.EOI);
		
	}

}
