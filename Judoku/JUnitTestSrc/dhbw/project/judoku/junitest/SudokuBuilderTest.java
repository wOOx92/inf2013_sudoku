package dhbw.project.judoku.junitest;

import static org.junit.Assert.*;
import dhbw.project.judokugame.*;

import org.junit.Test;
import org.junit.experimental.theories.internal.Assignments;

public class SudokuBuilderTest extends SudokuBuilder{
	/**
	 * This Sudoku is used representative for 9x9 Sudokus. 
	 * It has been proofen that there are no Sudokus with
	 * less than 17 clues, so this Sudoku is guaranteed 
	 * irreducible. 
	 */
	private int[][] sdkTestCaseOne = new int[9][9];
	
	/**
	 * This Sudoku is used representative for 4x4 Sudokus.
	 */
	private int[][] sdkTestCaseTwo = new int[4][4];
	
	public SudokuBuilderTest() {
		
		/*
		 *  Unsolved
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
		
		/* 
		 * Solved
		 *  -----------------------
		 * | 9 3 7 | 6 4 5 | 8 2 1 |
		 * | 8 5 2 | 9 1 3 | 4 7 6 |
		 * | 6 1 4 | 2 8 7 | 3 5 9 |
		 * |-----------------------|
		 * | 7 6 3 | 8 2 9 | 1 4 5 |
		 * | 2 4 9 | 5 3 1 | 6 8 7 |
		 * | 1 8 5 | 4 7 6 | 9 3 2 |
		 * |-----------------------|
		 * | 4 9 6 | 3 5 2 | 7 1 8 |
		 * | 3 2 1 | 7 9 8 | 5 6 4 | 
		 * | 5 7 8 | 1 6 4 | 2 9 3 |
		 *  -----------------------
		 */
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
		 sdkTestCaseTwo[0][2] = 4;
		 sdkTestCaseTwo[1][0] = 1;
		 sdkTestCaseTwo[2][3] = 3;
		 sdkTestCaseTwo[3][1] = 1;
	}

	@Test
	public void hasUniqueSolutionTest() {

		/*
		 * Testing Case 1 9x9
		 */
		setCarreeSize(3);
		int[][] testCaseOneCopy = SudokuBuilder.deepCopy(sdkTestCaseOne);

		/*
		 * Possibility 1: unique solution
		 */
		assertTrue("This Sudoku only had one solution.",
				hasUniqueSolution(testCaseOneCopy, 100));

		/*
		 * Possibility 2: multiple solution (since there are no unique 9x9
		 * Sudokus with 16 clues)
		 */
		testCaseOneCopy[0][4] = 0;
		assertFalse("This Sudoku had multiple solutions",
				hasUniqueSolution(testCaseOneCopy, 100));

		/*
		 * Possibility 3: no solution (placing two numbers next to each other
		 * makes it unsolvable)
		 */
		testCaseOneCopy[0][4] = 4;
		testCaseOneCopy[1][4] = 4;
		assertFalse("This Sudoku had no solution at all.",
				hasUniqueSolution(testCaseOneCopy, 100));

		/*
		 * Testing Case 2 4x4
		 */
		setCarreeSize(2);
		int[][] testCaseTwoCopy = SudokuBuilder.deepCopy(sdkTestCaseTwo);

		/*
		 * Possibility 1: unique solution
		 */
		assertTrue("This Sudoku only has one Solution",
				hasUniqueSolution(testCaseTwoCopy, 100));

		/*
		 * Possibility 2: multiple solutions
		 */
		testCaseTwoCopy[0][2] = 0;
		assertFalse("This Sudoku has more than one solution",
				hasUniqueSolution(testCaseTwoCopy, 100));

		/*
		 * Possibility 3: no solution (because 4 is placed next to another 4)
		 */
		testCaseTwoCopy[0][2] = 4;
		testCaseTwoCopy[1][2] = 4;
		assertFalse("This Sudoku has no solution at all",
				hasUniqueSolution(testCaseTwoCopy, 100));
	}

	@Test
	public void doRandomCuttingTest() {
		/*
		 * If you add one clue, the resulting Sudoku should have 17 clues again.
		 * Adding more than one clue must not necessarily result in a Sudoku
		 * with 17 clues again, because one of the original clues could be cut
		 * out because the two additional clues add enough information to do so,
		 * but after that no other clue can be cut..
		 */	
		
		/*
		 * Testing 9x9
		 */
		setCarreeSize(3);
		int[][] testCaseCopy = SudokuBuilder.deepCopy(sdkTestCaseOne);
		testCaseCopy[6][6] = 7;
		setSudokuGridStorage(testCaseCopy);
		assertTrue("An additional clue was not cut out." , this.doRandomCutting(Difficulty.UNRESTRICTED) == 1);
		
		testCaseCopy = SudokuBuilder.deepCopy(sdkTestCaseOne);
		testCaseCopy[4][7] = 8;
		setSudokuGridStorage(testCaseCopy);
		assertTrue("An additional clue was not cut out." , this.doRandomCutting(Difficulty.UNRESTRICTED) == 1);
		
		/*
		 * Testing 4x4
		 */
		setCarreeSize(2);
		testCaseCopy = SudokuBuilder.deepCopy(sdkTestCaseTwo);
		testCaseCopy[2][1] = 2;
		setSudokuGridStorage(testCaseCopy);
		assertTrue("An additional clue was not cut out." , this.doRandomCutting(Difficulty.UNRESTRICTED) == 1);
		
		testCaseCopy = SudokuBuilder.deepCopy(sdkTestCaseTwo);
		testCaseCopy[2][0] = 4;
		setSudokuGridStorage(testCaseCopy);
		assertTrue("An additional clue was not cut out." , this.doRandomCutting(Difficulty.UNRESTRICTED) == 1);
	}
}
