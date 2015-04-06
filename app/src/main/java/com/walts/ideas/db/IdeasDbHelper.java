package com.walts.ideas.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

public class IdeasDbHelper extends SQLiteOpenHelper {

    //, created DATETIME DEFAULT CURRENT_TIMESTAMP
    private static final String SQL_CREATE_TABLES = "CREATE TABLE idea (id INTEGER PRIMARY KEY, title TEXT, desc TEXT)";
    private static final String SQL_DELETE_ENTRIES = "DROP TABLE IF EXISTS ideas";

    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "Ideas.db";

    public IdeasDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_TABLES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(SQL_DELETE_ENTRIES);
        onCreate(db);
    }

    public long insertIdea(Idea idea) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("title", idea.title);
        values.put("desc", idea.desc);
        long newId = db.insert("idea", null, values);
        db.close();
        return newId;
    }

    public int updateIdea(Idea idea) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("title", idea.title);
        values.put("desc", idea.desc);
        return db.update("idea", values, "id  = ?", new String[] {String.valueOf(idea.id)});
    }

    public int deleteIdea(long id) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete("idea", "id = ?", new String[]{String.valueOf(id)});
    }

    public Idea getIdea(long id) {
        SQLiteDatabase db = this.getWritableDatabase();
        Idea idea = null;
        String selectQuery = "SELECT * FROM idea WHERE id = '" + id + "'";
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                idea = new Idea(cursor.getString(1), cursor.getString(2));
                idea.id = cursor.getLong(0);
            } while (cursor.moveToNext());
        }
        return idea;
    }

    public ArrayList<Idea> getAllIdeas() {
        ArrayList<Idea> ideaEntities = new ArrayList<>();
        String selectQuery = "SELECT * FROM idea";
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                Idea idea = new Idea(cursor.getString(1), cursor.getString(2));
                idea.id = cursor.getLong(0);
                ideaEntities.add(idea);
            } while (cursor.moveToNext());
        }
        return ideaEntities;
    }

}
