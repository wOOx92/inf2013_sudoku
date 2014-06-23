//Alternative
//Index zwischen 1 und 100 als Schwierigkeit
//In der Oberfläche direkt angeben oder per Umrechnung -> Easy = Random zwischen 1 und 20

public enum Difficulty {
	EASY,
	MEDIUM,
	HARD,
	EXTREME;
	
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
}
