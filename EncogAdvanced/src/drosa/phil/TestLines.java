package drosa.phil;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import drosa.DAO.DAO;
import drosa.data.ConvertLib;
import drosa.data.DataProvider;
import drosa.experimental.ticksStudy.Tick;
import drosa.finances.Quote;
import drosa.finances.QuoteBidAsk;
import drosa.finances.QuoteShort;
import drosa.utils.DateUtils;
import drosa.utils.PrintUtils;

public class TestLines {
	
	 public static ArrayList<Quote> calculateCalendarAdjusted(ArrayList<Quote> data){
	        
		 	ArrayList<Quote> transformed = new ArrayList<Quote>();
		 
	        if (data==null) return null;
            Calendar cal = Calendar.getInstance();
	        for (int i=0;i<data.size();i++){
	        	Quote q = data.get(i);
	        	Quote qNew = new Quote();
	        	qNew.copy(q);
	            Date d = data.get(i).getDate();

	            cal.setTimeInMillis(d.getTime());
	            int offset = DateUtils.calculatePepperGMTOffset(cal);

				//if (cal.get(Calendar.YEAR)==2009)
					//System.out.println(DateUtils.datePrint(d)+" "+offset);
	            
	            cal.add(Calendar.HOUR_OF_DAY, offset);
	            qNew.getDate().setTime(cal.getTimeInMillis());
	            
	            //f (cal.get(Calendar.YEAR)==2009)
					//System.out.println(PrintUtils.Print(qNew));
	            
	            transformed.add(qNew);
	        }
	        
	        return transformed;
	 }
	 
	 public static ArrayList<QuoteShort> calculateCalendarAdjustedS(ArrayList<QuoteShort> data){
	        
		 	ArrayList<QuoteShort> transformed = new ArrayList<QuoteShort>();
		 
	        if (data==null) return null;
	        Calendar cal = Calendar.getInstance();
	        for (int i=0;i<data.size();i++){
	        	QuoteShort q = data.get(i);
	        	QuoteShort qNew = new QuoteShort();
	        	qNew.copy(q);
	        	QuoteShort.getCalendar(cal, q);	        	
	            int offset = DateUtils.calculatePepperGMTOffset(cal);
	            //System.out.println("cal antes: "+DateUtils.datePrint(cal));
	            cal.add(Calendar.HOUR_OF_DAY, offset);
	            //System.out.println("cal despues: "+DateUtils.datePrint(cal)+' '+offset);
	            qNew.setCal(cal);
	           
	            transformed.add(qNew);
	        }
	        
	        return transformed;
	 }
	 
	 public static void calculateCalendarAdjustedSinside(ArrayList<QuoteShort> data){
	        
		 	//ArrayList<QuoteShort> transformed = new ArrayList<QuoteShort>();
		 
	        if (data==null) return ;
	        Calendar cal = Calendar.getInstance();
	        for (int i=0;i<data.size();i++){
	        	QuoteShort q = data.get(i);
	        	//QuoteShort qNew = new QuoteShort();
	        	//qNew.copy(q);
	        	QuoteShort.getCalendar(cal, q);	
	        	int h = cal.get(Calendar.HOUR_OF_DAY);
	            int offset = DateUtils.calculatePepperGMTOffset(cal);
	            
	           /* if (offset>0 
	            		&& cal.get(Calendar.DAY_OF_WEEK)==Calendar.FRIDAY
	            		
	            		) {
	            	offset += 48;//para pasar el fin de semana
	            }*/
	            //System.out.println("cal antes: "+DateUtils.datePrint(cal));
	            cal.add(Calendar.HOUR_OF_DAY, offset);
	            //System.out.println("cal despues: "+DateUtils.datePrint(cal)+' '+offset);
	            q.setCal(cal);
	           
	            //transformed.add(qNew);
	        }	        	        
	 }
	 
	 public static ArrayList<Tick> calculateCalendarAdjustedT(ArrayList<Tick> data){
	        
		 	ArrayList<Tick> transformed = new ArrayList<Tick>();
		 
	        if (data==null) return null;
	        Calendar calq = Calendar.getInstance();
	        for (int i=0;i<data.size();i++){
	        	Tick q = data.get(i);
	        	Tick qNew = new Tick();
	        	qNew.copy(q);
	        	Tick.getCalendar(calq, q);
	            int offset = DateUtils.calculatePepperGMTOffset(calq);

				//if (cal.get(Calendar.YEAR)==2009)
					//System.out.println(DateUtils.datePrint(d)+" "+offset);
	            
	            calq.add(Calendar.HOUR_OF_DAY, offset);
	            qNew.setCal(calq);
	            
	            //f (cal.get(Calendar.YEAR)==2009)
					//System.out.println(PrintUtils.Print(qNew));
	            
	            transformed.add(qNew);
	        }
	        
	        return transformed;
	 }
	 
	 public static ArrayList<QuoteBidAsk> calculateCalendarAdjustedBidAsk(ArrayList<QuoteBidAsk> data){
	        
		 	ArrayList<QuoteBidAsk> transformed = new ArrayList<QuoteBidAsk>();
		 
	        if (data==null) return null;	      
	        for (int i=0;i<data.size();i++){
	        	QuoteBidAsk q = data.get(i);
	        	QuoteBidAsk qNew = new QuoteBidAsk();
	        	qNew.copy(q);
	            Calendar cal = data.get(i).getCal();
	            int offset = DateUtils.calculatePepperGMTOffset(cal);
	            //System.out.println(DateUtils.datePrint(d)+" "+offset);
	            
	            cal.add(Calendar.HOUR_OF_DAY, offset);
	            qNew.getCal().setTimeInMillis(cal.getTimeInMillis());
	            transformed.add(qNew);
	        }
	        
	        return transformed;
	 }

	 public static double getAverageRange(ArrayList<Quote> dailyData,int bar){
	        int i;
	        int longADRPeriod  = 100;
	        int shortADRPeriod = 3;
	        double localHigh   = 0;
	        double localLow    = 0;
	        double highMALong  = 0;
	        double lowMALong   = 0;
	        double highMAShort = 0;
	        double lowMAShort  = 0;
	        i=0;
	        Calendar cal = Calendar.getInstance();
	        int total=0;
	        //System.out.println("[getAverageRange] bar "+bar);
	        while (total<longADRPeriod)
		{
	            if ((bar-i)>=0)
	            {
	                Quote q = dailyData.get(bar-i);
	                cal.setTime(q.getDate());
	                if (cal.get(Calendar.DAY_OF_WEEK)>=Calendar.MONDAY 
	                        && cal.get(Calendar.DAY_OF_WEEK)<=Calendar.FRIDAY){
	                    localLow  = dailyData.get(bar-i).getLow();
	                    localHigh = dailyData.get(bar-i).getHigh();
	                    lowMALong += localLow;
	                    highMALong += localHigh;
	                    if ( i < shortADRPeriod ){
	                        lowMAShort += localLow;
	                        highMAShort += localHigh;
	                    }
	                    total++;
	                   /* System.out.println("lowMAShort highMAShort localLow localHigh:" 
	                            +PrintUtils.Print(lowMAShort)
	                            +" "+PrintUtils.Print(highMAShort)
	                            +" "+PrintUtils.Print(localLow)
	                            +" "+PrintUtils.Print(localHigh)
	                            );
	                      */
	                }
	            }else{
	                break;
	            }
	            i++;
		}
		lowMALong /= longADRPeriod;
		highMALong /= longADRPeriod;
		lowMAShort /= shortADRPeriod;
		highMAShort /= shortADRPeriod;

	        if ((highMALong - lowMALong)<=(highMAShort - lowMAShort)){
	            return (highMALong - lowMALong);
	        }else{
	            return (highMAShort - lowMAShort);
	        }
	    }
	 
	 
	 public static void testDP(ArrayList<Quote> data,ArrayList<Quote> dailyData,
				ArrayList<Quote> weeklyData,ArrayList<Quote> monthlyData,
				double pipsL,double pipsH,int bars){
			
			Calendar qCal = Calendar.getInstance();
			int beforeDay = -1;
			//POINTS
			double DO = -1;
			double DP = -1;
			double DR1 = -1;
			double DS1 = -1;
			double lastHigh = -1;
			double lastLow = -1;
			double lastClose = -1;
			boolean DPBreached = false;
			double DPBreachValue = -1;
			int totalBreached=0;
			int totalClosed=0;
			for (int i=0;i<data.size();i++){
				Quote q = data.get(i);
				qCal.setTime(q.getDate());
				int actualDay = qCal.get(Calendar.DAY_OF_YEAR);
				
				if (actualDay!=beforeDay){
					int lastDay   =  DateUtils.findLastDateIndex(dailyData,q,1);
					int lastWeek  =  DateUtils.findLastDateIndex(weeklyData,q,2);
					int lastMonth =  DateUtils.findLastDateIndex(monthlyData,q,3);
					//double range = getAverageRange(dailyData,lastDay);
					if (lastDay!=-1){
						lastHigh = dailyData.get(lastDay).getHigh();
	                    lastLow = dailyData.get(lastDay).getLow();
	                    lastClose = dailyData.get(lastDay).getClose();
	                    DO = q.getOpen();
	                    double range = getAverageRange(dailyData,lastDay);
	                    //DP = DO + ( range * 0.382);//fibr1
	                    DP = ( lastHigh + lastLow + lastClose ) / 3;
	                    //DP =( 2 * DPa ) - lastLow;
	                    //DR1 = ( 2 * DP ) - lastLow;
	                    //DS1 = ( 2 * DP ) - lastHigh;
	                    					
						beforeDay = actualDay;
						
						System.out.println("**NEW Day: "+DateUtils.datePrint(q.getDate())
								+" "+PrintUtils.Print(DO)
								+" "+PrintUtils.Print(DP)
								+" "+PrintUtils.Print(DR1)
								+" "+PrintUtils.Print(DS1)
						);
						DPBreached = false;
					}
				}
				
				if (DP>DO){
					if (!DPBreached){				
						if (q.getHigh()>=(DP+pipsL) && q.getHigh()<=(DP+pipsH)){
							System.out.println("1 DP superado en "+i
									+" "+PrintUtils.Print(q.getHigh())
									+" "+PrintUtils.Print(DP)
									);		
							DPBreached= true;
							DPBreachValue = q.getHigh();
							totalBreached++;							
						}
					}else{//breached
						/*if (q.getHigh()<(DP)){
							System.out.println("DP anulado "+i+" "+PrintUtils.Print(q.getHigh()));		
							DPBreached= false;
							totalClosed++;
						}*/
						if (q.getLow()<DO){
							System.out.println("DP anulado "+i+" "+PrintUtils.Print(q.getHigh()));		
							DPBreached= false;
							totalClosed++;
						}
					}
				}else if (DP<DO){
						if (!DPBreached){				
							if (q.getLow()<=(DP-pipsL) && q.getLow()>=(DP-pipsH)){
								System.out.println("2 DP superado "+i
										+" "+PrintUtils.Print(q.getLow())
										+" "+PrintUtils.Print(DP)
										);
								DPBreached = true;
								DPBreachValue = q.getLow();
								totalBreached++;
							}
						}else{//breached
							/*if (q.getLow()>(DP)){
								System.out.println("DP anulado "+i+" "+PrintUtils.Print(q.getLow()));	
								DPBreached= false;	
								totalClosed++;
							}*/
							if (q.getHigh()>DO){
								System.out.println("DP anulado "+i+" "+PrintUtils.Print(q.getHigh()));		
								DPBreached= false;
								totalClosed++;
							}
						}
				}
				
				
			}
			System.out.println("Total breached and closed: "+totalBreached
					+" "+totalClosed
					+" "+PrintUtils.Print((totalClosed*100.0)/totalBreached)+"%"
					);
	}
	 
