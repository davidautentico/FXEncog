package drosa.utils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Formatter;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.TimeZone;

import drosa.classes.Tick;
import drosa.data.DataProvider;
import drosa.finances.Quote;
import drosa.finances.QuoteShort;

public class DateUtils {

	public static final String DATE_FORMAT_NOW = "yyyy-MM-dd HH:mm:ss";
	public static final String FORMAT_YYYYMMDD = "yyyy-MM-dd";
	
	public static int getDaysValue(Tick t){
		
		return t.getYear()*365+t.getMonth()*30+t.getDay();
	}
	
	public static int getMinutesValue(Tick t){
		
		return (t.getYear()*365+t.getMonth()*30+t.getDay())*1440+t.getHour()*60+t.getMin();
	}
	
	public static int getSecsValue(Tick t){
		
		return t.getYear()*365+t.getMonth()*30+t.getDay();
	}
	
	public static boolean isDateEqual(GregorianCalendar gc1,GregorianCalendar gc2){
		
		if (gc1.get(Calendar.YEAR)==gc2.get(Calendar.YEAR)
				&& gc1.get(Calendar.MONTH)==gc2.get(Calendar.MONTH)
				&& gc1.get(Calendar.DAY_OF_MONTH)==gc2.get(Calendar.DAY_OF_MONTH))
			return true;
		
		
		return false;
	}
	
	public static boolean isDateTimeEqual(Calendar actual,Calendar calendar){
		
		if (actual.get(Calendar.YEAR)==calendar.get(Calendar.YEAR)
				&& actual.get(Calendar.MONTH)==calendar.get(Calendar.MONTH)
				&& actual.get(Calendar.DAY_OF_MONTH)==calendar.get(Calendar.DAY_OF_MONTH)
				&& actual.get(Calendar.HOUR_OF_DAY)==calendar.get(Calendar.HOUR_OF_DAY)
				&& actual.get(Calendar.MINUTE)==calendar.get(Calendar.MINUTE)
				&& actual.get(Calendar.SECOND)==calendar.get(Calendar.SECOND)
				)
			return true;
		
		
		return false;
	}
	
	public static boolean isDateTimeEqualMinute(Calendar actual,Calendar calendar){
		
		if (actual.get(Calendar.YEAR)==calendar.get(Calendar.YEAR)
				&& actual.get(Calendar.MONTH)==calendar.get(Calendar.MONTH)
				&& actual.get(Calendar.DAY_OF_MONTH)==calendar.get(Calendar.DAY_OF_MONTH)
				&& actual.get(Calendar.HOUR_OF_DAY)==calendar.get(Calendar.HOUR_OF_DAY)
				&& actual.get(Calendar.MINUTE)==calendar.get(Calendar.MINUTE)
				)
			return true;
		
		
		return false;
	}
	
	public static String getAlways2digits(int number){
		int res = number+100;
		
		String resStr = String.valueOf(res).substring(1);
		
		return resStr;
	}
	
	public static String getYMD(Calendar cal){
		
		String dateStr = String.valueOf(cal.get(Calendar.YEAR))
					+'.'+getAlways2digits(cal.get(Calendar.MONTH)+1)
					+'.'+getAlways2digits(cal.get(Calendar.DAY_OF_MONTH))
					;
		
		return dateStr;
	}
	
	public static String getDukasFormat(Calendar cal){
		
		String dateStr = String.valueOf(cal.get(Calendar.YEAR))
					+'.'+getAlways2digits(cal.get(Calendar.MONTH)+1)
					+'.'+getAlways2digits(cal.get(Calendar.DAY_OF_MONTH))
					+" "+getAlways2digits(cal.get(Calendar.HOUR_OF_DAY))
					+":"+getAlways2digits(cal.get(Calendar.MINUTE))
					+":"+getAlways2digits(cal.get(Calendar.SECOND))
					;
		
		return dateStr;
	}
	
public static boolean isDateEqual2(GregorianCalendar gc1,GregorianCalendar gc2){
		
		if (gc1.get(Calendar.YEAR)==gc2.get(Calendar.YEAR)
				&& gc1.get(Calendar.MONTH)==gc2.get(Calendar.MONTH)
				&& gc1.get(Calendar.DAY_OF_MONTH)==gc2.get(Calendar.DAY_OF_MONTH)
				&& gc1.get(Calendar.HOUR_OF_DAY)==gc2.get(Calendar.HOUR_OF_DAY)
				&& gc1.get(Calendar.MINUTE)==gc2.get(Calendar.MINUTE)
				)
			return true;
		
		
		return false;
	}
	
