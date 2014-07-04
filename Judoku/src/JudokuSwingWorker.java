import javax.swing.SwingWorker;

public class JudokuSwingWorker extends SwingWorker<Sudoku,Void>{
	final Difficulty diff;
	
	public JudokuSwingWorker(Difficulty diff){
		this.diff = diff;
	}
	
	public Sudoku doInBackground(){
		return new SudokuBuilder().newSudoku(diff);
	}
}
