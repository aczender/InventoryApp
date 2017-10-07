package com.example.andrew.inventoryapp.data;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * API Contract for the Inventoryapp
 */
public final class InventoryContract {

    // To prevent someone from accidentally instantiating the contract class,
    // give it an empty constructor.
    private InventoryContract() {
    }

    /**
     * The "Content authority" is a name for the entire content provider, similar to the
     * relationship between a domain name and its website.  A convenient string to use for the
     * content authority is the package name for the app, which is guaranteed to be unique on the
     * device.
     */
    public static final String CONTENT_AUTHORITY = "com.example.andrew.inventoryapp";

    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public static final String PATH_INVENTORY = "inventory";

    /**
     * Inner class that defines constant values for the store database table.
     * Each entry in the table represents a single device.
     */
    public static final class DeviceEntry implements BaseColumns {

        /**
         * The content URI to access the device data in the provider
         */
        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_INVENTORY);


        public static final String CONTENT_LIST_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_INVENTORY;

        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_INVENTORY;

        /**
         * Name of database table for devices
         */
        public final static String TABLE_NAME = "devices";

        public final static String _ID = BaseColumns._ID;


        public final static String COLUMN_DEVICE_NAME = "name";

        public final static String COLUMN_DEVICE_QUANTITY = "quantity";

        public final static String COLUMN_DEVICE_PRICE = "price";
        public final static String COLUMN_CONTACT_INFO = "info";
        public final static String COLUMN_DEVICE_PICTURE = "picture";

    }

}