package com.example.bestfilms.classes;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.ByteArrayOutputStream;

public class Film {

    private String nameRu;
    private Bitmap poster;

    public Film(String nameRu, Bitmap poster){
        this.nameRu = nameRu;
        this.poster = poster;
    }

    public Film(String nameRu, byte[] poster){
        this.nameRu = nameRu;
        this.poster = BitmapFactory.decodeByteArray(poster, 0, poster.length);;
    }

    public String getNameRu() {
        return nameRu;
    }

    public byte[] getPosterBytes(){
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        poster.compress(Bitmap.CompressFormat.JPEG, 0, stream);
        return stream.toByteArray();
    }

    public Bitmap getPoster() {
        return poster;
    }
}
