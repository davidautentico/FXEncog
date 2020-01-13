package drosa.apuestas.year2019.indabet;

public class MatchInda {
	int year = 0;
	int day = 0;
	String monthStr = "";
	String season = "";
	String matchType="";
	
	String homeTeam="";
	String awayTeam = "";
	int homeGoals = 0;
	int awayGoals = 0;
	double homeOdds1 = 0;
	double awayOdds1 = 0;
	double homeOdds2 = 0;
	double awayOdds2 = 0;
	
	
	public int getYear() {
		return year;
	}
	public void setYear(int year) {
		this.year = year;
	}
	public int getDay() {
		return day;
	}
	public void setDay(int day) {
		this.day = day;
	}
	public String getMonthStr() {
		return monthStr;
	}
	public void setMonthStr(String monthStr) {
		this.monthStr = monthStr;
	}
	public String getSeason() {
		return season;
	}
	public void setSeason(String season) {
		this.season = season;
	}
	public String getMatchType() {
		return matchType;
	}
	public void setMatchType(String matchType) {
		this.matchType = matchType;
	}
	public String getHomeTeam() {
		return homeTeam;
	}
	public void setHomeTeam(String homeTeam) {
		this.homeTeam = homeTeam;
	}
	public String getAwayTeam() {
		return awayTeam;
	}
	public void setAwayTeam(String awayTeam) {
		this.awayTeam = awayTeam;
	}
	public int getHomeGoals() {
		return homeGoals;
	}
	public void setHomeGoals(int homeGoals) {
		this.homeGoals = homeGoals;
	}
	public int getAwayGoals() {
		return awayGoals;
	}
	public void setAwayGoals(int awayGoals) {
		this.awayGoals = awayGoals;
	}
	public double getHomeOdds1() {
		return homeOdds1;
	}
	public void setHomeOdds1(double homeOdds1) {
		this.homeOdds1 = homeOdds1;
	}
	public double getAwayOdds1() {
		return awayOdds1;
	}
	public void setAwayOdds1(double awayOdds1) {
		this.awayOdds1 = awayOdds1;
	}
	public double getHomeOdds2() {
		return homeOdds2;
	}
	public void setHomeOdds2(double homeOdds2) {
		this.homeOdds2 = homeOdds2;
	}
	public double getAwayOdds2() {
		return awayOdds2;
	}
	public void setAwayOdds2(double awayOdds2) {
		this.awayOdds2 = awayOdds2;
	}
	public void setParameters(int year, int day, String monthStr, String season, String matchType,
			String homeTeam, String awayTeam, int homeGoals, int awayGoals, 
			double homeOdds1, double awayOdds1,
			double homeOdds2, double awayOdds2) {
		// TODO Auto-generated method stub
		
		this.year = year;
		this.day = day;
		this.monthStr = monthStr;
		this.season = season;
		this.matchType=matchType;
		
		this.homeTeam=homeTeam;
		this.awayTeam = awayTeam;
		this.homeGoals = homeGoals;
		this.awayGoals = awayGoals;
		this.homeOdds1 = homeOdds1;
		this.awayOdds1 = awayOdds1;
		this.homeOdds2 = homeOdds2;
		this.awayOdds2 = awayOdds2;
		
		
		
	}
	
	
}
