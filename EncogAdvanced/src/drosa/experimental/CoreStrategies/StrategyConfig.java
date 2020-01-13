package drosa.experimental.CoreStrategies;

import drosa.utils.PrintUtils;

public class StrategyConfig {
	
	boolean enabled = false;
	int hour;
	int thr;
	int tp;
	int sl;
	int barsBack;
	int maxBars;
	double tpf;
	double slf;
	double risk;
	
	public StrategyConfig(){
		
	}
	
	public StrategyConfig(StrategyConfig aConfig){
		this.enabled	= aConfig.enabled;
		this.hour 		= aConfig.hour;
		this.thr 		= aConfig.thr;
		this.tp 		= aConfig.tp;
		this.sl 		= aConfig.sl;
		this.barsBack 	= aConfig.barsBack;
		this.maxBars 	= aConfig.maxBars;
		this.tpf		= aConfig.tpf;
		this.slf		= aConfig.slf;
		this.risk	    = aConfig.risk;
	}
	
	public void copy (StrategyConfig aConfig){
		this.enabled	= aConfig.enabled;
		this.hour 		= aConfig.hour;
		this.thr 		= aConfig.thr;
		this.tp 		= aConfig.tp;
		this.sl 		= aConfig.sl;
		this.barsBack 	= aConfig.barsBack;
		this.maxBars 	= aConfig.maxBars;
		this.tpf		= aConfig.tpf;
		this.slf		= aConfig.slf;
		this.risk	    = aConfig.risk;
	}
	
	public void setParams(int h,int thr,int tp,int sl,int risk,boolean enabled){
		this.hour = h;
		this.thr = thr;
		this.tp = tp;
		this.sl = sl;
		this.enabled = enabled;
		this.risk = risk*1.0/10;
	}
	public void setParams(int h,int thr,int tp,int sl,int maxBars,int risk,boolean enabled){
		this.hour = h;
		this.thr = thr;
		this.tp = tp;
		this.sl = sl;
		this.maxBars = maxBars;
		this.enabled = enabled;
		this.risk = risk*1.0/10;
	}
	public void setParams(int h,int thr,int tp,int sl,
			int maxBars,int barsBack,int risk,
			boolean enabled){
		this.hour = h;
		this.thr = thr;
		this.tp = tp;
		this.sl = sl;
		this.maxBars = maxBars;
		this.barsBack = barsBack;
		this.enabled = enabled;		
		this.risk = risk*1.0/10;
	}
	public void setParams(int h,int thr,double tpf,double slf,int maxBars,
			int risk,
			int barsBack,boolean enabled){
		this.hour = h;
		this.thr = thr;
		this.tpf = tpf;
		this.slf = slf;
		this.maxBars = maxBars;
		this.barsBack = barsBack;
		this.enabled = enabled;	
		this.risk = risk*1.0/10;
	}
		
	
	public double getTpf() {
		return tpf;
	}

	public void setTpf(double tpf) {
		this.tpf = tpf;
	}

	public double getSlf() {
		return slf;
	}

	public void setSlf(double slf) {
		this.slf = slf;
	}

	public int getMaxBars() {
		return maxBars;
	}
	public void setMaxBars(int maxBars) {
		this.maxBars = maxBars;
	}
	public int getHour() {
		return hour;
	}
	public void setHour(int hour) {
		this.hour = hour;
	}
	public int getThr() {
		return thr;
	}
	public void setThr(int thr) {
		this.thr = thr;
	}
	public int getTp() {
		return tp;
	}
	public void setTp(int tp) {
		this.tp = tp;
	}
	public int getSl() {
		return sl;
	}
	public void setSl(int sl) {
		this.sl = sl;
	}
	public boolean isEnabled() {
		return enabled;
	}
	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}
	public int getBarsBack() {
		return barsBack;
	}
	public void setBarsBack(int barsBack) {
		this.barsBack = barsBack;
	}
	
	
			
	public double getRisk() {
		return risk;
	}

	public void setRisk(double risk) {
		this.risk = risk;
	}

	public String toString(){
		return this.hour + " " + this.enabled
				 + " " + this.thr 
		 + " " + this.tp 
		 + " " + this.sl		 
		 + " " + this.maxBars
		 + " " + this.barsBack
		 + " " + PrintUtils.Print2dec(this.risk, false)
		 ;
	}
	
	public String toStringf(){
		return this.hour + " " + this.enabled
				 + " " + this.thr 
		 + " " + PrintUtils.Print2dec(tpf, false)
		 + " " + PrintUtils.Print2dec(this.slf, false)
		 + " " + this.barsBack 
		 + " " + this.maxBars
		 + " " + PrintUtils.Print2dec(this.risk, false)
		 ;
	}

	public void multiplyBars(int factor) {
		this.barsBack *= factor;
		this.maxBars *=factor;
		this.thr *= factor;		
	}
}
