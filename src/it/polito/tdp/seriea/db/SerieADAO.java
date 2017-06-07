package it.polito.tdp.seriea.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import it.polito.tdp.seriea.model.Results;
import it.polito.tdp.seriea.model.Season;
import it.polito.tdp.seriea.model.Team;

public class SerieADAO {
	
	public List<Season> listSeasons() {
		String sql = "SELECT season, description FROM seasons" ;
		
		List<Season> result = new ArrayList<>() ;
		
		Connection conn = DBConnect.getConnection() ;
		
		try {
			PreparedStatement st = conn.prepareStatement(sql) ;
			
			ResultSet res = st.executeQuery() ;
			
			while(res.next()) {
				result.add( new Season(res.getInt("season"), res.getString("description"))) ;
			}
			
			conn.close();
			return result ;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null ;
		}
	}
	
	public List<Team> listTeams() {
		String sql = "SELECT team FROM teams" ;
		
		List<Team> result = new ArrayList<>() ;
		
		Connection conn = DBConnect.getConnection() ;
		
		try {
			PreparedStatement st = conn.prepareStatement(sql) ;
			
			ResultSet res = st.executeQuery() ;
			
			while(res.next()) {
				result.add( new Team(res.getString("team"))) ;
			}
			
			conn.close();
			return result ;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null ;
		}
	}
	
	public List<Results> getRisultati(int season, Map<String,Team> team){
			String sql = "select HomeTeam, AwayTeam, FTR from matches where season = ?" ;
	
			List<Results> result = new ArrayList<>() ;
			
			Connection conn = DBConnect.getConnection() ;
			
			try {
				PreparedStatement st = conn.prepareStatement(sql) ;
				st.setInt(1, season);
				
				ResultSet res = st.executeQuery() ;
				
				while(res.next()) {
					result.add( new Results(team.get(res.getString("HomeTeam")), team.get(res.getString("AwayTeam")),res.getString("FTR"))) ;
				}
				
				conn.close();
				return result ;
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return null ;
			}
			
	}


}
