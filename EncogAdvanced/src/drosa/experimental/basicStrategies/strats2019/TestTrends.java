package drosa.experimental.basicStrategies.strats2019;

import java.util.ArrayList;
import java.util.Calendar;

import drosa.DAO.DAO;
import drosa.data.DataProvider;
import drosa.experimental.forexfactory.FFNewsClass;
import drosa.experimental.ticksStudy.Tick;
import drosa.experimental.zznbrum.TrendClass;
import drosa.experimental.zznbrum.TrendInfo;
import drosa.finances.QuoteShort;
import drosa.memory.Sizeof;
import drosa.phil.TestLines;
import drosa.utils.PrintUtils;
import drosa.utils.TradingUtils;

public class TestTrends {
	
	
	public static void getLegs2(
			ArrayList<QuoteShort> data,
			ArrayList<Integer> maxMins,
			int y1,int y2,int h1,int h2,
			int thr,
			int size){
		
		
		ArrayList<TrendInfo> dataTrend = new ArrayList<TrendInfo>();
		
		TradingUtils.calculateTrendsHL2(data,
				size,dataTrend);
		//System.out.println(dataTrend.size());
		
		Calendar cal = Calendar.getInstance();
		double acc = 0;
		int count = 0;
		int winPips = 0;
		int lostPips = 0;
		int wins = 0;
		int losses = 0;
		int entry = 0;
		int mode = 0;
		int tp =50;
		for (int i=0;i<data.size();i++){
			QuoteShort q = data.get(i);
			QuoteShort.getCalendar(cal, q);
			
			int h = cal.get(Calendar.HOUR_OF_DAY);
			int y = cal.get(Calendar.YEAR);
			
			TrendInfo ti = dataTrend.get(i); 
			
			//test entries
			if (mode==0){
				if (y>=y1 && y<=y2){
					if (h>=h1 && h<=h2){
						if (maxMins.get(i)>=thr
								&& q.getClose5()-q.getOpen5()>=40
								//&& q.getClose5()-data.get(i-36).getClose5()>=300
								//&& ti.getLeg()==1 
								//&& ti.getActualExtensionClose()>=1*size
								//&& maxMins.get(i)>=thr
								){
							entry = q.getClose5();
							mode = -1;
							//System.out.println(entry);
						}else if (maxMins.get(i)<=-thr
								&& -q.getClose5()+q.getOpen5()>=40
								//&& ti.getLeg()==-1 
								//&& -q.getClose5()+data.get(i-36).getClose5()>=300
								//&& ti.getActualExtensionClose()>=1*size
								//&& maxMins.get(i)<=-thr
								){
							entry = q.getClose5();
							mode = 1;
						}
					}
				}
			}else if (mode==1){
				if (q.getClose5()-entry>=150 || q.getClose5()-entry<=-600 
						|| -q.getClose5()+data.get(i-36).getClose5()>=300
						//-q.getClose5()+data.get(i-12).getClose5()>=100
						//ti.getLeg()==-1
						){
					int pips = q.getClose5()-entry;
					mode = 0;
					if (pips>=0){
						winPips += pips;
						wins++;
					}else{
						lostPips += -pips;
						losses++;
					}
				}
			}else if (mode==-1){
				if (-q.getClose5()+entry>=150 || -q.getClose5()+entry<=-600
						|| q.getClose5()-data.get(i-36).getClose5()>=300
						//q.getClose5()-data.get(i-12).getClose5()>=100
						//ti.getLeg()==1
						){
					int pips = -q.getClose5()+entry;
					mode = 0;
					if (pips>=0){
						winPips += pips;
						wins++;
					}else{
						lostPips += -pips;
						losses++;
					}
				}
			}
			
		}
		count = wins+losses;
		System.out.println(y1 +" "+y2+" "+h1+" "+h2+" "+thr
				+" || "+count 
				+" "+winPips +" "+lostPips
				+" "+PrintUtils.Print2dec(winPips*1.0/lostPips, false)
				+" "+PrintUtils.Print2dec((winPips-lostPips)*0.1/count, false)
				);
	}
	
