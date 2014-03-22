package ru.moto64.rescue_notifier;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;
import android.util.Log;

/**
 * Created by ehorohorin on 23/03/14.
 */
public class IncidentDataBase extends SQLiteOpenHelper implements BaseColumns {

    private static final String DATABASE_NAME = "moto64_incidents_database.db";
    private static final int DATABASE_VERSION = 1;
    public static final String TABLE_NAME = "incidents_table";
    public static final String INCIDENT_DATE = "incident_date";
    public static final String INCIDENT_TITLE = "incident_title";
    public static final String INCIDENT_TEXT = "incident_text";

    private static final String SQL_CREATE_ENTRIES = "CREATE TABLE "
            + TABLE_NAME + " (" + IncidentDataBase._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + INCIDENT_DATE + " TEXT" + ","
            + INCIDENT_TITLE + " VARCHAR(255)" + ","
            + INCIDENT_TEXT + " TEXT" +
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
}
