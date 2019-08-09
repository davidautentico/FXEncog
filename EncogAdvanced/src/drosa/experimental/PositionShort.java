package drosa.experimental;

import java.util.ArrayList;
import java.util.Calendar;

import drosa.finances.QuoteShort;
import drosa.strategies.PositionStatus;
import drosa.strategies.auxiliar.PositionType;
import drosa.utils.DateUtils;
import drosa.utils.PrintUtils;
import drosa.utils.TradeLog;
import drosa.utils.TradingUtils;

public class PositionShort {
	
	long id = -1;
	Calendar pendingCal = Calendar.getInstance();
	Calendar openCal = Calendar.getInstance();
	Calendar closeCal = Calendar.getInstance();
	int entry = 0;
	int sl = -1;
	int tp = -1;
	int expiredTime = 0;
	long microLots = 0;
	double margin = 0;
	double risk = 0;
	int win = 0; //1:win, 0:neutral, -1:loss
	long pendingIndex = 0;
	int pendingDayIndex = 0;
	long openIndex = 0;
	long closeIndex = 0;
	int openDiff = 0;
	double winPips = 0;
	double currentAtr=0.0;
	int dayOrder = 0;
	PositionStatus positionStatus = PositionStatus.NONE;
	PositionType positionType = PositionType.NONE;
	boolean isPendingValid = true;
	boolean isAutoRemoveActivate = false; //cuando alcanza una tasa negativa despues de una positiva
	double risked$$ = 0.0;
	double pip$$ = 0.0;
	private double maxAdversion;
	private int extraParam = 0;
	private int order = 0;
	private int order0 = 0;
	private int maxProfit = 0;
	private int transactionCosts = 0;
	private int maxLoss=0;
	
	
	public int getMaxLoss() {
		return maxLoss;
	}
	public void setMaxLoss(int maxLoss) {
		this.maxLoss = maxLoss;
	}
	public int getTransactionCosts() {
		return transactionCosts;
	}
	public void setTransactionCosts(int transactionCosts) {
		this.transactionCosts = transactionCosts;
	}
	public int getMaxProfit() {
		return maxProfit;
	}
	public void setMaxProfit(int maxProfit) {
		this.maxProfit = maxProfit;
	}
	public int getOrder0() {
		return order0;
	}
	public int getExtraParam() {
		return extraParam;
	}
	public void setExtraParam(int extraParam) {
		this.extraParam = extraParam;
	}
	public double getPip$$() {
		return pip$$;
	}
	public void setPip$$(double pip$$) {
		this.pip$$ = pip$$;
	}
	public double getRisked$$() {
		return risked$$;
	}
	public void setRisked$$(double risked$$) {
		this.risked$$ = risked$$;
	}
	public boolean isPendingValid() {
		return isPendingValid;
	}
	public void setPendingValid(boolean isPendingValid) {
		this.isPendingValid = isPendingValid;
	}
	public boolean isAutoRemoveActivate() {
		return isAutoRemoveActivate;
	}
	public void setAutoRemoveActivate(boolean isAutoRemoveActivate) {
		this.isAutoRemoveActivate = isAutoRemoveActivate;
	}
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public int getDayOrder() {
		return dayOrder;
	}
	public void setDayOrder(int dayOrder) {
		this.dayOrder = dayOrder;
	}
	public double getCurrentAtr() {
		return currentAtr;
	}
	public void setCurrentAtr(double currentAtr) {
		this.currentAtr = currentAtr;
	}
	public double getWinPips() {
		return winPips;
	}
	public void setWinPips(double winPips) {
		this.winPips = winPips;
	}
	public int getExpiredTime() {
		return expiredTime;
	}
	public void setExpiredTime(int expiredTime) {
		this.expiredTime = expiredTime;
	}
	public int getOpenDiff() {
		return openDiff;
	}
	public void setOpenDiff(int openDiff) {
		this.openDiff = openDiff;
	}
	
	public Calendar getPendingCal() {
		return pendingCal;
	}
	public void setPendingCal(Calendar pendingCal) {
		this.pendingCal = pendingCal;
	}
	public Calendar getOpenCal() {
		return openCal;
	}
	public void setOpenCal(Calendar openCal) {
		this.openCal.setTimeInMillis(openCal.getTimeInMillis()); 
	}
	public Calendar getCloseCal() {
		return closeCal;
	}
	public void setCloseCal(Calendar closeCal) {
		this.closeCal = closeCal;
	}
	public int getEntry() {
		return entry;
	}
	public void setEntry(int entry) {
		this.entry = entry;
	}
	public int getSl() {
		return sl;
	}
	public void setSl(int sl) {
		this.sl = sl;
	}
	public int getTp() {
		return tp;
	}
	public void setTp(int tp) {
		this.tp = tp;
	}
	public long getMicroLots() {
		return microLots;
	}
	public void setMicroLots(long microLots) {
		this.microLots = microLots;
	}
	
	public double getMargin() {
		return margin;
	}
	public void setMargin(double margin) {
		this.margin = margin;
	}
	public PositionStatus getPositionStatus() {
		return positionStatus;
	}
	public void setPositionStatus(PositionStatus positionStatus) {
		this.positionStatus = positionStatus;
	}
	public PositionType isClose() {
		return positionType;
	}
	public void setPositionType(PositionType positionType) {
		this.positionType = positionType;
	}
			
	public PositionType getPositionType() {
		return positionType;
	}
	public long getPendingIndex() {
		return pendingIndex;
	}
	public void setPendingIndex(long pendingIndex) {
		this.pendingIndex = pendingIndex;
	}
	
	public int getPendingDayIndex() {
		return pendingDayIndex;
	}
	public void setPendingDayIndex(int pendingDayIndex) {
		this.pendingDayIndex = pendingDayIndex;
	}
	public long getOpenIndex() {
		return openIndex;
	}
	public void setOpenIndex(long openIndex) {
		this.openIndex = openIndex;
	}
	public long getCloseIndex() {
		return closeIndex;
	}
	public void setCloseIndex(long closeIndex) {
		this.closeIndex = closeIndex;
	}
	public int getWin() {
		return win;
	}
	public void setWin(int win) {
		this.win = win;
	}
	
	public double getRisk() {
		return risk;
	}
	public void setRisk(double risk) {
		this.risk = risk;
	}
	public String toString(){
		String res = this.positionStatus+" "+this.positionType+" "+this.microLots+" "+this.entry+" "+this.sl+" "+this.tp+" "+this.win;		
		return res; 
	}
	