	public static void getLegs(
			ArrayList<QuoteShort> data,
			ArrayList<Integer> maxMins,
			int y1,int y2,int h1,int h2,
			int thr,
			int size){
		
		ArrayList<TrendClass> dataTrends = TradingUtils.calculateTrends(data,size);
		
		Calendar cal = Calendar.getInstance();
		double acc = 0;
		int count = 0;
		int winPips = 0;
		int lostPips = 0;
		int wins = 0;
		int losses = 0;
		for (int i=0;i<dataTrends.size();i++){
			TrendClass tc = dataTrends.get(i);
			
			cal.setTimeInMillis(tc.getMillisIndex1());
			int h = cal.get(Calendar.HOUR_OF_DAY);
			int y = cal.get(Calendar.YEAR);
			
			int index3 = tc.getIndex3();
			int maxMin = maxMins.get(index3);
			if (y>=y1 && y<=y2){
				if (h>=h1 && h<=h2){
					if ((maxMin>=thr
							&& tc.getSizeClose()>=0
							) 
							|| (maxMin<=-thr
							   && tc.getSizeClose()<=-0
									)
							){
						//double sizea = Math.abs(tc.getSizeClose())*1.0/size;					
						//System.out.println(PrintUtils.Print2dec(sizea, false));
						//acc += sizea;
						//count++;
						
						int pips = 0;
						if (tc.getSizeClose()>=0){
							pips = data.get(tc.getIndexC()).getClose5()-data.get(tc.getIndex3()).getClose5();
							
							double sizea = Math.abs(pips)*1.0/size;					
							//System.out.println(PrintUtils.Print2dec(sizea, false));
							acc += sizea;
							count++;
							
						}else{
							pips = -data.get(tc.getIndexC()).getClose5()+data.get(tc.getIndex3()).getClose5();
							
							double sizea = Math.abs(pips)*1.0/size;					
							//System.out.println(PrintUtils.Print2dec(sizea, false));
							acc += sizea;
							count++;
						}
						
						if (pips>=0){
							winPips += pips;
							wins++;
							
							/*System.out.println(
									pips+" "+
									PrintUtils.Print2dec(Math.abs(pips)*1.0/size, false)
									+" "+winPips
									+" || "+lostPips
									
									);*/
						}else{
							lostPips += -pips;
							losses++;
							
							/*System.out.println(
									pips+" "+
									PrintUtils.Print2dec(Math.abs(pips)*1.0/size, false)
									+" "+winPips
									+" || "+lostPips
									
									);*/
						}
					}
				}
			}
		}
		System.out.println(y1 +" "+y2+" "+h1+" "+h2+" "+thr
				+" || "+count 
				+" "+PrintUtils.Print2dec(acc*1.0/count, false)
				+" "+PrintUtils.Print2dec(winPips*1.0/lostPips, false)
				+" || "+PrintUtils.Print2dec(lostPips*1.0/winPips, false)
				+" "+PrintUtils.Print2dec((lostPips-winPips)*0.1/count, false)
				);
	}

	public static void main(String[] args) {
		String path0 ="C:\\fxdata\\";
		
		
		//String pathEURUSD = path0+"EURUSD_1 Min_Bid_2009.01.01_2019.04.01.csv";
		String pathEURUSD = path0+"EURUSD_5 Mins_Bid_2004.01.01_2019.07.16.csv";
		//String pathEURUSD = path0+"EURUSD_15 Mins_Bid_2004.01.01_2019.04.06.csv";
			String pathNews = path0+"News.csv";
			
			ArrayList<String> paths = new ArrayList<String>();
			paths.add(pathEURUSD);
			//paths.add(pathEURAUD);paths.add(pathNZDUSD);
			
			int total = 0;
			ArrayList<Double> pfs = new ArrayList<Double>();
			int limit = paths.size()-1;
			limit = 0;
			String provider ="";
			try {
				Sizeof.runGC ();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			ArrayList<QuoteShort> dataI 		= null;
			ArrayList<QuoteShort> dataS 		= null;
			ArrayList<FFNewsClass> news = new ArrayList<FFNewsClass>();	
			//FFNewsClass.readNews(pathNews,news,0);
			ArrayList<Tick> ticks = new ArrayList<Tick>();
			for (int i = 0;i<=limit;i++){
				String path = paths.get(i);				
				dataI 		= new ArrayList<QuoteShort>();			
				dataI 		= DAO.retrieveDataShort5m(path, DataProvider.DUKASCOPY_FOREX4);			
				TestLines.calculateCalendarAdjustedSinside(dataI);			
				dataS = TradingUtils.cleanWeekendDataS(dataI);  
				ArrayList<QuoteShort> data = dataS;
				ArrayList<Integer> maxMins = TradingUtils.calculateMaxMinByBarShortAbsoluteInt(data);
				

				int size = 200;
				
				for (int y1=2009;y1<=2009;y1++){
					int y2 = y1+10;
					for (int h1=0;h1<=0;h1++){
						int h2 = h1+9;
					 	for (int thr=0;thr<=4000;thr+=12)	
					 		TestTrends.getLegs2(data,maxMins, y1, y2,h1,h2,thr, size);
					}
				}
			
			}
	}

}
