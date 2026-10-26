package br.com.rogersilva;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.rest.client.inject.RestClient;

@Path("starwars")
@Produces(MediaType.APPLICATION_JSON)
public class StarWarsResource {
    @RestClient
    StarWarsService starWarsService;

    @GET
    @Path("starships")
    public  String starships(){
        return starWarsService.getStarships();
    }
}
