package hu.progmasters.mcpweatherserver;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
public class WeatherService {

    private final RestClient restClient;
    private final ObjectMapper objectMapper;
    private final String apiKey;

    public WeatherService(RestClient.Builder builder, @Value("${openweather.api.key}") String apiKey) {
        this.restClient = builder
                .baseUrl("https://api.openweathermap.org")
                .defaultHeader("User-Agent", "szfilep@gmail.com")
                .build();
        this.objectMapper = new ObjectMapper();
        this.apiKey = apiKey;
    }

    @Tool(description = "Get weather forecast by location")
    public String getWeatherForecastByLocation(@ToolParam(description = "Latitude") double latitude, @ToolParam(description = "Longitude") double longitude) {
        try {
            String uri = "/data/2.5/weather?lat=" + latitude + "&lon=" + longitude + "&appid=" + apiKey + "&units=metric";
            String response = restClient.get().uri(uri).retrieve().body(String.class);
            JsonNode root = objectMapper.readTree(response);
            JsonNode main = root.path("main");

            double temp = main.path("temp").asDouble();
            String tempUnit = "C"; // since metric
            JsonNode wind = root.path("wind");
            String windSpeed = wind.path("speed").asText() + " m/s";
            double windDeg = wind.path("deg").asDouble();
            String windDirection = getWindDirection(windDeg);
            JsonNode weather = root.path("weather");
            String description = weather.isArray() && !weather.isEmpty() ? weather.get(0).path("description").asText() : "No description";

            return "Temperature: " + temp + " " + tempUnit + "\n" +
                   "Wind speed: " + windSpeed + "\n" +
                   "Wind direction: " + windDirection + "\n" +
                   "Detailed forecast: " + description;
        } catch (Exception e) {
            return "Error retrieving weather forecast: " + e.getMessage();
        }
    }

    @Tool(description = "Get weather alerts by location")
    public String getAlerts(@ToolParam(description = "Latitude") double latitude, @ToolParam(description = "Longitude") double longitude) {
        // OpenWeatherMap 2.5 API does not provide alerts
        return "Weather alerts are not available with the current API (OpenWeatherMap 2.5).";
    }

    private String getWindDirection(double degrees) {
        String[] directions = {"N", "NNE", "NE", "ENE", "E", "ESE", "SE", "SSE", "S", "SSW", "SW", "WSW", "W", "WNW", "NW", "NNW"};
        int index = (int) Math.round(degrees / 22.5) % 16;
        return directions[index];
    }
}
