import java.awt.EventQueue;
import java.util.Arrays;


public class Controller{
	public static void main(String[] args) {
		initGui();
	}
	public static void initGui(){
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					GUI_Window window = new GUI_Window();
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
}
