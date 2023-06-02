package pl.gawryszewski.edp_projekt.model;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.apache.commons.io.IOUtils;
import pl.gawryszewski.edp_projekt.application.Config;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

public class ApiCommunicator {
    private static final String CLIENT_ID = Config.getInstance().getClientId();
    private static final String CLIENT_SECRET = Config.getInstance().getClientSecret();
    private static final String TOKEN_URL = "https://allegro.pl.allegrosandbox.pl/auth/oauth/token";
    private String accessToken;

    public ApiCommunicator(){
        try {
            setAccessToken();
            //System.out.println(accessToken);
        } catch (IOException e){
            e.printStackTrace();
            System.out.println("Could not get accessToken");
        }
    }

    private void setAccessToken() throws IOException {
        try {
            URL url = new URL(TOKEN_URL);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");

            String authString = CLIENT_ID + ":" + CLIENT_SECRET;
            String authHeader = "Basic " + Base64.getEncoder().encodeToString(authString.getBytes(StandardCharsets.UTF_8));
            connection.setRequestProperty("Authorization", authHeader);
            connection.setDoOutput(true);

            String postData = "grant_type=client_credentials";
            byte[] postDataBytes = postData.getBytes(StandardCharsets.UTF_8);

            try (OutputStream outputStream = connection.getOutputStream()) {
                outputStream.write(postDataBytes);
            }
            String responseString = getResponseBody(connection);
            int startIndex = responseString.indexOf(":\"") + 2;
            int endIndex = responseString.indexOf("\"", startIndex);
            accessToken = responseString.substring(startIndex, endIndex);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public List<Item> getOffersByProductName(String productName, int amount){
        List<Item> offers = new ArrayList<>();
        try {
            String encodedProductName = URLEncoder.encode(productName, StandardCharsets.UTF_8);
            String urlWithParams = "https://api.allegro.pl.allegrosandbox.pl/offers/listing" + "?phrase=" + encodedProductName + "&limit="+amount;
            URL url = new URL(urlWithParams);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Authorization", "Bearer " + accessToken);
            connection.setRequestProperty("Accept", "application/vnd.allegro.public.v1+json");
            String responseString = getResponseBody(connection);
            if(responseString!=null) {
                Gson gson = new Gson();
                JsonObject json = gson.fromJson(responseString, JsonObject.class);
                if (json.has("items")) {
                    JsonObject items = json.getAsJsonObject("items");
                    JsonArray regular = items.get("regular").getAsJsonArray();
                    JsonArray promoted = items.get("promoted").getAsJsonArray();
                    addOffersFromCategory(regular, offers);
                    addOffersFromCategory(promoted, offers);
                }
            }
            else {
                System.out.println("Could not get offers");
            }
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Could not get offers");
        }
        return offers;
    }
    public void addOffersFromCategory(JsonArray offerType, List<Item> offers){
        for(int i = 0; i<offerType.size(); i++){
            try {
                JsonObject offer = offerType.get(i).getAsJsonObject();
                String name = offer.get("name").getAsString();
                String offerUrl;
                try {
                    offerUrl = offer.get("vendor").getAsJsonObject().get("url").getAsString();
                } catch (NullPointerException e) {
                    offerUrl = "None";
                }
                String imagePath = offer.get("images").getAsJsonArray().
                        get(0).getAsJsonObject().get("url").getAsString();
                URL imageUrl = new URL(imagePath);
                byte[] imageData = null;
                try{
                    InputStream inputStream = imageUrl.openStream();
                    imageData = IOUtils.toByteArray(inputStream);
                } catch (IOException e){
                    System.out.println("Could not get image");
                }
                String price = offer.get("sellingMode").getAsJsonObject().
                        get("price").getAsJsonObject().
                        get("amount").getAsString();
                Item item = new Item(name, price, offerUrl, imageData);
                offers.add(item);
            } catch (IOException e){
                e.printStackTrace();
                System.out.println("Could not add offer");
            }
        }
    }
    public String getResponseBody(HttpURLConnection connection){
        String responseString = null;
        try{
            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                StringBuilder response = new StringBuilder();
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }
                }
                responseString = response.toString();
                System.out.println(responseString);
            }
            else {
                throw new IOException("HTTP error: " + responseCode);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return responseString;
    }
}
