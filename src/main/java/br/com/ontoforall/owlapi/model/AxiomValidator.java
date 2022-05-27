package br.com.ontoforall.owlapi.model;

import org.json.JSONArray;
import org.json.JSONObject;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.expression.OWLEntityChecker;
import org.semanticweb.owlapi.expression.ShortFormEntityChecker;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.util.CachingBidirectionalShortFormProvider;
import org.semanticweb.owlapi.util.SimpleShortFormProvider;
import org.semanticweb.owlapi.util.mansyntax.ManchesterOWLSyntaxParser;

public class AxiomValidator {

	private String id;
	private JSONArray classes;
	private JSONArray propriedades;
	private JSONArray axiomas;
	
	public AxiomValidator(JSONObject ontologia) {
		this.id = ontologia.getString("id") + "/";
		ontologia.getString("outformat");
		this.classes = ontologia.getJSONArray("ontoclass");
		this.propriedades = ontologia.getJSONArray("ontoproperties");
		this.axiomas = ontologia.getJSONArray("ontoaxioms");
	}

	class Provider extends CachingBidirectionalShortFormProvider {

		private SimpleShortFormProvider provider = new SimpleShortFormProvider();

		@Override
		protected String generateShortForm(OWLEntity entity) {
			return provider.getShortForm(entity);
		}
	}
	
	private Provider carregaProvider(){
		Provider shortFormProvider = new Provider();
		OWLDataFactory df = OWLManager.getOWLDataFactory();

		// Carrega as Classes
		for (int i = 0; i < this.classes.length(); i++)
		shortFormProvider.add(df.getOWLClass(IRI.create(this.id + this.classes.get(i))));

		//Carrega as Propriedades
		for (int i = 0; i < this.propriedades.length(); i++)
			shortFormProvider.add(df.getOWLDataProperty(IRI.create(this.id + this.propriedades.get(i))));

		return shortFormProvider;
	}
	
	public String validAxioms() {
		Provider shortFormProvider = carregaProvider();
		OWLEntityChecker entityChecker = new ShortFormEntityChecker(shortFormProvider);
		OWLDataFactory df = OWLManager.getOWLDataFactory();
		
		/**
		 * Trabalha se as classes
		 */
		for (int i = 0; i < this.classes.length(); i++) {
			shortFormProvider.add(df.getOWLClass(IRI.create("https://onto4alleditor.com/pt/ID/" + this.classes.get(i))));
		}

		/**
		 * Trabalha se os axiomas 
		 */
		ManchesterOWLSyntaxParser parser = OWLManager.createManchesterParser();
		parser.setOWLEntityChecker(entityChecker);
		
		try {
			for (int i = 0; i < this.axiomas.length(); i++) {
				parser.setStringToParse(this.axiomas.getString(i));
				parser.parseAxiom();
			}
			return Boolean.toString(true);
		} catch (Exception e) {
			System.out.println(e.toString());
			return Boolean.toString(false);
		}
	}
}
