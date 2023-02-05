package com.example.bestfilms.api;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.example.bestfilms.classes.DescriptionFilm;
import com.example.bestfilms.classes.CardFilm;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import java.net.URL;
import java.util.ArrayList;

import javax.net.ssl.HttpsURLConnection;

public class Request {

    private static final String API_KEY = "e30ffed0-76ab-4dd6-b41f-4c9da2b2735b";
    private static final String MAIN_URL = "https://kinopoiskapiunofficial.tech";
    private static final String GET_TOP_100_POPULAR_FILMS = "/api/v2.2/films/top?type=TOP_100_POPULAR_FILMS";
    private static final String GET_DESCRIPTION_FILM_BY_ID = "/api/v2.2/films/%s";

    private Request(){}

    public static ArrayList<CardFilm> loadListPopularFilms() throws IOException, JSONException {
        JSONObject json = sendGetRequest(GET_TOP_100_POPULAR_FILMS);
        return parseFilmsFromJson(json);
    }

    public static DescriptionFilm loadDescriptionFilmById(Long filmId) throws JSONException, IOException {
        JSONObject json = sendGetRequest(String.format(GET_DESCRIPTION_FILM_BY_ID, filmId));
        return parseDescriptionFromJson(json);
    }

    private static JSONObject sendGetRequest(String param) throws IOException, JSONException {
        URL url = new URL(MAIN_URL + param);
        HttpsURLConnection connection = (HttpsURLConnection)url.openConnection();
        connection.setRequestMethod("GET");
        connection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
        connection.setRequestProperty("X-API-KEY", API_KEY);
        connection.setReadTimeout(10000);

        if(connection.getResponseCode() != HttpsURLConnection.HTTP_OK){ return null; }

        InputStream in = new BufferedInputStream(connection.getInputStream());
        BufferedReader reader = new BufferedReader(new InputStreamReader(in));

        StringBuilder buf = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            buf.append(line);
        }
        connection.disconnect();

        return new JSONObject(buf.toString());
    }

    private static ArrayList<CardFilm> parseFilmsFromJson(JSONObject json) throws JSONException, IOException {
        if(json == null) return null;
        JSONArray films = json.getJSONArray("films");
        ArrayList<CardFilm> list = new ArrayList<>();
        for(int idx_f = 0; idx_f < films.length(); idx_f++){
            JSONObject film = films.getJSONObject(idx_f);
            long filmId = film.getInt("filmId");
            String nameRu = film.getString("nameRu");
            String year = film.getString("year");
            Bitmap poster = getBitmapFromUrl(film.getString("posterUrlPreview"));
            list.add(new CardFilm(filmId, nameRu, poster, year));
        }
        return list;
    }

    private static DescriptionFilm parseDescriptionFromJson(JSONObject json) throws JSONException, IOException {
        if(json == null) return null;
        String description = json.getString("description");
        String nameRu = json.getString("nameRu");
        Bitmap poster = getBitmapFromUrl(json.getString("posterUrl"));
        String[] genres = parseStringJSONArray(json.getJSONArray("genres"), "genre");
        String[] countries = parseStringJSONArray(json.getJSONArray("countries"), "country");
        return new DescriptionFilm(description, poster, nameRu, genres, countries);
    }

    private static Bitmap getBitmapFromUrl(String url) throws IOException {
        InputStream in = new URL(url).openStream();
        return BitmapFactory.decodeStream(in);
    }

    private static String[] parseStringJSONArray(JSONArray array, String key) throws JSONException {
        String[] items = new String[array.length()];
        for(int idx = 0; idx < array.length(); idx++){
            JSONObject item = array.getJSONObject(idx);
            items[idx] = item.getString(key);
        }
        return items;
    }



}