	public String toString2(){
		String pendingStr = "";
		String openStr = "";
		String closeStr = "";
		//pendingStr = DateUtils.datePrint(this.pendingCal);
		if (this.positionStatus==PositionStatus.PENDING){
			pendingStr = DateUtils.datePrint(this.pendingCal);
			closeStr="";
		}
		if (this.positionStatus==PositionStatus.OPEN){
			openStr = DateUtils.datePrint(this.openCal);
			closeStr="";
		}
		if (this.positionStatus==PositionStatus.CLOSE){
			pendingStr = DateUtils.datePrint(this.pendingCal);
			openStr = DateUtils.datePrint(this.openCal);
			closeStr = DateUtils.datePrint(this.closeCal);
		}
		String res = this.positionStatus+" "+this.positionType
				+" "+pendingStr
				+" "+openStr
				+" "+closeStr
				+" "+PrintUtils.Print2(this.risk)
				+" "+pendingIndex+" "+openIndex+" "+closeIndex+" "+this.entry+" "+this.sl+" "+this.tp+" "+this.microLots+" "+this.win+" "+this.winPips;		
		return res; 
	}
	
	public static void removePositions(ArrayList<PositionShort> positions,PositionStatus positionStatus){
		
		int i=0;
		while (i<positions.size()){
			PositionShort p = positions.get(i);
			
			if (p.getPositionStatus()==positionStatus){
				positions.remove(i);
			}else{
				i++;
			}
			//System.out.println(i+" "+positions.size());
		}
		
	}
	public static PositionsStats updatePositions(ArrayList<PositionShort> positions,
			PositionsStats stats,Calendar  cal,
			QuoteShort q,int qIndex,double actualBalance,double comm) {
		// TODO Auto-generated method stub
		stats.reset();
		double newBalance = actualBalance;
		int totalOpens = 0;	
		QuoteShort.getCalendar(cal, q);
		for (int i=0;i<positions.size();i++){			
			PositionShort p = positions.get(i);
			double winPips = 0;
			int actualShort = 0;
			int actualLong = 999999;
			if (p.getPositionStatus()==PositionStatus.PENDING){
				if (q.getLow5()<=p.getEntry() && p.getEntry()<=q.getHigh5()){
					p.setPositionStatus(PositionStatus.OPEN);
					p.setOpenIndex(qIndex);
				}
			}
			if (p.getPositionStatus()==PositionStatus.OPEN){
				boolean closed = false;
				if (p.getPositionType()==PositionType.LONG){
					actualLong = p.getEntry();
					//CIERRE NO WIN or expired
					if (q.getLow5()<=p.getSl() && p.getSl()>0){
						winPips = p.getEntry()-p.getSl();
						p.setWin(-1);
						p.setPositionStatus(PositionStatus.CLOSE);
						p.setWinPips(-winPips-comm*10);
						p.getCloseCal().setTimeInMillis(cal.getTimeInMillis());
						closed = true;
					}else if (qIndex>=p.getExpiredTime() && p.getExpiredTime()>0){
						winPips = q.getClose5()-p.getEntry()-comm*10;
						if (winPips>=0) p.setWin(1);
						else  p.setWin(-1);
						p.setPositionStatus(PositionStatus.CLOSE);
						p.getCloseCal().setTimeInMillis(cal.getTimeInMillis());
						p.setWinPips(winPips);
						closed = true;
					}
					//CIERRE WIN
					else if ((q.getHigh5()>=p.getTp() && p.getTp()>0) && qIndex!=p.getOpenIndex() ){
						winPips = p.getTp()-p.getEntry();
						p.setWin(1);
						p.setPositionStatus(PositionStatus.CLOSE);
						p.getCloseCal().setTimeInMillis(cal.getTimeInMillis());
						p.setWinPips(winPips-comm*10);
						closed = true;
					}
				}else if (p.getPositionType()==PositionType.SHORT){
					actualShort = p.getEntry();
					//CIERRE NO WIN or expired
					if (q.getHigh5()>=p.getSl() && p.getSl()>0){
						winPips = p.getSl()-p.getEntry();
						p.setWin(-1);
						p.setPositionStatus(PositionStatus.CLOSE);
						p.getCloseCal().setTimeInMillis(cal.getTimeInMillis());
						p.setWinPips(-winPips-comm*10);
						closed = true;
					}else if (qIndex>=p.getExpiredTime() && p.getExpiredTime()>0){
						winPips = p.getEntry()-q.getClose5();
						if (winPips>=0) p.setWin(1);
						else  p.setWin(-1);
						p.setPositionStatus(PositionStatus.CLOSE);
						p.getCloseCal().setTimeInMillis(cal.getTimeInMillis());
						p.setWinPips(winPips-comm*10);
						closed = true;
					}
					//cierre win
					else if ((q.getLow5()<=p.getTp() && p.getTp()>0) && qIndex!=p.getOpenIndex()){
						winPips = p.getEntry()-p.getTp();
						p.setWin(1);
						p.setPositionStatus(PositionStatus.CLOSE);
						p.getCloseCal().setTimeInMillis(cal.getTimeInMillis());
						p.setWinPips(winPips-comm*10);
						closed = true;
					}
				}
				if (!closed){
					if (actualLong<stats.getMinLongEntry()) stats.setMinLongEntry(actualLong);
					if (actualShort>stats.getMaxShortEntry()) stats.setMaxShortEntry(actualShort);
					totalOpens++;					
				}else{
					double amount =p.getMicroLots()*p.getWinPips()*0.1*0.1;
					double accProfit = stats.getTotalProfit$();
					double accLosses = stats.getTotalLoss$();
					newBalance += amount;
					if (amount>=0) stats.setTotalProfit$(accProfit+amount);
					else stats.setTotalLoss$(accLosses+amount);
					//System.out.println("new Balance: "+newBalance+" "+winPips);
				}
			}//OPEN
		}
		stats.setTotalOpens(totalOpens);
		stats.setActualBalance(newBalance);
		return stats;
	}
	
