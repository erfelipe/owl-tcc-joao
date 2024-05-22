package examples;

public class Example {

	public static void main(String[] args) {

		// ElementosOWL elementos = new ElementosOWL();
		// Example ex = new Example();
		
		// String owl = elementos.formataOWL(ex.textFromOntoForAll());

		// System.out.println(owl);
	}
	
	public String textFromOntoForAll() {	
		String texto = "{\n"
				+ "    \"id\": \"https://onto4alleditor.com/pt/idDoProjeto\",\n"
				+ "    \"outformat\": \"OWL\",\n"
				+ "    \"ontoclass\": [\"Pessoa\", \"Homem\", \"Mulher\", \"Human\"],\n"
				+ "    \n"
				+ "    \"ontoaxioms\": [\n"
				+ "    \"Homem subClassOf (Pessoa)\",   "
				+ "    \"Mulher subClassOf (Pessoa)\",  "
				+ "    \"Homem DisjointWith (Mulher)\", "
				+ "    \"Pessoa EquivalentTo (Human)\", "
				+ "       "				
				+ "    ], "
				+ " "
				+ "    \"ontoproperties\": [\"hasPart\"]\n"
				+ "}";
		
		return texto;
	}
	
}
