package br.com.ontoforall.owlapi.model;

import org.json.JSONObject;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;

public class Manager {

	public OWLOntology LoadOWL() throws OWLOntologyCreationException {
		OWLOntologyManager m = OWLManager.createOWLOntologyManager();
		OWLOntology o = m.loadOntologyFromOntologyDocument( IRI.create("https://protege.stanford.edu/ontologies/pizza/pizza.owl") );
//		int axiomsLenght = o.getAxiomCount();
		
		return o;
	}
	
	public String getClasses() throws OWLOntologyCreationException {
		
		JSONObject lista = new JSONObject();
		
		OWLOntology o = LoadOWL();
		o.classesInSignature().forEach(classe -> {
			lista.put(classe.toString(), classe.toString());
			System.out.println(classe);
		});
		String resp = lista.toString();
		
		return resp;
	}
}

//https://stackoverflow.com/questions/46619937/get-subclasses-of-a-class-owlapi

