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

	class Provider extends CachingBidirectionalShortFormProvider {

		private SimpleShortFormProvider provider = new SimpleShortFormProvider();

		@Override
		protected String generateShortForm(OWLEntity entity) {
			return provider.getShortForm(entity);
		}
	}

	public ElementosOWL() {

	}

	/**
	 * Valida se a estrutura da modelagem da ontologia é válida 
	 * @param ontologia
	 * @return String - Confirmacao em Linguagem Natural
	 */
	public String validaOWL(String ontologia) {
		OWLDataFactory df = OWLManager.getOWLDataFactory();

		Provider shortFormProvider = new Provider();
		OWLEntityChecker entityChecker = new ShortFormEntityChecker(shortFormProvider);

		JSONObject owl = new JSONObject(ontologia);

		/**
		 * Trabalha se as classes
		 */
		JSONArray classes = new JSONArray();
		classes = owl.getJSONArray("ontoclass");

		for (int i = 0; i < classes.length(); i++) {
			shortFormProvider.add(df.getOWLClass(IRI.create("https://onto4alleditor.com/pt/idDoProjeto/" + classes.get(i))));
		}

		/**
		 * Trabalha se os axiomas 
		 */
		JSONArray axiomas = new JSONArray();
		axiomas = owl.getJSONArray("ontoaxioms");
		ManchesterOWLSyntaxParser parser = OWLManager.createManchesterParser();
		parser.setOWLEntityChecker(entityChecker);
		
		try {
			for (int i = 0; i < axiomas.length(); i++) {
				parser.setStringToParse(axiomas.getString(i));
				parser.parseAxiom();
			}
			return Boolean.toString(true);
		} catch (Exception e) {
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
	 * @param ontologia
	 * @return String - Ontologia formatada
	 */
	public String formataOWL(String ontologia) {
		JSONObject owl = new JSONObject(ontologia);
		
		String tipoFormato = owl.getString("outformat");
		
		OWLOntologyManager man = OWLManager.createOWLOntologyManager();
		OWLDocumentFormat formato = getFormatoSaidaOntologia(tipoFormato);
		try {
			OWLOntology owlOntology = geraOWLdeString(ontologia);
			if (owlOntology != null) {
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				man.saveOntology(owlOntology, formato, baos);
				return baos.toString();
			} else {
				return "Erro: null. Problema na validação de axiomas.";
			}	
		} catch (Exception e) {
			return "Ocorreu o seguinte erro: " + e.getMessage();
		}
	}
	
	/**
	 * Recebe os elementos da ontologia em String/JSON e transforma em ontologia tipada
	 * @param String - elementos da ontologia 
	 * @return OWLOntology 
	 * @throws OWLOntologyCreationException 
	 */
	public OWLOntology geraOWLdeString(String ontologia) throws OWLOntologyCreationException {
		JSONObject owl = new JSONObject(ontologia);

		String id = owl.getString("id");
		
		IRI iri = IRI.create(id);
		OWLOntologyManager owlManager = OWLManager.createOWLOntologyManager();
		OWLOntology owlOntology;

		owlOntology = owlManager.createOntology(iri);
		OWLDataFactory dataFactory = owlOntology.getOWLOntologyManager().getOWLDataFactory();
		
		Provider shortFormProvider = new Provider();
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
		OWLObjectProperty hasSynonym = dataFactory.getOWLObjectProperty(iri + "#hasSynonym");
		OWLClassExpression pessoaHasSononym = dataFactory.getOWLObjectSomeValuesFrom(hasSynonym, dataFactory.getOWLClass("#Pessoa"));
		OWLClass gente = dataFactory.getOWLClass(iri + "#Gente");
		OWLSubClassOfAxiom ax = dataFactory.getOWLSubClassOfAxiom(gente, pessoaHasSononym);
		owlManager.applyChange(new AddAxiom(owlOntology, ax));
		
		/**
		 * Trabalha se os axiomas declarativos
		 */
		JSONArray axiomas = new JSONArray();
		axiomas = owl.getJSONArray("ontoaxioms");
		ManchesterOWLSyntaxParser parser = OWLManager.createManchesterParser();
		parser.setOWLEntityChecker(entityChecker);

		try {
			for (int i = 0; i < axiomas.length(); i++) {
				parser.setStringToParse(axiomas.getString(i));
				owlOntology.addAxiom(parser.parseAxiom());
			}			
		} catch (Exception e) {
			System.out.println(e.getMessage());
			return null;
		}
		return owlOntology;
	}
	
	/**
	 * Retorna o tipo de formato para streaming de saida da ontologia
	 * @param String - formato
	 * @return OWLDocumentFormat
	 */
	public OWLDocumentFormat getFormatoSaidaOntologia(String formato) {
		OWLDocumentFormat documentFormat = null;

		if (formato.equals("OWL")) {
			documentFormat = new OWLXMLDocumentFormat();
		} else if (formato.equals("TURTLE")) {
			documentFormat = new TurtleDocumentFormat();
		} else if (formato.equals("SINTAXEDL")) {
			documentFormat = new DLSyntaxDocumentFormat();
		} else if (formato.equals("SINTAXEDLHTML")) {
			documentFormat = new DLSyntaxHTMLDocumentFormat();
		} else if (formato.equals("SINTAXEFUNCIONAL")) {
			documentFormat = new FunctionalSyntaxDocumentFormat();
		} else if (formato.equals("KRSS")) {
			documentFormat = new KRSS2DocumentFormat();
		} else if (formato.equals("DOCUMENTOLATEX")) {
			documentFormat = new LatexDocumentFormat();
		} else if (formato.equals("N3")) {
			documentFormat = new N3DocumentFormat();
		} else if (formato.equals("SINTAXEMANCHESTER")) {
			documentFormat = new ManchesterSyntaxDocumentFormat();
		} else if (formato.equals("NQUAD")) {
			documentFormat = new NQuadsDocumentFormat();
		} else if (formato.equals("NTRIPLA")) {
			documentFormat = new NTriplesDocumentFormat();
		} else if (formato.equals("OBO")) {
			documentFormat = new OBODocumentFormat();
		} else if (formato.equals("RDFJSON")) {
			documentFormat = new RDFJsonDocumentFormat();
		} else if (formato.equals("RDFJSONLD")) {
			documentFormat = new RDFJsonLDDocumentFormat();
		} else if (formato.equals("RDFXML")) {
			documentFormat = new RDFXMLDocumentFormat();
		} else if (formato.equals("RIOTURTLE")) {
			documentFormat = new RioTurtleDocumentFormat();
		} else if (formato.equals("RIORDFXML")) {
			documentFormat = new RioRDFXMLDocumentFormat();
		} else if (formato.equals("TRIG")) {
			documentFormat = new TrigDocumentFormat();
		} else if (formato.equals("TRIX")) {
			documentFormat = new TrixDocumentFormat();
		}
		return documentFormat;
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
		};
		
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
