package drosa.DAO;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.sql.Connection;



import java.sql.ResultSet;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Enumeration;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;

import drosa.SQLConnectionUtils.SQLConnectionUtils;
import drosa.data.DataProvider;
import drosa.finances.COTReport;
import drosa.finances.Dividend;
import drosa.finances.ForexQuote;
import drosa.finances.FundamentalTrendPoint;
import drosa.finances.FutureCSIQuote;
import drosa.finances.FutureQuote;
import drosa.finances.IntradayQuote;
import drosa.finances.MonthPoint;
import drosa.finances.PeriodInformation;
import drosa.finances.Quote;
import drosa.finances.QuoteBidAsk;
import drosa.finances.QuoteShort;
import drosa.finances.SAPStock;
import drosa.finances.SplitData;
import drosa.finances.Stock;
import drosa.finances.SymbolSector;
import drosa.finances.Tick;
import drosa.finances.Trend;
import drosa.finances.TrendFilter;
import drosa.finances.TrendVolume;
import drosa.memory.Sizeof;
import drosa.phil.Currency;
import drosa.phil.NewsImpact;
import drosa.phil.NewsItem;
import drosa.phil.Range;
import drosa.phil.TmaDiff;
import drosa.phil.strategy.StrategyResult;
import drosa.phil.strategy.StrategyTrade;
import drosa.phil.strategy.TradeType;
import drosa.strategies.DIBSObject;
import drosa.strategies.PeakInterval;
import drosa.strategies.QuoteClassification;
import drosa.strategies.StrongMarkets;
import drosa.strategies.SymbolInfo;
import drosa.strategies.summary.StrategySummary;
import drosa.strategies.MarketStructure;
import drosa.utils.DateUtils;
import drosa.utils.FundamentalType;
import drosa.utils.PrintUtils;
import drosa.utils.QuoteDataType;
import drosa.utils.TimeFrame;
import drosa.utils.TradingUtils;
import drosa.utils.TrendType;

public class DAO {
		
		public static String getLastInsertDate(SQLConnectionUtils sqlHelper, String symbol,TimeFrame tf)
		{			
			String tableName = null;
			String qry = null;
			String dt="1900-01-01";
			
			switch(tf){
				case DAILY: 
					tableName = "dailyQuotes";
					break;
					
				case WEEKLY: 
					tableName  = "weeklyQuotes";
					break;
				
				case MONTHLY: 
					tableName  = "monthlyQuotes";
					break;
			}
			try{
				Connection conn = sqlHelper.getConnection();
				Statement stmt = conn.createStatement();	
				
															
				qry = "SELECT MAX(tradetime) as dt FROM "+tableName+
					" WHERE Symbol='"+symbol+"'";
								
				ResultSet rs = stmt.executeQuery(qry);
				
				while (rs.next()) {
					if (rs.getString("dt")!=null){
						dt = rs.getString("dt");
					}
				}
				rs.close();
				stmt.close();						
			}catch(Exception e){
				System.out.println("[error] isRecordInserted: "+e.getMessage());
				System.out.println("[error] query: "+qry);
				return dt;
			}
			//System.out.println("Last Insert: "+dt);
			return dt;						
		}
		
		public static String getLastInsertDate(SQLConnectionUtils sqlHelper,String tableName, String symbol)
		{			
			String qry =null;
			String dt="1900-01-01";
			try{
				Connection conn = sqlHelper.getConnection();
				Statement stmt = conn.createStatement();	
				
															
				qry = "SELECT MAX(tradetime) as dt FROM "+tableName+
					" WHERE Symbol='"+symbol+"'";
								
				ResultSet rs = stmt.executeQuery(qry);
				
				while (rs.next()) {
					if (rs.getString("dt")!=null){
						dt = rs.getString("dt");
					}
				}
				rs.close();
				stmt.close();						
			}catch(Exception e){
				System.out.println("[error] isRecordInserted: "+e.getMessage());
				System.out.println("[error] query: "+qry);
				return dt;
			}
			//System.out.println("Last Insert: "+dt);
			return dt;						
		}
		
		public static String getInsertDate(SQLConnectionUtils sqlHelper, String tableName,String type)
		{						
			String qry = null;
			String dt="1900-01-01";
			
			try{
				Connection conn = sqlHelper.getConnection();
				Statement stmt = conn.createStatement();	
				
															
				qry = "SELECT "+type+"(tradetime) as dt FROM `"+tableName+"`";					
				//System.out.println("[ getInsertDate] query: "+qry);				
				ResultSet rs = stmt.executeQuery(qry);
				
				while (rs.next()) {
					if (rs.getString("dt")!=null){
						dt = rs.getString("dt");
					}
				}
				rs.close();
				stmt.close();						
			}catch(Exception e){
				System.out.println("[error] getInsertDate: "+e.getMessage());
				System.out.println("[error] query: "+qry);
				return dt;
			}
			//System.out.println("Last Insert: "+dt);
			return dt;						
		}
			
		private static void insertQuote(SQLConnectionUtils sql, String symbol,Quote q,TimeFrame tf){
			
			String tableName = null;
			String qry = null;
			
			switch(tf){
				case DAILY: 
					tableName = "dailyQuotes";
					break;
					
				case WEEKLY: 
					tableName  = "weeklyQuotes";
					break;
				
				case MONTHLY: 
					tableName  = "monthlyQuotes";
					break;
			}
			try{
				Connection conn = sql.getConnection();
				Statement stmt = conn.createStatement();	
				
				//prepare date
				java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd");

				String currentDate = sdf.format(q.getDate());
				// Print the users table
				qry = "INSERT INTO "+tableName+"(tradetime,Symbol,`Open`,High,Low,`Close`,Volume,AdjClose)"+
						 " VALUES('"+
						 currentDate+
						 "','"+symbol+
						 "',"+q.getOpen()+
						 ","+q.getHigh()+						
						 ","+q.getLow()+
						 ","+q.getClose()+
						 ","+q.getVolume()+						
						 ","+q.getAdjClose()+")";
						
				stmt.executeUpdate(qry);
							
				//rs.close();
				stmt.close();							
			}catch(Exception e){
				System.out.println("[error] insertData: "+e.getMessage());
				System.out.println("query: "+qry);
				//e.printStackTrace();				
			}
		}
	
		
		private static void insertQuote(SQLConnectionUtils sql,String tableName, String symbol,Quote q,TimeFrame tf){
					
			String qry = null;
						
			try{
				Connection conn = sql.getConnection();
				Statement stmt = conn.createStatement();	
				
				//prepare date
				java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd");

				String currentDate = sdf.format(q.getDate());
				// Print the users table
				qry = "INSERT INTO "+tableName+"(tradetime,Symbol,`Open`,High,Low,`Close`,Volume,AdjClose)"+
						 " VALUES('"+
						 currentDate+
						 "','"+symbol+
						 "',"+q.getOpen()+
						 ","+q.getHigh()+						
						 ","+q.getLow()+
						 ","+q.getClose()+
						 ","+q.getVolume()+						
						 ","+q.getAdjClose()+")";
						
				stmt.executeUpdate(qry);
							
				//rs.close();
				stmt.close();							
			}catch(Exception e){
				System.out.println("[error] insertData: "+e.getMessage());
				System.out.println("query: "+qry);
				//e.printStackTrace();				
			}
		}
		
		public static void insertStockInfo(SQLConnectionUtils sql,String tableName, 
				String symbol,String symbolDownload,String description){
			
			String qry = null;
						
			try{
				Connection conn = sql.getConnection();
				Statement stmt = conn.createStatement();	
								
				// Print the users table
				qry = "INSERT INTO "+tableName+"(symbol,Symbol_download,description)"+
						 " VALUES('"+
						 symbol+
						 "','"+symbolDownload+
						 "','"+description+
						 "')";
						
				stmt.executeUpdate(qry);
							
				//rs.close();
				stmt.close();							
			}catch(Exception e){
				System.out.println("[error] insertData: "+e.getMessage());
				System.out.println("query: "+qry);
				//e.printStackTrace();				
			}
		}
	
		public static void insertStrategy(SQLConnectionUtils sql,
				String tableName, String symbol, String provider,
				int from, int to, boolean mm, double risk,double spread,int entryHour,
				int exitHour,int sp,int sl,
				double balanceI,
				StrategySummary strat){
			
			String qry = null;
			int intMM=0;
			if (mm)
				intMM=1;
						
			try{
				Connection conn = sql.getConnection();
				Statement stmt = conn.createStatement();	
				
				double winPer = strat.getTotalWins()*100.0/strat.getTotalTrades();
				double avgPips = (strat.getTotalwinPips()-strat.getTotallossPips())/strat.getTotalTrades();
				double pf = Math.abs(strat.getGrossProfit()/strat.getGrossLoss());
				// Print the users table
				qry = "INSERT INTO "+tableName+" (symbol,provider,`from`,`to`,mm,risk,spread," +
						"entryHour,exitHour,sp,sl,balanceI,balanceF,trades,winPer,avgPips,PF)"+
						 " VALUES('"+
						 symbol+
						 "','"+provider+
						 "',"+from+
						 ","+to+						
						 ","+intMM+
						 ","+risk+
						 ","+spread+
						 ","+entryHour+
						 ","+exitHour+
						 ","+sp+
						 ","+sl+
						 ","+PrintUtils.Print(balanceI)+						
						 ","+PrintUtils.Print(strat.getPatrimonio())+
						 ","+strat.getTotalTrades()+
						 ","+PrintUtils.Print(winPer)+
						 ","+PrintUtils.Print(avgPips)+
						 ","+PrintUtils.Print(pf)+
						 ")";
						
				stmt.executeUpdate(qry);
							
				//rs.close();
				stmt.close();							
			}catch(Exception e){
				System.out.println("[error] insertData: "+e.getMessage());
				System.out.println("query: "+qry);
				//e.printStackTrace();				
			}
		}

		public static void storeStock(SQLConnectionUtils sql,Stock s)
		{
			List<Quote> quotes = s.getQuotes();
			for (int i=0;i<s.getQuotes().size();i++){
				Quote q = quotes.get(i);
				insertQuote(sql,s.getSymbol(),q,s.getTf());				
			}			
		}
		
		public static List<Quote> retrieveQuotes(SQLConnectionUtils sqlHelper,String table,String symbol,GregorianCalendar gc1,GregorianCalendar gc2)
		{	
			
			List<Quote> quotes = new ArrayList<Quote>();						
			String tableName = table;
			String qry = null;			
			
			try{
				Connection conn = sqlHelper.getConnection();
				Statement stmt = conn.createStatement();	
				
				String whereClause = null;
				System.out.println("symbol: "+symbol);
				if (symbol.equalsIgnoreCase("ALL_SYMBOLS"))
					whereClause = "";
				else
					whereClause = " and Symbol='"+symbol+"'";
				
															
				qry = "SELECT * FROM "+tableName					
					+ " WHERE tradetime>='"+DateUtils.calendarToString(gc1, "yyyy-MM-dd") 
					+ "' and tradetime<='"+DateUtils.calendarToString(gc2, "yyyy-MM-dd")+"'"
					+ whereClause
					+ " ORDER BY tradetime asc";
				
				System.out.println("[retrieveQuotes] qry : "+qry);
				
				ResultSet rs = stmt.executeQuery(qry);
								
				while (rs.next()) {	
					Quote q = new Quote();
					q.setDate(rs.getDate("tradetime"));
					q.setSymbol(rs.getString("symbol"));
					q.setOpen(rs.getFloat("open"));
					q.setHigh(rs.getFloat("high"));
					q.setLow(rs.getFloat("low"));
					q.setClose(rs.getFloat("close"));
					q.setVolume(rs.getLong("volume"));	
					q.setAdjClose(rs.getFloat("adjClose"));
					quotes.add(q);									
				}
				rs.close();
				stmt.close();						
			}catch(Exception e){
				System.out.println("[error] retrieveQuotes: "+e.getMessage());
				System.out.println("[error] query: "+qry);
				e.printStackTrace();
				return quotes;
			}
			//System.out.println("Last Insert: "+volume);						
			return quotes;			
		}
		
		public static void updateSymbol(SQLConnectionUtils sqlHelper,String tableName,String symbol){
			String qry = null;			
			
			try{
				Connection conn = sqlHelper.getConnection();
				Statement stmt = sqlHelper.getStatement();	
				
				qry = "UPDATE`"+tableName					
					+ "` SET symbol='"+symbol+"'";
				
				//System.out.println("[retrieveQuotes] qry : "+qry);
				
				stmt.executeUpdate(qry);
			
				//stmt.close();						
			}catch(Exception e){
				System.out.println("[error] retrieveQuotes: "+e.getMessage());
				System.out.println("[error] query: "+qry);
				e.printStackTrace();
			}
		}
		
		public static void renameTable(SQLConnectionUtils sqlHelper,String oldName,String newName){
			String qry = null;			
			
			try{
				Connection conn = sqlHelper.getConnection();
				Statement stmt = sqlHelper.getStatement();	
				
				qry = " RENAME TABLE `"+oldName+"` TO `"+newName+"`";
				
				//System.out.println("[retrieveQuotes] qry : "+qry);
				
				stmt.executeUpdate(qry);
			
				//stmt.close();						
			}catch(Exception e){
				System.out.println("[error] retrieveQuotes: "+e.getMessage());
				System.out.println("[error] query: "+qry);
				e.printStackTrace();
			}
		}
		
		public static ArrayList<Quote> retrieveQuotes2(SQLConnectionUtils sqlHelper,
				String tableName,String symbol, GregorianCalendar gc1,GregorianCalendar gc2,boolean onlyClose)
		{	
			
			ArrayList<Quote> quotes = new ArrayList<Quote>();						
			String qry = null;			
			
			try{
				Connection conn = sqlHelper.getConnection();
				Statement stmt = sqlHelper.getStatement();	
				
				String whereClause = null;
				
				if (symbol.equalsIgnoreCase("ALL_SYMBOLS"))
					whereClause = "";
				else
					whereClause = " and Symbol='"+symbol+"'";
				
															
				qry = "SELECT * FROM `"+tableName					
					+ "` WHERE tradetime>='"+DateUtils.calendarToString(gc1, "yyyy-MM-dd") 
					+ "' and tradetime<='"+DateUtils.calendarToString(gc2, "yyyy-MM-dd")+" 23:59:59'"
					+ whereClause
					+ " ORDER BY tradetime asc";
				
				//System.out.println("[retrieveQuotes] qry : "+qry);				
				ResultSet rs = stmt.executeQuery(qry);							
				while (rs.next()) {	
					Quote q = new Quote();
					Date date = new Date();
				    java.sql.Timestamp mysqlTimestamp = rs.getTimestamp("tradetime");			
					date.setTime(mysqlTimestamp.getTime());					
					q.setDate(date);
					q.setSymbol(rs.getString("symbol"));
					q.setOpen(rs.getFloat("open"));
					q.setHigh(rs.getFloat("high"));
					q.setLow(rs.getFloat("low"));
					q.setClose(rs.getFloat("close"));
					q.setVolume(rs.getLong("volume"));
					if (!onlyClose)
						q.setAdjClose(rs.getFloat("AdjClose"));	
					
					quotes.add(q);									
				}
				rs.close();
				//stmt.close();						
			}catch(Exception e){
				System.out.println("[error] retrieveQuotes: "+e.getMessage());
				System.out.println("[error] query: "+qry);
				//e.printStackTrace();
				return quotes;
			}
			//System.out.println("Last Insert: "+volume);						
			return quotes;			
		}
		
		public static Stock retrieveStock(String Symbol,GregorianCalendar gc1,GregorianCalendar gc2,TimeFrame tf){
		
				Stock s = null;
				
				return s;
		}
		
		public static  IntradayQuote getLastIntradayQuote(SQLConnectionUtils sqlHelper,String symbol)
		{			
			String qry = null;						
			IntradayQuote q = new IntradayQuote();
			q.setSymbol(symbol);
			try{
				Connection conn = sqlHelper.getConnection();
				Statement stmt = conn.createStatement();	
				
															
				qry = "SELECT * FROM intradayquotes"+
					" WHERE Symbol='"+symbol+"'"+" ORDER BY tradetime desc";
								
				ResultSet rs = stmt.executeQuery(qry);
				
				
				while (rs.next()) {					
					q.setContracts(rs.getLong("contracts"));
					q.setDateTime(rs.getDate("tradetime"));
					q.setPrice(rs.getFloat("price"));
					q.setVolume(rs.getLong("volumeTotal"));
					//System.out.println("Quote: "+q.getSymbol()+","+q.getDateTime()+","
					//							+q.getPrice()+","+q.getVolume());
					break;					
				}
				rs.close();
				stmt.close();						
			}catch(Exception e){
				System.out.println("[error] isRecordInserted: "+e.getMessage());
				System.out.println("[error] query: "+qry);
				return q;
			}
			//System.out.println("Last Insert: "+volume);
			return q;					
		}
		
		public static  long getLastVolume(SQLConnectionUtils sqlHelper,String symbol)
		{			
			String qry = null;			
			Long volume=(long) 0;
			
			try{
				Connection conn = sqlHelper.getConnection();
				Statement stmt = conn.createStatement();	
																			
				qry = "SELECT volumeTotal as v FROM intradayquotes"+
					" WHERE Symbol='"+symbol+"'"+" ORDER BY tradetime desc";
								
				ResultSet rs = stmt.executeQuery(qry);
				
				while (rs.next()) {
					if (rs.getString("v")!=null){
						volume = rs.getLong("v");
						break;
					}
				}
				rs.close();
				stmt.close();						
			}catch(Exception e){
				System.out.println("[error] isRecordInserted: "+e.getMessage());
				System.out.println("[error] query: "+qry);
				return volume;
			}
			//System.out.println("Last Insert: "+volume);
			return volume;					
		}

