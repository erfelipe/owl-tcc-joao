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
						+ "	 *   \"outformat\": \"OWL\",\n"
						+ "	 *   \"ontoclass\": [\"Pessoa\", \"Homem\", \"Mulher\"],\n"
						+ "	 *   \"ontoaxioms\": [\"Homem subClassOf (Pessoa)\", \"Mulher subClassOf (Pessoa)\"],\n"
						+ "	 *   \"ontoproperties\": [\"hasPart\"]\n"
						+ "	 *	}";

		return texto;
	}
	
}
