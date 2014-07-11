package dhbw.project.judokugame;
import java.awt.EventQueue;
import java.util.Arrays;


public class Controller {
	private GUI_Window spielfeld;
	
	public static void main(String[] args) {
		Controller c = new Controller();
		c.initGui(c);
	}
	public void initGui(final Controller c){
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					GUI_Window window = new GUI_Window(c);
					spielfeld = window;
					window.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
	
	public void resetPuzzle(NumberPuzzle puzzle){
		puzzle.reset();
	}
	public void undoPuzzle(NumberPuzzle puzzle){
		puzzle.undo();
	}

	public void redoPuzzle(NumberPuzzle puzzle){
		puzzle.redo();
	}
	
	public int validateUserSolution(NumberPuzzle puzzle){
		int mistakes = 0;
		for(int x = 0; x < 9; x++) {
			for(int y = 0; y < 9; y++) {
				if(puzzle.getRecentGrid()[y][x] != puzzle.getSolvedGird()[y][x]){
					mistakes++;
				}
			}
		}
		return mistakes;
	}
	
	public void giveHintPuzzle(NumberPuzzle puzzle){
		int[] coords = puzzle.searchMistake();
		if(coords.length == 0){
			puzzle.giveHint();
		}
		else{
			spielfeld.displayMistake(coords[0], coords[1]);
		}
	}
}
