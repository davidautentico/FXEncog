package drosa.experimental.ticks;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedHashMap;

import drosa.classes.Tick;
import drosa.experimental.PositionShort;
import drosa.strategies.PositionStatus;
import drosa.strategies.auxiliar.PositionType;
import drosa.utils.DateUtils;
import drosa.utils.PrintUtils;
import drosa.utils.TradingUtils;

public class TestHighLow {
	
	public static void tradeHighLowMaxMinsLastVisit(ArrayList<Tick> data,
			ArrayList<Integer> maxMins,
			int h1,int h2,
			int thr,
			int tp,int sl,
			int minDiffMinutes,
			int maxTrades,
			boolean debug){
		
		LinkedHashMap<Integer,Integer> prices = new LinkedHashMap<Integer,Integer>(); 
		
		for (int i=0;i<=300000;i+=25){
			prices.put(i,0);
		}
		
		int lastDay = -1;		
		int wins = 0;
		int losses = 0;
		//int lastDayTrade = -1;
		ArrayList<PositionShort> positions = new ArrayList<PositionShort>();
		for (int i=1;i<data.size();i++){
			Tick t1 = data.get(i-1);
			Tick t = data.get(i);
			
			int day = DateUtils.getDaysValue(t);
			int h = t.getHour();
			
			if (day!=lastDay){
				//if (debug) System.out.println("[***NUEVO DIA***] "+t1.toString()+" || "+t.toString()+" || "+actualLowBid+" "+actualHighAsk);
				lastDay = day;
			}
			
			//ultima visita a este precio
			int actualMinBar = t.getMinuteBar();
			int lastVisit = (int) prices.get(t.getPrice());
			int diffMinutes = actualMinBar-lastVisit;
			int maxMin = maxMins.get(i-1);
			//System.out.println(diffMinutes+" "+actualMinBar+" "+lastVisit);
			if (h1<=h && h<=h2){
				if (diffMinutes>=minDiffMinutes && lastVisit>0){
					int totalOpen = PositionShort.countTotal(positions, PositionStatus.OPEN);				
					if (totalOpen<maxTrades){
						if (maxMin>=thr && t.getPrice()>t1.getPrice()){
							PositionShort pos = new PositionShort();
							pos.setEntry(t.getPrice());
							pos.setTp(t.getPrice()-25*tp);
							pos.setSl(t.getPrice()+25*sl);
							pos.setPositionType(PositionType.SHORT);
							pos.getOpenCal().set(Calendar.YEAR, t.getYear());
							pos.getOpenCal().set(Calendar.MONTH, t.getMonth()-1);
							pos.getOpenCal().set(Calendar.DAY_OF_MONTH, t.getDay());
							pos.setPositionStatus(PositionStatus.OPEN);
							positions.add(pos);
						}else if (maxMin<=-thr && t.getPrice()<t1.getPrice()){
							PositionShort pos = new PositionShort();
							pos.setEntry(t.getPrice());
							pos.setTp(t.getPrice()+25*tp);
							pos.setSl(t.getPrice()-25*sl);
							pos.setPositionType(PositionType.LONG);
							pos.setPositionStatus(PositionStatus.OPEN);
							positions.add(pos);
						}						
					}	
				}
			}			
			
			int p = 0;
			while (p<positions.size()){
				PositionShort pos = positions.get(p);
				boolean closed = false;
				if (pos.getPositionType()==PositionType.LONG){
					if (t.getPrice()>=pos.getTp()){
						pos.setWin(1);
						closed = true;
						wins++;
					}else if (t.getPrice()<=pos.getSl()){
						pos.setWin(-1);
						closed = true;
						losses++;
					}
				}else if (pos.getPositionType()==PositionType.SHORT){
					if (t.getPrice()<=pos.getTp()){
						pos.setWin(1);
						closed = true;
						wins++;
					}else if (t.getPrice()>=pos.getSl()){
						pos.setWin(-1);
						closed = true;
						losses++;
					}
				}
				
				if (closed){
					pos.setPositionStatus(PositionStatus.OPEN);
					//if (debug) System.out.println("[CLOSED] "+pos.toString2()+" || "+t.toString());
					positions.remove(p);
				}else{
					p++;
				}
			}
			
			//actualizamos la visita del precio
			prices.put(t.getPrice(), t.getMinuteBar());
		}
		
		int totalTrades = wins+losses;
		double winPer = wins*100.0/totalTrades;
		double avg = (wins*tp-losses*sl)*1.0/totalTrades;
		double pf = (wins*tp*1.0)/(losses*sl);
		System.out.println(
				thr
				+" "+minDiffMinutes
				+" "+h1+" "+h2
				+" "+tp+" "+sl				
				+"|| "
				+" "+totalTrades+" "+PrintUtils.Print2dec(winPer, false)
				+" "+PrintUtils.Print2dec(avg, false)
				+" "+PrintUtils.Print2dec(pf, false)
				);
	}
	
