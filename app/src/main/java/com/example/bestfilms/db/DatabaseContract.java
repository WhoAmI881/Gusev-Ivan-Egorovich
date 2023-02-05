package com.example.bestfilms.db;

public final class DatabaseContract {

    private DatabaseContract() {}

    public static final String TABLE_NAME = "films";
    public static final String COLUMN_NAME_ID = "filmId";
    public static final String COLUMN_NAME_NAME_RU = "nameRu";
    public static final String COLUMN_NAME_YEAR = "year";
    public static final String COLUMN_NAME_POSTER = "poster";

    public static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + TABLE_NAME + " (" +
                    COLUMN_NAME_ID + " INTEGER PRIMARY KEY," +
                    COLUMN_NAME_NAME_RU + " TEXT," +
                    COLUMN_NAME_YEAR + " TEXT," +
                    COLUMN_NAME_POSTER + " BLOB);";

    public static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + TABLE_NAME;
}
