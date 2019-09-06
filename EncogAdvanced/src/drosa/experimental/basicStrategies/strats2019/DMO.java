package drosa.experimental.basicStrategies.strats2019;

import java.util.ArrayList;

import drosa.experimental.PositionShort;
import drosa.finances.QuoteShort;

public class DMO extends AlgoBasic  {

	@Override
	public void doManagePositions(ArrayList<QuoteShort> data, int i, ArrayList<PositionShort> positions) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void doEvaluateExits(ArrayList<QuoteShort> data, int i, ArrayList<PositionShort> positions,
			StratPerformance sp) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public int doEvaluateEntries(
			ArrayList<QuoteShort> data,
			ArrayList<Integer> maxMins,
			int i, ArrayList<PositionShort> positions,
			boolean canTrade, StratPerformance sp) {
		
		
		return 0;
	}

}
