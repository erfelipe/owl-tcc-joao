package br.com.ontoforall.owlapi.resources;

import br.com.ontoforall.owlapi.model.ElementosOWL;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("axioms")
public class AxiomValidator {
	
	
	
	@GET
	@Produces(MediaType.TEXT_PLAIN)
	public String isWorking() {
		return Boolean.TRUE.toString();	
	}
	
	
	@GET
	@Path("valid")
	@Produces(MediaType.APPLICATION_JSON)
	public String valid() {
		ElementosOWL elementos = new ElementosOWL();
		
		return elementos.validaOWL();
	}
	
	
}