	public static int countTotal(ArrayList<PositionShort> positions,
			PositionStatus status) {
		// TODO Auto-generated method stub
		int total = 0;
		for (int i=0;i<positions.size();i++){
			PositionShort p = positions.get(i);
			if (p.getPositionStatus()==status)
				total++;
		}
		return total;
	}
	public static int countTotalResult(ArrayList<PositionShort> positions,
			int result) {
		// TODO Auto-generated method stub
		int total = 0;
		for (int i=0;i<positions.size();i++){
			PositionShort p = positions.get(i);
			if (p.getWin()==result)
				total++;
		}
		return total;
	}
	public static void studyDOdistance(ArrayList<QuoteShort> dailyData, ArrayList<PositionShort> positions) {
		// TODO Auto-generated method stub
		
		ArrayList<Integer> DOWinners = new ArrayList<Integer>();
		ArrayList<Integer> DOLosers = new ArrayList<Integer>();
		for (int i=0;i<=5000;i++){
			DOWinners.add(0);
			DOLosers.add(0);
		}
		
		int index = 0;
		for (int i=0;i<positions.size();i++){
			if (positions.get(i).getPositionStatus()!=PositionStatus.CLOSE) continue;
			int win = positions.get(i).getWin();
			int entry = positions.get(i).getEntry();
			int day = TradingUtils.getDayIndexShort(dailyData, positions.get(i).getOpenCal(),index);
			double DOvalue = dailyData.get(day).getOpen5();
			int diff = (int) (Math.abs(DOvalue-entry)/10);
			if (win==1){
				int count = DOWinners.get(diff);
				DOWinners.set(diff, count+1);
			}else{
				int count = DOLosers.get(diff);
				DOLosers.set(diff, count+1);
			}
		}
		
		for (int i=0;i<=40;i++){
			int total = DOWinners.get(i)+DOLosers.get(i);
			int wins = DOWinners.get(i);
			System.out.println(i+" "+PrintUtils.Print2(wins*100.0/total));
		}
	}
	public static void removeExpired(ArrayList<PositionShort> positions,
			PositionStatus status,int actual,int max) {
		// TODO Auto-generated method stub
		int i=0;
		long diffExp = 0;
		while (i<positions.size()){
			PositionShort p = positions.get(i);
			diffExp = actual-p.pendingIndex;
			if (p.getPositionStatus()==status && diffExp>=max){
				positions.remove(i);
			}else{
				i++;
			}
			//System.out.println(i+" "+positions.size());
		}
	}
	
	public static void addPosition(ArrayList<PositionShort> positions,
			PositionStatus positionStatus, 
			PositionType positionType,
			long microLots,
			int entry, int tp,
			int sl,int expiredTime, int index) {
		// TODO Auto-generated method stub
		PositionShort p = new PositionShort();
		p.setPositionStatus(positionStatus);
		p.setPositionType(positionType);
		p.setMicroLots(microLots);
		p.setEntry(entry);
		p.setTp(tp);
		p.setSl(sl);
		p.setExpiredTime(expiredTime);
		if (positionStatus==PositionStatus.PENDING){
			p.setPendingIndex(index);
		}
		positions.add(p);
	}
	public void copy(PositionShort p) {
		// TODO Auto-generated method stub
		openCal.setTimeInMillis(p.getOpenCal().getTimeInMillis());
		closeCal.setTimeInMillis(p.getCloseCal().getTimeInMillis());
		entry = p.getEntry();
		sl = p.getSl();
		tp = p.getTp();
		expiredTime = p.getExpiredTime();
		microLots = p.getMicroLots();
		margin = p.getMargin();
		risk = p.getRisk();
		win = p.getWin(); //1:win, 0:neutral, -1:loss
		pendingIndex = p.getPendingIndex();
		openIndex = p.getOpenIndex();
		closeIndex = p.getCloseIndex();
		openDiff = p.getOpenDiff();
		winPips = p.getWinPips();
		positionStatus = p.getPositionStatus();
		positionType = p.getPositionType();
		currentAtr = p.getCurrentAtr();
		dayOrder = p.getDayOrder();
		id = p.getId();
	}
	
	public static void getHigherLowerPos(
			ArrayList<PositionShort> positions,QuoteShort actual) {
		// TODO Auto-generated method stub
		int higher = -1;
		int lower = -1;
		for (int i=0;i<positions.size();i++){
			PositionShort p = positions.get(i);
			if (p.getPositionStatus()==PositionStatus.OPEN){
				if (p.getPositionType()==PositionType.SHORT && (higher==-1 || p.getEntry()>=higher))
					higher = p.getEntry();
				if (p.getPositionType()==PositionType.LONG && (lower==-1 || p.getEntry()<=lower))
					lower = p.getEntry();
			}
		}
		
		actual.setHigh5(higher);
		actual.setLow5(lower);
	}
	
	public static void evaluatePositions(GlobalStats stats,ArrayList<PositionShort> positions,
			QuoteShort q,Calendar actualCal,int actualIdx,
			boolean isOCO,
			double comm,boolean debug){
		
		boolean isOpen = false;
		int i = 0;
		while (i<positions.size()){
			PositionShort p = positions.get(i);
			boolean open = false;
			boolean closed = false;
			//evaluacion PENDING
			if (p.getPositionStatus()==PositionStatus.PENDING){
				if (p.getPositionType()==PositionType.LONG){
					if (q.getOpen5()<=p.getEntry() && p.getEntry()<=q.getHigh5()){
						open = true;
					}
				}
				if (p.getPositionType()==PositionType.SHORT){
					if (q.getOpen5()>=p.getEntry() && p.getEntry()>=q.getLow5()){
						open = true;
					}
				}
				if (open){
					isOpen = true;
					p.getOpenCal().setTimeInMillis(actualCal.getTimeInMillis());
					p.setOpenIndex(actualIdx);
					p.setPositionStatus(PositionStatus.OPEN);
					if (debug)
					System.out.println("[OPEN] "+DateUtils.datePrint(p.getPendingCal())+" || "+p.toString2());
				}
			}
			//evaluacion de las abiertas
			if (p.getPositionStatus()==PositionStatus.OPEN){
				closed = false;
				String type = "";
				double pips = 0;
				double pipsSL = Math.abs(p.getEntry()-p.getSl())*0.1;
				double pipsTP = Math.abs(p.getEntry()-p.getTp())*0.1;
				if (p.getPositionType()==PositionType.LONG){
					if (q.getLow5()<=p.getSl()){
						pips = (p.getSl()-p.getEntry())*0.1;
						type="SL";
						closed=true;
					}
					if (q.getHigh5()>=p.getTp()){
						pips = (p.getTp()-p.getEntry())*0.1;
						type="TP";
						closed = true;
					}
				}
				if (p.getPositionType()==PositionType.SHORT){
					if (q.getHigh5()>=p.getSl()){
						pips = (p.getEntry()-p.getSl())*0.1;
						type="SL";
						closed=true;
					}
					if (q.getLow5()<=p.getTp()){
						pips = (p.getEntry()-p.getTp())*0.1;
						type="TP";
						closed = true;
					}
				}
				
				if (closed){
					p.getCloseCal().setTimeInMillis(actualCal.getTimeInMillis());
					double pipsEarned = pips-comm;
					if (pipsEarned>=0){
						stats.addWins(1);
						stats.addWinPips(Math.abs(pipsEarned));
					}else{
						stats.addLosses(1);
						stats.addLostPips(Math.abs(pipsEarned));
					}
					p.setPositionStatus(PositionStatus.CLOSE);
					if (debug){
						System.out.println("[CLOSED "+type+"] "+p.toString2());
					}
					
				}
			}//open
			if (closed){
				positions.remove(i);
			}else{
				i++;
			}
		}//positions
		
		if (isOpen && isOCO){
			PositionShort.closePendings(positions);
			//System.out.println("total pendings: "+PositionShort.countTotal(positions, PositionStatus.PENDING)+" "+positions.size());	
		}
		
	}
	
