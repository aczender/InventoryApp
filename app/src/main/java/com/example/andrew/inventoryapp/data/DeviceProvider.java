package com.example.andrew.inventoryapp.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;

import com.example.andrew.inventoryapp.data.InventoryContract.DeviceEntry;

/**
 * {@link ContentProvider} for Inventory app.
 */
public class DeviceProvider extends ContentProvider {

    /**
     * Tag for the log messages
     */
    public static final String LOG_TAG = DeviceProvider.class.getSimpleName();

    /**
     * URI matcher code for the content URI for the devices table
     */
    private static final int DEVICES = 100;

    /**
     * URI matcher code for the content URI for a single device in the devices table
     */
    private static final int DEVICE_ID = 101;

    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    // Static initializer. This is run the first time anything is called from this class.
    static {
        // The calls to addURI() go here, for all of the content URI patterns that the provider
        // should recognize. All paths added to the UriMatcher have a corresponding code to return
        // when a match is found.


        sUriMatcher.addURI(InventoryContract.CONTENT_AUTHORITY, InventoryContract.PATH_INVENTORY,
                DEVICES);

        sUriMatcher.addURI(InventoryContract.CONTENT_AUTHORITY, InventoryContract.PATH_INVENTORY +
                "/#", DEVICE_ID);
    }

    /**
     * Database helper object
     */
    private DeviceDbHelper mDbHelper;


    /**
     * Initialize the provider and the database helper object.
     */
    @Override
    public boolean onCreate() {
        mDbHelper = new DeviceDbHelper(getContext());
        return true;
    }

