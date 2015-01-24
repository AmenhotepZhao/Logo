package tree;

import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;

import java.util.Iterator;

import org.junit.Before;
import org.junit.Test;

public class TreeTest {
	Tree<String> b1 = new Tree<String>("b1", (Tree<String>[])null);
	Tree<String> b2 = new Tree<String>("b2", (Tree<String>[])null);
	Tree<String> b3 = new Tree<String>("b3", (Tree<String>[])null);
	Tree<String> b4 = new Tree<String>("b4", (Tree<String>[])null);
	Tree<String> b5 = new Tree<String>("b5", (Tree<String>[])null);
	Tree<String> b6 = new Tree<String>("b6", (Tree<String>[])null);
	@Before
	public void setUp() throws Exception {
		b1.addChildren(b2, b3);
		b2.addChildren(b4);
		b4.addChildren(b5, b6);
	}

	@Test
	public void testTree() {
		Tree<String> x5 = new Tree<String>("b5", (Tree<String>[])null);
		Tree<String> x6 = new Tree<String>("b6", (Tree<String>[])null);
		Tree<String> [] children = (Tree<String>[]) new Tree[]{x5, x6};
		Tree<String> x4 = new Tree<String>("b4", children);
		children = (Tree<String>[]) new Tree[]{x4};
		Tree<String> x2 = new Tree<String>("b2", children);
		Tree<String> x3 = new Tree<String>("b3", (Tree<String>[])null);
		children = (Tree<String>[]) new Tree[]{x2, x3};
		Tree<String> x1 = new Tree<String>("b1", children);
		assertTrue(b1.equals(b1));
		assertTrue(b2.equals(x2));
		assertTrue(x1.equals(b1));
		assertFalse(x1.equals(b2));
		assertFalse(b6.equals(b2));
	}

	@Test
	public void testSetValue() {
		b1.setValue("x1");
		b2.setValue("x1");
		b3.setValue("x1");
		b4.setValue("x1");
		b5.setValue("x1");
		b6.setValue("x1");
		assertEquals("x1", b1.getValue());
		assertEquals("x1", b2.getValue());
		assertEquals("x1", b3.getValue());
		assertEquals("x1", b4.getValue());
		assertEquals("x1", b5.getValue());
		assertEquals("x1", b6.getValue());
	}

	@Test
	public void testGetValue() {
		assertEquals("b1", b1.getValue());
		assertEquals("b2", b2.getValue());
		assertEquals("b3", b3.getValue());
		assertEquals("b4", b4.getValue());
		assertEquals("b5", b5.getValue());
		assertEquals("b6", b6.getValue());
	}

	@Test
	public void testAddChildTreeOfV() {
		Tree<String> b7 = new Tree<String>("b7", (Tree<String>[])null);
		Tree<String> b8 = new Tree<String>("b8", (Tree<String>[])null);
		Tree<String> b9 = new Tree<String>("b9", (Tree<String>[])null);
		b1.addChild(b7);
		assertEquals(b7, b1.lastChild());
		assertTrue(b1.numberOfChildren() == 3);
		b1.addChild(b8);
		assertEquals(b8, b1.lastChild());
		assertTrue(b1.numberOfChildren() == 4);
		b8.addChild(b9);
		assertEquals(b9, b8.lastChild());
		assertTrue(b8.numberOfChildren() == 1);
	}

	@Test
	public void testAddChildIntTreeOfV() {
		Tree<String> b7 = new Tree<String>("b7", (Tree<String>[])null);
		Tree<String> b8 = new Tree<String>("b8", (Tree<String>[])null);
		Tree<String> b9 = new Tree<String>("b9", (Tree<String>[])null);
		b1.addChild(1, b7);
		assertEquals(b3, b1.lastChild());
		assertTrue(b1.numberOfChildren() == 3);
		b1.addChild(0, b8);
		assertEquals(b8, b1.firstChild());
		assertTrue(b1.numberOfChildren() == 4);
		b2.addChild(1, b9);
		assertEquals(b9, b2.lastChild());
		assertTrue(b2.numberOfChildren() == 2);
	}
	
