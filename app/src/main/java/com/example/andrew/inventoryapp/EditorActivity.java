package com.example.andrew.inventoryapp;

import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import android.widget.TextView;
import android.widget.Toast;

import com.example.andrew.inventoryapp.data.InventoryContract.DeviceEntry;

/**
 * Allows user to create a new pet or edit an existing one.
 */
public class EditorActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {


    private static final String LOG_TAG = EditorActivity.class.getName();

    private static final int PICK_IMAGE_REQUEST = 0;

    private Uri mCurrentDeviceUri;
    private EditText mNameEditText;
    private EditText mQuantityEditText;
    private EditText mPriceEditText;
    private EditText mContactEditText;
    private TextView mPictureText;

    private Uri mUri;
    private TextView mTextView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);

        mTextView = (TextView) findViewById(R.id.image_uri);

        Button pictureButton = (Button) findViewById(R.id.add_picture);
        pictureButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openImageSelector();
            }
        });

        mNameEditText = (EditText) findViewById(R.id.edit_device_name);
        mQuantityEditText = (EditText) findViewById(R.id.edit_device_quantity);
        mPriceEditText = (EditText) findViewById(R.id.edit_device_price);
        mContactEditText = (EditText) findViewById(R.id.edit_device_contact);
        mPictureText = (TextView) findViewById(R.id.image_uri);
    }

    private void saveDevice() {
        String nameString = mNameEditText.getText().toString().trim();
        String quantityString = mQuantityEditText.getText().toString().trim();
        String priceString = mPriceEditText.getText().toString().trim();
        String contactString = mContactEditText.getText().toString().trim();
        String pictureString = mPictureText.getText().toString().trim();

        if (mCurrentDeviceUri == null && TextUtils.isEmpty(nameString) && TextUtils.isEmpty
                (quantityString) && TextUtils.isEmpty(priceString)) {
            return;
        }

        ContentValues values = new ContentValues();

        values.put(DeviceEntry.COLUMN_DEVICE_NAME, nameString);
        values.put(DeviceEntry.COLUMN_DEVICE_QUANTITY, quantityString);
        values.put(DeviceEntry.COLUMN_DEVICE_PRICE, priceString);
        values.put(DeviceEntry.COLUMN_CONTACT_INFO, contactString);
        values.put(DeviceEntry.COLUMN_DEVICE_PICTURE, pictureString);

        Uri newUri = getContentResolver().insert(DeviceEntry.CONTENT_URI, values);
        if (newUri == null) {
            Toast.makeText(this, getString(R.string.editor_insert_device_failed), Toast.LENGTH_SHORT)
                    .show();
        } else {
            Toast.makeText(this, getString(R.string.editor_update_device_successful), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_editor, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_save:
                String nameString = mNameEditText.getText().toString().trim();
                String quantityString = mQuantityEditText.getText().toString().trim();
                String priceString = mPriceEditText.getText().toString().trim();
                String contactString = mContactEditText.getText().toString().trim();
                String pictureString = mPictureText.getText().toString().trim();

                if (TextUtils.isEmpty(nameString)) {
                    Toast.makeText(this, getString(R.string.name_required), Toast.LENGTH_SHORT)
                            .show();
                    return false;
                } else if (TextUtils.isEmpty(quantityString)) {
                    Toast.makeText(this, getString(R.string.quantity_required),
                            Toast.LENGTH_SHORT).show();
                    return false;
                } else if (TextUtils.isEmpty(priceString)) {
                    Toast.makeText(this, getString(R.string.price_required),
                            Toast.LENGTH_SHORT).show();
                    return false;
                } else if (TextUtils.isEmpty(contactString)) {
                    Toast.makeText(this, getString(R.string.contact_required),
                            Toast.LENGTH_SHORT).show();
                    return false;
                } else if (TextUtils.isEmpty(pictureString)) {
                    Toast.makeText(this, getString(R.string.picture_required),
                            Toast.LENGTH_SHORT).show();
                    return false;
                }
                saveDevice();
                finish();
                return true;
            case R.id.action_delete:
                showDeleteConfirmationDialog();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void openImageSelector() {
        Intent intent;

        if (Build.VERSION.SDK_INT < 19) {
            intent = new Intent(Intent.ACTION_GET_CONTENT);
        } else {
            intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
        }

        intent.setType("image/*");
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }


    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
    }

    /**
     * Show a dialog that warns the user there are unsaved changes that will be lost
     * if they continue leaving the editor.
     *
     * @param discardButtonClickListener is the click listener for what to do when
     *                                   the user confirms they want to discard their changes
     */
    private void showUnsavedChangesDialog(
            DialogInterface.OnClickListener discardButtonClickListener) {
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the postivie and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.unsaved_changes_dialog_msg);
        builder.setPositiveButton(R.string.discard, discardButtonClickListener);
        builder.setNegativeButton(R.string.keep_editing, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Keep editing" button, so dismiss the dialog
                // and continue editing the pet.
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    /**
     * Prompt the user to confirm that they want to delete this pet.
     */
    private void showDeleteConfirmationDialog() {
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the postivie and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.delete_dialog_msg);
        builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Delete" button, so delete the pet.
                deleteDevice();
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Cancel" button, so dismiss the dialog
                // and continue editing the pet.
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    /**
     * Perform the deletion of the pet in the database.
     */
    private void deleteDevice() {
        // Only perform the delete if this is an existing pet.
        if (mCurrentDeviceUri != null) {
            // Call the ContentResolver to delete the pet at the given content URI.
            // Pass in null for the selection and selection args because the mCurrentPetUri
            // content URI already identifies the pet that we want.
            int rowsDeleted = getContentResolver().delete(mCurrentDeviceUri, null, null);

            // Show a toast message depending on whether or not the delete was successful.
            if (rowsDeleted == 0) {
                // If no rows were deleted, then there was an error with the delete.
                Toast.makeText(this, getString(R.string.editor_delete_device_failed),
                        Toast.LENGTH_SHORT).show();
            } else {
                // Otherwise, the delete was successful and we can display a toast.
                Toast.makeText(this, getString(R.string.editor_delete_device_successful),
                        Toast.LENGTH_SHORT).show();
            }
        }

        // Close the activity
        finish();
    }
}