	 public static void testLine3(ArrayList<Quote> data,ArrayList<Quote> dailyData,
				ArrayList<Quote> weeklyData,ArrayList<Quote> monthlyData,
				LineType breachedLine,LineType targetLine,double pipsL,double pipsH,int bars){
			
			Calendar qCal = Calendar.getInstance();
			int beforeDay = -1;
			//POINTS
			double DO = -1;
			double DP = -1;double DR1=-1;double DR2=-1;double DR3=-1;double DS1=-1;double DS2=-1;double DS3=-1;
			double WP = -1;double WR1=-1;double WR2=-1;double WR3=-1;double WS1=-1;double WS2=-1;double WS3=-1;
			double MP = -1;double MR1=-1;double MR2=-1;double MR3=-1;double MS1=-1;double MS2=-1;double MS3=-1;
			double FIBR1=-1;double FIBS1=-1;double FIBR2=-1;double FIBS2=-1;double FIBR3=-1;double FIBS3=-1;
			double FIBR4=-1;double FIBS4=-1;double FIBR5=-1;double FIBS5=-1;
			double lastHighD = -1;double lastLowD = -1;double lastCloseD = -1;
			double lastHighW = -1;double lastLowW = -1;double lastCloseW = -1;
			double lastHighM = -1;double lastLowM = -1;double lastCloseM = -1;
			boolean DPBreached = false;
			int totalBreached=0;
			int totalClosed=0;
			int totalDOClosed=0;
			double firstBreachedValue =-1;
			double breachedLineValue=-1;
			double range = -1;
			double actualMaxDesv=-999;
			ArrayList<PhilLine> lines = new ArrayList<PhilLine>(); 
			ArrayList<Integer> upBreachPoints = new ArrayList<Integer>();  
			ArrayList<Integer> downBreachPoints = new ArrayList<Integer>();
			ArrayList<Integer> lineToDoTotal = new ArrayList<Integer>();
			ArrayList<Integer> lineToDo = new ArrayList<Integer>();
			ArrayList<Double> lineToDoDesv = new ArrayList<Double>();
			ArrayList<Double> lineToDoPos = new ArrayList<Double>();
			for (int i=0;i<=4;i++){
				lineToDo.add(0);
				lineToDoTotal.add(0);
				lineToDoDesv.add(0.0);
				lineToDoPos.add(0.0);
			}
					
			for (int i=0;i<data.size();i++){
				Quote q = data.get(i);
				qCal.setTime(q.getDate());
				int actualDay = qCal.get(Calendar.DAY_OF_YEAR);
				int dayWeek = qCal.get(Calendar.DAY_OF_WEEK);
				
				if (dayWeek==Calendar.SATURDAY || dayWeek==Calendar.SUNDAY){
	            	continue;
	            }
				
				if (actualDay!=beforeDay){
					
					int lastDay   =  DateUtils.findLastDateIndex(dailyData,q,1);
					int lastWeek  =  DateUtils.findLastDateIndex(weeklyData,q,2);
					int lastMonth =  DateUtils.findLastDateIndex(monthlyData,q,3);
					//double range = getAverageRange(dailyData,lastDay);
					lines.clear();
					if (lastDay!=-1){
						lastHighD = dailyData.get(lastDay).getHigh();
	                    lastLowD = dailyData.get(lastDay).getLow();
	                    lastCloseD = dailyData.get(lastDay).getClose();
	                    DO = q.getOpen();
	                    range = getAverageRange(dailyData,lastDay);                                       					
						beforeDay = actualDay;					
						//System.out.println("**NEW Day DO: "+DateUtils.datePrint(q.getDate())
						//		+" "+PrintUtils.Print(DO)
						//);
						DP = ( lastHighD + lastLowD + lastCloseD ) / 3;
						DR1 = ( 2 * DP ) - lastLowD;
	                    DS1 = ( 2 * DP ) - lastHighD;
	                    DR2 = DP + ( lastHighD - lastLowD );
	                    DS2 = DP - ( lastHighD - lastLowD );
	                    DR3 = ( 2 * DP ) + ( lastHighD - ( 2 * lastLowD ) );
	                    DS3 = ( 2 * DP ) - ( ( 2 * lastHighD ) - lastLowD );
	                    lines.add(PhilLine.createLine(LineType.DO, DO));
	                    lines.add(PhilLine.createLine(LineType.DP, DP));
	                    lines.add(PhilLine.createLine(LineType.DR1, DR1));
	                    lines.add(PhilLine.createLine(LineType.DS1, DS1));
	                    lines.add(PhilLine.createLine(LineType.DR2, DR2));
	                    lines.add(PhilLine.createLine(LineType.DS2, DS2));
	                    lines.add(PhilLine.createLine(LineType.DR3, DR3));
	                    lines.add(PhilLine.createLine(LineType.DS3, DS3));
					}
					if (lastWeek!=-1){
						lastHighW = dailyData.get(lastWeek).getHigh();
	                    lastLowW = dailyData.get(lastWeek).getLow();
	                    lastCloseW = dailyData.get(lastWeek).getClose();
	                    WP = ( lastHighW + lastLowW + lastCloseW ) / 3;
	                    WR1 = ( 2 * WP ) - lastLowW;
	                    WS1 = ( 2 * WP ) - lastHighW;
	                    WR2 = WP + ( lastHighW - lastLowW );
	                    WS2 = WP - ( lastHighW - lastLowW );
	                    WR3 = ( 2 * WP ) + ( lastHighW - ( 2 * lastLowW ) );
	                    WS3 = ( 2 * WP ) - ( ( 2 * lastHighW ) - lastLowW );
	                   
	                    lines.add(PhilLine.createLine(LineType.WP, WP));
	                    lines.add(PhilLine.createLine(LineType.WR1, WR1));
	                    lines.add(PhilLine.createLine(LineType.WS1, WS1));
	                    lines.add(PhilLine.createLine(LineType.WR2, WR2));
	                    lines.add(PhilLine.createLine(LineType.WS2, WS2));
	                    lines.add(PhilLine.createLine(LineType.WR3, WR3));
	                    lines.add(PhilLine.createLine(LineType.WS3, WS3));
					}
					if (lastMonth!=-1){
						lastHighM = dailyData.get(lastMonth).getHigh();
	                    lastLowM = dailyData.get(lastMonth).getLow();
	                    lastCloseM = dailyData.get(lastMonth).getClose();
	                    MP = ( lastHighM + lastLowM + lastCloseM ) / 3;
	                    MR1 = ( 2 * MP ) - lastLowM;
	                    MS1 = ( 2 * MP ) - lastHighM;
	                    MR2 = MP + ( lastHighM - lastLowM );
	                    MS2 = MP - ( lastHighM - lastLowM );
	                    MR3 = ( 2 * MP ) + ( lastHighM - ( 2 * lastLowM ) );
	                    MS3 = ( 2 * MP ) - ( ( 2 * lastHighM ) - lastLowM );
	                                    
	                    lines.add(PhilLine.createLine(LineType.MP, MP));
	                    lines.add(PhilLine.createLine(LineType.MR1, MR1));
	                    lines.add(PhilLine.createLine(LineType.MS1, MS1));
	                    lines.add(PhilLine.createLine(LineType.MR2, MR2));
	                    lines.add(PhilLine.createLine(LineType.MS2, MS2));
	                    lines.add(PhilLine.createLine(LineType.MR3, MR3));
	                    lines.add(PhilLine.createLine(LineType.MS3, MS3));
					}
					
					FIBR1 = DO + ( range * 0.382 );
		            FIBS1 = DO - ( range * 0.382 );
		            FIBR2 = DO + ( range * 0.618 );
		            FIBS2 = DO - ( range * 0.618 );
		            FIBR3 = DO + ( range * 0.764 );
		            FIBS3 = DO - ( range * 0.764 );
		            FIBR4 = DO + ( range * 1.000 );
		            FIBS4 = DO - ( range * 1.000 );
		            FIBR5 = DO + ( range * 1.382 );
		            FIBS5 = DO - ( range * 1.382 );
		            lines.add(PhilLine.createLine(LineType.FIBR1, FIBR1));
		            lines.add(PhilLine.createLine(LineType.FIBR2, FIBR2));           
	                lines.add(PhilLine.createLine(LineType.FIBR3, FIBR3));
	                lines.add(PhilLine.createLine(LineType.FIBR4, FIBR4));
	                lines.add(PhilLine.createLine(LineType.FIBR5, FIBR5));
	                lines.add(PhilLine.createLine(LineType.FIBS1, FIBS1));
		            lines.add(PhilLine.createLine(LineType.FIBS2, FIBS2));           
	                lines.add(PhilLine.createLine(LineType.FIBS3, FIBS3));
	                lines.add(PhilLine.createLine(LineType.FIBS4, FIBS4));
	                lines.add(PhilLine.createLine(LineType.FIBS5, FIBS5));
					breachedLineValue=getLineValue(lines,breachedLine);
					/*System.out.println("Testing LINE FIBS1 range "+breachedLine
							+" "+PrintUtils.Print(breachedLineValue)
							+" "+PrintUtils.Print(FIBS1)
							+" range "+PrintUtils.Print(range)
							);
					*/
					DPBreached = false;
					beforeDay=actualDay;
				}
				
				if (breachedLineValue>DO){
					if (!DPBreached){				
						if (q.getHigh()>=(breachedLineValue+pipsL) && q.getHigh()<=(breachedLineValue+pipsH)){
							//System.out.println("1  superado en "+i
							//		+" "+PrintUtils.Print(q.getHigh())
							//		+" "+PrintUtils.Print(breachedLineValue)
							//	);
							int pipsDo = (int) ((breachedLineValue-DO)/0.0001);
							int pos = pipsDo/10;
							if (pos>lineToDoTotal.size()-1) pos = lineToDoTotal.size()-1;
							int acc = lineToDoTotal.get(pos);
							lineToDoTotal.set(pos, acc+1);
							
							DPBreached= true;
							totalBreached++;		
							upBreachPoints.add(i);
							actualMaxDesv = q.getHigh();
							firstBreachedValue = q.getHigh();
						}
					}else{//breached					
						if (q.getHigh()>actualMaxDesv) actualMaxDesv=q.getHigh();
						
						//if (q.getHigh()<breachedLineValue){
						if (q.getLow()<=DO){
							//System.out.println("DP anulado "+i+" "+PrintUtils.Print(q.getHigh()));
							double pipsPos = ((firstBreachedValue-DO)/0.0001);
							int pipsDo = (int) ((breachedLineValue-DO)/0.0001);
							double desv = (actualMaxDesv-breachedLineValue)/0.0001;
							int pos = pipsDo/10;
							if (pos>lineToDo.size()-1) pos = lineToDo.size()-1;
							int acc = lineToDo.get(pos);
							lineToDo.set(pos, acc+1);
							double pips = lineToDoDesv.get(pos);
							double pipsPosAcc = lineToDoPos.get(pos);
							lineToDoDesv.set(pos, pips+desv);
							lineToDoPos.set(pos, pipsPosAcc+pipsPos);
							DPBreached= false;
							totalClosed++;
							actualMaxDesv=-999;
							System.out.println("Total closed Pos DESV PIPPOS "+totalClosed
									+" "+ pos+" "+pipsDo
									+" "+PrintUtils.Print(breachedLineValue)
									+" "+PrintUtils.Print(DO)
									+" "+PrintUtils.Print(desv)+" "+PrintUtils.Print(pipsPos));
						}
					}
				}else if (breachedLineValue<DO){
						if (!DPBreached){				
							if (q.getLow()<=(breachedLineValue-pipsL) && q.getLow()>=(breachedLineValue-pipsH)){
								//System.out.println("2 DP superado "+i
								//		+" "+PrintUtils.Print(q.getLow())
								//		+" "+PrintUtils.Print(breachedLineValue)
								//		);
								
								DPBreached = true;
								totalBreached++;
								downBreachPoints.add(i);
							}
						}else{//breached						
							//if (q.getLow()>breachedLineValue){
							if (q.getHigh()>=DO){
								//System.out.println("DP anulado "+i+" "+PrintUtils.Print(q.getHigh()));	
								DPBreached= false;
								totalClosed++;
							}
						}
				}
			}
			System.out.println("Total breached and closed: "+totalBreached
					+" "+totalClosed
					+" "+PrintUtils.Print((totalClosed*100.0)/totalBreached)+"%"
					);
			studyBreachPoints(data,upBreachPoints,10,10,true);
			studyBreachPoints(data,null,10,10,true);
		}
	 
