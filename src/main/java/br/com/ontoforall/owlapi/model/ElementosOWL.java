package br.com.ontoforall.owlapi.model;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

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
import org.semanticweb.owlapi.model.OWLLiteral;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.PrefixManager;
import org.semanticweb.owlapi.util.DefaultPrefixManager;
import org.semanticweb.owlapi.util.mansyntax.ManchesterOWLSyntaxParser;

public class ElementosOWL {

	private OWLOntology ont;
	private OWLDocumentFormat filetype;

	public ElementosOWL(JSONObject json_arr) throws Exception{
		if (!json_arr.has("id") || !json_arr.has("filetype"))
			throw new Exception("ID ou Filetype está ausente.");
		else{
			this.filetype = carregaFormatoSaidaOntologia(json_arr.getString("filetype"));
			IRI iri = IRI.create(json_arr.getString("id") + "/");
			OWLOntologyManager owlManager = OWLManager.createOWLOntologyManager();
			this.ont = owlManager.createOntology(iri);
			if (json_arr.has("classes"))
				this.loadClasses(json_arr.getJSONArray("classes"));
			if (json_arr.has("object properties"))
				this.loadObjectProperties(json_arr.getJSONArray("object properties"));
			if (json_arr.has("constraints"))
				this.loadConstraints(json_arr.getJSONArray("constraints"));
		}
	}

	public ElementosOWL(){}

	private void loadClasses(JSONArray json_classes){
		OWLDataFactory df = OWLManager.getOWLDataFactory();
		for (int i = 0; i < json_classes.length(); i++){
			JSONObject json_class = json_classes.getJSONObject(i);
			OWLClass  classe = initClass(json_class.getString("Name"));
			this.ont.add(df.getOWLDeclarationAxiom(classe));
			if (json_class.has("SubClassOf"))
				this.loadClassProperties(json_class.getJSONArray("SubClassOf"), classe, "SubClassOf");
			if (json_class.has("EquivalentTo"))
				this.loadClassProperties(json_class.getJSONArray("EquivalentTo"), classe, "EquivalentTo");
			if (json_class.has("DisjointWith"))
				this.loadClassProperties(json_class.getJSONArray("DisjointWith"), classe, "DisjointWith");
			if (json_class.has("Annotation"))
				this.loadClassAnnotation(json_class.getJSONArray("Annotation"), classe);
		}
	}

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

	private void loadClassAnnotation(JSONArray anno_array, OWLClass cl){
		OWLDataFactory df = OWLManager.getOWLDataFactory();
		for (int i = 0; i < anno_array.length(); i++){
			JSONObject anno = anno_array.getJSONObject(i);
			OWLLiteral lit = df.getOWLLiteral(anno.getString("Annotation"), anno.getString("Language"));
			OWLAnnotation owl_anno = df.getOWLAnnotation(df.getRDFSComment(), lit);
			this.ont.add(df.getOWLAnnotationAssertionAxiom(cl.getIRI(), owl_anno));
		}
	}

	private void loadObjectPropertyAnnotation(JSONArray anno_array, OWLObjectProperty obj_prop){
		OWLDataFactory df = OWLManager.getOWLDataFactory();
		for (int i = 0; i < anno_array.length(); i++){
			JSONObject anno = anno_array.getJSONObject(i);
			OWLLiteral lit = df.getOWLLiteral(anno.getString("Annotation"), anno.getString("Language"));
			OWLAnnotation owl_anno = df.getOWLAnnotation(df.getRDFSComment(), lit);
			this.ont.add(df.getOWLAnnotationAssertionAxiom(obj_prop.getIRI(), owl_anno));
		}
	}

	private void loadClassProperties(JSONArray prop_array, OWLClass cl, String property){
		OWLDataFactory df = OWLManager.getOWLDataFactory();
		for (int i = 0; i < prop_array.length(); i++){
			OWLClass oclass  = this.initClass(prop_array.get(i).toString());
			switch (property) {
				case "SubClassOf":
					this.ont.addAxiom(df.getOWLSubClassOfAxiom(cl, oclass));
					break;
				case "EquivalentTo":
					this.ont.addAxiom(df.getOWLEquivalentClassesAxiom(cl, oclass));
					break;
				case "DisjointWith":
					this.ont.addAxiom(df.getOWLDisjointClassesAxiom(cl, oclass));
					break;
				default:
					break;
			}
		}
	}

	private OWLClass initClass(String name){
		OWLDataFactory df = OWLManager.getOWLDataFactory();
		PrefixManager pm = new DefaultPrefixManager(this.ont.getOntologyID().getOntologyIRI().get().toString());
		return df.getOWLClass(name, pm);
	}

