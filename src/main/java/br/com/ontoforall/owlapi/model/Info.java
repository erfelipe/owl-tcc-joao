package br.com.ontoforall.owlapi.model;

public class Info {

	
	public String getInfo() {
		String texto =  "Metodos: " + System.lineSeparator() + 
						"GET  - http://200.17.70.211:13951/owlapi/webapi/ontology        - retorna boolean" + System.lineSeparator() +
						"GET  - http://200.17.70.211:13951/owlapi/webapi/ontology/info   - retorna string"  + System.lineSeparator() +
						"POST - http://200.17.70.211:13951/owlapi/webapi/ontology/valid  - retorna boolean" + System.lineSeparator() +
						"POST - http://200.17.70.211:13951/owlapi/webapi/ontology/format - retorna string " + System.lineSeparator() +
						"" + System.lineSeparator() +
						"Modelo de dados antigo para os metodos POST acima" 										+ System.lineSeparator() + 
						"	  {\n"
						+ "	    \"id\": \"https://onto4alleditor.com/pt/idDoProjeto\",\n"
						+ "	    \"outformat\": \"OWL\",\n"
						+ "	    \"ontoclass\": [\"Pessoa\", \"Homem\", \"Mulher\"],\n"
						+ "	    \"ontoaxioms\": [\"Homem subClassOf (Pessoa)\", \"Mulher subClassOf (Pessoa)\"],\n"
						+ "	    \"ontoproperties\": [\"hasPart\"]\n"
						+ "	 }" 																				 + System.lineSeparator() 
		
						+ "POST - http://200.17.70.211:13951/owlapi/webapi/ontology/read - retorna string/json " + System.lineSeparator() 
						+ "O endpoint /read  recebe um arquivo OWL/XML serializado em String ";

		return texto;
	}
	
}
