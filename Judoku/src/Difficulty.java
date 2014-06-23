//Alternative
//Index zwischen 1 und 100 als Schwierigkeit
//In der Oberfläche direkt angeben oder per Umrechnung -> Easy = Random zwischen 1 und 20

public enum Difficulty {
	EASY,		//Logisch geschnittene Sudokus mit 30 oder mehr Vorgaben
	MEDIUM,		//Logisch geschnittene Sudokus mit 25-30 Vorgaben und 2x Zufallsschneiden
	HARD,		//Vollstaendig geschnittene Sudokus mit 21-25 Vorgaben
	EXTREME;	//Vollstaendig geschnittene Sudokus mit weniger als 21 Vorgaben
	
	public int toInt(){
		if(this == EASY){
			return 0;
		}
		if(this == MEDIUM){
			return 1;
		}
		if(this == HARD){
			return 2;
		}
		return 3;
	}
	
	public int toRandomCuttingIndex(){
		if(this == EASY){
			return 0;
		}
		if(this == MEDIUM){
			return 2;
		}
		return 81;
	}
	
	public int minNumberOfClues(){
		if(this == EASY){
			return 30;
		}
		if(this == MEDIUM){
			return 25;
		}
		if(this == HARD){
			return 21;
		}
		return 17;
	}
}
