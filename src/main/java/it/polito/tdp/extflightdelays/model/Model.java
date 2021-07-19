package it.polito.tdp.extflightdelays.model;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jgrapht.Graphs;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;
import org.jgrapht.traverse.BreadthFirstIterator;

import it.polito.tdp.extflightdelays.db.ExtFlightDelaysDAO;

public class Model {
	private SimpleWeightedGraph<Airport, DefaultWeightedEdge> grafo;
	private ExtFlightDelaysDAO dao;
	private Map<Integer, Airport> idMap;

	public Model() {
		dao = new ExtFlightDelaysDAO();
		idMap = new HashMap<Integer, Airport>();
		dao.loadAllAirports(idMap);
	}

	public void creaGrafo( int x) {
		grafo = new SimpleWeightedGraph<Airport, DefaultWeightedEdge>(DefaultWeightedEdge.class);
		 // Aggiungo i vertici  "filtrati"
		Graphs.addAllVertices(grafo, dao.getVertici(x, idMap));
		
		// Aggiungo gli archi
	   for(Rotta r: dao.getRotte(idMap)) {
		   // controllo se la rotta è relatica al grafo contiene i due aereoporti in questione
		   if(this.grafo.containsVertex(r.getA1()) && 
				   this.grafo.containsVertex(r.getA2())) {
			   DefaultWeightedEdge e = this.grafo.getEdge(r.getA1(), r.getA2());
			   if(e==null) {
				   Graphs.addEdgeWithVertices(grafo, r.getA1(), r.getA2(), r.getN());
			   } else {
				   double pesoVecchio = this.grafo.getEdgeWeight(e);
				   double pesoNuovo = pesoVecchio + r.getN();
				   this.grafo.setEdgeWeight(e,pesoNuovo);
				   
			   }
		   }
	   }
		System.out.println("Grafo creato");
		System.out.println("# Vertici : "+ grafo.vertexSet().size());
		System.out.println(" Archi : "+ grafo.edgeSet().size());
		
	}

	public Set<Airport> getVertici() {
		return this.grafo.vertexSet();
	}
	
	public List<Airport> trovaPercorso(Airport a1,Airport a2){
		 List<Airport> percorso = new LinkedList<>();
		
		 // visita in ampiezza
		 
		 BreadthFirstIterator<Airport, DefaultWeightedEdge> it =
				 new BreadthFirstIterator<>(grafo,a1);
		 it.addTraversalListener(new TraversalListener<Airport, DefaultWeightedEdge>());
		 
		 while(it.hasNext()) {
			 it.next();
		 }
		 return percorso;
	}
}
