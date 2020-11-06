package br.com.ontoforall.owlapi.resources;

import br.com.ontoforall.owlapi.model.ElementosOWL;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

@Path("axioms")
public class AxiomValidator {
	
	public AxiomValidator() {
		
	}
	
	@GET
	@Produces(MediaType.TEXT_PLAIN)
	public String isWorking() {
		return Boolean.TRUE.toString();	
	}
	
	@GET
	@Path("teste")
	@Produces(MediaType.APPLICATION_JSON)
	public String valid() {
		ElementosOWL elementos = new ElementosOWL();
		
		return elementos.validaOWL("");
	}
	
	@POST
	@Path("validacao")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response validacao(String ontologia) {
		ElementosOWL elementos = new ElementosOWL();
		//System.out.println(ontologia);
		String resp = elementos.validaOWL(ontologia);
		//return elementos.validaOWL(ontologia);
		System.out.println("Vai resposta... ");
		return Response.status(Status.ACCEPTED)
				.entity(resp)
				.build();
	}
	
}
