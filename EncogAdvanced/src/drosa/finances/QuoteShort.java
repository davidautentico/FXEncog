package drosa.finances;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Calendar;

import drosa.experimental.TradeResultSimple;
import drosa.utils.DateUtils;

public class QuoteShort {
	short year;
	byte month;
	byte day;
	byte hh;
	byte mm;
	byte ss;
	short open;
	short high;
	short low;
	short close;
	int open5 = -1;
	int high5 = -1;
	int low5 = -1;
	int close5 = -1;
	int index;
	int extra;
	long vol = 0;
	long maxMin = 0;
	int highIdx = 0;
	int lowIdx = 0;
	int bid = -1;
	int ask = -1;
	
	
	
	public int getBid() {
		return bid;
	}
	public void setBid(int bid) {
		this.bid = bid;
	}
	public int getAsk() {
		return ask;
	}
	public void setAsk(int ask) {
		this.ask = ask;
	}
	public int getHighIdx() {
		return highIdx;
	}
	public void setHighIdx(int highIdx) {
		this.highIdx = highIdx;
	}
	public int getLowIdx() {
		return lowIdx;
	}
	public void setLowIdx(int lowIdx) {
		this.lowIdx = lowIdx;
	}
	public long getVol() {
		return vol;
	}
	public void setVol(long vol) {
		this.vol = vol;
	}
	public short getYear() {
		return year;
	}
	public void setYear(short year) {
		this.year = year;
	}
	public byte getMonth() {
		return month;
	}
	public void setMonth(byte month) {
		this.month = month;
	}
	public byte getDay() {
		return day;
	}
	public void setDay(byte day) {
		this.day = day;
	}
	
	public byte getHh() {
		return hh;
	}
	public void setHh(byte hh) {
		this.hh = hh;
	}
	public byte getMm() {
		return mm;
	}
	public void setMm(byte mm) {
		this.mm = mm;
	}
	public byte getSs() {
		return ss;
	}
	public void setSs(byte ss) {
		this.ss = ss;
	}
	public short getOpen() {
		return open;
	}
	public void setOpen(short open) {
		this.open = open;
	}
	public short getHigh() {
		return high;
	}
	public void setHigh(short high) {
		this.high = high;
	}
	public short getLow() {
		return low;
	}
	public void setLow(short low) {
		this.low = low;
	}
	public short getClose() {
		return close;
	}
	public void setClose(short close) {
		this.close = close;
	}
	
	public int getOpen5() {
		return open5;
	}
	public void setOpen5(int open5) {
		this.open5 = open5;
	}
	public int getHigh5() {
		return high5;
	}
	public void setHigh5(int high5) {
		this.high5 = high5;
	}
	public int getLow5() {
		return low5;
	}
	public void setLow5(int low5) {
		this.low5 = low5;
	}
	public int getClose5() {
		return close5;
	}
	public void setClose5(int close5) {
		this.close5 = close5;
	}
	
	
	public long getMaxMin() {
		return maxMin;
	}
	public static boolean equalValues(QuoteShort lastQuote, QuoteShort q) {
		// TODO Auto-generated method stub
		if (lastQuote.getOpen()!= q.getOpen()) return false;
		if (lastQuote.getHigh()!= q.getHigh()) return false;
		if (lastQuote.getLow()!= q.getLow()) return false;
		if (lastQuote.getClose()!= q.getClose()) return false;
		
		return true;
	}
	
	public String toString(){
		String str = DateUtils.datePrint(year, month, day, hh, mm, ss)+" "+open5+" "+high5+" "+low5+" "+close5;
		return str;
	}
	
	public String toStringTick(){
		String str = DateUtils.datePrint(year, month, day, hh, mm, ss)+" "+ask+" "+bid;
		return str;
	}
	
	public String toStringDetails(){
		String str = year+" "+month+" "+day+" "+hh+" "+mm+" "+ss+" "+open5+" "+high5+" "+low5+" "+close5;
		return str;
	}
	