	public static void evaluatePositions(ArrayList<PositionShort> positions,
			QuoteShort maxMin,
			GlobalStats stats,double comm, QuoteShort q,Calendar cal,int actualIdx,boolean debug) {
		
		QuoteShort.getCalendar(cal, q);
		
		int longLow = maxMin.getHigh5();
		int shortHigh= maxMin.getLow5(); 
		int j = 0;
		while (j<positions.size()){
			//System.out.println("evaluando "+j);
			PositionShort p = positions.get(j);
			
			if (p.getPositionStatus()==PositionStatus.PENDING){
				long diffPending = actualIdx-p.getPendingIndex();
				if (diffPending>=100){
					positions.remove(j);//borramos y no avanzamos
					//System.out.println("borrado pos "+j);
					continue;
				}
				if (p.getPositionType()==PositionType.SHORT){//contratrendmode
					if (q.getHigh5()>=p.getEntry()){
						p.setOpenIndex(actualIdx);
						p.setPositionStatus(PositionStatus.OPEN);
						p.getOpenCal().setTimeInMillis(cal.getTimeInMillis());
						//System.out.println("[OPEN SHORT Pending] "+p.toString2());
					}
				}else if (p.getPositionType()==PositionType.LONG){//contratrendmode
					if (q.getLow5()<=p.getEntry()){
						p.setOpenIndex(actualIdx);
						p.setPositionStatus(PositionStatus.OPEN);
						p.getOpenCal().setTimeInMillis(cal.getTimeInMillis());
					}
				}
			}
			
			if (p.getPositionStatus()==PositionStatus.OPEN){
				//System.out.println(q.toString());
				boolean closed = false;
				int win = 0;
				double pips = 0;
				if (p.getPositionType()==PositionType.SHORT){
					if (q.getHigh5()>=p.getSl()){
						pips = (p.getEntry()-p.getSl())*0.1;
						closed = true;
					}else if (actualIdx==p.getOpenIndex() && q.getClose5()<=p.getTp()){
						pips = (p.getEntry()-p.getTp())*0.1;
						closed = true;
					}else if (actualIdx!=p.getOpenIndex() && q.getLow5()<=p.getTp()){
						pips = (p.getEntry()-p.getTp())*0.1;
						closed = true;
					}
				}
				if (p.getPositionType()==PositionType.LONG){
					if (q.getLow5()<=p.getSl()){
						pips = (p.getSl()-p.getEntry())*0.1;
						closed = true;
						//System.out.println("[SL] "+q.toString());
					}else if (actualIdx==p.getOpenIndex() && q.getClose5()>=p.getTp()){
						pips = (p.getTp()-p.getEntry())*0.1;
						closed = true;
						//System.out.println("[TP CL] "+q.toString());
					}else if (actualIdx!=p.getOpenIndex() && q.getHigh5()>=p.getTp()){
						pips = (p.getTp()-p.getEntry())*0.1;
						closed = true;
						//System.out.println("[TP H] "+q.toString());
					}
				}	
				
				if (closed){	
					int riskedPips = Math.abs(p.getSl()-p.getEntry());
					stats.addRiskPips(riskedPips);
					
					p.getCloseCal().setTimeInMillis(cal.getTimeInMillis());
					p.setPositionStatus(PositionStatus.CLOSE);
					p.setWin(win);
					
					double profit = 0;
					double pipsEarned = pips-comm;
					if (pipsEarned>=0){						
						profit = p.getMicroLots()*0.1*(pipsEarned);
						stats.addWins(1);
						stats.addWinPips(pipsEarned);
						stats.addWinLossSeq(1);
						win=1;
					}else{					
						profit = p.getMicroLots()*0.1*(pipsEarned);
						stats.addLosses(1);
						stats.addLostPips(-pipsEarned);
						stats.addWinLossSeq(-1);
						win=-1;
					}
					p.setWin(win);
					double DD = 0.0;
					double balance = stats.getBalance();
					stats.setBalance(balance+profit);
					if (stats.getMaxBalance()<=stats.getBalance()){
						stats.setMaxBalance(stats.getBalance());
					}					
					else{
						DD = 100.0-stats.getBalance()*100.0/stats.getMaxBalance();
						if (DD>=stats.getMaxDD()) stats.setMaxDD(DD);
					}
					/*if (debug){
						System.out.println(p.toString2()+" || "+win+" "+win*pipsEarned);
					}	*/			
					
					if (p.getId()==1){
						stats.addSpecialTotalTrades(1);
						if (win==1){
							stats.addSpecialWinsTrades(1);
							stats.addSpecialWinPips(pipsEarned);
						}else{
							stats.addSpecialLostPips(-pipsEarned);
						}
					}
					
					positions.remove(j);//borramos y no avanzamos
					if (win==-1){
						//System.out.println(p.toString2()+" || "+win+" "+win*pipsEarned);
					}
										
					if (debug){
						System.out.println(p.toString2()+" || "+win+" "+pipsEarned 
								+" || "+PrintUtils.Print2(stats.getBalance())
								+" "+PrintUtils.Print2(DD)
						);
					}
				}else{
					if (p.getPositionType()==PositionType.LONG && p.getEntry()<=longLow) longLow = p.getEntry();
					//System.out.println("entry y shortHigh: "+p.getEntry()+" "+shortHigh);
					if (p.getPositionType()==PositionType.SHORT && p.getEntry()>=shortHigh){
						shortHigh = p.getEntry();
					}
					j++;//avanzamos
				}
			}else{//open
				j++;
			}					
		}//for positions	
		if (positions.size()==0){
			//System.out.println("posiciones 0");
			longLow = 999999;
			shortHigh= -999999; 
		}
		
		maxMin.setHigh5(longLow);
		maxMin.setLow5(shortHigh);
	}
	public static int getMaxTp(ArrayList<PositionShort> positions,
			PositionType type) {
		
		int res = -1;
		for (int i = 0;i<positions.size();i++){
			PositionShort p = positions.get(i);
			if (p.getPositionType()==type && type ==PositionType.LONG)
				if (p.getTp()>res || res==-1) res = p.getTp();
			if (p.getPositionType()==type && type ==PositionType.SHORT)
				if (p.getTp()<res || res==-1) res = p.getTp();
		}
		
		return res;
	}
	
