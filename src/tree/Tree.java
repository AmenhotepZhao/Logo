package tree;

import java.util.Arrays;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.StringTokenizer;
/**
 * Tree class. Each node only keeps information about its direct descendents. No loops are allowed in the tree.
 * @author Xiaolu Xu
 *
 * @param <V> Value that each node holds
 */
public class Tree<V> {
    /**
     * Node value of tree
     */
	private V value;
	/**
	 * List of children
	 */
	private ArrayList<Tree<V>> children = new ArrayList<Tree<V>>();

	/**
	 * Constructs a new tree with given value and children
	 * 
	 * @param value     the value of the new tree
	 * @param children  are the children of the new tree
	 */
	public Tree(V value, Tree<V>... children){
		setValue(value);
		if (children != null) addChildren(children);	
	}
	
	/**
	 * Set the value of the tree
	 * 
	 * @param value the new value of the tree
	 */
	public void setValue(V value){
		this.value = value;
	}
	
	/**
	 * Gets the value of the tree
	 * 
	 * @return the value of the tree
	 */
	public V getValue(){
		return value;
	}
	
	/**
	 * add a new child to the tree
	 * 
	 * @param newChild                   the child needed to be add
	 * @throws IllegalArgumentException  if adding the child made an invalid tree
	 */
	public void addChild(Tree<V> newChild) throws IllegalArgumentException{
		if (newChild == null || newChild.contains(this)) throw new IllegalArgumentException();
		children.add(newChild);
	}
	
	/**
	 * Adds a new child at a given index of the children
	 * 
	 * @param index                      the index that the child would have
	 * @param newChild                   the child needed to be add
	 * @throws IllegalArgumentException  if adding the child made an invalid tree or the index is out of bound
	 */
	public void addChild(int index, Tree<V> newChild) throws IllegalArgumentException{
		if (newChild == null || index < 0 || index > numberOfChildren()) throw new IllegalArgumentException();	
		if (newChild.contains(this)) throw new IllegalArgumentException();
		if (index == numberOfChildren()) addChild(newChild);
		else children.add(index, newChild);
	}
	
	/**
	 * Adds a list of children to the tree
	 * 
	 * @param children                  the children to be added
	 * @throws IllegalArgumentException if failed to add any one of the child 
	 */
	public void addChildren(Tree<V>... children) throws IllegalArgumentException{
		if (children == null) throw new IllegalArgumentException();
		for (Tree<V> t: children) if (t.contains(this)) throw new IllegalArgumentException();
		this.children.addAll(Arrays.asList(children));
	}
	
	/**
	 * Removes the child with given index
	 * 
	 * @param index                   the index of the child to be removed
	 * @return						  the removed child
	 * @throws NoSuchElementException if the index is out of bounds
	 */
	public Tree<V> removeChild(int index) throws NoSuchElementException{
		if (index < 0 || index >= numberOfChildren()) throw new NoSuchElementException();
		Tree<V> t = children.get(index);
		children.remove(index);
		return t;
	}
	
	/**
	 * Gives the first child of the tree
	 * 
	 * @return the first child of the tree, null if there is no child
	 */
	public Tree<V> firstChild(){
		if (numberOfChildren() == 0) return null;
		return children.get(0);
	}
	
	/**
	 * Gives the last child of the tree
	 * 
	 * @return the last child of the tree, null if there is no child
	 */
	public Tree<V> lastChild(){
		if (numberOfChildren() == 0) return null;
		return children.get(children.size() - 1);
	}
	
	/**
	 * Gives the number of the children
	 * 
	 * @return the number of children of the tree
	 */
	public int numberOfChildren(){
		if (children == null) return 0;
		return children.size();
	}
	
	/**
	 * Gives the index-th child of the tree
	 * 
	 * @param index  					the index of the child
	 * @return		 					the index-th child of the tree	
	 * @throws NoSuchElementException	if the index is out of bound
	 */
	public Tree<V> child(int index) throws NoSuchElementException{
		if (index >= children.size() || index < 0) throw new NoSuchElementException();
		return children.get(index);
	}
	
