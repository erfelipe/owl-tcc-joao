package br.com.ontoforall.owlapi.model;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.expression.OWLEntityChecker;
import org.semanticweb.owlapi.expression.ShortFormEntityChecker;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.util.CachingBidirectionalShortFormProvider;
import org.semanticweb.owlapi.util.SimpleShortFormProvider;
import org.semanticweb.owlapi.util.mansyntax.ManchesterOWLSyntaxParser;

public class ElementosOWL {

	class Provider extends CachingBidirectionalShortFormProvider {

		private SimpleShortFormProvider provider = new SimpleShortFormProvider();

		@Override
		protected String generateShortForm(OWLEntity entity) {
			return provider.getShortForm(entity);
		}
	}

	public ElementosOWL() {

	}

	public JSONObject geraElementosTeste() {
		
		JSONObject objOWL = new JSONObject();
		
		List<String> classes = new ArrayList<String>();
		List<String> axiomas = new ArrayList<String>();
		
		classes.add("Pessoa");
		classes.add("Homem");
		classes.add("Mulher");
		
		axiomas.add("Homem subClassOf (Pessoa)");
		//axiomas.add("Mulher subClassOf (Pessoa)");
		
		objOWL.put("classes", classes);
		objOWL.put("axiomas", axiomas);
		
		String texto = objOWL.toString();
		System.out.println(texto);
		
		return objOWL;
	}
	
	public String validaOWL() {
		OWLDataFactory df = OWLManager.getOWLDataFactory();

		Provider shortFormProvider = new Provider();
		OWLEntityChecker entityChecker = new ShortFormEntityChecker(shortFormProvider);

		JSONObject owl = this.geraElementosTeste();

		/**
		 * Trabalha se as classes
		 */
		JSONArray classes = new JSONArray();
		classes = owl.getJSONArray("classes");

		for (int i = 0; i < classes.length(); i++) {
			shortFormProvider.add(df.getOWLClass(IRI.create("http://ontoforall.com.br/" + classes.get(i))));
		}

		/**
		 * Trabalha se os axiomas (duvida se posso passar varios axiomas ou apenas um de cada vez
		 */
		JSONArray axiomas = new JSONArray();
		axiomas = owl.getJSONArray("axiomas");
		ManchesterOWLSyntaxParser parser = OWLManager.createManchesterParser();
		parser.setOWLEntityChecker(entityChecker);

		for (int i = 0; i < axiomas.length(); i++) {
			parser.setStringToParse(axiomas.getString(i));
		}

		OWLAxiom axiom = parser.parseAxiom();

		return "Checagem " + axiom.toString();
	}
	
	
}
