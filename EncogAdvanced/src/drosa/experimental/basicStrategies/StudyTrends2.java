package drosa.experimental.basicStrategies;

import java.util.ArrayList;
import java.util.Calendar;

import drosa.DAO.DAO;
import drosa.data.DataProvider;
import drosa.experimental.forexfactory.FFNewsClass;
import drosa.experimental.zznbrum.TrendClass;
import drosa.finances.QuoteShort;
import drosa.memory.Sizeof;
import drosa.phil.TestLines;
import drosa.utils.DateUtils;
import drosa.utils.PrintUtils;
import drosa.utils.TradingUtils;

public class StudyTrends2 {
	
	public static ArrayList<TrendClass> doTest3(ArrayList<QuoteShort> data,
			int y1,int y2,
			int h1,int h2,
			int minSize,
			int tp,int minDiff,
			boolean reset,
			boolean printSumm,
			int debug
			){
		
		//ArrayList<Double> trendsIndex = new ArrayList<Double>();
		
		int mode = 0;
		int index1 = 0;
		int index2 = 0;
		int index3 = 0;
		int lastDay = -1;
		ArrayList<TrendClass> trends = new ArrayList<TrendClass>();
		Calendar cal = Calendar.getInstance();
		Calendar cal1 = Calendar.getInstance();
		Calendar cal11 = Calendar.getInstance();
		Calendar cal2 = Calendar.getInstance();
		Calendar cal3 = Calendar.getInstance();
		QuoteShort.getCalendar(cal1, data.get(0));
		
		int accDay = 0;
		int totalDay = 0;
		int accGlobal = 0;
		int totalDays=0;
		int maxTrend = 0;
		int maxH = 0;
		ArrayList<Integer> hChangesArr = new ArrayList<Integer>();
		ArrayList<Integer> hHLDayArr = new ArrayList<Integer>();
		ArrayList<Integer> hChangesDayArr = new ArrayList<Integer>();
		for (int i=0;i<=23;i++){
			hChangesArr.add(0);
			hChangesDayArr.add(0);
			hHLDayArr.add(0);
		}
		int totalDays2=0;
		int high = -1;
		int low = -1;
		int modeTrade = 0;
		int entry = 0;
		int entrySL = 0;
		int entryIdx = 0;
		boolean enabledHalfClose = false;
		int maxLoss = 0;
		
		int wins = 0;
		int losses = 0;
		int winPips = 0;
		int lostPips = 0;
		for (int i=1;i<data.size()-10;i++){
			QuoteShort q = data.get(i);
			QuoteShort q1 = data.get(i-1);
			QuoteShort.getCalendar(cal, q);
			QuoteShort.getCalendar(cal11, q1);
			//System.out.println(q.toString());
			int h = cal.get(Calendar.HOUR_OF_DAY);
			int day = cal.get(Calendar.DAY_OF_YEAR);
			int y = cal.get(Calendar.YEAR);
			
			if (y<y1 || y>y2) continue;
			
			if (day!=lastDay){
				
				if (lastDay!=-1){
					if (reset)
					if (mode==1){
						int size = data.get(index2).getHigh5()-data.get(index1).getLow5();
						accDay+=size; 
						totalDay++;		
						
						int h0 = cal1.get(Calendar.HOUR_OF_DAY);
						hChangesDayArr.set(h0, 1);
					}else if (mode==-1){
						int size = data.get(index1).getHigh5()-data.get(index2).getLow5();
						accDay+=size;
						totalDay++;
						
						int h0 = cal1.get(Calendar.HOUR_OF_DAY);
						hChangesDayArr.set(h0, 1);
					}
					
					if (debug==1)						
					if (totalDay>0){
						System.out.println("[DAY ]"+DateUtils.datePrint(cal11)
						+" | "+totalDay+" "+PrintUtils.Print2dec(accDay*1.0/totalDay,false)
						+" "+maxTrend+" "+maxH
						);
					}else{
						System.out.println("[DAY ]"+DateUtils.datePrint(cal11)
						+" | "+totalDay
						);
					}
					
					if (debug==2)
					System.out.println("[DAY ]"+DateUtils.datePrint(cal11)
						//+" "+(hHLDayArr.get(9)+hHLDayArr.get(10)+hHLDayArr.get(11))
						+" "+(hChangesDayArr.get(15)+hChangesDayArr.get(16)+hChangesDayArr.get(17))
						);

					accGlobal+=accDay;
					totalDays+=totalDay;
				}
				
				for (int j=0;j<=23;j++){
					int tot = hChangesArr.get(j);
					hChangesArr.set(j, tot+hChangesDayArr.get(j));					
					hChangesDayArr.set(j,0);
					hHLDayArr.set(j, 0);
				}
				
				if (reset){
					mode = 0;
					index1 = i;
					index2 = i;
				}
				maxTrend=0;
				
				high = -1;
				low = -1;
				accDay = 0;
				totalDay = 0;
				lastDay = day;
				totalDays2++;
			}
			
			if (high==-1 || q.getHigh5()>=high){
				high = q.getHigh5();
				hHLDayArr.set(h, 1);
				if (debug==3)
				if (h==10){
					System.out.println("[DAY HIGH]"+DateUtils.datePrint(cal)
					+" | "+q.toString()
					);
				}
			}
			if (low==-1 || q.getLow5()<=low){
				low = q.getLow5();
				hHLDayArr.set(h, 1);
				if (debug==3)
				if (h==10){
					System.out.println("[DAY LOW]"+DateUtils.datePrint(cal)
					+" | "+q.toString()
					);
				}
			}
			
			int actualSizeH1 = q.getHigh5()-data.get(index1).getLow5();
			int actualSizeL1 = data.get(index1).getHigh5()-q.getLow5();
			int actualSizeH2 = q.getHigh5()-data.get(index2).getLow5();
			int actualSizeL2 = data.get(index2).getHigh5()-q.getLow5();
			
			double actualTrendIndex = 0;
			if (mode==0){
				if (actualSizeH1>=minSize){
					index2=i;
					mode=1;
					
					//trendsIndex.add(actualSizeH1*1.0/minSize);
					actualTrendIndex = (q.getClose5()-data.get(index1).getLow5())*1.0/minSize;
				}else if (actualSizeL1>=minSize){
					index2=i;
					mode=-1;
					
					//trendsIndex.add(-actualSizeL1*1.0/minSize);
					actualTrendIndex = (data.get(index1).getHigh5()-q.getClose5())*1.0/minSize;
				}else{
					//trendsIndex.add(0.0);
				}
			}else if (mode==1){
				if (actualSizeL2>=minSize){
					//guardar trends
					int size = data.get(index2).getHigh5()-data.get(index1).getLow5();
					int sizeClose = q.getClose5()-data.get(index1).getLow5();
					//if (h<=9)
					
					TrendClass tsize = new TrendClass();					
					tsize.setSize(size);
					tsize.setSizeClose(sizeClose);
					tsize.setMillisIndex1(cal1.getTimeInMillis());
					tsize.setMillisIndex2(cal.getTimeInMillis());
					QuoteShort.getCalendar(cal3, data.get(index3));
					tsize.setMillisOpen(cal3.getTimeInMillis());
					tsize.setMode(1);
					trends.add(tsize);
					
					accDay+=size; 
					totalDay++;
					int h0 = cal1.get(Calendar.HOUR_OF_DAY);
					hChangesDayArr.set(h0, 1);
															
					mode=-1;
					index1 = index2;
					index2 = i;
					index3 = i;//definición de trend
					QuoteShort.getCalendar(cal1, data.get(index1));
					
					actualTrendIndex = (data.get(index1).getHigh5()-q.getClose5())*1.0/minSize;
					
					if (debug==3)
						if (cal1.get(Calendar.HOUR_OF_DAY)==10){
							System.out.println("[TREND DOWN] "
							+" "+data.get(index1).getHigh5()
							+" "+q.getLow5()
							+" | "+q.toString()
							);
						}
					//trendsIndex.add((data.get(index2).getClose5()-data.get(index1).getClose5())*1.0/minSize);
				}else if (q.getHigh5()>=data.get(index2).getHigh5()){
					index2 = i;
					
					actualTrendIndex = (q.getClose5()-data.get(index1).getLow5())*1.0/minSize;
					//trendsIndex.add(actualSizeH1*1.0/minSize);
				}
			}else if (mode==-1){
				if (actualSizeH2>=minSize){
					//guardar trends
					int size = data.get(index1).getHigh5()-data.get(index2).getLow5();
					int sizeClose = data.get(index1).getHigh5()-q.getClose5();
					//if (h<=9)
					
					TrendClass tsize = new TrendClass();					
					tsize.setSize(-size);
					tsize.setSizeClose(-sizeClose);
					tsize.setMillisIndex1(cal1.getTimeInMillis());
					tsize.setMillisIndex2(cal.getTimeInMillis());
					QuoteShort.getCalendar(cal3, data.get(index3));
					tsize.setMillisOpen(cal3.getTimeInMillis());
					//tsize.getCal().setTimeInMillis(cal1.getTimeInMillis());
					tsize.setMode(-1);
					trends.add(tsize);
					
					accDay+=size; 
					totalDay++;
					int h0 = cal1.get(Calendar.HOUR_OF_DAY);
					hChangesDayArr.set(h0, 1);
					
					mode=1;
					index1 = index2;
					index2 = i;
					index3 = i;//definición de trend
					QuoteShort.getCalendar(cal1, data.get(index1));
					
					actualTrendIndex = (q.getClose5()-data.get(index1).getLow5())*1.0/minSize;
					
					if (debug==3)
						if (h==10){
							System.out.println("[TREND high] "
							+" "+cal1.get(Calendar.HOUR_OF_DAY)==10
							+" "+q.getHigh5()
							+" | "+q.toString()
							);
						}

					//trendsIndex.add((data.get(index2).getClose5()-data.get(index1).getClose5())*1.0/minSize);
				}else if (q.getLow5()<=data.get(index2).getLow5()){
					index2 = i;
					actualTrendIndex = (data.get(index1).getHigh5()-q.getClose5())*1.0/minSize;
					//trendsIndex.add(actualSizeL1*1.0/minSize);
				}
			}
			
			//modo trading
			QuoteShort qindex2 = data.get(index2);
			
			if (h>=h1 && h<=h2
					&& modeTrade==0
					){
				if (mode==1){//es arriba										
					int diffH = qindex2.getHigh5()-q.getClose5();
					
					if (q.getHigh5()-q.getLow5()>=100
							&& modeTrade==0){
						modeTrade = 1;
						entry = q.getClose5();
						entrySL = entry-200;
						entryIdx = i;
						maxLoss = 0;
						enabledHalfClose=false;
						//System.out.println("[NEW SHORT] "+q.getClose5()+" | "+q.toString());
					}
					
				}else if (mode==-1){//va hacia abajo
					int diffL = q.getClose5()-qindex2.getLow5();
					
					if (q.getHigh5()-q.getLow5()>=100
							&& modeTrade==0){
						modeTrade = -1;
						entry = q.getClose5();
						entrySL = entry+200;
						entryIdx = i;
						maxLoss = 0;
						enabledHalfClose=false;
						//System.out.println("[NEW SHORT] "+q.getClose5()+" | "+q.toString());
					}
				}
			}
			
			
			
			if (i>entryIdx){
				if (modeTrade==-1){	
					int profit = entry-q.getClose5();
					String str="";
					if (profit>=tp){
						str+=" WIN";
						modeTrade=0;
						maxLoss = 0;
						wins++;
						winPips +=tp;
						//System.out.println("[SHORT "+str+"] "+profit+" | "+entry+" | "+q.toString());
					}else{					
						if (q.getHigh5()>=entrySL){
							profit = entry-entrySL;
							//System.out.println("[SHORT LOSS] "+profit+" | "+entry+" | "+q.toString());
							modeTrade=0;
							losses++;
							lostPips += -profit;
						}
					}
					
				}else if (modeTrade==1){
					int profit = q.getClose5()-entry;
					String str="";
					if (profit>=tp){
						str+=" WIN";
						modeTrade=0;
						maxLoss = 0;
						wins++;
						winPips +=tp;
						//System.out.println("[SHORT "+str+"] "+profit+" | "+entry+" | "+q.toString());
					}else{					
						if (q.getLow5()<=entrySL){
							profit = entrySL-entry;
							//System.out.println("[SHORT LOSS] "+profit+" | "+entry+" | "+q.toString());
							modeTrade=0;
							losses++;
							lostPips += -profit;
						}
					}
				}
			}
			
			
		}
		
		
		String str="";
		for (int i=0;i<=23;i++){
			str+=" "+i+"= "
					+" "+PrintUtils.Print2dec(hChangesArr.get(i)*100.0/totalDays2,false);
		}

		int tot = wins+losses;
		if (printSumm)
		System.out.println(
				y1+" "+y1
				+" "+minSize
				+" "+tp
				+" "+minDiff
				+" || "
				//+" "+totalDays2
				//+" "+PrintUtils.Print(accGlobal*1.0/totalDays)
				//+" || "+str
				//+" || "
				+" "+tot
				+" "+PrintUtils.Print2dec(winPips*1.0/lostPips,false)
				);
		
		return trends;
	}
	
