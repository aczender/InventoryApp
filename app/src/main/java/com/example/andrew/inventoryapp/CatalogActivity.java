package com.example.andrew.inventoryapp;

import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.support.design.widget.FloatingActionButton;

import com.example.andrew.inventoryapp.data.InventoryContract.DeviceEntry;


/**
 * Displays list of pets that were entered and stored in the app.
 */
public class CatalogActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final String LOG_TAG = CatalogActivity.class.getName();
    private static final int DEVICE_LOADER = 0;

    DeviceCursorAdapter mCursorAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_catalog);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.floatingActionButton);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CatalogActivity.this, EditorActivity.class);
                startActivity(intent);
            }
        });


        ListView deviceListView = (ListView) findViewById(R.id.list);

        View emptyView = findViewById(R.id.empty_view);
        deviceListView.setEmptyView(emptyView);

        mCursorAdapter = new DeviceCursorAdapter(this, null);
        deviceListView.setAdapter(mCursorAdapter);

        deviceListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                Log.i(LOG_TAG, "Test: something called");
                Intent intent = new Intent(CatalogActivity.this, DeviceDetailActivity.class);

                Uri currentDeviceUri = ContentUris.withAppendedId(DeviceEntry.CONTENT_URI, id);
                intent.setData(currentDeviceUri);
                startActivity(intent);
            }
        });

        getLoaderManager().initLoader(DEVICE_LOADER, null, this);
    }

    private void insertDevice() {
        ContentValues values = new ContentValues();
        values.put(DeviceEntry.COLUMN_DEVICE_NAME, "DummyDevice");
        values.put(DeviceEntry.COLUMN_DEVICE_QUANTITY, 1);
        values.put(DeviceEntry.COLUMN_DEVICE_PRICE, 99);
        values.put(DeviceEntry.COLUMN_CONTACT_INFO, "dummy@dummy.com");
        values.put(DeviceEntry.COLUMN_DEVICE_PICTURE, "samplepic");

        Uri newUri = getContentResolver().insert(DeviceEntry.CONTENT_URI, values);
    }

    private void deleteAllDevices() {
        int rowsDeleted = getContentResolver().delete(DeviceEntry.CONTENT_URI, null, null);
        Log.v("CatalogActivity", rowsDeleted + " rows deleted from device database");
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_catalog, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_insert_dummy_data:
                insertDevice();
                return true;
            case R.id.action_delete_all_entries:
                deleteAllDevices();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle args) {
        String[] projection = {
                DeviceEntry._ID,
                DeviceEntry.COLUMN_DEVICE_NAME,
                DeviceEntry.COLUMN_DEVICE_QUANTITY,
                DeviceEntry.COLUMN_DEVICE_PRICE,
                DeviceEntry.COLUMN_CONTACT_INFO};

        return new CursorLoader(this,
                DeviceEntry.CONTENT_URI,
                projection,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mCursorAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mCursorAdapter.swapCursor(null);
    }
}