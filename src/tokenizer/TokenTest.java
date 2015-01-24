package tokenizer;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import tokenizer.Token;
import tokenizer.TokenType;


/**
 * Test for token class.
 */

public class TokenTest {
	Token s;
	Token nl;
	Token symb;
	Token num;
	Token err;
	Token key;

	@Before
	public void setUp() throws Exception {
		s = new Token(TokenType.EOI, "");
		nl = new Token(TokenType.EOL, "\n");
		symb = new Token(TokenType.SYMBOL, "(");
		num = new Token(TokenType.NUMBER, "123");
		err = new Token(TokenType.ERROR, "");
		key = new Token(TokenType.KEYWORD, "if");
	}

	@Test
	public void testHashCode() {
		assertEquals(TokenType.EOI.hashCode() + "".hashCode(), s.hashCode());
		assertEquals(TokenType.EOL.hashCode() + "\n".hashCode(), nl.hashCode());
		assertEquals(TokenType.SYMBOL.hashCode() + "(".hashCode(), symb.hashCode());
		assertEquals(TokenType.NUMBER.hashCode() + "123".hashCode(), num.hashCode());
		assertEquals(TokenType.ERROR.hashCode() + "".hashCode(), err.hashCode());
		assertEquals(TokenType.KEYWORD.hashCode() + "if".hashCode(), key.hashCode());
	}

	@Test
	public void testGetValue() {
		assertEquals("", s.getValue());
		assertEquals("", err.getValue());
		assertEquals("\n", nl.getValue());
		assertEquals("123", num.getValue());
		assertEquals("if", key.getValue());
		assertEquals("(", symb.getValue());
	}

	@Test
	public void testGetType() {
		assertEquals(TokenType.EOI, s.getType());
		assertEquals(TokenType.ERROR, err.getType());
		assertEquals(TokenType.EOL, nl.getType());
		assertEquals(TokenType.NUMBER, num.getType());
		assertEquals(TokenType.KEYWORD, key.getType());
		assertEquals(TokenType.SYMBOL, symb.getType());
	}

	@Test
	public void testEqualsObject() {
		Token s2 = new Token(TokenType.EOI, "");
		Token nl2 = new Token(TokenType.EOL, "\n");
		Token symb2 = new Token(TokenType.SYMBOL, "(");
		Token num2 = new Token(TokenType.NUMBER, "123");
		Token err2 = new Token(TokenType.ERROR, "");
		Token key2 = new Token(TokenType.KEYWORD, "if");
		assertEquals(s, s2);
		assertEquals(nl, nl2);
		assertEquals(symb, symb2);
		assertEquals(num, num2);
		assertEquals(err, err2);
		assertEquals(key, key2);
		symb2 = new Token(TokenType.SYMBOL, ")");
		assertFalse(symb.equals(symb2));
		num2 = new Token(TokenType.NUMBER, "125");
		assertFalse(num.equals(num2));
		
	}

}
