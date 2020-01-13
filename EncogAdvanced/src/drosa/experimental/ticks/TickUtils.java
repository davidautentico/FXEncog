package drosa.experimental.ticks;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

import drosa.classes.Tick;
import drosa.finances.QuoteShort;
import drosa.utils.DateUtils;

public class TickUtils {
	
	
	
	public static Tick decodeDave(String linea){
		
		
		String[] values = linea.split(" ");
		
		String dateStr = values[0].trim();
        String timeStr = values[1].trim();
        short year  = Short.valueOf(dateStr.substring(6,10));
		byte day = Byte.valueOf(dateStr.substring(0,2));
		byte month   = Byte.valueOf(dateStr.substring(3,5));
					
		byte hh = Byte.valueOf(timeStr.substring(0,2));
		byte mm = Byte.valueOf(timeStr.substring(3,5));
        byte ss = Byte.valueOf(timeStr.substring(6,8));
		
        String price = values[2];
        String vol = values[3]; 
        String bid = values[4];
        String ask = values[5];
        String minuteBar = values[6];        
        String highMinute = values[7];
        String lowMinute = values[8];
        				
		//System.out.println(price+" "+vol+" "+bid+" "+ask);
        Tick t = new Tick();
		t.setDay(day);
		t.setMonth(month);
		t.setYear(year);
		t.setHour(hh);
		t.setMin(mm);
		t.setSec(Byte.valueOf(ss));
		t.setPrice(Integer.valueOf(price));
		t.setVolume(Integer.valueOf(vol));
		t.setBid(Integer.valueOf(bid));
		t.setAsk(Integer.valueOf(ask));
		//t.setMaxMin(Integer.valueOf(maxMin));
		t.setMinuteBar(Integer.valueOf(minuteBar));
		t.setMinuteHigh(Integer.valueOf(highMinute));
		t.setMinuteLow(Integer.valueOf(lowMinute));
		//System.out.println(t.toString());
		
		return t;
	}
	
	public static Tick decodeDave2(String linea){
		
		
		String[] values = linea.split(" ");
		
		String dateStr = values[0].trim();
        String timeStr = values[1].trim();
        short year  = Short.valueOf(dateStr.substring(6,10));
		byte day = Byte.valueOf(dateStr.substring(0,2));
		byte month   = Byte.valueOf(dateStr.substring(3,5));
					
		byte hh = Byte.valueOf(timeStr.substring(0,2));
		byte mm = Byte.valueOf(timeStr.substring(3,5));
        byte ss = Byte.valueOf(timeStr.substring(6,8));
		
        String price = values[2];
        String vol = values[3]; 
        String bid = values[4];
        String ask = values[5];
		//System.out.println(price+" "+vol+" "+bid+" "+ask);
        Tick t = new Tick();
		t.setDay(day);
		t.setMonth(month);
		t.setYear(year);
		t.setHour(hh);
		t.setMin(mm);
		t.setSec(Byte.valueOf(ss));
		t.setPrice(Integer.valueOf(price));
		t.setVolume(Integer.valueOf(vol));
		t.setBid(Integer.valueOf(bid));
		t.setAsk(Integer.valueOf(ask));
		
		return t;
	}
	
