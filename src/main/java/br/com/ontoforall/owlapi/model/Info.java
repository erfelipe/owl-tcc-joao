package br.com.ontoforall.owlapi.model;

public class Info {

	
	public String getInfo() {
		String texto =  "Métodos: " + System.lineSeparator() + 
						"GET  - https://whispering-gorge-06411.herokuapp.com/webapi/ontology        - retorna boolean" + System.lineSeparator() +
						"GET  - https://whispering-gorge-06411.herokuapp.com/webapi/ontology/info   - retorna string"  + System.lineSeparator() +
						"POST - https://whispering-gorge-06411.herokuapp.com/webapi/ontology/valid  - retorna boolean" + System.lineSeparator() +
						"POST - https://whispering-gorge-06411.herokuapp.com/webapi/ontology/format - retorna string " + System.lineSeparator() +
						"Modelo de dados para os metodos POST" + System.lineSeparator() + 
						"	 * {\n"
						+ "	 *   \"id\": \"https://onto4alleditor.com/pt/idDoProjeto\",\n"
						+ "	 *   \"formatosaida\": \"OWL\",\n"
						+ "	 *   \"classes\": [\"Pessoa\", \"Homem\", \"Mulher\"],\n"
						+ "	 *   \"axiomas\": [\"Homem subClassOf (Pessoa)\", \"Mulher subClassOf (Pessoa)\"],\n"
						+ "	 *   \"propriedades\": [\"hasPart\"]\n"
						+ "	 *	}";

		return texto;
	}
	
}