    /**
     * Perform the query for the given URI. Use the given projection, selection, selection arguments, and sort order.
     */
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
                        String sortOrder) {
        // Get readable database
        SQLiteDatabase database = mDbHelper.getReadableDatabase();

        // This cursor will hold the result of the query
        Cursor cursor;

        // Figure out if the URI matcher can match the URI to a specific code
        int match = sUriMatcher.match(uri);
        switch (match) {
            case DEVICES:
                // For the devices code, query the devices table directly with the given
                // projection, selection, selection arguments, and sort order. The cursor
                // could contain multiple rows of the devices table.
                cursor = database.query(DeviceEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;
            case DEVICE_ID:
                // For the device_ID code, extract out the ID from the URI.
                // For an example URI such as "content://com.example.android.devices/devices/3",
                // the selection will be "_id=?" and the selection argument will be a
                // String array containing the actual ID of 3 in this case.
                //
                // For every "?" in the selection, we need to have an element in the selection
                // arguments that will fill in the "?". Since we have 1 question mark in the
                // selection, we have 1 String in the selection arguments' String array.
                selection = DeviceEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};

                // This will perform a query on the devices table where the _id equals 3 to return a
                // Cursor containing that row of the table.
                cursor = database.query(DeviceEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;
            default:
                throw new IllegalArgumentException("Cannot query unknown URI " + uri);
        }

        // Set notification URI on the Cursor,
        // so we know what content URI the Cursor was created for.
        // If the data at this URI changes, then we know we need to update the Cursor.
        cursor.setNotificationUri(getContext().getContentResolver(), uri);

        // Return the cursor
        return cursor;
    }


    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case DEVICES:
                return insertDevice(uri, contentValues);
            default:
                throw new IllegalArgumentException("Insertion is not supported for " + uri);
        }
    }

    /**
     * Insert a device into the database with the given content values. Return the new content URI
     * for that specific row in the database.
     */
    private Uri insertDevice(Uri uri, ContentValues values) {
        // Check that the name is not null
        String name = values.getAsString(DeviceEntry.COLUMN_DEVICE_NAME);
        if (name == null) {
            throw new IllegalArgumentException("Device requires a name");
        }

        // If the amount is provided, check that it's greater than or equal to 0
        Integer quantity = values.getAsInteger(DeviceEntry.COLUMN_DEVICE_QUANTITY);
        if (quantity != null && quantity < 0) {
            throw new IllegalArgumentException("Device requires valid quantity");
        }

        // If the price is provided, check that it's greater than or equal to 0
        Integer price = values.getAsInteger(DeviceEntry.COLUMN_DEVICE_PRICE);
        if (price != null && price < 0) {
            throw new IllegalArgumentException("Device requires valid price");
        }

        // Get writeable database
        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        // Insert the new device with the given values
        long id = database.insert(DeviceEntry.TABLE_NAME, null, values);
        // If the ID is -1, then the insertion failed. Log an error and return null.
        if (id == -1) {
            Log.e(LOG_TAG, "Failed to insert row for " + uri);
            return null;
        }

        // Notify all listeners that the data has changed for the device content URI
        //uri: content://com.example.andrew.inventoryapp/inventoryapp
        getContext().getContentResolver().notifyChange(uri, null);

        // Return the new URI with the ID (of the newly inserted row) appended at the end
        return ContentUris.withAppendedId(uri, id);
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        // Get writeable database
        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        // Track the number of rows that were deleted
        int rowsDeleted;

        final int match = sUriMatcher.match(uri);
        switch (match) {
            case DEVICES:
                // Delete all rows that match the selection and selection args
                rowsDeleted = database.delete(DeviceEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case DEVICE_ID:
                // Delete a single row given by the ID in the URI
                selection = DeviceEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                rowsDeleted = database.delete(DeviceEntry.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Deletion is not supported for " + uri);
        }

        // If 1 or more rows were deleted, then notify all listeners that the data at the
        // given URI has changed
        if (rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        // Return the number of rows deleted
        return rowsDeleted;
    }

    @Override
    public int update(Uri uri, ContentValues contentValues, String selection,
                      String[] selectionArgs) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case DEVICES:
                return updateDevice(uri, contentValues, selection, selectionArgs);
            case DEVICE_ID:
                // For the device_ID code, extract out the ID from the URI,
                // so we know which row to update. Selection will be "_id=?" and selection
                // arguments will be a String array containing the actual ID.
                selection = DeviceEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                return updateDevice(uri, contentValues, selection, selectionArgs);
            default:
                throw new IllegalArgumentException("Update is not supported for " + uri);
        }
    }

    /**
     * Update devices in the database with the given content values. Apply the changes to the rows
     * specified in the selection and selection arguments (which could be 0 or 1 or more devices).
     * Return the number of rows that were successfully updated.
     */
    private int updateDevice(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        // If the {@link deviceEntry#COLUMN_device_NAME} key is present,
        // check that the name value is not null.
        if (values.containsKey(DeviceEntry.COLUMN_DEVICE_NAME)) {
            String name = values.getAsString(DeviceEntry.COLUMN_DEVICE_NAME);
            if (name == null) {
                throw new IllegalArgumentException("Device requires a name");
            }
        }


        // If the {@link deviceEntry#COLUMN_device_WEIGHT} key is present,
        // check that the weight value is valid.
        if (values.containsKey(DeviceEntry.COLUMN_DEVICE_QUANTITY)) {
            // Check that the weight is greater than or equal to 0 kg
            Integer quantity = values.getAsInteger(DeviceEntry.COLUMN_DEVICE_QUANTITY);
            if (quantity != null && quantity < 0) {
                throw new IllegalArgumentException("Device requires valid quantity");
            }
        }

        if (values.containsKey(DeviceEntry.COLUMN_DEVICE_PRICE)) {
            // Check that the weight is greater than or equal to 0 kg
            Integer price = values.getAsInteger(DeviceEntry.COLUMN_DEVICE_PRICE);
            if (price != null && price < 0) {
                throw new IllegalArgumentException("Device requires valid price");
            }
        }

        // If there are no values to update, then don't try to update the database
        if (values.size() == 0) {
            return 0;
        }

        // Otherwise, get writeable database to update the data
        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        // Perform the update on the database and get the number of rows affected
        int rowsUpdated = database.update(DeviceEntry.TABLE_NAME, values, selection, selectionArgs);

        // If 1 or more rows were updated, then notify all listeners that the data at the
        // given URI has changed
        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        // Return the number of rows updated
        return rowsUpdated;
    }


    @Override
    public String getType(Uri uri) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case DEVICES:
                return DeviceEntry.CONTENT_LIST_TYPE;
            case DEVICE_ID:
                return DeviceEntry.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalStateException("Unknown URI " + uri + " with match " + match);
        }
    }
}