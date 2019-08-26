package drosa.finance.classes;

import java.util.ArrayList;
import java.util.Calendar;

import drosa.finance.utils.DateUtils;


public class TestLines {
	 public static void calculateCalendarAdjustedSinside(ArrayList<QuoteShort> data){
	        
		 	//ArrayList<QuoteShort> transformed = new ArrayList<QuoteShort>();
		 
	        if (data==null) return ;
	        Calendar cal = Calendar.getInstance();
	        for (int i=0;i<data.size();i++){
	        	QuoteShort q = data.get(i);
	        	//QuoteShort qNew = new QuoteShort();
	        	//qNew.copy(q);
	        	QuoteShort.getCalendar(cal, q);	        	
	            int offset = DateUtils.calculatePepperGMTOffset(cal);
	            //System.out.println("cal antes: "+DateUtils.datePrint(cal));
	            cal.add(Calendar.HOUR_OF_DAY, offset);
	            //System.out.println("cal despues: "+DateUtils.datePrint(cal)+' '+offset);
	            q.setCal(cal);
	           
	            //transformed.add(qNew);
	        }	        	        
	 }
}
