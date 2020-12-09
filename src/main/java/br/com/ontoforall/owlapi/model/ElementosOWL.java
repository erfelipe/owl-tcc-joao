package br.com.ontoforall.owlapi.model;

import java.io.ByteArrayOutputStream;
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
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLDocumentFormat;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
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
	 * Recebe os elementos da ontologia em String e transforma em ontologia tipada
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
		} else if (formato.equals("SINTAXEMANCHERTER")) {
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
	
}
