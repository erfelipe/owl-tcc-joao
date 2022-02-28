package br.com.ontoforall.owlapi.model;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONObject;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.expression.OWLEntityChecker;
import org.semanticweb.owlapi.formats.DLSyntaxDocumentFormat;
import org.semanticweb.owlapi.formats.DLSyntaxHTMLDocumentFormat;
import org.semanticweb.owlapi.formats.FunctionalSyntaxDocumentFormat;
import org.semanticweb.owlapi.formats.KRSS2DocumentFormat;
import org.semanticweb.owlapi.formats.LatexDocumentFormat;
import org.semanticweb.owlapi.formats.ManchesterSyntaxDocumentFormat;
import org.semanticweb.owlapi.formats.N3DocumentFormat;
import org.semanticweb.owlapi.formats.NQuadsDocumentFormat;
import org.semanticweb.owlapi.formats.NTriplesDocumentFormat;
import org.semanticweb.owlapi.formats.OBODocumentFormat;
import org.semanticweb.owlapi.formats.OWLXMLDocumentFormat;
import org.semanticweb.owlapi.formats.RDFJsonDocumentFormat;
import org.semanticweb.owlapi.formats.RDFJsonLDDocumentFormat;
import org.semanticweb.owlapi.formats.RDFXMLDocumentFormat;
import org.semanticweb.owlapi.formats.RioRDFXMLDocumentFormat;
import org.semanticweb.owlapi.formats.RioTurtleDocumentFormat;
import org.semanticweb.owlapi.formats.TrigDocumentFormat;
import org.semanticweb.owlapi.formats.TrixDocumentFormat;
import org.semanticweb.owlapi.formats.TurtleDocumentFormat;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAnnotation;
import org.semanticweb.owlapi.model.OWLAnnotationProperty;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLDatatype;
import org.semanticweb.owlapi.model.OWLDocumentFormat;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLIndividual;
import org.semanticweb.owlapi.model.OWLLiteral;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.util.DefaultPrefixManager;
import org.semanticweb.owlapi.util.mansyntax.ManchesterOWLSyntaxParser;
import org.semanticweb.owlapi.vocab.OWL2Datatype;
import org.semanticweb.owlapi.vocab.OWLRDFVocabulary;

public class OntologyExporter {

	private static final String TYPE = "Type";
	private OWLOntology ont;
	private OWLDocumentFormat filetype;
	private OWLDataFactory df = OWLManager.getOWLDataFactory();

	//JSON keys
	private static final String FUNCTIONAL = "Functional";
	private static final String CHARACTERISTICS = "Characteristics";
	private static final String RANGE = "Range";
	private static final String DOMAIN = "Domain";
	private static final String INVERSE_OF = "InverseOf";
	private static final String SUB_PROPERTY_OF = "SubPropertyOf";
	private static final String TEXT = "Text";
	private static final String LANGUAGE = "Language";
	private static final String VALUE = "Value";
	private static final String DATA_TYPE = "DataType";
	private static final String DATA_PROPERTY = "DataProperty";
	private static final String RELATIONSHIP = "Relationship";
	private static final String DIFFERENT_FROM = "DifferentFrom";
	private static final String SAME_AS = "SameAs";
	private static final String TYPES = "Types";
	private static final String EQUIVALENT_TO = "EquivalentTo";
	private static final String DISJOINT_WITH = "DisjointWith";
	private static final String ANNOTATION = "Annotation";
	private static final String SUB_CLASS_OF = "SubClassOf";
	private static final String NAME = "Name";
	private static final String CONSTRAINTS = "constraints";
	private static final String INDIVIDUALS = "individuals";
	private static final String DATA_PROPERTIES = "data properties";
	private static final String OBJECT_PROPERTIES = "object properties";
	private static final String CLASSES = "classes";
	private static final String FILETYPE = "filetype";
	private static final String ID = "id";