	public static final String calendarToString(Calendar cal, String dateformat) {
	    StringBuffer ret = new StringBuffer();
	    if(dateformat.equals(FORMAT_YYYYMMDD) ) {
	      ret.append(cal.get(Calendar.YEAR));
	      ret.append("-");
	      
	      String month = null;
	      int mo = cal.get(Calendar.MONTH) + 1; /* Calendar month is zero indexed, string months are not */
	      if(mo < 10) {
	        month = "0" + mo;
	      }
	      else {
	        month = "" + mo;
	      }
	      ret.append(month);      
	      
	      ret.append("-");
	      
	      String date = null;
	      int dt = cal.get(Calendar.DATE);
	      if(dt < 10) {
	        date = "0" + dt;
	      }
	      else {
	        date = "" + dt;
	      }
	      ret.append(date);
	    }
	    
	    return ret.toString();
	  }
	
	public static String datePrintYMD(Calendar gc){
		 int d = gc.get(Calendar.DAY_OF_MONTH)+100;
		 int m = gc.get(Calendar.MONTH)+1+100;
		 int y = gc.get(Calendar.YEAR);
		
		 
		 return String.valueOf(d).substring(1)+"-"+String.valueOf(m).substring(1)+"-"+String.valueOf(y)
				 ;
	}
	
	public static String datePrint(int year,int month,int day,int h,int m,int s){
		 int d  = day+100;
		 int mn = month+100;
		 int y  = year;
		 int hh = h+100;
		 int mt = m+100;
		 int ss = s+100;
		 
		 return String.valueOf(d).substring(1)+"-"+String.valueOf(mn).substring(1)+"-"+String.valueOf(y)
				 +" "+String.valueOf(hh).substring(1)+":"+String.valueOf(mt).substring(1)
				 +":"+String.valueOf(ss).substring(1);
	}
	
	public static String datePrint(int year,int month,int day){
		 int d  = day+100;
		 int mn = month+100;
		 int y  = year;
		 
		 return String.valueOf(d).substring(1)+"-"+String.valueOf(mn).substring(1)+"-"+String.valueOf(y)
				 ;
	}
	
	public static String datePrint(Calendar gc){
		 int d = gc.get(Calendar.DAY_OF_MONTH)+100;
		 int m = gc.get(Calendar.MONTH)+1+100;
		 int y = gc.get(Calendar.YEAR);
		 int h = gc.get(Calendar.HOUR_OF_DAY)+100;
		 int mn = gc.get(Calendar.MINUTE)+100;
		 int ss = gc.get(Calendar.SECOND)+100;
		 
		 return String.valueOf(d).substring(1)+"-"+String.valueOf(m).substring(1)+"-"+String.valueOf(y)
				 +" "+String.valueOf(h).substring(1)+":"+String.valueOf(mn).substring(1)
				 +":"+String.valueOf(ss).substring(1);
	}
	
	public static String datePrintMs(Calendar gc){
		 int d = gc.get(Calendar.DAY_OF_MONTH)+100;
		 int m = gc.get(Calendar.MONTH)+1+100;
		 int y = gc.get(Calendar.YEAR);
		 int h = gc.get(Calendar.HOUR_OF_DAY)+100;
		 int mn = gc.get(Calendar.MINUTE)+100;
		 int ss = gc.get(Calendar.SECOND)+100;
		 int ms = gc.get(Calendar.SECOND)+1000;
		 
		 return String.valueOf(d).substring(1)+"-"+String.valueOf(m).substring(1)+"-"+String.valueOf(y)
				 +" "+String.valueOf(h).substring(1)+":"+String.valueOf(mn).substring(1)
				 +":"+String.valueOf(ss).substring(1)
				 +"."+String.valueOf(ms).substring(1)
				 ;
	}
	
	public static String datePrint(Date dt){
		GregorianCalendar cal = new GregorianCalendar(); 
		cal.setTime(dt);
		return datePrint(cal);
	}
	