		public static void storeIntradayQuote(SQLConnectionUtils sqlHelper,
				IntradayQuote s) {
			// TODO Auto-generated method stub			
			String qry = null;
			
			try{
				Connection conn = sqlHelper.getConnection();
				Statement stmt = conn.createStatement();	
								
				qry = "INSERT INTO intradayQuotes(Tradetime,Symbol,price,volumeTotal,contracts)"+
						 " VALUES('"+
						 DateUtils.now()+
						 "','"+s.getSymbol()+
						 "',"+s.getPrice()+
						 ","+s.getVolume()+
						 ","+s.getContracts()+")";
				//System.out.println("query: "+qry);		
				stmt.executeUpdate(qry);
							
				//rs.close();
				stmt.close();							
			}catch(Exception e){
				System.out.println("[error] storeIntradayQuote: "+e.getMessage());
				System.out.println("query: "+qry);
				//e.printStackTrace();				
			}
		}
		
		
		
		
		public static void insertQuote(SQLConnectionUtils sqlHelper,
				String table,String symbol, Quote q) {
			// TODO Auto-generated method stub

			String qry = null;
			
			try{
				Connection conn = sqlHelper.getConnection();
				Statement stmt = conn.createStatement();	
				
				//prepare date
				java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd");

				String currentDate = sdf.format(q.getDate());
				// Print the users table

				qry = "INSERT INTO "+table+"(Tradetime,Symbol,`Open`,High,Low,`Close`,Volume,AdjClose)"+
							 " VALUES('"+
							 currentDate+
							 "','"+symbol+
							 "',"+q.getOpen()+
							 ","+q.getHigh()+						
							 ","+q.getLow()+
							 ","+q.getClose()+
							 ","+q.getVolume()+						
							 ","+q.getAdjClose()+")";
				
				//System.out.println("Query: "+qry);
				stmt.executeUpdate(qry);
							
				//rs.close();
				stmt.close();							
			}catch(Exception e){
				System.out.println("[error] insertData: "+e.getMessage());
				System.out.println("query: "+qry);
				//e.printStackTrace();				
			}			
		}

		public static void storeQuote(SQLConnectionUtils sqlHelper,
				String symbol, Quote q, TimeFrame tf) {
			// TODO Auto-generated method stub
			
			String tableName = null;
			String qry = null;
			
			switch(tf){
				case DAILY: 
					tableName = "dailyQuotes";
					break;
					
				case WEEKLY: 
					tableName  = "weeklyQuotes";
					break;
				
				case MONTHLY: 
					tableName  = "monthlyQuotes";
					break;
				
				case YEARLY: 
					tableName  = "yearlyQuotes";
					break;
			}
			try{
				Connection conn = sqlHelper.getConnection();
				Statement stmt = conn.createStatement();	
				
				//prepare date
				java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd");

				String currentDate = sdf.format(q.getDate());
				// Print the users table
				if (tf==TimeFrame.YEARLY){
					qry = "INSERT INTO "+tableName+"(tradetime,Symbol,`Open`,High,Low,`Close`,Volume,AdjClose,minDay,minMonth,maxDay,maxMonth,barsBetween)"+
						 " VALUES('"+
						 currentDate+
						 "','"+symbol+
						 "',"+q.getOpen()+
						 ","+q.getHigh()+						
						 ","+q.getLow()+
						 ","+q.getClose()+
						 ","+q.getVolume()+						
						 ","+q.getAdjClose()+
						 ","+q.getMinDay()+
						 ","+q.getMinMonth()+
						 ","+q.getMaxDay()+
						 ","+q.getMaxMonth()+
						 ","+q.getBarsBetween()+")";
				}else{
					qry = "INSERT INTO "+tableName+"(tradetime,Symbol,`Open`,High,Low,`Close`,Volume,AdjClose,minDay,maxDay)"+
							 " VALUES('"+
							 currentDate+
							 "','"+symbol+
							 "',"+q.getOpen()+
							 ","+q.getHigh()+						
							 ","+q.getLow()+
							 ","+q.getClose()+
							 ","+q.getVolume()+						
							 ","+q.getAdjClose()+
							 ","+q.getMinDay()+
							 ","+q.getMaxDay()+")";
				}
				
				System.out.println("Query: "+qry);
				stmt.executeUpdate(qry);
							
				//rs.close();
				stmt.close();							
			}catch(Exception e){
				System.out.println("[error] insertData: "+e.getMessage());
				System.out.println("query: "+qry);
				//e.printStackTrace();				
			}			
		}

		public static int getBarsBetween(SQLConnectionUtils sqlHelper,String symbol, Calendar cal1,Calendar cal2, TimeFrame tf) {
			// TODO Auto-generated method stub
			
			String tableName = null;
			String qry = null;
			String dt="1900-01-01";
			int bars=0;
			
			switch(tf){
				case DAILY: 
					tableName = "dailyQuotes";
					break;
					
				case WEEKLY: 
					tableName  = "weeklyQuotes";
					break;
				
				case MONTHLY: 
					tableName  = "monthlyQuotes";
					break;
			}
			try{
				Connection conn = sqlHelper.getConnection();
				Statement stmt = conn.createStatement();
				
				String whereClause=null;
				
				if (symbol.equalsIgnoreCase("ALL_SYMBOLS"))
					whereClause = "";
				else
					whereClause = " and Symbol='"+symbol+"'";
				
				qry = "SELECT count(*) as bars FROM "+tableName
					  +" WHERE tradetime>='"+DateUtils.calendarToString(cal1, "yyyy-MM-dd") 
					  + "' and tradetime<='"+DateUtils.calendarToString(cal2, "yyyy-MM-dd")+"'"
					  + whereClause;
								
				ResultSet rs = stmt.executeQuery(qry);
				//System.out.println("query de bars : "+qry);
				while (rs.next()) {
					if (rs.getString("bars")!=null){
						bars = rs.getInt("bars");
					}
				}
				rs.close();
				stmt.close();						
			}catch(Exception e){
				System.out.println("[error]getBarsBetween: "+e.getMessage());
				System.out.println("[error] query: "+qry);
				return bars;
			}			
			return bars;						
		}

		public static float Round(float Rval, int Rpl) {
			  float p = (float)Math.pow(10,Rpl);
			  Rval = Rval * p;
			  float tmp = Math.round(Rval);
			  return (float)tmp/p;
		}
		
		public static void storeTrend(SQLConnectionUtils sqlHelper,Trend trend) {
			// TODO Auto-generated method stub
						
			String tableName = null;
			String qry = null;
			
			tableName = "trends";
			try{
				Connection conn = sqlHelper.getConnection();
				Statement stmt = conn.createStatement();	
				
				PeriodInformation pi= trend.getPeriod();
				//prepare date
				java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd");
				String highDate = sdf.format(pi.getDateHigh());
				String lowDate = sdf.format(pi.getDateLow());
				
				//type
				int type=0;
				
				float close=0;
				float open=0;
				String lastDate;
				if (pi.getTrendType()==TrendType.UP){
					type = 1;
					close = pi.getHigh();
					open  = pi.getLow();
					lastDate = highDate;
				}else{
					type = 0;
					open  = pi.getHigh();
					close = pi.getLow();
					lastDate = lowDate;
				}
				
				float percent = Math.abs(((close*100)/open)-100);
				float absdifference =  Round(Math.abs(pi.getHigh()-pi.getLow()),2);
				float speed = absdifference/pi.getTradingDaysBetween();
				float speedAbsolute = Math.abs(percent)/pi.getTradingDaysBetween();
				//System.out.println("Percent obtenido y posicion: "+percent+","+Math.abs(Round(percent,1)*10));
				
				// Print the users table
				qry = "INSERT INTO "+tableName+"(symbol,lastDate,highdate,high,lowdate," +
						"low,bars,absdifference,percentagevar,speed,spAb," +
						"spAbI3d,spAbI5d,spAbI10d," +
						"spAbI20d,spAbI50d,spAbI100d," +
						"spAbF3d,spAbF5d,spAbF10d," +
						"spAbF20d,spAbF50d,spAbF100d,`type`)"+
						 " VALUES('"+
						 trend.getSymbol()+
						 "','"+lastDate+
						 "','"+highDate+
						 "',"+pi.getHigh()+
						 ",'"+lowDate+						
						 "',"+pi.getLow()+
						 ","+pi.getTradingDaysBetween()+
						 ","+absdifference+
						 ","+Round(percent,2)+
						 ","+Round(speed,2)+
						 ","+Round(speedAbsolute,4)+
						 ","+Round(pi.getSpeedAbsoluteInit3d(),4)+
						 ","+Round(pi.getSpeedAbsoluteInit5d(),4)+
						 ","+Round(pi.getSpeedAbsoluteInit10d(),4)+
						 ","+Round(pi.getSpeedAbsoluteInit20d(),4)+
						 ","+Round(pi.getSpeedAbsoluteInit50d(),4)+
						 ","+Round(pi.getSpeedAbsoluteInit100d(),4)+
						 ","+Round(pi.getSpeedAbsoluteFinish3d(),4)+
						 ","+Round(pi.getSpeedAbsoluteFinish5d(),4)+
						 ","+Round(pi.getSpeedAbsoluteFinish10d(),4)+
						 ","+Round(pi.getSpeedAbsoluteFinish20d(),4)+
						 ","+Round(pi.getSpeedAbsoluteFinish50d(),4)+
						 ","+Round(pi.getSpeedAbsoluteFinish100d(),4)+						
						 ","+type+")";
						
				stmt.executeUpdate(qry);
							
				//rs.close();
				stmt.close();							
			}catch(Exception e){
				System.out.println("[error] insertData: "+e.getMessage());
				System.out.println("query: "+qry);
				//e.printStackTrace();				
			}			
		}

		public static List<Trend> getTrends(SQLConnectionUtils sql,
				String symbol, GregorianCalendar fromDate,
				GregorianCalendar toDate, TimeFrame tf,int bars) {
			
			List<Trend> trends = new ArrayList<Trend>();						
			String tableName = "trends";
			String qry = null;			
			
			/*switch(tf){
				case DAILY: 
					tableName = "dailyQuotes";
					break;
					
				case WEEKLY: 
					tableName  = "weeklyQuotes";
					break;
				
				case MONTHLY: 
					tableName  = "monthlyQuotes";
					break;
				
				case YEARLY: 
					tableName  = "yearlyQuotes";
					break;
			}*/
			try{
				Connection conn = sql.getConnection();
				Statement stmt = conn.createStatement();	
				
				String whereClause = null;
				
				if (symbol.equalsIgnoreCase("ALL_SYMBOLS"))
					whereClause = "";
				else
					whereClause = " and Symbol='"+symbol+"'";
				
				if (bars>0)				
					whereClause += " and bars>="+bars;
				
															
				qry = "SELECT * FROM "+tableName					
					+ " WHERE lastDate>='"+DateUtils.calendarToString(fromDate, "yyyy-MM-dd") 
					+ "' and lastDate<='"+DateUtils.calendarToString(toDate, "yyyy-MM-dd")+"'"
					+ whereClause
					+ " ORDER BY lastdate asc";
								
				ResultSet rs = stmt.executeQuery(qry);
				
				System.out.println("query; "+qry);
				int numTrends = 0;
				while (rs.next()) {	
					System.out.println("Num trends: "+numTrends++);
					PeriodInformation pi = new PeriodInformation();
					pi.setAbsDifference(rs.getFloat("absdifference"));
					pi.setSpeed((rs.getFloat("speed")));
					pi.setPercentageVar((rs.getFloat("percentageVar")));
					pi.setLow(rs.getFloat("low"));
					pi.setHigh(rs.getFloat("high"));
					pi.setDateLow(rs.getDate("lowdate"));
					pi.setDateHigh(rs.getDate("highdate"));
					pi.setTrendType(TrendType.values()[rs.getInt("type")]);
					pi.setTradingDaysBetween(rs.getInt("bars"));
					pi.setSpeedAbsolute((rs.getFloat("speedAbsolute")));
					
					if (pi.getTrendType()==TrendType.DOWN){
						pi.setFromDate(pi.getDateHigh());
						pi.setToDate(pi.getDateLow());
					}else{
						pi.setFromDate(pi.getDateLow());
						pi.setToDate(pi.getDateHigh());
					}
					
					Trend t = new Trend();
					t.setSymbol(rs.getString("symbol"));
					t.setPeriod(pi);
					
					trends.add(t);									
				}
				rs.close();
				stmt.close();						
			}catch(Exception e){
				System.out.println("[error] getTrends: "+e.getMessage());
				System.out.println("[error] query: "+qry);
				e.printStackTrace();
				return trends;
			}
			//System.out.println("Last Insert: "+volume);						
			return trends;			
			
		}
		
		public static void storeTrendVolume(SQLConnectionUtils sqlHelper,TrendVolume trendVolume) {
			// TODO Auto-generated method stub
			
			
			String tableName = null;
			String qry = null;
			
			tableName = "trendVolume";
			try{
				//System.out.println("hstaaqui1");
				Connection conn = sqlHelper.getConnection();
				//System.out.println("hstaaqui2");
				Statement stmt = conn.createStatement();
				
				PeriodInformation pi= trendVolume.getPeriod();

				//prepare date
				java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd");
				String highDate = sdf.format(pi.getDateHigh());
				String lowDate = sdf.format(pi.getDateLow());

			
				String lastDate=null;
				int type=0;
				if (pi.getTrendType()==TrendType.UP){					
					lastDate = highDate;
					type=1;
				}else{					
					lastDate = lowDate;
					type=0;
				}
				
				qry = "INSERT INTO "+tableName+"(symbol,lastdate,highdate,lowdate,volumeTotal,volumeAverage," +
						"via3M,via2M,via1M,viM,vip1M,vip2M,vfM,vfa1M,vfa2M,vfa3M,type"+
						 ") VALUES('"+
						 trendVolume.getSymbol()+	
						 "','"+lastDate+
						 "','"+highDate+						 
						 "','"+lowDate+						
						 "',"+trendVolume.getVolumeTotal()+
						 ","+trendVolume.getVolumeAverage()+
						 ","+trendVolume.getVia3M()+
						 ","+trendVolume.getVia2M()+
						 ","+trendVolume.getVia1M()+
						 ","+trendVolume.getViM()+
						 ","+trendVolume.getVip1M()+
						 ","+trendVolume.getVip2M()+
						 ","+trendVolume.getVfM()+
						 ","+trendVolume.getVfa1M()+
						 ","+trendVolume.getVfa2M()+
						 ","+trendVolume.getVfa3M()+
						 ","+type+")";												 
				
				
				System.out.println("[storeTrendVolume] qry");
				stmt.executeUpdate(qry);
							
				//rs.close();
				stmt.close();							
			}catch(Exception e){
				System.out.println("[error] storeTrendVolume: "+e.getMessage());
				System.out.println("query: "+qry);
				//e.printStackTrace();				
			}			
		}

		public static Trend getTrend(SQLConnectionUtils sql,
				String symbol, GregorianCalendar fromDate,
				GregorianCalendar toDate, TimeFrame tf) {
			
			Trend trend = null;						
			String tableName = "trends";
			String qry = null;			
			
			try{
				Connection conn = sql.getConnection();
				Statement stmt = conn.createStatement();	
				
				String whereClause = null;
				
				if (symbol.equalsIgnoreCase("ALL_SYMBOLS"))
					whereClause = "";
				else
					whereClause = " and Symbol='"+symbol+"'";
				
															
				qry = "SELECT * FROM "+tableName					
					+ " WHERE lastDate>='"+DateUtils.calendarToString(fromDate, "yyyy-MM-dd") 
					+ "' and lastDate<='"+DateUtils.calendarToString(toDate, "yyyy-MM-dd")+"'"
					+ whereClause
					+ " ORDER BY lastdate asc";
								
				ResultSet rs = stmt.executeQuery(qry);
				
				//System.out.println("query; "+qry);
				while (rs.next()) {	
					PeriodInformation pi = new PeriodInformation();
					pi.setAbsDifference(rs.getFloat("absdifference"));
					pi.setSpeed((rs.getFloat("speed")));
					pi.setPercentageVar((rs.getFloat("percentageVar")));
					pi.setLow(rs.getFloat("low"));
					pi.setHigh(rs.getFloat("high"));
					pi.setDateLow(rs.getDate("lowdate"));
					pi.setDateHigh(rs.getDate("highdate"));
					pi.setTrendType(TrendType.values()[rs.getInt("type")]);
					pi.setTradingDaysBetween(rs.getInt("bars"));
					
					Trend t = new Trend();
					t.setSymbol(symbol);
					t.setPeriod(pi);																		
				}
				rs.close();
				stmt.close();						
			}catch(Exception e){
				System.out.println("[error] getTrends: "+e.getMessage());
				System.out.println("[error] query: "+qry);
				e.printStackTrace();
				return trend;
			}
			//System.out.println("Last Insert: "+volume);						
			return trend;			
			
		}

		public static List<Trend> getTrendsStartFinish(SQLConnectionUtils sql,
				String symbol, GregorianCalendar fromDate,
				GregorianCalendar toDate, int bars, TimeFrame tf) {
			
			List<Trend> trends = new ArrayList<Trend>();						
			String tableName = "trends";
			String qry = null;			
			
			try{
				Connection conn = sql.getConnection();
				Statement stmt = conn.createStatement();	
				
				String whereClause = null;
				
				if (symbol.equalsIgnoreCase("ALL_SYMBOLS"))
					whereClause = "";
				else
					whereClause = " and Symbol='"+symbol+"'";
				
															
				qry = "SELECT * FROM "+tableName					
					+ " WHERE (highDate>='"+DateUtils.calendarToString(fromDate, "yyyy-MM-dd") 
					+ "' and highDate<='"+DateUtils.calendarToString(toDate, "yyyy-MM-dd")+"')"
					+ " or (lowDate>='"+DateUtils.calendarToString(fromDate, "yyyy-MM-dd")
					+ "' and lowDate<='"+DateUtils.calendarToString(toDate, "yyyy-MM-dd")+"')"
					+" and bars>="+bars
					+ whereClause
					+ " ORDER BY lastdate asc";
								
				ResultSet rs = stmt.executeQuery(qry);
				
				System.out.println("query; "+qry);
				while (rs.next()) {	
					PeriodInformation pi = new PeriodInformation();
					pi.setAbsDifference(rs.getFloat("absdifference"));
					pi.setSpeed((rs.getFloat("speed")));
					pi.setPercentageVar((rs.getFloat("percentageVar")));
					pi.setLow(rs.getFloat("low"));
					pi.setHigh(rs.getFloat("high"));
					pi.setDateLow(rs.getDate("lowdate"));
					pi.setDateHigh(rs.getDate("highdate"));
					pi.setTrendType(TrendType.values()[rs.getInt("type")]);
					pi.setTradingDaysBetween(rs.getInt("bars"));
					
					Trend t = new Trend();
					t.setSymbol(rs.getString("symbol"));
					t.setPeriod(pi);
					
					trends.add(t);									
				}
				rs.close();
				stmt.close();						
			}catch(Exception e){
				System.out.println("[error] getTrends: "+e.getMessage());
				System.out.println("[error] query: "+qry);
				e.printStackTrace();
				return trends;
			}
			//System.out.println("Last Insert: "+volume);						
			return trends;			
			
		}