	public static double closeAllPositions(ArrayList<PositionShort> positions,
			GlobalStats stats, double comm, QuoteShort q_1, Calendar cal,
			int index, PositionType type, boolean debug) {
		// TODO Auto-generated method stub
		double totalPips = 0;
		int j = 0;
		while (j<positions.size()){
			PositionShort p = positions.get(j);			
			if (p.getPositionStatus()==PositionStatus.OPEN){
				//System.out.println(q.toString());
				boolean closed = false;
				int win = 0;
				double pipsEarned = 0;
				if (p.getPositionType()==type && type ==PositionType.LONG){
					pipsEarned = (q_1.getClose5()-p.getEntry())*0.1;
					closed = true;
					totalPips+=pipsEarned;
				}
				if (p.getPositionType()==type && type ==PositionType.SHORT){
					pipsEarned = (p.getEntry()-q_1.getClose5())*0.1;
					closed = true;
					totalPips+=pipsEarned;
				}
				
				if (closed){	
					if (pipsEarned>=0) win =1;
					else win =-1;
					p.getCloseCal().setTimeInMillis(cal.getTimeInMillis());
					p.setPositionStatus(PositionStatus.CLOSE);
					p.setWin(win);
					
					double profit = 0;
					if (win==1){						
						//pipsEarned = Math.abs(p.getTp()-p.getEntry())*0.1;
						profit = p.getMicroLots()*0.1*(pipsEarned-comm);
						stats.addWins(1);
						stats.addWinPips(Math.abs(pipsEarned));
						stats.addWinLossSeq(1);
					}
					if (win==-1){						
						//pipsEarned = Math.abs(p.getSl()-p.getEntry())*0.1;
						profit = -p.getMicroLots()*0.1*(pipsEarned+comm);
						stats.addLosses(1);
						stats.addLostPips(Math.abs(pipsEarned));
						stats.addWinLossSeq(-1);
					}
					double balance = stats.getBalance();
					stats.setBalance(balance+profit);
					if (debug)
						TradeLog.writeToLog("d:\\tradeLog.txt",p.toString2());
										
					positions.remove(j);//borramos y no avanzamos
					if (win==-1){
						//System.out.println(p.toString2()+" || "+win+" "+win*pipsEarned);
					}
					//System.out.println(p.toString2()+" || "+win+" "+win*pipsEarned);
				}else{
					j++;
				}
			}
		}//for positions	
		return totalPips;
	}
	public static double getFloatingPips(ArrayList<PositionShort> positions,
			int price) {
		// TODO Auto-generated method stub
		double res = 0;
		for (int i=0;i<positions.size();i++){
			PositionShort p = positions.get(i);
			if (p.getPositionStatus()==PositionStatus.OPEN){
				if (p.getPositionType()==PositionType.LONG){
					res+= (price-p.getEntry())*0.1;
				}else if (p.getPositionType()==PositionType.SHORT){
					res+= (p.getEntry()-price)*0.1;
				}
			}
		}
		
		return res;
	}
	
	public static void printDayInfo(String header,double initialBalance,ArrayList<PositionShort> positions,ArrayList<Double> balanceArr){
		
		int down = 0;
		double worstM=0;
		double avg=0;
		double avgDown=0;
		int total =0;
		int lastDay = -1;
		double lastDayBalance = -1;
		for (int i=0;i<positions.size();i++){
			PositionShort p = positions.get(i);
			Calendar cal = p.getCloseCal();	
			int m = cal.get(Calendar.DAY_OF_YEAR);
			if (m!=lastDay){
				if (lastDay!=-1){
					double lastBalance = balanceArr.get(i-1);
					double per = lastBalance*100.0/lastDayBalance-100.0;
					/*System.out.println("Month "+lastMonth
							+" "+PrintUtils.Print2dec(lastMonthBalance, false)
							+" "+PrintUtils.Print2dec(lastBalance, false)
							+" "+PrintUtils.Print2dec(per, false));*/
					avg+=per;
					total++;
					if (per<worstM) worstM = per;
					if (per<0){
						avgDown += per;
						down++;
					}
				}
				if (i>0)
					lastDayBalance = balanceArr.get(i-1);
				else lastDayBalance = initialBalance;
				lastDay = m;
			}
		}
		double factor = Math.abs(avg*1.0/avgDown);
		System.out.println(
				header
				+" || "+total
				+" "+PrintUtils.Print2dec(avg*1.0/total, false)
				+" "+PrintUtils.Print2dec(avgDown*1.0/total, false)
				+" "+PrintUtils.Print2dec(worstM, false)
				+" "+down
				+" || "+PrintUtils.Print2dec(factor, false)
				);
	}
	
	public static void printWeekInfo(String header,double initialBalance,ArrayList<PositionShort> positions,ArrayList<Double> balanceArr){
		
		int down = 0;
		double worstM=0;
		double avg=0;
		double avgDown=0;
		int total =0;
		int lastWeek = -1;
		double lastWeekBalance = -1;
		for (int i=0;i<positions.size();i++){
			PositionShort p = positions.get(i);
			Calendar cal = p.getCloseCal();	
			int m = cal.get(Calendar.WEEK_OF_YEAR);
			if (m!=lastWeek){
				if (lastWeek!=-1){
					double lastBalance = balanceArr.get(i-1);
					double per = lastBalance*100.0/lastWeekBalance-100.0;
					/*System.out.println("Month "+lastMonth
							+" "+PrintUtils.Print2dec(lastMonthBalance, false)
							+" "+PrintUtils.Print2dec(lastBalance, false)
							+" "+PrintUtils.Print2dec(per, false));*/
					avg+=per;
					total++;
					if (per<worstM) worstM = per;
					if (per<0){
						avgDown += per;
						down++;
					}
				}
				if (i>0)
					lastWeekBalance = balanceArr.get(i-1);
				else lastWeekBalance = initialBalance;
				lastWeek = m;
			}
		}
		double factor = Math.abs(avg*1.0/avgDown);
		System.out.println(
				header
				+" || "+total
				+" "+PrintUtils.Print2dec(avg*1.0/total, false)
				+" "+PrintUtils.Print2dec(avgDown*1.0/total, false)
				+" "+PrintUtils.Print2dec(worstM, false)
				+" "+down
				+" || "+PrintUtils.Print2dec(factor, false)
				);
	}
	
	public static void printMonthInfo(String header,double initialBalance,ArrayList<PositionShort> positions,ArrayList<Double> balanceArr){
		
		int down = 0;
		double worstM=0;
		double avg=0;
		double avgDown=0;
		int total =0;
		int lastMonth = -1;
		double lastMonthBalance = -1;
		for (int i=0;i<positions.size();i++){
			PositionShort p = positions.get(i);
			Calendar cal = p.getCloseCal();	
			int m = cal.get(Calendar.MONTH);
			if (m!=lastMonth){
				if (lastMonth!=-1){
					double lastBalance = balanceArr.get(i-1);
					double per = lastBalance*100.0/lastMonthBalance-100.0;
					/*System.out.println("Month "+lastMonth
							+" "+PrintUtils.Print2dec(lastMonthBalance, false)
							+" "+PrintUtils.Print2dec(lastBalance, false)
							+" "+PrintUtils.Print2dec(per, false));*/
					avg+=per;
					total++;
					if (per<worstM) worstM = per;
					if (per<0){
						avgDown += per;
						down++;
					}
				}
				if (i>0)
					lastMonthBalance = balanceArr.get(i-1);
				else lastMonthBalance = initialBalance;
				lastMonth = m;
			}
		}
		double factor = Math.abs(avg*1.0/avgDown);
		System.out.println(
				header
				+" || "+total
				+" "+PrintUtils.Print2dec(avg*1.0/total, false)
				+" "+PrintUtils.Print2dec(avgDown*1.0/total, false)
				+" "+PrintUtils.Print2dec(worstM, false)
				+" "+down
				+" || "+PrintUtils.Print2dec(factor, false)
				);
	}
	