	public static void tradeHighLowMaxMins(ArrayList<Tick> data,
			ArrayList<Integer> maxMins,
			int h1,int h2,
			int thr,
			int tp,int sl,
			int maxTrades,
			boolean debug){
		
		int lastDay = -1;
		int lastHighAsk = -1;
		int lastLowBid = -1;
		int actualHighAsk = -1;
		int actualLowBid = -1;
		int actualMovs = 0;
		boolean highTouched = false;
		boolean lowTouched = false;
		int highExecuted = -1;
		int lowExecuted = -1;
		int mode = 0;
		int wins = 0;
		int losses = 0;
		int lastDiff = 99999;
		int lastMinuteTrade = -1;
		//int lastDayTrade = -1;
		ArrayList<PositionShort> positions = new ArrayList<PositionShort>();
		for (int i=1;i<data.size();i++){
			Tick t1 = data.get(i-1);
			Tick t = data.get(i);
			
			int day = DateUtils.getDaysValue(t);
			int h = t.getHour();
			
			if (day!=lastDay){
				//if (debug) System.out.println("[***NUEVO DIA***] "+t1.toString()+" || "+t.toString()+" || "+actualLowBid+" "+actualHighAsk);
				
				highTouched = false;
				lowTouched = false;
				//mode = 0;
				actualHighAsk = -1;
				actualLowBid = -1;
				lastDay = day;
			}
			
			int maxMin = maxMins.get(i-1);
				
			if (maxMin>=thr
					&& h1<=h && h<=h2
				){
				int totalOpen = PositionShort.countTotal(positions, PositionStatus.OPEN);
				if (totalOpen<maxTrades){
					PositionShort pos = new PositionShort();
					pos.setEntry(t.getPrice());
					pos.setTp(t.getBid()-25*tp);
					pos.setSl(t.getBid()+25*sl);
					pos.setPositionType(PositionType.SHORT);
					pos.getOpenCal().set(Calendar.YEAR, t.getYear());
					pos.getOpenCal().set(Calendar.MONTH, t.getMonth()-1);
					pos.getOpenCal().set(Calendar.DAY_OF_MONTH, t.getDay());
					pos.setPositionStatus(PositionStatus.OPEN);
					if (lastMinuteTrade!=DateUtils.getMinutesValue(t1)
							//&& lastDayTrade!=day
							){
						positions.add(pos);
						//lastDayTrade =day;
						lastMinuteTrade = DateUtils.getMinutesValue(t1);
						//if (debug) System.out.println("[SHORT] "+t1.toString()+" || "+maxMin);
					}
				}
			}else if (maxMin<=-thr
					&& h1<=h && h<=h2
					){
				int totalOpen = PositionShort.countTotal(positions, PositionStatus.OPEN);
				if (totalOpen<maxTrades){
					PositionShort pos = new PositionShort();
					pos.setEntry(t.getPrice());
					pos.setTp(t.getAsk()+25*tp);
					pos.setSl(t.getAsk()-25*sl);
					pos.setPositionType(PositionType.LONG);
					pos.setPositionStatus(PositionStatus.OPEN);
					if (lastMinuteTrade!=DateUtils.getMinutesValue(t1)
							//&& lastDayTrade!=day
							){
						positions.add(pos);
						//lastDayTrade =day;
						lastMinuteTrade = DateUtils.getMinutesValue(t1);
						if (debug) System.out.println("[LONG] "+t1.toString()+" || "+maxMin);
					}
				}
			}
			
			
			int p = 0;
			while (p<positions.size()){
				PositionShort pos = positions.get(p);
				boolean closed = false;
				if (pos.getPositionType()==PositionType.LONG){
					if (t.getPrice()>=pos.getTp()){
						pos.setWin(1);
						closed = true;
						wins++;
					}else if (t.getPrice()<=pos.getSl()){
						pos.setWin(-1);
						closed = true;
						losses++;
					}
				}else if (pos.getPositionType()==PositionType.SHORT){
					if (t.getPrice()<=pos.getTp()){
						pos.setWin(1);
						closed = true;
						wins++;
					}else if (t.getPrice()>=pos.getSl()){
						pos.setWin(-1);
						closed = true;
						losses++;
					}
				}
				
				if (closed){
					pos.setPositionStatus(PositionStatus.OPEN);
					//if (debug) System.out.println("[CLOSED] "+pos.toString2()+" || "+t.toString());
					positions.remove(p);
				}else{
					p++;
				}
			}
		}
		
		int totalTrades = wins+losses;
		double winPer = wins*100.0/totalTrades;
		double avg = (wins*tp-losses*sl)*1.0/totalTrades;
		
		System.out.println(
				thr
				+" "+h1+" "+h2
				+" "+tp+" "+sl				
				+"|| "
				+" "+totalTrades+" "+PrintUtils.Print2dec(winPer, false)
				+" "+PrintUtils.Print2dec(avg, false)
				);
	}
	
