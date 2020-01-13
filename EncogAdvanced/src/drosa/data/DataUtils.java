package drosa.data;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import drosa.experimental.PositionShort;
import drosa.experimental.traderDale.PositionTraderDale;
import drosa.finances.QuoteShort;
import drosa.strategies.PositionStatus;
import drosa.strategies.auxiliar.PositionType;
import drosa.utils.DateUtils;
import drosa.utils.MathUtils;
import drosa.utils.PrintUtils;

public class DataUtils {
	
	public static void getDalyDataVolumes(ArrayList<TickQuote> data,ArrayList<ArrayList<Double>> volumesDaily){
		
		ArrayList<Double> volumes = new ArrayList<Double>();
		
		for (int i=0;i<20000;i++) volumes.add(0.0);
		
		int lastDay = -1;
		Calendar cal = Calendar.getInstance();
		for (int i=0;i<=data.size()-1;i++){
			TickQuote t = data.get(i);
			
			TickQuote.getCalendar(cal, t);
			int year = cal.get(Calendar.YEAR);
			int day = cal.get(Calendar.DAY_OF_YEAR);
			
			if (day!=lastDay){
				
				if (lastDay!=-1){
					for (int p = 0;p<volumes.size();p++){
						double volu = volumes.get(p);
						if (volu>0){
							System.out.println(p+" "+day+" "+PrintUtils.Print2dec(volu,false));
							volumesDaily.get(p).set(day, volu);//para el precio p, se coloca el volumen volu del dia day
						}
					}
				}
				
				//limpiamos volumenes y pasamos al siguiente dia
				for (int s=0;s<20000;s++) volumes.set(s,0.0);
				lastDay = day;
			}
			
			//if (cal.getTimeInMillis()<cal1.getTimeInMillis() || cal.getTimeInMillis()>cal2.getTimeInMillis()) continue;
			
			int avg = (t.getAsk()+t.getBid())/20;
			double vol = t.getAskvolume()+t.getBidvolume();
			
			
			volumes.set(avg, volumes.get(avg)+vol);			
		}
		
		
	}
	
	public static ArrayList<Double> getDataVolumes(ArrayList<TickQuote> data){
		
		ArrayList<Double> volumes = new ArrayList<Double>();
		
		for (int i=0;i<20000;i++) volumes.add(0.0);
		
		Calendar cal = Calendar.getInstance();
		for (int i=0;i<=data.size()-1;i++){
			TickQuote t = data.get(i);
			
			TickQuote.getCalendar(cal, t);
			
			//if (cal.getTimeInMillis()<cal1.getTimeInMillis() || cal.getTimeInMillis()>cal2.getTimeInMillis()) continue;
			
			int avg = (t.getAsk()+t.getBid())/20;
			double vol = t.getAskvolume()+t.getBidvolume();
			
			
			volumes.set(avg, volumes.get(avg)+vol);			
		}
		
		
		return volumes;
	}
	
	
	public static ArrayList<Double> getDataVolumes(ArrayList<TickQuote> data,Calendar cal1,Calendar cal2){
		
		ArrayList<Double> volumes = new ArrayList<Double>();
		
		for (int i=0;i<20000;i++) volumes.add(0.0);
		
		Calendar cal = Calendar.getInstance();
		for (int i=0;i<=data.size()-1;i++){
			TickQuote t = data.get(i);
			
			TickQuote.getCalendar(cal, t);
			
			if (cal.getTimeInMillis()<cal1.getTimeInMillis() || cal.getTimeInMillis()>cal2.getTimeInMillis()) continue;
			
			int avg = (t.getAsk()+t.getBid())/20;
			double vol = t.getAskvolume()+t.getBidvolume();
			
			
			volumes.set(avg, volumes.get(avg)+vol);			
		}
		
		
		return volumes;
	}
	
	private static int convert3(String[] values){
		int res = 0;
		//System.out.println(values[0]);
		if (values.length==2){			
			res = Integer.valueOf(values[0])*1000+Integer.valueOf(QuoteShort.fill3(values[1])); 
		}else if (values.length==1){
			res = Integer.valueOf(values[0])*1000; 
		}
		
		return res;
	}
	
	private static TickQuote decodeTickQuote(String linea){
	
		TickQuote tick = null;
		
		Date date = DateUtils.getDukasDate(linea.split(" ")[0].trim(),linea.split(" ")[1].trim());
        
        String timeStr = linea.split(" ")[1].trim();
        String dateStr = linea.split(" ")[0].trim();
       
        short year = 0;
		byte month 	= Byte.valueOf(dateStr.split("\\.")[1].trim());
		byte day   	= 0;
		
		if (dateStr.split("\\.")[0].trim().length()==4){
			year  = Short.valueOf(dateStr.split("\\.")[0].trim());
			day   = Byte.valueOf(dateStr.split("\\.")[2].trim());
		}else if (dateStr.split("\\.")[2].trim().length()==4){
			year  = Short.valueOf(dateStr.split("\\.")[2].trim());
			day   = Byte.valueOf(dateStr.split("\\.")[0].trim());
		}
		
       
				
        byte hh = Byte.valueOf(timeStr.substring(0,2));
        byte mm = Byte.valueOf(timeStr.substring(3,5));
        byte ss = Byte.valueOf(timeStr.substring(6,8));
        
        int ask = 0;
        int bid = 0;
        double askVolume = 0;
        double bidVolume = 0;
        
        
        double askdouble= Double.valueOf(linea.split(";")[1].replace(",", "."));            
		String delimiter = ".";
		if (linea.split(";")[1].contains(",") || linea.split(";")[2].contains(",")){
			delimiter = ",";
		}
		
		String askVolStr = linea.split(";")[3].replace(",", ".");
        String bidVolStr = linea.split(";")[4].replace(",", ".");
        
        try{
            askVolume	= Double.valueOf(askVolStr);
            bidVolume   = Double.valueOf(bidVolStr);
        }catch(Exception e){
        	//System.out.println(linea.split(" ")[2]+" "+openStr+". "+e.getMessage());
        }
        
		 if (askdouble<10.0){
			 //System.out.println(linea.split(" ")[2]);
	            String askStr = linea.split(";")[1].replace(delimiter, "");
	            String bidStr = linea.split(";")[2].replace(delimiter, "");
	            
	            askStr = QuoteShort.fill5(askStr);
	            bidStr = QuoteShort.fill5(bidStr);
	           
	            try{
		            ask	= Integer.valueOf(askStr);
		            bid = Integer.valueOf(bidStr);
	            }catch(Exception e){
	            	//System.out.println(linea.split(" ")[2]+" "+openStr+". "+e.getMessage());
	            }
         }else{
         	//System.out.println("A CONVERTIR: "+linea);
         		String[] valuesA = linea.split(";")[1].split(delimiter);
         		String[] valuesB = linea.split(";")[2].split(delimiter);
         		if (delimiter=="."){
         			valuesA = linea.split(";")[1].split("\\.");
             		valuesB = linea.split(";")[2].split("\\.");
         		}
         		         		
         		ask 	= convert3(valuesA); 
         		bid 	= convert3(valuesB);
         		
         		//System.out.println(ask+" "+bid);
         }
		
		 
		 tick = new TickQuote();
		 
		 tick.setAsk(ask);
		 tick.setBid(bid);
		 tick.setAskvolume(askVolume);
		 tick.setBidvolume(bidVolume);
		 tick.setYear(year);
		 tick.setMonth(month);
		 tick.setDay(day);
		 tick.setHh(hh);
		 tick.setMm(mm);
		 tick.setSs(ss);
		 
		return tick;
	}
	