	//CONSTRUCTOR 
	public OntologyExporter(JSONObject json_arr) throws Exception{
		if (!json_arr.has(ID) || !json_arr.has(FILETYPE))
			throw new Exception("ID ou Filetype está ausente.");
		else{
			this.filetype = getOWLFormat(json_arr.getString(FILETYPE));
			IRI iri = IRI.create(json_arr.getString(ID) + "/");
			OWLOntologyManager owlManager = OWLManager.createOWLOntologyManager();
			this.ont = owlManager.createOntology(iri);
			if (json_arr.has(CLASSES))
				this.loadClasses(json_arr.getJSONArray(CLASSES));
			if (json_arr.has(OBJECT_PROPERTIES))
				this.loadObjectProperties(json_arr.getJSONArray(OBJECT_PROPERTIES));
			if (json_arr.has(DATA_PROPERTIES))
				this.loadDataProperties(json_arr.getJSONArray(DATA_PROPERTIES));
			if (json_arr.has(INDIVIDUALS))
				this.loadIndividuals(json_arr.getJSONArray(INDIVIDUALS));
			if (json_arr.has(CONSTRAINTS))
				this.loadConstraints(json_arr.getJSONArray(CONSTRAINTS));
		}
	}

	//INIT METHODS
	private OWLClass initClass(String name){
		return df.getOWLClass(name, new DefaultPrefixManager(this.ont.getOntologyID().getOntologyIRI().get().toString()));
	}

	private OWLObjectProperty initObjectProperty(String name){
		return df.getOWLObjectProperty(name, new DefaultPrefixManager(this.ont.getOntologyID().getOntologyIRI().get().toString()));		
	}

	private OWLDataProperty initDataProperty(String name){
		return df.getOWLDataProperty(name, new DefaultPrefixManager(this.ont.getOntologyID().getOntologyIRI().get().toString()));		
	}

	private OWLNamedIndividual initIndividual(String name){
		return df.getOWLNamedIndividual(name, new DefaultPrefixManager(this.ont.getOntologyID().getOntologyIRI().get().toString()));
	}

	private OWLDatatype initDataType(String datatype){
		switch (datatype) {
			case "rdf:PlainLiteral":
				return df.getRDFPlainLiteral();
			case "rdf:XMLLiteral":
				return df.getOWLDatatype(OWL2Datatype.RDF_XML_LITERAL.getIRI());
			case "rdfs:Literal":
				return df.getOWLDatatype(OWL2Datatype.RDFS_LITERAL.getIRI());
			case "xsd:string":
				return df.getStringOWLDatatype();
			case "xsd:float":
				return df.getFloatOWLDatatype();
			case "xsd:double":
				return df.getDoubleOWLDatatype();
			case "xsd:boolean":
				return df.getBooleanOWLDatatype();
			case "xsd:integer":
				return df.getIntegerOWLDatatype();
			case "xsd:anyURI":
				return df.getOWLDatatype(OWL2Datatype.XSD_ANY_URI.getIRI());
			case "owl:rational":
				return df.getOWLDatatype(OWL2Datatype.OWL_RATIONAL.getIRI());
			case "owl:real":
				return df.getOWLDatatype(OWL2Datatype.OWL_RATIONAL.getIRI());
			default:
				return null;
		}
	}

	//LOAD METHODS
	private void loadClasses(JSONArray json_classes){
		for (int i = 0; i < json_classes.length(); i++){
			JSONObject json_class = json_classes.getJSONObject(i);
			OWLClass  classe = initClass(json_class.getString(NAME));
			this.ont.add(df.getOWLDeclarationAxiom(classe));
			if (json_class.has(SUB_CLASS_OF))
				this.loadClassProperties(json_class.getJSONArray(SUB_CLASS_OF), classe, SUB_CLASS_OF);
			if (json_class.has(EQUIVALENT_TO))
				this.loadClassProperties(json_class.getJSONArray(EQUIVALENT_TO), classe, EQUIVALENT_TO);
			if (json_class.has(DISJOINT_WITH))
				this.loadClassProperties(json_class.getJSONArray(DISJOINT_WITH), classe, DISJOINT_WITH);
			if (json_class.has(ANNOTATION))
				this.loadAnnotation(json_class.getJSONArray(ANNOTATION), classe, null, null, null);
		}
	}