	 public static ArrayList<PhilDay> calculateLines(ArrayList<Quote> data,ArrayList<Quote> dailyData,
				ArrayList<Quote> weeklyData,ArrayList<Quote> monthlyData){
		 
		 ArrayList<PhilDay> philDays = new ArrayList<PhilDay>();
		 
		 Calendar qCal = Calendar.getInstance();
		 int beforeDay = -1;
		 //POINTS
		 double DO = -1;
		 double DP = -1;double DR1=-1;double DR2=-1;double DR3=-1;double DS1=-1;double DS2=-1;double DS3=-1;
		 double WP = -1;double WR1=-1;double WR2=-1;double WR3=-1;double WS1=-1;double WS2=-1;double WS3=-1;
		 double MP = -1;double MR1=-1;double MR2=-1;double MR3=-1;double MS1=-1;double MS2=-1;double MS3=-1;
		 double FIBR1=-1;double FIBS1=-1;double FIBR2=-1;double FIBS2=-1;double FIBR3=-1;double FIBS3=-1;
		 double FIBR4=-1;double FIBS4=-1;double FIBR5=-1;double FIBS5=-1;
		 double lastHighD = -1;double lastLowD = -1;double lastCloseD = -1;
		 double lastHighW = -1;double lastLowW = -1;double lastCloseW = -1;
		 double lastHighM = -1;double lastLowM = -1;double lastCloseM = -1;
		
		 int lastDay=-1;
		 int lastWeek=-1;
		 int lastMonth=-1;
		 for (int i=0;i<data.size();i++){
				Quote q = data.get(i);
				qCal.setTime(q.getDate());
				int actualDay   = qCal.get(Calendar.DAY_OF_YEAR);
				int actualMonth = qCal.get(Calendar.MONTH);
				int actualWeek  = qCal.get(Calendar.WEEK_OF_YEAR);
				int dayWeek = qCal.get(Calendar.DAY_OF_WEEK);
				
				if (dayWeek==Calendar.SATURDAY || dayWeek==Calendar.SUNDAY){
	            	continue;
	            }
				
				double range = -1;
				if (actualDay!=beforeDay){
					//int lastDay   =  DateUtils.findLastDateIndex(dailyData,q,1);
					//int lastWeek  =  DateUtils.findLastDateIndex(weeklyData,q,2);
					//int lastMonth =  DateUtils.findLastDateIndex(monthlyData,q,3);
					//double range = getAverageRange(dailyData,lastDay);
					ArrayList<PhilLine> lines = new ArrayList<PhilLine>(); 
					if (lastDay!=-1){
						lastHighD = dailyData.get(lastDay).getHigh();
	                    lastLowD = dailyData.get(lastDay).getLow();
	                    lastCloseD = dailyData.get(lastDay).getClose();
	                    DO = q.getOpen();
	                    range = getAverageRange(dailyData,lastDay);                                       					
						beforeDay = actualDay;					
						/*System.out.println("**NEW Day DO: "+DateUtils.datePrint(q.getDate())
								+" "+PrintUtils.Print(DO)
						);*/
						DP = ( lastHighD + lastLowD + lastCloseD ) / 3;
						DR1 = ( 2 * DP ) - lastLowD;
	                    DS1 = ( 2 * DP ) - lastHighD;
	                    DR2 = DP + ( lastHighD - lastLowD );
	                    DS2 = DP - ( lastHighD - lastLowD );
	                    DR3 = ( 2 * DP ) + ( lastHighD - ( 2 * lastLowD ) );
	                    DS3 = ( 2 * DP ) - ( ( 2 * lastHighD ) - lastLowD );
	                    lines.add(PhilLine.createLine(LineType.DO, DO));//0
	                    lines.add(PhilLine.createLine(LineType.DP, DP));//1
	                    lines.add(PhilLine.createLine(LineType.DR1, DR1));//2
	                    lines.add(PhilLine.createLine(LineType.DS1, DS1));//3
	                    lines.add(PhilLine.createLine(LineType.DR2, DR2));//4
	                    lines.add(PhilLine.createLine(LineType.DS2, DS2));//5
	                    lines.add(PhilLine.createLine(LineType.DR3, DR3));//6
	                    lines.add(PhilLine.createLine(LineType.DS3, DS3));//7
					}
					if (lastWeek!=-1){
						lastHighW = dailyData.get(lastWeek).getHigh();
	                    lastLowW = dailyData.get(lastWeek).getLow();
	                    lastCloseW = dailyData.get(lastWeek).getClose();
	                    WP = ( lastHighW + lastLowW + lastCloseW ) / 3;
	                    WR1 = ( 2 * WP ) - lastLowW;
	                    WS1 = ( 2 * WP ) - lastHighW;
	                    WR2 = WP + ( lastHighW - lastLowW );
	                    WS2 = WP - ( lastHighW - lastLowW );
	                    WR3 = ( 2 * WP ) + ( lastHighW - ( 2 * lastLowW ) );
	                    WS3 = ( 2 * WP ) - ( ( 2 * lastHighW ) - lastLowW );
	                   
	                    lines.add(PhilLine.createLine(LineType.WP, WP));//8
	                    lines.add(PhilLine.createLine(LineType.WR1, WR1));//9
	                    lines.add(PhilLine.createLine(LineType.WS1, WS1));//10
	                    lines.add(PhilLine.createLine(LineType.WR2, WR2));//11
	                    lines.add(PhilLine.createLine(LineType.WS2, WS2));//12
	                    lines.add(PhilLine.createLine(LineType.WR3, WR3));//13
	                    lines.add(PhilLine.createLine(LineType.WS3, WS3));//14
					}
					if (lastMonth!=-1){
						lastHighM = dailyData.get(lastMonth).getHigh();
	                    lastLowM = dailyData.get(lastMonth).getLow();
	                    lastCloseM = dailyData.get(lastMonth).getClose();
	                    MP = ( lastHighM + lastLowM + lastCloseM ) / 3;
	                    MR1 = ( 2 * MP ) - lastLowM;
	                    MS1 = ( 2 * MP ) - lastHighM;
	                    MR2 = MP + ( lastHighM - lastLowM );
	                    MS2 = MP - ( lastHighM - lastLowM );
	                    MR3 = ( 2 * MP ) + ( lastHighM - ( 2 * lastLowM ) );
	                    MS3 = ( 2 * MP ) - ( ( 2 * lastHighM ) - lastLowM );
	                                    
	                    lines.add(PhilLine.createLine(LineType.MP, MP));//15
	                    lines.add(PhilLine.createLine(LineType.MR1, MR1));//16
	                    lines.add(PhilLine.createLine(LineType.MS1, MS1));//17
	                    lines.add(PhilLine.createLine(LineType.MR2, MR2));//18
	                    lines.add(PhilLine.createLine(LineType.MS2, MS2));//19
	                    lines.add(PhilLine.createLine(LineType.MR3, MR3));//20
	                    lines.add(PhilLine.createLine(LineType.MS3, MS3));//21
					}
					
					FIBR1 = DO + ( range * 0.382 );
		            FIBS1 = DO - ( range * 0.382 );
		            FIBR2 = DO + ( range * 0.618 );
		            FIBS2 = DO - ( range * 0.618 );
		            FIBR3 = DO + ( range * 0.764 );
		            FIBS3 = DO - ( range * 0.764 );
		            FIBR4 = DO + ( range * 1.000 );
		            FIBS4 = DO - ( range * 1.000 );
		            FIBR5 = DO + ( range * 1.382 );
		            FIBS5 = DO - ( range * 1.382 );
		            lines.add(PhilLine.createLine(LineType.FIBR1, FIBR1));//22
		            lines.add(PhilLine.createLine(LineType.FIBR2, FIBR2));//23          
	                lines.add(PhilLine.createLine(LineType.FIBR3, FIBR3));//24
	                lines.add(PhilLine.createLine(LineType.FIBR4, FIBR4));//25
	                lines.add(PhilLine.createLine(LineType.FIBR5, FIBR5));//26
	                lines.add(PhilLine.createLine(LineType.FIBS1, FIBS1));//27
		            lines.add(PhilLine.createLine(LineType.FIBS2, FIBS2));//28          
	                lines.add(PhilLine.createLine(LineType.FIBS3, FIBS3));//29
	                lines.add(PhilLine.createLine(LineType.FIBS4, FIBS4));//30
	                lines.add(PhilLine.createLine(LineType.FIBS5, FIBS5));//31
					
					/*System.out.println("Testing LINE FIBS1 range "+breachedLine
							+" "+PrintUtils.Print(breachedLineValue)
							+" "+PrintUtils.Print(FIBS1)
							+" range "+PrintUtils.Print(range)
							);
					*/
	                
	                //add ney day
	                Calendar dayCal = Calendar.getInstance();
	                dayCal.setTime(q.getDate());
	                PhilDay pDay = new PhilDay();
	                pDay.setDay(dayCal);
	                pDay.setIndex(i);
	                pDay.setLines(lines);
	                philDays.add(pDay);
	                
					beforeDay=actualDay;
					lastDay++;
				}
		 }
		 
		 
		 return philDays;
	 }
	
