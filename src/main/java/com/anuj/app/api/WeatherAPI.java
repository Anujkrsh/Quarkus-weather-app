package com.anuj.app.api;

import io.smallrye.mutiny.Uni;
import io.vertx.ext.web.client.WebClient;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/weather")
public class WeatherApi {

    private final WebClient client;

    public WeatherResource(Vercatx vertx) {
        this.client = WebClient.create(vertx);
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Uni<Response> getWeather(@QueryParam("city") String city) {
        String apiKey = "your_api_key_here";
        String url = "https://api.openweathermap.org/data/2.5/weather?q=" + city + "&appid=" + apiKey;

        return client
                .getAbs(url)
                .timeout(5000) // Set timeout to 5 seconds (5000 milliseconds)
                .send()
                .onItem().transform(response -> {
                    if (response.statusCode() == 200) {
                        String body = response.bodyAsString();
                        return Response.ok(body).build();
                    } else {
                        return Response.status(response.statusCode()).entity(response.statusMessage()).build();
                    }
                })
                .onFailure().recoverWithItem(failure -> Response.status(504).entity("Request timed out after 5 seconds").build());
    }
}