	private void loadObjectProperties(JSONArray json_obj_props){
		for (int i = 0; i < json_obj_props.length(); i++){
			JSONObject json_obj_prop = json_obj_props.getJSONObject(i);
			OWLObjectProperty  obj_prop = initObjectProperty(json_obj_prop.getString(NAME));
			this.ont.add(df.getOWLDeclarationAxiom(obj_prop));
			if (json_obj_prop.has(SUB_PROPERTY_OF))
				this.loadObjectPropertiesProperties(json_obj_prop.getJSONArray(SUB_PROPERTY_OF), obj_prop, SUB_PROPERTY_OF);
			if (json_obj_prop.has(EQUIVALENT_TO))
				this.loadObjectPropertiesProperties(json_obj_prop.getJSONArray(EQUIVALENT_TO), obj_prop, EQUIVALENT_TO);
			if (json_obj_prop.has(DISJOINT_WITH))
				this.loadObjectPropertiesProperties(json_obj_prop.getJSONArray(DISJOINT_WITH), obj_prop, DISJOINT_WITH);
			if (json_obj_prop.has(INVERSE_OF))
				this.loadObjectPropertiesProperties(json_obj_prop.getJSONArray(INVERSE_OF), obj_prop, INVERSE_OF);
			if(json_obj_prop.has(CHARACTERISTICS))
				this.loadObjectPropertiesCharacteristcs(json_obj_prop.getJSONArray(CHARACTERISTICS), obj_prop);
			if(json_obj_prop.has(DOMAIN))
				this.loadObjectPropertiesDomainRange(json_obj_prop.getJSONArray(DOMAIN), obj_prop, DOMAIN);
			if(json_obj_prop.has(RANGE))
				this.loadObjectPropertiesDomainRange(json_obj_prop.getJSONArray(RANGE), obj_prop, RANGE);
			if(json_obj_prop.has(ANNOTATION))
				this.loadAnnotation(json_obj_prop.getJSONArray(ANNOTATION), null, null, obj_prop, null);
		}
	}

	private void loadDataProperties(JSONArray json_obj_props){
		for (int i = 0; i < json_obj_props.length(); i++){
			JSONObject json_obj_prop = json_obj_props.getJSONObject(i);
			OWLDataProperty  data_prop = initDataProperty(json_obj_prop.getString(NAME));
			this.ont.add(df.getOWLDeclarationAxiom(data_prop));
			if (json_obj_prop.has(SUB_PROPERTY_OF))
				this.loadDataPropertiesProperties(json_obj_prop.getJSONArray(SUB_PROPERTY_OF), data_prop, SUB_PROPERTY_OF);
			if (json_obj_prop.has(EQUIVALENT_TO))
				this.loadDataPropertiesProperties(json_obj_prop.getJSONArray(EQUIVALENT_TO), data_prop, EQUIVALENT_TO);
			if (json_obj_prop.has(DISJOINT_WITH))
				this.loadDataPropertiesProperties(json_obj_prop.getJSONArray(DISJOINT_WITH), data_prop, DISJOINT_WITH);
			if(json_obj_prop.has(CHARACTERISTICS))
				this.loadDataPropertiesCharacteristcs(json_obj_prop.getJSONArray(CHARACTERISTICS), data_prop);
			if(json_obj_prop.has(DOMAIN))
				this.loadDataPropertiesDomainRange(json_obj_prop.getJSONArray(DOMAIN), data_prop, DOMAIN);
			if(json_obj_prop.has(RANGE))
				this.loadDataPropertiesDomainRange(json_obj_prop.getJSONArray(RANGE), data_prop, RANGE);
			if(json_obj_prop.has(ANNOTATION))
				this.loadAnnotation(json_obj_prop.getJSONArray(ANNOTATION), null, data_prop, null, null);
		}
	}

	private void loadIndividuals(JSONArray json_individuals){
		for (int i = 0; i < json_individuals.length(); i++){
			JSONObject json_individual = json_individuals.getJSONObject(i);
			OWLNamedIndividual  individual = initIndividual(json_individual.getString(NAME));
			this.ont.add(df.getOWLDeclarationAxiom(individual));
			if (json_individual.has(TYPES))
				this.loadIndividualProperties(json_individual.getJSONArray(TYPES), individual, TYPES);
			if (json_individual.has(SAME_AS))
				this.loadIndividualSameAsDifferentFrom(json_individual.getJSONArray(SAME_AS), individual, SAME_AS);
			if (json_individual.has(DIFFERENT_FROM))
				this.loadIndividualSameAsDifferentFrom(json_individual.getJSONArray(DIFFERENT_FROM), individual, DIFFERENT_FROM);
			if (json_individual.has(RELATIONSHIP))
				this.loadIndividualRelationship(json_individual.getJSONArray(RELATIONSHIP), individual);
			if (json_individual.has(ANNOTATION))
				this.loadAnnotation(json_individual.getJSONArray(ANNOTATION), null, null, null, individual);	
		}
	}