	public static String datePrint3(Date dt){
		GregorianCalendar cal = new GregorianCalendar(); 
		cal.setTime(dt);
		String str=String.valueOf(cal.get(Calendar.DAY_OF_MONTH))+"-"+String.valueOf(cal.get(Calendar.MONTH)+1);
		return str;
	}
	
		
	public static String yearPrint(Date dt){
		Calendar cal = DateUtils.getCalendar(dt);
		return String.valueOf(cal.get(Calendar.YEAR));
	}
	
	public static String monthPrint(Date dt){
		Calendar cal = DateUtils.getCalendar(dt);
		return String.valueOf((cal.get(Calendar.MONTH)+1));
	}
	
	public static GregorianCalendar getTodayDate()
	{
		GregorianCalendar cal = (GregorianCalendar) GregorianCalendar.getInstance();
		try{			
			Date dt = cal.getTime();
			SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
			sdf.format(dt);
			cal.setTime(dt);		
		}catch(Exception e){
			System.out.println("[error] getTodayDate: "+e.getMessage());
			return null;
		}		
		return cal;
	}
	
	public static Date stringToDate(String dateStr)
	{
		//System.out.println("[ stringToDateFormat] datestr: "+dateStr);
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");	
		Date d = null;	
		try{
			d = sdf.parse(dateStr);
		}catch (Exception e){
			System.out.println("[Error] stringToDateFormat: "+e.getMessage());
		}		
		return d;
	}
	
	public static GregorianCalendar stringToDateFormat(String dateStr) 
	{
		//System.out.println("[ stringToDateFormat] datestr: "+dateStr);
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");	
		Date d = null;	
		try{
			d = sdf.parse(dateStr);
		}catch (Exception e){
			System.out.println("[Error] stringToDateFormat: "+e.getMessage());
		}
		//System.out.println("[ stringToDateFormat] date before gregorian: "+d);
		GregorianCalendar cal = (GregorianCalendar)GregorianCalendar.getInstance();
		cal.setTime(d);
		return cal;
	}
	
	public static GregorianCalendar stringToDateFormat2(String dateStr) 
	{
		//System.out.println("[ stringToDateFormat] datestr: "+dateStr);
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");	
		Date d = null;	
		try{
			d = sdf.parse(dateStr);
		}catch (Exception e){
			System.out.println("[Error] stringToDateFormat: "+e.getMessage());
		}
		//System.out.println("[ stringToDateFormat] date before gregorian: "+d);
		GregorianCalendar cal = (GregorianCalendar)GregorianCalendar.getInstance();
		cal.setTime(d);
		return cal;
	}
	
