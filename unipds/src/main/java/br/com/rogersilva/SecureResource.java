package br.com.rogersilva;

import jakarta.annotation.security.RolesAllowed;
import jakarta.enterprise.context.RequestScoped;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import org.eclipse.microprofile.jwt.Claim;
import org.eclipse.microprofile.jwt.Claims;

@Path("/secure")
@RequestScoped
public class SecureResource {
    @Claim(standard = Claims.preferred_username)
    private String userName;

    @GET
    @Path("/claim")
    @RolesAllowed("Subscriber")
    public String getClaim() {
        return userName;
    }
}