	//LOAD PROPERTIES METHODS
	private void loadClassProperties(JSONArray prop_array, OWLClass cl, String property){
		for (int i = 0; i < prop_array.length(); i++){
			OWLClass oclass  = this.initClass(prop_array.get(i).toString());
			switch (property) {
				case SUB_CLASS_OF:
					this.ont.addAxiom(df.getOWLSubClassOfAxiom(cl, oclass));
					break;
				case EQUIVALENT_TO:
					this.ont.addAxiom(df.getOWLEquivalentClassesAxiom(cl, oclass));
					break;
				case DISJOINT_WITH:
					this.ont.addAxiom(df.getOWLDisjointClassesAxiom(cl, oclass));
					break;
				default:
					break;
			}
		}
	}
	private void loadObjectPropertiesProperties(JSONArray prop_array, OWLObjectProperty obj_prop, String property){
		for (int i = 0; i < prop_array.length(); i++){
			OWLObjectProperty oobjprop = this.initObjectProperty(prop_array.get(i).toString());
			switch (property) {
				case SUB_PROPERTY_OF:
					this.ont.addAxiom(df.getOWLSubObjectPropertyOfAxiom(obj_prop, oobjprop));
					break;
				case EQUIVALENT_TO:
					this.ont.addAxiom(df.getOWLEquivalentObjectPropertiesAxiom(obj_prop, oobjprop));
					break;
				case DISJOINT_WITH:
					this.ont.addAxiom(df.getOWLDisjointObjectPropertiesAxiom(obj_prop, oobjprop));
					break;
				case INVERSE_OF:
					this.ont.addAxiom(df.getOWLInverseObjectPropertiesAxiom(obj_prop, oobjprop));
				default:
					break;
			}
		}
	}

	private void loadDataPropertiesProperties(JSONArray prop_array, OWLDataProperty data_prop, String property){
		for (int i = 0; i < prop_array.length(); i++){
			OWLDataProperty odataprop = this.initDataProperty(prop_array.get(i).toString());
			switch (property) {
				case SUB_PROPERTY_OF:
					this.ont.addAxiom(df.getOWLSubDataPropertyOfAxiom(data_prop, odataprop));
					break;
				case EQUIVALENT_TO:
					this.ont.addAxiom(df.getOWLEquivalentDataPropertiesAxiom(data_prop, odataprop));
					break;
				case DISJOINT_WITH:
					this.ont.addAxiom(df.getOWLDisjointDataPropertiesAxiom(data_prop, odataprop));
					break;
				default:
					break;
			}
		}
	}

	private void loadIndividualProperties(JSONArray prop_array, OWLNamedIndividual ind, String property){
		for (int i = 0; i < prop_array.length(); i++){
			switch (property) {
				case TYPE:
					OWLClass oclass  = this.initClass(prop_array.get(i).toString());
					this.ont.addAxiom(df.getOWLClassAssertionAxiom(oclass, ind));
					break;
				default:
					break;
			}
		}
	}


	//ANNOTATION METHODS
	private void loadAnnotation(JSONArray anno_array, OWLClass cl, OWLDataProperty data_prop, 
	OWLObjectProperty obj_prop, OWLNamedIndividual ind){
		for (int i = 0; i < anno_array.length(); i++){
			JSONObject anno = anno_array.getJSONObject(i);
			String lang = anno.has(LANGUAGE) ? anno.getString(LANGUAGE): "";
			OWLLiteral lit = df.getOWLLiteral(anno.getString(TEXT), lang);
			OWLAnnotation owl_anno = df.getOWLAnnotation(getAnnotation(anno.getString("Property")), lit);
			if (cl != null)
				this.ont.add(df.getOWLAnnotationAssertionAxiom(cl.getIRI(), owl_anno));
			else if(data_prop != null)
				this.ont.add(df.getOWLAnnotationAssertionAxiom(data_prop.getIRI(), owl_anno));
			else if(obj_prop != null)
				this.ont.add(df.getOWLAnnotationAssertionAxiom(obj_prop.getIRI(), owl_anno));
			else if(ind != null)
				this.ont.add(df.getOWLAnnotationAssertionAxiom(ind.getIRI(), owl_anno));
		}		
	}

