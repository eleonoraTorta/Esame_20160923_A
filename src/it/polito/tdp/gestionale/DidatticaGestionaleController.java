package it.polito.tdp.gestionale;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import it.polito.tdp.gestionale.model.Corso;
import it.polito.tdp.gestionale.model.Model;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

public class DidatticaGestionaleController {

	private Model model;

	@FXML
	private ResourceBundle resources;

	@FXML
	private URL location;

	@FXML
	private TextField txtMatricolaStudente;

	@FXML
	private TextArea txtResult;

	@FXML
	void doCorsiFrequentati(ActionEvent event) {
		txtResult.clear();
		model.generaGrafo();
		List <Integer> statistica = model.getStatCorsi();
		for( Integer i =0 ; i< statistica.size(); i++){
			txtResult.appendText( "Numero di studenti che hanno frequentato " +  i + " corsi: " + statistica.get(i) + "\n");
		}
	
	}
	
	@FXML
	void doVisualizzaCorsi(ActionEvent event) {
		txtResult.clear();
		model.generaGrafo();
		List <Corso> best = model.findMinimalSet();
		txtResult.appendText("Corsi in cui e` necessario fare un intervento per raggiungere tutti gli studenti che frequentano almeno un corso:\n");
		for( Corso c : best){
			txtResult.appendText(c.getCodins() + " " + c.getNome() + "\n");
		}
	}

	@FXML
	void initialize() {
		assert txtMatricolaStudente != null : "fx:id=\"txtMatricolaStudente\" was not injected: check your FXML file 'DidatticaGestionale.fxml'.";
		assert txtResult != null : "fx:id=\"txtResult\" was not injected: check your FXML file 'DidatticaGestionale.fxml'.";

	}

	public void setModel(Model model) {
		this.model = model;
	}

}
