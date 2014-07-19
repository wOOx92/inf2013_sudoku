package dhbw.project.judoku.junitest;

import static org.junit.Assert.*;
import dhbw.project.judokugame.*;

import org.junit.Test;

public class SudokuBuilderTest extends SudokuBuilder{
   /**
	 * This Sudoku is used representative for 9x9 Sudokus. 
	 * It has been proofen that there are no Sudokus with
	 * less than 17 clues, so this Sudoku is guaranteed 
	 * irreducible. 
	 *  -----------------------
	 * | 0 0 0 | 0 4 0 | 0 2 0 |
	 * | 0 5 0 | 9 0 0 | 0 0 0 |
	 * | 0 1 0 | 0 0 0 | 0 0 0 |
	 * |-----------------------|
	 * | 0 0 0 | 8 0 0 | 1 0 5 |
	 * | 2 0 0 | 0 3 0 | 0 0 0 |
	 * | 0 0 0 | 0 0 0 | 9 0 0 |
	 * |-----------------------|
	 * | 4 9 0 | 0 0 2 | 0 0 0 |
	 * | 3 0 0 | 0 0 0 | 0 6 0 | 
	 * | 0 0 0 | 1 0 0 | 0 0 0 |
	 *  -----------------------
	 */
	private int[][] sdkTestCaseOne = new int[9][9];
	
	public SudokuBuilderTest() {
		sdkTestCaseOne[0][4] = 4;
		sdkTestCaseOne[0][7] = 2;
		sdkTestCaseOne[1][1] = 5;
		sdkTestCaseOne[1][3] = 9;
		sdkTestCaseOne[2][1] = 1;
		sdkTestCaseOne[3][3] = 8;
		sdkTestCaseOne[3][6] = 1;
		sdkTestCaseOne[3][8] = 5;
		sdkTestCaseOne[4][0] = 2;
		sdkTestCaseOne[4][4] = 3;
		sdkTestCaseOne[5][6] = 9;
		sdkTestCaseOne[6][0] = 4;
		sdkTestCaseOne[6][1] = 9;
		sdkTestCaseOne[6][5] = 2;
		sdkTestCaseOne[7][0] = 3;
		sdkTestCaseOne[7][7] = 6;
		sdkTestCaseOne[8][3] = 1;
	}
	@Test
	public void hasUniqueSolutionTest() {
		int[][] sdkTestCase1Copy = SudokuBuilder.deepCopy(sdkTestCaseOne);
		assertTrue("This test sudoku only has one Solution.", hasUniqueSolution(sdkTestCase1Copy, 1000));
		
		/*
		 * Now this Sudoku has 16 clues, it can not have a unique Solution anymore.
		 */
		sdkTestCase1Copy[0][4] = 0;
		assertFalse("Tested a 16 clue Sudoku, this must not be true.", hasUniqueSolution(sdkTestCase1Copy, 100));
	}

}
