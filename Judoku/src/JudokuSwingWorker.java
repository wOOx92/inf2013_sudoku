import javax.swing.SwingWorker;

public class JudokuSwingWorker extends SwingWorker<Sudoku,Void>{
	
	final Difficulty diff;
	final GUI_Window guiWndw;
	
	public JudokuSwingWorker(Difficulty diff, GUI_Window gw){
		this.diff = diff;
		this.guiWndw = gw;
	}
	
	@Override
	public Sudoku doInBackground(){
		return new SudokuBuilder().newSudoku(diff);
	}
	
	/*@Override
	public void done(){
		try{
			this.guiWndw.setNumberPuzzle(get());
		}
		catch(Exception ignore){
			//Ignore Exceptions;	
		}
	}*/
	
}