	 public static void testLine2(ArrayList<Quote> data,ArrayList<Quote> dailyData,
				ArrayList<Quote> weeklyData,ArrayList<Quote> monthlyData,
				LineType breachedLine,LineType targetLine,double pipsL,double pipsH,int bars){
			
			Calendar qCal = Calendar.getInstance();
			int beforeDay = -1;
			//POINTS
			double DO = -1;
			double DP = -1;double DR1=-1;double DR2=-1;double DR3=-1;double DS1=-1;double DS2=-1;double DS3=-1;
			double WP = -1;double WR1=-1;double WR2=-1;double WR3=-1;double WS1=-1;double WS2=-1;double WS3=-1;
			double MP = -1;double MR1=-1;double MR2=-1;double MR3=-1;double MS1=-1;double MS2=-1;double MS3=-1;
			double FIBR1=-1;double FIBS1=-1;double FIBR2=-1;double FIBS2=-1;double FIBR3=-1;double FIBS3=-1;
			double FIBR4=-1;double FIBS4=-1;double FIBR5=-1;double FIBS5=-1;
			double lastHighD = -1;double lastLowD = -1;double lastCloseD = -1;
			double lastHighW = -1;double lastLowW = -1;double lastCloseW = -1;
			double lastHighM = -1;double lastLowM = -1;double lastCloseM = -1;
			boolean DPBreached = false;
			int totalBreached=0;
			int totalClosed=0;
			int totalDOClosed=0;
			double firstBreachedValue =-1;
			double breachedLineValue=-1;
			double range = -1;
			double actualMaxDesv=-999;
			ArrayList<PhilLine> lines = new ArrayList<PhilLine>(); 
			ArrayList<Integer> upBreachPoints = new ArrayList<Integer>();  
			ArrayList<Integer> downBreachPoints = new ArrayList<Integer>();
			ArrayList<Integer> lineToDoTotal = new ArrayList<Integer>();
			ArrayList<Integer> lineToDo = new ArrayList<Integer>();
			ArrayList<Double> lineToDoDesv = new ArrayList<Double>();
			ArrayList<Double> lineToDoPos = new ArrayList<Double>();
			for (int i=0;i<=6;i++){
				lineToDo.add(0);
				lineToDoTotal.add(0);
				lineToDoDesv.add(0.0);
				lineToDoPos.add(0.0);
			}
					
			for (int i=0;i<data.size();i++){
				Quote q = data.get(i);
				qCal.setTime(q.getDate());
				int actualDay = qCal.get(Calendar.DAY_OF_YEAR);
				int dayWeek = qCal.get(Calendar.DAY_OF_WEEK);
				
				if (dayWeek==Calendar.SATURDAY || dayWeek==Calendar.SUNDAY){
	            	continue;
	            }
				
				//if (dayWeek!=Calendar.MONDAY) continue;
				//if (dayWeek!=Calendar.TUESDAY) continue;
				//if (dayWeek!=Calendar.WEDNESDAY) continue;
				//if (dayWeek!=Calendar.THURSDAY) continue;
				//if (dayWeek!=Calendar.FRIDAY) continue;
				
				if (actualDay!=beforeDay){
					
					int lastDay   =  DateUtils.findLastDateIndex(dailyData,q,1);
					int lastWeek  =  DateUtils.findLastDateIndex(weeklyData,q,2);
					int lastMonth =  DateUtils.findLastDateIndex(monthlyData,q,3);
					//double range = getAverageRange(dailyData,lastDay);
					lines.clear();
					if (lastDay!=-1){
						lastHighD = dailyData.get(lastDay).getHigh();
	                    lastLowD = dailyData.get(lastDay).getLow();
	                    lastCloseD = dailyData.get(lastDay).getClose();
	                    DO = q.getOpen();
	                    range = getAverageRange(dailyData,lastDay);                                       					
						beforeDay = actualDay;					
						//System.out.println("**NEW Day DO: "+DateUtils.datePrint(q.getDate())
						//		+" "+PrintUtils.Print(DO)
						//);
						DP = ( lastHighD + lastLowD + lastCloseD ) / 3;
						DR1 = ( 2 * DP ) - lastLowD;
	                    DS1 = ( 2 * DP ) - lastHighD;
	                    DR2 = DP + ( lastHighD - lastLowD );
	                    DS2 = DP - ( lastHighD - lastLowD );
	                    DR3 = ( 2 * DP ) + ( lastHighD - ( 2 * lastLowD ) );
	                    DS3 = ( 2 * DP ) - ( ( 2 * lastHighD ) - lastLowD );
	                    lines.add(PhilLine.createLine(LineType.DO, DO));
	                    lines.add(PhilLine.createLine(LineType.DP, DP));
	                    lines.add(PhilLine.createLine(LineType.DR1, DR1));
	                    lines.add(PhilLine.createLine(LineType.DS1, DS1));
	                    lines.add(PhilLine.createLine(LineType.DR2, DR2));
	                    lines.add(PhilLine.createLine(LineType.DS2, DS2));
	                    lines.add(PhilLine.createLine(LineType.DR3, DR3));
	                    lines.add(PhilLine.createLine(LineType.DS3, DS3));
					}
					if (lastWeek!=-1){
						lastHighW = dailyData.get(lastWeek).getHigh();
	                    lastLowW = dailyData.get(lastWeek).getLow();
	                    lastCloseW = dailyData.get(lastWeek).getClose();
	                    WP = ( lastHighW + lastLowW + lastCloseW ) / 3;
	                    WR1 = ( 2 * WP ) - lastLowW;
	                    WS1 = ( 2 * WP ) - lastHighW;
	                    WR2 = WP + ( lastHighW - lastLowW );
	                    WS2 = WP - ( lastHighW - lastLowW );
	                    WR3 = ( 2 * WP ) + ( lastHighW - ( 2 * lastLowW ) );
	                    WS3 = ( 2 * WP ) - ( ( 2 * lastHighW ) - lastLowW );
	                   
	                    lines.add(PhilLine.createLine(LineType.WP, WP));
	                    lines.add(PhilLine.createLine(LineType.WR1, WR1));
	                    lines.add(PhilLine.createLine(LineType.WS1, WS1));
	                    lines.add(PhilLine.createLine(LineType.WR2, WR2));
	                    lines.add(PhilLine.createLine(LineType.WS2, WS2));
	                    lines.add(PhilLine.createLine(LineType.WR3, WR3));
	                    lines.add(PhilLine.createLine(LineType.WS3, WS3));
					}
					if (lastMonth!=-1){
						lastHighM = dailyData.get(lastMonth).getHigh();
	                    lastLowM = dailyData.get(lastMonth).getLow();
	                    lastCloseM = dailyData.get(lastMonth).getClose();
	                    MP = ( lastHighM + lastLowM + lastCloseM ) / 3;
	                    MR1 = ( 2 * MP ) - lastLowM;
	                    MS1 = ( 2 * MP ) - lastHighM;
	                    MR2 = MP + ( lastHighM - lastLowM );
	                    MS2 = MP - ( lastHighM - lastLowM );
	                    MR3 = ( 2 * MP ) + ( lastHighM - ( 2 * lastLowM ) );
	                    MS3 = ( 2 * MP ) - ( ( 2 * lastHighM ) - lastLowM );
	                                    
	                    lines.add(PhilLine.createLine(LineType.MP, MP));
	                    lines.add(PhilLine.createLine(LineType.MR1, MR1));
	                    lines.add(PhilLine.createLine(LineType.MS1, MS1));
	                    lines.add(PhilLine.createLine(LineType.MR2, MR2));
	                    lines.add(PhilLine.createLine(LineType.MS2, MS2));
	                    lines.add(PhilLine.createLine(LineType.MR3, MR3));
	                    lines.add(PhilLine.createLine(LineType.MS3, MS3));
					}
					
					FIBR1 = DO + ( range * 0.382 );
		            FIBS1 = DO - ( range * 0.382 );
		            FIBR2 = DO + ( range * 0.618 );
		            FIBS2 = DO - ( range * 0.618 );
		            FIBR3 = DO + ( range * 0.764 );
		            FIBS3 = DO - ( range * 0.764 );
		            FIBR4 = DO + ( range * 1.000 );
		            FIBS4 = DO - ( range * 1.000 );
		            FIBR5 = DO + ( range * 1.382 );
		            FIBS5 = DO - ( range * 1.382 );
		            lines.add(PhilLine.createLine(LineType.FIBR1, FIBR1));
		            lines.add(PhilLine.createLine(LineType.FIBR2, FIBR2));           
	                lines.add(PhilLine.createLine(LineType.FIBR3, FIBR3));
	                lines.add(PhilLine.createLine(LineType.FIBR4, FIBR4));
	                lines.add(PhilLine.createLine(LineType.FIBR5, FIBR5));
	                lines.add(PhilLine.createLine(LineType.FIBS1, FIBS1));
		            lines.add(PhilLine.createLine(LineType.FIBS2, FIBS2));           
	                lines.add(PhilLine.createLine(LineType.FIBS3, FIBS3));
	                lines.add(PhilLine.createLine(LineType.FIBS4, FIBS4));
	                lines.add(PhilLine.createLine(LineType.FIBS5, FIBS5));
					breachedLineValue=getLineValue(lines,breachedLine);
					/*System.out.println("Testing LINE FIBS1 range "+breachedLine
							+" "+PrintUtils.Print(breachedLineValue)
							+" "+PrintUtils.Print(FIBS1)
							+" range "+PrintUtils.Print(range)
							);
					*/
					DPBreached = false;
					beforeDay=actualDay;
				}
				
				if (breachedLineValue>DO){
					if (!DPBreached){				
						if (
								q.getHigh()>=(breachedLineValue+pipsL) 
								&& q.getHigh()<=(breachedLineValue+pipsH)
								//&& q.getOpen()<breachedLineValue 
								//&& q.getClose()<breachedLineValue 
								//&& breachedLineValue-q.getClose()>=0.0010 
								){
							//System.out.println("1  superado en "+i
							//		+" "+PrintUtils.Print(q.getHigh())
							//		+" "+PrintUtils.Print(breachedLineValue)
							//	);
							int pipsDo = (int) ((breachedLineValue-DO)/0.0001);
							int pos = pipsDo/10;
							if (pos>lineToDoTotal.size()-1) pos = lineToDoTotal.size()-1;
							int acc = lineToDoTotal.get(pos);
							lineToDoTotal.set(pos, acc+1);
							
							DPBreached= true;
							totalBreached++;		
							upBreachPoints.add(i);
							actualMaxDesv = q.getHigh();
							firstBreachedValue = q.getHigh();
						}
					}else{//breached					
						if (q.getHigh()>actualMaxDesv) actualMaxDesv=q.getHigh();
						
						//if (q.getHigh()<breachedLineValue){
						if (q.getLow()<=DO){
							//System.out.println("DP anulado "+i+" "+PrintUtils.Print(q.getHigh()));
							double pipsPos = ((firstBreachedValue-DO)/0.0001);
							int pipsDo = (int) ((breachedLineValue-DO)/0.0001);
							double desv = (actualMaxDesv-breachedLineValue)/0.0001;
							int pos = pipsDo/10;
							if (pos>lineToDo.size()-1) pos = lineToDo.size()-1;
							int acc = lineToDo.get(pos);
							lineToDo.set(pos, acc+1);
							double pips = lineToDoDesv.get(pos);
							double pipsPosAcc = lineToDoPos.get(pos);
							lineToDoDesv.set(pos, pips+desv);
							lineToDoPos.set(pos, pipsPosAcc+pipsPos);
							DPBreached= false;
							totalClosed++;
							actualMaxDesv=-999;
							System.out.println("Total closed Pos DESV PIPPOS "+totalClosed
									+" "+ pos+" "+pipsDo
									+" "+PrintUtils.Print(breachedLineValue)
									+" "+PrintUtils.Print(DO)
									+" "+PrintUtils.Print(desv)+" "+PrintUtils.Print(pipsPos));
						}
					}
				}else if (breachedLineValue<DO){
						if (!DPBreached){				
							if (q.getLow()<=(breachedLineValue-pipsL) && q.getLow()>=(breachedLineValue-pipsH)){
								//System.out.println("2 DP superado "+i
								//		+" "+PrintUtils.Print(q.getLow())
								//		+" "+PrintUtils.Print(breachedLineValue)
								//		);
								
								DPBreached = true;
								totalBreached++;
								downBreachPoints.add(i);
							}
						}else{//breached						
							//if (q.getLow()>breachedLineValue){
							if (q.getHigh()>=DO){
								//System.out.println("DP anulado "+i+" "+PrintUtils.Print(q.getHigh()));	
								DPBreached= false;
								totalClosed++;
							}
						}
				}
				
				
			}
			System.out.println("Total breached and closed: "+totalBreached
					+" "+totalClosed
					+" "+PrintUtils.Print((totalClosed*100.0)/totalBreached)+"%"
					);
			//studyBreachPoints(data,upBreachPoints,10,10,true);
			//studyBreachPoints(data,null,10,10,true);
			for (int i=0;i<lineToDoTotal.size();i++){
				int total = lineToDoTotal.get(i);
				int closed = lineToDo.get(i);
				double pipsDesv = lineToDoDesv.get(i);
				double pipsPos = lineToDoPos.get(i);
				if (total>0){
					System.out.println(i+" "+total
							+" "+PrintUtils.Print(closed*100.0/total)
							+" "+PrintUtils.Print(pipsDesv/closed)
							+" "+PrintUtils.Print(pipsPos/closed)
							);
				}
			}
		}
	 
