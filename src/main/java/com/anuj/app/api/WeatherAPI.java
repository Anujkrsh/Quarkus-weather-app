package com.anuj.app.api;

import io.smallrye.mutiny.Uni;
import io.vertx.ext.web.client.WebClient;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;


@Path("/weather")
public class WeatherAPI {

    @Inject
    WebClient client;

    private static final String API_URL = "https://api.openweathermap.org/data/2.5/weather";
    private static final String API_KEY = "your_api_key_here"; // Replace with your actual API key

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Uni<Response> getWeather(@QueryParam("city") String city) {
        String url = API_URL + "?q=" + city + "&appid=" + API_KEY + "&units=metric";

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