	public static ArrayList<TrendClass> doTest2(ArrayList<QuoteShort> data,
			int y1,int y2,
			int minSize,
			boolean reset,
			boolean printSumm,
			int debug
			){
		
		//ArrayList<Double> trendsIndex = new ArrayList<Double>();
		
		int mode = 0;
		int index1 = 0;
		int index2 = 0;
		int index3 = 0;
		int lastDay = -1;
		ArrayList<TrendClass> trends = new ArrayList<TrendClass>();
		Calendar cal = Calendar.getInstance();
		Calendar cal1 = Calendar.getInstance();
		Calendar cal11 = Calendar.getInstance();
		Calendar cal2 = Calendar.getInstance();
		Calendar cal3 = Calendar.getInstance();
		QuoteShort.getCalendar(cal1, data.get(0));
		
		int accDay = 0;
		int totalDay = 0;
		int accGlobal = 0;
		int totalDays=0;
		int maxTrend = 0;
		int maxH = 0;
		ArrayList<Integer> hChangesArr = new ArrayList<Integer>();
		ArrayList<Integer> hHLDayArr = new ArrayList<Integer>();
		ArrayList<Integer> hChangesDayArr = new ArrayList<Integer>();
		for (int i=0;i<=23;i++){
			hChangesArr.add(0);
			hChangesDayArr.add(0);
			hHLDayArr.add(0);
		}
		int totalDays2=0;
		int high = -1;
		int low = -1;
		for (int i=1;i<data.size();i++){
			QuoteShort q = data.get(i);
			QuoteShort q1 = data.get(i-1);
			QuoteShort.getCalendar(cal, q);
			QuoteShort.getCalendar(cal11, q1);
			//System.out.println(q.toString());
			int h = cal.get(Calendar.HOUR_OF_DAY);
			int day = cal.get(Calendar.DAY_OF_YEAR);
			int y = cal.get(Calendar.YEAR);
			
			if (y<y1 || y>y2) continue;
			
			if (day!=lastDay){
				
				if (lastDay!=-1){
					if (reset)
					if (mode==1){
						int size = data.get(index2).getHigh5()-data.get(index1).getLow5();
						accDay+=size; 
						totalDay++;		
						
						int h0 = cal1.get(Calendar.HOUR_OF_DAY);
						hChangesDayArr.set(h0, 1);
					}else if (mode==-1){
						int size = data.get(index1).getHigh5()-data.get(index2).getLow5();
						accDay+=size;
						totalDay++;
						
						int h0 = cal1.get(Calendar.HOUR_OF_DAY);
						hChangesDayArr.set(h0, 1);
					}
					
					if (debug==1)						
					if (totalDay>0){
						System.out.println("[DAY ]"+DateUtils.datePrint(cal11)
						+" | "+totalDay+" "+PrintUtils.Print2dec(accDay*1.0/totalDay,false)
						+" "+maxTrend+" "+maxH
						);
					}else{
						System.out.println("[DAY ]"+DateUtils.datePrint(cal11)
						+" | "+totalDay
						);
					}
					
					if (debug==2)
					System.out.println("[DAY ]"+DateUtils.datePrint(cal11)
						//+" "+(hHLDayArr.get(9)+hHLDayArr.get(10)+hHLDayArr.get(11))
						+" "+(hChangesDayArr.get(15)+hChangesDayArr.get(16)+hChangesDayArr.get(17))
						);

					accGlobal+=accDay;
					totalDays+=totalDay;
				}
				
				for (int j=0;j<=23;j++){
					int tot = hChangesArr.get(j);
					hChangesArr.set(j, tot+hChangesDayArr.get(j));					
					hChangesDayArr.set(j,0);
					hHLDayArr.set(j, 0);
				}
				
				if (reset){
					mode = 0;
					index1 = i;
					index2 = i;
				}
				maxTrend=0;
				
				high = -1;
				low = -1;
				accDay = 0;
				totalDay = 0;
				lastDay = day;
				totalDays2++;
			}
			
			if (high==-1 || q.getHigh5()>=high){
				high = q.getHigh5();
				hHLDayArr.set(h, 1);
				if (debug==3)
				if (h==10){
					System.out.println("[DAY HIGH]"+DateUtils.datePrint(cal)
					+" | "+q.toString()
					);
				}
			}
			if (low==-1 || q.getLow5()<=low){
				low = q.getLow5();
				hHLDayArr.set(h, 1);
				if (debug==3)
				if (h==10){
					System.out.println("[DAY LOW]"+DateUtils.datePrint(cal)
					+" | "+q.toString()
					);
				}
			}
			
			int actualSizeH1 = q.getHigh5()-data.get(index1).getLow5();
			int actualSizeL1 = data.get(index1).getHigh5()-q.getLow5();
			int actualSizeH2 = q.getHigh5()-data.get(index2).getLow5();
			int actualSizeL2 = data.get(index2).getHigh5()-q.getLow5();
			
			double actualTrendIndex = 0;
			if (mode==0){
				if (actualSizeH1>=minSize){
					index2=i;
					mode=1;
					
					//trendsIndex.add(actualSizeH1*1.0/minSize);
					actualTrendIndex = (q.getClose5()-data.get(index1).getLow5())*1.0/minSize;
				}else if (actualSizeL1>=minSize){
					index2=i;
					mode=-1;
					
					//trendsIndex.add(-actualSizeL1*1.0/minSize);
					actualTrendIndex = (data.get(index1).getHigh5()-q.getClose5())*1.0/minSize;
				}else{
					//trendsIndex.add(0.0);
				}
			}else if (mode==1){
				if (actualSizeL2>=minSize){
					//guardar trends
					int size = data.get(index2).getHigh5()-data.get(index1).getLow5();
					int sizeClose = q.getClose5()-data.get(index1).getLow5();
					//if (h<=9)
					
					TrendClass tsize = new TrendClass();					
					tsize.setSize(size);
					tsize.setSizeClose(sizeClose);
					tsize.setMillisIndex1(cal1.getTimeInMillis());
					tsize.setMillisIndex2(cal.getTimeInMillis());
					QuoteShort.getCalendar(cal3, data.get(index3));
					tsize.setMillisOpen(cal3.getTimeInMillis());
					tsize.setMode(1);
					trends.add(tsize);
					
					accDay+=size; 
					totalDay++;
					int h0 = cal1.get(Calendar.HOUR_OF_DAY);
					hChangesDayArr.set(h0, 1);
															
					mode=-1;
					index1 = index2;
					index2 = i;
					index3 = i;//definición de trend
					QuoteShort.getCalendar(cal1, data.get(index1));
					
					actualTrendIndex = (data.get(index1).getHigh5()-q.getClose5())*1.0/minSize;
					
					if (debug==3)
						if (cal1.get(Calendar.HOUR_OF_DAY)==10){
							System.out.println("[TREND DOWN] "
							+" "+data.get(index1).getHigh5()
							+" "+q.getLow5()
							+" | "+q.toString()
							);
						}
					//trendsIndex.add((data.get(index2).getClose5()-data.get(index1).getClose5())*1.0/minSize);
				}else if (q.getHigh5()>=data.get(index2).getHigh5()){
					index2 = i;
					
					actualTrendIndex = (q.getClose5()-data.get(index1).getLow5())*1.0/minSize;
					//trendsIndex.add(actualSizeH1*1.0/minSize);
				}
			}else if (mode==-1){
				if (actualSizeH2>=minSize){
					//guardar trends
					int size = data.get(index1).getHigh5()-data.get(index2).getLow5();
					int sizeClose = data.get(index1).getHigh5()-q.getClose5();
					//if (h<=9)
					
					TrendClass tsize = new TrendClass();					
					tsize.setSize(-size);
					tsize.setSizeClose(-sizeClose);
					tsize.setMillisIndex1(cal1.getTimeInMillis());
					tsize.setMillisIndex2(cal.getTimeInMillis());
					QuoteShort.getCalendar(cal3, data.get(index3));
					tsize.setMillisOpen(cal3.getTimeInMillis());
					//tsize.getCal().setTimeInMillis(cal1.getTimeInMillis());
					tsize.setMode(-1);
					trends.add(tsize);
					
					accDay+=size; 
					totalDay++;
					int h0 = cal1.get(Calendar.HOUR_OF_DAY);
					hChangesDayArr.set(h0, 1);
					
					mode=1;
					index1 = index2;
					index2 = i;
					index3 = i;//definición de trend
					QuoteShort.getCalendar(cal1, data.get(index1));
					
					actualTrendIndex = (q.getClose5()-data.get(index1).getLow5())*1.0/minSize;
					
					if (debug==3)
						if (h==10){
							System.out.println("[TREND high] "
							+" "+cal1.get(Calendar.HOUR_OF_DAY)==10
							+" "+q.getHigh5()
							+" | "+q.toString()
							);
						}

					//trendsIndex.add((data.get(index2).getClose5()-data.get(index1).getClose5())*1.0/minSize);
				}else if (q.getLow5()<=data.get(index2).getLow5()){
					index2 = i;
					actualTrendIndex = (data.get(index1).getHigh5()-q.getClose5())*1.0/minSize;
					//trendsIndex.add(actualSizeL1*1.0/minSize);
				}
			}
			
			
		}
		
		
		String str="";
		for (int i=0;i<=23;i++){
			str+=" "+i+"= "
					+" "+PrintUtils.Print2dec(hChangesArr.get(i)*100.0/totalDays2,false);
		}

		if (printSumm)
		System.out.println(
				y1+" "+y1
				+" "+minSize
				+" || "
				+" "+totalDays2
				+" "+PrintUtils.Print(accGlobal*1.0/totalDays)
				+" || "+str
				);
		
		return trends;
	}
	