	public static ArrayList<PositionTraderDale> retrieveTraderDaleTrade(String fileName){
		
		ArrayList<PositionTraderDale> trades = new ArrayList<PositionTraderDale> ();
		
		File archivo = null;
	    FileReader fr = null;
	    BufferedReader br = null;
	    Calendar cal = Calendar.getInstance();
	    try {
	    	// Apertura del fichero y creacion de BufferedReader para poder
	        // hacer una lectura comoda (disponer del metodo readLine()).
	        archivo = new File (fileName);
	        fr = new FileReader (archivo);
	        br = new BufferedReader(fr);

	        // Lectura del fichero
	        String line;
	        int i=0;
	        PositionTraderDale trade = null;
	        while((line=br.readLine())!=null){
	        		        	
	        	String afileName = "";
	        	String pair = line.split(",")[0].trim();
	        	int day = Integer.valueOf(line.split(",")[1].trim());
	        	int month = Integer.valueOf(line.split(",")[2].trim());
	        	int year = Integer.valueOf(line.split(",")[3].trim());
	        	int mode = Integer.valueOf(line.split(",")[4].trim());
	        	int entry = Integer.valueOf(line.split(",")[5].trim());
	        	int tp = 0;
	        	int sl = 0;
	        	
	        	PositionTraderDale posDale	= new PositionTraderDale(afileName,day,month,year,mode,entry,tp,sl);
	        	posDale.setPair(pair);
	        	
	        	trades.add(posDale);
	    		
	        	i++;
	        }    
	    }catch(Exception e){
	    	e.printStackTrace();
	    }finally{
	         // En el finally cerramos el fichero, para asegurarnos
	         // que se cierra tanto si todo va bien como si salta 
	         // una excepcion.
	         try{                    
	            if( null != fr ){   
	               fr.close();     
	            }                  
	         }catch (Exception e2){ 
	            e2.printStackTrace();
	         }
	   }	
		
		
		return trades;
	}
	
	public static ArrayList<TickQuote> retrieveTickQuotes(String fileName,int startLine){
	
		ArrayList<TickQuote> data = new ArrayList<TickQuote>();
		File archivo = null;
	    FileReader fr = null;
	    BufferedReader br = null;
	    Calendar cal = Calendar.getInstance();
	    try {
	    	// Apertura del fichero y creacion de BufferedReader para poder
	        // hacer una lectura comoda (disponer del metodo readLine()).
	        archivo = new File (fileName);
	        fr = new FileReader (archivo);
	        br = new BufferedReader(fr);

	        // Lectura del fichero
	        String line;
	        int i=0;
	        TickQuote lastQ = null;
	        while((line=br.readLine())!=null){
	        	if (i>=startLine){
	        		 TickQuote q = decodeTickQuote(line); 
	        		 TickQuote.getCalendar(cal, q);
	        		 int s = cal.get(Calendar.SECOND); 
	        		 /*if (s%5==0){
	        			 //System.out.println("add1: "+q.toString()+" "+q.getOpen()+" "+q.getOpen5());
	        			 data.add(q);
	        		 }else if (lastQ==null){// || !TickQuote.isSame(q,lastQ)){
	        			 //System.out.println("add2: "+q.toString()+" "+q.getOpen()+" "+q.getOpen5());
	        			 data.add(q);
	        		 }*/
	        		 data.add(q);
	        		 //System.out.println(q.toString());
	        		 lastQ = q;
	        	}
	        	i++;
	        }    
	    }catch(Exception e){
	    	e.printStackTrace();
	    }finally{
	         // En el finally cerramos el fichero, para asegurarnos
	         // que se cierra tanto si todo va bien como si salta 
	         // una excepcion.
	         try{                    
	            if( null != fr ){   
	               fr.close();     
	            }                  
	         }catch (Exception e2){ 
	            e2.printStackTrace();
	         }
	   }	
		
		
		
		return data;
	}
	
