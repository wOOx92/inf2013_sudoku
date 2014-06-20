public interface INumberPuzzle {
	// Contains all methods, which are used by the Gui

	public int[][] recentGrid = new int[9][9];

	public int[][] getRecentGrid();

	public boolean trySetValue(int x, int y, int value);

	public void giveHint();
}