		public static Quote getLastCloseQuote(SQLConnectionUtils sql,String symbol,
				GregorianCalendar date, TimeFrame tf) {
			// TODO Auto-generated method stub
			Quote q = null;
			
			String tableName = null;
			String qry = null;			
			
			switch(tf){
				case DAILY: 
					tableName = "dailyQuotes";
					break;
					
				case WEEKLY: 
					tableName  = "weeklyQuotes";
					break;
				
				case MONTHLY: 
					tableName  = "monthlyQuotes";
					break;
				
				case YEARLY: 
					tableName  = "yearlyQuotes";
					break;
			}
			try{
				Connection conn = sql.getConnection();
				Statement stmt = conn.createStatement();	
				
				String whereClause = null;
				
				if (symbol.equalsIgnoreCase("ALL_SYMBOLS"))
					whereClause = "";
				else
					whereClause = " and Symbol='"+symbol+"'";
				
															
				qry = "SELECT * FROM "+tableName					
					+ " WHERE tradetime<='"+DateUtils.calendarToString(date, "yyyy-MM-dd")+"'" 					
					+ whereClause
					+ " ORDER BY tradetime desc";
				
				//System.out.println("[retrieveQuotes] qry : "+qry);
				
				ResultSet rs = stmt.executeQuery(qry);
								
				while (rs.next()) {	
					q = new Quote();
					q.setDate(rs.getDate("tradetime"));
					q.setSymbol(rs.getString("symbol"));
					q.setOpen(rs.getFloat("open"));
					q.setHigh(rs.getFloat("high"));
					q.setLow(rs.getFloat("low"));
					q.setClose(rs.getFloat("close"));
					q.setVolume(rs.getLong("volume"));	
					if (tf == TimeFrame.YEARLY){
						q.setMaxMonth(rs.getInt("maxMonth"));
						q.setMaxDay(rs.getInt("maxDay"));
						q.setMinMonth(rs.getInt("minMonth"));
						q.setMinDay(rs.getInt("minDay"));
					}	
					break;
				}
				rs.close();
				stmt.close();						
			}catch(Exception e){
				System.out.println("[error] retrieveQuotes: "+e.getMessage());
				System.out.println("[error] query: "+qry);
				e.printStackTrace();
				return q;
			}
			//System.out.println("Last Insert: "+volume);						
			return q;			
		}
		
		public static int getTotalLowHigh(SQLConnectionUtils sql,String symbol,int bars,
				int value, TimeFrame tf,int type) {
			
			int total=0;
			String qry = null;			
			String whereClauseFinal=null;
			String typeStr=null;
			
			if (type==0)
				typeStr="lowDate";
			else
				typeStr="highDate";
			
			String valueStr=null;
			if (value<10) valueStr='0'+String.valueOf(value);
			else valueStr =String.valueOf(value);
			switch(tf){
				case DAILY: 
					
					whereClauseFinal="WHERE "+typeStr+" like "+"'"+"%-%-"+valueStr+"'";
					break;
					
				case WEEKLY: 
					//tableName  = "weeklyQuotes";
					break;
				
				case MONTHLY: 
					whereClauseFinal="WHERE "+typeStr+" like "+"'"+"%-"+valueStr+"-%"+"'";
					break;
				
				case YEARLY: 
					//tableName  = "yearlyQuotes";
					break;
			}
			try{
				Connection conn = sql.getConnection();
				Statement stmt = conn.createStatement();	
				
				String whereClause = null;
				
				
				if (symbol.equalsIgnoreCase("ALL_SYMBOLS"))
					whereClause = "";
				else
					whereClause = " and Symbol='"+symbol+"'";
				
				if (bars>0)				
					whereClause += " and bars>="+bars;
				
				if (type==0)
					whereClause += " and type=0";
				else
					whereClause += " and type=1";
				
															
				qry = "SELECT count(*) as result FROM trends "									
					+ whereClauseFinal
					+ whereClause;
				
				//System.out.println("[getTotalLowHigh] qry : "+qry);
				
				ResultSet rs = stmt.executeQuery(qry);
								
				while (rs.next()) {	
					total = rs.getInt("result");
				}
				rs.close();
				stmt.close();						
			}catch(Exception e){
				System.out.println("[error] getTotalLowHigh: "+e.getMessage());
				System.out.println("[error] query: "+qry);
				e.printStackTrace();
				return total;
			}
			//System.out.println("Last Insert: "+volume);						
			return total;			
		}
		
		public static List<Trend> getTrends(SQLConnectionUtils sql,
				String symbol,TrendType tType, int barsLow, int barsHigh) {
			
			List<Trend> trends = new ArrayList<Trend>();									
			String qry = null;			
			
			try{
				Connection conn = sql.getConnection();
				Statement stmt = conn.createStatement();	
				
				String whereClause = null;
				
				int type=0;
				if (tType==TrendType.UP){
					type=1;
				}
				
				if (!symbol.equalsIgnoreCase("ALL_SYMBOLS")){
					whereClause = " WHERE symbol='"+symbol+"' and type= "+type+
								" and bars>="+barsLow+" and bars<="+barsHigh;
				}else{
					whereClause = " WHERE type= "+type+
								"  and bars>="+barsLow+" and bars<="+barsHigh;
				}
																							
				qry = "SELECT * FROM trends "					
					+whereClause+" order by bars desc";					
				
				//System.out.println("[getTrend]query; "+qry);
				ResultSet rs = stmt.executeQuery(qry);
								
				int numTrends = 0;
				while (rs.next()) {	
					numTrends++;
					PeriodInformation pi = new PeriodInformation();
					pi.setAbsDifference(rs.getFloat("absdifference"));
					pi.setSpeed((rs.getFloat("speed")));
					pi.setPercentageVar((rs.getFloat("percentageVar")));
					pi.setLow(rs.getFloat("low"));
					pi.setHigh(rs.getFloat("high"));
					pi.setDateLow(rs.getDate("lowdate"));
					pi.setDateHigh(rs.getDate("highdate"));
					pi.setTrendType(TrendType.values()[rs.getInt("type")]);
					pi.setTradingDaysBetween(rs.getInt("bars"));
					pi.setSpeedAbsolute((rs.getFloat("spAb")));
					
					if (pi.getTrendType()==TrendType.DOWN){
						pi.setFirstDate(pi.getDateHigh());
						pi.setLastDate(pi.getDateLow());
						pi.setFromDate(pi.getDateHigh());
						pi.setToDate(pi.getDateLow());
					}else{
						pi.setFirstDate(pi.getDateLow());
						pi.setLastDate(pi.getDateHigh());
						pi.setFromDate(pi.getDateLow());
						pi.setToDate(pi.getDateHigh());
					}
					
					Trend t = new Trend();
					t.setSymbol(rs.getString("symbol"));
					t.setPeriod(pi);
					
					trends.add(t);									
				}
				rs.close();
				stmt.close();						
			}catch(Exception e){
				System.out.println("[error] getTrends: "+e.getMessage());
				System.out.println("[error] query: "+qry);
				e.printStackTrace();
				return trends;
			}
			//System.out.println("Last Insert: "+volume);						
			return trends;			
		}

		public static List<Trend> getTrend(SQLConnectionUtils sql,
				String symbol, TrendFilter t1, TrendFilter t2) {
			
			List<Trend> trends = new ArrayList<Trend>();									
			String qry = null;			
			
			try{
				Connection conn = sql.getConnection();
				Statement stmt = conn.createStatement();	
				
				String whereClause = null;
				
				whereClause = "WHERE t1.symbol=t2.symbol ";
				
				if (!symbol.equalsIgnoreCase("ALL_SYMBOLS"))				
					whereClause += " and t1.Symbol='"+symbol+"'";
				
				whereClause += " and t1.bars>="+t1.getLowBar()+
				" and t1.bars<="+t1.getHighBar()+
				" and t2.bars>="+t2.getLowBar()+
				" and t2.bars<="+t2.getHighBar();
											
				if ((t1.getType()==TrendType.DOWN) &&
					(t2.getType()==TrendType.UP)){
					whereClause +=" and t1.type=0 and t2.type=1 "+
								  " and t1.lowdate=t2.lowdate";
				}else if ((t1.getType()==TrendType.UP) &&
					(t2.getType()==TrendType.DOWN)){
					whereClause +=" and t1.type=1 and t2.type=0 "+
					  " and t1.highdate=t2.highdate";						
				}
																			
				qry = "SELECT * FROM trends t1,trends t2 "					
					+whereClause+" order by t1.bars";					
				
				System.out.println("[getTrend]query; "+qry);
				ResultSet rs = stmt.executeQuery(qry);
				
				
				int numTrends = 0;
				while (rs.next()) {	
					numTrends++;
					PeriodInformation pi = new PeriodInformation();
					pi.setAbsDifference(rs.getFloat("absdifference"));
					pi.setSpeed((rs.getFloat("speed")));
					pi.setPercentageVar((rs.getFloat("percentageVar")));
					pi.setLow(rs.getFloat("low"));
					pi.setHigh(rs.getFloat("high"));
					pi.setDateLow(rs.getDate("lowdate"));
					pi.setDateHigh(rs.getDate("highdate"));
					pi.setTrendType(TrendType.values()[rs.getInt("type")]);
					pi.setTradingDaysBetween(rs.getInt("bars"));
					pi.setSpeedAbsolute((rs.getFloat("speedAbsolute")));
					
					if (pi.getTrendType()==TrendType.DOWN){
						pi.setFromDate(pi.getDateHigh());
						pi.setToDate(pi.getDateLow());
					}else{
						pi.setFromDate(pi.getDateLow());
						pi.setToDate(pi.getDateHigh());
					}
					
					Trend t = new Trend();
					t.setSymbol(rs.getString("symbol"));
					t.setPeriod(pi);
					
					trends.add(t);									
				}
				rs.close();
				stmt.close();						
			}catch(Exception e){
				System.out.println("[error] getTrends: "+e.getMessage());
				System.out.println("[error] query: "+qry);
				e.printStackTrace();
				return trends;
			}
			//System.out.println("Last Insert: "+volume);						
			return trends;			
		}

		public static void storeMonthPoint(SQLConnectionUtils sqlHelper,MonthPoint mp) {
			// TODO Auto-generated method stub
						
			String tableName = null;
			String qry = null;
			
			tableName = "monthlypoints";
			try{
				Connection conn = sqlHelper.getConnection();
				Statement stmt = conn.createStatement();	
				
				//type
				int t1_type=0;
				int t2_type=0;
				
				TrendFilter t1 = mp.getT1();
				TrendFilter t2 = mp.getT2();
								
				if (t1.getType()==TrendType.UP){
					t1_type = 1;					
				}else{
					t1_type= 0;
				}
				
				if (t2.getType()==TrendType.UP){
					t2_type = 1;					
				}else{
					t2_type= 0;
				}
				
				int t1_low = t1.getLowBar();
				int t1_high =t1.getHighBar();
				int t2_low =t2.getLowBar();
				int t2_high =t2.getHighBar();
				
				long total= mp.getTotal();
				int month = mp.getMonth();
				String symbol = mp.getSymbol();
								
				
				// Print the users table
				qry = "INSERT INTO "+tableName+"(mes,symbol,t1_type,t2_type,t1_low,t1_high,t2_low,t2_high,total)"+
						 " VALUES("+
						 month+
						 ",'"+symbol+
						 "',"+t1_type+
						 ","+t2_type+
						 ","+t1_low+						
						 ","+t1_high+
						 ","+t2_low+
						 ","+t2_high+						 
						 ","+total+")";
						
				stmt.executeUpdate(qry);
							
				//rs.close();
				stmt.close();							
			}catch(Exception e){
				System.out.println("[error] storeMonthPoint: "+e.getMessage());
				System.out.println("query: "+qry);
				//e.printStackTrace();				
			}			
		}

		public static int getMaxBarsTrends(SQLConnectionUtils sql,String symbol,TrendFilter t1,TrendFilter t2) {
			// TODO Auto-generated method stub
																	
			String qry = null;
			int max=0;
			
			try{
				Connection conn = sql.getConnection();
				Statement stmt = conn.createStatement();	
				
				String whereClause = null;
				
				whereClause = "WHERE t1.symbol=t2.symbol ";
				
				if (!symbol.equalsIgnoreCase("ALL_SYMBOLS"))				
					whereClause += " and t1.Symbol='"+symbol+"'";
				
				whereClause += " and t1.bars>="+t1.getLowBar()+
				" and t1.bars<="+t1.getHighBar();				
											
				if ((t1.getType()==TrendType.DOWN) &&
					(t2.getType()==TrendType.UP)){
					whereClause +=" and t1.type=0 and t2.type=1 "+
								  " and t1.lowdate=t2.lowdate";
				}else if ((t1.getType()==TrendType.UP) &&
					(t2.getType()==TrendType.DOWN)){
					whereClause +=" and t1.type=1 and t2.type=0 "+
					  " and t1.highdate=t2.highdate";						
				}
																			
				qry = "SELECT max(t2.bars) as max FROM trends t1,trends t2 "					
					+whereClause;					
				
				System.out.println("[getTrend]query; "+qry);
				ResultSet rs = stmt.executeQuery(qry);
				
							
				while (rs.next()) {	
					max = rs.getInt("max");											
				}
				rs.close();
				stmt.close();						
			}catch(Exception e){
				System.out.println("[error] getTrends: "+e.getMessage());
				System.out.println("[error] query: "+qry);
				e.printStackTrace();
				return max;
			}
			//System.out.println("Last Insert: "+volume);						
			return max;			
		}

		public static List<Trend> getTrends(SQLConnectionUtils sql,
				String symbol, TrendFilter t1, TrendFilter t2) {
			
			String qry = null;
			List<Trend> trends = new ArrayList<Trend>();
			
			try{
				Connection conn = sql.getConnection();
				Statement stmt = conn.createStatement();	
				
				String whereClause = null;
				
				whereClause = "WHERE t1.symbol=t2.symbol ";
				
				if (!symbol.equalsIgnoreCase("ALL_SYMBOLS"))				
					whereClause += " and t1.Symbol='"+symbol+"'";
				
				whereClause += " and t1.bars>="+t1.getLowBar()+
				" and t1.bars<="+t1.getHighBar();				
											
				if ((t1.getType()==TrendType.DOWN) &&
					(t2.getType()==TrendType.UP)){
					whereClause +=" and t1.type=0 and t2.type=1 "+
								  " and t1.lowdate=t2.lowdate";
				}else if ((t1.getType()==TrendType.UP) &&
					(t2.getType()==TrendType.DOWN)){
					whereClause +=" and t1.type=1 and t2.type=0 "+
					  " and t1.highdate=t2.highdate";						
				}
																			
				qry = "SELECT t1.lowdate as lowdate,t1.low as low,t1.highdate,t1.high as high,t1.bars,t2.bars as t2bars FROM trends t1,trends t2 "					
					+whereClause;					
				
				System.out.println("[getTrend]query; "+qry);
				ResultSet rs = stmt.executeQuery(qry);
							
				while (rs.next()) {	
					PeriodInformation pi = new PeriodInformation();
					pi.setTradingDaysBetween(rs.getInt("t2bars"));
					if (t1.getType()==TrendType.DOWN){
						pi.setDateLow(rs.getDate("lowdate"));
						pi.setLow(rs.getFloat("low"));
					}else{
						pi.setDateHigh(rs.getDate("highdate"));
						pi.setHigh(rs.getFloat("high"));
					}
					
					Trend t = new Trend();
					t.setSymbol(symbol);
					t.setPeriod(pi);	
					
					trends.add(t);
					//System.out.println("aÃ±adiendi a trebds: "+rs.getInt("t2bars"));
				}
				rs.close();
				stmt.close();						
			}catch(Exception e){
				System.out.println("[error] getTrends: "+e.getMessage());
				System.out.println("[error] query: "+qry);
				e.printStackTrace();
				return trends;
			}
			//System.out.println("Last Insert: "+volume);						
			return trends;			
		}

		private static void addTrend(List<Trend> trends, Trend t) {
			// TODO Auto-generated method stub
			for (int i=0;i<trends.size();i++){
				Trend ti = trends.get(i);
			
				if (t.getPeriod().getTradingDaysBetween()<ti.getPeriod().getTradingDaysBetween()){
					trends.add(i, t);
					return;
				}else if (t.getPeriod().getTradingDaysBetween()==ti.getPeriod().getTradingDaysBetween()){
					return;
				}
			}
			
			trends.add(t);
		}

		public static void StoreFundamentalPoint(SQLConnectionUtils sqlHelper,String symbol, FundamentalTrendPoint last,
				FundamentalTrendPoint actual, boolean modeclose) {
			// TODO Auto-generated method stub
			String tableName = null;
			String qry = null;
			
			tableName = "fundamentalpoints";
			try{
				Connection conn = sqlHelper.getConnection();
				Statement stmt = conn.createStatement();	
								
				//prepare date
				java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd");
				String firstDate = sdf.format(last.getDate());
				String lastDate = sdf.format(actual.getDate());
				
				Calendar from = Calendar.getInstance();
				Calendar to = Calendar.getInstance();
				from.setTime(last.getDate());
				to.setTime(actual.getDate());
							
				//type
				int type=-1;
				int bars=getBarsBetween(sqlHelper,symbol, from, to, TimeFrame.DAILY);
								
				if (actual.getType()==FundamentalType.HIGH){
					type = 1;					
				}else{
					if (actual.getType()==FundamentalType.LOW)
						type = 0;					
				}
				
				//modeclose
				int closePrices = 0;
				if (modeclose){
					closePrices = 1;					
				}
				
				float percent = Math.abs(((actual.getValue()*100)/last.getValue())-100);				
				float speedAbsolute = Math.abs(percent)/bars;
				//System.out.println("Percent obtenido y posicion: "+percent+","+Math.abs(Round(percent,1)*10));
				
				// Print the users table
				qry = "INSERT INTO "+tableName+"(symbol,firstdate,lastDate,`value`,bars,var," +
						"abSpeed,pointType,modeClose)"+
						 " VALUES('"+
						 symbol+
						 "','"+firstDate+
						 "','"+lastDate+
						 "',"+actual.getValue()+
						 ","+bars+												 
						 ","+Round(percent,2)+	
						 ","+Round(speedAbsolute,4)+	
						 ","+type+
						 ","+closePrices+")";
						
				stmt.executeUpdate(qry);
							
				//rs.close();
				stmt.close();							
			}catch(Exception e){
				System.out.println("[error] insertData: "+e.getMessage());
				System.out.println("query: "+qry);
				//e.printStackTrace();				
			}			
		}
		
