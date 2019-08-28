package drosa.finance.utils;

import java.text.DecimalFormat;



import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Formatter;
import java.util.List;


public class PrintUtils {

	
	public static String format(DecimalFormat f, double num) {
        if (Double.isNaN(num) || Double.isInfinite(num))
            return String.valueOf(num);
        return f.format(num);
    }
	
	public static String Print(double d) {
			// TODO Auto-generated method stub		
	           // DecimalFormat df = new DecimalFormat("#.####");                 
		   //return df.format(d).replace(',', '.');
				DecimalFormat df = new DecimalFormat("000.0000");
			   return df.format(d).replace(',', '.');
	            //Formatter df= new Formatter();	           
	            //return df.format("%.4f",d).toString().replace(',', '.');
	}
	
	public static String Print2dec(double d,boolean comma, int zeros) {
		DecimalFormat df = null;
		if (zeros>=6)
			df = new DecimalFormat("000000.00");
		if (zeros==5)
			df = new DecimalFormat("00000.00");
		if (zeros==4)
			df = new DecimalFormat("0000.00");
		if (zeros==3)
			df = new DecimalFormat("000.00");
		if (zeros==2)
			df = new DecimalFormat("00.00");
		if (zeros==1)
			df = new DecimalFormat("0.00");
		
		if (!comma)
			return df.format(d).replace(',', '.');
		return df.format(d);
	}
	
	public static String Print2Int(int d,int zeros) {
		DecimalFormat df = null;
		if (zeros>=6)
			df = new DecimalFormat("000000");
		if (zeros==5)
			df = new DecimalFormat("00000");
		if (zeros==4)
			df = new DecimalFormat("0000");
		if (zeros==3)
			df = new DecimalFormat("000");
		if (zeros==2)
			df = new DecimalFormat("00");
		if (zeros==1)
			df = new DecimalFormat("0");

		return df.format(d);
	}
	
	public static String Print2dec(double d,boolean comma) {
		DecimalFormat df = null;		
		df = new DecimalFormat("0.00");
				
		if (!comma)
			return df.format(d).replace(',', '.');
		return df.format(d);
	}
	
	public static String Print3dec(double d,boolean comma) {
		DecimalFormat df = null;		
		df = new DecimalFormat("0.000");
				
		if (!comma)
			return df.format(d).replace(',', '.');
		return df.format(d);
	}
	
	public static String Print4dec(double d) {
		DecimalFormat df = new DecimalFormat("#.0000");
		return df.format(d).replace(',', '.');
	}
		
	public static String Print5dec(double d) {
		DecimalFormat df = new DecimalFormat("#.00000");
		return df.format(d).replace(',', '.');
	}
	
		public static String Print3(double d) {
	            Formatter df= new Formatter();	           
	            return df.format("%.4f",d).toString().replace(',', '.');
		}
		
	public static String Print2(double d) {
		// TODO Auto-generated method stub
		DecimalFormat df = new DecimalFormat("#.##");
	   return df.format(d).replace(',', '.');
	}
	
	public static String Print2(double d,boolean comma) {
		// TODO Auto-generated method stub
		DecimalFormat df = new DecimalFormat("#.##");
		
	    String res = df.format(d).replace(',', '.');
	    
	    if (comma)
	    	res = df.format(d);
	    
	    return res;
	}
	
	
	public static String Print(double d, int i) {
		// TODO Auto-generated method stub
		DecimalFormat df = new DecimalFormat(".##");
		 return df.format(d).replace(',', '.');
	}



	public static String PrintInt(int d) {
		// TODO Auto-generated method stub
		DecimalFormat df = new DecimalFormat("00000");
		   return df.format(d).replace(',', '.');
	}
	
	public static String PrintInt2(int d) {
		// TODO Auto-generated method stub
		DecimalFormat df = new DecimalFormat("00");
		   return df.format(d).replace(',', '.');
	}

	public static String Print2dec2(double d, boolean millions) {
		// TODO Auto-generated method stub
		DecimalFormat df = new DecimalFormat("000000.00");
		String suf="";
		double d1 = d;
		if (millions){
			if (d>=1000000000000.0){
				d1 = d*1.0/1000000000000.0;
				suf="b";
			}else if (d>=1000000){
					d1 = d*1.0/1000000;
					suf="m";
			}
		}
				
		return df.format(d1)+suf;
	}

	public static void print(ArrayList<Double> values) {
		for (int i=0;i<values.size();i++){
			System.out.println(PrintUtils.Print2(values.get(i), true));
		}
		
	}
	
	public static String getArrayStr(ArrayList<Integer> values,int begin) {
		String str="";
		for (int i=begin;i<values.size();i++){
			str+=values.get(i)+" ";
		}
		return str;
		
	}
	
	
}