	public static void printSummaryExt(ArrayList<PositionShort> positions,int n){
		
		int total = positions.size()-n;
		int begin = 0;
		int end = 0;
		for (int i=0;i<total;i++){
			begin = i;
			end = i+n-1;
			printSummary(positions,begin,end);
		}
	}
	
	public static void printSummary(ArrayList<PositionShort> positions,int n){
		
		int total = positions.size();
		int boxes = total/n;
		int begin = 0;
		int end = 0;
		for (int i=1;i<=boxes;i++){
			begin = (i-1)*n;
			end = begin+n-1;
			printSummary(positions,begin,end);
		}
		
		if ((total-1)>end){
			end = total-1;
			begin = end-(n-1);
			printSummary(positions,begin,end);
		}
	}
	
	public static void printSummary(ArrayList<PositionShort> positions,int begin,int end){
		
		int wins = 0;
		int losses = 0;
		int winPips = 0;
		int lostPips = 0;
	
		for (int i=begin;i<=end;i++){
			PositionShort p = positions.get(i);
			
			if (p.getWin()==1){
				wins++;
				winPips+=p.getWinPips();
			}else if (p.getWin()==-1){
				losses++;
				lostPips+=Math.abs(p.getWinPips());
			}
		}
		int total = wins+losses;
		double winPer = wins*100.0/total;
		double avgPips = (winPips-lostPips)*1.0/total;
		System.out.println(
				PrintUtils.Print2Int(wins,3)
				+" "+PrintUtils.Print2Int(losses,3)
				+" "+PrintUtils.Print2dec(winPer, false, 2)//+" "+wins
				//PrintUtils.Print2dec(avgPips, false, 2)
				);
		/*System.out.println(PrintUtils.Print2Int(begin, 5)+" "+PrintUtils.Print2Int(end, 5)
				+" || "+total
				+" "+PrintUtils.Print2dec(winPer, false, 2)
				+" "+PrintUtils.Print2dec(avgPips, false, 2)
				);*/
	}
	
	public static void printSummary(ArrayList<PositionShort> positions,int dFrom,int mFrom,int yFrom,
			int dTo,int mTo,int yTo){
		
		int wins = 0;
		int losses = 0;
		int winPips = 0;
		int lostPips = 0;
		
		Calendar cFrom = Calendar.getInstance();
		Calendar cTo = Calendar.getInstance();
		Calendar cal = Calendar.getInstance();
		cFrom.set(yFrom, mFrom, dFrom);
		cTo.set(yTo, mTo, dTo);
		for (int i=0;i<positions.size();i++){
			PositionShort p = positions.get(i);
			cal = p.getCloseCal();
			if (cal.getTimeInMillis()>cTo.getTimeInMillis()) break;
			if (cal.getTimeInMillis()<cFrom.getTimeInMillis()) continue;
			
			if (p.getWin()==1){
				wins++;
				winPips+=p.getWinPips();
			}else if (p.getWin()==-1){
				losses++;
				lostPips+=Math.abs(p.getWinPips());
			}
		}
		int total = wins+losses;
		double winPer = wins*100.0/total;
		double avgPips = (winPips-lostPips)*1.0/total;
		System.out.println(DateUtils.datePrint(cFrom)+" "+DateUtils.datePrint(cTo)
				+" || "+total
				+" "+PrintUtils.Print2dec(winPer, false, 2)
				+" "+PrintUtils.Print2dec(avgPips, false, 2)
				);
	}
	
	
	public static void studyPositions(
			String header,
			ArrayList<PositionShort> positions,int h1,int h2,int tp,int sl, int order) {
		// TODO Auto-generated method stub
		int wins = 0;
		int losses = 0;
		for (int i=0;i<positions.size();i++){
			PositionShort p = positions.get(i);
			int h = p.getOpenCal().get(Calendar.HOUR_OF_DAY);
			if (p.getPositionStatus()==PositionStatus.CLOSE){
				if (Math.abs(p.getDayOrder())>=order && h1<=h && h<=h2){
					if (p.getWin()==1) wins++;
					if (p.getWin()==-1) losses++;
				}
			}
		}
		int total = wins+losses;
		double winPer = wins*100.0/total;
		double lossPer = 100.0-winPer;
		double avgPips = (winPer*tp-lossPer*sl)*1.0/100.0;
		System.out.println(
				header
				+" "+order
				+" "+tp
				+" "+sl
				+" || "+total
				+" "+PrintUtils.Print2dec(winPer, false, 2)
				+" "+PrintUtils.Print2dec(avgPips, false, 2)
				);
	}
	/**
	 * Diferenca en dias
	 * @param positions
	 * @param actualCal
	 * @param diffThr
	 */
	public static void closeByTime(GlobalStats stats,ArrayList<PositionShort> positions,
			Calendar actualCal,
			int value,
			int index, 
			long diffThr,
			double comm,
			boolean debug) {
		// TODO Auto-generated method stub
		
		int i =0;
		while (i<positions.size()){
			PositionShort p = positions.get(i);
			long millisDiff = actualCal.getTimeInMillis()-p.getOpenCal().getTimeInMillis();
			int days = (int) (millisDiff / (1000*60*60*24));
			boolean closed = false;
			if (days>=diffThr){
				PositionShort.closeByValue(stats,p,value,index,comm);
				if  (debug)
				System.out.println("[CLOSED TIME] "+p.toString2());
				closed = true;
			}
			if (closed) positions.remove(i);
			if (!closed) i++;
		}
	}
	
