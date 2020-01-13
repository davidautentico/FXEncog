package drosa.apuestas;

import drosa.utils.PrintUtils;

public class ApuestasStats {
	
	String team="";
	int bets = 0;
	int wins = 0;
	double profit$ = 0;
	double avgOdds = 1.0;
	
	public ApuestasStats(String team) {
		this.team = team;
	}
	
	public double getAvgOdds() {
		return avgOdds;
	}

	public void setAvgOdds(double avgOdds) {
		this.avgOdds = avgOdds;
	}

	public double getPercent(){
		return wins*100.0/bets;
	}
	public String getTeam() {
		return team;
	}
	public void setTeam(String team) {
		this.team = team;
	}
	public int getBets() {
		return bets;
	}
	public void setBets(int bets) {
		this.bets = bets;
	}
	public int getWins() {
		return wins;
	}
	public void setWins(int wins) {
		this.wins = wins;
	}
	public double getProfit$() {
		return profit$;
	}
	public void setProfit$(double profit$) {
		this.profit$ = profit$;
	}
	
	public String toString(){
		return team+" "+PrintUtils.Print2Int(bets,4)+" "+PrintUtils.Print2dec(avgOdds, false)+" "+PrintUtils.Print2dec(wins*100.0/bets, false)+" "+PrintUtils.Print2dec(profit$, false);
	}
	public void addResult(double profit, double odds) {
		bets++;
		this.avgOdds = (this.avgOdds*(bets-1)+odds)/(bets);
		if (profit>=0) wins++;
		profit$+=profit;
	}

}
