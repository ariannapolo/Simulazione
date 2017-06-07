package it.polito.tdp.seriea.model;

public class Results {
	
	private Team teamH;
	private Team teamA;
	private String res;
	public Results(Team teamH, Team teamA, String res) {
		super();
		this.teamH = teamH;
		this.teamA = teamA;
		this.res = res;
	}
	
	
	public Team getTeamH() {
		return teamH;
	}
	public Team getTeamA() {
		return teamA;
	}
	public String getRes() {
		return res;
	}
	
	

}