	public static void doTest(ArrayList<QuoteShort> data,
			int y1,int y2,
			int minSize,
			boolean reset,
			int debug
			){
		
		//ArrayList<Double> trendsIndex = new ArrayList<Double>();
		
		int mode = 0;
		int index1 = 0;
		int index2 = 0;
		int index3 = 0;
		int lastDay = -1;
		ArrayList<TrendClass> trends = new ArrayList<TrendClass>();
		Calendar cal = Calendar.getInstance();
		Calendar cal1 = Calendar.getInstance();
		Calendar cal2 = Calendar.getInstance();
		Calendar cal3 = Calendar.getInstance();
		QuoteShort.getCalendar(cal1, data.get(0));
		
		int accDay = 0;
		int totalDay = 0;
		int accGlobal = 0;
		int totalDays=0;
		int maxTrend = 0;
		int maxH = 0;
		ArrayList<Integer> maxHArr = new ArrayList<Integer>();
		for (int i=0;i<=23;i++){
			maxHArr.add(0);
		}
		for (int i=1;i<data.size();i++){
			QuoteShort q = data.get(i);
			QuoteShort q1 = data.get(i-1);
			QuoteShort.getCalendar(cal, q);
			QuoteShort.getCalendar(cal1, q1);
			//System.out.println(q.toString());
			int h = cal.get(Calendar.HOUR_OF_DAY);
			int day = cal.get(Calendar.DAY_OF_YEAR);
			int y = cal.get(Calendar.YEAR);
			
			if (y<y1 || y>y2) continue;
			
			if (day!=lastDay){
				
				if (lastDay!=-1){
					if (reset)
					if (mode==1){
						int size = data.get(index2).getHigh5()-data.get(index1).getLow5();
						accDay+=size; 
						totalDay++;		
						if (size>=maxTrend){
							maxTrend = size;
							maxH = cal3.get(Calendar.HOUR_OF_DAY);
							int t = maxHArr.get(maxH);
							maxHArr.set(maxH, t+1);
						}
					}else if (mode==-1){
						int size = data.get(index1).getHigh5()-data.get(index2).getLow5();
						accDay+=size;
						totalDay++;
						if (size>=maxTrend){
							maxTrend = size;
							maxH = cal3.get(Calendar.HOUR_OF_DAY);
							int t = maxHArr.get(maxH);
							maxHArr.set(maxH, t+1);
						}
					}
					
					if (debug==1)
					if (totalDay>0){
						System.out.println("[DAY ]"+DateUtils.datePrint(cal1)
						+" | "+totalDay+" "+PrintUtils.Print2dec(accDay*1.0/totalDay,false)
						+" "+maxTrend+" "+maxH
						);
					}else{
						System.out.println("[DAY ]"+DateUtils.datePrint(cal1)
						+" | "+totalDay
						);
					}
					
					accGlobal+=accDay;
					totalDays+=totalDay;
				}
				
				if (reset){
					mode = 0;
					index1 = i;
					index2 = i;
				}
				maxTrend=0;
				
				accDay = 0;
				totalDay = 0;
				lastDay = day;
			}
			
			int actualSizeH1 = q.getHigh5()-data.get(index1).getLow5();
			int actualSizeL1 = data.get(index1).getHigh5()-q.getLow5();
			int actualSizeH2 = q.getHigh5()-data.get(index2).getLow5();
			int actualSizeL2 = data.get(index2).getHigh5()-q.getLow5();
			
			double actualTrendIndex = 0;
			if (mode==0){
				if (actualSizeH1>=minSize){
					index2=i;
					mode=1;
					
					//trendsIndex.add(actualSizeH1*1.0/minSize);
					actualTrendIndex = (q.getClose5()-data.get(index1).getLow5())*1.0/minSize;
				}else if (actualSizeL1>=minSize){
					index2=i;
					mode=-1;
					
					//trendsIndex.add(-actualSizeL1*1.0/minSize);
					actualTrendIndex = (data.get(index1).getHigh5()-q.getClose5())*1.0/minSize;
				}else{
					//trendsIndex.add(0.0);
				}
			}else if (mode==1){
				if (actualSizeL2>=minSize){
					//guardar trends
					int size = data.get(index2).getHigh5()-data.get(index1).getLow5();
					int sizeClose = q.getClose5()-data.get(index1).getLow5();
					//if (h<=9)
					
					TrendClass tsize = new TrendClass();					
					tsize.setSize(size);
					tsize.setSizeClose(sizeClose);
					tsize.setMillisIndex1(cal1.getTimeInMillis());
					tsize.setMillisIndex2(cal.getTimeInMillis());
					QuoteShort.getCalendar(cal3, data.get(index3));
					tsize.setMillisOpen(cal3.getTimeInMillis());
					tsize.setMode(1);
					trends.add(tsize);
					
					accDay+=size; 
					totalDay++;
					if (size>=maxTrend){
						maxTrend = size;
						maxH = cal3.get(Calendar.HOUR_OF_DAY);
						int t = maxHArr.get(maxH);
						maxHArr.set(maxH, t+1);
					}
					
					
					mode=-1;
					index1 = index2;
					index2 = i;
					index3 = i;//definición de trend
					QuoteShort.getCalendar(cal1, data.get(index1));
					
					actualTrendIndex = (data.get(index1).getHigh5()-q.getClose5())*1.0/minSize;
					//trendsIndex.add((data.get(index2).getClose5()-data.get(index1).getClose5())*1.0/minSize);
				}else if (q.getHigh5()>=data.get(index2).getHigh5()){
					index2 = i;
					
					actualTrendIndex = (q.getClose5()-data.get(index1).getLow5())*1.0/minSize;
					//trendsIndex.add(actualSizeH1*1.0/minSize);
				}
			}else if (mode==-1){
				if (actualSizeH2>=minSize){
					//guardar trends
					int size = data.get(index1).getHigh5()-data.get(index2).getLow5();
					int sizeClose = data.get(index1).getHigh5()-q.getClose5();
					//if (h<=9)
					
					TrendClass tsize = new TrendClass();					
					tsize.setSize(-size);
					tsize.setSizeClose(-sizeClose);
					tsize.setMillisIndex1(cal1.getTimeInMillis());
					tsize.setMillisIndex2(cal.getTimeInMillis());
					QuoteShort.getCalendar(cal3, data.get(index3));
					tsize.setMillisOpen(cal3.getTimeInMillis());
					//tsize.getCal().setTimeInMillis(cal1.getTimeInMillis());
					tsize.setMode(-1);
					trends.add(tsize);
					
					accDay+=size; 
					totalDay++;
					if (size>=maxTrend){
						maxTrend = size;
						maxH = cal3.get(Calendar.HOUR_OF_DAY);
						int t = maxHArr.get(maxH);
						maxHArr.set(maxH, t+1);
					}
					
					mode=1;
					index1 = index2;
					index2 = i;
					index3 = i;//definición de trend
					QuoteShort.getCalendar(cal1, data.get(index1));
					
					actualTrendIndex = (q.getClose5()-data.get(index1).getLow5())*1.0/minSize;
					
					//trendsIndex.add((data.get(index2).getClose5()-data.get(index1).getClose5())*1.0/minSize);
				}else if (q.getLow5()<=data.get(index2).getLow5()){
					index2 = i;
					actualTrendIndex = (data.get(index1).getHigh5()-q.getClose5())*1.0/minSize;
					//trendsIndex.add(actualSizeL1*1.0/minSize);
				}
			}
		}
		
		int tot = 0;
		for (int i=0;i<=23;i++){
			tot += maxHArr.get(i);
		}
		
		String str="";
		for (int i=0;i<=23;i++){
			str+=" "+i+"= "
					+" "+PrintUtils.Print2dec(maxHArr.get(i)*100.0/tot,false);
		}

		System.out.println(
				y1+" "+y1
				+" "+minSize
				+" || "
				+" "+totalDays
				+" "+PrintUtils.Print(accGlobal*1.0/totalDays)
				+" || "+str
				);
	}
	