		public static void StoreCOTReport(SQLConnectionUtils sqlHelper,COTReport cot) {
			// TODO Auto-generated method stub
			String tableName = null;
			String qry = null;
			
			tableName = "cotreports";
			try{
				Connection conn = sqlHelper.getConnection();
				Statement stmt = conn.createStatement();	
								
				//prepare date
				java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd");
				String date = sdf.format(cot.getDate());
				
				
				
				qry = "INSERT INTO "+tableName+
						"(market,date,open_interest," +
						"noncommercial_long,noncommercial_short,noncommercial_spread,"+
						"commercial_long,commercial_short,"+
						"totalreportable_long,totalreportable_short,"+
						"nonreportable_long,nonreportable_short)"+
						 " VALUES('"+
						 cot.getMarket()+"','"+date+"',"+cot.getOpenInterest()+
						 ","+cot.getNonCommercialLong()+","+cot.getNonCommercialShort()+","+cot.getNonCommercialSpread()+
						 ","+cot.getCommercialLong()+","+cot.getCommercialShort()+
						 ","+cot.getTotalReportableLong()+","+cot.getTotalReportableShort()+
						 ","+cot.getNonReportableLong()+","+cot.getNonReportableShort()+")";
						 
						 
						 												
				stmt.executeUpdate(qry);
							
				//rs.close();
				stmt.close();							
			}catch(Exception e){
				System.out.println("[error] insertData: "+e.getMessage());
				System.out.println("query: "+qry);
				//e.printStackTrace();				
			}			
		}
		
		public static void createForexTable(SQLConnectionUtils sqlHelper,String tableName) {
			String qry = null;
			
			try{
				Connection conn = sqlHelper.getConnection();
				Statement stmt = conn.createStatement();	
				
				qry = "CREATE TABLE "+tableName+"("
						  +"`tradetime` datetime NOT NULL,"
						  +"`timeframe` int(11) DEFAULT NULL,"
						  +"`symbol` varchar(255) DEFAULT NULL,"
						  +"`open` float DEFAULT NULL,"
						  +"`high` float DEFAULT NULL,"
						  +"`low` float DEFAULT NULL,"
						  +"`close` float DEFAULT NULL,"
						  +"`volume` bigint(20) DEFAULT NULL,"
						  +"PRIMARY KEY (`tradetime`)"
						  +") ENGINE=InnoDB";
						 						 						 												
				stmt.executeUpdate(qry);
							
				//rs.close();
				stmt.close();							
			}catch(Exception e){
				System.out.println("[error] createForexTable: "+e.getMessage());
				System.out.println("query: "+qry);
				//e.printStackTrace();				
			}
		
		}
		
		public static void storeForexData(SQLConnectionUtils sqlHelper,
				String symbol, ForexQuote forexQuote, String tableName) {
			// TODO Auto-generated method stub		
			String qry = null;
						
			try{
				Connection conn = sqlHelper.getConnection();
				Statement stmt = conn.createStatement();	
								
				//prepare date
				java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm");
				String date = sdf.format(forexQuote.getDateTime());
								
				qry = "INSERT INTO "+tableName+
						"(tradetime,timeframe,symbol,`open`,high,low,`close`,volume)"+
						 " VALUES('"+
						 date+
						 "',1,"+"'"+symbol+"'"+
						 ","+forexQuote.getOpen()+
						 ","+forexQuote.getHigh()+
						 ","+forexQuote.getLow()+
						 ","+forexQuote.getClose()+
						 ","+forexQuote.getVolume()						
						 +")";
						 
						 
						 												
				stmt.executeUpdate(qry);
							
				//rs.close();
				stmt.close();							
			}catch(Exception e){
				System.out.println("[error] StoreForexData: "+e.getMessage());
				System.out.println("query: "+qry);
				//e.printStackTrace();				
			}
		}

		public static List<ForexQuote> getForexData(
				SQLConnectionUtils sqlHelper, String tableName, GregorianCalendar fromDate, GregorianCalendar toDate) {
			// TODO Auto-generated method stub
			List<ForexQuote> data = new ArrayList<ForexQuote>();									
			String qry = null;			
			
			try{
				Connection conn = sqlHelper.getConnection();
				Statement stmt = conn.createStatement();							
																			
				qry = "SELECT * FROM "+tableName
					+" WHERE (tradetime>='"+DateUtils.calendarToString(fromDate, "yyyy-MM-dd") 
					+ "' and tradetime<'"+DateUtils.calendarToString(toDate, "yyyy-MM-dd")+"')"
					+ " ORDER BY tradetime asc";
								
				ResultSet rs = stmt.executeQuery(qry);
				
				//System.out.println("query: "+qry);	
				int i=1;
				while (rs.next()) {	
					//System.out.println("quote: "+i++);
					ForexQuote fq = new ForexQuote();					
					fq.setSymbol(rs.getString("symbol"));
					fq.setDateTime(rs.getDate("tradetime"));
					fq.setOpen(rs.getFloat("open"));
					fq.setHigh(rs.getFloat("high"));
					fq.setLow(rs.getFloat("low"));
					fq.setClose(rs.getFloat("close"));
					fq.setVolume(rs.getLong("volume"));
										
					data.add(fq);						
				}
				rs.close();
				stmt.close();						
			}catch(Exception e){
				System.out.println("[error] getTrends: "+e.getMessage());
				System.out.println("[error] query: "+qry);
				e.printStackTrace();
				return data;
			}
						
			return data;
		}

		public static void insertSplitData(SQLConnectionUtils sqlHelper,String symbol,SplitData splitData) {
			// TODO Auto-generated method stub
			String qry = null;
			
			try{
				Connection conn = sqlHelper.getConnection();
				Statement stmt = conn.createStatement();	
								
				//prepare date
				java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm");
				String date = sdf.format(splitData.getDate());
				
				int type=0;
				
				if (splitData.getType()==QuoteDataType.SPLIT){
					type=1;
				}
				
				qry = "INSERT INTO splitData"
						+"(symbol,date,`type`,`value`)"+
						 " VALUES('"+
						 symbol+"'"+
						 ",'"+date+"'"+						
						 ","+type+
						 ","+splitData.getValue()+						 				
						 ")";
						 						 						 												
				stmt.executeUpdate(qry);
							
				//rs.close();
				stmt.close();							
			}catch(Exception e){
				System.out.println("[error] StoreForexData: "+e.getMessage());
				System.out.println("query: "+qry);
				//e.printStackTrace();				
			}
		}

		public static void storeAdjustedStock(SQLConnectionUtils sql,
				Stock s) {
			// TODO Auto-generated method stub
			List<Quote> quotes = s.getQuotes();
			for (int i=0;i<s.getQuotes().size();i++){
				Quote q = quotes.get(i);
				insertQuote(sql,"adj_dailyquotes",s.getSymbol(),q,s.getTf());				
			}	
		}

		public static void createFuturosTable(SQLConnectionUtils sqlHelper,
				String tableName) {
			// TODO Auto-generated method stub
			String qry = null;
			
			try{
				Connection conn = sqlHelper.getConnection();
				Statement stmt = conn.createStatement();	
				
				qry = "CREATE TABLE `"+tableName+"`("
						  +"`tradetime` date NOT NULL,"
						  +"`symbol` varchar(255) DEFAULT NULL,"
						  +"`open` float DEFAULT NULL,"
						  +"`high` float DEFAULT NULL,"
						  +"`low` float DEFAULT NULL,"
						  +"`close` float DEFAULT NULL,"
						  +"`volume` bigint(20) DEFAULT -1,"
						  +"`openInterest` bigint(20) DEFAULT -1,"
						  +"`totalVolume` bigint(20) DEFAULT -1,"
						  +"`totalOpenInterest` bigint(20) DEFAULT -1,"
						  +"PRIMARY KEY (`tradetime`)"
						  +") ENGINE=InnoDB";
						 						 						 												
				stmt.executeUpdate(qry);
							
				//rs.close();
				stmt.close();							
			}catch(Exception e){
				System.out.println("[error] createForexTable: "+e.getMessage());
				System.out.println("query: "+qry);
				//e.printStackTrace();				
			}
		}

		public static void storeFutureData(SQLConnectionUtils sqlHelper,
				String symbol, FutureQuote futureQuote, String tableName) {
			// TODO Auto-generated method stub
			
		}

		public static void insertFutureCSIData(SQLConnectionUtils sqlHelper, String symbol, String tableName, FutureCSIQuote futureCSIQuote) {
			// TODO Auto-generated method stub
			// TODO Auto-generated method stub		
						String qry = null;
									
						try{
							Connection conn = sqlHelper.getConnection();
							Statement stmt = conn.createStatement();	
											
							//prepare date
							java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd");
							String date = sdf.format(futureCSIQuote.getDate());
											
							qry = "INSERT INTO `"+tableName+
									"`(tradetime,symbol,`open`,high,low,`close`,volume,openInterest)"+
									 " VALUES('"+
									 date+
									 "','"+symbol+"'"+
									 ","+futureCSIQuote.getOpen()+
									 ","+futureCSIQuote.getHigh()+
									 ","+futureCSIQuote.getLow()+
									 ","+futureCSIQuote.getClose()+
									 ","+futureCSIQuote.getVolume()+
									 ","+futureCSIQuote.getOpenInterest()
									 +")";
									 									 												
							stmt.executeUpdate(qry);
										
							//rs.close();
							//System.out.println("query: "+qry);
							stmt.close();							
						}catch(Exception e){
							System.out.println("[error] StoreForexData: "+e.getMessage());
							System.out.println("query: "+qry);
							//e.printStackTrace();				
						}
		}
		
		
		public static void storeRandomData(SQLConnectionUtils sqlHelper,float high,
					float open,float close,float low,String tableName) {
			// TODO Auto-generated method stub		
			String qry = null;
						
			try{
				Connection conn = sqlHelper.getConnection();
				Statement stmt = conn.createStatement();	
								
				qry = "INSERT INTO "+tableName+
						"(`open`,high,low,`close`)"+
						 " VALUES("+
						 open+
						 ","+high+
						 ","+low+
						 ","+close						
						 +")";
						 	 												
				stmt.executeUpdate(qry);
							
				//rs.close();
				stmt.close();							
			}catch(Exception e){
				System.out.println("[error] storeRandomData: "+e.getMessage());
				System.out.println("query: "+qry);
				//e.printStackTrace();				
			}
		}

		public static List<ForexQuote> getRandomData(
				SQLConnectionUtils sqlHelper, String tableName) {
			// TODO Auto-generated method stub
			List<ForexQuote> data = new ArrayList<ForexQuote>();									
			String qry = null;			
			
			try{
				Connection conn = sqlHelper.getConnection();
				Statement stmt = conn.createStatement();							
																			
				qry = "SELECT * FROM "+tableName;
								
				ResultSet rs = stmt.executeQuery(qry);
				
				//System.out.println("query: "+qry);	
				int i=1;
				while (rs.next()) {	
					//System.out.println("quote: "+i++);
					ForexQuote fq = new ForexQuote();					
					fq.setOpen(rs.getFloat("open"));
					fq.setHigh(rs.getFloat("high"));
					fq.setLow(rs.getFloat("low"));
					fq.setClose(rs.getFloat("close"));
										
					data.add(fq);						
				}
				rs.close();
				stmt.close();						
			}catch(Exception e){
				System.out.println("[error] getRandomDatas: "+e.getMessage());
				System.out.println("[error] query: "+qry);
				e.printStackTrace();
				return data;
			}
						
			return data;
		}
		
		private static void addSymbols(ArrayList<String> symbols,String symbol){
			for (int i=0;i<symbols.size();i++){
				if (symbols.get(i).equalsIgnoreCase(symbol)){
					return;
				}
			}
			symbols.add(symbol);
		}

		
		
		public static boolean symbolExists(SQLConnectionUtils sqlHelper, String tableName, 
				String symbol,boolean pattern){
			
			ArrayList<String> symbols = new ArrayList<String>();						
			String qry = null;			
			
			try{
				Connection conn = sqlHelper.getConnection();
				Statement stmt = conn.createStatement();	
				
				qry = "SELECT distinct(symbol) FROM "+tableName;
				if (!pattern)
					qry += " WHERE symbol='"+symbol+"'";
				else
					qry += " WHERE symbol LIKE '"+symbol+"%'";
				
				ResultSet rs = stmt.executeQuery(qry);
								
				while (rs.next()) {	
					rs.close();
					stmt.close();	
					return true;									
				}
				
				rs.close();
				stmt.close();						
			}catch(Exception e){
				System.out.println("[error]  retrieveSymbols: "+e.getMessage());
				System.out.println("[error] query: "+qry);
				e.printStackTrace();
				return false;
			}
			//System.out.println("Last Insert: "+volume);						
			return false;			
		}
		
		
		public static ArrayList<SymbolSector> retrieveSymbolSector(
				SQLConnectionUtils sqlHelper,String tableName) {
			
			ArrayList<SymbolSector> symbols = new ArrayList<SymbolSector>();						
			String qry = null;			
			
			try{
				Connection conn = sqlHelper.getConnection();
				Statement stmt = conn.createStatement();	
				
				qry = "SELECT symbol,sourceTable FROM "+tableName;
				
				ResultSet rs = stmt.executeQuery(qry);
							
				while (rs.next()) {	
					SymbolSector ss = new SymbolSector();
					ss.setSymbol(rs.getString("symbol"));	
					ss.setSourceTable(rs.getString("sourceTable"));
					symbols.add(ss);
				}
				rs.close();
	
				stmt.close();						
			}catch(Exception e){
				System.out.println("[error]  retrieveSymbols: "+e.getMessage());
				System.out.println("[error] query: "+qry);
				e.printStackTrace();
				return null;
			}
			//System.out.println("Last Insert: "+volume);						
			return symbols;			
		}
		
		public static ArrayList<SymbolSector> retrieveSymbolsSector(
				SQLConnectionUtils sqlHelper,String tableName,String fieldName,  
				String pattern) {
			
			ArrayList<SymbolSector> symbols = new ArrayList<SymbolSector>();						
			String qry = null;			
			
			try{
				Connection conn = sqlHelper.getConnection();
				Statement stmt = conn.createStatement();	
				
				qry = "SELECT distinct(symbol) FROM "+tableName;
				qry += " WHERE "+fieldName+" LIKE '%"+pattern+"%'";
				
				ResultSet rs = stmt.executeQuery(qry);
							
				while (rs.next()) {	
					SymbolSector ss = new SymbolSector();
					ss.setSymbol(rs.getString("symbol"));	
					//ss.setSourceTable(rs.getString("sourceTable"));
					symbols.add(ss);
				}
				rs.close();
	
				stmt.close();						
			}catch(Exception e){
				System.out.println("[error]  retrieveSymbols: "+e.getMessage());
				System.out.println("[error] query: "+qry);
				e.printStackTrace();
				return null;
			}
			//System.out.println("Last Insert: "+volume);						
			return symbols;			
		}

		public static boolean isCointegrationInserted(
				SQLConnectionUtils sqlHelper, String tableName, String cod, GregorianCalendar fromdate, GregorianCalendar todate){
			// TODO Auto-generated method stub
			
			
			String qry = null;
		
			try{
				Connection conn = sqlHelper.getConnection();
				Statement stmt =sqlHelper.getStatement();
				//Statement stmt = conn.createStatement();	
																		
				qry = "SELECT cod FROM "+tableName+
					" WHERE cod='"+cod+"'"
					+" and fromdate>='"+DateUtils.calendarToString(fromdate, "yyyy-MM-dd")
					+"' and todate<='"+DateUtils.calendarToString(todate, "yyyy-MM-dd")+"'" ;
								
				ResultSet rs = stmt.executeQuery(qry);
				
				//System.out.println("[isCointegrationInserted] qry consulta: "+qry);
				while (rs.next()) {
					if (rs.getString("cod")!=null){
						//System.out.println("[isCointegrationInserted] cod insertado: "+cod);
						return true;
					}
				}
				rs.close();
			}catch(Exception e){
				System.out.println("[error] isCointegrationInserted: "+e.getMessage());
				System.out.println("[error] query: "+qry);
				return false;
			}
								
			return false;
		}

		
		
	

		public static void insertDividend(SQLConnectionUtils sqlHelper,
				String symbol, GregorianCalendar gc, double value) {
			// TODO Auto-generated method stub
			String qry = null;
			String tableName = "dividends";
			
			try{
				Connection conn = sqlHelper.getConnection();
				Statement stmt = conn.createStatement();	
								
				//prepare date
				java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd");
				String date = sdf.format(gc.getTime());
								
				qry = "INSERT INTO `"+tableName+
						"`(dividenddate,symbol,amount)"+
						 " VALUES('"+
						 date+
						 "','"+symbol+"'"+
						 ","+value
						 +")";
						 									 												
				stmt.executeUpdate(qry);
							
				//rs.close();
				System.out.println("query: "+qry);
				stmt.close();							
			}catch(Exception e){
				System.out.println("[error] insertDividend: "+e.getMessage());
				System.out.println("query: "+qry);
				//e.printStackTrace();				
			}
			
			
		}
		
