package com.walts.ideas.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.walts.ideas.helpers.FileHelpers;
import com.walts.ideas.helpers.SHA1;

import java.util.ArrayList;
import java.util.List;

public class IdeasDbHelper extends SQLiteOpenHelper {

    private static IdeasDbHelper dbHelper = null;

    private static final String TAG = "IdeasDbHelper";

    private static final int DATABASE_VERSION = 2;
    private static final String DATABASE_NAME = "Ideas.db";

    private static final String LOREM_IPSUM_FILE = "lorem-ipsum.txt";
    private static final String CREATE_TABLES_SQL_FILE = "CREATE_TABLES.sql";
    private static final String DROP_TABLES_SQL_FILE = "DROP_TABLES.sql";

    private Context context;

    //http://stackoverflow.com/questions/18147354/sqlite-connection-leaked-although-everything-closed
    public static IdeasDbHelper getInstance(Context context) {
        if (dbHelper == null) {
            dbHelper = new IdeasDbHelper(context.getApplicationContext());
            //dbHelper.createTestIdeas(context);
            dbHelper.createTestQuestions(context);
        }
        return dbHelper;
    }

    private void createTestQuestions(Context context) {
        int numberOfQuestionsToCreate = 20;
        for (int i = 0; i < numberOfQuestionsToCreate; i++) {
            Question question = new Question("Question #" + (i + 1));
            insertQuestion(question);
        }
    }

    private long insertQuestion(Question question) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("question", question.question);
        return db.insert("question", null, values);
    }

    private IdeasDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTablesSqlString = FileHelpers.getTextFileContent(context, CREATE_TABLES_SQL_FILE);
        String[] queries = createTablesSqlString.split(";");
        for (String query : queries) {
            db.execSQL(query);
        }
    }

    private void createTestIdeas(Context context) {
        String loremIpsumString = FileHelpers.getTextFileContent(context, LOREM_IPSUM_FILE);
        int numberOfIdeasToCreate = 20;
        for (int i = 0; i < numberOfIdeasToCreate; i++) {
            Idea idea = new Idea("Lorem ipsum idea #" + (i + 1), loremIpsumString);
            insertIdea(idea);
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        String createTablesSqlString = FileHelpers.getTextFileContent(context, DROP_TABLES_SQL_FILE);
        db.execSQL(createTablesSqlString);
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

    public List<Question> getAllQuestions() {
        ArrayList<Question> questionEntities = new ArrayList<>();
        String selectQuery = "SELECT * FROM question";
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                Question question = new Question(cursor.getString(1));
                question.id = cursor.getLong(0);
                question.createdDate = cursor.getString(2);
                questionEntities.add(question);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return questionEntities;
    }
}