	public static void decodeKibot(Tick t,String linea,int y1,int y2){
		
		
		String[] values = linea.split(",");
		
		String dateStr = values[0].trim();
        String timeStr = values[1].trim();
        short year  = Short.valueOf(dateStr.substring(6,10));
        
		byte month = Byte.valueOf(dateStr.substring(0,2));
		byte day   = Byte.valueOf(dateStr.substring(3,5));
					
		byte hh = Byte.valueOf(timeStr.substring(0,2));
		byte mm = Byte.valueOf(timeStr.substring(3,5));
        byte ss = Byte.valueOf(timeStr.substring(6,8));
		
        String price = QuoteShort.fillES(values[2]);
        String bid = QuoteShort.fillES(values[3]);
        String ask = QuoteShort.fillES(values[4]);
        String vol = values[5];       
               			
		//System.out.println(price+" "+vol+" "+bid+" "+ask);
        //Tick t = new Tick();
		t.setDay(day);
		t.setMonth(month);
		t.setYear(year);
		t.setHour(hh);
		t.setMin(mm);
		t.setSec(Byte.valueOf(ss));
		t.setPrice(Integer.valueOf(price));
		t.setVolume(Integer.valueOf(vol));
		t.setBid(Integer.valueOf(bid));
		t.setAsk(Integer.valueOf(ask));
		
		int diffB = (t.getBid()-t.getPrice())/25;
		int diffA = (t.getPrice()-t.getAsk())/25;
		if ((t.getPrice()<t.getBid() || t.getPrice()>t.getAsk()) && (diffB>=12 || diffA>=12)){
			System.out.println(linea);
			
		}
		

	}

	public static Tick decodeKibot(String linea,int y1,int y2){
		
		
		String[] values = linea.split(",");
		
		String dateStr = values[0].trim();
        String timeStr = values[1].trim();
        short year  = Short.valueOf(dateStr.substring(6,10));
        
        if (year<y1 || year>y2) return null;
		byte month = Byte.valueOf(dateStr.substring(0,2));
		byte day   = Byte.valueOf(dateStr.substring(3,5));
					
		byte hh = Byte.valueOf(timeStr.substring(0,2));
		byte mm = Byte.valueOf(timeStr.substring(3,5));
        byte ss = Byte.valueOf(timeStr.substring(6,8));
		
        String price = QuoteShort.fillES(values[2]);
        String bid = QuoteShort.fillES(values[3]);
        String ask = QuoteShort.fillES(values[4]);
        String vol = values[5];       
		
		
		//System.out.println(price+" "+vol+" "+bid+" "+ask);
        Tick t = new Tick();
		t.setDay(day);
		t.setMonth(month);
		t.setYear(year);
		t.setHour(hh);
		t.setMin(mm);
		t.setSec(Byte.valueOf(ss));
		t.setPrice(Integer.valueOf(price));
		t.setVolume(Integer.valueOf(vol));
		t.setBid(Integer.valueOf(bid));
		t.setAsk(Integer.valueOf(ask));
		
		//System.out.println(t.toString());
		
		return t;
	}
	
