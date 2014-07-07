import java.awt.EventQueue;
import java.util.Arrays;


public class Controller{
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
					window.getJFrame().setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
	
	public static int[][] deepCopy(int [][] template){
		int[][] copy = new int[template.length][0];
		for(int i = 0; i < template.length; i++){
			copy[i] = Arrays.copyOf(template[i], template[i].length);
		}
		return copy;
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