	public static void testLine(ArrayList<Quote> data,ArrayList<Quote> dailyData,
			ArrayList<Quote> weeklyData,ArrayList<Quote> monthlyData,
			LineType breachedLine,LineType targetLine,double pipsL,double pipsH,int bars){
		
		Calendar qCal = Calendar.getInstance();
		int beforeDay = -1;
		//POINTS
		double DO = -1;
		double DP = -1;double DR1=-1;double DR2=-1;double DR3=-1;double DS1=-1;double DS2=-1;double DS3=-1;
		double WP = -1;double WR1=-1;double WR2=-1;double WR3=-1;double WS1=-1;double WS2=-1;double WS3=-1;
		double MP = -1;double MR1=-1;double MR2=-1;double MR3=-1;double MS1=-1;double MS2=-1;double MS3=-1;
		double FIBR1=-1;double FIBS1=-1;double FIBR2=-1;double FIBS2=-1;double FIBR3=-1;double FIBS3=-1;
		double FIBR4=-1;double FIBS4=-1;double FIBR5=-1;double FIBS5=-1;
		double lastHighD = -1;double lastLowD = -1;double lastCloseD = -1;
		double lastHighW = -1;double lastLowW = -1;double lastCloseW = -1;
		double lastHighM = -1;double lastLowM = -1;double lastCloseM = -1;
		boolean DPBreached = false;
		double DPBreachValue = -1;
		boolean newDay = false;
		int totalBreached=0;
		int totalClosed=0;
		double breachedLineValue=-1;
		double range = -1;
		ArrayList<PhilLine> lines = new ArrayList<PhilLine>(); 
		ArrayList<Integer> upBreachPoints = new ArrayList<Integer>();  
		ArrayList<Integer> downBreachPoints = new ArrayList<Integer>();
		for (int i=0;i<data.size();i++){
			Quote q = data.get(i);
			qCal.setTime(q.getDate());
			int actualDay = qCal.get(Calendar.DAY_OF_YEAR);
			int dayWeek = qCal.get(Calendar.DAY_OF_WEEK);
			
			if (dayWeek==Calendar.SATURDAY || dayWeek==Calendar.SUNDAY){
            	continue;
            }
			
			//if (dayWeek!=Calendar.MONDAY) continue;
			//if (dayWeek!=Calendar.TUESDAY) continue;
			//if (dayWeek!=Calendar.WEDNESDAY) continue;
			//if (dayWeek!=Calendar.THURSDAY) continue;
			//if (dayWeek!=Calendar.FRIDAY) continue;
			
			if (actualDay!=beforeDay){
				
				int lastDay   =  DateUtils.findLastDateIndex(dailyData,q,1);
				int lastWeek  =  DateUtils.findLastDateIndex(weeklyData,q,2);
				int lastMonth =  DateUtils.findLastDateIndex(monthlyData,q,3);
				//double range = getAverageRange(dailyData,lastDay);
				lines.clear();
				if (lastDay!=-1){
					lastHighD = dailyData.get(lastDay).getHigh();
                    lastLowD = dailyData.get(lastDay).getLow();
                    lastCloseD = dailyData.get(lastDay).getClose();
                    DO = q.getOpen();
                    range = getAverageRange(dailyData,lastDay);                                       					
					beforeDay = actualDay;					
					System.out.println("**NEW Day DO: "+DateUtils.datePrint(q.getDate())
							+" "+PrintUtils.Print(DO)
					);
					DP = ( lastHighD + lastLowD + lastCloseD ) / 3;
					DR1 = ( 2 * DP ) - lastLowD;
                    DS1 = ( 2 * DP ) - lastHighD;
                    DR2 = DP + ( lastHighD - lastLowD );
                    DS2 = DP - ( lastHighD - lastLowD );
                    DR3 = ( 2 * DP ) + ( lastHighD - ( 2 * lastLowD ) );
                    DS3 = ( 2 * DP ) - ( ( 2 * lastHighD ) - lastLowD );
                    lines.add(PhilLine.createLine(LineType.DO, DO));
                    lines.add(PhilLine.createLine(LineType.DP, DP));
                    lines.add(PhilLine.createLine(LineType.DR1, DR1));
                    lines.add(PhilLine.createLine(LineType.DS1, DS1));
                    lines.add(PhilLine.createLine(LineType.DR2, DR2));
                    lines.add(PhilLine.createLine(LineType.DS2, DS2));
                    lines.add(PhilLine.createLine(LineType.DR3, DR3));
                    lines.add(PhilLine.createLine(LineType.DS3, DS3));
				}
				if (lastWeek!=-1){
					lastHighW = dailyData.get(lastWeek).getHigh();
                    lastLowW = dailyData.get(lastWeek).getLow();
                    lastCloseW = dailyData.get(lastWeek).getClose();
                    WP = ( lastHighW + lastLowW + lastCloseW ) / 3;
                    WR1 = ( 2 * WP ) - lastLowW;
                    WS1 = ( 2 * WP ) - lastHighW;
                    WR2 = WP + ( lastHighW - lastLowW );
                    WS2 = WP - ( lastHighW - lastLowW );
                    WR3 = ( 2 * WP ) + ( lastHighW - ( 2 * lastLowW ) );
                    WS3 = ( 2 * WP ) - ( ( 2 * lastHighW ) - lastLowW );
                   
                    lines.add(PhilLine.createLine(LineType.WP, WP));
                    lines.add(PhilLine.createLine(LineType.WR1, WR1));
                    lines.add(PhilLine.createLine(LineType.WS1, WS1));
                    lines.add(PhilLine.createLine(LineType.WR2, WR2));
                    lines.add(PhilLine.createLine(LineType.WS2, WS2));
                    lines.add(PhilLine.createLine(LineType.WR3, WR3));
                    lines.add(PhilLine.createLine(LineType.WS3, WS3));
				}
				if (lastMonth!=-1){
					lastHighM = dailyData.get(lastMonth).getHigh();
                    lastLowM = dailyData.get(lastMonth).getLow();
                    lastCloseM = dailyData.get(lastMonth).getClose();
                    MP = ( lastHighM + lastLowM + lastCloseM ) / 3;
                    MR1 = ( 2 * MP ) - lastLowM;
                    MS1 = ( 2 * MP ) - lastHighM;
                    MR2 = MP + ( lastHighM - lastLowM );
                    MS2 = MP - ( lastHighM - lastLowM );
                    MR3 = ( 2 * MP ) + ( lastHighM - ( 2 * lastLowM ) );
                    MS3 = ( 2 * MP ) - ( ( 2 * lastHighM ) - lastLowM );
                                    
                    lines.add(PhilLine.createLine(LineType.MP, MP));
                    lines.add(PhilLine.createLine(LineType.MR1, MR1));
                    lines.add(PhilLine.createLine(LineType.MS1, MS1));
                    lines.add(PhilLine.createLine(LineType.MR2, MR2));
                    lines.add(PhilLine.createLine(LineType.MS2, MS2));
                    lines.add(PhilLine.createLine(LineType.MR3, MR3));
                    lines.add(PhilLine.createLine(LineType.MS3, MS3));
				}
				
				FIBR1 = DO + ( range * 0.382 );
	            FIBS1 = DO - ( range * 0.382 );
	            FIBR2 = DO + ( range * 0.618 );
	            FIBS2 = DO - ( range * 0.618 );
	            FIBR3 = DO + ( range * 0.764 );
	            FIBS3 = DO - ( range * 0.764 );
	            FIBR4 = DO + ( range * 1.000 );
	            FIBS4 = DO - ( range * 1.000 );
	            FIBR5 = DO + ( range * 1.382 );
	            FIBS5 = DO - ( range * 1.382 );
	            lines.add(PhilLine.createLine(LineType.FIBR1, FIBR1));
	            lines.add(PhilLine.createLine(LineType.FIBR2, FIBR2));           
                lines.add(PhilLine.createLine(LineType.FIBR3, FIBR3));
                lines.add(PhilLine.createLine(LineType.FIBR4, FIBR4));
                lines.add(PhilLine.createLine(LineType.FIBR5, FIBR5));
                lines.add(PhilLine.createLine(LineType.FIBS1, FIBS1));
	            lines.add(PhilLine.createLine(LineType.FIBS2, FIBS2));           
                lines.add(PhilLine.createLine(LineType.FIBS3, FIBS3));
                lines.add(PhilLine.createLine(LineType.FIBS4, FIBS4));
                lines.add(PhilLine.createLine(LineType.FIBS5, FIBS5));
				breachedLineValue=getLineValue(lines,breachedLine);
				System.out.println("Testing LINE FIBS1 range "+breachedLine
						+" "+PrintUtils.Print(breachedLineValue)
						+" "+PrintUtils.Print(FIBS1)
						+" range "+PrintUtils.Print(range)
						);
				DPBreached = false;
				beforeDay=actualDay;
				newDay =true;
			}
			
			newDay = true;
			if (breachedLineValue>DO  && newDay){
				if (!DPBreached){				
					if (q.getHigh()>=(breachedLineValue+pipsL) && q.getHigh()<=(breachedLineValue+pipsH)){
						//System.out.println("1  superado en "+i
						//		+" "+PrintUtils.Print(q.getHigh())
						//		+" "+PrintUtils.Print(breachedLineValue)
						//	);		
					
						DPBreached= true;
						DPBreachValue = q.getHigh();
						totalBreached++;		
						upBreachPoints.add(i);
					}
				}else{//breached					
					if (q.getHigh()<breachedLineValue){
						//System.out.println("DP anulado "+i+" "+PrintUtils.Print(q.getHigh()));
						newDay = false;
						DPBreached= false;
						totalClosed++;
					}
				}
			}else if (breachedLineValue<DO && newDay){
					if (!DPBreached){				
						if (q.getLow()<=(breachedLineValue-pipsL) && q.getLow()>=(breachedLineValue-pipsH)){
							//System.out.println("2 DP superado "+i
							//		+" "+PrintUtils.Print(q.getLow())
							//		+" "+PrintUtils.Print(breachedLineValue)
							//		);
							
							DPBreached = true;
							DPBreachValue = q.getLow();
							totalBreached++;
							downBreachPoints.add(i);
						}
					}else{//breached						
						if (q.getLow()>breachedLineValue){
							//System.out.println("DP anulado "+i+" "+PrintUtils.Print(q.getHigh()));	
							newDay = false;
							DPBreached= false;
							totalClosed++;
						}
					}
			}
			
			
		}
		System.out.println("Total breached and closed: "+totalBreached
				+" "+totalClosed
				+" "+PrintUtils.Print((totalClosed*100.0)/totalBreached)+"%"
				);
		studyBreachPoints(data,upBreachPoints,10,10,true);
		studyBreachPoints(data,null,10,10,true);
		//estudio up points
		/*double breachValue=-1;
		Quote hl = null;
		double posPips=0;
		double negPips=0;
		int totaln=0;
		int totalp=0;
		for (int i=1;i<data.size();i++){
			breachValue = data.get(i-1).getHigh();
			hl = calculateHL(data,i+1,i+bars);
			double pos = (breachValue-hl.getLow())/0.0001;
			double neg = (hl.getHigh()-breachValue)/0.0001;
			posPips+=(breachValue-hl.getLow())/0.0001;
			negPips+=(hl.getHigh()-breachValue)/0.0001;
			if (neg>=5.0) totaln++;
			if (pos>=5.0) totalp++;
		}
		System.out.println("total %>10 "+data.size()
				+" "+PrintUtils.Print(totalp*100.0/data.size())+"%"
				+" "+PrintUtils.Print(totaln*100.0/data.size())+"%"
				+" "+PrintUtils.Print(totalp*1.0/totaln)
				);
		posPips=0;
		negPips=0;
		totaln=0;
		totalp=0;
		for (int i=0;i<upBreachPoints.size();i++){
			int point = upBreachPoints.get(i);
			breachValue = data.get(point).getHigh();
			hl = calculateHL(data,point+1,point+bars);
			double pos = (breachValue-hl.getLow())/0.0001;
			double neg = (hl.getHigh()-breachValue)/0.0001;
			System.out.println("price breached +PIPS -PIPS: "
					+PrintUtils.Print(breachValue)
					+" "+PrintUtils.Print(hl.getHigh())
					+" "+PrintUtils.Print(hl.getLow())
					+" "+PrintUtils.Print(pos)
					+" "+PrintUtils.Print(neg)
					+" "+PrintUtils.Print(posPips/negPips)
			);
			posPips+=(breachValue-hl.getLow())/0.0001;
			negPips+=(hl.getHigh()-breachValue)/0.0001;
			if (neg>=5.0) totaln++;
			if (pos>=5.0) totalp++;
		}		
		if (upBreachPoints.size()>0)
			System.out.println(bars+" UP Positive Negative "+PrintUtils.Print(posPips/upBreachPoints.size())
					+" "+PrintUtils.Print(negPips/upBreachPoints.size())
					+" "+PrintUtils.Print(posPips/upBreachPoints.size())
					);
		System.out.println("total %>10 "+upBreachPoints.size()
				+" "+PrintUtils.Print(totalp*100.0/upBreachPoints.size())+"%"
				+" "+PrintUtils.Print(totaln*100.0/upBreachPoints.size())+"%"
				+" "+PrintUtils.Print(totalp*1.0/totaln)
				);
		posPips=0;
		negPips=0;
		for (int i=0;i<downBreachPoints.size();i++){
			int point = downBreachPoints.get(i);
			breachValue = data.get(point).getLow();
			hl = calculateHL(data,point+1,point+bars);
			//	System.out.println("price breached +positive -negative: "
			//	+PrintUtils.Print(breachValue)
			//	+" "+PrintUtils.Print(hl.getLow())
			//	+" "+PrintUtils.Print(hl.getHigh())
			//	);
			negPips+=(breachValue-hl.getLow())/0.0001;
			posPips+=(hl.getHigh()-breachValue)/0.0001;
		}
		if (downBreachPoints.size()>0)
			System.out.println("DOWN Positive Negative "+PrintUtils.Print(posPips/downBreachPoints.size())
				+" "+PrintUtils.Print(negPips/downBreachPoints.size())
				+" "+PrintUtils.Print(posPips/negPips)
				);*/
	}
	
