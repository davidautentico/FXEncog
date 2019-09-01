package drosa.finance.classes;

public class StrategyPerformance {
	
	int trades = 0;
	int wins = 0;
	int losses = 0;
	
	public int getTrades() {
		return trades;
	}
	public void setTrades(int trades) {
		this.trades = trades;
	}
	public int getWins() {
		return wins;
	}
	public void setWins(int wins) {
		this.wins = wins;
	}
	public int getLosses() {
		return losses;
	}
	public void setLosses(int losses) {
		this.losses = losses;
	}
	public void reset() {
		trades = 0;
		wins = 0;
		losses = 0;
	}
	
	public double getWinRate(){
		if (trades==0) return -1;
		else return wins*100.0/trades;
	}
	
	
}