		public static ArrayList<Dividend> retrieveDividend(SQLConnectionUtils sqlHelper,String symbol) {
			// TODO Auto-generated method stub
			ArrayList<Dividend> dividends = new ArrayList<Dividend>(); 
			String qry = null;
			String tableName = "dividends";
			
			try{
				Connection conn = sqlHelper.getConnection();
				Statement stmt = conn.createStatement();	
				
				qry = "SELECT dividenddate,symbol,amount FROM `"+tableName+
						"` WHERE symbol='"+symbol+"'"
						+ " ORDER BY dividenddate asc";
						
				//System.out.println("[retrieveDividend] qry : "+qry);
						
				ResultSet rs = stmt.executeQuery(qry);
										
				while (rs.next()) {	
					Dividend d = new Dividend();
					d.setDate(rs.getDate("dividenddate"));
					d.setSymbol(rs.getString("symbol"));
					d.setAmount(rs.getFloat("amount"));							
					dividends.add(d);									
				}
				rs.close();										
				//System.out.println("query: "+qry);
				stmt.close();							
			}catch(Exception e){
				System.out.println("[error] retrieveDividend: "+e.getMessage());
				System.out.println("query: "+qry);
				return null;
				//e.printStackTrace();				
			}
			return dividends;
			
		}

		public static void updateAdjCloseQuote(SQLConnectionUtils sqlHelper, Quote quote) {
			// TODO Auto-generated method stub
			String qry = null;
			String tableName = "dailyquotes";
			
			try{
				Connection conn = sqlHelper.getConnection();
				Statement stmt = conn.createStatement();	
								
				//prepare date
				java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd");
				String date = sdf.format(quote.getDate());
								
				qry = "UPDATE "+tableName
						+" SET adjClose="+quote.getAdjClose()
						+" WHERE symbol='"+quote.getSymbol()
						+"' and tradetime='"+date+"'";
				
				//System.out.println("[updateQuote] qry: "+qry);
				stmt.executeUpdate(qry);
							
				//rs.close();
				stmt.close();							
			}catch(Exception e){
				System.out.println("[error] StoreForexData: "+e.getMessage());
				System.out.println("query: "+qry);
				//e.printStackTrace();				
			}
		}

		public static void insertSAPStock(SQLConnectionUtils sqlHelper,
				SAPStock sapStock) {
			// TODO Auto-generated method stub
			String qry = null;
			String tableName = "indexcomponents";
			
			try{
				Connection conn = sqlHelper.getConnection();
				Statement stmt = conn.createStatement();	
								
				qry = "INSERT INTO `"+tableName+
						"`(`index`,`symbol`,`name`,`sector`)"+
						 " VALUES('"+
						 sapStock.getIndex()+
						 "','"+sapStock.getSymbol()+
						 "','"+sapStock.getName()+
						 "','"+sapStock.getSector()+"')";
						 									 												
				stmt.executeUpdate(qry);
							
				//rs.close();
				System.out.println("query: "+qry);
				stmt.close();							
			}catch(Exception e){
				System.out.println("[error] insertSAPStock: "+e.getMessage());
				System.out.println("query: "+qry);
				//e.printStackTrace();				
			}
			
			
		}

		public static ArrayList<Date> retrieveDates(
				SQLConnectionUtils sqlHelper, String tableName, String symbol,
				GregorianCalendar from, GregorianCalendar to) {
			// TODO Auto-generated method stub
			ArrayList<Date> dates = new ArrayList<Date>();						
			String qry = null;			
			
			try{
				Connection conn = sqlHelper.getConnection();
				Statement stmt = sqlHelper.getStatement();	
				
				String whereClause = null;
				
				if (symbol.equalsIgnoreCase("ALL_SYMBOLS"))
					whereClause = "";
				else
					whereClause = " and Symbol='"+symbol+"'";
				
															
				qry = "SELECT distinct(tradetime) FROM `"+tableName					
					+ "` WHERE tradetime>='"+DateUtils.calendarToString(from, "yyyy-MM-dd") 
					+ "' and tradetime<='"+DateUtils.calendarToString(to, "yyyy-MM-dd")+"'"
					+ whereClause
					+ " ORDER BY tradetime asc";
				
				//System.out.println("[retrieveQuotes] qry : "+qry);
				
				ResultSet rs = stmt.executeQuery(qry);
								
				while (rs.next()) {	
					dates.add(rs.getDate("tradetime"));								
				}
				rs.close();
				//stmt.close();						
			}catch(Exception e){
				System.out.println("[error] retrieveQuotes: "+e.getMessage());
				System.out.println("[error] query: "+qry);
				e.printStackTrace();
				return null;
			}
			//System.out.println("Last Insert: "+volume);						
			return dates;			
		}

		public static ArrayList<String> retrieveTables(
				SQLConnectionUtils sqlHelper, String dataBBDD) {
			// TODO Auto-generated method stub
			ArrayList<String> tables = new ArrayList<String>();						
			String qry = null;			
			
			try{
				Connection conn = sqlHelper.getConnection();
				Statement stmt = conn.createStatement();	
				
				
					qry = "SHOW TABLES";
					ResultSet rs = stmt.executeQuery(qry);
					
					while (rs.next()) {	
						String table = rs.getString("Tables_in_"+dataBBDD);
						
						tables.add(table);	
						//System.out.println("tabla encontada: "+symbol);
					}
				
				stmt.close();						
			}catch(Exception e){
				System.out.println("[error] retrieveTables: "+e.getMessage());
				System.out.println("[error] query: "+qry);
				e.printStackTrace();
				return null;
			}
			//System.out.println("Last Insert: "+volume);		
			return tables;
		}

		public static void updateAdjQuote(SQLConnectionUtils sqlHelper,
				Quote quote) {
			// TODO Auto-generated method stub
			String qry = null;
			String tableName = "dailyquotes";
			
			try{
				Connection conn = sqlHelper.getConnection();
				Statement stmt = conn.createStatement();	
								
				//prepare date
				java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd");
				String date = sdf.format(quote.getDate());
								
				qry = "UPDATE "+tableName
						+" SET adjClose="+quote.getAdjClose()
						+" ,adjOpen="+quote.getAdjOpen()
						+" ,adjLow="+quote.getAdjLow()
						+" ,adjHigh="+quote.getAdjHigh()
						+" WHERE symbol='"+quote.getSymbol()
						+"' and tradetime='"+date+"'";
				
				//System.out.println("[updateQuote] qry: "+qry);
				stmt.executeUpdate(qry);
							
				//rs.close();
				stmt.close();							
			}catch(Exception e){
				System.out.println("[error] StoreForexData: "+e.getMessage());
				System.out.println("query: "+qry);
				//e.printStackTrace();				
			}
		}

		public static void insertFutureDesciprion(SQLConnectionUtils sqlHelper,
				String tableName, String symbol, String descr) {
			// TODO Auto-generated method stub
			String qry = null;
			
			try{
				Connection conn = sqlHelper.getConnection();
				Statement stmt = conn.createStatement();	
								
				
				qry = "INSERT INTO `"+tableName+
						"`(symbol,description)"+
						 " VALUES('"+
						 symbol+
						 "','"+descr+"')";
						 									 												
				stmt.executeUpdate(qry);
							
				//rs.close();
				//System.out.println("query: "+qry);
				stmt.close();							
			}catch(Exception e){
				System.out.println("[error] StoreForexData: "+e.getMessage());
				System.out.println("query: "+qry);
				//e.printStackTrace();				
			}
		}

		public static SymbolInfo getSymbolInfo(
				SQLConnectionUtils sqlHelper, String tableName, String symbol) {
			// TODO Auto-generated method stub
			String qry = null;
			SymbolInfo symbolInfo = new SymbolInfo();
			try{
				Connection conn = sqlHelper.getConnection();
				Statement stmt =sqlHelper.getStatement();
				//Statement stmt = conn.createStatement();	
																		
				qry = "SELECT mult,margin from `"+tableName+"`"+
					" WHERE symbol='"+symbol+"'";	
				ResultSet rs = stmt.executeQuery(qry);
				
				//System.out.println("[isCointegrationInserted] qry consulta: "+qry);
				while (rs.next()) {
						int mult = rs.getInt("mult");
						int margin = rs.getInt("margin");
						symbolInfo.setMult(mult);
						symbolInfo.setMargin(margin);
				}
				rs.close();
			}catch(Exception e){
				System.out.println("[error]getSymbolInfo: "+e.getMessage());
				System.out.println("[error] query: "+qry);
				return null;
			}
								
			return symbolInfo;
		}
		
		public static SymbolInfo getSymbolInfo2(
				SQLConnectionUtils sqlHelper, String tableName, String symbol) {
			// TODO Auto-generated method stub
			String qry = null;
			SymbolInfo symbolInfo = new SymbolInfo();
			try{
				Connection conn = sqlHelper.getConnection();
				Statement stmt =sqlHelper.getStatement();
				//Statement stmt = conn.createStatement();	
																		
				qry = "SELECT symbol,description,minTick,tickValue from `"+tableName+"`"+
					" WHERE symbol='"+symbol+"'";	
				ResultSet rs = stmt.executeQuery(qry);
				
				//System.out.println("[isCointegrationInserted] qry consulta: "+qry);
				while (rs.next()) {
						String description = rs.getString("description");
						double minTick = rs.getDouble("minTick");
						double tickValue = rs.getDouble("tickValue");
						symbolInfo.setSymbol(symbol);
						symbolInfo.setDescription(description);
						symbolInfo.setTickValue(tickValue);
						symbolInfo.setMinTick(minTick);
				}
				rs.close();
			}catch(Exception e){
				System.out.println("[error]getSymbolInfo: "+e.getMessage());
				System.out.println("[error] query: "+qry);
				return null;
			}
								
			return symbolInfo;
		}
		
		
		


		public static void storeForexData(SQLConnectionUtils sqlHelper,
				String symbol, Quote forexQuote, String tableName) {
			// TODO Auto-generated method stub
			String qry = null;
			
			try{
				Connection conn = sqlHelper.getConnection();
				Statement stmt = conn.createStatement();	
								
				//prepare date
				java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm");
				String date = sdf.format(forexQuote.getDate());
								
				qry = "INSERT INTO "+tableName+
						"(tradetime,timeframe,symbol,`open`,high,low,`close`,volume)"+
						 " VALUES('"+
						 date+
						 "',1,"+"'"+symbol+"'"+
						 ","+forexQuote.getOpen()+
						 ","+forexQuote.getHigh()+
						 ","+forexQuote.getLow()+
						 ","+forexQuote.getClose()+
						 ","+forexQuote.getVolume()						
						 +")";
						 
						 
						 												
				stmt.executeUpdate(qry);
							
				//rs.close();
				stmt.close();							
			}catch(Exception e){
				System.out.println("[error] StoreForexData: "+e.getMessage());
				System.out.println("query: "+qry);
				//e.printStackTrace();				
			}
		}

		public static void insertDIBSObject(SQLConnectionUtils sql,String symbol,DIBSObject obj) {
			// TODO Auto-generated method stub
			String qry = null;
			
			try{
				Connection conn = sql.getConnection();
				Statement stmt = conn.createStatement();	
				
				// Print the users table
				qry = "INSERT INTO umbralsstudy(rr,symbol,EntryDay,hourL,hourH,umbralL,umbralH,totalTrades,numTrades,percentage,result)"+
						 " VALUES("+
						 obj.getRr()+
						 ",'"+symbol+
						 "',"+obj.getDay()+
						 ","+obj.getHourL()+
						 ","+obj.getHourH()+						
						 ","+obj.getUmbralL()+
						 ","+obj.getUmbralH()+
						 ","+obj.getTotalTrades()+						
						 ","+obj.getNumTrades()+
						 ","+PrintUtils.Print(obj.getPercentage())+
						 ","+PrintUtils.Print(obj.getResult())+
						 ")";
						
				stmt.executeUpdate(qry);
							
				stmt.close();							
			}catch(Exception e){
				System.out.println("[error] insertDIBSObject: "+e.getMessage());
				System.out.println("query: "+qry);				
			}
		}
		
		public static void insertQuoteClassificationObject(SQLConnectionUtils sql,String tableName,
				String symbol,String dataProvider, int entryHour, QuoteClassification obj){
			String qry = null;
			
			try{
				Connection conn = sql.getConnection();
				Statement stmt = conn.createStatement();	
				
				java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:SS");

				String date = sdf.format(obj.getDate());
				// Print the users table
				qry = "INSERT INTO "+tableName+"(`date`,symbol,dataProvider,entryHour,`type`,`interval`,"
						+"factor1,factor2,factor3,factor4,factor5,factor6,factor7)"+
						 " VALUES('"+
						 date+
						 "','"+symbol+
						 "','"+dataProvider+
						 "',"+entryHour+
						 ",'"+obj.getMs().name()+
						 "',"+obj.getInterval()+
						 ","+obj.getFactors().get(0)+
						 ","+obj.getFactors().get(1)+
						 ","+obj.getFactors().get(2)+
						 ","+obj.getFactors().get(3)+
						 ","+obj.getFactors().get(4)+
						 ","+obj.getFactors().get(5)+
						 ","+obj.getFactors().get(6)+
						 ")";
						
				stmt.executeUpdate(qry);
							
				stmt.close();							
			}catch(Exception e){
				System.out.println("[error]insertQuoteClassificationObject: "+e.getMessage());
				System.out.println("query: "+qry);				
			}
		}

		public static ArrayList<QuoteClassification> retrieveQuoteClassifications(
				SQLConnectionUtils sqlHelper, drosa.strategies.TimeFrame tf, String symbol,
				int h, String type,int interval1,int interval2,
				int factor,double umbral1,double umbral2,
				GregorianCalendar fromDate,
				GregorianCalendar toDate) {
			// TODO Auto-generated method stub
			ArrayList<QuoteClassification> cls = new ArrayList<QuoteClassification>();
			String qry = null;
			String whereClause=" ";
			String whereClause2=" and `type`='"+type+"'";
			
			String tableName="";
			
			if (tf==drosa.strategies.TimeFrame.WEEKLY)
				tableName="weekly";
			if (tf==drosa.strategies.TimeFrame.DAILY)
				tableName="daily";
			/*if (type.equalsIgnoreCase("NRX")){
				whereClause2=" and (`type`='"+type+"')";	
			}*/
			if (type.equalsIgnoreCase("NRX")){
				whereClause2=" and (`type`='"+type+"' or `type`='IB')";	
			}
			if (type.equalsIgnoreCase("NONE")){
				whereClause2=" ";	
			}
			
			String whereClause3=" and factor"+String.valueOf(factor)+">="+umbral1
			+"  and factor"+String.valueOf(factor)+"<="+umbral2;
			
			if (interval1>0 && !type.equalsIgnoreCase("NONE"))
				whereClause=" and (`interval`>="+interval1+" and `interval`<="+interval2+")";
			try{
				Connection conn = sqlHelper.getConnection();
				Statement stmt = conn.createStatement();	
												
				qry = "SELECT * FROM "+tableName					
					+ " WHERE symbol='"+symbol+"'"
					+ whereClause2
					+ whereClause3
					+ whereClause
					+" and entryHour="+h
					+" and `date`>='"+DateUtils.calendarToString(fromDate, "yyyy-MM-dd") 
						+ "' and `date`<='"+DateUtils.calendarToString(toDate, "yyyy-MM-dd")+" 23:59:59'"
					+ " ORDER BY date asc";
				
				//System.out.println("[retrieveQuotes] qry : "+qry);
				
				ResultSet rs = stmt.executeQuery(qry);
								
				while (rs.next()) {	
					QuoteClassification cl = new QuoteClassification();
					Date date = new Date();
				    java.sql.Timestamp mysqlTimestamp = rs.getTimestamp("date");
				    //System.out.println("timestamp: "+mysqlTimestamp);
					date.setTime(mysqlTimestamp.getTime());
					//System.out.println("date: "+date);
					cl.setDate(date);
					cl.setMs(MarketStructure.valueOf(rs.getString("type")));
					cl.setInterval(rs.getInt("interval"));
					ArrayList<Double> factors = new ArrayList<Double>();
					factors.add((double) rs.getFloat("factor1"));
					factors.add((double) rs.getFloat("factor2"));
					factors.add((double) rs.getFloat("factor3"));
					factors.add((double) rs.getFloat("factor4"));
					factors.add((double) rs.getFloat("factor5"));
					factors.add((double) rs.getFloat("factor6"));
					factors.add((double) rs.getFloat("factor7"));
					cl.setFactors(factors);
					cls.add(cl);									
				}
				rs.close();
				stmt.close();						
			}catch(Exception e){
				System.out.println("[error]  retrieveQuoteClassifications: "+e.getMessage());
				System.out.println("[error] query: "+qry);
				e.printStackTrace();
				return cls;
			}
			
			return cls;
		}
		
		public static void insertPeakInterval(SQLConnectionUtils sql,String tableName, 
				String symbol,Quote q,int interval,boolean high){
			
			String qry = null;
						
			try{
				Connection conn = sql.getConnection();
				Statement stmt = conn.createStatement();	
				
				double peakValue = q.getHigh();
				int type =1;
				
				if (!high){
					peakValue = q.getLow();
					type=0;
				}
				//prepare date
				java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd");

				String currentDate = sdf.format(q.getDate());
				// Print the users table
				qry = "INSERT INTO "+tableName+"(tradetime,symbol,pinterval,peakValue,peakType)"+
						 " VALUES('"+
						 currentDate+
						 "','"+symbol+
						 "',"+interval+
						 ","+Float.valueOf(PrintUtils.Print(peakValue))+						
						 ","+type+")";
						
				stmt.executeUpdate(qry);
							
				//rs.close();
				stmt.close();							
			}catch(Exception e){
				System.out.println("[error] insertData: "+e.getMessage());
				System.out.println("query: "+qry);
				//e.printStackTrace();				
			}
		}
		
		public static void insertStrongMarkets(SQLConnectionUtils sql,Date tradetime,int interval, 
				String markets){
			
			String qry = null;
						
			try{
				Connection conn = sql.getConnection();
				Statement stmt = conn.createStatement();	
				
				//prepare date
				java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd");

				String currentDate = sdf.format(tradetime);
				// Print the users table
				qry = "INSERT INTO strongmarkets(tradetime,pinterval,markets)"+
						 " VALUES('"+
						 currentDate+
						 "',"+interval+
						 ",'"+markets+						
						 "')";
						
				stmt.executeUpdate(qry);
							
				//rs.close();
				stmt.close();							
			}catch(Exception e){
				System.out.println("[error] insertData: "+e.getMessage());
				System.out.println("query: "+qry);
				//e.printStackTrace();				
			}
		}
		