	/**
	 * dire
	 * @param data
	 * @param points
	 * @param sl
	 * @param tp
	 * @param direction
	 */
	private static void studyBreachPoints(ArrayList<Quote> data,
			ArrayList<Integer> points, double sl, double tp,boolean up) {
		// TODO Auto-generated method stub
		double avgHC=0;
		int win=0;
		int loss=0;
		int point =-1;
		double value =-1;
		double close=-1;
		int size = data.size();
		if (points!=null){
			size = points.size();
		}
		for (int i=0;i<size;i++){
			point = i;
			value = data.get(i).getHigh();
			close = data.get(i).getClose();
			if (points!=null){
				point = points.get(i);
				value = data.get(point).getHigh();
				close = data.get(point).getClose();
			}
			double diff = (value-close)/0.0001; 
			if (diff<=20.0){
				avgHC+=diff;
				double slValue = value+0.0001*sl;
				double tpValue = value-0.0001*tp;
				//System.out.println("POINT SL TP : "+
				//	PrintUtils.Print(value)
				//	+" "+PrintUtils.Print(slValue)
				//	+" "+PrintUtils.Print(tpValue )
				//);
				for (int j=point+1;j<data.size();j++){
					Quote q = data.get(j);
					//System.out.println(PrintUtils.getOHLC(q));
					if (q.getHigh()>=slValue){
						loss++;
						break;
					}else if (q.getLow()<=tpValue){
						win++;
						break;
					}
				}
			}
		}
		System.out.println(sl+" "+tp+" win % "+(win+loss)+" "+PrintUtils.Print(win*100.0/(win+loss))+" "+PrintUtils.Print(avgHC/size));
	}

	private static Quote calculateHL(ArrayList<Quote> data, int begin, int end) {
		// TODO Auto-generated method stub
			Quote hl = new Quote();
			hl.setHigh(-999);
			hl.setLow(999);
			if (end>=data.size()-1) end=data.size()-1;
			for (int i=begin;i<=end;i++){
				Quote q = data.get(i);
				if (q.getHigh()>hl.getHigh()) hl.setHigh(q.getHigh());
				if (q.getLow()<hl.getLow()) hl.setLow(q.getLow());
			}
			return hl;
	}
		

	private static double getLineValue(ArrayList<PhilLine> lines,
			LineType lineType) {
		// TODO Auto-generated method stub
		
		for (int i=0;i<lines.size();i++){
			PhilLine line = lines.get(i);
			if (line.getLineType()==lineType)
				return line.getValue();
		}
		
		return -1;
	}

