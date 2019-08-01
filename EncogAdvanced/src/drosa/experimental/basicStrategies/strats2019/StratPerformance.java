package drosa.experimental.basicStrategies.strats2019;

public class StratPerformance {
	
	double pf = 0.0;
	double pfYears = 0.0;
	int years = 0;
	double avgPips  = 0.0;
	int winPips = 0;
	int lostPips = 0;
	int trades = 0;
	int wins = 0;
	int losses = 0;
	
	double initialBalance = 0;
	double maxBalance = 0;
	double actualBalance = 0;
	double maxDD = 0;
	
	public double getPf() {
		return pf;
	}
	public void setPf(double pf) {
		this.pf = pf;
	}
	public int getYears() {
		return years;
	}
	public void setYears(int years) {
		this.years = years;
	}
	public double getAvgPips() {
		return avgPips;
	}
	public void setAvgPips(double avgPips) {
		this.avgPips = avgPips;
	}
	public double getPfYears() {
		return pfYears;
	}
	public void setPfYears(double pfYears) {
		this.pfYears = pfYears;
	}
	public double getMaxDD() {
		return maxDD;
	}
	public void setMaxDD(double maxDD) {
		this.maxDD = maxDD;
	}	
		
	public int getTrades() {
		return trades;
	}
	public void setTrades(int trades) {
		this.trades = trades;
	}
	public int getWinPips() {
		return winPips;
	}
	public void setWinPips(int winPips) {
		this.winPips = winPips;
	}
	public int getLostPips() {
		return lostPips;
	}
	public void setLostPips(int lostPips) {
		this.lostPips = lostPips;
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
	public double getInitialBalance() {
		return initialBalance;
	}
	public void setInitialBalance(double initialBalance) {
		this.initialBalance = initialBalance;
		this.actualBalance = initialBalance;
		this.maxBalance	= initialBalance;
	}
	public double getMaxBalance() {
		return maxBalance;
	}
	public void setMaxBalance(double maxBalance) {
		this.maxBalance = maxBalance;
	}
	public double getActualBalance() {
		return actualBalance;
	}
	public void setActualBalance(double actualBalance) {
		this.actualBalance = actualBalance;
	}
	public void reset() {
		pf = 0.0;
		pfYears = 0.0;
		years = 0;
		avgPips  = 0.0;
		 maxDD = 0;		
		winPips = 0;
		lostPips = 0;
		trades = 0;
		wins = 0;
		losses = 0;
	}
	
	public void addTrade(long miniLots,int pips,int comm) {
		trades++;
		pips-=comm;
		if (pips>=0){
			winPips += pips;
			wins++;
		}else{
			lostPips += -pips;
			losses++;
		}		
		
		actualBalance += miniLots*0.1*pips;
		if (actualBalance>=maxBalance){
			maxBalance = actualBalance;
		}else{
			double actualDD = 100.0-actualBalance*100.0/maxBalance;
			if (actualDD>=maxDD){
				maxDD = actualDD;
			}
		}		
	}
	

}