	private void loadObjectProperties(JSONArray json_obj_props){
		OWLDataFactory df = OWLManager.getOWLDataFactory();
		for (int i = 0; i < json_obj_props.length(); i++){
			JSONObject json_obj_prop = json_obj_props.getJSONObject(i);
			OWLObjectProperty  obj_prop = initObjectProperty(json_obj_prop.getString("Name"));
			this.ont.add(df.getOWLDeclarationAxiom(obj_prop));
			if (json_obj_prop.has("SubPropertyOf"))
				this.loadObjectPropertiesProperties(json_obj_prop.getJSONArray("SubPropertyOf"), obj_prop, "SubPropertyOf");
			if (json_obj_prop.has("EquivalentTo"))
				this.loadObjectPropertiesProperties(json_obj_prop.getJSONArray("EquivalentTo"), obj_prop, "EquivalentTo");
			if (json_obj_prop.has("DisjointWith"))
				this.loadObjectPropertiesProperties(json_obj_prop.getJSONArray("DisjointWith"), obj_prop, "DisjointWith");
			if (json_obj_prop.has("InverseOf"))
				this.loadObjectPropertiesProperties(json_obj_prop.getJSONArray("InverseOf"), obj_prop, "InverseOf");
			if(json_obj_prop.has("Characteristics"))
				this.loadObjectPropertiesCharacteristcs(json_obj_prop.getJSONArray("Characteristics"), obj_prop);
			if(json_obj_prop.has("Domain"))
				this.loadObjectPropertiesDomainRange(json_obj_prop.getJSONArray("Domain"), obj_prop, "Domain");
			if(json_obj_prop.has("Range"))
				this.loadObjectPropertiesDomainRange(json_obj_prop.getJSONArray("Range"), obj_prop, "Range");
			if(json_obj_prop.has("Annotation"))
				this.loadObjectPropertyAnnotation(json_obj_prop.getJSONArray("Annotation"), obj_prop);
		}
	}

	private void loadObjectPropertiesDomainRange(JSONArray domain_range_array, OWLObjectProperty obj_prop, String domain_range){
		OWLDataFactory df = OWLManager.getOWLDataFactory();
		for (int i = 0; i < domain_range_array.length(); i++){
			OWLClass cl = initClass(domain_range_array.getString(i));
			if (domain_range.equals("Domain"))
				this.ont.add(df.getOWLObjectPropertyDomainAxiom(obj_prop, cl));
			else // Range
				this.ont.add(df.getOWLObjectPropertyRangeAxiom(obj_prop, cl));
		}
	}

