package br.com.ontoforall.owlapi.model;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.stream.Collectors;

import org.json.JSONArray;
import org.json.JSONObject;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.expression.OWLEntityChecker;
import org.semanticweb.owlapi.expression.ShortFormEntityChecker;
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
import org.semanticweb.owlapi.model.AddAxiom;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLDocumentFormat;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.OWLSubClassOfAxiom;
import org.semanticweb.owlapi.util.CachingBidirectionalShortFormProvider;
import org.semanticweb.owlapi.util.SimpleShortFormProvider;
import org.semanticweb.owlapi.util.mansyntax.ManchesterOWLSyntaxParser;

public class ElementosOWL {

	private String id;
	private String formato;
	private JSONArray classes;
	private JSONArray propriedades;
	private JSONArray axiomas;

	class Provider extends CachingBidirectionalShortFormProvider {

		private SimpleShortFormProvider provider = new SimpleShortFormProvider();

		@Override
		protected String generateShortForm(OWLEntity entity) {
			return provider.getShortForm(entity);
		}
	}

	public ElementosOWL(JSONObject ontologia) {
		this.id = ontologia.getString("id") + "/";
		this.formato = ontologia.getString("outformat");
		this.classes = ontologia.getJSONArray("ontoclass");
		this.propriedades = ontologia.getJSONArray("ontoproperties");
		this.axiomas = ontologia.getJSONArray("ontoaxioms");
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

	/**
	 * Valida se a estrutura da modelagem da ontologia é válida 
	 * @return String - Confirmacao em Linguagem Natural
	 */
	public String validaOWL() {

		Provider shortFormProvider = carregaProvider();
		OWLEntityChecker entityChecker = new ShortFormEntityChecker(shortFormProvider);

		//Carrega os Axiomas
		JSONObject owl = new JSONObject(ontologia);

		/**
		 * Trabalha se as classes
		 */
		JSONArray classes = new JSONArray();
		classes = owl.getJSONArray("ontoclass");

		for (int i = 0; i < classes.length(); i++) {
			shortFormProvider.add(df.getOWLClass(IRI.create("https://onto4alleditor.com/pt/definirID/" + classes.get(i))));
		}

		/**
		 * Trabalha se os axiomas 
		 */
		JSONArray axiomas = new JSONArray();
		axiomas = owl.getJSONArray("ontoaxioms");

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
	
	/**
	 * String esperada:
	 * {
	 *   "id": "https://onto4alleditor.com/pt/idDoProjeto",
	 *   "outformat": "OWL",
	 *   "ontoclass": ["Pessoa", "Homem", "Mulher"],
	 *   "ontoaxioms": ["Homem subClassOf (Pessoa)", "Mulher subClassOf (Pessoa)"],
	 *   "ontoproperties": ["hasPart"]
	 *	}
	 * @return String - Ontologia formatada
	 */
	public String formataOWL() {
		
		OWLOntologyManager man = OWLManager.createOWLOntologyManager();
		try {
			OWLDocumentFormat formato = getFormatoSaidaOntologia();
			OWLOntology owlOntology = geraOWLdeString();
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				man.saveOntology(owlOntology, formato, baos);
				return baos.toString();
		} catch (Exception e) {
			return "Ocorreu o seguinte erro: " + e.toString();
		}
	}
	
	/**
	 * Recebe os elementos da ontologia em String/JSON e transforma em ontologia tipada
	 * @param String - elementos da ontologia 
	 * @return OWLOntology 
	 * @throws Exception
	 */
	private OWLOntology geraOWLdeString() throws Exception {
		
		IRI iri = IRI.create(this.id);
		OWLOntologyManager owlManager = OWLManager.createOWLOntologyManager();
		OWLOntology owlOntology = owlManager.createOntology(iri);
		Provider shortFormProvider = carregaProvider();
		OWLEntityChecker entityChecker = new ShortFormEntityChecker(shortFormProvider);

		/**
		 * Trabalha se as classes
		 */
		JSONArray classes = new JSONArray();
		classes = owl.getJSONArray("ontoclass");

		for (int i = 0; i < classes.length(); i++) {
			shortFormProvider.add(dataFactory.getOWLClass(iri.toString() + "#" + classes.get(i)));
		}
		
		/**
		 * Teste com axiomas !OWL
		 */
		// OWLObjectProperty hasSynonym = dataFactory.getOWLObjectProperty(iri + "#hasSynonym");
		// OWLClassExpression pessoaHasSononym = dataFactory.getOWLObjectSomeValuesFrom(hasSynonym, dataFactory.getOWLClass("#Pessoa"));
		// OWLClass gente = dataFactory.getOWLClass(iri + "#Gente");
		// OWLSubClassOfAxiom ax = dataFactory.getOWLSubClassOfAxiom(gente, pessoaHasSononym);
		// owlManager.applyChange(new AddAxiom(owlOntology, ax));
		
		/**
		 * Trabalha se os axiomas declarativos
		 */
		JSONArray axiomas = new JSONArray();
		axiomas = owl.getJSONArray("ontoaxioms");

		ManchesterOWLSyntaxParser parser = OWLManager.createManchesterParser();

		parser.setOWLEntityChecker(entityChecker);
		int index = 0;
		
		// Verifica Axiomas
		try {
			for (int i = 0; i < this.axiomas.length(); i++) {
				index = i;
				parser.setStringToParse(this.axiomas.getString(i));
				owlOntology.addAxiom(parser.parseAxiom());
			}
			return owlOntology;		
		} catch (Exception e) {

			throw new Exception("\nAxioma: ( " + this.axiomas.getString(index) + ") \n\n" + 
								"Erro: ( " + e.toString() + " )"
			);

			System.out.println(e.getMessage());
			return null;

		}
		
	}
	
	/**
	 * Retorna o tipo de formato para streaming de saida da ontologia
	 * @param String - formato
	 * @return OWLDocumentFormat
	 * @throws Exception
	 */
	private OWLDocumentFormat getFormatoSaidaOntologia() throws Exception {
	
		switch (this.formato) {
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
