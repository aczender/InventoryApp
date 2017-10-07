package com.example.andrew.inventoryapp;

import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.andrew.inventoryapp.data.InventoryContract.DeviceEntry;


import static com.example.andrew.inventoryapp.data.InventoryContract.DeviceEntry.COLUMN_CONTACT_INFO;
import static com.example.andrew.inventoryapp.data.InventoryContract.DeviceEntry.COLUMN_DEVICE_NAME;
import static com.example.andrew.inventoryapp.data.InventoryContract.DeviceEntry.COLUMN_DEVICE_QUANTITY;

public class DeviceDetailActivity extends AppCompatActivity
        implements LoaderManager.LoaderCallbacks<Cursor> {

    /**
     * Tag for log messages
     */
    private static final String LOG_TAG = DeviceDetailActivity.class.getName();

    /**
     * Identifier for the pet data loader
     */
    private static final int DEVICE_LOADER = 0;

    private Uri mCurrentDeviceUri;
    private TextView mNameTextView;
    private TextView mQuantityTextView;
    private TextView mPriceTextView;
    private TextView mContactTextView;
    private ImageView mPictureImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_layout);

        // Examine the intent that was used to launch this activity
        Intent intent = getIntent();
        mCurrentDeviceUri = intent.getData();

        mNameTextView = (TextView) findViewById(R.id.display_device_name);
        mQuantityTextView = (TextView) findViewById(R.id.display_device_quantity);
        mPriceTextView = (TextView) findViewById(R.id.display_device_price);
        mContactTextView = (TextView) findViewById(R.id.display_contact_info);
        mPictureImageView = (ImageView) findViewById(R.id.display_picture);

        // Kick off the loader
        getLoaderManager().initLoader(DEVICE_LOADER, null, this);

    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        // Define a projection that specifies the columns from the table we care about.
        String[] projection = {
                DeviceEntry._ID,
                DeviceEntry.COLUMN_DEVICE_NAME,
                DeviceEntry.COLUMN_DEVICE_QUANTITY,
                DeviceEntry.COLUMN_DEVICE_PRICE,
                DeviceEntry.COLUMN_CONTACT_INFO,
                DeviceEntry.COLUMN_DEVICE_PICTURE};

        // This loader will execute the ContentProvider's query method on a background thread
        return new CursorLoader(this,   // Parent activity context
                mCurrentDeviceUri,   // Provider content URI to query
                projection,             // Columns to include in the resulting Cursor
                null,                   // No selection clause
                null,                   // No selection arguments
                null);                  // Default sort order
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, final Cursor cursor) {
        // Proceed with moving to the first row of the cursor and reading data from it
        // (This should be the only row in the cursor)
        if (cursor.moveToFirst()) {
            // Find the columns of device attributes that we're interested in
            int nameColumnIndex = cursor.getColumnIndex(COLUMN_DEVICE_NAME);
            int quantityColumnIndex = cursor.getColumnIndex(COLUMN_DEVICE_QUANTITY);
            int priceColumnIndex = cursor.getColumnIndex(DeviceEntry.COLUMN_DEVICE_PRICE);
            int contactColumnIndex = cursor.getColumnIndex(DeviceEntry.COLUMN_CONTACT_INFO);
            int pictureColumnIndex = cursor.getColumnIndex(DeviceEntry.COLUMN_DEVICE_PICTURE);


            // Extract out the value from the Cursor for the given column index
            String name = cursor.getString(nameColumnIndex);
            int quantity = cursor.getInt(quantityColumnIndex);
            int price = cursor.getInt(priceColumnIndex);
            String contact = cursor.getString(contactColumnIndex);
            String picture = cursor.getString(pictureColumnIndex);

            // Update the views on the screen with the values from the database
            mNameTextView.setText(name);
            mQuantityTextView.setText(Integer.toString(quantity));
            mPriceTextView.setText(Integer.toString(price));
            mContactTextView.setText(contact);
            mPictureImageView.setImageURI(Uri.parse(picture));
        }

        // Find the buttons which will be clicked on
        Button minusButton = (Button) findViewById(R.id.minus_button);
        Button plusButton = (Button) findViewById(R.id.plus_button);
        Button deleteButton = (Button) findViewById(R.id.delete_button);
        Button orderButton = (Button) findViewById(R.id.order_button);

        minusButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Perform action on click
                Log.i(LOG_TAG, "TEST: Minus onClick called");

                //get the Uri for the current device
                int itemIdColumnIndex = cursor.getColumnIndex(DeviceEntry._ID);
                final long itemId = cursor.getLong(itemIdColumnIndex);
                Uri mCurrentDeviceUri = ContentUris.withAppendedId(DeviceEntry.CONTENT_URI, itemId);

                // Find the columns of device attributes that we're interested in
                int quantityColumnIndex = cursor.getColumnIndex(COLUMN_DEVICE_QUANTITY);

                //read the device attributes from the Cursor for the current device
                String deviceQuantity = cursor.getString(quantityColumnIndex);

                //convert the string to an integer
                int updateQuantity = Integer.parseInt(deviceQuantity);

                if (updateQuantity > 0) {

                    //decrease the quantity by 1
                    updateQuantity--;

                    // Defines an object to contain the updated values
                    ContentValues updateValues = new ContentValues();
                    updateValues.put(COLUMN_DEVICE_QUANTITY, updateQuantity);

                    int rowsUpdate = getContentResolver().update(mCurrentDeviceUri, updateValues,
                            null, null);
                } else {
                    Toast.makeText(DeviceDetailActivity.this, "Quantity is 0 and can't be reduced.", Toast
                            .LENGTH_SHORT).show();
                }

            }
        });

        plusButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Perform action on click
                Log.i(LOG_TAG, "TEST: Plus onClick called");


                int itemIdColumnIndex = cursor.getColumnIndex(DeviceEntry._ID);
                final long itemId = cursor.getLong(itemIdColumnIndex);
                Uri mCurrentDeviceUri = ContentUris.withAppendedId(DeviceEntry.CONTENT_URI, itemId);

                int quantityColumnIndex = cursor.getColumnIndex(COLUMN_DEVICE_QUANTITY);

                String deviceQuantity = cursor.getString(quantityColumnIndex);

                //convert the string to an integer
                int updateQuantity = Integer.parseInt(deviceQuantity);

                //increase the quantity by 1
                updateQuantity++;

                // Defines an object to contain the updated values
                ContentValues updateValues = new ContentValues();
                updateValues.put(DeviceEntry.COLUMN_DEVICE_QUANTITY, updateQuantity);

                int rowsUpdate = getContentResolver().update(mCurrentDeviceUri, updateValues, null,
                        null);
            }
        });

        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Perform action on click
                Log.i(LOG_TAG, "TEST: Delete onClick called");

                // Create an AlertDialog.Builder and set the message, and click listeners
                // for the positive and negative buttons on the dialog.
                AlertDialog.Builder builder = new AlertDialog.Builder(DeviceDetailActivity.this);
                builder.setMessage(R.string.delete_dialog_msg);
                builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User clicked the "Delete" button, so delete the device.
                        //get the Uri for the current device
                        int itemIdColumnIndex = cursor.getColumnIndex(DeviceEntry._ID);
                        final long itemId = cursor.getLong(itemIdColumnIndex);
                        Uri mCurrentDeviceUri = ContentUris.withAppendedId(DeviceEntry.CONTENT_URI,
                                itemId);

                        int rowsDeleted = getContentResolver().delete(mCurrentDeviceUri, null,
                                null);

                        // Close the activity
                        finish();
                    }
                });
                builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User clicked the "Cancel" button, so dismiss the dialog
                        // and continue editing the device.
                        if (dialog != null) {
                            dialog.dismiss();
                        }
                    }
                });

                // Create and show the AlertDialog
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
            }
        });

        orderButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Perform action on click
                Log.i(LOG_TAG, "TEST: Order onClick called");


                int itemIdColumnIndex = cursor.getColumnIndex(DeviceEntry._ID);
                final long itemId = cursor.getLong(itemIdColumnIndex);
                Uri mCurrentDeviceUri = ContentUris.withAppendedId(DeviceEntry.CONTENT_URI, itemId);


                int contactColumnIndex = cursor.getColumnIndex(COLUMN_CONTACT_INFO);
                int nameColumnIndex = cursor.getColumnIndex(COLUMN_DEVICE_NAME);

                //read the device attributes from the Cursor for the current device
                String deviceContact = cursor.getString(contactColumnIndex);
                String[] message = {deviceContact};

                //read the device name to use in subject line
                String deviceName = cursor.getString(nameColumnIndex);
                String subjectLine = "Need to order: " + deviceName;

                Intent intent = new Intent(Intent.ACTION_SENDTO);
                intent.setData(Uri.parse("mailto:")); // only email apps should handle this
                intent.putExtra(Intent.EXTRA_SUBJECT, subjectLine);
                intent.putExtra(Intent.EXTRA_EMAIL, message);
                if (intent.resolveActivity(getPackageManager()) != null) {
                    startActivity(intent);
                }
            }

        });
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}