	public static void tradeHighLow(ArrayList<Tick> data,int maxTicks,boolean debug){
		int lastDay = -1;
		int lastHighAsk = -1;
		int lastLowBid = -1;
		int actualHighAsk = -1;
		int actualLowBid = -1;
		int actualMovs = 0;
		boolean highTouched = false;
		boolean lowTouched = false;
		int highExecuted = -1;
		int lowExecuted = -1;
		int mode = 0;
		int wins = 0;
		int losses = 0;
		int lastDiff = 99999;
		for (int i=1;i<data.size();i++){
			Tick t1 = data.get(i-1);
			Tick t = data.get(i);
			
			int day = DateUtils.getDaysValue(t);
			
			if (day!=lastDay){
				
				
				lastHighAsk = actualHighAsk +0*25;
				lastLowBid = actualLowBid -0*25;
				
				if (debug)
				System.out.println("[***NUEVO DIA***] "+t1.toString()
					+" || "+t.toString()
					+" || "+actualLowBid+" "+actualHighAsk);
				
				highTouched = false;
				lowTouched = false;
				mode = 0;
				actualHighAsk = -1;
				actualLowBid = -1;
				lastDay = day;
			}
			
			if (mode==0){
				if (t.getBid()>=lastHighAsk){
					highExecuted = t.getBid(); //porque estas vendiendo
					//if (debug) System.out.println("[****HIGH TOUCHED*****] "+highExecuted+" || "+t1.toString()+" || "+t.toString());
					mode = 1;
				}else if (t.getAsk()<=lastLowBid){
					lowExecuted = t.getAsk();//porque estas comprando
					mode = -1;
				}
			}else if (mode==1){ //voy vendido-> miro en el bid a ver a cuanto estan dispuestos a pagar
				int diffPos = (highExecuted-t.getAsk())/25;
				if (diffPos>=maxTicks){
					wins++;
					mode = 2;
				}else if (diffPos<=-maxTicks){
					losses++;
					mode = 2;
				}
				if (diffPos!=lastDiff){
					//if (debug) System.out.println(t.toString()+" || "+highExecuted+" "+diffPos);
					lastDiff = diffPos;
				}
			}else if (mode==-1){ //voy comprado-> busco el BID para vender
				int diffPos = (t.getBid()-lowExecuted)/25;
				if (diffPos>=maxTicks){
					wins++;
					mode = 2;
				}else if (diffPos<=-maxTicks){
					losses++;
					mode = 2;
				}
			}
			
			
			if (actualHighAsk==-1 || t.getAsk()>=actualHighAsk){
				actualHighAsk = t.getAsk();
			}
			if (actualLowBid==-1 || t.getBid()<=actualLowBid){
				actualLowBid = t.getBid();
			}
		}
		
		int totalTrades = wins+losses;
		double winPer = wins*100.0/totalTrades;
		double avg = (wins*maxTicks-losses*maxTicks)*1.0/totalTrades;
		
		System.out.println(
				maxTicks
				+"|| "
				+" "+totalTrades+" "+PrintUtils.Print2dec(winPer, false)
				+" "+PrintUtils.Print2dec(avg, false)
				);
	}
	
	public static void calculateHighLow(ArrayList<Tick> data){
		
		int lastDay = -1;
		int actualHighAsk = -1;
		int actualLowBid = -1;
		for (int i=1;i<data.size();i++){
			Tick t1 = data.get(i-1);
			Tick t = data.get(i);
			
			int day = DateUtils.getDaysValue(t);
			
			if (day!=lastDay){		
				
				System.out.println(DateUtils.datePrint(t1.getYear(), t1.getMonth(), t1.getDay())+" "+actualLowBid+" "+actualHighAsk);
				
				actualHighAsk = -1;
				actualLowBid = -1;
				lastDay = day;
			}
												
			if (actualHighAsk==-1 || t.getAsk()>=actualHighAsk){
				actualHighAsk = t.getAsk();
			}
			if (actualLowBid==-1 || t.getBid()<=actualLowBid){
				actualLowBid = t.getBid();
			}
		}
	}
	
	

