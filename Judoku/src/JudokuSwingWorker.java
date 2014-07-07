import javax.swing.SwingWorker;

public class JudokuSwingWorker extends SwingWorker<Sudoku,Void>{
	
	final Difficulty diff;
	
	public JudokuSwingWorker(Difficulty diff){
		this.diff = diff;
	}
	
	@Override
	public Sudoku doInBackground(){
		return new SudokuBuilder().newSudoku(diff);
	}
	
	public Sudoku easyGet(){
		try{
			return this.get();
		}
		catch(Exception e){
			return null;
		}
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
