package tokenizer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.HashMap;
import java.util.Set;

/**
 * Tokenizer class. Parses the input string and tokenizes the input.
 * @author xiaolu
 *
 */
public class Tokenizer {
	HashMap<String, Boolean> keyword = new HashMap<String, Boolean>();
	private String inputString = "";
	
	private int index = -1;
	/**
	 * Private enum class signifying the state that the state machine is in.
	 * @author xiaolu
	 *
	 */
	private enum States {READY, IN_NUMBER, IN_NAME, ERROR, IN_SYMBOL, IN_COMMENT, IN_DECIMAL, IN_EXPONENT};
	private Token lastToken;
	boolean push = false;
	
	/**
	 * Constructor class. Reads the input from a reader, and parses the input into tokens.
	 * @param reader reader which input is read from
	 * @param keywords keywords that are recognized for the specified input
	 */
	public Tokenizer(Reader reader, Set<String> keywords) {
		if (reader == null || keywords == null) return;
		for (String s: keywords){
			keyword.put(s, true);
		}
		BufferedReader buffer = new BufferedReader(reader);
		StringBuilder builder = new StringBuilder();
		int curr;
		try {
			curr = buffer.read();
			while (curr != -1){
				builder.append((char) curr);
				curr = buffer.read();
			}
			inputString = builder.toString();
			inputString.replaceFirst("[ ]+$", "");
			inputString += " ";			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	/**
	 * Returns if the input has any more tokens to return
	 * @return true if there are more tokens to be returned and false if not
	 */
	public boolean hasNext() {
		return push || index + 1 < inputString.length();
	}
	
	/**
	 * Move the index of the input back one single character
	 */
	private void backUp(){
		index--;
	}

	/**
	 * Returns the next token of the input string
	 * @return token
	 */
	public Token next(){
		if (push) {
			push = false;
			return lastToken;
		}
		States state = States.READY;
		String value = "";
		
		if (!hasNext()) {
			throw new RuntimeException();
		}
		
        while (index++ < inputString.length()) {
        	char ch = inputString.charAt(index);
        	
        	switch (state) {
	        	case READY: {
	        		if (ch == '\n') {
	        			lastToken = new Token(TokenType.EOL, "\n");
	        			return lastToken;
	        		}
	        		if (Character.isWhitespace(ch)) {
	        			if (!hasNext()) {
	        				lastToken = new Token(TokenType.EOI, "");
	        				return lastToken;
	        			}
	        			break;
	        		}
	        		value += ch;
	        		if (Character.isJavaIdentifierStart(ch)) {
	        			state = States.IN_NAME;
	        			break;
	        		}
	        		if (Character.isDigit(ch)) {
	        			state = States.IN_NUMBER;
	        			break;
	        		}
	        		if (ch == '.') {
	        			state = States.IN_DECIMAL;
	        			break;
	        		}
	        		else {
	        			if (ch != '/'){
	        				lastToken = new Token(TokenType.SYMBOL, value);
	        				return lastToken;
	        			}
	        			state = States.IN_SYMBOL;
	        			break;
	        		}
	        	}
	        	case IN_SYMBOL: {
        			if (ch != '/'){
        				lastToken = new Token(TokenType.SYMBOL, value.substring(0, 1));
        				state = States.READY;
        				backUp();
        				return lastToken;
        			}
        			state = States.IN_COMMENT;
        			break;
	        	}
	        	case IN_COMMENT :{
	        		if (ch != '\n') break;
	        		lastToken = new Token(TokenType.EOL, "\n");
	        		return lastToken;
	        	}
	        	case IN_NAME: {
	        		if (Character.isJavaIdentifierPart(ch)) value += ch;
	        		else {
	        	    	state = States.READY;
	        	    	backUp(); // save char for next time
	        	    	if (keyword.containsKey(value)) lastToken = new Token(TokenType.KEYWORD, value);
	        	    	else lastToken = new Token(TokenType.NAME, value);
	        	    	return lastToken;
	        	    }
	        		break;
	        	}
	        	case IN_NUMBER: {
	        	    if (Character.isDigit(ch)) value += ch;
	        	    else if (ch == '.') {
	        	    	value += ch;
	        	    	state = States.IN_DECIMAL;
	        	    }
	           	    else if (ch == 'e' || ch == 'E') {
	        	    	value += ch;
	        	    	state = States.IN_EXPONENT;
	        	    }
	           	    else if (ch == 'f' || ch == 'F' || ch == 'd' || ch == 'D') {
	           	    	value += ch;
	        			lastToken = new Token(TokenType.NUMBER, value);
	        			state = States.READY;
	        			return lastToken;
	           	    }
	        	    else {
	        	    	state = States.READY;
	        	    	backUp(); // save char for next time
	        	    	lastToken =  new Token(TokenType.NUMBER, value);
	        	    	return lastToken;
	        	    }
	        	    break;
	        	}
	        	case IN_DECIMAL: {
	        		if (Character.isDigit(ch)) {
	        			value += ch;
	        			break;
	        		}
	        		else if (ch == 'e' || ch == 'E') {
	        			value += ch;
	        			state = States.IN_EXPONENT;
	        			break;
	        		}
	        		else if (ch == 'f' || ch == 'F' || ch == 'd' || ch == 'D') {
	        			if (!Character.isDigit(value.charAt(value.length() - 1))) {
	        				state = States.ERROR;
	        				break;
	        			}
	        			value += ch;
	        			lastToken = new Token(TokenType.NUMBER, value);
	        			state = States.READY;
	        			return lastToken;
	        		}
	        		else {
	        			if (value.length() == 1) lastToken = new Token(TokenType.SYMBOL, ".");
	        			else lastToken = new Token(TokenType.NUMBER, value);
	        			backUp();
	        			state = States.READY;
	        			return lastToken;
	        		}
	        	}
	        	case IN_EXPONENT: {
	        		if (Character.isDigit(ch)) value += ch;
	        		else if (ch == 'f' || ch == 'F' || ch == 'd' || ch == 'D') {
	        			if (!Character.isDigit(value.charAt(value.length() - 1))) {
	        				state = States.ERROR;
	        				break;
	        			}
	        			value += ch;
	        			lastToken = new Token(TokenType.NUMBER, value);
	        			state = States.READY;
	        			return lastToken;
	        		}
	        		else if (ch == '+' || ch == '-'){
	        			if (value.charAt(value.length() - 1) != 'e' && value.charAt(value.length() - 1) != 'E') {
	        				state = States.ERROR;
	        				break;
	        			}
	        			value += ch;
	        		}
	        		else if (value.charAt(value.length() - 1) != 'e' && value.charAt(value.length() - 1) != 'E'){
	        			state = States.READY;
	        	    	backUp(); // save char for next time
	        	    	lastToken =  new Token(TokenType.NUMBER, value);
	        	    	return lastToken;
	        		}
	        		else state = States.ERROR;
	        		break;
	        	}
	        	default: {
	        		state = States.READY;
	        		backUp();
	        		return new Token(TokenType.ERROR, "");
	        	}
        	}
        }
		return null;
	}
	
	/**
	 * Pushes back a token to be returned the next round.
	 */
	public void pushBack(){
		if (index != -1) push = true;
	}
}