	/**
	 * Creates an iterator for the children of the tree
	 * 
	 * @return an iterator of the children
	 */
	public Iterator<Tree<V>> children(){
		Iterator<Tree<V>> it = children.iterator();
		return it;
	}
	
	/**
	 * Validates whether a tree is a leaf 
	 * 
	 * @return true if the tree has no children, false if the tree has children
	 */
	public boolean isLeaf(){
		if (numberOfChildren() == 0) return true;
		return false;
	}

	/**
	 * Checked whether the tree contains a node
	 * 
	 * @param node  the node need to be checked whether in the tree
	 * @return true if the tree contains the node, false if the tree doesn't  
	 */
	private boolean contains(Tree<V> node){
		if (this == node) return true;
		for (Tree<V> t: children){
			if (t.contains(node)) return true;
		}
		return false;
	}
	
	/**
	 * Overrides the equals method
	 */
	@Override 
	public boolean equals(Object object){
		if (object == null) return false;
		if (object == this) return true;
		if (!(object instanceof Tree)) return false;
		Tree<V> t = (Tree<V>) object;
		if (this.value == null && t.value != null || t.value == null && this.value != null) return false;
		if (this.value != null && t.value != null) if (!this.value.equals(t.value)) return false;
		if (this.numberOfChildren() != t.numberOfChildren()) return false;
		Iterator<Tree<V>> it = t.children();
		for (Tree<V> child: children){
			if (!child.equals(it.next())) return false;
		}
		return true;
	}
	
	@Override
	public String toString(){
		return preorder(1);
	}
	
	/**
	 * Creates a string representing the tree by doing pre-order traversal of the tree recursively
	 * 
	 * @param level the level of the tree
	 * @return a string that represents the tree
	 */
	private String preorder(int level){
		StringBuilder builder = new StringBuilder();
		builder.append(value + "\n");
		for (Tree<V> t: children){
			for (int i = 0; i < level; i++) builder.append("  ");
			builder.append(t.preorder(level + 1));
		}
		return builder.toString();
	}
	
	/**
     * Parses a string of the general form
     * <code>value(child, child, ..., child)</code> and returns the
     * corresponding tree. Children may be separated by commas and/or spaces.
     * Node values are all Strings.
     * 
     * @param s The String to be parsed.
     * @return The resultant Tree&lt;String&lt;.
     * @throws IllegalArgumentException
     *             If problems are detected in the input string.
     */
    public static Tree<String> parse(String s) throws IllegalArgumentException {
        StringTokenizer tokenizer = new StringTokenizer(s, " ()", true);
        List<String> tokens = new LinkedList<String>();
        while (tokenizer.hasMoreTokens()) {
            String token = tokenizer.nextToken();
            if (token.trim().length() == 0)
                continue;
            tokens.add(token);
        }
        Tree<String> result = parse(tokens);
        if (tokens.size() > 0) {
            throw new IllegalArgumentException("Leftover tokens: " + tokens);
        }
        return result;
    }
    
    /**
     * Parses and returns one tree, consisting of a value and possible children
     * (enclosed in parentheses), starting at the first element of tokens.
     * Returns null if this token is a close parenthesis, or if there are no
     * more tokens.
     * 
     * @param tokens
     *            The tokens that describe a Tree.
     * @return The Tree described by the tokens.
     * @throws IllegalArgumentException
     *             If problems are detected in the input list.
     */
    private static Tree<String> parse(List<String> tokens)
            throws IllegalArgumentException {
        // No tokens -- return null
        if (tokens.size() == 0) {
            return null;
        }
        // Get the next token and remove it from the list
        String token = tokens.remove(0);
        // If the token is an open parenthesis
        if (token.equals("(")) {
            throw new IllegalArgumentException(
                "Unexpected open parenthesis before " + tokens);
        }
        // If the token is a close parenthesis, we are at the end of a list of
        // children
        if (token.equals(")")) {
            return null;
        }
        // Make a tree with this token as its value
        Tree<String> tree = new Tree<String>(token);
        // Check for children
        if (tokens.size() > 0 && tokens.get(0).equals("(")) {
            tokens.remove(0);
            Tree<String> child;
            while ((child = parse(tokens)) != null) {
                tree.addChildren(child);
            }
        }
        return tree;
    }
}