	public static void main(String[] args) throws FileNotFoundException {
		String fileNameES1 = "C:\\fxdata\\futuros\\ticks\\ES1.txt";
		String fileNameES2 = "C:\\fxdata\\futuros\\ticks\\ES2.txt";
		String fileName0 = "C:\\fxdata\\futuros\\ticks\\ES_2013_2016_ticks.txt";
		String fileName1 = "C:\\fxdata\\futuros\\ticks\\ES_06_09_2015.txt";
		String fileName2 = "C:\\fxdata\\futuros\\ticks\\ES_07_09_2015.txt";
		String fileName3 = "C:\\fxdata\\futuros\\ticks\\ES_08_09_2015.txt";
		String fileName4 = "C:\\fxdata\\futuros\\ticks\\ES_09_09_2015.txt";
		String fileName5 = "C:\\fxdata\\futuros\\ticks\\ES_09_12_2015.txt";
		String fileName6 = "C:\\fxdata\\futuros\\ticks\\ES_10_12_2015.txt";
		String fileName7 = "C:\\fxdata\\futuros\\ticks\\ES_11_12_2015.txt";
		String fileName8 = "C:\\fxdata\\futuros\\ticks\\ES_12_12_2015.txt";
		String fileName9 = "C:\\fxdata\\futuros\\ticks\\ES_12_03_2016.txt";
		String fileName10 = "C:\\fxdata\\futuros\\ticks\\ES_01_03_2016.txt";
		String fileName11 = "C:\\fxdata\\futuros\\ticks\\ES_02_03_2016.txt";
		String fileName12 = "C:\\fxdata\\futuros\\ticks\\ES_03_03_2016.txt";
		String fileName13 = "C:\\fxdata\\futuros\\ticks\\ES_03_06_2016.txt";
		String fileName14 = "C:\\fxdata\\futuros\\ticks\\ES_04_06_2016.txt";
		
		ArrayList<String> fileNames = new ArrayList<String>();
		fileNames.add(fileNameES1);
		fileNames.add(fileNameES2);
		fileNames.add(fileName0);
		fileNames.add(fileName1);
		fileNames.add(fileName2);
		fileNames.add(fileName3);
		fileNames.add(fileName4);
		fileNames.add(fileName5);
		fileNames.add(fileName6);
		fileNames.add(fileName7);
		fileNames.add(fileName8);
		fileNames.add(fileName9);
		fileNames.add(fileName10);
		fileNames.add(fileName11);
		fileNames.add(fileName12);
		fileNames.add(fileName13);
		fileNames.add(fileName14);
		
		ArrayList<Tick> data = null;
		ArrayList<Integer> maxMins = null;
		int limit = 2;
		//int limit = fileNames.size()-1;
		for (int i=2;i<=limit;i++){
			String fileName = fileNames.get(i);
			if (data!=null) data.clear();
			if (maxMins!=null) maxMins.clear();
			
			//TickUtils.savePrices1min(fileName,2016,2016,false);
			
			// TickUtils.savePrices1min(fileName,2010,2010);
			/*if (i==0)
				TickUtils.savePrices1min(fileName,2009,2012);
			if (i==1)
				TickUtils.savePrices1min(fileName,2013,2016);*/
			/*for (int y1=2013;y1<=2013;y1++){
				int y2 = y1+3;
				if (data!=null)
					data.clear();
				if (maxMins!=null)
					maxMins.clear();
				data = TickUtils.readFastTicksDave(fileName,y1,y2);
				//maxMins = TickUtils.calculateMaxMinTime(data);
				try(  PrintWriter out = new PrintWriter(fileName+"_"+y1+"_"+y2+"_ticks.txt")  ){
					for (int d=0;d<data.size();d++){
						out.println(data.get(d).toString());
					}
					out.close();
				}
				
			}*/
			
			data = TickUtils.readFastTicksDave(fileName,y1,y2);
		
			int y1=2009;
			int y2=2016;
			data = TickUtils.readTicksDaveMinutes(fileName,y1,y2);
			System.out.println("data: "+data.size());
			maxMins = TickUtils.calculateMaxMinMinuteBar(data);
			System.out.println("maxMins: "+maxMins.size());
			for (int tp=5;tp<=5;tp++){	
				for (int sl=tp;sl<=200*tp;sl+=tp){
					for (int minDiff=0;minDiff<=0;minDiff+=600){
						for (int thr=1000;thr<=1000;thr+=10){
							for (int h1=0;h1<=0;h1++){
								int h2 = h1+23;
								//TestHighLow.tradeHighLowMaxMins(data,maxMins,h1,h2,thr,tp,sl,10000,false);
								TestHighLow.tradeHighLowMaxMinsLastVisit(data,maxMins,h1,h2,thr,tp,sl,minDiff,10000,false);
							}
						}	
					}
				}
			}
			
			
		}

	}

}
