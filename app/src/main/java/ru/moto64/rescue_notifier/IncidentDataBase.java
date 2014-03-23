package ru.moto64.rescue_notifier;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ehorohorin on 23/03/14.
 */
public class IncidentDataBase extends SQLiteOpenHelper implements BaseColumns {

    private static final String DATABASE_NAME = "moto64_incidents_database.db";
    private static final int DATABASE_VERSION = 2;
    public static final String TABLE_NAME = "incidents_table";
    public static final String INCIDENT_DATE = "incident_date";
    public static final String INCIDENT_TITLE = "incident_title";
    public static final String INCIDENT_TEXT = "incident_text";
    private static final String NEWS_URL = "news_url";
    private static final String IS_READ = "is_read" ;

    private static final String SQL_CREATE_ENTRIES = "CREATE TABLE "
            + TABLE_NAME + " (" + IncidentDataBase._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + INCIDENT_DATE + " TEXT" + ","
            + INCIDENT_TITLE + " VARCHAR(255)" + ","
            + INCIDENT_TEXT + " TEXT" + ","
            + NEWS_URL + " VARCHAR(255)" + ","
            + IS_READ + " INTEGER" +

            ");";

    private static final String SQL_DELETE_ENTRIES = "DROP TABLE IF EXISTS "
            + TABLE_NAME;

    public IncidentDataBase(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_ENTRIES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w("LOG_TAG", "Upgrading database from " + oldVersion
                + " to " + newVersion + ", that will purge existing data");
        // Удаляем предыдущую таблицу при апгрейде
        db.execSQL(SQL_DELETE_ENTRIES);
        // Создаём новый экземпляр таблицы
        onCreate(db);
    }

    // Adding new incident
    void addIncident(Incident incident) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(INCIDENT_DATE, incident.getDate().toString()); // Incident Date
        values.put(INCIDENT_TITLE, incident.getTitle()); // Incident Title
        values.put(INCIDENT_TEXT, incident.getText());

        // Inserting Row
        db.insert(TABLE_NAME, null, values);
        db.close(); // Closing database connection
    }

    public IncidentDataBase(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    // Getting single incident
    Incident getIncident(int id) {
        SQLiteDatabase db = this.getReadableDatabase();

        // FIXME: change SQL
        Cursor cursor = db.query(TABLE_NAME, new String[] { _ID,
                        INCIDENT_DATE, INCIDENT_TITLE, INCIDENT_TEXT, NEWS_URL, IS_READ }, _ID + "=?",
                new String[] { String.valueOf(id) }, null, null, null, null);
        if (cursor != null)
            cursor.moveToFirst();

        Incident incident = new Incident(Integer.parseInt(cursor.getString(0)),
                cursor.getString(1), cursor.getString(2), cursor.getString(3), cursor.getString(4), cursor.getInt(5));
        return incident;
    }

    // Getting All Incidents
    public List<Incident> getAllIncidents() {
        List<Incident> incidentList = new ArrayList<Incident>();

        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_NAME;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                int id = Integer.parseInt(cursor.getString(0));
                String date = cursor.getString(1);
                String title = cursor.getString(2);
                String text = cursor.getString(3);
                String url = cursor.getString(4);
                int isRead = cursor.getInt(5);
                Incident incident = new Incident(id, date, title, text, url, isRead);

                String name = cursor.getString(1) +"\n"+ cursor.getString(2);
                // ArrayofName -- ArrayList
                // AndroidSQLiteTutorialActivity.ArrayofName.add(name);
                // Adding contact to list
                incidentList.add(incident);
            } while (cursor.moveToNext());
        }

        return incidentList;
    }

    // Updating single incident
    public int updateIncident(Incident incident) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(_ID, incident.getID());
        values.put(INCIDENT_DATE, incident.getDate());
        values.put(INCIDENT_TITLE, incident.getTitle());
        values.put(INCIDENT_TEXT, incident.getText());
        values.put(NEWS_URL, incident.getUrl());
        values.put(IS_READ, incident.getIsRead());

        // updating row
        return db.update(TABLE_NAME, values, _ID + " = ?",
                new String[] { String.valueOf(incident.getID()) });
    }

    // Deleting single contact
    public void deleteIncident(Incident incident) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_NAME, _ID + " = ?",
                new String[] { String.valueOf(incident.getID()) });
        db.close();
    }


    // Getting incidents Count
    public int getIncidentCount() {
        String countQuery = "SELECT  * FROM " + TABLE_NAME;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        cursor.close();

        // return count
        return cursor.getCount();
    }

}
