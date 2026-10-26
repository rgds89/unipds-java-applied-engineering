package br.com.rogersilva;

import org.eclipse.microprofile.health.HealthCheck;
import org.eclipse.microprofile.health.HealthCheckResponse;
import org.eclipse.microprofile.health.Readiness;
import org.eclipse.microprofile.rest.client.inject.RestClient;

@Readiness
public class ReadinessCheck implements HealthCheck {
    @RestClient
    StarWarsService starWarsService;

    @Override
    public HealthCheckResponse call() {
        return starWarsService.getStarships().contains(StarWarsService.MSG_ERROR) ? HealthCheckResponse.down("ERROR") : HealthCheckResponse.up("OK");
    }
}