	private OWLAnnotationProperty getAnnotation(String anno){
		switch (anno) {
			case "rdfs:comment":
				return df.getRDFSComment();
			case "rdfs:label":
				return df.getRDFSLabel();
			case "rdfs:isDefinedBy":
				return df.getRDFSIsDefinedBy();
			case "owl:priorVersion":
				return df.getOWLAnnotationProperty(OWLRDFVocabulary.OWL_PRIOR_VERSION.getIRI());
			case "owl:versionInfo":
				return df.getOWLAnnotationProperty(OWLRDFVocabulary.OWL_VERSION_INFO.getIRI());
			default:
				return null;
		}
	}

	//CARACHTERISTICS METHODS
	private void loadObjectPropertiesCharacteristcs(JSONArray characs, OWLObjectProperty obj_prop){
		Set<OWLAxiom> propAxioms = new HashSet<OWLAxiom>();

		for (int i = 0; i < characs.length(); i++){
			switch (characs.getString(i)) {
				case FUNCTIONAL:
					propAxioms.add(df.getOWLFunctionalObjectPropertyAxiom(obj_prop));
					break;
				case "Inverse Functional":
					propAxioms.add(df.getOWLInverseFunctionalObjectPropertyAxiom(obj_prop));
					break;
				case "Transitive":
					propAxioms.add(df.getOWLTransitiveObjectPropertyAxiom(obj_prop));
					break;
				case "Symmetric":
					propAxioms.add(df.getOWLSymmetricObjectPropertyAxiom(obj_prop));
					break;
				case "Asymmetric":
					propAxioms.add(df.getOWLAsymmetricObjectPropertyAxiom(obj_prop));
					break;
				case "Reflexive":
					propAxioms.add(df.getOWLReflexiveObjectPropertyAxiom(obj_prop));
					break;
				case "Irreflexive":
					propAxioms.add(df.getOWLIrreflexiveObjectPropertyAxiom(obj_prop));
					break;
				default:
					break;
			}
		}
		this.ont.addAxioms(propAxioms);		
	}

	private void loadDataPropertiesCharacteristcs(JSONArray characs, OWLDataProperty data_prop){
		Set<OWLAxiom> propAxioms = new HashSet<OWLAxiom>();

		for (int i = 0; i < characs.length(); i++){
			switch (characs.getString(i)) {
				case FUNCTIONAL:
					propAxioms.add(df.getOWLFunctionalDataPropertyAxiom(data_prop));
					break;
				default:
					break;
			}
		}
		this.ont.addAxioms(propAxioms);		
	}

	// DOMAIN/RANGE METHODS
	private void loadObjectPropertiesDomainRange(JSONArray domain_range_array, OWLObjectProperty obj_prop, String domain_range){
		for (int i = 0; i < domain_range_array.length(); i++){
			OWLClass cl = initClass(domain_range_array.getString(i));
			if (domain_range.equals(DOMAIN))
				this.ont.add(df.getOWLObjectPropertyDomainAxiom(obj_prop, cl));
			else // Range
				this.ont.add(df.getOWLObjectPropertyRangeAxiom(obj_prop, cl));
		}
	}

	private void loadDataPropertiesDomainRange(JSONArray domain_range_array, OWLDataProperty data_prop, String domain_range){
		for (int i = 0; i < domain_range_array.length(); i++){
			if (domain_range.equals(DOMAIN)){
				OWLClass cl = initClass(domain_range_array.getString(i));
				this.ont.add(df.getOWLDataPropertyDomainAxiom(data_prop, cl));
			}else // Range
				this.ont.add(df.getOWLDataPropertyRangeAxiom(data_prop, initDataType(domain_range_array.getString(i))));
		}
	}

	//INDIVIDUALS METHODS
	private void loadIndividualRelationship(JSONArray json_relationships, OWLIndividual ind){
		for (int i = 0; i < json_relationships.length(); i++){
			JSONObject rel = json_relationships.getJSONObject(i);
			OWLDataProperty dp = initDataProperty(rel.getString(DATA_PROPERTY));
			OWLDatatype datatype = initDataType(rel.getString(DATA_TYPE));
			OWLLiteral lit = df.getOWLLiteral(rel.getString(VALUE), datatype);
			this.ont.add(df.getOWLDataPropertyAssertionAxiom(dp, ind, lit));
		}
	}


