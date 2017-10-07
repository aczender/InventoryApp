package com.example.andrew.inventoryapp;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.example.andrew.inventoryapp.data.InventoryContract.DeviceEntry;

import static com.example.andrew.inventoryapp.data.InventoryContract.DeviceEntry.COLUMN_DEVICE_QUANTITY;


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


        buyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

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

        // Read the device attributes from the Cursor for the current phone
        String deviceName = cursor.getString(nameColumnIndex);
        String deviceQuantity = cursor.getString(quantityColumnIndex);
        String devicePrice = cursor.getString(priceColumnIndex);

        // Update the TextViews with the attributes for the current device
        nameTextView.setText(deviceName);
        quantityTextView.setText(deviceQuantity);
        priceTextView.setText(devicePrice);
    }

}