		public static ArrayList<StrongMarkets> retrieveStrongMarkets(SQLConnectionUtils sql,
				GregorianCalendar from,GregorianCalendar to,int interval){
			
			ArrayList<StrongMarkets> markets = new ArrayList<StrongMarkets>();
			String qry = null;
						
			try{
				Connection conn = sql.getConnection();
				Statement stmt = conn.createStatement();	
							
				qry = "SELECT * FROM strongMarkets"					
					+ " WHERE `pinterval`="+interval
					+ " and tradetime>='"+DateUtils.calendarToString(from, "yyyy-MM-dd")+" 00:00:00'"
					+ " and tradetime<='"+DateUtils.calendarToString(to, "yyyy-MM-dd")+" 23:59:59'"
					+" order by tradetime desc";
				
				//System.out.println("[retrieveQuotes] qry : "+qry);
				
				ResultSet rs = stmt.executeQuery(qry);
								
				while (rs.next()) {	
					StrongMarkets m = new StrongMarkets();
					Date date1 = new Date();
				    java.sql.Timestamp mysqlTimestamp = rs.getTimestamp("tradetime");
				    //System.out.println("timestamp: "+mysqlTimestamp);
					date1.setTime(mysqlTimestamp.getTime());
					
					m.setTradetime(date1);
					m.setMarkets(rs.getString("markets"));
					markets.add(m);						
				}
							
				//rs.close();
				stmt.close();							
			}catch(Exception e){
				System.out.println("[error] retrieveStrongMarkets: "+e.getMessage());
				System.out.println("query: "+qry);
				//e.printStackTrace();		
				return null;
			}
			return markets;
		}
		
		public static Date retrieveLastMinMax(SQLConnectionUtils sqlHelper,String symbol,
				int interval,GregorianCalendar gc,boolean isHigh){
			Date date = null;
			String qry = null;
			try{
				Connection conn = sqlHelper.getConnection();
				Statement stmt = conn.createStatement();	
				int type=1;
				if (!isHigh) type=0;
												
				qry = "SELECT max(tradetime) as tradetime FROM peakInterval"					
					+ " WHERE symbol='"+symbol+"'"
					+ " and `pinterval`="+interval
					+ " and `peaktype`="+type
					+ " and tradetime<='"+DateUtils.calendarToString(gc, "yyyy-MM-dd")+"'";
				
				//System.out.println("[retrieveQuotes] qry : "+qry);
				
				ResultSet rs = stmt.executeQuery(qry);
								
				while (rs.next()) {	
					Date date1 = new Date();
				    java.sql.Timestamp mysqlTimestamp = rs.getTimestamp("tradetime");
				    //System.out.println("timestamp: "+mysqlTimestamp);
					date1.setTime(mysqlTimestamp.getTime());
					return date1;							
				}
				rs.close();
				stmt.close();						
			}catch(Exception e){
				System.out.println("[error]  retrieveQuoteClassifications: "+e.getMessage());
				System.out.println("[error] query: "+qry);
				e.printStackTrace();
				return date;
			}
			return date;
		}
		
		public static ArrayList<PeakInterval> retrievePeaks(SQLConnectionUtils sqlHelper,
				int interval,GregorianCalendar gc){
			ArrayList<PeakInterval> peaks = new ArrayList<PeakInterval>();
			Date date = null;
			String qry = null;
			try{
				Connection conn = sqlHelper.getConnection();
				Statement stmt = conn.createStatement();	
							
				qry = "SELECT tradetime FROM peakInterval"					
					+ " WHERE `pinterval`="+interval
					+ " and tradetime<='"+DateUtils.calendarToString(gc, "yyyy-MM-dd")+" 00:00:00'"
					+" order by tradetime desc";
				
				System.out.println("[retrievePeaks] qry : "+qry);
				
				ResultSet rs = stmt.executeQuery(qry);
								
				while (rs.next()) {	
					PeakInterval peak = new PeakInterval();
					Date date1 = new Date();
				    java.sql.Timestamp mysqlTimestamp = rs.getTimestamp("tradetime");
				    //System.out.println("timestamp: "+mysqlTimestamp);
					date1.setTime(mysqlTimestamp.getTime());
					
					peak.setTradetime(date1);
					peak.setSymbol(rs.getString("symbol"));
					peak.setPinterval(rs.getInt("pinterval"));
					peak.setPeakType(rs.getInt("peakType"));
					peaks.add(peak);						
				}
				rs.close();
				stmt.close();						
			}catch(Exception e){
				System.out.println("[error]  retrieveQuoteClassifications: "+e.getMessage());
				System.out.println("[error] query: "+qry);
				e.printStackTrace();
				return peaks;
			}
			return peaks;
		}

		public static void insertDelistedStockInfo(SQLConnectionUtils sql,
				String tableName, String symbol, String description) {
			// TODO Auto-generated method stub
			String qry = null;
			
			try{
				Connection conn = sql.getConnection();
				Statement stmt = conn.createStatement();	
								
				// Print the users table
				qry = "INSERT INTO "+tableName+"(symbol,description)"+
						 " VALUES('"+
						 symbol+
						 "','"+description+
						 "')";
						
				stmt.executeUpdate(qry);
							
				//rs.close();
				stmt.close();
				//System.out.println("query: "+qry);
			}catch(Exception e){
				System.out.println("[error] insertData: "+e.getMessage());
				System.out.println("query: "+qry);
				//e.printStackTrace();				
			}
		}
		
		public static void insertStockSourceTable(SQLConnectionUtils sql,
				String tableName, String symbol, String sourceTable) {
			// TODO Auto-generated method stub
			String qry = null;
			
			try{
				Connection conn = sql.getConnection();
				Statement stmt = conn.createStatement();	
								
				// Print the users table
				qry = "INSERT INTO "+tableName+"(symbol,sourceTable)"+
						 " VALUES('"+
						 symbol+
						 "','"+sourceTable+
						 "')";
						
				stmt.executeUpdate(qry);
							
				//rs.close();
				stmt.close();
				//System.out.println("query: "+qry);
			}catch(Exception e){
				System.out.println("[error] insertData: "+e.getMessage());
				System.out.println("query: "+qry);
				//e.printStackTrace();				
			}
		}


		public static void insertSectorStockInfo(SQLConnectionUtils sql,
				String tableName, String symbol, String sector) {
			// TODO Auto-generated method stub
			// TODO Auto-generated method stub
						String qry = null;
						
						try{
							Connection conn = sql.getConnection();
							Statement stmt = conn.createStatement();	
											
							// Print the users table
							qry = "INSERT INTO "+tableName+"(symbol,sector)"+
									 " VALUES('"+
									 symbol+
									 "','"+sector+
									 "')";
									
							stmt.executeUpdate(qry);
										
							//rs.close();
							stmt.close();
							//System.out.println("query: "+qry);
						}catch(Exception e){
							System.out.println("[error] insertData: "+e.getMessage());
							System.out.println("query: "+qry);
							//e.printStackTrace();				
						}
		}
		
		private static Quote decodeQuote(String linea,int type){
			Date date = null;
			double open = 0;
			double high = 0;
			double low = 0;
			double close = 0;
			long volume= 0;
			if (type==1){
				//hora		
				date = DateUtils.getCSIDate(linea.split(" ")[0].trim());
			
				open = Float.valueOf(linea.split(" ")[1].trim());
				high = Float.valueOf(linea.split(" ")[2].trim());
				low = Float.valueOf(linea.split(" ")[3].trim());
				close = Float.valueOf(linea.split(" ")[4].trim());
				volume= Long.valueOf(linea.split(" ")[5].trim());
			}
			if (type==2){//PITRADING STOCKS
				//hora		
				date = DateUtils.getPiTradingDate(linea.split(",")[0].trim(),linea.split(",")[1].trim());
			
				open = Float.valueOf(linea.split(",")[2].trim());
				high = Float.valueOf(linea.split(",")[3].trim());
				low = Float.valueOf(linea.split(",")[4].trim());
				close = Float.valueOf(linea.split(",")[5].trim());
				volume= Long.valueOf(linea.split(",")[6].trim());
			}
			
			if (type==3){
				 //hora		
	            date = DateUtils.getDukasDate(linea.split(" ")[0].trim(),linea.split(" ")[1].trim());
				
	            open = Float.valueOf(linea.split(" ")[2].trim());
	            high = Float.valueOf(linea.split(" ")[3].trim());
	            low = Float.valueOf(linea.split(" ")[4].trim());
	            close = Float.valueOf(linea.split(" ")[5].trim());
			}
			Quote fq = new Quote();
			
			fq.setDate(date);
			fq.setOpen(open);
			fq.setClose(close);
			fq.setHigh(high);
			fq.setLow(low);		
			fq.setVolume(volume);
			
			return fq;
		}
		
		private static NewsItem decodeNewsItem(String line){
			NewsItem item = new NewsItem();
			
			String dateStr 	= line.split(",")[0].trim().split(" ")[0];
			String timeStr	= line.split(",")[0].trim().split(" ")[1];
			String currency	= line.split(",")[1].trim();
			String impact	= line.split(",")[2].trim();
			String descr	= line.split(",")[3].trim();
			
			Integer day 	= Integer.valueOf(dateStr.substring(0,2));
			Integer month	= Integer.valueOf(dateStr.substring(3,5));
			Integer year    = Integer.valueOf(dateStr.substring(6,10));
			Integer hour 	= Integer.valueOf(timeStr.substring(0,2));
	        Integer min 	= Integer.valueOf(timeStr.substring(3,5));
	                
	        Calendar cal = Calendar.getInstance();
	        cal.set(year, month, day, hour, min);
	        
	        item.setCurrency(Currency.valueOf(currency));
	        item.setImpact(NewsImpact.valueOf(impact));
	        item.setDate(cal);
	        item.setDescription(descr);
	        
			
			return item;
		}
		
		public static Tick decodeTickDukas(String line){
			Calendar date = DateUtils.getTickDate(line.split(" ")[0].trim(),line.split(" ")[1].trim(),DataProvider.DUKASCOPY_FOREX);
			double ask			= Float.valueOf(line.split(" ")[2].trim());
            double bid			= Float.valueOf(line.split(" ")[3].trim());
            double askVolume	= Float.valueOf(line.split(" ")[4].trim());
            double bidVolume 	= Float.valueOf(line.split(" ")[5].trim());
            
            Tick tick = new Tick();
            tick.setDate(date);
            tick.setAsk(ask);
            tick.setBid(bid);
            tick.setAskVolume(askVolume);
            tick.setBidVolume(bidVolume);
            
            return tick;
		}
		
		private static Tick decodeTickPepper(String line,int thr){
			String dateTimeStr = line.split(",")[1].trim();
			Calendar date = DateUtils.getTickDate(dateTimeStr.split(" ")[0].trim(),dateTimeStr.split(" ")[1].trim(),DataProvider.PEPPERSTONE_FOREX);
			double ask			= Float.valueOf(line.split(",")[3].trim());
            double bid			= Float.valueOf(line.split(",")[2].trim());
            
            Tick tick = null;
            
            if ((ask-bid)/0.00001>thr){
            	tick = new Tick();
            	tick.setDate(date);
            	tick.setAsk(ask);
            	tick.setBid(bid);
            }
            return tick;
		}
		
		private static Quote decodeQuote(String linea,DataProvider type){
			Date date = null;
			double open = 0;
			double high = 0;
			double low = 0;
			double close = 0;
			long volume= 0;
		
			if (type==DataProvider.DUKASCOPY_FOREX){
				 //hora		
	            date = DateUtils.getDukasDate(linea.split(" ")[0].trim(),linea.split(" ")[1].trim());
				
	            open = Float.valueOf(linea.split(" ")[2].trim());
	            high = Float.valueOf(linea.split(" ")[3].trim());
	            low = Float.valueOf(linea.split(" ")[4].trim());
	            close = Float.valueOf(linea.split(" ")[5].trim());
			}
			
			if (type==DataProvider.DUKASCOPY_FOREX2){
				 //hora		
	            date = DateUtils.getDukasDate2(linea.split(" ")[0].trim(),linea.split(" ")[1].trim());
				
	            open = Float.valueOf(linea.split(" ")[2].trim());
	            high = Float.valueOf(linea.split(" ")[3].trim());
	            low = Float.valueOf(linea.split(" ")[4].trim());
	            close = Float.valueOf(linea.split(" ")[5].trim());
			}
			
			if (type==DataProvider.PEPPERSTONE_FOREX){
				 //hora		
	            date = DateUtils.getPepperDate(linea.split(",")[0].trim(),linea.split(",")[1].trim());
				
	            open = Float.valueOf(linea.split(",")[2].trim());
	            high = Float.valueOf(linea.split(",")[3].trim());
	            low = Float.valueOf(linea.split(",")[4].trim());
	            close = Float.valueOf(linea.split(",")[5].trim());
			}
			
			if (type==DataProvider.FOREXDATA_FOREX){
				 //hora		
	            date = DateUtils.getPepperDate(linea.split(",")[0].trim(),linea.split(",")[1].trim());
				
	            open = Float.valueOf(linea.split(",")[2].trim());
	            high = Float.valueOf(linea.split(",")[3].trim());
	            low = Float.valueOf(linea.split(",")[4].trim());
	            close = Float.valueOf(linea.split(",")[5].trim());
			}
			if (type==DataProvider.FOREXDATA_FOREX0){
				 //hora		
	            date = DateUtils.getForexDataDate(linea.split(",")[0].trim(),linea.split(",")[1].trim());
				
	            open = Float.valueOf(linea.split(",")[2].trim());
	            high = Float.valueOf(linea.split(",")[3].trim());
	            low = Float.valueOf(linea.split(",")[4].trim());
	            close = Float.valueOf(linea.split(",")[5].trim());
			}
						
			Quote fq = new Quote();
			
			fq.setDate(date);
			fq.setOpen(open);
			fq.setClose(close);
			fq.setHigh(high);
			fq.setLow(low);		
			fq.setVolume(volume);
			
			//System.out.println(linea+" || "+DateUtils.datePrint(date)+" "+PrintUtils.getOHLC(fq));
			return fq;
		}
		
		
		private static int convert3(String[] values){
			int res = 0;
			if (values.length==2){
				res = Integer.valueOf(values[0])*1000+Integer.valueOf(QuoteShort.fill3(values[1])); 
			}else if (values.length==1){
				res = Integer.valueOf(values[0])*1000; 
			}
			
			return res;
		}
		
