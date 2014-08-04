//package dhbw.project.puzzlemodel;
//
///**
// * NumberPuzzle is thought to be a unified interface for every puzzle based on completing a grid (e. g. Sudoku and Str8ts). 
// * 
// * @author Dennis Uteg, Florian Steurer, Markus Wingler, Michael Jauch
// */
//public interface NumberPuzzle {
//	
//	public int getSize();
//	
//	public int getCarreeSize();
//	
//	/**
//	 * @return The recent state as a 2-dimensional array.
//	 */
//	public int[][] getRecentGrid();
//
//	/**
//	 * @return The start state (when a new game is started) as a 2-dimensional array.
//	 */
//	public int[][] getStartGrid();
//
//	/**
//	 * @return The solution as a 2-dimensional array.
//	 */
//	public int[][] getSolvedGrid();
//	
//	/**
//	 * Sets a value in the recentGrid inside the NumberPuzzle.
//	 * 
//	 * @param x
//	 *            The x-value of the cell.
//	 * @param y
//	 *            The y-value of the cell.
//	 * @param value
//	 *            The value which is to be set.
//	 */
//	public void setValue(int x, int y, int value);
//
//	/**
//	 * Sets a useful hint in the recentGrid within the NumberPuzzle.
//	 */
//	public void giveHint();
//
//	/**
//	 * Searches for first wrong entry in the NumberPuzzle.
//	 * 
//	 * @return An array of size 2 with the x and y value of the mistake. An
//	 *         empty array if there are no mistakes.
//	 */
//	public int[] searchMistake();
//
//	/**
//	 * Resets the NumberPuzzle to the initial state.
//	 */
//	public void reset();
//
//	/**
//	 * Sets the NumberPuzzle to the previous state.
//	 */
//	public void undo();
//
//	/**
//	 * Checks whether there are any actions to undo. 
//	 * @see NumberPuzzle#undo()
//	 * @return True, if there are actions to be undone, false otherwise.
//	 */
//	public boolean undoPossible();
//
//	/**
//	 * Sets the NumberPuzzle to a state before an undo()-call.
//	 */
//	public void redo();
//
//	/**
//	 * Checks whether there are any actions to redo. 
//	 * @see NumberPuzzle#redo()
//	 * @return True, if there are actions to be redone, false otherwise.
//	 */
//	public boolean redoPossible();
//}
