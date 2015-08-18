package com.burfdevelopment.burfworld.Database;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.sql.Database;
import com.badlogic.gdx.sql.DatabaseCursor;
import com.badlogic.gdx.sql.DatabaseFactory;
import com.badlogic.gdx.sql.SQLiteGdxException;

/**
 * Created by burfies1 on 15/08/15.
 */



public class DatabaseTest {

    Database dbHandler;

    public static final String TABLE_COMMENTS = "comments";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_COMMENT = "comment";

    private static final String DATABASE_NAME = "comments.db";
    private static final int DATABASE_VERSION = 1;

    // Database creation sql statement
    private static final String DATABASE_CREATE = "create table if not exists "
            + TABLE_COMMENTS + "(" + COLUMN_ID
            + " integer primary key autoincrement, " + COLUMN_COMMENT
            + " text not null);";

    public DatabaseTest() {
        Gdx.app.log("DatabaseTest", "creation started");
        dbHandler = DatabaseFactory.getNewDatabase(DATABASE_NAME,
                DATABASE_VERSION, DATABASE_CREATE, null);

        dbHandler.setupDatabase();
        try {
            dbHandler.openOrCreateDatabase();
            dbHandler.execSQL(DATABASE_CREATE);
        } catch (SQLiteGdxException e) {
            e.printStackTrace();
        }

        Gdx.app.log("DatabaseTest", "created successfully");

        try {
            dbHandler
                    .execSQL("INSERT INTO comments ('comment') VALUES ('This is a test comment')");
        } catch (SQLiteGdxException e) {
            e.printStackTrace();
        }

        DatabaseCursor cursor = null;

        try {
            cursor = dbHandler.rawQuery("SELECT * FROM comments");
        } catch (SQLiteGdxException e) {
            e.printStackTrace();
        }
        while (cursor.next()) {
            Gdx.app.log("FromDb", String.valueOf(cursor.getString(1)));
        }

        try {
            dbHandler.closeDatabase();
        } catch (SQLiteGdxException e) {
            e.printStackTrace();
        }
        dbHandler = null;
        Gdx.app.log("DatabaseTest", "dispose");
    }
}
