package drosa.experimental.CoreStrategies;

import java.util.Calendar;

import drosa.strategies.PositionStatus;
import drosa.strategies.auxiliar.PositionType;
import drosa.utils.DateUtils;

public class PositionCore {

		Calendar openCal = Calendar.getInstance();
		
		int order = 0;
		int entry = 0;
		int sl = 0;
		int tp = 0;
		int win = 0;
		int entryIndex = 0;
		int maxIndex = 0;
		double pipValue = 0;
		int microLots = 0;
		
		PositionType positionType = PositionType.NONE;
		PositionStatus positionStatus = PositionStatus.NONE;
		
		int maxProfit = 0;
		int maxAdverseBeforeProfit = 0;
		
		int indexMinMax = -1;
		
		
		
		public int getMicroLots() {
			return microLots;
		}
		public void setMicroLots(int micrtoLots) {
			this.microLots = micrtoLots;
		}
		public double getPipValue() {
			return pipValue;
		}
		public void setPipValue(double pipValue) {
			this.pipValue = pipValue;
		}
		public int getOrder() {
			return order;
		}
		public void setOrder(int order) {
			this.order = order;
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
		public PositionType getPositionType() {
			return positionType;
		}
		public void setPositionType(PositionType positionType) {
			this.positionType = positionType;
		}
		public PositionStatus getPositionStatus() {
			return positionStatus;
		}
		public void setPositionStatus(PositionStatus positionStatus) {
			this.positionStatus = positionStatus;
		}
		public int getMaxProfit() {
			return maxProfit;
		}
		public void setMaxProfit(int maxProfit) {
			this.maxProfit = maxProfit;
		}
		public int getMaxAdverseBeforeProfit() {
			return maxAdverseBeforeProfit;
		}
		public void setMaxAdverseBeforeProfit(int maxAdverseBeforeProfit) {
			this.maxAdverseBeforeProfit = maxAdverseBeforeProfit;
		}
		public int getWin() {
			return win;
		}
		public void setWin(int win) {
			this.win = win;
		}
		public int getEntryIndex() {
			return entryIndex;
		}
		public void setEntryIndex(int entryIndex) {
			this.entryIndex = entryIndex;
		}
		public int getMaxIndex() {
			return maxIndex;
		}
		public void setMaxIndex(int maxIndex) {
			this.maxIndex = maxIndex;
		}
		public int getIndexMinMax() {
			return indexMinMax;
		}
		public void setIndexMinMax(int indexMinMax) {
			this.indexMinMax = indexMinMax;
		}	
		
		
		
		public Calendar getOpenCal() {
			return openCal;
		}
		public void setOpenCal(Calendar openCal) {
			this.openCal = openCal;
		}
		public String toString(){
			return DateUtils.datePrint(openCal)+" "+this.order+" "+this.entry+" "+this.sl+" "+this.tp+" "+this.win;
			
		}
		
		
}