	private void loadObjectPropertiesCharacteristcs(JSONArray characs, OWLObjectProperty obj_prop){
		OWLDataFactory df = OWLManager.getOWLDataFactory();
		Set<OWLAxiom> propAxioms = new HashSet<OWLAxiom>();

		for (int i = 0; i < characs.length(); i++){
			switch (characs.getString(i)) {
				case "Functional":
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

	private void loadObjectPropertiesProperties(JSONArray prop_array, OWLObjectProperty obj_prop, String property){
		OWLDataFactory df = OWLManager.getOWLDataFactory();
		for (int i = 0; i < prop_array.length(); i++){
			OWLObjectProperty oobjprop = this.initObjectProperty(prop_array.get(i).toString());
			switch (property) {
				case "SubPropertyOf":
					this.ont.addAxiom(df.getOWLSubObjectPropertyOfAxiom(obj_prop, oobjprop));
					break;
				case "EquivalentTo":
					this.ont.addAxiom(df.getOWLEquivalentObjectPropertiesAxiom(obj_prop, oobjprop));
					break;
				case "DisjointWith":
					this.ont.addAxiom(df.getOWLDisjointObjectPropertiesAxiom(obj_prop, oobjprop));
					break;
				case "InverseOf":
					this.ont.addAxiom(df.getOWLInverseObjectPropertiesAxiom(obj_prop, oobjprop));
				default:
					break;
			}
		}
	}

	private OWLObjectProperty initObjectProperty(String name){
		OWLDataFactory df = OWLManager.getOWLDataFactory();
		PrefixManager pm = new DefaultPrefixManager(this.ont.getOntologyID().getOntologyIRI().get().toString());
		return df.getOWLObjectProperty(name, pm);		
	}

/* 	private Provider carregaProvider(){
		Provider shortFormProvider = new Provider();
		OWLDataFactory df = OWLManager.getOWLDataFactory();

		// Carrega as Classes
		for (int i = 0; i < this.classes.length(); i++)
		shortFormProvider.add(df.getOWLClass(IRI.create(this.id + this.classes.get(i))));

		//Carrega as Propriedades
		for (int i = 0; i < this.propriedades.length(); i++)
			shortFormProvider.add(df.getOWLDataProperty(IRI.create(this.id + this.propriedades.get(i))));

		//Carrega os individuos
		for (int i = 0; i < this.individuos.length(); i++)
			shortFormProvider.add(df.getOWLNamedIndividual(IRI.create(this.id + this.individuos.get(i))));
		return shortFormProvider;
	} */

	/**
	 * Valida se a estrutura da modelagem da ontologia é válida 
	 * @return String - Confirmacao em Linguagem Natural
	 */
/* 	public String validaOWL() {

		Provider shortFormProvider = carregaProvider();
		OWLEntityChecker entityChecker = new ShortFormEntityChecker(shortFormProvider);

		//Carrega os Axiomas
		ManchesterOWLSyntaxParser parser = OWLManager.createManchesterParser();
		parser.setOWLEntityChecker(entityChecker);
		
		try {
			for (int i = 0; i < this.axiomas.length(); i++) {
				parser.setStringToParse(this.axiomas.getString(i));
				parser.parseAxiom();
			}
			return Boolean.toString(true);
		} catch (Exception e) {
			return Boolean.toString(false);
		}
	} */

	public String validaOWL(){
		return null;
	}
	
	/**
	 * String esperada:
	 * {
	 *   "id": "https://onto4alleditor.com/pt/idDoProjeto",
	 *   "outformat": "OWL",
	 *   "ontoclass": ["Pessoa", "Homem", "Mulher"],
	 *   "ontoaxioms": ["Homem subClassOf (Pessoa)", "Mulher subClassOf (Pessoa)"],
	 *   "ontoproperties": ["hasPart", "hasSister", "hasBrother"],
	 * 	 "ontoindividuals": ["João", "Maria"],
	 *   "ontoinverse": ["hasSister hasBrother"],
	 *   "caracproperties": ["hasSister Asymetric Irreflexive Inverse_Functional"],
	 * 	 "classannotations": ["hasSister pt Possui uma irmã"]
	 *	}
	 * @return String - Ontologia formatada
	 */
	public String formataOWL() {
		
		OWLOntologyManager man = OWLManager.createOWLOntologyManager();
		try {
				OWLOntology owlOntology = geraOWLdeString();
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				man.saveOntology(owlOntology, this.filetype, baos);
				return baos.toString();
		} catch (Exception e) {
			return "Ocorreu o seguinte erro: " + e.toString();
		}
	}
	
	/**
	 * Recebe os elementos da ontologia em String e transforma em ontologia tipada
	 * @param String - elementos da ontologia 
	 * @return OWLOntology 
	 * @throws Exception
	 */
	private OWLOntology geraOWLdeString() throws Exception {
		
		//Carrega as classes
		
		//Provider shortFormProvider = carregaProvider();

		// Verifica Axiomas
		//owlOntology = formataAxiomas(owlOntology, shortFormProvider);
		//Verifica as propriedades inversas
		//owlOntology = formataPropriedadeInversa(owlOntology);
		//Verifica caracteristicas de propriedades
		//owlOntology = formataCaracteristicasDePropriedades(owlOntology);
		//Verifica anotações
		//owlOntology = formataAnotacoes(owlOntology);

		return this.ont;
	}
	
	/**
	 * Retorna o tipo de formato para streaming de saida da ontologia
	 * @param String - formato
	 * @return OWLDocumentFormat
	 * @throws Exception
	 */
	private OWLDocumentFormat carregaFormatoSaidaOntologia(String formato) throws Exception {
		switch (formato) {
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
	
	public String readFromOWL(String ontologia) throws OWLOntologyCreationException {
		ArrayList<String> classes = new ArrayList<String>();
		ArrayList<String> axiomas = new ArrayList<String>(); 
		
		OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
		
		InputStream targetStream = new ByteArrayInputStream(ontologia.getBytes());
		OWLOntology ontology = manager.loadOntologyFromOntologyDocument(targetStream); 
		
		for (Object oc : ontology.classesInSignature().collect(Collectors.toSet())) {
			classes.add(extractClassName(oc.toString()));
			// get all axioms for each class
			for (Object axiom : ontology.axioms((OWLClass) oc).collect(Collectors.toSet())) {
				axiomas.add(axiomWithOutIRI(axiom.toString()));
			};
		};
		return CodeClassAxiomToStr(classes, axiomas);
	}
	
	public String extractClassName(String owlClass) {
		Integer inicio = owlClass.indexOf("#") + 1;
		Integer fim = owlClass.indexOf(">");
		String resp = "";
		
		if ((inicio > 0) && (fim > 0)) {
			resp = owlClass.substring(inicio, fim);
		}
		
		return resp;
	}
	
	public String axiomWithOutIRI(String axiom) {
		String construto = "";
		
		Integer endConstruto = axiom.indexOf("(");
		if (endConstruto > 0) {
			construto = axiom.substring(0, endConstruto);	
		} else {
			construto = "";
		}
		
		Integer iniParams = axiom.indexOf("(");
		Integer fimParams = axiom.indexOf(")");
		String params = axiom.substring(iniParams, fimParams);
		String[] itemParams = params.split(" ");
		
		String param1 = extractClassName(itemParams[0]);
		String param2 = extractClassName(itemParams[1]);
		
		return construto + " (" + param1 + " " + param2 + ")";
	}
	
	public String CodeClassAxiomToStr(ArrayList<String> classes, ArrayList<String> axiomas) {
		JSONObject extracao = new JSONObject();
		
		extracao.put("class", classes);
		extracao.put("axiomas", axiomas);
		
		return extracao.toString();
	}
}
