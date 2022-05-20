package br.com.ontoforall.owlapi.resources;

import br.com.ontoforall.owlapi.model.OntologyExporter;
import br.com.ontoforall.owlapi.model.Info;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.Provider;

import org.json.JSONException;
import org.json.JSONObject;

@Path("ontology")
public class Rotas {

	public Rotas() {

	}

	@GET
	@Produces(MediaType.TEXT_PLAIN)
	public String funciona() {
		return Boolean.TRUE.toString();
	}

	@GET
	@Path("info")
	@Produces(MediaType.TEXT_PLAIN)
	public Response info() {
		Info info = new Info();

		return Response.status(Status.ACCEPTED).entity(info.getInfo()).header("Access-Control-Allow-Origin", "*").build();
	}

	@POST
	@Path("valid")	
	@Produces(MediaType.TEXT_PLAIN)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response validacao(String ontologia) throws JSONException, Exception {
//		ElementosOWL elementos = new ElementosOWL(new JSONObject(ontologia));
//		String resp = elementos.validaOWL();
		String resp = Boolean.TRUE.toString();
		
		return Response.status(Status.ACCEPTED).entity(resp).header("Access-Control-Allow-Origin", "https://onto4all.com").build();
	}

	@POST
	@Path("valid2")	
	@Produces(MediaType.TEXT_PLAIN)
	@Consumes(MediaType.TEXT_PLAIN)
	public Response validacao2(String ontologia) throws JSONException, Exception {
//		ElementosOWL elementos = new ElementosOWL(new JSONObject(ontologia));
//		String resp = elementos.validaOWL();
		String resp = Boolean.TRUE.toString();
		return Response.status(Status.ACCEPTED).entity(resp).build();
	}

	@POST
	@Path("format")
	@Produces(MediaType.TEXT_PLAIN)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response formata(String ontology) throws JSONException, Exception {
		OntologyExporter ont = new OntologyExporter(new JSONObject(ontology));
		String resp = ont.exportOntology();
		return Response.status(Status.ACCEPTED).entity(resp).header("Access-Control-Allow-Origin", "*").build();
	}

/*	@POST
	@Path("read")
	@Produces(MediaType.TEXT_PLAIN)
	@Consumes(MediaType.APPLICATION_XML)
	public Response readFromOWL(String ontologia) throws OWLOntologyCreationException {
		ElementosOWL elementos = new ElementosOWL();
		String resp = elementos.readFromOWL(ontologia);
		return Response.status(Status.ACCEPTED)
						.entity(resp)
						.build();
	}*/
}

