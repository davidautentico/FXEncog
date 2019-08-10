package drosa.experimental.basicStrategies.strats2019;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import drosa.utils.MathUtils;

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
	int maxAdversionAcc = 0;
	double maxAdversionAvg = 0; 
	
	double initialBalance = 0;
	double maxBalance = 0;
	double actualBalance = 0;
	double maxDD = 0;
	double profitPer = 0.0;
	
	HashMap<Integer,ArrayList<Double>> monthData = new HashMap<Integer,ArrayList<Double>>();
	HashMap<Integer,ArrayList<Integer>> monthTradesP = new HashMap<Integer,ArrayList<Integer>>();
	HashMap<Integer,ArrayList<Integer>> monthTradesSL = new HashMap<Integer,ArrayList<Integer>>();
	
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
	
	public double getProfitPer() {
		return profitPer;
	}
	public void setProfitPer(double profitPer) {
		this.profitPer = profitPer;
	}
	
	public int getMaxAdversionAcc() {
		return maxAdversionAcc;
	}
	public void setMaxAdversionAcc(int maxAdversionAcc) {
		this.maxAdversionAcc = maxAdversionAcc;
	}
	public double getMaxAdversionAvg() {
		return maxAdversionAvg;
	}
	public void setMaxAdversionAvg(double maxAdversionAvg) {
		this.maxAdversionAvg = maxAdversionAvg;
	}
	
	//avgDD	
	public double getMonthDataDD(double dt){
		
		double val = 0;
		ArrayList<Double> values = new ArrayList<Double>();
		
		Object[] keys = monthData.keySet().toArray();
		Arrays.sort(keys);
		
		double lastBalance = initialBalance;
		for(Object key : keys) {
			ArrayList<Double> v = monthData.get(key);
			double initialBal = v.get(0);
			double maxDD = 0.0;
			double maxBal= initialBal;
			for (int i=1;i<v.size();i++){
				double aBal = v.get(i);
				if (aBal<maxBal){
					double dd = 100.0-aBal*100.0/maxBal;
					if (dd>=maxDD) maxDD = dd;
				}else{
					maxBal = aBal;
				};
			}
			//System.out.println(key+" "+maxDD);
			values.add(maxDD);
		}
		
		double avg = MathUtils.average(values);
		double dt1 = Math.sqrt(MathUtils.variance(values));
		
		return avg+dt1*dt;
	}
	
	//avgDD	
	public double getMonthDataDDRR(double initialBalance,double risk,double dt){
		
		double val = 0;
		ArrayList<Double> values = new ArrayList<Double>();
		
		Object[] keys = monthTrades.keySet().toArray();
		Arrays.sort(keys);
		
		double lastBalance = initialBalance;
		for(Object key : keys) {
			ArrayList<Double> v =  monthTrades.get(key);
			double initialBal = initialBalance;
			double maxDD = 0.0;
			double maxBal= initialBal;
			double actualBal = initialBal;
			for (int i=0;i<v.size();i++){
				double riskF = v.get(i)*risk;
				
				double old = actualBal;
				actualBal  = actualBal*(1+riskF/100.0);
				System.out.println(v.get(i)
						+" || "+old
						+" || "+actualBal
						+" "+risk
						);
				if (actualBal<maxBal){
					double dd = 100.0-actualBal*100.0/maxBal;
					if (dd>=maxDD){
						maxDD = dd;
						/*System.out.println("[maxdd] "+v.get(i)
								+" || "+actualBal+" || "+maxBal
								+" || "+dd
								);*/
					}
				}else{
					maxBal = actualBal;
				};
				/*System.out.println(v.get(i)
						+" || "+actualBal+" || "+maxBal
						);*/
			}
			if (v.size()>0){
				System.out.println(key+" "+maxDD);
				values.add(maxDD);
			}
		}
		
		double avg = MathUtils.average(values);
		double dt1 = Math.sqrt(MathUtils.variance(values));
		
		return avg+dt1*dt;
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
		monthData.clear();
		monthTradesP.clear();
		monthTradesSL.clear();
	}
	
	public void addTrade(long miniLots,
			int pips,
			int sl,//reward:risk
			int maxAdversion,
			int comm,
			Calendar cal) {
		trades++;
		pips-=comm;
		if (pips>=0){
			winPips += pips;
			wins++;
		}else{
			lostPips += -pips;
			losses++;
		}		
		//averagemaxloss
		maxAdversionAcc += maxAdversion;
		maxAdversionAvg = maxAdversionAcc*0.1/trades;
		
		actualBalance += miniLots*0.1*pips;
		if (actualBalance>=maxBalance){
			maxBalance = actualBalance;
		}else{
			double actualDD = 100.0-actualBalance*100.0/maxBalance;
			if (actualDD>=maxDD){
				maxDD = actualDD;
			}
		}	
		
		profitPer = actualBalance*100.0/initialBalance-100.0;
		
		int m = cal.get(Calendar.MONTH);
		int y = cal.get(Calendar.YEAR);
		
		int key = y*100+m;
		if (!monthData.containsKey(key)){
			monthData.put(key, new ArrayList<Double>());
			monthTradesP.put(key, new ArrayList<Integer>());
			monthTradesSL.put(key, new ArrayList<Integer>());
		}

		monthData.get(key).add(actualBalance);
		monthTradesP.get(key).add(pips);
		monthTradesSL.get(key).add(sl);
		/*if (!monthData.containsKey(key)){
			monthData.put(key, actualBalance);
		}else{
			monthData.p
		}*/
	}
	

}
