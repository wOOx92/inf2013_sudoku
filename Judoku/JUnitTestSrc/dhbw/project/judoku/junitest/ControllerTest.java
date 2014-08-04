package dhbw.project.judoku.junitest;

import static org.junit.Assert.*;

import org.junit.Test;

import dhbw.project.judokugame.Controller;
import dhbw.project.puzzlemodel.Sudoku;

public class ControllerTest {

	@Test
	public void trySetValueTest() {
		Controller c = new Controller();
		Sudoku dummy = new Sudoku(3);
		
		
		assertFalse("Strings should not be accepted.", c.trySetValue(1, 3, "a", dummy));
		assertFalse("Strings should not be accepted.", c.trySetValue(1, 3, "string", dummy));
		assertFalse("A too big value has been accepted.", c.trySetValue(1, 3, "10", dummy));
		assertTrue("Valid request was denied.", c.trySetValue(1, 3, "0", dummy));
		assertTrue("Valid request was denied.", c.trySetValue(1, 3, "9", dummy));
		assertFalse("Invalid x-Value accepted.", c.trySetValue(10, 8, "4", dummy));
		assertFalse("Invalid y-Value accepted.", c.trySetValue(5, 11, "4", dummy));
		assertFalse("Negative x-Value accepted.", c.trySetValue(-4, 8, "4", dummy));
		assertFalse("Negative y-Value accepted.", c.trySetValue(4, -4, "4", dummy));
	}
}
