package br.com.rogersilva;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Path("/unipds")
@Produces(MediaType.TEXT_PLAIN)
@Consumes(MediaType.TEXT_PLAIN)
public class UnipdsResource {
    private int i = 0;

    @GET
    public int getI(){
        return i;
    }

    @GET
    @Path("getIDiferentao")
    public String getIDiferentao(){return LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));}

    @POST
    public void addI(){ i++;}

    @DELETE
    public void removeI(){ i--;}

    @PUT
    public void setI(int i){ this.i = i;}
}