	public static int evaluateOrder(ArrayList<TickQuote> data,Calendar cal1,Calendar cal2,
			int value,int tp,int sl,
			int mode,
			boolean debug
			){
		
		Calendar cal = Calendar.getInstance();
		boolean isExecuted = false;
		int win = 0;
		int executedPrice = 0;
		int executedTP = 0;
		int executedSL = 0;
		boolean isHourOk = false;
		for (int i=0;i<=data.size()-1;i++){
			TickQuote t = data.get(i);
			
			TickQuote.getCalendar(cal, t);
			
			
			if (cal.getTimeInMillis()>cal2.getTimeInMillis()) break;
			
			if (cal.getTimeInMillis()>=cal1.getTimeInMillis() && cal.getTimeInMillis()<=cal2.getTimeInMillis()){
				isHourOk = true;
			}else{
				isHourOk = false;
			}
			
			if (!isExecuted){
				if (isHourOk){
					if (mode==1){
						if (t.getAsk()<=value){
							isExecuted = true;
							executedPrice = t.getAsk();
							executedTP = executedPrice+tp*10;
							executedSL = executedPrice-sl*10;
							if (debug){
								System.out.println("[LONG EXECUTED AT] "+t.toString());
							}
						}
					}else if (mode==-1){
						if (t.getBid()>=value){
							isExecuted = true;
							executedPrice = t.getBid();
							executedTP = executedPrice-tp*10;
							executedSL = executedPrice+sl*10;
							if (debug){
								System.out.println("[SHORT EXECUTED AT] "+t.toString());
							}
						}
					}
				}
			}else{
				if (mode==1){
					if (t.getBid()>=executedTP){
						win = 1;
						if (debug){
							System.out.println("[LONG WIN] "+t.toString());
						}
						break;
					}else if (t.getBid()<=executedSL){
						win = -1;
						if (debug){
							System.out.println("[LONG LOSS] "+t.toString());
						}
						break;
					} 
				}else if (mode==-1){
					if (t.getAsk()<=executedTP){
						win = 1;
						if (debug){
							System.out.println("[SHORT WIN] "+t.toString());
						}
						break;						
					}else if (t.getAsk()>=executedSL){
						win = -1;
						if (debug){
							System.out.println("[SHORT LOSS] "+t.toString());
						}
						break;
					} 
				}
			}
		}
		
		if (debug)
			System.out.println("RESULT "+win);
		
		return win;
	}
	
	public static void printOpens(ArrayList<TickQuote> data){
		
		
		int lastDay = -1;
		Calendar cal = Calendar.getInstance();
		for (int i=0;i<data.size();i++){
			TickQuote t = data.get(i);
			TickQuote.getCalendar(cal, t);
			
			int day = cal.get(Calendar.DAY_OF_YEAR);
			
			if (day!=lastDay){
				
				System.out.println(t.toString()+" || "+(int)((t.getAsk()+t.getBid())/2));
				
				lastDay = day;
			}
		}
		
	}
	
	public static void adjustTimeZone(ArrayList<TickQuote> data){
		
		Calendar cal = Calendar.getInstance();
		for (int i=0;i<data.size();i++){
			TickQuote t = data.get(i);
			
			TickQuote.getCalendar(cal, t);
			
			int offset = DateUtils.calculatePepperGMTOffset(cal);
	        //System.out.println("cal antes: "+DateUtils.datePrint(cal));
	        cal.add(Calendar.HOUR_OF_DAY, offset);
	        //System.out.println("cal despues: "+DateUtils.datePrint(cal)+' '+offset);
	        t.setCal(cal);
		}
	}
	
	public static void doCheckVolumes(ArrayList<Double> volumes){
		
		double avg = 0;
		int count = 0;
		for (int i=0;i<volumes.size();i++){
			double vol = volumes.get(i);
			if (vol>0){
				avg += vol;
				count++;
			}
		}
		avg = avg/count;
		
		for (int i=0;i<volumes.size();i++){
			double vol = volumes.get(i);
			if (vol>0){
				double per = vol/avg;
				System.out.println(i+" "+PrintUtils.Print2dec(vol, false)+" "+PrintUtils.Print2dec(per, false));
			}
		}
	}
	
