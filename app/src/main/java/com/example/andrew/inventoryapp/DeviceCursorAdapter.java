package com.example.andrew.inventoryapp;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.andrew.inventoryapp.data.InventoryContract.DeviceEntry;


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
    public void bindView(View view, Context context, Cursor cursor) {
        TextView nameTextView = (TextView) view.findViewById(R.id.name);
        TextView quantityTextView = (TextView) view.findViewById(R.id.quantity);
        TextView priceTextView = (TextView) view.findViewById(R.id.price);
        ImageView shopping = (ImageView) view.findViewById(R.id.shopping);

        shopping.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i(LOG_TAG, "TEST: onClick called");
            }
        });

        int nameColumnIndex = cursor.getColumnIndex(DeviceEntry.COLUMN_DEVICE_NAME);
        int quantityColumnIndex = cursor.getColumnIndex(DeviceEntry.COLUMN_DEVICE_QUANTITY);

        String deviceName = cursor.getString(nameColumnIndex);
        String deviceQuantity = cursor.getString(quantityColumnIndex);

        nameTextView.setText(deviceName);
        summaryTextView.setText(deviceQuantity);
    }
}