	/**
	 * Diferenca en bars
	 * @param positions
	 * @param actualCal
	 * @param diffThr
	 */
	public static void closeByTimeBars(GlobalStats stats,ArrayList<PositionShort> positions,
			Calendar actualCal,
			int value,
			int index, 
			long diffThr,
			double comm,
			boolean debug) {
		// TODO Auto-generated method stub
		
		int i =0;
		while (i<positions.size()){
			PositionShort p = positions.get(i);
			boolean closed = false;
			if (p.getPositionStatus()==PositionStatus.OPEN){
				long diff = index-p.getOpenIndex();
				if (diff>=diffThr){
					PositionShort.closeByValue(stats,p,value,index,comm);
					if  (debug)
					System.out.println("[CLOSED TIME] "+p.toString2());
					closed = true;
				}
			}
			if (closed){
				double profit = p.getMicroLots()*0.1*(p.getWinPips());
				double balance = stats.getBalance();
				stats.setBalance(balance+profit);
				if (stats.getMaxBalance()<=stats.getBalance()){
					stats.setMaxBalance(stats.getBalance());
				}
				else{
					double DD = 100.0-stats.getBalance()*100.0/stats.getMaxBalance();
					if (DD>=stats.getMaxDD()) stats.setMaxDD(DD);
				}
				
				positions.remove(i);
			}
			if (!closed) i++;
		}
	}
	
	/**
	 * Mover a BE si el precio se aleja
	 * @param positions
	 * @param actualCal
	 * @param diffThr
	 */
	public static void moveBEDiffPer(GlobalStats stats,ArrayList<PositionShort> positions,
			int value,
			int slPips,
			double per,
			double comm,
			boolean debug) {
		// TODO Auto-generated method stub
		
		int i =0;
		while (i<positions.size()){
			PositionShort p = positions.get(i);
			if (p.getPositionStatus()==PositionStatus.OPEN){
				if (p.getPositionType()==PositionType.LONG){
					int diff = value-p.getEntry();
					if (diff<0){
						double perDiff = Math.abs((diff*0.1)*1.0/slPips);
						if (perDiff>=per){
							p.setTp(p.getEntry()+((int)comm+1)*10);
						}
					}
				}else if (p.getPositionType()==PositionType.SHORT){
					int diff = p.getEntry()-value;
					if (diff<0){
						double perDiff = Math.abs((diff*0.1)*1.0/slPips);
						if (perDiff>=per){
							p.setTp(p.getEntry()-((int)comm+1)*10);
						}
					}
				}
			}
			i++;
		}
	}
	
	public static void closeByValue(GlobalStats stats,PositionShort p, int value, int index,double comm) {
		// TODO Auto-generated method stub
		double pipsEarned = 0;
		
		if (p.getPositionStatus()!=PositionStatus.OPEN) return;
		
		if (p.getPositionType()==PositionType.LONG){
			int pips = value-p.getEntry();
			pipsEarned = pips*0.1-comm;
		}else if (p.getPositionType()==PositionType.SHORT){
			int pips = p.getEntry()-value;
			pipsEarned = pips*0.1-comm;
		}
		
		p.setCloseIndex(index);
		p.setWinPips(pipsEarned);
		p.setPositionStatus(PositionStatus.CLOSE);
		if (pipsEarned>=0){
			stats.addWins(1);
			stats.addWinPips(Math.abs(pipsEarned));
			if (p.getId()==1){
				stats.addSpecialWinsTrades(1);
				stats.addSpecialWinPips(pipsEarned);
			}
		}else{
			stats.addLosses(1);
			stats.addLostPips(Math.abs(pipsEarned));
			if (p.getId()==1){
				stats.addSpecialLostPips(-pipsEarned);
			}
		}
		if (p.getId()==1){
			stats.addSpecialTotalTrades(1);
		}
	}
	
	public static long addPending(ArrayList<PositionShort> positions,
			PositionType positionType,Calendar cal, int entry, int tpValue, int slValue, int index) {
		// TODO Auto-generated method stub
		PositionShort position = new PositionShort();
		position.setPositionStatus(PositionStatus.PENDING);
		position.setPositionType(positionType);
		position.setEntry(entry);
		position.setTp(tpValue);
		position.setSl(slValue);
		position.setPendingIndex(index);
		position.getPendingCal().setTimeInMillis(cal.getTimeInMillis());
		position.setId(cal.getTimeInMillis());
		positions.add(position);
		
		return position.getId();
	}
	
	
	public static void closePendings(ArrayList<PositionShort> positions) {
		// TODO Auto-generated method stub
		int i = 0;
		while (i<positions.size()){
			PositionShort p = positions.get(i);
			if (p.getPositionStatus()==PositionStatus.PENDING){
				positions.remove(i);
			}else{
				i++;
			}
		}
	}
	public static void closeAllPositionsByValue(GlobalStats stats,
			ArrayList<PositionShort> positions,
			int value, int index, double comm) {
		// TODO Auto-generated method stub
		int i = 0;
		while (i<positions.size()){
			PositionShort p = positions.get(i);
			boolean closed = false;
			if (p.getPositionStatus()==PositionStatus.OPEN){
				closed = true;
				PositionShort.closeByValue(stats, p, value, index, comm);				
				
				positions.remove(i);
			}
			if (!closed) i++;
		}
		
	}
	
	public static void closeAllPositionsByValue(GlobalStats stats,
			ArrayList<PositionShort> positions,PositionType positionType,
			int value, int index, double comm) {
		// TODO Auto-generated method stub
		int i = 0;
		while (i<positions.size()){
			PositionShort p = positions.get(i);
			boolean closed = false;
			if (p.getPositionStatus()==PositionStatus.OPEN
					&& p.getPositionType()==positionType
					){
				closed = true;
				PositionShort.closeByValue(stats, p, value, index, comm);
				positions.remove(i);
			}
			if (!closed) i++;
		}
		
	}

	public static double calculatePips(ArrayList<PositionShort> positions,
			int value) {
		
		double totalPips = 0;
		for (int i=0;i<positions.size();i++){
			PositionShort p = positions.get(i);
			if (p.getPositionStatus()!=PositionStatus.OPEN) continue;
			double pips = 0;
			if (p.getPositionType()==PositionType.LONG){
				pips = (value-p.getEntry())*0.1;
			}else if (p.getPositionType()==PositionType.SHORT){
				pips = (p.getEntry()-value)*0.1;
			}
			totalPips += pips;
		}
		
		return totalPips;
	}
	
	private static int avgPrice(ArrayList<PositionShort> positions,PositionType positionType){
		int avgPrice = 0;
		int total= 0;
		for (int i=0;i<positions.size();i++){
			PositionShort p = positions.get(i);
			if (p.getPositionStatus()!=PositionStatus.OPEN) continue;
			if (p.getPositionType()==positionType){
				avgPrice += p.getEntry();
			}
			total++;
		}
		if (total>0)
			return avgPrice/total;
		return 0;
	}
	
