//Grobe Gui Markus -> bis Dienstag

//JTable 9*9
//Buttons
	//Hint
	//GenerateNew
	//Reset
	//Difficulty auswahl
//Events f�r jeden Button

//�ndert die Gui Table direkt das Objekt?

public class Gui {
	private NumberPuzzle gameField;
	
	//Wird aufgerufen wenn das gameField ge�ndert wird oder resettet wird.
	public void refreshView(){
		
	}
	
	public void setGameField(NumberPuzzle newGameField){
		this.gameField = newGameField;
		refreshView();
	}
	
	public void resetGame(){
		this.gameField.reset();
		refreshView();
	}
	
	//Button clicked events f�r jeden Button
	
	//GridChangedEvent
	
	//Buttons und JTable
}