	public static void dotest3(ArrayList<TrendClass> trends,
			int h1,int h2,
			int minSize,int tp,int debug){
		
		Calendar cal = Calendar.getInstance();
		Calendar cal1 = Calendar.getInstance();
		int lastDay = -1;
		int acc09 = 0;
		int acc10 = 0;
		int total09=0;
		int total10=0;
		
		
		int acc09g = 0;
		int acc10g = 0;
		int total09g=0;
		int total10g=0;
		
		int actualCount=0;
		
		int count10 = 0;
		int count15=0;
		int count5=0;
		int count4=0;
		int count3=0;
		int count2=0;
		int count1=0;
		int countRachas = 0;
		int winPips = 0;
		int lostPips = 0;
		int wins = 0;
		int losses = 0;
		int accLoss = 0;
		
		int accLossGlobal = 0;
		int lossesGlobal = 0;
		for (int i=0;i<trends.size();i++){
			TrendClass t = trends.get(i);
			cal.setTimeInMillis(t.getMillisOpen());
			
			int day = cal.get(Calendar.DAY_OF_YEAR);
			if (day!=lastDay){
				
				if (lastDay!=-1){
					/*System.out.println(
							PrintUtils.Print2dec(acc09*0.1/total09, false)
							+" || "
							+" "+PrintUtils.Print2dec(acc10*0.1/total10, false)
							);*/


					acc09g +=acc09;
					total09g+=total09;
					
					acc10g +=acc10;					
					total10g+=total10;
				}
				
				acc09 = 0;
				acc10 = 0;
				total09=0;
				total10=0;
				lastDay = day;
			}
			
			int h= cal.get(Calendar.HOUR_OF_DAY);
			if (h>=0 && h<=7){
				acc09 +=Math.abs(t.getSize());
				total09++;
			}
			if (h>=10 && h<=14){
				acc10 +=Math.abs(t.getSize());
				total10++;
			}
			
			int sizeAbs = Math.abs(t.getSize());
			
			if (sizeAbs>=2*minSize){
				
				if (actualCount>=1) count1++;
				if (actualCount>=2) count2++;
				if (actualCount>=3) count3++;
				if (actualCount>=4) count4++;
				if (actualCount>=5) count5++;
				if (actualCount>=10) count10++;
				if (actualCount>=15) count15++;
				
				if (actualCount>0){
					accLossGlobal += accLoss;
					lossesGlobal+=actualCount;
				}
				if (debug==2){
					System.out.println(actualCount+" || "+accLoss);
				}
				actualCount=0;
				countRachas++;
				accLoss = 0;
			}else{
				actualCount++;	
				accLoss += 2*minSize-sizeAbs;
			}
			
			cal1.setTimeInMillis(t.getMillisOpen());
			int hopen = cal1.get(Calendar.HOUR_OF_DAY);
			
			if (hopen>=h1 && hopen<=h2){
				int profit = sizeAbs-400;
				
				if (sizeAbs>=tp){
					winPips+=(tp-minSize);
					wins++;
				}else{
					if (profit>=0){
						wins++;
						winPips+=profit;
					}else{
						lostPips+=-profit;
						losses++;
					}
				}
			}
			
			if (debug==1)
			System.out.println("l="+actualCount
					+" "+PrintUtils.Print2dec(accLoss*0.1, false)
					+" ("+(2*minSize-sizeAbs)+")"
					+" || "
					+" "+lossesGlobal
					+" "+PrintUtils.Print2dec(accLossGlobal*0.1/lossesGlobal, false)
					+" || "+PrintUtils.Print2dec(Math.abs(t.getSize()*0.1), false));
		}
		
		int total = wins+losses;
		double winPer = wins*100.0/total;
		double pf = winPips*1.0/lostPips;
		
		System.out.println("[resumen] "+
				PrintUtils.Print2dec(acc09g*0.1/total09g, false)
				+" || "
				+" "+PrintUtils.Print2dec(acc10g*0.1/total10g, false)
				+" || "
				+" "+countRachas
				+" "+count1
				+" "+count2
				+" "+count3
				+" "+count4
				+" "+count5
				+" "+count10
				+" "+count15
				+" || "+PrintUtils.Print2dec(winPer, false)
				+" "+PrintUtils.Print2dec(pf, false)
				+" || "
				+" "+lossesGlobal
				+" "+PrintUtils.Print2dec(accLossGlobal*0.1/lossesGlobal, false)
				);
	}