	public static void moveToBE(ArrayList<PositionShort> positions, int value,int min,boolean isOnlyOne) {
		
		int avgPriceLongs  = avgPrice(positions,PositionType.LONG);
		int avgPriceShorts = avgPrice(positions,PositionType.SHORT);
		
		for (int i=0;i<positions.size();i++){
			PositionShort p = positions.get(i);
			if (p.getPositionStatus()!=PositionStatus.OPEN) continue;
			if (p.getPositionType()==PositionType.LONG){
				int actualDiff = (int) ((value-p.getEntry())*0.1);
				//if (actualDiff<min) continue;
				int avgPrice = p.getEntry();
				if (!isOnlyOne) avgPrice = avgPriceLongs;
				if (avgPrice+30<=value){
					p.setSl(avgPrice+10);
				}
			}else if (p.getPositionType()==PositionType.SHORT){
				int actualDiff = (int) ((p.getEntry()-value)*0.1);
				//if (actualDiff<min) continue;
				int avgPrice = p.getEntry();
				if (!isOnlyOne) avgPrice = avgPriceShorts;
				if (avgPrice-30>=value){
					p.setSl(avgPrice-10);
				}
			}
		}
		
	}
	public static String evaluatePosition(ArrayList<QuoteShort> data, int index,int end,Calendar cal,
			PositionShort pos,int be,int maxExpiration,double comm,boolean isFromOpen,boolean debug) {
		
		if (pos==null) return "-1 NONE";
		
		int finalIndex = index;
		String codOp = "NONE";
		
		PositionStatus status = pos.getPositionStatus();
		PositionType type = pos.getPositionType();
		boolean closed = false;
		boolean open = false;
		for (int i=index;i<=end;i++){
			finalIndex=i;
			QuoteShort q = data.get(i);
			QuoteShort.getCalendar(cal, q);
			if (status==PositionStatus.PENDING){
				open = false;
				if (type==PositionType.LONG){
					if (q.getHigh5()>=pos.getEntry() && q.getLow5()<=pos.getEntry()){
						pos.setPositionStatus(PositionStatus.OPEN);
						pos.getOpenCal().setTimeInMillis(cal.getTimeInMillis());
						open = true;
					}
				}else if (type==PositionType.SHORT){
					if (q.getLow5()<=pos.getEntry() && q.getHigh5()>=pos.getEntry()){
						pos.setPositionStatus(PositionStatus.OPEN);
						pos.getOpenCal().setTimeInMillis(cal.getTimeInMillis());
						//System.out.println("[OPEN] ");
						open = true;
					}
				}
				if (open){
					pos.setOpenIndex(i);
					if (debug)
						System.out.println("[OPEN] "+pos.toString2()+" "+index+" "+i+" || "+q.toString());
				}else{
					long expDiff = i-pos.getPendingIndex();
					if (expDiff>=maxExpiration){
						pos.setPositionStatus(PositionStatus.CLOSE);
						codOp="EXP";
						return finalIndex+" "+codOp;
					}
				}
			}
			
			status = pos.getPositionStatus();
			
			if (status==PositionStatus.OPEN){
				long expDiff = i-pos.getOpenIndex();
				open=true;
				//System.out.println(pos.toString2()+" || "+index+" "+finalIndex+" "+codOp+" || "+q.toString());
				double pips = 0;
				if (type==PositionType.LONG){
					if (q.getLow5()<=pos.getSl()){
						pips = (pos.getSl()-pos.getEntry())*0.1;
						codOp="SL";
						closed = true;
					}else if (q.getHigh5()>=pos.getTp() && (expDiff>0 || isFromOpen)){
						pips = (pos.getTp()-pos.getEntry())*0.1;
						codOp="TP";
						closed = true;
					}
					else if (q.getClose5()>=pos.getTp() && (expDiff==0 || isFromOpen)){
						pips = (pos.getTp()-pos.getEntry())*0.1;
						codOp="TP";
						closed = true;
					}
					else if (q.getHigh5()>=pos.getEntry()+be*10){
						pos.setSl(pos.getEntry()+0+(int)comm*10);
					}
				}else if (type==PositionType.SHORT){
					if (q.getHigh5()>=pos.getSl()){
						pips = (pos.getEntry()-pos.getSl())*0.1;
						codOp="SL";
						closed = true;
					}else if (q.getLow5()<=pos.getTp() && (expDiff>0 || isFromOpen)){
						pips = (pos.getEntry()-pos.getTp())*0.1;
						codOp="TP";
						closed = true;
					}
					else if (q.getClose5()<=pos.getTp() && (expDiff==0 || isFromOpen)){
						pips = (pos.getEntry()-pos.getTp())*0.1;
						codOp="TP";
						closed = true;
					}
					else if (q.getLow5()<=pos.getEntry()-be*10){
						pos.setSl(pos.getEntry()-0-(int)comm*10);
					}
				}
				
				if (closed){
					double pipsEarned = pips-comm;
					pos.setWinPips(pipsEarned);
					pos.setPositionStatus(PositionStatus.CLOSE);
					pos.getCloseCal().setTimeInMillis(cal.getTimeInMillis());
					if (pipsEarned>=0){
						pos.setWin(1);
					}else{
						pos.setWin(-1);
					}
					if (debug)
						System.out.println(pos.toString2()+" || "+index+" "+finalIndex+" "+codOp+" || "+q.toString());
					break;//salimos
				}
			}
		}
		
		if (open && !closed){
			double pips = 0;
			QuoteShort qn = data.get(end);
			QuoteShort.getCalendar(cal, qn);
			if (type==PositionType.LONG){
				pips = (qn.getClose5()-pos.getEntry())*0.1;
			}else if (type==PositionType.SHORT){
				pips = (pos.getEntry()-qn.getClose5())*0.1;
			}
			double pipsEarned = pips-comm;
			pos.setWinPips(pipsEarned);
			pos.setPositionStatus(PositionStatus.CLOSE);
			pos.getCloseCal().setTimeInMillis(cal.getTimeInMillis());
			if (pipsEarned>=0){
				pos.setWin(1);
			}else{
				pos.setWin(-1);
			}
			/*System.out.println("[CLOSED END] "+pos.toString2()
					+" || "+PrintUtils.Print2dec(pos.getWinPips(),false)+" || "+DateUtils.datePrint(cal));
			*/
			codOp="END";
		}
		
		return finalIndex+" "+codOp;
	}
	public double getMaxAdversion() {
		// TODO Auto-generated method stub
		return maxAdversion;
	}
	public void setMaxAdversion(double maxAdversion) {
		this.maxAdversion = maxAdversion;
	}
	public void setOrder(int order) {
		this.order = order;
	}
	public int getOrder() {
		return order;
	}
	public void setOrder0(int order0) {
		this.order0  = order0;
		
	}
	public static int countTotalPips(ArrayList<PositionShort> positions, PositionStatus status, int value) {

		int result = 0;
		
		for (int i=0;i<positions.size();i++){
			PositionShort p = positions.get(i);
			if (p.getPositionStatus()==status){
				if (p.getPositionType()==PositionType.LONG){
					result += value-p.getEntry();
				}else{
					result += p.getEntry()-value;
				}
			}
		}
		return result;
	}
	
}
