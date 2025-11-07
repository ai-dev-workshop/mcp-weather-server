Let’s implement a WeatherService.java that uses a REST client to query the data from the https://openweathermap.org/api API.
Create a WeatherService @Service class. Create a RestClient that calss api.weather.gov with szfilep@gmail.com email in the header.

Use this API  https://api.openweathermap.org/data/2.5/weather?lat={lat}&lon={lon}&appid={API key}
Create a @Tool: getWeatherForecastByLocation(double latitude, double longitude) which returns the detailed forecast:
- Temperature and unit
- Wind speed and direction
- Detailed forecast description

