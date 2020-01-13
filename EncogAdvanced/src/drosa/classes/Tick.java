package drosa.classes;

import java.io.BufferedReader;
import java.io.DataOutput;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

import drosa.data.DataProvider;
import drosa.finances.Quote;
import drosa.utils.DateUtils;

public class Tick {
	int price;
	int bid;
	int ask;
	int volume;
	
	byte day;
	byte month;
	short year;
	byte hour;
	byte min;
	byte sec;
	private int maxMin;
	private int minuteBar;
	private int minuteHigh;
	private int minuteLow;
	
	public int getPrice() {
		return price;
	}
	public void setPrice(int price) {
		this.price = price;
	}
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
	public int getVolume() {
		return volume;
	}
	public void setVolume(int volume) {
		this.volume = volume;
	}
	public byte getHour() {
		return hour;
	}
	public void setHour(byte hour) {
		this.hour = hour;
	}
	public byte getMin() {
		return min;
	}
	public void setMin(byte min) {
		this.min = min;
	}
	public byte getSec() {
		return sec;
	}
	public void setSec(byte sec) {
		this.sec = sec;
	}		
	public byte getDay() {
		return day;
	}
	public void setDay(byte day) {
		this.day = day;
	}
	public byte getMonth() {
		return month;
	}
	public void setMonth(byte month) {
		this.month = month;
	}
	public short getYear() {
		return year;
	}
	public void setYear(short year) {
		this.year = year;
	}
	public String toString(){
		return DateUtils.datePrint(year, month, day, hour, min, sec)+" "+price+" "+volume+" "+bid+" "+ask+" "+minuteBar+" "+minuteHigh+" "+minuteLow;		
	}
	public String toString2(){
		return DateUtils.datePrint(year, month, day, hour, min, sec)+" "+price+" "+volume+" "+bid+" "+ask;		
	}
	
	//DATE,TIME,PRICE,VOL,BID,ASK
	public static Tick decodeBigMikeTick(String line){
		Tick t = new Tick();
		//System.out.println(line);
		String[] values = line.split("\t", -1);
		
		String date = values[0];
		String time = values[1];
		String price = values[2].replace(".", "");
		String vol = values[3];
		String bid = values[5].replace(".", "");
		String ask = values[6].replace(".", "");
		
		//System.out.println(price+" "+vol+" "+bid+" "+ask);
		t.setDay(Byte.valueOf(date.substring(8, 10)));
		t.setMonth(Byte.valueOf(date.substring(5, 7)));
		t.setYear(Short.valueOf(date.substring(0, 4)));
		t.setHour(Byte.valueOf(time.substring(0, 2)));
		t.setMin(Byte.valueOf(time.substring(3, 5)));
		t.setSec(Byte.valueOf(time.substring(6, 8)));
		t.setPrice(Integer.valueOf(price));
		t.setVolume(Integer.valueOf(vol));
		t.setBid(Integer.valueOf(bid));
		t.setAsk(Integer.valueOf(ask));
		
		//System.out.println(t.toString());
		
		return t;
	}
	
	public static ArrayList<Tick> readTicksBigMike(String fileName,boolean onlyChanges){
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
		        int lastBid = -1;
		        int lastAsk = -1;
		        int lastDay = -1;
		        while((line=br.readLine())!=null){
		        	Tick t = Tick.decodeBigMikeTick(line);
		        	int day = DateUtils.getDaysValue(t);
		        	if (!onlyChanges || t.getBid()!=lastBid || t.getAsk()!=lastAsk || day!=lastDay){
		        		data.add(t);
		        		i++;
		        		lastBid = t.getBid();
		        		lastAsk = t.getAsk();
		        		lastDay = day;
		        	}
		        	/*if (i%100000 == 0){
		        		System.out.println(i);
		        	}*/
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

	
	
	public static void main(String[] args) throws Exception {
		
		String fileName = "C:\\fxdata\\futuros\\ticks\\ES_04_06_2016.txt";
		
		Tick.readTicksBigMike(fileName,false);
	}
	public void setMaxMin(int maxMin){
		// TODO Auto-generated method stub
		this.maxMin = maxMin;
	}
	public int getMaxMin() {
		return maxMin;
	}
	public void setMinuteBar(int minuteBar) {
		// TODO Auto-generated method stub
		this.minuteBar = minuteBar;
	}
	public int getMinuteBar() {
		return minuteBar;
	}
	public void setMinuteHigh(int minuteHigh) {
		this.minuteHigh = minuteHigh;
	}
	public void setMinuteLow(int minuteLow) {
		this.minuteLow = minuteLow;
	}
	public int getMinuteHigh() {
		return minuteHigh;
	}
	public int getMinuteLow() {
		return minuteLow;
	}
	
	
	
	
}
