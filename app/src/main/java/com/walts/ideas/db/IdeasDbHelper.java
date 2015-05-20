package com.walts.ideas.db;

import android.content.ContentValues;
import android.content.Context;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.walts.ideas.SHA1;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class IdeasDbHelper extends SQLiteOpenHelper {

    private static IdeasDbHelper dbHelper = null;

    private static final String TAG = "IdeasDbHelper";

    private static final String SQL_CREATE_TABLES = "CREATE TABLE idea (" +
            "id INTEGER PRIMARY KEY, " +
            "title TEXT, desc TEXT, " +
            "created_date DATE DEFAULT (datetime('now','localtime')), " +
            "password TEXT, " +
            "latitude REAL, " +
            "longitude REAL, " +
            "address TEXT" +
            ")";
    private static final String SQL_DELETE_ENTRIES = "DROP TABLE IF EXISTS ideas";

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "Ideas.db";

    //http://stackoverflow.com/questions/18147354/sqlite-connection-leaked-although-everything-closed
    public static IdeasDbHelper getInstance(Context context) {
        if (dbHelper == null) {
            dbHelper = new IdeasDbHelper(context.getApplicationContext());
            dbHelper.createTestIdeas(context);
        }
        return dbHelper;
    }

    private IdeasDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_TABLES);
    }

    private void createTestIdeas(Context context) {
        try {
            AssetManager assetManager = context.getAssets();
            InputStream inputStream = assetManager.open("lorem-ipsum.txt");

            BufferedReader bufferedReader = null;
            StringBuilder stringBuilder = new StringBuilder();

            try {
                bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    stringBuilder.append(line);
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (bufferedReader != null) {
                    try {
                        bufferedReader.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            String loremIpsumString = stringBuilder.toString();
            int numberOfIdeasToCreate = 20;
            for (int i = 0; i < numberOfIdeasToCreate; i++) {
                Idea idea = new Idea("Lorem ipsum idea #" + (i + 1), loremIpsumString);
                insertIdea(idea);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
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
        values.put("latitude", idea.latitude);
        values.put("longitude", idea.longitude);
        values.put("address", idea.address);
        return db.insert("idea", null, values);
    }

    public int updateIdea(Idea idea) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("title", idea.title);
        values.put("desc", idea.desc);
        values.put("latitude", idea.latitude);
        values.put("longitude", idea.longitude);
        values.put("address", idea.address);
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
                idea.createdDate = cursor.getString(3);
                idea.password = cursor.getString(4);
                idea.latitude = cursor.getDouble(5);
                idea.longitude = cursor.getDouble(6);
                idea.address = cursor.getString(7);
            } while (cursor.moveToNext());
        }
        cursor.close();
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
                idea.createdDate = cursor.getString(3);
                idea.password = cursor.getString(4);
                ideaEntities.add(idea);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return ideaEntities;
    }

    public int removePassword(Idea idea) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("password", "");
        return db.update("idea", values, "id  = ?", new String[] {String.valueOf(idea.id)});
    }

    public int addPassword(Idea idea) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("password", SHA1.sha1Hash(idea.password));
        return db.update("idea", values, "id  = ?", new String[] {String.valueOf(idea.id)});
    }
}
