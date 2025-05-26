package com.example.myapplication;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "UserDB";
    private static final int DATABASE_VERSION = 4; // Updated version for new table

    // Users table
    private static final String TABLE_USERS = "users";
    private static final String KEY_ID = "id";
    private static final String KEY_USERNAME = "username";
    private static final String KEY_PASSWORD = "password";

    // Check-in table
    private static final String TABLE_CHECKIN = "checkin";
    private static final String KEY_CHECKIN_ID = "checkin_id";
    private static final String KEY_CHECKIN_USERNAME = "username";
    private static final String KEY_CHECKIN_DATE = "checkin_date";

    private static final String CREATE_TABLE_USERS = "CREATE TABLE " + TABLE_USERS + "("
            + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + KEY_USERNAME + " TEXT UNIQUE,"
            + KEY_PASSWORD + " TEXT)";

    private static final String CREATE_TABLE_CHECKIN = "CREATE TABLE " + TABLE_CHECKIN + "("
            + KEY_CHECKIN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + KEY_CHECKIN_USERNAME + " TEXT,"
            + KEY_CHECKIN_DATE + " TEXT,"
            + "FOREIGN KEY(" + KEY_CHECKIN_USERNAME + ") REFERENCES " + TABLE_USERS + "(" + KEY_USERNAME + "))";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_USERS);
        db.execSQL(CREATE_TABLE_CHECKIN);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 4) {
            db.execSQL(CREATE_TABLE_CHECKIN);
        }
    }

    // Existing user methods
    public void addUser(String username, String password) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_USERNAME, username);
        values.put(KEY_PASSWORD, password);
        db.insert(TABLE_USERS, null, values);
        db.close();
    }

    public boolean checkUser(String username, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        String[] columns = {KEY_ID};
        String selection = KEY_USERNAME + " = ?" + " AND " + KEY_PASSWORD + " = ?";
        String[] selectionArgs = {username, password};
        Cursor cursor = db.query(TABLE_USERS, columns, selection, selectionArgs, null, null, null);
        int count = cursor.getCount();
        cursor.close();
        db.close();
        return count > 0;
    }

    public boolean checkUsername(String username) {
        SQLiteDatabase db = this.getReadableDatabase();
        String[] columns = {KEY_ID};
        String selection = KEY_USERNAME + " = ?";
        String[] selectionArgs = {username};
        Cursor cursor = db.query(TABLE_USERS, columns, selection, selectionArgs, null, null, null);
        int count = cursor.getCount();
        cursor.close();
        db.close();
        return count > 0;
    }

    // Check-in methods
    public boolean checkInToday(String username) {
        String today = getCurrentDate();
        SQLiteDatabase db = this.getReadableDatabase();

        String[] columns = {KEY_CHECKIN_ID};
        String selection = KEY_CHECKIN_USERNAME + " = ? AND " + KEY_CHECKIN_DATE + " = ?";
        String[] selectionArgs = {username, today};

        Cursor cursor = db.query(TABLE_CHECKIN, columns, selection, selectionArgs, null, null, null);
        boolean hasCheckedIn = cursor.getCount() > 0;
        cursor.close();
        db.close();

        return hasCheckedIn;
    }

    public boolean performCheckIn(String username) {
        if (checkInToday(username)) {
            return false; // Already checked in today
        }

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_CHECKIN_USERNAME, username);
        values.put(KEY_CHECKIN_DATE, getCurrentDate());

        long result = db.insert(TABLE_CHECKIN, null, values);
        db.close();

        return result != -1;
    }

    public int getTotalCheckInDays(String username) {
        SQLiteDatabase db = this.getReadableDatabase();
        String[] columns = {KEY_CHECKIN_ID};
        String selection = KEY_CHECKIN_USERNAME + " = ?";
        String[] selectionArgs = {username};

        Cursor cursor = db.query(TABLE_CHECKIN, columns, selection, selectionArgs, null, null, null);
        int count = cursor.getCount();
        cursor.close();
        db.close();

        return count;
    }

    private String getCurrentDate() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        return sdf.format(new Date());
    }
}