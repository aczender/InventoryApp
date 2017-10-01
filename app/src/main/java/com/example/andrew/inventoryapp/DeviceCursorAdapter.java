package com.example.andrew.inventoryapp;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.andrew.inventoryapp.data.InventoryContract.DeviceEntry;

import static com.example.andrew.inventoryapp.data.InventoryContract.DeviceEntry.COLUMN_DEVICE_QUANTITY;


/**
 * Created by Andrew on 2017.09.24..
 */

public class DeviceCursorAdapter extends CursorAdapter {

    private static final String LOG_TAG = DeviceCursorAdapter.class.getName();

    public DeviceCursorAdapter(Context context, Cursor c) {
        super(context, c, 0);
    }

    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
    }

    @Override
    public void bindView(View view, final Context context, final Cursor cursor) {
        TextView nameTextView = (TextView) view.findViewById(R.id.name);
        TextView quantityTextView = (TextView) view.findViewById(R.id.quantity);
        TextView priceTextView = (TextView) view.findViewById(R.id.price);
        Button buyButton = (Button) view.findViewById(R.id.buy);

        final int position = cursor.getPosition();

        buyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i(LOG_TAG, "TEST: onClick called");
                cursor.moveToPosition(position);

                int itemIdColumnIndex = cursor.getColumnIndex(DeviceEntry._ID);
                final long itemId = cursor.getLong(itemIdColumnIndex);
                Uri mCurrentDeviceUri = ContentUris.withAppendedId(DeviceEntry.CONTENT_URI, itemId);

                int quantityColumnIndex = cursor.getColumnIndex(COLUMN_DEVICE_QUANTITY);

                String deviceQuantity = cursor.getString(quantityColumnIndex);

                int updateQuantity = Integer.parseInt(deviceQuantity);

                if (updateQuantity > 0) {

                    //decrease the quantity by 1
                    updateQuantity--;

                    // Defines an object to contain the updated values
                    ContentValues updateValues = new ContentValues();
                    updateValues.put(DeviceEntry.COLUMN_DEVICE_QUANTITY, updateQuantity);

                    //update the phone with the content URI mCurrentPhoneUri and pass in the new
                    //content values. Pass in null for the selection and selection args
                    //because mCurrentPhoneUri will already identify the correct row in the database that
                    // we want to modify.
                    int rowsUpdate = context.getContentResolver().update(mCurrentDeviceUri, updateValues,
                            null, null);
                } else {
                    Toast.makeText(context, "Quantity is 0 and can't be reduced.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        int nameColumnIndex = cursor.getColumnIndex(DeviceEntry.COLUMN_DEVICE_NAME);
        int quantityColumnIndex = cursor.getColumnIndex(COLUMN_DEVICE_QUANTITY);
        int priceColumnIndex = cursor.getColumnIndex(DeviceEntry.COLUMN_DEVICE_PRICE);

        // Read the phone attributes from the Cursor for the current phone
        String deviceName = cursor.getString(nameColumnIndex);
        String deviceQuantity = cursor.getString(quantityColumnIndex);
        String devicePrice = cursor.getString(priceColumnIndex);

        // Update the TextViews with the attributes for the current phone
        nameTextView.setText(deviceName);
        quantityTextView.setText(deviceQuantity);
        priceTextView.setText(devicePrice);
    }

}