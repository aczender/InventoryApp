package com.example.andrew.inventoryapp;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.example.andrew.inventoryapp.data.InventoryContract.DeviceEntry;

/**
 * Created by Andrew on 2017.09.24..
 */

public class DeviceCursorAdapter extends CursorAdapter {

    public DeviceCursorAdapter(Context context, Cursor c) {
        super(context, c, 0);
    }

    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.list_item, parent,false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        TextView nameTextView = (TextView) view.findViewById(R.id.name);

        int nameColumnIndex = cursor.getColumnIndex(DeviceEntry.COLUMN_DEVICE_NAME);

        String deviceName = cursor.getString(nameColumnIndex);

        nameTextView.setText(deviceName);
    }
}
