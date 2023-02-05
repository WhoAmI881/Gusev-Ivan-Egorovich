package com.example.bestfilms.classes;

import android.graphics.Bitmap;


public class DescriptionFilm extends Film {
    private final String DELIMITER = ", ";

    private String description;
    private String[] genres;
    private String[] countries;

    public DescriptionFilm(String description, Bitmap poster, String nameRu, String[] genres, String[] countries){
        super(nameRu, poster);
        this.description = description;
        this.genres = genres;
        this.countries = countries;
    }

    public String getDescription() {
        return description;
    }

    public String getGenres() {
        return String.join(DELIMITER, genres);
    }

    public String getCountries() {
        return String.join(DELIMITER, countries);
    }


}
