package interpreter;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import parser.ParserTest;

/**
 * Test of all of the classes and testable methods
 * @author xiaolu
 *
 */
@RunWith(Suite.class)
@SuiteClasses({
	tree.TreeTest.class,
	tokenizer.TokenizerTest.class,
	ParserTest.class
})
public class AllTests {

}