	public String toStringExt(){
		String str = DateUtils.datePrint(year, month, day, hh, mm, ss)+" "+open5
				+" "+high5+" "+low5+" "+close5+" "+vol+" "+maxMin
				
				;
		return str;
	}
	
	private static QuoteShort decodeLine(String line){
		int open5 = 0;
		int high5 = 0;
		int low5 = 0;
		int close5 = 0;
		
		short year = 0;
		byte month = 0;
		byte day = 0;
		byte hh = 0;
		byte mm = 0;
		byte ss = 0;
		
		String timeStr = line.split(" ")[1].trim();
        String dateStr = line.split(" ")[0].trim();
        
        year  = Short.valueOf(dateStr.split("-")[2].trim());
		month = Byte.valueOf(dateStr.split("-")[1].trim());
		day   = Byte.valueOf(dateStr.split("-")[0].trim());
				
        hh = Byte.valueOf(timeStr.substring(0,2));
        mm = Byte.valueOf(timeStr.substring(3,5));
        ss = Byte.valueOf(timeStr.substring(6,8));
        
		open5  = Integer.valueOf(line.split(" ")[2].trim());
        high5  = Integer.valueOf(line.split(" ")[3].trim());
        low5   = Integer.valueOf(line.split(" ")[4].trim());
        close5 = Integer.valueOf(line.split(" ")[5].trim());
		
       
        QuoteShort fq = new QuoteShort();
        fq.setOpen5(open5);
		fq.setClose5(close5);
		fq.setHigh5(high5);
		fq.setLow5(low5);	
		fq.setYear(year);
		fq.setMonth(month);
		fq.setDay(day);
		fq.setHh(hh);
		fq.setMm(mm);
		fq.setSs(ss);
		 //System.out.println("LINE: "+line+" TRADE: "+trade.toString());
		return fq;
	}
	

