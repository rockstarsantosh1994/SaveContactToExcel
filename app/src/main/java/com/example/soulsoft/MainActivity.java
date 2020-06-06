package com.example.soulsoft;

import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.ContactsContract;
import android.util.Log;
import android.widget.Toast;

import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private Cursor cursor;
    private boolean csv_status = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Dexter.withContext(MainActivity.this)
                .withPermissions(
                        Manifest.permission.READ_CONTACTS,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE
                ).withListener(new MultiplePermissionsListener() {
            @Override
            public void onPermissionsChecked(MultiplePermissionsReport report) {
                createCSV();
            }
            @Override
            public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                Toast.makeText(MainActivity.this, "Permission rejected", Toast.LENGTH_SHORT).show();
            }
        }).check();
    }

    private void createCSV() {
        CSVWriter writer = null;
        try {
            writer = new CSVWriter(new FileWriter(Environment.getExternalStorageDirectory().getAbsolutePath() + "/my_test_contact.csv"));
        } catch (IOException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
        String displayName;
        String number;
        long _id;
        String[] columns = new String[]{ ContactsContract.Contacts._ID,
                ContactsContract.Contacts.DISPLAY_NAME };
        assert writer != null;
        writer.writeColumnNames(); // Write column header
        Cursor cursor = getContentResolver().query(ContactsContract.Contacts.CONTENT_URI,
                columns,
                null,
                null,
                ContactsContract.Data.DISPLAY_NAME + " COLLATE LOCALIZED ASC");
        //startManagingCursor(cursor);
        assert cursor != null;
        if(cursor.moveToFirst()) {
            do {
                _id = Long.parseLong(cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID)));
                displayName = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                number = getPrimaryNumber(_id);
                writer.writeNext((displayName + "/" + number).split("/"));
            } while(cursor.moveToNext());
            csv_status = true;
           /// tvMessage.setVisibility(View.VISIBLE);
          //  tvMessage.setText("All Contacts are copied and csv file created successfully.... you can check in file manager");
            //exportCSV();

        } else {
            csv_status = false;
        }
        try {
            if(writer != null)
                writer.close();
        } catch (IOException e) {
            Log.e("Test", e.toString());
        }

    }// Method  close.

    private void exportCSV() {
        if(csv_status == true) {
            //CSV file is created so we need to Export that ...
            final File CSVFile = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/my_test_contact.csv");
            //Log.i("SEND EMAIL TESTING", "Email sending");
            Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);
            emailIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            emailIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            emailIntent.setType("text/csv");
            emailIntent .putExtra(android.content.Intent.EXTRA_SUBJECT, "Test contacts ");
            emailIntent.putExtra(Intent.EXTRA_STREAM, Uri.parse("file://" + CSVFile.getAbsolutePath()));
            emailIntent .putExtra(android.content.Intent.EXTRA_TEXT, "\n\nAdroid developer\n Rockstar Studio \n Santosh");
            emailIntent.setType("message/rfc822"); // Shows all application that supports SEND activity
            try {
                startActivity(Intent.createChooser(emailIntent, "Send mail..."));
            } catch (android.content.ActivityNotFoundException ex) {
                Toast.makeText(getApplicationContext(), "Email client : " + ex.toString(), Toast.LENGTH_SHORT);
            }
        } else {
            Toast.makeText(getApplicationContext(), "Information not available to create CSV.", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Get primary Number of requested  id.
     *
     * @return string value of primary number.
     */
    private String getPrimaryNumber(long _id) {
        String primaryNumber = null;
        try {
            Cursor cursor = getContentResolver().query( ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                    new String[]{ContactsContract.CommonDataKinds.Phone.NUMBER, ContactsContract.CommonDataKinds.Phone.TYPE},
                    ContactsContract.CommonDataKinds.Phone.CONTACT_ID +" = "+ _id, // We need to add more selection for phone type
                    null,
                    null);
            if(cursor != null) {
                while(cursor.moveToNext()){
                    switch(cursor.getInt(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.TYPE))){
                        case ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE :
                            primaryNumber = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                            break;
                        case ContactsContract.CommonDataKinds.Phone.TYPE_HOME :
                            primaryNumber = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                            break;
                        case ContactsContract.CommonDataKinds.Phone.TYPE_WORK :
                            primaryNumber = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                            break;
                        case ContactsContract.CommonDataKinds.Phone.TYPE_OTHER :
                    }
                    if(primaryNumber != null)
                        break;
                }
            }
        } catch (Exception e) {
            Log.i("test", "Exception " + e.toString());
        } finally {
            if(cursor != null) {
                cursor.deactivate();
                cursor.close();
            }
        }
        return primaryNumber;
    }
}