	public static void doBasicSystem(ArrayList<TickQuote> data,int tp,int sl,int distance){
		
		int wins = 0;
		int losses = 0;
		
		Calendar calfrom = Calendar.getInstance();
		Calendar calto = Calendar.getInstance();
		Calendar cal1 = Calendar.getInstance();
		Calendar cal = Calendar.getInstance();
		Calendar cal3 = Calendar.getInstance();
		Calendar cal4 = Calendar.getInstance();
		int lastDay = -1;
		int open = -1;
		for (int i=1;i<data.size();i++){
			TickQuote t1 = data.get(i-1);
			TickQuote t = data.get(i);
			TickQuote.getCalendar(cal1, t1);
			TickQuote.getCalendar(cal, t);
			
			
			int day = cal.get(Calendar.DAY_OF_YEAR);
			
			if (lastDay!=day){
				open = (t.getBid()+t.getAsk())/20;
				if (lastDay!=-1){
					//se calculan los volumenes
					calfrom.set(cal1.get(Calendar.YEAR),cal1.get(Calendar.MONTH), cal1.get(Calendar.DAY_OF_MONTH),0, 0, 0);
					calto.set(cal1.get(Calendar.YEAR),cal1.get(Calendar.MONTH), cal1.get(Calendar.DAY_OF_MONTH),23, 59, 59);
					
					ArrayList<Double> volumes = DataUtils.getDataVolumes(data, calfrom, calto);
					//System.out.println("***VOLUMENES DIA : "+DateUtils.datePrint(cal1)+" || "+DateUtils.datePrint(calfrom)+" "+DateUtils.datePrint(calto)+" || "+open);
					for (int v=0;v<volumes.size();v++){			
						double vol = volumes.get(v);
						if (vol>0){
							//System.out.println(v+" "+PrintUtils.Print2dec(vol, false));
						}
					}
					
					//metodo simple evaluar con fijo de pips
					cal3.set(cal.get(Calendar.YEAR),cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH),0, 0, 0);
					cal4.set(cal.get(Calendar.YEAR),cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH),23, 59, 59);
					int winS = DataUtils.evaluateOrder(data, cal3, cal4, open*10+distance*10, tp, sl, -1,false);
					int winL = DataUtils.evaluateOrder(data, cal3, cal4, open*10-distance*10, tp, sl, 1,false);
					//DataUtils.evaluateOrder(data, cal1, cal2, 112010, 10, 12, -1,true);
					
					if (winS==1) wins++;
					if (winS==-1) losses++;
					if (winL==1) wins++;
					if (winL==-1) losses++;
				}
				
				
				
				lastDay = day;
			}
		}
		
		int trades = wins+losses;
		
		double winPer = wins*100.0/trades;
		double avg = (wins*tp-losses*sl)*1.0/trades;
		
		System.out.println(
				tp+" "+sl+" "+distance
				+" || "
				+" "+trades
				+" "+PrintUtils.Print2dec(winPer, false)
				+" "+PrintUtils.Print2dec(avg, false)
				);
		
	}
	
    public static void doBasicSystem2(ArrayList<TickQuote> data,
    		ArrayList<Integer> maxMins,
    		int maxMin,
    		int tp,int sl,int distance,
    		double minVolFactor,
    		double factor,
    		boolean debug
    		){
		
		int wins = 0;
		int losses = 0;
		
		ArrayList<PositionShort> positions = new ArrayList<PositionShort>();
		ArrayList<Double> volumes = new ArrayList<Double>();
		ArrayList<Integer> shortValues = new ArrayList<Integer>();
		ArrayList<Integer> longValues = new ArrayList<Integer>();
		
		for (int i=0;i<20000;i++) volumes.add(0.0);
		
		Calendar calfrom = Calendar.getInstance();
		Calendar calto = Calendar.getInstance();
		Calendar cal1 = Calendar.getInstance();
		Calendar cal = Calendar.getInstance();
		Calendar cal3 = Calendar.getInstance();
		Calendar cal4 = Calendar.getInstance();
		int lastDay = -1;
		int open = -1;
		for (int i=1;i<data.size();i++){
			TickQuote t1 = data.get(i-1);
			TickQuote t = data.get(i);
			TickQuote.getCalendar(cal1, t1);
			TickQuote.getCalendar(cal, t);
			
			
			int day = cal.get(Calendar.DAY_OF_YEAR);
			int h = cal.get(Calendar.HOUR_OF_DAY);
			
			if (lastDay!=day){
				open = (t.getBid()+t.getAsk())/20;
				if (lastDay!=-1){
					if (debug)
					System.out.println("****NEW DAY : "+open);
					//se calculan los volumenes
					calfrom.set(cal1.get(Calendar.YEAR),cal1.get(Calendar.MONTH), cal1.get(Calendar.DAY_OF_MONTH),0, 0, 0);
					calto.set(cal1.get(Calendar.YEAR),cal1.get(Calendar.MONTH), cal1.get(Calendar.DAY_OF_MONTH),23, 59, 59);
					
					//ArrayList<Double> volumes = DataUtils.getDataVolumes(data, calfrom, calto);
					//System.out.println("***VOLUMENES DIA : "+DateUtils.datePrint(cal1)+" || "+DateUtils.datePrint(calfrom)+" "+DateUtils.datePrint(calto)+" || "+open);
					double avg = 0;
					int count=0;
					for (int v=0;v<volumes.size();v++){			
						double vol = volumes.get(v);
						if (vol>0){
							//System.out.println(v+" "+PrintUtils.Print2dec(vol, false));
							avg += vol;
							count++;
						}
					}
					
					double avgVol = avg/count;
					
					double minVol = avgVol*minVolFactor;
					
					//borramos posiciones no abiertas
					int p = 0;
					while (p<positions.size()){
						PositionShort pos = positions.get(p);
						if (pos.getPositionStatus()!=PositionStatus.OPEN){
							positions.remove(p);
						}else{
							p++;
						}
					}
					
					//buscar entradas para SHORT
					//shortValues.clear();
					for (int sh = open+0;sh<= open+100;sh+=1){
						double valueSh1 = volumes.get(sh-1);
						double valueSh = volumes.get(sh);
						
						//if (valueSh>=valueSh1*2 && valueSh1>=400){
						if (valueSh>=minVol 
								&& valueSh>=valueSh1*factor && valueSh1>0
								){
							//System.out.println("added: "+sh+" "+valueSh+" || "+(sh-1)+" "+valueSh1);
							//shortValues.add(sh);
							//break;
							PositionShort pos = new PositionShort();
							pos.setPositionStatus(PositionStatus.PENDING);
							pos.setEntry(sh*10);
							pos.setTp(sh*10-tp*10);
							pos.setSl(sh*10+sl*10);
							pos.setPositionType(PositionType.SHORT);
							positions.add(pos);
							if (debug){
								System.out.println("[SHORT ADDED] "+pos.getEntry());
							}
							break;
						}
					}
					
					//buscar entradas para LONG
					//longValues.clear();
					for (int lo = open-100;lo<=open-0;lo+=1){
						double valueLo1 = volumes.get(lo-1);
						double valueLo = volumes.get(lo);
						
						//if (valueLo>=valueLo1*2 && valueLo1>=400){
						if (valueLo>=minVol 
								&& valueLo>=valueLo1*factor && valueLo1>0
								){
							//longValues.add(lo);
							//break;
							PositionShort pos = new PositionShort();
							pos.setPositionStatus(PositionStatus.PENDING);
							pos.setEntry(lo*10);
							pos.setTp(lo*10+tp*10);
							pos.setSl(lo*10-sl*10);
							pos.setPositionType(PositionType.LONG);
							positions.add(pos);
							if (debug){
								System.out.println("[LONG ADDED] "+pos.getEntry());
							}
							break;
						}
					}
										
				}
				
				//limpiamos volumenes
				volumes.clear();
				for (int c=0;c<20000;c++) volumes.add(0.0);
				
				lastDay = day;
			}
			
			//evaluamos posiciones limite
			int p = 0;
			while (p<positions.size()){
				PositionShort pos = positions.get(p);
				boolean isClosed = false;
				int win = 0;
				int totalOpen = PositionShort.countTotal(positions, PositionStatus.OPEN);
				if (pos.getPositionStatus()==PositionStatus.PENDING && totalOpen<=5){
					int thr1 = maxMins.get(i-1);
					if (h>=0 && h<=9){
						if (pos.getPositionType()==PositionType.SHORT){
							if (t.getBid()>=pos.getEntry()
									&& (int)t.getBid()/10>=(int)pos.getEntry()/10 && (int)t.getBid()/10<=(int)pos.getEntry()/10-5 
									&& thr1>=maxMin
									){	
								if (debug){
									System.out.println("[SHORT EXECUTED AT] "+t.toString()+" || "+pos.getEntry()+" "+pos.getTp()+" "+pos.getSl());
								}
								pos.setEntry(t.getBid());
								pos.setTp(t.getBid()-10*tp);
								pos.setSl(t.getBid()+10*sl);
								pos.setPositionStatus(PositionStatus.OPEN);
								/*if (debug){
									System.out.println("[SHORT EXECUTED AT] "+t.toString()+" || "+pos.getEntry()+" "+pos.getTp()+" "+pos.getSl());
								}*/
							}
						}else if (pos.getPositionType()==PositionType.LONG){
							if (t.getAsk()<=pos.getEntry()
									&& (int)t.getAsk()/10>=(int)pos.getEntry()/10-5 && (int)t.getAsk()/10<=(int)pos.getEntry()/10
									&& thr1<=-maxMin
									){	
								if (debug){
									System.out.println("[LONG EXECUTED AT] "+t.toString()+" || "+pos.getEntry()+" "+pos.getTp()+" "+pos.getSl());
								}
								pos.setEntry(t.getAsk());
								pos.setTp(t.getAsk()+10*tp);
								pos.setSl(t.getAsk()-10*sl);
								pos.setPositionStatus(PositionStatus.OPEN);
								/*if (debug){
									System.out.println("[LONG EXECUTED AT] "+t.toString()+" || "+pos.getEntry()+" "+pos.getTp()+" "+pos.getSl());
								}*/
							}
						}
					}//h
				}
															
				if (pos.getPositionStatus()==PositionStatus.OPEN){
					
					if (pos.getPositionType()==PositionType.SHORT){
						if (t.getAsk()<=pos.getTp()){
							isClosed = true;
							win = 1;
							if (debug){
								System.out.println("[SHORT WIN] "+t.toString()+" || "+pos.getTp());
							}
						}else if (t.getAsk()>=pos.getSl()){
							isClosed = true;
							win = -1;
							if (debug){
								System.out.println("[SHORT LOSS] "+t.toString());
							}
						} 
					}else if (pos.getPositionType()==PositionType.LONG){
						if (t.getBid()>=pos.getTp()){
							isClosed = true;
							win = 1;
							if (debug){
								System.out.println("[LONG WIN] "+t.toString()+" || "+pos.getTp());
							}
						}else if (t.getBid()<=pos.getSl()){
							isClosed = true;
							win = -1;
							if (debug){
								System.out.println("[LONG LOSS] "+t.toString());
							}
						} 
					}
				}
				
				if (isClosed){
					if (win==1) wins++;
					if (win==-1) losses++;
					positions.remove(p);
				}else{
					p++;
				}
			}
			
			
			//acumulamos volumenes de este dia
			int avg = (t.getAsk()+t.getBid())/20;
			double vol = t.getAskvolume()+t.getBidvolume();						
			volumes.set(avg, volumes.get(avg)+vol);		
		}
		
		int trades = wins+losses;
		
		double winPer = wins*100.0/trades;
		double avg = (wins*tp-losses*sl)*1.0/trades;
		double pf = (wins*tp)*1.0/(losses*sl);
		
		System.out.println(
				tp+" "+sl+" "+distance+" "+PrintUtils.Print2dec(minVolFactor, false)+" "+PrintUtils.Print2dec(factor, false)+" "+maxMin
				+" || "
				+" "+trades
				+" "+PrintUtils.Print2dec(winPer, false)
				+" "+PrintUtils.Print2dec(avg, false)
				+" "+PrintUtils.Print2dec(pf, false)
				);
		
	}
    
    public static ArrayList<Integer> calculateMaxMinSecTickQuote(
			ArrayList<TickQuote> data) {
		
		int count5000 = 0;
		Calendar calFrom = Calendar.getInstance();
		Calendar calTo = Calendar.getInstance();
		ArrayList<Integer> maxMins = new ArrayList<Integer>();
		for (int i=0;i<data.size();i++){
			TickQuote q = data.get(i);
			TickQuote.getCalendar(calTo, q);
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
				TickQuote qj = data.get(j);
				boolean isHigh = false;
				boolean isLow = false;
				
				if (q.getAsk()>qj.getAsk()){
					isHigh = true;
				}
				if (q.getBid()<qj.getBid()){
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
			if (nbarsH>=nbarsL){
				maxMin=nbarsH;
			}
			if (nbarsH<nbarsL){
				maxMin=-nbarsL;
			}
			int secs=0;
			if (i-Math.abs(maxMin)>=0)
				TickQuote.getCalendar(calFrom, data.get(i-Math.abs(maxMin)));
			
			secs = (int) ((calTo.getTimeInMillis()-calFrom.getTimeInMillis())/1000);
			maxMins.add(secs);
		}
		return maxMins;
	}

	public static void main(String[] args) {
		
		//String pathEURUSD = "C:\\fxdata\\EURUSD_UTC_Ticks_Bid_2016.11.01_2017.01.01.csv";
		String pathEURUSD91 = "C:\\fxdata\\EURUSD_UTC_Ticks_Bid_2009.01.01_2009.03.01.csv";
		String pathEURUSD92 = "C:\\fxdata\\EURUSD_UTC_Ticks_Bid_2009.03.01_2009.05.01.csv";
		String pathEURUSD93 = "C:\\fxdata\\EURUSD_UTC_Ticks_Bid_2009.05.01_2009.07.01.csv";
		String pathEURUSD94 = "C:\\fxdata\\EURUSD_UTC_Ticks_Bid_2009.07.01_2009.09.01.csv";
		String pathEURUSD95 = "C:\\fxdata\\EURUSD_UTC_Ticks_Bid_2009.09.01_2009.11.01.csv";
		String pathEURUSD96 = "C:\\fxdata\\EURUSD_UTC_Ticks_Bid_2009.11.01_2010.01.01.csv";
		String pathEURUSD01 = "C:\\fxdata\\EURUSD_UTC_Ticks_Bid_2010.01.01_2010.03.01.csv";
		String pathEURUSD02 = "C:\\fxdata\\EURUSD_UTC_Ticks_Bid_2010.03.01_2010.05.01.csv";
		String pathEURUSD03 = "C:\\fxdata\\EURUSD_UTC_Ticks_Bid_2010.05.01_2010.07.01.csv";
		String pathEURUSD04 = "C:\\fxdata\\EURUSD_UTC_Ticks_Bid_2010.07.01_2010.09.01.csv";
		String pathEURUSD05 = "C:\\fxdata\\EURUSD_UTC_Ticks_Bid_2010.09.01_2010.11.01.csv";
		String pathEURUSD06 = "C:\\fxdata\\EURUSD_UTC_Ticks_Bid_2010.11.01_2011.01.01.csv";
		String pathEURUSD11 = "C:\\fxdata\\EURUSD_UTC_Ticks_Bid_2011.01.01_2011.03.01.csv";
		String pathEURUSD12 = "C:\\fxdata\\EURUSD_UTC_Ticks_Bid_2011.03.01_2011.05.01.csv";
		String pathEURUSD13 = "C:\\fxdata\\EURUSD_UTC_Ticks_Bid_2011.05.01_2011.07.01.csv";
		String pathEURUSD14 = "C:\\fxdata\\EURUSD_UTC_Ticks_Bid_2011.07.01_2011.09.01.csv";
		String pathEURUSD15 = "C:\\fxdata\\EURUSD_UTC_Ticks_Bid_2011.09.01_2011.11.01.csv";
		String pathEURUSD16 = "C:\\fxdata\\EURUSD_UTC_Ticks_Bid_2011.11.01_2012.01.01.csv";
		String pathEURUSD21 = "C:\\fxdata\\EURUSD_UTC_Ticks_Bid_2012.01.01_2012.03.01.csv";
		String pathEURUSD22 = "C:\\fxdata\\EURUSD_UTC_Ticks_Bid_2012.03.01_2012.05.01.csv";
		String pathEURUSD23 = "C:\\fxdata\\EURUSD_UTC_Ticks_Bid_2012.05.01_2012.07.01.csv";
		String pathEURUSD24 = "C:\\fxdata\\EURUSD_UTC_Ticks_Bid_2012.07.01_2012.09.01.csv";
		String pathEURUSD25 = "C:\\fxdata\\EURUSD_UTC_Ticks_Bid_2012.09.01_2012.11.01.csv";
		String pathEURUSD26 = "C:\\fxdata\\EURUSD_UTC_Ticks_Bid_2012.11.01_2013.01.01.csv";
		String pathEURUSD31 = "C:\\fxdata\\EURUSD_UTC_Ticks_Bid_2013.01.01_2013.03.01.csv";
		String pathEURUSD32 = "C:\\fxdata\\EURUSD_UTC_Ticks_Bid_2013.03.01_2013.05.01.csv";
		String pathEURUSD33 = "C:\\fxdata\\EURUSD_UTC_Ticks_Bid_2013.05.01_2013.07.01.csv";
		String pathEURUSD34 = "C:\\fxdata\\EURUSD_UTC_Ticks_Bid_2013.07.01_2013.09.01.csv";
		String pathEURUSD35 = "C:\\fxdata\\EURUSD_UTC_Ticks_Bid_2013.09.01_2013.11.01.csv";
		String pathEURUSD36 = "C:\\fxdata\\EURUSD_UTC_Ticks_Bid_2013.11.01_2014.01.01.csv";
		String pathEURUSD41 = "C:\\fxdata\\EURUSD_UTC_Ticks_Bid_2014.01.01_2014.03.01.csv";
		String pathEURUSD42 = "C:\\fxdata\\EURUSD_UTC_Ticks_Bid_2014.03.01_2014.05.01.csv";
		String pathEURUSD43 = "C:\\fxdata\\EURUSD_UTC_Ticks_Bid_2014.05.01_2014.07.01.csv";
		String pathEURUSD44 = "C:\\fxdata\\EURUSD_UTC_Ticks_Bid_2014.07.01_2014.09.01.csv";
		String pathEURUSD45 = "C:\\fxdata\\EURUSD_UTC_Ticks_Bid_2014.09.01_2014.11.01.csv";
		String pathEURUSD46 = "C:\\fxdata\\EURUSD_UTC_Ticks_Bid_2014.11.01_2015.01.01.csv";
		String pathEURUSD51 = "C:\\fxdata\\EURUSD_UTC_Ticks_Bid_2015.01.01_2015.03.01.csv";
		String pathEURUSD52 = "C:\\fxdata\\EURUSD_UTC_Ticks_Bid_2015.03.01_2015.05.01.csv";
		String pathEURUSD53 = "C:\\fxdata\\EURUSD_UTC_Ticks_Bid_2015.05.01_2015.07.01.csv";
		String pathEURUSD54 = "C:\\fxdata\\EURUSD_UTC_Ticks_Bid_2015.07.01_2015.09.01.csv";
		String pathEURUSD55 = "C:\\fxdata\\EURUSD_UTC_Ticks_Bid_2015.09.01_2015.11.01.csv";
		String pathEURUSD56 = "C:\\fxdata\\EURUSD_UTC_Ticks_Bid_2015.11.01_2016.01.01.csv";
		String pathEURUSD1 = "C:\\fxdata\\EURUSD_UTC_Ticks_Bid_2016.01.01_2016.03.01.csv";
		String pathEURUSD2 = "C:\\fxdata\\EURUSD_UTC_Ticks_Bid_2016.03.01_2016.05.01.csv";
		String pathEURUSD3 = "C:\\fxdata\\EURUSD_UTC_Ticks_Bid_2016.05.01_2016.07.01.csv";
		String pathEURUSD4 = "C:\\fxdata\\EURUSD_UTC_Ticks_Bid_2016.07.01_2016.09.01.csv";
		String pathEURUSD5 = "C:\\fxdata\\EURUSD_UTC_Ticks_Bid_2016.09.01_2016.11.01.csv";
		String pathEURUSD2009_1 = "C:\\fxdata\\EURUSD_UTC_Ticks_Bid_2009.01.01_2009.06.30.csv";
		String pathEURUSD2009_2 = "C:\\fxdata\\EURUSD_UTC_Ticks_Bid_2009.07.01_2009.12.31.csv";
		String pathEURUSD2010_1 = "C:\\fxdata\\EURUSD_UTC_Ticks_Bid_2010.01.01_2010.06.30.csv";
		String pathEURUSD2010_2 = "C:\\fxdata\\EURUSD_UTC_Ticks_Bid_2010.07.01_2010.12.31.csv";
		String pathEURUSD2011_1 = "C:\\fxdata\\EURUSD_UTC_Ticks_Bid_2011.01.01_2011.06.30.csv";
		String pathEURUSD2011_2 = "C:\\fxdata\\EURUSD_UTC_Ticks_Bid_2011.07.01_2011.12.31.csv";
		String pathEURUSD2012_1 = "C:\\fxdata\\EURUSD_UTC_Ticks_Bid_2012.01.01_2012.06.30.csv";
		String pathEURUSD2012_2 = "C:\\fxdata\\EURUSD_UTC_Ticks_Bid_2012.07.01_2012.12.31.csv";
		String pathEURUSD2013_1 = "C:\\fxdata\\EURUSD_UTC_Ticks_Bid_2013.01.01_2013.06.30.csv";
		String pathEURUSD2013_2 = "C:\\fxdata\\EURUSD_UTC_Ticks_Bid_2013.07.01_2013.12.31.csv";
		String pathEURUSD2014_1 = "C:\\fxdata\\EURUSD_UTC_Ticks_Bid_2014.01.01_2014.06.30.csv";
		String pathEURUSD2014_2 = "C:\\fxdata\\EURUSD_UTC_Ticks_Bid_2014.07.01_2014.12.31.csv";
		String pathEURUSD2015_1 = "C:\\fxdata\\EURUSD_UTC_Ticks_Bid_2015.01.01_2015.06.30.csv";
		String pathEURUSD2015_2 = "C:\\fxdata\\EURUSD_UTC_Ticks_Bid_2015.07.01_2015.12.31.csv";
		String pathEURUSD2016_1 = "C:\\fxdata\\EURUSD_UTC_Ticks_Bid_2016.01.01_2016.06.30.csv";
		String pathEURUSD2016_2 = "C:\\fxdata\\EURUSD_UTC_Ticks_Bid_2016.07.01_2016.12.31.csv";
		
		ArrayList<String> files = new ArrayList<String>();
		ArrayList<String> files2 = new ArrayList<String>();
		files.add(pathEURUSD91);files.add(pathEURUSD92);files.add(pathEURUSD93);
		files.add(pathEURUSD94);files.add(pathEURUSD95);files.add(pathEURUSD96);
		files.add(pathEURUSD01);files.add(pathEURUSD02);files.add(pathEURUSD03);
		files.add(pathEURUSD04);files.add(pathEURUSD05);files.add(pathEURUSD06);
		files.add(pathEURUSD11);files.add(pathEURUSD12);files.add(pathEURUSD13);
		files.add(pathEURUSD14);files.add(pathEURUSD15);files.add(pathEURUSD16);
		files.add(pathEURUSD21);files.add(pathEURUSD22);files.add(pathEURUSD23);
		files.add(pathEURUSD24);files.add(pathEURUSD25);files.add(pathEURUSD26);
		files.add(pathEURUSD31);files.add(pathEURUSD32);files.add(pathEURUSD33);
		files.add(pathEURUSD34);files.add(pathEURUSD35);files.add(pathEURUSD36);
		files.add(pathEURUSD41);files.add(pathEURUSD42);files.add(pathEURUSD43);
		files.add(pathEURUSD44);files.add(pathEURUSD45);files.add(pathEURUSD46);
		files.add(pathEURUSD51);files.add(pathEURUSD52);files.add(pathEURUSD53);
		files.add(pathEURUSD54);files.add(pathEURUSD55);files.add(pathEURUSD56);
		files.add(pathEURUSD1);files.add(pathEURUSD2);files.add(pathEURUSD3);
		files.add(pathEURUSD4);files.add(pathEURUSD5);
		files2.add(pathEURUSD2009_1);files2.add(pathEURUSD2009_2);
		files2.add(pathEURUSD2010_1);files2.add(pathEURUSD2010_2);
		files2.add(pathEURUSD2011_1);files2.add(pathEURUSD2011_2);
		files2.add(pathEURUSD2012_1);files2.add(pathEURUSD2012_2);
		files2.add(pathEURUSD2013_1);files2.add(pathEURUSD2013_2);
		files2.add(pathEURUSD2014_1);files2.add(pathEURUSD2014_2);
		files2.add(pathEURUSD2015_1);files2.add(pathEURUSD2015_2);
		files2.add(pathEURUSD2016_1);files2.add(pathEURUSD2016_2);
		
		/*ArrayList<ArrayList<Double>>  volumesDaily = new ArrayList<ArrayList<Double>>();
		for (int i=0;i<20000;i++){
			ArrayList<Double> values = new ArrayList<Double>();
			for (int j=0;j<=366;j++) values.add(0.0);
			 volumesDaily.add(values);
		}
		
		int initial = 0;
		for (int year=2014;year<=2016;year++){	
			for (int i=0;i<20000;i++){
				ArrayList<Double> values = new ArrayList<Double>();
				for (int j=0;j<=366;j++) values.add(0.0);
				 volumesDaily.set(i,values);
			}
			for (int i=initial;i<=initial+1;i++){
				String fileName = files2.get(i);
				ArrayList<TickQuote> data = DataUtils.retrieveTickQuotes(fileName,1);
				DataUtils.adjustTimeZone(data);
				System.out.println("ticks: "+data.size()+" "+fileName);	
				//extraccion de volumenes
				DataUtils.getDalyDataVolumes(data, volumesDaily);			
			}
			initial+=2;
			DataUtils.saveVolumesToFile("C:\\fxdata\\volumes"+year+".csv",volumesDaily);
		}*/
		
		int totalSpread = 0;
		int count = 0;
		int h1= 16;
		int h2= 23;
		Calendar cal = Calendar.getInstance();
		for (int year=2016;year<=2016;year++){	
			String fileNames12 ="C:\\fxdata\\USDJPY_UTC_Ticks_Bid_2012.01.01_2012.12.31.csv";
			String fileNames13 ="C:\\fxdata\\USDJPY_UTC_Ticks_Bid_2013.01.01_2013.12.31.csv";
			String fileNames14 ="C:\\fxdata\\USDJPY_UTC_Ticks_Bid_2014.01.01_2014.12.31.csv";
			String fileNames15 ="C:\\fxdata\\USDJPY_UTC_Ticks_Bid_2015.01.01_2015.12.31.csv";
			String fileNames22 ="C:\\fxdata\\AUDUSD_UTC_Ticks_Bid_2012.01.01_2012.12.31.csv";
			String fileNames23 ="C:\\fxdata\\AUDUSD_UTC_Ticks_Bid_2013.01.01_2013.12.31.csv";
			String fileNames24 ="C:\\fxdata\\AUDUSD_UTC_Ticks_Bid_2014.01.01_2014.12.31.csv";
			String fileNames25 ="C:\\fxdata\\AUDUSD_UTC_Ticks_Bid_2015.01.01_2015.12.31.csv";
			String fileNames32 ="C:\\fxdata\\GBPUSD_UTC_Ticks_Bid_2012.01.01_2012.12.31.csv";
			String fileNames33 ="C:\\fxdata\\GBPUSD_UTC_Ticks_Bid_2013.01.01_2013.12.31.csv";
			String fileNames34 ="C:\\fxdata\\GBPUSD_UTC_Ticks_Bid_2014.01.01_2014.12.31.csv";
			String fileNames35 ="C:\\fxdata\\GBPUSD_UTC_Ticks_Bid_2015.01.01_2015.12.31.csv";
			files2.clear();
			files2.add(fileNames12);
			files2.add(fileNames13);
			files2.add(fileNames14);
			files2.add(fileNames15);
			files2.add(fileNames22);
			files2.add(fileNames23);
			files2.add(fileNames24);
			files2.add(fileNames25);
			files2.add(fileNames32);
			files2.add(fileNames33);
			files2.add(fileNames34);
			files2.add(fileNames35);
			for (int f=0;f<=files2.size()-1;f++){
				String fileName = files2.get(f);
				ArrayList<TickQuote> data = DataUtils.retrieveTickQuotes(fileName,1);
				DataUtils.adjustTimeZone(data);
				//System.out.println("ticks: "+data.size()+" "+fileName);	
				totalSpread = 0;
				count = 0;
				for (int i=0;i<data.size();i++){
					TickQuote t = data.get(i);
					TickQuote.getCalendar(cal, t);
					int h = cal.get(Calendar.HOUR_OF_DAY);
					
					if (h>=h1 && h<=h2){
						int spread = t.getBid()-t.getAsk();
						if (spread*0.1>=4.0) System.out.println(PrintUtils.Print2dec(spread*0.1,false)+" || "+t.toString());
						totalSpread += Math.abs(t.getBid()-t.getAsk());
						count++;
					}
				}
				double avg = totalSpread*0.1/count;
				System.out.println(fileName+" || "+PrintUtils.Print2dec(avg, false));
			}
		}
		
	}

	private static void saveVolumesToFile(String fileName, ArrayList<ArrayList<Double>> volumesDaily) {
		
		PrintWriter pw;
		try {
			pw = new PrintWriter(new File(fileName));
			 
			boolean toPrint = false; 
			for (int p=0;p<volumesDaily.size();p++){
				ArrayList<Double> values = volumesDaily.get(p);
				StringBuilder sb = new StringBuilder();
				sb.append(p);sb.append(';');
				toPrint = false; 
				for (int d=0;d<values.size();d++){
			        double value = values.get(d);
			        if (value>0){
			        	toPrint = true; 
			        	System.out.println("[saveVolumesToFile] "+p+" "+d+" "+value);
			        }
			        sb.append(PrintUtils.Print2dec(value, false));sb.append(';');			       
				}
				sb.append('\n');	
				if (toPrint)
			    pw.write(sb.toString());
			}
			pw.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * Deveulve un entero representando en numero de segundos que han pasado desde 1980
	 * @param year
	 * @param month
	 * @param day
	 * @param hh
	 * @param mm
	 * @param ss
	 * @return
	 */
	public static int getDateInSeconds(short year, byte month, byte day, byte hh, byte mm, byte ss) {
		// TODO Auto-generated method stub
		
		int value = (year-1980)*12*30*24*3600//año
					+month*30*24*3600//mes
					+day*24*3600
					+hh*3600
					+mm*60
					+ss
				;		
		return value;
	}

}
