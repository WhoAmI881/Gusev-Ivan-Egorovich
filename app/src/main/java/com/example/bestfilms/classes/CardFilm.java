package com.example.bestfilms.classes;

import android.graphics.Bitmap;

public class CardFilm extends Film{
    private long filmId;
    private String year;
    private boolean favourites;

    public CardFilm(long filmId, String nameRu, Bitmap poster, String year){
        super(nameRu, poster);
        this.filmId = filmId;
        this.year = year;
    }

    public CardFilm(long filmId, String nameRu, byte[] poster, String year){
        super(nameRu, poster);
        this.filmId = filmId;
        this.year = year;
    }

    public String getYear() {
        return year;
    }

    public long getFilmId() {
        return filmId;
    }

    public void setFavourites(boolean favourites) {
        this.favourites = favourites;
    }

    public boolean isFavourites() {
        return favourites;
    }
}