	public static ArrayList<Double> calculateRetracements(ArrayList<Quote> data, ArrayList<PhilDay> philDays,
			LineType startLine,LineType targetLine,double pipsL,double pipsH,int hl,int hh,
			int dayL,int dayH,
			int maxPips,int pipsOffset,int maxBars, int mode){
		
		ArrayList<Double> retracements = new ArrayList<Double>(); 
		
		int totalBreachings=0;
		double totalAvgPips=0;
		int losses=0;
		int wins=0;
		
		for (int i=0;i<philDays.size()-1;i++){//Para cada día
			PhilDay pDay = philDays.get(i);
			PhilDay nextDay = philDays.get(i+1);
			ArrayList<PhilLine> lines = pDay.getLines();
			int index0 = pDay.getIndex();
			int index1 = nextDay.getIndex()-1;
			double LINE=-1;
			double	TARGET = data.get(index0).getOpen();			

			for (int j=0;j<lines.size();j++){
				if (lines.get(j).getLineType()==startLine)
					LINE = lines.get(j).getValue();
				if (lines.get(j).getLineType()==targetLine){
					TARGET = lines.get(j).getValue();
				}
			}
			TARGET = TARGET-0.0001*pipsOffset;			
			boolean breached = false;
			double tradeRef=-1;
			
			Calendar cal = Calendar.getInstance();
			for (int j=index0;j<=index1;j++){				
				Quote q = data.get(j);
				cal.setTime(q.getDate());
				int h = cal.get(Calendar.HOUR_OF_DAY);
				int dayWeek=cal.get(Calendar.DAY_OF_WEEK);				
				if (TARGET<LINE && mode>=0){
					if (!breached 
							//&& q.getOpen()<LINE			
							//&& q.getClose()<LINE		
							&& q.getHigh()-LINE>=pipsL 
							&& q.getHigh()-LINE<=pipsH 
							//&& q.getHigh()-q.getClose()<=0.0005
							&& dayWeek>=dayL && dayWeek<=dayH
							&& h>=hl && h<=hh
							){												
						//System.out.println(DateUtils.datePrint(q.getDate())
						//		+" "+PrintUtils.Print(LINE)+" "+PrintUtils.Print(q.getHigh()));
						tradeRef = q.getHigh();
						//tradeRef = q.getClose();						
						if (tradeRef>=LINE+0.0008 && tradeRef<=LINE+0.0011){	
							TARGET = tradeRef-0.0001*pipsOffset;
							tradeRef=LINE+0.0008;
							Quote qBreach = calculateMaxRetrace(data,j+1,tradeRef,maxPips);
							double posPips = (tradeRef-qBreach.getLow())/0.0001;
							double negPips = (qBreach.getHigh()-tradeRef)/0.0001;
							totalBreachings++;
							if (negPips>posPips)
								losses++;
							else wins++;
							retracements.add(posPips);
							totalAvgPips+=posPips;
							breached = true;
						}
						//break;
					}//breached
					else if (q.getLow()<=TARGET){
						breached = false;
					}
				}//mode
			}//for day						
		}//for philDay
		System.out.println("total AvgPips: "
				+" "+totalBreachings
				+" "+PrintUtils.Print(totalAvgPips/totalBreachings)
				+" "+PrintUtils.Print(wins*100.0/totalBreachings));
		return retracements;
	}
	
	private static void studyPhilDays(ArrayList<Quote> data, ArrayList<PhilDay> philDays,
			LineType startLine,LineType targetLine,double pipsL,double pipsH,int hl,int hh,
			int dayL,int dayH,
			int maxPips,int pipsOffset,int maxBars, int mode) {
		// TODO Auto-generated method stub
		
		int totalBreached=0;
		int totalFibClosed=0;
		int totalFibFailed=0;
		double accMaxAdv=0;
		double accMaxPos=0;
		
		double accBars=0;
		double wins=0;
		double losses=0;
		double tradeRef=-1;
		double maxAdv=-9999;
		double maxPos=9999;	
		double pipPos=0;
		double lastPos=-9999;
		for (int i=0;i<philDays.size()-1;i++){//Para cada día
			PhilDay pDay = philDays.get(i);
			PhilDay nextDay = philDays.get(i+1);
			ArrayList<PhilLine> lines = pDay.getLines();
			int index0 = pDay.getIndex();
			int index1 = nextDay.getIndex()-1;
			double LINE=-1;
			double	TARGET = data.get(index0).getOpen();

			for (int j=0;j<lines.size();j++){
				if (lines.get(j).getLineType()==startLine)
					LINE = lines.get(j).getValue();
				if (lines.get(j).getLineType()==targetLine){
					TARGET = lines.get(j).getValue();
				}
			}
			TARGET = TARGET-0.0001*pipsOffset;			
			boolean breached = false;			
		
			lastPos-=9999;		
			tradeRef=LINE;
			int initialBar=-1; 
			maxAdv=-9999;
			maxPos=9999;
			Calendar cal = Calendar.getInstance();
			for (int j=index0;j<=index1;j++){
				
				Quote q = data.get(j);
				cal.setTime(q.getDate());
				int h = cal.get(Calendar.HOUR_OF_DAY);
				int dayWeek=cal.get(Calendar.DAY_OF_WEEK);
				
				if (TARGET<LINE && mode>=0){
					if (!breached 
							//&& q.getOpen()<LINE			
							//&& q.getClose()<LINE		
							&& q.getHigh()-LINE>=pipsL 
							&& q.getHigh()-LINE<=pipsH 
							//&& q.getHigh()-q.getClose()<=0.0005
							&& dayWeek>=dayL && dayWeek<=dayH
							&& h>=hl && h<=hh
							){
												
						//System.out.println(DateUtils.datePrint(q.getDate())
						//		+" "+PrintUtils.Print(LINE)+" "+PrintUtils.Print(q.getHigh()));
						tradeRef = q.getHigh();
						//tradeRef = q.getClose();
						//if (tradeRef==LINE+0.0001){
						if (tradeRef>=LINE+0.0005 && tradeRef<=LINE+0.0010){	
							TARGET = tradeRef-0.0001*pipsOffset;
							Quote qBreach = calculateBreachingResult(data,j+1,tradeRef,maxBars,TARGET,maxPips);
							double posPips = (tradeRef-qBreach.getLow())/0.0001;
							double negPips = (qBreach.getHigh()-tradeRef)/0.0001;
							accMaxAdv += negPips;
							accMaxPos += posPips;
							//if (posPips>negPips) wins++;
							if (qBreach.getClose()>0) wins++;
							if (qBreach.getClose()<0) losses++;
							totalBreached++;
							breached = true;
						}
						//break;
					}//breached
					else if (q.getLow()<=TARGET){
						breached = false;
					}
				}//mode
			}//for day
			
		}//main for
	
		double avgPos = accMaxPos/totalBreached;
		double avgNeg = accMaxAdv/totalBreached;
		double winPer = wins/totalBreached;
		double lossPer = losses/totalBreached;
		System.out.format("%5d %5d %5d %8.2f %8.2f %8.2f %8.2f %8.2f %8.2f %8.2f\n",hl,hh,
				totalBreached,
				wins*100.0/totalBreached,
				losses*100.0/totalBreached,
				accMaxPos/totalBreached,accMaxAdv/totalBreached,
				(accMaxPos-accMaxAdv)/totalBreached,
				avgPos*winPer-avgNeg*lossPer,
				winPer*pipsOffset-lossPer*maxPips
					);
		/*
			//%5s %5s %5s %5s %8s %8s %5s %8s
			System.out.format("%5d %5d %5d %4.2f %4.2f %4.2f %4.2f %8.2f %8.2f %8.2f %8.2f\n", hl, hh, 
					totalBreached,winPro*100.0,lossPro*100.0,win1,loss1,
					avgPips,avgAdvPips,(accPipPos-accMaxAdv)/totalBreached,accBars/totalFibClosed);
		*/
	}
	
	public static Quote calculateMaxRetrace(ArrayList<Quote> data, int begin,double refValue,int maxPipsNeg){
		Quote qRes = new Quote();
		qRes.setClose(0);
		int end= data.size()-1;	
		double maxNeg=-1;
		double maxPos=99999;
		double maxNegValue = refValue+maxPipsNeg*0.0001;		

		for (int i=begin;i<=end;i++){
			Quote q = data.get(i);
			if (q.getHigh()>maxNeg)
				maxNeg = q.getHigh();
			if (q.getLow()<maxPos)
				maxPos = q.getLow();
			qRes.setHigh(maxNeg);
			qRes.setLow(maxPos);
			if (maxNeg>maxNegValue){
				//System.out.println("[calculateMaxRetrace] refValue maxNegValue: "+PrintUtils.Print( refValue)+" "+PrintUtils.Print(maxNegValue)
				//		+" "+PrintUtils.Print((refValue-maxPos)/0.0001)+" "+PrintUtils.Print((refValue-maxNeg)/0.0001));
				qRes.setClose(-1);
				return qRes;
			}
			
		}
		//System.out.println("maxPips: "+maxPips);
		return qRes;
	}
	
	private static Quote calculateBreachingResult(ArrayList<Quote> data, int begin,double refValue,
			int maxBars, double target, int maxPipsNeg) {
		// TODO Auto-generated method stub
		Quote qRes = new Quote();
		qRes.setClose(0);
		int end= begin+maxBars-1;
		if (maxBars<0)
			end = data.size()-1;
		if (end>data.size()-1)
			end = data.size()-1;
		double maxNeg=-1;
		double maxPos=99999;
		double maxNegValue = refValue+maxPipsNeg*0.0001;
		double maxPosValue = target;
		//System.out.println("MAXNEG TARGET: "+PrintUtils.Print(maxNegValue)+" "+PrintUtils.Print(target));
		for (int i=begin;i<=end;i++){
			Quote q = data.get(i);
			if (q.getHigh()>maxNeg)
				maxNeg = q.getHigh();
			if (q.getLow()<maxPos)
				maxPos = q.getLow();
			qRes.setHigh(maxNeg);
			qRes.setLow(maxPos);
			if (maxNeg>maxNegValue){
				//System.out.println("LOSS: "+PrintUtils.Print(maxNeg));
				qRes.setClose(-1);
				return qRes;
			}
			if (maxPos<maxPosValue){
				qRes.setClose(1);
				//System.out.println("WIN: "+PrintUtils.Print(maxPos));
				return qRes;
			}
		}
		//System.out.println("maxPips: "+maxPips);
		return qRes;
	}

	
	private static ArrayList<Quote> cleanData(ArrayList<Quote> dataS) {
		// TODO Auto-generated method stub
		ArrayList<Quote> data = new ArrayList<Quote>();
		
		Calendar cal = Calendar.getInstance();
		for (int i=0;i<dataS.size();i++){
			Quote q = dataS.get(i);
			cal.setTime(q.getDate());
			if (cal.get(Calendar.DAY_OF_WEEK)==Calendar.SATURDAY || cal.get(Calendar.DAY_OF_WEEK)==Calendar.SUNDAY)
				continue;
			Quote qNew = new Quote();
			qNew.copy(q);
			data.add(q);
		}
		
		return data;
	}
	
