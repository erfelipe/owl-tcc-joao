package br.com.ontoforall.owlapi.model;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Collection;

import javax.ws.rs.core.Response.Status;

import org.json.JSONArray;
import org.json.JSONObject;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.OWLAnnotation;
import org.semanticweb.owlapi.model.OWLAnnotationAxiom;
import org.semanticweb.owlapi.model.OWLAnnotationProperty;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLDatatype;
import org.semanticweb.owlapi.model.OWLIndividualAxiom;
import org.semanticweb.owlapi.model.OWLLogicalAxiom;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLObjectPropertyAxiom;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;

public class OntologyImporter {

	public JSONObject readFromOWL(String ontologia) throws OWLOntologyCreationException {
		JSONObject json = new JSONObject();
		JSONArray error = new JSONArray();
		Status s = Status.OK;

		OWLOntologyManager manager = OWLManager.createOWLOntologyManager();

		InputStream targetStream = new ByteArrayInputStream(ontologia.getBytes());
		OWLOntology ontology = manager.loadOntologyFromOntologyDocument(targetStream);

		JSONArray classes = new JSONArray();
		JSONArray individuals = new JSONArray();
		JSONArray objectProperties = new JSONArray();

		// classes
		Collection<OWLClass> OWLClasses = ontology.getClassesInSignature();
		for (OWLClass owlClass : OWLClasses) {
			JSONObject classe = new JSONObject();
			JSONArray annotations = new JSONArray();

			classe.put("Name", owlClass.getIRI().getShortForm());

			for (OWLLogicalAxiom axiom : ontology.getAxioms(owlClass)) {

				Collection<OWLClass> owlClassAxion = axiom.getClassesInSignature();
				JSONArray jsonClass = new JSONArray();
				for (OWLClass owlObj : owlClassAxion) {
					jsonClass.put(owlObj.getIRI().getShortForm());
				}

				classe.put(axiom.getAxiomType().toString(), jsonClass);
			}

			// Por algum motivo não está retnrnando nada
			Collection<OWLAnnotationProperty> OWLAnnotationProperties = owlClass.getAnnotationPropertiesInSignature();
			for (OWLAnnotationProperty owlAnnotationsPropertie : OWLAnnotationProperties) {
				JSONObject annotation = new JSONObject();

				for (OWLAnnotationAxiom axiom : ontology.getAxioms(owlAnnotationsPropertie)) {
					Collection<OWLClass> owlClassAxion = axiom.getClassesInSignature();
					JSONArray jsonAnnotation = new JSONArray();
					for (OWLClass owlObj : owlClassAxion) {
						jsonAnnotation.put(owlObj.getIRI().getShortForm());
					}

					annotation.put(axiom.getAxiomType().toString(), jsonAnnotation);
				}

				annotations.put(annotation);
			}

			classe.put("Annotation", annotations);

			classes.put(classe);

		}

		// Individuals
		Collection<OWLNamedIndividual> OWLIndividuals = ontology.getIndividualsInSignature();
		for (OWLNamedIndividual owlInvidual : OWLIndividuals) {
			JSONObject individual = new JSONObject();
			JSONArray annotations = new JSONArray();

			individual.put("Name", owlInvidual.getIRI().getShortForm());

			for (OWLIndividualAxiom axiom : ontology.getAxioms(owlInvidual)) {
				JSONArray jsonAnnotation = new JSONArray();
				Collection<OWLNamedIndividual> owlClassAxion2 = axiom.getIndividualsInSignature();
				for (OWLNamedIndividual owlObj : owlClassAxion2) {
					jsonAnnotation.put(owlObj.getIRI().getShortForm());
				}
				
				individual.put(axiom.getAxiomType().toString(), jsonAnnotation);
			}

			// Por algum motivo não está retnrnando nada
			Collection<OWLAnnotationProperty> OWLAnnotationProperties = owlInvidual.getAnnotationPropertiesInSignature();
			for (OWLAnnotationProperty owlAnnotationsPropertie : OWLAnnotationProperties) {
				JSONObject annotation = new JSONObject();

				for (OWLAnnotationAxiom axiom : ontology.getAxioms(owlAnnotationsPropertie)) {
					Collection<OWLClass> owlClassAxion = axiom.getClassesInSignature();
					JSONArray jsonAnnotation = new JSONArray();
					for (OWLClass owlObj : owlClassAxion) {
						jsonAnnotation.put(owlObj.getIRI().getShortForm());
					}

					// Está retornando vazio, verificar
					annotation.put(axiom.getAxiomType().toString(), jsonAnnotation);
				}

				annotations.put(annotation);
			}

			individual.put("Annotation", annotations);
			
			individuals.put(individual);

		}
		
		//object properties
		Collection<OWLObjectProperty> OWLObjectProperties = ontology.getObjectPropertiesInSignature();
		for (OWLObjectProperty owlObjectPropertie : OWLObjectProperties) {
			JSONObject objectPropertie = new JSONObject();
			JSONArray annotations = new JSONArray();

			objectPropertie.put("Name", owlObjectPropertie.getIRI().getShortForm());

			for (OWLObjectPropertyAxiom axiom : ontology.getAxioms(owlObjectPropertie)) {
				JSONArray jsonAnnotation = new JSONArray();
				
				Collection<OWLObjectProperty> owlProperties = axiom.getObjectPropertiesInSignature();
				for (OWLObjectProperty owlObj : owlProperties) {
					jsonAnnotation.put(owlObj.getIRI().getShortForm());
				}
				
				objectPropertie.put(axiom.getAxiomType().toString(), jsonAnnotation);
			}

			// Por algum motivo não está retnrnando nada
			Collection<OWLAnnotationProperty> OWLAnnotationProperties = owlObjectPropertie.getAnnotationPropertiesInSignature();
			for (OWLAnnotationProperty owlAnnotationsPropertie : OWLAnnotationProperties) {
				JSONObject annotation = new JSONObject();

				for (OWLAnnotationAxiom axiom : ontology.getAxioms(owlAnnotationsPropertie)) {
					Collection<OWLClass> owlClassAxion = axiom.getClassesInSignature();
					JSONArray jsonAnnotation = new JSONArray();
					for (OWLClass owlObj : owlClassAxion) {
						jsonAnnotation.put(owlObj.getIRI().getShortForm());
					}

					// Está retornando vazio, verificar
					annotation.put(axiom.getAxiomType().toString(), jsonAnnotation);
				}

				annotations.put(annotation);
			}

			objectPropertie.put("Annotation", annotations);
			
			objectProperties.put(objectPropertie);

		}
		
		if (!classes.isEmpty()) {
			json.put("classes", classes);
		} else {
			error.put("Não foi encontrado nenhuma classe.");
			s = Status.BAD_REQUEST;
		}

		json.put("individuals", individuals);
		json.put("object properties", objectProperties);

		JSONObject resp = new JSONObject();
		if (error.isEmpty()) {
			resp.put("Ontology", json);
			resp.put("Status", s);
		} else {
			resp.put("Error", error);
			resp.put("Status", s);
		}

		return resp;
	}
}
