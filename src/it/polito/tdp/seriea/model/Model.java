package it.polito.tdp.seriea.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jgrapht.Graphs;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleDirectedWeightedGraph;
import org.jgrapht.traverse.BreadthFirstIterator;

import it.polito.tdp.seriea.db.SerieADAO;

public class Model {
	
	private SerieADAO dao = new SerieADAO();
	private SimpleDirectedWeightedGraph<Team,DefaultWeightedEdge> graph;
	private Map<String,Team> teams = new HashMap<>();
	
	public Collection<Team> getTeam(){
		if(teams.isEmpty()){
			for(Team t : dao.listTeams())
				teams.put(t.getTeam(),t);
			
		}
		return teams.values();
		
	}

	
	public List<Season> getStagioni(){
		return dao.listSeasons();
		
	}


	public void creaGrafo(Season s) {
		graph = new SimpleDirectedWeightedGraph<>(DefaultWeightedEdge.class);
		this.getTeam();
//		Graphs.addAllVertices(graph, teams);
		List<Results> res = dao.getRisultati(s.getSeason(),teams);
		for(Results r : res){
			double peso;
			if(r.getRes().compareTo("H")==0){
				peso = 1;
			}else if(r.getRes().compareTo("A")==0){
				peso = -1;
			}else{
				peso = 0;
			}
			//System.out.println(r.getTeamA());
			Graphs.addEdgeWithVertices(graph, r.getTeamH(), r.getTeamA(), peso);
			
		}
	
		
	}
	
	public List<Team> calcolaClassifica(){
		 List<Team> classifica = new ArrayList<>();
		 for(Team t : teams.values()){
			 t.setPunteggio(0);
		 }
		
		for(DefaultWeightedEdge e : graph.edgeSet()){
			Team th = graph.getEdgeSource(e);
			Team ta = graph.getEdgeTarget(e);
			
			if(graph.getEdgeWeight(e)==1.0 && th!=null){
				//home ha vinto
				th.incrementaPunteggio(3);
			}else if(graph.getEdgeWeight(e)==0.0){
				//pareggio
				th.incrementaPunteggio(1);
				ta.incrementaPunteggio(1);
			}else{
				//home ha perso
				ta.incrementaPunteggio(3);
			}
			//System.out.println(graph.getEdgeSource(e)+" punteggio: "+graph.getEdgeSource(e).getPunteggio()+" "+graph.getEdgeTarget(e)+" punteggio: "+graph.getEdgeTarget(e).getPunteggio());
		}
		
		classifica.addAll(graph.vertexSet());
		Collections.sort(classifica);
		return classifica;
		
	}
	
	public List<Team> domino(Team team){
		List<Team> best = new ArrayList<Team>();
		//List<Team> percorso = new ArrayList<Team>();		
//		BreadthFirstIterator<Team,DefaultWeightedEdge> bfi= new BreadthFirstIterator<Team,DefaultWeightedEdge>(graph,team);
//		
//		while(bfi.hasNext()){
//			best.add(bfi.next());
//		}
		
		//recursive
		return best;
		
	}
	
	
	
	
	public static void main(String arg[]){
		Model m = new Model();
		m.creaGrafo(new Season(2003,"2002/2003"));
		
		System.out.println(m.teams);
		for(DefaultWeightedEdge e : m.graph.edgeSet()){
			System.out.println(m.graph.getEdgeSource(e)+"-"+m.graph.getEdgeTarget(e)+" peso: "+m.graph.getEdgeWeight(e));
		}
		
		for(Team t : m.calcolaClassifica()){
			System.out.println(t.getTeam()+" : "+t.getPunteggio()+"\n");
    	}
	
		System.out.println(m.domino(new Team("Roma")));
	}
	
	

}
