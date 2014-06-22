import java.util.Arrays;


public class Controller {
	public static void main (String[]args){
		
	}
	
	public static void initGui(){
		//creates GUI objects and sets the windows to visible
		System.out.println("Hello World");
		
	}
	
	public static int[][] deepCopy(int [][] template){
		int[][] copy = new int[template.length][0];
		for(int i = 0; i < template.length; i++){
			copy[i] = Arrays.copyOf(template[i], template[i].length);
		}
		return copy;
	}
}
