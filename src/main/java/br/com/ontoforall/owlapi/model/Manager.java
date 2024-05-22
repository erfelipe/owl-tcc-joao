package br.com.ontoforall.owlapi.model;

import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;

public class Manager {

	public void LoadOWL() throws OWLOntologyCreationException {
		OWLOntologyManager m = create();
		
		OWLOntology o = m.loadOntologyFromOntologyDocument(new IRI("") );
		assertNotNull(o);
	}

}