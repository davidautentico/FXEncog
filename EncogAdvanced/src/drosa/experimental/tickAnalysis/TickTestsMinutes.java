package drosa.experimental.tickAnalysis;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

import drosa.data.DataUtils;
import drosa.data.TickQuote;
import drosa.utils.PrintUtils;

public class TickTestsMinutes {

	public static void main(String[] args) {
		String fileName12 ="c:\\fxdata\\EURUSD_UTC_Ticks_Bid_2012.01.01_2012.12.31.csv";
		String fileName13 ="c:\\fxdata\\EURUSD_UTC_Ticks_Bid_2013.01.01_2013.12.31.csv";
		String fileName14 ="c:\\fxdata\\EURUSD_UTC_Ticks_Bid_2014.01.01_2014.12.31.csv";
		String fileName15 ="c:\\fxdata\\EURUSD_UTC_Ticks_Bid_2015.01.01_2015.12.31.csv";
		String fileName36 ="c:\\fxdata\\EURUSD_UTC_Ticks_Bid_2013.01.01_2016.12.31.csv";
		String fileName56 ="c:\\fxdata\\EURUSD_UTC_Ticks_Bid_2015.01.01_2016.12.31.csv";
		
		String currencyStr = "eurusd";
		HashMap<Integer,ArrayList<Double>> spreadsDict = new HashMap<Integer,ArrayList<Double>>();
		for (int h=0;h<=23;h++) {
			for (int min=0;min<=55;min+=5) {
				int hmin = h*12+min/5;
				spreadsDict.put(hmin,new ArrayList<Double>()); 
			}
		}
		Calendar cal = Calendar.getInstance();
		
		for (int y1=2012;y1<=2019;y1++) {
			String fileName		= "";
			//String fileName		= "c:\\fxdata\\"+currencyStr+"_Ticks_"+y1+".09.01_"+y1+".11.21.csv";
			String fileName1 	= "c:\\fxdata\\"+currencyStr+"_Ticks_"+y1+".03.16_"+y1+".05.22.csv";
			ArrayList<Integer> spreads = new ArrayList<Integer>();
			ArrayList<Integer> counts = new ArrayList<Integer>();
			for (int i=0;i<=23;i++){
				for (int min=0;min<=55;min+=5) {
					spreads.add(0);
					counts.add(0);
				}
			}
			ArrayList<String> fileNames = new ArrayList<String>();
			//fileNames.add(fileName); 
			fileNames.add(fileName1);
			for (int f=0; f<=fileNames.size()-1;f++) {
				fileName = fileNames.get(f);
				ArrayList<TickQuote> data = DataUtils.retrieveTickQuotes(fileName,1);				
				System.out.println(fileName+" "+data.size());
								
				//añadimos a spreads acumulando
				for (int i=0;i<data.size();i++){
					TickQuote t = data.get(i);
					TickQuote.getCalendar(cal, t);
					int h = cal.get(Calendar.HOUR_OF_DAY);
					int min = cal.get(Calendar.MINUTE);
					int hmin = h*12+min/5;
					min=0;
					int spreadDiff = Math.abs(t.getAsk()-t.getBid());
					
					spreads.set(hmin, spreads.get(hmin)+spreadDiff);
					counts.set(hmin, counts.get(hmin)+1);
				}
			}
			//sacamos media y añadimos
			for (int h=0;h<=23;h++){
				for (int min=0;min<=55;min+=5) {
					int hmin = h*12+min/5;
					double avg = spreads.get(hmin)*0.1/counts.get(hmin);
					
					int hreal = (h+3)%24;
					int hminreal = hreal*12+min/5;
					//System.out.println(hreal+" "+h+" "+PrintUtils.Print2dec(avg,false)+" "+counts.get(h));
					ArrayList<Double> arr = spreadsDict.get(hminreal);
					arr.add(avg);
				}
			}
		}//y1
		
		for (int h=0;h<=23;h++){
			for (int min=0;min<=55;min+=5) {
				int hmin = h*12+min/5;
				ArrayList<Double> arr = spreadsDict.get(hmin);
				String str ="";
				for (int j=0;j<arr.size();j++) {
					str += PrintUtils.Print2dec(arr.get(j),false)+" ";
				}
				System.out.println(h+":"+min+" || "+str);
			}
		}

	}

}