	public static void main(String[] args) throws Exception {
		String path0 ="C:\\fxdata\\";
		//String path0 = "C:\\Users\\David\\Documents\\fxdata\\";

		String pathEURUSD = path0+"EURUSD_5 Mins_Bid_2014.01.01_2019.11.05.csv";
		String pathNews = path0+"News.csv";
		
		ArrayList<String> paths = new ArrayList<String>();
		paths.add(pathEURUSD);
		//paths.add(pathEURAUD);paths.add(pathNZDUSD);
		
		int total = 0;
		ArrayList<Double> pfs = new ArrayList<Double>();
		int limit = paths.size()-1;
		limit = 0;
		String provider ="";
		Sizeof.runGC ();
		ArrayList<QuoteShort> dataI 		= null;
		ArrayList<QuoteShort> dataS 		= null;
		ArrayList<FFNewsClass> news = new ArrayList<FFNewsClass>();	
		//FFNewsClass.readNews(pathNews,news,0);
		for (int i = 0;i<=limit;i++){
			String path = paths.get(i);			
			dataI 		= DAO.retrieveDataShort5m(path, DataProvider.DUKASCOPY_FOREX4);									
			//dataS 		= TestLines.calculateCalendarAdjustedS(dataI);
			TestLines.calculateCalendarAdjustedSinside(dataI);
			//TradingUtils.cleanWeekendDataSinside(dataI); 	
			dataS = TradingUtils.cleanWeekendDataS(dataI);  
			ArrayList<QuoteShort> data = null;
			ArrayList<QuoteShort> dataNoise = null;
			data = dataS;
			
			int y1=2012;
			int y2=2017;
			int minSize=200;
			
			
			ArrayList<TrendClass> arr = new ArrayList<TrendClass>();
			ArrayList<Double> arrd = new ArrayList<Double>();
			Calendar cal = Calendar.getInstance();
			for (minSize=200;minSize<=200;minSize+=100){
				for (int testSize=200;testSize<=1200;testSize+=100) {
					//ArrayList<TrendClass> arr = TradingUtils.calculateTrends(data, minSize);
					int cases = 0;
					int avg = 0;
					arr = TradingUtils.calculateTrendsHL(data, minSize,arrd);
					for (int j=0;j<arr.size()-2;j++) {
						TrendClass t = arr.get(j);
						TrendClass t1 = arr.get(j+1);
						TrendClass t2 = arr.get(j+2);
						cal.setTimeInMillis(t.getMillisOpen());
						int hopen = cal.get(Calendar.HOUR_OF_DAY);
						//System.out.println(t.getSize());
						if (hopen>=0) {
							int absSize = Math.abs(t.getSize());
							int absSize1 = Math.abs(t1.getSize());
							if (absSize>=testSize) {
								cases++;
								//
								avg+= t1.getSize()+t2.getSize();
							}
						}
					}
					System.out.println(testSize+" "+cases+" "+PrintUtils.Print2dec(avg*1.0/cases, false));
				}
			}			
			
		}

	}

}