	@Test(expected = Exception.class)
	public void testAddChildException() {
		//null values or index out of bound
		Tree<String> b7 = new Tree<String>("b7", (Tree<String>[])null);
		b1.addChild(3, b7);
		b1.addChild(-1, b7);
		b2.addChild(1, null);
		b1.addChildren(null);
	}
	
	@Test(expected = Exception.class)
	public void testAddChildException2() {
		//Cycles detected
		Tree<String> b7 = new Tree<String>("b7", (Tree<String>[])null);
		Tree<String> b8 = new Tree<String>("b8", (Tree<String>[])null);
		b1.addChild(1, b7);
		b7.addChild(b8);
		b8.addChild(b1);
	}


	@Test
	public void testAddChildren() {
		Tree<String> b7 = new Tree<String>("b7", (Tree<String>[])null);
		Tree<String> b8 = new Tree<String>("b8", (Tree<String>[])null);
		Tree<String> b9 = new Tree<String>("b9", (Tree<String>[])null);
		b1.addChildren(b7, b8);
		assertEquals(b7, b1.child(2));
		assertEquals(b8, b1.child(3));
		assertTrue(b1.numberOfChildren() == 4);
		b2.addChildren(b9);
		assertEquals(b9, b2.lastChild());
		assertTrue(b2.numberOfChildren() == 2);
	}

	@Test
	public void testRemoveChild() {
		b1.removeChild(1);
		assertTrue(b1.numberOfChildren() == 1);
		assertTrue(b1.lastChild().equals(b2));
		b1.removeChild(0);
		assertTrue(b1.numberOfChildren() == 0);
		assertTrue(b1.lastChild() == null);
	}
	
	@Test(expected = Exception.class)
	public void testRemoveChildException() {
		b1.removeChild(2);
		b2.removeChild(1);
		b5.removeChild(0);
	}

	@Test
	public void testFirstChild() {
		assertEquals(b2, b1.firstChild());
		assertEquals(b4, b2.firstChild());
		assertEquals(b5, b4.firstChild());
		assertEquals(null, b3.firstChild());
		assertEquals(null, b5.firstChild());
		assertEquals(null, b6.firstChild());
	}

	@Test
	public void testLastChild() {
		assertEquals(b3, b1.lastChild());
		assertEquals(b4, b2.lastChild());
		assertEquals(b6, b4.lastChild());
		assertEquals(null, b3.lastChild());
		assertEquals(null, b5.lastChild());
		assertEquals(null, b6.lastChild());
		
	}

	@Test
	public void testNumberOfChildren() {
		assertEquals(2, b1.numberOfChildren());
		assertEquals(2, b4.numberOfChildren());
		assertEquals(1, b2.numberOfChildren());
		assertEquals(0, b3.numberOfChildren());
		assertEquals(0, b5.numberOfChildren());
		assertEquals(0, b6.numberOfChildren());
	}

	@Test
	public void testChild() {
		assertEquals(b1.child(0), b2);
		assertEquals(b1.child(1), b3);
		assertEquals(b2.child(0), b4);
		assertEquals(b4.child(0), b5);
		assertEquals(b4.child(1), b6);
		assertThat(b4.child(1), not(b5));
		assertThat(b1.child(1), not(b1));
		assertThat(b2.child(0), not(b1));
	}
	
	@Test(expected = Exception.class)
	public void testChildException() {
		b1.child(4);
		b6.child(0);
	}

	@Test
	public void testChildren() {
		Iterator<Tree<String>> it = b1.children();
		assertEquals(b2, it.next());
		assertEquals(b3, it.next());
		it = b2.children();
		assertEquals(b4, it.next());
	}

