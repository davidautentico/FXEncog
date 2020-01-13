package drosa.utils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

import drosa.experimental.PositionShort;
import drosa.experimental.TradeResultSimple;
import drosa.experimental.CoreStrategies.PositionCore;
import drosa.experimental.ticksStudy.Tick;
import drosa.experimental.zznbrum.TrendClass;
import drosa.experimental.zznbrum.TrendInfo;
import drosa.finances.Quote;
import drosa.finances.QuoteBidAsk;
import drosa.finances.QuoteShort;
import drosa.phil.LineType;
import drosa.phil.PhilDay;
import drosa.phil.PhilDayShort;
import drosa.phil.PhilLine;
import drosa.phil.PhilLineShort;
import drosa.phil.PriceTestResult;
import drosa.phil.TestLines;
import drosa.strategies.PositionStatus;
import drosa.strategies.auxiliar.PositionType;

public class TradingUtils {
	
	
	public static void gerMovingAverageHash(
			HashMap<Integer,Integer> smaHash,
			ArrayList<QuoteShort> data,
			int n
			){
		
		Calendar cal = Calendar.getInstance();
		ArrayList<Integer> closeValues = new ArrayList<Integer>();
		for (int i=0;i<data.size();i++){
			QuoteShort q = data.get(i);
			QuoteShort.getCalendar(cal, q);
			int y = cal.get(Calendar.YEAR);
			int m = cal.get(Calendar.MONTH);
			int d = cal.get(Calendar.DAY_OF_MONTH);
			int h = cal.get(Calendar.HOUR_OF_DAY);
			int min = cal.get(Calendar.MINUTE);
			
			//int valueDate = (y-2000)*100000000+m*
			
			closeValues.add(data.get(i).getClose5());
			
			int end = closeValues.size()-1;
			int begin = end-n+1;
			if (begin<=0) begin = 0;
			int avg = (int) MathUtils.average(closeValues, begin, end);
		}
	}
	/**
	 * Estudiado a nivel de tick con dukascopy
	 * @param year
	 * @param h
	 * @return
	 */
	public static int getTransactionCosts(int year,int h,int mode){
		int spread = 20;
		
		int commisions = 6;//darwinex
		
		if (year==2019){
			if (h==0) spread = 16;
			if (h==1) spread = 6;
			if (h>=2 && h<=8) spread = 4;
			if (h>=9 && h<=22) spread = 3;
			if (h==23) spread = 6;
		}else if (year==2018){
			if (h==0) spread = 10;
			if (h==1) spread = 5;
			if (h>=2 && h<=8) spread = 4;
			if (h>=9 && h<=22) spread = 4;
			if (h==23) spread = 11;
		}else if (year==2017){
			if (h==0) spread = 10;
			if (h==1) spread = 7;
			if (h>=2 && h<=8) spread = 3;
			if (h>=9 && h<=22) spread = 3;
			if (h==23) spread = 5;
		}else if (year==2016){
			if (h==0) spread = 12;
			if (h==1) spread = 7;
			if (h>=2 && h<=8) spread = 3;
			if (h>=9 && h<=22) spread = 2;
			if (h==23) spread = 5;
		}else if (year==2015){
			if (h==0) spread = 11;
			if (h==1) spread = 6;
			if (h>=2 && h<=8) spread = 3;
			if (h>=9 && h<=22) spread = 3;
			if (h==23) spread = 4;
		}else if (year==2014){
			if (h==0) spread = 6;
			if (h==1) spread = 4;
			if (h>=2 && h<=8) spread = 3;
			if (h>=9 && h<=22) spread = 2;
			if (h==23) spread = 3;
		}else if (year==2013){
			if (h==0) spread = 14;
			if (h==1) spread = 8;
			if (h>=2 && h<=8) spread = 6;
			if (h>=9 && h<=22) spread = 4;
			if (h==23) spread = 7;
		}else if (year==2012){
			if (h==0) spread = 15;
			if (h==1) spread = 11;
			if (h>=2 && h<=8) spread = 9;
			if (h>=9 && h<=22) spread = 8;
			if (h==23) spread = 10;
		}else if (year==2011){
			if (h==0) spread = 17;
			if (h==1) spread = 14;
			if (h>=2 && h<=8) spread = 11;
			if (h>=9 && h<=22) spread = 9;
			if (h==23) spread = 11;
		}else if (year==2010){
			if (h==0) spread = 12;
			if (h==1) spread = 12;
			if (h>=2 && h<=5) spread = 11;
			if (h>=6 && h<=9) spread = 10;
			if (h>=10 && h<=19) spread = 9;
			if (h>=20 && h<=22) spread = 10;
			if (h==23) spread = 11;
		}else if (year==2009){
			if (h==0) spread = 17;
			if (h==1) spread = 16;
			if (h>=2 && h<=8) spread = 13;
			if (h>=9 && h<=22) spread = 10;
			if (h==23) spread = 11;
		}
		
		if (mode==2) return spread;
		if (mode==3) return commisions;
		
		return spread+commisions;
	}
	
	public static int getTransactionCosts(
			HashMap<Integer,ArrayList<Double>> spreads,
			int year,int h,int mode
	){
		int spread = 20;
		
		int commisions = 6;//darwinex
		
		if (spreads!=null && spreads.containsKey(year)){
			spread = (int) Math.ceil(spreads.get(year).get(h)*10.0);
		}
				
		if (mode==2) return spread;
		if (mode==3) return commisions;
		
		return spread+commisions;
	}
	
	public static int getTransactionCostsMinutes(
			HashMap<Integer,ArrayList<Double>> spreads,
			int year,int hmin,int mode
	){
		int spread = 20;
		
		int commisions = 6;//darwinex
		
		if (spreads.containsKey(year)){
			spread = (int) Math.ceil(spreads.get(year).get(hmin)*10.0);
		}
				
		if (mode==2) return spread;
		if (mode==3) return commisions;
		
		return spread+commisions;
	}
	
	public static int calculateMiniLots(double balance,int sl,double risk){
		int miniLots = 0;
		
		//1 miniLot = 0.10$
		double totalRisk$ = balance*risk/100.0;
		double miniPip$ = totalRisk$/sl;
		double pip$ = 10*miniPip$;
		
		miniLots = (int) (pip$/0.10); 
		
		return miniLots;
	}
	
	public static void getMaxMinMoves(ArrayList<QuoteShort> data,
			QuoteShort qm,int begin,int end){
		
		int begin2 = begin;
		int end2 = end;
		
		if (begin<=0) begin2 = 0;
		if (end>=data.size()-1) end2=data.size()-1;
		
		int maxLong = 0;
		int maxShort = 0;
		for (int i=begin2;i<=end2;i++){
			QuoteShort q = data.get(i);
			for (int j=i+1;j<=end2;j++){
				QuoteShort qj = data.get(j);
				int sizeS = q.getHigh5()-qj.getLow5();
				if (sizeS>=maxShort) maxShort = sizeS;
				int sizeL = qj.getHigh5()-q.getLow5();
				if (sizeL>=maxLong) maxLong = sizeL;
			}
		}
		qm.setHigh5(maxLong);
		qm.setLow5(maxShort);
	}
	
	public static int getMaxMinIndex(ArrayList<QuoteShort> data,int begin,int end,boolean isHigh){
		
		if (begin<=0) begin = 0;
		if (end>=data.size()-1) end = data.size()-1;
		int res = begin;
		int value = -1;
		for (int i=begin;i<=end;i++){
			QuoteShort q = data.get(i);
			if (isHigh){
				if (value==-1 || q.getHigh5()>=value){
					value = q.getHigh5();
					res = i;
				}
			}else{
				if (value==-1 || q.getLow5()<=value){
					value = q.getLow5();
					res = i;
				}
			}
		}
		
		return res;
	}
	
	public static ArrayList<Integer> calculateFractals(ArrayList<QuoteShort> data,int n,boolean isHigh){
		
		int thr = n/2;
		ArrayList<Integer> fractals = new ArrayList<Integer>();
		int lastFractal = -1;
		Calendar cal = Calendar.getInstance();
		for (int i=0;i<data.size();i++){
			QuoteShort q = data.get(i);
			QuoteShort.getCalendar(cal, q);
			int index1 = -1;
			int index2 = -1;
			if (isHigh){
				index1 = TradingUtils.getMaxMinIndex(data, i-thr, i, true);
				index2 = TradingUtils.getMaxMinIndex(data, i, i+thr, true);
				//System.out.println(q.toString()+" || "+i+" "+index1+" "+index2);
			}else{
				index1 = TradingUtils.getMaxMinIndex(data,  i-thr, i, false);
				index2 = TradingUtils.getMaxMinIndex(data, i, i+thr, false);
			}
			if (index1==index2 && index1==i){
				//System.out.println(index1+" "+index2);
				lastFractal = i;
			}
			fractals.add(lastFractal);
		}
		
		return fractals;
	}
	
	public static ArrayList<Integer> calculateFractals(ArrayList<QuoteShort> data,int n){
		
		int thr = n/2;
		ArrayList<Integer> fractals = new ArrayList<Integer>();
		int lastFractal = -1;
		Calendar cal = Calendar.getInstance();
		Calendar calm = Calendar.getInstance();
		QuoteShort qm = new QuoteShort();
		QuoteShort qm1 = new QuoteShort();
		QuoteShort qm2 = new QuoteShort();
		for (int i=0;i<data.size();i++){
			QuoteShort q = data.get(i);
			QuoteShort.getCalendar(cal, q);
			
			lastFractal = 0;
			
			if ((i-n)>=0 && (i+n)<=data.size()-1){
				TradingUtils.getMaxMinShort(data, qm1, cal, i-n, i-1);
				TradingUtils.getMaxMinShort(data, qm2, cal, i+1, i+n);				
				//for up
				if (q.getHigh5()>qm1.getHigh5() && q.getHigh5()>qm2.getHigh5()){
					lastFractal = 1;
				}else if (q.getLow5()<qm1.getLow5() && q.getLow5()<qm2.getLow5()){
					lastFractal = -1;
				} 
			}			
			fractals.add(lastFractal);
		}
		
		return fractals;
	}
	
	public static ArrayList<QuoteShort> calculateBoxesH(ArrayList<QuoteShort> data,int begin,int end,int period){
	
		int boxes = 24/period; //numero de cajas por dia
		ArrayList<QuoteShort>  dailyBoxes = new ArrayList<QuoteShort> ();
		for (int i=0;i<boxes;i++){
			QuoteShort q = new QuoteShort();
			q.init(-1);
			dailyBoxes.add(q);
		}
		ArrayList<QuoteShort>  result = new ArrayList<QuoteShort> ();
		
		if (end>data.size()-1) end = data.size()-1;
		int lastDay = -1;
		Calendar cal = Calendar.getInstance();
		for (int i=begin;i<=end;i++){
			QuoteShort q = data.get(i);
			QuoteShort.getCalendar(cal, q);
			int h = cal.get(Calendar.HOUR_OF_DAY);
			int day = cal.get(Calendar.DAY_OF_YEAR);
			//nuevo dia, se reinician los boxes y se guardan en resultados
			if (day!=lastDay){
				if (lastDay!=-1){
					for (int b=0;b<boxes;b++){
						QuoteShort qnew = new QuoteShort();
						qnew.copy(dailyBoxes.get(b));
						result.add(qnew);
					}
				}
				//reiniciamos boxes
				dailyBoxes.clear();
				for (int b=0;b<boxes;b++){
					QuoteShort qb = new QuoteShort();
					qb.init(-1);
					dailyBoxes.add(qb);
				}
				lastDay = day;
			}
			
			//actualizacion del box correspondiente
			int box = h/period; //me da el box al que pertenece
			QuoteShort qbox = dailyBoxes.get(box);			
			if (qbox.getOpen5()==-1){
				qbox.copy(q);
				System.out.println("qbox inicializado: "+qbox.toString()+" || "+box);
			}
			if (qbox.getHigh5()==-1 || q.getHigh5()>=qbox.getHigh5()) qbox.setHigh5(q.getHigh5());
			if (qbox.getLow5()==-1 || q.getLow5()<=qbox.getLow5()) qbox.setLow5(q.getLow5());
			qbox.setClose5(q.getClose5());
		}
		
		
		return result;
	}
	

	
	public static ArrayList<Quote> calculateMaxMinByBar(ArrayList<Quote> data,int lookBack){
		ArrayList<Quote> maxMin = new ArrayList<Quote>();
		
		Calendar cal = Calendar.getInstance();
		double minActual = 9999;
		double maxActual = 0;
		int lastDay = -1;
		Quote qNew = null;
		for (int i = 0;i<data.size();i++){
			Quote q = data.get(i);
			cal.setTime(q.getDate());
			int day = cal.get(Calendar.DAY_OF_WEEK);
			int h = cal.get(Calendar.HOUR_OF_DAY);
			Quote q0 = TradingUtils.getMaxMin(data,i-lookBack,i-1);
			
			//inicializamos cuota
			qNew = new Quote();
			qNew.getDate().setTime(q.getDate().getTime());
			qNew.setIndex(i);
			qNew.setExtra(0);
			maxMin.add(qNew);//lo metemos
			
			if (i>0)
			if (TradingUtils.getPipsDiff(q.getHigh(),q0.getHigh())>=1){
				qNew.setExtra(1);
			}else if (TradingUtils.getPipsDiff(q0.getLow(),q.getLow())>=1){
				qNew.setExtra(-1);
			}
		}
		
		return maxMin;
	}
	
