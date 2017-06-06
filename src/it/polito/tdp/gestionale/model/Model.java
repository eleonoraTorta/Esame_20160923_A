package it.polito.tdp.gestionale.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jgrapht.Graphs;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleGraph;

import it.polito.tdp.gestionale.db.DidatticaDAO;

public class Model {

	private List<Corso> corsi;
	private List<Studente> studenti;
	private DidatticaDAO didatticaDAO;
	private SimpleGraph <Nodo, DefaultEdge> grafo;   // i vertici sono di tipo NODO
	private Map<Integer, Studente> mappaStudenti;
	

	public Model() {
		grafo = new SimpleGraph <Nodo, DefaultEdge> (DefaultEdge.class);
		didatticaDAO = new DidatticaDAO();
		mappaStudenti = new HashMap <Integer, Studente>();
	}
	
	public List <Studente> getTuttiStudenti(){
		if (studenti == null){
			studenti = didatticaDAO.getTuttiStudenti();
			
			getTuttiStudenti();    			// Verifico di avere richiamato il metodo
			for( Studente s : studenti){
				mappaStudenti.put(s.hashCode(), s);   // Riempio la mappa di studenti
			}
		}
		return studenti;	
	}
	
	public List <Corso> getTuttiCorsi(){
		if(corsi == null){
			corsi = didatticaDAO.getTuttiICorsi();
			
			this.getTuttiStudenti();   //Mi assicuro di averlo chiamato
			for (Corso c : corsi){
				didatticaDAO.setStudentiIscrittiAlCorso(c, mappaStudenti);  // riempio le liste di studenti che frequentano ogni corso
			}
		}
		return corsi;
		
	}
	public void generaGrafo(){
		
		studenti = getTuttiStudenti();
		System.out.println("Studenti #: " + studenti.size());
		
		corsi = getTuttiCorsi();
		System.out.println("Corsi #: " + corsi.size());
		
		//Aggiungiamo i nodi
		Graphs.addAllVertices(grafo, studenti);
		Graphs.addAllVertices(grafo, corsi);
		System.out.println("Numero vertici: " + grafo.vertexSet().size());
		
		//Aggiungiamo gli archi
		for(Corso c : corsi){
			for(Studente s : c.getStudenti()){
				grafo.addEdge(c, s);
			}
		}
		System.out.println("Numero archi: "+ grafo.edgeSet().size());
		System.out.println("Grafo creato!");
			
	}
	
	public List <Integer> getStatCorsi(){
		// creo un array di interi in cui ogni cella rappresenta il numero di corsi frequentati da uno studente
		// il numero di corsi e` dato dalla posizione della cella
		List <Integer> statCorsi = new ArrayList <Integer>();
		
		// Inizializzo la strutta dati dove salvare  le statistiche
		for( int i =0; i < corsi.size() +1 ; i++){
			statCorsi.add(0);	
		}
		
		// Aggiorno le statistiche
		for( Studente s : studenti){
			int ncorsi = Graphs.neighborListOf(grafo, s).size();  // ogni arco collegato ad uno studente rappresenta un corso frequentato 
			int counter = statCorsi.get(ncorsi);
			counter ++;
			statCorsi.set(ncorsi, counter);   // aggiorna la cella in posizione ncorsi con una nuova variabile counter 
		}
		
		return statCorsi;	
	}
}