	public static ArrayList<QuoteShort> readFromDisk(String fileName){
		ArrayList<QuoteShort> data = new ArrayList<QuoteShort>();
		File archivo = null;
	    FileReader fr = null;
	    BufferedReader br = null;
	    String line="";
	    try {
	    	// Apertura del fichero y creacion de BufferedReader para poder
	        // hacer una lectura comoda (disponer del metodo readLine()).
	        archivo = new File (fileName);
	        fr = new FileReader (archivo);
	        br = new BufferedReader(fr);

	        // Lectura del fichero

	        int i=0;
	        while((line=br.readLine())!=null){
	        	if (i>0){
	        		QuoteShort item = decodeLine(line);     	
	        		data.add(item);
	        		//System.out.println(item.toString());
	        	}
	        	i++;
	        	//if (i%20000==0)
	        		//System.out.println("i: "+i);
	        }    
	    }catch(Exception e){
	    	e.printStackTrace();
	    	 System.out.println("[error] "+line);
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
	
	
	
	public static void saveToDisk(ArrayList<QuoteShort> data,String fileName){
		try{
			  // Create file 
			  FileWriter fstream = new FileWriter(fileName);
			  BufferedWriter out = new BufferedWriter(fstream);

			  for (int i=0;i<data.size();i++){
				  QuoteShort res = data.get(i);
				  out.write(res.toString());
				  out.newLine();
			  }
			  //Close the output stream
			  out.close();
		}catch (Exception e){//Catch exception if any
			  System.err.println("Error: " + e.getMessage());
		}
	}
	
	public static void saveToDiskTick(ArrayList<QuoteShort> data,String fileName){
		try{
			  // Create file 
			  FileWriter fstream = new FileWriter(fileName);
			  BufferedWriter out = new BufferedWriter(fstream);

			  for (int i=0;i<data.size();i++){
				  QuoteShort res = data.get(i);
				  out.write(res.toStringTick());
				  out.newLine();
			  }
			  //Close the output stream
			  out.close();
		}catch (Exception e){//Catch exception if any
			  System.err.println("Error: " + e.getMessage());
		}
	}
	
	public static void saveToDiskDetails(ArrayList<QuoteShort> data,String fileName){
		try{
			  // Create file 
			  FileWriter fstream = new FileWriter(fileName);
			  BufferedWriter out = new BufferedWriter(fstream);

			  for (int i=0;i<data.size();i++){
				  QuoteShort res = data.get(i);
				  out.write(res.toStringDetails());
				  out.newLine();
			  }
			  //Close the output stream
			  out.close();
		}catch (Exception e){//Catch exception if any
			  System.err.println("Error: " + e.getMessage());
		}
	}
	
	public static void saveToDiskExtra(ArrayList<QuoteShort> data,String fileName){
		try{
			  // Create file 
			  FileWriter fstream = new FileWriter(fileName);
			  BufferedWriter out = new BufferedWriter(fstream);

			  for (int i=0;i<data.size();i++){
				  QuoteShort res = data.get(i);
				  out.write(res.toString()+" "+res.extra);
				  out.newLine();
			  }
			  //Close the output stream
			  out.close();
		}catch (Exception e){//Catch exception if any
			  System.err.println("Error: " + e.getMessage());
		}
	}
	
	public static void saveToDiskClean(ArrayList<QuoteShort> data,String fileName){
		Calendar cal1 = Calendar.getInstance();
		Calendar cal2 = Calendar.getInstance();
		try{
			  // Create file 
			  FileWriter fstream = new FileWriter(fileName);
			  BufferedWriter out = new BufferedWriter(fstream);

			  QuoteShort lastInserted = null;
			  for (int i=0;i<data.size();i++){
				  QuoteShort res = data.get(i);
				  if (i>0){
					  if (lastInserted==null || DateUtils.isGreater(cal1, cal2, data.get(i), lastInserted)==1){
						  out.write(res.toString());
						  out.newLine();
						  lastInserted = res;
					  }
				  }else{
					  out.write(res.toString());
					  out.newLine();
				  }
			  }
			  //Close the output stream
			  out.close();
		}catch (Exception e){//Catch exception if any
			  System.err.println("Error: " + e.getMessage());
		}
	}
	public static void getCalendar(Calendar cal, QuoteShort q) {
		// TODO Auto-generated method stub
		cal.set(q.getYear(), q.getMonth()-1, q.getDay(), q.getHh(), q.getMm(),q.getSs());
	}
	public void copy(QuoteShort q) {
		// TODO Auto-generated method stub
		this.year = q.getYear();
		this.month = q.getMonth();
		this.day = q.getDay();
		this.hh = q.getHh();
		this.mm = q.getMm();
		this.ss = q.getSs();
		this.open = q.getOpen();
		this.high = q.getHigh();
		this.low = q.getLow();
		this.close = q.getClose();
		this.open5 = q.getOpen5();
		this.high5 = q.getHigh5();
		this.low5 = q.getLow5();
		this.close5 = q.getClose5();
		this.index = q.getIndex();
		this.extra = q.getExtra();
	}
	
	private int getIndex() {
		// TODO Auto-generated method stub
		return this.index;
	}
	public void setIndex(int value) {
		// TODO Auto-generated method stub
		this.index = value;
	}
	public int getExtra() {
		return extra;
	}
	public void setExtra(int extra) {
		this.extra = extra;
	}
	public void setCal(Calendar cal) {
		// TODO Auto-generated method stub
		this.year  = (short) cal.get(Calendar.YEAR);
		this.month = (byte) (cal.get(Calendar.MONTH)+1);
		this.day   = (byte) cal.get(Calendar.DAY_OF_MONTH);
		this.hh    = (byte) cal.get(Calendar.HOUR_OF_DAY);
		this.mm    = (byte) cal.get(Calendar.MINUTE);
		this.ss    = (byte) cal.get(Calendar.SECOND);
	}
	
	public static void convertQuoteToShort(Calendar cal,Quote q,QuoteShort qs){
		
		int mult4 = 10000;
		int mult5 = 100000;
		
		if (q.getOpen()>10.0){
			mult4 = 100;
			mult5 = 1000;
		}
		
		short open = (short) (q.getOpen()*mult4);
		short high = (short) (q.getHigh()*mult4);
		short low = (short) (q.getLow()*mult4);
		short close = (short) (q.getClose()*mult4);
		
		int open5  = (int) (q.getOpen()*mult5);
		int high5  = (int) (q.getHigh()*mult5);
		int low5   = (int) (q.getLow()*mult5);
		int close5 = (int) (q.getClose()*mult5);
		
		qs.setOpen(open);
		qs.setHigh(high);
		qs.setLow(low);
		qs.setClose(close);
		qs.setOpen5(open5);
		qs.setHigh5(high5);
		qs.setLow5(low5);
		qs.setClose5(close5);
		qs.setIndex(q.getIndex());
		qs.setExtra(q.getExtra());
		cal.setTimeInMillis(q.getDate().getTime());
		qs.setCal(cal);
	}
	
	public static ArrayList<QuoteShort> convertQuoteArraytoQuoteShort(ArrayList<Quote> arrayQ){
		ArrayList<QuoteShort> arrayS = new ArrayList<QuoteShort>();
		
		Calendar cal = Calendar.getInstance();
		for (int i=0;i<arrayQ.size();i++){
			QuoteShort s = new QuoteShort();
			QuoteShort.convertQuoteToShort(cal,arrayQ.get(i), s);
			arrayS.add(s);
		}
		
		return arrayS;
	}
	
	public static void main(String[] args) throws Exception {
		
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.YEAR, 2014);
		cal.set(Calendar.MONTH, Calendar.JANUARY);
		cal.set(Calendar.DAY_OF_MONTH,23);
		
		QuoteShort q = new QuoteShort();
		q.setCal(cal);
		
		System.out.println("prueba: "+q.toString());
		
	}
	
	
	public static int getMax(ArrayList<QuoteShort> data, int begin, int end) {
		// TODO Auto-generated method stub
		
		int max = -1;
		int maxIndex = -1;
		if (begin<=0) begin = 0;
		for (int i=begin;i<=end;i++){
			if (data.get(i).getHigh5()>=max){
				max = data.get(i).getHigh5();
				maxIndex = i;
			}
		}
		
		return maxIndex;
	}
	
	public static int getMin(ArrayList<QuoteShort> data, int begin, int end) {
		// TODO Auto-generated method stub
		int min = 999999999;
		int minIndex = -1;
		if (begin<=0) begin = 0;
		for (int i=begin;i<=end;i++){
			if (data.get(i).getHigh5()<=min){
				min = data.get(i).getLow5();
				minIndex = i;
			}
		}
		
		return minIndex;
	}
	public void init(int value) {
		// TODO Auto-generated method stub
		this.open5 = value;
		this.high5 = value;
		this.low5 = value;
		this.close5 = value;
		this.extra = value;
	}
	public static boolean isSame(QuoteShort q1, QuoteShort q2) {
		// TODO Auto-generated method stub		
		if (q1.getOpen5()!=q2.getOpen5()) return false;
		if (q1.getClose5()!=q2.getClose5()) return false;
		if (q1.getHigh5()!=q2.getHigh5()) return false;
		if (q1.getLow5()!=q2.getLow5()) return false;
		
		return true;
	}
	public static String fill5(String valueStr) {
		// TODO Auto-generated method stub
		
		if (valueStr.length()==1) return valueStr+"00000";
		if (valueStr.length()==2) return valueStr+"0000";
		if (valueStr.length()==3) return valueStr+"000";
		if (valueStr.length()==4) return valueStr+"00";
		if (valueStr.length()==5) return valueStr+"0";
		//if (valueStr.length()==6) return valueStr+"00000";
		
		return valueStr;
	}
	
	public static String fill4(String valueStr) {
		// TODO Auto-generated method stub
		
		if (valueStr.length()==1) return valueStr+"0000";
		if (valueStr.length()==2) return valueStr+"000";
		if (valueStr.length()==3) return valueStr+"00";
		if (valueStr.length()==4) return valueStr+"0";
		if (valueStr.length()>=5) return valueStr+"";
		//if (valueStr.length()==6) return valueStr+"00000";
		
		return valueStr;
	}
	
	public static String fill3(String valueStr) {
		// TODO Auto-generated method stub
		
		if (valueStr.length()==1) return valueStr+"00";
		if (valueStr.length()==2) return valueStr+"0";
		//if (valueStr.length()==6) return valueStr+"00000";
		
		return valueStr;
	}
	
	public static String fillES(String valueStr) {
		// TODO Auto-generated method stub
		
		if (!valueStr.contains(".")) return valueStr+"00";
		
		String[] ps = valueStr.split("\\.");
		String p1 = ps[0].trim();
		String p2 = ps[1].trim();
		
		if (p2.length()==0){
			p2 ="00";
		}else if (p2.equalsIgnoreCase("5")){
			p2 ="50";
		}
		
		if (p2.trim().equalsIgnoreCase("")) p2="00";
		
		//System.out.println(valueStr+": "+p1+p2);
		//return (p1+p2).trim();
		return (p1+p2).trim();
	}
	public void setMaxMin(long maxMin) {
		// TODO Auto-generated method stub
		this.maxMin = maxMin;
	}
	
	public static ArrayList<QuoteShort> createBricksFromBricks(
			ArrayList<QuoteShort> bricksBase,
			int aSize
			){
		
		ArrayList<QuoteShort> data = new ArrayList<QuoteShort>();
		
		QuoteShort qNew = null;
		int open =  bricksBase.get(0).getOpen5();
		int close =  bricksBase.get(0).getClose5();
		int mode = 0;
		for (int i=1;i< bricksBase.size();i++){
			QuoteShort q = bricksBase.get(i);
			
			int diffH  = q.getClose5()-open;
			int diffL = open-q.getClose5();
			
			if (diffH==aSize){
				qNew = new QuoteShort();
				qNew.setOpen5(open);
				qNew.setClose5(q.getClose5());
				qNew.setHigh5(q.getClose5());
				qNew.setLow5(open);
				
				data.add(qNew);
								
				
				open = q.getClose5();
				close = open;
			}else if (diffL==aSize){
				qNew = new QuoteShort();
				qNew.setOpen5(open);
				qNew.setClose5(q.getClose5());
				qNew.setHigh5(open);
				qNew.setLow5(q.getClose5());
				
				data.add(qNew);
				
				
				
				open = q.getClose5();
				close = open;
			}
			
			/*if (mode==0){
				if (diffH==aSize){
					mode = 1;
					close = q.getClose5();
				}else if (diffL==aSize){
					mode = -1;
					close = q.getClose5();
				}
			}else if (mode==1){
				if (q.getClose5()>close){
					close = q.getClose5();
				}else{
					int diffCC = close-q.getClose5();
					if (diffCC==aSize){
						qNew = new QuoteShort();
						qNew.setOpen5(open);
						qNew.setClose5(close);
						qNew.setHigh5(close);
						qNew.setLow5(open);
						
						data.add(qNew);
						
						open = close;
						close = q.getClose5();
						mode = -1;
					}
				}
			}else if (mode==-1){
				if (q.getClose5()<close){
					close = q.getClose5();
				}else{
					int diffCC = q.getClose5()-close;
					if (diffCC==aSize){
						qNew = new QuoteShort();
						qNew.setOpen5(open);
						qNew.setClose5(close);
						qNew.setHigh5(open);
						qNew.setLow5(close);
						
						data.add(qNew);
						
						open = close;
						close = q.getClose5();
						mode = 1;
					}
				}
			}*/
		}
		
		return data;
	}
	
	
}
