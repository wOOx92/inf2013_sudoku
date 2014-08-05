package dhbw.project.judoku.junitest;

import static org.junit.Assert.*;

import org.junit.Test;

import dhbw.project.judokugame.Controller;
import dhbw.project.puzzlemodel.Sudoku;

public class ControllerTest extends Controller{

	@Test
	public void trySetValueTest() {
		Sudoku dummy = new Sudoku(3);
		
		assertFalse("Strings should not be accepted.", this.trySetValue(1, 3, "a", dummy));
		assertFalse("Strings should not be accepted.", this.trySetValue(1, 3, "string", dummy));
		assertFalse("A too big value has been accepted.", this.trySetValue(1, 3, "10", dummy));
		assertTrue("Valid request was denied.", this.trySetValue(1, 3, "0", dummy));
		assertTrue("Valid request was denied.", this.trySetValue(1, 3, "9", dummy));
		assertFalse("Invalid x-Value accepted.", this.trySetValue(10, 8, "4", dummy));
		assertFalse("Invalid y-Value accepted.", this.trySetValue(5, 11, "4", dummy));
		assertFalse("Negative x-Value accepted.", this.trySetValue(-4, 8, "4", dummy));
		assertFalse("Negative y-Value accepted.", this.trySetValue(4, -4, "4", dummy));
	}
}