	private static int countRanges(ArrayList<Double> ranges,double range){
		int count=0;
		for (int i=0;i<ranges.size();i++){
			if (ranges.get(i)<range){
				count++;
			}
		}
		return count;
	}
	
	private static ArrayList<Double> calculateRanges(ArrayList<Quote> data, 
							int dayL, int dayH,int hl, int hh){
			int result =0;
			Calendar cal = Calendar.getInstance();
			int lastDay=-1;
			double min=999;
			double max=-999;
			ArrayList<Double> ranges =  new ArrayList<Double> (); 
			for (int i=0;i<data.size();i++){
				Quote q = data.get(i);
				cal.setTime(q.getDate());
				int h = cal.get(Calendar.HOUR_OF_DAY);
				int dayWeek=cal.get(Calendar.DAY_OF_WEEK);
				int day = cal.get(Calendar.DAY_OF_YEAR);

				if (dayWeek>=dayL && dayWeek<=dayH){
					if (day!=lastDay){
						if (lastDay!=-1){
							double range = (max-min)/0.0001;
							ranges.add(range);
							//System.out.println("range: "+range);
						}
						max=-999;
						min=999;
						lastDay = day;
					}
					if (h>=hl && h<=hh){
						if (q.getHigh()>max){
							max = q.getHigh();
						}
						if (q.getLow()<min){
							min = q.getLow();
						}
					}
				}//if day				
			}//for
						
			return ranges;
		}
	
	private static void calculateHLTimes(ArrayList<Quote> data, int hl, int hh,
			int dayL, int dayH) {
		// TODO Auto-generated method stub
		ArrayList<Integer> hmins = new ArrayList<Integer>(); 
		ArrayList<Integer> hmaxs = new ArrayList<Integer>(); 
		for (int i=0;i<=23;i++){
			hmins.add(0);
			hmaxs.add(0);
		}
		Calendar cal = Calendar.getInstance();
		int hmax=-1;
		int hmin=-1;
		int lastDay=-1;
		double min=999;
		double max=-999;
		for (int i=0;i<data.size();i++){
			Quote q = data.get(i);
			cal.setTime(q.getDate());
			int h = cal.get(Calendar.HOUR_OF_DAY);
			int dayWeek=cal.get(Calendar.DAY_OF_WEEK);
			int day = cal.get(Calendar.DAY_OF_YEAR);
			
			if (dayWeek>=dayL && dayWeek<=dayH){
				if (day!=lastDay){
					if (lastDay!=-1){
						int accMin = hmins.get(hmin);
						int accMax = hmaxs.get(hmax);
						hmins.set(hmin, accMin+1);
						hmaxs.set(hmax, accMax+1);
					}
					max=-999;
					min=999;
					lastDay = day;
				}
				if (q.getHigh()>max){
					max = q.getHigh();
					hmax=h;
				}
				if (q.getLow()<min){
					min = q.getLow();
					hmin=h;
				}
			}
			
		}
		for (int i=0;i<=23;i++){
			System.out.println("Hora "+i+" "+hmaxs.get(i)+" "+hmins.get(i));
		}
	}

	public static int getTotalLess(ArrayList<Double> array,double umbral,boolean less){
		int total=0;
		for (int i=0;i<array.size();i++){
			if (less && array.get(i)<umbral){
				total++;
			}
			if (!less && array.get(i)>umbral){
				total++;
			}
		}
		
		return total;
	}
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		//String fileName ="resources/eurusd5b.csv";
		String fileName = "resources/EURUSD_5 Mins_Bid_2009.01.01_2013.05.31.csv";
				
		ArrayList<Quote> dataS = null;
		ArrayList<Quote> dataI = null;
		if (fileName.equals("resources/eurusd5b.csv")){		
			dataS = DAO.retrieveData(fileName, DataProvider.PEPPERSTONE_FOREX);
		}else{
			dataI = DAO.retrieveData(fileName, DataProvider.DUKASCOPY_FOREX);
			dataS  =  TestLines.calculateCalendarAdjusted(dataI);
		}
		
		ArrayList<Quote> data = cleanData(dataS);
		ArrayList<Quote> dailyData = ConvertLib.createDailyData(data);
		ArrayList<Quote> weeklyData = ConvertLib.createWeeklyData(data);
		ArrayList<Quote> monthlyData = ConvertLib.createMonthlyData(data);
		System.out.println("FILENAME: "+fileName);
		System.out.println("5min data: "+data.size());
		System.out.println("daily data: "+dailyData.size());
		System.out.println("weekly data: "+weeklyData.size());
		System.out.println("monthly data: "+monthlyData.size());
		
		/*ArrayList<Double> ranges1 = TestLines.calculateRanges(data, Calendar.MONDAY, Calendar.FRIDAY, 0, 7);
		ArrayList<Double> ranges2 = TestLines.calculateRanges(data, Calendar.MONDAY, Calendar.FRIDAY, 8, 15);
		ArrayList<Double> ranges3 = TestLines.calculateRanges(data, Calendar.MONDAY, Calendar.FRIDAY, 16, 23);
		for (int i=0;i<ranges2.size();i++){
			System.out.println(PrintUtils.Print(ranges2.get(i))+","+PrintUtils.Print(ranges3.get(i)));
		}*/
		/*ArrayList<Double> ranges = null;
		for (int i=Calendar.MONDAY;i<=Calendar.MONDAY;i++){
			for (int hl=0;hl<=15;hl++){
				int hh = hl+18;
				ranges = TestLines.calculateRanges(data, i, i+4, hl, hh);
				int value = TestLines.countRanges(ranges, 100);
				System.out.println("hl hh total %: "+hl+" "+hh+" "+value+" "+PrintUtils.Print(value*100.0/ranges.size())+"%");
			}
		}*/
		
		double pipsL=0.0001;
		double pipsH=0.0020;
		//TestLines.testLine2(data, dailyData, weeklyData, monthlyData, type, LineType.DO, pipsL, pipsH, 10);
		ArrayList<PhilDay> philDays = calculateLines(data, dailyData, weeklyData, monthlyData);
		System.out.println("days: "+philDays.size());
		
		/*for (int i=0;i<philDays.size()-1;i++){
			PhilDay day = philDays.get(i);
			PhilDay day1 = philDays.get(i+1);
			int indexI = day.getIndex();
			int indexF = day1.getIndex()-1;
			double max=-9999;
			double min=99999;
			//System.out.println(indexI+" "+indexF);
			for (int j=indexI;j<=indexF;j++){
				Quote q = data.get(j);
				//System.out.println(PrintUtils.Print(q));
				if (q.getLow()<min){
					min=q.getLow();
				}
				if (q.getHigh()>max){
					max =q.getHigh();
				}
				//System.out.println(max+" "+min);
			}
			System.out.println("Rango : "+PrintUtils.Print((max-min)/0.0001));
		}*/
		
		
		LineType source = LineType.FIBR1;
		LineType target = LineType.DO;
		pipsL = 0.0001;
		pipsH = 0.0050;
		int offset = 4;
		int cutoff = 10;
		
		/*System.out.println("Parameters-> "
				+source.name()+" to "+target.name()+"-"+offset+" "+PrintUtils.Print(pipsL/0.0001)+" "+PrintUtils.Print(pipsH/0.0001));
		System.out.format("%5s %5s %5s %5s %5s %5s %5s %5s %5s %5s %8s\n", 
				"hourL","hourH","total","win%","loss%","win1%","loss1%",
				"avgPips","avgAdvPips","PowF","avgBars");
		*/
		
		for (int day=Calendar.MONDAY;day<=Calendar.MONDAY;day++){
		    for (int hl=0;hl<=0;hl++){
		    	int hh = hl+23;
		    	System.out.println("HL HH: "+hl+" "+hh);
				ArrayList<Double> retracements =TestLines.calculateRetracements(data,philDays,source,target,pipsL,pipsH,hl,hh,day,day+4,
						cutoff,offset,10000,1);
				int total0 = getTotalLess(retracements,4,false);
				int total = getTotalLess(retracements,10,false);
				int total1 = getTotalLess(retracements,20,false);
				int total2 = getTotalLess(retracements,30,false);
				int total3 = getTotalLess(retracements,40,false);
				int total4 = getTotalLess(retracements,50,false);
				System.out.println("total less 5 10 20 30 40 50: "
						+" "+PrintUtils.Print(total0*100/retracements.size())+"%"
						+" "+PrintUtils.Print(total*100/retracements.size())+"%"
						+" "+PrintUtils.Print(total1*100/retracements.size())+"%"
						+" "+PrintUtils.Print(total2*100/retracements.size())+"%"
						+" "+PrintUtils.Print(total3*100/retracements.size())+"%"
						+" "+PrintUtils.Print(total4*100/retracements.size())+"%"
						);
			}
		}
		
		//for (pipsL=0.0001;pipsL<=0.0030;pipsL+=0.0001)
		/*for (int day=Calendar.MONDAY;day<=Calendar.MONDAY;day++)
		for (int h=0;h<=19;h+=1){
				//studyPhilDays(data,philDays,LineType.FIBR2,LineType.FIBR1,0.0001,0.0007,h,h+3,Calendar.MONDAY,Calendar.FRIDAY,500,false);
			studyPhilDays(data,philDays,source,target,pipsL,pipsH,h+0,h+4,day+0,day+4,
					cutoff,offset,10000,1);
		}*/
		
		/*for (int day=Calendar.MONDAY;day<=Calendar.MONDAY;day++)
		for (int h=0;h<=0;h++){
			calculateHLTimes(data,h,h+4,day+0,day+4);
		}*/
	}

	public static ArrayList<QuoteShort> calculateCalendarAdjustedShort(
			ArrayList<QuoteShort> data) {
		// TODO Auto-generated method stub
		ArrayList<QuoteShort> transformed = new ArrayList<QuoteShort>();
		 
        if (data==null) return null;
        Calendar cal = Calendar.getInstance();
        for (int i=0;i<data.size();i++){
        	QuoteShort q = data.get(i);
        	QuoteShort qNew = new QuoteShort();
        	qNew.copy(q);
        	QuoteShort.getCalendar(cal, q);
            int offset = DateUtils.calculatePepperGMTOffset(cal);

            cal.add(Calendar.HOUR_OF_DAY, offset);
            qNew.setCal(cal);
           
            transformed.add(qNew);
        }
        
        return transformed;
	}

	

	

	

	

}
