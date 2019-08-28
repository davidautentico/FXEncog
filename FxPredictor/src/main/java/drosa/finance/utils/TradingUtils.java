package drosa.finance.utils;

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

import drosa.finance.classes.PositionShort;
import drosa.finance.classes.QuoteShort;
import drosa.finance.types.PositionType;


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

	
	
	public static String generateFileName(String suffix,String symbol,int tp,int sl,int begin,int end){
		String fileName =suffix+symbol+"_"+String.valueOf((int)tp)+"_"+String.valueOf((int)sl)
		+"_"+String.valueOf(begin)+"_"+String.valueOf(end)+".csv";
		
		if (begin<0){
			fileName =suffix+symbol+"_"+String.valueOf((int)tp)+"_"+String.valueOf((int)sl)
					+".csv";
			 
		}
		return fileName;
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
				//System.out.println("a�adiendo nbars: "+nbarsH+" "+nbarsL+" "+count5000);
			}
			if (nbarsH>=nbarsL) maxMin.setExtra(nbarsH);
			if (nbarsH<nbarsL) maxMin.setExtra(-nbarsL);
			maxMins.add(maxMin);
		}
		return maxMins;
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
				//System.out.println("a�adiendo nbars: "+nbarsH+" "+nbarsL+" "+count5000);
			}
			if (nbarsH>=nbarsL) maxMin=nbarsH;
			if (nbarsH<nbarsL) maxMin=-nbarsL;
			maxMins.add(maxMin);
		}
		return maxMins;
	}
	
	public static ArrayList<Integer> calculateMaxMinByBarShortAbsoluteIntLimit(
			ArrayList<QuoteShort> data,int limit) {
		
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
				//System.out.println("a�adiendo nbars: "+nbarsH+" "+nbarsL+" "+count5000);
			}
			if (nbarsH>=nbarsL) maxMin=nbarsH;
			if (nbarsH<nbarsL) maxMin=-nbarsL;
			
			if (maxMin>=limit) maxMin = limit;
			if (maxMin<=limit) maxMin = limit;
			maxMins.add(maxMin);
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
	
	//CALCULO DE FEATURES
	
	public static ArrayList<Integer> getHours(ArrayList<QuoteShort> data) {
		// TODO Auto-generated method stub
		return null;
	}
}
