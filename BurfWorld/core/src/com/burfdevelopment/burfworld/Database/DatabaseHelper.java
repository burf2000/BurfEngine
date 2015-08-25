package com.burfdevelopment.burfworld.Database;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.math.Vector;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.sql.Database;
import com.badlogic.gdx.sql.DatabaseCursor;
import com.badlogic.gdx.sql.DatabaseFactory;
import com.badlogic.gdx.sql.SQLiteGdxException;
import com.badlogic.gdx.utils.Array;

/**
 * Created by burfies1 on 15/08/15.
 */



public class DatabaseHelper {

    com.badlogic.gdx.sql.Database dbHandler;

    public static final String TABLE_CHUNKS = "chunks";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_CHUNK_DATA = "chunk_data";
    public static final String COLUMN_CHUNK_X = "x";
    public static final String COLUMN_CHUNK_Y = "y";
    public static final String COLUMN_CHUNK_Z = "z";

    private static final String DATABASE_NAME = "database.db";
    private static final int DATABASE_VERSION = 1;

    // DatabaseHelper creation sql statement
    private static final String DATABASE_CREATE = "create table if not exists "
            + TABLE_CHUNKS + "(" + COLUMN_ID
            + " integer primary key autoincrement, "
            + COLUMN_CHUNK_DATA + " text not null,"
            + COLUMN_CHUNK_X + " int,"
            + COLUMN_CHUNK_Y + " int,"
            + COLUMN_CHUNK_Z + " int"
            + ");";




    public void addChunk (float x, float y, float z, String data)
    {
        String sql =  "INSERT INTO " + TABLE_CHUNKS + " " +
                "('"+ COLUMN_CHUNK_DATA +"','"+COLUMN_CHUNK_X+"','"+COLUMN_CHUNK_Y+"', '"+COLUMN_CHUNK_Z+"') " + //,
                "VALUES ('" + data +"','"+ x +"','"+ y +"','"+ z +"')";

        try {
            dbHandler
                    .execSQL(sql);
        } catch (SQLiteGdxException e) {
            e.printStackTrace();
        }
    }

    public void updateChunk(float x, float y, float z, String data)
    {
        String sql =  "UPDATE " + TABLE_CHUNKS + " " +
                "SET '"+ COLUMN_CHUNK_DATA +"' = '" + data + "' " +
                "WHERE x = '" + x +"' AND y = '" + y +"' and z = '" + z + "'";

        try {
            dbHandler
                    .execSQL(sql);
        } catch (SQLiteGdxException e) {
            e.printStackTrace();
        }
    }

    public Array<Vector3> getHeightChunk(float x, float z)
    {
        DatabaseCursor cursor = null;
        Array<Vector3> v = new Array<Vector3>();

        try {
            cursor = dbHandler.rawQuery("SELECT * FROM " + TABLE_CHUNKS + " WHERE x = '" + x + "' and z = '" + z + "'" );
        } catch (SQLiteGdxException e) {
            e.printStackTrace();
        }

        while (cursor.next()) {
            Gdx.app.log("FromDb", String.valueOf(cursor.getString(0)) + " " + String.valueOf(cursor.getString(1)) + " " + String.valueOf(cursor.getString(3)) + " " + String.valueOf(cursor.getString(4)));
            Vector3 r = new Vector3(Float.valueOf(cursor.getString(2)),Float.valueOf(cursor.getString(3)),Float.valueOf(cursor.getString(4)));
            v.add(r);
        }

        return v;
    }


    public String getChunk(float x, float y, float z)
    {
        DatabaseCursor cursor = null;

        try {
            cursor = dbHandler.rawQuery("SELECT * FROM " + TABLE_CHUNKS + " WHERE x = '" + x +"' AND y = '" + y +"' and z = '" + z + "'" );
        } catch (SQLiteGdxException e) {
            e.printStackTrace();
        }

//        while (cursor.next()) {
//            Gdx.app.log("FromDb", String.valueOf(cursor.getString(0)) + " "  + String.valueOf(cursor.getString(1)) + " "  + String.valueOf(cursor.getString(3)) + " "  + String.valueOf(cursor.getString(4)));
//        }

        if (cursor.getCount() > 0)
        {
            cursor.next();
            return String.valueOf(cursor.getString(1));
        }
        else
        {
            return null;
        }
    }

    public void listAll()
    {
        DatabaseCursor cursor = null;

        try {
            cursor = dbHandler.rawQuery("SELECT * FROM " + TABLE_CHUNKS);
        } catch (SQLiteGdxException e) {
            e.printStackTrace();
        }
        while (cursor.next()) {
            Gdx.app.log("FromDb", String.valueOf(cursor.getString(1)) + " "  + String.valueOf(cursor.getString(2)) + " "  + String.valueOf(cursor.getString(3)) + " "  + String.valueOf(cursor.getString(4)));
        }
    }

    public void closeDatabase()
    {
        try {
            dbHandler.closeDatabase();
        } catch (SQLiteGdxException e) {
            e.printStackTrace();
        }
        dbHandler = null;
        Gdx.app.log("DatabaseHelper", "dispose");
    }


    public DatabaseHelper() {
        Gdx.app.log("DatabaseHelper", "creation started");
        dbHandler = DatabaseFactory.getNewDatabase(DATABASE_NAME,
                DATABASE_VERSION, DATABASE_CREATE, null);

        dbHandler.setupDatabase();
        try {
            dbHandler.openOrCreateDatabase();
            dbHandler.execSQL(DATABASE_CREATE);
        } catch (SQLiteGdxException e) {
            e.printStackTrace();
        }

        Gdx.app.log("DatabaseHelper", "created successfully");

        //addChunk(5, 5, 5, "Simon");
        //getChunk(5, 5, 5);
        //updateChunk(3,"burf");
        //getChunk(5, 5, 5);
        //closeDatabase();
    }
}
