package tokenizer;

/**
 * Class for tokens stored based on token type and string value
 * @author xiaolu
 *
 */

public class Token {
	private TokenType type;
	private String value;
	
	/**
	 * Creates a token with the specified Token type and string value associated with it
	 * @param type type of token
	 * @param value string value of token
	 */
	public Token(TokenType type, String value){
		this.type = type;
		this.value = value;
	}
	
	/**
	 * Returns string value of the token
	 * @return string value associated with the token
	 */
	public String getValue(){
		return value;
	}
	
	/**
	 * Return token type associated with the token
	 * @return token type associated with the token
	 */
	public TokenType getType(){
		return type;
	}
	
	@Override public boolean equals(Object o){
		if (o == null) return false;
		if (o == this) return true;
		if (!(o instanceof Token)) return false;
		Token t = (Token) o;
		if (t.getType() == this.type && t.getValue().equals(this.value)) return true;
		return false;
	}
	
	@Override public int hashCode(){
		return type.hashCode() + value.hashCode();
	}

	@Override public String toString() {
		return getValue();
	}
}
