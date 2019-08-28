package drosa.finance.utils;

import java.util.ArrayList;

import drosa.finance.classes.DAO;
import drosa.finance.classes.QuoteShort;
import drosa.finance.classes.TestLines;
import drosa.finance.types.DataProvider;

public class DataUtils {
	
	/**
	 * Leemos los datos y los convertimos a la clase financiera
	 * @param fileName
	 */
	public static ArrayList<QuoteShort> readData(String fileName) {
		
		ArrayList<QuoteShort> dataI 		= DAO.retrieveDataShort5m(fileName, DataProvider.DUKASCOPY_FOREX4);		
		//System.out.println(dataI.size());
		TestLines.calculateCalendarAdjustedSinside(dataI);			
		
		return TradingUtils.cleanWeekendDataS(dataI);  		
	}
	
}
