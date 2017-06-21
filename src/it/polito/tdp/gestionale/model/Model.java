package it.polito.tdp.gestionale.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
	private List <Corso> best;
	

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
				mappaStudenti.put(s.getMatricola(), s);   // Riempio la mappa di studenti
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
		// creo un array di interi in cui ogni cella rappresenta il numero di corsi frequentati da uno studente.
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
			statCorsi.set(ncorsi, counter);   // aggiorna il contenuto della cella in posizione ncorsi con una nuova variabile counter 
		}
		
		return statCorsi;	
	}
	
	// FUNZIONE RICORSIVA
	
	// METODO 1
	public List <Corso> findMinimalSet(){
		List<Corso> parziale = new ArrayList <Corso>();
		List<Corso> best = new ArrayList <Corso>();
		
		recursive(parziale, best); 
		return best;	
	}
	
	public void recursive(List <Corso> parziale, List <Corso> best){
		// Non serve una condizione di terminazione perche l'algoritmo ricorsvo terminera da se` dopo aver esplorato i rami
		// basandosi sulle condizioni che ho creato
		
	//	System.out.println(parziale);
		
		// Contro quale e` la soluzione migliore
		HashSet <Studente> hashSetStudenti = new HashSet <Studente>(this.getTuttiStudenti());
		for(Corso  corso : parziale){
			hashSetStudenti.removeAll(corso.getStudenti());
		}
		if( hashSetStudenti.isEmpty()){
			if(best.isEmpty()){
				best.addAll(parziale);
			}
			if(parziale.size() < best.size()){
				best.clear();
				best.addAll(parziale);
			}
		}
	
		for(Corso c : this.getTuttiCorsi()){
			// AGGIUNGO un corso a parziale SOLO SE:
			// 1) parziale e` vuoto
			// 2) il codice del corso e` maggiore del codice dell'ultimo corso inserito
			if(parziale.isEmpty() || c.compareTo(parziale.get(parziale.size()-1)) > 0){
				parziale.add(c);
				recursive(parziale,best);
				parziale.remove(c);		
			}
		}
	}
	
	// METODO 2
	public List<Corso> getBestInsieme(){
		List <Corso> parziale = new ArrayList <Corso>();
		List <Corso> best = new ArrayList <Corso> (this.getTuttiCorsi());;
		this.recursive2(parziale, best);
		return best;	
	}
	
	public void recursive2 (List <Corso> parziale, List <Corso> best){
		// Condizione di discriminazione
		// Controllo che contenga tutti gli studenti che frequentano almeno un corso
		if( this.contieneTutti(parziale) == true){
			if( best.isEmpty()){
				best.addAll(parziale);
			}
			if(parziale.size() < best.size()){
				best.clear();
				best.addAll(parziale);
			}
		}
		
		for(Corso c : this.getTuttiCorsi()){
			if( ! parziale.contains(c)){
				parziale.add(c);
				recursive( parziale, best);
				parziale.remove(c);
			}	
		}
		
	}

	private boolean contieneTutti(List <Corso> parziale) {
		List <Studente> contenuti = new ArrayList <Studente>();
		for( Corso c : parziale){
			for( Studente stud : c.getStudenti()){
				if(! contenuti.contains(stud)){
					contenuti.add(stud);
				}
			}
		}
		int frequentanti =0;
		int parziali = 0;
		for( Studente s : this.getTuttiStudenti()){
			if( Graphs.neighborListOf(grafo, s).size() > 0){
				frequentanti ++;
				if( contenuti.contains(s)){
					parziali ++;
				}
			}
		}
		if( frequentanti == parziali){
			return true;
		}
		return false;
	} 
	
	

}
