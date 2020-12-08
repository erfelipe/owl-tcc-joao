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

@Path("ontologia")
public class Rotas {
	
	public Rotas() {
		
	}
	
	@GET
	@Produces(MediaType.TEXT_PLAIN)
	public String funciona() {
		return Boolean.TRUE.toString();	
	}
	
	@POST
	@Path("valid")
	@Produces(MediaType.TEXT_PLAIN)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response validacao(String ontologia) {
		ElementosOWL elementos = new ElementosOWL();
		String resp = elementos.validaOWL(ontologia);
		return Response.status(Status.ACCEPTED)
				.entity(resp)
				.build();
	}
	
	@POST
	@Path("format")
	@Produces(MediaType.TEXT_PLAIN)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response formata(String ontologia) {
		ElementosOWL elementos = new ElementosOWL();
		String resp = elementos.formataOWL(ontologia);
		return Response.status(Status.ACCEPTED)
				.entity(resp)
				.build();
	}
}
