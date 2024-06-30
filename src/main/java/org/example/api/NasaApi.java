package org.example.api;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.example.image.Photo;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class NasaApi {
    private static final String API_URL = "https://api.nasa.gov/mars-photos/api/v1/rovers/curiosity/photos?sol=1000&api_key=DEMO_KEY";
    private static final int TIMEOUT = 5000;

    public List<Photo> getPhotos() {
        List<Photo> photos = new ArrayList<>();
        try {
            URL url = new URL(API_URL);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(TIMEOUT);
            connection.setReadTimeout(TIMEOUT);

            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuilder content = new StringBuilder();
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                content.append(inputLine);
            }
            in.close();

            JsonObject jsonObject = JsonParser.parseString(content.toString()).getAsJsonObject();
            JsonArray photosArray = jsonObject.getAsJsonArray("photos");

            Gson gson = new Gson();
            for (JsonElement element : photosArray) {
                Photo photo = gson.fromJson(element, Photo.class);
                photos.add(photo);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return photos;
    }
}
