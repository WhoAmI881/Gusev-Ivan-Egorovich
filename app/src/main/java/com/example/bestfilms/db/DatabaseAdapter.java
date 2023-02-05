package com.example.bestfilms.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.bestfilms.classes.CardFilm;

import java.util.ArrayList;


public class DatabaseAdapter {

    private DatabaseHelper mHelper;
    private SQLiteDatabase mDataBase;

    public DatabaseAdapter(Context context){
        mHelper = new DatabaseHelper(context.getApplicationContext());
    }

    public ArrayList<CardFilm> getAllFilms(){
        ArrayList<CardFilm> cardFilms = new ArrayList<>();
        Cursor cursor = getAllEntries();
        while (cursor.moveToNext()){
            cursor.getColumnIndex(DatabaseContract.COLUMN_NAME_ID);
            long filmId = cursor.getLong(cursor.getColumnIndexOrThrow(DatabaseContract.COLUMN_NAME_ID));
            String nameRu = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseContract.COLUMN_NAME_NAME_RU));
            String year = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseContract.COLUMN_NAME_YEAR));
            byte[] poster = cursor.getBlob(cursor.getColumnIndexOrThrow(DatabaseContract.COLUMN_NAME_POSTER));
            CardFilm cardFilm = new CardFilm(filmId, nameRu, poster, year);
            cardFilm.setFavourites(true);
            cardFilms.add(cardFilm);
        }
        cursor.close();
        return cardFilms;
    }

    private Cursor getAllEntries(){
        String[] columns = new String[] {
                DatabaseContract.COLUMN_NAME_ID,
                DatabaseContract.COLUMN_NAME_NAME_RU,
                DatabaseContract.COLUMN_NAME_YEAR,
                DatabaseContract.COLUMN_NAME_POSTER
        };
        return mDataBase.query(DatabaseContract.TABLE_NAME, columns,
                null, null, null, null, null);
    }

    public void insert(CardFilm cardFilm){
        ContentValues cv = new ContentValues();
        cv.put(DatabaseContract.COLUMN_NAME_ID, cardFilm.getFilmId());
        cv.put(DatabaseContract.COLUMN_NAME_NAME_RU, cardFilm.getNameRu());
        cv.put(DatabaseContract.COLUMN_NAME_YEAR, cardFilm.getYear());
        cv.put(DatabaseContract.COLUMN_NAME_POSTER, cardFilm.getPosterBytes());

        mDataBase.insertWithOnConflict(DatabaseContract.TABLE_NAME, null, cv, SQLiteDatabase.CONFLICT_IGNORE);
    }

    public long delete(long filmId){
        String whereClause = DatabaseContract.COLUMN_NAME_ID + " = ?";
        String[] whereArgs = new String[]{String.valueOf(filmId)};
        return mDataBase.delete(DatabaseContract.TABLE_NAME, whereClause, whereArgs);
    }

    public void open(){
        mDataBase = mHelper.getWritableDatabase();
    }

    public void close(){
        mHelper.close();
    }


}