	private void loadIndividualSameAsDifferentFrom(JSONArray individuals, OWLNamedIndividual individual, String same_different){
		Set<OWLNamedIndividual> axioms = new HashSet<OWLNamedIndividual>();
		axioms.add(df.getOWLNamedIndividual(individual));
		for (int i = 0; i < individuals.length(); i++){
			OWLNamedIndividual ind = initIndividual(individuals.getString(i));
			axioms.add(df.getOWLNamedIndividual(ind));
		}
		if(same_different.equals(SAME_AS))
			this.ont.addAxioms(df.getOWLSameIndividualAxiom(axioms));
		else if(same_different.equals(DIFFERENT_FROM))
			this.ont.addAxioms(df.getOWLDifferentIndividualsAxiom(axioms));
	}

	// CONSTRAINTS METHODS
	private ManchesterOWLSyntaxParser loadParser(){
		ManchesterOWLSyntaxParser parser = OWLManager.createManchesterParser();
		parser.setDefaultOntology(this.ont);
		final Map<String, OWLEntity> map = new HashMap<>();
		ont.signature().forEach(x -> map.put(x.getIRI().getFragment(), x));
		
		parser.setOWLEntityChecker(new OWLEntityChecker() {
			
			private <T> T v(String name, Class<T> t) {
				OWLEntity e = map.get(name);
				if (t.isInstance(e)) {
					return t.cast(e);
				}
				return null;
			}
	
			@Override
			public OWLObjectProperty getOWLObjectProperty(String name) {
				return v(name, OWLObjectProperty.class);
			}
	
			@Override
			public OWLNamedIndividual getOWLIndividual(String name) {
				return v(name, OWLNamedIndividual.class);
			}
	
			@Override
			public OWLDatatype getOWLDatatype(String name) {
				return v(name, OWLDatatype.class);
			}
	
			@Override
			public OWLDataProperty getOWLDataProperty(String name) {
				return v(name, OWLDataProperty.class);
			}
	
			@Override
			public OWLClass getOWLClass(String name) {
				return v(name, OWLClass.class);
			}
	
			@Override
			public OWLAnnotationProperty getOWLAnnotationProperty(String name) {
				return v(name, OWLAnnotationProperty.class);
			}
		});
		return parser;
	}

	private void loadConstraints(JSONArray constraints){
		ManchesterOWLSyntaxParser parser = this.loadParser();
		
		for (int i = 0; i < constraints.length(); i++) {
			parser.setStringToParse(constraints.getString(i));
			this.ont.add(parser.parseAxiom());
		}
	}

	//EXPORT METHODS
	public String exportOntology() {
		
		OWLOntologyManager man = OWLManager.createOWLOntologyManager();
		try {
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				man.saveOntology(ont, this.filetype, baos);
				return baos.toString();
		} catch (Exception e) {
			return "Ocorreu o seguinte erro: " + e.toString();
		}
	}
	
	private OWLDocumentFormat getOWLFormat(String format) throws Exception {
		switch (format) {
			case "OWL":
				return new OWLXMLDocumentFormat();
			case "TURTLE":
				return new TurtleDocumentFormat();
			case "SINTAXEDL":
				return new DLSyntaxDocumentFormat();
			case "SINTAXEDLHTML":
				return new DLSyntaxHTMLDocumentFormat();
			case "SINTAXEFUNCIONAL":
				return new FunctionalSyntaxDocumentFormat();
			case "KRSS":
				return new KRSS2DocumentFormat();
			case "DOCUMENTOLATEX":
				return new LatexDocumentFormat();
			case "N3":
				return new N3DocumentFormat();
			case "SINTAXEMANCHESTER":
				return new ManchesterSyntaxDocumentFormat();
			case "NQUAD":
				return new NQuadsDocumentFormat();
			case "NTRIPLA":
				return new NTriplesDocumentFormat();
			case "OBO":
				return new OBODocumentFormat();
			case "RDFJSON":
				return new RDFJsonDocumentFormat();
			case "RDFJSONLD":
				return new RDFJsonLDDocumentFormat();
			case "RDFXML":
				return new RDFXMLDocumentFormat();
			case "RIOTURTLE":
				return new RioTurtleDocumentFormat();
			case "RIORDFXML":
				return new RioRDFXMLDocumentFormat();
			case "TRIG":
				return new TrigDocumentFormat();
			case "TRIX":
				return new TrixDocumentFormat();
			default:
				throw new Exception("Formato de saída inexistente ou não suportado!");
		}
	}
}