	 public static String now() {
		    Calendar cal = Calendar.getInstance();
		    SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT_NOW);
		    return sdf.format(cal.getTime());

		  }
	
	public static GregorianCalendar getCalendar(String timestamp)
    throws Exception {
   /*
   ** we specify Locale.US since months are in english
   */
   SimpleDateFormat sdf = new SimpleDateFormat
     ("dd-MM-yyyy", Locale.US);
   Date d = sdf.parse(timestamp);
   GregorianCalendar cal = (GregorianCalendar)GregorianCalendar.getInstance();
   cal.setTime(d);
   return cal;
 }
	
	public static Calendar getCalendar(Date d){
		Calendar cal=Calendar.getInstance();
		cal.setTime(d);  
		
		return cal;
	}

	public static boolean isTodayDate(Date dateTime) {
		if (dateTime== null) return false;
		// TODO Auto-generated method stub
		//System.out.println("[isTodayDate] date: "+dateTime);
		Calendar cal = Calendar.getInstance();
		Calendar currentcal = Calendar.getInstance();		
		cal.setTime(dateTime);		
		currentcal.set(currentcal.get(Calendar.YEAR),
		currentcal.get(Calendar.MONTH), currentcal.get(Calendar.DAY_OF_MONTH));
		
		
		if(currentcal.get(Calendar.DAY_OF_MONTH)==cal.get(Calendar.DAY_OF_MONTH) 
				&& currentcal.get(Calendar.MONTH)==cal.get(Calendar.MONTH)
				&& currentcal.get(Calendar.YEAR)==cal.get(Calendar.YEAR)){
			
			//System.out.println("[isTodayDate] ES EL MISMO DIA");
			return true;
		}
		else
			return false;				
	}
	
	public static Date getDateForexData0(String dateStr){
		Date date = new Date();
		

			Integer y = Integer.valueOf(dateStr.substring(6, 10));
			Integer d = Integer.valueOf(dateStr.substring(3, 5));
			Integer m = Integer.valueOf(dateStr.substring(0, 2));
			
			date.setDate(d);
			date.setMonth(m-1);
			date.setYear(y-1900);

		return date;		
	}
	
	public static Date getDate(String dateStr){
		Date date = new Date();
		
		//System.out.println("getdate");
		if (dateStr.contains(".")){
			Integer y = Integer.valueOf(dateStr.split("\\.")[0].trim());
			Integer m = Integer.valueOf(dateStr.split("\\.")[1].trim());
			Integer d = Integer.valueOf(dateStr.split("\\.")[2].trim());
			
			date.setDate(d);
			date.setMonth(m-1);
			date.setYear(y-1900);
			
			//System.out.println("date : "+datePrint(date));
		}
		return date;		
	}
	
	public static Date getDate2(String dateStr){
		Date date = new Date();
		
		//System.out.println("getdate");
		if (dateStr.contains("-")){
			Integer d = Integer.valueOf(dateStr.split("\\-")[0].trim());
			Integer m = Integer.valueOf(dateStr.split("\\-")[1].trim());
			Integer y = Integer.valueOf(dateStr.split("\\-")[2].trim());
			
			date.setDate(d);
			date.setMonth(m-1);
			date.setYear(y-1900);
			
			//System.out.println("date : "+datePrint(date));
		}
		return date;		
	}
	
	public static Date getDateTime(String dateStr,String timeStr){
		Date date = new Date();
		
		String datefor = null;
		//fecha
		if (dateStr.contains(".")){
			Integer y = Integer.valueOf(dateStr.split("\\.")[0].trim());
			Integer m = Integer.valueOf(dateStr.split("\\.")[1].trim());
			Integer d = Integer.valueOf(dateStr.split("\\.")[2].trim());
			
			datefor =  String.valueOf(y)+"-"+String.valueOf(m)+"-"+String.valueOf(d);				
		}
		
		//hora		
		DateFormat dfm = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		//dfm.setTimeZone(TimeZone.getTimeZone("Europe/Zurich"));
		try {
			//System.out.println("Antes de parse: "+datefor+" "+timeStr+":00");	
			date = dfm.parse(datefor+" "+timeStr+":00");
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
					
		return date;		
	}

	public static Date getDateTime2(String date, String time) {
		// TODO Auto-generated method stub
		return null;
	}

	public static Date stringToDate2(String dateStr) {
		// TODO Auto-generated method stub
		Date date = new Date();
			
		DateFormat dfm = new SimpleDateFormat("yyyy-MM-dd");
		//dfm.setTimeZone(TimeZone.getTimeZone("Europe/Zurich"));
		try {
			//System.out.println("Antes de parse: "+datefor+" "+timeStr+":00");	
			date = dfm.parse(dateStr);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
		
		return date;
	}

	public static String datePrint2(Date d) {
		// TODO Auto-generated method stub
		return String.valueOf(d.getYear()+1900)+"-"+String.valueOf(d.getMonth()+1)+"-"+
			   String.valueOf(d.getDate());
	}

	public static Date getCSIDate(String dateStr) {
		// TODO Auto-generated method stub
		Date date = new Date();
		
		Integer y = Integer.valueOf(dateStr.substring(0,4));
		Integer m = Integer.valueOf(dateStr.substring(4,6));
		Integer d = Integer.valueOf(dateStr.substring(6,8));
		
		date.setDate(d);
		date.setMonth(m-1);
		date.setYear(y-1900);
		
		
		return date;
	}

	public static Date getPiTradingDate(String dateStr, String timeStr) {
		// TODO Auto-generated method stub
Date date = new Date();
		
		Integer y = Integer.valueOf(dateStr.substring(6,10));
		Integer m = Integer.valueOf(dateStr.substring(0,2));
		Integer d = Integer.valueOf(dateStr.substring(3,5));
		
		Integer hh = Integer.valueOf(timeStr.substring(0,2));
		Integer mm = Integer.valueOf(timeStr.substring(2,4));
		
		date.setDate(d);
		date.setMonth(m-1);
		date.setYear(y-1900);
		date.setHours(hh);
		date.setMinutes(mm);
		
		
		return date;
	}

	public static Date getDukasDate(String dateStr,String timeStr) {
		// TODO Auto-generated method stub
            //System.out.println(dateStr+" "+timeStr);
            Integer hh = Integer.valueOf(timeStr.substring(0,2));
            Integer mm = Integer.valueOf(timeStr.substring(3,5));
            Integer ss = Integer.valueOf(timeStr.substring(6,8));
                
            Date date = getDate(dateStr);
            date.setHours(hh);
            date.setMinutes(mm);
            date.setSeconds(ss);
            //System.out.println("date: "+DateUtils.datePrint(date)+" "+hh+" "+mm+" "+ss);
  		
            return date;
	}
	
	public static Date getDukasDate2(String dateStr,String timeStr) {
		// TODO Auto-generated method stub
            //System.out.println(dateStr+" "+timeStr);
            Integer hh = Integer.valueOf(timeStr.substring(0,2));
            Integer mm = Integer.valueOf(timeStr.substring(3,5));
            Integer ss = Integer.valueOf(timeStr.substring(6,8));
                
            Date date = getDate2(dateStr);
            date.setHours(hh);
            date.setMinutes(mm);
            date.setSeconds(ss);
            //System.out.println("date: "+DateUtils.datePrint(date)+" "+hh+" "+mm+" "+ss);
  		
            return date;
	}
	
	public static Calendar getDukasCalendar(String dateStr,String timeStr) {
		// TODO Auto-generated method stub
            //System.out.println(dateStr+" "+timeStr);
            Integer hourOfDay = Integer.valueOf(timeStr.substring(0,2));
            Integer minute = Integer.valueOf(timeStr.substring(3,5));
            Integer second = Integer.valueOf(timeStr.substring(6,8));
                
            Calendar cal = Calendar.getInstance();
            int date  = Integer.valueOf(dateStr.substring(0,2));
            int month = Integer.valueOf(dateStr.substring(3,5))-1;
            int year  = Integer.valueOf(dateStr.substring(6,10));
            
            cal.set(year, month, date, hourOfDay, minute, second);
            //System.out.println("date: "+DateUtils.datePrint(date)+" "+hh+" "+mm+" "+ss);
  		
            return cal;
	}
	
	public static Date getPepperDate(String dateStr,String timeStr) {
		// TODO Auto-generated method stub
            //System.out.println(dateStr+" "+timeStr);
            Integer hh = Integer.valueOf(timeStr.substring(0,2));
            Integer mm = Integer.valueOf(timeStr.substring(3,5));
            //Integer ss = Integer.valueOf(timeStr.substring(6,8));
                
            Date date = getDate(dateStr);
            date.setHours(hh);
            date.setMinutes(mm);
            date.setSeconds(00);
            //System.out.println("date: "+DateUtils.datePrint(date)+" "+hh+" "+mm+" "+ss);
  		
            return date;
	}
	
	public static Date getForexDataDate(String dateStr,String timeStr) {
		// TODO Auto-generated method stub
            //System.out.println(dateStr+" "+timeStr);
            Integer hh = Integer.valueOf(timeStr.substring(0,2));
            Integer mm = Integer.valueOf(timeStr.substring(3,5));
            //Integer ss = Integer.valueOf(timeStr.substring(6,8));
                
            Date date = getDateForexData0(dateStr);
            date.setHours(hh);
            date.setMinutes(mm);
            date.setSeconds(00);
            //System.out.println("date: "+DateUtils.datePrint(date)+" "+hh+" "+mm+" "+ss);
  		
            return date;
	}
	
	public static Calendar getTrueFxDate(String calStr) {
		// TODO Auto-generated method stub
            //System.out.println(dateStr+" "+timeStr);
			String timeStr = calStr.split(" ")[1].trim();
			String dateStr = calStr.split(" ")[0].trim();
            Integer hourOfDay = Integer.valueOf(timeStr.substring(0,2));
            Integer minute = Integer.valueOf(timeStr.substring(3,5));
            //Integer ss = Integer.valueOf(timeStr.substring(6,8));
                
            Integer year = Integer.valueOf(dateStr.substring(0,4));
            Integer month =  Integer.valueOf(dateStr.substring(4,6));
            Integer date =  Integer.valueOf(dateStr.substring(6,8));
            
            Calendar cal = Calendar.getInstance();
            cal.set(year, month-1, date, hourOfDay, minute,0);
            //System.out.println("date: "+DateUtils.datePrint(cal));
  		
            return cal;
	}

	public static int findLastDateIndex(ArrayList<Quote> data,Quote toFind,int type){
        Calendar calOr = Calendar.getInstance();
        calOr.setTime(toFind.getDate());
        Calendar calF = Calendar.getInstance();
        calF.setTime(toFind.getDate());
        if (type==1){
            int day = calF.get(Calendar.DAY_OF_WEEK);  
            if (day==Calendar.SUNDAY) calF.add(Calendar.DAY_OF_YEAR, -2);
            else if (day==Calendar.MONDAY) calF.add(Calendar.DAY_OF_YEAR, -3);
            else calF.add(Calendar.DAY_OF_YEAR, -1);
        }else if (type==2){
            calF.add(Calendar.WEEK_OF_YEAR, -1);
        }else if (type==3){
            calF.add(Calendar.MONTH, -1);
        }
        /*System.out.println("[findLastDateIndex] original - actualizado "
                +DateUtils.getDayName(calOr.get(Calendar.DAY_OF_WEEK))+" "+calOr.get(Calendar.DAY_OF_YEAR)
                +"-"+DateUtils.getDayName(calF.get(Calendar.DAY_OF_WEEK))+" "+calF.get(Calendar.DAY_OF_YEAR)
                );
       */
        Calendar actual = Calendar.getInstance();
        for (int i=0;i<data.size();i++){
            Quote q = data.get(i);
            actual.setTime(q.getDate());
            int year = actual.get(Calendar.YEAR);
            int week = actual.get(Calendar.WEEK_OF_YEAR);
            int month = actual.get(Calendar.MONTH);
            int day = actual.get(Calendar.DAY_OF_YEAR);
            int dayWeek = actual.get(Calendar.DAY_OF_WEEK);
            
           /* System.out.println("[findLastDateIndex] actual "
                +year
                +" "+day
                +" "+DateUtils.getDayName(dayWeek)
                );
            */
            if (year == calF.get(Calendar.YEAR)){
                if (type==1 && day == calF.get(Calendar.DAY_OF_YEAR))
                    return i;
                if (type==2 && week == calF.get(Calendar.WEEK_OF_YEAR))
                    return i;
                if (type==3 && month == calF.get(Calendar.MONTH))
                    return i;
            }
            
        }
        return -1;
    }
	
	public static int calculatePepperGMTOffset(Calendar cal){
        int offset = 2;
        
        int calY = cal.get(Calendar.YEAR);
        int calM = cal.get(Calendar.MONTH);
        int calD = cal.get(Calendar.DAY_OF_MONTH);
        
        if (calY==2009){
            if ((calM>Calendar.MARCH && calM<Calendar.NOVEMBER) 
                    || (calM==Calendar.MARCH && calD>=8)
                    || (calM==Calendar.NOVEMBER && calD<=1)
                    ){
                return 3;
            }
        }
        
        if (calY==2010){
            if ((calM>Calendar.MARCH && calM<Calendar.NOVEMBER) 
                    || (calM==Calendar.MARCH && calD>=14)
                    || (calM==Calendar.NOVEMBER && calD<7)
                    ){
                return 3;
            }
        }
        
        if (calY==2011){
            if ((calM>Calendar.MARCH && calM<Calendar.NOVEMBER) 
                    || (calM==Calendar.MARCH && calD>=13)
                    || (calM==Calendar.NOVEMBER && calD<6)
                    ){
                return 3;
            }
        }
        
        if (calY==2012){
            if ((calM>Calendar.MARCH && calM<Calendar.NOVEMBER) 
                    || (calM==Calendar.MARCH && calD>=11)
                    || (calM==Calendar.NOVEMBER && calD<4)
                    ){
                return 3;
            }
        }
        
        if (calY==2013){
            if ((calM>Calendar.MARCH && calM<Calendar.NOVEMBER) 
            	|| (calM==Calendar.MARCH && calD>=10)
            	|| (calM==Calendar.NOVEMBER && calD<3)
            	){
                return 3;
            }
        }
        
        if (calY==2014){
            if ((calM>Calendar.MARCH && calM<Calendar.NOVEMBER) 
            	|| (calM==Calendar.MARCH && calD>=9)
            	|| (calM==Calendar.NOVEMBER && calD<3)
            	){
                return 3;
            }
        }
        
        if (calY==2015){
            if ((calM>Calendar.MARCH && calM<Calendar.NOVEMBER) 
            	|| (calM==Calendar.MARCH && calD>=9)
            	|| (calM==Calendar.NOVEMBER && calD<3)
            	){
                return 3;
            }
        }
        
        if (calY==2016){
            if ((calM>Calendar.MARCH && calM<=Calendar.OCTOBER)
            	|| (calM==Calendar.MARCH && calD>=13)
            	|| (calM==Calendar.OCTOBER && calD<31)
            	){
                return 3;
            }
        }
        
        if (calY==2017){
            if ((calM>Calendar.MARCH  && calM<=Calendar.OCTOBER)
            	|| (calM==Calendar.MARCH && calD>=13)
            	|| (calM==Calendar.OCTOBER && calD<31)
            	){
                return 3;
            }
        }
        
        if (calY==2018){
            if ((calM>Calendar.MARCH  && calM<=Calendar.OCTOBER)
            	|| (calM==Calendar.MARCH && calD>=13)
            	|| (calM==Calendar.OCTOBER && calD<31)
            	){
                return 3;
            }
        }
        
        if (calY==2019){
            if ((calM>Calendar.MARCH  && calM<=Calendar.OCTOBER)
            	|| (calM==Calendar.MARCH && calD>=10)
            	|| (calM==Calendar.OCTOBER && calD<31)
            	){
                return 3;
            }
        }
        
        return offset;
    }

	public static Calendar getTickDate(String dateStr, String timeStr, DataProvider dataProvider) {
		// TODO Auto-generated method stub
		int y = -1;
		int m = -1;
		int d = -1;
		
		if (dataProvider==DataProvider.DUKASCOPY_FOREX){
			y = Integer.valueOf(dateStr.split("\\.")[0].trim());
			m = Integer.valueOf(dateStr.split("\\.")[1].trim());
			d = Integer.valueOf(dateStr.split("\\.")[2].trim());
		} else if (dataProvider==DataProvider.PEPPERSTONE_FOREX){
			y = Integer.valueOf(dateStr.substring(0,4));
			m = Integer.valueOf(dateStr.substring(4,6));
			d = Integer.valueOf(dateStr.substring(6,8));
		} 
		
		Integer hh = Integer.valueOf(timeStr.substring(0,2));
        Integer mm = Integer.valueOf(timeStr.substring(3,5));
        Integer ss = Integer.valueOf(timeStr.substring(6,8));
        Integer ms = Integer.valueOf(timeStr.substring(9,12));
              
        
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR,y);
        cal.set(Calendar.MONTH,m-1);
        cal.set(Calendar.DAY_OF_MONTH, d);
        cal.set(Calendar.HOUR_OF_DAY,hh);
        cal.set(Calendar.MINUTE, mm);
        cal.set(Calendar.SECOND, ss);
        cal.set(Calendar.MILLISECOND,ms);
                    
          //System.out.println("date: "+DateUtils.datePrint(date)+" "+hh+" "+mm+" "+ss);
		
        return cal;
	}

	public static boolean isSameDay(Calendar cal1, Calendar cal2) {
		// TODO Auto-generated method stub
		int y1 = cal1.get(Calendar.YEAR);
		int m1 = cal1.get(Calendar.MONTH);
		int d1 = cal1.get(Calendar.DAY_OF_MONTH);
		int y2 = cal2.get(Calendar.YEAR);
		int m2 = cal2.get(Calendar.MONTH);
		int d2 = cal2.get(Calendar.DAY_OF_MONTH);
		
		if (y1==y2 && m1==m2 && d1==d2 ) return true;
		return false;
	}

	public static int isGreater(Calendar cal1,Calendar cal2,QuoteShort q1, QuoteShort q2) {
		// TODO Auto-generated method stub
		cal1.set(q1.getYear(), q1.getMonth()-1, q1.getDay(), q1.getHh(), q1.getMm(),q1.getSs());
		cal2.set(q2.getYear(), q2.getMonth()-1, q2.getDay(), q2.getHh(), q2.getMm(),q2.getSs());
		
		if (cal1.getTimeInMillis()>cal2.getTimeInMillis()){ 
			//System.out.println("return 1");
			return 1;
		}
		if (cal1.getTimeInMillis()<cal2.getTimeInMillis()){
			System.out.println("return -1: "+DateUtils.datePrint(cal1)+" "+DateUtils.datePrint(cal2));
			return -1;
		}
		//System.out.println("return 0: "+DateUtils.datePrint(cal1)+" "+DateUtils.datePrint(cal2));
		return 0;
	}

}
