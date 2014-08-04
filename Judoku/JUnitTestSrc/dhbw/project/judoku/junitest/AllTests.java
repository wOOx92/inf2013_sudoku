package dhbw.project.judoku.junitest;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({ 
	SudokuBuilderTest.class,
	SudokuTest.class,
	ControllerTest.class
	})
public class AllTests {
}
