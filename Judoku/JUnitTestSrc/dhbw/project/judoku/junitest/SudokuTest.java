package dhbw.project.judoku.junitest;

import static org.junit.Assert.*;

import org.junit.Test;

import dhbw.project.puzzlemodel.Sudoku;
import dhbw.project.puzzlemodel.SudokuBuilder;

public class SudokuTest {
	/* 
	 * Unsolved
	 *  -----------
	 * | 0 0 | 4 0 |
	 * | 1 0 | 0 0 |
	 * |-----------|
	 * | 0 0 | 0 3 |
	 * | 0 1 | 0 0 |
	 *  -----------
	 */
	private int[][] sdkUnsolved;
	
	/* 
	 * Solved
	 *  -----------
	 * | 2 3 | 4 1 |
	 * | 1 4 | 3 2 |
	 * |-----------|
	 * | 4 2 | 1 3 |
	 * | 3 1 | 2 4 |
	 *  -----------
	 */
	private int[][] sdkSolved;
	
	public SudokuTest() {
		sdkUnsolved = new int[4][4];
		sdkUnsolved[0][2] = 4;
		sdkUnsolved[1][0] = 1;
		sdkUnsolved[2][3] = 3;
		sdkUnsolved[3][1] = 1;

		sdkSolved = new int[4][4];
		sdkSolved[0][0] = 2;
		sdkSolved[0][1] = 3;
		sdkSolved[0][2] = 4;
		sdkSolved[0][3] = 1;
		sdkSolved[1][0] = 1;
		sdkSolved[1][1] = 4;
		sdkSolved[1][2] = 3;
		sdkSolved[1][3] = 2;
		sdkSolved[2][0] = 4;
		sdkSolved[2][1] = 2;
		sdkSolved[2][2] = 1;
		sdkSolved[2][3] = 3;
		sdkSolved[3][0] = 3;
		sdkSolved[3][1] = 1;
		sdkSolved[3][2] = 2;
		sdkSolved[3][3] = 4;
	}

	@Test
	public void setValueTest() {
		Sudoku sdkTestObj = new Sudoku(sdkUnsolved, sdkSolved);
		int[][] recentGrid = SudokuBuilder.deepCopy(sdkUnsolved);

		recentGrid[0][0] = 2;
		sdkTestObj.setValue(0, 0, 2);
		assertArrayEquals("The value 2 was not set at (0|0).", recentGrid,
				sdkTestObj.getRecentGrid());

		recentGrid[3][0] = 3;
		sdkTestObj.setValue(0, 3, 3);
		assertArrayEquals("The value 3 was not set at (0|3).", recentGrid,
				sdkTestObj.getRecentGrid());

		recentGrid[1][3] = 4;
		sdkTestObj.setValue(3, 1, 4);
		assertArrayEquals("The value 4 was not set at (3|1).", recentGrid,
				sdkTestObj.getRecentGrid());

		recentGrid[3][0] = 2;
		sdkTestObj.setValue(0, 3, 2);
		assertArrayEquals("Overwriting 3 at (0|3) with 2 did not work.",
				recentGrid, sdkTestObj.getRecentGrid());

		recentGrid[1][3] = 0;
		sdkTestObj.setValue(3, 1, 0);
		assertArrayEquals(
				"Deleting 4 by overwriting it with 0 at (3|1) did not work.",
				recentGrid, sdkTestObj.getRecentGrid());
	}

	@Test
	public void searchMistakeTest() {
		Sudoku sdkTestObj = new Sudoku(sdkUnsolved, sdkSolved);

		assertArrayEquals(
				"A mistake was recognized even though no values have been set.",
				new int[0], sdkTestObj.searchMistake());

		sdkTestObj.setValue(0, 0, 1);
		int[] mistake = new int[2];
		mistake[0] = 0;
		mistake[1] = 0;

		assertArrayEquals("1 at (0|0) was not recognized as a mistake.",
				mistake, sdkTestObj.searchMistake());
		sdkTestObj.setValue(0, 0, 0);

		sdkTestObj.setValue(3, 1, 2);
		assertArrayEquals("2 at (3|1) was wrongly recognized as a mistake.",
				new int[0], sdkTestObj.searchMistake());

		sdkTestObj.setValue(2, 3, 4);
		mistake[0] = 2;
		mistake[1] = 3;

		assertArrayEquals("3 at (2|3) was not recognized as a mistake.",
				mistake, sdkTestObj.searchMistake());
	}

	@Test
	public void giveHintTest() {
		/*
		 * The unsolved 4x4 grid has 4 clues at the beginning.
		 */

		Sudoku sdkTestObj = new Sudoku(sdkUnsolved, sdkSolved);
		sdkTestObj.giveHint();
		assertTrue(
				"After requesting an hint, the Sudoku still has as much clues as before.",
				Sudoku.getNumberOfClues(sdkTestObj.getRecentGrid()) == 5);
		
		sdkTestObj.giveHint();
		assertTrue(
				"After requesting an hint, the Sudoku still has as much clues as before.",
				Sudoku.getNumberOfClues(sdkTestObj.getRecentGrid()) == 6);

		/*
		 * Completely fill the Sudoku with hints.
		 */
		for (int i = 0; i < 20; i++) {
			sdkTestObj.giveHint();
		}

		assertArrayEquals(
				"Giving all possible hints does not result in the solved grid",
				sdkSolved, sdkTestObj.getSolvedGrid());
	}
	
	/*
	 * Undo() / Redo() 
	 * reset()
	 */
}
