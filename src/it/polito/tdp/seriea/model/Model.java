package it.polito.tdp.seriea.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jgrapht.Graphs;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleDirectedWeightedGraph;
import org.jgrapht.traverse.BreadthFirstIterator;

import it.polito.tdp.seriea.db.SerieADAO;

public class Model {
	
	private SerieADAO dao = new SerieADAO();
	private SimpleDirectedWeightedGraph<Team,DefaultWeightedEdge> graph;
	private Map<String,Team> teams = new HashMap<>();
	private List<Team> best; 
	
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
	
	public List<Team> domino(){
		this.riduciGrafo(8);
		
		List<Team> domino = new ArrayList<Team>();
		
		for(Team t : graph.vertexSet()){
			best = new ArrayList<Team>();
			List<Team> parzialeTeam= new LinkedList<>();
			List<DefaultWeightedEdge> archi = new ArrayList<>();
			
			parzialeTeam.add(t);
			recursive(parzialeTeam, best , archi, t);
			System.out.println("Partenza:"+t+" domino "+best.size()+" "+best);
			if(best.size()>=domino.size()){
				domino.clear();
				domino.addAll(best);
			}
		}
		
		return domino;
		
	}
	
	
	private void recursive(List<Team> parziale, List<Team> best, List<DefaultWeightedEdge> archi, Team last) {
		if( parziale.size()>=best.size()){
			best.clear();
			best.addAll(parziale);
		}
		
		for(DefaultWeightedEdge e : graph.edgesOf(last)){
			if(graph.getEdgeWeight(e)==1  && graph.getEdgeSource(e).equals(last) && !archi.contains(e)){
				parziale.add(graph.getEdgeTarget(e));
				archi.add(e);
				recursive(parziale, best, archi, graph.getEdgeTarget(e));
				//System.out.println(archi);		
				archi.remove(archi.size()-1);
				parziale.remove(parziale.size()-1);					
			}
		}
	}

	private void riduciGrafo(int dim) {
		Set<Team> togliere = new HashSet<>() ;
		
		Iterator<Team> iter = graph.vertexSet().iterator() ;
		for(int i=0; i<graph.vertexSet().size()-dim; i++) {
			togliere.add(iter.next()) ;
		}
		graph.removeAllVertices(togliere) ;
		System.err.println("Attenzione: cancello dei vertici dal grafo");
		System.err.println("Vertici rimasti: "+graph.vertexSet().size()+"\n");
	}

	public static void main(String arg[]){
		Model m = new Model();
		m.creaGrafo(new Season(2003,"2002/2003"));
		
		System.out.println(m.teams);
		for(DefaultWeightedEdge e : m.graph.edgeSet()){
			System.out.println(m.graph.getEdgeSource(e)+"-"+m.graph.getEdgeTarget(e)+" peso: "+m.graph.getEdgeWeight(e));
		}
		
		for(Team t : m.calcolaClassifica()){
			System.out.println(t.getTeam()+" : "+t.getPunteggio());
    	}
	
		System.out.println(m.domino());
	}
	
	

}