		private static QuoteShort decodeQuoteShort(String linea,DataProvider type){
			Date date = null;
			int open5 = 0;
			int high5 = 0;
			int low5 = 0;
			int close5 = 0;
			long vol= 0;
			long maxMin = 0;
			short year = 0;
			byte month = 0;
			byte day = 0;
			byte hh = 0;
			byte mm = 0;
			byte ss = 0;
		
			if (type==DataProvider.DUKASCOPY_FOREX){
				 //hora		
	            date = DateUtils.getDukasDate(linea.split(" ")[0].trim(),linea.split(" ")[1].trim());
	            
	            String timeStr = linea.split(" ")[1].trim();
	            String dateStr = linea.split(" ")[0].trim();
	            
	            year  = Short.valueOf(dateStr.split("\\.")[0].trim());
				month = Byte.valueOf(dateStr.split("\\.")[1].trim());
				day   = Byte.valueOf(dateStr.split("\\.")[2].trim());
						
	            hh = Byte.valueOf(timeStr.substring(0,2));
	            mm = Byte.valueOf(timeStr.substring(3,5));
	            ss = Byte.valueOf(timeStr.substring(6,8));
	            
	            double open = Double.valueOf(linea.split(" ")[2]);
	            //System.out.println("A CONVERTIR: "+linea);
	            if (open<10.0){
		            String openStr = linea.split(" ")[2].replace(".", "");
		            String highStr = linea.split(" ")[3].replace(".", "");
		            String lowStr = linea.split(" ")[4].replace(".", "");
		            String closeStr = linea.split(" ")[5].replace(".", "");
		            
		            openStr = QuoteShort.fill5(openStr);
		            highStr = QuoteShort.fill5(highStr);
		            lowStr = QuoteShort.fill5(lowStr);
		            closeStr =QuoteShort.fill5(closeStr);
		           
		            open5	= Integer.valueOf(openStr);
		            high5 	= Integer.valueOf(highStr);
		            low5 	= Integer.valueOf(lowStr);
		            close5 	= Integer.valueOf(closeStr);
	            }else{
	            	//System.out.println("A CONVERTIR: "+linea);
	            		String[] valuesO = linea.split(" ")[2].split("\\.");
	            		String[] valuesH = linea.split(" ")[3].split("\\.");
	            		String[] valuesL = linea.split(" ")[4].split("\\.");
	            		String[] valuesC = linea.split(" ")[5].split("\\.");
	            		
	            		
	            	open5 	= convert3(valuesO); 
	            	high5 	= convert3(valuesH); 
	            	low5	= convert3(valuesL); 
	            	close5 	= convert3(valuesC); 
	            }
	      
	            //System.out.println(linea.split(" ")[2].replace(".", "")+" "+open5);
			}else if (type==DataProvider.KIBOT){
		            
	            String dateStr = linea.split(",")[0].trim();
	            String timeStr = linea.split(",")[1].trim();
	            year  = Short.valueOf(dateStr.substring(6,10));
				month = Byte.valueOf(dateStr.substring(0,2));
				day   = Byte.valueOf(dateStr.substring(3,5));
						
	            hh = Byte.valueOf(timeStr.substring(0,2));
	            mm = Byte.valueOf(timeStr.substring(3,5));
	            //ss = Byte.valueOf(timeStr.substring(6,8));
				
	            String openStr = linea.split(",")[2].replace(".", "");
	            String highStr = linea.split(",")[3].replace(".", "");
	            String lowStr = linea.split(",")[4].replace(".", "");
	            String closeStr = linea.split(",")[5].replace(".", "");
	            String volStr = linea.split(",")[6].replace(".", "");
	           
	           
	            open5	= Integer.valueOf(openStr);
	            high5 	= Integer.valueOf(highStr);
	            low5 	= Integer.valueOf(lowStr);
	            close5 	= Integer.valueOf(closeStr);
	            vol		= Long.valueOf(volStr);
			}else if (type==DataProvider.KIBOTES){
	            String dateStr = linea.split(",")[0].trim();
	            String timeStr = linea.split(",")[1].trim();
	            year  = Short.valueOf(dateStr.substring(6,10));
				month = Byte.valueOf(dateStr.substring(0,2));
				day   = Byte.valueOf(dateStr.substring(3,5));
						
	            hh = Byte.valueOf(timeStr.substring(0,2));
	            mm = Byte.valueOf(timeStr.substring(3,5));
	            //ss = Byte.valueOf(timeStr.substring(6,8));
				
	            String openStr = QuoteShort.fillES(linea.split(",")[2]);
	            String highStr = QuoteShort.fillES(linea.split(",")[3]);
	            String lowStr  = QuoteShort.fillES(linea.split(",")[4]);
	            String closeStr = QuoteShort.fillES(linea.split(",")[5]);
	            String volStr = linea.split(",")[6].replace(".", "");
	           
	           
	            open5	= Integer.valueOf(openStr);
	            high5 	= Integer.valueOf(highStr);
	            low5 	= Integer.valueOf(lowStr);
	            close5 	= Integer.valueOf(closeStr);
	            vol		= Long.valueOf(volStr);
		}else if (type==DataProvider.DAVE){
			String dateStr="";
			String timeStr="";
			try{
            	dateStr = linea.split(" ")[0].trim();
				timeStr = linea.split(" ")[1].trim();
			}catch(Exception e){
				System.out.println("[ERROR] : "+linea+" "+dateStr+" "+timeStr);
			}
            year  = Short.valueOf(dateStr.substring(6,10));
			day = Byte.valueOf(dateStr.substring(0,2));
			month   = Byte.valueOf(dateStr.substring(3,5));
					
            hh = Byte.valueOf(timeStr.substring(0,2));
            mm = Byte.valueOf(timeStr.substring(3,5));
            //ss = Byte.valueOf(timeStr.substring(6,8));
			
            String openStr = linea.split(" ")[2];
            String highStr = linea.split(" ")[3];
            String lowStr  = linea.split(" ")[4];
            String closeStr = linea.split(" ")[5];
            //String volStr = linea.split(",")[6].replace(".", "");
           
           
            open5	= Integer.valueOf(openStr);
            high5 	= Integer.valueOf(highStr);
            low5 	= Integer.valueOf(lowStr);
            close5 	= Integer.valueOf(closeStr);
            //vol		= Long.valueOf(volStr);
		}else if (type==DataProvider.DAVEVOL){
			String dateStr="";
			String timeStr="";
			try{
            	dateStr = linea.split(" ")[0].trim();
				timeStr = linea.split(" ")[1].trim();
			}catch(Exception e){
				System.out.println("[ERROR] : "+linea+" "+dateStr+" "+timeStr);
			}
            year	= Short.valueOf(dateStr.substring(6,10));
			day 	= Byte.valueOf(dateStr.substring(0,2));
			month   = Byte.valueOf(dateStr.substring(3,5));
					
            hh = Byte.valueOf(timeStr.substring(0,2));
            mm = Byte.valueOf(timeStr.substring(3,5));
            ss = Byte.valueOf(timeStr.substring(6,8));
			
            String openStr = linea.split(" ")[2];
            String highStr = linea.split(" ")[3];
            String lowStr  = linea.split(" ")[4];
            String closeStr = linea.split(" ")[5];
            String volStr = linea.split(" ")[6].replace(".", "");
           
           
            open5	= Integer.valueOf(openStr);
            high5 	= Integer.valueOf(highStr);
            low5 	= Integer.valueOf(lowStr);
            close5 	= Integer.valueOf(closeStr);
            vol		= Long.valueOf(volStr);
		}else if (type==DataProvider.DAVEVOLMAXMIN){
			String dateStr="";
			String timeStr="";
			try{
            	dateStr = linea.split(" ")[0].trim();
				timeStr = linea.split(" ")[1].trim();
			}catch(Exception e){
				System.out.println("[ERROR] : "+linea+" "+dateStr+" "+timeStr);
			}
            year	= Short.valueOf(dateStr.substring(6,10));
			day 	= Byte.valueOf(dateStr.substring(0,2));
			month   = Byte.valueOf(dateStr.substring(3,5));
					
            hh = Byte.valueOf(timeStr.substring(0,2));
            mm = Byte.valueOf(timeStr.substring(3,5));
            ss = Byte.valueOf(timeStr.substring(6,8));
			
            String openStr = linea.split(" ")[2];
            String highStr = linea.split(" ")[3];
            String lowStr  = linea.split(" ")[4];
            String closeStr = linea.split(" ")[5];
            String volStr = linea.split(" ")[6].replace(".", "");
            String maxMinStr = linea.split(" ")[7];
            
            open5	= Integer.valueOf(openStr);
            high5 	= Integer.valueOf(highStr);
            low5 	= Integer.valueOf(lowStr);
            close5 	= Integer.valueOf(closeStr);
            vol		= Long.valueOf(volStr);
            maxMin  = Long.valueOf(maxMinStr);
		}else if (type==DataProvider.DUKASCOPY_FOREX3){
			 //hora		
            date = DateUtils.getDukasDate(linea.split(" ")[0].trim(),linea.split(" ")[1].trim());
            
            String timeStr = linea.split(" ")[1].trim();
            String dateStr = linea.split(" ")[0].trim();
            
            if (dateStr.split("\\.")[2].trim().length()==4){
            	year  = Short.valueOf(dateStr.split("\\.")[2].trim());
            	day   = Byte.valueOf(dateStr.split("\\.")[0].trim());
            }else{
            	year  = Short.valueOf(dateStr.split("\\.")[0].trim());
            	day   = Byte.valueOf(dateStr.split("\\.")[2].trim());
            }
			month = Byte.valueOf(dateStr.split("\\.")[1].trim());
				
					
            hh = Byte.valueOf(timeStr.substring(0,2));
            mm = Byte.valueOf(timeStr.substring(3,5));
            ss = Byte.valueOf(timeStr.substring(6,8));
            
            double open = Double.valueOf(linea.split(" ")[2].replace(",", "."));            
			String delimiter = ".";
			if (linea.split(" ")[3].contains(",") || linea.split(" ")[2].contains(",") || linea.split(" ")[4].contains(",") || linea.split(" ")[5].contains(",")){
				delimiter = ",";
			}
            if (open<10.0){
	            String openStr = linea.split(" ")[2].replace(delimiter, "");
	            String highStr = linea.split(" ")[3].replace(delimiter, "");
	            String lowStr = linea.split(" ")[4].replace(delimiter, "");
	            String closeStr = linea.split(" ")[5].replace(delimiter, "");
	            
	            openStr = QuoteShort.fill5(openStr);
	            highStr = QuoteShort.fill5(highStr);
	            lowStr = QuoteShort.fill5(lowStr);
	            closeStr =QuoteShort.fill5(closeStr);
	           
	            try{
		            open5	= Integer.valueOf(openStr);
		            high5 	= Integer.valueOf(highStr);
		            low5 	= Integer.valueOf(lowStr);
		            close5 	= Integer.valueOf(closeStr);
	            }catch(Exception e){
	            	//System.out.println(linea.split(" ")[2]+" "+openStr+". "+e.getMessage());
	            }
            }else{
            	//System.out.println("A CONVERTIR: "+linea);
            		String[] valuesO = linea.split(" ")[2].split(delimiter);
            		String[] valuesH  = linea.split(" ")[3].split(delimiter);
            		String[] valuesL = linea.split(" ")[4].split(delimiter);
            		String[] valuesC = linea.split(" ")[5].split(delimiter);
            		if (delimiter=="."){
            			valuesO  = linea.split(" ")[2].split("\\.");
            			valuesH = linea.split(" ")[3].split("\\.");
            			valuesL  = linea.split(" ")[4].split("\\.");
            			valuesC = linea.split(" ")[5].split("\\.");
             		}
            		
            	open5 	= convert3(valuesO); 
            	high5 	= convert3(valuesH); 
            	low5	= convert3(valuesL); 
            	close5 	= convert3(valuesC); 
            }
      
            //System.out.println(linea.split(" ")[2].replace(".", "")+" "+open5);
		}else if (type==DataProvider.PEPPERSTONE_FOREX){
			 //hora		
            date = DateUtils.getPepperDate(linea.split(",")[0].trim(),linea.split(",")[1].trim());
            
            String timeStr = linea.split(",")[1].trim();
            String dateStr = linea.split(",")[0].trim();
            
            year  = Short.valueOf(dateStr.split("\\.")[0].trim());
			month = Byte.valueOf(dateStr.split("\\.")[1].trim());
			day   = Byte.valueOf(dateStr.split("\\.")[2].trim());
					
            hh = Byte.valueOf(timeStr.substring(0,2));
            mm = Byte.valueOf(timeStr.substring(3,5));
            ss = 0;
            
            double open = Double.valueOf(linea.split(",")[2].replace(",", "."));            
			String delimiter = ".";
			if (linea.split(",")[3].contains(",") || linea.split(",")[2].contains(",") || linea.split(",")[4].contains(",") || linea.split(",")[5].contains(",")){
				delimiter = ",";
			}
            if (open<10.0){
	            String openStr	= linea.split(",")[2].replace(delimiter, "");
	            String highStr 	= linea.split(",")[3].replace(delimiter, "");
	            String lowStr 	= linea.split(",")[4].replace(delimiter, "");
	            String closeStr = linea.split(",")[5].replace(delimiter, "");
	            
	            openStr = QuoteShort.fill5(openStr);
	            highStr = QuoteShort.fill5(highStr);
	            lowStr = QuoteShort.fill5(lowStr);
	            closeStr =QuoteShort.fill5(closeStr);
	           
	            try{
		            open5	= Integer.valueOf(openStr);
		            high5 	= Integer.valueOf(highStr);
		            low5 	= Integer.valueOf(lowStr);
		            close5 	= Integer.valueOf(closeStr);
	            }catch(Exception e){
	            	//System.out.println(linea.split(" ")[2]+" "+openStr+". "+e.getMessage());
	            }
            }else{
            	//System.out.println("A CONVERTIR: "+linea);
            		String[] valuesO	= linea.split(",")[2].split(delimiter);
            		String[] valuesH  	= linea.split(",")[3].split(delimiter);
            		String[] valuesL 	= linea.split(",")[4].split(delimiter);
            		String[] valuesC 	= linea.split(",")[5].split(delimiter);
            		if (delimiter=="."){
            			valuesO  = linea.split(",")[2].split("\\.");
            			valuesH = linea.split(",")[3].split("\\.");
            			valuesL  = linea.split(",")[4].split("\\.");
            			valuesC = linea.split(",")[5].split("\\.");
             		}
            		
            	open5 	= convert3(valuesO); 
            	high5 	= convert3(valuesH); 
            	low5	= convert3(valuesL); 
            	close5 	= convert3(valuesC); 
            }		
		}else if (type==DataProvider.DUKASCOPY_FOREX4){
			 //hora		
			String dateTimeStr = linea.split(";")[0].trim(); 
			String timeStr = dateTimeStr.split(" ")[1].trim();
            String dateStr = dateTimeStr.split(" ")[0].trim();
            date = DateUtils.getDukasDate(dateStr,timeStr);
            
                        
            if (dateStr.split("\\.")[2].trim().length()==4){
            	year  = Short.valueOf(dateStr.split("\\.")[2].trim());
            	day   = Byte.valueOf(dateStr.split("\\.")[0].trim());
            }else{
            	year  = Short.valueOf(dateStr.split("\\.")[0].trim());
            	day   = Byte.valueOf(dateStr.split("\\.")[2].trim());
            }
			month = Byte.valueOf(dateStr.split("\\.")[1].trim());
				
					
            hh = Byte.valueOf(timeStr.substring(0,2));
            mm = Byte.valueOf(timeStr.substring(3,5));
            ss = Byte.valueOf(timeStr.substring(6,8));
            
            double open = Double.valueOf(linea.split(";")[1].replace(",", "."));            
			String delimiter = ".";
			if (linea.split(";")[1].contains(",") || linea.split(";")[2].contains(",") || linea.split(";")[3].contains(",") || linea.split(";")[4].contains(",")){
				delimiter = ",";
			}
            if (open<10.0){
            	 String openStr	= linea.split(";")[1].replace(delimiter, "");
 	            String highStr 	= linea.split(";")[2].replace(delimiter, "");
 	            String lowStr 	= linea.split(";")[3].replace(delimiter, "");
 	            String closeStr = linea.split(";")[4].replace(delimiter, "");
 	            
 	            openStr = QuoteShort.fill5(openStr);
 	            highStr = QuoteShort.fill5(highStr);
 	            lowStr = QuoteShort.fill5(lowStr);
 	            closeStr =QuoteShort.fill5(closeStr);
 	           
 	            try{
 		            open5	= Integer.valueOf(openStr);
 		            high5 	= Integer.valueOf(highStr);
 		            low5 	= Integer.valueOf(lowStr);
 		            close5 	= Integer.valueOf(closeStr);
	            }catch(Exception e){
	            	System.out.println("error: "+linea+" || "+closeStr+" errro="+e.getMessage());
	            }
            }else{
            	//System.out.println("A CONVERTIR: "+linea);
            		String[] valuesO = linea.split(";")[1].split(delimiter);
            		String[] valuesH  = linea.split(";")[2].split(delimiter);
            		String[] valuesL = linea.split(";")[3].split(delimiter);
            		String[] valuesC = linea.split(";")[4].split(delimiter);
            		if (delimiter=="."){
            			valuesO  = linea.split(";")[1].split("\\.");
            			valuesH = linea.split(";")[2].split("\\.");
            			valuesL  = linea.split(";")[3].split("\\.");
            			valuesC = linea.split(";")[4].split("\\.");
             		}
            		
            	open5 	= convert3(valuesO); 
            	high5 	= convert3(valuesH); 
            	low5	= convert3(valuesL); 
            	close5 	= convert3(valuesC); 
            }
      
            //System.out.println(linea.split(" ")[2].replace(".", "")+" "+open5);
		}
						
			QuoteShort fq = new QuoteShort();
						
			fq.setOpen5(open5);
			fq.setClose5(close5);
			fq.setHigh5(high5);
			fq.setLow5(low5);
			fq.setVol(vol);
			fq.setYear(year);
			fq.setMonth(month);
			fq.setDay(day);
			fq.setHh(hh);
			fq.setMm(mm);
			fq.setSs(ss);
			fq.setMaxMin(maxMin);
			
			//System.out.println(linea+ " || "+fq.toString());
			
			return fq;
		}
		
		private static QuoteBidAsk decodeQuoteBidAsk(String linea,DataProvider type){
			Calendar cal = null;
			double bid = 0;
			double ask = 0;
			
			if (type==DataProvider.TRUEFX){
	            cal = DateUtils.getTrueFxDate(linea.split(",")[1].trim());	
	            bid = Float.valueOf(linea.split(",")[2].trim());
	            ask = Float.valueOf(linea.split(",")[3].trim());
			}
						
			QuoteBidAsk fq = new QuoteBidAsk();
			
			fq.setCal(cal);
			fq.setBid(bid);
			fq.setAsk(ask);
			fq.setOpenBid(bid);
			fq.setOpenAsk(ask);
			fq.setHighBid(bid);
			fq.setHighAsk(ask);
			fq.setLowBid(bid);
			fq.setLowAsk(ask);
			fq.setCloseBid(bid);
			fq.setCloseAsk(ask);
			//System.out.println(DateUtils.datePrint(date)+" "+PrintUtils.getOHLC(fq));
			return fq;
		}
		
		private static TmaDiff decodeTmaDiff(String line){
			
			Date date = null;
			String 	dateStr     = line.split(" ")[0];
			String 	timeStr	    = line.split(" ")[1];
			Integer upperDiff	= Integer.valueOf(line.split(" ")[2].trim());
			Integer lowerDiff	= Integer.valueOf(line.split(" ")[3].trim());
			
			Integer day 	= Integer.valueOf(dateStr.substring(0,2));
			Integer month	= Integer.valueOf(dateStr.substring(3,5))-1;
			Integer year= Integer.valueOf(dateStr.substring(6,10));
			Integer hour 	= Integer.valueOf(timeStr.substring(0,2));
	        Integer min 	= Integer.valueOf(timeStr.substring(3,5));
	        Integer ss 	= Integer.valueOf(timeStr.substring(6,8));
	                
	        Calendar cal = Calendar.getInstance();
	        cal.set(year, month, day, hour, min,ss);
	        
	        TmaDiff tmaDiff = new TmaDiff();
	        tmaDiff.setCal(cal);
			tmaDiff.setDiffUp(upperDiff);
			tmaDiff.setDiffDown(lowerDiff);
			
			return tmaDiff;
		}
		
		private static StrategyTrade decodeStrategyResultTrade(String line){
			
			String 	dateStr     = line.split(" ")[0];
			String 	timeStr	    = line.split(" ")[1];
			String 	dateStr2    = line.split(" ")[2];
			String 	timeStr2	= line.split(" ")[3];
			TradeType tradeType = TradeType.valueOf(line.split(" ")[4].trim());
			double entry		= Double.valueOf(line.split(" ")[5].trim());
			double tp			= Double.valueOf(line.split(" ")[7].trim());
			double sl			= Double.valueOf(line.split(" ")[7].trim());
			boolean win			= Boolean.valueOf(line.split(" ")[8].trim());
			
			Integer day 	= Integer.valueOf(dateStr.substring(8,10));
			Integer month	= Integer.valueOf(dateStr.substring(5,7))-1;
			Integer year	= Integer.valueOf(dateStr.substring(0,4));
			Integer hour 	= Integer.valueOf(timeStr.substring(0,2));
	        Integer min 	= Integer.valueOf(timeStr.substring(3,5));
	        Integer ss 		= Integer.valueOf(timeStr.substring(6,8));
	        
	        Integer day2 	= Integer.valueOf(dateStr2.substring(8,10));
			Integer month2	= Integer.valueOf(dateStr2.substring(5,7))-1;
			Integer year2	= Integer.valueOf(dateStr2.substring(0,4));
			Integer hour2 	= Integer.valueOf(timeStr2.substring(0,2));
	        Integer min2 	= Integer.valueOf(timeStr2.substring(3,5));
	        Integer ss2 		= Integer.valueOf(timeStr2.substring(6,8));
	                
	        Calendar cal = Calendar.getInstance();
	        cal.set(year, month, day, hour, min,ss);
	        Calendar cal2 = Calendar.getInstance();
	        cal2.set(year2, month2, day2, hour2, min2,ss2);
	        
	        StrategyTrade trade = new StrategyTrade();
	        trade.setOpenCal(cal);
	        trade.setCloseCal(cal2);
	        trade.setTradeType(tradeType);
	        trade.setEntry(entry);
	        trade.setSl(sl);
	        trade.setTp(tp);
	        trade.setWin(win);
	   
			return trade;
		}
		
