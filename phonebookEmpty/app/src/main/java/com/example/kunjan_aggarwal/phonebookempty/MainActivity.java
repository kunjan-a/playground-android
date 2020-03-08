package com.example.kunjan_aggarwal.phonebookempty;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.v4.content.FileProvider;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.Date;

public class MainActivity extends Activity {

    private Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button button = findViewById(R.id.button);
        final ProgressBar progressBar = findViewById(R.id.progressBar);
        final Context appContext = getApplicationContext();
        final Context activityContext = getBaseContext();
        handler = new Handler();
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View button) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        final Cursor cursor = getContactsCursor(appContext);
                        JSONArray contacts = new JSONArray();
                        if (cursor != null) {
                            initProgressBar(cursor, progressBar);
                            while (cursor.moveToNext()) {
                                JSONObject jsonObject = getJsonObject(cursor);
                                contacts.put(jsonObject);
                                String lookupKey = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.LOOKUP_KEY));
                                Uri uri = Uri.withAppendedPath(ContactsContract.Contacts.CONTENT_LOOKUP_URI, lookupKey);
                                appContext.getContentResolver().delete(uri, null, null);
                                updateProgressBar(progressBar);
                            }
                            cursor.close();
                        }
                        String contactString = contacts.toString();

                        Log.i("delete", "contact json length:" + contactString.length());
                        Log.i("delete", "removed " + progressBar.getMax());

                        final File outputFile = getContactDumpFile();
                        boolean writtenSuccessfully = writeContactsInDumpFile(contactString, outputFile);
                        if (writtenSuccessfully) {
                            shareContactsOutside(outputFile, activityContext);
                        }
                    }
                }).start();
            }
        });
    }

    private void updateProgressBar(final ProgressBar progressBar) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                progressBar.incrementProgressBy(1);
            }
        });
    }

    private void initProgressBar(final Cursor cursor, final ProgressBar progressBar) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                progressBar.setMax(cursor.getCount());
            }
        });
    }

    private Cursor getContactsCursor(Context context) {
        ContentResolver contentResolver = context.getContentResolver();
        return contentResolver.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null);
    }

    private boolean writeContactsInDumpFile(String contactString, File outputFile) {
        Log.i("delete", "writing contact dump to:" + outputFile.getAbsolutePath());
        boolean writtenSuccessfully = false;
        try {
            PrintWriter printWriter = new PrintWriter(outputFile);
            printWriter.println(contactString);
            printWriter.flush();
            Log.i("delete", "wrote contacts dump to:" + outputFile.getAbsolutePath());

            writtenSuccessfully = true;

        } catch (FileNotFoundException e) {
            Log.e("delete", "writing to output file failed", e);
            e.printStackTrace();
        }
        return writtenSuccessfully;
    }

    @NonNull
    private File getContactDumpFile() {
        File filesDir = getFilesDir();
        File contacts_dir = new File(filesDir, "contacts");
        contacts_dir.mkdir();
        return new File(contacts_dir, "contacts_" + new Date().getTime() + ".txt");
    }

    private void shareContactsOutside(final File outputFile, final Context context) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                try {
                    Uri fileUri = FileProvider.getUriForFile(context,
                            "com.example.kunjan_aggarwal.phonebookempty.fileprovider",
                            outputFile);
                    Log.i("delete", "created shareable uri:" + fileUri);
                    Intent fileShareIntent = new Intent();
                    fileShareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    String type = getContentResolver().getType(fileUri);
                    Log.i("delete", "content resolver has set the url type to:" + type);
                    fileShareIntent.setDataAndType(fileUri, type);
                    fileShareIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    Log.i("delete", "launching activity to share the contacts");
                    context.startActivity(fileShareIntent);
                } catch (IllegalArgumentException e) {
                    Log.e("delete", "creating sharing uri failed", e);
                }
            }
        });
    }

    @NonNull
    private JSONObject getJsonObject(Cursor cursor) {
        JSONObject jsonObject = new JSONObject();
        for (int i = 0; i < cursor.getColumnCount(); i++) {
            if (cursor.getType(i) == Cursor.FIELD_TYPE_STRING) {
                String columnName = cursor.getColumnName(i);
                String value = cursor.getString(i);
                try {
                    jsonObject.put(columnName, value);
                } catch (JSONException e) {
                    Log.e("delete", "json conversion failed", e);
                    e.printStackTrace();
                }
            } else if (cursor.getType(i) == Cursor.FIELD_TYPE_INTEGER) {
                String columnName = cursor.getColumnName(i);
                int value = cursor.getInt(i);
                try {
                    jsonObject.put(columnName, value);
                } catch (JSONException e) {
                    Log.e("delete", "json conversion failed", e);
                    e.printStackTrace();
                }
            } else if (cursor.getType(i) == Cursor.FIELD_TYPE_FLOAT) {
                String columnName = cursor.getColumnName(i);
                float value = cursor.getFloat(i);
                try {
                    jsonObject.put(columnName, value);
                } catch (JSONException e) {
                    Log.e("delete", "json conversion failed", e);
                    e.printStackTrace();
                }
            } else {
                // Log.i("delete", "skipping " + cursor.getColumnName(i) + ", type:" + cursor.getType(i));
            }
        }
        return jsonObject;
    }
}