	public static String generateFileName(String suffix,String symbol,int tp,int sl,int begin,int end){
		String fileName =suffix+symbol+"_"+String.valueOf((int)tp)+"_"+String.valueOf((int)sl)
		+"_"+String.valueOf(begin)+"_"+String.valueOf(end)+".csv";
		
		if (begin<0){
			fileName =suffix+symbol+"_"+String.valueOf((int)tp)+"_"+String.valueOf((int)sl)
					+".csv";
			 
		}
		return fileName;
	}
	
	
	public static boolean checkTradeFileConsistency(ArrayList<TradeResultSimple> res1,ArrayList<TradeResultSimple> res2){
		System.out.println("sizes 1 y 2: "+res1.size()+" "+res2.size());
		if (res1.size()!=res2.size()) return false;
		
		for (int i=0;i<res1.size();i++){
			TradeResultSimple t1 = res1.get(i);
			TradeResultSimple t2 = res2.get(i);
			
			if (t2.getBuyCloseCal().getTimeInMillis()<t1.getBuyCloseCal().getTimeInMillis() 
					&& !DateUtils.isDateTimeEqual(t2.getBuyCloseCal(), t1.getBuyCloseCal())){
				System.out.println(t1.toString()+" << "+t2.toString());
				return false;
			}
			if (t2.getSellCloseCal().getTimeInMillis()<t1.getSellCloseCal().getTimeInMillis()
					&& !DateUtils.isDateTimeEqual(t2.getSellCloseCal(), t1.getSellCloseCal())){
				System.out.println(t1.toString()+" << "+t2.toString());
				return false;
			}
		}
		
		return true;
	}
	
	
	public static void mergeFiles(File[] files, File mergedFile) {
		 
		FileWriter fstream = null;
		BufferedWriter out = null;
		try {
			fstream = new FileWriter(mergedFile, true);
			 out = new BufferedWriter(fstream);
		} catch (IOException e1) {
			e1.printStackTrace();
		}
 
		for (File f : files) {
			System.out.println("merging: " + f.getName());
			FileInputStream fis;
			try {
				fis = new FileInputStream(f);
				BufferedReader in = new BufferedReader(new InputStreamReader(fis));
 
				String aLine;
				while ((aLine = in.readLine()) != null) {
					out.write(aLine);
					out.newLine();
				}
 
				in.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
 
		try {
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
 
	}
	
	

	
	public static boolean checkConsistency(ArrayList<Quote> data){
		
		Calendar cal = Calendar.getInstance();
		for (int i = 1;i<data.size();i++){
			Quote q1 = data.get(i-1);
			Quote q2 = data.get(i);
			cal.setTimeInMillis(q2.getDate().getTime());
			//if (cal.get(Calendar.YEAR)==2009)
				//System.out.println("[checkConsistency] "+PrintUtils.Print(q1)+" "+PrintUtils.Print(q2));
			if (q2.getDate().getTime()<=q1.getDate().getTime()){
				System.out.println("[checkConsistency] ERROR "+PrintUtils.Print(q1)+" "+PrintUtils.Print(q2));
				return false;
			}
		}
		
		return true;
	}
	
	public static double checkConsistencyHoles(ArrayList<Quote> data,Calendar from,Calendar to,int h1,int h2,int thr){
		
		int total = 0;
		double avg = 0;
		int totalHoles = 0;
		Calendar cal = Calendar.getInstance();
		for (int i = 1;i<data.size();i++){
			Quote q1 = data.get(i-1);
			Quote q2 = data.get(i);
			cal.setTimeInMillis(q2.getDate().getTime());
			int h = cal.get(Calendar.HOUR_OF_DAY);
			int dayWeek = cal.get(Calendar.DAY_OF_WEEK);
			if (cal.getTimeInMillis()<from.getTimeInMillis() || cal.getTimeInMillis()>to.getTimeInMillis()){
				continue;
			}
			
			if (h==0 && dayWeek==Calendar.MONDAY) continue;
			
			if (h<h1 || h>h2) continue;
			int diffPips = Math.abs(TradingUtils.getPipsDiff(q2.getOpen(), q1.getClose()));
			avg+=diffPips;
			total++;
			if (diffPips>=thr){
				totalHoles++;
				System.out.println("[checkConsistencyHoles] hole "
						
						+"totalHoles= "+totalHoles+" diffPips= "+diffPips
						+" rate= "+PrintUtils.Print(totalHoles*100.0/total)
						+" "+PrintUtils.Print(q2)+" "+PrintUtils.Print(q1)
						);
				
			}
		}
		
		return avg/total;
	}
	
	public static String getFileLines(String symbol,int actualM, int actualY) {
		// TODO Auto-generated method stub
		String fileLines = symbol+"_";
		
		if (actualM==Calendar.JANUARY){
			fileLines+="01";
		}
		if (actualM==Calendar.FEBRUARY){
			fileLines+="02";
		}
		if (actualM==Calendar.MARCH){
			fileLines+="03";
		}
		if (actualM==Calendar.APRIL){
			fileLines+="04";
		}
		if (actualM==Calendar.MAY){
			fileLines+="05";
		}
		if (actualM==Calendar.JUNE){
			fileLines+="06";
		}
		if (actualM==Calendar.JULY){
			fileLines+="07";
		}
		if (actualM==Calendar.AUGUST){
			fileLines+="08";
		}
		if (actualM==Calendar.SEPTEMBER){
			fileLines+="09";
		}
		if (actualM==Calendar.OCTOBER){
			fileLines+="10";
		}
		if (actualM==Calendar.NOVEMBER){
			fileLines+="11";
		}
		if (actualM==Calendar.DECEMBER){
			fileLines+="12";
		}
		
		fileLines+="_"+String.valueOf(actualY)+"_LINES.csv";
		
		return fileLines;
	}

	public static String getFileData(String symbol,int actualM, int actualY) {
		// TODO Auto-generated method stub
		String fileLines = symbol+"_";
		
		if (actualM==Calendar.JANUARY){
			fileLines+="01";
		}
		if (actualM==Calendar.FEBRUARY){
			fileLines+="02";
		}
		if (actualM==Calendar.MARCH){
			fileLines+="03";
		}
		if (actualM==Calendar.APRIL){
			fileLines+="04";
		}
		if (actualM==Calendar.MAY){
			fileLines+="05";
		}
		if (actualM==Calendar.JUNE){
			fileLines+="06";
		}
		if (actualM==Calendar.JULY){
			fileLines+="07";
		}
		if (actualM==Calendar.AUGUST){
			fileLines+="08";
		}
		if (actualM==Calendar.SEPTEMBER){
			fileLines+="09";
		}
		if (actualM==Calendar.OCTOBER){
			fileLines+="10";
		}
		if (actualM==Calendar.NOVEMBER){
			fileLines+="11";
		}
		if (actualM==Calendar.DECEMBER){
			fileLines+="12";
		}
		
		fileLines+="_"+String.valueOf(actualY)+"_1s_data_trim.csv";
		
		return fileLines;
	}
	
	public static String getFileTmaDiff(String symbol,int actualM, int actualY) {
		// TODO Auto-generated method stub
		String fileLines = symbol+"_";
		
		if (actualM==Calendar.JANUARY){
			fileLines+="01";
		}
		if (actualM==Calendar.FEBRUARY){
			fileLines+="02";
		}
		if (actualM==Calendar.MARCH){
			fileLines+="03";
		}
		if (actualM==Calendar.APRIL){
			fileLines+="04";
		}
		if (actualM==Calendar.MAY){
			fileLines+="05";
		}
		if (actualM==Calendar.JUNE){
			fileLines+="06";
		}
		if (actualM==Calendar.JULY){
			fileLines+="07";
		}
		if (actualM==Calendar.AUGUST){
			fileLines+="08";
		}
		if (actualM==Calendar.SEPTEMBER){
			fileLines+="09";
		}
		if (actualM==Calendar.OCTOBER){
			fileLines+="10";
		}
		if (actualM==Calendar.NOVEMBER){
			fileLines+="11";
		}
		if (actualM==Calendar.DECEMBER){
			fileLines+="12";
		}
		
		fileLines+="_"+String.valueOf(actualY)+"_tma5mDiff.csv";
		
		return fileLines;
	}
	
	public static int getDayIndex(ArrayList<Quote> dataSource, Calendar actualCal, int index){
        int daySearch = actualCal.get(Calendar.DAY_OF_MONTH);
        Calendar sourceCal = Calendar.getInstance();
        for (int i=index;i<dataSource.size();i++){
            Quote q = dataSource.get(i);
            sourceCal.setTime(q.getDate());
            int daySource = sourceCal.get(Calendar.DAY_OF_MONTH);
            if (DateUtils.isSameDay(sourceCal, actualCal)){
                return i;
            }
        }        
        return -1;
	}
	
	public static int getDayIndexShort(ArrayList<QuoteShort> dataSource, Calendar actualCal, int index){
        int daySearch = actualCal.get(Calendar.DAY_OF_MONTH);
        Calendar sourceCal = Calendar.getInstance();
        for (int i=index;i<dataSource.size();i++){
            QuoteShort q = dataSource.get(i);
            q.getCalendar(sourceCal, q);
            int daySource = sourceCal.get(Calendar.DAY_OF_MONTH);
            if (DateUtils.isSameDay(sourceCal, actualCal)){
                return i;
            }
        }        
        return -1;
	}
	
	public static int getMinuteIndex(ArrayList<Quote> dataSource, Calendar actualCal){
        int daySearch = actualCal.get(Calendar.DAY_OF_MONTH);
        int hSearch   = actualCal.get(Calendar.HOUR_OF_DAY);
        int minSearch = actualCal.get(Calendar.MINUTE);
        Calendar sourceCal = Calendar.getInstance();
        for (int i=0;i<dataSource.size();i++){
            Quote q = dataSource.get(i);
            sourceCal.setTime(q.getDate());
            int daySource = sourceCal.get(Calendar.DAY_OF_MONTH);
            int h = sourceCal.get(Calendar.HOUR_OF_DAY);
            int min = sourceCal.get(Calendar.MINUTE);
            if (DateUtils.isSameDay(sourceCal, actualCal)
            		&& h==hSearch && min==minSearch){
                return i;
            }
        }        
        return -1;
	}
	
	public static int getMinuteIndex(ArrayList<QuoteShort> dataSource,Calendar sourceCal, Calendar actualCal,int index){
        int daySearch = actualCal.get(Calendar.DAY_OF_MONTH);
        int hSearch   = actualCal.get(Calendar.HOUR_OF_DAY);
        int minSearch = actualCal.get(Calendar.MINUTE);
        if (index<=0) index = 0;
        for (int i=index;i<dataSource.size();i++){
            QuoteShort q = dataSource.get(i);
            QuoteShort.getCalendar(sourceCal, q);
            int daySource = sourceCal.get(Calendar.DAY_OF_MONTH);
            int h = sourceCal.get(Calendar.HOUR_OF_DAY);
            int min = sourceCal.get(Calendar.MINUTE);
            if (DateUtils.isSameDay(sourceCal, actualCal)
            		&& h==hSearch && min==minSearch){
                return i;
            }
        }        
        return -1;
	}

	public static ArrayList<Quote> getDayData(ArrayList<Quote> dataSource, Calendar actualCal){
		
		ArrayList<Quote> dayData = new ArrayList<Quote> ();

       
        Calendar sourceCal = Calendar.getInstance();
        for (int i=0;i<dataSource.size();i++){
            Quote q = dataSource.get(i);
            sourceCal.setTimeInMillis((q.getDate().getTime()));
            
            sourceCal.setTimeInMillis(q.getDate().getTime());
  			int day =  sourceCal.get(Calendar.DAY_OF_MONTH);
  			int month =  sourceCal.get(Calendar.MONTH);
  			int y =  sourceCal.get(Calendar.YEAR);  			
  			if (day==2 && month==Calendar.NOVEMBER && y==2009){
  				//System.out.println(PrintUtils.Print(q));
  			}
            int daySource = sourceCal.get(Calendar.DAY_OF_MONTH);
            if (DateUtils.isSameDay(sourceCal, actualCal)){
                Quote qNew = new Quote();
                qNew.copy(q);
                dayData.add(qNew);
            }else if (sourceCal.getTimeInMillis()>actualCal.getTimeInMillis()){
            	break;
            }
        }        
	
        return dayData;
	}
	
public static ArrayList<QuoteShort> getDayDataShort(ArrayList<QuoteShort> dataSource, Calendar actualCal,int index){
		
		ArrayList<QuoteShort> dayData = new ArrayList<QuoteShort> ();
       
        Calendar sourceCal = Calendar.getInstance();
        for (int i=index;i<dataSource.size();i++){
        	QuoteShort q = dataSource.get(i);
        	QuoteShort.getCalendar(sourceCal, q);

            if (DateUtils.isSameDay(sourceCal, actualCal)){
                QuoteShort qNew = new QuoteShort();
                qNew.copy(q);
                dayData.add(qNew);
            }else if (sourceCal.getTimeInMillis()>actualCal.getTimeInMillis()){
            	break;
            }
        }        
	
        return dayData;
	}
	
	
	public static Quote testPriceMovement(ArrayList<Quote> data, int begin, int end,
			double beginValue,double targetValue,boolean down){
		Quote result = new Quote();
		result.setOpen(-999.0);
		result.setHigh(0);
		result.setLow(0);
		for (int i=begin;i<=end;i++){
			Quote q = data.get(i);
			int diffL = TradingUtils.getPipsDiff(targetValue, q.getLow());
			int diffH = TradingUtils.getPipsDiff(q.getHigh(),beginValue);
			//System.out.println("H L: "+PrintUtils.Print(q.getHigh())+" "+PrintUtils.Print(q.getLow()));
			if (down){
				if (diffL>=0){
					result.setOpen(999.0);
					result.setIndex(i);
					return result;
				}else if (diffH>=result.getHigh()){
					result.setHigh(diffH);
				}
			}else{
				
			}
		}
		return result;
	}
	
	public static Quote checkPriceRetrace(ArrayList<Quote> data, int begin, int end,
			double beginValue,double targetValue,double maxValue,boolean down){
		Quote result = new Quote();
		result.setOpen(-50.0);
		result.setHigh(0);
		result.setLow(0);
		for (int i=begin;i<=end;i++){
			Quote q = data.get(i);
			if (down){
				int advPips = TradingUtils.getPipsDiff(q.getHigh(),maxValue);
				int posPips = TradingUtils.getPipsDiff(targetValue,q.getLow());
				if (advPips>=0){
					result.setOpen(-999.0);
					return result;
				}
				if (posPips>=0){
					result.setOpen(999.0);
					return result;
				}
			}else{//LINE<DO
				int advPips = TradingUtils.getPipsDiff(maxValue,q.getLow());
				int posPips = TradingUtils.getPipsDiff(q.getHigh(),targetValue);
				if (advPips>=0){
					result.setOpen(-999.0);
					return result;
				}
				if (posPips>=0){
					result.setOpen(999.0);
					return result;
				}
			}
		}
		return result;
	}
	
	public static Quote testPriceContinuation(ArrayList<Quote> data, int begin, int end,
			double beginValue,double limitValue,int tp){
		Quote result = new Quote();
		result.setOpen(100.0);
		result.setHigh(0);
		result.setLow(0);
		//System.out.println("begin date : "+DateUtils.datePrint(data.get(begin).getDate()));
		for (int i=begin;i<=end;i++){
			
			Quote q = data.get(i);
			//System.out.println(PrintUtils.getOHLC(q));
			int diffL = TradingUtils.getPipsDiff(beginValue, q.getLow());
			int diffH = TradingUtils.getPipsDiff(q.getHigh(),limitValue);
			//System.out.println("diffs: "+PrintUtils.getOHLC(q)+" "+diffH+" "+diffL);
			if (diffH>=0){
				//System.out.print("LOSS");
				result.setOpen(-999.0);
				result.setHigh(diffH);
				return result;
			}
		
			 	if (diffL>=tp){
			 		result.setLow(diffL);
			 		return result;
			 	}
				/*else if (diffL>=result.getLow()){
				//System.out.println("actualización low: "+PrintUtils.Print(q.getHigh()));
				result.setLow(diffL);*/
											
		}
		return result;
	}
	
	public static String completeNumber5(String num){
        String com=num;
        
        if (com.length()==5)
            com+="0";
        if (com.length()==4)
            com+="00";
        if (com.length()==3)
            com+="000";
        if (com.length()==2)
            com+="0000";
        if (com.length()==1)
            com+="00000";
        return com;
    }
	
	public static String completeNumber(String num){
        String com=num;
     
        if (com.length()==4)
            com+="0";
        if (com.length()==3)
            com+="00";
        if (com.length()==2)
            com+="000";
        if (com.length()==1)
            com+="0000";
        return com;
    }
	
	public static int getPipsDiff(double val1,double val2){
        String val1Str = PrintUtils.Print3(val1);
        String val2Str = PrintUtils.Print3(val2);
        val1Str = completeNumber(val1Str.substring(0, 1)+val1Str.substring(2, val1Str.length()));
        val2Str = completeNumber(val2Str.substring(0, 1)+val2Str.substring(2, val2Str.length()));
        
        //System.out.println("[getPipsDiff] "+val1+" "+val2+" "+val1Str+" "+val2Str);
        int diff =Integer.valueOf(val1Str)-Integer.valueOf(val2Str);
        return diff;
    }
	
	public static int getPipsDiff5(double val1,double val2){
        String val1Str = PrintUtils.Print5dec(val1);
        String val2Str = PrintUtils.Print5dec(val2);
        val1Str = completeNumber5(val1Str.substring(0, 1)+val1Str.substring(2, val1Str.length()));
        val2Str = completeNumber5(val2Str.substring(0, 1)+val2Str.substring(2, val2Str.length()));
        

        int diff =Integer.valueOf(val1Str)-Integer.valueOf(val2Str);
        return diff;
    }
	
	static double getAverageRange(ArrayList<Quote> dailyData,int bar,int shortPeriod,int longPeriod){
        int i;
        int longADRPeriod  = longPeriod;
        int shortADRPeriod = shortPeriod;
        double localHigh   = 0;
        double localLow    = 0;
        double highMALong  = 0;
        double lowMALong   = 0;
        double highMAShort = 0;
        double lowMAShort  = 0;
        i=0;
        Calendar cal = Calendar.getInstance();
        int total=0;
        //System.out.println("[getAverageRange] bar "+bar);
        while (total<longADRPeriod)
	{
            if ((bar-i)>=0)
            {
                Quote q = dailyData.get(bar-i);
                cal.setTime(q.getDate());
                if (cal.get(Calendar.DAY_OF_WEEK)>=Calendar.MONDAY 
                        && cal.get(Calendar.DAY_OF_WEEK)<=Calendar.FRIDAY){
                    localLow  = dailyData.get(bar-i).getLow();
                    localHigh = dailyData.get(bar-i).getHigh();
                    lowMALong += localLow;
                    highMALong += localHigh;
                    if ( i < shortADRPeriod ){
                        lowMAShort += localLow;
                        highMAShort += localHigh;
                    }
                    total++;
                }
            }else{
                break;
            }
            i++;
	}
	lowMALong /= longADRPeriod;
	highMALong /= longADRPeriod;
	lowMAShort /= shortADRPeriod;
	highMAShort /= shortADRPeriod;

        if ((highMALong - lowMALong)<=(highMAShort - lowMAShort)){
            return (highMALong - lowMALong);
        }else{
            return (highMAShort - lowMAShort);
        }
    }
	
	static double getAverageRangeShort(ArrayList<QuoteShort> dailyData,int bar,int shortPeriod,int longPeriod){
        int i;
        int longADRPeriod  = longPeriod;
        int shortADRPeriod = shortPeriod;
        double localHigh   = 0;
        double localLow    = 0;
        double highMALong  = 0;
        double lowMALong   = 0;
        double highMAShort = 0;
        double lowMAShort  = 0;
        i=0;
        Calendar cal = Calendar.getInstance();
        int total=0;
        //System.out.println("[getAverageRange] bar "+bar);
        while (total<longADRPeriod)
	{
            if ((bar-i)>=0)
            {
                QuoteShort q = dailyData.get(bar-i);
                QuoteShort.getCalendar(cal, q);
                if (cal.get(Calendar.DAY_OF_WEEK)>=Calendar.MONDAY 
                        && cal.get(Calendar.DAY_OF_WEEK)<=Calendar.FRIDAY){
                    localLow  = dailyData.get(bar-i).getLow();
                    localHigh = dailyData.get(bar-i).getHigh();
                    lowMALong += localLow;
                    highMALong += localHigh;
                    if ( i < shortADRPeriod ){
                        lowMAShort += localLow;
                        highMAShort += localHigh;
                    }
                    total++;
                }
            }else{
                break;
            }
            i++;
	}
	lowMALong /= longADRPeriod;
	highMALong /= longADRPeriod;
	lowMAShort /= shortADRPeriod;
	highMAShort /= shortADRPeriod;

        if ((highMALong - lowMALong)<=(highMAShort - lowMAShort)){
            return (highMALong - lowMALong);
        }else{
            return (highMAShort - lowMAShort);
        }
    }
	
	static double getAverageRange(ArrayList<Quote> dailyData,int bar){
        int i;
        int longADRPeriod  = 100;
        int shortADRPeriod = 3;
        double localHigh   = 0;
        double localLow    = 0;
        double highMALong  = 0;
        double lowMALong   = 0;
        double highMAShort = 0;
        double lowMAShort  = 0;
        i=0;
        Calendar cal = Calendar.getInstance();
        int total=0;
        //System.out.println("[getAverageRange] bar "+bar);
        while (total<longADRPeriod)
	{
            if ((bar-i)>=0)
            {
                Quote q = dailyData.get(bar-i);
                cal.setTime(q.getDate());
                if (cal.get(Calendar.DAY_OF_WEEK)>=Calendar.MONDAY 
                        && cal.get(Calendar.DAY_OF_WEEK)<=Calendar.FRIDAY){
                    localLow  = dailyData.get(bar-i).getLow();
                    localHigh = dailyData.get(bar-i).getHigh();
                    lowMALong += localLow;
                    highMALong += localHigh;
                    if ( i < shortADRPeriod ){
                        lowMAShort += localLow;
                        highMAShort += localHigh;
                    }
                    total++;
                }
            }else{
                break;
            }
            i++;
	}
	lowMALong /= longADRPeriod;
	highMALong /= longADRPeriod;
	lowMAShort /= shortADRPeriod;
	highMAShort /= shortADRPeriod;

        if ((highMALong - lowMALong)<=(highMAShort - lowMAShort)){
            return (highMALong - lowMALong);
        }else{
            return (highMAShort - lowMAShort);
        }
    }
	
	public static ArrayList<PhilDay>calculateFIBS(ArrayList<QuoteShort> data,ArrayList<QuoteShort> dailyData,
			int shortPeriod,int longPeriod){
		
		 ArrayList<PhilDay> philDays = new ArrayList<PhilDay>();
		 Calendar qCal = Calendar.getInstance();
		 int beforeDay = -1;
		 double FIBR1=-1;double FIBS1=-1;double FIBR2=-1;double FIBS2=-1;double FIBR3=-1;double FIBS3=-1;
		 double FIBR4=-1;double FIBS4=-1;double FIBR5=-1;double FIBS5=-1;
		 double DO=-1;
		 int lastDay=-1;

		 for (int i=0;i<data.size();i++){
			 QuoteShort q = data.get(i);
			 QuoteShort.getCalendar(qCal, q);
	        int actualDay   = qCal.get(Calendar.DAY_OF_YEAR);
	        int dayWeek = qCal.get(Calendar.DAY_OF_WEEK);

	        if (dayWeek==Calendar.SATURDAY || dayWeek==Calendar.SUNDAY){
	            continue;
	        }

	        double range = -1;
	        if (actualDay!=beforeDay){
	            ArrayList<PhilLine> lines = new ArrayList<PhilLine>(); 
	            if (lastDay!=-1){
	                DO = q.getOpen();
	                range = getAverageRangeShort(dailyData,lastDay,shortPeriod,longPeriod);                                       					
	                beforeDay = actualDay;					
	                lines.add(PhilLine.createLine(LineType.DO, DO));//0
	            }
	           
	            FIBR1 = DO + ( range * 0.382 );//System.out.println("FIB1: "+PrintUtils.Print(FIBR1)+" "+range);
	            FIBS1 = DO - ( range * 0.382 );
	            FIBR2 = DO + ( range * 0.618 );
	            FIBS2 = DO - ( range * 0.618 );
	            FIBR3 = DO + ( range * 0.764 );
	            FIBS3 = DO - ( range * 0.764 );
	            FIBR4 = DO + ( range * 1.000 );
	            FIBS4 = DO - ( range * 1.000 );
	            FIBR5 = DO + ( range * 1.382 );
	            FIBS5 = DO - ( range * 1.382 );
	            lines.add(PhilLine.createLine(LineType.FIBR1, FIBR1));//22
	            lines.add(PhilLine.createLine(LineType.FIBR2, FIBR2));//23          
	            lines.add(PhilLine.createLine(LineType.FIBR3, FIBR3));//24
	            lines.add(PhilLine.createLine(LineType.FIBR4, FIBR4));//25
	            lines.add(PhilLine.createLine(LineType.FIBR5, FIBR5));//26
	            lines.add(PhilLine.createLine(LineType.FIBS1, FIBS1));//27
	            lines.add(PhilLine.createLine(LineType.FIBS2, FIBS2));//28          
	            lines.add(PhilLine.createLine(LineType.FIBS3, FIBS3));//29
	            lines.add(PhilLine.createLine(LineType.FIBS4, FIBS4));//30
	            lines.add(PhilLine.createLine(LineType.FIBS5, FIBS5));//31

	            //add ney day
	            Calendar dayCal = Calendar.getInstance();
	            QuoteShort.getCalendar(dayCal, q);
	            PhilDay pDay = new PhilDay();
	            pDay.setDay(dayCal);
	            pDay.setIndex(i);
	            pDay.setLines(lines);
	            philDays.add(pDay);

	            beforeDay=actualDay;
	            lastDay++;
	        }
		 }
		 
		 return philDays;
	}
	
	public static ArrayList<PhilDayShort>calculateFIBSShort(ArrayList<QuoteShort> data,ArrayList<QuoteShort> dailyData,
			int shortPeriod,int longPeriod){
		
		 ArrayList<PhilDayShort> philDays = new ArrayList<PhilDayShort>();
		 Calendar qCal = Calendar.getInstance();
		 int beforeDay = -1;
		 short FIBR1=-1;short FIBS1=-1;short FIBR2=-1;short FIBS2=-1;short FIBR3=-1;short FIBS3=-1;
		 short FIBR4=-1;short FIBS4=-1;short FIBR5=-1;short FIBS5=-1;
		 short DO=-1;
		 int lastDay=-1;

		 for (int i=0;i<data.size();i++){
			 QuoteShort q = data.get(i);
			 QuoteShort.getCalendar(qCal, q);
	        int actualDay   = qCal.get(Calendar.DAY_OF_YEAR);
	        int dayWeek = qCal.get(Calendar.DAY_OF_WEEK);

	        if (dayWeek==Calendar.SATURDAY || dayWeek==Calendar.SUNDAY){
	            continue;
	        }

	        double range = -1;
	        if (actualDay!=beforeDay){
	            ArrayList<PhilLineShort> lines = new ArrayList<PhilLineShort>(); 
	            if (lastDay!=-1){
	                DO = q.getOpen();
	                range = getAverageRangeShort(dailyData,lastDay,shortPeriod,longPeriod);                                       					
	                beforeDay = actualDay;					
	                lines.add(PhilLineShort.createLine(LineType.DO, DO));//0
	            }
	           
	            FIBR1 = (short) (DO + ( range * 0.382 ));//System.out.println("FIB1: "+PrintUtils.Print(FIBR1)+" "+range);
	            FIBS1 = (short) (DO - ( range * 0.382 ));
	            FIBR2 = (short) (DO + ( range * 0.618 ));
	            FIBS2 = (short) (DO - ( range * 0.618 ));
	            FIBR3 = (short) (DO + ( range * 0.764 ));
	            FIBS3 = (short) (DO - ( range * 0.764 ));
	            FIBR4 = (short) (DO + ( range * 1.000 ));
	            FIBS4 = (short) (DO - ( range * 1.000 ));
	            FIBR5 = (short) (DO + ( range * 1.382 ));
	            FIBS5 = (short) (DO - ( range * 1.382 ));
	            lines.add(PhilLineShort.createLine(LineType.FIBR1, FIBR1));//22
	            lines.add(PhilLineShort.createLine(LineType.FIBR2, FIBR2));//23          
	            lines.add(PhilLineShort.createLine(LineType.FIBR3, FIBR3));//24
	            lines.add(PhilLineShort.createLine(LineType.FIBR4, FIBR4));//25
	            lines.add(PhilLineShort.createLine(LineType.FIBR5, FIBR5));//26
	            lines.add(PhilLineShort.createLine(LineType.FIBS1, FIBS1));//27
	            lines.add(PhilLineShort.createLine(LineType.FIBS2, FIBS2));//28          
	            lines.add(PhilLineShort.createLine(LineType.FIBS3, FIBS3));//29
	            lines.add(PhilLineShort.createLine(LineType.FIBS4, FIBS4));//30
	            lines.add(PhilLineShort.createLine(LineType.FIBS5, FIBS5));//31

	            //add ney day
	            Calendar dayCal = Calendar.getInstance();
	            QuoteShort.getCalendar(dayCal, q);
	            PhilDayShort pDay = new PhilDayShort();
	            pDay.setDay(dayCal);
	            pDay.setIndex(i);
	            pDay.setLines(lines);
	            philDays.add(pDay);

	            beforeDay=actualDay;
	            lastDay++;
	        }
		 }
		 
		 return philDays;
	}
	
	public static ArrayList<PhilDay> calculateLines(ArrayList<Quote> data,ArrayList<Quote> dailyData,
			ArrayList<Quote> weeklyData,ArrayList<Quote> monthlyData){
	 
    ArrayList<PhilDay> philDays = new ArrayList<PhilDay>();
	 
    Calendar qCal = Calendar.getInstance();
    int beforeDay = -1;
    //POINTS
    double DO = -1;
    double DP = -1;double DR1=-1;double DR2=-1;double DR3=-1;double DS1=-1;double DS2=-1;double DS3=-1;
    double WP = -1;double WR1=-1;double WR2=-1;double WR3=-1;double WS1=-1;double WS2=-1;double WS3=-1;
    double MP = -1;double MR1=-1;double MR2=-1;double MR3=-1;double MS1=-1;double MS2=-1;double MS3=-1;
    double FIBR1=-1;double FIBS1=-1;double FIBR2=-1;double FIBS2=-1;double FIBR3=-1;double FIBS3=-1;
    double FIBR4=-1;double FIBS4=-1;double FIBR5=-1;double FIBS5=-1;
    double lastHighD = -1;double lastLowD = -1;double lastCloseD = -1;
    double lastHighW = -1;double lastLowW = -1;double lastCloseW = -1;
    double lastHighM = -1;double lastLowM = -1;double lastCloseM = -1;

    int lastDay=-1;
    int lastWeek=-1;
    int lastMonth=-1;
    for (int i=0;i<data.size();i++){
        Quote q = data.get(i);
        qCal.setTime(q.getDate());
        int actualDay   = qCal.get(Calendar.DAY_OF_YEAR);
        int actualMonth = qCal.get(Calendar.MONTH);
        int actualWeek  = qCal.get(Calendar.WEEK_OF_YEAR);
        int dayWeek = qCal.get(Calendar.DAY_OF_WEEK);

        if (dayWeek==Calendar.SATURDAY || dayWeek==Calendar.SUNDAY){
            continue;
        }

        double range = -1;
        if (actualDay!=beforeDay){
            //int lastDay   =  DateUtils.findLastDateIndex(dailyData,q,1);
            lastWeek  =  DateUtils.findLastDateIndex(weeklyData,q,2);
            lastMonth =  DateUtils.findLastDateIndex(monthlyData,q,3);
            //double range = getAverageRange(dailyData,lastDay);
            ArrayList<PhilLine> lines = new ArrayList<PhilLine>(); 
            if (lastDay!=-1){
                lastHighD = dailyData.get(lastDay).getHigh();
                lastLowD = dailyData.get(lastDay).getLow();
                lastCloseD = dailyData.get(lastDay).getClose();
                DO = q.getOpen();
                range = getAverageRange(dailyData,lastDay);                                       					
                beforeDay = actualDay;					
                /*System.out.println("**NEW Day DO: "+DateUtils.datePrint(q.getDate())
                                +" "+PrintUtils.Print(DO)
                );*/
                DP = ( lastHighD + lastLowD + lastCloseD ) / 3;
                DR1 = ( 2 * DP ) - lastLowD;
                DS1 = ( 2 * DP ) - lastHighD;
                DR2 = DP + ( lastHighD - lastLowD );
                DS2 = DP - ( lastHighD - lastLowD );
                DR3 = ( 2 * DP ) + ( lastHighD - ( 2 * lastLowD ) );
                DS3 = ( 2 * DP ) - ( ( 2 * lastHighD ) - lastLowD );
                lines.add(PhilLine.createLine(LineType.DO, DO));//0
                lines.add(PhilLine.createLine(LineType.DP, DP));//1
                lines.add(PhilLine.createLine(LineType.DR1, DR1));//2
                lines.add(PhilLine.createLine(LineType.DS1, DS1));//3
                lines.add(PhilLine.createLine(LineType.DR2, DR2));//4
                lines.add(PhilLine.createLine(LineType.DS2, DS2));//5
                lines.add(PhilLine.createLine(LineType.DR3, DR3));//6
                lines.add(PhilLine.createLine(LineType.DS3, DS3));//7
                lines.add(PhilLine.createLine(LineType.YH, lastHighD));
                lines.add(PhilLine.createLine(LineType.YL, lastLowD));
                
            }
            if (lastWeek!=-1){
                lastHighW = weeklyData.get(lastWeek).getHigh();
                lastLowW = weeklyData.get(lastWeek).getLow();
                lastCloseW = weeklyData.get(lastWeek).getClose();
                WP = ( lastHighW + lastLowW + lastCloseW ) / 3;
                WR1 = ( 2 * WP ) - lastLowW;
                WS1 = ( 2 * WP ) - lastHighW;
                WR2 = WP + ( lastHighW - lastLowW );
                WS2 = WP - ( lastHighW - lastLowW );
                WR3 = ( 2 * WP ) + ( lastHighW - ( 2 * lastLowW ) );
                WS3 = ( 2 * WP ) - ( ( 2 * lastHighW ) - lastLowW );

                lines.add(PhilLine.createLine(LineType.WP, WP));//8
                lines.add(PhilLine.createLine(LineType.WR1, WR1));//9
                lines.add(PhilLine.createLine(LineType.WS1, WS1));//10
                lines.add(PhilLine.createLine(LineType.WR2, WR2));//11
                lines.add(PhilLine.createLine(LineType.WS2, WS2));//12
                lines.add(PhilLine.createLine(LineType.WR3, WR3));//13
                lines.add(PhilLine.createLine(LineType.WS3, WS3));//14
                lines.add(PhilLine.createLine(LineType.WH, lastHighW));
                lines.add(PhilLine.createLine(LineType.WL, lastLowW));
                //System.out.println("new WP: "+WP);
            }
            if (lastMonth!=-1){
                lastHighM  = monthlyData.get(lastMonth).getHigh();
                lastLowM   = monthlyData.get(lastMonth).getLow();
                lastCloseM = monthlyData.get(lastMonth).getClose();
                MP = ( lastHighM + lastLowM + lastCloseM ) / 3;
                MR1 = ( 2 * MP ) - lastLowM;
                MS1 = ( 2 * MP ) - lastHighM;
                MR2 = MP + ( lastHighM - lastLowM );
                MS2 = MP - ( lastHighM - lastLowM );
                MR3 = ( 2 * MP ) + ( lastHighM - ( 2 * lastLowM ) );
                MS3 = ( 2 * MP ) - ( ( 2 * lastHighM ) - lastLowM );

                lines.add(PhilLine.createLine(LineType.MP, MP));//15
                lines.add(PhilLine.createLine(LineType.MR1, MR1));//16
                lines.add(PhilLine.createLine(LineType.MS1, MS1));//17
                lines.add(PhilLine.createLine(LineType.MR2, MR2));//18
                lines.add(PhilLine.createLine(LineType.MS2, MS2));//19
                lines.add(PhilLine.createLine(LineType.MR3, MR3));//20
                lines.add(PhilLine.createLine(LineType.MS3, MS3));//21
            }

            FIBR1 = DO + ( range * 0.382 );
            FIBS1 = DO - ( range * 0.382 );
            FIBR2 = DO + ( range * 0.618 );
            FIBS2 = DO - ( range * 0.618 );
            FIBR3 = DO + ( range * 0.764 );
            FIBS3 = DO - ( range * 0.764 );
            FIBR4 = DO + ( range * 1.000 );
            FIBS4 = DO - ( range * 1.000 );
            FIBR5 = DO + ( range * 1.382 );
            FIBS5 = DO - ( range * 1.382 );
            lines.add(PhilLine.createLine(LineType.FIBR1, FIBR1));//22
            lines.add(PhilLine.createLine(LineType.FIBR2, FIBR2));//23          
            lines.add(PhilLine.createLine(LineType.FIBR3, FIBR3));//24
            lines.add(PhilLine.createLine(LineType.FIBR4, FIBR4));//25
            lines.add(PhilLine.createLine(LineType.FIBR5, FIBR5));//26
            lines.add(PhilLine.createLine(LineType.FIBS1, FIBS1));//27
            lines.add(PhilLine.createLine(LineType.FIBS2, FIBS2));//28          
            lines.add(PhilLine.createLine(LineType.FIBS3, FIBS3));//29
            lines.add(PhilLine.createLine(LineType.FIBS4, FIBS4));//30
            lines.add(PhilLine.createLine(LineType.FIBS5, FIBS5));//31

            /*System.out.println("Testing LINE FIBS1 range "+breachedLine
                            +" "+PrintUtils.Print(breachedLineValue)
                            +" "+PrintUtils.Print(FIBS1)
                            +" range "+PrintUtils.Print(range)
                            );
            */

            //add ney day
            Calendar dayCal = Calendar.getInstance();
            dayCal.setTime(q.getDate());
            PhilDay pDay = new PhilDay();
            pDay.setDay(dayCal);
            pDay.setIndex(i);
            pDay.setLines(lines);
            philDays.add(pDay);

            beforeDay=actualDay;
            lastDay++;
        }
    }
    return philDays;
}

	public static ArrayList<Quote> cleanWeekendData(ArrayList<Quote> dataS) {
		// TODO Auto-generated method stub
		ArrayList<Quote> data = new ArrayList<Quote>();
		
		Calendar cal = Calendar.getInstance();
		for (int i=0;i<dataS.size();i++){
			Quote q = dataS.get(i);
			cal.setTime(q.getDate());
			int day = cal.get(Calendar.DAY_OF_YEAR);
			if (cal.get(Calendar.DAY_OF_WEEK)==Calendar.SATURDAY || cal.get(Calendar.DAY_OF_WEEK)==Calendar.SUNDAY
					|| day==1)
				continue;
			Quote qNew = new Quote();
			qNew.copy(q);
			data.add(q);
		}
		return data;
	}
	
	public static ArrayList<QuoteShort> cleanWeekendDataS(ArrayList<QuoteShort> dataS) {
		// TODO Auto-generated method stub
		ArrayList<QuoteShort> data = new ArrayList<QuoteShort>();
		
		Calendar cal = Calendar.getInstance();
		for (int i=0;i<dataS.size();i++){
			QuoteShort q = dataS.get(i);
			QuoteShort.getCalendar(cal, q);
			int day = cal.get(Calendar.DAY_OF_YEAR);
			if (cal.get(Calendar.DAY_OF_WEEK)==Calendar.SATURDAY || cal.get(Calendar.DAY_OF_WEEK)==Calendar.SUNDAY
					|| day==1)
				continue;
			QuoteShort qNew = new QuoteShort();
			qNew.copy(q);
			data.add(q);
			//System.out.println(i);
		}
		return data;
	}
	
	public static void cleanWeekendDataSinside(ArrayList<QuoteShort> dataS) {
		
		Calendar cal = Calendar.getInstance();
		int i = 0;
		while (i<dataS.size()){		
			QuoteShort q = dataS.get(i);
			QuoteShort.getCalendar(cal, q);
			int day = cal.get(Calendar.DAY_OF_YEAR);
			if (cal.get(Calendar.DAY_OF_WEEK)==Calendar.SATURDAY || cal.get(Calendar.DAY_OF_WEEK)==Calendar.SUNDAY
					|| day==1){
				dataS.remove(i);
			}else{
				i++;
				//System.out.println(i);
			}		
			
		}
		
	}
	
	public static ArrayList<Tick> cleanWeekendDataT(ArrayList<Tick> dataS) {
		// TODO Auto-generated method stub
		ArrayList<Tick> data = new ArrayList<Tick>();
		
		Calendar cal = Calendar.getInstance();
		for (int i=0;i<dataS.size();i++){
			Tick q = dataS.get(i);
			Tick.getCalendar(cal, q);
			if (cal.get(Calendar.DAY_OF_WEEK)==Calendar.SATURDAY || cal.get(Calendar.DAY_OF_WEEK)==Calendar.SUNDAY)
				continue;
			Tick qNew = new Tick();
			qNew.copy(q);
			data.add(q);
		}
		return data;
	}
	
	public static ArrayList<QuoteBidAsk> cleanWeekendDataBidAsk(ArrayList<QuoteBidAsk> dataS) {
		// TODO Auto-generated method stub
		ArrayList<QuoteBidAsk> data = new ArrayList<QuoteBidAsk>();
		
		Calendar cal = Calendar.getInstance();
		for (int i=0;i<dataS.size();i++){
			QuoteBidAsk q = dataS.get(i);
			cal.setTimeInMillis(q.getCal().getTimeInMillis());
			if (cal.get(Calendar.DAY_OF_WEEK)==Calendar.SATURDAY || cal.get(Calendar.DAY_OF_WEEK)==Calendar.SUNDAY)
				continue;
			QuoteBidAsk qNew = new QuoteBidAsk();
			qNew.copy(q);
			data.add(qNew);
		}
		return data;
	}

	
	
	public static void testPriceMovement(PriceTestResult res,ArrayList<Quote> data,
			int begin, int end, double beginValue, double stopLoss,
			double takeProfit, int mode,boolean debug) {
		
		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(data.get(begin).getDate().getTime());
		res.setWin(0);
		if (debug)
		System.out.println("Date entry sl tp mode : "
				+DateUtils.datePrint(cal)
				+" "+PrintUtils.Print4dec(beginValue)
				+" "+PrintUtils.Print4dec(stopLoss)
				+" "+PrintUtils.Print4dec(takeProfit)
				+" "+mode
				);
				
		res.getCal().setTimeInMillis(data.get(begin).getDate().getTime());
		
		int maxPositive = 0;
		int maxNegative = 0;
		int diffTP = 0;
		int diffSL = 0;
		int diffEntry=0;
		
		for (int i=begin;i<=end;i++){			
			Quote q = data.get(i);			
			cal.setTimeInMillis(q.getDate().getTime());
			
			if (mode == 1){ //interesa que suba tp arriba
				if (takeProfit<=0) diffTP=0;
				else diffTP    = TradingUtils.getPipsDiff(q.getHigh(),takeProfit);
				diffSL    = TradingUtils.getPipsDiff(stopLoss,q.getLow());	
				diffEntry = TradingUtils.getPipsDiff(q.getHigh(), beginValue);
				if (diffEntry>=maxPositive) maxPositive = diffEntry;
				if (diffEntry<=maxNegative) maxNegative = diffEntry;
				if (debug)
				System.out.println("interesa que suba high low "
						+PrintUtils.Print4dec(q.getHigh())
						+" "+PrintUtils.Print4dec(q.getLow())
						+" "+diffTP
						+" "+diffSL
						);
			} else if (mode == 0){ //interesa que baje tp abajo
				if (takeProfit<=0) diffTP=0;
				else diffTP = TradingUtils.getPipsDiff(takeProfit, q.getLow());
				diffSL = TradingUtils.getPipsDiff(q.getHigh(),stopLoss);
				diffEntry = TradingUtils.getPipsDiff(beginValue,q.getLow());
				if (diffEntry>=maxPositive) maxPositive = diffEntry;
				if (diffEntry<=maxNegative) maxNegative = diffEntry;
				if (debug)
				System.out.println("interesa que baje high low "
						+" "+DateUtils.datePrint(q.getDate())
						+" "+PrintUtils.Print4dec(q.getLow())
						+" "+PrintUtils.Print4dec(q.getHigh())
						+" "+diffTP
						+" "+diffSL
						);
			}			
			if (stopLoss>=0){
				if (diffSL>=0){
					if (debug)
					System.out.println("STOPLOSS");
					res.setIndex(i);
					res.setWin(-1);
					res.setMaxPositive(maxPositive);
					res.setMaxNegative(maxNegative);
					res.setLastDiff(diffEntry);
					res.getCloseTime().setTimeInMillis(cal.getTimeInMillis());
					return ;
				}
			}
			
			if (takeProfit>=0){
				if (diffTP>=0){
					if (debug)
					System.out.println("TAKEPROFIT");
					res.setIndex(i);
					res.setWin(1);
					res.setMaxPositive(maxPositive);
					res.setMaxNegative(maxNegative);
					res.setLastDiff(diffEntry);
					res.getCloseTime().setTimeInMillis(cal.getTimeInMillis());
					return ;
				}
			}
			
			/*if (stopLoss>=0){
				if (diffSL>=0){
					result.setIndex(i);
					result.setWin(false);
					result.setMaxPositive(maxPositive);
					result.setMaxNegative(maxNegative);
					result.setLastDiff(diffEntry);
					return result;
				}
			}*/
		}
		res.setIndex(-1);
		//res.setWin(true);
		res.setMaxPositive(maxPositive);
		res.setMaxNegative(maxNegative);
		res.setLastDiff(diffEntry);
		return ;		
	}

	public static void testPriceMovementShort(PriceTestResult res,ArrayList<QuoteShort> data,
			int begin, int end, int beginValue, int stopLoss,
			int takeProfit, int mode,boolean limit,boolean debug) {
		
		Calendar cal = Calendar.getInstance();
		QuoteShort.getCalendar(cal, data.get(begin)); //fecha inicial
		res.setWin(0);
		if (debug)
		System.out.println("Date entry sl tp mode : "
				+DateUtils.datePrint(cal)
				+" "+beginValue
				+" "+stopLoss
				+" "+takeProfit
				+" "+mode
				);
		
		QuoteShort.getCalendar(res.getCal(), data.get(begin)); //fecha inicial
		
		int maxPositive = 0;
		int maxNegative = 0;
		int diffTP = 0;
		int diffSL = 0;
		int diffEntry=0;
		boolean activate = false;
		
		for (int i=begin;i<=end;i++){			
			QuoteShort q = data.get(i);			
			QuoteShort.getCalendar(cal, q); //fecha inicial
			
			if (limit && !activate){
				if (q.getHigh()>=beginValue && beginValue>=q.getLow()) activate = true;
			}
			if (!limit || activate){
				if (mode == 1){ //interesa que suba tp arriba
					if (takeProfit<=0) diffTP=0;
					else diffTP    	= q.getHigh()-takeProfit;
					diffSL    		= stopLoss-q.getLow();	
					diffEntry 		= q.getHigh()-beginValue;
					if (diffEntry>=maxPositive) maxPositive = diffEntry;
					if (diffEntry<=maxNegative) maxNegative = diffEntry;
					if (debug)
					System.out.println("interesa que suba high low "
							+" "+DateUtils.datePrint(cal)
							+" "+q.getHigh()
							+" "+q.getLow()
							+" "+diffTP
							+" "+diffSL
							);
				} else if (mode == 0){ //interesa que baje tp abajo
					if (takeProfit<=0) diffTP=0;
					else diffTP = takeProfit-q.getLow();
					diffSL 		= q.getHigh()-stopLoss;
					diffEntry 	= beginValue-q.getLow();
					if (diffEntry>=maxPositive) maxPositive = diffEntry;
					if (diffEntry<=maxNegative) maxNegative = diffEntry;
					if (debug){
						System.out.println("interesa que baje high low "
								+" "+DateUtils.datePrint(cal)
								+" "+q.getLow()
								+" "+q.getHigh()
								+" "+diffTP
								+" "+diffSL
								);
					}
				}			
				if (stopLoss>=0){
					if (diffSL>=0){
						if (debug)
						System.out.println("STOPLOSS");
						res.setIndex(i);
						res.setWin(-1);
						res.setMaxPositive(maxPositive);
						res.setMaxNegative(maxNegative);
						res.setLastDiff(diffEntry);
						res.getCloseTime().setTimeInMillis(cal.getTimeInMillis());
						return ;
					}
				}
				
				if (takeProfit>=0){
					if (diffTP>=0){
						if (debug)
						System.out.println("TAKEPROFIT");
						res.setIndex(i);
						res.setWin(1);
						res.setMaxPositive(maxPositive);
						res.setMaxNegative(maxNegative);
						res.setLastDiff(diffEntry);
						res.getCloseTime().setTimeInMillis(cal.getTimeInMillis());
						return ;
					}
				}
				
				/*if (stopLoss>=0){
					if (diffSL>=0){
						result.setIndex(i);
						result.setWin(false);
						result.setMaxPositive(maxPositive);
						result.setMaxNegative(maxNegative);
						result.setLastDiff(diffEntry);
						return result;
					}
				}*/
			}
		}
		res.setIndex(-1);
		//res.setWin(true);
		res.setMaxPositive(maxPositive);
		res.setMaxNegative(maxNegative);
		res.setLastDiff(diffEntry);
		return ;		
	}
	
	/**
	 * Posicion ya abierta previamente
	 * @param pos
	 * @param data
	 * @param begin
	 * @param end
	 * @param debug
	 */
	public static void testPosition(PositionShort pos,ArrayList<QuoteShort> data,
			int begin, int end,boolean debug){
		
		pos.setWin(0);
		
		if (end>data.size()-1) end = data.size()-1;
		
		for (int i=begin;i<=end;i++){
			QuoteShort q = data.get(i);
			
			if (pos.getPositionType()==PositionType.LONG){
				if (q.getLow5()<=pos.getSl()){
					pos.setWin(-1);
					return;
				}
				if (q.getHigh5()>=pos.getTp()){
					pos.setWin(1);
					return;
				}
			}
			if (pos.getPositionType()==PositionType.SHORT){
				if (q.getHigh5()>=pos.getSl()){
					pos.setWin(-1);
					return;
				}
				if (q.getLow5()<=pos.getTp()){
					pos.setWin(1);
					return;
				}
			}
		}
		
	}
	
	public static void testPriceMovementShort(PositionShort pos,ArrayList<QuoteShort> data,
			int begin, int end, short beginValue, short stopLoss,
			short takeProfit, int mode,boolean limit,boolean debug) {
		//System.out.println("entrado");
		Calendar cal = Calendar.getInstance();
		QuoteShort.getCalendar(cal, data.get(begin)); //fecha inicial
		pos.setWin(0);
		pos.setEntry(beginValue);
		pos.setSl(stopLoss);
		pos.setTp(takeProfit);
		if (debug)
		System.out.println("Date entry sl tp mode index: "
				+DateUtils.datePrint(cal)
				+" "+beginValue
				+" "+stopLoss
				+" "+takeProfit
				+" "+mode
				+" "+begin
				);
		
		//QuoteShort.getCalendar(res.getCal(), data.get(begin)); //fecha inicial
		
		int maxPositive = 0;
		int maxNegative = 0;
		int diffTP = 0;
		int diffSL = 0;
		int diffEntry=0;
		boolean activate = false;
		int openIndex = 0;
		if (!limit) openIndex=begin;
		for (int i=begin;i<=end;i++){			
			QuoteShort q = data.get(i);			
			QuoteShort.getCalendar(cal, q); //fecha inicial
			
			if (limit && !activate){
				if (q.getHigh()>=beginValue && beginValue>=q.getLow()){
					activate = true;
					pos.setOpenIndex(i);
					openIndex=i;
					pos.setPositionStatus(PositionStatus.OPEN);
					if (debug){
						System.out.println("Operacion activada index "+i);
					}
				}
			}
			if (!limit || activate){
				if (mode == 1){ //interesa que suba tp arriba
					pos.setPositionType(PositionType.LONG);
					if (takeProfit<=0) diffTP=0;
					else diffTP    	= q.getHigh()-takeProfit;
					diffSL    		= stopLoss-q.getLow();	
					diffEntry 		= q.getHigh()-beginValue;
					if (diffEntry>=maxPositive) maxPositive = diffEntry;
					if (diffEntry<=maxNegative) maxNegative = diffEntry;
					if (debug)
					System.out.println("interesa que suba high low "
							+" "+DateUtils.datePrint(cal)
							+" "+q.getHigh()
							+" "+q.getLow()
							+" "+diffTP
							+" "+diffSL
							);
				} else if (mode == 0){ //interesa que baje tp abajo
					pos.setPositionType(PositionType.SHORT);
					if (takeProfit<=0) diffTP=0;
					else diffTP = takeProfit-q.getLow();
					diffSL 		= q.getHigh()-stopLoss;
					diffEntry 	= beginValue-q.getLow();
					if (diffEntry>=maxPositive) maxPositive = diffEntry;
					if (diffEntry<=maxNegative) maxNegative = diffEntry;
					if (debug){
						System.out.println("interesa que baje high low "
								+" "+DateUtils.datePrint(cal)
								+" "+q.getHigh()
								+" "+q.getLow()
								+" "+diffSL
								+" "+diffTP
								);
					}
				}			
				if (stopLoss>=0){
					if (diffSL>=0){
						if (debug)
						System.out.println("STOPLOSS");
						pos.setCloseIndex(i);
						pos.setWin(-1);
						pos.setPositionStatus(PositionStatus.CLOSE);
						pos.getCloseCal().setTimeInMillis(cal.getTimeInMillis());
						return ;
					}
				}
				
				if (takeProfit>=0 && (!limit || openIndex!=i)){
					if (diffTP>=0){
						if (limit && openIndex==i){}
						else{
							if (debug)
							System.out.println("TAKEPROFIT");
							pos.setCloseIndex(i);
							pos.setWin(1);
							pos.setPositionStatus(PositionStatus.CLOSE);
							pos.getCloseCal().setTimeInMillis(cal.getTimeInMillis());
							//System.out.println
							return ;
						}
					}
				}
				
				/*if (stopLoss>=0){
					if (diffSL>=0){
						result.setIndex(i);
						result.setWin(false);
						result.setMaxPositive(maxPositive);
						result.setMaxNegative(maxNegative);
						result.setLastDiff(diffEntry);
						return result;
					}
				}*/
			}
		}
		
		return ;		
	}
	
	public static int calculateMaxAdv(ArrayList<Quote> data, int index,
			double entry,int tp, boolean buy) {
		// TODO Auto-generated method stub
		int res = 0;
		int pipsDiff = -999;
		int pipsExit = -999;
		double exit = 0;
		for ( int i = index;i<data.size();i++){
			Quote q = data.get(i);
			
			if ( buy){
				exit = entry + 0.0001*tp;
				
				pipsDiff = TradingUtils.getPipsDiff(entry, q.getLow());
				pipsExit = TradingUtils.getPipsDiff(q.getHigh(), exit);
				//System.out.println(PrintUtils.Print(exit)+" "+pipsExit
				//		+"--"+q.toString());
			}else{
				exit = entry - 0.0001*tp;
				pipsDiff = TradingUtils.getPipsDiff(q.getHigh(),entry);
				pipsExit = TradingUtils.getPipsDiff(exit,q.getLow());
			}
			if (pipsDiff>res)
				res = pipsDiff;
			if (pipsExit>=0) return res;
			
		}
		return res;
	}

	public static ArrayList<PhilDay> calculateLinesAtr(ArrayList<Quote> dailyData,int period,double atrFactor) {
		// TODO Auto-generated method stub
		ArrayList<PhilDay> philDays = new ArrayList<PhilDay>();
		
		//calculo atr values
		ArrayList<Double> atrValues = MathUtils.calculateAtr(dailyData, period);
		
		for (int i=period;i<dailyData.size();i++){
			Quote q = dailyData.get(i); //day
			double atr = atrValues.get(i); //atr
			int pips = (int) (atr*atrFactor); //pips
			double value = q.getOpen()+pips*0.0001; //valor a testear
			Calendar qCal = Calendar.getInstance();
			
			ArrayList<PhilLine> lines = new ArrayList<PhilLine>(); 
			qCal.setTime(dailyData.get(i).getDate());
			PhilLine line = new PhilLine();
			line.setValue(value);
			line.setLineType(LineType.ATR);
			lines.add(line);
			
			PhilDay pDay = new PhilDay();
			pDay.setDay(qCal);
			pDay.setLines(lines);
			
			philDays.add(pDay);
		}
		return philDays;
	}
	
	public static ArrayList<PhilDay> calculateLinesOffset(ArrayList<Quote> data,int offset,boolean add) {
		// TODO Auto-generated method stub
		ArrayList<PhilDay> philDays = new ArrayList<PhilDay>();
			
		for (int i=0;i<data.size();i++){
			Quote q = data.get(i); //day
			double value = q.getOpen()+offset*0.0001; //valor a testear
			if (!add)
				value = q.getOpen()-offset*0.0001; //valor a testear
			Calendar qCal = Calendar.getInstance();
			
			ArrayList<PhilLine> lines = new ArrayList<PhilLine>(); 
			qCal.setTime(q.getDate());
			PhilLine DO = new PhilLine();
			DO.setValue(q.getOpen());
			DO.setLineType(LineType.DO);
			lines.add(DO);
			
			PhilLine line = new PhilLine();
			line.setValue(value);
			line.setLineType(LineType.DOoffset);
			lines.add(line);
			
			PhilDay pDay = new PhilDay();
			pDay.setDay(qCal);
			pDay.setLines(lines);
			
			philDays.add(pDay);
		}
		return philDays;
	}

	public static Quote getMaxMin(ArrayList<Quote> data, int begin, int end) {
		// TODO Auto-generated method stub
		double actualMax = -1;
		double actualMin = 999999;
		
		if (begin<0) begin = 0;
		if (end>data.size()-1) end =data.size()-1;
		for (int i = begin;i<=end;i++){
			Quote q = data.get(i);
			if (q.getHigh()>actualMax){
				actualMax = q.getHigh();
			}
			if (q.getLow()<actualMin){
				actualMin = q.getLow();
			}
		}
		Quote q = new Quote();
		q.setHigh(actualMax);
		q.setLow(actualMin);
		
		return q;
	}
	
	public static int getMaxMinIndex(ArrayList<QuoteShort> data, int begin, int end,int value,boolean isMax) {
		
		if (begin<0) begin = 0;
		if (end>data.size()-1) end =data.size()-1;
		//System.out.println(begin+" "+end);
		for (int i = begin;i<=end;i++){
			QuoteShort q = data.get(i);
			if (isMax && q.getHigh5()>=value){
				return i;
			}
			if (!isMax && q.getLow5()<=value){
				//System.out.println(q.toString());
				return i;
			}
		}
		return -1;
	}
	
	public static int getMaxMinIndexAdverse(ArrayList<QuoteShort> data, int begin, int end,int value,QuoteShort qAdverse,boolean isMax) {
		
		if (begin<0) begin = 0;
		if (end>data.size()-1) end =data.size()-1;
		//System.out.println(begin+" "+end);
		qAdverse.setOpen5(data.get(begin).getOpen5());
		for (int i = begin;i<=end;i++){
			QuoteShort q = data.get(i);
			if (isMax && q.getHigh5()>=value){
				return i;
			}else if (isMax){
				if (q.getLow5()<=qAdverse.getOpen5()){
					qAdverse.setOpen5(q.getLow5());
				}
			}
			
			if (!isMax && q.getLow5()<=value){
				//System.out.println(q.toString());
				return i;
			}else if (!isMax){
				if (q.getHigh5()>=qAdverse.getOpen5()){
					qAdverse.setOpen5(q.getHigh5());
				}
			}
		}
		return -1;
	}

	
	public static QuoteShort getMaxMinShort(ArrayList<QuoteShort> data,QuoteShort qm,Calendar cal, int begin, int end) {
		// TODO Auto-generated method stub
		int actualMax = 0;
		int actualMin = 300000;
		int actualClose = 0;
		if (begin<0) begin = 0;
		if (end>data.size()-1) end =data.size()-1;
		int highIdx = -1;
		int lowIdx = -1;
		//System.out.println(begin+" "+end);
		for (int i = begin;i<=end;i++){
			QuoteShort q = data.get(i);
			if (q.getHigh5()>actualMax){
				actualMax = q.getHigh5();
				highIdx = i;
			}
			if (q.getLow5()<actualMin){
				actualMin = q.getLow5();
				lowIdx = i;
			}
			actualClose = q.getClose5();
			QuoteShort.getCalendar(cal, q);
		}

		qm.setCal(cal);
		qm.setHighIdx(highIdx);
		qm.setLowIdx(lowIdx);
		qm.setHigh5(actualMax);
		qm.setLow5(actualMin);
		qm.setClose5(actualClose);
		
		return qm;
	}
	
	public static QuoteShort getMaxMinShortOpen(ArrayList<QuoteShort> data,QuoteShort qm,Calendar cal, int begin, int end) {
		// TODO Auto-generated method stub
		int actualMax = 0;
		int actualMin = 300000;
		int actualClose = 0;
		if (begin<0) begin = 0;
		if (end>data.size()-1) end =data.size()-1;
		int highIdx = -1;
		int lowIdx = -1;
		//System.out.println(begin+" "+end);
		for (int i = begin;i<=end;i++){
			QuoteShort q = data.get(i);
			if (q.getOpen5()>actualMax){
				actualMax = q.getOpen5();
				highIdx = i;
			}
			if (q.getOpen5()<actualMin){
				actualMin = q.getOpen5();
				lowIdx = i;
			}
			actualClose = q.getClose5();
			QuoteShort.getCalendar(cal, q);
		}

		qm.setCal(cal);
		qm.setHighIdx(highIdx);
		qm.setLowIdx(lowIdx);
		qm.setHigh5(actualMax);
		qm.setLow5(actualMin);
		qm.setClose5(actualClose);
		
		return qm;
	}
	
	public static int getFirstTouch(ArrayList<QuoteShort> data,int begin, int end,int value) {

		if (begin<1) begin = 1;
		if (end>data.size()-1) end =data.size()-1;
		//System.out.println(begin+" "+end);
		for (int i = begin;i<=end;i++){
			QuoteShort q1 = data.get(i-1);
			QuoteShort q = data.get(i);
			if (q.getHigh5()>=value && q.getLow5()<=value)
				return i;
		}
		
		return -1;
	}
	
	public static int getLastTouch(ArrayList<QuoteShort> data,int begin, int end,int value) {

		if (begin<1) begin = 1;
		if (end>data.size()-1) end =data.size()-1;
		//System.out.println(begin+" "+end);
		for (int i = end;i>=begin;i--){
			QuoteShort q1 = data.get(i-1);
			QuoteShort q = data.get(i);
			if (q.getHigh5()>=value && q.getLow5()<=value)
				return i;
		}
		
		return -1;
	}
	
	public static QuoteShort getMaxMinShortTP(ArrayList<QuoteShort> data,QuoteShort qm,Calendar cal, int begin, int end,int valueTP,boolean debug) {
		// TODO Auto-generated method stub
		int actualMax = 0;
		int actualMin = 300000;
		int actualClose = 0;
		if (begin<0) begin = 0;
		if (end>data.size()-1) end =data.size()-1;

		boolean isLong = false;
		if (valueTP>=data.get(begin).getOpen5()){
			isLong = true;
		}
		for (int i = begin;i<=end;i++){
			QuoteShort q = data.get(i);
			if (debug)
				System.out.println("[getMaxMinShortTP] "+valueTP+" "+isLong+" "+q.toString());
			if (q.getHigh5()>actualMax){
				actualMax = q.getHigh5();
			}
			if (q.getLow5()<actualMin){
				actualMin = q.getLow5();
			}
			
			if (isLong){
				if (q.getHigh5()>=valueTP){
					actualClose = valueTP;
					QuoteShort.getCalendar(cal, q);
					//System.out.println(q.toString());
					break;
				}
			}else{
				if (q.getLow5()<=valueTP){
					actualClose = valueTP;
					QuoteShort.getCalendar(cal, q);
					break;
				}
			}
			actualClose = q.getClose5();
			QuoteShort.getCalendar(cal, q);
		}
		
		//System.out.println(data.get(begin).toString());
		
		qm.setCal(cal);
		qm.setHigh((short)(actualMax/10));
		qm.setLow((short)(actualMin/10));
		qm.setHigh5(actualMax);
		qm.setLow5(actualMin);
		qm.setClose5(actualClose);
		
		return qm;
	}
	
	public static QuoteShort getMaxMinShortTPMaxBars(ArrayList<QuoteShort> data,QuoteShort qm,Calendar cal, int begin, int end,int valueTP,
			int maxBars,boolean debug) {
		// TODO Auto-generated method stub
		int actualMax = 0;
		int actualMin = 300000;
		int actualClose = 0;
		if (begin<0) begin = 0;
		if (end>data.size()-1) end =data.size()-1;

		boolean isLong = false;
		if (valueTP>=data.get(begin).getOpen5()){
			isLong = true;
		}
		for (int i = begin;i<=end;i++){
			QuoteShort q = data.get(i);
			if (debug)
				System.out.println("[getMaxMinShortTP] "+valueTP+" "+isLong+" "+q.toString());
			if (q.getHigh5()>actualMax){
				actualMax = q.getHigh5();
			}
			if (q.getLow5()<actualMin){
				actualMin = q.getLow5();
			}
			
			if (isLong){
				if (q.getHigh5()>=valueTP){
					actualClose = valueTP;
					QuoteShort.getCalendar(cal, q);
					//System.out.println(q.toString());
					break;
				}
			}else{
				if (q.getLow5()<=valueTP){
					actualClose = valueTP;
					QuoteShort.getCalendar(cal, q);
					break;
				}
			}
			actualClose = q.getClose5();
			QuoteShort.getCalendar(cal, q);
		}
		
		//System.out.println(data.get(begin).toString());
		
		qm.setCal(cal);
		qm.setHigh((short)(actualMax/10));
		qm.setLow((short)(actualMin/10));
		qm.setHigh5(actualMax);
		qm.setLow5(actualMin);
		qm.setClose5(actualClose);
		
		return qm;
	}
	
	public static QuoteShort getMaxMinShortTPSLMaxBars(ArrayList<QuoteShort> data,
			QuoteShort qm,Calendar cal, int begin, int end,
			int valueTP,int valueSL,
			int maxBars,boolean debug) {
		// TODO Auto-generated method stub
		int actualMax = 0;
		int actualMin = 300000;
		int actualClose = 0;
		if (begin<0) begin = 0;
		if (end>data.size()-1) end =data.size()-1;

		boolean isLong = false;
		if (valueTP>=data.get(begin).getOpen5()){
			isLong = true;
		}
		for (int i = begin;i<=end;i++){
			QuoteShort q = data.get(i);
			if (debug)
				System.out.println("[getMaxMinShortTPSL] "+valueTP+" "+isLong+" "+q.toString());
			if (q.getHigh5()>actualMax){
				actualMax = q.getHigh5();
			}
			if (q.getLow5()<actualMin){
				actualMin = q.getLow5();
			}
			
			if (isLong){
				if (q.getLow5()<=valueSL){
					actualClose = valueSL;
					QuoteShort.getCalendar(cal, q);
					//System.out.println(q.toString());
					break;
				}
				if (q.getHigh5()>=valueTP){
					actualClose = valueTP;
					QuoteShort.getCalendar(cal, q);
					//System.out.println(q.toString());
					break;
				}
			}else{
				if (q.getHigh5()>=valueSL){
					actualClose = valueSL;
					QuoteShort.getCalendar(cal, q);
					//System.out.println(q.toString());
					break;
				}
				if (q.getLow5()<=valueTP){
					actualClose = valueTP;
					QuoteShort.getCalendar(cal, q);
					break;
				}
			}
			actualClose = q.getClose5();
			QuoteShort.getCalendar(cal, q);
		}
		
		//System.out.println(data.get(begin).toString());
		
		qm.setCal(cal);
		qm.setHigh((short)(actualMax/10));
		qm.setLow((short)(actualMin/10));
		qm.setHigh5(actualMax);
		qm.setLow5(actualMin);
		qm.setClose5(actualClose);
		
		return qm;
	}
	
	public static double getAccVolume(ArrayList<QuoteShort> data,int begin, int end) {
		
		if (begin<0) begin = 0;
		if (end>data.size()-1) end =data.size()-1;
		
		int totalVol = 0;
		int count = 0;
		for (int i=begin;i<=end;i++){
			totalVol+= data.get(i).getVol();
			count++;
		}
		
		return totalVol*1.0/count;
	}
	
	public static QuoteShort getMaxMinShortTPSL(ArrayList<QuoteShort> data,
			QuoteShort qm,Calendar cal, int begin, int end,int valueTP,int valueSL,boolean debug) {
		// TODO Auto-generated method stub
		int actualMax = 0;
		int actualMin = 300000;
		int actualClose = 0;
		if (begin<0) begin = 0;
		if (end>data.size()-1) end =data.size()-1;

		int isSame = 0;
		int actualResult = 0;
		boolean isLong = false;
		if (valueTP>=data.get(begin).getOpen5()){
			isLong = true;
		}
		for (int i = begin;i<=end;i++){
			QuoteShort q = data.get(i);
			if (q.getHigh5()>actualMax){
				actualMax = q.getHigh5();
			}
			if (q.getLow5()<actualMin){
				actualMin = q.getLow5();
			}
			
			if (isLong){
				if (q.getHigh5()>=valueTP){
					actualClose = valueTP;
					if (q.getOpen5()>=valueTP) actualClose = q.getOpen5();
					QuoteShort.getCalendar(cal, q);
					actualResult = 1;
					//System.out.println(q.toString());
					break;
				}else if (q.getLow5()<=valueSL){
					actualClose = valueSL;
					if (q.getOpen5()<=valueSL) actualClose = q.getOpen5();
					QuoteShort.getCalendar(cal, q);
					actualResult  = -1;
					//System.out.println(q.toString());
					break;
				}
			}else{
				if (q.getLow5()<=valueTP){
					actualClose = valueTP;
					if (q.getOpen5()<=valueTP) actualClose = q.getOpen5();
					QuoteShort.getCalendar(cal, q);
					actualResult = 1;
					break;
				}else if (q.getHigh5()>=valueSL){
					actualClose = valueSL;
					if (q.getOpen5()>=valueSL) actualClose = q.getOpen5();
					QuoteShort.getCalendar(cal, q);
					//System.out.println(q.toString());
					actualResult = -1;
					break;
				}
			}
			actualClose = q.getClose5();
			QuoteShort.getCalendar(cal, q);
		}
		
		//System.out.println(data.get(begin).toString());
		
		qm.setCal(cal);
		qm.setOpen5(actualResult);
		qm.setHigh((short)(actualMax/10));
		qm.setLow((short)(actualMin/10));
		qm.setHigh5(actualMax);
		qm.setLow5(actualMin);
		qm.setClose5(actualClose);
		qm.setExtra(isSame);
		
		return qm;
	}
	
	//Se cierran posiciones al terminar el dia
	public static QuoteShort getMaxMinShortEntryTPSLClose(ArrayList<QuoteShort> data,
			QuoteShort qm,Calendar cal, int begin, int end,
			int entry,int valueTP,int valueSL,boolean debug) {
		// TODO Auto-generated method stub
		int actualMax = 0;
		int actualMin = 300000;
		int actualClose = 0;
		if (begin<0) begin = 0;
		if (end>data.size()-1) end =data.size()-1;

		int isSame = 0;
		int actualResult = 0;
		
		boolean isLong = false;
		if (valueTP>=entry){
			isLong = true;
		}

		Calendar cal1 = Calendar.getInstance();
		QuoteShort.getCalendar(cal1, data.get(begin));
		int day = cal1.get(Calendar.DAY_OF_YEAR);
		for (int i = begin;i<=end;i++){
			QuoteShort q = data.get(i);
			QuoteShort.getCalendar(cal1, q);
			int actualDay = cal.get(Calendar.DAY_OF_YEAR);
			int h = cal1.get(Calendar.HOUR_OF_DAY);
			int min = cal1.get(Calendar.MINUTE);
			
			if (h==0 && min<=10) continue;//el broker esta cerrado a esas horas
			
			if (q.getHigh5()>actualMax){
				actualMax = q.getHigh5();
			}
			if (q.getLow5()<actualMin){
				actualMin = q.getLow5();
			}
			
			if (isLong){
				if (q.getHigh5()>=valueTP){
					actualClose = valueTP;
					//if (q.getOpen5()>=valueTP) actualClose = q.getOpen5();
					if (q.getOpen5()>=valueTP) actualClose = valueTP;
					QuoteShort.getCalendar(cal, q);
					actualResult = 1;
					//System.out.println(q.toString());
					break;
				}else if (q.getLow5()<=valueSL){
					actualClose = valueSL;
					//if (q.getOpen5()<=valueSL) actualClose = q.getOpen5();
					if (q.getOpen5()<=valueSL) actualClose = valueSL;
					QuoteShort.getCalendar(cal, q);
					actualResult  = -1;
					//System.out.println(q.toString());
					break;
				}else if (actualDay>day){
					actualClose = q.getOpen5();
					QuoteShort.getCalendar(cal, q);
					//System.out.println(q.toString());
					break;
				}
			}else{
				if (q.getLow5()<=valueTP){
					actualClose = valueTP;
					//if (q.getOpen5()<=valueTP) actualClose = q.getOpen5();
					if (q.getOpen5()<=valueTP) actualClose = valueTP;
					QuoteShort.getCalendar(cal, q);
					actualResult = 1;
					break;
				}else if (q.getHigh5()>=valueSL){
					actualClose = valueSL;
					//if (q.getOpen5()>=valueSL) actualClose = q.getOpen5();
					if (q.getOpen5()>=valueSL) actualClose = valueSL;
					QuoteShort.getCalendar(cal, q);
					//System.out.println(q.toString());
					actualResult = -1;
					break;
				}else if (actualDay>day){
					actualClose = q.getOpen5();
					QuoteShort.getCalendar(cal, q);
					//System.out.println(q.toString());
					break;
				}
			}
			actualClose = q.getClose5();
			QuoteShort.getCalendar(cal, q);
		}
		
		//System.out.println(data.get(begin).toString());
		
		qm.setCal(cal);
		qm.setOpen5(actualResult);
		qm.setHigh((short)(actualMax/10));
		qm.setLow((short)(actualMin/10));
		qm.setHigh5(actualMax);
		qm.setLow5(actualMin);
		qm.setClose5(actualClose);
		qm.setExtra(isSame);
		
		return qm;
	}
	
	public static QuoteShort getMaxMinShortEntryTPSL(ArrayList<QuoteShort> data,
			QuoteShort qm,Calendar cal, int begin, int end,
			int entry,int valueTP,int valueSL,boolean debug) {
		// TODO Auto-generated method stub
		int actualMax = 0;
		int actualMin = 300000;
		int actualClose = 0;
		if (begin<0) begin = 0;
		if (end>data.size()-1) end =data.size()-1;

		int isSame = 0;
		int actualResult = 0;
		boolean isLong = false;
		if (valueTP>=entry){
			isLong = true;
		}

		Calendar cal1 = Calendar.getInstance();
		int iend=begin;
		for (int i = begin;i<=end;i++){
			QuoteShort q = data.get(i);
			QuoteShort.getCalendar(cal1, q);
			int h = cal1.get(Calendar.HOUR_OF_DAY);
			int min = cal1.get(Calendar.MINUTE);
			
			if (h==0 && min<=10) continue;//el broker esta cerrado a esas horas
			
			if (q.getHigh5()>actualMax){
				actualMax = q.getHigh5();
			}
			if (q.getLow5()<actualMin){
				actualMin = q.getLow5();
			}
			
			if (isLong){
				if (q.getHigh5()>=valueTP){
					actualClose = valueTP;
					//if (q.getOpen5()>=valueTP) actualClose = q.getOpen5();
					if (q.getOpen5()>=valueTP) actualClose = valueTP;
					QuoteShort.getCalendar(cal, q);
					actualResult = 1;
					iend=i;
					//System.out.println(q.toString());
					break;
				}else if (q.getLow5()<=valueSL){
					actualClose = valueSL;
					//if (q.getOpen5()<=valueSL) actualClose = q.getOpen5();
					if (q.getOpen5()<=valueSL) actualClose = valueSL;
					QuoteShort.getCalendar(cal, q);
					actualResult  = -1;
					iend=i;
					//System.out.println(q.toString());
					break;
				}
			}else{
				if (q.getLow5()<=valueTP){
					actualClose = valueTP;
					//if (q.getOpen5()<=valueTP) actualClose = q.getOpen5();
					if (q.getOpen5()<=valueTP) actualClose = valueTP;
					QuoteShort.getCalendar(cal, q);
					actualResult = 1;
					iend=i;
					break;
				}else if (q.getHigh5()>=valueSL){
					actualClose = valueSL;
					//if (q.getOpen5()>=valueSL) actualClose = q.getOpen5();
					if (q.getOpen5()>=valueSL) actualClose = valueSL;
					QuoteShort.getCalendar(cal, q);
					//System.out.println(q.toString());
					actualResult = -1;
					iend=i;
					break;
				}
			}
			actualClose = q.getClose5();
			QuoteShort.getCalendar(cal, q);
		}
		
		//System.out.println(data.get(begin).toString());
		
		qm.setCal(cal);
		qm.setOpen5(actualResult);
		qm.setHigh((short)(actualMax/10));
		qm.setLow((short)(actualMin/10));
		qm.setHigh5(actualMax);
		qm.setLow5(actualMin);
		qm.setClose5(actualClose);
		qm.setExtra(iend);
		
		return qm;
	}
	
	
	public static int getMaxMinShortTPSLIndex(ArrayList<QuoteShort> data,
			QuoteShort qm,Calendar cal, int begin, int end,int valueTP,int valueSL,boolean debug) {
		// TODO Auto-generated method stub
		
		
		int actualMax = 0;
		int actualMin = 300000;
		int actualClose = 0;
		if (begin<0) begin = 0;
		if (end>data.size()-1) end =data.size()-1;

		int index = begin;
		int isSame = 0;
		int actualResult = 0;
		boolean isLong = false;
		if (valueTP>=data.get(begin).getOpen5()){
			isLong = true;
		}
		for (int i = begin;i<=end;i++){
			QuoteShort q = data.get(i);
			if (q.getHigh5()>actualMax){
				actualMax = q.getHigh5();
			}
			if (q.getLow5()<actualMin){
				actualMin = q.getLow5();
			}
			
			if (isLong){
				if (q.getHigh5()>=valueTP){
					actualClose = valueTP;
					if (q.getOpen5()>=valueTP) actualClose = q.getOpen5();
					QuoteShort.getCalendar(cal, q);
					actualResult = 1;
					index = i;
					//System.out.println(q.toString());
					break;
				}else if (q.getLow5()<=valueSL){
					actualClose = valueSL;
					if (q.getOpen5()<=valueSL) actualClose = q.getOpen5();
					QuoteShort.getCalendar(cal, q);
					actualResult  = -1;
					index = i;
					//System.out.println(q.toString());
					break;
				}
			}else{
				if (q.getLow5()<=valueTP){
					actualClose = valueTP;
					if (q.getOpen5()<=valueTP) actualClose = q.getOpen5();
					QuoteShort.getCalendar(cal, q);
					actualResult = 1;
					index = i;
					break;
				}else if (q.getHigh5()>=valueSL){
					actualClose = valueSL;
					if (q.getOpen5()>=valueSL) actualClose = q.getOpen5();
					QuoteShort.getCalendar(cal, q);
					//System.out.println(q.toString());
					actualResult = -1;
					index = i;
					break;
				}
			}
			actualClose = q.getClose5();
			QuoteShort.getCalendar(cal, q);
			index = i;
		}
		
		//System.out.println(data.get(begin).toString());
		
		qm.setCal(cal);
		qm.setOpen5(actualResult);
		qm.setHigh((short)(actualMax/10));
		qm.setLow((short)(actualMin/10));
		qm.setHigh5(actualMax);
		qm.setLow5(actualMin);
		qm.setClose5(actualClose);
		qm.setExtra(isSame);
		
		return index;
	}
	
	public static QuoteShort getMaxMinShortLimitSL(ArrayList<QuoteShort> data, int begin, int end,int limitSL,boolean isLong) {
		// TODO Auto-generated method stub
		int actualMax = 0;
		int actualMin = 300000;
		int actualClose = 0;
		if (begin<0) begin = 0;
		if (end>data.size()-1) end =data.size()-1;
		//System.out.println(begin+" "+end);
		int lastIndex = begin;
		int openValue = data.get(begin).getOpen5();
		int win = 0;
		for (int i = begin;i<=end;i++){
			lastIndex =i;
			QuoteShort q = data.get(i);
			if (q.getHigh5()>actualMax){
				actualMax = q.getHigh5();
			}
			if (q.getLow5()<actualMin){
				actualMin = q.getLow5();
			}
			actualClose = q.getClose5();
			
			if (isLong){
				int diffPips = openValue-q.getLow5();
				if (diffPips*0.1>=limitSL){
					win = -1;
					break;
				}
			}else{
				int diffPips = q.getHigh5()-openValue;
				if (diffPips*0.1>=limitSL){
					win = -1;
					break;
				}
			}
		}
		QuoteShort q = new QuoteShort();
		q.setOpen5(win);
		q.setHigh((short)(actualMax/10));
		q.setLow((short)(actualMin/10));
		q.setHigh5(actualMax);
		q.setLow5(actualMin);
		q.setClose5(actualClose);
		q.setExtra(lastIndex);
		return q;
	}
	
	public static QuoteShort getMaxMinShortLimitSLTP(ArrayList<QuoteShort> data, int begin, int end,int limitSL,int limitTP,boolean isLong) {
		// TODO Auto-generated method stub
		int actualMax = 0;
		int actualMin = 300000;
		int actualClose = 0;
		if (begin<0) begin = 0;
		if (end>data.size()-1) end =data.size()-1;
		//System.out.println(begin+" "+end);
		int openValue = data.get(begin).getOpen5();
		int win = 0;
		int openIdx=begin;
		int lastIdx = begin;
		for (int i = begin;i<=end;i++){
			QuoteShort q = data.get(i);
			if (q.getHigh5()>actualMax){
				actualMax = q.getHigh5();
			}
			if (q.getLow5()<actualMin){
				actualMin = q.getLow5();
			}
			actualClose = q.getClose5();
			int diffOC = Math.abs(openValue-actualClose);
			
			if (isLong){
				int diffPipsL = openValue-q.getLow5();
				int diffPipsH = q.getHigh5()-openValue;
				
				if (diffPipsL*0.1>=limitSL){
					win=-1;
					lastIdx = i;
					break;
				}
				if (i>openIdx && diffPipsH*0.1>=limitTP){
					lastIdx = i;
					win=1;
					break;
				}
			}else{//short
				int diffPipsL = openValue-q.getLow5();
				int diffPipsH = q.getHigh5()-openValue;
				
			
				if (i>openIdx && diffPipsL*0.1>=limitTP){
					lastIdx = i;
					win=1;
					break;
				}
				if (diffPipsH*0.1>=limitSL){
					lastIdx = i;
					win=-1;
					break;
				}
			}
			lastIdx = i;
		}
		QuoteShort q = new QuoteShort();
		q.setHigh((short)(actualMax/10));
		q.setLow((short)(actualMin/10));
		q.setHigh5(actualMax);
		q.setLow5(actualMin);
		q.setClose5(actualClose);
		q.setExtra(win);
		q.setOpen5(lastIdx);
		
		return q;
	}
	
	public static QuoteShort getMaxMinShortLimitSLReversal(ArrayList<QuoteShort> data, 
			ArrayList<QuoteShort> maxMins,
			int begin, int end,int limitSL,
			int limitThr,
			boolean isLong) {
		// TODO Auto-generated method stub
		int actualMax = 0;
		int actualMin = 300000;
		int actualClose = 0;
		if (begin<0) begin = 0;
		if (end>data.size()-1) end =data.size()-1;
		//System.out.println(begin+" "+end);
		int openValue = data.get(begin).getOpen5();
		for (int i = begin;i<=end;i++){
			QuoteShort q = data.get(i);
			QuoteShort mm = maxMins.get(i);
			if (q.getHigh5()>actualMax){
				actualMax = q.getHigh5();
			}
			if (q.getLow5()<actualMin){
				actualMin = q.getLow5();
			}
			actualClose = q.getClose5();
			
			if (isLong){
				int diffPips = openValue-q.getClose5();
				if (diffPips*0.1>=limitSL){
					break;
				}
				if (limitThr!=-1 && mm.getExtra()<=-limitThr) break;
			}else{
				int diffPips = q.getClose5()-openValue;
				if (diffPips*0.1>=limitSL){
					break;
				}
				if (limitThr!=-1 && mm.getExtra()>=limitThr) break;
			}
		}
		QuoteShort q = new QuoteShort();
		q.setHigh((short)(actualMax/10));
		q.setLow((short)(actualMin/10));
		q.setHigh5(actualMax);
		q.setLow5(actualMin);
		q.setClose5(actualClose);
		
		return q;
	}


	public static int findQuote(ArrayList<Quote> data, Calendar calFind,int index) {
		// TODO Auto-generated method stub
		Calendar cal = Calendar.getInstance();
		for (int i=index;i<data.size();i++){
			Quote q = data.get(i);
			cal.setTimeInMillis(q.getDate().getTime());
			
			if (DateUtils.isSameDay(cal, calFind)) return i;
		}
		return -1;
		
	}
	
	public static int findQuoteShort(ArrayList<QuoteShort> data, Calendar calFind,int index) {
		// TODO Auto-generated method stub
		Calendar cal = Calendar.getInstance();
		for (int i=index;i<data.size();i++){
			QuoteShort q = data.get(i);
			QuoteShort.getCalendar(cal, q);
			if (DateUtils.isSameDay(cal, calFind)) return i;
		}
		return -1;
		
	}
	
	public static int findQuoteShortMinute(ArrayList<QuoteShort> data, Calendar calFind,int index) {
		// TODO Auto-generated method stub
		Calendar cal = Calendar.getInstance();
		for (int i=index;i<data.size();i++){
			QuoteShort q = data.get(i);
			QuoteShort.getCalendar(cal, q);
			if (DateUtils.isSameDay(cal, calFind)){
				return i;
			}
		}
		return -1;
		
	}


	public static ArrayList<QuoteShort> cleanWeekendDataShort(
			ArrayList<QuoteShort> dataS) {
		// TODO Auto-generated method stub
		ArrayList<QuoteShort> data = new ArrayList<QuoteShort>();
		
		Calendar cal = Calendar.getInstance();
		for (int i=0;i<dataS.size();i++){
			QuoteShort q = dataS.get(i);
			QuoteShort.getCalendar(cal,q);
			if (cal.get(Calendar.DAY_OF_WEEK)==Calendar.SATURDAY || cal.get(Calendar.DAY_OF_WEEK)==Calendar.SUNDAY)
				continue;
			QuoteShort qNew = new QuoteShort();
			qNew.copy(q);
			data.add(q);
		}
		return data;
	}
	
	/**
	 * Calcula el maximo de barras superadas por q
	 * @param data
	 * @return
	 */
	public static ArrayList<QuoteShort> calculatePeaks(
			ArrayList<QuoteShort> data,int thr,boolean debug) {
		
		int lastValue = 0;
		int actualValue = 0;
		Calendar cal = Calendar.getInstance();
		ArrayList<QuoteShort> peaks= new ArrayList<QuoteShort>();
		for (int i=0;i<data.size();i++){
			QuoteShort q = data.get(i);
			QuoteShort.getCalendar(cal, q);
			QuoteShort peak= new QuoteShort();
			peak.setExtra(0);

			
			int end = i-thr;
			if (end<0) end = 0;
			boolean isHigh = false;
			boolean isLow = false;
			for (int j=i-1;j>=end && i>=thr;j--){
				QuoteShort qj = data.get(j);
								
				if (q.getHigh5()>qj.getHigh5()){
					if (isLow){ //era un minimo
						actualValue = lastValue;
						break;
					}
					actualValue = 1;
					isHigh = true;
				}
				if (q.getLow5()<qj.getLow5()){
					if (isHigh){
						actualValue = lastValue;
						break;
					}
					actualValue = -1;
					isLow = true;
				}
								
				if (!isHigh && !isLow){
					actualValue = lastValue;
					break;
				}
			}
			if (actualValue!=lastValue && actualValue!=0){
				lastValue = actualValue;
				if (debug)
					System.out.println("new peak: "+lastValue+" "+DateUtils.datePrint(cal));
			}
			
			peak.setExtra(actualValue);
			peaks.add(peak);
		}
		return peaks;
	}

	/**
	 * Calcula el maximo de barras superadas por q
	 * @param data
	 * @return
	 */
	public static ArrayList<QuoteShort> calculateMaxMinByBarShortAbsolute(
			ArrayList<QuoteShort> data) {
		
		int count5000 = 0;
		ArrayList<QuoteShort> maxMins = new ArrayList<QuoteShort>();
		for (int i=0;i<data.size();i++){
			QuoteShort q = data.get(i);
			QuoteShort maxMin = new QuoteShort();
			maxMin.setExtra(0);
			int modeH = 0;
			int modeL = 0;
			int nbarsH = 0;
			int nbarsL = 0;
			if (i%20000==0){
				//System.out.println("calculados "+i);
			}
			//QuoteShort qmax = TradingUtils.getMaxMinShort(data, i-170000, i-1);
			for (int j=i-1;j>=0;j--){
				QuoteShort qj = data.get(j);
				boolean isHigh = false;
				boolean isLow = false;
				
				if (q.getHigh5()>qj.getHigh5()){
					isHigh = true;
				}
				if (q.getLow5()<qj.getLow5()){
					isLow = true;
				}
				
				//System.out.println();
				
				if (modeH==0 || modeH==1){
					if (isHigh){
						modeH=1;
						nbarsH++;
					}else{
						modeH=-1;
					}
				}
				if (modeL==0 || modeL==1){
					if (isLow){
						modeL=1;
						nbarsL++;
					}else{
						modeL=-1;
					}
				}
				
				if (!isHigh) modeH = -1;
				if (!isLow)  modeL = -1;
				
				if (!isHigh && !isLow) break;
				if (modeH==-1 && modeL==-1) break;
			}
			if (Math.abs(nbarsH)>=5000 || Math.abs(nbarsL)>=5000){
				count5000++;
				//System.out.println("añadiendo nbars: "+nbarsH+" "+nbarsL+" "+count5000);
			}
			if (nbarsH>=nbarsL) maxMin.setExtra(nbarsH);
			if (nbarsH<nbarsL) maxMin.setExtra(-nbarsL);
			maxMins.add(maxMin);
		}
		return maxMins;
	}
	
	public static ArrayList<Double> calculateTrendingIndex(ArrayList<QuoteShort> data,int minSize,ArrayList<TrendClass> trends ){
		
		ArrayList<Double> trendsIndex = new ArrayList<Double>();
		
		int mode = 0;
		int index1 = 0;
		int index2 = 0;
		int lastDay = -1;
		//ArrayList<TrendClass> trends = new ArrayList<TrendClass>();
		Calendar cal = Calendar.getInstance();
		Calendar cal1 = Calendar.getInstance();
		QuoteShort.getCalendar(cal1, data.get(0));
		for (int i=0;i<data.size();i++){
			QuoteShort q = data.get(i);
			QuoteShort.getCalendar(cal, q);
			int h = cal.get(Calendar.HOUR_OF_DAY);
			int day = cal.get(Calendar.DAY_OF_YEAR);
			if (day!=lastDay){
				
				lastDay = day;
			}
			
			int actualSizeH1 = q.getClose5()-data.get(index1).getClose5();
			int actualSizeL1 = data.get(index1).getClose5()-q.getClose5();
			int actualSizeH2 = q.getClose5()-data.get(index2).getClose5();
			int actualSizeL2 = data.get(index2).getClose5()-q.getClose5();
			
			if (mode==0){
				if (actualSizeH1>=minSize){
					index2=i;
					mode=1;
					
					trendsIndex.add(actualSizeH1*1.0/minSize);
				}else if (actualSizeL1>=minSize){
					index2=i;
					mode=-1;
					
					trendsIndex.add(-actualSizeL1*1.0/minSize);
				}else{
					trendsIndex.add(0.0);
				}
			}else if (mode==1){
				if (actualSizeL2>=minSize){
					//guardar trends
					int size = data.get(index2).getClose5()-data.get(index1).getClose5();
					//if (h<=9)
					
					TrendClass tsize = new TrendClass();					
					tsize.setSize(size);
					//tsize.getCal().setTimeInMillis(cal1.getTimeInMillis());
					trends.add(tsize);
					
					mode=-1;
					index1 = index2;
					index2 = i;
					QuoteShort.getCalendar(cal1, data.get(index1));
					
					trendsIndex.add((data.get(index2).getClose5()-data.get(index1).getClose5())*1.0/minSize);
				}else if (q.getClose5()>=data.get(index2).getClose5()){
					index2 = i;
					
					trendsIndex.add(actualSizeH1*1.0/minSize);
				}
			}else if (mode==-1){
				if (actualSizeH2>=minSize){
					//guardar trends
					int size = data.get(index1).getClose5()-data.get(index2).getClose5();
					//if (h<=9)
					
					TrendClass tsize = new TrendClass();					
					tsize.setSize(size);
					//tsize.getCal().setTimeInMillis(cal1.getTimeInMillis());
					trends.add(tsize);
					
					mode=1;
					index1 = index2;
					index2 = i;
					QuoteShort.getCalendar(cal1, data.get(index1));
					
					trendsIndex.add((data.get(index2).getClose5()-data.get(index1).getClose5())*1.0/minSize);
				}else if (q.getClose5()<=data.get(index2).getClose5()){
					index2 = i;
					
					trendsIndex.add(actualSizeL1*1.0/minSize);
				}
			}
			
			trendsIndex.add((data.get(i).getClose5()-data.get(index1).getClose5())*1.0/minSize);
			double f = (data.get(i).getClose5()-data.get(index1).getClose5())*1.0/minSize;
			/*System.out.println(DateUtils.datePrint(cal)
					+" "+PrintUtils.Print2dec(f, false)
					+" || "+q.toString()
					);*/
		}

		return trendsIndex;
	}
	
public static ArrayList<TrendClass> calculateTrends(ArrayList<QuoteShort> data,int minSize){
		
		//ArrayList<Double> trendsIndex = new ArrayList<Double>();
		
		int wins = 0;
		int losses = 0;
		int winPips = 0;
		int lostPips = 0;
		int maxTP = 200;
		int mode = 0;
		int index1 = 0;
		int index2 = 0;
		int index3 = 0;
		int lastDay = -1;
		int entry = 0;
		ArrayList<TrendClass> trends = new ArrayList<TrendClass>();
		Calendar cal = Calendar.getInstance();
		Calendar cal1 = Calendar.getInstance();
		Calendar cale = Calendar.getInstance();
		QuoteShort.getCalendar(cal1, data.get(0));
		for (int i=0;i<data.size();i++){
			QuoteShort q = data.get(i);
			QuoteShort.getCalendar(cal, q);
			int h = cal.get(Calendar.HOUR_OF_DAY);
			int day = cal.get(Calendar.DAY_OF_YEAR);
			if (day!=lastDay){
				
				lastDay = day;
			}
			
			int actualSizeH1 = q.getClose5()-data.get(index1).getClose5();
			int actualSizeL1 = data.get(index1).getClose5()-q.getClose5();
			int actualSizeH2 = q.getClose5()-data.get(index2).getClose5();
			int actualSizeL2 = data.get(index2).getClose5()-q.getClose5();
			
			if (mode==0){
				if (actualSizeH1>=minSize){
					index2=i;
					index3=i;
					mode=1;
					entry = i;
					//trendsIndex.add(actualSizeH1*1.0/minSize);
				}else if (actualSizeL1>=minSize){
					index2=i;
					index3=i;
					mode=-1;
					entry = i;
					
					//trendsIndex.add(-actualSizeL1*1.0/minSize);
				}else{
					//trendsIndex.add(0.0);
				}
			}else if (mode==1){
				if (actualSizeL2>=minSize){
					//guardar trends
					int size = data.get(index2).getClose5()-data.get(index1).getClose5();
					int sizeC = q.getClose5()-data.get(index1).getClose5();
					
					//if (h<=9)
					
					if (entry>=0) {
						QuoteShort.getCalendar(cale, data.get(entry));
						int he = cale.get(Calendar.HOUR_OF_DAY);
						int sizeR = q.getClose5()-data.get(entry).getClose5();
						if (he<=5) {
							int pips = sizeR;
							if (pips>=0) {
								wins++;
								winPips += pips;
							}else {
								losses++;
								lostPips += -pips;
							}
						}
					}
					
					TrendClass tsize = new TrendClass();					
					tsize.setSize(size);
					tsize.setSizeClose(sizeC);
					tsize.setMillisIndex1(cal1.getTimeInMillis());
					tsize.setMillisIndex2(cal.getTimeInMillis());
					tsize.setIndex1(index1);
					tsize.setIndexC(i);
					tsize.setIndex3(index3);
					tsize.setMode(mode);
					
					trends.add(tsize);
					
					mode=-1;
					index1 = index2;
					index2 = i;
					index3 = i;
					QuoteShort.getCalendar(cal1, data.get(index1));
					
					
					//trendsIndex.add((data.get(index2).getClose5()-data.get(index1).getClose5())*1.0/minSize);
				}else if (q.getClose5()>=data.get(index2).getClose5()){
					index2 = i;
					
					if (entry>=0) {
						int actualSize = data.get(index2).getClose5()-data.get(entry).getClose5();
						QuoteShort.getCalendar(cale, data.get(entry));
						int he = cale.get(Calendar.HOUR_OF_DAY);
						if (actualSize>=maxTP && he<5){
							wins++;
							winPips += actualSize;
							entry = -1;
						}
					}
				}
			}else if (mode==-1){
				if (actualSizeH2>=minSize){
					//guardar trends
					int size = data.get(index1).getClose5()-data.get(index2).getClose5();
					int sizeC = data.get(index1).getClose5()-q.getClose5();
					//if (h<=9)
					
					TrendClass tsize = new TrendClass();	
					tsize.setSize(size);
					tsize.setSizeClose(-size);
					tsize.setSizeClose(sizeC);
					tsize.setMillisIndex1(cal1.getTimeInMillis());
					tsize.setMillisIndex2(cal.getTimeInMillis());
					tsize.setIndex1(index1);
					tsize.setIndex3(index3);
					tsize.setIndexC(i);
					tsize.setMode(mode);
					//tsize.getCal().setTimeInMillis(cal1.getTimeInMillis());
					trends.add(tsize);
					
					mode=1;
					index1 = index2;
					index2 = i;
					index3 = i;
					QuoteShort.getCalendar(cal1, data.get(index1));
					
					//trendsIndex.add((data.get(index2).getClose5()-data.get(index1).getClose5())*1.0/minSize);
					entry = i;
				}else if (q.getClose5()<=data.get(index2).getClose5()){
					index2 = i;
					
					//trendsIndex.add(actualSizeL1*1.0/minSize);
				}
			}
			double f = (data.get(i).getClose5()-data.get(index1).getClose5())*1.0/minSize;
			/*System.out.println(DateUtils.datePrint(cal)
					+" "+PrintUtils.Print2dec(f, false)
					+" || "+q.toString()
					);*/
		}
		
		int trades = wins+losses;
		double winPer = wins*100.0/trades;
		double pf = winPips*1.0/lostPips;
		/*System.out.println(trades
				+" "+PrintUtils.Print2dec(winPer, false)
				+" "+PrintUtils.Print2dec(pf, false)
		);*/

		return trends;
	}

public static void calculateTrendsHL2(ArrayList<QuoteShort> data,
		int minSize,ArrayList<TrendInfo> dataTrend){
	
	int mode = 0;
	int index1 = 0;
	int index2 = 0;
	int index3 = 0;
	int lastDay = -1;
	Calendar cal = Calendar.getInstance();
	Calendar cal1 = Calendar.getInstance();
	Calendar cal2 = Calendar.getInstance();
	Calendar cal3 = Calendar.getInstance();
	QuoteShort.getCalendar(cal1, data.get(0));
	
	int actualExtension = 0;
	int maxExtension = 0;
	for (int i=0;i<data.size();i++){
		QuoteShort q = data.get(i);
		QuoteShort.getCalendar(cal, q);
		//System.out.println(q.toString());
		int h = cal.get(Calendar.HOUR_OF_DAY);
		int day = cal.get(Calendar.DAY_OF_YEAR);
		if (day!=lastDay){
			
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
				
				actualExtension = q.getClose5()-data.get(index1).getLow5();
				maxExtension = actualSizeH1;
			}else if (actualSizeL1>=minSize){
				index2=i;
				mode=-1;
				actualExtension =  data.get(index1).getHigh5()-q.getClose5();
				maxExtension = actualSizeL1;
			}else{
				//trendsIndex.add(0.0);
			}
		}else if (mode==1){
			//actualExtension
			actualExtension = q.getClose5()-data.get(index1).getLow5();
			if (actualSizeL2>=minSize){											
				mode=-1;				
				actualExtension =  data.get(index2).getHigh5()-q.getClose5();
				maxExtension = actualSizeL2;
				
				index1 = index2;
				index2 = i;
				index3 = i;//definición de trend
				QuoteShort.getCalendar(cal1, data.get(index1));				
			}else if (q.getHigh5()>=data.get(index2).getHigh5()){
				index2 = i;				
				maxExtension = actualSizeH1;
			}
		}else if (mode==-1){
			actualExtension = data.get(index1).getHigh5()-q.getClose5();
			if (actualSizeH2>=minSize){
				mode=1;
				actualExtension =  q.getClose5()-data.get(index2).getLow5();
				maxExtension = actualSizeH2;
				
				index1 = index2;
				index2 = i;
				index3 = i;//definición de trend
				QuoteShort.getCalendar(cal1, data.get(index1));				
			}else if (q.getLow5()<=data.get(index2).getLow5()){
				index2 = i;
				maxExtension = actualSizeL1;
			}
		}
		//añadimos información a salida
		TrendInfo ti = new TrendInfo();
		ti.setLeg(mode);
		ti.setActualExtensionClose(actualExtension);
		ti.setMaxExtension(maxExtension);
		dataTrend.add(ti);
	}

}

public static ArrayList<TrendClass> calculateTrendsHL(ArrayList<QuoteShort> data,
		int minSize,
		ArrayList<Double> trendsIndex
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
	for (int i=0;i<data.size();i++){
		QuoteShort q = data.get(i);
		QuoteShort.getCalendar(cal, q);
		//System.out.println(q.toString());
		int h = cal.get(Calendar.HOUR_OF_DAY);
		int day = cal.get(Calendar.DAY_OF_YEAR);
		if (day!=lastDay){
			
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
				
				
				mode=-1;
				index1 = index2;
				index2 = i;
				index3 = i;//definición de trend
				QuoteShort.getCalendar(cal1, data.get(index1));
				
				actualTrendIndex = (data.get(index1).getHigh5()-q.getClose5())*1.0/minSize;
				//trendsIndex.add((data.get(index2).getClose5()-data.get(index1).getClose5())*1.0/minSize);
				System.out.println(PrintUtils.Print2dec(size*0.1, false));
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
				
				mode=1;
				index1 = index2;
				index2 = i;
				index3 = i;//definición de trend
				QuoteShort.getCalendar(cal1, data.get(index1));
				
				actualTrendIndex = (q.getClose5()-data.get(index1).getLow5())*1.0/minSize;
				
				//trendsIndex.add((data.get(index2).getClose5()-data.get(index1).getClose5())*1.0/minSize);
				System.out.println(PrintUtils.Print2dec(size*0.1, false));
			}else if (q.getLow5()<=data.get(index2).getLow5()){
				index2 = i;
				actualTrendIndex = (data.get(index1).getHigh5()-q.getClose5())*1.0/minSize;
				//trendsIndex.add(actualSizeL1*1.0/minSize);
			}
		}
		trendsIndex.add(actualTrendIndex);
	}

	return trends;
}


public static ArrayList<TrendClass> calculateTrendsHLDay(ArrayList<QuoteShort> data,
		int minSize,
		ArrayList<Integer> lastZZ,
		int debug
		){
	
	//ArrayList<Double> trendsIndex = new ArrayList<Double>();
	
	int mode = 0;
	int index1 = 0;
	int index2 = 0;
	int lastDay = -1;
	ArrayList<TrendClass> trends = new ArrayList<TrendClass>();
	Calendar cal = Calendar.getInstance();
	Calendar cal1 = Calendar.getInstance();
	Calendar cal2 = Calendar.getInstance();
	int lastZZValue = -1;
	for (int i=0;i<data.size();i++){
		QuoteShort q = data.get(i);
		QuoteShort.getCalendar(cal, q);
		//System.out.println(q.toString());
		int h = cal.get(Calendar.HOUR_OF_DAY);
		int day = cal.get(Calendar.DAY_OF_YEAR);
		if (day!=lastDay){
			index1 = 0;
			index2 = 0;
			lastZZValue = -1;
			mode = 0;
			lastDay = day;
		}
		
		int actualSizeH1 = q.getHigh5()-data.get(index1).getLow5();
		int actualSizeL1 = data.get(index1).getHigh5()-q.getLow5();
		int actualSizeH2 = q.getHigh5()-data.get(index2).getLow5();
		int actualSizeL2 = data.get(index2).getHigh5()-q.getLow5();
		
		double actualTrendIndex = 0;
		if (mode==0){
			if (actualSizeH1>=minSize){
				index2 =i;
				mode =1;				
				//trendsIndex.add(actualSizeH1*1.0/minSize);
				actualTrendIndex = (q.getClose5()-data.get(index1).getLow5())*1.0/minSize;
				lastZZValue = q.getHigh5();
				if (debug==1)
				System.out.println("[NEW HIGH LEG] "+lastZZValue+" "+q.toString());
			}else if (actualSizeL1>=minSize){
				index2 =i;
				mode =-1;				
				//trendsIndex.add(-actualSizeL1*1.0/minSize);
				actualTrendIndex = (data.get(index1).getHigh5()-q.getClose5())*1.0/minSize;
				lastZZValue = -q.getLow5();
				if (debug==1)
				System.out.println("[NEW LOW LEG] "+lastZZValue+" "+q.toString());
			}else{
				//trendsIndex.add(0.0);
			}
		}else if (mode==1){
			if (actualSizeL2>=minSize){
				//guardar trends
				int size = data.get(index2).getHigh5()-data.get(index1).getLow5();
				//if (h<=9)
				
				TrendClass tsize = new TrendClass();					
				tsize.setSize(size);
				tsize.setMillisIndex1(cal1.getTimeInMillis());
				tsize.setMillisIndex1(cal.getTimeInMillis());
				trends.add(tsize);
				
				mode=-1;
				index1 = index2;
				index2 = i;
				QuoteShort.getCalendar(cal1, data.get(index1));
				
				actualTrendIndex = (data.get(index1).getHigh5()-q.getClose5())*1.0/minSize;
				//trendsIndex.add((data.get(index2).getClose5()-data.get(index1).getClose5())*1.0/minSize);
				
				//nuevo semaforo
				lastZZValue = -q.getLow5();
				if (debug==1)
				System.out.println("[NEW LOW LEG] "+lastZZValue+" "+q.toString());
			}else if (q.getHigh5()>=data.get(index2).getHigh5()){
				index2 = i;
				
				actualTrendIndex = (q.getClose5()-data.get(index1).getLow5())*1.0/minSize;
				//actualizamos el semaforo
				lastZZValue = q.getHigh5();
				if (debug==1)
				System.out.println("[***UPDATE HIGH LEG] "+lastZZValue+" "+q.toString());
			}
		}else if (mode==-1){
			if (actualSizeH2>=minSize){
				//guardar trends
				int size = data.get(index1).getHigh5()-data.get(index2).getLow5();
				//if (h<=9)
				
				TrendClass tsize = new TrendClass();					
				tsize.setSize(-size);
				tsize.setMillisIndex1(cal1.getTimeInMillis());
				tsize.setMillisIndex1(cal.getTimeInMillis());
				//tsize.getCal().setTimeInMillis(cal1.getTimeInMillis());
				trends.add(tsize);
				
				mode=1;
				index1 = index2;
				index2 = i;
				QuoteShort.getCalendar(cal1, data.get(index1));
				
				actualTrendIndex = (q.getClose5()-data.get(index1).getLow5())*1.0/minSize;
				
				//nuevo semaforo
				lastZZValue = q.getHigh5();
				if (debug==1)
				System.out.println("[NEW HIGH LEG] "+lastZZValue+" "+q.toString());
			}else if (q.getLow5()<=data.get(index2).getLow5()){
				index2 = i;
				actualTrendIndex = (data.get(index1).getHigh5()-q.getClose5())*1.0/minSize;
				
				//actualizamos el semaforo
				lastZZValue = -q.getLow5();
				if (debug==1)
				System.out.println("[***UPDATE LOW LEG] "+lastZZValue+" "+q.toString());
			}
		}
		lastZZ.add(lastZZValue);
		if (debug==2)
		System.out.println(DateUtils.datePrint(cal)+" "+lastZZValue);
	}

	return trends;
}
	
	public static ArrayList<Integer> calculateMaxMinByBarShortAbsoluteInt(
			ArrayList<QuoteShort> data) {
		
		int count5000 = 0;
		ArrayList<Integer> maxMins = new ArrayList<Integer>();
		for (int i=0;i<data.size();i++){
			QuoteShort q = data.get(i);
			int maxMin = 0;
			//maxMin.setExtra(0);
			int modeH = 0;
			int modeL = 0;
			int nbarsH = 0;
			int nbarsL = 0;
			if (i%20000==0){
				//System.out.println("calculados "+i);
			}
			//El i se toma high y Low
			//para backtest mirar siempre i-1
			for (int j=i-1;j>=0;j--){
				QuoteShort qj = data.get(j);
				boolean isHigh = false;
				boolean isLow = false;
				
				if (q.getHigh5()>qj.getHigh5()){
					isHigh = true;
				}
				if (q.getLow5()<qj.getLow5()){
					isLow = true;
				}
				
				//System.out.println();
				
				if (modeH==0 || modeH==1){
					if (isHigh){
						modeH=1;
						nbarsH++;
					}else{
						modeH=-1;
					}
				}
				if (modeL==0 || modeL==1){
					if (isLow){
						modeL=1;
						nbarsL++;
					}else{
						modeL=-1;
					}
				}
				
				if (!isHigh) modeH = -1;
				if (!isLow)  modeL = -1;
				
				if (!isHigh && !isLow) break;
				if (modeH==-1 && modeL==-1) break;
			}
			if (Math.abs(nbarsH)>=5000 || Math.abs(nbarsL)>=5000){
				count5000++;
				//System.out.println("añadiendo nbars: "+nbarsH+" "+nbarsL+" "+count5000);
			}
			if (nbarsH>=nbarsL) maxMin=nbarsH;
			if (nbarsH<nbarsL) maxMin=-nbarsL;
			maxMins.add(maxMin);
			//System.out.println("añadidoMaxmin para "+i+" || "+q.toString()+" || "+maxMin);
		}
		return maxMins;
	}

	public static ArrayList<Integer> decodeHours(String hours) {

		ArrayList<Integer> hoursThr = new ArrayList<Integer>();
		for (int i=0;i<=23;i++) hoursThr.add(-1);
		String[] hoursStr = hours.split(" ");
		for (int i=0;i<hoursStr.length;i++)
			hoursThr.set(i,Integer.valueOf(hoursStr[i]));
				
		return hoursThr;
	}

	public static int countHighsLows(ArrayList<QuoteShort> data,int begin, int end, boolean isHigh) {

		int total = 0;
		
		if (begin<=0) begin = 0;
		if (end>=data.size()-1) end = data.size()-1;
		
		int max = -1;
		int min = -1;
		for (int i=begin;i<=end;i++){
			QuoteShort q = data.get(i);
			
			if (isHigh){
				if (max==-1 || q.getHigh5()>max){
					total++;
					max = q.getHigh5();
				}
			}else{
				if (min==-1 || q.getLow5()<min){
					total++;
					min = q.getLow5();
				}
			}
		}
		
		return total;
	}

	public static ArrayList<QuoteShort> calculateLastVisitShort(ArrayList<QuoteShort> data) {
		
		ArrayList<QuoteShort> lastVisit = new ArrayList<QuoteShort>();
		
		
		
		return lastVisit;
	}

	public static double getAvgDifference(ArrayList<QuoteShort> data, int begin, int end, int index, 
			boolean isHigh,boolean debug) {
		
		if (debug)
			System.out.println("[getAvgDifference] INITIAL: "+data.get(index).toString());
		int value = data.get(index).getOpen5();
		
		if (begin<=0) begin = 0;
		if (end>=data.size()-1) end = data.size()-1;
		
		int acc = 0;
		int total = 0;
		for (int i=begin;i<=end;i++){
			int diff = value-data.get(i).getHigh5();
			if (!isHigh){
				diff = data.get(i).getLow5()-value;
			}
			if (debug)
				System.out.println("[getAvgDifference] "+isHigh+" "+diff+" "+value+" || "+data.get(i).toString());
			acc+=diff;
			total++;
		}
		
		return acc*1.0/total;
	}

	public static HashMap<Integer, Integer> calculateLastDayValues(ArrayList<QuoteShort> data) {
		
		HashMap<Integer, Integer> hash = new HashMap<Integer, Integer>();
		
		int dayNumber = -1;
		int lastDay = -1;
		int value = -1;
		Calendar cal = Calendar.getInstance();
		for (int i=0;i<data.size();i++){
			QuoteShort q = data.get(i);
			QuoteShort.getCalendar(cal, q);
			int day = cal.get(Calendar.DAY_OF_YEAR);
			
			if (day!=lastDay){				
				if (lastDay!=-1){
					hash.put(dayNumber, value);
				}				
				lastDay = day;
			}
			dayNumber = cal.get(Calendar.YEAR)*365+cal.get(Calendar.DAY_OF_YEAR);
			value = q.getClose5();
		}
		hash.put(dayNumber, value);
		
		return hash;
	}
	
	public static ArrayList<Integer> getMovingAverage(
			ArrayList<QuoteShort> data,int n){
		
		ArrayList<Integer> ma = new ArrayList<Integer>();
		
		
		int acc = 0;
		
		for (int i=0;i<data.size();i++){
			
			int actual = data.get(i).getClose5();
			int firstIdx = i-n;
			if (firstIdx<=0) firstIdx = 0;
			int first = data.get(firstIdx).getClose5();
			
			acc -= first;
			acc += actual;
			int currentAvg = acc/(i-firstIdx+1);
			
			ma.add(currentAvg);
		}
		
		
		return ma;
	}

	public static int getMaxReaction(ArrayList<QuoteShort> data, int begin, int end, int value, int maxPipsDiff, int mode) {
		// TODO Auto-generated method stub
		
		if (begin<=0){
			begin = 0;
		}
		int maxReaction = 0;
		for (int i=begin;i<=end;i++){
			QuoteShort q = data.get(i);
			
			if (mode==-1){
				int diff = value-q.getHigh5();
				if (diff<=maxPipsDiff*10){
					//System.out.println("diff: "+diff);
					for (int j=i+1;j<=end;j++){
						int re = q.getHigh5()-data.get(j).getLow5();
						if (re>=maxReaction) maxReaction = re;
					}
				}
			}else if (mode==1){
				int diff = q.getLow5()-value;
				if (diff<=maxPipsDiff*10){
					for (int j=i+1;j<=end;j++){
						int re = data.get(j).getHigh5()-q.getLow5();
						if (re>=maxReaction) maxReaction = re;
					}
				}
			}
		}
		
		return maxReaction;
	}

	public static ArrayList<QuoteShort> addNoise(ArrayList<QuoteShort> data, int h1, int h2, int noisePips) {
		
		ArrayList<QuoteShort> noiseData = new ArrayList<QuoteShort>();
		
		Calendar cal = Calendar.getInstance();
		Calendar calqm = Calendar.getInstance();
		QuoteShort qm = new QuoteShort();
			
		
		for (int i=0;i<data.size();i++){
			QuoteShort q = data.get(i);
			QuoteShort.getCalendar(cal, q);
			
			int y = cal.get(Calendar.YEAR);
			int day = cal.get(Calendar.DAY_OF_YEAR);
			int h = cal.get(Calendar.HOUR_OF_DAY);
			int min = cal.get(Calendar.MINUTE);
			int dayWeek = cal.get(Calendar.DAY_OF_WEEK);
			
			if (h>=h1 && h<=h2){
				QuoteShort newQ = new QuoteShort();
				newQ.copy(q);
				newQ.setHigh5(q.getHigh5()+noisePips*10);
				newQ.setLow5(q.getLow5()-noisePips*10);
				noiseData.add(newQ);
			}else{
				QuoteShort newQ = new QuoteShort();
				newQ.copy(q);
				noiseData.add(newQ);
			}			
		}
		return noiseData;
	}

	public static int getLastDayQuote(ArrayList<QuoteShort> data,int index, Calendar cal,int offset) {
		// TODO Auto-generated method stub
		
		int day = cal.get(Calendar.DAY_OF_YEAR);
		Calendar cal2 = Calendar.getInstance();
		for (int i=index;i<data.size();i++){
			QuoteShort q = data.get(i);
			QuoteShort.getCalendar(cal2, q);
			
			int day2 = cal2.get(Calendar.DAY_OF_YEAR);
			int diff = day2-day;
			if (diff>=offset){
				return i-1;
			}
		}
		return 0;
	}

	public static double calculateAccLoss(int totaltouches, double factor,double baseComm) {
		
		if (totaltouches<=1) return 0+baseComm;
		
		double acc = 0;
		double size = 1.0;
		//si hay dos touched, hay un fallo porque el segundo atraviesa hasta tp
		for (int i=2;i<=totaltouches;i++){	
			
			acc += size*factor;
			acc += size*baseComm;
			//System.out.println(totaltouches+" || "+acc+" "+size+" || "+factor);			
			size = (acc+1.0)+(acc+1.0)*baseComm;			
		}
		
		//la comision se agrega al final, porque los hedges se cierran entre ellos
		
		return acc;
	}
	
	public static double calculateAccLossTry(int totaltouches, double factor,double baseComm) {
				
		double acc = 0;
		double size = 1.0;
		//si hay dos touched, hay un fallo porque el segundo atraviesa hasta tp
		for (int i=1;i<=totaltouches;i++){	
			
			acc += size*factor;
			acc += size*baseComm;
			//System.out.println(totaltouches+" || "+acc+" "+size+" || "+factor);			
			size = (acc+1.0)+(acc+1.0)*baseComm;			
		}
		
		//la comision se agrega al final, porque los hedges se cierran entre ellos
		
		return acc;
	}

	public static int getRange(ArrayList<QuoteShort> data, int index, int nbars) {
		// TODO Auto-generated method stub
		
		QuoteShort qm = new QuoteShort();
		Calendar calq = Calendar.getInstance();
		
		TradingUtils.getMaxMinShort(data, qm, calq, index-nbars, index);
		
		return qm.getHigh5()-qm.getLow5();

	}
	public static long getOpenSize(ArrayList<PositionShort> positions) {
		// TODO Auto-generated method stub
		
		long actualSize = 0;
		for (int i=0;i<positions.size();i++) {
			PositionShort p = positions.get(i);
			
			if (p.getPositionStatus()==PositionStatus.OPEN) {
				actualSize += p.getMicroLots();
			}
		}
		return actualSize;
	}
	
	public static long getOpenSize2(ArrayList<PositionCore> positions) {
		// TODO Auto-generated method stub
		
		long actualSize = 0;
		for (int i=0;i<positions.size();i++) {
			PositionCore p = positions.get(i);
			
			if (p.getPositionStatus()==PositionStatus.OPEN) {
				actualSize += p.getMicroLots();
			}
		}
		return actualSize;
	}
}
