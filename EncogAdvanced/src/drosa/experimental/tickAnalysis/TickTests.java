package drosa.experimental.tickAnalysis;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

import drosa.data.DataUtils;
import drosa.data.TickQuote;
import drosa.utils.PrintUtils;

public class TickTests {

	public static void main(String[] args) {
		
		String fileName12 ="c:\\fxdata\\EURUSD_UTC_Ticks_Bid_2012.01.01_2012.12.31.csv";
		String fileName13 ="c:\\fxdata\\EURUSD_UTC_Ticks_Bid_2013.01.01_2013.12.31.csv";
		String fileName14 ="c:\\fxdata\\EURUSD_UTC_Ticks_Bid_2014.01.01_2014.12.31.csv";
		String fileName15 ="c:\\fxdata\\EURUSD_UTC_Ticks_Bid_2015.01.01_2015.12.31.csv";
		String fileName36 ="c:\\fxdata\\EURUSD_UTC_Ticks_Bid_2013.01.01_2016.12.31.csv";
		String fileName56 ="c:\\fxdata\\EURUSD_UTC_Ticks_Bid_2015.01.01_2016.12.31.csv";
		
		String currencyStr = "eurusd";
		HashMap<Integer,ArrayList<Double>> spreadsDict = new HashMap<Integer,ArrayList<Double>>();
		for (int h=0;h<=23;h++) spreadsDict.put(h,new ArrayList<Double>()); 
		for (int y1=2012;y1<=2019;y1++) {
			String fileName ="c:\\fxdata\\"+currencyStr+"_Ticks_"+y1+".09.01_"+y1+".11.21.csv";
			//String fileName ="c:\\fxdata\\EURUSD_UTC_Ticks_Bid_2016.10.01_2016.11.01.csv";
			ArrayList<TickQuote> data = DataUtils.retrieveTickQuotes(fileName,1);
			
			System.out.println(fileName+" "+data.size());
			Calendar cal = Calendar.getInstance();
			ArrayList<Integer> spreads = new ArrayList<Integer>();
			ArrayList<Integer> counts = new ArrayList<Integer>();
			for (int i=0;i<=23;i++){
				spreads.add(0);
				counts.add(0);
			}
			for (int i=0;i<data.size();i++){
				TickQuote t = data.get(i);
				TickQuote.getCalendar(cal, t);
				int h = cal.get(Calendar.HOUR_OF_DAY);
				int min = cal.get(Calendar.MINUTE);
				min=0;
				int spreadDiff = Math.abs(t.getAsk()-t.getBid());
				
				spreads.set(h, spreads.get(h)+spreadDiff);
				counts.set(h, counts.get(h)+1);
			}
			
			for (int h=0;h<=23;h++){
				int hour = h/60; 
				int min = h-hour*60;
				double avg = spreads.get(h)*0.1/counts.get(h);
				
				int hreal = (h+3)%24;
				//System.out.println(hreal+" "+h+" "+PrintUtils.Print2dec(avg,false)+" "+counts.get(h));
				ArrayList<Double> arr = spreadsDict.get(hreal);
				arr.add(avg);
			}
		}//y1
		
		for (int h=0;h<=23;h++){
			ArrayList<Double> arr = spreadsDict.get(h);
			String str ="";
			for (int j=0;j<arr.size();j++) {
				str += PrintUtils.Print2dec(arr.get(j),false)+" ";
			}
			System.out.println(h+" || "+str);
		}
	}

}
