public interface INumberPuzzle {
	// Contains all methods, which are used by the Gui

	public int[][] recentGrid = new int[9][9];

	public int[][] getRecentGrid();

	public boolean trySetValue(int x, int y, int value);

	public void giveHint(); //Fill out an useful number
	
	public int[] markMistake(); //Check mistakes, if mistake found -> X and Y value; empty array means no mistakes. Check impl. with tupel
	
	public void reset();
	
}