	@Test(expected = Exception.class)
	public void testChildrenException() {
		Iterator<Tree<String>> it = b1.children();
		assertEquals(b2, it.next());
		assertEquals(b3, it.next());
		it.next();
		it = b5.children();
		it.next();
	}
	
	@Test
	public void testIsLeaf() {
		assertFalse(b1.isLeaf());
		assertFalse(b2.isLeaf());
		assertTrue(b3.isLeaf());
		assertFalse(b4.isLeaf());
		assertTrue(b5.isLeaf());
		assertTrue(b6.isLeaf());
	}

	@Test
	public void testEqualsObject() {
		Tree<String> x1 = new Tree<String>("b1", (Tree<String>[])null);
		Tree<String> x2 = new Tree<String>("b2", (Tree<String>[])null);
		Tree<String> x3 = new Tree<String>("b3", (Tree<String>[])null);
		Tree<String> x4 = new Tree<String>("b4", (Tree<String>[])null);
		Tree<String> x5 = new Tree<String>("b5", (Tree<String>[])null);
		Tree<String> x6 = new Tree<String>("b6", (Tree<String>[])null);
		x1.addChildren(x2, x3);
		x2.addChildren(x4);
		x4.addChildren(x5, x6);
		assertTrue(b1.equals(b1));
		assertTrue(b2.equals(x2));
		assertTrue(x1.equals(b1));
		assertFalse(x1.equals(b2));
		assertFalse(b6.equals(b2));
		assertTrue(b1.equals(x1));
		x6.setValue("b5");
		assertFalse(b1.equals(x1));
		x4.removeChild(1);
		assertFalse(b1.equals(x1));
		assertFalse(b4.equals(x4));
		assertTrue(b5.equals(x5));
		
		Tree<String> xnull = null;
		assertFalse(b1.equals(xnull));
		
		//dave's test
		/*
         *              a1              a2
         *             / \             / \
         *            /   \           /   \
         *          b1     c1        b2    c2
         *          /\     /\       /\     /\
         *        d1  e1 f1  g1   e2  d2 f2  g2
         */
		Tree<String> tree, tree1, tree2;
	    Tree<String> a1, b1, c1, d1, e1, f1, g1, a2, b2, c2, d2, e2, f2, g2;
        a1 = new Tree<String>("a");
        b1 = new Tree<String>("b");
        c1 = new Tree<String>("c");
        d1 = new Tree<String>("d");
        e1 = new Tree<String>("e");
        f1 = new Tree<String>("f");
        g1 = new Tree<String>("g");
        a1.addChildren(b1, c1);
        b1.addChildren(d1, e1);
        c1.addChildren(f1, g1);
        tree1 = a1;

        a2 = new Tree<String>("a");
        b2 = new Tree<String>("b");
        c2 = new Tree<String>("c");
        d2 = new Tree<String>("d");
        e2 = new Tree<String>("e");
        f2 = new Tree<String>("f");
        g2 = new Tree<String>("g");
        a2.addChildren(b2, c2);
        b2.addChildren(e2, d2);
        c2.addChildren(f2, g2);
        tree2 = a2;
        
        assertTrue(f1.equals(f2));
        assertFalse(f1.equals(g1));
        assertTrue(a1.equals(a1));
        assertFalse(a1.equals(a2));
        assertTrue(c1.equals(c2));
        assertFalse(b1.equals(b2));
        Tree<String>x = new Tree<String>("x");
        Tree<String>y = new Tree<String>(null);
        assertFalse(f1.equals(null));
        assertFalse(x.equals(y));
        assertFalse(y.equals(x));
        Tree<String>z = new Tree<String>(null);
        z.addChildren(new Tree<String>("child"));
        assertFalse(y.equals(z));
	}

	@Test
	public void testToString() {
		assertEquals("b1\n  b2\n    b4\n      b5\n      b6\n  b3\n", b1.toString());
		assertEquals("b2\n  b4\n    b5\n    b6\n", b2.toString());
		assertEquals("b6\n", b6.toString());
	}

}