	public static ArrayList<QuoteShort> readFastTicksKibot1min(String fileName,int y1,int y2){
		ArrayList<QuoteShort> data = new  ArrayList<QuoteShort>();
			
		File archivo = null;
		FileReader fr = null;
		BufferedReader br = null;

		try {
			// Apertura del fichero y creacion de BufferedReader para poder
		    // hacer una lectura comoda (disponer del metodo readLine()).
			long startTime = System.nanoTime();
	        Path file = Paths.get(fileName);
	        try
	        {
	        	int j = 0;
	            int i = 0;
	            int lastBid =-1;
	            int lastAsk =-1;
	            int lastDay = -1;
	            int lastPrice = -1;
	        	//Java 8: Stream class
	            Stream<String> lines = Files.lines( file, StandardCharsets.UTF_8 );
	            //PrintWriter out = new PrintWriter(fileName+"_changes.txt");
	            int lastMinute = -1;
	            int minuteBar = 0;
	            QuoteShort qactual = null;
	            Tick t = new Tick();
	            for( String line : (Iterable<String>) lines::iterator )
	            {
	            	short y= Short.valueOf(line.substring(6,10));
		        	short m= Short.valueOf(line.substring(0,2));
	            	j++;
		        	if (j%1000000 == 0){
		        		System.out.println("leido: "+j+" || "+m+" "+y);
		        	}
		        	
		        	if(y<y1) continue;
		        	if (y>y2) break;
		        	//if (m!=11) continue;
		        	//out.println(line);
		        	TickUtils.decodeKibot(t,line,y1,y2);
		        	if (t==null) continue;
		        	int tminute = DateUtils.getMinutesValue(t);
		        	if (tminute!=lastMinute){
		        		//se agrega a la nueva QuoteShort
		        		if (qactual!=null){
		        			data.add(qactual);
		        			qactual = null;
		        		}
		        		qactual = new QuoteShort();
		        		qactual.setOpen5(t.getPrice());
		        		qactual.setHigh5(t.getPrice());
		        		qactual.setLow5(t.getPrice());
		        		qactual.setClose5(t.getPrice());
		        		qactual.setYear(t.getYear());
		        		qactual.setMonth(t.getMonth());
		        		qactual.setDay(t.getDay());
		        		qactual.setHh(t.getHour());
		        		qactual.setMm(t.getMin());
		        		qactual.setSs(t.getSec());
		        		
		        		lastMinute = tminute;
		        	}else{
		        		if (t.getPrice()>qactual.getHigh5())
		        			qactual.setHigh5(t.getPrice());
		        		if (t.getPrice()<qactual.getLow5())
		        			qactual.setLow5(t.getPrice());
		        		qactual.setClose5(t.getPrice());
		        	}
	            }//for
	            if (qactual!=null)
	            	data.add(qactual);
	        
	        } catch (IOException ioe){
	            ioe.printStackTrace();
	        }
	 
	        long endTime = System.nanoTime();
	        long elapsedTimeInMillis = TimeUnit.MILLISECONDS.convert((endTime - startTime), TimeUnit.NANOSECONDS);
	        System.out.println("Total elapsed time: " + elapsedTimeInMillis);
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
	
	public static ArrayList<Tick> readFastTicksKibot(String fileName,
			int y1,int y2,
			int m1,int m2,
			boolean onlyChanges
	){
		ArrayList<Tick> data = new  ArrayList<Tick>();
			
		File archivo = null;
		FileReader fr = null;
		BufferedReader br = null;
		Calendar cal = Calendar.getInstance();
		Calendar from = Calendar.getInstance();
		Calendar to = Calendar.getInstance();
		from.set(y1,m1, 1);
		to.set(y2,m2,30);
		try {
			// Apertura del fichero y creacion de BufferedReader para poder
		    // hacer una lectura comoda (disponer del metodo readLine()).
			long startTime = System.nanoTime();
	        Path file = Paths.get(fileName);
	        try
	        {
	        	int j = 0;
	            int i = 0;
	            int lastBid =-1;
	            int lastAsk =-1;
	            int lastDay = -1;
	            int lastPrice = -1;
	        	//Java 8: Stream class
	            Stream<String> lines = Files.lines( file, StandardCharsets.UTF_8 );
	            //PrintWriter out = new PrintWriter(fileName+"_changes.txt");
	            int lastMinute = -1;
	            int minuteBar = 0;
	            for( String line : (Iterable<String>) lines::iterator )
	            {
	            	short y= Short.valueOf(line.substring(6,10));
		        	short m= Short.valueOf(line.substring(0,2));
		        	short dd= Short.valueOf(line.substring(3,5));
	            	j++;
		        	if (j%1000000 == 0){
		        		System.out.println("leido: "+j+" || "+m+" "+y);
		        	}
		        	cal.set(y,m-1,dd);
		        	
		        	if(y<y1) continue;
		        	if (y>y2) break;
		        	if (cal.compareTo(from)<0) continue;
		        	if (cal.compareTo(to)>0) break;
		        	
		        	//if (m!=11) continue;
		        	//out.println(line);
		        	Tick t = TickUtils.decodeKibot(line,y1,y2);
		        	if (t==null) continue;
		        	//System.out.println("leido: "+t.toString());
		        	int day = DateUtils.getDaysValue(t);
		        	//if (!onlyChanges || t.getBid()!=lastBid || t.getAsk()!=lastAsk || day!=lastDay){
		        	if (t.getPrice()!=lastPrice || day!=lastDay){
		        		int actualMinute = DateUtils.getMinutesValue(t);
		        		if (actualMinute!=lastMinute){
		        			lastMinute = actualMinute;
		        			minuteBar++;
		        		}
		        		t.setMinuteBar(minuteBar);
		        		data.add(t);
		        		i++;
		        		lastBid = t.getBid();
		        		lastAsk = t.getAsk();
		        		lastDay = day;
		        		lastPrice = t.getPrice();
		        		if (i%100000 == 0){
			        		System.out.println("añadido: "+i);
			        	}
		        	}
	            }
	        
	        } catch (IOException ioe){
	            ioe.printStackTrace();
	        }
	 
	        long endTime = System.nanoTime();
	        long elapsedTimeInMillis = TimeUnit.MILLISECONDS.convert((endTime - startTime), TimeUnit.NANOSECONDS);
	        System.out.println("Total elapsed time: " + elapsedTimeInMillis);
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
	
	public static void savePrices(String fileName,int y1,int y2) throws FileNotFoundException{
		ArrayList<Tick> data = TickUtils.readFastTicksKibot(fileName,y1,y1,0,11,false);
		ArrayList<Integer> maxMins = TickUtils.calculateMaxMinTime(data);
		try(  PrintWriter out = new PrintWriter(fileName+"_changes.txt")  ){
			for (int d=0;d<data.size();d++){
				out.println(data.get(d).toString()+" "+maxMins.get(d));
			}
			out.close();
		}
		
		data.clear();
		maxMins.clear();
	}
	
	public static void savePrices1min(String fileName,int y1,int y2,boolean save) throws FileNotFoundException{
		ArrayList<QuoteShort> data = TickUtils.readFastTicksKibot1min(fileName,y1,y2);
		//ArrayList<Integer> maxMins = TickUtils.calculateMaxMinTime(data);
		if (save){
			try(  PrintWriter out = new PrintWriter(fileName+"_"+y1+"_"+y2+"_1min.txt")  ){
				for (int d=0;d<data.size();d++){
					//out.println(data.get(d).toString()+" "+maxMins.get(d));
					out.println(data.get(d).toString());
				}
				out.close();
			}
		}
		
		data.clear();
		//maxMins.clear();
	}
	
	public static ArrayList<Tick> readTicksDaveMinutes(String fileName,int y1,int y2,int v){
		ArrayList<Tick> data = new  ArrayList<Tick>();
		

		FileReader fr = null;

		try {
			// Apertura del fichero y creacion de BufferedReader para poder
		    // hacer una lectura comoda (disponer del metodo readLine()).
			long startTime = System.nanoTime();
	        Path file = Paths.get(fileName);
	        try
	        {
	        	//Java 8: Stream class
	            Stream<String> lines = Files.lines( file, StandardCharsets.UTF_8 );
	            for( String line : (Iterable<String>) lines::iterator )
	            {	
	            	try{
	            		if (line.trim().length()<10) continue;
		            	short y= Short.valueOf(line.substring(6,10));
			        	if (y<y1) continue;
			        	if (y>y2) break;
			        	Tick t = null;
			        	if (v==1)
			        		t = TickUtils.decodeDave(line);
			        	else t = TickUtils.decodeDave2(line);
			        	if (t==null) continue;
			        	data.add(t);
	            	}catch(Exception e){
	            		System.out.println("[ERROR] msg: "+line+" ."+e.getMessage());
	            	}
	            }
	        
	        } catch (IOException ioe){
	            ioe.printStackTrace();
	            
	        }
	 
	        long endTime = System.nanoTime();
	        long elapsedTimeInMillis = TimeUnit.MILLISECONDS.convert((endTime - startTime), TimeUnit.NANOSECONDS);
	       // System.out.println("Total elapsed time: " + elapsedTimeInMillis);
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
	
	public static ArrayList<Tick> readFastTicksDave(String fileName,int y1,int y2){
		ArrayList<Tick> data = new  ArrayList<Tick>();
			
		File archivo = null;
		FileReader fr = null;
		BufferedReader br = null;

		try {
			// Apertura del fichero y creacion de BufferedReader para poder
		    // hacer una lectura comoda (disponer del metodo readLine()).
			long startTime = System.nanoTime();
	        Path file = Paths.get(fileName);
	        try
	        {
	        	int j = 0;
	            int i = 0;
	            int lastBid =-1;
	            int lastAsk =-1;
	            int lastDay = -1;
	            int lastPrice = 0;
	            int lastMinute = 0;
	        	//Java 8: Stream class
	            Stream<String> lines = Files.lines( file, StandardCharsets.UTF_8 );
	            int minuteBar=-1;
	            int lastMinuteHigh= -1;
	            int lastMinuteLow= -1;
	            for( String line : (Iterable<String>) lines::iterator )
	            {
	            	j++;
		        	if (j%1000000 == 0){
		        		System.out.println("leido: "+j);
		        	}
		        	short y= Short.valueOf(line.substring(6,10));
		        	if (y<y1) continue;
		        	if (y>y2) break;

		        	//Tick t = TickUtils.decodeDave(line);
		        	//if (t==null) continue;
		        	Tick t = TickUtils.decodeKibot(line, y1, y2);
		        	if (t==null) continue;
		        	//System.out.println("leido: "+t.toString());
		        	int day = DateUtils.getDaysValue(t);
		        	int actualMinute = DateUtils.getMinutesValue(t);
		        	if (t.getPrice()!=lastPrice || day!=lastDay|| actualMinute!=lastMinute){
		        		if (actualMinute!=lastMinute){
		        			lastMinuteHigh = t.getPrice();
		        			lastMinuteLow = t.getPrice();
		        			minuteBar++;
		        			lastMinute = actualMinute;
		        		}
		        		if (day!=lastDay){
		        			lastDay = day;
		        		}
		        		lastPrice = t.getPrice();
		        		t.setMinuteBar(minuteBar);
		        		if (lastMinuteHigh==-1 || t.getPrice()>=lastMinuteHigh) lastMinuteHigh = t.getPrice();
		        		if (lastMinuteLow==-1 || t.getPrice()<=lastMinuteLow) lastMinuteLow = t.getPrice();
		        		t.setMinuteHigh(lastMinuteHigh);
		        		t.setMinuteLow(lastMinuteLow);
		        		data.add(t);
		        		i++;
		        		if (i%100000 == 0){
			        		System.out.println("añadido: "+i);
			        	}
		        	}
	            }
	        
	        } catch (IOException ioe){
	            ioe.printStackTrace();
	        }
	 
	        long endTime = System.nanoTime();
	        long elapsedTimeInMillis = TimeUnit.MILLISECONDS.convert((endTime - startTime), TimeUnit.NANOSECONDS);
	        System.out.println("Total elapsed time: " + elapsedTimeInMillis);
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
	
	public static ArrayList<Tick> readFastTicksDave1min(String fileName){
		ArrayList<Tick> data = new  ArrayList<Tick>();
			
		File archivo = null;
		FileReader fr = null;
		BufferedReader br = null;

		try {
			// Apertura del fichero y creacion de BufferedReader para poder
		    // hacer una lectura comoda (disponer del metodo readLine()).
			long startTime = System.nanoTime();
	        Path file = Paths.get(fileName);
	        try
	        {
	        	int j = 0;
	            int i = 0;
	            int lastBid =-1;
	            int lastAsk =-1;
	            int lastDay = -1;
	        	//Java 8: Stream class
	            Stream<String> lines = Files.lines( file, StandardCharsets.UTF_8 );
	            
	            for( String line : (Iterable<String>) lines::iterator )
	            {
	            	j++;
		        	if (j%1000000 == 0){
		        		System.out.println("leido: "+j);
		        	}
		        	short y= Short.valueOf(line.substring(6,10));

		        	Tick t = TickUtils.decodeDave(line);
		        	if (t==null) continue;
		        	//System.out.println("leido: "+t.toString());
		        	int day = DateUtils.getDaysValue(t);
		        	if (t.getBid()!=lastBid || t.getAsk()!=lastAsk || day!=lastDay){
		        		data.add(t);
		        		i++;
		        		lastBid = t.getBid();
		        		lastAsk = t.getAsk();
		        		lastDay = day;
		        		if (i%100000 == 0){
			        		System.out.println("añadido: "+i);
			        	}
		        	}
	            }
	        
	        } catch (IOException ioe){
	            ioe.printStackTrace();
	        }
	 
	        long endTime = System.nanoTime();
	        long elapsedTimeInMillis = TimeUnit.MILLISECONDS.convert((endTime - startTime), TimeUnit.NANOSECONDS);
	        System.out.println("Total elapsed time: " + elapsedTimeInMillis);
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
	
	public static ArrayList<Tick> readTicksKibot(String fileName,int y1,int y2,boolean onlyChanges){
		ArrayList<Tick> data = new  ArrayList<Tick>();
			
		File archivo = null;
		FileReader fr = null;
		BufferedReader br = null;

		try {
			// Apertura del fichero y creacion de BufferedReader para poder
		    // hacer una lectura comoda (disponer del metodo readLine()).
			archivo = new File (fileName);
		    fr = new FileReader (archivo);
		    br = new BufferedReader(fr);

		        // Lectura del fichero
		        String line;
		        int i=0;
		        int j=0;
		        int lastBid = -1;
		        int lastAsk = -1;
		        int lastDay = -1;
		        while((line=br.readLine())!=null){
		        	j++;
		        	if (j%10000000 == 0){
		        		System.out.println("leido: "+j);
		        	}
		        	short y= Short.valueOf(line.substring(6,10));
		        	if(y<y1 || y>y2) continue;
		        	Tick t = TickUtils.decodeKibot(line,y1,y2);
		        	if (t==null) continue;
		        	int day = DateUtils.getDaysValue(t);
		        	if (!onlyChanges || t.getBid()!=lastBid || t.getAsk()!=lastAsk || day!=lastDay){
		        		data.add(t);
		        		i++;
		        		lastBid = t.getBid();
		        		lastAsk = t.getAsk();
		        		lastDay = day;
		        	}
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
	
	public static ArrayList<Integer> calculateMaxMinTime(ArrayList<Tick> data){
		 ArrayList<Integer> maxMins = new ArrayList<Integer>();
		 		 
		 for (int i=0;i<data.size();i++){
			 Tick t = data.get(i);
			 int thrH = 0;
			 int thrL= 0;
			 int modeH = 0;
			 int modeL = 0;	
			 int timeStampH = 0;
			 int timeStampL = 0;
			 for (int j=i-1;j>=0;j--){
				 Tick tj = data.get(j);
				 boolean isHigh = false;
				 boolean isLow = false;
				 
				 if (t.getPrice()>tj.getPrice()){
					 isHigh = true;
				 }
				 
				 if (t.getPrice()<tj.getPrice()){
					 isLow = true;
				 }
				 if (modeH==0 || modeH==1){
						if (isHigh){
							modeH=1;
							thrH++;
							timeStampH = t.getMinuteBar()-tj.getMinuteBar();
							/*if (t.getMonth()==12 && t.getDay()==31)
							System.out.println(t.toString()+" "+tj.toString()
									+" || "+DateUtils.getMinutesValue(t)+" "+DateUtils.getMinutesValue(tj)
									+" || "+timeStampH
									);*/
						}else{
							modeH=-1;
						}
				 }
				 
				if (modeL==0 || modeL==1){
					if (isLow){
						modeL=1;
						thrL++;
						timeStampL = t.getMinuteBar()-tj.getMinuteBar();
					}else{
						modeL=-1;
					}
				}
					
				if (!isHigh) modeH = -1;
				if (!isLow)  modeL = -1;
				
				if (!isHigh && !isLow) break;
				if (modeH==-1 && modeL==-1) break;
			 }//J
			 
			 if (timeStampH>=timeStampL){
				 maxMins.add(timeStampH);
				 /*if (t.getMonth()==12 && t.getDay()==31)
						System.out.println(i+" "+(maxMins.size()-1)
								+" || "+t.toString()
								+" || "+timeStampH
								);*/
			 }
			 else {
				 maxMins.add(-timeStampL);
			 }
		 }
		 		 
		 return maxMins;
	}
	
	public static ArrayList<Integer> calculateMaxMinMinuteBar(ArrayList<Tick> data){
		 ArrayList<Integer> maxMins = new ArrayList<Integer>();
		 		 
		 for (int i=0;i<data.size();i++){
			 Tick t = data.get(i);
			 int modeH = 0;
			 int modeL = 0;	
			 int timeStampH = 0;
			 int timeStampL = 0;
			 int lastMinute = -1;
			 boolean exited = false;
			 //if (t.getMinuteBar()%1000==0) 
			 //System.out.println("actualMinute: "+t.getMinuteBar());
			 for (int j=i-1;j>=0 && !exited;j--){
				 Tick tj = data.get(j);
				 boolean isHigh = false;
				 boolean isLow = false;
				 int actualMinute = tj.getMinuteBar();
								
					 if (t.getPrice()>tj.getMinuteHigh()){
						 isHigh = true;
					 }					 
					 if (t.getPrice()<tj.getMinuteLow()){
						 isLow = true;
					 }
					 
					 if (modeH==0 || modeH==1){
						 if (isHigh){
							modeH=1;
							timeStampH = t.getMinuteBar()-tj.getMinuteBar();
						}else{
							modeH=-1;
						}
					 }
					 if (modeL==0 || modeL==1){
						 if (isLow){
							modeL=1;
							timeStampL = t.getMinuteBar()-tj.getMinuteBar();
						}else{
							modeL=-1;
						}
					 }
					 
					 if (!isHigh) modeH = -1;
					 if (!isLow)  modeL = -1;
					 if (!isHigh && !isLow){
						 //System.out.println("exited: "+t.getMinuteBar());
						 exited=true;
					 }
					 if (modeH==-1 && modeL==-1){
						 exited=true;
					 }
														
			 }//J
			 
			 if (timeStampH>=timeStampL){
				 maxMins.add(timeStampH);	
				 //System.out.println("timestamp: "+timeStampH);
			 }
			 else {
				 maxMins.add(-timeStampL);
			 }
		 }
		 		 
		 return maxMins;
	}
	
	public static ArrayList<Integer> calculateMaxMin(ArrayList<Tick> data){
		 ArrayList<Integer> maxMins = new ArrayList<Integer>();
		 		 
		 for (int i=0;i<data.size();i++){
			 
			 Tick t = data.get(i);
			 int thrH = 0;
			 int thrL= 0;
			 int modeH = 0;
			 int modeL = 0;	
			 int timeStampH = 0;
			 int timeStampL = 0;
			 for (int j=i-1;j>=0;j--){
				 Tick tj = data.get(j);
				 boolean isHigh = false;
				 boolean isLow = false;
				 
				 if (t.getAsk()>=tj.getAsk()){
					 isHigh = true;
				 }
				 
				 if (t.getBid()<=tj.getBid()){
					 isLow = true;
				 }
				 if (modeH==0 || modeH==1){
						if (isHigh){
							modeH=1;
							thrH++;
						}else{
							modeH=-1;
						}
				 }
				 
				if (modeL==0 || modeL==1){
					if (isLow){
						modeL=1;
						thrL++;
					}else{
						modeL=-1;
					}
				}
					
				if (!isHigh) modeH = -1;
				if (!isLow)  modeL = -1;
				
				if (!isHigh && !isLow) break;
				if (modeH==-1 && modeL==-1) break;
			 }
			 
			 if (thrH>=thrL) maxMins.add(thrH);
			 else if (thrL>=thrH) maxMins.add(-thrL);
		 }
		 		 
		 return maxMins;
	}

}
