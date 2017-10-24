package com.burfdevelopment.burfworld.Database

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.math.Vector3
import com.badlogic.gdx.sql.DatabaseCursor
import com.badlogic.gdx.sql.DatabaseFactory
import com.badlogic.gdx.sql.SQLiteGdxException
import com.badlogic.gdx.utils.Array

/**
 * Created by burfies1 on 21/10/2017.
 */

class DatabaseHelper {

    internal var dbHandler: com.badlogic.gdx.sql.Database? = null


    fun addChunk(x: Float, y: Float, z: Float, data: String) {

        if (findChunk(x, y, z) == false) {

            Gdx.app.log("DATABASE", "Adding $x $y $z ")

            val sql = "INSERT INTO " + TABLE_CHUNKS + " " +
                    "('" + COLUMN_CHUNK_DATA + "','" + COLUMN_CHUNK_X + "','" + COLUMN_CHUNK_Y + "', '" + COLUMN_CHUNK_Z + "') " + //,

                    "VALUES ('" + data + "','" + x + "','" + y + "','" + z + "')"

            try {
                dbHandler!!
                        .execSQL(sql)
            } catch (e: SQLiteGdxException) {
                e.printStackTrace()
            }

        }
    }


    fun updateChunk(x: Float, y: Float, z: Float, data: String) {
        val sql = "UPDATE " + TABLE_CHUNKS + " " +
                "SET '" + COLUMN_CHUNK_DATA + "' = '" + data + "' " +
                "WHERE x = '" + x + "' AND y = '" + y + "' and z = '" + z + "'"

        Gdx.app.log("DATABASE", "Updating $x $y $z ")

        try {
            dbHandler!!
                    .execSQL(sql)
        } catch (e: SQLiteGdxException) {
            e.printStackTrace()
        }

    }

    fun getHeightChunk(x: Float, z: Float): Array<Vector3> {
        var cursor: DatabaseCursor? = null
        val v = Array<Vector3>()

        try {
            cursor = dbHandler!!.rawQuery("SELECT * FROM $TABLE_CHUNKS WHERE x = '$x' and z = '$z'")
        } catch (e: SQLiteGdxException) {
            e.printStackTrace()
        }

        while (cursor!!.next()) {
            Gdx.app.log("FromDb", cursor.getString(0).toString() + " " + cursor.getString(1).toString() + " " + cursor.getString(3).toString() + " " + cursor.getString(4).toString())
            val r = Vector3(java.lang.Float.valueOf(cursor.getString(2))!!, java.lang.Float.valueOf(cursor.getString(3))!!, java.lang.Float.valueOf(cursor.getString(4))!!)
            v.add(r)
        }

        return v
    }

    fun findChunk(x: Float, y: Float, z: Float): Boolean {
        var cursor: DatabaseCursor? = null

        Gdx.app.log("DATABASE", "Finding $x $y $z ")

        try {
            cursor = dbHandler!!.rawQuery("SELECT * FROM $TABLE_CHUNKS WHERE x = '$x' AND y = '$y' and z = '$z'")
        } catch (e: SQLiteGdxException) {
            e.printStackTrace()
        }

        return (cursor!!.count > 0)

    }

    fun getChunk(x: Float, y: Float, z: Float): String? {
        var cursor: DatabaseCursor? = null

        Gdx.app.log("DATABASE", "GETTING $x $y $z ")

        try {
            cursor = dbHandler!!.rawQuery("SELECT * FROM $TABLE_CHUNKS WHERE x = '$x' AND y = '$y' and z = '$z'")
        } catch (e: SQLiteGdxException) {
            e.printStackTrace()
        }

        if (cursor!!.count > 0) {
            cursor.next()
            return cursor.getString(1).toString()
        } else {
            return null
        }
    }

    private fun listAll() {
        var cursor: DatabaseCursor? = null

        try {
            cursor = dbHandler!!.rawQuery("SELECT * FROM " + TABLE_CHUNKS)
        } catch (e: SQLiteGdxException) {
            e.printStackTrace()
        }

        Gdx.app.log("DATABASE", "COUNT " + cursor!!.count)

    }

    fun closeDatabase() {
        try {
            dbHandler!!.closeDatabase()
        } catch (e: SQLiteGdxException) {
            e.printStackTrace()
        }

        dbHandler = null
        Gdx.app.log("DATABASE", "dispose")
    }

    init {
        Gdx.app.log("DATABASE", "creation started")
        dbHandler = DatabaseFactory.getNewDatabase(DATABASE_NAME,
                DATABASE_VERSION, DATABASE_CREATE, null)

        dbHandler!!.setupDatabase()
        try {
            dbHandler!!.openOrCreateDatabase()
            dbHandler!!.execSQL(DATABASE_CREATE)
        } catch (e: SQLiteGdxException) {
            e.printStackTrace()
        }

        Gdx.app.log("DATABASE", "created successfully")

        //addChunk(0, 0, 0, "1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1");
        //getChunk(5, 5, 5);
        //updateChunk(3,"burf");
        //getChunk(5, 5, 5);
        //closeDatabase();

        listAll()
    }

    companion object {

        val TABLE_CHUNKS = "chunks"
        val COLUMN_ID = "_id"
        val COLUMN_CHUNK_DATA = "chunk_data"
        val COLUMN_CHUNK_X = "x"
        val COLUMN_CHUNK_Y = "y"
        val COLUMN_CHUNK_Z = "z"

        private val DATABASE_NAME = "database.db"
        private val DATABASE_VERSION = 1

        // DatabaseHelper creation sql statement
        private val DATABASE_CREATE = ("create table if not exists "
                + TABLE_CHUNKS + "(" + COLUMN_ID
                + " integer primary key autoincrement, "
                + COLUMN_CHUNK_DATA + " text not null,"
                + COLUMN_CHUNK_X + " int,"
                + COLUMN_CHUNK_Y + " int,"
                + COLUMN_CHUNK_Z + " int"
                + ");")
    }
}