		public static ArrayList<Quote> retrieveData(String fileName,int type){
			ArrayList<Quote> data = new  ArrayList<Quote>();
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
		        while((line=br.readLine())!=null){
		        	if (i>0){
		        		 Quote q = decodeQuote(line,type);     	
		        		 data.add(q);
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
		
		/**
		 * Devuelve los ficheros que hay en directory
		 * @param directory
		 * @return
		 */
		public static ArrayList<String> getFileNames2(String path){
			
			ArrayList<String> files = new ArrayList<String>();
			 
			File folder = new File(path);
			File[] listOfFiles = folder.listFiles(); 
			 
			for (int i = 0; i < listOfFiles.length; i++) {
				if (listOfFiles[i].isFile()){
					String file = listOfFiles[i].getName();
					files.add(file);
			     }
			}
			return files;
		}
		
		public static ArrayList<Quote> retrieveData(String fileName,DataProvider providerType,int startLine){
			ArrayList<Quote> data = new  ArrayList<Quote>();
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
		        while((line=br.readLine())!=null){
		        	if (i>=startLine){
		        		 Quote q = decodeQuote(line,providerType);     	
		        		 data.add(q);
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
    
		public static ArrayList<Tick> retrieveTicks(String fileName,DataProvider dataProvider,int thr){
			ArrayList<Tick> data = new  ArrayList<Tick>();
			File archivo = null;
		    FileReader fr = null;
		    BufferedReader br = null;
		    
		    int startLine = 1;
		    if (dataProvider == DataProvider.PEPPERSTONE_FOREX)
		    	startLine = 0;

		    try {
		    	// Apertura del fichero y creacion de BufferedReader para poder
		        // hacer una lectura comoda (disponer del metodo readLine()).
		        archivo = new File (fileName);
		        fr = new FileReader (archivo);
		        br = new BufferedReader(fr);

		        // Lectura del fichero
		        String line;
		        int i=0;
		        int count=0;
		        int max = -1;
		        while((line=br.readLine())!=null){
		        	if (i>=startLine){
		        		Tick t = null;
		        		if (dataProvider == DataProvider.DUKASCOPY_FOREX)
		        			t = decodeTickDukas(line);     	
		        		if (dataProvider == DataProvider.PEPPERSTONE_FOREX)
		        			t = decodeTickPepper(line,thr); 
		        		//System.out.println("count: "+count++);
		        		if (t!=null){
		        			int spread = TradingUtils.getPipsDiff5(t.getAsk(), t.getBid());
		        			if (spread>100){
		        				max = spread;
		        				System.out.println(t.toString()+" "+spread);
		        			}
		        			
		        		}
		        		//data.add(t);
		        		//t = null;
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
    
		public static ArrayList<QuoteShort> retrieveDataShort(ArrayList<File>files,DataProvider providerType){
			ArrayList<QuoteShort> data = new  ArrayList<QuoteShort>();
			FileReader fr = null;
		    BufferedReader br = null;
		    Calendar cal1 = Calendar.getInstance();
		    Calendar cal2 = Calendar.getInstance();
		    try {
				Sizeof.runGC();
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		    long before = Sizeof.usedMemory();
			for (int i=0;i<files.size();i++){
				
				File f = files.get(i);
				if (!f.exists()) continue;
				//System.out.println("File: "+f.getName());
				try {
					Sizeof.runGC();
					
			    	// Apertura del fichero y creacion de BufferedReader para poder
			        // hacer una lectura comoda (disponer del metodo readLine()).
			        fr = new FileReader (f);
			        br = new BufferedReader(fr);
			        // Lectura del fichero
			        String line;
			        int j=0;
			        QuoteShort lastQuote  = null;
			        while((line=br.readLine())!=null){
			        	if (j>0){
			        		//System.out.println(line);
			        		 QuoteShort q = decodeQuoteShort(line,providerType);     	
			        		 if (lastQuote!=null){
			        			if (QuoteShort.equalValues(lastQuote,q)){
			        				//System.out.println("NO SE AÑADE VALORES IGUALES");
			        			//}else if (DateUtils.isGreater(cal1,cal2,q,lastQuote)!=1){
			        				//System.out.println("NO SE AÑADE FECHA IGUAL O ANTERIOR");
			        			}else{
			        				data.add(q);
			        				lastQuote = q;
			        				//System.out.println(q.toString());
			        			}
			        		 }else{
			        			 data.add(q);
			        			 lastQuote = q;
			        			 System.out.println(q.toString());
			        		 }
			        	}
			        	j++;
			        }    
			        Sizeof.runGC ();
					long after = Sizeof.usedMemory();
					double KB = (after-before)/1024;
					double MB = KB/1024;
					System.out.println(after-before+" bytes "+KB+" KB "+MB+" MB "+" quotes: "+data.size());
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
				
			}
			
			return data;
		}
		
		public static void readSpreads(String fileName,int y1,int y2,HashMap<Integer,ArrayList<Double>> spreads) {
			spreads.clear();
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
		        QuoteShort lastQ = null;
		        while((line=br.readLine())!=null){		        
		        	String[] values0 = line.split(" ");
		        	//System.out.println(values0[0]+" "+values0.length+" line: "+line);
		        	int h = Integer.valueOf(values0[0].trim());
		        			        	
		        	String yearsStr = values0[1].trim();
		        	String[] values1 = yearsStr.split(" ");
		        	int yearActual = y1;
		        	for (int j=2;j<values0.length;j++) {//para cada año		
		        		yearActual = y1+j-2;
		        		if (!spreads.containsKey(yearActual)) {
		        			spreads.put(yearActual, new ArrayList<Double>());
		        			for (int r=0;r<=23;r++) spreads.get(yearActual).add(0.0);
		        		}
		        		ArrayList<Double> arr = spreads.get(yearActual);
		        		
		        		String str = values0[j];
		        		Double vald = Integer.valueOf(str.replace(".", ""))/100.0;
		        		//System.out.println(yearActual+" | h="+h+" | "+vald);
		        		arr.set(h, vald);		        		
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
		}
		
		public static void readSpreadsMinutes(String fileName,int y1,int y2,HashMap<Integer,ArrayList<Double>> spreads) {
			spreads.clear();
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
		        QuoteShort lastQ = null;
		        while((line=br.readLine())!=null){		        
		        	String[] values0 = line.split(" ");
		        	//System.out.println(values0[0]+" "+values0.length+" line: "+line);
		        	//int h = Integer.valueOf(values0[0].trim());
		        	int hmin = i;
		        			        	
		        	String yearsStr = values0[1].trim();
		        	String[] values1 = yearsStr.split(" ");
		        	int yearActual = y1;
		        	for (int j=2;j<values0.length;j++) {//para cada año		
		        		yearActual = y1+j-2;
		        		if (!spreads.containsKey(yearActual)) {
		        			spreads.put(yearActual, new ArrayList<Double>());
		        			for (int r=0;r<=23*12+11;r++) spreads.get(yearActual).add(0.0);
		        		}
		        		ArrayList<Double> arr = spreads.get(yearActual);
		        		
		        		String str = values0[j];
		        		Double vald = Integer.valueOf(str.replace(".", ""))/100.0;
		        		//System.out.println(yearActual+" | h="+h+" | "+vald);
		        		try{
		        			arr.set(hmin, vald);	
		        		}catch (Exception e2){ 
				            System.out.println("error: "+line+" "+e2.getMessage());
				         }	
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
		}
		
		public static ArrayList<QuoteShort> retrieveDataShort5m(String fileName,DataProvider providerType){
			ArrayList<QuoteShort> data = new  ArrayList<QuoteShort>();
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
		        QuoteShort lastQ = null;
		        while((line=br.readLine())!=null){
		        	if (i>0){
		        		 QuoteShort q = decodeQuoteShort(line,providerType); 
		        		 QuoteShort.getCalendar(cal, q);
		        		 int s = cal.get(Calendar.SECOND); 
		        		 if (s%5==0){
		        			 //System.out.println("add1: "+q.toString()+" "+q.getOpen()+" "+q.getOpen5());
		        			 data.add(q);
		        		 }else if (lastQ==null || !QuoteShort.isSame(q,lastQ)){
		        			 //System.out.println("add2: "+q.toString()+" "+q.getOpen()+" "+q.getOpen5());
		        			 data.add(q);
		        		 }
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
		
		public static ArrayList<QuoteShort> retrieveDataShort(String fileName,DataProvider providerType){
			ArrayList<QuoteShort> data = new  ArrayList<QuoteShort>();
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
		        while((line=br.readLine())!=null){
		        	if (i>0){
		        		 QuoteShort q = decodeQuoteShort(line,providerType);     	
		        		 data.add(q);
		        		 //System.out.println(q.toString());
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
		
		public static ArrayList<Quote> retrieveData(String fileName,DataProvider providerType){
			ArrayList<Quote> data = new  ArrayList<Quote>();
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
		        while((line=br.readLine())!=null){
		        	if (i>0){
		        		 Quote q = decodeQuote(line,providerType);     	
		        		 data.add(q);
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
		
		public static ArrayList<QuoteShort> retrieveDataDOW(String fileName,DataProvider providerType){
			ArrayList<QuoteShort> data = new  ArrayList<QuoteShort>();
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
		        while((line=br.readLine())!=null){
		        	if (i>0){
		        		if (line.trim().length()>0){
		        		 QuoteShort q = decodeQuoteShort(line,providerType);     	
		        		 data.add(q);
		        		}
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
		
		public static ArrayList<QuoteShort> retrieveDataESNQ(String fileName,DataProvider providerType,int startLine){
			ArrayList<QuoteShort> data = new  ArrayList<QuoteShort>();
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
		        while((line=br.readLine())!=null){
		        	if (i>= startLine){
		        		 QuoteShort q = decodeQuoteShort(line,providerType);     	
		        		 data.add(q);
		        		 //System.out.println(q.toString()+" "+q.getVol());
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
		
		public static ArrayList<QuoteBidAsk> retrieveDataBidAsk(String fileName,DataProvider providerType){
			ArrayList<QuoteBidAsk> data = new  ArrayList<QuoteBidAsk>();
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
		        while((line=br.readLine())!=null){
		        	if (i>0){
		        		QuoteBidAsk q = decodeQuoteBidAsk(line,providerType);   
		        		System.out.println(i);
		        		data.add(q);
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
		
		public static ArrayList<QuoteBidAsk> retrieveDataBidAsk1m(String fileName,DataProvider providerType){
			ArrayList<QuoteBidAsk> data = new  ArrayList<QuoteBidAsk>();
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
		        QuoteBidAsk qNew = null;
		        int lastMin = -1;
		        int min = -2;
		        int actualMin = 0;
		        boolean first = true;
		        Calendar cal = Calendar.getInstance();
		        while((line=br.readLine())!=null){
		        	if (i>0){
		        		QuoteBidAsk q = decodeQuoteBidAsk(line,providerType);   
		        		cal.setTimeInMillis(q.getCal().getTimeInMillis());
		        		if (data.size()>1 && data.get(data.size()-1).getCal().getTimeInMillis()>=cal.getTimeInMillis()){
        					System.out.println("DATE ANTERIOR NO SE METE");
        					continue;
        				}
		        		min = cal.get(Calendar.MINUTE);//leemos el minuto
		        		if (first && min!=0) continue;
		        		first = false;
		        		if (min!=lastMin){
		        			if (qNew!=null){//añadimos una nueva		        				
		        				data.add(qNew);
		                        //System.out.println("QUote added: "+qNew.toStringBid());
		                        qNew = null;
		                    }
		                    qNew = new QuoteBidAsk();
		                    qNew.copy(q);
		                    qNew.setOpenBid(q.getBid());
		                    qNew.setOpenAsk(q.getAsk());
		                    qNew.setHighBid(q.getBid());
		                    qNew.setHighAsk(q.getAsk());
		                    qNew.setLowBid(q.getBid());
		                    qNew.setLowAsk(q.getAsk());
		                    qNew.setCloseBid(q.getBid());
		                    qNew.setCloseAsk(q.getAsk());
		                    lastMin = min;
		        		}else{
		                    if (q.getBid()>qNew.getHighBid()){
		                        qNew.setHighBid(q.getBid());
		                        qNew.setHighAsk(q.getAsk());
		                    }
		                    if (q.getBid()<qNew.getLowBid()){
		                        qNew.setLowBid(q.getBid());
		                        qNew.setLowAsk(q.getAsk());
		                    }
		                    qNew.setCloseBid(q.getBid());
		                    qNew.setCloseAsk(q.getAsk());
		                } 
		        	}
		        	i++;
		        }  
		        if (min!=lastMin){//insertamos
	                if (qNew!=null){//añadimos una nueva
	                	data.add(qNew);
	                    //System.out.println("QUote added: "+DateUtils.datePrint(qNew.getDate())+" "+PrintUtils.Print(qNew));
	                    qNew = null;
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
		
		/**
		 * Devuelve los ficheros que hay en directory
		 * @param directory
		 * @return
		 */
		public static ArrayList<String> getFileNames(String path){
			
			ArrayList<String> files = new ArrayList<String>();
			 
			File folder = new File(path);
			File[] listOfFiles = folder.listFiles(); 
			 
			for (int i = 0; i < listOfFiles.length; i++) {
				if (listOfFiles[i].isFile()){
					String file = listOfFiles[i].getName();
					files.add(file);
			     }
			}
			return files;
		}
		
		/**
		 * Devuelve las quotes en un fichero de stocks de Pitrading
		 * @param fileName
		 * @return
		 */
		public  static ArrayList<Quote> retrievePiTradingStocks(String fileName){
			ArrayList<Quote> data = new  ArrayList<Quote>();
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
		        while((line=br.readLine())!=null){
		        	if (i>0){
		        		//System.out.println(line);
		        		 Quote q = decodeQuote(line,2);     	
		        		 data.add(q);
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

		public static void retrieveData(ArrayList<Quote> data, String fileName,
				DataProvider providerType, int startLine) {
			// TODO Auto-generated method stub
			if (data==null)
				data = new  ArrayList<Quote>();
			data.clear();
			
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
		        while((line=br.readLine())!=null){
		        	if (i>=startLine){
		        		 Quote q = decodeQuote(line,providerType);     	
		        		 data.add(q);
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
		}
		
		public static ArrayList<Quote> retrieveData2(String fileName,
				DataProvider providerType, int startLine) {
			// TODO Auto-generated method stub
			ArrayList<Quote> data = new  ArrayList<Quote>();
			
			
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
		        while((line=br.readLine())!=null){
		        	if (i>=startLine){
		        		 Quote q = decodeQuote(line,providerType);     	
		        		 data.add(q);
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
		
		public static ArrayList<NewsItem> loadNews(String fileName){
			
			ArrayList<NewsItem> news = new ArrayList<NewsItem>();
			
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
		        while((line=br.readLine())!=null){
		        	if (i>=0){
		        		 NewsItem item = decodeNewsItem(line);     	
		        		 news.add(item);
		        		 System.out.println(item.toString());
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
			
		    return news;
		}
		
		public static ArrayList<Range> loadRanges(String fileName){
			
			ArrayList<Range> ranges = new ArrayList<Range>();
			
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
		        while((line=br.readLine())!=null){
		        	if (i>=0){
		        		 Range range = decodeRangeItem(line);     	
		        		 ranges.add(range);
		        		 //System.out.println(range.toString());
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
			
		    return ranges;
		}

		private static Range decodeRangeItem(String line) {
			// TODO Auto-generated method stub
			Range item = new Range();
			
			String 	dateStr = line.split(",")[0].trim().split(" ")[0];
			String 	timeStr	= line.split(",")[0].trim().split(" ")[1];
			Integer range	= Integer.valueOf(line.split(",")[1].trim());
			
			
			Integer day 	= Integer.valueOf(dateStr.substring(0,2));
			Integer month	= Integer.valueOf(dateStr.substring(3,5));
			Integer year= Integer.valueOf(dateStr.substring(6,10));
			Integer hour 	= Integer.valueOf(timeStr.substring(0,2));
	        Integer min 	= Integer.valueOf(timeStr.substring(3,5));
	        Integer ss 	= Integer.valueOf(timeStr.substring(6,8));
	                
	        Calendar cal = Calendar.getInstance();
	        cal.set(year, month, day, hour, min,ss);
	        
	        item.setDate(cal);
	        item.setRange(range);
	        			
			return item;
		}
		
		public static ArrayList<TmaDiff> retrieveTmaDiff(String fileName){
			ArrayList<TmaDiff> data = new  ArrayList<TmaDiff>();
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
		        while((line=br.readLine())!=null){
		        	if (i>=0){
		        		 TmaDiff q = decodeTmaDiff(line);     	
		        		 data.add(q);
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
		
		public static StrategyResult retrieveStrategyResult(String fileName){
			
			StrategyResult res = new StrategyResult ();
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
		        while((line=br.readLine())!=null){
		        	if (i==1){
		        		int tp			= Integer.valueOf(line.split(",")[0].trim());
		    			int sl			= Integer.valueOf(line.split(",")[1].trim());
		    			int totalTrades	= Integer.valueOf(line.split(",")[2].trim());
		    			double winsPer	= Double.valueOf(line.split(",")[3].trim());	
		    			int bestTrack 	= Integer.valueOf(line.split(",")[4].trim());
		    			res.setTp(tp);
		    			res.setSl(sl);
		    			res.setTotalTrades(totalTrades);
		    			res.setWinsPer(winsPer);
		    			res.setBestTrack(bestTrack);
		    			//System.out.println(tp+","+sl+","+totalTrades+" "+PrintUtils.Print2dec(winsPer, false)
		    			//		+","+bestTrack);
		        	}
		        	if (i>=3){
		        		//System.out.println(line);
		        		StrategyTrade q = decodeStrategyResultTrade(line);     	
		        		res.getTrades().add(q);
		        		//System.out.println(q.toString());
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
		    return res;
		}

		
	
}
