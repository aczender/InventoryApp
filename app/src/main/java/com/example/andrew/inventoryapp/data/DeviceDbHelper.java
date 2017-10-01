package com.example.andrew.inventoryapp.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.andrew.inventoryapp.data.InventoryContract.DeviceEntry;


/**
 * Created by Andrew on 2017.08.13..
 */

public class DeviceDbHelper extends SQLiteOpenHelper {
    public static final String LOG_TAG = DeviceDbHelper.class.getSimpleName();

    /** Name of the database file */
    private static final String DATABASE_NAME = "store.db";

    /**
     * Database version. If you change the database schema, you must increment the database version.
     */
    private static final int DATABASE_VERSION = 1;


    public DeviceDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    /**
     * This is called when the database is created for the first time.
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create a String that contains the SQL statement to create the pets table
        String SQL_CREATE_STORE_TABLE =  "CREATE TABLE " + DeviceEntry.TABLE_NAME + " ("
                + DeviceEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + DeviceEntry.COLUMN_DEVICE_NAME + " TEXT NOT NULL, "
                + DeviceEntry.COLUMN_DEVICE_QUANTITY + " INTEGER NOT NULL DEFAULT 0, "
                + DeviceEntry.COLUMN_DEVICE_PRICE + " INTEGER NOT NULL DEFAULT 0);";

        // Execute the SQL statement
        db.execSQL(SQL_CREATE_STORE_TABLE);
    }

    /**
     * This is called when the database needs to be upgraded.
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // The database is still at version 1, so there's nothing to do be done here.
    }
}