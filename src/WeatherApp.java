import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.Scanner;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

//fetching latest weather
//external API
public class WeatherApp {
    public static JSONObject getWeatherData(String locationName){
        //get location geolocation API
        JSONArray locationData = getLocationData(locationName);

        JSONObject location= (JSONObject) locationData.get(0);
        double latitude =(double) location.get("latitude");
        double longitude =(double) location.get("longitude");
        //build API request URL with loaction coordinates
        String urlString="https://api.open-meteo.com/v1/forecast?latitude="+ latitude+ "&longitude="+longitude
                +"&hourly=temperature_2m,relative_humidity_2m,weather_code,wind_speed_10m";
        try{
            HttpURLConnection conn= fetchApiResponse(urlString);
            //check for response status
            //200 means success
            if(conn.getResponseCode()!=200){
                System.out.println("Error: could not connect to api");
                return null;
            }
            //store resulting json data
            StringBuilder resultJson = new StringBuilder();
            Scanner scanner= new Scanner(conn.getInputStream());
            while(scanner.hasNext()){
                //read and store into the string builder
                resultJson.append(scanner.nextLine());
            }
            //close scanner
            scanner.close();
            //close url
            conn.disconnect();
            //parse through our data
            JSONParser parser = new JSONParser();
            JSONObject resultJsonObj = (JSONObject) parser.parse(String.valueOf(resultJson));

            //retrieve hourly data
            JSONObject hourly= (JSONObject) resultJsonObj.get("hourly");
            //current hours data
            JSONArray time = (JSONArray) hourly.get("time");
            int index= findIndexOfCurrentTime(time);

            JSONArray temperatureData= (JSONArray) hourly.get("temperature_2m");
            double temperature=(double) temperatureData.get(index);

            //get weather code
            JSONArray weathercode=(JSONArray) hourly.get("weathercode");
            String weatherCondition=convertWeatherCode((long)weathercode.get(index));

            //get humidity
            JSONArray relativeHumidity=(JSONArray) hourly.get("relativehumidity");
            long humidity= (long) relativeHumidity.get(index);
            //get windspeed
            JSONArray windspeedData=(JSONArray) hourly.get("windspeed_10m");
            double windspeed=(double) windspeedData.get(index);

            //build weather json data object
            JSONObject weatherData= new JSONObject();
            weatherData.put("temperature", temperature);
            weatherData.put("weather_condition", weatherCondition);
            weatherData.put("humidity", humidity);
            weatherData.put("windspeed", windspeed);

            return weatherData;

        }catch(Exception e){
            e.printStackTrace();

        }

        return null;
    }
    public static JSONArray getLocationData(String locationName){
        //replace whitespace to adhere to API's request format
        locationName = locationName.replaceAll(" ", "+");

        //build API url with location paramter
        String urlString = "https://geocoding-api.open-meteo.com/v1/search?name=" +
                locationName + "&count=10&language=en&format=json";

        try{
            HttpURLConnection conn = fetchApiResponse(urlString);
            //check response status
            //200 means successful
            if(conn.getResponseCode() != 200) {
                System.out.println("Error: could not connect to API");
                return null;
            }
            else {
                // store API results
                StringBuilder resultJson= new StringBuilder();
                Scanner scanner= new Scanner(conn.getInputStream());

                //read and store the resulting data into sgtring builder
                while(scanner.hasNext()){
                    resultJson.append(scanner.nextLine());
                }

                //close scanner
                scanner.close();

                //close url
                conn.disconnect();

                //parse JSON string into JSON obj
                JSONParser parser = new JSONParser();
                JSONObject resultsJsonObj = (JSONObject) parser.parse(String.valueOf(resultJson));

                //get list of location data the API generated from the location name
                JSONArray locationData = (JSONArray) resultsJsonObj.get("results");
                return locationData;
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
        //couldnt find location
        return null;
    }
    private static HttpURLConnection fetchApiResponse(String urlString){
        try{
            //attempt to create connection
            URL url = new URL(urlString);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            //set request method
            conn.setRequestMethod("GET");
            //connect to API
            conn.connect();
            return conn;
        }
        catch(IOException e){
            e.printStackTrace();
        }
        //could not make connection
        return null;
    }

    private static int findIndexOfCurrentTime(JSONArray timeList){
        String currentTime=getCurrentTime();
        //iterate through time list and see which matches current time
        for (int i = 0; i < timeList.size(); i++) {
            String time=(String) timeList.get(i);
            if(time.equalsIgnoreCase(currentTime)){
                return i;
            }
        }

        return 0;
    }
    public static String getCurrentTime(){
        //get current date and time
        LocalDateTime currentDateTime = LocalDateTime.now();

        //format date
        DateTimeFormatter formatter= DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH':00'");

        String formattedDateTime= currentDateTime.format(formatter);

        return formattedDateTime;
    }

    private static String convertWeatherCode(long weathercode){
    String weatherCondition="";
    if(weathercode==0L){
        weatherCondition="Clear";
    }else if(weathercode <=3L && weathercode > 0L){
        weatherCondition="Cloudy";
    }
    else if((weathercode <=67L && weathercode >= 51L)
        || (weathercode <=99L && weathercode >= 80L)){
        weatherCondition="Rain";
    }
    else if(weathercode <=77L && weathercode >=71L){
        weatherCondition="Snow";
    }
    return weatherCondition;
    }

}
